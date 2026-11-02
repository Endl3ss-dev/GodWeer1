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

@Environment(value=EnvType.CLIENT)
public class ShaderEspOutlinePipeline {
    private static final int MAX_MIPS = 5;
    private static final int UBO_SIZE = 112;
    private static final class_2960 VSH = class_2960.method_60655((String)"decide", (String)"core/shader_esp_glow");
    private static final RenderPipeline DOWN_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/shader_esp_down")).withVertexShader(VSH).withFragmentShader(class_2960.method_60655((String)"decide", (String)"core/shader_esp_down")).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("EspKawaseData", class_10789.field_60031).withSampler("Sampler0").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final RenderPipeline UP_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/shader_esp_up")).withVertexShader(VSH).withFragmentShader(class_2960.method_60655((String)"decide", (String)"core/shader_esp_up")).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("EspKawaseData", class_10789.field_60031).withSampler("Sampler0").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final RenderPipeline COMBINE_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/shader_esp_combine")).withVertexShader(VSH).withFragmentShader(class_2960.method_60655((String)"decide", (String)"core/shader_esp_combine")).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("EspData", class_10789.field_60031).withSampler("maskTexture").withSampler("glowTexture").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector3f MODEL_OFFSET = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private final GpuTexture[] mipTextures = new GpuTexture[5];
    private final GpuTextureView[] mipViews = new GpuTextureView[5];
    private final int[] mipWidths = new int[5];
    private final int[] mipHeights = new int[5];
    private GpuBuffer kawaseBuffer;
    private GpuBuffer espBuffer;
    private GpuBuffer dummyVertexBuffer;
    private ByteBuffer kawaseData;
    private ByteBuffer espData;
    private boolean initialized = false;
    private int lastWidth = 0;
    private int lastHeight = 0;

    private void ensureInitialized() {
        if (this.initialized) {
            return;
        }
        this.kawaseData = MemoryUtil.memAlloc((int)16);
        this.espData = MemoryUtil.memAlloc((int)112);
        ByteBuffer dummy = MemoryUtil.memAlloc((int)4);
        dummy.putInt(0);
        dummy.flip();
        this.dummyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "minecraft:shader_esp_dummy_vertex", 32, dummy);
        MemoryUtil.memFree((Buffer)dummy);
        this.kawaseBuffer = RenderSystem.getDevice().createBuffer(() -> "minecraft:shader_esp_kawase_ubo", 136, 16L);
        this.espBuffer = RenderSystem.getDevice().createBuffer(() -> "minecraft:shader_esp_ubo", 136, 112L);
        this.initialized = true;
    }

    private void ensureMips(int width, int height) {
        if (width == this.lastWidth && height == this.lastHeight && this.mipTextures[0] != null) {
            return;
        }
        this.cleanupMips();
        int w = width;
        int h = height;
        for (int i = 0; i < 5; ++i) {
            w = Math.max(1, w / 2);
            h = Math.max(1, h / 2);
            this.mipWidths[i] = w;
            this.mipHeights[i] = h;
            int fw = w;
            int fh = h;
            int idx = i;
            this.mipTextures[i] = RenderSystem.getDevice().createTexture(() -> "minecraft:shader_esp_mip_" + idx, 12, TextureFormat.RGBA8, fw, fh, 1, 1);
            this.mipViews[i] = RenderSystem.getDevice().createTextureView(this.mipTextures[i]);
        }
        this.lastWidth = width;
        this.lastHeight = height;
    }

    private void cleanupMips() {
        for (int i = 0; i < 5; ++i) {
            if (this.mipViews[i] != null) {
                this.mipViews[i].close();
                this.mipViews[i] = null;
            }
            if (this.mipTextures[i] == null) continue;
            this.mipTextures[i].close();
            this.mipTextures[i] = null;
        }
    }

    private void writeKawaseUbo(CommandEncoder encoder, float halfPixelX, float halfPixelY, float offset) {
        this.kawaseData.clear();
        this.kawaseData.putFloat(halfPixelX);
        this.kawaseData.putFloat(halfPixelY);
        this.kawaseData.putFloat(offset);
        this.kawaseData.putFloat(0.0f);
        this.kawaseData.flip();
        encoder.writeToBuffer(this.kawaseBuffer.slice(), this.kawaseData);
    }

    private void writeEspUbo(CommandEncoder encoder, int width, int height, Params p) {
        this.espData.clear();
        this.putRgb(p.color);
        this.espData.putFloat(ShaderEspOutlinePipeline.clamp(p.opacity, 0.0f, 1.0f));
        this.putRgb(p.friendColor);
        this.espData.putFloat(p.friendEnabled ? 1.0f : 0.0f);
        this.espData.putFloat(1.0f / (float)width);
        this.espData.putFloat(1.0f / (float)height);
        this.espData.putFloat(Math.max(0, Math.min(5, p.outlineWidth)));
        this.espData.putFloat(p.outlineMode);
        this.espData.putFloat(Math.max(0.0f, p.glowStrength));
        this.espData.putFloat(Math.max(0.05f, p.glowFalloff));
        this.espData.putFloat(Math.max(0.0f, p.fillOpacity));
        this.espData.putFloat(Math.max(0.0f, p.innerGlow));
        this.espData.putFloat(Math.max(0.0f, p.outlineStrength));
        this.espData.putFloat(ShaderEspOutlinePipeline.clamp(p.outlineWhite, 0.0f, 1.0f));
        this.espData.putFloat(p.glowEnabled ? 1.0f : 0.0f);
        this.espData.putFloat(p.fillEnabled ? 1.0f : 0.0f);
        this.espData.putFloat(ShaderEspOutlinePipeline.phase(p.shimmerPeriodSec));
        this.espData.putFloat(p.shimmerEnabled ? 1.0f : 0.0f);
        this.espData.putFloat(Math.max(0.005f, p.shimmerWidth));
        this.espData.putFloat(Math.max(0.0f, p.shimmerBrightness));
        this.espData.putFloat(p.pulseEnabled ? ShaderEspOutlinePipeline.clamp(p.pulseAmount, 0.0f, 1.0f) : 0.0f);
        this.espData.putFloat(ShaderEspOutlinePipeline.phase(1.0f / Math.max(0.05f, p.pulseSpeed)));
        this.espData.putFloat(Math.max(0.0f, p.saturation));
        this.espData.putFloat(p.outlineEnabled ? 1.0f : 0.0f);
        this.espData.flip();
        encoder.writeToBuffer(this.espBuffer.slice(), this.espData);
    }

    private void putRgb(int argb) {
        this.espData.putFloat((float)(argb >> 16 & 0xFF) / 255.0f);
        this.espData.putFloat((float)(argb >> 8 & 0xFF) / 255.0f);
        this.espData.putFloat((float)(argb & 0xFF) / 255.0f);
    }

    private static float clamp(float v, float min, float max) {
        return v < min ? min : Math.min(v, max);
    }

    private static float phase(float periodSec) {
        long periodMs = Math.max(1L, (long)(periodSec * 1000.0f));
        return (float)(System.currentTimeMillis() % periodMs) / (float)periodMs;
    }

    public void render(GpuTextureView maskView, GpuTextureView targetView, int width, int height, Params p) {
        GpuBufferSlice dt;
        int i;
        this.ensureInitialized();
        this.ensureMips(width, height);
        float radius = ShaderEspOutlinePipeline.clamp(p.glowRadius, 0.0f, 1.0f);
        int levels = Math.max(1, Math.min(5, 1 + Math.round(radius * 4.0f)));
        float upOffset = 0.5f + radius * 3.5f;
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        class_12137 linearSampler = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
        GpuTextureView currentInput = maskView;
        int inputW = width;
        int inputH = height;
        for (i = 0; i < levels; ++i) {
            this.writeKawaseUbo(encoder, 0.5f / (float)inputW, 0.5f / (float)inputH, 1.0f);
            dt = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)COLOR_MODULATOR, (Vector3fc)MODEL_OFFSET, (Matrix4fc)TEXTURE_MATRIX);
            GpuTextureView src = currentInput;
            int fi = i;
            try (RenderPass rp = encoder.createRenderPass(() -> "minecraft:shader_esp_down_" + fi, this.mipViews[fi], OptionalInt.of(0));){
                rp.setPipeline(DOWN_PIPELINE);
                rp.setVertexBuffer(0, this.dummyVertexBuffer);
                rp.bindTexture("Sampler0", src, linearSampler);
                RenderSystem.bindDefaultUniforms((RenderPass)rp);
                rp.setUniform("DynamicTransforms", dt);
                rp.setUniform("EspKawaseData", this.kawaseBuffer);
                rp.draw(0, 6);
            }
            currentInput = this.mipViews[i];
            inputW = this.mipWidths[i];
            inputH = this.mipHeights[i];
        }
        i = levels - 1;
        while (i > 0) {
            this.writeKawaseUbo(encoder, 0.5f / (float)this.mipWidths[i], 0.5f / (float)this.mipHeights[i], upOffset);
            dt = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)COLOR_MODULATOR, (Vector3fc)MODEL_OFFSET, (Matrix4fc)TEXTURE_MATRIX);
            int fi = i--;
            try (RenderPass rp = encoder.createRenderPass(() -> "minecraft:shader_esp_up_" + (fi - 1), this.mipViews[fi - 1], OptionalInt.of(0));){
                rp.setPipeline(UP_PIPELINE);
                rp.setVertexBuffer(0, this.dummyVertexBuffer);
                rp.bindTexture("Sampler0", this.mipViews[fi], linearSampler);
                RenderSystem.bindDefaultUniforms((RenderPass)rp);
                rp.setUniform("DynamicTransforms", dt);
                rp.setUniform("EspKawaseData", this.kawaseBuffer);
                rp.draw(0, 6);
            }
        }
        this.writeEspUbo(encoder, width, height, p);
        GpuBufferSlice dt2 = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)COLOR_MODULATOR, (Vector3fc)MODEL_OFFSET, (Matrix4fc)TEXTURE_MATRIX);
        try (RenderPass rp = encoder.createRenderPass(() -> "minecraft:shader_esp_combine", targetView, OptionalInt.empty());){
            rp.setPipeline(COMBINE_PIPELINE);
            rp.setVertexBuffer(0, this.dummyVertexBuffer);
            rp.bindTexture("maskTexture", maskView, linearSampler);
            rp.bindTexture("glowTexture", this.mipViews[0], linearSampler);
            RenderSystem.bindDefaultUniforms((RenderPass)rp);
            rp.setUniform("DynamicTransforms", dt2);
            rp.setUniform("EspData", this.espBuffer);
            rp.draw(0, 6);
        }
    }

    public void close() {
        this.cleanupMips();
        if (this.kawaseBuffer != null) {
            this.kawaseBuffer.close();
            this.kawaseBuffer = null;
        }
        if (this.espBuffer != null) {
            this.espBuffer.close();
            this.espBuffer = null;
        }
        if (this.dummyVertexBuffer != null) {
            this.dummyVertexBuffer.close();
            this.dummyVertexBuffer = null;
        }
        if (this.kawaseData != null) {
            MemoryUtil.memFree((Buffer)this.kawaseData);
            this.kawaseData = null;
        }
        if (this.espData != null) {
            MemoryUtil.memFree((Buffer)this.espData);
            this.espData = null;
        }
        this.initialized = false;
        this.lastWidth = 0;
        this.lastHeight = 0;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Params {
        public int color = -16711681;
        public int friendColor = -11141291;
        public boolean friendEnabled = true;
        public float opacity = 1.0f;
        public float saturation = 1.0f;
        public boolean glowEnabled = true;
        public float glowRadius = 0.5f;
        public float glowStrength = 1.4f;
        public float glowFalloff = 1.2f;
        public boolean fillEnabled = true;
        public float fillOpacity = 0.35f;
        public float innerGlow = 0.5f;
        public boolean outlineEnabled = true;
        public int outlineWidth = 2;
        public int outlineMode = 0;
        public float outlineStrength = 1.5f;
        public float outlineWhite = 0.5f;
        public boolean pulseEnabled = false;
        public float pulseSpeed = 1.0f;
        public float pulseAmount = 0.3f;
        public boolean shimmerEnabled = true;
        public float shimmerWidth = 0.04f;
        public float shimmerPeriodSec = 5.0f;
        public float shimmerBrightness = 0.8f;
    }
}

