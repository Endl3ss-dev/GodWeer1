#version 150

uniform sampler2D InputSampler;

layout(std140) uniform Uniforms {
    vec4 color;
    vec2 size;
    vec2 location;
    float radius;
    float alpha;
    vec2 uvStart;
    vec4 uvSize;
    vec4 scissorRect;
};

out vec4 fragColor;

float roundedBoxSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b, 0.0)) - r;
}

void main() {
    vec2 fragPx = gl_FragCoord.xy;
    
    if (scissorRect.z > 0.0) {
        if (fragPx.x < scissorRect.x || fragPx.x > scissorRect.z ||
            fragPx.y < scissorRect.y || fragPx.y > scissorRect.w) {
            discard;
        }
    }
    
    vec2 halfSize = size * 0.5;
    vec2 relativePos = fragPx - location;
    
    float u = relativePos.x / size.x;
    float v = 1.0 - (relativePos.y / size.y);
    
    if (u < 0.0 || u > 1.0 || v < 0.0 || v > 1.0) discard;
    
    vec2 centered = halfSize - relativePos;
    centered.y = -centered.y;
    float dist = roundedBoxSDF(centered, halfSize - radius - 1.0, radius);
    float smoothedAlpha = (1.0 - smoothstep(0.0, 2.0, dist)) * alpha;
    
    if (smoothedAlpha < 0.01) discard;
    
    vec2 texCoord = uvStart + vec2(u, v) * uvSize.xy;
    
    vec4 texColor = texture(InputSampler, texCoord);
    fragColor = vec4(texColor.rgb * color.rgb, texColor.a * smoothedAlpha * color.a);
}
