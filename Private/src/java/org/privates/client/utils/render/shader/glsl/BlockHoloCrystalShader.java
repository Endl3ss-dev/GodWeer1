package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class BlockHoloCrystalShader implements IShader {
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
                "vec2 hash2(vec2 p) {\n" +
                "    return fract(sin(vec2(dot(p, vec2(127.1, 311.7)), dot(p, vec2(269.5, 183.3)))) * 43758.5453);\n" +
                "}\n" +
                "\n" +
                "float voronoi(vec2 uv, float scale) {\n" +
                "    vec2 i = floor(uv * scale);\n" +
                "    vec2 f = fract(uv * scale);\n" +
                "    \n" +
                "    float minDist = 10.0;\n" +
                "    vec2 minPoint = vec2(0.0);\n" +
                "    \n" +
                "    for (int y = -1; y <= 1; y++) {\n" +
                "        for (int x = -1; x <= 1; x++) {\n" +
                "            vec2 neighbor = vec2(float(x), float(y));\n" +
                "            vec2 point = hash2(i + neighbor);\n" +
                "            point = 0.5 + 0.5 * sin(u_time * 0.5 + 6.28 * point);\n" +
                "            vec2 diff = neighbor + point - f;\n" +
                "            float dist = length(diff);\n" +
                "            \n" +
                "            if (dist < minDist) {\n" +
                "                minDist = dist;\n" +
                "                minPoint = i + neighbor + point;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    return minDist;\n" +
                "}\n" +
                "\n" +
                "vec3 hsv2rgb(vec3 c) {\n" +
                "    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);\n" +
                "    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);\n" +
                "    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);\n" +
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
                "    float voronoiPattern = voronoi(uv, 12.0);\n" +
                "    float voronoiPattern2 = voronoi(uv + vec2(u_time * 0.1), 8.0);\n" +
                "    \n" +
                "    float crystalEdge = smoothstep(0.05, 0.15, voronoiPattern);\n" +
                "    float crystalFacet = step(0.02, voronoiPattern);\n" +
                "    \n" +
                "    float hue = fract(u_time * 0.15 + dist * 0.5 + voronoiPattern * 2.0);\n" +
                "    vec3 rainbow = hsv2rgb(vec3(hue, 0.7, 1.0));\n" +
                "    \n" +
                "    vec3 baseColor = mix(u_color.rgb, rainbow, 0.6);\n" +
                "    \n" +
                "    float refraction = sin(voronoiPattern * 30.0 + u_time * 2.0) * 0.5 + 0.5;\n" +
                "    vec2 refractOffset = vec2(\n" +
                "        sin(angle * 3.0 + u_time) * 0.01,\n" +
                "        cos(angle * 3.0 + u_time) * 0.01\n" +
                "    ) * refraction;\n" +
                "    \n" +
                "    vec3 refractedColor = texture2D(u_diffuse_sampler, uv + refractOffset).rgb;\n" +
                "    \n" +
                "    vec3 glow = vec3(0.0);\n" +
                "    float totalWeight = 0.0;\n" +
                "    for (int i = -2; i <= 2; i++) {\n" +
                "        for (int j = -2; j <= 2; j++) {\n" +
                "            vec2 offset = vec2(float(i), float(j)) * u_texel_size * 2.0;\n" +
                "            float weight = exp(-length(offset) * 0.8);\n" +
                "            glow += texture2D(u_diffuse_sampler, uv + offset).rgb * weight;\n" +
                "            totalWeight += weight;\n" +
                "        }\n" +
                "    }\n" +
                "    glow /= totalWeight;\n" +
                "    \n" +
                "    vec3 finalColor = mix(refractedColor, glow, 0.4);\n" +
                "    finalColor = mix(finalColor, baseColor, crystalFacet * 0.7);\n" +
                "    \n" +
                "    float shimmer = sin(uv.x * 30.0 + u_time * 4.0) * sin(uv.y * 30.0 - u_time * 3.0);\n" +
                "    shimmer = shimmer * 0.5 + 0.5;\n" +
                "    shimmer = pow(shimmer, 3.0);\n" +
                "    finalColor += vec3(1.0) * shimmer * crystalEdge * 0.4;\n" +
                "    \n" +
                "    float fresnel = pow(1.0 - crystalEdge, 2.0);\n" +
                "    finalColor += baseColor * fresnel * 0.5;\n" +
                "    \n" +
                "    float wave1 = sin(dist * 15.0 - u_time * 3.0) * 0.5 + 0.5;\n" +
                "    float wave2 = sin(angle * 8.0 + u_time * 2.0) * 0.5 + 0.5;\n" +
                "    float combinedWave = (wave1 + wave2) * 0.5;\n" +
                "    finalColor += rainbow * combinedWave * 0.3 * crystalEdge;\n" +
                "    \n" +
                "    float hologram = sin(uv.y * 80.0 + u_time * 10.0) * 0.05 + 0.95;\n" +
                "    finalColor *= hologram;\n" +
                "    \n" +
                "    float glitch = step(0.98, hash(vec2(floor(uv.y * 50.0), floor(u_time * 5.0))));\n" +
                "    finalColor += vec3(glitch) * 0.3;\n" +
                "    \n" +
                "    float edgeDist = min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y));\n" +
                "    float edgeGlow = smoothstep(0.0, 0.12, edgeDist);\n" +
                "    finalColor += rainbow * (1.0 - edgeGlow) * 0.7;\n" +
                "    \n" +
                "    float pulse = sin(u_time * 2.0) * 0.2 + 0.8;\n" +
                "    finalColor *= pulse;\n" +
                "\n" +
                "    gl_FragColor = vec4(finalColor, alpha);\n" +
                "}";
    }
}
