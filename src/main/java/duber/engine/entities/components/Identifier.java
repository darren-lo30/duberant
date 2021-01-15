package duber.engine.entities.components;

public class Identifier extends Component {
    private int id;
    
    public Identifier(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    private Identifier() {}
}