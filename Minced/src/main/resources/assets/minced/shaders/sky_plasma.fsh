#version 150

uniform vec4 u_Color;
uniform vec2 u_Resolution;
uniform vec2 u_Mouse;
uniform float u_Scale;
uniform float u_Time;
uniform float u_Fov;
uniform float u_WorldLocked;
uniform mat4 u_WorldFromView;

out vec4 fragColor;

#define TAU 6.28318530718
#define MAX_ITER 5
#define INTENSITY 0.005

void main() {
    vec2 uv;
    if (u_WorldLocked > 0.5) {
        vec2 ndc = (gl_FragCoord.xy / u_Resolution) * 2.0 - 1.0;
        float aspect = u_Resolution.x / u_Resolution.y;
        float tanFov = tan(u_Fov * 0.5);
        vec3 rayView = normalize(vec3(ndc.x * aspect * tanFov, ndc.y * tanFov, -1.0));
        vec3 ray = normalize((u_WorldFromView * vec4(rayView, 0.0)).xyz);
        uv = vec2(atan(ray.x, ray.z) / 6.28318530718, asin(clamp(ray.y, -1.0, 1.0)) / 3.14159265) * u_Scale;
    } else {
        uv = (gl_FragCoord.xy / u_Resolution.xy + u_Mouse) * u_Scale;
    }

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

        float compX = p.x * INTENSITY / max(abs(sinX), 0.001);
        float compY = p.y * INTENSITY / max(abs(cosY), 0.001);
        float distance = sqrt(compX * compX + compY * compY);

        value += 1.0 / distance;
    }

    value = 1.17 - pow(value / float(MAX_ITER), 1.4);

    float pwr = value * value;
    pwr = pwr * pwr;
    pwr = pwr * pwr;

    vec3 baseColor = vec3(abs(pwr));
    vec3 enhancedColor = clamp(baseColor + vec3(0.0, 0.35, 0.5), 0.0, 1.0);
    float luminance = dot(enhancedColor, vec3(0.299, 0.587, 0.114));

    fragColor = vec4(vec3(luminance) * u_Color.rgb * 1.4, luminance * u_Color.a);
}
