package duber.engine.loaders;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIQuatKey;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVectorKey;
import org.lwjgl.assimp.AIVertexWeight;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;

import duber.engine.utilities.Utils;
import duber.engine.exceptions.LWJGLException;

import duber.engine.graphics.Material;
import duber.engine.graphics.Mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.nio.IntBuffer;

import duber.engine.graphics.Texture;
import duber.engine.graphics.animations.AnimationData;
import duber.engine.graphics.animations.AnimationFrame;

import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_AMBIENT;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;

public class MeshLoader {
    private static Map<MeshResource, MeshData> meshDatabase = new HashMap<>();

    private MeshLoader() {}

    public static MeshData load(String modelFile) throws LWJGLException {
        return load(modelFile, null);
    }

    public static MeshData load(String modelFile, String texturesDirectory) throws LWJGLException {
        return load(modelFile, texturesDirectory, MeshResource.DEFAULT_FLAGS);
    }

    public static MeshData load(String modelFile, String texturesDirectory, int flags) throws LWJGLException {
        return load(new MeshResource(modelFile, texturesDirectory, flags));
    }

    public static MeshData load(MeshResource meshResource) throws LWJGLException {
        if(meshDatabase.containsKey(meshResource)) {
            return meshDatabase.get(meshResource);
        }
        
        String modelFile = meshResource.getModelFile();
        String texturesDirectory = meshResource.getTextureDirectory();
        int flags = meshResource.getFlags();

        AIScene aiScene = aiImportFile(modelFile, flags);
        if (aiScene == null) {
            throw new LWJGLException("Error loading model");
        }

        List<Material> materials;
        if(texturesDirectory == null) {
            materials = new ArrayList<>(0);
        } else {
            materials = new ArrayList<>();
            int numMaterials = aiScene.mNumMaterials();
            PointerBuffer aiMaterials = aiScene.mMaterials();
    
            for(int i = 0; i<numMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
                processMaterial(aiMaterial, materials, texturesDirectory);
            }
        }        

        List<Bone> boneList = new ArrayList<>();
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Mesh[] meshes = new Mesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh, materials, boneList);
            meshes[i] = mesh;
        }

        Node rootNode = buildNodesTree(aiScene.mRootNode(), null);
        Matrix4f globalInverseTransformation = toMatrix(aiScene.mRootNode().mTransformation()).invert();
        Map<String, AnimationData> animationData = processAnimations(aiScene, boneList, rootNode,
                globalInverseTransformation);
        return new MeshData(meshes, animationData);
    }

    private static Node buildNodesTree(AINode aiNode, Node parentNode) {
        String nodeName = aiNode.mName().dataString();
        Node node = new Node(nodeName, parentNode, toMatrix(aiNode.mTransformation()));

        int numChildren = aiNode.mNumChildren();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < numChildren; i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Node childNode = buildNodesTree(aiChildNode, node);
            node.addChild(childNode);
        }
        return node;
    }

    private static Map<String, AnimationData> processAnimations(AIScene aiScene, List<Bone> boneList,
                                                            Node rootNode, Matrix4f globalInverseTransformation) {
        Map<String, AnimationData> animationData = new HashMap<>();

        // Process all animationData
        int numAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for (int i = 0; i < numAnimations; i++) {
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));
            int maxFrames = calcAnimationMaxFrames(aiAnimation);

            List<AnimationFrame> frames = new ArrayList<>();
            AnimationData animation = new AnimationData(aiAnimation.mName().dataString(), frames, aiAnimation.mDuration());
            animationData.put(animation.getName(), animation);

            for (int j = 0; j < maxFrames; j++) {
                AnimationFrame animatedFrame = new AnimationFrame();
                buildFrameMatrices(aiAnimation, boneList, animatedFrame, j, rootNode,
                        rootNode.getNodeTransformation(), globalInverseTransformation);
                frames.add(animatedFrame);
            }
        }
        return animationData;
    }

    private static void buildFrameMatrices(AIAnimation aiAnimation, List<Bone> boneList, AnimationFrame animatedFrame, int frame,
                                           Node node, Matrix4f parentTransformation, Matrix4f globalInverseTransform) {
        String nodeName = node.getName();
        AINodeAnim aiNodeAnim = findAIAnimNode(aiAnimation, nodeName);
        Matrix4f nodeTransform = node.getNodeTransformation();
        if (aiNodeAnim != null) {
            nodeTransform = buildNodeTransformationMatrix(aiNodeAnim, frame);
        }
        Matrix4f nodeGlobalTransform = new Matrix4f(parentTransformation).mul(nodeTransform);

        List<Bone> affectedBones = boneList.stream().filter( b -> b.getBoneName().equals(nodeName)).collect(Collectors.toList());
        for (Bone bone: affectedBones) {
            Matrix4f boneTransform = new Matrix4f(globalInverseTransform).mul(nodeGlobalTransform).
                    mul(bone.getOffsetMatrix());
            animatedFrame.setMatrix(bone.getBoneId(), boneTransform);
        }

        for (Node childNode : node.getChildren()) {
            buildFrameMatrices(aiAnimation, boneList, animatedFrame, frame, childNode, nodeGlobalTransform,
                    globalInverseTransform);
        }
    }

    private static Matrix4f buildNodeTransformationMatrix(AINodeAnim aiNodeAnim, int frame) {
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        AIVectorKey aiVecKey;
        AIVector3D vec;

        Matrix4f nodeTransform = new Matrix4f();
        int numPositions = aiNodeAnim.mNumPositionKeys();
        if (numPositions > 0) {
            aiVecKey = positionKeys.get(Math.min(numPositions - 1, frame));
            vec = aiVecKey.mValue();
            nodeTransform.translate(vec.x(), vec.y(), vec.z());
        }
        int numRotations = aiNodeAnim.mNumRotationKeys();
        if (numRotations > 0) {
            AIQuatKey quatKey = rotationKeys.get(Math.min(numRotations - 1, frame));
            AIQuaternion aiQuat = quatKey.mValue();
            Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
            nodeTransform.rotate(quat);
        }
        int numScalingKeys = aiNodeAnim.mNumScalingKeys();
        if (numScalingKeys > 0) {
            aiVecKey = scalingKeys.get(Math.min(numScalingKeys - 1, frame));
            vec = aiVecKey.mValue();
            nodeTransform.scale(vec.x(), vec.y(), vec.z());
        }

        return nodeTransform;
    }

    private static AINodeAnim findAIAnimNode(AIAnimation aiAnimation, String nodeName) {
        AINodeAnim result = null;
        int numAnimNodes = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();
        for (int i=0; i<numAnimNodes; i++) {
            AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
            if ( nodeName.equals(aiNodeAnim.mNodeName().dataString())) {
                result = aiNodeAnim;
                break;
            }
        }
        return result;
    }

    private static int calcAnimationMaxFrames(AIAnimation aiAnimation) {
        int maxFrames = 0;
        int numNodeAnims = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();
        for (int i=0; i<numNodeAnims; i++) {
            AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
            int numFrames = Math.max(Math.max(aiNodeAnim.mNumPositionKeys(), aiNodeAnim.mNumScalingKeys()),
                    aiNodeAnim.mNumRotationKeys());
            maxFrames = Math.max(maxFrames, numFrames);
        }

        return maxFrames;
    }

    private static void processBones(AIMesh aiMesh, List<Bone> boneList, List<Integer> boneIds,
                                     List<Float> weights) {
        Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        for (int i = 0; i < numBones; i++) {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int id = boneList.size();
            Bone bone = new Bone(id, aiBone.mName().dataString(), toMatrix(aiBone.mOffsetMatrix()));
            boneList.add(bone);
            int numWeights = aiBone.mNumWeights();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            for (int j = 0; j < numWeights; j++) {
                AIVertexWeight aiWeight = aiWeights.get(j);
                VertexWeight vw = new VertexWeight(bone.getBoneId(), aiWeight.mVertexId(),
                        aiWeight.mWeight());
                List<VertexWeight> vertexWeightList = weightSet.get(vw.getVertexId());
                if (vertexWeightList == null) {
                    vertexWeightList = new ArrayList<>();
                    weightSet.put(vw.getVertexId(), vertexWeightList);
                }
                vertexWeightList.add(vw);
            }
        }

        int numVertices = aiMesh.mNumVertices();
        for (int i = 0; i < numVertices; i++) {
            List<VertexWeight> vertexWeightList = weightSet.get(i);
            for (int j = 0; j < Mesh.MAX_WEIGHTS; j++) {
                if (vertexWeightList != null && j < vertexWeightList.size()) {
                    VertexWeight vw = vertexWeightList.get(j);
                    weights.add(vw.getWeight());
                    boneIds.add(vw.getBoneId());
                } else {
                    weights.add(0.0f);
                    boneIds.add(0);
                }
            }
        }
    }

    private static Mesh processMesh(AIMesh aiMesh, List<Material> materials, List<Bone> boneList) {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Integer> boneIds = new ArrayList<>();
        List<Float> weights = new ArrayList<>();

        processVertexPositions(aiMesh, vertices);
        processNormals(aiMesh, normals);
        processTextureCoords(aiMesh, textures);
        processVertexIndices(aiMesh, indices);
        processBones(aiMesh, boneList, boneIds, weights);

        // Texture coordinates may not have been populated. We need at least the empty slots
        if (textures.isEmpty()) {
            int numElements = (vertices.size() / 3) * 2;
            for (int i=0; i<numElements; i++) {
                textures.add(0.0f);
            }
        }

        Mesh mesh = new Mesh(Utils.listToFloatArray(vertices), Utils.listToFloatArray(textures),
                Utils.listToFloatArray(normals), Utils.listToIntArray(indices),
                Utils.listToIntArray(boneIds), Utils.listToFloatArray(weights));
        Material material;
        int materialIdx = aiMesh.mMaterialIndex();
        if (materialIdx >= 0 && materialIdx < materials.size()) {
            material = materials.get(materialIdx);
        } else {
            material = new Material();
        }
        mesh.setMaterial(material);

        return mesh;
    }

    private static Matrix4f toMatrix(AIMatrix4x4 aiMatrix4x4) {
        Matrix4f result = new Matrix4f();
        result.m00(aiMatrix4x4.a1());
        result.m10(aiMatrix4x4.a2());
        result.m20(aiMatrix4x4.a3());
        result.m30(aiMatrix4x4.a4());
        result.m01(aiMatrix4x4.b1());
        result.m11(aiMatrix4x4.b2());
        result.m21(aiMatrix4x4.b3());
        result.m31(aiMatrix4x4.b4());
        result.m02(aiMatrix4x4.c1());
        result.m12(aiMatrix4x4.c2());
        result.m22(aiMatrix4x4.c3());
        result.m32(aiMatrix4x4.c4());
        result.m03(aiMatrix4x4.d1());
        result.m13(aiMatrix4x4.d2());
        result.m23(aiMatrix4x4.d3());
        result.m33(aiMatrix4x4.d4());

        return result;
    }

    private static void processMaterial(AIMaterial aiMaterial, List<Material> materials, String texturesDirectory) throws LWJGLException {

        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String texturePath = path.dataString();

        Texture texture = null;
        if(texturePath != null && texturePath.length() > 0) {
            TextureDatabase textureDatabase = TextureDatabase.getInstance();
            texture = textureDatabase.getTexture(texturesDirectory + "/" + texturePath);
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

    public static class MeshData {
        private Mesh[] meshes;
        private Map<String, AnimationData> animationData;

        public MeshData(Mesh[] meshes, Map<String, AnimationData> animationData) {
            this.meshes = meshes;
            this.animationData = animationData;
        }

        public Mesh[] getMeshes() {
            return meshes;
        }

        public Map<String, AnimationData> getAnimationData() {
            return animationData;
        }
    }
}