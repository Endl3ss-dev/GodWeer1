package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class EntityTransparentOutlineGlsl implements IShader {
    @Override
    public String shader() {
        return """
                #version 120
                                        
                uniform vec2 location, rectSize;
                uniform sampler2D tex;
                uniform vec4 color;
                uniform float time;
                uniform float outlineWidth;
                uniform float fillAlpha;
                uniform vec4 outlineColor;
                                        
                void main() {
                    vec2 texCoord = gl_TexCoord[0].st;
                    float texAlpha = texture2D(tex, texCoord).a;
                    
                    if (texAlpha < 0.01) {
                        discard;
                    }
                    
                    // Простое определение края - проверяем соседние пиксели
                    vec2 pixelSize = 1.0 / rectSize;
                    bool isEdge = false;
                    
                    // Проверяем 8 направлений вокруг текущего пикселя
                    float checkRadius = outlineWidth;
                    for (float i = 0.0; i < 8.0; i++) {
                        float angle = i * 3.14159 / 4.0;
                        vec2 offset = vec2(cos(angle), sin(angle)) * pixelSize * checkRadius;
                        float neighborAlpha = texture2D(tex, texCoord + offset).a;
                        
                        if (neighborAlpha < 0.01) {
                            isEdge = true;
                            break;
                        }
                    }
                    
                    // Если это край - используем цвет обводки, иначе - цвет заливки
                    vec3 finalColor;
                    float finalAlpha;
                    
                    if (isEdge) {
                        // Обводка - яркая и непрозрачная
                        finalColor = outlineColor.rgb;
                        finalAlpha = outlineColor.a * texAlpha;
                        
                        // Легкое пульсирующее свечение
                        float pulse = sin(time * 2.0) * 0.15 + 0.85;
                        finalColor *= pulse;
                    } else {
                        // Заливка - полупрозрачная
                        finalColor = color.rgb;
                        finalAlpha = fillAlpha * texAlpha;
                    }
                    
                    gl_FragColor = vec4(finalColor, finalAlpha);
                }""";
    }
}
