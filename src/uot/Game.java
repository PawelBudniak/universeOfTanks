package uot;

import uot.objects.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;


public class Game extends AbstractEngine{
    private static final int TANK_LEN = 36;
    private static final int TANK_WID = 54;
    private static final int START_X = 80;

    static final int TICK = 15;
    static final int NET_TICK = 25;

    public static final int N_TERRAIN_BLOCKS = 8;
    private Timer networkClock;
    private LinkedList<Bullet> bullets;
    private Player[] players;
    private int this_player = 0;
    private int other_player = 1;
    private ObjectOutputStream out;
    private ObjectInputStream in;


    public Game(String p1_nick, String p2_nick, boolean online){
        super();
        this.terrain = new LinkedList<>();
        this.bullets = new LinkedList<>();
        this.winner = null;
        players = new Player[2];
        players[0] = new Player(p1_nick,
                new Tank.Builder(START_X, BOARD_LENGTH /2, TANK_WID, TANK_LEN).ammoCapacity(6).reloadTime(800).build());
        players[1] = new Player(p2_nick,
                new Tank.Builder(BOARD_LENGTH - START_X, BOARD_LENGTH /2, TANK_WID, TANK_LEN).ammoCapacity(6).reloadTime(800).build());
        generateWalls();
        generateTerrain();
        gameClock = new Timer(TICK, new GameClock());
        display.addKeyListener(new KeyHandler());
        display.addMouseListener(new MouseHandler());
        if (!online)
            gameClock.start();
    }

    public Game(String p1_nick, String p2_nick, ObjectOutputStream out, ObjectInputStream in){
        this(p1_nick, p2_nick, true);
        this.in = in;
        this.out = out;
        networkClock = new Timer(NET_TICK, (ActionEvent) -> {
            sendPacket();
            receivePacket();
        });
        sendBoard();

    }

    void connectionLost(){
        connectionLost = true;
        networkClock.stop();
    }


    public void sendBoard() {
        BoardPacket packet = new BoardPacket(players, bullets, terrain);
        try{
            out.writeObject(packet);
            out.flush();
            //out.reset();
        } catch (IOException e){
            connectionLost();
        }
        gameClock.start();
        networkClock.start();
    }


    public void sendPacket(){
        LinkedList<Coordinates> coords = new LinkedList<>();
        bullets.forEach(bullet -> coords.add(new Coordinates(bullet.getX(), bullet.getY())));
        ServerPacket packet = new ServerPacket(players[this_player].getX(), players[this_player].getY(),
                                        players[other_player].getX(), players[other_player].getY(), coords, isOver, winner);

        try{
            out.writeObject(packet);
            out.flush();
            if (isOver) {
                System.out.println("GAME OVER SENT");
                networkClock.stop();
            }
            //out.reset();
        }catch (IOException e){
            connectionLost();
        }
    }
    public void receivePacket(){
        try {
            ClientPacket packet = (ClientPacket) in.readObject();
            Player player2 = players[other_player];
            player2.setW_pressed(packet.isW_pressed());
            player2.setA_pressed(packet.isA_pressed());
            player2.setD_pressed(packet.isD_pressed());
            player2.setS_pressed(packet.isS_pressed());

            if (packet.isMouseInputValid()){
                Bullet bullet = player2.shoot(packet.getMouseX(), packet.getMouseY());
                if (bullet != null)
                    bullets.add(bullet);
            }
        }catch (IOException | ClassNotFoundException e){
            connectionLost();
        }
    }

    /** generate terrain in the middle of the board */
    private void generateTerrain(){
        Random random = new Random();
        for (int i = 0; i < N_TERRAIN_BLOCKS; i++) {
            Terrain new_block;
            do{
                new_block = new Terrain(random.nextInt(BOARD_WIDTH), random.nextInt(BOARD_LENGTH));
            }while(players[0].collision(new_block) || players[1].collision(new_block));
            terrain.add(new_block);
        }

    }
    /** generate the walls at the edges of the board */
    private void generateWalls(){
        final int WALL_WIDTH = 10;
        terrain.add(new Terrain(0,0, BOARD_WIDTH, WALL_WIDTH));                                 // upper
        terrain.add(new Terrain(0, BOARD_LENGTH - WALL_WIDTH, BOARD_WIDTH, WALL_WIDTH));        // bottom
        terrain.add(new Terrain(0,0, WALL_WIDTH, BOARD_LENGTH));                                // left
        terrain.add(new Terrain(BOARD_WIDTH - WALL_WIDTH,0, WALL_WIDTH, BOARD_LENGTH));         // right

    }


    public boolean isOver() {
        return isOver && (networkClock == null || !networkClock.isRunning());
    }

    private class GameClock implements ActionListener{
        private int counter;

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            // bullets <-> players collisions

            for (Iterator<Bullet> iBullet = bullets.iterator(); iBullet.hasNext(); ) {
                Bullet bullet = iBullet.next();

                for (Player player : players) {
                    if (!player.isOriginOf(bullet))
                        if (player.collision(bullet)) {

                            double hp = player.hit(bullet);
                            if (hp <= 0) {
                                // player is the one with hp <= 0
                                if (players[this_player] == player)
                                    winner = players[other_player].getName();
                                else
                                    winner = players[this_player].getName();

                                System.out.println(player.getName() + " loses!");
                                isOver = true;
                                display.repaint();
                                return;
                            }

                            iBullet.remove();
                        }
                }
            }
            // bullets <-> terrain collisions
            for (Iterator<Bullet> iBullet = bullets.iterator(); iBullet.hasNext(); ) {
                Bullet bullet = iBullet.next();

                for (Terrain block : terrain) {
                    if (bullet.collision(block)) {
                        //System.out.println("bullet-terrain");
                        iBullet.remove();
                        break;
                    }
                }
            }

            // players <-> terrain collisions
            bouncePlayerCollisions();

            bullets.forEach(Bullet::move);



            getDisplay().repaint();

        }
        private void bouncePlayerCollisions(){
            for (int i = 0; i < players.length;  ++i) {
                Player player = players[i];
                for (Terrain block: terrain){
                    if (player.collision(block)) {
                        player.bounce(block);
                        break;
                    }
                }
                Player other = players[(i+1)%2];
                boolean collision = player.collision(other);
                if (collision)
                    player.bounce(other);
                player.move();
            }
        }
        /** deprecated, could be used instead of bouncing */
        private void preventPlayerCollisions(){
            boolean p1_collision = terrain.stream().anyMatch(t -> players[0].willCollide(t));
            boolean p2_collision = terrain.stream().anyMatch(t -> players[1].willCollide(t));
            // players <-> players collisions
            p1_collision = p1_collision || players[0].willCollide(players[1]);
            p2_collision = p2_collision || players[1].willCollide(players[0]);
            if (!p1_collision)
                players[0].move();
            if (!p2_collision)
                players[1].move();
        }

    }

    @Override
    protected void drawTanks(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(TANK1_IMG, players[this_player].getX(), players[this_player].getY(), getDisplay());
        g2.drawImage(TANK2_IMG, players[other_player].getX(), players[other_player].getY(), getDisplay());

    }

    @Override
    protected void drawBullets(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        for (Bullet bullet: bullets){
            g2.drawImage(BULLET_IMG,bullet.getX(),bullet.getY(),getDisplay());
        }
    }

    private class KeyHandler extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            players[this_player].keyPressed(e.getKeyCode());
        }
        @Override
        public void keyReleased(KeyEvent e){
            players[this_player].keyReleased(e.getKeyCode());
        }
    }
    private class MouseHandler implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                Bullet new_bullet = players[this_player].shoot(mouseEvent.getX(), mouseEvent.getY());
                if (new_bullet != null)
                    bullets.add(new_bullet);
            }
            else if (mouseEvent.getButton() == MouseEvent.BUTTON3){
                Bullet new_bullet = players[(this_player + 1)% 2].shoot(mouseEvent.getX(), mouseEvent.getY());
                if (new_bullet != null)
                    bullets.add(new_bullet);
            }
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
        }
    }

}
