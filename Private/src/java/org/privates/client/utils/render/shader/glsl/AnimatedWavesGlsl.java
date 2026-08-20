package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class AnimatedWavesGlsl implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D u_diffuse_sampler;\n" +
                "uniform sampler2D u_other_sampler;\n" +
                "uniform vec2 u_texel_size;\n" +
                "uniform float u_time;\n" +
                "uniform float u_wave_strength;\n" +
                "\n" +
                "void main() {\n" +
                "    vec2 uv = gl_TexCoord[0].st;\n" +
                "    uv.y = 1.0 - uv.y;\n" +
                "\n" +
                "    float alpha = texture2D(u_other_sampler, uv).a;\n" +
                "    if (alpha == 0.0) discard;\n" +
                "\n" +
                "    vec2 wave = vec2(\n" +
                "        sin(uv.y * 20.0 + u_time * 3.0) * 0.003,\n" +
                "        cos(uv.x * 20.0 + u_time * 3.0) * 0.003\n" +
                "    ) * u_wave_strength;\n" +
                "\n" +
                "    vec3 color = texture2D(u_diffuse_sampler, uv + wave).rgb;\n" +
                "\n" +
                "    vec3 blur = vec3(0.0);\n" +
                "    for (int i = -2; i <= 2; i++) {\n" +
                "        for (int j = -2; j <= 2; j++) {\n" +
                "            vec2 offset = vec2(float(i), float(j)) * u_texel_size * 0.5;\n" +
                "            blur += texture2D(u_diffuse_sampler, uv + wave + offset).rgb;\n" +
                "        }\n" +
                "    }\n" +
                "    blur /= 25.0;\n" +
                "\n" +
                "    color = mix(blur, color, 0.7);\n" +
                "\n" +
                "    gl_FragColor = vec4(color, alpha);\n" +
                "}";
    }
}