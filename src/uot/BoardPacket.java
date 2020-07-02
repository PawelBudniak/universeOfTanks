package uot;

import uot.objects.Bullet;
import uot.objects.Terrain;

import java.io.Serializable;
import java.util.List;
import java.util.Queue;

public class BoardPacket implements Serializable {

    private static final long serialVersionUID = 7030644259386334167L;
    private List<Terrain> terrain;

    public BoardPacket(List<Terrain> terrain) {
        this.terrain = terrain;
    }

    public List<Terrain> getTerrain() {
        return terrain;
    }
}

