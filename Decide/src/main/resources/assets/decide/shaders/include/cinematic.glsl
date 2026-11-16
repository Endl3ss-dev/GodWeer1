layout(std140) uniform CinematicConfig {
    vec4 Lift;
    vec4 Gain;
    float Saturation;
    float Contrast;
    float Exposure;
    float Vignette;
    float Bloom;
    float Threshold;
    float Radius;
    float Grain;
    float Chroma;
    float Strength;
    float Time;
};

const vec3 LUMA = vec3(0.2126, 0.7152, 0.0722);

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

vec3 grade(vec3 color) {
    color *= Exposure;
    color = color * Gain.rgb + Lift.rgb * (1.0 - color);
    color = (color - 0.5) * Contrast + 0.5;
    color = mix(vec3(dot(color, LUMA)), color, Saturation);
    return clamp(color, 0.0, 1.0);
}

vec3 scene(sampler2D source, vec2 uv) {
    vec2 shift = (uv - 0.5) * Chroma * 0.012;
    return vec3(texture(source, uv + shift).r, texture(source, uv).g, texture(source, uv - shift).b);
}

vec3 finish(vec3 color, vec2 uv, vec2 size) {
    color = mix(min(color, 1.0), grade(color), Strength);
    float edge = length((uv - 0.5) * vec2(size.x / size.y, 1.0));
    color *= 1.0 - Vignette * smoothstep(0.3, 0.95, edge);
    color += (hash(uv * size + fract(Time) * 91.7) - 0.5) * Grain;
    return clamp(color, 0.0, 1.0);
}
