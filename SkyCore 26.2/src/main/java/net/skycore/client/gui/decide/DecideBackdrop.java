package net.skycore.client.gui.decide;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.skycore.client.util.BeginFrameHelper;
import net.skycore.client.util.CurrentGuiGraphicsExtractorHelper;
import net.skycore.client.util.GuiPipelines;
import net.skycore.client.util.ScissorAreaHelper;
import org.joml.Matrix3x2fc;

public final class DecideBackdrop {
    private static TextureTarget sceneCopy;

    private DecideBackdrop() {
    }

    private static GpuTextureView ensureSceneTarget() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || RenderSystem.tryGetDevice() == null || mc.gameRenderer == null) {
            return null;
        }
        RenderTarget main = mc.gameRenderer.mainRenderTarget();
        if (main == null || main.getColorTexture() == null || main.width <= 1 || main.height <= 1) {
            return null;
        }
        if (sceneCopy == null || sceneCopy.width != main.width || sceneCopy.height != main.height) {
            if (sceneCopy != null) {
                sceneCopy.destroyBuffers();
            }
            sceneCopy = new TextureTarget("skycore-decide-grayscale-scene", main.width, main.height, false, GpuFormat.RGBA8_UNORM);
        }
        return sceneCopy.getColorTextureView();
    }

    public static void captureSceneNow(RenderTarget sourceTarget) {
        if (sceneCopy == null || sourceTarget == null || sourceTarget.getColorTexture() == null) {
            return;
        }
        if (sceneCopy.width != sourceTarget.width || sceneCopy.height != sourceTarget.height) {
            return;
        }
        RenderSystem.getDevice()
                .createCommandEncoder()
                .copyTextureToTexture(
                        sourceTarget.getColorTexture(),
                        sceneCopy.getColorTexture(),
                        0, 0, 0, 0, 0,
                        sourceTarget.width,
                        sourceTarget.height
                );
    }

    public static void drawGrayscale(float w, float h, float state) {
        if (state <= 0.005f || w <= 0.0f || h <= 0.0f) {
            return;
        }
        GuiGraphicsExtractor extractor = CurrentGuiGraphicsExtractorHelper.currentGuiGraphicsExtractor();
        GpuTextureView view = ensureSceneTarget();
        if (extractor == null || view == null) {
            return;
        }
        int argb = DecideDraw.getColor(255, state);
        BeginFrameHelper.customElement(new GrayscaleState(extractor.pose(), w, h, argb, view, BeginFrameHelper.activeScissor()));
    }

    public static void drawRays(float w, float h, float timeSec, int colorA, int colorB) {
        if (w <= 0.0f || h <= 0.0f || (((colorA >>> 24) & 0xFF) == 0 && ((colorB >>> 24) & 0xFF) == 0)) {
            return;
        }
        GuiGraphicsExtractor extractor = CurrentGuiGraphicsExtractorHelper.currentGuiGraphicsExtractor();
        if (extractor == null) {
            return;
        }
        int rg = (((colorB >>> 16) & 0xFF)) | (((colorB >>> 8) & 0xFF) << 8);
        int ba = (colorB & 0xFF) | (((colorB >>> 24) & 0xFF) << 8);
        int wQuarter = Math.max(4, Math.round(w * 4.0f));
        BeginFrameHelper.customElement(new EffectState(
                extractor.pose(), w, h, timeSec, colorA, rg, ba, 1, wQuarter, h, BeginFrameHelper.activeScissor()
        ));
    }

    public static void drawHalftone(float w, float h, float mouseX, float mouseY, float uScale, float alpha01) {
        if (alpha01 <= 0.004f || w <= 0.0f || h <= 0.0f) {
            return;
        }
        GuiGraphicsExtractor extractor = CurrentGuiGraphicsExtractorHelper.currentGuiGraphicsExtractor();
        if (extractor == null) {
            return;
        }
        int color = DecideDraw.getColor(255, alpha01);
        int px = clampShort(Math.round(mouseX * 4.0f));
        int py = clampShort(Math.round(mouseY * 4.0f));
        int packedSpacing = clampShort(Math.round(6.0f * uScale * 64.0f));
        float reach = 100.0f * uScale;
        BeginFrameHelper.customElement(new EffectState(
                extractor.pose(), w, h, reach, color, px, py, 2, packedSpacing, uScale, BeginFrameHelper.activeScissor()
        ));
    }

    public static void drawScanlines(float w, float h, float step, float edgeFade, float phase, float alpha01) {
        if (alpha01 <= 0.004f || w <= 0.0f || h <= 0.0f) {
            return;
        }
        GuiGraphicsExtractor extractor = CurrentGuiGraphicsExtractorHelper.currentGuiGraphicsExtractor();
        if (extractor == null) {
            return;
        }
        int color = DecideDraw.getColor(255, alpha01);
        int packedStep = clampShort(Math.round(Math.max(0.5f, step) * 64.0f));
        int packedAreaW = clampShort(Math.round(w));
        int packedEdge = clampShort(Math.round(edgeFade * 16.0f));
        BeginFrameHelper.customElement(new EffectState(
                extractor.pose(), w, h, phase, color, packedStep, packedAreaW, 3, packedEdge, h, BeginFrameHelper.activeScissor()
        ));
    }

    private static int clampShort(int value) {
        return Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, value));
    }

    private static final class GrayscaleState extends ScissorAreaHelper {
        private final float w;
        private final float h;
        private final int color;
        private final TextureSetup setup;

        GrayscaleState(Matrix3x2fc pose, float w, float h, int color, GpuTextureView view, ScreenRectangle scissor) {
            super(pose, scissor, 0.0f, 0.0f, w, h);
            this.w = w;
            this.h = h;
            this.color = color;
            this.setup = TextureSetup.singleTexture(view, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
        }

        @Override
        public RenderPipeline pipeline() {
            return GuiPipelines.DECIDE_GRAYSCALE;
        }

        @Override
        public TextureSetup textureSetup() {
            return this.setup;
        }

        @Override
        public void buildVertices(VertexConsumer vc) {
            emit(vc, 0.0f, 0.0f);
            emit(vc, 0.0f, this.h);
            emit(vc, this.w, this.h);
            emit(vc, this.w, 0.0f);
        }

        private void emit(VertexConsumer vc, float x, float y) {
            vc.addVertex(transformX(x, y), transformY(x, y), 0.0f)
                    .setColor(this.color)
                    .setUv(0.0f, 0.0f)
                    .setUv1(0, 0)
                    .setUv2(0, 0)
                    .setNormal(0.0f, 0.0f, 1.0f)
                    .setLineWidth(0.0f);
        }
    }

    private static final class EffectState extends ScissorAreaHelper {
        private final float w;
        private final float h;
        private final float paramZ;
        private final int colorA;
        private final int uv1X;
        private final int uv1Y;
        private final int mode;
        private final int uv2Y;
        private final float lineWidth;

        EffectState(Matrix3x2fc pose, float w, float h, float paramZ, int colorA,
                    int uv1X, int uv1Y, int mode, int uv2Y, float lineWidth, ScreenRectangle scissor) {
            super(pose, scissor, 0.0f, 0.0f, w, h);
            this.w = w;
            this.h = h;
            this.paramZ = paramZ;
            this.colorA = colorA;
            this.uv1X = uv1X;
            this.uv1Y = uv1Y;
            this.mode = mode;
            this.uv2Y = uv2Y;
            this.lineWidth = lineWidth;
        }

        @Override
        public RenderPipeline pipeline() {
            return GuiPipelines.DECIDE_EFFECTS;
        }

        @Override
        public TextureSetup textureSetup() {
            return TextureSetup.noTexture();
        }

        @Override
        public void buildVertices(VertexConsumer vc) {
            emit(vc, 0.0f, 0.0f);
            emit(vc, 0.0f, this.h);
            emit(vc, this.w, this.h);
            emit(vc, this.w, 0.0f);
        }

        private void emit(VertexConsumer vc, float x, float y) {
            vc.addVertex(transformX(x, y), transformY(x, y), this.paramZ)
                    .setColor(this.colorA)
                    .setUv(x, y)
                    .setUv1(this.uv1X, this.uv1Y)
                    .setUv2(this.mode, this.uv2Y)
                    .setNormal(0.0f, 0.0f, 1.0f)
                    .setLineWidth(this.lineWidth);
        }
    }
}
