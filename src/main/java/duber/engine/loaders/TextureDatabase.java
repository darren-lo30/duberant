package duber.engine.loaders;

import java.util.HashMap;
import java.util.Map;

import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Texture;

/**
 * Stores all textures that have been previously loaded
 * @author Darren Lo
 * @version 1.0
 */
public class TextureDatabase {
    /** The instance of the TextureDatabsae */
    private static TextureDatabase instance;

    /** The Map containing the textures previously loaded */
    private Map<String, Texture> texturesMap;

    /**
     * Constructs a TextureDatabase
     */
    private TextureDatabase() {
        texturesMap = new HashMap<>();
    }

    /**
     * Gets an instance of the TextureDatabase.
     * @return the instance of the TextureDatabase
     */
    public static synchronized TextureDatabase getInstance() {
        if (instance == null) {
            instance = new TextureDatabase();
        }
        return instance;
    }

    /**
     * Gets a texture from a given file path.
     * @param filePath the file path
     * @return the Texture from the file path 
     * @throws LWJGLException if the texture could not be laoded
     */
    public Texture getTexture(String filePath) throws LWJGLException {
        Texture texture;
        if (!texturesMap.containsKey(filePath)) {
            texture = new Texture(filePath);
            texturesMap.put(filePath, texture);
        } else {
            texture = texturesMap.get(filePath);
        }

        return texture;
    }
}