package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class GlowEnhancementGlsl implements IShader {
    @Override
    public String shader() {
        return """
                #version 120
                
                uniform sampler2D u_diffuse_sampler;
                uniform sampler2D u_other_sampler;
                uniform vec2 u_texel_size;
                uniform float u_glow_intensity;
                uniform float u_bloom_threshold;
                uniform float u_shadow_depth;
                
                vec3 rgb2hsv(vec3 c) {
                    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
                    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
                    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
                    
                    float d = q.x - min(q.w, q.y);
                    float e = 1.0e-10;
                    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
                }
                
                vec3 hsv2rgb(vec3 c) {
                    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
                    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
                    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
                }
                
                void main() {
                    vec2 uv = gl_TexCoord[0].st;
                    uv.y = 1.0 - uv.y;
                
                    float alpha = texture2D(u_other_sampler, uv).a;
                    if (alpha < 0.01) discard;
                
                    vec3 color = texture2D(u_diffuse_sampler, uv).rgb;
                    
                    float luminance = dot(color, vec3(0.299, 0.587, 0.114));
                    
                    vec3 hsv = rgb2hsv(color);
                    
                    float isBright = step(u_bloom_threshold, luminance);
                    float isDark = step(luminance, 1.0 - u_bloom_threshold);
                    
                    if (isBright > 0.5) {
                        hsv.y *= (1.0 + u_glow_intensity * 0.5);
                        hsv.z *= (1.0 + u_glow_intensity);
                        
                        vec3 enhanced = hsv2rgb(hsv);
                        
                        vec3 bloom = vec3(0.0);
                        float bloomRadius = 3.0;
                        float samples = 0.0;
                        
                        for (float x = -bloomRadius; x <= bloomRadius; x += 1.0) {
                            for (float y = -bloomRadius; y <= bloomRadius; y += 1.0) {
                                vec2 offset = vec2(x, y) * u_texel_size * 2.0;
                                float dist = length(vec2(x, y));
                                if (dist <= bloomRadius) {
                                    float weight = 1.0 - (dist / bloomRadius);
                                    bloom += texture2D(u_diffuse_sampler, uv + offset).rgb * weight;
                                    samples += weight;
                                }
                            }
                        }
                        bloom /= samples;
                        
                        float bloomStrength = (luminance - u_bloom_threshold) / (1.0 - u_bloom_threshold);
                        bloomStrength = pow(bloomStrength, 2.0) * u_glow_intensity * 0.3;
                        
                        color = mix(enhanced, enhanced + bloom * bloomStrength, bloomStrength);
                    } else if (isDark > 0.5) {
                        float shadowFactor = 1.0 - (luminance / (1.0 - u_bloom_threshold));
                        shadowFactor = pow(shadowFactor, 1.5);
                        
                        hsv.z *= (1.0 - shadowFactor * u_shadow_depth * 0.4);
                        hsv.y *= (1.0 + shadowFactor * 0.2);
                        
                        color = hsv2rgb(hsv);
                    } else {
                        hsv.y *= (1.0 + u_glow_intensity * 0.15);
                        color = hsv2rgb(hsv);
                    }
                    
                    color = clamp(color, 0.0, 1.0);
                    
                    gl_FragColor = vec4(color, alpha);
                }
                """;
    }
}
