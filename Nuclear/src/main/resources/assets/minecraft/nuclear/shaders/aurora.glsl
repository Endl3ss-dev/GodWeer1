#version 120

uniform float time;
uniform float intensity;
uniform float speed;
uniform vec3 cameraPos;
uniform float cameraYaw;
uniform vec2 resolution;
uniform vec3 color1;
uniform vec3 color2;
uniform vec3 color3;

varying vec3 worldPos;

float noise(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 4; i++) {
        value += amplitude * noise(p);
        p *= 2.0;
        amplitude *= 0.5;
    }
    return value;
}

float auroraWave(vec2 p, float time) {
    float wave = sin(p.x * 3.0 + time * 0.8) * 0.6 + 0.4;
    wave *= sin(p.x * 5.0 + time * 0.5) * 0.4 + 0.6;
    wave *= sin(p.x * 10.0 + time * 0.3) * 0.3 + 0.7;
    wave *= sin(p.x * 15.0 + time * 0.2) * 0.2 + 0.8;
    wave += sin(p.x * 1.5 + time * 1.2) * 0.2 + 0.2;
    return clamp(wave, 0.0, 1.0);
}

void main() {
    vec3 pos = normalize(worldPos);
    
    float theta = atan(pos.z, pos.x);

    if (theta < 0.0) theta += 3.14159 * 2.0;
    
    // Компенсируем поворот камеры - добавляем yaw чтобы привязать к миру
    theta += cameraYaw;
    
    float phi = acos(pos.y);  
    
    float horizontalCoord = theta / 3.14159;
    float verticalCoord = phi / 3.14159;
    
    float t = time * speed;
    
    float wave1 = auroraWave(vec2(horizontalCoord, verticalCoord), t);
    float wave2 = auroraWave(vec2(horizontalCoord * 1.3 + 0.5, verticalCoord), t * 0.8);
    float wave3 = auroraWave(vec2(horizontalCoord * 0.7 - 0.3, verticalCoord), t * 1.2);
    float wave4 = auroraWave(vec2(horizontalCoord * 1.7 + 1.0, verticalCoord), t * 0.6);
    float wave5 = auroraWave(vec2(horizontalCoord * 0.5 - 0.7, verticalCoord), t * 1.5);
    
    float combinedWave = (wave1 * 0.35 + wave2 * 0.25 + wave3 * 0.2 + wave4 * 0.1 + wave5 * 0.1);
    
    float noiseValue = fbm(vec2(horizontalCoord * 2.5, verticalCoord) + vec2(t * 0.15, 0.0));
    combinedWave = mix(combinedWave, combinedWave * (0.7 + noiseValue * 0.3), 0.4);
    
    combinedWave = pow(combinedWave, 0.8);
    
    float verticalGradient = 1.0 - verticalCoord * 0.5;
    verticalGradient = max(0.2, verticalGradient);
    verticalGradient = pow(verticalGradient, 1.2);
    
    float colorMix = sin(theta * 0.5 + t * 0.3) * 0.5 + 0.5;
    vec3 baseColor = mix(color1, color2, colorMix);
    baseColor = mix(baseColor, color3, sin(theta * 0.3 + t * 0.2) * 0.3 + 0.3);

    float alpha = combinedWave * verticalGradient * intensity;

    float glow = smoothstep(0.1, 0.9, combinedWave) * 0.8;
    baseColor += vec3(glow * 0.6);

    baseColor *= 1.5;

    float pulse = sin(t * 0.3) * 0.1 + 0.9;
    baseColor *= pulse;
    alpha *= (pulse * 0.5 + 0.5);

    gl_FragColor = vec4(baseColor * alpha, alpha * 1.0);
}

