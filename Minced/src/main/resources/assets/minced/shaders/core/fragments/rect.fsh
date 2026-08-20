#version 150

layout(std140) uniform Uniforms {
    vec4 color1;
    vec2 uSize;
    vec2 uLocation;
    float radius;
    vec4 scissorRect;
};

out vec4 fragColor;

float roundedBoxSDF(vec2 p, vec2 size, float radius) {
    vec2 d = abs(p) - size + vec2(radius);
    return min(max(d.x, d.y), 0.0) + length(max(d, 0.0)) - radius;
}

void main() {
    vec2 fragPx = gl_FragCoord.xy;

    if (scissorRect.z > 0.0) {
        if (fragPx.x < scissorRect.x || fragPx.x > scissorRect.z ||
            fragPx.y < scissorRect.y || fragPx.y > scissorRect.w) {
            discard;
        }
    }

    vec2 localPos = fragPx - uLocation - uSize * 0.5;
    float dist = roundedBoxSDF(localPos, uSize * 0.5, radius);
    float alpha = clamp(1.0 - dist, 0.0, 1.0) * color1.a;
    if (alpha < 0.01) discard;
    fragColor = vec4(color1.rgb, alpha);
}
