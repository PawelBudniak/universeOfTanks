package uot.objects;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.*;

public class Tank extends RectangularObject {


    public static final double DEFAULT_HEALTH = 300.0;
    public static final int DEFAULT_RELOAD_TIME = 500;
    /** How many times can the tank shoot continuously before having to reload */
    public static final int DEFAULT_AMMO_CAPACITY = 5;
    public static final Color DEFAULT_COLOR = Color.blue;

    private static final double BOUNCE_MODIFIER = 2.5;
    private static final double SPEED = 0.4;
    private static final double SPEED_CAP = 6.0;
    private static final double FRICTION = 0.97;

    /** tank parameters */
    private final double maxHealth;
    private final int reloadTime;
    private final int ammoCapacity;
    private final Color color;

    /** current state (current coordinates are kept in the inherited protected rectangle field) */
    private int ammoLeft;
    private double healthLeft;
    // will be initialized to 0 by default
    private double ax;          // x direction acceleration
    private double ay;          // y direction acceleration
    private double actual_dx;   // approximate change in the y direction each tick -- is set with each key press
    private double actual_dy;   // approximate change in the x direction each tick -- is set with each key press
    private double prev_dx;     // previous change in the y direction -- is set each tick
    private double prev_dy;     // previous change in the y direction -- is set each tick
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

        // yellow
        if (from.getX() < getX() + getWidth() && prev_x + getWidth()<= from.getX()) {
            actual_dx = -BOUNCE_MODIFIER * prev_dx;
        }
        // red
         else if (from.getX() + from.getWidth() > getX() && prev_x >= from.getWidth() + from.getX()){
                actual_dx = -BOUNCE_MODIFIER * prev_dx;
        }
        // blue
         else if (from.getY() < getY() + getHeight() && prev_y + getHeight() <= from.getY()) {
            actual_dy = -BOUNCE_MODIFIER * prev_dy;
        }
        // purple
         else if (from.getY() + from.getHeight() > getY() && prev_y >= from.getY() + from.getHeight() ) {
                actual_dy = -BOUNCE_MODIFIER * prev_dy;
        }
//         else{
//             actual_dx = -BOUNCE_MODIFIER * prev_dx;
//             actual_dy = -BOUNCE_MODIFIER * prev_dy;
//        }

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

        prev_dx = actual_dx;
        prev_dy = actual_dy;

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
    public void keyPressed(KeyEvent e){
        int code = e.getKeyCode();
        switch(code){
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
    public void  keyReleased(KeyEvent e){
        int code = e.getKeyCode();
        switch(code){
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
        public Tank build(){
            return new Tank(this);
        }

    }
    private Tank(Builder builder){
        rectangle = builder.rectangle;
        maxHealth = builder.maxHealth;
        ammoCapacity = builder.ammoCapacity;
        reloadTime = builder.reloadTime;
        color = builder.color;


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

    public Color getColor() {
        return color;
    }

    public int getAmmoLeft() {
        return ammoLeft;
    }

    public double getHealthLeft() {
        return healthLeft;
    }



    @Override
    public String toString() {
        return "Tank{" +
                "ammoLeft=" + ammoLeft +
                ", healthLeft=" + healthLeft +
                ", prev_dx=" + prev_dx +
                ", prev_dy=" + prev_dy +
                ", rectangle=" + rectangle +
                '}';
    }
}
