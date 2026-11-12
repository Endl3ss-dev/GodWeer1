/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.Std140Builder
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_276
 *  net.minecraft.class_279
 *  net.minecraft.class_279$class_9961
 *  net.minecraft.class_283
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_9909
 *  net.minecraft.class_9922
 *  net.minecraft.class_9923
 *  net.minecraft.class_9924
 *  net.minecraft.class_9925
 *  org.lwjgl.system.MemoryStack
 */
package ru.decide.utils.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_276;
import net.minecraft.class_279;
import net.minecraft.class_283;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_9909;
import net.minecraft.class_9922;
import net.minecraft.class_9923;
import net.minecraft.class_9924;
import net.minecraft.class_9925;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public final class PostEffect {
    private static final class_2960 FULL = class_2960.method_60655((String)"decide", (String)"cinematic_full");
    private static final Set<class_2960> TARGETS = Set.of(class_279.field_53104, FULL);
    private final class_2960 id;
    private final String block;
    private final int size;
    private final int shrink;
    private final Consumer<Std140Builder> writer;
    private class_279 bound;
    private GpuBuffer uniforms;

    public PostEffect(class_2960 id, String block, int size, Consumer<Std140Builder> writer) {
        this(id, block, size, 1, writer);
    }

    public PostEffect(class_2960 id, String block, int size, int shrink, Consumer<Std140Builder> writer) {
        this.id = id;
        this.block = block;
        this.size = size + 15 & 0xFFFFFFF0;
        this.shrink = shrink;
        this.writer = writer;
    }

    public void run(class_9922 allocator) {
        class_310 client = class_310.method_1551();
        class_279 processor = client.method_62887().method_62941(this.id, TARGETS);
        if (processor == null) {
            return;
        }
        if (processor != this.bound) {
            this.bind(processor);
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            Std140Builder builder = Std140Builder.onStack((MemoryStack)stack, (int)this.size);
            this.writer.accept(builder);
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.uniforms.slice(), builder.get());
        }
        class_276 main = client.method_1522();
        class_9909 graph = new class_9909();
        HashMap<class_2960, class_9925<class_276>> handles = new HashMap<class_2960, class_9925<class_276>>();
        handles.put(class_279.field_53104, graph.method_61914("main", (Object)main));
        handles.put(FULL, graph.method_61912("full", (class_9924)new class_9923(main.field_1482, main.field_1481, false, 0)));
        int width = Math.max(1, main.field_1482 / this.shrink);
        int height = Math.max(1, main.field_1481 / this.shrink);
        processor.method_62234(graph, width, height, (class_279.class_9961)new Targets(handles));
        graph.method_61909(allocator);
    }

    private void bind(class_279 processor) {
        this.bound = processor;
        this.uniforms = RenderSystem.getDevice().createBuffer(() -> String.valueOf(this.id) + " UBO", 136, (long)this.size);
        for (class_283 pass : processor.field_1497) {
            GpuBuffer previous;
            Map buffers = pass.field_60123;
            if (!buffers.containsKey(this.block) || (previous = buffers.put(this.block, this.uniforms)) == null) continue;
            previous.close();
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record Targets(Map<class_2960, class_9925<class_276>> handles) implements class_279.class_9961
    {
        public void method_62225(class_2960 id, class_9925<class_276> handle) {
            this.handles.put(id, handle);
        }

        public class_9925<class_276> method_62224(class_2960 id) {
            return this.handles.get(id);
        }
    }
}

