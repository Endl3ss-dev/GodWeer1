#version 150

layout(std140) uniform uTime { float time; };
layout(std140) uniform uResolution { vec2 resolution; };
layout(std140) uniform uColor { vec3 tintColor; };
layout(std140) uniform uParams { vec4 params; };
layout(std140) uniform uCamera { vec4 cameraData; };

out vec4 fragColor;

mat3 rotX(float a) {
    float c = cos(a), s = sin(a);
    return mat3(1.0,0.0,0.0, 0.0,c,s, 0.0,-s,c);
}
mat3 rotY(float a) {
    float c = cos(a), s = sin(a);
    return mat3(c,0.0,s, 0.0,1.0,0.0, -s,0.0,c);
}

// Bloom: radial god-rays emanating from centre
void main() {
    float alpha     = params.x;
    float speed     = params.y;
    float scale     = params.z;
    float intensity = params.w;
    vec2  camDir    = cameraData.xy;
    float fov       = cameraData.z;

    vec2  uv     = gl_FragCoord.xy / resolution.xy;
    vec2  sp     = uv * 2.0 - 1.0;
    float aspect = resolution.x / resolution.y;
    float tanV   = tan(radians(fov) * 0.5);
    vec3  rayV   = normalize(vec3(sp.x * tanV * aspect, sp.y * tanV, 1.0));
    vec3  rayW   = rotY(camDir.x) * rotX(camDir.y) * rayV;

    vec2  p2  = rayW.xz * scale * 0.3;
    float t   = time * speed * 0.4;
    float r   = length(p2) + 0.001;
    float ang = atan(p2.y, p2.x);

    // Rotating rays
    float rays = 0.0;
    for (int k = 0; k < 8; k++) {
        float a = ang - t + float(k) * 0.7854; // 2pi/8
        rays += pow(max(0.0, cos(a * 3.0)), 8.0) * exp(-r * 1.5);
    }

    // Core glow
    float core  = exp(-r * 2.5) * (0.6 + 0.4 * sin(t * 2.0));
    // Pulsing halo
    float halo  = smoothstep(0.6, 0.0, abs(r - 0.8 - 0.2 * sin(t * 1.5))) * 0.3;

    float total = (rays * 0.6 + core + halo) * intensity * 20.0;
    vec3  color = tintColor * total + tintColor * core * 0.3;
    fragColor = vec4(color, alpha);
}
