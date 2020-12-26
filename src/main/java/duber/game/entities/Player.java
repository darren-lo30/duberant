package duber.game.entities;

import org.joml.Vector4f;

import duber.engine.items.Camera;
import duber.engine.graphics.Material;
import duber.engine.graphics.Mesh;
import duber.engine.items.GameItem;
import duber.engine.loaders.OBJLoader;

/**
 * Player
 */
public class Player {
    GameItem model;
    Camera camera;

    public Player() {
        camera = new Camera();
        try{ 
            Mesh playerMesh = OBJLoader.loadMesh("/models/cube.obj");
            playerMesh.setMaterial(new Material(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), 1.0f));
            model = new GameItem(playerMesh);
            model.setPosition(0, -30, 50);
            model.setScale(5.0f);
        } catch (Exception e) {
            System.out.println("Could not load model");
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public GameItem getModel() {
        return model;
    }

    public void move(float offsetX, float offsetY, float offsetZ) {
        camera.movePosition(offsetX, offsetY, offsetZ);
        model.movePosition(offsetX, offsetY, offsetZ);
    }

    public void rotate(float rotationX, float rotationY, float rotationZ) {
        model.rotate(rotationX, rotationY, 0);
        camera.rotate(rotationX, rotationY, rotationZ);
    }
    
}