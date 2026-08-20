package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class PixelatedGlassGlsl implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D u_diffuse_sampler;\n" +
                "uniform sampler2D u_other_sampler;\n" +
                "uniform vec2 u_resolution;\n" +
                "uniform float u_pixel_size;\n" +
                "\n" +
                "void main() {\n" +
                "    vec2 uv = gl_TexCoord[0].st;\n" +
                "    uv.y = 1.0 - uv.y;\n" +
                "\n" +
                "    float alpha = texture2D(u_other_sampler, uv).a;\n" +
                "    if (alpha == 0.0) discard;\n" +
                "\n" +
                "    vec2 pixelated = floor(uv * u_resolution / u_pixel_size) * u_pixel_size / u_resolution;\n" +
                "\n" +
                "    vec3 color = texture2D(u_diffuse_sampler, pixelated).rgb;\n" +
                "\n" +
                "    vec3 blur = vec3(0.0);\n" +
                "    for (int i = -1; i <= 1; i++) {\n" +
                "        for (int j = -1; j <= 1; j++) {\n" +
                "            vec2 offset = vec2(float(i), float(j)) * u_pixel_size / u_resolution;\n" +
                "            blur += texture2D(u_diffuse_sampler, pixelated + offset).rgb;\n" +
                "        }\n" +
                "    }\n" +
                "    blur /= 9.0;\n" +
                "\n" +
                "    color = mix(blur, color, 0.5);\n" +
                "\n" +
                "    gl_FragColor = vec4(color, alpha);\n" +
                "}";
    }
}