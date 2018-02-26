#version 150

layout ( triangles ) in;
layout ( triangle_strip, max_vertices = 3) out;

out vec3 finalColour;

uniform mat4 projectionViewMatrix;
uniform vec3 cameraPosition;

const vec3 lightDirection = normalize(vec3(0.4, -1.0, 0.8));
const vec3 waterColour = vec3(0.2, 0.4, 0.45);
const vec3 lightColour = vec3(1.0, 0.6, 0.6);
const float reflectivity = 0.5;
const float shineDamper = 14.0;
const float ambientLighting = 0.3;

vec3 calculateTriangleNormal(){
	vec3 tangent = gl_in[1].gl_Position.xyz - gl_in[0].gl_Position.xyz;
	vec3 bitangent = gl_in[2].gl_Position.xyz - gl_in[0].gl_Position.xyz;
	vec3 normal = cross(tangent, bitangent);	
	return normalize(normal);
}

vec3 calculateSpecular(vec4 worldPosition, vec3 normal){
	vec3 viewVector = normalize(cameraPosition - worldPosition.xyz);
	vec3 reflectedLightDirection = reflect(lightDirection, normal);
	float specularFactor = dot(reflectedLightDirection, viewVector);
	specularFactor = max(pow(specularFactor, shineDamper), 0.0);
	return lightColour * specularFactor * reflectivity;
}

void main(void){

	vec3 normal = calculateTriangleNormal();
	float brightness = max(dot(-lightDirection, normal), ambientLighting);
	vec3 colour = waterColour * brightness;

	vec4 worldPosition = gl_in[0].gl_Position;
	gl_Position = projectionViewMatrix * worldPosition;
	finalColour = colour + calculateSpecular(worldPosition, normal);
	EmitVertex();
	
	worldPosition = gl_in[1].gl_Position;
	gl_Position = projectionViewMatrix * worldPosition;
	finalColour = colour+ calculateSpecular(worldPosition, normal);
	EmitVertex();
	
	worldPosition = gl_in[2].gl_Position;
	gl_Position = projectionViewMatrix * worldPosition;
	finalColour = colour+ calculateSpecular(worldPosition, normal);
	EmitVertex();
	
	EndPrimitive();

}