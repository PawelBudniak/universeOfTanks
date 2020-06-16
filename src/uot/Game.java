package uot;

import uot.objects.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;


public class Game {
    private static final int TICK = 15;
    private static final int TANK_LEN = 36;
    private static final int TANK_WID = 54;
    private static final int START_X = 80;
    private static final String P1_PATH = "src/uot/objects/images/blue tank.png";
    private static final String P2_PATH = "src/uot/objects/images/red tank.png";
    private static final String BL_PATH = "src/uot/objects/images/bullet.png";
    private static final Image BULLET_IMG;
    private static final Image TANK1_IMG;
    private static final Image TANK2_IMG;

    private static final Color TERRAIN_COLOR = Color.DARK_GRAY;
    public static final int N_TERRAIN_BLOCKS = 8;
    private final int boardLength;
    private final int boardWidth;
    private Timer gameClock;
    private Timer networkClock;
    private LinkedList<Terrain> terrain;
    private LinkedList<Bullet> bullets;
    private Player[] players;
    private int this_player = 0;
    private int other_player = 1;
    //Player player1;
    //Player player2;
    private final Display display;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isOver;

    static{
        ImageIcon i = new ImageIcon(P1_PATH);
        TANK1_IMG = i.getImage();
        i = new ImageIcon(P2_PATH);
        TANK2_IMG = i.getImage();
        i = new ImageIcon(BL_PATH);
        BULLET_IMG = i.getImage();
    }

    public Game(int boardLength, int boardWidth, String p1_nick, String p2_nick){
        this.terrain = new LinkedList<>();
        this.bullets = new LinkedList<>();
        this.boardWidth = boardWidth;
        this.boardLength = boardLength;
        players = new Player[2];
        players[0] = new Player(p1_nick,
                new Tank.Builder(START_X, boardLength/2, TANK_WID, TANK_LEN).build());
        players[1] = new Player(p2_nick,
                new Tank.Builder(boardLength - START_X, boardLength/2, TANK_WID, TANK_LEN).build());
        generateWalls();
        generateTerrain();
        //sendBoard();
        gameClock = new Timer(TICK, new GameClock());
        networkClock = new Timer (25, (ActionEvent) -> {
            sendPacket();
            receivePacket();
        });
        this.display = new Display();
        display.addKeyListener(new KeyHandler());
        display.addMouseListener(new MouseHandler());
        gameClock.start();
        networkClock.start();
    }

    public Game(int boardLength, int boardWidth, String p1_nick, String p2_nick, ObjectOutputStream out, ObjectInputStream in){
        this(boardLength, boardWidth, p1_nick, p2_nick);
        this.in = in;
        this.out = out;
    }

    public void sendBoard() {
        System.out.println("wysylam board");
        BoardPacket packet = new BoardPacket(players, bullets, terrain);
        try{
            out.writeObject(packet);
            out.flush();
            out.reset();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

//    public void sendPacket(){
//        //  System.out.println("wysylam pakiet, " + players[0].getTank());
//        BoardPacket packet = new BoardPacket(players, bullets, null);
//        try{
//            out.writeObject(packet);
//            out.flush();
//            out.reset();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }

    public void sendPacket(){
        //  System.out.println("wysylam pakiet, " + players[0].getTank());
        LinkedList<Coordinates> coords = new LinkedList<>();
        bullets.forEach(bullet -> coords.add(new Coordinates(bullet.getX(), bullet.getY())));
        ServerPacket packet = new ServerPacket(players[this_player].getX(), players[this_player].getY(),
                                        players[other_player].getX(), players[other_player].getY(), coords);
        try{
            out.writeObject(packet);
            out.flush();
            //out.reset();
        }catch (IOException e){
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    /** generate terrain in the middle of the board */
    // prevent terrain collisions?
    private void generateTerrain(){
        Random random = new Random();
        for (int i = 0; i < N_TERRAIN_BLOCKS; i++) {
            Terrain new_block;
            do{
                new_block = new Terrain(random.nextInt(boardWidth), random.nextInt(boardLength));
            }while(players[0].collision(new_block) || players[1].collision(new_block));
            terrain.add(new_block);
        }

    }
    /** generate the walls at the edges of the board */
    private void generateWalls(){
        // najpierw dam 5 px szerokosci pozniej mozna zminiejszyc /zwiekszyc
        final int WALL_WIDTH = 10;
        terrain.add(new Terrain(0,0, boardWidth, WALL_WIDTH));                               // upper
        terrain.add(new Terrain(0,0, WALL_WIDTH, boardLength));                              // left
        terrain.add(new Terrain(boardWidth - WALL_WIDTH,0, WALL_WIDTH, boardLength));        // right
        terrain.add(new Terrain(0, boardLength - WALL_WIDTH, boardWidth, WALL_WIDTH));       // bottom
    }


    public Display getDisplay() {
        return display;
    }


    private class GameClock implements ActionListener{
        private int counter;

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            // bullets <-> players collisions
            //receivePacket();

            for (Iterator<Bullet> iBullet = bullets.iterator(); iBullet.hasNext(); ) {
                Bullet bullet = iBullet.next();

                for (Player player : players) {
                    if (!player.isOriginOf(bullet))
                        if (player.collision(bullet)) {
                            //
                            // System.out.println("bullet-player");
                            double hp = player.hit(bullet);
                            System.out.println(player.getName() + " hp: " + hp);
                            if (hp <= 0) {
                                System.out.println(player.getName() + " loses!");
                                networkClock.stop();
                                isOver = true;
                                sendPacket();
                                gameClock.stop();
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

            //players <-> terrain collisions (prevent them beforehand)
            //preventPlayerCollisions();


            bullets.forEach(Bullet::move);

            display.repaint();

//
            //sendPacket();

        }
        /** wersja z odbijaniem */
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
        /** wersja z blokowaniem ruchu kolizyjnego */
        private void preventPlayerCollisions(){
            boolean p1_collision = terrain.stream().anyMatch(t -> players[0].willCollide(t));
            boolean p2_collision = terrain.stream().anyMatch(t -> players[1].willCollide(t));
            // players <-> players collisions
            // jak obaj wejda w tym samym momencie na siebie to i tak bedzie kolizja wiec to nie jest zbyt dobre
            p1_collision = p1_collision || players[0].willCollide(players[1]);
            p2_collision = p2_collision || players[1].willCollide(players[0]);
            // bullets <-> bullets collisions?
            if (!p1_collision)
                players[0].move();
            if (!p2_collision)
                players[1].move();
        }

    }
    private class Display extends JPanel{

        public Display(){
            setFocusable(true);
            //setBackground(Color.black);
            setPreferredSize(new Dimension(boardWidth, boardLength));
        }

        private void drawTanks(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            //for (Player player: players){
                //g2.drawImage(player.getImage(),player.getX(),player.getY(),this);
                g2.drawImage(TANK1_IMG, players[this_player].getX(), players[this_player].getY(), this);
                g2.drawImage(TANK2_IMG, players[other_player].getX(), players[other_player].getY(), this);

                //g2.drawImage(player.getImage(),player.getX(),player.getY(),this);
            //}
        }
        private void drawTerrain(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            for (Terrain block: terrain){
                //g2.drawImage(block.getImage(),block.getX(),block.getY(),this);
                g2.setColor(TERRAIN_COLOR);
                g2.fill(block.getShape());

            }
        }

        public void drawBoard(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            var terrainImage = "src/uot/objects/images/ground2.png";
            var ii = new ImageIcon(terrainImage);
            Image image = ii.getImage();
            g2.drawImage(image,0,0,this);
        }

        private void drawBullets(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            for (Bullet bullet: bullets){
                g2.drawImage(BULLET_IMG,bullet.getX(),bullet.getY(),this);
            }
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            drawBoard(g);
            drawTerrain(g);
            drawTanks(g);
            drawBullets(g);
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
