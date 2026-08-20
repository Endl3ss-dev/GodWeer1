package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class WinterShader implements IShader {
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
                    
                    // Cool winter tint
                    vec3 winterTint = vec3(0.95, 0.98, 1.05); // Subtle cool blue
                    
                    // Subtle desaturation
                    float luminance = dot(color, vec3(0.299, 0.587, 0.114));
                    vec3 desaturated = mix(vec3(luminance), color, 1.0 - u_intensity * 0.15);
                    
                    // Apply winter effect
                    vec3 result = desaturated * winterTint;
                    
                    gl_FragColor = vec4(result, alpha);
                }
                """;
    }
}
