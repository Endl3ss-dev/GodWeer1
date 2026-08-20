package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class LiquidGlassGlsl implements IShader {
    @Override
    public String shader() {
        return """
                #version 120
                
                uniform vec2 size;
                uniform float round;
                uniform float lensStrength;
                uniform float blurStrength;
                uniform vec4 glassColor;
                uniform sampler2D bgTexture;
                uniform vec2 position;
                uniform vec2 screenSize;
                
                float clamp01(float a) {
                    return clamp(a, 0.0, 1.0);
                }
                
                void main() {
                    vec2 uv = gl_TexCoord[0].st;
                
                    // m2 должен быть от -1 до 1 для корректного расчета
                    vec2 m2 = (uv - 0.5);
                
                    // Вычисляем округленный бокс (суперэллипс степени 8)
                    vec2 sizeDiff = (vec2(1., .56)/round/2.0*screenSize.x/screenSize.y-pow(7.5/80000.0, 1.0/8.0));
                    vec2 m22 = abs(vec2(m2.x*screenSize.x/screenSize.y, m2.y))/ max(round, 1.);
                    float roundedBox = pow(abs(max(m22.x-sizeDiff.x,.0)),8.0)+pow(abs(max(m22.y-sizeDiff.y,0.0)),8.0);
                
                    // Три слоя для эффекта
                    float rb1 = clamp01((1.0 - roundedBox * 10000.0) * 4.0);
                    float rb2 = clamp01((0.95 - roundedBox * 9500.0) * 16.0) - 
                                clamp01(pow(0.9 - roundedBox * 9500.0, 1.0) * 16.0);
                    float rb3 = clamp01((1.5 - roundedBox * 11000.0) * 2.0) - 
                                clamp01(pow(1.0 - roundedBox * 11000.0, 1.0) * 2.0);
                
                    // Усиленный антиалиасинг с двойным smoothstep
                    float transition = smoothstep(0.0, 1.0, smoothstep(0.0, 1.0, rb1));
                
                    // Координаты фона для антиалиасинга
                    vec2 bgUV = (position + uv * size) / screenSize;
                    bgUV.y = 1.0 - bgUV.y;
                    vec4 bgColor = texture2D(bgTexture, clamp(bgUV, 0.0, 1.0));
                
                    // Эффект линзы
                    vec2 lensUV = (uv - 0.5) * (1.0 - roundedBox * lensStrength) + .5;
                
                    // Преобразуем в экранные координаты
                    vec2 screenPos = position + lensUV * size;
                    vec2 screenUV = screenPos / screenSize;
                    screenUV.y = 1.0 - screenUV.y;
                
                    // Размытие
                    vec4 blurColor = vec4(0.0);
                    float total = 0.0;
                
                    for (float x = -4.0; x <= 4.0; x += 1.0) {
                        for (float y = -4.0; y <= 4.0; y += 1.0) {
                            vec2 offset = vec2(x, y) * blurStrength / screenSize;
                            vec2 sampleUV = clamp(screenUV + offset, 0.0, 1.0);
                            blurColor += texture2D(bgTexture, sampleUV);
                            total += 1.0;
                        }
                    }
                    blurColor /= total;
                
                    // Градиент освещения
                    float m2y = m2.y;
                    float gradient = clamp01((clamp(-m2y, 0.0, 0.2) * rb3 + .1) / 2.0);
                
                    // Финальное освещение БЕЗ rb1 для правильного антиалиасинга
                    vec4 lighting = clamp(blurColor + gradient + vec4(rb2) * 0.3, 0.0, 1.0);
                
                    // Применяем тинт стекла
                    lighting = mix(lighting, glassColor, glassColor.a * 0.1);
                
                    // Антиалиасинг - смешиваем с фоном и используем transition как альфа-канал
                    gl_FragColor = vec4(mix(bgColor.rgb, lighting.rgb, transition), transition);
                }""";
    }
}