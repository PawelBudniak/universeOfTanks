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

    private static final double DEFAULT_BOUNCE_MODIFIER = 1.1;
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
    private double xVelocity;   // approximate change in the y direction each tick
    private double yVelocity;   // approximate change in the x direction each tick
    private double prev_x;      // x coordinate in the previous tick
    private double prev_y;      // y coordinate in the previous tick
    private boolean a_pressed;
    private boolean w_pressed;
    private boolean d_pressed;
    private boolean s_pressed;
    private boolean bouncedLastTick;
    private int bounceLockCounter;
    private int BOUNCE_LOCK_LIMIT = 100;


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
            xVelocity = -bounceModifier * xVelocity;
        }
        // object on the left
        else if (from.getX() + from.getWidth() >= getX() && prev_x >= from.getWidth() + from.getX()){
            xVelocity = -bounceModifier * xVelocity;
        }
        // object below the tank
        else if (from.getY() <= getY() + getHeight() && prev_y + getHeight() <= from.getY()) {
            yVelocity = -bounceModifier * yVelocity;
        }
        // object above the tank
        else if (from.getY() + from.getHeight() >= getY() && prev_y >= from.getY() + from.getHeight() ) {
            yVelocity = -bounceModifier * yVelocity;
        }
        // last ditch effort
        else{
            xVelocity = -bounceModifier * xVelocity;
            yVelocity = -bounceModifier * yVelocity;
        }
        yVelocity = getCappedVelocity(yVelocity);
        xVelocity = getCappedVelocity(xVelocity);

        bouncedLastTick = true;

    }
    // to albo bounce
    public boolean willCollide(RectangularObject other){
        return new Tank.Builder(getX()+(int)getNextDx(), getY() + (int)getNextDy(), getWidth(), getHeight()).build().collision(other);
    }

    private double getCappedVelocity(double v){
        if (v > 0)
            return Math.min(v,speedCap);
        else
            return Math.max(v, -speedCap);
    }

    private double getNextDx(){
        double nextVx = xVelocity;
        nextVx += ax;
        nextVx = getCappedVelocity(nextVx);

        nextVx = friction * nextVx;
        return nextVx;

    }
    private double getNextDy(){
        double nextVy = yVelocity;
        nextVy += ay;
        nextVy = getCappedVelocity(nextVy);
        nextVy = friction * nextVy;
        return nextVy;
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
        else if (w_pressed)
            ay = -acceleration;
        else
            ay = 0;


        // if there was a bounce - prevent velocity change for one tick, so the tank can safely bounce without getting stuck
        if (!bouncedLastTick && bounceLockCounter < BOUNCE_LOCK_LIMIT) {
            xVelocity = getNextDx();
            yVelocity = getNextDy();
            bounceLockCounter = 0;
        }
        else
            ++bounceLockCounter;

        prev_x = getX();
        prev_y = getY();

        int dx = (int)Math.round(xVelocity);
        int dy = (int)Math.round(yVelocity);

        setX(getX() + dx);
        setY(getY() + dy);

        bouncedLastTick = false;
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

    public double getXVelocity() {
        return xVelocity;
    }

    public double getYVelocity() {
        return yVelocity;
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
            if (bounceModifier < 1)
                throw new IllegalArgumentException("bounceModifier has to be bigger or equal 1");
            this.bounceModifier = bounceModifier; return this;
        }

        public Builder acceleration(double acceleration) {
            this.acceleration = acceleration; return this;
        }

        public Builder speedCap(double speedCap) {
            this.speedCap = speedCap; return this;
        }

        public Builder friction(double friction) {
            if (friction > 1 || friction < 0)
                throw new IllegalArgumentException("Friction has to be a value between 0 and 1");
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
                ", actual_dx=" + xVelocity +
                ", actual_dy=" + yVelocity +
                ", prev_x=" + prev_x +
                ", prev_y=" + prev_y +
                ", a_pressed=" + a_pressed +
                ", w_pressed=" + w_pressed +
                ", d_pressed=" + d_pressed +
                ", s_pressed=" + s_pressed +
                '}';
    }
}
