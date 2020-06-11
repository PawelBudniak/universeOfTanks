package uot;

import uot.objects.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Random;

public class Game {
    private static final int TICK = 20;
    private static final int TANK_LEN = 15;
    private static final int TANK_WID = 20;
    private static final int START_X = 80;
    private static final Color P1_COLOR = Color.GREEN;
    private static final Color P2_COLOR = Color.BLUE;
    public static final int N_TERRAIN_BLOCKS = 8;
    private int boardLength;
    private int boardWidth;
    private Timer gameClock;
    LinkedList<Terrain> terrain;
    LinkedList<Bullet> bullets;
    Player player1;
    Player player2;


    public Game(int boardLength, int boardWidth, String p1_nick, String p2_nick){
        this.boardWidth = boardWidth;
        this.boardLength = boardLength;
        this.player1 = new Player(p1_nick,
                new Tank.Builder(START_X, boardLength/2, TANK_WID, TANK_LEN).color(P1_COLOR).build());
        this.player2 = new Player(p2_nick,
                new Tank.Builder(boardLength - START_X, boardLength/2, TANK_WID, TANK_LEN).color(P2_COLOR).build());
        generateWalls();
        generateTerrain();
        gameClock = new Timer(TICK, new GameClock());

    }



    /** generate terrain in the middle of the board */
    // prevent terrain collisions?
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
        terrain.add(new Terrain(0, boardLength - WALL_WIDTH, boardWidth, WALL_WIDTH));       // bottom
    }



    private class GameClock implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            for (Bullet bullet: bullets){
                // bullets <-> players collisions
                if (player1.collision(bullet)) {
                    player1.hit(bullet);
                    bullets.remove(bullet);
                }
                if (player2.collision(bullet)){
                    player2.hit(bullet);
                    bullets.remove(bullet);
                }
                // bullets <-> terrain collisions
                for (Terrain block: terrain){
                    if (bullet.collision(block)){
                        bullets.remove(bullet);
                    }
                }
            }
            // players <-> terrain collisions (prevent them beforehand)
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
