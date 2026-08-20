#version 150

in vec3 Position;
in vec2 UV0;
in vec4 Color;

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

out vec2 TexCoord;
out vec4 FragColor;

void main() {
    TexCoord = UV0;
    FragColor = Color;

    vec2 ndc = (Position.xy / vec2(ScreenWidth, ScreenHeight)) * 2.0 - 1.0;
    ndc.y = -ndc.y;
    gl_Position = vec4(ndc, 0.0, 1.0);
}
