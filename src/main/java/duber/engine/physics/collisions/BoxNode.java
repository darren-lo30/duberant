package duber.engine.physics.collisions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joml.Vector3f;

/**
 * A node that is also a Box inside an Octree.
 * @author Darren Lo
 * @version 1.0
 */
public class BoxNode extends Box {
    /** The number of children each node has */
    private static final int NUM_CHILDREN = 8;

    /** The List of FaceBoxes contained inside the node */
    private final List<FaceBox> faceBoxes;

    /** The Array of BoxNodes that are the children */
    private final BoxNode[] children;

    /** IF this BoxNode is partitioned yet */
    private boolean partitioned;

    /**
     * Constructs a BoxNode that spans over the min to max bounds
     * @param minXYZ the min bounds of the box
     * @param maxXYZ the max bounds of the box
     */
    public BoxNode(Vector3f minXYZ, Vector3f maxXYZ) {
        super(minXYZ, maxXYZ);
        partitioned = false;
        faceBoxes = new ArrayList<>();
        children = new BoxNode[NUM_CHILDREN];
    }

    /**
     * Partitions the box node into 8 children.
     */
    private void partition() {
        Vector3f length = calculateLength();
        
        //Parition the cube into 8 smaller cubes
        Vector3f b1Min = new Vector3f(minXYZ.x, minXYZ.y, minXYZ.z);

        Vector3f b1Max = new Vector3f(minXYZ.x + length.x / 2
                , minXYZ.y + length.y / 2, minXYZ.z + length.z / 2);
        
        Vector3f b2Min = new Vector3f(minXYZ.x + length.x / 2, minXYZ.y, minXYZ.z);
        
        Vector3f b2Max = new Vector3f(minXYZ.x + length.x
                , minXYZ.y + length.y / 2, minXYZ.z + length.z / 2);
        
        Vector3f b3Min = new Vector3f(minXYZ.x, minXYZ.y, minXYZ.z + length.z / 2);
        
        Vector3f b3Max = new Vector3f(minXYZ.x + length.x / 2
                , minXYZ.y + length.y / 2, minXYZ.z + length.z);
        
        Vector3f b4Min = new Vector3f(minXYZ.x + length.x / 2
                , minXYZ.y, minXYZ.z + length.z / 2);
        
        Vector3f b4Max = new Vector3f(minXYZ.x + length.x
                , minXYZ.y + length.y / 2, minXYZ.z + length.z);

        Vector3f b5Min = new Vector3f(minXYZ.x, minXYZ.y + length.y / 2, minXYZ.z);
        
        Vector3f b5Max = new Vector3f(minXYZ.x + length.x / 2
                , minXYZ.y + length.y, minXYZ.z + length.z / 2);
        
        Vector3f b6Min = new Vector3f(minXYZ.x + length.x / 2
                , minXYZ.y + length.y / 2, minXYZ.z);
        
        Vector3f b6Max = new Vector3f(minXYZ.x + length.x
                , minXYZ.y + length.y, minXYZ.z + length.z / 2);
        
        Vector3f b7Min = new Vector3f(minXYZ.x
                , minXYZ.y + length.y / 2, minXYZ.z + length.z / 2);
        
        Vector3f b7Max = new Vector3f(minXYZ.x + length.x / 2
                , minXYZ.y + length.y, minXYZ.z + length.z);
        
        Vector3f b8Min = new Vector3f(minXYZ.x + length.x / 2
                , minXYZ.y + length.y / 2, minXYZ.z + length.z / 2);
        
        Vector3f b8Max = new Vector3f(minXYZ.x + length.x
                , minXYZ.y + length.y, minXYZ.z + length.z);
        
        Vector3f[][] minMax = {
            {b1Min, b1Max},
            {b2Min, b2Max},
            {b3Min, b3Max},
            {b4Min, b4Max},
            {b5Min, b5Max},
            {b6Min, b6Max},
            {b7Min, b7Max},
            {b8Min, b8Max},
        };

        //Generate the 8 children nodes
        for (int i = 0; i < 8; i++) {
            children[i] = new BoxNode(minMax[i][0], minMax[i][1]);
        }
        partitioned = true;
    }

    /**
     * Adds a FaceBox to this BoxNode.
     * @param faceBox the FaceBox to add
     */
    public void addFaceBox(FaceBox faceBox) {
        if (!partitioned) {
            partition();
        }

        for(BoxNode childNode: children) {
            if (faceBox.isCompletelyInside(childNode)){
                childNode.addFaceBox(faceBox);
                return;
            }
        }
        faceBoxes.add(faceBox);
    }

    /**
     * Gets all the intersecting EntityFaces between a Box and the FaceBoxes in this BoxNode.
     * @param box the Box to check
     * @param faces the Collection storing all interesecting EntityFaces
     */
    public void getIntersectingFaces(Box box, Collection<EntityFace> faces) {
        if (partitioned) {
            for(BoxNode childNode: children) {
                if (childNode.intersects(box)){
                    childNode.getIntersectingFaces(box, faces);
                }
            }
        }

        if (intersects(box)) {
            for(FaceBox faceBox: faceBoxes) {
                if (faceBox.intersects(box)) {
                    faces.add(faceBox.getFace());
                }
            }
        }
    }  
}