package duber.engine.loaders;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import duber.engine.Utils;
import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Material;
import duber.engine.graphics.Mesh;
import duber.engine.graphics.Texture;

import static org.lwjgl.assimp.Assimp.*;

public class MeshLoader {
    private MeshLoader() {}
    public static Mesh[] load(String resourcePath, String textureDirectory) throws LWJGLException {
        return load(resourcePath, textureDirectory, 
            aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
    }

    public static Mesh[] load(String resourcePath, String textureDirectory, int flags) throws LWJGLException {
        AIScene aiScene = aiImportFile(resourcePath, flags);
        if(aiScene == null) {
            throw new LWJGLException("Could not load model");
        }

        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<Material> materials = new ArrayList<>();

        for(int i = 0; i<numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            processMaterial(aiMaterial, materials, textureDirectory);
        }

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Mesh[] meshes = new Mesh[numMeshes];
        for(int i = 0; i<numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh, materials);
            meshes[i] = mesh;
        }

        return meshes;
    }

    private static void processMaterial(AIMaterial aiMaterial, List<Material> materials, String textureDirectory) throws LWJGLException {

        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String texturePath = path.dataString();

        Texture texture = null;
        if(texturePath != null && texturePath.length() > 0) {
            TextureDatabase textureDatabase = TextureDatabase.getInstance();
            texture = textureDatabase.getTexture(textureDirectory + "/" + texturePath);
        }

        //Set container to store material colours
        AIColor4D colour = AIColor4D.create();
    
        //Set colours as default
        Vector4f ambientColour = Material.DEFAULT_COLOUR;
        Vector4f diffuseColour = Material.DEFAULT_COLOUR;
        Vector4f specularColour = Material.DEFAULT_COLOUR;

        //Get materials ambient colour
        int ambientDefined = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, colour);
        if(ambientDefined == 0) {
            ambientColour = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        //Get materials diffuse colour
        int diffuseDefined = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, colour);
        if(diffuseDefined == 0) {
            diffuseColour = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        //Get materials specular colour
        int specularDefined = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, colour);
        if(specularDefined == 0) {
            specularColour = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        //Add the material to the list
        Material material = new Material(ambientColour, diffuseColour, specularColour, 1.0f);
        material.setTexture(texture);
        materials.add(material);
    }

    private static Mesh processMesh(AIMesh aiMesh, List<Material> materials) {
        List<Float> vertexPositions = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> vertexIndices = new ArrayList<>();

        processVertexPositions(aiMesh, vertexPositions);
        processTextureCoords(aiMesh, textureCoords);
        processNormals(aiMesh, normals);
        processVertexIndices(aiMesh, vertexIndices);

        Mesh mesh = new Mesh(
            Utils.listToFloatArray(vertexPositions),
            Utils.listToFloatArray(textureCoords),
            Utils.listToFloatArray(normals),
            Utils.listToIntArray(vertexIndices)
        );

        Material material;
        int materialIdx = aiMesh.mMaterialIndex();
        if(materialIdx >= 0 && materialIdx < materials.size()) {
            material = materials.get(materialIdx);
        } else {
            material = new Material();
        }

        mesh.setMaterial(material);

        return mesh;
    }

    private static void processVertexPositions(AIMesh aiMesh, List<Float> vertexPositions) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        
        while(aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertexPositions.add(aiVertex.x());
            vertexPositions.add(aiVertex.y());
            vertexPositions.add(aiVertex.z());
        }
    }

    private static void processTextureCoords(AIMesh aiMesh, List<Float> textureCoords) {
        AIVector3D.Buffer aiTextureCoords = aiMesh.mTextureCoords(0);
        if(aiTextureCoords == null) {
            return;
        }
        
        while(aiTextureCoords.remaining() > 0) {
            AIVector3D textCoord = aiTextureCoords.get();
            textureCoords.add(textCoord.x());
            textureCoords.add(1 - textCoord.y());
        }
    }

    private static void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        if(aiNormals == null) {
            return;
        }

        while(aiNormals.remaining() > 0) {
            AIVector3D normal = aiNormals.get();
            normals.add(normal.x());
            normals.add(normal.y());
            normals.add(normal.z());
        }
    }

    private static void processVertexIndices(AIMesh aiMesh, List<Integer> vertexIndices) {
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        while(aiFaces.remaining() > 0) {
            AIFace aiFace = aiFaces.get();
            IntBuffer indexBuffer = aiFace.mIndices();
            while(indexBuffer.remaining() > 0) {
                vertexIndices.add(indexBuffer.get());
            }
        }
    }
}