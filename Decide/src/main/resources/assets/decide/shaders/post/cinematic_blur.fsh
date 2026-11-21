#version 330

#moj_import <decide:cinematic.glsl>
#moj_import <decide:cinematic_blur.glsl>

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform BlurConfig {
    vec2 BlurDir;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    fragColor = vec4(gaussian(InSampler, texCoord, BlurDir / InSize, Radius * InSize.y).rgb, 1.0);
}
