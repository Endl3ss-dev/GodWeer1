#version 150

uniform sampler2D InputSampler;

layout(std140) uniform ShaderTestKawase {
    vec4 kawase;
};

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    vec2 h = kawase.xy * kawase.z;
    vec4 sum = texture(InputSampler, uv) * 4.0;
    sum += texture(InputSampler, uv - h);
    sum += texture(InputSampler, uv + h);
    sum += texture(InputSampler, uv + vec2(h.x, -h.y));
    sum += texture(InputSampler, uv - vec2(h.x, -h.y));
    fragColor = sum / 8.0;
}
