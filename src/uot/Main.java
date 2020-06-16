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

        System.out.println("Press 1 for testing in single player mode, press 2 for multiplayer");
        Scanner s = new Scanner(System.in);
        int choice = s.nextInt();
        if (choice == 1)
            singlePlayer();
        else if (choice == 2)
            multiPlayer();

    }

    private static void singlePlayer(){
        EventQueue.invokeLater(()->
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

}
