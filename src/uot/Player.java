package uot;

import uot.objects.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.Serializable;


public class Player implements Serializable {
    private static final long serialVersionUID = 3304568958729910057L;
    private final String name;
    private final Tank tank;

    public Player(String name, Tank tank) {
        this.name = name;
        this.tank = tank;
    }

    public String getName() {
        return name;
    }

    public Tank getTank() {
        return tank;
    }
    public boolean collision(Player other){
        return tank.collision(other.tank);
    }
    public boolean collision(RectangularObject obj){
        return tank.collision(obj);
    }
    public double hit(Bullet bullet){
        return tank.hit(bullet);
    }
    public Bullet shoot(int x, int y){
        return tank.shoot(x,y);
    }
    public void move(){
        tank.move();
    }
    public boolean willCollide(RectangularObject obj){
        return tank.willCollide(obj);
    }
    public void keyPressed(int keyCode) { tank.keyPressed(keyCode); }
    public void keyReleased(int keyCode) { tank.keyReleased(keyCode); }
    public boolean willCollide(Player other) { return tank.willCollide(other.tank); }
    public void bounce(RectangularObject from) { tank.bounce(from); }
    public void bounce(Player from) { tank.bounce(from.getTank()); }
   //public Image getImage() {return tank.getImage();}
    public int getX() { return tank.getX(); }
    public int getY() { return tank.getY(); }
    public int getWidth() { return tank.getWidth(); }
    public int getHeight() { return tank.getHeight(); }
    public Rectangle getShape() { return tank.getShape(); }
    public boolean isOriginOf(Bullet b) { return tank.isOriginOf(b); }

    public void setA_pressed(boolean a_pressed) {
        tank.setA_pressed(a_pressed);
    }

    public void setW_pressed(boolean w_pressed) {
        tank.setW_pressed(w_pressed);
    }

    public void setD_pressed(boolean d_pressed) {
        tank.setD_pressed(d_pressed);
    }

    public void setS_pressed(boolean s_pressed) {
        tank.setS_pressed(s_pressed);
    }


}
