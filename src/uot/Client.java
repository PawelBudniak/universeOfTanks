package uot;

import uot.objects.Terrain;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.ThreadPoolExecutor;

public class Client {

    public static void main(String[] args) {

        String hostName = "192.168.0.178";
        int portNumber = 4445;

        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                ObjectOutputStream out =
                        new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream( (clientSocket.getInputStream()))
        ) {
            clientSocket.setTcpNoDelay(true);
            Client client = new Client(out, in);
            client.receiveBoard();
            JFrame frame = new GameFrame(client.getDisplay());
            frame.setVisible(true);
            while (!client.isGameOver()){
                ;
                //client.receivePacket();w
            }

        } catch (UnknownHostException e) {
            System.err.println("Unkown host");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("I/O error");
            System.exit(1);
        }
    }


    private ObjectOutputStream out;
    private ObjectInputStream in;

    private LinkedList<Terrain> terrain;
    private List<Coordinates> bullets;
    private int serverTankX;
    private int serverTankY;
    private int clientTankY;
    private int clientTankX;
    private boolean gameOver;
    private String winner;

    private boolean a_pressed;
    private boolean w_pressed;
    private boolean d_pressed;
    private boolean s_pressed;
    private int mouseX;
    private int mouseY;
    private boolean isMouseInputValid;

    private Timer clock;
    private Timer networkClock;

    private static final Image BULLET_IMG;
    private static final Image TANK1_IMG;
    private static final Image TANK2_IMG;
    private static final Image ROCK_IMG;
    private static final Image SIDE_IMG;
    private static final Image HORIZ_IMG;
    private Display display;
    private final int this_player = 1;
    private final int other_player = 0;



    static{
        ImageIcon i = new ImageIcon(Game.TANK1_PATH);
        TANK1_IMG = i.getImage();
        i = new ImageIcon(Game.TANK2_PATH);
        TANK2_IMG = i.getImage();
        i = new ImageIcon(Game.BL_PATH);
        BULLET_IMG = i.getImage();
        i = new ImageIcon(Game.ROCK_PATH);
        ROCK_IMG = i.getImage();
        i = new ImageIcon(Game.SIDE_PATH);
        SIDE_IMG = i.getImage();
        i = new ImageIcon(Game.HORIZ_PATH);
        HORIZ_IMG = i.getImage();
    }


    public Client(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
        terrain = null;
        //receivePacket(); // get initial board state
        display = new Display();
        clock  = new Timer(Game.TICK, new Clock());
        networkClock = new Timer (Game.NET_TICK, (ActionEvent) -> {
            sendPacket();
            receivePacket();
        });
    }

    public Display getDisplay() {
        return display;
    }

    public boolean isGameOver() {
        return gameOver;
    }


    public void receiveBoard(){
        while(terrain == null) {
            try {
                BoardPacket received = (BoardPacket) in.readObject();
                //System.out.println("" + received.getBullets() + "," + received.getPlayers() + "," + received.getTerrain());
                //System.out.println(received.getPlayers()[0]);
                //if (received.getPlayers()[0] != null) System.out.println(received.getPlayers()[0].getTank());
                if (received.getTerrain() != null) {
                    terrain = received.getTerrain();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        clock.start();
        networkClock.start();

    }

    public void sendPacket(){

        try{
            ClientPacket packet = new ClientPacket(a_pressed, w_pressed, d_pressed, s_pressed, mouseX, mouseY, isMouseInputValid);
            isMouseInputValid = false;
            // System.out.println(packet);
            out.writeObject(packet);
            out.flush();
            //out.reset();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void gameOver(){
        networkClock.stop();
    }


    public void receivePacket(){
        try {
            ServerPacket received = (ServerPacket) in.readObject();
            bullets = received.getBullets();
            serverTankX = received.getServerTankX();
            serverTankY = received.getServerTankY();
            clientTankX = received.getClientTankX();
            clientTankY = received.getClientTankY();
            gameOver = received.isGameOver();
            if (gameOver) {
                winner = received.getWinner();
                display.repaint();
                gameOver();
            }

        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }
    private class Clock implements ActionListener{
        private int counter;
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
//            ++counter;
//            if(counter %4== 1) {
//                receivePacket();
//                sendPacket();
//            }
            display.repaint();
        }
    }

    public class Display extends JPanel{
        public Display(){
            addKeyListener(new KeyHandler());
            addMouseListener(new MouseHandler());
            setFocusable(true);
            //setBackground(Color.black);
            setPreferredSize(new Dimension(Game.BOARD_WIDTH, Game.BOARD_LENGTH));
        }

        private void drawTanks(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(TANK2_IMG, clientTankX, clientTankY, this);
            g2.drawImage(TANK1_IMG, serverTankX, serverTankY, this);

            //g2.drawImage(player.getImage(),player.getX(),player.getY(),this);
            //g2.drawImage(player.getImage(),player.getX(),player.getY(),this);
        }
        private void drawTerrain(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            int i = 0;
            for (Terrain block: terrain){
                if(i<=1)
                g2.drawImage(HORIZ_IMG,block.getX(),block.getY(),this);
                if(i<=3)
                    g2.drawImage(SIDE_IMG,block.getX(),block.getY(),this);
                if(i>3)
                    g2.drawImage(ROCK_IMG,block.getX(),block.getY(),this);
                i++;

            }
        }

        private void drawGameOver(Graphics g){
            clock.stop();
            Graphics2D g2 = (Graphics2D) g;
            String msg = winner + " wins";
            Font font = new Font("MS Gothic",Font.BOLD, 35);
            FontMetrics metrics =  getDisplay().getFontMetrics(font);

            g2.setColor(Color.pink);
            g2.setFont(font);
            g2.drawString(msg,(Game.BOARD_WIDTH-metrics.stringWidth(msg))/2,Game.BOARD_LENGTH/2);

        }

        private void drawBoard(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            var terrainImage = "src/uot/objects/images/ground2.png";
            var ii = new ImageIcon(terrainImage);
            Image image = ii.getImage();
            g2.drawImage(image,0,0,this);
        }

        private void drawBullets(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            for (Coordinates bullet: bullets){
                g2.drawImage(BULLET_IMG,bullet.getX(),bullet.getY(),this);
            }
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            if (terrain == null) return;
            if (isGameOver())
                drawGameOver(g);
            else {
                drawBoard(g);
                drawTerrain(g);
                drawTanks(g);
                drawBullets(g);
            }
        }
    }





    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e ){
            int keyCode = e.getKeyCode();
            switch(keyCode){
                case KeyEvent.VK_W:
                    w_pressed = true;
                    break;
                case KeyEvent.VK_A:
                    a_pressed = true;
                    break;
                case KeyEvent.VK_D:
                    d_pressed = true;
                    break;
                case KeyEvent.VK_S:
                    s_pressed = true;
                    break;
            }
        }
        @Override
        public void  keyReleased(KeyEvent e ){
            int keyCode = e.getKeyCode();
            switch(keyCode){
                case KeyEvent.VK_W:
                    w_pressed = false;
                    break;
                case KeyEvent.VK_S:
                    s_pressed = false;
                    break;
                case KeyEvent.VK_A:
                    a_pressed = false;
                    break;
                case KeyEvent.VK_D:
                    d_pressed = false;
                    break;
            }
        }
    }
    private class MouseHandler implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                mouseX = mouseEvent.getX();
                mouseY = mouseEvent.getY();
                isMouseInputValid = true;

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
