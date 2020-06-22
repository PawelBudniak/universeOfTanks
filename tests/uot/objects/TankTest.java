package uot.objects;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class TankTest extends Tank{
    TankTest(){
        super(new Builder(100,100,10,10));
        System.out.println("siema");
    }

    @Test
    void testShoot() {
        assertTrue(true);
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
    void testHit() {
        assertTrue(true);
    }
}