package uot.objects;

import java.awt.*;

public class Bullet extends RectangularObject{

    public static final Color DEFAULT_COLOR = Color.red;
    private static final int width = 5;
    private static final int height = 5;
    private static final int speed = 5;

    private double exactX;
    private double exactY;
    private final Tank origin;

    public void setxDirection(int x,int y) {
        this.xDirection = speed*Math.cos(-(Math.atan2(x-this.getX(),y-this.getY()))+Math.PI/2 );
    }

    public void setyDirection(int x, int y) {
        this.yDirection = speed*Math.sin(-(Math.atan2(x-this.getX(),y-this.getY()))+Math.PI/2);
    }

    private double xDirection;
    private double yDirection;

    public Bullet(int startX,int startY,int targetX, int targetY, Tank origin){
        this.rectangle = new Rectangle(startX,startY,width, height);
        this.origin = origin;
        setxDirection(targetX,targetY);
        setyDirection(targetX,targetY);
        exactX = startX;
        exactY = startY;
    }

    public double getDmg()
    {
        return Math.random()*20+10;//20 - max dmg , 10 - min dmg
    }

    public void move()
    {
        exactX +=  xDirection;
        exactY +=  yDirection;

        rectangle.setLocation((int)exactX,(int)exactY);
    }

    public Tank getOrigin() {
        return origin;
    }

    @Override
    public String toString() {
        return "Bullet{" +
                "exactX=" + exactX +
                ", exactY=" + exactY +
                ", origin=" + origin +
                ", xDirection=" + xDirection +
                ", yDirection=" + yDirection +
                ", rectangle=" + rectangle +
                '}';
    }
}
