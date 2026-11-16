#version 150
layout(std140) uniform u_resolution {
    vec2 resolution;
};
layout(std140) uniform u_radius {
    float radius;
};
uniform sampler2D u_texture;
out vec4 fragColor;
void main() {
    vec2 uv = gl_FragCoord.xy / resolution;
    vec2 stepUv = vec2(max(radius, 1.0), 0.0) / resolution;

    vec4 color = texture(u_texture, uv) * 0.172;
    color += texture(u_texture, uv + stepUv * 1.0) * 0.158;
    color += texture(u_texture, uv - stepUv * 1.0) * 0.158;
    color += texture(u_texture, uv + stepUv * 2.0) * 0.125;
    color += texture(u_texture, uv - stepUv * 2.0) * 0.125;
    color += texture(u_texture, uv + stepUv * 3.0) * 0.084;
    color += texture(u_texture, uv - stepUv * 3.0) * 0.084;
    color += texture(u_texture, uv + stepUv * 4.0) * 0.048;
    color += texture(u_texture, uv - stepUv * 4.0) * 0.048;

    fragColor = color;
}
