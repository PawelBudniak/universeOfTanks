package uot;

import javax.swing.*;

import javax.swing.*;

public class OptionFrame extends JFrame {

    public OptionFrame(){
        JFrame frame = new JFrame("wybor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new Option());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

