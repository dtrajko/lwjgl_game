#version 150

out vec4 out_Color;

in vec3 colour;

void main(void){

	out_Color = vec4(colour, 1.0);

}