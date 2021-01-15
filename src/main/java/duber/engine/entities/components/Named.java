package duber.engine.entities.components;

public class Named extends Component {
    String name;

    public Named() {
        name = "NULL";
    }

    public Named(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    
}