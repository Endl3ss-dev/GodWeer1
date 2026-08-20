#version 150

uniform sampler2D blurredTexture;
uniform vec2 resolution;
uniform vec2 size;
uniform vec4 radius;
uniform float alpha;
uniform vec4 color;
uniform vec4 ColorModulator;

in vec2 texCoord;
out vec4 fragColor;

float signedDistanceField(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x  = (p.y > 0.0) ? r.x : r.y;
    
    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main() {
    vec2 rectHalf = size * 0.5;

    vec2 blurredPos = gl_FragCoord.xy / resolution;
    vec3 blurredColor = mix(texture(blurredTexture, blurredPos).rgb, color.rgb, color.a);

    vec2 localCoord = texCoord * size;

    float sdf = signedDistanceField(rectHalf - localCoord, rectHalf - 1.0, radius);
    float rr = 1.0 - smoothstep(0.0, 1.0, sdf);

    fragColor = vec4(blurredColor, rr * alpha) * ColorModulator;
}
