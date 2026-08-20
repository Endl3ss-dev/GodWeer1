#version 150

uniform vec2 size;
uniform vec4 color0, color1, color2, color3;
uniform vec4 radius;
uniform vec4 ColorModulator;

in vec2 TexCoord;
out vec4 fragColor;

float signedDistanceField(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x  = (p.y > 0.0) ? r.x : r.y;

    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

vec4 createGradient(vec2 pos) {
    return mix(mix(color0, color1, pos.y),
               mix(color2, color3, pos.y),
               pos.x);
}

void main() {
    vec2 halfSize = size * 0.5;
    float sdf = signedDistanceField(halfSize - TexCoord * size, halfSize - 1.0, radius);
    vec4 gradient = createGradient(TexCoord);
    float edgeAlpha = 1.0 - smoothstep(0.0, 1.0, sdf);
    fragColor = vec4(gradient.rgb, gradient.a * edgeAlpha) * ColorModulator;
}
