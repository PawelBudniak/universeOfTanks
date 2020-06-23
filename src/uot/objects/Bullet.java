package uot.objects;

import java.awt.*;
import java.io.Serializable;

public class Bullet extends RectangularObject implements Serializable {

    public static final Color DEFAULT_COLOR = Color.red;
    private static final int WIDTH = 5;
    private static final int HEIGHT = 5;
    private static final int SPEED = 5;
    private static final long serialVersionUID = 1397659479066755887L;
    private final double dmg = 15;

    private double exactX;
    private double exactY;
    private final Tank origin;

    public void setxDirection(int x,int y) {
        this.xDirection = SPEED *Math.cos(-(Math.atan2(x-this.getX(),y-this.getY()))+Math.PI/2 );
    }

    public void setyDirection(int x, int y) {
        this.yDirection = SPEED *Math.sin(-(Math.atan2(x-this.getX(),y-this.getY()))+Math.PI/2);
    }

    private double xDirection;
    private double yDirection;

    public Bullet(int startX,int startY,int targetX, int targetY, Tank origin){
        this.rectangle = new Rectangle(startX,startY, WIDTH, HEIGHT);
        this.origin = origin;
        setxDirection(targetX,targetY);
        setyDirection(targetX,targetY);
        exactX = startX;
        exactY = startY;
    }

    public double getDmg() { return dmg; }

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
