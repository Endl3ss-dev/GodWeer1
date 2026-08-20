#version 150

uniform sampler2D Sampler0;

layout(std140) uniform Uniforms {
    float Range;
    float Thickness;
    float Smoothness;
    float ScreenWidth;
    float ScreenHeight;
    float MaskStartX;
    float MaskWidth;
    float UseMask;
    vec4 scissorRect;
};

in vec2 TexCoord;
in vec4 FragColor;
out vec4 OutColor;

float median(vec3 color) {
    return max(min(color.r, color.g), min(max(color.r, color.g), color.b));
}

void main() {
    vec2 fragPx = gl_FragCoord.xy;
    
    if (scissorRect.z > 0.0) {
        if (fragPx.x < scissorRect.x || fragPx.x > scissorRect.z ||
            fragPx.y < scissorRect.y || fragPx.y > scissorRect.w) {
            discard;
        }
    }
    
    float dist = median(texture(Sampler0, TexCoord).rgb) - 0.5 + Thickness;
    vec2 h = vec2(dFdx(TexCoord.x), dFdy(TexCoord.y)) * textureSize(Sampler0, 0);
    float pixels = Range * inversesqrt(h.x * h.x + h.y * h.y);
    float alpha = smoothstep(-Smoothness, Smoothness, dist * pixels);

    if (alpha < 0.01) discard;

    float mask = 1.0;
    if (UseMask > 0.5) {
        float relativeX = fragPx.x - MaskStartX;
        mask = 1.0 - smoothstep(MaskWidth - 10.0, MaskWidth, relativeX);
        if (mask < 0.01) discard;
    }

    OutColor = vec4(FragColor.rgb, FragColor.a * alpha * mask);
}
