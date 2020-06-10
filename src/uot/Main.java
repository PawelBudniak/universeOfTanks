package uot;
import uot.objects.*;

import java.awt.*;

public class Main {

    public static void main(String[] args) {
        Tank t = new Tank.Builder(10, 10, 300 ,400).maxHealth(20.3).ammoCapacity(4).color(Color.BLACK).build();
        System.out.println("Test");
	// write your code here
    }
}
