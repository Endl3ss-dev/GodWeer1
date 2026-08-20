package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class SummerShader implements IShader {
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
                    
                    // Warm summer tint
                    vec3 summerTint = vec3(1.05, 1.0, 0.95); // Subtle warm
                    
                    // Gentle brightness boost
                    float brightness = 1.0 + u_intensity * 0.1;
                    
                    // Subtle saturation increase
                    float luminance = dot(color, vec3(0.299, 0.587, 0.114));
                    vec3 saturated = mix(vec3(luminance), color, 1.0 + u_intensity * 0.2);
                    
                    // Apply summer effect
                    vec3 result = saturated * summerTint * brightness;
                    
                    gl_FragColor = vec4(result, alpha);
                }
                """;
    }
}
