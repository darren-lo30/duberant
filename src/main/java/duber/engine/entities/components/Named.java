package duber.engine.entities.components;

/**
 * A component that gives an Entity a name.
 * @author Darren Lo
 * @version 1.0
 */
public class Named extends Component {
    /** The name. */
    String name;

    /** 
     * Constructs a Named component with no name. 
     */
    public Named() {
        name = "NULL";
    }

    /**
     * Constructs a Named component.
     * @param name the name
     */
    public Named(String name) {
        this.name = name;
    }

    /**
     * Gets the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }    
}