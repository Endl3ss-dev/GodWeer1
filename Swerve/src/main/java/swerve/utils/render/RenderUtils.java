package swerve.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import swerve.Swerve;

import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.ShaderProgram;

import org.joml.Matrix4f;

public final class RenderUtils {

    private static boolean roundedRectShaderWarned;

    private RenderUtils() {
    }

    public static boolean isRoundedRectShaderReady() {
        return ShaderUtils.get(ShaderUtils.ROUNDED_RECT) != null;
    }

    public static void drawRoundedRect(PoseStack matrices, float x, float y, float width, float height,
                                       float radius, int color) {
        drawRoundedRect(matrices, x, y, width, height, radius, radius, radius, radius, color);
    }

    public static void drawRoundedRect(PoseStack matrices, float x, float y, float width, float height,
                                       float topLeft, float topRight, float bottomRight, float bottomLeft,
                                       int color) {
        if (width <= 0.0f || height <= 0.0f) {
            return;
        }

        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.ROUNDED_RECT);
        ShaderProgram program = ShaderUtils.ROUNDED_RECT;

        if (shader == null) {
            // Фолбэк на ванильный шейдер: без скруглений, но геометрия остаётся видимой,
            // иначе провал компиляции кастомного шейдера выглядит как «GUI не открылся».
            if (!roundedRectShaderWarned) {
                roundedRectShaderWarned = true;
                Swerve.LOGGER.error("Shader '{}' is not compiled, falling back to vanilla POSITION_COLOR",
                        ShaderUtils.ROUNDED_RECT.configId());
            }
            program = CoreShaders.POSITION_COLOR;
        } else {
            ShaderUtils.set(shader, "Size", width, height);
            ShaderUtils.set(shader, "Radius", topLeft, topRight, bottomRight, bottomLeft);
            ShaderUtils.set(shader, "Smoothness", 1.0f);
            ShaderUtils.set(shader, "ColorModulator", 1.0f, 1.0f, 1.0f, 1.0f);
        }

        // [!] Раньше alpha == 0 принудительно превращалась в 255. Из-за этого любой затухающий
        // элемент (TargetHud, строки ArrayList, уведомления) в конце анимации вспыхивал
        // непрозрачным белым вместо исчезновения. Нулевая альфа — это «не рисовать».
        int alpha = (color >> 24) & 0xFF;
        if (alpha == 0) {
            return;
        }
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = alpha / 255.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        Matrix4f matrix = matrices.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        buffer.addVertex(matrix, x, y, 0.0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, x, y + height, 0.0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, x + width, y + height, 0.0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, x + width, y, 0.0f).setColor(r, g, b, a);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            RenderSystem.setShader(program);
            BufferUploader.drawWithShader(mesh);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static void drawRoundCircle(PoseStack matrices, float x, float y, float radius, int color) {
        drawRoundedRect(matrices, x - (radius / 2.0f), y - (radius / 2.0f), radius, radius,
                (radius / 2.0f) - 0.5f, color);
    }

    public static void drawRect(PoseStack matrices, float x, float y, float width, float height, int color) {
        drawRoundedRect(matrices, x, y, width, height, 0.0f, color);
    }
}
