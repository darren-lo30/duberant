package duber.engine;

import org.joml.Vector2d;
import org.joml.Vector2f;

/**
 * Information about the mouse.
 * @author Darren Lo
 * @version 1.0
 */
public class MouseInput {
    /** Previous position of the mouse. */
    private final Vector2d previousPos = new Vector2d(0, 0);

    /** Current position of the mouse. */
    private final Vector2d currentPos = new Vector2d(0, 0);

    /** The cursor's displacement. */
    private final Vector2f cursorDisplacement = new Vector2f();

    
    /** If the left mouse button is presed. */
    private boolean leftButtonIsPressed = false;

    /** If the right mouse button is pressed. */
    private boolean rightButtonIsPressed = false;

    /**
     * Sets the current positoin of the mouse.
     * @param xPos the x pos of the mouse
     * @param yPos the y pos of the mouse
     */
    public void setCurrentPos(double xPos, double yPos) {
        this.currentPos.set(xPos, yPos);
    }

    /**
     * Gets if the left mouse button is pressed
     * @return if the left mouse button is pressed
     */
    public boolean leftButtonIsPressed() {
        return leftButtonIsPressed;
    }

    /**
     * Sets if the left mouse button is pressed.
     * @param pressed if the left mouse button is pressed
     */
    public void setLeftButtonIsPressed(boolean pressed) {
        leftButtonIsPressed = pressed;
    }

    /**
     * Gets if the right mouse button is pressed
     * @return if the right mouse button is pressed
     */
    public boolean rightButtonIsPressed() {
        return rightButtonIsPressed;
    }

    /**
     * Sets if the right mouse button is pressed.
     * @param pressed if the right mouse button is pressed
     */
    public void setRightButtonIsPressed(boolean pressed) {
        rightButtonIsPressed = pressed;
    }

    /**
     * Gets the cursor's displacement.
     * @return the cursor's displacement
     */
    public Vector2f getCursorDisplacement() {
        return cursorDisplacement;
    }

    /**
     * Updates the cursor's displacement.
     */
    public void updateCursorDisplacement() {
        cursorDisplacement.x = (float) (currentPos.x - previousPos.x);
        cursorDisplacement.y = (float) (currentPos.y - previousPos.y);
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }


    /**
     * Clears all data about the mouse.
     */
    public void clear() {
        updateCursorDisplacement();
        cursorDisplacement.set(0, 0);
        previousPos.set(0, 0);
        currentPos.set(0, 0);
        leftButtonIsPressed = false;
        rightButtonIsPressed = false;
    }
}