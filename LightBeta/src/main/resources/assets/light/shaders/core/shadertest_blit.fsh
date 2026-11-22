#version 150

uniform sampler2D InputSampler;

layout(std140) uniform ShaderTestData {
    vec4 color1;
    vec4 color2;
    vec4 fillColor;
    vec4 outlineColor;
    vec4 params0;
    vec4 params1;
    vec4 params2;
    vec4 params3;
    vec4 screen;
    vec4 misc;
};

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 c = texture(InputSampler, texCoord);
    if (c.a < 0.002) {
        discard;
    }
    fragColor = vec4(c.rgb / max(c.a, 0.001), clamp(c.a * misc.z, 0.0, 1.0));
}
