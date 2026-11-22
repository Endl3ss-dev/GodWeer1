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
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_12137
 *  net.minecraft.class_276
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
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.awt.Color;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_12137;
import net.minecraft.class_276;
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
import ru.decide.module.impl.render.ShaderSky;
import ru.decide.utils.render.KawaseBlurPipeline;

@Environment(value=EnvType.CLIENT)
public class ShaderSkyRenderer {
    private static final class_2960 SHADER_PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/shader_sky");
    private static final class_2960 BLUR_PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/shader_sky_blur");
    private static final class_2960 VERTEX_SHADER = class_2960.method_60655((String)"decide", (String)"core/shader_sky");
    private static final class_2960 FULLSCREEN_VERTEX_SHADER = class_2960.method_60655((String)"decide", (String)"core/shader_sky_fullscreen");
    private static final class_2960 SHADER_FRAGMENT = class_2960.method_60655((String)"decide", (String)"core/shader_sky");
    private static final class_2960 BLUR_FRAGMENT = class_2960.method_60655((String)"decide", (String)"core/shader_sky_blur");
    private static final RenderPipeline SHADER_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(SHADER_PIPELINE_ID).withVertexShader(VERTEX_SHADER).withFragmentShader(SHADER_FRAGMENT).withVertexFormat(class_290.field_1592, VertexFormat.class_5596.field_27379).withUniform("ShaderSkyData", class_10789.field_60031).withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final RenderPipeline BLUR_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(BLUR_PIPELINE_ID).withVertexShader(FULLSCREEN_VERTEX_SHADER).withFragmentShader(BLUR_FRAGMENT).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("ShaderSkyData", class_10789.field_60031).withSampler("Sampler0").withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vector3f MODEL_OFFSET = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final int BUFFER_SIZE = 96;
    private static ShaderSkyRenderer instance;
    private final class_310 client;
    private final KawaseBlurPipeline blurPipeline = new KawaseBlurPipeline();
    private GpuBuffer uniformBuffer;
    private GpuBuffer dummyVertexBuffer;
    private GpuBuffer celestialVertexBuffer;
    private ByteBuffer dataBuffer;
    private boolean initialized = false;
    private boolean enabled = false;
    private float celestialScale = -1.0f;
    private long startTime = System.currentTimeMillis();

    public ShaderSkyRenderer() {
        this.client = class_310.method_1551();
        instance = this;
    }

    public static ShaderSkyRenderer getInstance() {
        if (instance == null) {
            instance = new ShaderSkyRenderer();
        }
        return instance;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            this.ensureInitialized();
            this.startTime = System.currentTimeMillis();
        }
    }

    private void ensureInitialized() {
        if (this.initialized) {
            return;
        }
        this.dataBuffer = MemoryUtil.memAlloc((int)96);
        ByteBuffer dummy = MemoryUtil.memAlloc((int)4);
        dummy.putInt(0);
        dummy.flip();
        this.dummyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "client:shader_sky_dummy_vertex", 32, dummy);
        MemoryUtil.memFree((Buffer)dummy);
        this.uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "client:shader_sky_uniform", 136, 96L);
        this.ensureCelestialBuffer(this.targetCelestialScale());
        this.initialized = true;
    }

    public void renderSky() {
        ShaderSky module = ShaderSky.getInstance();
        if (!this.enabled || module == null || !module.isEnabled()) {
            return;
        }
        if (!module.mode.is("Blur")) {
            return;
        }
        class_276 framebuffer = this.client.method_1522();
        if (framebuffer == null || framebuffer.method_71639() == null) {
            return;
        }
        this.ensureInitialized();
        this.renderBlur(framebuffer, module);
    }

    public void renderCelestialShader() {
        ShaderSky module = ShaderSky.getInstance();
        if (!this.enabled || module == null || !module.isEnabled() || module.mode.is("Blur")) {
            return;
        }
        class_276 framebuffer = this.client.method_1522();
        if (framebuffer == null || framebuffer.method_71639() == null) {
            return;
        }
        this.ensureInitialized();
        this.ensureCelestialBuffer(this.targetCelestialScale());
        this.writeUniforms(framebuffer.field_1482, framebuffer.field_1481, module);
        this.drawSkybox(framebuffer);
    }

    private void renderBlur(class_276 framebuffer, ShaderSky module) {
        if (framebuffer.method_30277() == null || framebuffer.method_71639() == null) {
            return;
        }
        GpuTextureView blurred = this.blurPipeline.blur(framebuffer.method_30277(), framebuffer.method_71639(), framebuffer.field_1482, framebuffer.field_1481, 3, 3.0f);
        if (blurred == null) {
            return;
        }
        this.writeUniforms(framebuffer.field_1482, framebuffer.field_1481, module);
        this.drawFullscreen(framebuffer, BLUR_PIPELINE, blurred);
    }

    private void writeUniforms(int width, int height, ShaderSky module) {
        float alpha = ShaderSkyRenderer.getShaderAlpha(module);
        Color color1 = new Color(ShaderSky.geteColor1(), true);
        Color color2 = new Color(ShaderSky.geteColor2(), true);
        Color accentColor = Color.WHITE;
        this.dataBuffer.clear();
        this.dataBuffer.putFloat(width);
        this.dataBuffer.putFloat(height);
        this.dataBuffer.putFloat((float)(System.currentTimeMillis() - this.startTime) / 1000.0f);
        this.dataBuffer.putFloat(alpha);
        ShaderSkyRenderer.putColor(this.dataBuffer, color1, alpha);
        ShaderSkyRenderer.putColor(this.dataBuffer, color2, alpha);
        ShaderSkyRenderer.putColor(this.dataBuffer, accentColor, alpha);
        this.dataBuffer.putFloat(ShaderSky.getModeId(module.mode.getValue()));
        this.dataBuffer.putFloat(module.speed.getValue().floatValue());
        this.dataBuffer.putFloat(module.scale.getValue().floatValue());
        this.dataBuffer.putFloat(module.intensity.getValue().floatValue());
        this.dataBuffer.putFloat(module.stars.getValue().floatValue());
        this.dataBuffer.putFloat(module.vanillaSky.getValue() != false ? 1.0f : 0.0f);
        this.dataBuffer.putFloat(module.sunAngle.getValue().floatValue());
        this.dataBuffer.putFloat(0.0f);
        this.dataBuffer.flip();
        RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.uniformBuffer.slice(), this.dataBuffer);
    }

    private void drawSkybox(class_276 framebuffer) {
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)COLOR_MODULATOR, (Vector3fc)MODEL_OFFSET, (Matrix4fc)TEXTURE_MATRIX);
        try (RenderPass renderPass = encoder.createRenderPass(() -> "client:shader_sky_pass", framebuffer.method_71639(), OptionalInt.empty(), framebuffer.method_71640(), OptionalDouble.empty());){
            renderPass.setPipeline(SHADER_PIPELINE);
            renderPass.setVertexBuffer(0, this.celestialVertexBuffer);
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
            renderPass.setUniform("DynamicTransforms", transforms);
            renderPass.setUniform("ShaderSkyData", this.uniformBuffer);
            renderPass.draw(0, 36);
        }
    }

    private static void putColor(ByteBuffer buffer, Color color, float opacity) {
        buffer.putFloat((float)color.getRed() / 255.0f);
        buffer.putFloat((float)color.getGreen() / 255.0f);
        buffer.putFloat((float)color.getBlue() / 255.0f);
        buffer.putFloat((float)color.getAlpha() / 255.0f * opacity);
    }

    private static int getModeId(String mode) {
        return switch (mode) {
            case "Night" -> 1;
            case "Snow" -> 2;
            case "Sky" -> 3;
            case "Star" -> 4;
            case "Glow" -> 5;
            case "Full" -> 6;
            case "WebShader" -> 7;
            case "Plasma" -> 8;
            case "ChamsFill" -> 9;
            case "BaseWarp" -> 10;
            case "Waves" -> 11;
            case "PhobAurora" -> 12;
            case "BlackHole" -> 13;
            case "Galaxy" -> 14;
            case "MilkyWay" -> 15;
            case "Origin" -> 16;
            case "QuantumNebula" -> 17;
            case "AmbGalaxy", "Space" -> 18;
            case "SpiralGalaxy" -> 19;
            case "Darkness" -> 20;
            case "Pseudo" -> 21;
            case "Cyclone" -> 22;
            case "Lightning" -> 23;
            case "Juice" -> 24;
            case "Nebula" -> 25;
            case "Sunset" -> 26;
            case "Clouds" -> 27;
            case "SolarStorm" -> 28;
            case "AuroraVeil" -> 29;
            case "SolarWind" -> 30;
            case "FrozenNebula" -> 31;
            case "EmberSky" -> 32;
            case "Oceanic" -> 33;
            case "VoidRift" -> 34;
            case "Prism" -> 35;
            case "MeteorShower" -> 36;
            case "LunarHalo" -> 37;
            case "ElectricDream" -> 38;
            case "RoseQuartz" -> 39;
            case "EmeraldDrift" -> 40;
            case "EclipseWave" -> 41;
            case "CosmicDust" -> 42;
            case "IonStorm" -> 43;
            case "CelestialBloom" -> 44;
            case "MidnightRain" -> 45;
            case "GoldenHour" -> 46;
            case "Abyssal" -> 47;
            default -> 0;
        };
    }

    private static float getShaderAlpha(ShaderSky module) {
        if (module.mode.is("Blur")) {
            return module.intensity.getValue().floatValue();
        }
        if (module.vanillaSky.getValue().booleanValue()) {
            return 0.78f;
        }
        return 1.0f;
    }

    private void drawFullscreen(class_276 framebuffer, RenderPipeline pipeline, GpuTextureView texture) {
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)COLOR_MODULATOR, (Vector3fc)MODEL_OFFSET, (Matrix4fc)TEXTURE_MATRIX);
        try (RenderPass renderPass = encoder.createRenderPass(() -> "client:shader_sky_pass", framebuffer.method_71639(), OptionalInt.empty(), framebuffer.method_71640(), OptionalDouble.empty());){
            renderPass.setPipeline(pipeline);
            renderPass.setVertexBuffer(0, this.dummyVertexBuffer);
            if (texture != null) {
                class_12137 sampler = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
                renderPass.bindTexture("Sampler0", texture, sampler);
            }
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
            renderPass.setUniform("DynamicTransforms", transforms);
            renderPass.setUniform("ShaderSkyData", this.uniformBuffer);
            renderPass.draw(0, 6);
        }
    }

    private static ByteBuffer createCelestialQuad(float s) {
        float[] vertices = new float[]{-s, -s, -s, s, -s, -s, s, s, -s, -s, -s, -s, s, s, -s, -s, s, -s, s, -s, s, -s, -s, s, -s, s, s, s, -s, s, -s, s, s, s, s, s, -s, -s, s, -s, -s, -s, -s, s, -s, -s, -s, s, -s, s, -s, -s, s, s, s, -s, -s, s, -s, s, s, s, s, s, -s, -s, s, s, s, s, s, -s, -s, s, -s, s, s, -s, s, s, s, -s, s, -s, s, s, s, -s, s, s, -s, -s, s, s, -s, s, s, -s, -s, -s, -s, s, s, -s, -s, -s, -s, -s};
        ByteBuffer buffer = MemoryUtil.memAlloc((int)(vertices.length * 4));
        for (float vertex : vertices) {
            buffer.putFloat(vertex);
        }
        buffer.flip();
        return buffer;
    }

    private void ensureCelestialBuffer(float scale) {
        if (this.celestialVertexBuffer != null && Math.abs(this.celestialScale - scale) < 0.5f) {
            return;
        }
        if (this.celestialVertexBuffer != null) {
            this.celestialVertexBuffer.close();
            this.celestialVertexBuffer = null;
        }
        this.celestialScale = scale;
        ByteBuffer cup = ShaderSkyRenderer.createCelestialQuad(scale);
        this.celestialVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "client:shader_sky_celestial_vertex", 32, cup);
        MemoryUtil.memFree((Buffer)cup);
    }

    private float targetCelestialScale() {
        float viewDist = (float)Math.max(2, this.client.field_1690.method_38521()) * 16.0f;
        return Math.max(16.0f, Math.min(512.0f, viewDist * 0.7f));
    }

    public void invalidate() {
        this.enabled = false;
        this.close();
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
        if (this.celestialVertexBuffer != null) {
            this.celestialVertexBuffer.close();
            this.celestialVertexBuffer = null;
        }
        if (this.dataBuffer != null) {
            MemoryUtil.memFree((Buffer)this.dataBuffer);
            this.dataBuffer = null;
        }
        this.blurPipeline.close();
        this.initialized = false;
    }
}

