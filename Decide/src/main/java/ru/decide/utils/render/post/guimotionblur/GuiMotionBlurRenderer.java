/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.platform.DestFactor
 *  com.mojang.blaze3d.platform.SourceFactor
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_6367
 *  org.lwjgl.system.MemoryStack
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.utils.render.post.guimotionblur;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_6367;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.utils.render.others.FullscreenQuadBuffer;
import ru.decide.utils.render.others.RenderSampler;

@Environment(value=EnvType.CLIENT)
public final class GuiMotionBlurRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:GuiMotionBlur");
    private static final int MAX_MASK_RECTS = 192;
    public static final int MASK_RECT_STRIDE = 6;
    private static final int MASK_VERTICES_PER_RECT = 6;
    private static final VertexFormat MASK_FORMAT = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("UV1", VertexFormatElement.UV1).add("Color", VertexFormatElement.COLOR).build();
    private static final int MASK_VERTEX_STRIDE_BYTES = MASK_FORMAT.getVertexSize();
    private static final int MASK_VERTEX_BYTES = 1152 * MASK_VERTEX_STRIDE_BYTES;
    private static final class_2960 BLUR_PIPELINE_ID = GuiMotionBlurRenderer.id("pipeline/post/guimotionblur/gaussian");
    private static final class_2960 MASK_PIPELINE_ID = GuiMotionBlurRenderer.id("pipeline/post/guimotionblur/mask");
    private static final class_2960 MASK_REPLACE_PIPELINE_ID = GuiMotionBlurRenderer.id("pipeline/post/guimotionblur/mask_replace");
    private static final class_2960 COMPOSITE_PIPELINE_ID = GuiMotionBlurRenderer.id("pipeline/post/guimotionblur/composite");
    private static final class_2960 FULLSCREEN_SHADER = GuiMotionBlurRenderer.id("post/guimotionblur/fullscreen");
    private static final class_2960 GAUSSIAN_SHADER = GuiMotionBlurRenderer.id("post/guimotionblur/gaussian");
    private static final class_2960 MASK_SHADER = GuiMotionBlurRenderer.id("post/guimotionblur/mask");
    private static final class_2960 COMPOSITE_SHADER = GuiMotionBlurRenderer.id("post/guimotionblur/composite");
    private static final RenderPipeline BLUR_PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(BLUR_PIPELINE_ID).withVertexShader(FULLSCREEN_SHADER).withFragmentShader(GAUSSIAN_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withSampler("MotionInput").withUniform("MotionBlurParams", class_10789.field_60031).withoutBlend().withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withCull(false).build();
    private static final RenderPipeline MASK_PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(MASK_PIPELINE_ID).withVertexShader(MASK_SHADER).withFragmentShader(MASK_SHADER).withVertexFormat(MASK_FORMAT, VertexFormat.class_5596.field_27379).withBlend(new BlendFunction(SourceFactor.ONE, DestFactor.ONE)).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withCull(false).build();
    private static final RenderPipeline MASK_REPLACE_PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(MASK_REPLACE_PIPELINE_ID).withVertexShader(MASK_SHADER).withFragmentShader(MASK_SHADER).withVertexFormat(MASK_FORMAT, VertexFormat.class_5596.field_27379).withoutBlend().withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withCull(false).build();
    private static final RenderPipeline COMPOSITE_PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(COMPOSITE_PIPELINE_ID).withVertexShader(FULLSCREEN_SHADER).withFragmentShader(COMPOSITE_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withSampler("MotionScene").withSampler("MotionBackground").withSampler("MotionBlurred").withSampler("MotionBackgroundBlurred").withSampler("MotionMask").withUniform("MotionCompositeParams", class_10789.field_60031).withoutBlend().withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withCull(false).build();
    private static GpuBuffer fullscreenVertexBuffer;
    private static GpuBuffer maskVertexBuffer;
    private static GpuBuffer blurUniformBuffer;
    private static GpuBuffer compositeUniformBuffer;
    private static GpuTexture sceneCopyTexture;
    private static GpuTextureView sceneCopyTextureView;
    private static GpuTexture backgroundCopyTexture;
    private static GpuTextureView backgroundCopyTextureView;
    private static GpuTexture remoteBackgroundCopyTexture;
    private static GpuTextureView remoteBackgroundCopyTextureView;
    private static class_6367 maskTarget;
    private static class_6367 horizontalTarget;
    private static class_6367 verticalTarget;
    private static class_6367 backgroundHorizontalTarget;
    private static class_6367 backgroundVerticalTarget;
    private static int sourceWidth;
    private static int sourceHeight;
    private static boolean backgroundCaptured;
    private static class_276 backgroundCaptureTarget;
    private static boolean remoteBackgroundCaptured;
    private static class_276 remoteBackgroundCaptureTarget;
    private static boolean disabledAfterError;
    private static boolean pipelinesRegistered;

    private GuiMotionBlurRenderer() {
    }

    public static void shutdown() {
        GuiMotionBlurRenderer.releaseResources();
        disabledAfterError = false;
    }

    private static float clamp(float f, float min, float max) {
        return f < min ? min : Math.max(min, Math.min(max, f));
    }

    private static class_2960 id(String s) {
        return class_2960.method_60655((String)"decide", (String)s);
    }

    private static void closeBuffer(GpuBuffer buf) {
        if (buf != null) {
            buf.close();
        }
    }

    private static void composite(CommandEncoder ce, GpuTextureView fbView, GpuTextureView scene, GpuTextureView blurred, GpuTextureView bgView, GpuTextureView bgBlurred, float alpha, float blurVal, int sx, int sy, int sw, int sh, int fbW, int fbH, float[] rects, int rx, int ry, int rw, int rh, float scale, float origX, float origY) {
        if (scene == null || blurred == null || bgView == null || bgBlurred == null) {
            return;
        }
        int adjustedY = Math.max(0, fbH - (sy + sh));
        GuiMotionBlurRenderer.writeCompositeUniform(ce, alpha, blurVal, fbW, fbH, rects, rx, ry, rw, rh, scale, origX, origY);
        try (RenderPass rp = ce.createRenderPass(() -> "decidevisuals:gui_motion_blur_composite", scene, OptionalInt.empty(), null, OptionalDouble.empty());){
            rp.setPipeline(COMPOSITE_PIPELINE);
            rp.setVertexBuffer(0, fullscreenVertexBuffer);
            rp.enableScissor(sx, adjustedY, sw, sh);
            rp.bindTexture("MotionScene", scene, RenderSampler.linear());
            rp.bindTexture("MotionBackground", bgBlurred, RenderSampler.linear());
            rp.bindTexture("MotionBlurred", blurred, RenderSampler.linear());
            rp.bindTexture("MotionBackgroundBlurred", bgView, RenderSampler.linear());
            rp.bindTexture("MotionMask", fbView, RenderSampler.linear());
            rp.setUniform("MotionCompositeParams", compositeUniformBuffer.slice());
            rp.draw(0, 6);
        }
    }

    private static boolean ensureReady(int w, int h, float f) {
        try {
            GpuDevice device = RenderSystem.tryGetDevice();
            if (device == null) {
                return false;
            }
            if (!pipelinesRegistered) {
                class_10799.method_67887((RenderPipeline)BLUR_PIPELINE);
                class_10799.method_67887((RenderPipeline)MASK_PIPELINE);
                class_10799.method_67887((RenderPipeline)MASK_REPLACE_PIPELINE);
                class_10799.method_67887((RenderPipeline)COMPOSITE_PIPELINE);
                pipelinesRegistered = true;
            }
            if (fullscreenVertexBuffer == null || fullscreenVertexBuffer.isClosed()) {
                fullscreenVertexBuffer = FullscreenQuadBuffer.getOrCreate();
            }
            if (maskVertexBuffer == null || maskVertexBuffer.isClosed() || maskVertexBuffer.size() < (long)MASK_VERTEX_BYTES) {
                GuiMotionBlurRenderer.closeBuffer(maskVertexBuffer);
                maskVertexBuffer = device.createBuffer(() -> "decidevisuals:gui_motion_blur_mask_vertices", 40, (long)MASK_VERTEX_BYTES);
            }
            if (blurUniformBuffer == null || blurUniformBuffer.isClosed() || blurUniformBuffer.size() < 16L) {
                GuiMotionBlurRenderer.closeBuffer(blurUniformBuffer);
                blurUniformBuffer = device.createBuffer(() -> "decidevisuals:gui_motion_blur_uniform", 136, 16L);
            }
            if (compositeUniformBuffer == null || compositeUniformBuffer.isClosed() || compositeUniformBuffer.size() < 80L) {
                GuiMotionBlurRenderer.closeBuffer(compositeUniformBuffer);
                compositeUniformBuffer = device.createBuffer(() -> "decidevisuals:gui_motion_blur_composite_uniform", 136, 80L);
            }
            GuiMotionBlurRenderer.ensureTargets(device, w, h, f);
            return fullscreenVertexBuffer != null && maskVertexBuffer != null && blurUniformBuffer != null && compositeUniformBuffer != null && sceneCopyTextureView != null && backgroundCopyTextureView != null && remoteBackgroundCopyTextureView != null && maskTarget != null && horizontalTarget != null && verticalTarget != null && backgroundHorizontalTarget != null && backgroundVerticalTarget != null;
        }
        catch (Throwable t) {
            GuiMotionBlurRenderer.disableAfterError(t);
            return false;
        }
    }

    public static void captureBackground(class_276 fb, float f) {
        boolean isMain;
        class_310 mc = class_310.method_1551();
        boolean bl = isMain = mc != null && fb == mc.method_1522();
        if (disabledAfterError || !Float.isFinite(f)) {
            GuiMotionBlurRenderer.clearBackgroundCapture(isMain);
            return;
        }
        if (fb == null || fb.field_1482 <= 0 || fb.field_1481 <= 0 || fb.method_30277() == null) {
            GuiMotionBlurRenderer.clearBackgroundCapture(isMain);
            return;
        }
        if (!GuiMotionBlurRenderer.ensureReady(fb.field_1482, fb.field_1481, 1.0f)) {
            GuiMotionBlurRenderer.clearBackgroundCapture(isMain);
            return;
        }
        try {
            RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(fb.method_30277(), isMain ? backgroundCopyTexture : remoteBackgroundCopyTexture, 0, 0, 0, 0, 0, fb.field_1482, fb.field_1481);
            if (isMain) {
                backgroundCaptured = true;
                backgroundCaptureTarget = fb;
            } else {
                remoteBackgroundCaptured = true;
                remoteBackgroundCaptureTarget = fb;
            }
        }
        catch (Throwable t) {
            GuiMotionBlurRenderer.clearBackgroundCapture(isMain);
            GuiMotionBlurRenderer.disableAfterError(t);
        }
    }

    public static void captureBackground(float f) {
        class_310 mc = class_310.method_1551();
        if (mc != null && mc.field_1773 != null && mc.method_1522() != null) {
            GuiMotionBlurRenderer.captureBackground(mc.method_1522(), f);
        } else {
            backgroundCaptured = false;
            backgroundCaptureTarget = null;
        }
    }

    public static void applyWithCopy(class_276 fb, float alpha, float blurVal, int sx, int sy, int sw, int sh, float[] rects, int mask, int rx, int ry, int rw, int rh, float renderScale, float origX, float origY, boolean replace) {
        if (disabledAfterError) {
            return;
        }
        float a = GuiMotionBlurRenderer.clamp(alpha, 0.0f, 1.0f);
        float rs = Float.isFinite(renderScale) ? GuiMotionBlurRenderer.clamp(renderScale, 0.25f, 4.0f) : 1.0f;
        boolean hasScale = Math.abs(rs - 1.0f) > 5.0E-4f;
        int rectCount = GuiMotionBlurRenderer.safeMaskRectCount(rects, mask);
        if ((hasScale || !(a <= 0.002f)) && Float.isFinite(blurVal) && rectCount > 0 && fb != null && fb.field_1482 > 0 && fb.field_1481 > 0 && fb.method_30277() != null && fb.method_71639() != null) {
            int csx = GuiMotionBlurRenderer.clampInt(sx, 0, fb.field_1482);
            int csy = GuiMotionBlurRenderer.clampInt(sy, 0, fb.field_1481);
            int csx2 = GuiMotionBlurRenderer.clampInt(sx + sw, csx, fb.field_1482);
            int csy2 = GuiMotionBlurRenderer.clampInt(sy + sh, csy, fb.field_1481);
            int csw = csx2 - csx;
            int csh = csy2 - csy;
            if (csw > 0 && csh > 0) {
                int crx = hasScale ? GuiMotionBlurRenderer.clampInt(rx, 0, fb.field_1482) : 0;
                int cry = hasScale ? GuiMotionBlurRenderer.clampInt(ry, 0, fb.field_1481) : 0;
                int crx2 = hasScale ? GuiMotionBlurRenderer.clampInt(rx + rw, crx, fb.field_1482) : 0;
                int cry2 = hasScale ? GuiMotionBlurRenderer.clampInt(ry + rh, cry, fb.field_1481) : 0;
                int crw = crx2 - crx;
                int crh = cry2 - cry;
                if (!hasScale || crw > 0 && crh > 0) {
                    float b = GuiMotionBlurRenderer.clamp(blurVal, 0.0f, 48.0f);
                    if (GuiMotionBlurRenderer.ensureReady(fb.field_1482, fb.field_1481, 1.0f)) {
                        boolean isRemote;
                        boolean isBg = backgroundCaptured && fb == backgroundCaptureTarget;
                        boolean bl = isRemote = remoteBackgroundCaptured && fb == remoteBackgroundCaptureTarget;
                        if (isBg || isRemote) {
                            GpuTextureView bgView = isBg ? backgroundCopyTextureView : remoteBackgroundCopyTextureView;
                            GuiMotionBlurRenderer.clearBackgroundCapture(isBg);
                            try {
                                CommandEncoder ce = RenderSystem.getDevice().createCommandEncoder();
                                ce.copyTextureToTexture(fb.method_30277(), sceneCopyTexture, 0, 0, 0, 0, 0, fb.field_1482, fb.field_1481);
                                GuiMotionBlurRenderer.gaussianChain(ce, sceneCopyTextureView, fb.field_1482, fb.field_1481, horizontalTarget, verticalTarget, b, csx, csy, csw, csh);
                                GuiMotionBlurRenderer.gaussianChain(ce, bgView, fb.field_1482, fb.field_1481, backgroundHorizontalTarget, backgroundVerticalTarget, b, csx, csy, csw, csh);
                                GuiMotionBlurRenderer.renderMask(ce, rects, rectCount, fb.field_1482, fb.field_1481, replace);
                                GuiMotionBlurRenderer.composite(ce, fb.method_71639(), sceneCopyTextureView, verticalTarget.method_71639(), bgView, backgroundVerticalTarget.method_71639(), a, b, csx, csy, csw, csh, fb.field_1482, fb.field_1481, rects, crx, cry, crw, crh, rs, origX, origY);
                            }
                            catch (Throwable t) {
                                GuiMotionBlurRenderer.disableAfterError(t);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void applyWithCopy(float alpha, float blurVal, int sx, int sy, int sw, int sh, float[] rects, int mask, int rx, int ry, int rw, int rh, float renderScale, float origX, float origY) {
        class_310 mc = class_310.method_1551();
        if (mc != null && mc.field_1773 != null && mc.method_1522() != null) {
            GuiMotionBlurRenderer.applyWithCopy(mc.method_1522(), alpha, blurVal, sx, sy, sw, sh, rects, mask, rx, ry, rw, rh, renderScale, origX, origY, false);
        }
    }

    public static void applyWithCopy(class_276 fb, float alpha, float blurVal, float bx, float by, float bw, float bh, int mask, int cardCount, float ox, float oy, float ow, float oh, float renderScale, float origX, float origY) {
        GuiMotionBlurRenderer.applyWithCopy(fb, alpha, blurVal, (int)bx, (int)by, (int)bw, (int)bh, null, mask, (int)ox, (int)oy, (int)ow, (int)oh, renderScale, origX, origY, false);
    }

    private static void gaussianChain(CommandEncoder ce, GpuTextureView input, int w, int h, class_6367 hTarget, class_6367 vTarget, float blurVal, int sx, int sy, int sw, int sh) {
        float scaledBlur = blurVal / 16.0f;
        int pad = (int)Math.ceil(blurVal) + 8;
        int cx = GuiMotionBlurRenderer.clampInt(sx - pad, 0, w);
        int cy = GuiMotionBlurRenderer.clampInt(sy - pad, 0, h);
        int cx2 = GuiMotionBlurRenderer.clampInt(sx + sw + pad, cx, w);
        int cy2 = GuiMotionBlurRenderer.clampInt(sy + sh + pad, cy, h);
        if (cx2 - cx > 0 && cy2 - cy > 0) {
            GuiMotionBlurRenderer.gaussianPass(ce, input, hTarget, scaledBlur / (float)Math.max(1, w), 0.0f, cx, cy, cx2 - cx, cy2 - cy);
            GuiMotionBlurRenderer.gaussianPass(ce, hTarget.method_71639(), vTarget, 0.0f, scaledBlur / (float)Math.max(1, h), cx, cy, cx2 - cx, cy2 - cy);
        }
    }

    private static void clearBackgroundCapture(boolean isMain) {
        if (isMain) {
            backgroundCaptured = false;
            backgroundCaptureTarget = null;
        } else {
            remoteBackgroundCaptured = false;
            remoteBackgroundCaptureTarget = null;
        }
    }

    private static void releaseResources() {
        GuiMotionBlurRenderer.closeTargets();
        GuiMotionBlurRenderer.closeBuffer(maskVertexBuffer);
        GuiMotionBlurRenderer.closeBuffer(blurUniformBuffer);
        GuiMotionBlurRenderer.closeBuffer(compositeUniformBuffer);
        maskVertexBuffer = null;
        blurUniformBuffer = null;
        compositeUniformBuffer = null;
        fullscreenVertexBuffer = null;
    }

    private static int safeMaskRectCount(float[] rects, int n) {
        return rects != null && n > 0 ? Math.min(Math.min(n, 192), rects.length / 6) : 0;
    }

    private static int writeMaskVertices(ByteBuffer buf, float[] rects, int count, int w, int h) {
        int written = 0;
        int n = GuiMotionBlurRenderer.safeMaskRectCount(rects, count);
        float fw = Math.max(1.0f, (float)w);
        float fh = Math.max(1.0f, (float)h);
        for (int i = 0; i < n; ++i) {
            int off = i * 6;
            if (rects[off + 4] < 0.0f) continue;
            float rw = rects[off + 2];
            float rh = rects[off + 3];
            if (rw <= 0.5f || rh <= 0.5f) continue;
            float rx = rects[off] - 8.0f;
            float ry = rects[off + 1] - 8.0f;
            float rx2 = rects[off] + rw + 8.0f;
            float ry2 = rects[off + 1] + rh + 8.0f;
            float nx1 = rx / fw * 2.0f - 1.0f;
            float nx2 = rx2 / fw * 2.0f - 1.0f;
            float ny1 = 1.0f - ry / fh * 2.0f;
            float ny2 = 1.0f - ry2 / fh * 2.0f;
            float cr = Math.min(8.0f, Math.min(rw, rh) * 0.5f);
            int cw = Math.min(Short.MAX_VALUE, Math.round(rw * 8.0f));
            int ch = Math.min(Short.MAX_VALUE, Math.round(rh * 8.0f));
            GuiMotionBlurRenderer.putVertex(buf, nx1, ny2, cr, -8.0f, rh + 8.0f, cw, ch, Math.round(GuiMotionBlurRenderer.clamp(rects[off + 4], 0.0f, 1.0f) * 255.0f), Math.round(GuiMotionBlurRenderer.clamp(rects[off + 5], 0.0f, 1.0f) * 255.0f));
            GuiMotionBlurRenderer.putVertex(buf, nx2, ny2, cr, rw + 8.0f, rh + 8.0f, cw, ch, Math.round(GuiMotionBlurRenderer.clamp(rects[off + 4], 0.0f, 1.0f) * 255.0f), Math.round(GuiMotionBlurRenderer.clamp(rects[off + 5], 0.0f, 1.0f) * 255.0f));
            GuiMotionBlurRenderer.putVertex(buf, nx2, ny1, cr, rw + 8.0f, -8.0f, cw, ch, Math.round(GuiMotionBlurRenderer.clamp(rects[off + 4], 0.0f, 1.0f) * 255.0f), Math.round(GuiMotionBlurRenderer.clamp(rects[off + 5], 0.0f, 1.0f) * 255.0f));
            GuiMotionBlurRenderer.putVertex(buf, nx1, ny2, cr, -8.0f, rh + 8.0f, cw, ch, Math.round(GuiMotionBlurRenderer.clamp(rects[off + 4], 0.0f, 1.0f) * 255.0f), Math.round(GuiMotionBlurRenderer.clamp(rects[off + 5], 0.0f, 1.0f) * 255.0f));
            GuiMotionBlurRenderer.putVertex(buf, nx2, ny1, cr, rw + 8.0f, -8.0f, cw, ch, Math.round(GuiMotionBlurRenderer.clamp(rects[off + 4], 0.0f, 1.0f) * 255.0f), Math.round(GuiMotionBlurRenderer.clamp(rects[off + 5], 0.0f, 1.0f) * 255.0f));
            GuiMotionBlurRenderer.putVertex(buf, nx1, ny1, cr, -8.0f, -8.0f, cw, ch, Math.round(GuiMotionBlurRenderer.clamp(rects[off + 4], 0.0f, 1.0f) * 255.0f), Math.round(GuiMotionBlurRenderer.clamp(rects[off + 5], 0.0f, 1.0f) * 255.0f));
            written += 6;
        }
        return written;
    }

    private static void closeTargets() {
        if (sceneCopyTextureView != null) {
            sceneCopyTextureView.close();
            sceneCopyTextureView = null;
        }
        if (sceneCopyTexture != null) {
            sceneCopyTexture.close();
            sceneCopyTexture = null;
        }
        if (backgroundCopyTextureView != null) {
            backgroundCopyTextureView.close();
            backgroundCopyTextureView = null;
        }
        if (backgroundCopyTexture != null) {
            backgroundCopyTexture.close();
            backgroundCopyTexture = null;
        }
        if (remoteBackgroundCopyTextureView != null) {
            remoteBackgroundCopyTextureView.close();
            remoteBackgroundCopyTextureView = null;
        }
        if (remoteBackgroundCopyTexture != null) {
            remoteBackgroundCopyTexture.close();
            remoteBackgroundCopyTexture = null;
        }
        if (maskTarget != null) {
            maskTarget.method_1238();
            maskTarget = null;
        }
        if (horizontalTarget != null) {
            horizontalTarget.method_1238();
            horizontalTarget = null;
        }
        if (verticalTarget != null) {
            verticalTarget.method_1238();
            verticalTarget = null;
        }
        if (backgroundHorizontalTarget != null) {
            backgroundHorizontalTarget.method_1238();
            backgroundHorizontalTarget = null;
        }
        if (backgroundVerticalTarget != null) {
            backgroundVerticalTarget.method_1238();
            backgroundVerticalTarget = null;
        }
        sourceWidth = -1;
        sourceHeight = -1;
        backgroundCaptured = false;
        backgroundCaptureTarget = null;
        remoteBackgroundCaptured = false;
        remoteBackgroundCaptureTarget = null;
    }

    private static void gaussianPass(CommandEncoder ce, GpuTextureView input, class_6367 output, float dx, float dy, int sx, int sy, int sw, int sh) {
        if (input == null || output == null || output.method_71639() == null) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer buf = stack.calloc(16);
            buf.putFloat(0, dx);
            buf.putFloat(4, dy);
            buf.position(0);
            ce.writeToBuffer(blurUniformBuffer.slice(0L, 16L), buf);
        }
        try (RenderPass rp = ce.createRenderPass(() -> "decidevisuals:gui_motion_blur_gaussian", output.method_71639(), OptionalInt.empty(), null, OptionalDouble.empty());){
            rp.setPipeline(BLUR_PIPELINE);
            rp.setVertexBuffer(0, fullscreenVertexBuffer);
            int adjY = Math.max(0, output.field_1481 - (sy + sh));
            rp.enableScissor(sx, adjY, sw, sh);
            rp.bindTexture("MotionInput", input, RenderSampler.linear());
            rp.setUniform("MotionBlurParams", blurUniformBuffer.slice(0L, 16L));
            rp.draw(0, 6);
        }
    }

    private static void ensureTargets(GpuDevice device, int w, int h, float f) {
        if (sceneCopyTextureView != null && backgroundCopyTextureView != null && remoteBackgroundCopyTextureView != null && sourceWidth == w && sourceHeight == h && maskTarget != null && GuiMotionBlurRenderer.maskTarget.field_1482 == w && GuiMotionBlurRenderer.maskTarget.field_1481 == h) {
            return;
        }
        GuiMotionBlurRenderer.closeTargets();
        sceneCopyTexture = device.createTexture(() -> "decidevisuals:gui_motion_blur_scene_copy", 5, TextureFormat.RGBA8, w, h, 1, 1);
        sceneCopyTextureView = device.createTextureView(sceneCopyTexture);
        backgroundCopyTexture = device.createTexture(() -> "decidevisuals:gui_motion_blur_background_copy", 5, TextureFormat.RGBA8, w, h, 1, 1);
        backgroundCopyTextureView = device.createTextureView(backgroundCopyTexture);
        remoteBackgroundCopyTexture = device.createTexture(() -> "decidevisuals:gui_motion_blur_remote_bg_copy", 5, TextureFormat.RGBA8, w, h, 1, 1);
        remoteBackgroundCopyTextureView = device.createTextureView(remoteBackgroundCopyTexture);
        maskTarget = new class_6367("decidevisuals_gui_motion_blur_mask", w, h, false);
        horizontalTarget = new class_6367("decidevisuals_gui_motion_blur_scene_h", w, h, false);
        verticalTarget = new class_6367("decidevisuals_gui_motion_blur_scene_result", w, h, false);
        backgroundHorizontalTarget = new class_6367("decidevisuals_gui_motion_blur_bg_h", w, h, false);
        backgroundVerticalTarget = new class_6367("decidevisuals_gui_motion_blur_bg_result", w, h, false);
        sourceWidth = w;
        sourceHeight = h;
    }

    private static void writeCompositeUniform(CommandEncoder ce, float alpha, float blurVal, int fbW, int fbH, float[] rects, int rx, int ry, int rw, int rh, float scale, float origX, float origY) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer buf = stack.calloc(80);
            float rrx = 0.0f;
            float rry = 0.0f;
            float rrw = fbW;
            float rrh = fbH;
            if (rects != null && rects.length >= 4) {
                rrx = rects[0];
                rry = rects[1];
                rrw = Math.max(0.0f, rects[2]);
                rrh = Math.max(0.0f, rects[3]);
            }
            buf.putFloat(0, alpha);
            buf.putFloat(4, 0.035f);
            buf.putFloat(8, 1.0f);
            buf.putFloat(12, 10.0f);
            buf.putFloat(16, rrx);
            buf.putFloat(20, rry);
            buf.putFloat(24, rrw);
            buf.putFloat(28, rrh);
            buf.putFloat(32, fbW);
            buf.putFloat(36, fbH);
            buf.putFloat(40, 18.0f);
            buf.putFloat(44, scale);
            buf.putFloat(48, rx);
            buf.putFloat(52, ry);
            buf.putFloat(56, Math.max(0, rw));
            buf.putFloat(60, Math.max(0, rh));
            buf.putFloat(64, origX);
            buf.putFloat(68, origY);
            buf.putFloat(72, Math.max(0.0f, blurVal));
            buf.putFloat(76, 0.0f);
            buf.position(0);
            ce.writeToBuffer(compositeUniformBuffer.slice(0L, 80L), buf);
        }
    }

    private static void disableAfterError(Throwable t) {
        LOGGER.error("[GuiMotionBlur] disabled after render error", t);
        backgroundCaptured = false;
        GuiMotionBlurRenderer.releaseResources();
        disabledAfterError = true;
    }

    private static int clampInt(int v, int min, int max) {
        return v < min ? min : Math.max(min, Math.min(max, v));
    }

    private static void renderMask(CommandEncoder ce, float[] rects, int count, int w, int h, boolean replace) {
        int written;
        if (rects == null || maskTarget == null || maskTarget.method_71639() == null || maskVertexBuffer == null) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer buf = stack.malloc(count * 6 * MASK_VERTEX_STRIDE_BYTES);
            written = GuiMotionBlurRenderer.writeMaskVertices(buf, rects, count, w, h);
            if (written > 0) {
                buf.flip();
                ce.writeToBuffer(maskVertexBuffer.slice(0L, (long)buf.remaining()), buf);
            }
        }
        try (RenderPass rp = ce.createRenderPass(() -> "decidevisuals:gui_motion_blur_mask", maskTarget.method_71639(), OptionalInt.of(0), null, OptionalDouble.empty());){
            if (written > 0) {
                rp.setPipeline(replace ? MASK_REPLACE_PIPELINE : MASK_PIPELINE);
                rp.setVertexBuffer(0, maskVertexBuffer);
                rp.draw(0, written);
            }
        }
    }

    private static void putVertex(ByteBuffer buf, float x, float y, float z, float u, float v, int cu, int cv, int cr, int ca) {
        buf.putFloat(x);
        buf.putFloat(y);
        buf.putFloat(z);
        buf.putFloat(u);
        buf.putFloat(v);
        buf.putShort((short)cu);
        buf.putShort((short)cv);
        buf.put((byte)cr);
        buf.put((byte)ca);
        buf.put((byte)0);
        buf.put((byte)-1);
    }

    static {
        sourceWidth = -1;
        sourceHeight = -1;
    }
}

