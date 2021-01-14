package duber.engine.entities.components;

public class Identifier extends Component {
    private String name;

    public Identifier(){}
    
    public Identifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}