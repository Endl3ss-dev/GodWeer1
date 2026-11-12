/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.render.others;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class FullscreenQuadBuffer {
    private static GpuBuffer buffer;

    private FullscreenQuadBuffer() {
    }

    public static void close() {
        if (buffer != null) {
            buffer.close();
            buffer = null;
        }
    }

    public static GpuBuffer getOrCreate() {
        GpuDevice gpuDevice = RenderSystem.tryGetDevice();
        if (gpuDevice == null) {
            return null;
        }
        if (buffer != null && !buffer.isClosed()) {
            return buffer;
        }
        float[] vertices = new float[]{-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f};
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder());
        for (float v : vertices) {
            bb.putFloat(v);
        }
        bb.flip();
        buffer = gpuDevice.createBuffer(() -> "decidevisuals:fullscreen_quad", 32, bb);
        return buffer;
    }
}

