package uot.objects;

import java.awt.*;

public class Terrain extends RectangularObject{

    public static final int DEFAULT_WIDTH = 20;
    public static final int DEFAULT_LENGTH = 20;

    public Terrain(Rectangle r){
        this.rectangle = new Rectangle(r);
    }

    public Terrain(int x, int y, int width, int height) {
        this.rectangle = new Rectangle(x, y, width, height);
    }
    public Terrain(int x, int y) {
        this.rectangle = new Rectangle(x, y, DEFAULT_WIDTH, DEFAULT_LENGTH);
    }

    @Override
    public String toString() {
        return "Terrain{" +
                "rectangle=" + rectangle +
                '}';
    }
}
