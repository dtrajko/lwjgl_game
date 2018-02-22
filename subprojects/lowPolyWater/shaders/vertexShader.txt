#version 150

in vec3 position;

uniform float time;

const float PI = 3.1415926535897932384626433832795;
const float amplitude = 0.01;

float generateHeight(){
	float component1 = sin(2.0 * PI * time + (position.x * 16.0)) * amplitude;
	float component2 = sin(2.0 * PI * time + (position.y * position.x * 8.0)) * amplitude;
	return component1 + component2;
}

void main(void){
	
	float height = generateHeight();
	gl_Position = vec4(position.x, height, position.y, 1.0);

}