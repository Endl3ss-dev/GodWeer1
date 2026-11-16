#version 330

#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

in vec2 screenUv;
flat in float grayState;

out vec4 fragColor;

void main() {
    vec4 source = texture(Sampler0, clamp(screenUv, vec2(0.0), vec2(1.0)));
    float gray = (source.r + source.g + source.b) / 3.0;
    fragColor = vec4(mix(source.rgb, vec3(gray), grayState), 1.0) * ColorModulator;
}
