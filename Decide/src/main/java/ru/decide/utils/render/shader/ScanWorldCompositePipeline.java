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
 *  com.mojang.blaze3d.platform.DestFactor
 *  com.mojang.blaze3d.platform.SourceFactor
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_12137
 *  net.minecraft.class_243
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
package ru.decide.utils.render.shader;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.awt.Color;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_12137;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public final class ScanWorldCompositePipeline {
    private static final class_2960 PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/scanworld_composite");
    private static final class_2960 SHADER_ID = class_2960.method_60655((String)"decide", (String)"core/scanworld_composite");
    private static final BlendFunction ADD_BLEND = new BlendFunction(SourceFactor.ONE, DestFactor.ONE, SourceFactor.ONE, DestFactor.ONE);
    private static final RenderPipeline PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(PIPELINE_ID).withVertexShader(SHADER_ID).withFragmentShader(SHADER_ID).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("ScanData", class_10789.field_60031).withSampler("DepthSampler").withBlend(ADD_BLEND).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector3f MODEL_OFFSET = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final int BUFFER_SIZE = 256;
    private static final int UNIFORM_RING = 16;
    private GpuBuffer[] uniformBuffers;
    private int uniformRingIndex;
    private GpuBuffer dummyVertexBuffer;
    private ByteBuffer dataBuffer;
    private boolean initialized;

    private void ensureInitialized() {
        if (this.initialized) {
            return;
        }
        this.dataBuffer = MemoryUtil.memAlloc((int)256);
        ByteBuffer dummy = MemoryUtil.memAlloc((int)4);
        dummy.putInt(0).flip();
        this.dummyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "client:scanworld_dummy_vertex", 32, dummy);
        MemoryUtil.memFree((Buffer)dummy);
        this.uniformBuffers = new GpuBuffer[16];
        this.initialized = true;
    }

    private GpuBuffer nextUniformBuffer() {
        GpuBuffer buf = this.uniformBuffers[this.uniformRingIndex];
        if (buf == null) {
            int idx = this.uniformRingIndex;
            this.uniformBuffers[this.uniformRingIndex] = buf = RenderSystem.getDevice().createBuffer(() -> "client:scanworld_uniform_" + idx, 136, 256L);
        }
        this.uniformRingIndex = (this.uniformRingIndex + 1) % 16;
        return buf;
    }

    public void render(GpuTextureView targetView, GpuTextureView depthView, int width, int height, class_243 cameraPos, class_243 center, Matrix4f invView, Matrix4f invProj, float radius, float bandWidth, float fade, Color outerColor, Color midColor, Color innerColor, Color scanlineColor) {
        this.ensureInitialized();
        this.prepareUniformData(width, height, cameraPos, center, invView, invProj, radius, bandWidth, fade, outerColor, midColor, innerColor, scanlineColor);
        GpuBuffer uniformBuffer = this.nextUniformBuffer();
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        encoder.writeToBuffer(uniformBuffer.slice(0L, (long)this.dataBuffer.remaining()), this.dataBuffer);
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)COLOR_MODULATOR, (Vector3fc)MODEL_OFFSET, (Matrix4fc)TEXTURE_MATRIX);
        class_12137 depthSampler = RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST);
        try (RenderPass pass = encoder.createRenderPass(() -> "client:scanworld_pass", targetView, OptionalInt.empty());){
            pass.setPipeline(PIPELINE);
            pass.setVertexBuffer(0, this.dummyVertexBuffer);
            pass.bindTexture("DepthSampler", depthView, depthSampler);
            RenderSystem.bindDefaultUniforms((RenderPass)pass);
            pass.setUniform("DynamicTransforms", dynamicTransforms);
            pass.setUniform("ScanData", uniformBuffer);
            pass.draw(0, 6);
        }
    }

    private void prepareUniformData(int width, int height, class_243 cameraPos, class_243 center, Matrix4f invView, Matrix4f invProj, float radius, float bandWidth, float fade, Color outerColor, Color midColor, Color innerColor, Color scanlineColor) {
        this.dataBuffer.clear();
        ScanWorldCompositePipeline.putMatrix(this.dataBuffer, invView);
        ScanWorldCompositePipeline.putMatrix(this.dataBuffer, invProj);
        this.dataBuffer.putFloat((float)cameraPos.field_1352);
        this.dataBuffer.putFloat((float)cameraPos.field_1351);
        this.dataBuffer.putFloat((float)cameraPos.field_1350);
        this.dataBuffer.putFloat(0.0f);
        this.dataBuffer.putFloat((float)center.field_1352);
        this.dataBuffer.putFloat((float)center.field_1351);
        this.dataBuffer.putFloat((float)center.field_1350);
        this.dataBuffer.putFloat(radius);
        ScanWorldCompositePipeline.putColor(this.dataBuffer, outerColor);
        ScanWorldCompositePipeline.putColor(this.dataBuffer, midColor);
        ScanWorldCompositePipeline.putColor(this.dataBuffer, innerColor);
        ScanWorldCompositePipeline.putColor(this.dataBuffer, scanlineColor);
        this.dataBuffer.putFloat(width);
        this.dataBuffer.putFloat(height);
        this.dataBuffer.putFloat(bandWidth);
        this.dataBuffer.putFloat((float)(System.currentTimeMillis() % 1000000L) / 1000.0f);
        this.dataBuffer.putFloat(fade);
        this.dataBuffer.putFloat(0.0f);
        this.dataBuffer.putFloat(0.0f);
        this.dataBuffer.putFloat(0.0f);
        this.dataBuffer.flip();
    }

    private static void putColor(ByteBuffer buffer, Color color) {
        buffer.putFloat((float)color.getRed() / 255.0f);
        buffer.putFloat((float)color.getGreen() / 255.0f);
        buffer.putFloat((float)color.getBlue() / 255.0f);
        buffer.putFloat((float)color.getAlpha() / 255.0f);
    }

    private static void putMatrix(ByteBuffer buffer, Matrix4f matrix) {
        buffer.putFloat(matrix.m00()).putFloat(matrix.m01()).putFloat(matrix.m02()).putFloat(matrix.m03());
        buffer.putFloat(matrix.m10()).putFloat(matrix.m11()).putFloat(matrix.m12()).putFloat(matrix.m13());
        buffer.putFloat(matrix.m20()).putFloat(matrix.m21()).putFloat(matrix.m22()).putFloat(matrix.m23());
        buffer.putFloat(matrix.m30()).putFloat(matrix.m31()).putFloat(matrix.m32()).putFloat(matrix.m33());
    }

    public void close() {
        if (this.uniformBuffers != null) {
            for (int i = 0; i < this.uniformBuffers.length; ++i) {
                if (this.uniformBuffers[i] == null) continue;
                this.uniformBuffers[i].close();
                this.uniformBuffers[i] = null;
            }
            this.uniformBuffers = null;
        }
        this.uniformRingIndex = 0;
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

