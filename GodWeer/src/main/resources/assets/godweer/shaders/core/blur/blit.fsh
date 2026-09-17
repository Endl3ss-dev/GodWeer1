#version 150

in vec2 TexCoord;
in vec4 FragColor;

uniform sampler2D Sampler0;

out vec4 OutColor;

void main() {
    // Альфа источника игнорируется: она нужна была только на этапе копии кадра,
    // а прозрачные области кадра иначе «съели» бы результат размытия.
    vec4 texel = texture(Sampler0, TexCoord);
    OutColor = vec4(texel.rgb * FragColor.rgb, FragColor.a);
}
