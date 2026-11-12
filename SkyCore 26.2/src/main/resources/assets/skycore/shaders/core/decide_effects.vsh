#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <skycore:ui_common.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in float LineWidth;

out vec2 pixelCoord;
out vec4 colorA;
flat out ivec2 packedUv1;
flat out int effectMode;
flat out int packedAux;
flat out float paramZ;
flat out float auxFloat;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position.xy, 0.0, 1.0);
    pixelCoord = UV0;
    colorA = Color;
    packedUv1 = UV1;
    effectMode = UV2.x;
    packedAux = UV2.y;
    paramZ = Position.z;
    auxFloat = LineWidth;
}
