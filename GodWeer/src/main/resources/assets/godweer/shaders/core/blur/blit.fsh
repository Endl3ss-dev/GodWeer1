#version 150

in vec2 TexCoord;
in vec4 FragColor;

uniform sampler2D Sampler0;

out vec4 OutColor;

void main() {
    OutColor = texture(Sampler0, TexCoord) * FragColor;
}
