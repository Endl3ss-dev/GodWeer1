#version 330 core

layout(location = 0) out vec4 fragColor;

uniform sampler2D u_ScreenTexture;
uniform sampler2D u_DepthTexture;
uniform vec2 u_Resolution;
uniform mat4 u_InverseProjectionMatrix;
uniform mat4 u_InverseViewMatrix;
uniform mat4 u_PreviousProjectionMatrix;
uniform mat4 u_PreviousViewMatrix;
uniform vec3 u_CameraPos;
uniform vec3 u_PreviousCameraPos;
uniform float u_Strength;
uniform float u_TemporalScale;
uniform float u_MaxRadius;
uniform float u_EdgeFocus;
uniform float u_ChromaticPhase;
uniform float u_DepthGuard;
uniform float u_Decay;
uniform float u_Activation;
uniform float u_GlobalMotion;
uniform vec2 u_CameraVelocity;
uniform int u_Samples;

const int MAX_SAMPLES = 12;

float sat(float value) { return clamp(value, 0.0, 1.0); }
vec2 safeUv(vec2 uv) { return clamp(uv, vec2(0.0005), vec2(0.9995)); }
float lum(vec3 color) { return dot(color, vec3(0.2126, 0.7152, 0.0722)); }

vec3 worldPos(vec2 uv, float depth) {
    vec4 clip = vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    vec4 view = u_InverseProjectionMatrix * clip;
    view /= max(abs(view.w), 0.000001);
    return (u_InverseViewMatrix * view).xyz;
}

vec2 previousUv(vec3 world, vec2 fallback, out float valid) {
    vec4 view = u_PreviousViewMatrix * vec4(world - u_PreviousCameraPos, 1.0);
    vec4 clip = u_PreviousProjectionMatrix * view;
    if (abs(clip.w) <= 0.000001) { valid = 0.0; return fallback; }
    vec2 uv = (clip.xy / clip.w) * 0.5 + 0.5;
    valid = step(0.0, clip.w) * step(vec2(-0.18), uv).x * step(uv, vec2(1.18)).x;
    return uv;
}

float edgeMask(vec2 uv, float depth, vec3 center) {
    vec2 px = 1.0 / max(u_Resolution, vec2(1.0));
    vec3 l = texture(u_ScreenTexture, safeUv(uv - vec2(px.x, 0.0))).rgb;
    vec3 r = texture(u_ScreenTexture, safeUv(uv + vec2(px.x, 0.0))).rgb;
    vec3 d = texture(u_ScreenTexture, safeUv(uv - vec2(0.0, px.y))).rgb;
    vec3 u = texture(u_ScreenTexture, safeUv(uv + vec2(0.0, px.y))).rgb;
    float gradient = abs(lum(r) - lum(l)) + abs(lum(u) - lum(d));
    float contrast = abs(lum(center) - (lum(l) + lum(r) + lum(d) + lum(u)) * 0.25);
    float depthGradient = abs(texture(u_DepthTexture, safeUv(uv + vec2(px.x, 0.0))).r -
            texture(u_DepthTexture, safeUv(uv - vec2(px.x, 0.0))).r);
    float threshold = mix(0.105, 0.018, sat(u_EdgeFocus / 1.6));
    float result = gradient * 1.28 + contrast * 1.85 + depthGradient * mix(18.0, 46.0, u_DepthGuard);
    return smoothstep(threshold, threshold * 3.8 + 0.0001, result);
}

float depthWeight(vec2 uv, float centerDepth) {
    float sampleDepth = texture(u_DepthTexture, safeUv(uv)).r;
    float valid = 1.0 - step(0.999999, centerDepth);
    float reject = smoothstep(0.004, 0.052, abs(sampleDepth - centerDepth)) * valid;
    return mix(1.0, 1.0 - reject * 0.86, u_DepthGuard);
}

void main() {
    vec2 uv = gl_FragCoord.xy / max(u_Resolution, vec2(1.0));
    vec4 original = texture(u_ScreenTexture, uv);
    // The game may use a temporary projection for FOV/camera effects. Using
    // its inverse for reprojection can produce invalid coordinates and black
    // frames, so use the stable camera velocity for this temporal trail.
    vec2 centered = uv * 2.0 - 1.0;
    vec2 velocity = (u_CameraVelocity + centered * dot(u_CameraVelocity, centered) * 0.34) * u_TemporalScale;
    float speed = length(velocity * u_Resolution);
    velocity *= min(1.0, u_MaxRadius / max(speed, 0.0001));
    speed = min(speed, u_MaxRadius);

    float speedMask = smoothstep(max(0.03, u_Activation), u_Activation * 4.0 + 0.0001, speed);
    float depth = texture(u_DepthTexture, uv).r;
    float structural = edgeMask(uv, depth, original.rgb);
    float motionFill = smoothstep(0.55, 10.0, speed);
    float centerGuard = 1.0 - smoothstep(0.0, 0.46, length(centered));
    float mask = sat(speedMask * max(structural * 0.92, motionFill * mix(0.42, 0.74, 1.0 - centerGuard))
            * mix(0.90, 1.0, smoothstep(0.0005, 0.045, u_GlobalMotion)) * u_Strength);
    if (mask <= 0.001) { fragColor = original; return; }

    int samples = clamp(u_Samples, 3, MAX_SAMPLES);
    vec3 accumulated = vec3(0.0);
    vec3 maxHold = original.rgb;
    float total = 0.0;
    for (int i = 0; i < MAX_SAMPLES; i++) {
        if (i >= samples) break;
        float t = float(i) / float(max(samples - 1, 1));
        float weight = exp(-t * u_Decay) * depthWeight(safeUv(uv - velocity * t), depth);
        vec2 offset = velocity * t;
        vec3 sampleColor = vec3(
                texture(u_ScreenTexture, safeUv(uv - offset * mix(1.0, 0.76, u_ChromaticPhase))).r,
                texture(u_ScreenTexture, safeUv(uv - offset)).g,
                texture(u_ScreenTexture, safeUv(uv - offset * mix(1.0, 1.32, u_ChromaticPhase))).b);
        accumulated += sampleColor * weight;
        total += weight;
        maxHold = max(maxHold, sampleColor * (0.92 + 0.08 * (1.0 - t)));
    }
    vec3 trail = accumulated / max(total, 0.0001);
    vec3 result = mix(original.rgb, mix(trail, maxHold, 0.18 + smoothstep(0.72, 1.0, lum(original.rgb)) * 0.10), mask);
    fragColor = vec4(clamp(result, 0.0, 1.0), original.a);
}
