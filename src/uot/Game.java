package uot;

import uot.objects.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.activation.ActivationInstantiator;
import java.util.LinkedList;
import java.util.Random;

public class Game {
    public static final int N_TERRAIN_BLOCKS = 8;
    private int boardLength;
    private int boardWidth;
    private Timer gameClock;
    LinkedList<Terrain> terrain;
    LinkedList<Bullet> bullets;
    Player player1;
    Player player2;




    /** generate terrain in the middle of the board */
    private void generateTerrain(){
        Random random = new Random();
        for (int i = 0; i < N_TERRAIN_BLOCKS; i++) {
            Terrain new_block;
            do{
                new_block = new Terrain(random.nextInt(), random.nextInt());
            }while(player1.collision(new_block) || player2.collision(new_block));
            terrain.add(new_block);
        }

    }
    /** generate the walls at the edges of the board */
    private void generateWalls(){
        // najpierw dam 5 px szerokosci pozniej mozna zminiejszyc /zwiekszyc
        final int WALL_WIDTH = 5;
        terrain.add(new Terrain(0,0, boardWidth, WALL_WIDTH));                               // upper
        terrain.add(new Terrain(0,0, WALL_WIDTH, boardLength));                              // left
        terrain.add(new Terrain(boardWidth - WALL_WIDTH,0, WALL_WIDTH, boardLength));        // right
        terrain.add(new Terrain(0, boardLength - WALL_WIDTH, boardWidth, WALL_WIDTH))        // bottom
    }



    private class Tick implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            for (Bullet bullet: bullets){
                // bullets <-> players collisions
                if (player1.collision(bullet)) {
                    player1.hit(bullet);
                    bullets.remove(bullet)
                }
                if (player2.collision(bullet){
                    player2.hit(bullet);
                    bullets.remove(bullet);
                }
                // bullets <-> terrain collisions
                for (Terrain block: terrain){
                    if (bullet.collision(terrain)){
                        bullets.remove(bullet);
                    }
                }
            }
            // players <-> terrain collisions
            boolean p1_collision = terrain.stream().anyMatch(t -> player1.willCollide(t));
            boolean p2_collision = terrain.stream().anyMatch(t -> player2.willCollide(t));
            // players <-> players collisions
            // bullets <-> bullets collisions?
            if (!p1_collision)
                player1.move();
            if (!p2_collision)
                player2.move();
        }
    }






}
