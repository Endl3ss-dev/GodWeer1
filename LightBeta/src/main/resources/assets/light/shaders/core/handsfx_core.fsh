#version 150

uniform sampler2D MaskSampler;

layout(std140) uniform HandsFxData {
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
    vec4 src = texture(MaskSampler, texCoord);
    if (src.a < 0.01) {
        discard;
    }
    vec3 col = src.rgb / max(src.a, 0.001);
    float lum = max(max(col.r, col.g), col.b);
    float alpha = src.a * params3.w * (0.35 + lum * 0.65);
    fragColor = vec4(col, clamp(alpha, 0.0, 1.0));
}
