package uot.objects;

import java.awt.*;
import java.io.Serializable;

public class Bullet extends RectangularObject implements Serializable {

    public static final Color DEFAULT_COLOR = Color.red;
    private static final int WIDTH = 5;
    private static final int HEIGHT = 5;
    private static final double DEFAULT_SPEED = 5.0;
    private static final long serialVersionUID = 1397659479066755887L;
    private final double dmg = 15;

    private double speed;
    private double exactX;
    private double exactY;
    private double xDirection;
    private double yDirection;
    private final Tank origin;

    public void setxDirection(int x,int y) {
        this.xDirection = speed *Math.cos(-(Math.atan2(x-this.getX(),y-this.getY()))+Math.PI/2 );
    }

    public void setyDirection(int x, int y) {
        this.yDirection = speed *Math.sin(-(Math.atan2(x-this.getX(),y-this.getY()))+Math.PI/2);
    }


    public Bullet(int startX,int startY,int targetX, int targetY, Tank origin){
        this.rectangle = new Rectangle(startX,startY, WIDTH, HEIGHT);
        this.origin = origin;
        exactX = startX;
        exactY = startY;
        speed = DEFAULT_SPEED;
        setxDirection(targetX,targetY);
        setyDirection(targetX,targetY);
    }
    public Bullet(int startX,int startY,int targetX, int targetY, Tank origin, double speed) {
        this(startX,startY,targetX,targetY,origin);
        this.speed = speed;
        // use custom speed value
        setxDirection(targetX,targetY);
        setyDirection(targetX,targetY);
    }


    public double getDmg() { return dmg; }

    public void move()
    {
        exactX +=  xDirection;
        exactY +=  yDirection;

        rectangle.setLocation((int)Math.round(exactX),(int)Math.round(exactY));
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
