package uot.objects;

import javax.swing.*;
import java.awt.*;

public class Terrain extends RectangularObject{

    public static final int DEFAULT_WIDTH = 30;
    public static final int DEFAULT_LENGTH = 30;
    private Image image;

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
        var terrainImg = "src/uot/objects/images/rock.png";
        var ii = new ImageIcon(terrainImg);
        image = ii.getImage();
    }

    public Image getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "Terrain{" +
                "rectangle=" + rectangle +
                '}';
    }
}
