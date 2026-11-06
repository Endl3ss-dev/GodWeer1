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

float hash(vec3 p) {
    p = fract(p * vec3(127.1, 311.7, 74.7));
    p += dot(p, p.yzx + 19.19);
    return fract((p.x + p.y) * p.z);
}

float nebulaNoise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(mix(hash(i),          hash(i+vec3(1,0,0)), f.x),
            mix(hash(i+vec3(0,1,0)), hash(i+vec3(1,1,0)), f.x), f.y),
        mix(mix(hash(i+vec3(0,0,1)), hash(i+vec3(1,0,1)), f.x),
            mix(hash(i+vec3(0,1,1)), hash(i+vec3(1,1,1)), f.x), f.y), f.z);
}

// Volumetric nebula: layered noise clouds with colour shifts
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

    vec3 p = rayW * scale * 0.25 + vec3(0.0, 0.0, time * speed * 0.08);

    float n  = 0.0;
    float amp = 0.5;
    float freq = 1.0;
    for (int k = 0; k < 5; k++) {
        n   += nebulaNoise(p * freq) * amp;
        amp  *= 0.5;
        freq *= 2.1;
    }
    n = pow(max(0.0, n - 0.3), 1.4);

    // Two-tone colour: base tint + purplish highlight
    vec3  highlight = vec3(tintColor.z, tintColor.x * 0.5, tintColor.y * 1.5) * 0.8;
    vec3  cloudColor = mix(tintColor, highlight, n * 0.6);
    float brightness = n * intensity * 18.0;
    fragColor = vec4(cloudColor * brightness, alpha);
}
