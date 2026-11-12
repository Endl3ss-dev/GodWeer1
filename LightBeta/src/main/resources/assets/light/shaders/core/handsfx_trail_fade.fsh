#version 150

uniform sampler2D PrevSampler;

layout(std140) uniform HandsFxData {
    vec4 color1;
    vec4 color2;
    vec4 fillColor;
    vec4 outlineColor;
    vec4 params0;
    vec4 params1;
    vec4 params2;
    vec4 params3;
    vec4 screen;
    vec4 misc;
};

in vec2 texCoord;
out vec4 fragColor;

float wob(float y, float t) {
    return sin(y * 9.0 + t * 4.3) * 0.6
         + sin(y * 17.0 - t * 2.1) * 0.3
         + sin(y * 4.0 + t * 1.3) * 0.4
         + sin(y * 28.0 + t * 5.7) * 0.15;
}

vec4 blurSample(vec2 uv) {
    vec2 px = screen.zw;
    vec4 c = texture(PrevSampler, uv) * 0.36;
    c += texture(PrevSampler, uv + vec2(px.x, 0.0)) * 0.16;
    c += texture(PrevSampler, uv - vec2(px.x, 0.0)) * 0.16;
    c += texture(PrevSampler, uv + vec2(0.0, px.y)) * 0.16;
    c += texture(PrevSampler, uv - vec2(0.0, px.y)) * 0.16;
    return c;
}

void main() {
    vec2 uv = texCoord;
    float t = params2.x;
    float dt = params2.y;
    float turb = params2.z;
    float flickAmp = params2.w;

    float dxTurb = (wob(uv.y, t) - wob(uv.y, t - dt)) * turb;
    float yFactor = 0.7 + uv.y * 0.8;
    vec2 displacement = vec2(params3.x + dxTurb, params3.y * yFactor);
    vec4 color = blurSample(uv - displacement);

    float flick = 1.0 - flickAmp + flickAmp * (0.5 + 0.5 * sin(t * 14.0) + 0.25 * sin(t * 9.3));
    float oldAlpha = color.a;
    float newAlpha = max(0.0, oldAlpha * (0.985 * flick) - params3.z);
    float scale = oldAlpha > 0.001 ? newAlpha / oldAlpha : 0.0;
    fragColor = vec4(color.rgb * scale, newAlpha);
}
