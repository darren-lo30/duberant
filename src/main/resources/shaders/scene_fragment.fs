#version 330

in vec2 outTextureCoord;
in vec3 modelViewVertexNormal;
in vec3 modelViewVertexPosition;
in mat4 outModelViewMatrix;

out vec4 fragColour;

const int MAX_POINT_LIGHTS = 100;
const int MAX_SPOT_LIGHTS = 100;

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct PointLight {
    vec3 colour;
    vec3 position;
    float intensity;
    Attenuation attenuation;
};

struct DirectionalLight {
    vec3 colour;
    vec3 direction;
    float intensity;
};

struct SpotLight {
    PointLight pointLight;
    vec3 coneDirection;
    float cutOffAngle;
};

struct Material {
    vec4 ambientColour;
    vec4 diffuseColour;
    vec4 specularColour;
    float reflectance;

    int hasTexture;
    int hasNormalMap;
};



uniform sampler2D texture_sampler;
uniform sampler2D normalMap;

uniform Material material;
uniform vec3 ambientLight;
uniform float specularPower;
uniform DirectionalLight directionalLight;

uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

vec4 ambientColour;
vec4 diffuseColour;
vec4 specularColour;

void setupColours(Material material, vec2 textureCoord){
    if(material.hasTexture == 1){
        ambientColour = texture(texture_sampler, textureCoord);
        diffuseColour = ambientColour;
        specularColour = ambientColour;
    } else {
        ambientColour = material.ambientColour;
        diffuseColour = material.diffuseColour;
        specularColour = material.specularColour;
    }
}

vec4 calculateAdjustedLightColour(vec3 lightColour, float lightIntensity, vec3 position, vec3 vectorToLightSource, vec3 normal){
    vec4 adjustedDiffuseColour = vec4(0, 0, 0, 0);
    vec4 adjustedSpecularColour = vec4(0, 0, 0, 0);


    //Calculating diffuse light (Less light reaches areas that have a greater angle with light source)
    float diffuseFactor = max(dot(normal, vectorToLightSource), 0.0);
    adjustedDiffuseColour = diffuseColour * vec4(lightColour, 1.0) * lightIntensity * diffuseFactor;

    //Calculate specular light (Reflected light)
    vec3 cameraDirection = normalize(-position);
    vec3 vectorFromLightSource = -vectorToLightSource;
    vec3 reflectedLight = normalize(reflect(vectorFromLightSource, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    adjustedSpecularColour = specularColour * lightIntensity * specularFactor * material.reflectance *  vec4(lightColour, 1.0);

    return (adjustedDiffuseColour + adjustedSpecularColour);
}

vec4 calculatePointLight(PointLight light, vec3 position, vec3 normal){
    //Calculate adjusted light colour factoring in diffuse and specular light
    vec3 lightDirection = light.position - position;
    vec3 vectorToLightSource = normalize(lightDirection);
    vec4 adjustedLightColour = calculateAdjustedLightColour(light.colour, light.intensity, position, vectorToLightSource, normal);
    
    //Attenuation (Light becomes less intense the further away you are)
    float distance = length(lightDirection);
    float attenuationFactor = light.attenuation.constant + light.attenuation.linear * distance +
        light.attenuation.exponent * distance * distance;
    return adjustedLightColour / attenuationFactor;
}

vec4 calculateSpotLight(SpotLight light, vec3 position, vec3 normal){
    vec3 lightDirection = light.pointLight.position - position;
    vec3 vectorToLightSource = normalize(lightDirection);
    vec3 vectorFromLightSource = -vectorToLightSource;
    
    float cosAngleBetween = dot(vectorFromLightSource, normalize(light.coneDirection));
    
    float cosCutOffAngle = cos(light.cutOffAngle);
    vec4 adjustedColour = vec4(0, 0, 0, 0);
    if(cosAngleBetween > cosCutOffAngle){
        adjustedColour = calculatePointLight(light.pointLight, position, normal);
        adjustedColour *= (1.0 - (1.0 - cosAngleBetween)/(1.0 - cosCutOffAngle));
    } 

    return adjustedColour;
}

vec4 calculateDirectionalLight(DirectionalLight light, vec3 position, vec3 normal){
    return calculateAdjustedLightColour(light.colour, light.intensity, position, 
        normalize(light.direction), normal);
}

vec3 calculateNormal(Material material, vec3 normal, vec2 textureCoord, mat4 modelViewMatrix){
    vec3 adjustedNormal = normal;
    if(material.hasNormalMap == 1){
        adjustedNormal = texture(normalMap, textureCoord).rgb;
        //Map to [-1, 1]
        adjustedNormal = normalize(adjustedNormal * 2 - 1);
        adjustedNormal = normalize(modelViewMatrix * vec4(adjustedNormal, 0.0)).xyz;
    }

    return adjustedNormal;
}


void main(){
    setupColours(material, outTextureCoord);

    vec3 mappedNormal = calculateNormal(material, modelViewVertexNormal, outTextureCoord, outModelViewMatrix);
    //Calculate lighting component from diffuse and specular lighting
    vec4 pointLightComponent = vec4(0, 0, 0, 0);
    for(int i = 0; i<MAX_POINT_LIGHTS; i++){
        if(pointLights[i].intensity > 0){
            pointLightComponent += calculatePointLight(pointLights[i], modelViewVertexPosition, mappedNormal);
        }
    }

    vec4 spotLightComponent = vec4(0, 0, 0, 0);
    for(int i = 0; i<MAX_SPOT_LIGHTS; i++){
        if(spotLights[i].pointLight.intensity > 0){
            spotLightComponent += calculateSpotLight(spotLights[i], modelViewVertexPosition, mappedNormal);
        }
    }
    vec4 directionalLightComponent = calculateDirectionalLight(directionalLight, modelViewVertexPosition, mappedNormal);

    //Calculate frag colour with lighting and shadow
    fragColour = ambientColour * vec4(ambientLight, 1) + 
        (pointLightComponent + spotLightComponent + directionalLightComponent);

}