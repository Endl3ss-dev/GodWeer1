package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class BlockRainbowShader implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D u_diffuse_sampler;\n" +
                "uniform sampler2D u_other_sampler;\n" +
                "uniform vec2 u_texel_size;\n" +
                "uniform float u_time;\n" +
                "uniform float u_rainbow_speed;\n" +
                "uniform float u_rainbow_saturation;\n" +
                "\n" +
                "vec3 hsv2rgb(vec3 c) {\n" +
                "    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);\n" +
                "    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);\n" +
                "    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);\n" +
                "}\n" +
                "\n" +
                "float noise(vec2 p) {\n" +
                "    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);\n" +
                "}\n" +
                "\n" +
                "float voronoi(vec2 uv, float scale) {\n" +
                "    vec2 i = floor(uv * scale);\n" +
                "    vec2 f = fract(uv * scale);\n" +
                "    \n" +
                "    float minDist = 10.0;\n" +
                "    \n" +
                "    for (int y = -1; y <= 1; y++) {\n" +
                "        for (int x = -1; x <= 1; x++) {\n" +
                "            vec2 neighbor = vec2(float(x), float(y));\n" +
                "            vec2 point = vec2(\n" +
                "                noise(i + neighbor),\n" +
                "                noise(i + neighbor + vec2(100.0, 200.0))\n" +
                "            );\n" +
                "            point = 0.5 + 0.5 * sin(u_time * 0.8 + 6.28 * point);\n" +
                "            vec2 diff = neighbor + point - f;\n" +
                "            float dist = length(diff);\n" +
                "            minDist = min(minDist, dist);\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    return minDist;\n" +
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
                "    float angle = atan(uv.y - center.y, uv.x - center.x);\n" +
                "    \n" +
                "    float voronoiPattern = voronoi(uv, 8.0);\n" +
                "    \n" +
                "    float hue = fract(u_time * u_rainbow_speed + dist * 0.8 + angle * 0.15 + voronoiPattern * 0.3);\n" +
                "    vec3 rainbow = hsv2rgb(vec3(hue, u_rainbow_saturation, 1.0));\n" +
                "\n" +
                "    vec3 blur = vec3(0.0);\n" +
                "    float totalWeight = 0.0;\n" +
                "    for (int i = -2; i <= 2; i++) {\n" +
                "        for (int j = -2; j <= 2; j++) {\n" +
                "            vec2 offset = vec2(float(i), float(j)) * u_texel_size;\n" +
                "            float weight = exp(-length(offset) * 1.0);\n" +
                "            float offsetDist = length((uv + offset) - center);\n" +
                "            float offsetAngle = atan((uv.y + offset.y) - center.y, (uv.x + offset.x) - center.x);\n" +
                "            float offsetHue = fract(u_time * u_rainbow_speed + offsetDist * 0.8 + offsetAngle * 0.15);\n" +
                "            blur += hsv2rgb(vec3(offsetHue, u_rainbow_saturation, 1.0)) * weight;\n" +
                "            totalWeight += weight;\n" +
                "        }\n" +
                "    }\n" +
                "    blur /= totalWeight;\n" +
                "\n" +
                "    vec3 finalColor = mix(color, blur, 0.4);\n" +
                "    finalColor = mix(finalColor, rainbow, 0.75);\n" +
                "\n" +
                "    float wave1 = sin(dist * 15.0 - u_time * 4.0) * 0.5 + 0.5;\n" +
                "    float wave2 = sin(angle * 5.0 + u_time * 3.0) * 0.5 + 0.5;\n" +
                "    float combinedWave = (wave1 + wave2) * 0.5;\n" +
                "    \n" +
                "    float crystalEdge = smoothstep(0.3, 0.7, voronoiPattern);\n" +
                "    finalColor += rainbow * combinedWave * 0.4 * crystalEdge;\n" +
                "    \n" +
                "    float shimmer = sin(uv.x * 20.0 + u_time * 6.0) * sin(uv.y * 20.0 + u_time * 5.0);\n" +
                "    shimmer = shimmer * 0.5 + 0.5;\n" +
                "    finalColor += vec3(1.0) * shimmer * 0.15 * crystalEdge;\n" +
                "    \n" +
                "    float edgeDist = min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y));\n" +
                "    float edgeGlow = smoothstep(0.0, 0.12, edgeDist);\n" +
                "    finalColor += rainbow * (1.0 - edgeGlow) * 0.6;\n" +
                "\n" +
                "    gl_FragColor = vec4(finalColor, alpha);\n" +
                "}";
    }
}
