package duber.engine.entities;

import duber.engine.entities.components.MeshBody;
import duber.engine.graphics.Mesh;

public class SkyBox extends Entity {
    public SkyBox(Mesh skyBoxMesh) {
        super();
        setMeshBody(new MeshBody(skyBoxMesh));        
    }    

    @SuppressWarnings("unused")
    private SkyBox() {}
}