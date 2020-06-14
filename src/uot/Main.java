package uot;
import uot.objects.*;

import javax.swing.*;
import java.awt.*;

import java.net.*;
import java.io.*;

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

//        EventQueue.invokeLater(()->
//        {
//            Game game = new Game(500, 500, "mati", "seba");
//            JFrame frame = new GameFrame(game.getDisplay());
//            frame.setVisible(true);
//        });



        try (
                ServerSocket serverSocket = new ServerSocket(4444);
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out =
                        new ObjectOutputStream(clientSocket.getOutputStream());

//                PrintWriter out =
//                        new PrintWriter(clientSocket.getOutputStream(), true);
//                BufferedReader in = new BufferedReader(
//                        new InputStreamReader(clientSocket.getInputStream()));
                ObjectInputStream in =
                        new ObjectInputStream(clientSocket.getInputStream());
        ) {
            out.writeObject(new ServerPacket(null, null, null));
            Game game = new Game(500, 500,"Seba","Mati", out, in);
            JFrame frame = new GameFrame(game.getDisplay());
            //frame.add(game.getDisplay());
            frame.setVisible(true);


//            String inputLine, outputLine;
//
//            // Initiate conversation with client
//            System.out.println("przed");
//            out.println("siema tutej serwer here");
//            out.println("wiad 2");
//
//            while ((inputLine = in.()) != null) {
//                System.out.println("weszlem");
//
//                outputLine = inputLine;
//                out.println(outputLine);
//                System.out.println(outputLine);
//                int input = Integer.parseInt(inputLine);
//                game.getInput(input, input);
//
//                if (outputLine.equals("Bye."))
//                    break;
//            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + 4444 + " or listening for a connection");
            System.out.println(e.getMessage());
        }


        System.out.println("Test");
	// write your code here
    }
}
