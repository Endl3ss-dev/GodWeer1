#version 150

// Живой фон GUI Swerve: шёлковые ленты и плазменные прожилки в цветах палитры.
// Цвета приходят из MenuTheme (акцент, второй цвет, база) — фон всегда в теме меню.

in vec2 FragCoord;
in vec4 FragColor;

uniform float Time;
uniform vec2 Resolution;
uniform vec4 ColorA;
uniform vec4 ColorB;
uniform vec4 Base;
uniform float Intensity;

out vec4 OutColor;

// ---- шум ---------------------------------------------------------

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float amp = 0.55;
    mat2 rot = mat2(0.80, 0.60, -0.60, 0.80);
    for (int i = 0; i < 5; i++) {
        v += amp * noise(p);
        p = rot * p * 2.02 + vec2(3.1, 7.7);
        amp *= 0.52;
    }
    return v;
}

// ---- полотно ------------------------------------------------------

void main() {
    float aspect = Resolution.x / max(1.0, Resolution.y);
    vec2 uv = FragCoord;
    vec2 p = (uv - 0.5) * vec2(aspect, 1.0) * 1.35;
    float t = Time * 0.055;

    // двойное доменное искажение — «шёлковый поток»
    vec2 q = vec2(fbm(p * 1.15 + vec2(0.0, t * 1.15)),
                  fbm(p * 1.15 + vec2(5.2, t * 0.90)));
    vec2 r = vec2(fbm(p * 1.35 + 2.3 * q + vec2(1.7, 9.2) + t * 0.65),
                  fbm(p * 1.35 + 2.3 * q + vec2(8.3, 2.8) - t * 0.50));
    float f = fbm(p + 2.1 * r);

    // диагональные ленты, текущие сквозь искажение
    float band = sin((p.x * 1.35 - p.y * 1.75) * 1.9 + f * 5.4 + t * 2.2);
    float ribbons = pow(0.5 + 0.5 * band, 3.0);

    // тонкие плазменные прожилки
    float veins = (1.0 - smoothstep(0.0, 0.045, abs(f - 0.52))) * 0.90
                + (1.0 - smoothstep(0.0, 0.035, abs(f - 0.30))) * 0.60;

    // цвет: база -> акцент -> второй цвет
    vec3 col = Base.rgb;
    float glowA = smoothstep(0.15, 0.95, f + ribbons * 0.35);
    float glowB = smoothstep(0.32, 1.05, length(r) * 0.95 + ribbons * 0.42);
    col = mix(col, ColorA.rgb, glowA * 0.55);
    col = mix(col, ColorB.rgb, glowB * 0.48);
    col += ColorA.rgb * veins * 0.55;
    col += ColorB.rgb * pow(ribbons, 2.0) * 0.16;

    // мраморные контуры — тонкие нити поверх полотна
    float contour = abs(fract(f * 5.0 - t * 1.1) - 0.5);
    float lines = (1.0 - smoothstep(0.0, 0.075, contour)) * 0.10;
    col += mix(ColorA.rgb, ColorB.rgb, ribbons) * lines;

    // пыль-искры, мерцающие в потоке
    vec2 cell = floor(uv * Resolution / 3.0);
    float star = step(0.9972, hash(cell + floor(Time * 1.5)));
    float twinkle = 0.5 + 0.5 * sin(Time * 3.0 + hash(cell) * 40.0);
    col += star * twinkle * 0.45 * mix(ColorA.rgb, ColorB.rgb, hash(cell * 1.37));

    // виньетка и лёгкий подъём к центру
    float vig = 1.0 - smoothstep(0.28, 1.42, length(p * vec2(0.82, 1.0)));
    col *= 0.32 + 0.68 * vig;

    // зерно против бэндинга
    col += (hash(uv * Resolution + fract(Time) * 251.0) - 0.5) * 0.022;

    float alpha = Intensity * Base.a * (0.62 + 0.38 * vig);
    OutColor = vec4(max(col, vec3(0.0)), alpha);
}
