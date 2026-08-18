#version 120

uniform sampler2D textureIn;
uniform vec2 texelSize, direction;
uniform vec4 color;
uniform bool avoidTexture;
uniform float exposure, radius;
uniform float weights[8];

#define offset direction * texelSize
#define MAX_R 6

void main() {
    if (direction.y == 1.0 && avoidTexture) {
        if (texture2D(textureIn, gl_TexCoord[0].st).a != 0.0) discard;
    }

    vec4 centerTex = texture2D(textureIn, gl_TexCoord[0].st);
    float rCap = min(radius, float(MAX_R));
    float innerAlpha = centerTex.a * weights[0];
    vec3 colorSum = centerTex.rgb * centerTex.a * weights[0];

    for (float r = 1.0; r <= rCap; r += 1.0) {
        int ri = int(r);
        vec4 t1 = texture2D(textureIn, gl_TexCoord[0].st + offset * r);
        vec4 t2 = texture2D(textureIn, gl_TexCoord[0].st - offset * r);
        innerAlpha += t1.a * weights[ri] + t2.a * weights[ri];
        colorSum += t1.rgb * t1.a * weights[ri] + t2.rgb * t2.a * weights[ri];
    }

    vec3 outRgb = colorSum / max(innerAlpha, 0.001);
    gl_FragColor = vec4(outRgb, color.a * innerAlpha);
}