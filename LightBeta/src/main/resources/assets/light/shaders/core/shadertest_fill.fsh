#version 150

uniform sampler2D HandSampler;
uniform sampler2D MaskSampler;

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

vec3 hue2rgb(float h) {
    vec3 p = abs(fract(vec3(h) + vec3(0.0, 2.0 / 3.0, 1.0 / 3.0)) * 6.0 - 3.0);
    return clamp(p - 1.0, 0.0, 1.0);
}

void main() {
    vec2 uv = texCoord;
    float mask = texture(MaskSampler, uv).a;
    if (mask < 0.05) {
        discard;
    }

    vec4 hand = texture(HandSampler, uv);
    vec3 fill = params0.y > 0.5
        ? hue2rgb(fract(uv.y * params1.w + params2.x * params1.z))
        : fillColor.rgb;

    if (params0.z > 0.5) {
        fill *= mix(1.0, dot(hand.rgb, vec3(0.299, 0.587, 0.114)), params0.w);
    }

    fragColor = vec4(mix(hand.rgb, fill, fillColor.a), mask);
}
