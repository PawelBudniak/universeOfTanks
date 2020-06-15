package uot;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private static final long serialVersionUID = 4090460896275072342L;
    public int x;
    public int y;
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
