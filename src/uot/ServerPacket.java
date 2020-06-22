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
    private double serverHealth;
    private double clientHealth;

    public ServerPacket(int serverTankX, int serverTankY, int clientTankX, int clientTankY, List<Coordinates> bullets, boolean gameOver, String winner, double serverHealth, double clientHealth) {
        this.serverTankX = serverTankX;
        this.serverTankY = serverTankY;
        this.clientTankX = clientTankX;
        this.clientTankY = clientTankY;
        this.bullets = bullets;
        this.gameOver = gameOver;
        this.winner = winner;
        this.serverHealth = serverHealth;
        this.clientHealth = clientHealth;
    }


    public double getServerHealth() {
        return serverHealth;
    }

    public double getClientHealth() {
        return clientHealth;
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
                ", serverHealth=" + serverHealth +
                ", clientHealth=" + clientHealth +
                '}';
    }
}
