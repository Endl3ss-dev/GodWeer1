package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class BlockEnergyGridShader implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D u_diffuse_sampler;\n" +
                "uniform sampler2D u_other_sampler;\n" +
                "uniform vec2 u_texel_size;\n" +
                "uniform float u_time;\n" +
                "uniform vec4 u_color;\n" +
                "\n" +
                "float hash(vec2 p) {\n" +
                "    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);\n" +
                "}\n" +
                "\n" +
                "float noise(vec2 p) {\n" +
                "    vec2 i = floor(p);\n" +
                "    vec2 f = fract(p);\n" +
                "    f = f * f * (3.0 - 2.0 * f);\n" +
                "    float a = hash(i);\n" +
                "    float b = hash(i + vec2(1.0, 0.0));\n" +
                "    float c = hash(i + vec2(0.0, 1.0));\n" +
                "    float d = hash(i + vec2(1.0, 1.0));\n" +
                "    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);\n" +
                "}\n" +
                "\n" +
                "float fbm(vec2 p) {\n" +
                "    float value = 0.0;\n" +
                "    float amplitude = 0.5;\n" +
                "    for (int i = 0; i < 5; i++) {\n" +
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
                "    float gridSize = 15.0;\n" +
                "    vec2 gridUV = uv * gridSize;\n" +
                "    vec2 gridID = floor(gridUV);\n" +
                "    vec2 gridFract = fract(gridUV);\n" +
                "    \n" +
                "    float lineWidth = 0.08;\n" +
                "    float gridX = smoothstep(lineWidth, lineWidth * 0.5, gridFract.x) + \n" +
                "                  smoothstep(1.0 - lineWidth, 1.0 - lineWidth * 0.5, gridFract.x);\n" +
                "    float gridY = smoothstep(lineWidth, lineWidth * 0.5, gridFract.y) + \n" +
                "                  smoothstep(1.0 - lineWidth, 1.0 - lineWidth * 0.5, gridFract.y);\n" +
                "    float grid = max(gridX, gridY);\n" +
                "    \n" +
                "    float cellNoise = hash(gridID + u_time * 0.1);\n" +
                "    float cellPulse = sin(cellNoise * 6.28 + u_time * 2.0) * 0.5 + 0.5;\n" +
                "    \n" +
                "    vec2 center = vec2(0.5, 0.5);\n" +
                "    float dist = length(uv - center);\n" +
                "    float wave = sin(dist * 20.0 - u_time * 3.0) * 0.5 + 0.5;\n" +
                "    \n" +
                "    float energyFlow = fbm(uv * 3.0 + vec2(u_time * 0.3, u_time * 0.2));\n" +
                "    energyFlow = pow(energyFlow, 2.0);\n" +
                "    \n" +
                "    vec3 gridColor = u_color.rgb * grid;\n" +
                "    gridColor *= (0.7 + cellPulse * 0.3);\n" +
                "    gridColor += u_color.rgb * wave * 0.4;\n" +
                "    gridColor += u_color.rgb * energyFlow * 0.5;\n" +
                "    \n" +
                "    vec3 glow = vec3(0.0);\n" +
                "    float totalWeight = 0.0;\n" +
                "    for (int i = -3; i <= 3; i++) {\n" +
                "        for (int j = -3; j <= 3; j++) {\n" +
                "            vec2 offset = vec2(float(i), float(j)) * u_texel_size * 1.5;\n" +
                "            float weight = exp(-length(offset) * 1.2);\n" +
                "            glow += texture2D(u_diffuse_sampler, uv + offset).rgb * weight;\n" +
                "            totalWeight += weight;\n" +
                "        }\n" +
                "    }\n" +
                "    glow /= totalWeight;\n" +
                "    \n" +
                "    vec3 finalColor = mix(color, glow, 0.3);\n" +
                "    finalColor += gridColor * 1.2;\n" +
                "    \n" +
                "    float scanline = sin(uv.y * 100.0 + u_time * 5.0) * 0.03 + 0.97;\n" +
                "    finalColor *= scanline;\n" +
                "    \n" +
                "    float edgeDist = min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y));\n" +
                "    float edgeGlow = smoothstep(0.0, 0.15, edgeDist);\n" +
                "    finalColor += u_color.rgb * (1.0 - edgeGlow) * 0.8;\n" +
                "    \n" +
                "    vec3 highlight = vec3(1.0, 1.0, 1.0) * grid * cellPulse * 0.3;\n" +
                "    finalColor += highlight;\n" +
                "\n" +
                "    gl_FragColor = vec4(finalColor, alpha);\n" +
                "}";
    }
}
