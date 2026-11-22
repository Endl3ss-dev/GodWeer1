/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
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
 *  net.minecraft.class_310
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
import net.minecraft.class_10799;
import net.minecraft.class_12137;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryUtil;
import ru.decide.utils.render.DrawBatcher;

@Environment(value=EnvType.CLIENT)
public class GrayscalePipeline {
    private static final class_2960 PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/grayscale");
    private static final class_2960 VERTEX_SHADER = class_2960.method_60655((String)"decide", (String)"core/grayscale");
    private static final class_2960 FRAGMENT_SHADER = class_2960.method_60655((String)"decide", (String)"core/grayscale");
    private static final Vector3f MODEL_OFFSET = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final float FIXED_GUI_SCALE = 2.0f;
    private static final int BUFFER_SIZE = 16;
    private static final RenderPipeline PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(PIPELINE_ID).withVertexShader(VERTEX_SHADER).withFragmentShader(FRAGMENT_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("GrayscaleData", class_10789.field_60031).withSampler("Sampler0").withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private GpuBuffer uniformBuffer;
    private GpuBuffer dummyVertexBuffer;
    private GpuTexture sceneTexture;
    private GpuTextureView sceneTextureView;
    private ByteBuffer dataBuffer;
    private int lastWidth;
    private int lastHeight;
    private boolean initialized;

    private void ensureInitialized() {
        if (this.initialized) {
            return;
        }
        this.dataBuffer = MemoryUtil.memAlloc((int)16);
        ByteBuffer dummyData = MemoryUtil.memAlloc((int)4);
        dummyData.putInt(0);
        dummyData.flip();
        this.dummyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "client:grayscale_dummy_vertex", 32, dummyData);
        MemoryUtil.memFree((Buffer)dummyData);
        this.uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "client:grayscale_uniform", 136, 16L);
        this.initialized = true;
    }

    private void ensureSceneTexture(int width, int height) {
        if (this.sceneTexture != null && width == this.lastWidth && height == this.lastHeight) {
            return;
        }
        if (this.sceneTextureView != null) {
            this.sceneTextureView.close();
            this.sceneTextureView = null;
        }
        if (this.sceneTexture != null) {
            this.sceneTexture.close();
            this.sceneTexture = null;
        }
        this.sceneTexture = RenderSystem.getDevice().createTexture(() -> "client:grayscale_scene", 5, TextureFormat.RGBA8, width, height, 1, 1);
        this.sceneTextureView = RenderSystem.getDevice().createTextureView(this.sceneTexture);
        this.lastWidth = width;
        this.lastHeight = height;
    }

    public void draw(float state) {
        if (state <= 0.01f) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client == null || client.method_1522() == null) {
            return;
        }
        if (client.method_1522().method_30277() == null) {
            return;
        }
        this.ensureInitialized();
        int width = client.method_1522().field_1482;
        int height = client.method_1522().field_1481;
        if (width <= 0 || height <= 0) {
            return;
        }
        this.ensureSceneTexture(width, height);
        DrawBatcher.flushPending();
        this.dataBuffer.clear();
        this.dataBuffer.putFloat((float)client.method_22683().method_4489() / 2.0f);
        this.dataBuffer.putFloat((float)client.method_22683().method_4506() / 2.0f);
        this.dataBuffer.putFloat(2.0f);
        this.dataBuffer.putFloat(Math.max(0.0f, Math.min(1.0f, state)));
        this.dataBuffer.flip();
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        encoder.copyTextureToTexture(client.method_1522().method_30277(), this.sceneTexture, 0, 0, 0, 0, 0, width, height);
        encoder.writeToBuffer(this.uniformBuffer.slice(), this.dataBuffer);
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)COLOR_MODULATOR, (Vector3fc)MODEL_OFFSET, (Matrix4fc)TEXTURE_MATRIX);
        class_12137 sampler = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
        try (RenderPass renderPass = encoder.createRenderPass(() -> "client:grayscale_pass", client.method_1522().method_71639(), OptionalInt.empty(), client.method_1522().method_71640(), OptionalDouble.empty());){
            renderPass.setPipeline(PIPELINE);
            renderPass.setVertexBuffer(0, this.dummyVertexBuffer);
            renderPass.bindTexture("Sampler0", this.sceneTextureView, sampler);
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.setUniform("GrayscaleData", this.uniformBuffer);
            renderPass.draw(0, 6);
        }
    }

    public void close() {
        if (this.sceneTextureView != null) {
            this.sceneTextureView.close();
            this.sceneTextureView = null;
        }
        if (this.sceneTexture != null) {
            this.sceneTexture.close();
            this.sceneTexture = null;
        }
        if (this.uniformBuffer != null) {
            this.uniformBuffer.close();
            this.uniformBuffer = null;
        }
        if (this.dummyVertexBuffer != null) {
            this.dummyVertexBuffer.close();
            this.dummyVertexBuffer = null;
        }
        if (this.dataBuffer != null) {
            MemoryUtil.memFree((Buffer)this.dataBuffer);
            this.dataBuffer = null;
        }
        this.lastWidth = 0;
        this.lastHeight = 0;
        this.initialized = false;
    }
}

