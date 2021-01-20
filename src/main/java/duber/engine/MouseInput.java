package duber.engine;

import org.joml.Vector2d;
import org.joml.Vector2f;

public class MouseInput {
    private final Vector2d previousPos = new Vector2d(0, 0);

    private final Vector2d currentPos = new Vector2d(0, 0);

    private final Vector2f cursorDisplacement = new Vector2f();

    private boolean leftButtonIsPressed = false;

    private boolean rightButtonIsPressed = false;

    public void setCurrentPos(double xPos, double yPos) {
        this.currentPos.set(xPos, yPos);
    }

    public void setLeftButtonIsPressed(boolean pressed) {
        leftButtonIsPressed = pressed;
    }

    public void setRightButtonIsPressed(boolean pressed) {
        rightButtonIsPressed = pressed;
    }

    public Vector2f getCursorDisplacement() {
        return cursorDisplacement;
    }

    public void updateCursorDisplacement() {
        cursorDisplacement.x = (float) (currentPos.x - previousPos.x);
        cursorDisplacement.y = (float) (currentPos.y - previousPos.y);
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public boolean leftButtonIsPressed() {
        return leftButtonIsPressed;
    }

    public boolean rightButtonIsPressed() {
        return rightButtonIsPressed;
    }

    public void clear() {
        updateCursorDisplacement();
        cursorDisplacement.set(0, 0);
        previousPos.set(0, 0);
        currentPos.set(0, 0);
        leftButtonIsPressed = false;
        rightButtonIsPressed = false;
    }
}