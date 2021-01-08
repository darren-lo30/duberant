package duber.engine.entities;

import org.joml.Vector3f;

import duber.engine.Cleansable;
import duber.engine.graphics.Mesh;

public class RenderableEntity extends Entity implements Cleansable {
    private Mesh[] meshes;
    private Vector3f[] vertices;
    private Face[] faces;

    private boolean visible;

    public RenderableEntity() {
        this(new Mesh[0]);
    }

    public RenderableEntity(Mesh mesh) {
        this(new Mesh[]{mesh});
    }

    public RenderableEntity(Mesh[] meshes) {
        super();
        setMeshes(meshes);
        visible = true;
    }

    public RenderableEntity(RenderableEntity renderableEntity) {
        super();
        this.meshes = renderableEntity.getMeshes();
        this.vertices = renderableEntity.getVertices();
        this.faces = renderableEntity.getFaces();
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
                Face entityFace = new Face(face);
                entityFace.setEntity(this);
                faces[currIdx++] = entityFace;
            }
        }
    }

    public Mesh getMesh() {
        return meshes[0];
    }

    public Mesh[] getMeshes() {
        return meshes;
    }

    public void setMeshes(Mesh[] meshes) {
        this.meshes = meshes;
        retrieveAllVertices();
        retrieveAllFaces();
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