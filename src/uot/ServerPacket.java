package uot;

import java.io.Serializable;
import java.util.List;

public class ServerPacket implements Serializable {

    private static final long serialVersionUID = 1787734974709445764L;
    private int serverTankX;
    private int serverTankY;
    private int clientTankX;
    private int clientTankY;
    private List<Coordinates> bullets;
    private boolean gameOver;
    private String winner;

    public ServerPacket(int serverTankX, int serverTankY, int clientTankX, int clientTankY, List<Coordinates> bullets, boolean gameOver, String winner) {
        this.serverTankX = serverTankX;
        this.serverTankY = serverTankY;
        this.clientTankX = clientTankX;
        this.clientTankY = clientTankY;
        this.bullets = bullets;
        this.gameOver = gameOver;
        this.winner = winner;
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

    public boolean isGameOver() { return gameOver; }

    public String getWinner() {
        return winner;
    }

    @Override
    public String toString() {
        return "ServerPacket{" +
                "serverTankX=" + serverTankX +
                ", serverTankY=" + serverTankY +
                ", clientTankX=" + clientTankX +
                ", clientTankY=" + clientTankY +
                ", bullets=" + bullets +
                ", gameOver=" + gameOver +
                ", winner='" + winner + '\'' +
                '}';
    }
}
