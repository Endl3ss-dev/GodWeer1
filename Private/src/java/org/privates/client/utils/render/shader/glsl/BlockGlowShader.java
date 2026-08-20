package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class BlockGlowShader implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D u_diffuse_sampler;\n" +
                "uniform sampler2D u_other_sampler;\n" +
                "uniform vec2 u_texel_size;\n" +
                "uniform float u_time;\n" +
                "uniform float u_glow_intensity;\n" +
                "uniform vec4 u_color;\n" +
                "\n" +
                "float noise(vec2 p) {\n" +
                "    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);\n" +
                "}\n" +
                "\n" +
                "float fbm(vec2 p) {\n" +
                "    float value = 0.0;\n" +
                "    float amplitude = 0.5;\n" +
                "    for (int i = 0; i < 4; i++) {\n" +
                "        value += amplitude * noise(p);\n" +
                "        p *= 2.0;\n" +
                "        amplitude *= 0.5;\n" +
                "    }\n" +
                "    return value;\n" +
                "}\n" +
                "\n" +
                "void main() {\n" +
                "    vec2 uv = gl_TexCoord[0].st;\n" +
                "    uv.y = 1.0 - uv.y;\n" +
                "\n" +
                "    float alpha = texture2D(u_other_sampler, uv).a;\n" +
                "    if (alpha == 0.0) discard;\n" +
                "\n" +
                "    vec3 color = texture2D(u_diffuse_sampler, uv).rgb;\n" +
                "\n" +
                "    vec2 center = vec2(0.5, 0.5);\n" +
                "    float dist = length(uv - center);\n" +
                "    \n" +
                "    float wave1 = sin(dist * 20.0 - u_time * 4.0) * 0.5 + 0.5;\n" +
                "    float wave2 = sin(dist * 15.0 + u_time * 3.0) * 0.5 + 0.5;\n" +
                "    float wave3 = sin(dist * 25.0 - u_time * 5.0) * 0.5 + 0.5;\n" +
                "    \n" +
                "    float waves = (wave1 + wave2 + wave3) / 3.0;\n" +
                "    \n" +
                "    float angle = atan(uv.y - center.y, uv.x - center.x);\n" +
                "    float spiral = sin(angle * 3.0 + dist * 10.0 - u_time * 2.0) * 0.5 + 0.5;\n" +
                "    \n" +
                "    float noiseVal = fbm(uv * 5.0 + u_time * 0.5);\n" +
                "    \n" +
                "    float energy = waves * 0.4 + spiral * 0.3 + noiseVal * 0.3;\n" +
                "    \n" +
                "    vec3 glow = vec3(0.0);\n" +
                "    float totalWeight = 0.0;\n" +
                "    for (int i = -3; i <= 3; i++) {\n" +
                "        for (int j = -3; j <= 3; j++) {\n" +
                "            vec2 offset = vec2(float(i), float(j)) * u_texel_size * 2.0;\n" +
                "            float weight = exp(-length(offset) * 1.5);\n" +
                "            glow += texture2D(u_diffuse_sampler, uv + offset).rgb * weight;\n" +
                "            totalWeight += weight;\n" +
                "        }\n" +
                "    }\n" +
                "    glow /= totalWeight;\n" +
                "\n" +
                "    float pulse = sin(u_time * 2.5) * 0.4 + 0.6;\n" +
                "    vec3 energyColor = u_color.rgb * energy * pulse * u_glow_intensity;\n" +
                "    \n" +
                "    color = mix(color, glow, 0.3);\n" +
                "    color += energyColor * 0.8;\n" +
                "    \n" +
                "    float edgeDist = min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y));\n" +
                "    float edgeGlow = smoothstep(0.0, 0.15, edgeDist);\n" +
                "    color += energyColor * (1.0 - edgeGlow) * 1.2;\n" +
                "    \n" +
                "    vec3 highlight = vec3(1.0) * energy * pulse * 0.3;\n" +
                "    color += highlight;\n" +
                "\n" +
                "    gl_FragColor = vec4(color, alpha);\n" +
                "}";
    }
}
