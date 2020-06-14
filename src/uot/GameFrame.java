package uot;

import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame(JPanel p){
        super("Universe of Tanks");
        //Game game = new Game(500, 500,"Seba","Mati");
        add(p);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
