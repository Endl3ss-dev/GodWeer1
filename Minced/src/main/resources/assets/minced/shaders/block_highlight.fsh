#version 150

uniform float time;
uniform vec2 resolution;
uniform vec3 themeColor;

in vec4 vertexColor;
out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    
    vec2 p1 = uv + vec2(sin(uv.y * 15.0 + time * 1.3) * 0.03, cos(uv.x * 22.0 - time * 0.9) * 0.04);
    vec2 p2 = uv + vec2(cos(uv.y * 18.0 - time * 1.5) * 0.02, sin(uv.x * 12.0 + time * 1.1) * 0.03);

    float w1 = sin((p1.x * 18.0 + p1.y * 26.0) - time * 3.1) * 0.5 + 0.5;
    float w2 = sin((p2.x * 25.0 - p2.y * 14.0) + time * 2.4) * 0.5 + 0.5;
    float w3 = sin((p1.x * 35.0 + p2.y * 8.0) + time * 4.2) * 0.5 + 0.5;
    
    float intensity = 0.35 + (w1 * w2) * 0.55 + w3 * 0.2;

    vec3 col = themeColor * intensity * 1.45;
    float alpha = clamp((0.15 + (w1 * w2) * 0.45 + w3 * 0.1) * vertexColor.a, 0.0, 1.0);
    
    fragColor = vec4(col, alpha);
}
