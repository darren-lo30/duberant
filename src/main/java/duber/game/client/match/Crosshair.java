package duber.game.client.match;

import org.joml.Vector4f;

public class Crosshair {
    private int width;
    private int height;
    private int thickness;
    private Vector4f colour;

    public Crosshair() {
        width = 20;
        height = 20;
        thickness = 5;
        colour = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public Vector4f getColour() {
        return colour;
    }

    public void setColour(Vector4f colour) {
        this.colour = colour;
    }


}