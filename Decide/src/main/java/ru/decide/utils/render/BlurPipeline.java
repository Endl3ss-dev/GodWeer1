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
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_12137
 *  net.minecraft.class_290
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
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_12137;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.lwjgl.system.MemoryUtil;
import ru.decide.utils.render.DrawBatcher;
import ru.decide.utils.render.ScreenBlur;

@Environment(value=EnvType.CLIENT)
public class BlurPipeline
implements DrawBatcher.Batched {
    private static final class_2960 PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/blur");
    private static final class_2960 VERTEX_SHADER = class_2960.method_60655((String)"decide", (String)"core/blur");
    private static final class_2960 FRAGMENT_SHADER = class_2960.method_60655((String)"decide", (String)"core/blur");
    private static final float FIXED_GUI_SCALE = 2.0f;
    private static final RenderPipeline PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(PIPELINE_ID).withVertexShader(VERTEX_SHADER).withFragmentShader(FRAGMENT_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("BlurData", class_10789.field_60031).withSampler("Sampler0").withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final int BUFFER_SIZE = 128;
    private static final int UNIFORM_RING = 64;
    private GpuBuffer[] uniformBuffers;
    private int uniformRingIndex = 0;
    private GpuBuffer dummyVertexBuffer;
    private ByteBuffer dataBuffer;
    private boolean initialized = false;
    private final ArrayList<GpuBuffer> pendingDraws = new ArrayList();

    private int getFixedScaledWidth() {
        class_310 client = class_310.method_1551();
        if (client == null || client.method_22683() == null) {
            return 960;
        }
        return (int)Math.ceil((double)client.method_22683().method_4489() / 2.0);
    }

    private int getFixedScaledHeight() {
        class_310 client = class_310.method_1551();
        if (client == null || client.method_22683() == null) {
            return 540;
        }
        return (int)Math.ceil((double)client.method_22683().method_4506() / 2.0);
    }

    private void ensureInitialized() {
        if (this.initialized) {
            return;
        }
        this.dataBuffer = MemoryUtil.memAlloc((int)128);
        ByteBuffer dummyData = MemoryUtil.memAlloc((int)4);
        dummyData.putInt(0);
        dummyData.flip();
        this.dummyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "client:blur_vertex", 32, dummyData);
        MemoryUtil.memFree((Buffer)dummyData);
        this.uniformBuffers = new GpuBuffer[64];
        this.initialized = true;
    }

    private GpuBuffer nextUniformBuffer() {
        GpuBuffer buf = this.uniformBuffers[this.uniformRingIndex];
        if (buf == null) {
            int idx = this.uniformRingIndex;
            this.uniformBuffers[this.uniformRingIndex] = buf = RenderSystem.getDevice().createBuffer(() -> "client:blur_uniform_" + idx, 136, 128L);
        }
        this.uniformRingIndex = (this.uniformRingIndex + 1) % 64;
        return buf;
    }

    public void drawBlur(float x, float y, float width, float height, float radius, float[] radii, int color) {
        this.drawBlur(x, y, width, height, radius, radii, color, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void drawGlassBlur(float x, float y, float width, float height, float alpha, float[] radii, int tintColor, float distortion, float waveSize, float edgeLight, float shine) {
        this.drawBlur(x, y, width, height, alpha, radii, tintColor, distortion, waveSize, edgeLight, shine);
    }

    private void drawBlur(float x, float y, float width, float height, float radius, float[] radii, int color, float distortion, float waveSize, float edgeLight, float shine) {
        ScreenBlur.markUsed();
        if (!ScreenBlur.isReady()) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.method_1522() == null) {
            return;
        }
        this.ensureInitialized();
        int fbWidth = ScreenBlur.getCachedWidth();
        int fbHeight = ScreenBlur.getCachedHeight();
        int fixedScreenWidth = this.getFixedScaledWidth();
        int fixedScreenHeight = this.getFixedScaledHeight();
        this.prepareUniformData(x, y, width, height, fixedScreenWidth, fixedScreenHeight, fbWidth, fbHeight, 2.0f, radius, radii, color, distortion, waveSize, edgeLight, shine);
        GpuBuffer uniformBuffer = this.nextUniformBuffer();
        RenderSystem.getDevice().createCommandEncoder().writeToBuffer(uniformBuffer.slice(0L, (long)this.dataBuffer.remaining()), this.dataBuffer);
        this.pendingDraws.add(uniformBuffer);
        if (DrawBatcher.isEnabled()) {
            DrawBatcher.register(this);
            if (this.pendingDraws.size() >= 64) {
                DrawBatcher.drawImmediate(this);
            }
        } else {
            DrawBatcher.flushPending();
            DrawBatcher.drawImmediate(this);
        }
    }

    @Override
    public int batchLayer() {
        return -1;
    }

    @Override
    public void uploadBatch(CommandEncoder encoder) {
    }

    @Override
    public void drawBatch(RenderPass pass) {
        if (this.pendingDraws.isEmpty()) {
            return;
        }
        if (!ScreenBlur.isReady()) {
            this.pendingDraws.clear();
            return;
        }
        class_12137 sampler = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
        pass.setPipeline(PIPELINE);
        pass.setVertexBuffer(0, this.dummyVertexBuffer);
        pass.bindTexture("Sampler0", ScreenBlur.getBlurredView(), sampler);
        for (GpuBuffer uniformBuffer : this.pendingDraws) {
            pass.setUniform("BlurData", uniformBuffer);
            pass.draw(0, 6);
        }
        this.pendingDraws.clear();
    }

    @Override
    public void discardBatch() {
        this.pendingDraws.clear();
    }

    public void flush() {
        if (this.pendingDraws.isEmpty()) {
            return;
        }
        DrawBatcher.drawImmediate(this);
    }

    private void prepareUniformData(float x, float y, float width, float height, float screenWidth, float screenHeight, int fbWidth, int fbHeight, float guiScale, float blurRadius, float[] radii, int color, float distortion, float waveSize, float edgeLight, float shine) {
        this.dataBuffer.clear();
        this.dataBuffer.putFloat(x);
        this.dataBuffer.putFloat(y);
        this.dataBuffer.putFloat(width);
        this.dataBuffer.putFloat(height);
        this.dataBuffer.putFloat(screenWidth);
        this.dataBuffer.putFloat(screenHeight);
        this.dataBuffer.putFloat(guiScale);
        this.dataBuffer.putFloat(blurRadius);
        this.dataBuffer.putFloat(fbWidth);
        this.dataBuffer.putFloat(fbHeight);
        this.dataBuffer.putFloat(0.0f);
        this.dataBuffer.putFloat(0.0f);
        this.dataBuffer.putFloat(radii[0]);
        this.dataBuffer.putFloat(radii[1]);
        this.dataBuffer.putFloat(radii[2]);
        this.dataBuffer.putFloat(radii[3]);
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        this.dataBuffer.putFloat(r);
        this.dataBuffer.putFloat(g);
        this.dataBuffer.putFloat(b);
        this.dataBuffer.putFloat(a);
        this.dataBuffer.putFloat(Math.max(0.0f, distortion));
        this.dataBuffer.putFloat(Math.max(0.0f, waveSize));
        this.dataBuffer.putFloat(Math.max(0.0f, edgeLight));
        this.dataBuffer.putFloat(Math.max(0.0f, shine));
        this.dataBuffer.flip();
    }

    public void close() {
        this.pendingDraws.clear();
        if (this.uniformBuffers != null) {
            for (GpuBuffer buf : this.uniformBuffers) {
                if (buf == null) continue;
                buf.close();
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

