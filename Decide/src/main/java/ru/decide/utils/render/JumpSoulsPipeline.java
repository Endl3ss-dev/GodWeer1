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
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_12137
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  org.lwjgl.system.MemoryStack
 */
package ru.decide.utils.render;

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
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_12137;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public final class JumpSoulsPipeline {
    public static final int UNIFORM_FLOATS = 412;
    private static final int UNIFORM_SIZE = 1648;
    private static final int GLOW_DOWNSCALE = 4;
    private static final class_2960 PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/post/jumpsouls");
    private static final class_2960 COMPOSITE_PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/post/jumpsouls_composite");
    private static final class_2960 SHADER = class_2960.method_60655((String)"decide", (String)"post/jumpsouls/jumpsouls");
    private static final class_2960 COMPOSITE_SHADER = class_2960.method_60655((String)"decide", (String)"post/jumpsouls/composite");
    private static RenderPipeline pipeline;
    private static RenderPipeline compositePipeline;
    private static GpuBuffer groundUniform;
    private static GpuBuffer volumeUniform;
    private static GpuTexture depthCopy;
    private static GpuTextureView depthCopyView;
    private static GpuTexture glowTexture;
    private static GpuTextureView glowTextureView;
    private static int targetWidth;
    private static int targetHeight;
    private static boolean disabledAfterError;

    private JumpSoulsPipeline() {
    }

    public static void apply(class_276 renderTarget, float[] uniform) {
        if (disabledAfterError) {
            return;
        }
        if (renderTarget == null) {
            return;
        }
        if (renderTarget.method_71639() == null || renderTarget.method_30278() == null) {
            return;
        }
        if (renderTarget.field_1482 <= 0 || renderTarget.field_1481 <= 0) {
            return;
        }
        JumpSoulsPipeline.init();
        if (pipeline == null || compositePipeline == null) {
            return;
        }
        if (groundUniform == null || volumeUniform == null) {
            return;
        }
        if (!JumpSoulsPipeline.ensureTargets(renderTarget.field_1482, renderTarget.field_1481)) {
            return;
        }
        boolean glowOn = uniform.length > 5 && uniform[5] > 0.001f;
        try {
            class_12137 nearest = RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST);
            class_12137 linear = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
            CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
            encoder.copyTextureToTexture(renderTarget.method_30278(), depthCopy, 0, 0, 0, 0, 0, renderTarget.field_1482, renderTarget.field_1481);
            JumpSoulsPipeline.upload(encoder, groundUniform, uniform, 0.0f);
            try (RenderPass pass = encoder.createRenderPass(() -> "client:jumpsouls_ground", renderTarget.method_71639(), OptionalInt.empty());){
                pass.setPipeline(pipeline);
                pass.bindTexture("DepthSampler", depthCopyView, nearest);
                pass.setUniform("Souls", groundUniform);
                pass.draw(0, 6);
            }
            if (!glowOn) {
                return;
            }
            JumpSoulsPipeline.upload(encoder, volumeUniform, uniform, 1.0f);
            pass = encoder.createRenderPass(() -> "client:jumpsouls_volume", glowTextureView, OptionalInt.of(0));
            try {
                pass.setPipeline(pipeline);
                pass.bindTexture("DepthSampler", depthCopyView, nearest);
                pass.setUniform("Souls", volumeUniform);
                pass.draw(0, 6);
            }
            finally {
                if (pass != null) {
                    pass.close();
                }
            }
            pass = encoder.createRenderPass(() -> "client:jumpsouls_composite", renderTarget.method_71639(), OptionalInt.empty());
            try {
                pass.setPipeline(compositePipeline);
                pass.bindTexture("GlowSampler", glowTextureView, linear);
                pass.draw(0, 6);
            }
            finally {
                if (pass != null) {
                    pass.close();
                }
            }
        }
        catch (Throwable t) {
            disabledAfterError = true;
            JumpSoulsPipeline.closeTargets();
        }
    }

    private static void upload(CommandEncoder encoder, GpuBuffer buffer, float[] uniform, float mode) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer data = stack.calloc(1648);
            for (int i = 0; i < 412; ++i) {
                data.putFloat(i * 4, i < uniform.length ? uniform[i] : 0.0f);
            }
            data.putFloat(32, mode);
            data.position(0);
            encoder.writeToBuffer(buffer.slice(0L, 1648L), data);
        }
    }

    private static void init() {
        if (disabledAfterError) {
            return;
        }
        try {
            if (pipeline == null) {
                pipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(PIPELINE_ID).withVertexShader(SHADER).withFragmentShader(SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("Souls", class_10789.field_60031).withSampler("DepthSampler").withBlend(new BlendFunction(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA)).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
            }
            if (compositePipeline == null) {
                compositePipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(COMPOSITE_PIPELINE_ID).withVertexShader(SHADER).withFragmentShader(COMPOSITE_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withSampler("GlowSampler").withBlend(new BlendFunction(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA)).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
            }
            groundUniform = JumpSoulsPipeline.ensureUniform(groundUniform, "client:jump_souls_uniforms");
            volumeUniform = JumpSoulsPipeline.ensureUniform(volumeUniform, "client:jump_souls_volume_uniforms");
        }
        catch (Throwable t) {
            disabledAfterError = true;
            pipeline = null;
            compositePipeline = null;
            JumpSoulsPipeline.closeUniforms();
        }
    }

    private static GpuBuffer ensureUniform(GpuBuffer current, String name) {
        if (current != null && !current.isClosed() && current.size() >= 1648L) {
            return current;
        }
        if (current != null) {
            current.close();
        }
        return RenderSystem.getDevice().createBuffer(() -> name, 136, 1648L);
    }

    private static boolean ensureTargets(int width, int height) {
        GpuDevice device = RenderSystem.tryGetDevice();
        if (device == null) {
            return false;
        }
        if (depthCopy != null && glowTexture != null && targetWidth == width && targetHeight == height) {
            return true;
        }
        JumpSoulsPipeline.closeTargets();
        depthCopy = device.createTexture(() -> "client:jumpsouls_depth_copy", 5, TextureFormat.DEPTH32, width, height, 1, 1);
        depthCopyView = device.createTextureView(depthCopy);
        glowTexture = device.createTexture(() -> "client:jumpsouls_glow", 12, TextureFormat.RGBA8, Math.max(1, width / 4), Math.max(1, height / 4), 1, 1);
        glowTextureView = device.createTextureView(glowTexture);
        targetWidth = width;
        targetHeight = height;
        return true;
    }

    public static void clear() {
        JumpSoulsPipeline.closeTargets();
    }

    private static void closeTargets() {
        if (depthCopyView != null) {
            depthCopyView.close();
            depthCopyView = null;
        }
        if (depthCopy != null) {
            depthCopy.close();
            depthCopy = null;
        }
        if (glowTextureView != null) {
            glowTextureView.close();
            glowTextureView = null;
        }
        if (glowTexture != null) {
            glowTexture.close();
            glowTexture = null;
        }
        targetWidth = -1;
        targetHeight = -1;
    }

    private static void closeUniforms() {
        if (groundUniform != null) {
            groundUniform.close();
            groundUniform = null;
        }
        if (volumeUniform != null) {
            volumeUniform.close();
            volumeUniform = null;
        }
    }

    static {
        targetWidth = -1;
        targetHeight = -1;
    }
}

