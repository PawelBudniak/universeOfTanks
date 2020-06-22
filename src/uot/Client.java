package uot;

import uot.objects.Terrain;

import javax.swing.*;
import java.awt.*;
import java.net.SocketException;
import java.util.List;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AbstractEngine{

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
            JFrame frame = new GameFrame(client.getDisplay());
            frame.setVisible(true);
            while (!client.isGameOver()){
                ;
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

    private List<Coordinates> bullets;
    private int serverTankX;
    private int serverTankY;
    private int clientTankY;
    private int clientTankX;
    private double clientHealth;
    private double serverHealth;

    private boolean a_pressed;
    private boolean w_pressed;
    private boolean d_pressed;
    private boolean s_pressed;
    private int mouseX;
    private int mouseY;
    private boolean isMouseInputValid;

    private Timer networkClock;

    private final int this_player = 1;
    private final int other_player = 0;




    public Client(ObjectOutputStream out, ObjectInputStream in) {
        super();
        this.out = out;
        this.in = in;
        terrain = null;
        display.addKeyListener(new KeyHandler());
        display.addMouseListener(new MouseHandler());
        setMaxHealth((int)TANK_HEALTH, (int)TANK_HEALTH);
        gameClock = new Timer(Game.TICK, new Clock());
        networkClock = new Timer (Game.NET_TICK, (ActionEvent) -> {
            sendPacket();
            receivePacket();
        });
        receiveBoard();
    }



    public boolean isGameOver() {
        return isOver;
    }

    void connectionLost(){
        connectionLost = true;
        networkClock.stop();
    }


    public void receiveBoard(){
        try {
            BoardPacket received = (BoardPacket) in.readObject();
            if (received.getTerrain() != null) {
                terrain = received.getTerrain();
            }
        } catch (IOException | ClassNotFoundException e){
            connectionLost();
        }
        gameClock.start();
        networkClock.start();

    }

    public void sendPacket(){

        try{
            ClientPacket packet = new ClientPacket(a_pressed, w_pressed, d_pressed, s_pressed, mouseX, mouseY, isMouseInputValid);
            isMouseInputValid = false;
            out.writeObject(packet);
            out.flush();
            //out.reset();
        }catch (IOException e) {
            connectionLost();
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
            winner = received.getWinner();
            isOver = received.isGameOver();
            clientHealth = received.getClientHealth();
            serverHealth = received.getServerHealth();
            if (isOver) {
                display.repaint();
                gameOver();
            }

        }catch (IOException | ClassNotFoundException e){
            connectionLost();
        }

    }
    private class Clock implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            drawHealthBars(serverTankX,serverTankY,(int)serverHealth,clientTankX,clientTankY,(int)clientHealth);
            display.repaint();
        }
    }

    @Override
    protected void drawTanks(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(TANK2_IMG, clientTankX, clientTankY, getDisplay());
        g2.drawImage(TANK1_IMG, serverTankX, serverTankY, getDisplay());

    }

    @Override
    protected void drawBullets(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        for (Coordinates bullet: bullets){
            g2.drawImage(BULLET_IMG,bullet.getX(),bullet.getY(),getDisplay());
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
