#version 150

in vec2 TexCoord;
in vec4 FragColor;

uniform sampler2D Sampler0;
uniform float Range; // distance field range of the msdf font texture
uniform float Thickness; // text thickness
uniform float Smoothness; // edge smoothness
uniform bool Outline; // if false, outline computation will be ignored
uniform float OutlineThickness;
uniform vec4 OutlineColor;

out vec4 OutColor;

float median(vec3 color) {
    return max(min(color.r, color.g), min(max(color.r, color.g), color.b));
}

void main() {
    // Получаем значение расстояния из текстуры
    float dist = median(texture(Sampler0, TexCoord).rgb) - 0.5 + Thickness;

    // Вычисляем размер пикселей в координатах текстуры
    vec2 h = vec2(dFdx(TexCoord.x), dFdy(TexCoord.y)) * textureSize(Sampler0, 0);
    float pixels = Range * inversesqrt(h.x * h.x + h.y * h.y);

    // Вычисляем альфа-канал с учетом сглаживания
    float alpha = smoothstep(-Smoothness, Smoothness, dist * pixels);
    vec4 color = vec4(FragColor.rgb, FragColor.a * alpha);

    if (Outline) {
        // Вычисляем альфа-канал для контура
        float outlineAlpha = smoothstep(-Smoothness, Smoothness, (dist + OutlineThickness) * pixels);
        color = mix(OutlineColor, color, outlineAlpha);
    }

    // Применяем окончательный цвет
    OutColor = color;
}