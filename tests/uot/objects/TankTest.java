package uot.objects;

import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.time.Duration;


import static org.junit.jupiter.api.Assertions.*;

class TankTest  {

    static Rectangle defaultRect = new Rectangle(100,100,10,10);
    Tank tank = new Tank.Builder(defaultRect).build();

    @Test
    void testCollision(){
        Tank tank1 = new Tank.Builder(0,0,10,10).build();
        Terrain obstacle = new Terrain(10,0,3,3);
        assertFalse(tank1.collision(obstacle), "Adjacent positioning should not be a collsion");
    }

    @Test
    void testCorrectCollision(){
        Tank tank1 = new Tank.Builder(0,0,10,10).build();
        Terrain obstacle = new Terrain(9,0,3,3);
        assertTrue(tank1.collision(obstacle));
    }
    @Test
    void testCollisionTank(){
        Tank tank1 = new Tank.Builder(0,0,10,10).build();
        Tank tank2 = new Tank.Builder(10,0,3,3).build();
        assertFalse(tank1.collision(tank2), "Adjacent positioning should not be a collsion");
    }
    @Test
    void testCorrectCollisionTank(){
        Tank tank1 = new Tank.Builder(0,0,10,10).build();
        Tank tank2 = new Tank.Builder(9,0,3,3).build();
        assertTrue(tank1.collision(tank2));
    }


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
        assertAll("Negative coordinates cause exception throw",
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
        assertAll("Shooting with 0 bullets left should not be possible",
                ()-> assertNotNull(b1),
                ()-> assertNull(b2)
        );
    }
    @Test
    void testReload() {
        int tReload = 300;
        Tank tank1 = new Tank.Builder(defaultRect).ammoCapacity(1).reloadTime(tReload).build();
        tank1.shoot(10,10);
        // possibly could fail depending on what timer Tank uses for the reload timing
        Timer timer = new Timer(tReload, (actionEvent -> assertNotNull(tank1.shoot(10,10))));
    }

    @Test
    void testSmallBounce(){
        final double smallBounce = 1.00001;
        testBounce(smallBounce);
        testLowVelocityBounce(smallBounce);
    }
    @Test
    void testBigBounce(){
        final double bigBounce = 6.0;
        testBounce(bigBounce);
        testLowVelocityBounce(6.0);
    }

    void testBounce(double bounceModifier) {
        Tank tank1 = new Tank.Builder(0,0,20,20).friction(1).speedCap(10).acceleration(2).bounceModifier(bounceModifier).build();
        Terrain obstacle = new Terrain(25,0, 5,5);
        tank1.setD_pressed(true);
        assertTimeoutPreemptively(Duration.ofMillis(50), ()-> {
                    while (!tank1.collision(obstacle)) {
                        tank1.move();
                    }
                }, "Tank never reaches the object - something wrong with movement");
        tank1.bounce(obstacle);
        tank1.move();
        assertFalse(tank1.collision(obstacle), "There should be no collision after bouncing");
    }

    void testLowVelocityBounce(double bounceModifier){
        Tank tank1 = new Tank.Builder(0,0,20,20).friction(1).speedCap(1).acceleration(0.2).bounceModifier(bounceModifier).build();
        Terrain obstacle = new Terrain(21,0, 5,5);
        tank1.setD_pressed(true);
        assertTimeoutPreemptively(Duration.ofMillis(50), ()-> {
            while (!tank1.collision(obstacle)) {
                tank1.move();
            }
        }, "Tank never reaches the object - something wrong with movement");
        tank1.bounce(obstacle);
        tank1.move();
        assertFalse(tank1.collision(obstacle), "There should be no collision after bouncing");
    }


    @Test
    void testAcceleration(){
        Tank tank1 = new Tank.Builder(0,0,20,20).friction(1).speedCap(10).acceleration(1).build();
        tank1.setS_pressed(true);
        tank1.move();
        tank1.move();
        assertEquals(2.0, tank1.getYVelocity() );
    }

    @Test
    void testSpeedCap(){
        final int CAP = 10;
        Tank tank1 = new Tank.Builder(0,0,20,20).friction(1).speedCap(10).acceleration(9).build();
        tank1.setS_pressed(true);
        tank1.move();
        tank1.move();
        assertEquals(CAP, tank1.getYVelocity() );
    }




    @Test
    void testMoveY() {
        Tank tank1 = new Tank.Builder(0,0,20,20).friction(1).speedCap(1).acceleration(1).build();
        tank1.setS_pressed(true);
        tank1.move();
        assertEquals(1, tank1.getY());
    }
    @Test
    void testMoveX() {
        Tank tank1 = new Tank.Builder(0,0,20,20).friction(1).speedCap(1).acceleration(1).build();
        tank1.setD_pressed(true);
        tank1.move();
        assertEquals(1, tank1.getX());
    }
    @Test
    void testFriction(){
        final int n_loop = 100;
        final double EPSILON = 0.01;
        Tank tank1 = new Tank.Builder(0,0,20,20).friction(0.9).speedCap(8).acceleration(8).build();
        tank1.setS_pressed(true);
        tank1.move();
        tank1.setS_pressed(false);
        for (int i =0; i < n_loop; ++i) {
            tank1.move();
        }
        assertTrue(tank1.getYVelocity() < EPSILON, "Velocity eventually should get very close to 0 due to friction");



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