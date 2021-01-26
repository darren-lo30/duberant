package duber.game.client.match;

import org.joml.Vector4f;

/** 
 * A crosshair. 
 * @author Darren Lo
 * @version 1.0
 */
public class Crosshair {
    /** The width of this Crosshair. */
    private int width;
    /** The height of this Crosshair. */
    private int height;
    /** The thickness of this Crosshair. */
    private int thickness;
    /** The colour of this Crosshair. */
    private Vector4f colour;

    /** 
     * Constructs a default Crosshair.
     */
    public Crosshair() {
        width = 20;
        height = 20;
        thickness = 5;
        colour = new Vector4f(255f, 255f, 255f, 255f);
    }

    /**
     * Gets the width.
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width.
     * @param width the width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets the height.
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height.
     * @param height the height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the thickness.
     * @return the thickness
     */
    public int getThickness() {
        return thickness;
    }

    /**
     * Sets the thickness.
     * @param thickness the thickness
     */
    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    /**
     * Gets the colour.
     * @return the colour
     */
    public Vector4f getColour() {
        return colour;
    }

    /**
     * Sets the colour.
     * @param colour the colour
     */
    public void setColour(Vector4f colour) {
        this.colour = colour;
    }


}