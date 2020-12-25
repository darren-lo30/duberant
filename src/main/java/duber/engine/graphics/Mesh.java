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

import org.lwjgl.system.MemoryUtil;

import duber.engine.items.GameItem;

public class Mesh {    
    private static final int MAX_WEIGHTS = 4;
    protected final int vaoId;
    protected final List<Integer> vboIdList;
    private final int vertexCount;
    
    private boolean rigid;

    private Material material;

    public Mesh(float[] positions, float[] textureCoords, float[] normals, int[] indices){
        this(positions, textureCoords, normals, indices, 
            new int[MAX_WEIGHTS * positions.length / 3],
            new float[MAX_WEIGHTS * positions.length / 3]);
    }

    public Mesh(float[] positions, float[] textureCoords, float[] normals, int[] indices, int[] jointIndices, float[] weights){
        material = new Material();
        vboIdList = new ArrayList<>();
        
        FloatBuffer positionBuffer = null;
        FloatBuffer textureCoordsBuffer = null;
        FloatBuffer normalsBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer weightsBuffer = null;
        IntBuffer jointIndicesBuffer = null;

        vertexCount = indices.length;

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
            if(normals.length == 0){
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
    }

    protected void freeBuffer(Buffer buffer){
        if(buffer != null){
            MemoryUtil.memFree(buffer);
        }
    }

    public void setMaterial(Material material){
        this.material = material;
    }

    public Material getMaterial(){
        return material;
    }

	public int getVaoId() {
		return vaoId;
    }

    public int getVertexCount(){
        return vertexCount;
    }

    protected void initRender(){
        //Set active texture if it exists
        if(material.hasTexture()){
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, material.getTexture().getId());
        }

        if(material.hasNormalMap()){
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, material.getNormalMap().getId());
        }

        //Draw mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);
    }

    protected void endRender(){
        //Restore state by unbinding everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);
        glBindVertexArray(0);

        glBindTexture(GL_TEXTURE_2D, 0);
    }


    public void render(){
        initRender();

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        endRender();
    }

    public void render(List<? extends GameItem> gameItems, Consumer<GameItem> consumer){
        initRender();
        for(GameItem gameItem: gameItems){
            consumer.accept(gameItem);
            glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        }
        endRender();
    }
    
    public void cleanup(){
        //Disable vertex array indices
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

        //Deletes VBOS
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for(int vboId: vboIdList){
            glDeleteBuffers(vboId);
        }

        //Delete texture if it exists
        if(material.hasTexture()){
            material.getTexture().cleanup();
        }
        if(material.hasNormalMap()){
            material.getNormalMap().cleanup();
        }

        //Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
    
}