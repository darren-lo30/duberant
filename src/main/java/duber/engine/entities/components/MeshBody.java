package duber.engine.entities.components;

import org.joml.Vector3f;

import duber.engine.graphics.Mesh;
import duber.engine.entities.Face;

public class MeshBody extends Component {
    private transient Mesh[] meshes;
    private transient Face[] faces;
    private transient Vector3f[] vertices;

    private boolean visible;

    public MeshBody() {}
    
    public MeshBody(MeshBody meshBody) {
        set(meshBody);
    }

    public MeshBody(Mesh mesh) {
        this(new Mesh[]{mesh});
    }
    
    public MeshBody(Mesh[] meshes) {
        this(meshes, false);
    }

    public MeshBody(Mesh[] meshes, boolean rendered) {
        this.meshes = meshes;
        visible = rendered;

        if(rendered) {
            for(Mesh mesh : meshes) {
                mesh.makeRenderable();
            }
        }

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

        int totalFaces = 0;
        for(Mesh mesh: meshes) {
            totalFaces += mesh.getFaces().length;
        }
        faces = new Face[totalFaces];

        currIdx = 0;
        for(Mesh mesh: meshes) {
            for(Face face: mesh.getFaces()) {
                faces[currIdx++] = face;
            }
        }
    }

    public void set(MeshBody meshBody) {
        meshes = meshBody.getMeshes();
        faces = meshBody.getFaces();
        vertices = meshBody.getVertices();
        visible = meshBody.isVisible();
    }

    public boolean isInitialized() {
        return meshes != null;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Mesh getMesh() {
        return meshes[0];
    }
    
    public Mesh[] getMeshes() {
        return meshes;
    }
    
    public Vector3f[] getVertices() {
        return vertices;
    }

    public Face[] getFaces() {
        return faces;
    }
}
