package uot.objects;

import org.junit.jupiter.api.Test;

import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

public class BulletTest {
    static Rectangle defaultRect = new Rectangle(100,100,10,10);
    Tank tank = new Tank.Builder(defaultRect).build();



    @Test
    void move() {
        assertTrue(true);
    }

    @Test
    void getOrigin() {
        Bullet bullet = new Bullet(tank.getX(),tank.getY(),100,100,tank);
        assertEquals(bullet.getOrigin(), tank);
    }
}
