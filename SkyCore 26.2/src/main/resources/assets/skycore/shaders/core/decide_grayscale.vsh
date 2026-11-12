#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in float LineWidth;

out vec2 screenUv;
flat out float grayState;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position.xy, 0.0, 1.0);
    screenUv = gl_Position.xy / gl_Position.w * 0.5 + 0.5;
    grayState = clamp(Color.a + (UV0.x + float(UV1.x + UV2.x) + LineWidth) * 0.0, 0.0, 1.0);
}
