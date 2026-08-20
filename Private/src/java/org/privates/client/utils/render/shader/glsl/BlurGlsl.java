package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class BlurGlsl implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D u_diffuse_sampler;\n" +
                "uniform sampler2D u_other_sampler;\n" +
                "uniform vec2 u_texel_size;\n" +
                "uniform vec2 u_direction;\n" +
                "uniform float u_radius;\n" +
                "uniform float u_kernel[128];\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    vec2 uv = gl_TexCoord[0].st;\n" +
                "    uv.y = 1.0 - uv.y;\n" +
                "\n" +
                "    float alpha = texture2D(u_other_sampler, uv).a;\n" +
                "    bool isVertical = u_direction.x == 0.0;\n" +
                "    \n" +
                "    if (isVertical && alpha == 0.0) {\n" +
                "        discard;\n" +
                "    }\n" +
                "\n" +
                "    vec4 pixel_color = texture2D(u_diffuse_sampler, uv) * u_kernel[0];\n" +
                "    vec2 offset_step = u_texel_size * u_direction;\n" +
                "    \n" +
                "    int maxRadius = int(min(u_radius, 127.0));\n" +
                "    \n" +
                "    for (int i = 1; i <= maxRadius; i++) {\n" +
                "        vec2 offset = float(i) * offset_step;\n" +
                "        float kernel_val = u_kernel[i];\n" +
                "        pixel_color += texture2D(u_diffuse_sampler, uv - offset) * kernel_val;\n" +
                "        pixel_color += texture2D(u_diffuse_sampler, uv + offset) * kernel_val;\n" +
                "    }\n" +
                "\n" +
                "    gl_FragColor = vec4(pixel_color.rgb, isVertical ? alpha : 1.0);\n" +
                "}";
    }
}