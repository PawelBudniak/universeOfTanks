package uot.objects;

import java.awt.*;

public abstract class RectangularObject {
    protected Rectangle rectangle;

    public boolean collision(RectangularObject other){
        return rectangle.intersects(other.rectangle);
    }
    public int getX() { return rectangle.x; }
    public int getY() { return rectangle.y; }
    public int getWidth() { return rectangle.width; }
    public int getHeight() { return rectangle.height; }
}
