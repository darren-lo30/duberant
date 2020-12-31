/*
package duber.engine.physics.collisions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import duber.engine.entities.ConcreteEntity;

public class SweepAndPrune extends BroadPhaseAlgorithm {
    private static final int NUM_AXES = 3;
    
    private static final int NUM_SENTINELS = 2;
    private static final int INVALID_BOX_INDEX = Integer.MAX_VALUE;
    
    private Box[] boxes;
    Map<ConcreteEntity, Integer> entityBoxIndex;
    private EndPoint[][] endPointsXYZ;
    
    private final Queue<Integer> freeBoxIndices;

    private int numBoxes;

    private final long minSentinelValue = encodeFloatToLong(Float.MIN_VALUE);
    private final long maxSentinelValue = encodeFloatToLong(Float.MAX_VALUE);

    private final long insertedMinEndPointValue = minSentinelValue + 1;
    private final long insertedMaxEndPointValue = maxSentinelValue - 1;

    private final long removedMinEndPointValue = maxSentinelValue - 2;
    private final long removedMaxEndPointValue = maxSentinelValue - 1;

    public SweepAndPrune(){
        boxes = new Box[0];
        endPointsXYZ = new EndPoint[3][];
        entityBoxIndex = new HashMap<>();
        freeBoxIndices = new LinkedList<>();
        numBoxes = 0;
    }

    @Override
    public void addEntity(ConcreteEntity entity) {
        AABB boundingBox = entity.getBoundingBox();
        
        if(boundingBox == null){
            throw new IllegalArgumentException("Bounding box was null");
        }
        
        int insertBoxIndex;
        if(!freeBoxIndices.isEmpty()) {
            insertBoxIndex = freeBoxIndices.poll();
        } else {
            if(numBoxes >= boxes.length) {
                resizeArray();
            }
            insertBoxIndex = numBoxes;
        }

        if(boxes[insertBoxIndex] == null){
            boxes[insertBoxIndex] = new Box();
        }

        //Update the sentinels for each of the axes
        int lastEndPointIndex = numBoxes * 2 + NUM_SENTINELS - 1;
        for(int axis = 0; axis<NUM_AXES; axis++) {
            validateSentinels(axis);
            //Shift the max sentinel up 2 spaces
            int newMaxSentinelIndex = lastEndPointIndex + 2;
            
            if(endPointsXYZ[axis][newMaxSentinelIndex] == null){
                endPointsXYZ[axis][newMaxSentinelIndex] = new EndPoint();
            } 
            endPointsXYZ[axis][newMaxSentinelIndex].setMaxSentinel();
        }

        Box box = boxes[insertBoxIndex];
        box.setEntity(entity);

        for(int axis = 0; axis<NUM_AXES; axis++){
            int newMinEndPointIndex = lastEndPointIndex;
            int newMaxEndPointIndex = lastEndPointIndex + 1;


            box.setMinEndPointIdx(axis, newMinEndPointIndex);
            box.setMaxEndPointIdx(axis, newMaxEndPointIndex);

            if(endPointsXYZ[axis][newMinEndPointIndex] == null){
                endPointsXYZ[axis][newMinEndPointIndex] = new EndPoint();
            }
            EndPoint minimumEndPoint = endPointsXYZ[axis][newMinEndPointIndex];
            minimumEndPoint.setProperties(insertBoxIndex, true, insertedMinEndPointValue);

            if(endPointsXYZ[axis][newMaxEndPointIndex] == null){
                endPointsXYZ[axis][newMaxEndPointIndex] = new EndPoint();
            }
            EndPoint maximumEndPoint = endPointsXYZ[axis][newMaxEndPointIndex];            
            maximumEndPoint.setProperties(insertBoxIndex, true, insertedMaxEndPointValue);
        }
        
        numBoxes++;
        entityBoxIndex.put(entity, insertBoxIndex);
        updateEntity(entity);
    }

    @Override
    public void updateEntity(ConcreteEntity entity){
        AABBLong aabbLong = new AABBLong(entity.getBoundingBox());
        updateEntity(entity, aabbLong);
    }

    private void updateEntity(ConcreteEntity entity, AABBLong aabbLong) {
        int boxIndex = entityBoxIndex.get(entity);
        Box box = boxes[boxIndex];
        
        
        for(int axis = 0; axis<NUM_AXES; axis++) {
            updateMinEndPoint(entity, box, axis, aabbLong);
            updateMaxEndPoint(entity, box, axis, aabbLong);
        }
    }

    private void updateMinEndPoint(ConcreteEntity entity, Box box, int axis, AABBLong aabbLong){
        int otherAxis1 = (axis+1) % NUM_AXES;
        int otherAxis2 = (axis+2) % NUM_AXES;
        EndPoint[] currAxisEndPoints = endPointsXYZ[axis];

        //Update the new min endpoint
        int originalMinEndPointIndex = box.getMinEndPointIdx(axis);
        EndPoint originalMinEndPoint = currAxisEndPoints[originalMinEndPointIndex];

        int currEndPointIndex = box.getMinEndPointIdx(axis);
        EndPoint currEndPoint = currAxisEndPoints[currEndPointIndex];

        if(!currEndPoint.isMin()){
            throw new IllegalStateException("Did not find a minimum end point");
        }

        long newEndPointValue = aabbLong.getMin(axis);

        //Shift left if the new end point value is lower than the previous value, otherwise shift right
        int shiftDirection = newEndPointValue < currEndPoint.getValue() ? -1 : 1;

        //Shift left
        currEndPoint.setValue(newEndPointValue);

        //While the end point we are updating is less than the end point before it, then we continue shifting down
        while(isContinueShifting(shiftDirection, newEndPointValue, currAxisEndPoints[currEndPointIndex+shiftDirection].getValue())){

            currEndPointIndex += shiftDirection;

            currEndPoint = currAxisEndPoints[currEndPointIndex];
            Box currEndPointBox = boxes[currEndPoint.getBoxIdx()];

            int currEndPointNewIndex = currEndPointIndex - shiftDirection;
            if(!currEndPoint.isMin()){
                if(box != currEndPointBox && compute2DIntersect(box, currEndPointBox, otherAxis1, otherAxis2) && 
                    compute1DIntersect(currEndPointBox, aabbLong, currAxisEndPoints, axis)){
                    
                    ConcreteEntity otherEntity = currEndPointBox.getEntity();
                    if(shiftDirection == -1) {
                        //If you are shifting left and are the min index, then you go to the left of a max index, you will intersect  
                        collidingPairs.addEntityPair(entity, otherEntity);
                    } else {
                        //If you are shifting right and are the min index, then you pass a max index, you will stop intersecting
                        collidingPairs.removeEntityPair(entity, otherEntity);
                    }
                }

                //Update the min end point of the current box
                currEndPointBox.setMaxEndPointIdx(axis, currEndPointNewIndex);                        
            } else {
                currEndPointBox.setMinEndPointIdx(axis, currEndPointNewIndex);                        
            }

            //Set the end point at the index after the current one as the current end point
            //We are essentially shifting the current end point to the right
            currAxisEndPoints[currEndPointIndex - shiftDirection] = currEndPoint;
        }

        //If we actually swapped some positions, then we change the curr endpoint idx
        if(currEndPointIndex != originalMinEndPointIndex){
            box.setMinEndPointIdx(axis, currEndPointIndex);
            currAxisEndPoints[currEndPointIndex] = originalMinEndPoint;
        }
    }

    private void updateMaxEndPoint(ConcreteEntity entity, Box box, int axis, AABBLong aabbLong){
        int otherAxis1 = (axis+1) % NUM_AXES;
        int otherAxis2 = (axis+2) % NUM_AXES;
        EndPoint[] currAxisEndPoints = endPointsXYZ[axis];

        //Update the new max endpoint
        int originalMaxEndPointIndex = box.getMaxEndPointIdx(axis);
        EndPoint originalMaxEndPoint = currAxisEndPoints[originalMaxEndPointIndex];

        int currEndPointIndex = box.getMaxEndPointIdx(axis);
        EndPoint currEndPoint = currAxisEndPoints[currEndPointIndex];

        if(!currEndPoint.isMax()){
            throw new IllegalStateException("Did not find a maximum end point");
        }

        long newEndPointValue = aabbLong.getMax(axis);

        //Shift left if the new end point value is lower than the previous value, otherwise shift right
        int shiftDirection = newEndPointValue < currEndPoint.getValue() ? -1 : 1;

        //Shift left
        currEndPoint.setValue(newEndPointValue);

        //While the end point we are updating is less than the end point before it, then we continue shifting down
        while(isContinueShifting(shiftDirection, newEndPointValue, currAxisEndPoints[currEndPointIndex+shiftDirection].getValue())){

            currEndPointIndex += shiftDirection;

            currEndPoint = currAxisEndPoints[currEndPointIndex];
            Box currEndPointBox = boxes[currEndPoint.getBoxIdx()];

            int currEndPointNewIndex = currEndPointIndex - shiftDirection;
            if(currEndPoint.isMin()){
                if(box != currEndPointBox && compute2DIntersect(box, currEndPointBox, otherAxis1, otherAxis2) && 
                    compute1DIntersect(currEndPointBox, aabbLong, currAxisEndPoints, axis)){

                        
                    ConcreteEntity otherEntity = currEndPointBox.getEntity();
                    if(shiftDirection == -1) {
                        //If you are shifting left and are a max index, then you pass a min index, you stop intersecting
                        collidingPairs.removeEntityPair(entity, otherEntity);
                    } else {
                        //If you are shifting right and are a max index, then you start intersecting
                        collidingPairs.addEntityPair(entity, otherEntity);
                    }
                }

                //Update the min end point of the current box
                currEndPointBox.setMinEndPointIdx(axis, currEndPointNewIndex);                        
            } else {
                //Update the max end point index of the current box which i
                currEndPointBox.setMaxEndPointIdx(axis, currEndPointNewIndex);                        
            }

            //Set the end point at the index after the current one as the current end point
            //We are essentially shifting the current end point to the right
            currAxisEndPoints[currEndPointIndex - shiftDirection] = currEndPoint;
        }

        //If we actually swapped some positions, then we change the curr endpoint idx
        if(currEndPointIndex != originalMaxEndPointIndex){
            box.setMaxEndPointIdx(axis, currEndPointIndex);
            currAxisEndPoints[currEndPointIndex] = originalMaxEndPoint;
        }
    }

    @Override
    public void removeEntity(ConcreteEntity entity) {
        AABBLong aabbLong = new AABBLong(removedMinEndPointValue, removedMaxEndPointValue);
        updateEntity(entity, aabbLong);

        int boxIndex = entityBoxIndex.get(entity);
        
        ///Remove end points
        for(int axis = 0; axis<NUM_AXES; axis++){
            validateSentinels(axis);

            //Shift the sentinel 2 down from its previous possition
            EndPoint newMaxSentinel = endPointsXYZ[axis][getMaxSentinelIndex() - 2];
            newMaxSentinel.setMaxSentinel();
        }

        freeBoxIndices.add(boxIndex);
        entityBoxIndex.remove(entity);
        numBoxes--;
    }

    private int getMaxSentinelIndex(){
        return numBoxes*2 + NUM_SENTINELS - 1;
    }

    private void validateSentinels(int axis){
        int maxSentinelIndex = getMaxSentinelIndex();
        if(endPointsXYZ[axis][0].getBoxIdx() != INVALID_BOX_INDEX || endPointsXYZ[axis][0].isMax()){
            throw new IllegalStateException("The first box for axis: " + axis + " is invalid as it should be a sentinel");
        }
        if(endPointsXYZ[axis][maxSentinelIndex].getBoxIdx() != INVALID_BOX_INDEX || endPointsXYZ[axis][maxSentinelIndex].isMin()){
            throw new IllegalStateException("The last box for axis: " + axis + " is invalid as it should be a sentinel");
        }
    }

    private void resizeArray(){
        int maxNumBoxes = boxes.length;

        int newMaxNumBoxes = maxNumBoxes == 0 ? 10000 : maxNumBoxes * 2;
        int numEndPoints = numBoxes * 2 + NUM_SENTINELS;
        int newMaxNumEndPoints = newMaxNumBoxes * 2 + NUM_SENTINELS;

        Box[] newBoxes = new Box[newMaxNumBoxes];
        EndPoint[][] newEndPointsXYZ = new EndPoint[3][newMaxNumEndPoints];

        if(maxNumBoxes == 0){
            //Set sentinels
            for(int axis = 0; axis<NUM_AXES; axis++){
                newEndPointsXYZ[axis][0] = new EndPoint();
                newEndPointsXYZ[axis][0].setMinSentinel();

                newEndPointsXYZ[axis][1] = new EndPoint();
                newEndPointsXYZ[axis][1].setMaxSentinel();
            }
        } else {
            //Copy previous arrays
            System.arraycopy(boxes, 0, newBoxes, 0, numBoxes);
            for(int axis = 0; axis<NUM_AXES; axis++){
                System.arraycopy(endPointsXYZ[axis], 0, newEndPointsXYZ[axis], 0, numEndPoints);
            }
        }

        boxes = newBoxes;
        endPointsXYZ = newEndPointsXYZ;
    }

    private static boolean compute1DIntersect(Box box1, AABBLong box2, EndPoint[] endPoints, int axis){
        return endPoints[box1.getMaxEndPointIdx(axis)].getValue() >= box2.getMin(axis);
    }

    private static boolean compute2DIntersect(Box box1, Box box2, int axis1, int axis2){
        return !(box2.getMaxEndPointIdx(axis1) < box1.getMinEndPointIdx(axis1) || 
                 box1.getMaxEndPointIdx(axis1) < box2.getMinEndPointIdx(axis1) || 
                 box2.getMaxEndPointIdx(axis2) < box1.getMinEndPointIdx(axis2) || 
                 box1.getMaxEndPointIdx(axis2) < box2.getMinEndPointIdx(axis2));
    }

    private static boolean isContinueShifting(int shiftDirection, long currValue, long nextValue) {
        if(shiftDirection == -1) {
            return currValue > nextValue;     
        } else {
            return currValue < nextValue;
        }
    }

    private static final long encodeFloatToLong(float num){
        long intNum = (long) Float.floatToIntBits(num) & 0xFFFFFFFFl;
        if ((intNum & 0x80000000l) == 0x80000000l) {
            intNum = ~intNum & 0xFFFFFFFFl;
        } else {
            intNum |= 0x80000000l;
        }
        return intNum;
    }

    private class EndPoint {
        private int boxIdx;
        private boolean isMin;
        private long value;

        public void setMinSentinel() {
            boxIdx = INVALID_BOX_INDEX;
            isMin = true;
            value = minSentinelValue;
        }

        public void setMaxSentinel() {
            boxIdx = INVALID_BOX_INDEX;
            isMin = false;
            value = maxSentinelValue;
        }

        public void setProperties(int boxIdx, boolean isMin, long value){
            this.boxIdx = boxIdx;
            this.isMin = isMin;
            this.value = value;
        }

        public long getValue() {
            return value;
        }
        
        public void setValue(long value) {
            this.value = value;
        }

        public int getBoxIdx() {
            return boxIdx;
        }

        public boolean isMin() {
            return isMin;
        }

        public boolean isMax(){
            return !isMin;
        }
    }

    private class AABBLong {
        private final long[] minXYZ = new long[NUM_AXES];
        private final long[] maxXYZ = new long[NUM_AXES];
        
        public AABBLong(AABB boundingBox) {
            minXYZ[0] = encodeFloatToLong(boundingBox.getMinXYZ().x());
            minXYZ[1] = encodeFloatToLong(boundingBox.getMinXYZ().y());
            minXYZ[2] = encodeFloatToLong(boundingBox.getMinXYZ().z());

            maxXYZ[0] = encodeFloatToLong(boundingBox.getMaxXYZ().x());
            maxXYZ[1] = encodeFloatToLong(boundingBox.getMaxXYZ().y());
            maxXYZ[2] = encodeFloatToLong(boundingBox.getMaxXYZ().z());
        }

        public AABBLong(long minValue, long maxValue) {
            for(int axis = 0; axis<NUM_AXES; axis++){
                minXYZ[axis] = minValue;
                maxXYZ[axis] = maxValue;
            }
        }

        public long getMin(int axis) {
            return minXYZ[axis];
        }

        public long getMax(int axis) {
            return maxXYZ[axis];
        }
    }

    private class Box {
        private int[] minEndPointIdx = new int[3];
        private int[] maxEndPointIdx = new int[3];
        private ConcreteEntity entity;

        public ConcreteEntity getEntity() {
            return entity;
        }

        public void setEntity(ConcreteEntity entity) {
            this.entity = entity;
        }
        
        public int getMinEndPointIdx(int axis) {
            return minEndPointIdx[axis];
        }

        public void setMinEndPointIdx(int axis, int value) {
            minEndPointIdx[axis] = value;
        }

        public int getMaxEndPointIdx(int axis) {
            return maxEndPointIdx[axis];
        }

        public void setMaxEndPointIdx(int axis, int value){
            maxEndPointIdx[axis] = value;
        }
    }



}*/