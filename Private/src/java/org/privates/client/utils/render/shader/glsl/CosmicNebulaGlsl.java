package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class CosmicNebulaGlsl implements IShader {
    @Override
    public String shader() {
        return """
                #version 120
                
                uniform sampler2D u_diffuse_sampler;
                uniform sampler2D u_other_sampler;
                uniform vec2 u_texel_size;
                uniform float u_time;
                uniform float u_nebula_density;
                
                // =============================================
                //  Утилиты
                // =============================================
                
                float hash(vec2 p) {
                    p = fract(p * vec2(127.1, 311.7));
                    p += dot(p, p + 19.19);
                    return fract(p.x * p.y);
                }
                
                // Value noise с бикубической интерполяцией
                float noise(vec2 p) {
                    vec2 i = floor(p);
                    vec2 f = fract(p);
                    // Бикубический hermite
                    vec2 u = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);
                    return mix(
                        mix(hash(i + vec2(0,0)), hash(i + vec2(1,0)), u.x),
                        mix(hash(i + vec2(0,1)), hash(i + vec2(1,1)), u.x),
                        u.y
                    );
                }
                
                // Матрица поворота
                mat2 rot(float a) {
                    float c = cos(a), s = sin(a);
                    return mat2(c, -s, s, c);
                }
                
                // Domain-warped FBM — каждая октава искажает пространство,
                // создавая органичные живые завихрения
                float fbm(vec2 p, float warpAmt) {
                    float val = 0.0;
                    float amp = 0.52;
                    float freq = 1.6;
                    vec2 warp = vec2(0.0);
                
                    for (int i = 0; i < 7; i++) {
                        vec2 pp = p * freq + warp;
                        float n = noise(pp);
                        val += n * amp;
                        // Искажаем следующую октаву результатом текущей
                        warp += vec2(
                            noise(pp + vec2(3.7, 1.2)),
                            noise(pp + vec2(8.3, 5.6))
                        ) * warpAmt;
                        amp  *= 0.48;
                        freq *= 2.07;
                        p = rot(0.37) * p;  // поворот для изотропности
                    }
                    return val;
                }
                
                // =============================================
                //  Звёзды
                // =============================================
                
                // Мерцающая звезда в воронойной клетке
                float star(vec2 uv, float cellScale, float threshold, float t) {
                    vec2 cell  = floor(uv * cellScale);
                    vec2 local = fract(uv * cellScale) - 0.5;
                    float h = hash(cell);
                    if (h < threshold) return 0.0;
                
                    // Уникальное мерцание для каждой звезды
                    float flicker = 0.72 + 0.28 * sin(t * (1.8 + h * 4.5) + h * 6.28318);
                    float r = 0.03 + h * 0.05;
                    float d = length(local);
                    float glow  = exp(-d * d / (r * r * 4.5));    // мягкое гало
                    float sharp = smoothstep(r, r * 0.25, d);     // яркое ядро
                    return (sharp * 0.65 + glow * 0.35) * flicker * ((h - threshold) / (1.0 - threshold));
                }
                
                // =============================================
                //  Основной шейдер
                // =============================================
                
                void main() {
                    vec2 uv = gl_TexCoord[0].st;
                    uv.y = 1.0 - uv.y;
                
                    float alpha = texture2D(u_other_sampler, uv).a;
                    if (alpha < 0.01) discard;
                
                    vec3 bgColor = texture2D(u_diffuse_sampler, uv).rgb;
                
                    float t = u_time;
                
                    // ─── 4 независимых слоя туманности ───
                    // Каждый дрейфует в своём направлении — создаёт глубину и движение
                
                    // Слой 1: крупные медленные облака, плывут влево-вверх
                    vec2 drift1 = vec2(sin(t * 0.07) * 0.14, cos(t * 0.05) * 0.10)
                                + t * vec2(-0.020, 0.013);
                    float cloud1 = fbm(uv * 2.8 + drift1, 0.35);
                
                    // Слой 2: средние облака, вправо с лёгким вращением координат
                    vec2 drift2 = vec2(cos(t * 0.06) * 0.11, sin(t * 0.08) * 0.09)
                                + t * vec2(0.016, -0.010);
                    float cloud2 = fbm(rot(t * 0.008) * uv * 4.2 + drift2 + vec2(3.7, 1.9), 0.28);
                
                    // Слой 3: мелкие детали, быстрее и резче
                    vec2 drift3 = vec2(sin(t * 0.11 + 1.3) * 0.08, cos(t * 0.09 + 2.1) * 0.07)
                                + t * vec2(0.024, 0.016);
                    float cloud3 = fbm(uv * 7.0 + drift3 + vec2(8.1, 5.4), 0.20);
                
                    // Слой 4: очень медленное глобальное перетекание формы (форма меняется)
                    vec2 drift4 = t * vec2(-0.007, -0.009)
                                + vec2(sin(t * 0.03) * 0.18, cos(t * 0.04) * 0.14);
                    float cloud4 = fbm(uv * 1.5 + drift4 + vec2(14.2, 7.7), 0.45);
                
                    // Объединяем слои
                    float density = cloud1 * 0.38 + cloud2 * 0.30 + cloud3 * 0.16 + cloud4 * 0.16;
                    density = pow(density * 1.65, 1.65) * u_nebula_density;
                    density = clamp(density, 0.0, 1.0);
                
                    // ─── Анимированные цвета ───
                    // colorShift медленно качается 0..1 — цвета плавно перетекают
                    float colorShift = sin(t * 0.07) * 0.5 + 0.5;
                    float colorShift2 = cos(t * 0.05 + 1.2) * 0.5 + 0.5;
                
                    // Холодный: синий ↔ фиолетовый
                    vec3 cold = mix(
                        vec3(0.04, 0.10, 0.58) + vec3(0.18, 0.0, 0.38) * cloud2,
                        vec3(0.12, 0.02, 0.62) + vec3(0.0,  0.08, 0.28) * cloud1,
                        colorShift
                    );
                
                    // Тёплый: красно-оранжевый с пульсацией
                    float warmPulse = 0.78 + 0.22 * sin(t * 0.18 + cloud1 * 4.2);
                    vec3 warm = mix(
                        vec3(0.68, 0.07, 0.18),
                        vec3(0.55, 0.18, 0.05),
                        colorShift2
                    ) * warmPulse + vec3(0.28, 0.10, 0.03) * cloud3;
                
                    // Голубое гало (меняет насыщенность)
                    vec3 halo = vec3(0.01, 0.20 + colorShift * 0.08, 0.60) * (1.0 - cloud4 * 0.5);
                
                    // Глубокий пурпур в пустоте
                    vec3 deep = vec3(0.07, 0.0, 0.16);
                
                    // Финальное смешение цветов туманности
                    float blend = clamp(cloud3 * 1.9 - 0.35 + sin(t * 0.12) * 0.12, 0.0, 1.0);
                    vec3 nebula = mix(cold, warm, blend);
                    nebula = mix(nebula, halo, clamp(1.0 - cloud1 * 2.3, 0.0, 0.45));
                    nebula = mix(deep, nebula, density);
                
                    // Горячие ядра внутри туманности
                    float hotCore = pow(max(cloud2 * cloud3 - 0.28, 0.0), 2.8) * 5.5;
                    nebula += vec3(0.88, 0.93, 1.0) * hotCore * density;
                
                    // ─── Звёздное поле с параллаксом ───
                    // Каждый слой звёзд движется с разной скоростью (параллакс глубины)
                    vec2 par1 = uv + t * vec2( 0.0030,  0.0018);
                    vec2 par2 = uv + t * vec2(-0.0050,  0.0032);
                    vec2 par3 = uv + t * vec2( 0.0018, -0.0042);
                
                    float s1 = star(par1,              120.0, 0.93, t);           // крупные
                    float s2 = star(par2 * 0.6 + vec2(0.41, 0.23), 90.0, 0.90, t) * 0.55; // средние
                    float s3 = star(par3 * 1.4 + vec2(0.73, 0.51), 60.0, 0.88, t) * 0.28; // мелкие
                
                    // Цвет звёзд слегка меняется со временем (тёплые ↔ холодные)
                    vec3 starTint = mix(vec3(1.0, 0.92, 0.80), vec3(0.80, 0.90, 1.0),
                                       sin(t * 0.09) * 0.5 + 0.5);
                    vec3 starColor = starTint * (s1 + s2 + s3);
                    // В облаках туманности звёзды частично скрыты
                    starColor *= (1.0 - density * 0.58);
                
                    // ─── Глобальная пульсация яркости ───
                    // Медленное «дыхание» — вся сцена слегка пульсирует
                    float breathe = 0.92 + 0.08 * sin(t * 0.28 + cloud4 * 2.5);
                    nebula *= breathe;
                
                    // ─── Финальная сборка ───
                    vec3 space = vec3(0.0, 0.0, 0.022);          // тёмно-синий фон космоса
                    vec3 cosmicColor = space;
                    cosmicColor = mix(cosmicColor, nebula, clamp(density * 1.12, 0.0, 1.0));
                    cosmicColor += starColor;
                
                    // Лёгкая примесь фона игры
                    cosmicColor = mix(bgColor * 0.10, cosmicColor, 0.92);
                
                    gl_FragColor = vec4(cosmicColor, alpha);
                }
                """;
    }
}