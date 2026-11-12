/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10789
 *  net.minecraft.class_12137
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  org.lwjgl.system.MemoryUtil
 */
package ru.decide.utils.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_12137;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.lwjgl.system.MemoryUtil;
import ru.decide.utils.render.DrawBatcher;

@Environment(value=EnvType.CLIENT)
public class SaturationPipeline {
    private static final int UNIFORM_SIZE = 64;
    private static RenderPipeline pipeline;
    private static GpuBuffer uniformBuffer;
    private static GpuTexture copyTexture;
    private static GpuTextureView copyTextureView;
    private static int lastWidth;
    private static int lastHeight;

    private SaturationPipeline() {
    }

    public static void init() {
        if (pipeline != null) {
            return;
        }
        pipeline = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(class_2960.method_60655((String)"decide", (String)"saturation")).withVertexShader(class_2960.method_60655((String)"decide", (String)"saturation_vertex")).withFragmentShader(class_2960.method_60655((String)"decide", (String)"saturation_fragment")).withVertexFormat(VertexFormat.builder().build(), VertexFormat.class_5596.field_27379).withUniform("Uniforms", class_10789.field_60031).withSampler("Sampler0").withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build();
        uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "SaturationPipeline Uniforms", 136, 64L);
    }

    public static void draw(float brightness, float saturation, float contrast, float hue) {
        class_310 client = class_310.method_1551();
        if (client == null || client.method_1522() == null) {
            return;
        }
        if (client.method_1522().method_30277() == null) {
            return;
        }
        if (pipeline == null) {
            SaturationPipeline.init();
        }
        if (pipeline == null || uniformBuffer == null) {
            return;
        }
        int fbWidth = client.method_1522().field_1482;
        int fbHeight = client.method_1522().field_1481;
        if (fbWidth <= 0 || fbHeight <= 0) {
            return;
        }
        SaturationPipeline.ensureCopyTexture(fbWidth, fbHeight);
        if (copyTexture == null || copyTextureView == null) {
            return;
        }
        DrawBatcher.flushPending();
        ByteBuffer buffer = MemoryUtil.memAlloc((int)64);
        buffer.putFloat(brightness).putFloat(0.0f).putFloat(0.0f).putFloat(0.0f);
        buffer.putFloat(saturation).putFloat(0.0f).putFloat(0.0f).putFloat(0.0f);
        buffer.putFloat(contrast).putFloat(0.0f).putFloat(0.0f).putFloat(0.0f);
        buffer.putFloat(hue).putFloat(0.0f).putFloat(0.0f).putFloat(0.0f);
        buffer.flip();
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        encoder.copyTextureToTexture(client.method_1522().method_30277(), copyTexture, 0, 0, 0, 0, 0, fbWidth, fbHeight);
        encoder.writeToBuffer(uniformBuffer.slice(), buffer);
        MemoryUtil.memFree((Buffer)buffer);
        class_12137 sampler = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
        try (RenderPass pass = encoder.createRenderPass(() -> "SaturationPipeline", client.method_1522().method_71639(), OptionalInt.empty(), client.method_1522().method_71640(), OptionalDouble.of(1.0));){
            pass.setPipeline(pipeline);
            pass.setUniform("Uniforms", uniformBuffer);
            pass.bindTexture("Sampler0", copyTextureView, sampler);
            pass.draw(0, 3);
        }
    }

    private static void ensureCopyTexture(int width, int height) {
        if (copyTexture != null && width == lastWidth && height == lastHeight) {
            return;
        }
        if (copyTextureView != null) {
            copyTextureView.close();
            copyTextureView = null;
        }
        if (copyTexture != null) {
            copyTexture.close();
            copyTexture = null;
        }
        copyTexture = RenderSystem.getDevice().createTexture(() -> "client:saturation_copy", 5, TextureFormat.RGBA8, width, height, 1, 1);
        copyTextureView = RenderSystem.getDevice().createTextureView(copyTexture);
        lastWidth = width;
        lastHeight = height;
    }

    public static void shutdown() {
        if (uniformBuffer != null) {
            uniformBuffer.close();
            uniformBuffer = null;
        }
        if (copyTextureView != null) {
            copyTextureView.close();
            copyTextureView = null;
        }
        if (copyTexture != null) {
            copyTexture.close();
            copyTexture = null;
        }
        lastWidth = 0;
        lastHeight = 0;
    }
}

