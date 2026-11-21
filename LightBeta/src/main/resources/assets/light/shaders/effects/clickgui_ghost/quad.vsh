#version 150

layout(std140) uniform GhostData {
    mat4 ProjView;
    vec4 Corner0;
    vec4 Corner1;
    vec4 Corner2;
    vec4 Corner3;
    vec4 UvRect;
    vec4 AlphaPad;
};

out vec2 texCoord;

void main() {
    int idx[6] = int[](0, 1, 2, 0, 2, 3);
    int i = idx[gl_VertexID];
    vec3 pos = i == 0 ? Corner0.xyz : i == 1 ? Corner1.xyz : i == 2 ? Corner2.xyz : Corner3.xyz;
    vec2 uv = i == 0 ? vec2(UvRect.x, UvRect.y)
            : i == 1 ? vec2(UvRect.z, UvRect.y)
            : i == 2 ? vec2(UvRect.z, UvRect.w)
            :          vec2(UvRect.x, UvRect.w);
    texCoord = uv;
    gl_Position = ProjView * vec4(pos, 1.0);
}
