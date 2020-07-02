package uot;

import javafx.scene.control.ProgressBar;
import uot.objects.Terrain;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class AbstractEngine {
    private static final String IMG_LOCATION = "resources/images/";
    static final String TANK1_PATH = IMG_LOCATION + "blue tank.png";
    static final String TANK2_PATH = IMG_LOCATION + "red tank.png";
    static final String BL_PATH = IMG_LOCATION + "bullet.png";
    static final String ROCK_PATH = IMG_LOCATION + "rock.png";
    static final String HORIZ_PATH = IMG_LOCATION + "horizontal wall.png";
    static final String SIDE_PATH = IMG_LOCATION + "side wall.png";
    static final String BACKGROUND_PATH = IMG_LOCATION + "ground2.png";

    static final int BOARD_WIDTH = 500;
    static final int BOARD_LENGTH = 500;
    private static final int HEALTH_BAR_HEIGHT = 8;
    private static final int HEALTH_BAR_WIDTH = 30;
    private static final int HEALTH_BAR_LENGTH = 5;
    private static final int TANK1_WID;
    private static final int TANK2_WID;


    protected static final Image BULLET_IMG;
    protected static final Image TANK1_IMG;
    protected static final Image TANK2_IMG;
    protected static final Image ROCK_IMG;
    protected static final Image HORIZ_IMG;
    protected static final Image SIDE_IMG;
    protected static final Image BACKGROUND_IMG;

    protected static final double TANK_HEALTH = 30;

    protected List<Terrain> terrain;
    protected Timer gameClock;
    protected Display display;
    protected volatile boolean isOver;
    protected volatile boolean connectionLost;
    String winner;
    JProgressBar p1HealthBar;
    JProgressBar p2HealthBar;

    private Thread networkThread;


    public AbstractEngine() {
        p1HealthBar = new JProgressBar(0,0);
        p2HealthBar = new JProgressBar(0,0);
        p1HealthBar.setForeground(Color.GREEN);
        p1HealthBar.setBackground(Color.RED);
        p1HealthBar.setBounds(0,0,HEALTH_BAR_WIDTH,HEALTH_BAR_LENGTH);
        p2HealthBar.setBounds(0,0,HEALTH_BAR_WIDTH,HEALTH_BAR_LENGTH);
        p2HealthBar.setForeground(Color.GREEN);
        p2HealthBar.setBackground(Color.RED);
        // health bars are hidden until the setMaxHealth method is called
        p1HealthBar.setVisible(false);
        p2HealthBar.setVisible(false);

        display = new Display();
        display.add(p1HealthBar);
        display.add(p2HealthBar);
    }

    static{
        ImageIcon i = new ImageIcon(TANK1_PATH);
        TANK1_IMG = i.getImage();
        TANK1_WID = i.getIconWidth();
        i = new ImageIcon(TANK2_PATH);
        TANK2_IMG = i.getImage();
        TANK2_WID = i.getIconWidth();
        i = new ImageIcon(BL_PATH);
        BULLET_IMG = i.getImage();
        i = new ImageIcon(ROCK_PATH);
        ROCK_IMG = i.getImage();
        i = new ImageIcon(HORIZ_PATH);
        HORIZ_IMG = i.getImage();
        i = new ImageIcon(SIDE_PATH);
        SIDE_IMG = i.getImage();
        i = new ImageIcon(BACKGROUND_PATH);
        BACKGROUND_IMG = i.getImage();

    }

    protected abstract void sendPacket();
    protected abstract void receivePacket();

    protected void stopNetworking(){
        networkThread.interrupt();
    }
    protected boolean isNetworkingAlive(){ return networkThread.isAlive(); }


    protected void initNetworking(int tick){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        sendPacket();
                        receivePacket();
                        Thread.sleep(tick);
                    }
                }
                catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
        };
        networkThread = new Thread(r);
        networkThread.start();
    }

    private void cleanBoard(){
        p1HealthBar.setVisible(false);
        p2HealthBar.setVisible(false);
    }

    protected abstract void drawTanks(Graphics g);

    public Display getDisplay() {
        return display;
    }

    private void drawMsg(Graphics g, String msg){
        Font font = new Font("MS Gothic",Font.BOLD, 35);
        FontMetrics metrics =  getDisplay().getFontMetrics(font);

        g.setColor(Color.pink);
        g.setFont(font);
        g.drawString(msg,(Game.BOARD_WIDTH-metrics.stringWidth(msg))/2,Game.BOARD_LENGTH/2);
    }

    protected void drawConnectionLost(Graphics g){
        gameClock.stop();
        cleanBoard();
        drawMsg(g, "Connection Lost");
    }

    protected void drawTerrain(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        int i =0;
        for (Terrain block: terrain){
            if(i<=1)
                g2.drawImage(HORIZ_IMG,block.getX(),block.getY(),display);
            if(i<=3)
                g2.drawImage(SIDE_IMG,block.getX(),block.getY(),display);
            if(i>3)
                g2.drawImage(ROCK_IMG,block.getX(),block.getY(),display);

            ++i;
        }
    }

    protected void drawBoard(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(BACKGROUND_IMG,0,0,display);
    }

    protected void drawGameOver(Graphics g){
        gameClock.stop();
        cleanBoard();
        drawMsg(g, winner + " wins");

    }

    /** If a subclass wants health bars to be visible it needs to call this method and set the maximum healths*/
    protected void setMaxHealth(int p1Health, int p2Health){
        p1HealthBar.setVisible(true);
        p2HealthBar.setVisible(true);
        p1HealthBar.setMaximum(p1Health);
        p2HealthBar.setMaximum(p2Health);
    }

    protected void drawHealthBars(int x1, int y1, int health1, int x2, int y2, int health2){
        p1HealthBar.setValue(health1);
        p2HealthBar.setValue(health2);
        int x_bar1 = x1 + TANK1_WID/2 - HEALTH_BAR_WIDTH/2;
        int x_bar2 = x2 + TANK2_WID/2 - HEALTH_BAR_WIDTH/2;
        p1HealthBar.setLocation(x_bar1, y1 - HEALTH_BAR_HEIGHT);
        p2HealthBar.setLocation(x_bar2, y2 - HEALTH_BAR_HEIGHT);
    }

    protected abstract void drawBullets(Graphics g);

    protected class Display extends JPanel {
        public Display() {
            setLayout(null);
            setFocusable(true);
            setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_LENGTH));
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (isOver)
                drawGameOver(g);
            else if (connectionLost)
                drawConnectionLost(g);
            else {
                drawBoard(g);
                drawTerrain(g);
                drawTanks(g);
                drawBullets(g);
            }
        }
    }


}
