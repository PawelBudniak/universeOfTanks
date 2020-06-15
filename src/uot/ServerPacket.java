package uot;

import uot.objects.Bullet;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ServerPacket implements Serializable {

    private static final long serialVersionUID = 1787734974709445764L;
    private int serverTankX;
    private int serverTankY;
    private int clientTankY;
    private int clientTankX;
    private List<Coordinates> bullets;

    public ServerPacket(int serverTankX, int serverTankY, int clientTankY, int clientTankX, List<Coordinates> bullets) {
        this.serverTankX = serverTankX;
        this.serverTankY = serverTankY;
        this.clientTankY = clientTankY;
        this.clientTankX = clientTankX;
        this.bullets = bullets;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getServerTankX() {
        return serverTankX;
    }

    public int getServerTankY() {
        return serverTankY;
    }

    public int getClientTankY() {
        return clientTankY;
    }

    public int getClientTankX() {
        return clientTankX;
    }

    public List<Coordinates> getBullets() {
        return bullets;
    }
}
