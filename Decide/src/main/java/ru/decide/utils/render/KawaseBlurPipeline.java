/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
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
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_12137
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.lwjgl.system.MemoryUtil
 */
package ru.decide.utils.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
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
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_12137;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryUtil;
import ru.decide.module.impl.render.Optimizer;

@Environment(value=EnvType.CLIENT)
public class KawaseBlurPipeline {
    private static final class_2960 DOWN_PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/kawase_down");
    private static final class_2960 DOWN_VERTEX_SHADER = class_2960.method_60655((String)"decide", (String)"core/kawase_down");
    private static final class_2960 DOWN_FRAGMENT_SHADER = class_2960.method_60655((String)"decide", (String)"core/kawase_down");
    private static final class_2960 UP_PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/kawase_up");
    private static final class_2960 UP_VERTEX_SHADER = class_2960.method_60655((String)"decide", (String)"core/kawase_up");
    private static final class_2960 UP_FRAGMENT_SHADER = class_2960.method_60655((String)"decide", (String)"core/kawase_up");
    private static final RenderPipeline DOWN_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(DOWN_PIPELINE_ID).withVertexShader(DOWN_VERTEX_SHADER).withFragmentShader(DOWN_FRAGMENT_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("KawaseParams", class_10789.field_60031).withSampler("Sampler0").withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final RenderPipeline UP_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(UP_PIPELINE_ID).withVertexShader(UP_VERTEX_SHADER).withFragmentShader(UP_FRAGMENT_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("KawaseParams", class_10789.field_60031).withSampler("Sampler0").withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector3f MODEL_OFFSET = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final int MAX_ITERATIONS = 8;
    private static final int MAX_PASSES = 17;
    private static final int UNIFORM_DATA_SIZE = 16;
    private GpuBuffer[] passUniformBuffers;
    private GpuBuffer dummyVertexBuffer;
    private ByteBuffer dataBuffer;
    private GpuTexture[] downTextures;
    private GpuTextureView[] downTextureViews;
    private GpuTexture[] upTextures;
    private GpuTextureView[] upTextureViews;
    private int[] downWidths;
    private int[] downHeights;
    private int[] upWidths;
    private int[] upHeights;
    private GpuTexture finalTexture;
    private GpuTextureView finalTextureView;
    private int lastWidth = 0;
    private int lastHeight = 0;
    private boolean initialized = false;

    private void ensureInitialized() {
        if (this.initialized) {
            return;
        }
        this.dataBuffer = MemoryUtil.memAlloc((int)16);
        ByteBuffer dummyData = MemoryUtil.memAlloc((int)4);
        dummyData.putInt(0);
        dummyData.flip();
        this.dummyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "minecraft:kawase_dummy_vertex", 32, dummyData);
        MemoryUtil.memFree((Buffer)dummyData);
        this.passUniformBuffers = new GpuBuffer[17];
        for (int i = 0; i < 17; ++i) {
            int idx = i;
            this.passUniformBuffers[i] = RenderSystem.getDevice().createBuffer(() -> "minecraft:kawase_uniform_" + idx, 136, 16L);
        }
        this.downTextures = new GpuTexture[8];
        this.downTextureViews = new GpuTextureView[8];
        this.upTextures = new GpuTexture[8];
        this.upTextureViews = new GpuTextureView[8];
        this.downWidths = new int[8];
        this.downHeights = new int[8];
        this.upWidths = new int[8];
        this.upHeights = new int[8];
        this.initialized = true;
    }

    private void ensureFramebuffers(int width, int height) {
        if (width == this.lastWidth && height == this.lastHeight) {
            return;
        }
        this.cleanupFramebuffers();
        this.finalTexture = RenderSystem.getDevice().createTexture(() -> "minecraft:kawase_final", 13, TextureFormat.RGBA8, width, height, 1, 1);
        this.finalTextureView = RenderSystem.getDevice().createTextureView(this.finalTexture);
        int w = width;
        int h = height;
        for (int i = 0; i < 8; ++i) {
            w = Math.max(1, w / 2);
            h = Math.max(1, h / 2);
            int index = i;
            int fw = w;
            int fh = h;
            this.downWidths[i] = fw;
            this.downHeights[i] = fh;
            this.upWidths[i] = fw;
            this.upHeights[i] = fh;
            this.downTextures[i] = RenderSystem.getDevice().createTexture(() -> "minecraft:kawase_down_" + index, 13, TextureFormat.RGBA8, fw, fh, 1, 1);
            this.downTextureViews[i] = RenderSystem.getDevice().createTextureView(this.downTextures[i]);
            this.upTextures[i] = RenderSystem.getDevice().createTexture(() -> "minecraft:kawase_up_" + index, 13, TextureFormat.RGBA8, fw, fh, 1, 1);
            this.upTextureViews[i] = RenderSystem.getDevice().createTextureView(this.upTextures[i]);
        }
        this.lastWidth = width;
        this.lastHeight = height;
    }

    private void cleanupFramebuffers() {
        if (this.finalTextureView != null) {
            this.finalTextureView.close();
            this.finalTextureView = null;
        }
        if (this.finalTexture != null) {
            this.finalTexture.close();
            this.finalTexture = null;
        }
        if (this.downTextureViews != null) {
            for (int i = 0; i < 8; ++i) {
                if (this.downTextureViews[i] != null) {
                    this.downTextureViews[i].close();
                    this.downTextureViews[i] = null;
                }
                if (this.downTextures[i] != null) {
                    this.downTextures[i].close();
                    this.downTextures[i] = null;
                }
                if (this.upTextureViews[i] != null) {
                    this.upTextureViews[i].close();
                    this.upTextureViews[i] = null;
                }
                if (this.upTextures[i] == null) continue;
                this.upTextures[i].close();
                this.upTextures[i] = null;
            }
        }
    }

    public GpuTextureView blur(GpuTexture sourceTexture, GpuTextureView sourceView, int width, int height, int iterations, float offset) {
        RenderPass rp;
        int fi;
        GpuBuffer passBuf;
        int i;
        if (sourceTexture == null || sourceView == null) {
            return null;
        }
        if (sourceTexture.isClosed() || sourceView.isClosed()) {
            return null;
        }
        if (Optimizer.isNoBlur()) {
            return null;
        }
        this.ensureInitialized();
        this.ensureFramebuffers(width, height);
        iterations = Math.min(Math.max(iterations, 1), 8);
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        class_12137 sampler = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
        GpuBufferSlice dynTransforms = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)COLOR_MODULATOR, (Vector3fc)MODEL_OFFSET, (Matrix4fc)TEXTURE_MATRIX);
        GpuTextureView currentSource = sourceView;
        int currentWidth = width;
        int currentHeight = height;
        int passIdx = 0;
        for (i = 0; i < iterations; ++i) {
            passBuf = this.passUniformBuffers[passIdx++];
            this.writeUniform(encoder, passBuf, currentWidth, currentHeight, offset);
            fi = i;
            rp = encoder.createRenderPass(() -> "minecraft:kawase_down_" + fi, this.downTextureViews[fi], OptionalInt.empty());
            try {
                rp.setPipeline(DOWN_PIPELINE);
                rp.setVertexBuffer(0, this.dummyVertexBuffer);
                rp.bindTexture("Sampler0", currentSource, sampler);
                RenderSystem.bindDefaultUniforms((RenderPass)rp);
                rp.setUniform("DynamicTransforms", dynTransforms);
                rp.setUniform("KawaseParams", passBuf);
                rp.draw(0, 6);
            }
            finally {
                if (rp != null) {
                    rp.close();
                }
            }
            currentSource = this.downTextureViews[i];
            currentWidth = this.downWidths[i];
            currentHeight = this.downHeights[i];
        }
        for (i = iterations - 1; i >= 0; --i) {
            passBuf = this.passUniformBuffers[passIdx++];
            this.writeUniform(encoder, passBuf, currentWidth, currentHeight, offset);
            fi = i;
            rp = encoder.createRenderPass(() -> "minecraft:kawase_up_" + fi, this.upTextureViews[fi], OptionalInt.empty());
            try {
                rp.setPipeline(UP_PIPELINE);
                rp.setVertexBuffer(0, this.dummyVertexBuffer);
                rp.bindTexture("Sampler0", currentSource, sampler);
                RenderSystem.bindDefaultUniforms((RenderPass)rp);
                rp.setUniform("DynamicTransforms", dynTransforms);
                rp.setUniform("KawaseParams", passBuf);
                rp.draw(0, 6);
            }
            finally {
                if (rp != null) {
                    rp.close();
                }
            }
            currentSource = this.upTextureViews[i];
            currentWidth = this.upWidths[i];
            currentHeight = this.upHeights[i];
        }
        GpuBuffer passBuf2 = this.passUniformBuffers[passIdx];
        this.writeUniform(encoder, passBuf2, currentWidth, currentHeight, offset);
        try (RenderPass rp2 = encoder.createRenderPass(() -> "minecraft:kawase_final", this.finalTextureView, OptionalInt.empty());){
            rp2.setPipeline(UP_PIPELINE);
            rp2.setVertexBuffer(0, this.dummyVertexBuffer);
            rp2.bindTexture("Sampler0", currentSource, sampler);
            RenderSystem.bindDefaultUniforms((RenderPass)rp2);
            rp2.setUniform("DynamicTransforms", dynTransforms);
            rp2.setUniform("KawaseParams", passBuf2);
            rp2.draw(0, 6);
        }
        return this.finalTextureView;
    }

    private void writeUniform(CommandEncoder encoder, GpuBuffer buf, int width, int height, float offset) {
        this.dataBuffer.clear();
        this.dataBuffer.putFloat(width);
        this.dataBuffer.putFloat(height);
        this.dataBuffer.putFloat(offset);
        this.dataBuffer.putFloat(0.0f);
        this.dataBuffer.flip();
        encoder.writeToBuffer(buf.slice(), this.dataBuffer);
    }

    public void close() {
        this.cleanupFramebuffers();
        if (this.passUniformBuffers != null) {
            for (GpuBuffer buf : this.passUniformBuffers) {
                if (buf == null) continue;
                buf.close();
            }
            this.passUniformBuffers = null;
        }
        if (this.dummyVertexBuffer != null) {
            this.dummyVertexBuffer.close();
            this.dummyVertexBuffer = null;
        }
        if (this.dataBuffer != null) {
            MemoryUtil.memFree((Buffer)this.dataBuffer);
            this.dataBuffer = null;
        }
        this.initialized = false;
    }
}

