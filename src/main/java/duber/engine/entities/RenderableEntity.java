package duber.engine.entities;

import org.joml.Vector3f;
import duber.engine.graphics.Mesh;

public class RenderableEntity extends Entity {
    private final Mesh[] meshes;
    private Vector3f[] vertices;
    private Face[] faces;
    private int textureIndex = 0;

    private boolean visible;

    public RenderableEntity(Mesh mesh) {
        this(new Mesh[]{mesh});
    }

    public RenderableEntity(Mesh[] meshes) {
        this.meshes = meshes;
        retrieveAllVertices();
        retrieveAllFaces();
        visible = true;
    }

    private void retrieveAllVertices() {
        int totalVertices = 0;
        for(Mesh mesh: meshes) {
            totalVertices += mesh.getVertices().length;
        }

        vertices = new Vector3f[totalVertices];
        int currIdx = 0;
        for(Mesh mesh: meshes) {
            for(Vector3f vertex: mesh.getVertices()) {
                vertices[currIdx++] = vertex;
            }
        }
    }

    private void retrieveAllFaces() {
        int totalFaces = 0;
        for(Mesh mesh: meshes) {
            totalFaces += mesh.getFaces().length;
        }
        faces = new Face[totalFaces];

        int currIdx = 0;
        for(Mesh mesh: meshes) {
            for(Face face: mesh.getFaces()) {
                faces[currIdx++] = face;
            }
        }
    }

    public Mesh getMesh() {
        return meshes[0];
    }

    public Mesh[] getMeshes() {
        return meshes;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Vector3f[] getVertices() {
        return vertices;
    }

    @Override
    public Face[] getFaces() {
        return faces;
    }

    public void cleanup() {
        if (meshes != null) {
            for (Mesh mesh : meshes) {
                mesh.cleanup();
            }
        }
    }
}