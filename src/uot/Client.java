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

public class Client {

    public static void main(String[] args) {

        String hostName = "192.168.0.178";
        int portNumber = 4445;

        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                ObjectOutputStream out =
                        new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream((clientSocket.getInputStream()))
        ) {
            Client client = new Client(out, in);
            client.receiveBoard();
            JFrame frame = new GameFrame(client.getDisplay());
            frame.setVisible(true);
            while (true){
                continue;
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

    private static final int TICK = 15;
    private LinkedList<Terrain> terrain;
    private List<Coordinates> bullets;
    //private Player[] players;
    private int serverTankX;
    private int serverTankY;
    private int clientTankY;
    private int clientTankX;
    private static final int boardWidth = 500;
    private static final int boardLength = 500;
    private static final Color TERRAIN_COLOR = Color.DARK_GRAY;
    private boolean a_pressed;
    private boolean w_pressed;
    private boolean d_pressed;
    private boolean s_pressed;
    private int mouseX;
    private int mouseY;
    private boolean isMouseInputValid;
    private Timer clock;
    private Timer networkClock;
    private static final String P1_PATH = "src/uot/objects/images/blue tank.png";
    private static final String P2_PATH = "src/uot/objects/images/red tank.png";
    private static final String BL_PATH = "src/uot/objects/images/bullet.png";
    private static final Image BULLET_IMG;
    private static final Image TANK1_IMG;
    private static final Image TANK2_IMG;
    private Display display;
    private final int this_player = 1;
    private final int other_player = 0;

    static{
        ImageIcon i = new ImageIcon(P1_PATH);
        TANK1_IMG = i.getImage();
        i = new ImageIcon(P2_PATH);
        TANK2_IMG = i.getImage();
        i = new ImageIcon(BL_PATH);
        BULLET_IMG = i.getImage();
    }


    public Client(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
        terrain = null;
        //receivePacket(); // get initial board state
        display = new Display();
        clock  = new Timer(TICK, new Clock());
        networkClock = new Timer (40, (ActionEvent) -> {
            sendPacket();
            receivePacket();
        });
    }

    public Display getDisplay() {
        return display;
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
                //e.printStackTrace();
            }
        }
        clock.start();
        networkClock.start();

    }

    public void sendPacket(){

        try{
            ClientPacket packet = new ClientPacket(a_pressed, w_pressed, d_pressed, s_pressed, mouseY, mouseY, isMouseInputValid);
            isMouseInputValid = false;
            // System.out.println(packet);
            out.writeObject(packet);
            out.flush();
            out.reset();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //public void receiveBoard(){}
    public void receivePacket(){
        try {
            ServerPacket received = (ServerPacket) in.readObject();
            //System.out.println("" + received.getBullets() +"," + received.getPlayers()+","+ received.getTerrain());
            //System.out.println(received.getPlayers()[0]);
            //if (received.getPlayers()[0] != null) System.out.println(received.getPlayers()[0].getTank());
            bullets = received.getBullets();
            serverTankX = received.getServerTankX();
            serverTankY = received.getServerTankY();
            clientTankX = received.getClientTankX();
            clientTankY = received.getClientTankY();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }
    private class Clock implements ActionListener{
        private int counter;
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            ++counter;
            if(counter %4== 1) {
                receivePacket();
                sendPacket();
            }
            display.repaint();
        }
    }

    public class Display extends JPanel{
        public Display(){
            addKeyListener(new KeyHandler());
            addMouseListener(new MouseHandler());
            setFocusable(true);
            //setBackground(Color.black);
            setPreferredSize(new Dimension(boardWidth, boardLength));
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
            for (Coordinates bullet: bullets){
                g2.drawImage(BULLET_IMG,bullet.getX(),bullet.getY(),this);
//                g2.setColor(bullet.DEFAULT_COLOR);
//                g2.fill(bullet.getShape());
            }
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            if (terrain == null) return;
            drawBoard(g);
            drawTerrain(g);
            drawTanks(g);
            drawBullets(g);
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
            if (mouseEvent.getButton() == mouseEvent.BUTTON1) {
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
