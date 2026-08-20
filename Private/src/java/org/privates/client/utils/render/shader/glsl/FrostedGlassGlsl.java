package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class FrostedGlassGlsl implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D u_diffuse_sampler;\n" +
                "uniform sampler2D u_other_sampler;\n" +
                "uniform vec2 u_texel_size;\n" +
                "uniform float u_time;\n" +
                "uniform float u_frost_amount;\n" +
                "\n" +
                "float rand(vec2 co) {\n" +
                "    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);\n" +
                "}\n" +
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
                "    for (int i = -4; i <= 4; i++) {\n" +
                "        for (int j = -4; j <= 4; j++) {\n" +
                "            vec2 offset = vec2(float(i), float(j)) * u_texel_size * u_frost_amount;\n" +
                "            vec2 noise = vec2(\n" +
                "                rand(uv + offset + u_time * 0.1),\n" +
                "                rand(uv.yx + offset + u_time * 0.1)\n" +
                "            ) * 0.003;\n" +
                "            color += texture2D(u_diffuse_sampler, uv + offset + noise).rgb;\n" +
                "            total += 1.0;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    color /= total;\n" +
                "    color = mix(color, vec3(1.0), 0.05);\n" +
                "\n" +
                "    gl_FragColor = vec4(color, alpha);\n" +
                "}";
    }
}