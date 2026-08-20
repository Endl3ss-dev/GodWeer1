package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class SmoothMotionGlsl implements IShader {
    @Override
    public String shader() {
        return """
                #version 120

                uniform sampler2D u_diffuse_sampler;
                uniform sampler2D u_other_sampler;
                uniform vec2 u_texel_size;
                uniform float u_smoothness;
                uniform float u_sharpness;
                uniform float u_time;

                vec3 sampleBlur(vec2 uv, vec2 radius) {
                    vec3 c = texture2D(u_diffuse_sampler, uv).rgb * 0.36;
                    c += texture2D(u_diffuse_sampler, uv + vec2(radius.x, 0.0)).rgb * 0.12;
                    c += texture2D(u_diffuse_sampler, uv - vec2(radius.x, 0.0)).rgb * 0.12;
                    c += texture2D(u_diffuse_sampler, uv + vec2(0.0, radius.y)).rgb * 0.12;
                    c += texture2D(u_diffuse_sampler, uv - vec2(0.0, radius.y)).rgb * 0.12;
                    c += texture2D(u_diffuse_sampler, uv + vec2(radius.x, radius.y)).rgb * 0.07;
                    c += texture2D(u_diffuse_sampler, uv + vec2(-radius.x, radius.y)).rgb * 0.07;
                    c += texture2D(u_diffuse_sampler, uv + vec2(radius.x, -radius.y)).rgb * 0.07;
                    c += texture2D(u_diffuse_sampler, uv + vec2(-radius.x, -radius.y)).rgb * 0.07;
                    return c;
                }

                void main() {
                    vec2 uv = gl_TexCoord[0].st;
                    uv.y = 1.0 - uv.y;

                    float alpha = texture2D(u_other_sampler, uv).a;
                    if (alpha < 0.01) discard;

                    vec3 base = texture2D(u_diffuse_sampler, uv).rgb;

                    // Subtle time jitter makes transitions feel less static.
                    float jitter = sin(u_time * 1.2 + uv.x * 8.0 + uv.y * 6.0) * 0.0006;
                    vec2 radius = u_texel_size * (1.0 + u_smoothness * 3.0 + jitter);

                    vec3 blur = sampleBlur(uv, radius);
                    vec3 smooth = mix(base, blur, clamp(u_smoothness, 0.0, 1.0));

                    // Lightweight unsharp mask to keep readability after smoothing.
                    vec3 edge = base - blur;
                    vec3 result = smooth + edge * (0.55 * clamp(u_sharpness, 0.0, 1.0));

                    gl_FragColor = vec4(clamp(result, 0.0, 1.0), alpha);
                }
                """;
    }
}

