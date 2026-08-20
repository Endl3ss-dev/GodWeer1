package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class TrailWavesShader implements IShader {
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
                "void main() {\n" +
                "    vec2 uv = gl_TexCoord[0].st;\n" +
                "    \n" +
                "    float alpha = texture2D(u_other_sampler, uv).a;\n" +
                "    if (alpha == 0.0) discard;\n" +
                "\n" +
                "    float progress = uv.x;\n" +
                "    float height = uv.y;\n" +
                "    \n" +
                "    float wavePos = progress - u_time * u_wave_speed * 0.3;\n" +
                "    \n" +
                "    float wave = exp(-pow(wavePos * 4.0, 2.0));\n" +
                "    \n" +
                "    float ripple1 = sin(wavePos * 20.0) * 0.5 + 0.5;\n" +
                "    float ripple2 = sin(wavePos * 35.0 + u_time * 2.0) * 0.5 + 0.5;\n" +
                "    float ripple3 = sin(wavePos * 50.0 - u_time * 3.0) * 0.5 + 0.5;\n" +
                "    \n" +
                "    float detailWave = (ripple1 * 0.5 + ripple2 * 0.3 + ripple3 * 0.2) * wave;\n" +
                "    \n" +
                "    float edgeFade = smoothstep(0.0, 0.2, height) * smoothstep(1.0, 0.8, height);\n" +
                "    \n" +
                "    float glow = wave * 1.5;\n" +
                "    float detail = detailWave * 0.8;\n" +
                "    \n" +
                "    float finalIntensity = (glow + detail) * edgeFade * u_wave_intensity;\n" +
                "    \n" +
                "    vec3 coreColor = u_color.rgb * 1.5;\n" +
                "    vec3 edgeColor = u_color.rgb * 0.6;\n" +
                "    \n" +
                "    vec3 waveColor = mix(edgeColor, coreColor, wave);\n" +
                "    \n" +
                "    float finalAlpha = finalIntensity * alpha;\n" +
                "    \n" +
                "    gl_FragColor = vec4(waveColor * finalIntensity, finalAlpha);\n" +
                "}";
    }
}
