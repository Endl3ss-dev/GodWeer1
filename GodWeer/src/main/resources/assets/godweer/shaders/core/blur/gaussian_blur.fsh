#version 150

in vec2 TexCoord;
in vec4 FragColor;

uniform sampler2D Sampler0;
uniform vec2 TexelSize;
uniform vec2 Direction;
uniform float Radius;

out vec4 OutColor;

// Жёсткий предел тапов: веса считаются в шейдере, uniform-массив ядра не нужен,
// потому что в 1.21.4 передача массива float в Uniform требует FloatBuffer и ломает batching.
const int MAX_TAPS = 24;

void main() {
    float sigma = max(Radius * 0.5, 0.5);
    float twoSigmaSquare = 2.0 * sigma * sigma;
    vec2 step = Direction * TexelSize;

    vec4 sum = texture(Sampler0, TexCoord);
    float weightSum = 1.0;

    // Диапазон ограничен uniform'ом Radius, а не break: цикл с динамическим условием
    // стабильно компилируется драйверами на GLSL 150.
    for (int i = 1; i <= MAX_TAPS; i++) {
        float offset = float(i);
        if (offset <= Radius) {
            float weight = exp(-(offset * offset) / twoSigmaSquare);
            vec2 delta = step * offset;

            sum += texture(Sampler0, TexCoord + delta) * weight;
            sum += texture(Sampler0, TexCoord - delta) * weight;
            weightSum += weight * 2.0;
        }
    }

    OutColor = sum / weightSum;
}
