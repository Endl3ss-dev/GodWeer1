package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class CrystalFacetsGlsl implements IShader {
    @Override
    public String shader() {
        return "#version 120\n" +
                "\n" +
                "uniform sampler2D u_diffuse_sampler;\n" +
                "uniform sampler2D u_other_sampler;\n" +
                "uniform vec2 u_texel_size;\n" +
                "uniform float u_time;\n" +
                "uniform float u_facet_size;\n" +
                "\n" +
                "float rand(vec2 co) {\n" +
                "    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);\n" +
                "}\n" +
                "\n" +
                "vec2 voronoi(vec2 uv, float scale) {\n" +
                "    vec2 i = floor(uv * scale);\n" +
                "    vec2 f = fract(uv * scale);\n" +
                "    \n" +
                "    float minDist = 10.0;\n" +
                "    vec2 minPoint = vec2(0.0);\n" +
                "    \n" +
                "    for (int y = -1; y <= 1; y++) {\n" +
                "        for (int x = -1; x <= 1; x++) {\n" +
                "            vec2 neighbor = vec2(float(x), float(y));\n" +
                "            vec2 point = vec2(\n" +
                "                rand(i + neighbor),\n" +
                "                rand(i + neighbor + vec2(100.0, 200.0))\n" +
                "            );\n" +
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
                "    return minPoint / scale;\n" +
                "}\n" +
                "\n" +
                "void main() {\n" +
                "    vec2 uv = gl_TexCoord[0].st;\n" +
                "    uv.y = 1.0 - uv.y;\n" +
                "\n" +
                "    float alpha = texture2D(u_other_sampler, uv).a;\n" +
                "    if (alpha == 0.0) discard;\n" +
                "\n" +
                "    vec2 facetUV = voronoi(uv, u_facet_size);\n" +
                "    vec3 color = texture2D(u_diffuse_sampler, facetUV).rgb;\n" +
                "\n" +
                "    vec3 blur = vec3(0.0);\n" +
                "    for (int i = -1; i <= 1; i++) {\n" +
                "        for (int j = -1; j <= 1; j++) {\n" +
                "            vec2 offset = vec2(float(i), float(j)) * u_texel_size * 2.0;\n" +
                "            blur += texture2D(u_diffuse_sampler, facetUV + offset).rgb;\n" +
                "        }\n" +
                "    }\n" +
                "    blur /= 9.0;\n" +
                "\n" +
                "    color = mix(blur, color, 0.6);\n" +
                "    color += vec3(0.1) * (1.0 - length(uv - facetUV) * 10.0);\n" +
                "\n" +
                "    gl_FragColor = vec4(color, alpha);\n" +
                "}";
    }
}