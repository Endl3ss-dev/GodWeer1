#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform vec3 color;
uniform vec3 color2;
uniform float density;
uniform float scale;
uniform float time;
uniform float speed;
uniform vec2 camera;

in vec2 TexCoord;
out vec4 OutColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    mat2 rot = mat2(cos(0.5), sin(0.5), -sin(0.5), cos(0.5));
    for (int i = 0; i < 5; i++) {
        v += a * noise(p);
        p = rot * p * 2.1;
        a *= 0.45;
    }
    return v;
}

void main() {
    vec2 uv = TexCoord;

    vec4 bloom = texture(Sampler0, uv);
    vec4 mask = texture(Sampler1, uv);

    float outer = bloom.a * (1.0 - mask.a);
    if (outer < 0.005) discard;

    float t = time * speed;
    vec2 offset = camera * 0.3;
    vec2 noiseUV = uv * scale + offset + t * 0.15;

    float mainNoise = fbm(noiseUV);
    float detailNoise = fbm(noiseUV * 2.5 + 100.0 + t * 0.1);
    float warpNoise = fbm(noiseUV + mainNoise * 0.4 + 200.0);

    float combined = mainNoise * 0.5 + detailNoise * 0.3 + warpNoise * 0.2;

    float threshold = 0.38 - density * 0.08;
    float smoke = smoothstep(threshold, threshold + 0.35, combined);

    float spread = clamp(outer * 1.5, 0.0, 1.0);
    smoke *= spread;

    float fadeUp = 1.0 - abs(uv.y - 0.5) * 0.6;
    float fadeSides = 1.0 - abs(uv.x - 0.5) * 0.4;
    float fade = clamp(fadeUp * fadeSides, 0.0, 1.0);

    float alpha = clamp(smoke * density * 1.8 * fade, 0.0, 0.85);
    alpha *= 1.0 - mask.a * 0.6;

    if (alpha < 0.005) discard;

    vec3 grad = mix(color, color2, combined * 0.6 + 0.2);
    OutColor = vec4(grad, alpha);
}
