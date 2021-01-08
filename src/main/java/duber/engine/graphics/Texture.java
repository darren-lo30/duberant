package duber.engine.graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import duber.engine.Cleansable;
import duber.engine.exceptions.LWJGLException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import static org.lwjgl.stb.STBImage.*;

public class Texture implements Cleansable {
    private int id;
    private int width;
    private int height;

    public Texture(String fileName) throws LWJGLException {
        ByteBuffer textureBuffer;
        //Load the texture
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            //Number of channels in the image(4 if there's rgba)
            IntBuffer channels = stack.mallocInt(1);

            //Load the image to the buffer
            textureBuffer = stbi_load(fileName, w, h, channels, 4);
            if(textureBuffer == null) {
                throw new LWJGLException(String.format("Could not load texture %s: Reason: %s", fileName, stbi_failure_reason()));
            }
            
            width = w.get();
            height = h.get();
        }

        id = createTexture(textureBuffer);
        stbi_image_free(textureBuffer);
    }


    public int getId() {
        return id;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }
    
    private int createTexture(ByteBuffer textureBuffer) {
        int textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureId);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        //Upload texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, textureBuffer);
        glGenerateMipmap(GL_TEXTURE_2D);

        return textureId;
    }

    public void cleanup() {
        glDeleteTextures(id);
    }
}