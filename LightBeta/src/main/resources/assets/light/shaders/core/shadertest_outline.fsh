#version 150

uniform sampler2D MaskSampler;
uniform sampler2D BloomSampler;

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
    float center = texture(MaskSampler, uv).a;
    if (center > 0.01) {
        discard;
    }

    vec2 texel = screen.zw * params1.y;
    float maxAlpha = 0.0;
    for (int x = -2; x <= 2; x++) {
        for (int y = -2; y <= 2; y++) {
            if (x == 0 && y == 0) {
                continue;
            }
            vec2 offset = vec2(float(x), float(y)) * texel;
            maxAlpha = max(maxAlpha, texture(MaskSampler, uv + offset).a);
        }
    }
    if (maxAlpha < 0.01) {
        discard;
    }

    vec3 col;
    int mode = int(params1.x + 0.5);
    if (mode == 1) {
        col = hue2rgb(fract(uv.y * params1.w + params2.x * params1.z));
    } else if (mode == 2) {
        vec4 bloom = texture(BloomSampler, uv);
        vec3 base = bloom.rgb / max(bloom.a, 0.001);
        float peak = max(base.r, max(base.g, base.b));
        if (peak > 0.001) {
            base /= peak;
        }
        float lum = dot(base, vec3(0.299, 0.587, 0.114));
        col = clamp(mix(vec3(lum), base, color2.a), 0.0, 1.0);
    } else {
        col = outlineColor.rgb;
    }

    fragColor = vec4(col, maxAlpha * outlineColor.a);
}
