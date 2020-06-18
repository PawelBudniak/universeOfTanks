package uot.objects;

import java.awt.*;
import java.io.Serializable;

public class ForwardingTank implements Serializable {

    private static final long serialVersionUID = -7230755938297269827L;
    private final Tank tank;

    public ForwardingTank(Tank tank) {
        this.tank = tank;
    }

    public Bullet shoot(int x, int y) {
        return tank.shoot(x, y);
    }

    public boolean isOriginOf(Bullet b) {
        return tank.isOriginOf(b);
    }

    public void bounce(RectangularObject from) {
        tank.bounce(from);
    }

    public void bounce(ForwardingTank from) {
        tank.bounce(from.tank);
    }

    public boolean willCollide(RectangularObject other) {
        return tank.willCollide(other);
    }

    public boolean willCollide(ForwardingTank from) {
        return tank.willCollide(from.tank);
    }

    public void move() {
        tank.move();
    }

    public void keyPressed(int keyCode) {
        tank.keyPressed(keyCode);
    }

    public void keyReleased(int keyCode) {
        tank.keyReleased(keyCode);
    }

    public double hit(Bullet bullet) {
        return tank.hit(bullet);
    }

    public double getMaxHealth() {
        return tank.getMaxHealth();
    }

    public int getReloadTime() {
        return tank.getReloadTime();
    }

    public int getAmmoCapacity() {
        return tank.getAmmoCapacity();
    }

    public int getAmmoLeft() {
        return tank.getAmmoLeft();
    }

    public double getHealthLeft() {
        return tank.getHealthLeft();
    }

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

    public boolean collision(RectangularObject other) {
        return tank.collision(other);
    }

    public boolean collision(ForwardingTank other) {
        return tank.collision(other.tank);
    }

    public int getX() {
        return tank.getX();
    }

    public int getY() {
        return tank.getY();
    }

    public int getWidth() {
        return tank.getWidth();
    }

    public int getHeight() {
        return tank.getHeight();
    }

    public Rectangle getShape() {
        return tank.getShape();
    }

    @Override
    public String toString() {
        return tank.toString();
    }
}
