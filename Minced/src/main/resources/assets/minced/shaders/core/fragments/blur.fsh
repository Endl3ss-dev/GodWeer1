#version 150

uniform sampler2D InputSampler;

layout(std140) uniform Uniforms {
    vec2 InputResolution;
    vec2 uSize;
    vec2 uLocation;
    vec4 radius;
    float blurRadius;
    float Brightness;
    vec4 color1;
    vec4 scissorRect;
};

in vec2 texCoord;
out vec4 fragColor;

const float PI2    = 6.2831853;
const float GOLDEN = 2.39996323;
const int   TAP_COUNT = 3;

float roundedBoxSDF(vec2 p, vec2 b, vec4 r, float smoothness) {
    vec2 s = sign(p);
    float cornerRadius =
        (s.x > 0.0 && s.y > 0.0) ? r.x :
        (s.x < 0.0 && s.y > 0.0) ? r.y :
        (s.x < 0.0 && s.y < 0.0) ? r.z :
                                   r.w;

    vec2 q = abs(p) - b + cornerRadius;

    vec2 qClamped = max(q, 0.0);
    float len = pow(
        pow(abs(qClamped.x), smoothness) +
        pow(abs(qClamped.y), smoothness),
        1.0 / smoothness
    );

    return min(max(q.x, q.y), 0.0) + len - cornerRadius;
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

mat2 rot(float a){
    float c = cos(a), s = sin(a);
    return mat2(c, -s, s, c);
}

vec3 wideMipBlur(vec2 uv, float pxRadius) {
    vec2 texel = 1.0 / InputResolution;
    float lod = 0.0;

    vec3 acc = textureLod(InputSampler, uv, lod).rgb;
    float wacc = 1.0;

    const vec2 dirs[3] = vec2[3](
        vec2(1.0, 0.0),
        vec2(0.7071, 0.7071),
        vec2(0.0, 1.0)
    );

    for (int i = 0; i < 3; ++i) {
        vec2 stepUV = dirs[i] * pxRadius * texel;
        acc += textureLod(InputSampler, uv + stepUV, lod).rgb;
        acc += textureLod(InputSampler, uv - stepUV, lod).rgb;
        wacc += 2.0;
    }

    return acc / wacc;
}

void main() {
    vec2 fragPx = gl_FragCoord.xy;
    
    if (scissorRect.z > 0.0) {
        if (fragPx.x < scissorRect.x || fragPx.x > scissorRect.z ||
            fragPx.y < scissorRect.y || fragPx.y > scissorRect.w) {
            discard;
        }
    }
    
    vec2 halfSize = uSize * 0.5;
    vec2 rectCenter = uLocation + halfSize;

    float alpha = 1.0 - smoothstep(
        0.0, 1.0,
        roundedBoxSDF(fragPx - rectCenter, halfSize, radius, 4.0)
    );

    if (alpha <= 1e-3) discard;

    vec2 fbCoord = fragPx / InputResolution;
    vec3 blurred = wideMipBlur(fbCoord, blurRadius);
    vec3 col = blurred * Brightness + color1.rgb;

    fragColor = vec4(col, alpha * color1.a);
}
