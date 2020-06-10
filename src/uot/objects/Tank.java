package uot.objects;

import java.awt.*;

public class Tank extends RectangularObject {

    public static final double DEFAULT_HEALTH = 300.0;
    public static final int DEFAULT_RELOAD_TIME = 500;
    public static final int DEFAULT_AMMO_CAPACITY = 5;      /** How many times can the tank shoot continuously before having to reload */
    public static final Color DEFAULT_COLOR = Color.blue;

    /** tank parameters */
    private final double maxHealth;
    private final int reloadTime;
    private final int ammoCapacity;
    private final Color color;

    /** current state */
    private int ammoLeft;
    private double healthLeft;

    public double hit(Bullet bullet){
        return health -= bullet.getDmg();
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
}
