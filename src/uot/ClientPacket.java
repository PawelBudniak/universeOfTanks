package uot;

import java.io.Serializable;

public class ClientPacket implements Serializable {
    private static final long serialVersionUID = -7273989242381916569L;

    private boolean a_pressed;
    private boolean w_pressed;
    private boolean d_pressed;
    private boolean s_pressed;
    private int mouseX;
    private int mouseY;
    private boolean isMouseInputValid;

    public ClientPacket(boolean a_pressed, boolean w_pressed, boolean d_pressed, boolean s_pressed, int mouseX, int mouseY, boolean isMouseInputValid) {
        this.a_pressed = a_pressed;
        this.w_pressed = w_pressed;
        this.d_pressed = d_pressed;
        this.s_pressed = s_pressed;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.isMouseInputValid = isMouseInputValid;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isA_pressed() {
        return a_pressed;
    }

    public boolean isW_pressed() {
        return w_pressed;
    }

    public boolean isD_pressed() {
        return d_pressed;
    }

    public boolean isS_pressed() {
        return s_pressed;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public boolean isMouseInputValid() {
        return isMouseInputValid;
    }


    @Override
    public String toString() {
        return "ClientPacket{" +
                "a_pressed=" + a_pressed +
                ", w_pressed=" + w_pressed +
                ", d_pressed=" + d_pressed +
                ", s_pressed=" + s_pressed +
                ", mouseX=" + mouseX +
                ", mouseY=" + mouseY +
                ", isMouseInputValid=" + isMouseInputValid +
                '}';
    }
}
