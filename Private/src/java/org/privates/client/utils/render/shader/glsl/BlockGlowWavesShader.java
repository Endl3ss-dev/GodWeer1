package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class BlockGlowWavesShader implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D u_diffuse_sampler;\n" +
                "uniform sampler2D u_other_sampler;\n" +
                "uniform vec2 u_texel_size;\n" +
                "uniform float u_time;\n" +
                "uniform vec4 u_color;\n" +
                "uniform float u_wave_speed;\n" +
                "uniform float u_wave_intensity;\n" +
                "\n" +
                "#define power 1.0\n" +
                "#define zoomOut 3.0\n" +
                "#define iter 10.0\n" +
                "#define glow 0.5\n" +
                "#define distortScale 0.8\n" +
                "#define distortPower 0.45\n" +
                "#define Speed 1.5\n" +
                "#define WaveSpeed 2.0\n" +
                "#define Brightness 0.3\n" +
                "\n" +
                "void main() {\n" +
                "    vec2 fragCoord = gl_TexCoord[0].st;\n" +
                "    fragCoord.y = 1.0 - fragCoord.y;\n" +
                "\n" +
                "    float alpha = texture2D(u_other_sampler, fragCoord).a;\n" +
                "    if (alpha == 0.0) discard;\n" +
                "\n" +
                "    vec2 uv = fragCoord;\n" +
                "    vec2 XYScale = vec2(1.0, 1.0);\n" +
                "    vec2 XYMove = vec2(0.0, 0.0);\n" +
                "    \n" +
                "    uv *= zoomOut;\n" +
                "    uv.xy = uv.xy * XYScale;\n" +
                "    uv.xy = uv.xy + XYMove;\n" +
                "    \n" +
                "    vec3 finalCol = vec3(0.0, 0.0, 0.0);\n" +
                "    float halfDistort = distortScale / 0.5;\n" +
                "    float distortsc2 = distortScale / distortScale + halfDistort;\n" +
                "    \n" +
                "    float adjustedTime = u_time * u_wave_speed;\n" +
                "    \n" +
                "    for(float i = 1.0; i < iter; i++) {\n" +
                "        uv.x += distortPower / i * sin(i * distortScale * uv.y - adjustedTime * Speed);\n" +
                "        uv.y += distortPower / i * sin(i * distortsc2 * uv.x + adjustedTime * Speed);\n" +
                "    }\n" +
                "    \n" +
                "    vec3 col = vec3(vec3(glow, glow, glow) / sin(adjustedTime * WaveSpeed - length(uv.yx) - uv.y));\n" +
                "    finalCol = vec3(col * col);\n" +
                "    \n" +
                "    vec3 Color = vec3(0.6, 0.2, 1.0) * Brightness * u_wave_intensity;\n" +
                "    \n" +
                "    vec3 result = finalCol.rgb * Color * power;\n" +
                "    \n" +
                "    vec3 glow_effect = vec3(0.0);\n" +
                "    float totalWeight = 0.0;\n" +
                "    for (int i = -2; i <= 2; i++) {\n" +
                "        for (int j = -2; j <= 2; j++) {\n" +
                "            vec2 offset = vec2(float(i), float(j)) * u_texel_size * 1.5;\n" +
                "            float weight = exp(-length(offset) * 1.0);\n" +
                "            glow_effect += texture2D(u_diffuse_sampler, fragCoord + offset).rgb * weight;\n" +
                "            totalWeight += weight;\n" +
                "        }\n" +
                "    }\n" +
                "    glow_effect /= totalWeight;\n" +
                "    \n" +
                "    result = mix(glow_effect, result, 0.8);\n" +
                "    \n" +
                "    gl_FragColor = vec4(result, alpha);\n" +
                "}";
    }
}
