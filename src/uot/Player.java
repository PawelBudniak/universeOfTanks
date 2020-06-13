package uot;

import uot.objects.*;

import java.awt.*;
import java.awt.event.KeyEvent;


public class Player {
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
    public void keyPressed(KeyEvent e) { tank.keyPressed(e); }
    public void keyReleased(KeyEvent e) { tank.keyReleased(e); }
    public boolean willCollide(Player other) { return tank.willCollide(other.tank); }
    public void bounce(RectangularObject from) { tank.bounce(from); }
    public void bounce(Player from) { tank.bounce(from.getTank()); }
   public Image getImage() {return tank.getImage();}
    public int getX() { return tank.getX(); }
    public int getY() { return tank.getY(); }
    public int getWidth() { return tank.getWidth(); }
    public int getHeight() { return tank.getHeight(); }
    public Rectangle getShape() { return tank.getShape(); }
    public boolean isOriginOf(Bullet b) { return tank.isOriginOf(b); }


}
