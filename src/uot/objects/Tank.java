package uot.objects;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.lang.*;

public class Tank extends RectangularObject implements Serializable {

    private static final long serialVersionUID = 5469024326928012237L;

    public static final double DEFAULT_HEALTH = 300.0;
    public static final int DEFAULT_RELOAD_TIME = 500;
    /** How many times can the tank shoot continuously before having to reload */
    public static final int DEFAULT_AMMO_CAPACITY = 5;
    public static final Color DEFAULT_COLOR = Color.blue;

    private static final double DEFAULT_BOUNCE_MODIFIER = 2.5;
    private static final double DEFAULT_ACCELERATION = 0.5;
    private static final double DEFAULT_SPEED_CAP = 6.0;
    private static final double DEFAULT_FRICTION = 0.97;

    private Timer reloadTimer;


    /** tank parameters */
    private final double maxHealth;
    private final int reloadTime;
    private final int ammoCapacity;
    private double bounceModifier;
    private double acceleration;
    private double speedCap;
    private double friction;

    //private final Image image;

    /** current state (current coordinates are kept in the inherited protected rectangle field) */

    private int ammoLeft;
    private double healthLeft;
    // will be initialized to 0 by default
    private double ax;          // x direction acceleration
    private double ay;          // y direction acceleration
    private double actual_dx;   // approximate change in the y direction each tick
    private double actual_dy;   // approximate change in the x direction each tick
    private double prev_x;      // x coordinate in the previous tick
    private double prev_y;      // y coordinate in the previous tick
    private boolean a_pressed;
    private boolean w_pressed;
    private boolean d_pressed;
    private boolean s_pressed;

    /** if facingRight is true the bullet will appear on the right side of the tank, if it's false, it will appear on the left side
     *  the bullet always appears in the middle height-wise */
    public Bullet shoot(int x, int y, boolean facingRight){
        if (x < 0 || y < 0)
            throw new IllegalArgumentException("Coordinates must be positive integers");

        final int SHIFT = 5;

        if (ammoLeft > 0) {
            --ammoLeft;
            reloadTimer.restart();
            if (facingRight)
                return new Bullet(getX() + getWidth()-SHIFT, getY()+getHeight()/2, x, y, this);
            else
                return new Bullet(getX() +SHIFT, getY()+getHeight()/2, x, y, this);
        }
        else {
            return null;
        }
    }

    public Bullet shoot(int x, int y){ return shoot(x,y, true); }

    public boolean isOriginOf(Bullet b){
        return b.getOrigin() == this;
    }

    public void bounce(RectangularObject from) {

        // object on the right
        if (from.getX() <= getX() + getWidth() && prev_x + getWidth()<= from.getX()) {
            actual_dx = -bounceModifier * actual_dx;
        }
        // object on the left
        else if (from.getX() + from.getWidth() >= getX() && prev_x >= from.getWidth() + from.getX()){
            actual_dx = -bounceModifier * actual_dx;
        }
        // object below the tank
        else if (from.getY() <= getY() + getHeight() && prev_y + getHeight() <= from.getY()) {
            actual_dy = -bounceModifier * actual_dy;
        }
        // object above the tank
        else if (from.getY() + from.getHeight() >= getY() && prev_y >= from.getY() + from.getHeight() ) {
            actual_dy = -bounceModifier * actual_dy;
        }
        // last ditch effort
        else{
            actual_dx = -bounceModifier * actual_dx;
            actual_dy = -bounceModifier * actual_dy;
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
            next_dx = Math.min(next_dx, speedCap);
        else
            next_dx = Math.max(next_dx, -speedCap);
        next_dx = friction * next_dx;
        return next_dx;

    }
    private double getNextDy(){
        double next_dy = actual_dy;
        next_dy += ay;
        if (next_dy > 0)
            next_dy = Math.min(next_dy, speedCap);
        else
            next_dy = Math.max(next_dy, -speedCap);
        next_dy = friction * next_dy;
        return next_dy;
    }

    public void move(){


        if (a_pressed && !d_pressed)
            ax = -acceleration;
        else if (d_pressed)
            ax = acceleration;
        else
            ax = 0;
        if (s_pressed && !w_pressed)
            ay = acceleration;
        else if(w_pressed)
            ay = -acceleration;
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

    public void setA_pressed(boolean a_pressed) {
        this.a_pressed = a_pressed;
    }

    public void setW_pressed(boolean w_pressed) {
        this.w_pressed = w_pressed;
    }

    public void setD_pressed(boolean d_pressed) {
        this.d_pressed = d_pressed;
    }

    public void setS_pressed(boolean s_pressed) {
        this.s_pressed = s_pressed;
    }



    public static class Builder{
        /** Required parameters */
        private final Rectangle rectangle;


        /** Optional parameters */
        private double maxHealth = DEFAULT_HEALTH;
        private int reloadTime = DEFAULT_RELOAD_TIME;
        private int ammoCapacity = DEFAULT_AMMO_CAPACITY;
        private double bounceModifier = DEFAULT_BOUNCE_MODIFIER;
        private double acceleration = DEFAULT_ACCELERATION;
        private double speedCap = DEFAULT_SPEED_CAP;
        private double friction = DEFAULT_FRICTION;

        public Builder(Rectangle r){
            this.rectangle = new Rectangle(r);
        }

        public Builder(int x, int y, int width, int height) {
            this.rectangle = new Rectangle(x, y, width, height);
        }


        public Builder bounceModifier(double bounceModifier) {
            this.bounceModifier = bounceModifier; return this;
        }

        public Builder acceleration(double acceleration) {
            this.acceleration = acceleration; return this;
        }

        public Builder speedCap(double speedCap) {
            this.speedCap = speedCap; return this;
        }

        public Builder friction(double friction) {
            this.friction = friction; return this;
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
        public Tank build(){
            return new Tank(this);
        }

    }
    // protected for testing
    protected Tank(Builder builder){
        rectangle = builder.rectangle;
        maxHealth = builder.maxHealth;
        ammoCapacity = builder.ammoCapacity;
        reloadTime = builder.reloadTime;
        ammoLeft = builder.ammoCapacity;
        healthLeft = builder.maxHealth;
        bounceModifier = builder.bounceModifier;
        acceleration = builder.acceleration;
        friction = builder.friction;
        speedCap = builder.speedCap;


        reloadTimer = new Timer(builder.reloadTime, (actionEvent) -> ammoLeft = ammoCapacity );
        reloadTimer.start();
    }

    @Override
    public String toString() {
        return "Tank{" +
//                "maxHealth=" + maxHealth +
//                ", reloadTime=" + reloadTime +
//                ", ammoCapacity=" + ammoCapacity +
//                ", image=" + image +
//                ", ammoLeft=" + ammoLeft +
//                ", healthLeft=" + healthLeft +
                "x = " + getX() +
                "y = " + getY() +
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
