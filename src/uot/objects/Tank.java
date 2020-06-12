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

    private static final int BOUNCE_MODIFIER = 2;
    private static final int SPEED = 2;
    private static final int SPEED_CAP = 6;

    /** tank parameters */
    private final double maxHealth;
    private final int reloadTime;
    private final int ammoCapacity;
    private final Color color;

    /** current state (current coordinates are kept in the inherited protected rectangle field) */
    private int ammoLeft;
    private double healthLeft;
    // will be initialized to 0 by default
    private int dy;         // change in the y direction each tick -- is set with each key press
    private int dx;         // change in the x direction each tick -- is set with each key press
    private int prev_dx;    // previous change in the y direction -- is set each tick
    private int prev_dy;    // previous change in the y direction -- is set each tick
    private Timer reloadTimer;
    // (to nie jest dobry pomysl jak cos, blokuje sie w bounce'ach co sekunde xD)
    private Timer bounceTimer;  // disable moving commands in the opposite direction for BOUNCE_TIME after bouncing
    private static int BOUNCE_TIME = 120;
    private boolean bouncingX = false;
    private boolean bouncingY = false;

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
    public void bounce() {
        bouncingX = true;
        bouncingY = true;
        bounceTimer.restart();
        if (dx > 0)
            dx = -Math.min(BOUNCE_MODIFIER * prev_dx, SPEED_CAP);
        else
            dx = -Math.max(BOUNCE_MODIFIER * dx, -SPEED_CAP);
        if (dy > 0)
            dy = -Math.min(BOUNCE_MODIFIER * prev_dy, SPEED_CAP);
        else
            dy = -Math.max(BOUNCE_MODIFIER * dy, -SPEED_CAP);
    }
    // to albo bounce
    public boolean willCollide(RectangularObject other){
        return new Tank.Builder(getX()+dx, getY() + dy, getWidth(), getHeight()).build().collision(other);
    }

    public void move(){
        prev_dx = dx;
        setX(getX() + dx);
        prev_dy = dy;
        setY(getY() + dy);
    }
    public void keyPressed(KeyEvent e){
        if (bouncingX || bouncingY) return;
        int code = e.getKeyCode();
        switch(code){
            case KeyEvent.VK_W:
                dy = -SPEED;
                break;
            case KeyEvent.VK_A:
                dx = -SPEED;
                break;
            case KeyEvent.VK_D:
                dx = SPEED;
                break;
            case KeyEvent.VK_S:
                dy = SPEED;
                break;
        }
    }
    public void  keyReleased(KeyEvent e){
        if (bouncingX || bouncingY) return;
        int code = e.getKeyCode();
        switch(code){
            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
                dy = 0;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_D:
                dx = 0;
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
        bounceTimer = new Timer(BOUNCE_TIME,        (actionEvent) ->{
            bouncingX = false;
            bouncingY = false;
        });
        reloadTimer.start();
        bounceTimer.start();

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
                ", dy=" + dy +
                ", dx=" + dx +
                ", prev_dx=" + prev_dx +
                ", prev_dy=" + prev_dy +
                ", rectangle=" + rectangle +
                '}';
    }
}
