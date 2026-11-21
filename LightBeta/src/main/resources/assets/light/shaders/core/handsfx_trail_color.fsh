#version 150

uniform sampler2D BloomSampler;

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
    vec2 uv = texCoord;
    vec4 bloom = texture(BloomSampler, uv);
    float alpha = clamp(bloom.a * misc.y, 0.0, 1.0);

    vec3 col;
    if (params0.x > 0.5) {
        vec3 base = bloom.rgb / max(bloom.a, 0.001);
        float peak = max(base.r, max(base.g, base.b));
        if (peak > 0.001) {
            base /= peak;
        }
        float lum = dot(base, vec3(0.299, 0.587, 0.114));
        col = clamp(mix(vec3(lum), base, color2.a), 0.0, 1.0);
    } else {
        col = mix(color1.rgb, color2.rgb, uv.y);
    }

    fragColor = vec4(col, alpha);
}
