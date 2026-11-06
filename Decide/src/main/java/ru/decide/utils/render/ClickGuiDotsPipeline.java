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
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
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
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
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
public class ClickGuiDotsPipeline {
    private static final class_2960 PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/clickgui_dots");
    private static final class_2960 VERTEX_SHADER = class_2960.method_60655((String)"decide", (String)"core/clickgui_dots");
    private static final class_2960 FRAGMENT_SHADER = class_2960.method_60655((String)"decide", (String)"core/clickgui_dots");
    private static final Vector3f MODEL_OFFSET = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final float FIXED_GUI_SCALE = 2.0f;
    private static final int BUFFER_SIZE = 80;
    private static final RenderPipeline PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(PIPELINE_ID).withVertexShader(VERTEX_SHADER).withFragmentShader(FRAGMENT_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("ClickGuiDotsData", class_10789.field_60031).withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private GpuBuffer uniformBuffer;
    private GpuBuffer dummyVertexBuffer;
    private ByteBuffer dataBuffer;
    private boolean initialized;

    private void ensureInitialized() {
        if (this.initialized) {
            return;
        }
        this.dataBuffer = MemoryUtil.memAlloc((int)80);
        ByteBuffer dummyData = MemoryUtil.memAlloc((int)4);
        dummyData.putInt(0);
        dummyData.flip();
        this.dummyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "client:clickgui_dots_dummy_vertex", 32, dummyData);
        MemoryUtil.memFree((Buffer)dummyData);
        this.initialized = true;
    }

    public void draw(float mouseX, float mouseY, float alpha, int primaryColor, int secondaryColor) {
        if (alpha <= 0.01f) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.method_1522() == null) {
            return;
        }
        this.ensureInitialized();
        DrawBatcher.flushPending();
        int framebufferWidth = client.method_22683().method_4489();
        int framebufferHeight = client.method_22683().method_4506();
        float fixedScreenWidth = (float)framebufferWidth / 2.0f;
        float fixedScreenHeight = (float)framebufferHeight / 2.0f;
        this.dataBuffer.clear();
        this.dataBuffer.putFloat(0.0f);
        this.dataBuffer.putFloat(0.0f);
        this.dataBuffer.putFloat(fixedScreenWidth);
        this.dataBuffer.putFloat(fixedScreenHeight);
        this.dataBuffer.putFloat(fixedScreenWidth);
        this.dataBuffer.putFloat(fixedScreenHeight);
        this.dataBuffer.putFloat(2.0f);
        this.dataBuffer.putFloat(alpha);
        this.dataBuffer.putFloat(mouseX);
        this.dataBuffer.putFloat(mouseY);
        this.dataBuffer.putFloat((float)(System.currentTimeMillis() % 100000L) / 1000.0f);
        this.dataBuffer.putFloat(0.0f);
        this.putColor(primaryColor, alpha);
        this.putColor(secondaryColor, alpha);
        this.dataBuffer.flip();
        this.uploadAndDraw(client);
    }

    private void putColor(int color, float alphaMul) {
        this.dataBuffer.putFloat((float)(color >> 16 & 0xFF) / 255.0f);
        this.dataBuffer.putFloat((float)(color >> 8 & 0xFF) / 255.0f);
        this.dataBuffer.putFloat((float)(color & 0xFF) / 255.0f);
        this.dataBuffer.putFloat((float)(color >> 24 & 0xFF) / 255.0f * alphaMul);
    }

    private void uploadAndDraw(class_310 client) {
        int size = this.dataBuffer.remaining();
        if (this.uniformBuffer == null || this.uniformBuffer.size() < (long)size) {
            if (this.uniformBuffer != null) {
                this.uniformBuffer.close();
            }
            this.uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "client:clickgui_dots_uniform", 136, (long)size);
        }
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        encoder.writeToBuffer(this.uniformBuffer.slice(), this.dataBuffer);
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)COLOR_MODULATOR, (Vector3fc)MODEL_OFFSET, (Matrix4fc)TEXTURE_MATRIX);
        try (RenderPass renderPass = encoder.createRenderPass(() -> "client:clickgui_dots_pass", client.method_1522().method_71639(), OptionalInt.empty(), client.method_1522().method_71640(), OptionalDouble.empty());){
            renderPass.setPipeline(PIPELINE);
            renderPass.setVertexBuffer(0, this.dummyVertexBuffer);
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.setUniform("ClickGuiDotsData", this.uniformBuffer);
            renderPass.draw(0, 6);
        }
    }

    public void close() {
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
        this.initialized = false;
    }
}

