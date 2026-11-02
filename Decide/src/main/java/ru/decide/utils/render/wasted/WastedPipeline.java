/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.GpuDevice
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
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.utils.render.wasted;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.utils.render.wasted.WastedState;

@Environment(value=EnvType.CLIENT)
public final class WastedPipeline {
    private static final int UNIFORM_FLOATS = 12;
    private static final int UNIFORM_SIZE = 48;
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"client/Wasted");
    private static final class_2960 PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/post/wasted");
    private static final class_2960 SHADER = class_2960.method_60655((String)"decide", (String)"post/wasted/wasted");
    private static RenderPipeline pipeline;
    private static ByteBuffer dataBuffer;
    private static GpuBuffer uniformBuffer;
    private static GpuTexture sceneCopy;
    private static GpuTextureView sceneCopyView;
    private static int lastWidth;
    private static int lastHeight;
    private static boolean disabledAfterError;

    private WastedPipeline() {
    }

    public static void invalidate() {
        disabledAfterError = false;
        WastedPipeline.closeTextures();
    }

    public static void apply(float tintR, float tintG, float tintB, float tintStrength) {
        if (disabledAfterError || !WastedState.isActive()) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_276 fb = client.method_1522();
        if (fb == null) {
            return;
        }
        int width = fb.field_1482;
        int height = fb.field_1481;
        if (width <= 0 || height <= 0) {
            return;
        }
        if (!WastedPipeline.ensureInitialized()) {
            return;
        }
        try {
            WastedPipeline.ensureTextures(width, height);
            ByteBuffer data = dataBuffer;
            GpuBuffer uniform = uniformBuffer;
            CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
            encoder.copyTextureToTexture(fb.method_30277(), sceneCopy, 0, 0, 0, 0, 0, width, height);
            data.clear();
            data.putFloat(WastedState.progress());
            data.putFloat((float)(System.currentTimeMillis() % 100000L) / 1000.0f);
            data.putFloat((float)width / (float)Math.max(1, height));
            data.putFloat(WastedState.strength());
            data.putFloat(tintR);
            data.putFloat(tintG);
            data.putFloat(tintB);
            data.putFloat(tintStrength);
            data.putFloat(WastedState.flash());
            data.putFloat(WastedState.radialBlur());
            data.putFloat(0.0f);
            data.putFloat(0.0f);
            data.flip();
            encoder.writeToBuffer(uniform.slice(), data);
            try (RenderPass pass = encoder.createRenderPass(() -> "client:wasted", fb.method_71639(), OptionalInt.empty());){
                pass.setPipeline(pipeline);
                pass.bindTexture("Sampler0", sceneCopyView, RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR));
                pass.setUniform("WastedData", uniform.slice());
                pass.draw(0, 6);
            }
        }
        catch (Throwable t) {
            disabledAfterError = true;
            LOGGER.error("[Wasted] apply failed, effect disabled", t);
            WastedPipeline.closeTextures();
        }
    }

    public static boolean deviceReady() {
        return RenderSystem.tryGetDevice() != null;
    }

    public static boolean validate() {
        if (disabledAfterError) {
            return false;
        }
        if (!WastedPipeline.deviceReady()) {
            return false;
        }
        return WastedPipeline.ensureInitialized();
    }

    private static boolean ensureInitialized() {
        GpuBuffer current = uniformBuffer;
        if (pipeline != null && dataBuffer != null && current != null && !current.isClosed()) {
            return true;
        }
        try {
            GpuBuffer uniform;
            if (pipeline == null) {
                pipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(PIPELINE_ID).withVertexShader(SHADER).withFragmentShader(SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("WastedData", class_10789.field_60031).withSampler("Sampler0").withoutBlend().withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
            }
            if (dataBuffer == null) {
                dataBuffer = MemoryUtil.memAlloc((int)48);
            }
            if ((uniform = uniformBuffer) == null || uniform.isClosed()) {
                uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "client:wasted_uniforms", 136, 48L);
            }
            return true;
        }
        catch (Throwable t) {
            disabledAfterError = true;
            pipeline = null;
            LOGGER.error("[Wasted] pipeline init failed, effect disabled", t);
            WastedPipeline.closeUniform();
            return false;
        }
    }

    private static void ensureTextures(int width, int height) {
        GpuTexture current = sceneCopy;
        if (current != null && !current.isClosed() && width == lastWidth && height == lastHeight) {
            return;
        }
        WastedPipeline.closeTextures();
        GpuDevice device = RenderSystem.tryGetDevice();
        if (device == null) {
            return;
        }
        sceneCopy = device.createTexture(() -> "client:wasted_scene", 5, TextureFormat.RGBA8, width, height, 1, 1);
        sceneCopyView = device.createTextureView(sceneCopy);
        lastWidth = width;
        lastHeight = height;
    }

    private static void closeTextures() {
        if (sceneCopyView != null) {
            sceneCopyView.close();
            sceneCopyView = null;
        }
        if (sceneCopy != null) {
            sceneCopy.close();
            sceneCopy = null;
        }
        lastWidth = -1;
        lastHeight = -1;
    }

    private static void closeUniform() {
        if (uniformBuffer != null) {
            uniformBuffer.close();
            uniformBuffer = null;
        }
    }

    static {
        lastWidth = -1;
        lastHeight = -1;
    }
}

