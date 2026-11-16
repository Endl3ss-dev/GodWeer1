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

// Spiral drain: rotating vortex lines converging to center
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

    vec2  p2  = rayW.xz * scale * 0.35;
    float t   = time * speed * 0.6;
    float len = length(p2) + 0.001;
    float ang = atan(p2.y, p2.x);

    float drain = 0.0;
    for (int k = 0; k < 6; k++) {
        float phase = ang - t * 1.2 - len * 2.5 + float(k) * 1.047;
        float line  = abs(sin(phase * 3.0)) * (1.0 - smoothstep(0.0, 2.5, len));
        drain += line * (1.0 / (1.0 + len * 0.8));
    }

    float vortex    = smoothstep(0.0, 1.0, drain) * (intensity * 20.0);
    float centerGlow = exp(-len * 1.8) * 1.5;
    float total     = vortex + centerGlow;
    vec3  color     = tintColor * total;
    fragColor = vec4(color, alpha);
}
