#version 150

layout(std140) uniform GhostData {
    mat4 ProjView;
    vec4 Corner0;
    vec4 Corner1;
    vec4 Corner2;
    vec4 Corner3;
    vec4 UvRect;
    vec4 AlphaPad;
};

uniform sampler2D GhostSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 src = texture(GhostSampler, texCoord);
    float a = src.a;
    if (a <= 0.0001) {
        fragColor = vec4(0.0);
        return;
    }

    vec3 color = src.rgb / a;
    float opaque = smoothstep(0.06, 0.85, a);
    float outA = mix(a, 1.0, opaque);
    fragColor = vec4(color * outA, outA) * AlphaPad.x;
}
