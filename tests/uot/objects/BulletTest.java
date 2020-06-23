package uot.objects;

import org.junit.jupiter.api.Test;

import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

public class BulletTest {

    private static final int EPSILON = 2;


    @Test
    void testSimpleMoveX(){
        Bullet bullet = new Bullet(0,0,1,0,null, 1.0);
        bullet.move();
        assertEquals(1, bullet.getX());
    }

    @Test
    void testSimpleMoveLeftX(){
        Bullet bullet = new Bullet(1,0,0,0,null, 1.0);
        bullet.move();
        assertEquals(0, bullet.getX());
    }

    @Test
    void testSimpleMoveY(){
        Bullet bullet = new Bullet(0,0,0,1,null, 1.0);
        bullet.move();
        assertEquals(1, bullet.getY());
    }

    @Test
    void testSimpleMoveLeftY(){
        Bullet bullet = new Bullet(0,1,0,0,null, 1.0);
        bullet.move();
        assertEquals(0, bullet.getY());
    }

    @Test
    void testSimpleMoveXY() {
        Bullet bullet = new Bullet(0,0,1,1,null, 1.0);
        bullet.move();
        assertAll(
                ()-> assertEquals(1, bullet.getX()),
                ()-> assertEquals(1, bullet.getY())
        );
    }


    @Test
    void testComplicatedMoveX() {
        int targetX = 133;
        int startX = 177;
        double speed = 2.32;
        Bullet bullet = new Bullet(startX,0, 100, 2, null, speed);
        int n_loops = (int) Math.ceil((double)Math.abs(targetX - startX)/speed);
        for (int i = 0; i < n_loops; ++i){
            bullet.move();
        }
        assertTrue(Math.abs(targetX - bullet.getX()) <= EPSILON, "Bullet should be in the expected place +- EPSILON\n" + "TargetX = " + targetX + " actualX =  " + bullet.getX() + " EPSILON = " + EPSILON);

    }

    @Test
    void testComplicatedMoveY() {
        int targetY = 121;
        int startY = 187;
        double speed = 2.32;
        Bullet bullet = new Bullet(0,startY, 0, targetY, null, speed);
        int n_loops = (int) Math.ceil((double)Math.abs(targetY - startY)/speed);
        for (int i = 0; i < n_loops; ++i){
            bullet.move();
        }
        assertTrue(Math.abs(targetY - bullet.getY()) <= EPSILON, "Bullet should be in the expected place +- EPSILON\n" + "TargetY = " + targetY + " actualY =  " + bullet.getY() + " EPSILON = " + EPSILON);


    }


    @Test
    void testComplicatedMoveXY() {
        int targetX = 133;
        int startX = 177;
        int targetY = 200;
        int startY = 143;
        double speed = 3.5;
        Bullet bullet = new Bullet(startX,startY, targetX, targetY, null, speed);
        int n_loops = (int) Math.ceil(Math.hypot(Math.abs(targetX - startX), Math.abs(targetY - startY))/speed);

        for (int i = 0; i < n_loops; ++i){
            bullet.move();
        }
        assertAll("Bullet should be in the expected place +- EPSILON\n",
                ()-> assertTrue(Math.abs(targetY - bullet.getY()) <= EPSILON, "TargetY = " + targetY + " actualY =  " + bullet.getY() + " EPSILON = " + EPSILON),
                ()-> assertTrue(Math.abs(targetX - bullet.getX()) <= EPSILON, "TargetX = " + targetX + " actualX =  " + bullet.getX() + " EPSILON = " + EPSILON)
        );
    }

    @Test
    void testGetOrigin() {
        Tank tank = new Tank.Builder(1,1,1,1).build();
        Bullet bullet = new Bullet(tank.getX(),tank.getY(),100,100,tank);
        assertEquals(bullet.getOrigin(), tank);
    }
}
