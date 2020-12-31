package duber.engine.entities;

import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Mesh;

public class SkyBox extends ConcreteEntity {
    public SkyBox(Mesh skyBoxMesh) throws LWJGLException {
        super(skyBoxMesh);        
    }    
}