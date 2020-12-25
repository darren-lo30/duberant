package duber.engine.loaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import duber.engine.Utils;
import duber.engine.graphics.InstancedMesh;
import duber.engine.graphics.Mesh;

public class OBJLoader {
    private OBJLoader(){}   

    public static Mesh loadMesh(String fileName) throws IOException {
        return loadMesh(fileName, 1);
    }
    
    public static Mesh loadMesh(String fileName, int instances) throws IOException {
        List<String> allLines = Utils.readAllLines(fileName);

        List<Vector3f> vertexPositions = new ArrayList<>();
        List<Vector2f> textureCoords = new ArrayList<>();
        List<Vector3f> vertexNormals = new ArrayList<>();
        
        List<Face> faces = new ArrayList<>();

        for(String line: allLines){
            String[] tokens = line.split("\\s+");
            switch (tokens[0]){
                case "v":
                    Vector3f vertexPosition = new Vector3f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3])
                    );
                    vertexPositions.add(vertexPosition);
                    break;
                case "vt":
                    Vector2f textureCoord = new Vector2f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2])
                    );
                    textureCoords.add(textureCoord);
                    break;
                case "vn":
                    Vector3f vertexNormal = new Vector3f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3])
                    );
                    vertexNormals.add(vertexNormal);
                    break;
                case "f":
                    faces.add(new Face(tokens[1], tokens[2], tokens[3]));
                    break;
                default:
                    break;
            }
        }        

        return meshFromFaces(vertexPositions, textureCoords, vertexNormals, faces, instances);

    }

    public static Mesh meshFromFaces(List<Vector3f> vertexPositionsList, List<Vector2f> textureCoordsList, 
        List<Vector3f> vertexNormalsList, List<Face> facesList, int instances){
        List<Integer> vertexIndices = new ArrayList<>();
        
        //Fill the position array used in the mesh
        float[] positionsArray = new float[vertexPositionsList.size() * 3];
        for(int i = 0; i<vertexPositionsList.size(); i++){
            positionsArray[i * 3] = vertexPositionsList.get(i).x;
            positionsArray[i*3 + 1] = vertexPositionsList.get(i).y;
            positionsArray[i*3 + 2] = vertexPositionsList.get(i).z;
        }

        float[] textureCoordsArray = new float[vertexPositionsList.size() * 2];
        float[] normalsArray = new float[vertexPositionsList.size() * 3];

        //Extracts face data into arrays that the mesh can use
        for(Face face: facesList){
            IndexGroup[] faceVertices = face.getVertices();
            for(IndexGroup faceVertex: faceVertices){
                processFaceVertex(faceVertex, textureCoordsList, vertexNormalsList, vertexIndices, textureCoordsArray, normalsArray);
            }
        }

        int[] vertexIndicesArray = vertexIndices.stream().mapToInt((Integer i) -> i).toArray();

        if(instances > 1){
            return new InstancedMesh(positionsArray, textureCoordsArray, normalsArray, vertexIndicesArray, instances);
        } else {
            return new Mesh(positionsArray, textureCoordsArray, normalsArray, vertexIndicesArray);
        }
    }

    public static void processFaceVertex(IndexGroup faceVertex, List<Vector2f> textureCoordList, 
        List<Vector3f> vertexNormalsList, List<Integer> vertexIndices, float[] textureCoordsArray, float[] normalsArray){

            //Set vertex indices
            int vertexIndex = faceVertex.getPositionIndex();
            vertexIndices.add(vertexIndex);

            //Set texture coordinate in array
            if(faceVertex.getTextureCoordIndex() >= 0){
                Vector2f textureCoord = textureCoordList.get(faceVertex.getTextureCoordIndex());
                textureCoordsArray[vertexIndex * 2] = textureCoord.x;
                textureCoordsArray[vertexIndex * 2 + 1] = 1 - textureCoord.y;
            }

            //Set vertexNormal vector in array
            if(faceVertex.getVertexNormalIndex() >= 0){
                Vector3f vertexNormal = vertexNormalsList.get(faceVertex.getVertexNormalIndex());
                normalsArray[vertexIndex * 3] = vertexNormal.x;
                normalsArray[vertexIndex * 3 + 1] = vertexNormal.y;
                normalsArray[vertexIndex * 3 + 2] = vertexNormal.z;
            }
    }

    private static class Face {
        private IndexGroup[] vertices;

        public Face(String vertex1, String vertex2, String vertex3){
            vertices = new IndexGroup[3];
            vertices[0] = strToIndexGroup(vertex1);
            vertices[1] = strToIndexGroup(vertex2);
            vertices[2] = strToIndexGroup(vertex3);
        }

        public IndexGroup strToIndexGroup(String str){
            IndexGroup indexGroup = new IndexGroup();

            String[] tokens = str.split("/");
            int length = tokens.length;

            indexGroup.setPositionIndex(Integer.parseInt(tokens[0]) - 1);
            if(length > 1){
                if(tokens[1].length() > 0){
                    indexGroup.setTextureCoordIndex(Integer.parseInt(tokens[1]) - 1);
                }
                if(length > 2){
                    indexGroup.setVertexNormalIndex(Integer.parseInt(tokens[2]) - 1);
                }
            }

            return indexGroup;
        }

        public IndexGroup[] getVertices(){
            return vertices;
        }
    }

    private static class IndexGroup {
        public static final int NO_INDEX = -1;

        private int positionIndex;
        private int textureCoordIndex;
        private int vertexNormalIndex;

        public IndexGroup(){
            positionIndex = NO_INDEX;
            textureCoordIndex = NO_INDEX;
            vertexNormalIndex = NO_INDEX;
        }

        public int getPositionIndex() {
            return positionIndex;
        }

        public void setPositionIndex(int positionIndex) {
            this.positionIndex = positionIndex;
        }

        public int getTextureCoordIndex() {
            return textureCoordIndex;
        }

        public void setTextureCoordIndex(int textureCoordIndex) {
            this.textureCoordIndex = textureCoordIndex;
        }

        public int getVertexNormalIndex() {
            return vertexNormalIndex;
        }

        public void setVertexNormalIndex(int vertexNormalIndex) {
            this.vertexNormalIndex = vertexNormalIndex;
        }
    }
}