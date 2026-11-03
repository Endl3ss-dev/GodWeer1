#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <skycore:ui_common.glsl>

in vec2 pixelCoord;
in vec4 colorA;
flat in ivec2 packedUv1;
flat in int effectMode;
flat in int packedAux;
flat in float paramZ;
flat in float auxFloat;

out vec4 fragColor;

float rayStrength(vec2 raySource, vec2 rayRefDirection, vec2 coord,
                  float seedA, float seedB, float speed,
                  float time, float reach, vec2 resolution) {
    vec2 sourceToCoord = coord - raySource;
    float len = max(length(sourceToCoord), 0.0001);
    float cosAngle = dot(sourceToCoord / len, rayRefDirection);

    return clamp(
        (0.45 + 0.15 * sin(cosAngle * seedA + time * speed)) +
        (0.3 + 0.2 * cos(-cosAngle * seedB + time * speed)),
        reach, 1.0) *
        clamp((resolution.x - len) / max(resolution.x, 1.0), reach, 1.0);
}

void main() {
    if (effectMode == 1) {
        vec2 resolution = vec2(max(float(packedAux) * 0.25, 1.0), max(auxFloat, 1.0));
        float time = paramZ;
        float intensity = 0.1;
        float rays = 0.08;
        float reach = 0.26;
        vec2 coord = pixelCoord;
        float speed = rays * 10.0;

        vec2 rayPos1 = vec2(resolution.x * 0.7, resolution.y * -0.4);
        vec2 rayRefDir1 = normalize(vec2(1.0, -0.116));
        float raySeedA1 = 36.2214 * speed;
        float raySeedB1 = 21.11349 * speed;
        float raySpeed1 = 1.5 * speed;

        vec2 rayPos2 = vec2(resolution.x * 0.8, resolution.y * -0.6);
        vec2 rayRefDir2 = normalize(vec2(1.0, 0.241));
        float raySeedA2 = 22.39910 * speed;
        float raySeedB2 = 18.0234 * speed;
        float raySpeed2 = 1.1 * speed;

        vec4 colorB = ui_unpackColor(packedUv1.x, packedUv1.y);
        float globalAlpha = clamp(colorB.a / 0.9, 0.0, 1.0);

        vec4 rays1 = rayStrength(rayPos1, rayRefDir1, coord, raySeedA1, raySeedB1, raySpeed1, time, reach, resolution) * colorA;
        vec4 rays2 = rayStrength(rayPos2, rayRefDir2, coord, raySeedA2, raySeedB2, raySpeed2, time, reach, resolution) * colorB;
        vec4 color = rays1 + rays2;

        float brightness = reach - (coord.y / resolution.y);
        color *= (brightness + (0.5 + intensity));
        color *= globalAlpha;

        float alpha = clamp(color.a, 0.0, 1.0);
        if (alpha < 0.004) {
            discard;
        }
        fragColor = vec4(clamp(color.rgb, 0.0, 1.0), alpha) * ColorModulator;
        return;
    }

    if (effectMode == 2) {
        float spacing = max(float(packedAux) / 64.0, 1.0);
        float minRadius = 0.7 * auxFloat;
        float maxRadius = 3.0 * auxFloat;
        float reach = max(paramZ, 1.0);
        vec2 pointer = vec2(packedUv1) * 0.25;

        float rowHeight = spacing * 0.8660254;
        float row = floor(pixelCoord.y / rowHeight);
        float rowOffset = mod(row, 2.0) * spacing * 0.5;
        float col = floor((pixelCoord.x - rowOffset) / spacing);
        vec2 node = vec2((col + 0.5) * spacing + rowOffset, (row + 0.5) * rowHeight);

        float toPointer = length(node - pointer);
        float weight = 1.0 - clamp(toPointer / reach, 0.0, 1.0);
        weight = weight * weight * (3.0 - 2.0 * weight);
        float radius = mix(minRadius, maxRadius, weight);
        if (radius <= 0.05) {
            discard;
        }

        float dist = length(pixelCoord - node);
        float aa = max(fwidth(dist), 0.4 * auxFloat);
        float dotVal = 1.0 - smoothstep(radius - aa, radius + aa, dist);
        float alpha = dotVal * colorA.a;
        if (alpha < 0.004) {
            discard;
        }
        fragColor = vec4(colorA.rgb, alpha) * ColorModulator;
        return;
    }

    if (effectMode == 3) {
        float step_ = max(float(packedUv1.x) / 64.0, 0.5);
        float areaW = max(float(packedUv1.y), 1.0);
        float edgeFade = float(packedAux) / 16.0;
        float lineWidth = max(edgeFade * 0.02, 0.2);
        float tower = edgeFade * 2.0;
        float areaH = max(auxFloat, 1.0);
        float phase = fract(paramZ);

        float distToLine = abs(mod(pixelCoord.y, step_) - step_ * 0.5);
        float half_ = lineWidth * 0.5;
        float aa = max(fwidth(pixelCoord.y), 0.35);
        float line = 1.0 - smoothstep(half_ - aa, half_ + aa, distToLine);
        if (line <= 0.001) {
            discard;
        }

        float waveY = -tower + phase * (areaH + tower * 2.0);
        float wave = 1.0 - clamp(abs(pixelCoord.y - waveY) / max(tower, 0.001), 0.0, 1.0);
        float bright = mix(0.2, 1.0, wave);

        float edge = 1.0;
        if (edgeFade > 0.001) {
            edge = clamp(pixelCoord.x / edgeFade, 0.0, 1.0)
                 * clamp((areaW - pixelCoord.x) / edgeFade, 0.0, 1.0);
        }

        float alpha = line * bright * edge * colorA.a;
        if (alpha < 0.004) {
            discard;
        }
        fragColor = vec4(colorA.rgb, alpha) * ColorModulator;
        return;
    }

    discard;
}
