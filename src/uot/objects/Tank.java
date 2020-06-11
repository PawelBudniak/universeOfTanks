package uot.objects;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Tank extends RectangularObject {


    public static final double DEFAULT_HEALTH = 300.0;
    public static final int DEFAULT_RELOAD_TIME = 500;
    /** How many times can the tank shoot continuously before having to reload */
    public static final int DEFAULT_AMMO_CAPACITY = 5;
    public static final Color DEFAULT_COLOR = Color.blue;

    private static final int BOUNCE_MODIFIER = 2;
    private static final int SPEED = 1;

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


    public Bullet shoot(int x, int y){
        return new Bullet(getX(), getY(), x, y);
    }


//    public void bounce(){
//        //tutaj by trzeba chyba inaczej ruchy liczyc, w sensie zrobic enum Direction
//        if (prev_dx != 0){
//            dx = -BOUNCE_MODIFIER * dx;
//        }
//
//    }
    // to albo bounce
    public boolean willCollide(RectangularObject other){
        return new Tank.Builder(getX()+dx, getY() + dy, getWidth(), getHeight()).build().collision(other);
    }

    public void move(){
        //prev_dx = dx;
        //prev_dy = dy;
        setX(getX() + dx);
        setY(getY() + dy);
    }
    // SPEED = 1 dalem se
    public void keyPressed(KeyEvent e){
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




    public double hit(Bullet bullet){
        // if my bullet => nothing()?
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
        maxHealth = builder.maxHealth;
        ammoCapacity = builder.ammoCapacity;
        reloadTime = builder.reloadTime;
        color = builder.color;
        ammoLeft = builder.ammoCapacity;
        healthLeft = builder.maxHealth;
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
