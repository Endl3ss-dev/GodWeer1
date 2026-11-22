#version 330

#moj_import <decide:cinematic.glsl>

uniform sampler2D InSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

in vec2 texCoord;

out vec4 fragColor;

vec3 bright(vec2 uv) {
    vec3 color = texture(InSampler, uv).rgb;
    float peak = max(color.r, max(color.g, color.b));
    return color * smoothstep(Threshold, Threshold + 0.25, peak);
}

void main() {
    vec2 quarter = 0.25 / OutSize;
    vec3 sum = bright(texCoord + quarter * vec2(-1.0, -1.0)) + bright(texCoord + quarter * vec2(1.0, -1.0))
            + bright(texCoord + quarter * vec2(-1.0, 1.0)) + bright(texCoord + quarter);
    fragColor = vec4(sum * 0.25, 1.0);
}
