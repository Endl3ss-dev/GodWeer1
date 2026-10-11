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

    // Силуэт рук: пиксели, где рука закрыла мир (глубина стала ближе).
    // Там, где рука оказалась ЗА миром, её не видно — маски там и не должно быть.
    float mask = smoothstep(uThreshold * 0.5, uThreshold * 1.5, depthBefore - depthAfter);
    OutColor = vec4(mask, mask, mask, mask);
}
