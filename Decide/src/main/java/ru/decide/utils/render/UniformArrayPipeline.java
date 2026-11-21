/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  org.lwjgl.system.MemoryUtil
 */
package ru.decide.utils.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.lwjgl.system.MemoryUtil;
import ru.decide.utils.render.DrawBatcher;

@Environment(value=EnvType.CLIENT)
public abstract class UniformArrayPipeline
implements DrawBatcher.Batched {
    protected static final float FIXED_GUI_SCALE = 2.0f;
    private static final int HEADER_SIZE = 16;
    private final String debugName;
    private final RenderPipeline renderPipeline;
    private final String uniformName;
    private final int maxItems;
    private final int bufferSize;
    private final int uniformRing;
    private GpuBuffer[] uniformBuffers;
    private int uniformRingIndex = 0;
    private GpuBuffer dummyVertexBuffer;
    private ByteBuffer dataBuffer;
    private boolean initialized = false;
    private int batchedItems = 0;
    private final ArrayList<Chunk> chunks = new ArrayList();

    protected UniformArrayPipeline(String debugName, RenderPipeline renderPipeline, String uniformName, int maxItems, int itemSize, int uniformRing) {
        this.debugName = debugName;
        this.renderPipeline = renderPipeline;
        this.uniformName = uniformName;
        this.maxItems = maxItems;
        this.bufferSize = 16 + maxItems * itemSize;
        this.uniformRing = uniformRing;
    }

    private void ensureInitialized() {
        if (this.initialized) {
            return;
        }
        this.dataBuffer = MemoryUtil.memAlloc((int)this.bufferSize);
        ByteBuffer dummyData = MemoryUtil.memAlloc((int)4);
        dummyData.putInt(0);
        dummyData.flip();
        this.dummyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "client:" + this.debugName + "_dummy_vertex", 32, dummyData);
        MemoryUtil.memFree((Buffer)dummyData);
        this.uniformBuffers = new GpuBuffer[this.uniformRing];
        this.initialized = true;
    }

    private GpuBuffer nextUniformBuffer() {
        GpuBuffer buf = this.uniformBuffers[this.uniformRingIndex];
        if (buf == null) {
            int idx = this.uniformRingIndex;
            this.uniformBuffers[this.uniformRingIndex] = buf = RenderSystem.getDevice().createBuffer(() -> "client:" + this.debugName + "_uniform_" + idx, 136, (long)this.bufferSize);
        }
        this.uniformRingIndex = (this.uniformRingIndex + 1) % this.uniformRing;
        return buf;
    }

    protected final ByteBuffer beginItem() {
        this.ensureInitialized();
        if (this.batchedItems == 0) {
            this.dataBuffer.clear();
            this.dataBuffer.position(16);
        }
        return this.dataBuffer;
    }

    protected final void endItem() {
        ++this.batchedItems;
        if (DrawBatcher.isEnabled()) {
            DrawBatcher.register(this);
            if (this.batchedItems >= this.maxItems) {
                if (this.chunks.size() >= this.uniformRing - 1) {
                    DrawBatcher.drawImmediate(this);
                } else {
                    this.sealChunk();
                }
            }
        } else {
            DrawBatcher.drawImmediate(this);
        }
    }

    private void sealChunk() {
        if (this.batchedItems == 0) {
            return;
        }
        class_310 client = class_310.method_1551();
        float fixedScreenWidth = (float)client.method_22683().method_4489() / 2.0f;
        float fixedScreenHeight = (float)client.method_22683().method_4506() / 2.0f;
        int endPosition = this.dataBuffer.position();
        this.dataBuffer.position(0);
        this.dataBuffer.putFloat(fixedScreenWidth);
        this.dataBuffer.putFloat(fixedScreenHeight);
        this.dataBuffer.putFloat(2.0f);
        this.dataBuffer.putFloat(0.0f);
        this.dataBuffer.position(0);
        this.dataBuffer.limit(endPosition);
        GpuBuffer uniformBuffer = this.nextUniformBuffer();
        RenderSystem.getDevice().createCommandEncoder().writeToBuffer(uniformBuffer.slice(0L, (long)this.dataBuffer.remaining()), this.dataBuffer);
        this.dataBuffer.limit(this.dataBuffer.capacity());
        this.chunks.add(new Chunk(uniformBuffer, this.batchedItems));
        this.batchedItems = 0;
    }

    @Override
    public final void uploadBatch(CommandEncoder encoder) {
        this.sealChunk();
    }

    @Override
    public final void drawBatch(RenderPass pass) {
        if (this.chunks.isEmpty()) {
            return;
        }
        pass.setPipeline(this.renderPipeline);
        pass.setVertexBuffer(0, this.dummyVertexBuffer);
        for (Chunk chunk : this.chunks) {
            pass.setUniform(this.uniformName, chunk.buffer());
            pass.draw(0, chunk.count() * 6);
        }
        this.chunks.clear();
    }

    @Override
    public final void discardBatch() {
        this.chunks.clear();
    }

    public final void flush() {
        if (this.batchedItems == 0 && this.chunks.isEmpty()) {
            return;
        }
        DrawBatcher.drawImmediate(this);
    }

    public void close() {
        this.batchedItems = 0;
        this.discardBatch();
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

    @Environment(value=EnvType.CLIENT)
    private record Chunk(GpuBuffer buffer, int count) {
    }
}

