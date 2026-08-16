#version 150

#moj_import <n2ver:common.glsl>

in vec2 FragCoord; // normalized fragment coord relative to the primitive
in vec4 FragColor;

out vec4 OutColor;

uniform float Radius;
uniform float Thickness;
uniform float StartAngle;
uniform float EndAngle;

void main() {
    // Нормализованные координаты от -1 до 1
    vec2 coord = FragCoord;

    // Полярные координаты
    float dist = length(coord);
    float angle = atan(coord.y, coord.x);

    // Нормализация угла в [0, 2?]
    angle = angle < 0.0 ? angle + 6.28318530718 : angle;

    // Проверка попадания в угловой диапазон
    float angleFade = smoothstep(StartAngle - 0.1, StartAngle + 0.1, angle) *
    (1.0 - smoothstep(EndAngle - 0.1, EndAngle + 0.1, angle));

    // Сглаживание границ
    float edge = fwidth(dist);
    float outer = smoothstep(1.0 - edge, 1.0 + edge, dist);
    float inner = smoothstep(Thickness - edge, Thickness + edge, dist);

    // Комбинируем результат
    float alpha = outer * (1.0 - inner) * angleFade;

    OutColor = vec4(FragColor.rgb, FragColor.a * alpha);

    if (OutColor.a < 0.01) discard;
}