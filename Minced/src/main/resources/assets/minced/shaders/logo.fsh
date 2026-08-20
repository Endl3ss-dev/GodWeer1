#version 150

uniform sampler2D Sampler0;
uniform vec2 size;
uniform float time;
uniform float outlineWidth;
uniform float alpha;
uniform vec2 uvOffset;
uniform vec2 uvScale;
uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    // настройки линии
    float speed = 0.5;
    float tilt = -0.6;
    float lineWidth = 0.04;
    float softness = 0.1;

    vec2 actualUV = (texCoord0 - uvOffset) / uvScale;
    bool insideTexture = actualUV.x >= 0.0 && actualUV.x <= 1.0 && actualUV.y >= 0.0 && actualUV.y <= 1.0;
    vec4 texColor = insideTexture ? texture(Sampler0, actualUV) : vec4(0.0);
    vec2 pixelSize = 1.0 / size;

    // линия
    float pixelPos = texCoord0.x + texCoord0.y * tilt;
    float minPos = min(0.0, tilt);
    float maxPos = max(1.0, 1.0 + tilt);
    float range = maxPos - minPos;
    float linePos = minPos + fract(time * speed) * (range + 0.5);
    float lineDist = abs(pixelPos - linePos);
    float lineIntensity = 1.0 - smoothstep(lineWidth, lineWidth + softness, lineDist);
    if (lineIntensity < 0.01) lineIntensity = 0.0;

    if (texColor.a < 0.5) {
        // обводка
        float minDist = outlineWidth + 1.0;

        for (float x = -outlineWidth; x <= outlineWidth; x += 1.0) {
            for (float y = -outlineWidth; y <= outlineWidth; y += 1.0) {
                if (x == 0.0 && y == 0.0) continue;
                float dist = length(vec2(x, y));
                if (dist <= outlineWidth) {
                    vec2 sampleCoord = texCoord0 + vec2(x, y) * pixelSize;
                    vec2 actualSampleUV = (sampleCoord - uvOffset) / uvScale;
                    if (actualSampleUV.x < 0.0 || actualSampleUV.x > 1.0 || actualSampleUV.y < 0.0 || actualSampleUV.y > 1.0) continue;
                    if (texture(Sampler0, actualSampleUV).a > 0.5) {
                        minDist = min(minDist, dist);
                    }
                }
            }
        }

        float outlineShape = 1.0 - smoothstep(0.0, outlineWidth, minDist);
        if (minDist > outlineWidth || outlineShape < 0.001) discard;
        if (lineIntensity < 0.01) discard;

        fragColor = vec4(1.0, 1.0, 1.0, outlineShape * lineIntensity * alpha) * ColorModulator;
    } else {
        // логотип
        vec3 finalColor = mix(texColor.rgb, vec3(1.0), lineIntensity);
        fragColor = vec4(finalColor, texColor.a * alpha) * vertexColor * ColorModulator;
    }
}
