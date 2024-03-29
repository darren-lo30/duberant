package duber.engine.graphics;

import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import duber.engine.Cleansable;
import duber.engine.entities.Entity;
import duber.engine.entities.Face;
import duber.engine.entities.components.MeshBody;
import duber.engine.utilities.Utils;

/**
 * A Mesh of a 3D object
 * @author Darren Lo
 * @version 1.0
 */
public class Mesh implements Cleansable {    

    /** The maximum number of weights. */
    public static final int MAX_WEIGHTS = 4;
    
    /** The position of vertices. */
    private final float[] positions;

    /** The texture coordinates. */
    private final float[] textureCoords;

    /** The normals of vertices */
    private final float[] normals;

    /** The indices of the vertices. */
    private final int[] indices;

    /** The indices of the joints. */
    private final int[] jointIndices;

    /** The weights. */
    private final float[] weights;

    /** The number of non-unique vertices. */
    private final int vertexCount;

    /** The faces of this Mesh. */
    private final Face[] faces;

    /** The unique vertices of this Mesh. */
    private final Vector3f[] vertices;
    
    /** The Vertex Array Object id of this Mesh. */
    private int vaoId;

    /** The List of Vertex buffer Object ids for this Mesh. */
    private List<Integer> vboIdList; 
    
    /**
     * The Material used for this Mesh.
     */
    private Material material;

    /**
     * Constructs a Mesh without animations.
     * @param positions the vertex positions
     * @param textureCoords the texture coordinates
     * @param normals the vertex normals
     * @param indices the vertex indices
     */
    public Mesh(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        this(positions, textureCoords, normals, indices, 
            Utils.buildIntArray(MAX_WEIGHTS * positions.length / 3, 0),
            Utils.buildFloatArray(MAX_WEIGHTS * positions.length / 3, 0));
    }

    /**
     * Constructs a Mesh with animations.
     * @param positions the vertex positions
     * @param textureCoords the texture coordinates
     * @param normals the vertex normals
     * @param indices the vertex indices
     * @param jointIndices the joint indices
     * @param weights the weights
     */
    public Mesh(float[] positions, float[] textureCoords, float[] normals, int[] indices, int[] jointIndices, float[] weights) {
        vertexCount = indices.length;

        this.positions = positions;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.jointIndices = jointIndices;
        this.weights = weights;

        material = new Material();

        //Set vertices
        vertices = new Vector3f[positions.length/3];
        for(int i = 0; i<positions.length; i+=3) {
            vertices[i/3] = new Vector3f(positions[i], positions[i+1], positions[i+2]);
        }

        //Set faces
        faces = new Face[indices.length/3];
        for(int i = 0; i<indices.length; i+=3) {
            Vector3f[] faceVertices = new Vector3f[3];
            for(int j = 0; j<3; j++) {
                faceVertices[j] = vertices[indices[i+j]];
            }
            faces[i/3] = new Face(faceVertices);
        }
    }
    
    /**
     * Determines if the Mesh is renderable.
     */
    public boolean isRenderable() {
        return vboIdList != null;
    }

    /**
     * Gets the number of non-unique vertices.
     * @return the number of non-unique vertices
     */
    public int getVertexCount() {
        return vertexCount;
    }
    
    /**
     * Gets all the vertices.
     * @return the vertices
     */
    public Vector3f[] getVertices() {
        return vertices;
    }

    /**
     * Gets all the faces.
     * @return the faces
     */
    public Face[] getFaces() {
        return faces;
    }

    /**
     * Gets the material.
     * @return the material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the material.
     * @param material the material
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * Makes this Mesh renderable.
     * @return if this Mesh was made renderable
     */
    public final boolean makeRenderable() {
        if (isRenderable()) {
            return false;
        }

        vboIdList = new ArrayList<>();
        
        FloatBuffer positionBuffer = null;
        FloatBuffer textureCoordsBuffer = null;
        FloatBuffer normalsBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer weightsBuffer = null;
        IntBuffer jointIndicesBuffer = null;

        try {
            //Create VAO
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            //Create position VBO
            int positionVboId = glGenBuffers();
            vboIdList.add(positionVboId);
            positionBuffer = MemoryUtil.memAllocFloat(positions.length);
            positionBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
            glBufferData(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            //Create index VBO
            int indicesVboId = glGenBuffers();
            vboIdList.add(indicesVboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            //Create texture VBO
            int textureCoordsVboId = glGenBuffers();
            vboIdList.add(textureCoordsVboId);
            textureCoordsBuffer = MemoryUtil.memAllocFloat(textureCoords.length);
            textureCoordsBuffer.put(textureCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, textureCoordsVboId);
            glBufferData(GL_ARRAY_BUFFER, textureCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            //Create normal VBO
            int normalsVboId = glGenBuffers();
            vboIdList.add(normalsVboId);
            if (normals.length == 0) {
                normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            } else {
                normalsBuffer = MemoryUtil.memAllocFloat(positions.length);
            }
            normalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, normalsVboId);
            glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            //Create weight VBO
            int weightVboId = glGenBuffers();
            vboIdList.add(weightVboId);
            weightsBuffer = MemoryUtil.memAllocFloat(weights.length);
            weightsBuffer.put(weights).flip();
            glBindBuffer(GL_ARRAY_BUFFER, weightVboId);
            glBufferData(GL_ARRAY_BUFFER, weightsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(3);
            glVertexAttribPointer(3, 4, GL_FLOAT, false, 0, 0);
            
            //Create joint indices VBO
            int jointIndicesVboId = glGenBuffers();
            vboIdList.add(jointIndicesVboId);
            jointIndicesBuffer = MemoryUtil.memAllocInt(jointIndices.length);
            jointIndicesBuffer.put(jointIndices).flip();
            glBindBuffer(GL_ARRAY_BUFFER, jointIndicesVboId);
            glBufferData(GL_ARRAY_BUFFER, jointIndicesBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(4);
            glVertexAttribPointer(4, 4, GL_FLOAT, false, 0, 0);            

            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glDisableVertexAttribArray(2);
            glDisableVertexAttribArray(3);
            glDisableVertexAttribArray(4);
            glBindVertexArray(0);
        } finally {
            freeBuffer(positionBuffer);
            freeBuffer(textureCoordsBuffer);
            freeBuffer(normalsBuffer);
            freeBuffer(indicesBuffer);
            freeBuffer(weightsBuffer);
            freeBuffer(jointIndicesBuffer);
        }

        return true;
    }


    /**
     * Called before this Mesh is rendered.
     */
    private void initRender() {
        if (!isRenderable()) {
            throw new IllegalStateException("Make the mesh renderable before rendering!");
        }
        
        //Set active texture if it exists
        if (material.hasTexture()) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, material.getTexture().getId());
        }

        if (material.hasNormalMap()) {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, material.getNormalMap().getId());
        }

        //Draw mesh
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);
    }

    /**
     * Called after this Mesh is rendered.
     */
    private void endRender() {
        //Restore state by unbinding everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);
        glBindVertexArray(0);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Renders this Mesh.
     */
    public void render() {
        initRender();
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        endRender();
    }

    /**
     * Renders a List of Entities with this Mesh.
     * @param entities the List of Entities to render
     * @param consumer the consumer to use before rendering
     */
    public void render(List<Entity> entities, Consumer<Entity> consumer) {
        initRender();
        for(Entity entity: entities) {
            consumer.accept(entity);
            
            if (entity.hasComponent(MeshBody.class) && entity.getComponent(MeshBody.class).isVisible()) {
                glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
            }
        }
        endRender();
    }

    /**
     * Frees a buffer.
     * @param buffer the buffer to free
     */
    private void freeBuffer(Buffer buffer) {
        if (buffer != null) {
            MemoryUtil.memFree(buffer);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        if (!isRenderable()) {
            return;
        }

        
        //Disable vertex array indices
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);

        //Deletes VBOS
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for(int vboId: vboIdList) {
            glDeleteBuffers(vboId);
        }

        //Delete texture if it exists
        if (material.hasTexture()) {
            material.getTexture().cleanup();
        }
        if (material.hasNormalMap()) {
            material.getNormalMap().cleanup();
        }

        //Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
    
}