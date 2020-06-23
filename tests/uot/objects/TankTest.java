package uot.objects;

import org.junit.jupiter.api.*;

import java.awt.*;
import java.io.BufferedOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class TankTest  {

    static Rectangle defaultRect = new Rectangle(100,100,10,10);
    Tank tank = new Tank.Builder(defaultRect).build();


    @Test
    void testShoot(){
        Tank tank1 = new Tank.Builder(defaultRect).ammoCapacity(3).build();
        assertAll(
                 ()  ->     assertNotNull(tank1.shoot(10,10)),
                 ()  ->     assertNotNull(tank1.shoot(12,12)),
                 ()  ->     assertNotNull(tank1.shoot(300,1000))
        );
    }


    @Test
    void testShoot2() {
        assertAll("Negative coordinates cause exepction throw",
                () -> assertThrows(IllegalArgumentException.class, ()-> tank.shoot(-1,2)),
                () -> assertThrows(IllegalArgumentException.class, ()-> tank.shoot(1,-2)),
                () -> assertThrows(IllegalArgumentException.class, ()-> tank.shoot(-1,-2))
        );
    }

    @Test
    void testNoAmmo() {
        int tReload = 100;
        Tank tank1 = new Tank.Builder(defaultRect).ammoCapacity(1).reloadTime(tReload).build();
        Bullet b1 = tank1.shoot(10,10);
        Bullet b2 = tank1.shoot(10,10);
        assertAll("Reload",
                ()-> assertNotNull(b1),
                ()-> assertNull(b2)
        );
    }
    @Test
    void testReload() throws InterruptedException {
        int tReload = 100;
        Tank tank1 = new Tank.Builder(defaultRect).ammoCapacity(1).reloadTime(tReload).build();
        Bullet b1 = tank1.shoot(10,10);
        Thread.sleep(tReload*2);
        assertNotNull(tank1.shoot(10,10));
    }

    @Test
    void testBounce() {
        assertTrue(true);
    }

    @Test
    void testWillCollide() {
        assertTrue(true);
    }

    @Test
    void testMove() {
        assertTrue(true);
    }

    @Test
    void testSelfHit() {
        double initial_hp = tank.getHealthLeft();
        Bullet b = new Bullet(10,10,10,10,tank);
        tank.hit(b);
        assertEquals(initial_hp, tank.getHealthLeft());
    }
    @Test
    void testHit(){
        double initial_hp = tank.getHealthLeft();
        Bullet b = new Bullet(10,10,10,10, new Tank.Builder(defaultRect).build());
        double dmg = b.getDmg();
        tank.hit(b);
        assertEquals(initial_hp - dmg, tank.getHealthLeft());
    }
}