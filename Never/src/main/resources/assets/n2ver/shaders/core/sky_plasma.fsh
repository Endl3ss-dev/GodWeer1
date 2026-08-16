#version 150

in vec3 vWorldDir;

uniform vec4 u_Color;
uniform float u_Scale;
uniform float u_Time;
uniform float u_Outline;
uniform float u_Glow;
uniform float u_Fill;
uniform float u_Alpha;

out vec4 OutColor;

#define TAU 6.28318530718
#define MAX_ITER 5
#define INTENSITY 0.005

void main() {
    vec3 ray = normalize(vWorldDir);
    vec2 uv = ray.xz / (1.0 + abs(ray.y)) * 0.5 * max(u_Scale, 0.05);

    vec2 p = mod(uv * TAU, TAU) - 250.0;
    vec2 iterPos = p;
    float value = 1.0;
    float timeOffset = u_Time * 0.5 + 23.0;

    for (int n = 0; n < MAX_ITER; n++) {
        float iterTime = timeOffset * (1.0 - (3.5 / float(n + 1)));
        iterPos = p + vec2(
            cos(iterTime - iterPos.x) + sin(iterTime + iterPos.y),
            sin(iterTime - iterPos.y) + cos(iterTime + iterPos.x)
        );

        float sinX = sin(iterPos.x + iterTime);
        float cosY = cos(iterPos.y + iterTime);
        float compX = p.x * INTENSITY / max(abs(sinX), 0.02);
        float compY = p.y * INTENSITY / max(abs(cosY), 0.02);
        float dist = max(sqrt(compX * compX + compY * compY), 0.04);
        value += 1.0 / dist;
    }

    value = 1.17 - pow(value / float(MAX_ITER), 1.4);
    float pwr = value * value;
    pwr = pwr * pwr;
    pwr = pwr * pwr;
    pwr = clamp(abs(pwr), 0.0, 1.0);

    vec3 baseColor = vec3(pwr);
    vec3 enhancedColor = clamp(baseColor + vec3(0.0, 0.35, 0.5), 0.0, 1.0);
    float luminance = dot(enhancedColor, vec3(0.299, 0.587, 0.114));

    float outlineAmt = clamp(u_Outline, 0.0, 1.0);
    float outline = pwr * outlineAmt;
    float glowFalloff = 1.0 / max(u_Glow * 0.08, 0.05);
    float glow = pow(pwr, glowFalloff);

    vec3 tint = u_Color.rgb * 1.4;
    vec3 fillCol = tint * u_Fill * 0.12;
    vec3 col = fillCol + tint * (luminance * 0.65 + outline * 0.85 + glow * 0.35);
    col *= max(u_Alpha, 0.0);

    OutColor = vec4(col, clamp(u_Alpha, 0.0, 1.0));
}
