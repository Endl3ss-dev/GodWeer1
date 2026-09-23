package godweer.utils.render.draw;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import godweer.utils.render.ShaderUtils;

import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.CoreShaders;

import org.joml.Matrix4f;

public final class RenderDraw {

    private RenderDraw() {
    }

    public static final class Rounded {

        private Rounded() {
        }

        public static void roundedRect(PoseStack stack, float x, float y, float width, float height,
                                        int color, Round round) {
            drawRectInternal(stack, x, y, width, height,
                    round.topLeft, round.topRight, round.bottomRight, round.bottomLeft, color);
        }

        public static void roundedRect(PoseStack stack, float x, float y, float width, float height,
                                        int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor,
                                        Round round) {
            drawGradientRectInternal(stack, x, y, width, height,
                    round.topLeft, round.topRight, round.bottomRight, round.bottomLeft,
                    topLeftColor, topRightColor, bottomRightColor, bottomLeftColor);
        }

        public static void roundedWithOutline(PoseStack stack, float x, float y, float width, float height,
                                               float outlineWidth, int bgColor, int outlineColor, Round round) {
            roundedRect(stack, x, y, width, height, bgColor, round);
            roundedOutline(stack, x, y, width, height, outlineWidth, outlineColor, round);
        }

        public static void roundedOutline(PoseStack stack, float x, float y, float width, float height,
                                           float outlineWidth, int color, Round round) {
            drawOutlineInternal(stack, x, y, width, height, outlineWidth,
                    round.topLeft, round.topRight, round.bottomRight, round.bottomLeft, color);
        }

        public static void smooth(PoseStack stack, float x, float y, float width, float height,
                                   int color, Round round) {
            roundedRect(stack, x, y, width, height, color, round);
        }

        public static void drawGradientRound(PoseStack stack, float x, float y, float width, float height,
                                              float radius, int topLeft, int topRight, int bottomRight, int bottomLeft) {
            drawGradientRectInternal(stack, x, y, width, height,
                    radius, radius, radius, radius, topLeft, topRight, bottomRight, bottomLeft);
        }

        public static void gradientRect(PoseStack stack, float x, float y, float width, float height,
                                         int topLeft, int topRight, int bottomRight, int bottomLeft, Round round) {
            drawGradientRectInternal(stack, x, y, width, height,
                    round.topLeft, round.topRight, round.bottomRight, round.bottomLeft,
                    topLeft, topRight, bottomRight, bottomLeft);
        }
    }

    public static final class Shadow {

        private Shadow() {
        }

        public static void drawShadow(PoseStack stack, float x, float y, float width, float height,
                                       float radius, float spread, int color) {
            CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.SHADOW6_RECT);
            if (shader == null) {
                return;
            }

            float expansion = radius * spread * 2.0f;
            float drawX = x - expansion;
            float drawY = y - expansion;
            float drawW = width + expansion * 2;
            float drawH = height + expansion * 2;

            ShaderUtils.set(shader, "Size", drawW, drawH);
            ShaderUtils.set(shader, "Radius", radius + expansion, radius + expansion, radius + expansion, radius + expansion);
            ShaderUtils.set(shader, "Smoothness", 2.0f);

            int alpha = (color >> 24) & 0xFF;
            if (alpha == 0) alpha = 255;
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;
            float a = alpha / 255.0f;

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();

            Matrix4f matrix = stack.last().pose();
            BufferBuilder buffer = Tesselator.getInstance()
                    .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            buffer.addVertex(matrix, drawX, drawY, 0.0f).setColor(r, g, b, a);
            buffer.addVertex(matrix, drawX, drawY + drawH, 0.0f).setColor(r, g, b, a);
            buffer.addVertex(matrix, drawX + drawW, drawY + drawH, 0.0f).setColor(r, g, b, a);
            buffer.addVertex(matrix, drawX + drawW, drawY, 0.0f).setColor(r, g, b, a);

            MeshData mesh = buffer.build();
            if (mesh != null) {
                RenderSystem.setShader(ShaderUtils.SHADOW6_RECT);
                BufferUploader.drawWithShader(mesh);
            }

            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }

    public static final class CustomStyled {

        private CustomStyled() {
        }

        public static void drawGui(PoseStack stack, float x, float y, float width, float height,
                                    float radius, boolean blur, int blurStrength) {
            CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.ROUNDED_RECT);
            if (shader == null) {
                return;
            }

            int bgColor = 0xE60A0A0F;

            ShaderUtils.set(shader, "Size", width, height);
            ShaderUtils.set(shader, "Radius", radius, radius, radius, radius);
            ShaderUtils.set(shader, "Smoothness", 1.0f);
            ShaderUtils.set(shader, "ColorModulator", 1.0f, 1.0f, 1.0f, 1.0f);

            int alpha = (bgColor >> 24) & 0xFF;
            if (alpha == 0) alpha = 255;
            float r = ((bgColor >> 16) & 0xFF) / 255.0f;
            float g = ((bgColor >> 8) & 0xFF) / 255.0f;
            float b = (bgColor & 0xFF) / 255.0f;
            float a = alpha / 255.0f;

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();

            Matrix4f matrix = stack.last().pose();
            BufferBuilder buffer = Tesselator.getInstance()
                    .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            buffer.addVertex(matrix, x, y, 0.0f).setColor(r, g, b, a);
            buffer.addVertex(matrix, x, y + height, 0.0f).setColor(r, g, b, a);
            buffer.addVertex(matrix, x + width, y + height, 0.0f).setColor(r, g, b, a);
            buffer.addVertex(matrix, x + width, y, 0.0f).setColor(r, g, b, a);

            MeshData mesh = buffer.build();
            if (mesh != null) {
                RenderSystem.setShader(ShaderUtils.ROUNDED_RECT);
                BufferUploader.drawWithShader(mesh);
            }

            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }

        public static void draw(PoseStack stack, float x, float y, float width, float height,
                                 Round round, boolean blur, float blurStrength) {
            drawGui(stack, x, y, width, height, round.topLeft, blur, (int) blurStrength);
        }
    }

    private static void drawRectInternal(PoseStack stack, float x, float y, float width, float height,
                                          float tl, float tr, float br, float bl, int color) {
        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.ROUNDED_RECT);
        if (shader == null) {
            return;
        }

        ShaderUtils.set(shader, "Size", width, height);
        ShaderUtils.set(shader, "Radius", tl, tr, br, bl);
        ShaderUtils.set(shader, "Smoothness", 1.0f);
        ShaderUtils.set(shader, "ColorModulator", 1.0f, 1.0f, 1.0f, 1.0f);

        int alpha = (color >> 24) & 0xFF;
        if (alpha == 0) alpha = 255;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = alpha / 255.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        Matrix4f matrix = stack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        buffer.addVertex(matrix, x, y, 0.0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, x, y + height, 0.0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, x + width, y + height, 0.0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, x + width, y, 0.0f).setColor(r, g, b, a);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            RenderSystem.setShader(ShaderUtils.ROUNDED_RECT);
            BufferUploader.drawWithShader(mesh);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void drawGradientRectInternal(PoseStack stack, float x, float y, float width, float height,
                                                  float tl, float tr, float br, float bl,
                                                  int cTL, int cTR, int cBR, int cBL) {
        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.GRADIENT6_RECT);
        if (shader == null) {
            drawRectInternal(stack, x, y, width, height, tl, tr, br, bl, cTL);
            return;
        }

        ShaderUtils.set(shader, "Size", width, height);
        ShaderUtils.set(shader, "Radius", tl, tr, br, bl);
        ShaderUtils.set(shader, "Smoothness", 1.0f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        Matrix4f matrix = stack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        buffer.addVertex(matrix, x, y, 0.0f).setColor(
                ((cTL >> 16) & 0xFF) / 255.0f, ((cTL >> 8) & 0xFF) / 255.0f, (cTL & 0xFF) / 255.0f, ((cTL >> 24) & 0xFF) / 255.0f);
        buffer.addVertex(matrix, x, y + height, 0.0f).setColor(
                ((cBL >> 16) & 0xFF) / 255.0f, ((cBL >> 8) & 0xFF) / 255.0f, (cBL & 0xFF) / 255.0f, ((cBL >> 24) & 0xFF) / 255.0f);
        buffer.addVertex(matrix, x + width, y + height, 0.0f).setColor(
                ((cBR >> 16) & 0xFF) / 255.0f, ((cBR >> 8) & 0xFF) / 255.0f, (cBR & 0xFF) / 255.0f, ((cBR >> 24) & 0xFF) / 255.0f);
        buffer.addVertex(matrix, x + width, y, 0.0f).setColor(
                ((cTR >> 16) & 0xFF) / 255.0f, ((cTR >> 8) & 0xFF) / 255.0f, (cTR & 0xFF) / 255.0f, ((cTR >> 24) & 0xFF) / 255.0f);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            RenderSystem.setShader(ShaderUtils.GRADIENT6_RECT);
            BufferUploader.drawWithShader(mesh);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void drawOutlineInternal(PoseStack stack, float x, float y, float width, float height,
                                             float lineWidth, float tl, float tr, float br, float bl, int color) {
        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.ROUNDED_RECT_OUTLINE);
        if (shader == null) {
            return;
        }

        ShaderUtils.set(shader, "Size", width, height);
        ShaderUtils.set(shader, "Radius", tl, tr, br, bl);
        ShaderUtils.set(shader, "Smoothness", 1.0f);
        ShaderUtils.set(shader, "LineWidth", lineWidth);

        int alpha = (color >> 24) & 0xFF;
        if (alpha == 0) alpha = 255;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = alpha / 255.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        Matrix4f matrix = stack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        buffer.addVertex(matrix, x, y, 0.0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, x, y + height, 0.0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, x + width, y + height, 0.0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, x + width, y, 0.0f).setColor(r, g, b, a);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            RenderSystem.setShader(ShaderUtils.ROUNDED_RECT_OUTLINE);
            BufferUploader.drawWithShader(mesh);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}
