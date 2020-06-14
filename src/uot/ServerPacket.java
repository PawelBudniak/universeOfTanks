package uot;

import uot.objects.Bullet;
import uot.objects.Terrain;

import java.util.LinkedList;

public class ServerPacket {
    private Player[] players;
    private LinkedList<Bullet> bullets;
    private LinkedList<Terrain> terrain;

    public ServerPacket(Player[] players, LinkedList<Bullet> bullets, LinkedList<Terrain> terrain) {
        this.players = players;
        this.bullets = bullets;
        this.terrain = terrain;
    }

    public Player[] getPlayers() {
        return players;
    }

    public LinkedList<Bullet> getBullets() {
        return bullets;
    }

    public LinkedList<Terrain> getTerrain() {
        return terrain;
    }
}

