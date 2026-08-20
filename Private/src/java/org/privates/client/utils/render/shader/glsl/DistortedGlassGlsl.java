package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class DistortedGlassGlsl implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D u_diffuse_sampler;\n" +
                "uniform sampler2D u_other_sampler;\n" +
                "uniform vec2 u_texel_size;\n" +
                "uniform float u_time;\n" +
                "uniform float u_intensity;\n" +
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
                "    \n" +
                "    if (alpha == 0.0) {\n" +
                "        discard;\n" +
                "    }\n" +
                "\n" +
                "    vec2 distortion = vec2(\n" +
                "        sin(uv.y * 10.0 + u_time * 2.0) * 0.005,\n" +
                "        cos(uv.x * 10.0 + u_time * 2.0) * 0.005\n" +
                "    ) * u_intensity;\n" +
                "\n" +
                "    vec2 noise = vec2(\n" +
                "        rand(uv + u_time) * 0.002,\n" +
                "        rand(uv.yx + u_time) * 0.002\n" +
                "    ) * u_intensity;\n" +
                "\n" +
                "    vec2 distorted_uv = uv + distortion + noise;\n" +
                "\n" +
                "    vec3 color = texture2D(u_diffuse_sampler, distorted_uv).rgb;\n" +
                "\n" +
                "    vec3 refraction = vec3(0.0);\n" +
                "    for (int i = -2; i <= 2; i++) {\n" +
                "        for (int j = -2; j <= 2; j++) {\n" +
                "            vec2 offset = vec2(float(i), float(j)) * u_texel_size * 0.5;\n" +
                "            refraction += texture2D(u_diffuse_sampler, distorted_uv + offset).rgb;\n" +
                "        }\n" +
                "    }\n" +
                "    refraction /= 25.0;\n" +
                "\n" +
                "    color = mix(color, refraction, 0.3);\n" +
                "\n" +
                "    float vignette = 1.0 - length(uv - 0.5) * 0.3;\n" +
                "    color *= vignette;\n" +
                "\n" +
                "    gl_FragColor = vec4(color, alpha);\n" +
                "}";
    }
}