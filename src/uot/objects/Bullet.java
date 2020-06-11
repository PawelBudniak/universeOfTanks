package uot.objects;

import java.awt.*;

public class Bullet extends RectangularObject{

    public static final Color DEFAULT_COLOR = Color.red;
    private static final int width = 5;
    private static final int height = 5;

    public void setxDirection(int x,int y) {
        this.xDirection = 5*Math.cos(-(Math.atan2(x-this.getX(),y-this.getY()))+Math.PI/2 );
    }

    public void setyDirection(int x, int y) {
        this.yDirection = 5*Math.sin(-(Math.atan2(x-this.getX(),y-this.getY()))+Math.PI/2);
    }

    private double xDirection;
    private double yDirection;

    public Bullet(int startX,int startY,int targetX, int targetY) {

        this.rectangle = new Rectangle(startX,startY,width, height);
        setxDirection(targetX,targetY);
        setyDirection(targetX,targetY);
    }

    public double getDmg()
    {
        return Math.random()*20+10;//20 - max dmg , 10 - min dmg
    }

    public void move()
    {
        int x = this.getX() + (int) xDirection;
        int y = this.getY() + (int) yDirection;

        rectangle.setLocation(x,y);
    }



}
