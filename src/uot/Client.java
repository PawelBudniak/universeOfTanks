package uot;

import uot.objects.Bullet;
import uot.objects.Terrain;

import javax.imageio.IIOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class Client {

    public static void main(String[] args) {

        String hostName = "192.168.0.178";
        int portNumber = 4444;

        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                ObjectOutputStream out =
                        new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream((clientSocket.getInputStream()));
        ) {
            Client client = new Client(out, in);
            JFrame frame = new GameFrame(client.getDisplay());
            frame.setVisible(true);

        } catch (UnknownHostException e) {
            System.err.println("Unkown host");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("I/O error");
            System.exit(1);
        }
    }


    private ObjectOutputStream out;
    private ObjectInputStream in;

    private static final int TICK = 25;
    private LinkedList<Terrain> terrain;
    private LinkedList<Bullet> bullets;
    private Player[] players;
    private static final int boardWidth = 500;
    private static final int boardLength = 500;
    private static final Color TERRAIN_COLOR = Color.DARK_GRAY;
    private int keyPressed;
    private int keyReleased;
    private boolean isKeyPressedValid;
    private boolean isKeyReleasedValid;
    private int mouseX;
    private int mouseY;
    private boolean isMouseInputValid;
    private Timer clock;
    private static final String P1_PATH = "src/uot/objects/images/blue tank.png";
    private static final String P2_PATH = "src/uot/objects/images/red tank.png";
    private static final Image TANK1_IMG;
    private static final Image TANK2_IMG;
    private Display display;

    static{
        ImageIcon i = new ImageIcon("src/uot/objects/images/blue tank.png");
        TANK1_IMG = i.getImage();
        i = new ImageIcon("src/uot/objects/images/red tank.png");
        TANK2_IMG = i.getImage();
    }


    public Client(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
        receivePacket(); // get initial board state
        display = new Display();
        clock  = new Timer(TICK, new Clock());
        clock.start();
    }

    public Display getDisplay() {
        return display;
    }

    public void sendPacket(){
        try{
            ClientPacket packet = new ClientPacket(keyPressed, isKeyPressedValid,keyReleased,isKeyReleasedValid,mouseX,mouseY,isMouseInputValid);
            out.writeObject(packet);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //public void receiveBoard(){}
    public void receivePacket(){
        try {
            ServerPacket received = (ServerPacket) in.readObject();
            bullets = received.getBullets();
            players = received.getPlayers();
            if (received.getTerrain() != null){
                terrain = received.getTerrain();
            }
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }
    private class Clock implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            sendPacket();
            receivePacket();
            display.repaint();
        }
    }

    public class Display extends JPanel{
        public Display(){
            setFocusable(true);
            //setBackground(Color.black);
            setPreferredSize(new Dimension(boardWidth, boardLength));
        }

        private void drawTanks(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            for (Player player: players){
                //g2.drawImage(player.getImage(),player.getX(),player.getY(),this);
                //g2.drawImage(player.getImage(),player.getX(),player.getY(),this);
            }
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
                g2.setColor(bullet.DEFAULT_COLOR);
                g2.fill(bullet.getShape());
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
    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e){
            keyPressed = e.getKeyCode();
            isKeyPressedValid = true;
        }
        @Override
        public void keyReleased(KeyEvent e){
            keyReleased = e.getKeyCode();
            isKeyReleasedValid = true;
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
