package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class HexagonalBlurGlsl implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D u_diffuse_sampler;\n" +
                "uniform sampler2D u_other_sampler;\n" +
                "uniform vec2 u_texel_size;\n" +
                "uniform float u_blur_radius;\n" +
                "\n" +
                "const float PI = 3.14159265359;\n" +
                "\n" +
                "void main() {\n" +
                "    vec2 uv = gl_TexCoord[0].st;\n" +
                "    uv.y = 1.0 - uv.y;\n" +
                "\n" +
                "    float alpha = texture2D(u_other_sampler, uv).a;\n" +
                "    if (alpha == 0.0) discard;\n" +
                "\n" +
                "    vec3 color = vec3(0.0);\n" +
                "    float total = 0.0;\n" +
                "\n" +
                "    for (int ring = 0; ring <= 3; ring++) {\n" +
                "        float radius = float(ring) * u_blur_radius;\n" +
                "        int samples = ring == 0 ? 1 : ring * 6;\n" +
                "        \n" +
                "        for (int i = 0; i < samples; i++) {\n" +
                "            float angle = (float(i) / float(samples)) * 2.0 * PI;\n" +
                "            vec2 offset = vec2(cos(angle), sin(angle)) * radius * u_texel_size;\n" +
                "            color += texture2D(u_diffuse_sampler, uv + offset).rgb;\n" +
                "            total += 1.0;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    color /= total;\n" +
                "    gl_FragColor = vec4(color, alpha);\n" +
                "}";
    }
}