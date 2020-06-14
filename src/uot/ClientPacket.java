package uot;

public class ClientPacket {
    private int keyPressed;
    private boolean isKeyPressedValid;
    private int keyReleased;
    private boolean isKeyReleasedValid;
    private int mouseX;
    private int mouseY;
    private boolean isMouseInputValid;

    public ClientPacket(int keyPressed, boolean isKeyPressedValid, int keyReleased, boolean isKeyReleasedValid, int mouseX, int mouseY, boolean isMouseInputValid) {
        this.keyPressed = keyPressed;
        this.isKeyPressedValid = isKeyPressedValid;
        this.keyReleased = keyReleased;
        this.isKeyReleasedValid = isKeyReleasedValid;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.isMouseInputValid = isMouseInputValid;
    }

    public int getKeyPressed() {
        return keyPressed;
    }

    public boolean isKeyPressedValid() {
        return isKeyPressedValid;
    }

    public int getKeyReleased() {
        return keyReleased;
    }

    public boolean isKeyReleasedValid() {
        return isKeyReleasedValid;
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
}
