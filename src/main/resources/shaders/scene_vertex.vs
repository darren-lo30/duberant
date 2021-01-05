#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in vec3 vertexNormal;

out vec2 outTextureCoord;
out vec3 modelViewVertexNormal;
out vec3 modelViewVertexPosition;
out vec4 modelLightViewVertexPosition;
out mat4 outModelViewMatrix;

const int MAX_WEIGHTS = 4;
const int MAX_JOINTS = 150;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

uniform mat4 jointsMatrix[MAX_JOINTS];

void main() {

    vec4 modelViewPosition = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * modelViewPosition;
    
    modelViewVertexNormal = normalize(modelViewMatrix * vec4(vertexNormal, 0.0)).xyz;
    modelViewVertexPosition = modelViewPosition.xyz;

    outModelViewMatrix = modelViewMatrix;
    outTextureCoord = textureCoord;
}