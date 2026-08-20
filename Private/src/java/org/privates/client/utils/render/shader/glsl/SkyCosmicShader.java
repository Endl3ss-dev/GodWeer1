package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class SkyCosmicShader implements IShader {
    @Override
    public String shader() {
        return """
                #version 120
                
                uniform sampler2D u_diffuse_sampler;
                uniform vec2 u_texel_size;
                uniform float u_time;
                uniform float u_intensity;
                
                float hash(vec2 p) {
                    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
                }
                
                float noise(vec2 p) {
                    vec2 i = floor(p);
                    vec2 f = fract(p);
                    f = f * f * (3.0 - 2.0 * f);
                    float a = hash(i);
                    float b = hash(i + vec2(1.0, 0.0));
                    float c = hash(i + vec2(0.0, 1.0));
                    float d = hash(i + vec2(1.0, 1.0));
                    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
                }
                
                float fbm(vec2 p) {
                    float value = 0.0;
                    float amplitude = 0.5;
                    for (int i = 0; i < 5; i++) {
                        value += amplitude * noise(p);
                        p *= 2.0;
                        amplitude *= 0.5;
                    }
                    return value;
                }
                
                void main() {
                    vec2 uv = gl_TexCoord[0].st;
                    
                    vec2 p = (uv * 2.0 - 1.0);
                    p.x *= 1.0 / u_texel_size.y * u_texel_size.x;
                    
                    // Cosmic background
                    float cosmic = fbm(p * 3.0 + u_time * 0.05);
                    
                    // Color gradient
                    vec3 color1 = vec3(0.1, 0.0, 0.3);
                    vec3 color2 = vec3(0.5, 0.0, 0.8);
                    vec3 color3 = vec3(0.0, 0.3, 0.6);
                    
                    vec3 cosmicColor = mix(color1, color2, cosmic);
                    cosmicColor = mix(cosmicColor, color3, sin(u_time * 0.2) * 0.5 + 0.5);
                    
                    // Stars
                    float stars = 0.0;
                    for (int i = 0; i < 3; i++) {
                        float fi = float(i);
                        vec2 starPos = p * (10.0 + fi * 5.0) + u_time * (0.1 + fi * 0.05);
                        float star = pow(max(0.0, 1.0 - length(fract(starPos) - 0.5) * 4.0), 10.0);
                        stars += star * (0.5 + 0.5 * sin(u_time * 2.0 + fi));
                    }
                    
                    cosmicColor += vec3(stars) * 0.5;
                    
                    // Pulsing effect
                    cosmicColor *= 0.8 + 0.2 * sin(u_time * 0.5);
                    
                    vec3 finalColor = cosmicColor * u_intensity;
                    
                    gl_FragColor = vec4(finalColor, u_intensity);
                }
                """;
    }
}
