package duber.engine.entities.components;

import org.joml.Vector3f;

import duber.engine.graphics.Mesh;
import duber.engine.entities.Face;

/**
 * A component that gives an Entity a renderable component.
 * @author Darren Lo
 * @version 1.0
 */
public class MeshBody extends Component {
    /** The meshes. */
    private transient Mesh[] meshes;

    /** The faces of all the meshes. */
    private transient Face[] faces;

    /** The vertices of all the meshes. */
    private transient Vector3f[] vertices;

    /** If this MeshBody is visible. */
    private boolean visible;

    /** 
     * Constructs a MeshBody without any Meshes.
     */
    public MeshBody() {}
    
    /**
     * Constructs a MeshBody from another MeshBody.
     * @param meshBody the MeshBody to copy
     */
    public MeshBody(MeshBody meshBody) {
        set(meshBody);
    }

    /**
     * Constructs a MeshBody from one Mesh.
     * @param mesh the Mesh.
     */
    public MeshBody(Mesh mesh) {
        this(new Mesh[]{mesh});
    }
    
    /**
     * Constructs a MeshBody from an array of Meshes.
     * @param meshes the array of Meshes
     */
    public MeshBody(Mesh[] meshes) {
        this(meshes, false);
    }

    /**
     * Constructs a MeshBody from an array of Meshes.
     * @param meshes the array of Meshes
     * @param rendered if the MeshBody is initially renderable.
     */
    public MeshBody(Mesh[] meshes, boolean rendered) {
        this.meshes = meshes;
        visible = rendered;

        if (rendered) {
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

    /**
     * Sets this MeshBody from another MeshBody.
     * @param meshBody the other MeshBody
     */
    public void set(MeshBody meshBody) {
        meshes = meshBody.getMeshes();
        faces = meshBody.getFaces();
        vertices = meshBody.getVertices();
        visible = meshBody.isVisible();
    }

    /**
     * Determines if this MeshBody is initialized.
     * @return if this MeshBody is initializied
     */
    public boolean isInitialized() {
        return meshes != null;
    }

    /**
     * Determines if this MeshBody is visible.
     * @return if this MeshBody is visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the visibility.
     * @param visible the visibility
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gets the first Mesh.
     * @return the first Mesh
     */
    public Mesh getMesh() {
        return meshes[0];
    }
    
    /**
     * Gets the meshes.
     * @return the meshes
     */
    public Mesh[] getMeshes() {
        return meshes;
    }
    
    /**
     * Gets the vertices.
     * @return the vertices
     */
    public Vector3f[] getVertices() {
        return vertices;
    }

    /**
     * Gets the faces.
     * @return the faces
     */
    public Face[] getFaces() {
        return faces;
    }
}
