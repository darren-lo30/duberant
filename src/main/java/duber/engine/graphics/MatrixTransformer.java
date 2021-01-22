package duber.engine.graphics;

import duber.engine.entities.components.Transform;

import org.joml.Matrix4f;
import org.joml.Vector3f;


public class MatrixTransformer {
    //Maps an object to its coordinates in the world
    private final Matrix4f modelMatrix;

    //Maps an object relative to the camera and the world
    private final Matrix4f modelViewMatrix;
    
    public MatrixTransformer() {
        modelMatrix = new Matrix4f();
        
        modelViewMatrix = new Matrix4f();        
    }

    public static final Matrix4f updateGeneralViewMatrix(Vector3f position, Vector3f rotation, Matrix4f viewMatrix) {
        //Rotate and translate the object relative to the camera
        return viewMatrix
            .identity()
            .rotateX(rotation.x)
            .rotateY(rotation.y)
            .translate(-position.x, -position.y, -position.z);
    }

    public final Matrix4f buildModelMatrix(Transform transform) {
        Vector3f position = transform.getPosition();
        Vector3f rotation = transform.getRotation();
        float scale = transform.getScale();

        return modelMatrix
            .identity()
            .translate(position)
            .rotateX(-rotation.x())
            .rotateY(-rotation.y())
            .rotateZ(-rotation.z())
            .scale(scale);
    }

    public final Matrix4f buildModelViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {        
        return viewMatrix.mulAffine(modelMatrix, modelViewMatrix);
    }

    public final Matrix4f buildModelViewMatrix(Transform transform, Matrix4f viewMatrix) {
        return buildModelViewMatrix(buildModelMatrix(transform), viewMatrix);
    }
}


