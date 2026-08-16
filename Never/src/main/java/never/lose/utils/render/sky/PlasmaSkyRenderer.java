package never.lose.utils.render.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import never.lose.NeverLose;
import never.lose.modules.impl.Render.Ambience;
import never.lose.utils.render.RenderConst;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public final class PlasmaSkyRenderer {

    private static final float SKY_SIZE = 16.0F;
    private static final int FACE_DIV = 16;
    private static final int VERTEX_FLOATS = 6 * FACE_DIV * FACE_DIV * 4 * 3;
    private static final float[] POSITIONS = new float[VERTEX_FLOATS];

    static {
        buildCubeSphere();
    }

    private PlasmaSkyRenderer() {
    }

    public static boolean draw(Ambience ambience, Matrix4f positionMatrix, Matrix4f projectionMatrix, float tickDelta) {
        ShaderProgram shader = RenderConst.PLASMA_SKY_SHADER;
        if (shader == null || positionMatrix == null || projectionMatrix == null) {
            return false;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        float worldTime = tickDelta;
        if (mc.world != null) {
            worldTime += mc.world.getTime() % 100000L;
        }
        float time = worldTime * ambience.skySpeed.getFloatValue() * 0.08F;
        int theme = NeverLose.getInstance().getThemeManager().getCurrentTheme().colorClient;
        float red = ((theme >> 16) & 0xFF) / 255.0F;
        float green = ((theme >> 8) & 0xFF) / 255.0F;
        float blue = (theme & 0xFF) / 255.0F;

        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.set(positionMatrix);
        RenderSystem.applyModelViewMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(() -> shader);

        setUniform(shader, "u_Time", time);
        setUniform(shader, "u_Scale", ambience.skyScale.getFloatValue());
        setUniform(shader, "u_Outline", ambience.skyOutline.getFloatValue());
        setUniform(shader, "u_Glow", ambience.skyGlow.getFloatValue());
        setUniform(shader, "u_Fill", ambience.skyFill.getFloatValue());
        setUniform(shader, "u_Alpha", ambience.skyAlpha.getFloatValue());
        setUniform(shader, "u_Color", red, green, blue, 1.0F);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < POSITIONS.length; i += 3) {
            buffer.vertex(POSITIONS[i], POSITIONS[i + 1], POSITIONS[i + 2]).color(255, 255, 255, 255);
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        modelView.popMatrix();
        RenderSystem.applyModelViewMatrix();
        return true;
    }

    private static void setUniform(ShaderProgram shader, String name, float value) {
        GlUniform uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(value);
        }
    }

    private static void setUniform(ShaderProgram shader, String name, float x, float y, float z, float w) {
        GlUniform uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(x, y, z, w);
        }
    }

    private static void buildCubeSphere() {
        int index = 0;
        for (int face = 0; face < 6; face++) {
            for (int y = 0; y < FACE_DIV; y++) {
                float v0 = (y / (float) FACE_DIV) * 2.0F - 1.0F;
                float v1 = ((y + 1) / (float) FACE_DIV) * 2.0F - 1.0F;
                for (int x = 0; x < FACE_DIV; x++) {
                    float u0 = (x / (float) FACE_DIV) * 2.0F - 1.0F;
                    float u1 = ((x + 1) / (float) FACE_DIV) * 2.0F - 1.0F;
                    index = putSphere(index, face, u0, v0);
                    index = putSphere(index, face, u1, v0);
                    index = putSphere(index, face, u1, v1);
                    index = putSphere(index, face, u0, v1);
                }
            }
        }
    }

    private static int putSphere(int index, int face, float u, float v) {
        float x;
        float y;
        float z;
        switch (face) {
            case 0 -> {
                x = 1.0F;
                y = v;
                z = -u;
            }
            case 1 -> {
                x = -1.0F;
                y = v;
                z = u;
            }
            case 2 -> {
                x = u;
                y = 1.0F;
                z = v;
            }
            case 3 -> {
                x = u;
                y = -1.0F;
                z = -v;
            }
            case 4 -> {
                x = u;
                y = v;
                z = 1.0F;
            }
            default -> {
                x = -u;
                y = v;
                z = -1.0F;
            }
        }
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        POSITIONS[index] = x / length * SKY_SIZE;
        POSITIONS[index + 1] = y / length * SKY_SIZE;
        POSITIONS[index + 2] = z / length * SKY_SIZE;
        return index + 3;
    }
}
