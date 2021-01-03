package duber.engine.graphics;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import duber.engine.Transformation;
import duber.engine.entities.RenderableEntity;


public class InstancedMesh extends Mesh {
    private static final int BYTES_PER_FLOAT = 4;

    private static final int BYTES_PER_VECTOR4F = 4 * InstancedMesh.BYTES_PER_FLOAT;

    private static final int MATRIX4_NUM_ELEMENTS = 4 * 4;

    private static final int BYTES_PER_MATRIX4F = MATRIX4_NUM_ELEMENTS * BYTES_PER_FLOAT;

    private static final int INSTANCE_DATA_SIZE_BYTES = BYTES_PER_MATRIX4F * 2 + BYTES_PER_FLOAT * 2;

    private static final int INSTANCE_DATA_SIZE_FLOATS = MATRIX4_NUM_ELEMENTS * 2 + 2;

    private int numInstances;
    private int instanceDataVBO;

    private FloatBuffer instanceDataBuffer = null;

    
    public InstancedMesh(float[] positions, float[] textureCoords, float[] normals, int[] indices, int numInstances) {
        super(positions, textureCoords, normals, indices);
        this.numInstances = numInstances;

        glBindVertexArray(vaoId);

        //Model view matrix
        instanceDataVBO = glGenBuffers();
        vboIdList.add(instanceDataVBO);
        instanceDataBuffer = MemoryUtil.memAllocFloat(numInstances * INSTANCE_DATA_SIZE_FLOATS);
        glBindBuffer(GL_ARRAY_BUFFER, instanceDataVBO);
        int currIdx = 5;
        int stride = 0;

        //Add model view matrix
        for(int i = 0; i<4; i++) {
            glVertexAttribPointer(currIdx, 4, GL_FLOAT, false, INSTANCE_DATA_SIZE_BYTES, stride);
            glVertexAttribDivisor(currIdx, 1);
            glEnableVertexAttribArray(currIdx);
            currIdx++;
            stride += BYTES_PER_VECTOR4F;
        }

        //Add model light view matrix
        for(int i = 0; i<4; i++) {
            glVertexAttribPointer(currIdx, 4, GL_FLOAT, false, INSTANCE_DATA_SIZE_BYTES, stride);
            glVertexAttribDivisor(currIdx, 1);
            glEnableVertexAttribArray(currIdx);
            currIdx++;
            stride += BYTES_PER_VECTOR4F;
        }

        glVertexAttribPointer(currIdx, 2, GL_FLOAT, false, InstancedMesh.INSTANCE_DATA_SIZE_BYTES, stride);
        glVertexAttribDivisor(currIdx, 1);
        glEnableVertexAttribArray(currIdx);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);     
    }

    public void render(List<? extends RenderableEntity> renderableEntities, Transformation transformation, Matrix4f viewMatrix, Matrix4f lightViewMatrix) {
        initRender();

        int chunkSize = numInstances;
        int length = renderableEntities.size();
        for(int i = 0; i<length; i+=chunkSize) {
            int end = Math.min(length, i + chunkSize);
            List<? extends RenderableEntity> subList = renderableEntities.subList(i, end);
            renderChunkInstanced(subList, transformation, viewMatrix, lightViewMatrix);
        }
        endRender();
    }

    private void renderChunkInstanced(List<? extends RenderableEntity> renderableEntities, Transformation transformation, Matrix4f viewMatrix, Matrix4f lightViewMatrix) {
        instanceDataBuffer.clear();

        Texture texture = getMaterial().getTexture();

        for(int i = 0; i<renderableEntities.size(); i++) {
            RenderableEntity renderableEntity = renderableEntities.get(i);
            
            Matrix4f modelMatrix = transformation.buildModelMatrix(renderableEntity.getTransform());
            if(viewMatrix != null) {
                Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
                modelViewMatrix.get(INSTANCE_DATA_SIZE_FLOATS * i, instanceDataBuffer);
            }

            if(lightViewMatrix != null) {
                Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(modelMatrix, lightViewMatrix);
                modelLightViewMatrix.get(INSTANCE_DATA_SIZE_FLOATS * i + MATRIX4_NUM_ELEMENTS, instanceDataBuffer);
            }

            if(texture != null) {
                int textureColumn = renderableEntity.getTextureIndex() % texture.getNumColumns();
                int textureRow = renderableEntity.getTextureIndex() / texture.getNumRows();
                float textureOffsetX = (float) textureColumn / texture.getNumColumns();
                float textureOffsetY = (float) textureRow / texture.getNumRows();

                int bufferPos = INSTANCE_DATA_SIZE_FLOATS * i + MATRIX4_NUM_ELEMENTS * 2;
                instanceDataBuffer.put(bufferPos, textureOffsetX);
                instanceDataBuffer.put(bufferPos + 1, textureOffsetY);
            }
        }


        glBindBuffer(GL_ARRAY_BUFFER, instanceDataVBO);
        glBufferData(GL_ARRAY_BUFFER, instanceDataBuffer, GL_DYNAMIC_DRAW);

        glDrawElementsInstanced(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0, renderableEntities.size());
        glBindBuffer(GL_ARRAY_BUFFER, 0);      
    }

    public int getNumInstances() {
        return numInstances;
    }

    @Override 
    public void initRender() {
        super.initRender();
        int start = 5;
        for(int i = start; i<start + 8; i++) {
            glEnableVertexAttribArray(i);
        }
    }

    @Override
    public void endRender() {
        int start = 5;
        for(int i = start; i<start + 8; i++) {
            glDisableVertexAttribArray(i);
        }
        super.endRender();
    }

    @Override
    public void cleanup() {
        freeBuffer(instanceDataBuffer);
    }

}