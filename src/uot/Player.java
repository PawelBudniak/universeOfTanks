package uot;

import uot.objects.*;

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
    public void move(){
        tank.move();
    }
    public boolean willCollide(RectangularObject obj){
        return tank.willCollide(obj);
    }

}
