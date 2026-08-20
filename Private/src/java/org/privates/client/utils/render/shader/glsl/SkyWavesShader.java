package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class SkyWavesShader implements IShader {
    @Override
    public String shader() {
        return """
                #version 120
                
                uniform sampler2D u_diffuse_sampler;
                uniform vec2 u_texel_size;
                uniform float u_time;
                uniform float u_intensity;
                
                void main() {
                    vec2 uv = gl_TexCoord[0].st;
                    
                    vec2 p = (uv * 2.0 - 1.0);
                    p.x *= 1.0 / u_texel_size.y * u_texel_size.x;
                    
                    // Wave patterns
                    float wave1 = sin(p.x * 5.0 + u_time * 0.5) * cos(p.y * 5.0 + u_time * 0.3);
                    float wave2 = sin(p.x * 3.0 - u_time * 0.4) * cos(p.y * 3.0 - u_time * 0.6);
                    float wave3 = sin(length(p) * 8.0 - u_time * 0.8);
                    
                    float waves = (wave1 + wave2 + wave3) * 0.3;
                    
                    // Color based on waves
                    vec3 color1 = vec3(0.2, 0.4, 0.8);
                    vec3 color2 = vec3(0.6, 0.2, 0.9);
                    vec3 waveColor = mix(color1, color2, waves * 0.5 + 0.5);
                    
                    // Add glow
                    float glow = exp(-length(p) * 0.5) * 0.3;
                    waveColor += vec3(glow);
                    
                    vec3 finalColor = waveColor * u_intensity;
                    
                    gl_FragColor = vec4(finalColor, u_intensity);
                }
                """;
    }
}
