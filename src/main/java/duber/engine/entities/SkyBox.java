package duber.engine.entities;

import duber.engine.entities.components.MeshBody;
import duber.engine.graphics.Mesh;

/**
 * A sky box.
 * @author Darren Lo
 * @version 1.0
 */
public class SkyBox extends Entity {
    /**
     * Constructor for a SkyBox.
     * @param skyBoxMesh the mesh used for this SkyBox
     */
    public SkyBox(Mesh skyBoxMesh) {
        super();
        addComponent(new MeshBody(skyBoxMesh));        
    }    

    /**
     * Used by Kryonet.
     */
    @SuppressWarnings("unused")
    private SkyBox() {}
}