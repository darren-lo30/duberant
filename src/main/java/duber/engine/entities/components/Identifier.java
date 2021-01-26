package duber.engine.entities.components;

/**
 * A component that gives an Entity an id.
 * @author Darren Lo
 * @version 1.0
 */
public class Identifier extends Component {
    /** The id. */
    private int id;
    
    /** 
     * Constructs an Identifier.
     * @param id the id
     */
    public Identifier(int id) {
        this.id = id;
    }

    /**
     * Gets the id.
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Used by Kryonet
     */
    @SuppressWarnings("unused")
    private Identifier() {}
}