#version 150

uniform sampler2D u_SceneTexture;
uniform sampler2D u_DepthTexture;

layout(std140) uniform WetMatrices {
    mat4 u_ViewMatrix;
    mat4 u_ProjectionMatrix;
    mat4 u_InverseProjectionMatrix;
    mat4 u_InverseViewMatrix;
};

layout(std140) uniform WetParams {
    vec2 u_Resolution;
    float u_Time;
    float u_Wetness;
    float u_PuddleCoverage;
    float u_ReflectionStrength;
    float u_MaxDistance;
    float u_RippleStrength;
    float u_RainAmount;
    int u_ReflectionSteps;
    int u_Ripples;
};

out vec4 fragColor;

const int MAX_REFLECTION_STEPS = 16;

vec2 vUv;

float saturate(float value) {
    return clamp(value, 0.0, 1.0);
}

vec2 safeUv(vec2 uv) {
    return clamp(uv, vec2(0.001), vec2(0.999));
}

float luminance(vec3 color) {
    return dot(color, vec3(0.2126, 0.7152, 0.0722));
}

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float valueNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float puddleNoise(vec2 p) {
    float value = valueNoise(p) * 0.56;
    value += valueNoise(p * 2.03 + vec2(17.7, 4.3)) * 0.29;
    value += valueNoise(p * 4.11 + vec2(-8.1, 13.2)) * 0.15;
    return value;
}

vec3 reconstructViewPosition(vec2 uv, float depth) {
    vec4 clip = vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    vec4 view = u_InverseProjectionMatrix * clip;
    float invW = abs(view.w) > 0.000001 ? 1.0 / view.w : 1.0;
    return view.xyz * invW;
}

vec3 reconstructWorldPosition(vec3 viewPosition) {
    return (u_InverseViewMatrix * vec4(viewPosition, 1.0)).xyz;
}

vec3 cameraWorldPosition() {
    return u_InverseViewMatrix[3].xyz;
}

// Мировая позиция пикселя. valid = 0, если там небо (глубина максимальна).
vec3 worldPositionAt(vec2 uv, out float valid) {
    float depth = texture(u_DepthTexture, safeUv(uv)).r;
    valid = depth < 0.999995 ? 1.0 : 0.0;
    return reconstructWorldPosition(reconstructViewPosition(uv, min(depth, 0.999999)));
}

// Наклон отрезка поверхности: перепад высоты на единицу горизонтального шага.
// У горизонтальной грани ≈ 0, у стены — очень много.
float slopeOf(vec3 delta) {
    float horizontal = length(delta.xz);
    if (horizontal <= 0.000001) return 64.0;
    return abs(delta.y) / horizontal;
}

// Принадлежит ли пиксель верхней грани блока.
// Нормаль через cross() здесь не используется: её знак приходилось
// доопределять сравнением с направлением на камеру, и у поверхностей около
// уровня глаз он переворачивался — из-за этого при взгляде вверх эффект
// ложился на весь экран. Вместо этого проверяем сами мировые координаты:
// у горизонтальной грани высота в окрестности постоянна.
float topFaceMask(vec2 uv, vec3 centerWorld) {
    vec2 texel = 1.0 / max(u_Resolution, vec2(1.0));

    float validL, validR, validD, validU;
    vec3 posL = worldPositionAt(uv - vec2(texel.x, 0.0), validL);
    vec3 posR = worldPositionAt(uv + vec2(texel.x, 0.0), validR);
    vec3 posD = worldPositionAt(uv - vec2(0.0, texel.y), validD);
    vec3 posU = worldPositionAt(uv + vec2(0.0, texel.y), validU);
    if (validL * validR * validD * validU < 0.5) return 0.0;

    // Из пары соседей берём ближайшего: на силуэте дальний сосед лежит
    // на другой геометрии и дал бы ложный наклон.
    vec3 rightA = posR - centerWorld;
    vec3 rightB = centerWorld - posL;
    vec3 upA = posU - centerWorld;
    vec3 upB = centerWorld - posD;
    vec3 deltaX = dot(rightA, rightA) < dot(rightB, rightB) ? rightA : rightB;
    vec3 deltaZ = dot(upA, upA) < dot(upB, upB) ? upA : upB;

    float slope = max(slopeOf(deltaX), slopeOf(deltaZ));
    return 1.0 - smoothstep(0.14, 0.42, slope);
}

vec2 projectPosition(vec3 position, out float valid) {
    vec4 clip = u_ProjectionMatrix * vec4(position, 1.0);
    if (clip.w <= 0.00001) {
        valid = 0.0;
        return vUv;
    }
    vec2 uv = clip.xy / clip.w * 0.5 + 0.5;
    vec2 inside = step(vec2(0.002), uv) * step(uv, vec2(0.998));
    valid = inside.x * inside.y;
    return safeUv(uv);
}

vec2 traceReflection(vec3 origin, vec3 rayDirection, vec3 surfaceNormal, out float hitMask) {
    vec3 rayPosition = origin + surfaceNormal * max(0.035, length(origin) * 0.0015);
    float stepLength = max(0.12, length(origin) * 0.014);
    float traveled = 0.0;
    vec2 lastUv = vUv;
    hitMask = 0.0;

    for (int i = 0; i < MAX_REFLECTION_STEPS; i++) {
        if (i >= u_ReflectionSteps) break;

        rayPosition += rayDirection * stepLength;
        traveled += stepLength;
        if (traveled > min(30.0, u_MaxDistance * 0.72) || rayPosition.z > -0.025) break;

        float projected = 0.0;
        vec2 rayUv = projectPosition(rayPosition, projected);
        if (projected < 0.5) break;
        lastUv = rayUv;

        float sceneDepth = texture(u_DepthTexture, rayUv).r;
        if (sceneDepth < 0.999995) {
            vec3 scenePosition = reconstructViewPosition(rayUv, sceneDepth);
            float depthDelta = scenePosition.z - rayPosition.z;
            float heightAboveSurface = dot(scenePosition - origin, surfaceNormal);
            float thickness = 0.07 + stepLength * 1.35;

            if (i > 1 && heightAboveSurface > 0.055
                    && depthDelta > 0.0 && depthDelta < thickness) {
                float edge = min(min(rayUv.x, rayUv.y), min(1.0 - rayUv.x, 1.0 - rayUv.y));
                hitMask = smoothstep(0.0, 0.055, edge)
                        * (1.0 - smoothstep(0.0, thickness, depthDelta) * 0.34);
                return rayUv;
            }
        }

        stepLength *= 1.235;
    }

    return lastUv;
}

vec3 blurredReflection(vec2 uv, float blurPixels) {
    vec2 texel = 1.0 / max(u_Resolution, vec2(1.0));
    vec2 horizontal = vec2(texel.x * blurPixels, 0.0);
    vec2 vertical = vec2(0.0, texel.y * blurPixels * 1.85);

    vec3 color = texture(u_SceneTexture, safeUv(uv)).rgb * 0.34;
    color += texture(u_SceneTexture, safeUv(uv + horizontal)).rgb * 0.12;
    color += texture(u_SceneTexture, safeUv(uv - horizontal)).rgb * 0.12;
    color += texture(u_SceneTexture, safeUv(uv + vertical)).rgb * 0.16;
    color += texture(u_SceneTexture, safeUv(uv - vertical)).rgb * 0.16;
    color += texture(u_SceneTexture, safeUv(uv + vertical * 2.15)).rgb * 0.05;
    color += texture(u_SceneTexture, safeUv(uv - vertical * 2.15)).rgb * 0.05;
    return color;
}

void main() {
    vUv = gl_FragCoord.xy / max(u_Resolution, vec2(1.0));

    vec4 original = texture(u_SceneTexture, vUv);
    float depth = texture(u_DepthTexture, vUv).r;
    if (depth >= 0.999995) {
        fragColor = original;
        return;
    }

    vec3 viewPosition = reconstructViewPosition(vUv, depth);
    float distanceToCamera = length(viewPosition);
    if (distanceToCamera > u_MaxDistance || distanceToCamera < 0.32) {
        fragColor = original;
        return;
    }

    vec3 worldPosition = reconstructWorldPosition(viewPosition);
    vec3 cameraWorld = cameraWorldPosition();

    // Пол находится строго ниже глаз. Это отсекает нижние грани блоков
    // над головой: по геометрии они такие же горизонтальные, как пол,
    // и никаким анализом наклона от него не отличаются.
    float belowEye = 1.0 - smoothstep(-0.02, 0.10, worldPosition.y - cameraWorld.y);
    if (belowEye <= 0.002) {
        fragColor = original;
        return;
    }

    // Верхняя грань блока: горизонтальная поверхность без наклона.
    float upwardMask = topFaceMask(vUv, worldPosition) * belowEye;

    if (upwardMask <= 0.002) {
        fragColor = original;
        return;
    }

    // Привязка к сетке блоков: верхняя грань лежит на кратном 1/8 блока уровне Y.
    // Шаг 1/8 (0.125) покрывает не только полные блоки и плиты (0.5), но и тонкие
    // поверхности вроде снежного покрова, чьи грани стоят на 0.125-уровнях. Раньше
    // сетка допускала только целые/половинные уровни (round(y*2)*0.5), из-за чего
    // снег с offset ≈ 0.125 проходил только там, где допуск успевал вырасти с
    // расстоянием (по краям экрана), а у центра отбраковывался — получалась «дыра».
    // Допуск растёт с расстоянием, потому что точность буфера глубины падает.
    float heightOffset = abs(worldPosition.y - round(worldPosition.y * 8.0) * 0.125);
    float heightTolerance = 0.03 + distanceToCamera * 0.004;
    float gridMask = 1.0 - smoothstep(heightTolerance, heightTolerance * 2.5, heightOffset);
    if (gridMask <= 0.002) {
        fragColor = original;
        return;
    }
    upwardMask *= gridMask;

    // Поверхность признана горизонтальной, поэтому мировая нормаль — строго вверх.
    vec3 viewNormal = normalize(mat3(u_ViewMatrix) * vec3(0.0, 1.0, 0.0));

    // Гладкий пол: покрытие равномерное, без покубовой рандомизации. Раньше
    // каждый блок получал своё значение через hash от целой позиции, из-за чего
    // одни блоки становились зеркальными, а соседние только затемнялись и
    // «выцветали». Теперь все горизонтальные грани получают одинаковое покрытие.
    float puddle = saturate(u_PuddleCoverage);

    float rangeFade = 1.0 - smoothstep(u_MaxDistance * 0.72, u_MaxDistance, distanceToCamera);
    float nearFade = smoothstep(0.32, 0.82, distanceToCamera);
    // Жёсткая привязка к блокам: эффект ограничен фиксированным радиусом по
    // горизонтали вокруг игрока, поэтому захваченная площадь не «растёт» при
    // подъёме головы и не уходит за горизонт.
    float horizDist = length(worldPosition.xz - cameraWorld.xz);
    float blockRadiusFade = 1.0 - smoothstep(22.0, 30.0, horizDist);
    float wetFilm = 0.44 + puddle * 0.56;
    float wetMask = saturate(upwardMask * rangeFade * nearFade * blockRadiusFade * wetFilm * u_Wetness);
    if (wetMask <= 0.002) {
        fragColor = original;
        return;
    }

    // Анимация капель/волн отключена: поверхность остаётся зеркально гладкой.
    vec3 reflectionNormal = viewNormal;

    vec3 incident = normalize(viewPosition);
    vec3 reflectedDirection = normalize(reflect(incident, reflectionNormal));

    // Отражение берём ТОЛЬКО из raymarch по буферу глубины: попавший луч
    // указывает на реальную геометрию кадра, поэтому отражение жёстко привязано
    // к миру и движется вместе с ним. Прежний запасной вариант проецировал само
    // направление отражения (projectDirection) — оно привязано к ориентации и
    // положению камеры, а не к миру, из-за чего отражения «плыли» при движении и,
    // в частности, при прыжке дёргались вместе с игроком, хотя мир оставался на
    // месте. Если луч не попал в геометрию — отражения сцены просто нет.
    float rayHit = 0.0;
    vec2 rayUv = traceReflection(viewPosition, reflectedDirection, viewNormal, rayHit);

    vec2 reflectionUv = rayUv;
    float reflectionValid = 0.0;
    if (rayHit > 0.001) {
        reflectionValid = rayHit * 0.90;
    }

    float border = min(min(reflectionUv.x, reflectionUv.y),
                       min(1.0 - reflectionUv.x, 1.0 - reflectionUv.y));
    reflectionValid *= smoothstep(0.0, 0.035, border);

    // Небо в отражении не показываем. Экранное отражение по направлению
    // (directionUv) при взгляде вверх попадает в область неба вверху кадра,
    // из-за чего в полу «проявлялось» небо, привязанное к повороту головы.
    // Если отражённый луч попал в пиксель неба (максимальная глубина) —
    // отражение считаем невалидным.
    float reflectionDepth = texture(u_DepthTexture, reflectionUv).r;
    if (reflectionDepth >= 0.999995) {
        reflectionValid = 0.0;
    }

    float roughness = mix(3.4, 1.35, puddle);
    vec3 reflectedColor = blurredReflection(reflectionUv, roughness);
    reflectedColor = mix(reflectedColor, reflectedColor * vec3(0.93, 0.98, 1.08), 0.34);

    vec3 viewToCamera = normalize(-viewPosition);
    float noV = saturate(dot(reflectionNormal, viewToCamera));
    float fresnel = 0.12 + 0.88 * pow(1.0 - noV, 3.0);

    float darkening = saturate(wetMask * (0.20 + puddle * 0.16));
    vec3 wetBase = original.rgb * (1.0 - darkening);
    float wetLuma = luminance(wetBase);
    wetBase = mix(vec3(wetLuma), wetBase, 1.12);
    wetBase *= vec3(0.96, 0.985, 1.035);

    float reflectivePuddle = smoothstep(0.30, 0.72, puddle);
    float reflectionAmount = wetMask
            * reflectivePuddle
            * (0.30 + fresnel * 0.82)
            * u_ReflectionStrength
            * reflectionValid;
    reflectionAmount = min(reflectionAmount, 0.90);

    vec3 color = mix(original.rgb, wetBase, saturate(wetMask * 0.92));
    color = mix(color, reflectedColor * 1.06, reflectionAmount);

    float reflectedLight = smoothstep(0.58, 1.0, luminance(reflectedColor));
    color += reflectedColor * reflectedLight * wetMask * (0.025 + fresnel * 0.09);
    color += vec3(0.035, 0.052, 0.075) * fresnel * wetMask * (0.30 + puddle * 0.70);

    fragColor = vec4(clamp(color, 0.0, 1.0), original.a);
}
