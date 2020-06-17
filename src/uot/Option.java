package uot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Option extends JPanel implements ActionListener {

        final JButton button1;
        final JButton button2;


        public Option() {
            button1 = new JButton("gra jednoosobowa");
            button2 = new JButton("gra dwuosobowa");

            button1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    singlePlayer();

                }
            });
            button2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    multiPlayer();
                }
            });

            add(button1);
            add(button2);
        }


        private static void singlePlayer() {
            EventQueue.invokeLater(() ->
            {
                Game game = new Game("mati", "seba");
                JFrame frame = new GameFrame(game.getDisplay());
                frame.setVisible(true);
            });
        }

        private static void multiPlayer(){
            final int port = 4445;

            try (
                    ServerSocket serverSocket = new ServerSocket(port);
                    Socket clientSocket = serverSocket.accept();
//                ObjectOutputStream out =
//                        new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream(), 65356));
//                ObjectInputStream in =
//                        new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream(), 65356))
                    ObjectOutputStream out =
                            new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream in =
                            new ObjectInputStream(clientSocket.getInputStream())
            ) {
                clientSocket.setTcpNoDelay(true);
                System.out.println(clientSocket.getSendBufferSize());
                //out.writeObject(new ServerPacket(null, null, null));
                Game game = new Game("Seba","laptok", out, in);
                game.sendBoard();
                JFrame frame = new GameFrame(game.getDisplay());
                frame.setVisible(true);


                while (!game.isOver()){
                    ;
                }
            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port "
                        + port + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            if(button1.getModel().isPressed())
            {
                singlePlayer();
            }
            if(button2.getModel().isPressed())
            {
                multiPlayer();
            }
        }


}
