package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class SkyNebulaShader implements IShader {
    @Override
    public String shader() {
        return """
                #version 120
                
                uniform sampler2D u_diffuse_sampler;
                uniform vec2 u_texel_size;
                uniform float u_time;
                uniform float u_intensity;
                
                // Simplex noise function
                vec3 mod289(vec3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
                vec2 mod289(vec2 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
                vec3 permute(vec3 x) { return mod289(((x*34.0)+1.0)*x); }
                
                float snoise(vec2 v) {
                    const vec4 C = vec4(0.211324865405187, 0.366025403784439, -0.577350269189626, 0.024390243902439);
                    vec2 i  = floor(v + dot(v, C.yy));
                    vec2 x0 = v -   i + dot(i, C.xx);
                    vec2 i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
                    vec4 x12 = x0.xyxy + C.xxzz;
                    x12.xy -= i1;
                    i = mod289(i);
                    vec3 p = permute(permute(i.y + vec3(0.0, i1.y, 1.0)) + i.x + vec3(0.0, i1.x, 1.0));
                    vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
                    m = m*m;
                    m = m*m;
                    vec3 x = 2.0 * fract(p * C.www) - 1.0;
                    vec3 h = abs(x) - 0.5;
                    vec3 ox = floor(x + 0.5);
                    vec3 a0 = x - ox;
                    m *= 1.79284291400159 - 0.85373472095314 * (a0*a0 + h*h);
                    vec3 g;
                    g.x  = a0.x  * x0.x  + h.x  * x0.y;
                    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
                    return 130.0 * dot(m, g);
                }
                
                void main() {
                    vec2 uv = gl_TexCoord[0].st;
                    
                    vec2 p = (uv * 2.0 - 1.0);
                    p.x *= 1.0 / u_texel_size.y * u_texel_size.x;
                    
                    // Nebula colors
                    float n1 = snoise(p * 2.0 + u_time * 0.1);
                    float n2 = snoise(p * 3.0 - u_time * 0.15);
                    float n3 = snoise(p * 4.0 + u_time * 0.08);
                    
                    vec3 nebula1 = vec3(0.5, 0.2, 0.8) * (n1 * 0.5 + 0.5);
                    vec3 nebula2 = vec3(0.2, 0.5, 0.9) * (n2 * 0.5 + 0.5);
                    vec3 nebula3 = vec3(0.8, 0.3, 0.5) * (n3 * 0.5 + 0.5);
                    
                    vec3 nebulaColor = (nebula1 + nebula2 + nebula3) * 0.4;
                    
                    // Stars
                    float stars = pow(max(0.0, snoise(p * 50.0)), 20.0) * 2.0;
                    nebulaColor += vec3(stars);
                    
                    // Apply intensity
                    vec3 finalColor = nebulaColor * u_intensity;
                    
                    gl_FragColor = vec4(finalColor, u_intensity);
                }
                """;
    }
}
