package uot;

import uot.objects.Terrain;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class AbstractEngine {
    static final String TANK1_PATH = "src/uot/objects/images/blue tank.png";
    static final String TANK2_PATH = "src/uot/objects/images/red tank.png";
    static final String BL_PATH = "src/uot/objects/images/bullet.png";
    static final String ROCK_PATH = "src/uot/objects/images/rock.png";
    static final String HORIZ_PATH = "src/uot/objects/images/horizontal wall.png";
    static final String SIDE_PATH = "src/uot/objects/images/side wall.png";
    static final String BACKGROUND_PATH = "src/uot/objects/images/ground2.png";
    static final int BOARD_WIDTH = 500;
    static final int BOARD_LENGTH = 500;

    protected static final Image BULLET_IMG;
    protected static final Image TANK1_IMG;
    protected static final Image TANK2_IMG;
    protected static final Image ROCK_IMG;
    protected static final Image HORIZ_IMG;
    protected static final Image SIDE_IMG;
    protected static final Image BACKGROUND_IMG;

    protected List<Terrain> terrain;
    protected Timer gameClock;
    protected Display display;
    protected boolean isOver;
    String winner;


    public AbstractEngine() {
        display = new Display();
    }

    static{
        ImageIcon i = new ImageIcon(TANK1_PATH);
        TANK1_IMG = i.getImage();
        i = new ImageIcon(TANK2_PATH);
        TANK2_IMG = i.getImage();
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

    protected abstract void drawTanks(Graphics g);

    public Display getDisplay() {
        return display;
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
        String msg = winner + " wins";
        Font font = new Font("MS Gothic",Font.BOLD, 35);
        FontMetrics metrics =  getDisplay().getFontMetrics(font);

        g.setColor(Color.pink);
        g.setFont(font);
        g.drawString(msg,(Game.BOARD_WIDTH-metrics.stringWidth(msg))/2,Game.BOARD_LENGTH/2);

    }

    protected abstract void drawBullets(Graphics g);

    protected class Display extends JPanel {

        public Display() {
            setFocusable(true);
            setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_LENGTH));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (isOver)
                drawGameOver(g);
            else {
                drawBoard(g);
                drawTerrain(g);
                drawTanks(g);
                drawBullets(g);
            }
        }
    }


}
