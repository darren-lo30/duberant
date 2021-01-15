package duber.engine.entities.components;

public class Identifier extends Component {
    private long id;
    
    public Identifier(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    private Identifier() {}
}