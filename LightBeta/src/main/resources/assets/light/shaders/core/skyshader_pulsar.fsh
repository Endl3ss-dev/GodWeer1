#version 150

layout(std140) uniform uTime {
    float time;
};
layout(std140) uniform uResolution {
    vec2 resolution;
};
layout(std140) uniform uColor {
    vec3 tintColor;
};
layout(std140) uniform uParams {
    vec4 params;
};
layout(std140) uniform uCamera {
    vec4 cameraData;
};

out vec4 fragColor;

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

float hash(vec3 p) {
    return fract(sin(dot(p, vec3(127.1, 311.7, 74.7))) * 43758.5453123);
}

float noise3(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);

    float n000 = hash(i + vec3(0.0, 0.0, 0.0));
    float n100 = hash(i + vec3(1.0, 0.0, 0.0));
    float n010 = hash(i + vec3(0.0, 1.0, 0.0));
    float n110 = hash(i + vec3(1.0, 1.0, 0.0));
    float n001 = hash(i + vec3(0.0, 0.0, 1.0));
    float n101 = hash(i + vec3(1.0, 0.0, 1.0));
    float n011 = hash(i + vec3(0.0, 1.0, 1.0));
    float n111 = hash(i + vec3(1.0, 1.0, 1.0));

    vec4 a = mix(vec4(n000, n010, n001, n011), vec4(n100, n110, n101, n111), f.x);
    vec2 b = mix(a.xz, a.yw, f.y);
    return mix(b.x, b.y, f.z);
}

float fbm(vec3 p) {
    float v = 0.0;
    float a = 0.5;
    vec3 shift = vec3(100.0);
    for (int i = 0; i < 4; ++i) {
        v += a * noise3(p);
        p = p * 2.0 + shift;
        a *= 0.5;
    }
    return v;
}

vec3 starField(vec3 rd, float animTime) {
    vec3 gridPos = floor(rd * 130.0);
    float starHash = hash(gridPos);

    vec3 starColor = vec3(0.0);
    if (starHash > 0.992) {
        vec3 starOffset = vec3(hash(gridPos + 1.1), hash(gridPos + 2.2), hash(gridPos + 3.3)) - 0.5;
        vec3 starPos = (gridPos + 0.5 + starOffset * 0.85) / 130.0;
        float d = length(rd - normalize(starPos));
        float twinkle = 0.55 + 0.45 * sin(animTime * 3.5 + starHash * 120.0);
        float starIntensity = exp(-d * 600.0) * twinkle * (0.3 + 0.7 * hash(gridPos + 5.5));

        vec3 col = vec3(1.0);
        float colorHash = hash(gridPos + 7.7);
        if (colorHash < 0.22) {
            col = vec3(0.65, 0.8, 1.0);
        } else if (colorHash < 0.38) {
            col = vec3(1.0, 0.82, 0.6);
        } else if (colorHash < 0.43) {
            col = vec3(1.0, 0.55, 0.55);
        }

        starColor = col * starIntensity;
    }
    return starColor;
}

void main() {
    float alpha = params.x;
    float speed = params.y;
    float scale = params.z;
    float intensity = params.w;
    vec2 cameraDir = cameraData.xy;
    float fov = cameraData.z;

    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 sp = uv * 2.0 - 1.0;
    float aspect = resolution.x / resolution.y;

    float tanV = tan(radians(fov) * 0.5);
    vec3 rayV = normalize(vec3(sp.x * tanV * aspect, sp.y * tanV, 1.0));
    vec3 rayW = rotY(cameraDir.x) * rotX(cameraDir.y) * rayV;

    float t = time * speed;

    float n1 = fbm(rayW * scale * 0.4 + vec3(t * 0.003, 0.0, 0.0));
    float n2 = fbm(rayW * scale * 0.9 - vec3(0.0, t * 0.002, 0.0));

    vec3 gasColor = mix(vec3(0.001, 0.003, 0.012), vec3(0.035, 0.01, 0.055), n1);
    gasColor = mix(gasColor, vec3(0.008, 0.025, 0.035), n2 * n1);

    float dust = smoothstep(0.3, 0.7, fbm(rayW * scale * 1.2 + vec3(0.1)));
    vec3 skyBackground = mix(gasColor, vec3(0.0005, 0.0005, 0.0015), dust * 0.85);

    vec3 col = skyBackground + starField(rayW, t);

    vec3 pulsarCenter = normalize(vec3(0.55, 0.65, -0.45));
    float frontMask = smoothstep(0.0, 0.4, dot(rayW, pulsarCenter));

    if (frontMask > 0.0) {
        vec3 tangent1 = normalize(cross(pulsarCenter, vec3(0.0, 1.0, 0.0)));
        vec3 tangent2 = cross(tangent1, pulsarCenter);

        vec2 p2d = vec2(dot(rayW, tangent1), dot(rayW, tangent2));
        float dCore = length(p2d);

        float pulse = 0.95 + 0.05 * sin(t * 8.0);
        float coreSize = 0.058 * pulse;
        float core = smoothstep(coreSize, 0.0, dCore);

        float flare = fbm(rayW * 12.0 + vec3(0.0, t * 0.25, 0.0));
        float halo = exp(-dCore * 16.0) * 3.2 + exp(-dCore * 3.5) * 0.9 * (0.7 + 0.3 * flare);

        float wobbleAngle = sin(t * 6.0) * 0.22;
        vec2 jetDir2d = vec2(sin(wobbleAngle), cos(wobbleAngle));
        float distAlong = dot(p2d, jetDir2d);
        float wave = sin(abs(distAlong) * 75.0 - t * 32.0) * 0.0055 * abs(distAlong);
        float distToJet = length(p2d - distAlong * jetDir2d) - wave;

        float jetIntensity = (exp(-distToJet * 320.0) * 3.0 + exp(-distToJet * 38.0) * 0.8);
        jetIntensity *= exp(-abs(distAlong) * 0.85);

        float jetFlicker = 0.82 + 0.18 * sin(t * 115.0) * cos(t * 145.0);
        vec3 theme = max(tintColor, vec3(0.001));

        vec3 pulsarCol = vec3(1.0) * core;
        pulsarCol += vec3(0.18, 0.45, 1.0) * theme * halo;
        pulsarCol += vec3(0.35, 0.68, 1.0) * theme * jetIntensity * jetFlicker;

        col += pulsarCol * frontMask;
    }

    col *= (1.0 + intensity * 10.0);
    fragColor = vec4(col, alpha);
}
