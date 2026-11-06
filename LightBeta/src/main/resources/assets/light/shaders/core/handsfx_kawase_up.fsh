#version 150

uniform sampler2D InputSampler;

layout(std140) uniform HandsFxKawase {
    vec4 kawase;
};

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    vec2 h = kawase.xy * kawase.z;
    vec4 sum = texture(InputSampler, uv + vec2(-h.x * 2.0, 0.0));
    sum += texture(InputSampler, uv + vec2(-h.x, h.y)) * 2.0;
    sum += texture(InputSampler, uv + vec2(0.0, h.y * 2.0));
    sum += texture(InputSampler, uv + vec2(h.x, h.y)) * 2.0;
    sum += texture(InputSampler, uv + vec2(h.x * 2.0, 0.0));
    sum += texture(InputSampler, uv + vec2(h.x, -h.y)) * 2.0;
    sum += texture(InputSampler, uv + vec2(0.0, -h.y * 2.0));
    sum += texture(InputSampler, uv + vec2(-h.x, -h.y)) * 2.0;
    fragColor = sum / 12.0;
}
