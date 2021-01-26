package duber.engine.loaders;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

/**
 * A node used in animations
 * @author Darren Lo
 * @version 1.0
 */
public class Node {

    /** The children nodes */
    private final List<Node> children;

    /** The name of this Node */
    private final String name;

    /** The parent of this Node */
    private final Node parent;

    /** The transformation for this Node */
    private Matrix4f nodeTransformation;

    /**
     * Constructs a Node.
     * @param name the name
     * @param parent the Node parent of this Node
     * @param nodeTransformation the node's transformation matrix
     */
    public Node(String name, Node parent, Matrix4f nodeTransformation) {
        this.name = name;
        this.parent = parent;
        this.nodeTransformation = nodeTransformation;
        this.children = new ArrayList<>();
    }

    /**
     * Gets the node's transformation.
     * @return the node's transformation.
     */
    public Matrix4f getNodeTransformation() {
        return nodeTransformation;
    }

    /**
     * Adds a child node.
     * @param node the child node
     */
    public void addChild(Node node) {
        this.children.add(node);
    }
    
    /**
     * Gets all children Node for this node.
     * @return the List of children Nodes
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     * Gets the name of this Node.
     * @return the name of this Node
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the parent of this Node.
     * @return the parent of this Node
     */
    public Node getParent() {
        return parent;
    }
}