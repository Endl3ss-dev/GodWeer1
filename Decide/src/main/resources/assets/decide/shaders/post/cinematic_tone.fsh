#version 330

#moj_import <decide:cinematic.glsl>

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    fragColor = vec4(finish(scene(InSampler, texCoord), texCoord, OutSize), 1.0);
}
