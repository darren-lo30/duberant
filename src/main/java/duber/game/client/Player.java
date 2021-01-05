package duber.game.client;

import java.util.Optional;

import org.joml.Vector3f;

import duber.engine.entities.Camera;
import duber.engine.entities.RenderableEntity;
import duber.engine.entities.components.RigidBody;
import duber.engine.entities.components.SphereCollider;
import duber.engine.graphics.Mesh;
import duber.game.client.scenes.Crosshair;

/**
 * Player
 */
public class Player {
    private float speed = 1.0f;
    private boolean jumping = false;
    private final Crosshair crosshair;

    private RenderableEntity model;
    private Camera camera;

    public Player(Mesh[] playerMeshes) {
        model = new RenderableEntity(playerMeshes);
        model.addRigidBody();
        SphereCollider sphereCollider = new SphereCollider(model);
        model.setCollider(sphereCollider);
        sphereCollider.setUnscaledRadius(1.0f);
        model.getTransform().setScale(5.0f);

        model.getTransform().getPosition().set(0, 0, 0);

        camera = new Camera();
        crosshair = new Crosshair();
    }

    public Camera getCamera() {
        return camera;
    }

    public RenderableEntity getModel() {
        return model;
    }

    public float getSpeed() {
        return speed;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public void updateCamera() {
        Vector3f playerPosition = model.getTransform().getPosition();
        Vector3f playerRotation = model.getTransform().getRotation();
        camera.getTransform().getPosition().set(playerPosition);
        camera.getTransform().getPosition().add(0, 20, 50);
        camera.getTransform().getRotation().set(playerRotation);
    }

    public RigidBody getPlayerBody() {
        Optional<RigidBody> playerBody = model.getRigidBody();

        if(playerBody.isPresent()) {
            return playerBody.get();
        } else {
            throw new IllegalStateException("Player lacks a rigid body");
        }
    }

    public Crosshair getCrosshair() {
        return crosshair;
    }
    
}