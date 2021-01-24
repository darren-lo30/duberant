#version 330

in vec2 outTextureCoord;
in vec3 modelViewPos;
out vec4 fragColour;

uniform int hasTexture;
uniform vec4 colour;
uniform sampler2D texture_sampler;
uniform vec3 ambientLight;

void main() {
    if (hasTexture == 1) {
        fragColour = vec4(ambientLight, 1.0) * texture(texture_sampler, outTextureCoord);
    } else {
        fragColour = colour * vec4(ambientLight, 1.0);
    }
}