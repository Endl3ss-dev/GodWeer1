#version 330

#moj_import <decide:cinematic.glsl>

uniform sampler2D InSampler;
uniform sampler2D BloomSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec3 color = scene(InSampler, texCoord) + texture(BloomSampler, texCoord).rgb * Bloom;
    fragColor = vec4(finish(color, texCoord, OutSize), 1.0);
}
