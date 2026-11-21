#version 150

layout(std140) uniform uTime {
    float time;
};
layout(std140) uniform uResolution {
    vec2 resolution;
};
layout(std140) uniform uColor {
    vec3 color1;
};
layout(std140) uniform uColor2 {
    vec3 color2;
};
layout(std140) uniform uParams {
    vec4 params;
};
layout(std140) uniform uCamera {
    vec4 cameraData;
};

out vec4 fragColor;

float hash13(vec3 p3) {
    p3 = fract(p3 * 0.1031);
    p3 += dot(p3, p3.zyx + 31.32);
    return fract((p3.x + p3.y) * p3.z);
}

float noise3(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    vec3 u = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(mix(hash13(i), hash13(i + vec3(1.0, 0.0, 0.0)), u.x),
            mix(hash13(i + vec3(0.0, 1.0, 0.0)), hash13(i + vec3(1.0, 1.0, 0.0)), u.x), u.y),
        mix(mix(hash13(i + vec3(0.0, 0.0, 1.0)), hash13(i + vec3(1.0, 0.0, 1.0)), u.x),
            mix(hash13(i + vec3(0.0, 1.0, 1.0)), hash13(i + vec3(1.0, 1.0, 1.0)), u.x), u.y),
        u.z);
}

float fbm3(vec3 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 4; i++) {
        value += amplitude * noise3(p);
        p = p * 2.04 + vec3(9.1, 17.3, 5.7);
        amplitude *= 0.5;
    }
    return value;
}

float stars(vec3 direction, float density, float size) {
    vec3 p = direction * density;
    vec3 id = floor(p);
    float h = hash13(id);
    if (h < 0.92) return 0.0;
    vec3 offset = fract(h * vec3(511.7, 269.5, 183.3)) - 0.5;
    float distanceToStar = length(fract(p) - 0.5 - offset * 0.6);
    return smoothstep(size, 0.0, distanceToStar) * (0.4 + 0.6 * h);
}

mat3 rotX(float a) {
    float c = cos(a), s = sin(a);
    return mat3(1.0, 0.0, 0.0,
                0.0,   c,   s,
                0.0,  -s,   c);
}

mat3 rotY(float a) {
    float c = cos(a), s = sin(a);
    return mat3(  c, 0.0,   s,
                0.0, 1.0, 0.0,
                 -s, 0.0,   c);
}

vec3 cameraRay(vec2 uv) {
    float fov = radians(cameraData.z);
    float aspect = resolution.x / max(resolution.y, 1.0);
    vec3 rayV = normalize(vec3(uv.x * tan(fov * 0.5) * aspect, uv.y * tan(fov * 0.5), 1.0));
    return rotY(cameraData.x) * rotX(cameraData.y) * rayV;
}

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy * 2.0 - 1.0;
    vec3 direction = cameraRay(uv);

    float rotation = time * 0.008;
    float c = cos(rotation);
    float s = sin(rotation);
    direction = vec3(c * direction.x + s * direction.z, direction.y,
                     -s * direction.x + c * direction.z);

    vec3 result = vec3(0.006, 0.008, 0.018);
    float twinkle = 0.75 + 0.25 * sin(time * 2.0 + hash13(floor(direction * 150.0)) * 40.0);
    result += vec3(1.0, 0.97, 0.92) * stars(direction, 150.0, 0.13) * twinkle;
    result += vec3(0.8, 0.88, 1.0) * stars(direction + 4.2, 90.0, 0.16) * 0.7;
    result += vec3(1.0, 0.8, 0.7) * stars(direction + 8.9, 55.0, 0.20) * 0.5;

    vec3 bandNormal = normalize(vec3(0.35, 0.85, 0.25));
    float bandDistance = dot(direction, bandNormal);
    float band = exp(-bandDistance * bandDistance * 9.0);
    float warp = noise3(direction * 7.0 + time * 0.01);
    float nebula = fbm3(direction * 4.0 + warp * 1.5);
    float dust = fbm3(direction * 9.0 + vec3(31.7));

    vec3 nebulaColor = mix(color1, color2, clamp(nebula * 1.6 - 0.25, 0.0, 1.0));
    result += nebulaColor * band * nebula * nebula * 2.6;

    vec3 coreDirection = normalize(cross(bandNormal, vec3(1.0, 0.0, 0.3)));
    float core = pow(clamp(dot(direction, coreDirection), 0.0, 1.0), 6.0);
    result += mix(vec3(1.0, 0.9, 0.75), color2, 0.3) * core * band * 0.9;
    result -= result * band * smoothstep(0.52, 0.78, dust) * 0.55;
    result += vec3(0.95, 0.92, 0.85) * stars(direction + 13.4, 210.0, 0.15) * band * 1.2;

    result = max(result, vec3(0.0));
    fragColor = vec4(result * params.x, 1.0);
}
