#version 150

uniform sampler2D Sampler0;
uniform vec2 TexelSize;
uniform float MaxDist;
uniform float Radius;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    float best = texture(Sampler0, texCoord).r * MaxDist;
    int r = int(Radius);
    for (int j = 1; j <= 96; j++) {
        if (j > r) break;
        float fj = float(j);
        if (fj >= best) break;
        float dhp = texture(Sampler0, texCoord + vec2(0.0, TexelSize.y * fj)).r * MaxDist;
        best = min(best, sqrt(dhp * dhp + fj * fj));
        float dhn = texture(Sampler0, texCoord - vec2(0.0, TexelSize.y * fj)).r * MaxDist;
        best = min(best, sqrt(dhn * dhn + fj * fj));
    }
    fragColor = vec4(best / MaxDist, 0.0, 0.0, 1.0);
}
