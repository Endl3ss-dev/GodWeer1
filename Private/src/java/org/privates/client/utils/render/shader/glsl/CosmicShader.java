package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class CosmicShader implements IShader {
    @Override
    public String shader() {
        return """
                #version 120
                
                uniform sampler2D u_diffuse_sampler;
                uniform sampler2D u_other_sampler;
                uniform vec2 u_texel_size;
                uniform float u_time;
                uniform float u_intensity;
                
                void main() {
                    vec2 uv = gl_TexCoord[0].st;
                    uv.y = 1.0 - uv.y;
                
                    float alpha = texture2D(u_other_sampler, uv).a;
                    if (alpha < 0.01) discard;
                
                    vec3 color = texture2D(u_diffuse_sampler, uv).rgb;
                    
                    // Subtle cosmic tint
                    vec3 cosmicTint = vec3(0.95, 0.9, 1.05); // Very subtle purple-blue
                    
                    // Gentle pulsing effect
                    float pulse = sin(u_time * 0.5) * 0.5 + 0.5;
                    pulse = pulse * 0.05; // Very subtle
                    
                    // Apply subtle tint
                    vec3 result = color * mix(vec3(1.0), cosmicTint, u_intensity * 0.3);
                    
                    // Add gentle brightness variation
                    result *= (1.0 + pulse * u_intensity);
                    
                    gl_FragColor = vec4(result, alpha);
                }
                """;
    }
}
