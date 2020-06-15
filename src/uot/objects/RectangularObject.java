package uot.objects;

import java.awt.*;
import java.io.Serializable;

public abstract class RectangularObject implements Serializable {
    private static final long serialVersionUID = 7277921765177628379L;
    protected Rectangle rectangle;

    public boolean collision(RectangularObject other){
        return rectangle.intersects(other.rectangle);
    }
    public int getX() { return rectangle.x; }
    public int getY() { return rectangle.y; }
    public int getWidth() { return rectangle.width; }
    public int getHeight() { return rectangle.height; }
    public void setX(int x){ rectangle.x = x; }
    public void setY(int y){ rectangle.y = y; }
    public Rectangle getShape() { return rectangle; }
}
