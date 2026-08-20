#version 150

in vec3 Position;

out vec2 texCoord;

void main() {
    // Position is already in NDC [-1,1] from Java
    gl_Position = vec4(Position, 1.0);
    // texCoord not used in fragment shader anymore (using gl_FragCoord directly)
    texCoord = vec2(0.0);
}