package duber.engine.items;

import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Mesh;

public class SkyBox extends GameItem {
    public SkyBox(Mesh skyBoxMesh) throws LWJGLException {
        super();        
        setMesh(skyBoxMesh);
    }    
}