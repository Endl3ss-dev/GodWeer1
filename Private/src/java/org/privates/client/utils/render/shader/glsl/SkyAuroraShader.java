package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class SkyAuroraShader implements IShader {
    @Override
    public String shader() {
        return """
                #version 120
                
                uniform sampler2D u_diffuse_sampler;
                uniform vec2 u_texel_size;
                uniform float u_time;
                uniform float u_intensity;
                
                // Improved noise functions
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
                    float frequency = 1.0;
                    
                    for (int i = 0; i < 5; i++) {
                        value += amplitude * noise(p * frequency);
                        frequency *= 2.0;
                        amplitude *= 0.5;
                    }
                    return value;
                }
                
                void main() {
                    vec2 uv = gl_TexCoord[0].st;
                    
                    vec2 p = (uv * 2.0 - 1.0);
                    p.x *= 1.0 / u_texel_size.y * u_texel_size.x;
                    
                    // Horizontal aurora bands - like in the image
                    float band1 = fbm(vec2(p.x * 2.0 + u_time * 0.1, p.y * 4.0 + u_time * 0.05));
                    float band2 = fbm(vec2(p.x * 1.5 - u_time * 0.08, p.y * 3.0 - u_time * 0.06));
                    float band3 = fbm(vec2(p.x * 2.5 + u_time * 0.12, p.y * 5.0 + u_time * 0.04));
                    
                    // Combine bands
                    float aurora = (band1 * 0.4 + band2 * 0.35 + band3 * 0.25);
                    
                    // Add horizontal wave distortion
                    float wave = sin(p.y * 8.0 + u_time * 0.3 + band1 * 3.0) * 0.5 + 0.5;
                    aurora = aurora * 0.7 + wave * 0.3;
                    
                    // Vertical position gradient - stronger at horizon (middle)
                    float horizonGradient = 1.0 - abs(p.y * 1.2);
                    horizonGradient = smoothstep(0.2, 0.8, horizonGradient);
                    aurora *= horizonGradient;
                    
                    // Aurora colors - green and cyan like in image (BOOSTED)
                    vec3 color1 = vec3(0.0, 2.5, 1.5);      // Very bright green
                    vec3 color2 = vec3(0.0, 2.0, 2.0);      // Bright cyan-green
                    vec3 color3 = vec3(0.3, 1.8, 2.2);      // Bright light blue
                    vec3 color4 = vec3(0.0, 1.5, 1.8);      // Bright dark cyan
                    
                    // Mix colors based on bands
                    vec3 auroraColor = mix(color1, color2, band1);
                    auroraColor = mix(auroraColor, color3, band2 * 0.6);
                    auroraColor = mix(auroraColor, color4, band3 * 0.4);
                    
                    // Add depth variation - darker at edges
                    float depth = 1.0 - abs(p.x) * 0.2;
                    auroraColor *= depth;
                    
                    // Brightness pulsing (STRONGER)
                    float pulse = 0.8 + 0.4 * sin(u_time * 0.5 + aurora * 2.0);
                    auroraColor *= pulse;
                    
                    // Add flowing shimmer
                    float shimmer = sin(p.x * 15.0 + u_time * 2.0 + p.y * 10.0) * 0.15 + 0.95;
                    auroraColor *= shimmer;
                    
                    // Stronger glow effect
                    float glow = pow(aurora, 0.6) * 2.0;
                    auroraColor *= glow;
                    
                    // Add subtle cloud-like texture
                    float clouds = fbm(vec2(p.x * 3.0 + u_time * 0.02, p.y * 2.0)) * 0.4;
                    auroraColor += vec3(clouds * 0.3, clouds * 0.4, clouds * 0.3);
                    
                    // Apply intensity (BOOSTED)
                    vec3 finalColor = auroraColor * aurora * u_intensity * 2.5;
                    
                    // Stronger alpha for blending
                    float alpha = aurora * u_intensity * horizonGradient * 1.5;
                    
                    gl_FragColor = vec4(finalColor, alpha);
                }
                """;
    }
}
