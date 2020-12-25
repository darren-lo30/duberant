#version 330

const int MAX_WEIGHTS = 4;
const int MAX_JOINTS = 150;

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in vec3 vertexNormal;
layout (location=3) in vec4 jointWeights;
layout (location=4) in ivec4 jointIndices;
layout (location=5) in mat4 instancedModelViewMatrix;
layout (location=9) in mat4 instancedModelLightViewMatrix;
layout (location=13) in vec2 textureOffset;

out vec2 outTextureCoord;
out vec3 modelViewVertexNormal;
out vec3 modelViewVertexPosition;
out vec4 modelLightViewVertexPosition;
out mat4 outModelViewMatrix;

uniform int isInstanced;
uniform mat4 nonInstancedModelViewMatrix;
uniform mat4 projectionMatrix;

uniform mat4 jointsMatrix[MAX_JOINTS];

uniform int numRows;
uniform int numColumns;

void main(){
    mat4 modelViewMatrix;

    if(isInstanced == 1){
        modelViewMatrix = instancedModelViewMatrix;
    } else {
        modelViewMatrix = nonInstancedModelViewMatrix;
    }

    vec4 modelViewPosition = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * modelViewPosition;
    
    modelViewVertexNormal = normalize(modelViewMatrix * vec4(vertexNormal, 0.0)).xyz;
    modelViewVertexPosition = modelViewPosition.xyz;

    outModelViewMatrix = modelViewMatrix;

    float x = (textureCoord.x / numColumns + textureOffset.x);
    float y = (textureCoord.y / numRows + textureOffset.y);
    outTextureCoord = vec2(x, y);
}