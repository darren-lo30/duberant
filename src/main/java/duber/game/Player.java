package duber.game;

import java.util.Optional;

import org.joml.Vector3f;

import duber.engine.entities.Camera;
import duber.engine.entities.RenderableEntity;
import duber.engine.graphics.Mesh;
import duber.engine.physics.RigidBody;
import duber.engine.physics.collisions.SphereCollider;
import duber.game.scenes.Crosshair;

/**
 * Player
 */
public class Player {
    private float speed = 10.0f;
    private boolean jumping = false;
    private final Crosshair crosshair;

    private RenderableEntity model;
    private Camera camera;

    public Player(Mesh[] playerMeshes) {
        model = new RenderableEntity(playerMeshes);

        
        model.setRigidBody(new RigidBody(model.getTransform()));
        SphereCollider sphereCollider = new SphereCollider(model);
        model.setCollider(sphereCollider);

        model.getTransform().setScale(50.0f);

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
        Vector3f playerPosition = getPlayerBody().getTransform().getPosition();
        Vector3f playerRotation = getPlayerBody().getTransform().getRotation();
        camera.getTransform().getPosition().set(playerPosition);
        camera.getTransform().getPosition().add(0, 0, 500);
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