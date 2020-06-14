package uot.objects;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.lang.*;

public class Tank extends RectangularObject implements Serializable {


    public static final double DEFAULT_HEALTH = 300.0;
    public static final int DEFAULT_RELOAD_TIME = 500;
    /** How many times can the tank shoot continuously before having to reload */
    public static final int DEFAULT_AMMO_CAPACITY = 5;
    public static final String DEFAULT_PATH = "src/images.blue tank.png";
    public static final Color DEFAULT_COLOR = Color.blue;

    private static final double BOUNCE_MODIFIER = 2.5;
    private static final double SPEED = 0.4;
    private static final double SPEED_CAP = 6.0;
    private static final double FRICTION = 0.97;
    private static final long serialVersionUID = 5469024326928012237L;

    /** tank parameters */
    private final double maxHealth;
    private final int reloadTime;
    private final int ammoCapacity;

    //private final Image image;

    /** current state (current coordinates are kept in the inherited protected rectangle field) */
    private int ammoLeft;
    private double healthLeft;
    // will be initialized to 0 by default
    private double ax;          // x direction acceleration
    private double ay;          // y direction acceleration
    private double actual_dx;   // approximate change in the y direction each tick
    private double actual_dy;   // approximate change in the x direction each tick
    private double prev_x;
    private double prev_y;
    private Timer reloadTimer;
    private boolean a_pressed;
    private boolean w_pressed;
    private boolean d_pressed;
    private boolean s_pressed;

    public Bullet shoot(int x, int y){
        if (ammoLeft > 0) {
            --ammoLeft;
            reloadTimer.restart();
            return new Bullet(getX(), getY(), x, y, this);
        }
        else {
            System.out.println("No ammo");
            return null;
        }
    }

    public boolean isOriginOf(Bullet b){
        return b.getOrigin() == this;
    }
    // experymentalne
    public void bounce(RectangularObject from) {

        // object on the right
        if (from.getX() <= getX() + getWidth() && prev_x + getWidth()<= from.getX()) {
            actual_dx = -BOUNCE_MODIFIER * actual_dx;
        }
        // object on the left
        else if (from.getX() + from.getWidth() >= getX() && prev_x >= from.getWidth() + from.getX()){
            actual_dx = -BOUNCE_MODIFIER * actual_dx;
        }
        // object below the tank
        else if (from.getY() <= getY() + getHeight() && prev_y + getHeight() <= from.getY()) {
            actual_dy = -BOUNCE_MODIFIER * actual_dy;
        }
        // object above the tank
        else if (from.getY() + from.getHeight() >= getY() && prev_y >= from.getY() + from.getHeight() ) {
            actual_dy = -BOUNCE_MODIFIER * actual_dy;
        }
        // last ditch effort
        else{
            System.out.println(this);
            //System.out.println(from);
            actual_dx = -BOUNCE_MODIFIER * actual_dx;
            actual_dy = -BOUNCE_MODIFIER * actual_dy;
        }

    }
    // to albo bounce
    public boolean willCollide(RectangularObject other){
        return new Tank.Builder(getX()+(int)getNextDx(), getY() + (int)getNextDy(), getWidth(), getHeight()).build().collision(other);
    }

    private double getNextDx(){
        double next_dx = actual_dx;
        next_dx += ax;
        if (next_dx > 0)
            next_dx = Math.min(next_dx, SPEED_CAP);
        else
            next_dx = Math.max(next_dx, -SPEED_CAP);
        next_dx = FRICTION * next_dx;
        return next_dx;

    }
    private double getNextDy(){
        double next_dy = actual_dy;
        next_dy += ay;
        if (next_dy > 0)
            next_dy = Math.min(next_dy, SPEED_CAP);
        else
            next_dy = Math.max(next_dy, -SPEED_CAP);
        next_dy = FRICTION * next_dy;
        return next_dy;
    }

    public void move(){


        if (a_pressed && !d_pressed)
            ax = -SPEED;
        else if (d_pressed)
            ax = SPEED;
        else
            ax = 0;
        if (s_pressed && !w_pressed)
            ay = SPEED;
        else if(w_pressed)
            ay = -SPEED;
        else
            ay = 0;

        actual_dx = getNextDx();
        actual_dy = getNextDy();

        prev_x = getX();
        prev_y = getY();

        int dx = (int)actual_dx;
        int dy = (int)actual_dy;

        setX(getX() + dx);
        setY(getY() + dy);
    }
    public void keyPressed(int keyCode ){
        switch(keyCode){
            case KeyEvent.VK_W:
                w_pressed = true;
                ay = -SPEED;
                break;
            case KeyEvent.VK_A:
                a_pressed = true;
                break;
            case KeyEvent.VK_D:
                d_pressed = true;
                break;
            case KeyEvent.VK_S:
                s_pressed = true;
                ay = SPEED;
                break;
        }
    }
    public void  keyReleased(int keyCode){
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



    /** Ignores self hits
     *  Return: remaining health */
    public double hit(Bullet bullet){
        if (bullet.getOrigin() == this)
            return healthLeft;
        return healthLeft -= bullet.getDmg();
    }

    public static class Builder{
        /** Required parameters */
        private final Rectangle rectangle;


        /** Optional parameters */
        private double maxHealth = DEFAULT_HEALTH;
        private int reloadTime = DEFAULT_RELOAD_TIME;
        private int ammoCapacity = DEFAULT_AMMO_CAPACITY;
        private Color color = DEFAULT_COLOR;
        private Image image;

        public Builder(Rectangle r){
            this.rectangle = new Rectangle(r);
        }

        public Builder(int x, int y, int width, int height) {
            this.rectangle = new Rectangle(x, y, width, height);
        }


        public Builder maxHealth(double val){
            maxHealth = val;    return this;
        }
        public Builder reloadTime(int val){
            reloadTime = val;   return this;
        }
        public Builder ammoCapacity(int val){
            ammoCapacity = val; return this;
        }
        public Builder color(Color val){
            color = val;        return this;
        }
//        public Builder image(String path ){
//            var ii = new ImageIcon(path);
//            image  = ii.getImage();
//            return this;
//        }
        public Tank build(){
            return new Tank(this);
        }

    }
    private Tank(Builder builder){
        rectangle = builder.rectangle;
        maxHealth = builder.maxHealth;
        ammoCapacity = builder.ammoCapacity;
        reloadTime = builder.reloadTime;

        //image = builder.image;


        ammoLeft = builder.ammoCapacity;
        healthLeft = builder.maxHealth;
        reloadTimer = new Timer(builder.reloadTime, (actionEvent) -> ammoLeft = ammoCapacity );
        reloadTimer.start();
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public int getAmmoCapacity() {
        return ammoCapacity;
    }


    public int getAmmoLeft() {
        return ammoLeft;
    }

    public double getHealthLeft() {
        return healthLeft;
    }

//    public Image getImage(){
//        return image;
//    }

    @Override
    public String toString() {
        return "Tank{" +
//                "maxHealth=" + maxHealth +
//                ", reloadTime=" + reloadTime +
//                ", ammoCapacity=" + ammoCapacity +
//                ", image=" + image +
//                ", ammoLeft=" + ammoLeft +
//                ", healthLeft=" + healthLeft +
                ", ax=" + ax +
                ", ay=" + ay +
                ", actual_dx=" + actual_dx +
                ", actual_dy=" + actual_dy +
                ", prev_x=" + prev_x +
                ", prev_y=" + prev_y +
                ", a_pressed=" + a_pressed +
                ", w_pressed=" + w_pressed +
                ", d_pressed=" + d_pressed +
                ", s_pressed=" + s_pressed +
                '}';
    }
}
