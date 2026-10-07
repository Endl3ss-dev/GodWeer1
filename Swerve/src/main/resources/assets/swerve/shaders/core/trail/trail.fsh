#version 150

in vec2 TexCoord;
in vec4 FragColor;

uniform float Time;
uniform float Speed;
uniform float Frequency;
uniform float Glow;
uniform float Opacity;
uniform float Distort;
uniform int Closed;
uniform int ColorMode;
uniform vec4 BaseColor;
uniform vec4 AccentColor;
uniform vec4 ColorModulator;

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

// 3 октавы fbm — компромисс между качеством дыма и стоимостью на пиксель
float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 3; i++) {
        value += noise(p) * amplitude;
        p = p * 2.17 + 13.7;
        amplitude *= 0.5;
    }
    return value;
}

vec3 palette(float t, float v) {
    if (ColorMode == 1) {
        return 0.5 + 0.5 * cos(6.28318 * (t + vec3(0.0, 0.333, 0.667)));
    } else if (ColorMode == 2) {
        vec3 deep = vec3(0.05, 0.02, 0.28);
        vec3 mid = vec3(0.0, 0.72, 0.95);
        vec3 top = vec3(0.72, 0.12, 0.9);
        return mix(mix(deep, mid, clamp(t, 0.0, 1.0)), top, v * 0.8);
    } else if (ColorMode == 3) {
        vec3 core = vec3(1.0, 0.95, 0.55);
        vec3 flame = vec3(1.0, 0.35, 0.05);
        vec3 ember = vec3(0.35, 0.02, 0.0);
        return mix(mix(ember, flame, clamp(t, 0.0, 1.0)), core, pow(1.0 - v, 2.0));
    }
    return mix(BaseColor.rgb, AccentColor.rgb, clamp(t, 0.0, 1.0));
}

void main() {
    float u = TexCoord.x;
    float v = TexCoord.y;
    float t = Time * Speed;

    // u = 1 у свежей головы следа, u = 0 у затухающего хвоста
    float head = u;
    float tail = 1.0 - u;

    // Замкнутые формы (труба, кольца) не обрезаем по краям: v там угловая координата
    float band = Closed == 1
        ? 1.0
        : smoothstep(0.0, 0.20, v) * smoothstep(0.0, 0.16, 1.0 - v);

    // Дым: два слоя fbm, всплывающие вверх и растворяющиеся к хвосту
    vec2 uv = vec2(u * Frequency * 0.9, v * 1.6 - t * 0.55);
    float smokeA = fbm(uv);
    float smokeB = fbm(uv * 2.3 + vec2(t * 0.18, -t * 0.32));
    float smoke = mix(smokeA, smokeB, 0.45);

    float rise = Closed == 1
        ? 0.55 + smoke * 0.65
        : smoothstep(1.05, 0.15, v + (smoke - 0.5) * Distort * 1.2);

    vec3 col = palette(smoke * 0.55 + head * 0.45, v);
    col += vec3(1.0) * pow(smoke, 3.0) * 0.4 * Glow;

    float alpha = band * rise * (0.15 + smoke * 0.9);

    // Затухание к хвосту + яркая «горячая» голова следа
    float fade = pow(head, 1.35);
    float hot = smoothstep(0.82, 1.0, head);
    col += palette(1.0, v) * hot * 0.6 * Glow;
    col *= 1.0 + smoke * 0.35;

    alpha *= fade * Opacity * FragColor.a;
    alpha *= 1.0 - tail * tail * 0.25;

    if (alpha <= 0.004) {
        discard;
    }

    OutColor = vec4(col * FragColor.rgb, clamp(alpha, 0.0, 1.0)) * ColorModulator;
}