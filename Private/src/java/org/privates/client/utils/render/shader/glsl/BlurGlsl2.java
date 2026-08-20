package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class BlurGlsl2 implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D sampler1;\n" +
                "uniform sampler2D sampler2;\n" +
                "uniform vec2 texelSize;\n" +
                "uniform vec2 direction;\n" +
                "uniform float radius;\n" +
                "uniform float kernel[64];\n" +
                "uniform vec4 blurColor;\n" +
                "\n" +
                "#define offset texelSize * direction\n" +
                "\n" +
                "void main() {\n" +
                "    vec2 uv = gl_TexCoord[0].st;\n" +
                "    uv.y = 1.0 - uv.y;\n" +
                "\n" +
                "    float alpha = texture2D(sampler2, uv).a;\n" +
                "    if (direction.x == 0.0 && alpha == 0.0) {\n" +
                "        discard;\n" +
                "    }\n" +
                "\n" +
                "    vec3 blr = texture2D(sampler1, uv).rgb * kernel[0];\n" +
                "\n" +
                "    for (float f = 1.0; f <= radius; f++) {\n" +
                "        blr += texture2D(sampler1, uv + f * offset).rgb * kernel[int(abs(f))];\n" +
                "        blr += texture2D(sampler1, uv - f * offset).rgb * kernel[int(abs(f))];\n" +
                "    }\n" +
                "\n" +
                "    gl_FragColor = vec4(blr, direction.x == 0.0 ? alpha : 1.0);\n" +
                "}";
    }
}