package uot.objects;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Terrain extends RectangularObject implements Serializable {

    public static final int DEFAULT_WIDTH = 30;
    public static final int DEFAULT_LENGTH = 30;
    private static final long serialVersionUID = 2287183422099577332L;

    public Terrain(Rectangle r){
        this.rectangle = new Rectangle(r);
    }

    public Terrain(int x, int y, int width, int height) {
        this.rectangle = new Rectangle(x, y, width, height);
    }
    public Terrain(int x, int y) {
        initTerrain(x,y);
    }
    private void initTerrain(int x, int y)
    {
        this.rectangle = new Rectangle(x,y,DEFAULT_WIDTH,DEFAULT_LENGTH);
    }



    @Override
    public String toString() {
        return "Terrain{" +
                "rectangle=" + rectangle +
                '}';
    }
}
