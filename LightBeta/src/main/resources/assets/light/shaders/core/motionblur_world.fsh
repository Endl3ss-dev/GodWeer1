#version 150

uniform sampler2D u_Current;
uniform sampler2D u_History;

layout(std140) uniform u_Resolution {
    vec2 resolution;
};
layout(std140) uniform u_Motion {
    vec2 velocity;
    float intensity;
    float padding;
};

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution;
    vec4 current = texture(u_Current, uv);
    if (intensity <= 0.0001 || dot(velocity, velocity) <= 0.00000001) {
        fragColor = current;
        return;
    }

    // Sample the previous world along the camera-motion direction.  The
    // current frame remains the base, so the image is sharp when stationary.
    const int SAMPLES = 10;
    vec4 trail = vec4(0.0);
    float weight = 0.0;
    for (int i = 0; i < SAMPLES; i++) {
        float t = (float(i) + 0.5) / float(SAMPLES);
        float w = 1.0 - t * 0.55;
        trail += texture(u_History, clamp(uv - velocity * t, vec2(0.0), vec2(1.0))) * w;
        weight += w;
    }
    trail /= max(weight, 0.0001);
    fragColor = mix(current, trail, intensity);
}
