package uot;
import uot.objects.*;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
//        Tank t = new Tank.Builder(10, 10, 300 ,400).maxHealth(20.3).ammoCapacity(4).color(Color.BLACK).build();
//        Bullet b = new Bullet(10,10,15,60, t);
//
//        for(int i = 0; i<=100;i++)
//        {
//            b.move();
//            System.out.println("lokalizacja: "+ b.getX()+ " " + b.getY());
//        }
        EventQueue.invokeLater(()->
        {
            JFrame frame = new GameFrame();
            frame.setVisible(true);
        });

        System.out.println("Test");
	// write your code here
    }
}
