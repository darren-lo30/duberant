package duber.engine.loaders;

import java.util.HashMap;
import java.util.Map;

import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Texture;

public class TextureDatabase {
    private static TextureDatabase instance;

    private Map<String, Texture> texturesMap;

    private TextureDatabase() {
        texturesMap = new HashMap<>();
    }

    public static synchronized TextureDatabase getInstance() {
        if(instance == null) {
            instance = new TextureDatabase();
        }
        return instance;
    }

    public Texture getTexture(String filePath) throws LWJGLException {
        Texture texture;
        if(!texturesMap.containsKey(filePath)) {
            texture = new Texture(filePath);
            texturesMap.put(filePath, texture);
        } else {
            texture = texturesMap.get(filePath);
        }

        return texture;
    }
}