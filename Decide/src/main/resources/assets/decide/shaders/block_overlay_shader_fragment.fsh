#version 330

layout(std140) uniform Globals {
    ivec3 CameraBlockPos;
    vec3 CameraOffset;
    vec2 ScreenSize;
    float GlintAlpha;
    float GameTime;
    int MenuBlurRadius;
    int UseRgss;
};

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

// Период "часов" шейдера. GameTime = (worldTime % 24000 + tickDelta) / 24000,
// то есть раз в игровые сутки (20 мин реального времени) time прыгает 2400 -> 0,
// и паттерн телепортируется. Гасим оверлей в узком окне вокруг перелома:
// анимация проходит через него бесшовно (паттерн замирает, гаснет, проявляется
// уже новый — и продолжает ползти дальше). 8 единиц = ~4 секунды в каждую сторону.
const float DAY_PERIOD = 2400.0;
const float DAY_WRAP_FADE = 8.0;

float dayWrapFade(float time) {
    return min(smoothstep(0.0, DAY_WRAP_FADE, time),
               smoothstep(DAY_PERIOD, DAY_PERIOD - DAY_WRAP_FADE, time));
}

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 345.45));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),
        mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x),
        f.y
    );
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 5; i++) {
        v += noise(p) * a;
        p = p * 2.02 + vec2(8.4, 5.7);
        a *= 0.5;
    }
    return v;
}

float ridged(vec2 p) {
    float v = 0.0;
    float a = 0.55;
    for (int i = 0; i < 4; i++) {
        float r = 1.0 - abs(noise(p) * 2.0 - 1.0);
        v += r * a;
        p = p * 2.18 + vec2(3.1, 9.2);
        a *= 0.52;
    }
    return v;
}

float edgeGlow(vec2 uv) {
    vec2 edge = min(uv, 1.0 - uv);
    float nearest = min(edge.x, edge.y);
    return 1.0 - smoothstep(0.0, 0.18, nearest);
}

float bwColormapRed(float x) {
    if (x < 0.0) return 54.0 / 255.0;
    if (x < 20049.0 / 82979.0) return (829.79 * x + 54.51) / 255.0;
    return 1.0;
}

float bwColormapGreen(float x) {
    if (x < 20049.0 / 82979.0) return 0.0;
    if (x < 327013.0 / 810990.0) return (8546482679670.0 / 10875673217.0 * x - 2064961390770.0 / 10875673217.0) / 255.0;
    if (x <= 1.0) return (103806720.0 / 483977.0 * x + 19607415.0 / 483977.0) / 255.0;
    return 1.0;
}

float bwColormapBlue(float x) {
    if (x < 0.0) return 54.0 / 255.0;
    if (x < 7249.0 / 82979.0) return (829.79 * x + 54.51) / 255.0;
    if (x < 20049.0 / 82979.0) return 127.0 / 255.0;
    if (x < 327013.0 / 810990.0) return (792.0224934136139 * x - 64.36479073560233) / 255.0;
    return 1.0;
}

vec3 bwColormap(float x) {
    return vec3(bwColormapRed(x), bwColormapGreen(x), bwColormapBlue(x));
}

float bwRand(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float bwNoise(vec2 p) {
    vec2 ip = floor(p);
    vec2 u = fract(p);
    u = u * u * (3.0 - 2.0 * u);
    float res = mix(
        mix(bwRand(ip), bwRand(ip + vec2(1.0, 0.0)), u.x),
        mix(bwRand(ip + vec2(0.0, 1.0)), bwRand(ip + vec2(1.0, 1.0)), u.x),
        u.y
    );
    return res * res;
}

const mat2 BW_MTX = mat2(0.80, 0.60, -0.60, 0.80);

float bwFbm(vec2 p, float t) {
    float f = 0.0;
    f += 0.500000 * bwNoise(p + vec2(t)); p = BW_MTX * p * 2.02;
    f += 0.031250 * bwNoise(p); p = BW_MTX * p * 2.01;
    f += 0.250000 * bwNoise(p); p = BW_MTX * p * 2.03;
    f += 0.125000 * bwNoise(p); p = BW_MTX * p * 2.01;
    f += 0.062500 * bwNoise(p); p = BW_MTX * p * 2.04;
    f += 0.015625 * bwNoise(p + vec2(sin(t)));
    return f / 0.96875;
}

float bwPattern(vec2 p, float t) {
    return bwFbm(p + vec2(bwFbm(p + vec2(bwFbm(p, t)), t)), t);
}

// ================= Стили из Destra "BlockOverlay" =================
// Исходники: block_cosmos / block_gyroid / block_fenel / block_clouds / block_nebula.
// Общий tint берётся из vertexColor (в Destra это baseColor * vertexColor).

// ---- Космос (block_cosmos) ----
vec3 csErot(vec3 p, vec3 ax, float ro) {
    return mix(dot(p, ax) * ax, p, cos(ro)) + sin(ro) * cross(ax, p);
}

float csWavelet(vec3 p, float z, float k) {
    float d = 0.0;
    float s = 1.0;
    float m = 0.0;
    for (int i = 0; i < 2; i++) {
        vec3 q = p * s;
        vec3 g = fract(floor(q) * vec3(123.34, 233.53, 314.15));
        g += dot(g, g + 23.234);
        float a = fract(g.x * g.y) * 1e3 + z * (mod(g.x + g.y, 2.0) - 1.0);
        q = fract(q) - 0.5;
        q = csErot(q, normalize(tan(g + 0.1)), a);
        d += sin(q.x * 10.0 + z) * smoothstep(0.25, 0.0, dot(q, q)) / s;
        p = csErot(p, normalize(vec3(-1.0, 1.0, 0.0)), atan(sqrt(2.0))) + float(i);
        m += 1.0 / s;
        s *= k;
    }
    return d / m;
}

float csHash13(vec3 p) {
    p = fract(p * 0.1031);
    p += dot(p, p.yzx + 33.33);
    return fract((p.x + p.y) * p.z);
}

float csStarLayer(vec3 dir, float scale, float threshold) {
    vec3 p = dir * scale;
    vec3 cell = floor(p);
    vec3 local = fract(p) - 0.5;
    float seed = csHash13(cell);
    float radius = mix(0.08, 0.20, seed);
    float star = smoothstep(radius, 0.0, length(local));
    star *= smoothstep(threshold, 1.0, seed);
    return star * mix(0.4, 1.0, smoothstep(threshold, 1.0, seed));
}

// ---- Гироид (block_gyroid) ----
float gyField(vec3 p) {
    return dot(cos(p), sin(p.yzx));
}

float gyFbm(vec3 p, float localTime) {
    float result = 0.0;
    float a = 0.5;
    for (int i = 0; i < 5; ++i) {
        p += result * 0.08;
        p.z += localTime * 0.12;
        result += abs(gyField(p / a) * a);
        a /= 1.75;
    }
    return result;
}

// ---- Облака (block_clouds) ----
const mat2 CLOUD_M = mat2(1.6, 1.2, -1.2, 1.6);

float cloudFbm(vec2 n, float localTime) {
    float total = 0.0;
    float amplitude = 0.1;
    for (int i = 0; i < 7; i++) {
        total += bwNoise(n) * amplitude;
        n = CLOUD_M * n;
        n += vec2(localTime * 0.02, localTime * 0.015);
        amplitude *= 0.4;
    }
    return total;
}

// ---- Небула (block_nebula) ----
float nebRandom(vec2 pos) {
    return fract(sin(dot(pos.xy, vec2(13.9898, 78.233))) * 43758.5453123);
}

float nebNoise(vec2 pos) {
    vec2 i = floor(pos);
    vec2 f = fract(pos);
    float a = nebRandom(i + vec2(0.0, 0.0));
    float b = nebRandom(i + vec2(1.0, 0.0));
    float c = nebRandom(i + vec2(0.0, 1.0));
    float d = nebRandom(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

float nebFbm(vec2 pos, float t) {
    float v = 0.0;
    float a = 0.5;
    vec2 shift = vec2(100.0);
    mat2 rot = mat2(cos(0.5), sin(0.5), -sin(0.5), cos(0.5));

    for (int i = 0; i < 5; i++) {
        float dir = mod(float(i), 2.0) > 0.5 ? 1.0 : -1.0;
        v += a * nebNoise(pos + vec2(dir * t * 0.3));
        pos = rot * pos * 2.0 + shift;
        a *= 0.5;
    }

    return v;
}

void main() {
    int mode = int(floor(texCoord0.y * 0.5 + 0.0001));
    vec2 uv = vec2(texCoord0.x, texCoord0.y - float(mode) * 2.0);
    vec4 tint = vertexColor;
    float time = GameTime * 2400.0;
    // В Destra BlockOverlay шейдеры получали time = секунды * 0.65.
    // Здесь GameTime — доля суток (0..1), умноженная на 2400, т.е. 2.0 единицы в секунду.
    // 0.325 = 0.65 / 2.0 — приводим скорость анимации к исходной.
    float destraTime = time * 0.325;
    float glow = edgeGlow(uv);
    vec2 patternUv = uv + vec2(time * 0.035, -time * 0.025);
    vec3 color = tint.rgb;
    float alpha = tint.a;

    if (mode == 0) {
        float pulse = 0.72 + 0.28 * sin(time * 1.5 + (patternUv.x + patternUv.y) * 8.0);
        float mist = fbm(patternUv * 3.0 + vec2(time * 0.12, -time * 0.08));
        color = mix(color * 0.55, mix(color, vec3(1.0), 0.35), mist * pulse + glow * 0.45);
        alpha *= 0.55 + mist * 0.3 + glow * 0.25;
    } else if (mode == 2) {
        vec2 flow = patternUv * 2.5;
        vec2 drift = vec2(time * 0.20, -time * 0.15);
        vec2 warp = vec2(fbm(flow * 0.90 + drift * 0.75 + vec2(0.0, 4.1)), fbm(flow * 0.78 - drift * 0.48 + vec2(3.7, 1.8)));
        vec2 q = flow + (warp - 0.5) * 1.8;
        float mist = fbm(q * 0.72 - drift * 0.24 + vec2(4.2, 8.1));
        float veins = pow(clamp(ridged(q * 1.85 + vec2(mist * 2.5, mist * 1.6) - drift * 0.55), 0.0, 1.0), 2.4);
        float strands = pow(clamp(1.0 - abs(sin((q.x * 1.08 + q.y * 0.42) * 1.7 + time * 0.85 + mist * 4.3)), 0.0, 1.0), 4.8);
        float energy = clamp(mist * 0.22 + veins * 0.88 + strands * 0.55, 0.0, 1.0);
        color = mix(color * 0.5, mix(color, vec3(1.0), 0.45), energy);
        alpha *= 0.35 + energy * 0.65;
    } else if (mode == 4) {
        float p1 = sin(patternUv.x * 8.0 + time * 2.0);
        float p2 = sin(patternUv.y * 6.0 + time * 1.5);
        float p3 = sin((patternUv.x + patternUv.y) * 5.0 + time * 1.8);
        float n = (p1 + p2 + p3) * 0.16 + 0.5;
        color = mix(color, mix(color, vec3(1.0), 0.7), n);
        alpha *= 0.45 + n * 0.55;
    } else if (mode == 8) {
        float shade = bwPattern(patternUv * 2.15, time * 0.18);
        color = bwColormap(shade);
        alpha *= clamp(0.35 + shade * 0.85, 0.0, 1.0);
    } else if (mode == 1) {
        // ---- Космос (Destra block_cosmos) ----
        vec2 suv = (uv * 2.0 - 1.0) * 1.65;
        vec3 dir = normalize(vec3(suv * 0.8, 1.0));
        float localTime = destraTime * 0.032;

        float a1 = 0.86;
        float a2 = 1.08;
        mat2 rot1 = mat2(cos(a1), sin(a1), -sin(a1), cos(a1));
        mat2 rot2 = mat2(cos(a2), sin(a2), -sin(a2), cos(a2));
        dir.xz *= rot1;
        dir.xy *= rot2;

        vec3 from = vec3(1.0, 0.5, 0.75);
        from += vec3(localTime * 1.8, localTime, -5.0);
        from.xz *= rot1;
        from.xy *= rot2;

        float s = 0.1;
        float fade = 1.0;
        vec3 vol = vec3(0.0);
        for (int r = 0; r < 10; r++) {
            vec3 p = from + s * dir * 0.5;
            float a = csWavelet(p * 2.9, 0.0, 1.9) * 2.0 - 1.0;
            a *= a * a;
            vol += vec3(s, s * s, s * s * s * s) * a * -8.8 * fade;
            fade *= 0.91;
            s += 0.032 * 1.55;
        }

        float stars = csStarLayer(from + dir * 1.05, 34.0, 0.965);
        stars = max(stars, csStarLayer(from + dir * 1.4, 58.0, 0.982));

        vol = mix(vec3(length(vol)), vol, 0.95);
        vol = clamp(vol.gbr * 0.01, vec3(0.0), vec3(1.0));
        vol += vol * stars;

        float intensity = clamp(max(max(vol.r, vol.g), vol.b) * 1.75, 0.0, 1.0);

        vec3 palA = clamp(tint.rgb * 1.22, 0.0, 1.0);
        vec3 palB = mix(tint.rgb, vec3(1.0), 0.35);
        vec3 palC = clamp(vec3(tint.rgb.r * 0.55 + 0.12, tint.rgb.g * 0.30 + 0.05, tint.rgb.b * 1.35 + 0.18), 0.0, 1.0);
        vec3 palD = clamp(tint.rgb * 0.24, 0.0, 1.0);

        vec3 eff = mix(palD, palA, clamp(vol.b * 2.2 + vol.g, 0.0, 1.0));
        eff = mix(eff, palB, clamp(vol.r * 2.4, 0.0, 1.0));
        eff = mix(eff, palC, clamp(stars * 0.55 + vol.g, 0.0, 1.0));
        eff += palD * (stars * 0.18);
        eff = clamp(eff * intensity + eff * 0.35, 0.0, 1.8);

        color = eff;
        alpha *= 0.22 + intensity * 0.45 + stars * 0.12;
    } else if (mode == 3) {
        // ---- Гироид (Destra block_gyroid) ----
        vec2 guv = (uv * 2.0 - 1.0) * 1.9;
        vec3 ray = normalize(vec3(guv, 0.35));
        vec3 samplePos = ray * 2.8;
        float localTime = destraTime * 0.9;
        float field = gyFbm(samplePos, localTime);
        float detail = gyFbm(samplePos + vec3(1.3, -0.8, 0.6), localTime * 0.82);
        float mask = smoothstep(0.18, 0.92, field);
        float veins = smoothstep(0.18, 0.86, abs(sin(field * 3.1 + detail * 1.6 - localTime * 0.55)));

        vec3 cLow = tint.rgb * 0.18;
        vec3 cMid = clamp(tint.rgb * 1.02 + vec3(0.05), 0.0, 1.0);
        vec3 cHigh = mix(tint.rgb, vec3(1.0), 0.20);
        vec3 cAccent = clamp(vec3(tint.rgb.r * 0.55 + 0.08, tint.rgb.g * 0.34 + 0.06, tint.rgb.b * 1.10 + 0.10), 0.0, 1.0);

        vec3 eff = mix(cLow, cMid, mask);
        eff = mix(eff, cHigh, veins * 0.42);
        eff = mix(eff, cAccent, smoothstep(0.45, 1.0, detail) * 0.32);
        eff += cMid * (detail * 0.05);

        color = clamp(eff, 0.0, 1.8);
        alpha *= 0.20 + mask * 0.34 + veins * 0.12;
    } else if (mode == 5) {
        // ---- Фенель (Destra block_fenel) ----
        vec2 fuv = (uv * 2.0 - 1.0) * 1.35;
        float shade = bwPattern(fuv * 1.25, destraTime * 0.9);
        vec3 heat = bwColormap(shade);

        vec3 c1 = tint.rgb * 0.16;
        vec3 c2 = clamp(tint.rgb * 1.08, 0.0, 1.0);
        vec3 c3 = mix(tint.rgb, vec3(1.0), 0.18);
        vec3 cAccent = clamp(vec3(tint.rgb.r * 0.62 + 0.06, tint.rgb.g * 0.42 + 0.05, tint.rgb.b * 1.02 + 0.08), 0.0, 1.0);

        vec3 eff = mix(c1, c2, heat.r * 0.92);
        eff = mix(eff, c3, heat.g * 0.32);
        eff = mix(eff, cAccent, heat.b * 0.22);
        eff += tint.rgb * (shade * 0.10);

        color = clamp(eff, 0.0, 1.8);
        alpha *= 0.18 + shade * 0.48;
    } else if (mode == 6) {
        // ---- Облака (Destra block_clouds) ----
        vec2 p = uv;
        vec2 cuv = (p * 2.0 - 1.0) * vec2(1.25, 1.0);
        float localTime = destraTime * 0.03;
        float q = cloudFbm(cuv * 0.55, localTime);

        float r = 0.0;
        vec2 uvRidged = cuv * 1.1;
        uvRidged -= q - localTime;
        float weight = 0.8;
        for (int i = 0; i < 4; i++) {
            r += abs(weight * bwNoise(uvRidged));
            uvRidged = CLOUD_M * uvRidged + localTime;
            weight *= 0.7;
        }

        float f = 0.0;
        vec2 uvShape = cuv * 1.1;
        uvShape -= q - localTime;
        weight = 0.7;
        for (int i = 0; i < 6; i++) {
            f += weight * bwNoise(uvShape);
            uvShape = CLOUD_M * uvShape + localTime;
            weight *= 0.6;
        }
        f *= r + f;

        float c = 0.0;
        float colourTime = destraTime * 0.06;
        vec2 uvColour = cuv * 2.2;
        uvColour -= q - colourTime;
        weight = 0.4;
        for (int i = 0; i < 4; i++) {
            c += weight * bwNoise(uvColour);
            uvColour = CLOUD_M * uvColour + colourTime;
            weight *= 0.6;
        }

        float c1c = 0.0;
        float ridgeTime = destraTime * 0.09;
        vec2 uvRidgeColour = cuv * 3.3;
        uvRidgeColour -= q - ridgeTime;
        weight = 0.4;
        for (int i = 0; i < 2; i++) {
            c1c += abs(weight * bwNoise(uvRidgeColour));
            uvRidgeColour = CLOUD_M * uvRidgeColour + ridgeTime;
            weight *= 0.6;
        }
        c += c1c;

        vec3 cLow = mix(tint.rgb * 0.22, tint.rgb * 0.62 + vec3(0.06, 0.08, 0.12), 0.6);
        vec3 cHigh = mix(tint.rgb * 1.05, mix(tint.rgb, vec3(1.0), 0.24), 0.5);
        vec3 skyColour = mix(cLow, cHigh, p.y);

        vec3 cloudBase = mix(tint.rgb, vec3(1.0), 0.42);
        vec3 cloudColour = cloudBase * clamp(0.5 + 0.3 * c, 0.0, 1.0);

        f = 0.2 + 8.0 * f * r;
        vec3 tintedSky = clamp(0.5 * skyColour + cloudColour, 0.0, 1.0);
        vec3 result = mix(skyColour, tintedSky, clamp(f + c, 0.0, 1.0));

        color = clamp(result, 0.0, 1.8);
        alpha *= 0.18 + clamp(f + c, 0.0, 1.0) * 0.45;
    } else if (mode == 7) {
        // ---- Небула (Destra block_nebula) ----
        vec2 nuv = (uv * 2.0 - 1.0) * 3.0;
        float t = destraTime;

        vec2 q = vec2(nebFbm(nuv, t * 0.8), nebFbm(nuv + vec2(1.0, 1.0), t * 0.6));
        vec2 r = vec2(nebFbm(nuv + q + vec2(1.7, 1.2), t * 0.7),
                      nebFbm(nuv + q + vec2(8.3, 2.8), t * 0.9));
        float f = nebFbm(nuv + r, t);

        vec3 col1 = tint.rgb * 1.4;
        vec3 col2 = tint.rgb * 0.5;
        vec3 col3 = vec3(tint.rgb.r * 0.4, tint.rgb.g * 0.3, tint.rgb.b * 1.4);

        vec3 eff = mix(col1, col2, clamp((f * f) * 4.0, 0.0, 1.0));
        eff = mix(eff, col1, clamp(length(q), 0.0, 1.0));
        eff = mix(eff, col3, clamp(length(r.x), 0.0, 1.0));

        eff = (f * f * f + 0.6 * f * f + 0.5 * f) * eff;

        color = clamp(eff * 1.8, 0.0, 1.0);
        alpha *= 0.15 + f * 0.25;
    }

    if (alpha <= 0.001) {
        discard;
    }

    alpha *= dayWrapFade(time);

    if (alpha <= 0.001) {
        discard;
    }

    fragColor = vec4(color, clamp(alpha, 0.0, 1.0));
}
