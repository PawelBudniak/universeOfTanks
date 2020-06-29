package uot;
import uot.objects.*;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        //JFrame frame = new OptionFrame();


        System.out.println("Press 1 for testing in single player mode, press 2 for multiplayer");
        Scanner s = new Scanner(System.in);
        int choice = s.nextInt();
        if (choice == 1)
            singlePlayer();
        else if (choice == 2)
            multiPlayer();

    }

    private static void singlePlayer(){
        Game game = new Game("mati", "seba", false);
        JFrame frame = new GameFrame(game.getDisplay());
        frame.setVisible(true);
    }
    private static void multiPlayer(){
        final int port = 4445;

        try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out =
                        new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in =
                        new ObjectInputStream(clientSocket.getInputStream())
        ) {
            clientSocket.setTcpNoDelay(true);
            Game game = new Game("Seba","laptok", out, in);
            JFrame frame = new GameFrame(game.getDisplay());
            frame.setVisible(true);

            while (!game.isNetworkingStopped() && !game.isOver()){
                ;
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

}
