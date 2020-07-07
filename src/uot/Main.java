package uot;

import javax.swing.*;
import java.awt.*;

import java.net.*;
import java.io.*;
import java.util.concurrent.CountDownLatch;

public class Main {

    private static GameFrame frame;
    private static JPanel option;
    public static CountDownLatch finished = new CountDownLatch(1);

    public static void main(String[] args) {

        //JFrame frame = new OptionFrame();
        EventQueue.invokeLater(() -> {
            option = new Option();
            frame = new GameFrame(option);
            frame.setVisible(true);
        });

    }

    private static void singlePlayer(){
        // using frame.switchPanel() here causes key inputs to be ignored until the window is minimized and opened again for some reason, so a new frame is constructed instead
        frame.setVisible(false);
        frame.getContentPane().remove(option);
        Game game = new Game("mati", "seba", false);

        frame = new GameFrame(game.getDisplay());
        frame.setVisible(true);

        frame.getContentPane().add(game.getDisplay());
    }
    private static void multiPlayer() {
        final int port = 4445;

        JPanel waitingPanel = new JPanel() {
            {
                setPreferredSize(new Dimension(Game.BOARD_WIDTH, Game.BOARD_LENGTH));
            }
            @Override
            public void paint(Graphics g){
                AbstractEngine.drawMsg(g, "Waiting for other player...", this);
            }
        };

        EventQueue.invokeLater(() ->
            frame.switchPanel(option, waitingPanel)
        );


        Runnable r = new Runnable() {
            @Override
            public void run() {
                try (
                        ServerSocket serverSocket = new ServerSocket(port);
                        Socket clientSocket = serverSocket.accept();
                        ObjectOutputStream out =
                                new ObjectOutputStream(clientSocket.getOutputStream());
                        ObjectInputStream in =
                                new ObjectInputStream(clientSocket.getInputStream())
                ) {
                    clientSocket.setTcpNoDelay(true);

                    Game game = new Game("Player1", "Player2", out, in);
                    EventQueue.invokeLater(() ->
                            frame.switchPanel(waitingPanel, game.getDisplay()
                    ));

                    finished.await();

                    // wait some time so that the gameOver packet will have time to get delivered before closing the socket
                    try {
                        Thread.sleep(300);
                    }catch(InterruptedException e){
                        Thread.currentThread().interrupt();
                    }
                    game.stopNetworking();

                } catch (IOException e) {
                    System.out.println("Exception caught when trying to listen on port "
                            + port + " or listening for a connection");
                    System.out.println(e.getMessage());
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }


    static class Option extends JPanel  {

        final JButton button1;
        final JButton button2;


        public Option() {
            button1 = new JButton("Single Player");
            button2 = new JButton("Multi Player");

            button1.addActionListener(e -> singlePlayer());
            button2.addActionListener(e -> multiPlayer());

            add(button1);
            add(button2);
        }




    }

}



