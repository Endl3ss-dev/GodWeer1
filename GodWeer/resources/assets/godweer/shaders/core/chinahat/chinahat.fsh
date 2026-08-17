#version 150

in vec2 TexCoord;
in vec4 FragColor;
in vec3 LocalPos;

uniform float Time;
uniform float Speed;
uniform float Frequency;
uniform float Glow;
uniform float OutlineSize;
uniform float ConeDepth;
uniform int Mode;
uniform int ColorMode;
uniform vec4 BaseColor;
uniform vec4 AccentColor;
uniform vec4 ColorModulator;

out vec4 OutColor;

// Генератор псевдослучайных чисел
float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

// 2D Шум для органических текстур
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

// Дробное броуновское движение (fbm) для дыма и тумана
float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 3; i++) {
        value += noise(p) * amplitude;
        p *= 2.5;
        amplitude *= 0.5;
    }
    return value;
}

vec3 getModeColor(float u, float v, float t) {
    if (ColorMode == 1) {
        float hue = u - v * 0.2 + t * 0.1;
        return 0.5 + 0.5 * cos(6.28318 * (hue + vec3(0.0, 0.333, 0.667)));
    } else if (ColorMode == 2) {
        float wave = sin(u * 6.28318 + t) * 0.5 + 0.5;
        vec3 col1 = vec3(0.1, 0.05, 0.4);
        vec3 col2 = vec3(0.0, 0.8, 0.9);
        vec3 col3 = vec3(0.7, 0.1, 0.85);
        return mix(mix(col1, col2, v), col3, wave);
    } else {
        float wave = sin(u * 6.28318 + t) * 0.5 + 0.5;
        return mix(BaseColor.rgb, AccentColor.rgb, mix(v, wave, 0.5));
    }
}

void main() {
    float t = Time * Speed;
    float u = TexCoord.x;
    float v = TexCoord.y;

    vec3 col = vec3(0.0);
    float alpha = 1.0;

    if (v >= 0.0) {
        if (Mode == 0) {
            // Градиент
            float wave = sin(u * 6.28318 * Frequency - t * 2.0) * 0.5 + 0.5;
            col = getModeColor(u, wave, t);
            alpha = mix(0.4, 0.95, v) * ConeDepth;
        } else if (Mode == 1) {
            // Дыхание ветра (Покачивание + Сдув + Глоу + Дым + Блюр)
<<<<<<< HEAD
            // Координаты дыма сдвигаются по оси U (сдув ветром) и V (подъем дыма)
            vec2 smokeUV = vec2(u * Frequency, v * 2.0 - t * 0.4);
            smokeUV.x += t * OutlineSize * 0.15; // OutlineSize используется как сила ветра для сдува

            // Многослойный дым с имитацией размытия краев (blur)
=======
            vec2 smokeUV = vec2(u * Frequency, v * 2.0 - t * 0.4);
            smokeUV.x += t * OutlineSize * 0.15;

            // Многослойный дым
>>>>>>> 49e82c67882decdf97540abd3c386d782f08425e
            float smoke1 = fbm(smokeUV);
            float smoke2 = fbm(smokeUV * 2.0 + vec2(t * 0.1, -t * 0.2));
            float combinedSmoke = mix(smoke1, smoke2, 0.4);

<<<<<<< HEAD
            // Мягкое размытое облако глоу (mist blur) вокруг конуса
            float blurMist = fbm(vec2(u * 3.0, v - t * 0.1)) * 0.5 + 0.5;

            // Цветовая основа
            vec3 baseCol = getModeColor(u, v, t);
            
            // Свечение и дымовые пряди
            col = baseCol * (1.0 + combinedSmoke * 1.5 + blurMist * Glow * 0.8);
            col += vec3(1.0, 1.0, 1.0) * (combinedSmoke * combinedSmoke * 0.2); // яркие дымовые ядра
            
=======
            // Размытый туман вокруг
            float blurMist = fbm(vec2(u * 3.0, v - t * 0.1)) * 0.5 + 0.5;

            vec3 baseCol = getModeColor(u, v, t);
            col = baseCol * (1.0 + combinedSmoke * 1.5 + blurMist * Glow * 0.8);
            col += vec3(1.0, 1.0, 1.0) * (combinedSmoke * combinedSmoke * 0.2);
>>>>>>> 49e82c67882decdf97540abd3c386d782f08425e
            alpha = mix(0.2, 0.85, v) * (0.3 + combinedSmoke * 0.7) * ConeDepth;
        } else if (Mode == 2) {
            // Квантовый режим
            float cellsX = floor(u * 32.0);
            float cellsY = floor(v * 8.0);
            float randomActive = sin(cellsX * 12.9898 + cellsY * 78.233 + t * 2.5) * 43758.5453;
            float glowIntensity = step(0.92, fract(randomActive));
            float gridX = smoothstep(0.0, 0.08, abs(sin(u * 3.14159 * 32.0)));
            float gridY = smoothstep(0.0, 0.08, abs(sin(v * 3.14159 * 8.0)));
            float grid = (1.0 - gridX) + (1.0 - gridY);
            vec3 baseCol = getModeColor(u, v, t);
            col = mix(baseCol * 0.4, vec3(1.0, 1.0, 1.0), glowIntensity * 0.8 + grid * 0.3);
            alpha = (mix(0.5, 0.9, v) * ConeDepth + (glowIntensity * 0.35 + grid * 0.15)) * ConeDepth;
<<<<<<< HEAD
        }
    } else {
        // Отрисовка внешней юбки глоу (мягкое свечение со сдувом дыма)
=======
        } else if (Mode == 3) {
            // Голограмма Never (Шейдер на основе Hands.java с сохранением 3D-теней и зеркальным бликом)
            vec3 norm = normalize(vec3(LocalPos.x, 0.35, LocalPos.z));
            vec3 lightDir = normalize(vec3(0.5, 1.0, 0.3));
            
            // Диффузное освещение + зеркальный глянец
            float diffuse = max(0.25, dot(norm, lightDir));
            vec3 viewDir = normalize(vec3(0.0, 0.0, 1.0));
            vec3 halfDir = normalize(lightDir + viewDir);
            float spec = pow(max(0.0, dot(norm, halfDir)), 12.0) * 0.45;
            
            vec3 fillCol = getModeColor(u, v, t);
            
            // Смешиваем цвет с тенями и добавляем блик
            col = fillCol * diffuse + vec3(1.0) * spec;
            alpha = mix(0.45, 0.9, v) * ConeDepth;
        }
    } else {
>>>>>>> 49e82c67882decdf97540abd3c386d782f08425e
        float dist = abs(v);
        alpha = pow(1.0 - dist, 1.8) * Glow * 0.65;
        if (alpha <= 0.001) {
            discard;
        }

        if (Mode == 0) {
            float wave = sin(u * 6.28318 * Frequency - t * 2.0) * 0.5 + 0.5;
            col = getModeColor(u, wave, t);
        } else if (Mode == 1) {
            vec2 smokeUV = vec2(u * Frequency, -t * 0.4);
            smokeUV.x += t * OutlineSize * 0.15;
            float smoke = fbm(smokeUV);
            col = getModeColor(u, 0.0, t) * (1.0 + smoke * Glow);
            alpha *= (0.4 + smoke * 0.6);
        } else if (Mode == 2) {
            float cellsX = floor(u * 32.0);
            float randomActive = sin(cellsX * 12.9898 + t * 2.5) * 43758.5453;
            float glowIntensity = step(0.92, fract(randomActive));
            col = mix(getModeColor(u, 0.0, t) * 0.4, vec3(1.0, 1.0, 1.0), glowIntensity * 0.8);
<<<<<<< HEAD
=======
        } else if (Mode == 3) {
            col = getModeColor(u, 0.0, t);
            alpha *= 0.8;
>>>>>>> 49e82c67882decdf97540abd3c386d782f08425e
        }
    }

    vec4 finalColor = vec4(col, alpha);
    if (finalColor.a <= 0.001) {
        discard;
    }

    OutColor = finalColor * ColorModulator;
}
