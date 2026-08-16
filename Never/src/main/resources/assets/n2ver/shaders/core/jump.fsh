#version 150

#moj_import <n2ver:common.glsl>

in vec2 FragCoord; // Нормализованные координаты (0-1)
in vec4 FragColor; // Цвет кольца (RGBA)

uniform vec2 Size;  // Размер прямоугольника
uniform float Animation; // Внутренний радиус кольца (0.0-1.0)

out vec4 OutColor;

void main() {
    float s = distance(FragCoord, vec2(0.5, 0.5));

    // Внутренний радиус с плавным переходом
    float innerRadius = 0.3;
    float innerTransition = 0.05; // Ширина перехода для внутреннего края
    float innerEdge = smoothstep(innerRadius - innerTransition, innerRadius + innerTransition, s);

    // Внешний радиус с плавным переходом
    float outerRadius = 0.4 * Animation;
    float outerTransition = 0.05; // Ширина перехода для внешнего края
    float outerEdge = 1.0 - smoothstep(outerRadius - outerTransition, outerRadius + outerTransition, s);

    // Комбинируем оба перехода
    float alpha = innerEdge * outerEdge;

    OutColor = vec4(FragColor.rgb, alpha * FragColor.a);
}