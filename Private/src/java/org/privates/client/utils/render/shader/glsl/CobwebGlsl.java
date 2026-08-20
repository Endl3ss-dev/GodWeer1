package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class CobwebGlsl implements IShader {
    @Override
    public String shader() {
        return """
                #version 120
                
                uniform sampler2D u_diffuse_sampler;
                uniform sampler2D u_other_sampler;
                uniform vec2      u_texel_size;
                uniform float     u_time;
                uniform float     u_speed;
                uniform float     u_intensity;
                
                float sq = 0.7071067811865476;
                
                float c(vec3 p) {
                    vec3 q = abs(mod(p + vec3(cos(p.z * 0.5), cos(p.x * 0.5), cos(p.y * 0.5)), 11.0) - 1.0);
                    float a = q.x + q.y + q.z - min(min(q.x, q.y), q.z) - max(max(q.x, q.y), q.z);
                    q = vec3(p.x + p.y, p.y + p.z, p.z + p.x) * sq;
                    q = abs(mod(q, 2.0) - 1.0);
                    float b = q.x + q.y + q.z - min(min(q.x, q.y), q.z) - max(max(q.x, q.y), q.z);
                    return min(a, b);
                }
                
                vec3 calcNormal(vec3 p) {
                    float o = c(p);
                    float ex = c(p - vec3(0.001, 0.0, 0.0));
                    float ey = c(p - vec3(0.0, 0.001, 0.0));
                    float ez = c(p - vec3(0.0, 0.0, 0.001));
                    return normalize(vec3(o - ex, o - ey, o - ez));
                }
                
                void main() {
                    vec2 uv = gl_TexCoord[0].st;
                    uv.y = 1.0 - uv.y;
                
                    float maskAlpha = texture2D(u_other_sampler, uv).a;
                    if (maskAlpha < 0.01) discard;
                
                    vec3 bgColor = texture2D(u_diffuse_sampler, uv).rgb;
                
                    float aspect = u_texel_size.y / u_texel_size.x;
                    vec2 p = uv * 2.0 - 1.0;
                    p.x *= aspect;
                
                    float t = u_time * u_speed;
                
                    vec3 origin = vec3(0.0, 0.0, t);
                    vec3 shift  = vec3(0.0, 0.0, 0.0);
                    vec3 b      = vec3(0.0);
                    vec3 d      = vec3(p, 1.0) / 32.0;
                    vec3 acc    = vec3(0.5);
                
                    for (int i = 0; i < 80; i++) {
                        float h = c(b + shift + origin);
                        b   += h * 10.0 * d;
                        acc += h;
                    }
                    acc /= 80.0;
                
                    vec3 norm = calcNormal(b + shift + origin);
                    float x = dot(norm, acc);
                
                    vec3 diffuse = vec3(0.21, 0.62, 0.96);
                    acc = (acc + pow(max(x, 0.0), 4.0)) * (1.0 - acc * 0.01) * diffuse;
                    acc *= b.z * 0.13 * u_intensity;
                
                    vec2 vig = p * 0.42;
                    vig.y /= aspect;
                    float vigAmt = clamp(1.0 - length(vig), 0.0, 1.0);
                
                    vec3 cobweb = clamp(acc * 2.4 * vigAmt, 0.0, 1.0);
                
                    vec3 color = mix(bgColor * 0.08, cobweb, 0.94);
                
                    gl_FragColor = vec4(color, maskAlpha);
                }
                """;
    }
}