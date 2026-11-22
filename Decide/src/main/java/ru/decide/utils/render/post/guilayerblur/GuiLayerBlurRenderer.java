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
 *  com.mojang.blaze3d.textures.GpuTextureView
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
 *  org.joml.Matrix4f
 *  org.lwjgl.system.MemoryStack
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.utils.render.post.guilayerblur;

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
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.utils.render.others.RenderSampler;
import ru.decide.utils.render.post.GuiShatterAnimation;

@Environment(value=EnvType.CLIENT)
public final class GuiLayerBlurRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:GuiLayerBlur");
    private static final int BLUR_UNIFORM_BYTES = 16;
    private static final int COMPOSITE_UNIFORM_BYTES = 16;
    private static final float TAPS_HALF = 16.0f;
    private static final class_2960 BLUR_PIPELINE_ID = GuiLayerBlurRenderer.id("pipeline/post/guilayerblur/gaussian");
    private static final class_2960 COMPOSITE_PIPELINE_ID = GuiLayerBlurRenderer.id("pipeline/post/guilayerblur/composite");
    private static final class_2960 FULLSCREEN = GuiLayerBlurRenderer.id("post/guilayerblur/fullscreen");
    private static final class_2960 SHARD_PIPELINE_ID = GuiLayerBlurRenderer.id("pipeline/post/guilayerblur/shard");
    private static final class_2960 SHARD_SHADER = GuiLayerBlurRenderer.id("post/guilayerblur/shard");
    private static final class_2960 GAUSSIAN_SHADER = GuiLayerBlurRenderer.id("post/guilayerblur/gaussian");
    private static final class_2960 COMPOSITE_SHADER = GuiLayerBlurRenderer.id("post/guilayerblur/composite");
    private static final VertexFormat SHARD_FORMAT = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("Color", VertexFormatElement.COLOR).build();
    private static RenderPipeline blurPipeline;
    private static RenderPipeline compositePipeline;
    private static RenderPipeline shardPipeline;
    private static GpuBuffer blurUniform;
    private static GpuBuffer compositeUniform;
    private static GpuBuffer shardVertexBuffer;
    private static ByteBuffer shardVertexData;
    private static final Matrix4f IDENTITY_MATRIX;
    private static class_6367 guiFbo;
    private static class_6367 tempH;
    private static class_6367 tempV;
    private static int texWidth;
    private static int texHeight;
    private static boolean captureActive;
    private static boolean panelRangeActive;
    private static boolean disabledAfterError;

    private GuiLayerBlurRenderer() {
    }

    public static void shutdown() {
        captureActive = false;
        GuiLayerBlurRenderer.closeTargets();
    }

    private static class_2960 id(String s) {
        return class_2960.method_60655((String)"decide", (String)s);
    }

    private static class_6367 destroy(class_6367 t) {
        if (t != null) {
            t.method_1238();
        }
        return null;
    }

    public static boolean available() {
        return !disabledAfterError;
    }

    private static float clamp01(float f) {
        return Math.max(0.0f, Math.min(1.0f, f));
    }

    private static boolean drawShards(CommandEncoder ce, GpuTextureView guiTex, float progress, float shatterAlpha, float screenScale, boolean worldMode) {
        if (shardPipeline == null || shardVertexBuffer == null || shardVertexData == null || tempH == null) {
            return false;
        }
        int count = GuiShatterAnimation.buildGeometry(shardVertexData, progress, screenScale, screenScale, worldMode);
        if (count <= 0 || shardVertexData.remaining() <= 0) {
            return false;
        }
        ce.writeToBuffer(shardVertexBuffer.slice(0L, (long)shardVertexData.remaining()), shardVertexData);
        try (RenderPass rp = ce.createRenderPass(() -> "decidevisuals:gui_capture_shards", tempH.method_71639(), OptionalInt.of(0));){
            rp.setPipeline(shardPipeline);
            rp.setVertexBuffer(0, shardVertexBuffer);
            rp.bindTexture("uGui", guiTex, RenderSampler.linear());
            rp.draw(0, count);
        }
        return true;
    }

    public static class_276 captureTarget() {
        return captureActive && panelRangeActive && guiFbo != null ? guiFbo : null;
    }

    public static void beginCapture(boolean enable) {
        class_276 fb;
        captureActive = false;
        panelRangeActive = false;
        if (!enable || disabledAfterError) {
            return;
        }
        class_310 mc = class_310.method_1551();
        class_276 class_2762 = fb = mc != null ? mc.method_1522() : null;
        if (fb == null || fb.method_71639() == null || fb.field_1482 <= 0 || fb.field_1481 <= 0) {
            return;
        }
        try {
            if (!GuiLayerBlurRenderer.ensurePipelines() || !GuiLayerBlurRenderer.ensureTargets(fb.field_1482, fb.field_1481)) {
                return;
            }
            CommandEncoder ce = RenderSystem.getDevice().createCommandEncoder();
            RenderPass rp = ce.createRenderPass(() -> "decidevisuals:gui_capture_clear", guiFbo.method_71639(), OptionalInt.of(0), guiFbo.method_71640(), OptionalDouble.of(1.0));
            if (rp != null) {
                rp.close();
            }
            captureActive = true;
        }
        catch (Throwable t) {
            GuiLayerBlurRenderer.disableAfterError(t);
        }
    }

    public static void markPanelRange() {
        if (captureActive) {
            panelRangeActive = true;
        }
    }

    public static void composite(float screenScale, float blurRadius) {
        class_276 fb;
        if (!captureActive || guiFbo == null || disabledAfterError) {
            captureActive = false;
            return;
        }
        captureActive = false;
        class_310 mc = class_310.method_1551();
        class_276 class_2762 = fb = mc != null ? mc.method_1522() : null;
        if (fb == null || fb.method_71639() == null || compositePipeline == null) {
            return;
        }
        int w = fb.field_1482;
        int h = fb.field_1481;
        if (w != texWidth || h != texHeight) {
            return;
        }
        try {
            boolean hasShatter;
            CommandEncoder ce = RenderSystem.getDevice().createCommandEncoder();
            float shatterP = GuiShatterAnimation.progress(0.0f);
            boolean bl = hasShatter = GuiShatterAnimation.isActive() && shatterP > 0.0f && tempH != null && tempV != null;
            if (hasShatter) {
                if (!GuiLayerBlurRenderer.drawShards(ce, guiFbo.method_71639(), shatterP, 1.0f, screenScale, false)) {
                    return;
                }
                if (blurRadius >= 0.5f) {
                    float b = blurRadius / 16.0f;
                    GuiLayerBlurRenderer.gaussianPass(ce, tempH.method_71639(), tempV, b / (float)w, 0.0f);
                    GuiLayerBlurRenderer.gaussianPass(ce, tempV.method_71639(), tempH, 0.0f, b / (float)h);
                }
                GuiLayerBlurRenderer.writeCompositeUniform(ce, 1.0f, 1.0f);
                try (RenderPass rp = ce.createRenderPass(() -> "decidevisuals:gui_capture_shard_composite", fb.method_71639(), OptionalInt.empty());){
                    rp.setPipeline(compositePipeline);
                    rp.bindTexture("uGui", tempH.method_71639(), RenderSampler.linear());
                    rp.setUniform("CompositeData", compositeUniform);
                    rp.draw(0, 6);
                }
                return;
            }
            GpuTextureView src = guiFbo.method_71639();
            if (blurRadius >= 0.5f && tempH != null && tempV != null) {
                float b = blurRadius / 16.0f;
                GuiLayerBlurRenderer.gaussianPass(ce, guiFbo.method_71639(), tempH, b / (float)w, 0.0f);
                GuiLayerBlurRenderer.gaussianPass(ce, tempH.method_71639(), tempV, 0.0f, b / (float)h);
                src = tempV.method_71639();
            }
            float invScale = screenScale > 1.0E-4f ? 1.0f / screenScale : 1.0f;
            GuiLayerBlurRenderer.writeCompositeUniform(ce, invScale, 1.0f);
            try (RenderPass rp = ce.createRenderPass(() -> "decidevisuals:gui_capture_composite", fb.method_71639(), OptionalInt.empty());){
                rp.setPipeline(compositePipeline);
                rp.bindTexture("uGui", src, RenderSampler.linear());
                rp.setUniform("CompositeData", compositeUniform);
                rp.draw(0, 6);
            }
        }
        catch (Throwable t) {
            GuiLayerBlurRenderer.disableAfterError(t);
        }
    }

    public static boolean captureActiveThisFrame() {
        return captureActive && guiFbo != null;
    }

    private static void closeTargets() {
        guiFbo = GuiLayerBlurRenderer.destroy(guiFbo);
        tempH = GuiLayerBlurRenderer.destroy(tempH);
        tempV = GuiLayerBlurRenderer.destroy(tempV);
        texWidth = -1;
        texHeight = -1;
    }

    private static void gaussianPass(CommandEncoder ce, GpuTextureView input, class_6367 output, float dx, float dy) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer buf = stack.calloc(16);
            buf.putFloat(0, dx);
            buf.putFloat(4, dy);
            buf.position(0);
            ce.writeToBuffer(blurUniform.slice(0L, 16L), buf);
        }
        try (RenderPass rp = ce.createRenderPass(() -> "decidevisuals:gui_capture_gaussian", output.method_71639(), OptionalInt.of(0));){
            rp.setPipeline(blurPipeline);
            rp.bindTexture("uInput", input, RenderSampler.linear());
            rp.setUniform("BlurData", blurUniform);
            rp.draw(0, 6);
        }
    }

    private static boolean ensureTargets(int w, int h) {
        GpuDevice device = RenderSystem.tryGetDevice();
        if (device == null || w <= 0 || h <= 0) {
            return false;
        }
        if (guiFbo != null && tempH != null && tempV != null && w == texWidth && h == texHeight) {
            return true;
        }
        GuiLayerBlurRenderer.closeTargets();
        guiFbo = new class_6367("decidevisuals_gui_capture", w, h, true);
        tempH = new class_6367("decidevisuals_gui_capture_h", w, h, false);
        tempV = new class_6367("decidevisuals_gui_capture_v", w, h, false);
        texWidth = w;
        texHeight = h;
        return true;
    }

    private static void writeCompositeUniform(CommandEncoder ce, float scale, float alpha) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer buf = stack.calloc(16);
            buf.putFloat(0, scale);
            buf.putFloat(4, alpha);
            buf.position(0);
            ce.writeToBuffer(compositeUniform.slice(0L, 16L), buf);
        }
    }

    private static boolean ensurePipelines() {
        if (blurPipeline != null && compositePipeline != null && shardPipeline != null && blurUniform != null && compositeUniform != null && shardVertexBuffer != null) {
            return true;
        }
        if (blurPipeline == null) {
            blurPipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(BLUR_PIPELINE_ID).withVertexShader(FULLSCREEN).withFragmentShader(GAUSSIAN_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withSampler("uInput").withUniform("BlurData", class_10789.field_60031).withoutBlend().withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
        }
        if (compositePipeline == null) {
            compositePipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(COMPOSITE_PIPELINE_ID).withVertexShader(FULLSCREEN).withFragmentShader(COMPOSITE_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withSampler("uGui").withUniform("CompositeData", class_10789.field_60031).withBlend(new BlendFunction(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA)).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
        }
        if (shardPipeline == null) {
            shardPipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(SHARD_PIPELINE_ID).withVertexShader(SHARD_SHADER).withFragmentShader(SHARD_SHADER).withVertexFormat(SHARD_FORMAT, VertexFormat.class_5596.field_27379).withSampler("uGui").withBlend(new BlendFunction(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA)).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
        }
        if (shardVertexBuffer == null) {
            shardVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "decidevisuals:guilayerblur_shard_vertices", 40, 44352L);
        }
        if (shardVertexData == null) {
            shardVertexData = ByteBuffer.allocateDirect(44352).order(ByteOrder.nativeOrder());
        }
        if (blurUniform == null) {
            blurUniform = RenderSystem.getDevice().createBuffer(() -> "decidevisuals:guilayerblur_blur_uniform", 136, 16L);
        }
        if (compositeUniform == null) {
            compositeUniform = RenderSystem.getDevice().createBuffer(() -> "decidevisuals:guilayerblur_composite_uniform", 136, 16L);
        }
        return blurPipeline != null && compositePipeline != null && shardPipeline != null && blurUniform != null && compositeUniform != null && shardVertexBuffer != null && shardVertexData != null;
    }

    private static void disableAfterError(Throwable t) {
        disabledAfterError = true;
        captureActive = false;
        GuiLayerBlurRenderer.closeTargets();
        LOGGER.error("[GuiLayerBlur] disabled after error", t);
    }

    static {
        IDENTITY_MATRIX = new Matrix4f();
        texWidth = -1;
        texHeight = -1;
    }
}

