#version 330

const int MAX_WEIGHTS = 4;
const int MAX_JOINTS = 150;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoord;
layout (location = 2) in vec3 vertexNormal;
layout (location = 3) in vec4 jointWeights;
layout (location = 4) in ivec4 jointIndices;

out vec2 outTextureCoord;
out vec3 modelViewVertexNormal;
out vec3 modelViewVertexPosition;
out vec4 modelLightViewVertexPosition;
out mat4 outModelViewMatrix;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

uniform mat4 jointsMatrix[MAX_JOINTS];

void main() {
    vec4 initPos = vec4(0, 0, 0, 0);
    vec4 initNormal = vec4(0, 0, 0, 0);
    int count = 0;
    for(int i = 0; i < MAX_WEIGHTS; i++) {
        float weight = jointWeights[i];
        if (weight > 0) {
            count++;
            int jointIndex = jointIndices[i];
            vec4 tmpPos = jointsMatrix[jointIndex] * vec4(position, 1.0);
            initPos += weight * tmpPos;

            vec4 tmpNormal = jointsMatrix[jointIndex] * vec4(vertexNormal, 0.0);
            initNormal += weight * tmpNormal;
        }
    }
    if (count == 0) {
        initPos = vec4(position, 1.0);
        initNormal = vec4(vertexNormal, 0.0);
    }
    vec4 modelViewPosition = modelViewMatrix * initPos;
    gl_Position = projectionMatrix * modelViewPosition;
    outTextureCoord = textureCoord;
    
    modelViewVertexNormal = normalize(modelViewMatrix * initNormal).xyz;
    modelViewVertexPosition = modelViewPosition.xyz;

    outModelViewMatrix = modelViewMatrix;
}