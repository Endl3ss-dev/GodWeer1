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
layout(std140) uniform uThunder {
    vec4 thunderData;
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

float hash(float n) {
    return fract(sin(n) * 43758.5453123);
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

void main() {
    float alpha = params.x;
    float speed = params.y;
    float scale = params.z;
    float intensity = params.w;
    vec2 cameraDir = cameraData.xy;
    float fov = cameraData.z;

    float thunderInterval = max(1.0, thunderData.x);
    float thunderChance = clamp(thunderData.y, 0.0, 1.0);
    float thunderGlow = max(0.0, thunderData.z);

    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 sp = uv * 2.0 - 1.0;
    float aspect = resolution.x / resolution.y;

    float tanV = tan(radians(fov) * 0.5);
    vec3 rayV = normalize(vec3(sp.x * tanV * aspect, sp.y * tanV, 1.0));
    vec3 rayW = rotY(cameraDir.x) * rotX(cameraDir.y) * rayV;

    float cloudDensity = 0.0;
    if (rayW.y > -0.15) {
        vec3 p = rayW * scale;
        p.x += time * speed * 0.08;
        p.z += time * speed * 0.04;
        cloudDensity = fbm(p);
        cloudDensity *= smoothstep(-0.15, 0.3, rayW.y);
    }

    float timeIndex = floor(time / thunderInterval);
    float timeOffset = fract(time / thunderInterval);
    float strikeHash = hash(timeIndex * 12.34);
    bool hasStrike = strikeHash < thunderChance;

    float flash = 0.0;
    float boltGlow = 0.0;

    if (hasStrike) {
        float strikeAngle = hash(timeIndex * 45.67) * 6.28318;
        float strikeOffset = (hash(timeIndex * 8.3) - 0.5) * 0.2;
        vec3 boltStart = normalize(vec3(cos(strikeAngle), 1.0, sin(strikeAngle)));
        vec3 boltEnd = normalize(vec3(cos(strikeAngle + strikeOffset), -0.2, sin(strikeAngle + strikeOffset)));

        float t = timeOffset;
        if (t < 0.55) {
            float flicker = 0.8 + 0.2 * sin(time * 95.0) * cos(time * 135.0);
            flash = (exp(-t * 22.0) * 1.5 + exp(-abs(t - 0.15) * 25.0) * 1.0 + exp(-abs(t - 0.35) * 15.0) * 0.4) * flicker;

            float boltIntensity = max(exp(-t * 18.0), exp(-abs(t - 0.15) * 22.0));
            if (boltIntensity > 0.02) {
                vec3 segment = boltEnd - boltStart;
                float segLen = length(segment);
                if (segLen > 0.0) {
                    vec3 segDir = segment / segLen;
                    float h = clamp(dot(rayW - boltStart, segDir), 0.0, segLen);
                    vec3 projection = boltStart + segDir * h;

                    float noiseSeed = timeIndex * 73.19;
                    float jagged = sin(h * 30.0 + noiseSeed) * 0.035;
                    jagged += cos(h * 70.0 - noiseSeed) * 0.015;
                    jagged += sin(h * 150.0 + noiseSeed * 1.5) * 0.007;
                    jagged += cos(h * 350.0 - noiseSeed * 3.0) * 0.003;

                    vec3 offsetProj = projection + vec3(jagged, 0.0, jagged * 0.6);
                    float distToBolt = length(rayW - normalize(offsetProj));
                    boltGlow = (exp(-distToBolt * 280.0) * 2.5 + exp(-distToBolt * 30.0) * 0.6) * boltIntensity * flicker;

                    if (hash(timeIndex * 29.5) > 0.3 && h > segLen * 0.2) {
                        vec3 bStart = projection;
                        vec3 bEnd = normalize(boltEnd + vec3(sin(strikeAngle + 0.8), -0.15, cos(strikeAngle + 0.8)) * 0.4);
                        vec3 bSeg = bEnd - bStart;
                        float bLen = length(bSeg);
                        if (bLen > 0.0) {
                            vec3 bDir = bSeg / bLen;
                            float bh = clamp(dot(rayW - bStart, bDir), 0.0, bLen);
                            vec3 bProj = bStart + bDir * bh;
                            float bJagged = sin(bh * 45.0 + noiseSeed) * 0.015 + cos(bh * 110.0) * 0.006;
                            vec3 bOffsetProj = bProj + vec3(bJagged, 0.0, bJagged);
                            float distToBranch = length(rayW - normalize(bOffsetProj));
                            boltGlow += (exp(-distToBranch * 220.0) * 1.2 + exp(-distToBranch * 30.0) * 0.3) * boltIntensity * (1.0 - bh / bLen) * flicker;
                        }
                    }

                    if (hash(timeIndex * 83.2) > 0.5 && h > segLen * 0.4) {
                        vec3 bStart = projection;
                        vec3 bEnd = normalize(boltEnd + vec3(sin(strikeAngle - 0.8), -0.2, cos(strikeAngle - 0.8)) * 0.45);
                        vec3 bSeg = bEnd - bStart;
                        float bLen = length(bSeg);
                        if (bLen > 0.0) {
                            vec3 bDir = bSeg / bLen;
                            float bh = clamp(dot(rayW - bStart, bDir), 0.0, bLen);
                            vec3 bProj = bStart + bDir * bh;
                            float bJagged = sin(bh * 50.0 - noiseSeed) * 0.015 + cos(bh * 120.0) * 0.006;
                            vec3 bOffsetProj = bProj + vec3(bJagged, 0.0, bJagged);
                            float distToBranch = length(rayW - normalize(bOffsetProj));
                            boltGlow += (exp(-distToBranch * 220.0) * 1.2 + exp(-distToBranch * 30.0) * 0.3) * boltIntensity * (1.0 - bh / bLen) * flicker;
                        }
                    }
                }
            }
        }
    }

    vec3 stormBase = mix(vec3(0.005, 0.005, 0.015), tintColor * 0.05, 0.5);
    vec3 flashColor = vec3(0.75, 0.82, 1.0) * flash * thunderGlow;
    vec3 finalSky = stormBase + flashColor * 0.25;

    vec3 cloudBaseColor = mix(vec3(0.01, 0.01, 0.02), vec3(0.06, 0.07, 0.12), cloudDensity);
    vec3 cloudLitColor = mix(vec3(0.02, 0.025, 0.05), vec3(0.8, 0.85, 1.0), cloudDensity * flash * thunderGlow * 0.7);
    vec3 finalCloud = mix(cloudBaseColor, cloudLitColor, min(flash * thunderGlow, 1.0));

    float cloudMix = smoothstep(0.18, 0.48, cloudDensity);
    vec3 finalColor = mix(finalSky, finalCloud, cloudMix);
    finalColor += vec3(0.85, 0.92, 1.0) * boltGlow * thunderGlow;
    finalColor *= (1.0 + intensity * 10.0);

    fragColor = vec4(finalColor, alpha);
}
