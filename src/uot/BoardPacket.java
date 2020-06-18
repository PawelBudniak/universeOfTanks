package uot;

import uot.objects.Bullet;
import uot.objects.Terrain;

import java.io.Serializable;
import java.util.List;

public class BoardPacket implements Serializable {

    private static final long serialVersionUID = 7030644259386334167L;
    private Player[] players;
    private List<Bullet> bullets;
    private List<Terrain> terrain;

    public BoardPacket(Player[] players, List<Bullet> bullets, List<Terrain> terrain) {
        this.players = players;
        this.bullets = bullets;
        this.terrain = terrain;
    }

    public Player[] getPlayers() {
        return players;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public List<Terrain> getTerrain() {
        return terrain;
    }
}

