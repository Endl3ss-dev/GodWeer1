#version 120
#extension GL_OES_standard_derivatives : enable

uniform float time;
uniform vec2 speed;
uniform vec3 themeColor;
uniform float alpha;
uniform int colorMode; // 0 = Кастомный, 1 = Разноцветный
uniform vec3 cameraOffset; // Позиция камеры для привязки к миру
uniform float breakProgress; // 0 = нет, 0..1 = прогресс ломания (заливка снизу вверх)
uniform float blockMinY;
uniform float blockMaxY;
varying vec3 vPos;

// Цвета: Мятный, Фиолетовый, Персиковый, Голубой, Розовый, Персиковый
const vec3 rainbowColors[6] = vec3[6](
    vec3(0.596, 1.0, 0.596),    // Мятный
    vec3(0.45, 0.25, 0.6),      // Фиолетовый (тусклее)
    vec3(1.0, 0.8, 0.6),        // Персиковый
    vec3(0.0, 0.749, 1.0),      // Голубой
    vec3(1.0, 0.412, 0.706),    // Розовый
    vec3(1.0, 0.85, 0.7)        // Персиковый (другой оттенок)
);

// Псевдо-рандом функция
float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

// Шум для волн
float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

// Фрактальный шум для более сложных волн
float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 4; i++) {
        value += amplitude * noise(p);
        p *= 2.0;
        amplitude *= 0.5;
    }
    return value;
}

vec3 getRainbowColor(float t) {
    float scaledT = mod(t, 6.0);
    int idx0 = int(floor(scaledT));
    int idx1 = int(mod(float(idx0 + 1), 6.0));
    float fract_t = fract(scaledT);
    return mix(rainbowColors[idx0], rainbowColors[idx1], fract_t);
}

mat2 rotate2D(float r) {
    return mat2(cos(r), -sin(r), sin(r), cos(r));
}

void main() {
    // Преобразуем координаты из view space в world space
    vec3 worldPos = vPos + cameraOffset;
    
    float heightFrac = 1.0;
    if (breakProgress > 0.001) {
        float heightSpan = blockMaxY - blockMinY + 0.0001;
        heightFrac = (worldPos.y - blockMinY) / heightSpan;
    }
    
    vec3 fdx = dFdx(worldPos);
    vec3 fdy = dFdy(worldPos);
    vec3 n_vec = normalize(cross(fdx, fdy));
    vec3 absN = abs(n_vec);

    vec2 uv;
    if (absN.x > 0.5) uv = worldPos.zy;
    else if (absN.z > 0.5) uv = worldPos.xy;
    else uv = worldPos.xz;

    // Локальные координаты 0..1 на грани — одинаковая скорость анимации на всех сторонах
    vec2 uvLocal = fract(uv);
    vec2 p_world = uvLocal * 0.5;
    vec2 p_local = (uvLocal - 0.5) * 0.5;
    
    vec3 col = vec3(0.0);
    float t = time * speed.x * 0.15;
    
    // Добавляем волновое искажение
    vec2 waveOffset = vec2(
        sin(p_world.y * 3.0 + t * 0.8) * 0.15,
        cos(p_world.x * 2.5 + t * 0.6) * 0.15
    );
    
    // Добавляем шумовое искажение для рандомности
    float noiseVal = fbm(p_world * 3.0 + t * 0.2);
    waveOffset += vec2(noiseVal - 0.5) * 0.2;
    
    vec2 p_distorted = p_world + waveOffset;
    
    vec2 n = vec2(0.0);
    vec2 q = vec2(0.0);
    float d = dot(p_local, p_local); 
    
    float S = 18.0;
    float a = -0.004;
    mat2 m = rotate2D(5.0 + sin(t * 0.1) * 0.5);

    vec2 p = p_distorted; 

    for (float j = 0.0; j < 8.0; j++) {
        p *= m;
        n *= m;
        // Добавляем волновую модуляцию в паттерн
        float waveModulation = sin(t * 0.4 + j * 0.7 + length(p) * 2.0) * 0.5;
        q = p * S + sin(t * 0.25) * 3.0 + sin(t * 0.2 - d * 8.0) * 0.1 + 2.5 * j - 0.85 * n;
        q += vec2(waveModulation);
        a += dot(cos(q) / S, vec2(0.22));
        n -= sin(q);
        S *= 1.65;
    }

    // Добавляем волновую пульсацию интенсивности
    float wavePulse = sin(length(p_world) * 8.0 - t * 1.0) * 0.15 + 0.85;
    
    float intensity1 = (a + 0.175) * wavePulse;
    float intensity2 = (9.0 * a + a + d) * wavePulse;

    vec3 currentColor;
    if (colorMode == 1) {
        // Разноцветный режим - цвет меняется волнами по позиции и времени
        float waveColor = sin(p_world.x * 4.0 + t * 0.4) * cos(p_world.y * 3.0 + t * 0.3);
        float colorIndex = (p_world.x + p_world.y) * 1.5 + noiseVal * 2.0 + t * speed.x * 0.1 + waveColor;
        currentColor = getRainbowColor(colorIndex);
    } else {
        currentColor = themeColor;
    }

    vec3 pattern = currentColor * intensity1 + currentColor * intensity2;
    
    vec3 background = currentColor * 0.3;
    
    col = max(background, background + pattern);

    col *= 1.3;

    // Зона прогресса ломания — тот же паттерн, с красноватым оттенком
    if (breakProgress > 0.001 && heightFrac < breakProgress) {
        vec3 breakTint = vec3(1.0, 0.22, 0.22);
        col = mix(col, col * breakTint, 0.72);
    }

    gl_FragColor = vec4(col, alpha);
}