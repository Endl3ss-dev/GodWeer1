#version 120

varying vec3 worldPos;

void main() {
    worldPos = gl_Vertex.xyz;
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}

