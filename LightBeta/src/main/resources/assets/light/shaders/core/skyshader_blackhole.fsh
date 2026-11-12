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

const vec3 BH_DIR = normalize(vec3(0.2, 0.45, -1.0));

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

float hash13(vec3 p3) {
    p3 = fract(p3 * 0.1031);
    p3 += dot(p3, p3.zyx + 31.32);
    return fract((p3.x + p3.y) * p3.z);
}

float noise2(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash12(i), hash12(i + vec2(1.0, 0.0)), u.x),
               mix(hash12(i + vec2(0.0, 1.0)), hash12(i + vec2(1.0, 1.0)), u.x), u.y);
}

float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 4; i++) {
        value += amplitude * noise2(p);
        p = p * 2.03 + vec2(11.3, 7.9);
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

vec2 rotate(vec2 p, float angle) {
    float c = cos(angle);
    float s = sin(angle);
    return vec2(c * p.x - s * p.y, s * p.x + c * p.y);
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

    vec3 ux = normalize(cross(BH_DIR, vec3(0.0, 1.0, 0.0)));
    vec3 uy = normalize(cross(ux, BH_DIR));
    float cosA = clamp(dot(direction, BH_DIR), -1.0, 1.0);
    float angleToHole = acos(cosA);
    vec3 perpendicular = direction - BH_DIR * cosA;
    float perpendicularLength = length(perpendicular);
    vec3 perpendicularNormal = perpendicularLength > 1e-5 ? perpendicular / perpendicularLength : ux;

    float rs = 0.12;
    float bend = 0.030 / max(angleToHole, 0.015);
    float lensedAngle = angleToHole - bend;
    vec3 lensedDirection = BH_DIR * cos(lensedAngle) + perpendicularNormal * sin(lensedAngle);

    vec3 result = vec3(0.006, 0.008, 0.016);
    result += vec3(0.9, 0.95, 1.0) * stars(lensedDirection, 130.0, 0.14);
    result += vec3(0.7, 0.8, 1.0) * stars(lensedDirection + 3.7, 70.0, 0.18) * 0.6;
    float nebula = fbm(lensedDirection.xy * 3.0 + lensedDirection.z * 2.0);
    result += color2 * nebula * nebula * 0.10;

    vec2 q = vec2(dot(direction, ux), dot(direction, uy));
    vec2 diskPosition = vec2(q.x, q.y * 3.4);
    float diskRadius = length(diskPosition);
    float diskMask = smoothstep(rs * 1.10, rs * 1.55, diskRadius)
                   * (1.0 - smoothstep(rs * 1.85, rs * 4.2, diskRadius));
    vec2 swirlUv = rotate(diskPosition / rs, -time * 0.6 + diskRadius * 9.0);
    float swirl = fbm(swirlUv * 3.5 - vec2(time * 0.4, 0.0));
    float doppler = 1.0 + 0.55 * (diskPosition.x / max(diskRadius, 1e-4));
    float heat = 1.0 - smoothstep(rs * 1.6, rs * 4.2, diskRadius);

    vec3 diskColor = mix(color1, color2, clamp(swirl * 1.5 - 0.1, 0.0, 1.0));
    float disk = diskMask * (0.30 + swirl) * doppler * (0.5 + 0.7 * heat);
    result += diskColor * disk * 1.6;

    float ring = exp(-pow((angleToHole - rs) * 150.0, 2.0));
    ring += 0.30 * exp(-pow((angleToHole - rs) * 45.0, 2.0));
    result += mix(vec3(1.0, 0.9, 0.7), color1, 0.35) * ring * 1.7;

    float hole = 1.0 - smoothstep(rs * 0.90, rs * 0.99, angleToHole);
    result *= 1.0 - hole;
    result = 1.0 - exp(-result * 1.25);

    fragColor = vec4(result * params.x, 1.0);
}
