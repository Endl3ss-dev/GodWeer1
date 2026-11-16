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

// Plasma: shifting interference pattern of sine waves
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

    vec3  p = rayW * scale;
    float t = time * speed;

    float v = 0.0;
    v += sin(p.x * 1.5 + t);
    v += sin(p.y * 1.5 + t * 0.9);
    v += sin(p.z * 1.5 + t * 1.1);
    v += sin((p.x + p.y + p.z) * 1.2 + t * 1.3);
    v += sin(length(p.xy) * 2.0 - t * 0.8);
    v += sin(length(p.yz) * 2.0 - t * 1.2);
    v  = (v + 6.0) / 12.0;  // normalise to [0,1]

    // Hue shift: create colour from tint with plasma pattern
    float hue    = v + t * 0.05;
    vec3  hsvCol = vec3(
        abs(mod(hue * 6.0 + 0.0, 6.0) - 3.0) - 1.0,
        abs(mod(hue * 6.0 + 4.0, 6.0) - 3.0) - 1.0,
        abs(mod(hue * 6.0 + 2.0, 6.0) - 3.0) - 1.0
    );
    hsvCol = clamp(hsvCol, 0.0, 1.0);

    float brightness = v * v * intensity * 25.0;
    vec3  color = mix(tintColor, hsvCol, 0.55) * brightness;
    fragColor = vec4(color, alpha);
}
