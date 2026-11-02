/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package ru.decide.utils.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public final class DrawBatcher {
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector3f MODEL_OFFSET = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final ArrayList<Batched> active = new ArrayList(8);
    private static boolean enabled = false;

    private DrawBatcher() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        if (!value) {
            DrawBatcher.flushPending();
        }
        enabled = value;
    }

    public static void register(Batched owner) {
        if (!active.contains(owner)) {
            active.add(owner);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void flushPending() {
        if (active.isEmpty()) {
            return;
        }
        Batched[] pending = active.toArray(new Batched[0]);
        active.clear();
        Arrays.sort(pending, Comparator.comparingInt(Batched::batchLayer));
        class_310 client = class_310.method_1551();
        if (client == null || client.method_1522() == null) {
            for (Batched batch : pending) {
                batch.discardBatch();
            }
            return;
        }
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        for (Batched batch : pending) {
            batch.uploadBatch(encoder);
        }
        GpuBufferSlice dynamicTransforms = DrawBatcher.writeDynamicTransforms();
        try (RenderPass pass = encoder.createRenderPass(() -> "client:2d_batch", client.method_1522().method_71639(), OptionalInt.empty(), client.method_1522().method_71640(), OptionalDouble.empty());){
            RenderSystem.bindDefaultUniforms((RenderPass)pass);
            pass.setUniform("DynamicTransforms", dynamicTransforms);
            for (Batched batch : pending) {
                batch.drawBatch(pass);
            }
        }
        finally {
            for (Batched batch : pending) {
                batch.discardBatch();
            }
        }
    }

    public static void drawImmediate(Batched owner) {
        DrawBatcher.drawImmediate(owner, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void drawImmediate(Batched owner, boolean upload) {
        class_310 client = class_310.method_1551();
        if (client == null || client.method_1522() == null) {
            owner.discardBatch();
            return;
        }
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        if (upload) {
            owner.uploadBatch(encoder);
        }
        GpuBufferSlice dynamicTransforms = DrawBatcher.writeDynamicTransforms();
        try (RenderPass pass = encoder.createRenderPass(() -> "client:2d_immediate", client.method_1522().method_71639(), OptionalInt.empty(), client.method_1522().method_71640(), OptionalDouble.empty());){
            RenderSystem.bindDefaultUniforms((RenderPass)pass);
            pass.setUniform("DynamicTransforms", dynamicTransforms);
            owner.drawBatch(pass);
        }
        finally {
            owner.discardBatch();
        }
    }

    private static GpuBufferSlice writeDynamicTransforms() {
        return RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)COLOR_MODULATOR, (Vector3fc)MODEL_OFFSET, (Matrix4fc)TEXTURE_MATRIX);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Batched {
        default public int batchLayer() {
            return 0;
        }

        public void uploadBatch(CommandEncoder var1);

        public void drawBatch(RenderPass var1);

        public void discardBatch();
    }
}

