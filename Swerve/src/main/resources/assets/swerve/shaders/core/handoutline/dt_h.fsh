#version 150

uniform sampler2D Sampler0;
uniform vec2 TexelSize;
uniform float MaxDist;
uniform float Radius;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    if (texture(Sampler0, texCoord).a > 0.003) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }
    float best = MaxDist;
    int r = int(Radius);
    for (int i = 1; i <= 96; i++) {
        if (i > r) break;
        float fi = float(i);
        if (fi >= best) break;
        if (texture(Sampler0, texCoord + vec2(TexelSize.x * fi, 0.0)).a > 0.003
                || texture(Sampler0, texCoord - vec2(TexelSize.x * fi, 0.0)).a > 0.003) {
            best = fi;
            break;
        }
    }
    fragColor = vec4(best / MaxDist, 0.0, 0.0, 1.0);
}
