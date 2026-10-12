#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform float uThreshold;

in vec2 TexCoord;
out vec4 OutColor;

void main() {
    vec2 uv = TexCoord;
    float depthBefore = texture(Sampler0, uv).r;
    float depthAfter = texture(Sampler1, uv).r;

    float diff = depthBefore - depthAfter;
    float mask = diff > uThreshold ? 1.0 : 0.0;

    // Закрываем мелкие зазоры и межпиксельные швы на предметах (morphological closing)
    if (mask < 0.5) {
        vec2 texel = 1.0 / vec2(textureSize(Sampler1, 0));
        float dL = texture(Sampler0, uv - vec2(texel.x, 0.0)).r - texture(Sampler1, uv - vec2(texel.x, 0.0)).r;
        float dR = texture(Sampler0, uv + vec2(texel.x, 0.0)).r - texture(Sampler1, uv + vec2(texel.x, 0.0)).r;
        float dU = texture(Sampler0, uv - vec2(0.0, texel.y)).r - texture(Sampler1, uv - vec2(0.0, texel.y)).r;
        float dD = texture(Sampler0, uv + vec2(0.0, texel.y)).r - texture(Sampler1, uv + vec2(0.0, texel.y)).r;

        bool mL = dL > uThreshold;
        bool mR = dR > uThreshold;
        bool mU = dU > uThreshold;
        bool mD = dD > uThreshold;

        if ((mL && mR) || (mU && mD) || ((mL || mR || mU || mD) && ((mL ? 1 : 0) + (mR ? 1 : 0) + (mU ? 1 : 0) + (mD ? 1 : 0) >= 3))) {
            mask = 1.0;
        } else {
            vec2 t2 = texel * 2.0;
            float dL2 = texture(Sampler0, uv - vec2(t2.x, 0.0)).r - texture(Sampler1, uv - vec2(t2.x, 0.0)).r;
            float dR2 = texture(Sampler0, uv + vec2(t2.x, 0.0)).r - texture(Sampler1, uv + vec2(t2.x, 0.0)).r;
            float dU2 = texture(Sampler0, uv - vec2(0.0, t2.y)).r - texture(Sampler1, uv - vec2(0.0, t2.y)).r;
            float dD2 = texture(Sampler0, uv + vec2(0.0, t2.y)).r - texture(Sampler1, uv + vec2(0.0, t2.y)).r;
            if ((dL2 > uThreshold && dR2 > uThreshold) || (dU2 > uThreshold && dD2 > uThreshold)) {
                mask = 1.0;
            }
        }
    }

    float packedDepth = clamp(depthAfter, 0.0, 1.0) * 65535.0;
    float hi = floor(packedDepth / 256.0) / 255.0;
    float lo = mod(packedDepth, 256.0) / 255.0;
    OutColor = vec4(hi, lo, mask, mask);
}
