package duber.engine.entities;

import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Mesh;

public class SkyBox extends RenderableEntity {
    public SkyBox(Mesh skyBoxMesh) throws LWJGLException {
        super(skyBoxMesh);        
    }    
}