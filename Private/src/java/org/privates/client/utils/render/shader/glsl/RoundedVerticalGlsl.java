package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class RoundedVerticalGlsl implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform vec4 color1;  // Верхний цвет\n" +
                "uniform vec4 color2;  \n" +
                "uniform vec4 color3;\n" +
                "uniform vec4 color4;  // Нижний цвет\n" +
                "\n" +
                "uniform vec2 size;\n" +
                "uniform vec4 round;\n" +
                "uniform float value;\n" +
                "uniform vec2 smoothness;\n" +
                "uniform bool shadow;\n" +
                "uniform float shadowAlpha;\n" +
                "\n" +
                "float roundedBox(vec2 center, vec2 size, vec4 radius) {\n" +
                "    radius.xy = (center.x > 0.0) ? radius.xy : radius.zw;\n" +
                "    radius.x  = (center.y > 0.0) ? radius.x : radius.y;\n" +
                "\n" +
                "    vec2 q = abs(center) - size + radius.x;\n" +
                "    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius.x;\n" +
                "}\n" +
                "\n" +
                "vec4 createVerticalGradient(vec2 pos) {\n" +
                "    float y = pos.y; // y-координата от 0 (верх) до 1 (низ)\n" +
                "    \n" +
                "    // Определяем 3 точки перехода между цветами (0.33 и 0.66 можно заменить на другие значения)\n" +
                "    float transition1 = 0.33;\n" +
                "    float transition2 = 0.66;\n" +
                "    \n" +
                "    if (y < transition1) {\n" +
                "        // Между color1 и color2\n" +
                "        return mix(color1, color2, y / transition1);\n" +
                "    } else if (y < transition2) {\n" +
                "        // Между color2 и color3\n" +
                "        return mix(color2, color3, (y - transition1) / (transition2 - transition1));\n" +
                "    } else {\n" +
                "        // Между color3 и color4\n" +
                "        return mix(color3, color4, (y - transition2) / (1.0 - transition2));\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "void main() {\n" +
                "    vec2 tex = gl_TexCoord[0].st * size;\n" +
                "\n" +
                "    float distance = roundedBox(tex - (size / 2.0), (size / 2.0) - value, round);\n" +
                "\n" +
                "    float smoothedAlpha = (1.0 - smoothstep(smoothness.x, smoothness.y, distance));\n" +
                "\n" +
                "    vec4 gradient = createVerticalGradient(gl_TexCoord[0].st);\n" +
                "\n" +
                "    if (shadow) {\n" +
                "        vec4 finalColor = mix(vec4(gradient.rgb, 0.0), vec4(gradient.rgb, gradient.a * smoothedAlpha), smoothedAlpha);\n" +
                "        gl_FragColor = vec4(finalColor.rgb, finalColor.a * shadowAlpha);\n" +
                "    } else {\n" +
                "        gl_FragColor = vec4(gradient.rgb, gradient.a * smoothedAlpha);                        \n" +
                "    }\n" +
                "}";
    }
}
