package swerve.utils.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import swerve.Swerve;
import swerve.modules.impl.render.ViewModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.world.InteractionHand;

// Порт ItemOutlineRenderer из Kimiko: силуэт схваченного предмета -> раздельное по осям
// расстояние до силуэта (dt_h, dt_v) -> три пульсирующие изолинии поверх кадра.
public final class HandOutline {

    private static final float MAX_DIST = 20.0F;
    private static final float RADIUS = 20.0F;
    private static final float THICKNESS = 2.0F;
    private static final float LEVEL_0 = 5.0F;
    private static final float LEVEL_1 = 10.0F;
    private static final float LEVEL_2 = 15.0F;
    private static final float MIN_ALPHA = 0.001F;
    private static final int SILHOUETTE_BUFFER_SIZE = 1 << 16;

    private static final ShaderProgram DT_H = ShaderUtils.register("handoutline", "dt_h", DefaultVertexFormat.POSITION);
    private static final ShaderProgram DT_V = ShaderUtils.register("handoutline", "dt_v", DefaultVertexFormat.POSITION);
    private static final ShaderProgram ISOLINE = ShaderUtils.register("handoutline", "isoline", DefaultVertexFormat.POSITION);

    private static RenderTarget silhouette;
    private static RenderTarget swap;
    private static ByteBufferBuilder silhouetteBytes;
    private static MultiBufferSource.BufferSource silhouetteBuffers;

    private static boolean disabled;
    private static boolean clearedThisFrame;
    private static boolean hasSilhouette;
    private static boolean capturing;

    private HandOutline() {
    }

    public static RenderTarget redirectTarget(RenderTarget original) {
        return capturing ? silhouette : original;
    }

    public static void beginFrame() {
        clearedThisFrame = false;
        hasSilhouette = false;
    }

    // Возвращает буфер силуэта, если рука сейчас подсвечивается, иначе null.
    // Между begin и end привязан FBO силуэта — вызывающий обязан вызвать end().
    public static MultiBufferSource begin(InteractionHand hand) {
        if (disabled || capturing || hand == null || ViewModel.outlineAlpha(hand) <= MIN_ALPHA) {
            return null;
        }
        try {
            RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
            if (!ensureTargets(main.width, main.height)) {
                return null;
            }
            if (!clearedThisFrame) {
                silhouette.clear();
                clearedThisFrame = true;
            }
            silhouette.bindWrite(true);
            capturing = true;
            return silhouetteBuffers;
        } catch (Throwable throwable) {
            fail(throwable);
            return null;
        }
    }

    public static void end() {
        if (!capturing) {
            return;
        }
        capturing = false;
        try {
            silhouetteBuffers.endBatch();
            hasSilhouette = true;
        } catch (Throwable throwable) {
            fail(throwable);
        } finally {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        }
    }

    public static void composite() {
        if (disabled || !hasSilhouette) {
            return;
        }
        hasSilhouette = false;
        float alpha = Math.max(ViewModel.outlineAlpha(InteractionHand.MAIN_HAND), ViewModel.outlineAlpha(InteractionHand.OFF_HAND));
        if (alpha <= MIN_ALPHA) {
            return;
        }

        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        try {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.disableBlend();

            float texelX = 1.0F / silhouette.width;
            float texelY = 1.0F / silhouette.height;

            CompiledShaderProgram shader = RenderSystem.setShader(DT_H);
            if (shader == null) {
                return;
            }
            setDistanceUniforms(shader, texelX, texelY);
            fullscreen(swap, silhouette.getColorTextureId());

            shader = RenderSystem.setShader(DT_V);
            if (shader == null) {
                return;
            }
            setDistanceUniforms(shader, texelX, texelY);
            fullscreen(silhouette, swap.getColorTextureId());

            shader = RenderSystem.setShader(ISOLINE);
            if (shader == null) {
                return;
            }
            ShaderUtils.set(shader, "MaxDist", MAX_DIST);
            ShaderUtils.set(shader, "Thickness", THICKNESS);
            ShaderUtils.set(shader, "Alpha", alpha);
            ShaderUtils.set(shader, "Phase", (System.currentTimeMillis() % 1000L) / 1000.0F);
            ShaderUtils.set(shader, "Levels", LEVEL_0, LEVEL_1, LEVEL_2);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.colorMask(true, true, true, false);
            fullscreen(main, silhouette.getColorTextureId());
        } catch (Throwable throwable) {
            fail(throwable);
        } finally {
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, 0);
            main.bindWrite(true);
        }
    }

    private static void setDistanceUniforms(CompiledShaderProgram shader, float texelX, float texelY) {
        ShaderUtils.set(shader, "TexelSize", texelX, texelY);
        ShaderUtils.set(shader, "MaxDist", MAX_DIST);
        ShaderUtils.set(shader, "Radius", RADIUS);
    }

    // Квад 0..1 — вершинный шейдер сам растягивает его на весь вьюпорт, матрицы не нужны.
    private static void fullscreen(RenderTarget target, int sourceTexture) {
        target.bindWrite(true);
        RenderSystem.setShaderTexture(0, sourceTexture);
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        builder.addVertex(0.0F, 0.0F, 0.0F);
        builder.addVertex(1.0F, 0.0F, 0.0F);
        builder.addVertex(1.0F, 1.0F, 0.0F);
        builder.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.drawWithShader(builder.buildOrThrow());
    }

    private static boolean ensureTargets(int width, int height) {
        if (width <= 0 || height <= 0) {
            return false;
        }
        if (silhouetteBuffers == null) {
            silhouetteBytes = new ByteBufferBuilder(SILHOUETTE_BUFFER_SIZE);
            silhouetteBuffers = MultiBufferSource.immediate(silhouetteBytes);
        }
        if (silhouette == null) {
            silhouette = createTarget(width, height);
            swap = createTarget(width, height);
            return true;
        }
        if (silhouette.width != width || silhouette.height != height) {
            silhouette.resize(width, height);
            swap.resize(width, height);
        }
        return true;
    }

    private static RenderTarget createTarget(int width, int height) {
        RenderTarget target = new TextureTarget(width, height, false);
        target.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        target.setFilterMode(GlConst.GL_NEAREST);
        return target;
    }

    private static void fail(Throwable throwable) {
        disabled = true;
        capturing = false;
        hasSilhouette = false;
        Swerve.LOGGER.error("Hand outline disabled after render error", throwable);
        if (silhouette != null) {
            silhouette.destroyBuffers();
            silhouette = null;
        }
        if (swap != null) {
            swap.destroyBuffers();
            swap = null;
        }
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }
}
