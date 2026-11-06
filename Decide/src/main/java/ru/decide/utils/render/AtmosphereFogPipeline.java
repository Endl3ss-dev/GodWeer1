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
 *  net.minecraft.class_12137
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  org.lwjgl.system.MemoryStack
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.utils.render;

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
import net.minecraft.class_12137;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class AtmosphereFogPipeline {
    public static final int UNIFORM_FLOATS = 92;
    private static final int UNIFORM_SIZE = 368;
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"client/AtmosphereFog");
    private static final class_2960 PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/post/atmospherefog");
    private static final class_2960 SHADER = class_2960.method_60655((String)"decide", (String)"post/atmospherefog/atmospherefog");
    public static final int OFF_RESOLUTION = 0;
    public static final int OFF_INV_PROJECTION = 4;
    public static final int OFF_INV_VIEW = 20;
    public static final int OFF_CAMERA_POS = 36;
    public static final int OFF_SUN_DIRECTION = 40;
    public static final int OFF_SUN_SCREEN = 44;
    public static final int OFF_FOG_A = 48;
    public static final int OFF_FOG_B = 52;
    public static final int OFF_SKY = 56;
    public static final int OFF_RAYS = 60;
    public static final int OFF_RAY_COLOR = 64;
    public static final int OFF_PALETTE = 68;
    private static RenderPipeline pipeline;
    private static GpuBuffer uniformBuffer;
    private static GpuTexture sceneCopy;
    private static GpuTextureView sceneCopyView;
    private static GpuTexture depthCopy;
    private static GpuTextureView depthCopyView;
    private static int sceneWidth;
    private static int sceneHeight;
    private static boolean disabledAfterError;

    private AtmosphereFogPipeline() {
    }

    public static void apply(class_276 renderTarget, float[] uniforms) {
        if (disabledAfterError || renderTarget == null || uniforms == null) {
            return;
        }
        if (renderTarget.method_30277() == null || renderTarget.method_71639() == null) {
            return;
        }
        if (renderTarget.method_30278() == null) {
            return;
        }
        if (renderTarget.field_1482 <= 0 || renderTarget.field_1481 <= 0) {
            return;
        }
        AtmosphereFogPipeline.init();
        if (pipeline == null || uniformBuffer == null) {
            return;
        }
        if (!AtmosphereFogPipeline.ensureSceneCopy(renderTarget.field_1482, renderTarget.field_1481)) {
            return;
        }
        try {
            class_12137 linear = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
            class_12137 nearest = RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST);
            CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
            encoder.copyTextureToTexture(renderTarget.method_30277(), sceneCopy, 0, 0, 0, 0, 0, renderTarget.field_1482, renderTarget.field_1481);
            encoder.copyTextureToTexture(renderTarget.method_30278(), depthCopy, 0, 0, 0, 0, 0, renderTarget.field_1482, renderTarget.field_1481);
            try (MemoryStack stack = MemoryStack.stackPush();){
                ByteBuffer data = stack.calloc(368);
                for (int i = 0; i < 92; ++i) {
                    data.putFloat(i * 4, i < uniforms.length ? uniforms[i] : 0.0f);
                }
                data.position(0);
                encoder.writeToBuffer(uniformBuffer.slice(0L, 368L), data);
            }
            try (RenderPass pass = encoder.createRenderPass(() -> "client:atmosphere_fog", renderTarget.method_71639(), OptionalInt.empty());){
                pass.setPipeline(pipeline);
                pass.bindTexture("Sampler0", sceneCopyView, linear);
                pass.bindTexture("Sampler1", depthCopyView, nearest);
                pass.setUniform("Fog", uniformBuffer);
                pass.draw(0, 6);
            }
        }
        catch (Throwable t) {
            disabledAfterError = true;
            LOGGER.error("[AtmosphereFog] apply failed, effect disabled", t);
            AtmosphereFogPipeline.closeTargets();
        }
    }

    public static boolean deviceReady() {
        return RenderSystem.tryGetDevice() != null;
    }

    public static boolean validate() {
        if (disabledAfterError) {
            return false;
        }
        if (!AtmosphereFogPipeline.deviceReady()) {
            return false;
        }
        AtmosphereFogPipeline.init();
        return pipeline != null && uniformBuffer != null;
    }

    private static void init() {
        if (disabledAfterError || pipeline != null) {
            return;
        }
        try {
            pipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(PIPELINE_ID).withVertexShader(SHADER).withFragmentShader(SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("Fog", class_10789.field_60031).withSampler("Sampler0").withSampler("Sampler1").withoutBlend().withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
            GpuBuffer current = uniformBuffer;
            if (current == null || current.isClosed() || current.size() < 368L) {
                AtmosphereFogPipeline.closeUniform();
                uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "client:atmosphere_fog_uniforms", 136, 368L);
            }
        }
        catch (Throwable t) {
            disabledAfterError = true;
            LOGGER.error("[AtmosphereFog] pipeline init failed, effect disabled", t);
            pipeline = null;
            AtmosphereFogPipeline.closeUniform();
        }
    }

    private static boolean ensureSceneCopy(int width, int height) {
        GpuDevice device = RenderSystem.tryGetDevice();
        if (device == null) {
            return false;
        }
        if (sceneCopy != null && sceneWidth == width && sceneHeight == height) {
            return true;
        }
        AtmosphereFogPipeline.closeTargets();
        sceneCopy = device.createTexture(() -> "client:atmosphere_fog_scene", 5, TextureFormat.RGBA8, width, height, 1, 1);
        sceneCopyView = device.createTextureView(sceneCopy);
        depthCopy = device.createTexture(() -> "client:atmosphere_fog_depth", 5, TextureFormat.DEPTH32, width, height, 1, 1);
        depthCopyView = device.createTextureView(depthCopy);
        sceneWidth = width;
        sceneHeight = height;
        return true;
    }

    public static void clear() {
        AtmosphereFogPipeline.closeTargets();
    }

    private static void closeTargets() {
        if (sceneCopyView != null) {
            sceneCopyView.close();
            sceneCopyView = null;
        }
        if (sceneCopy != null) {
            sceneCopy.close();
            sceneCopy = null;
        }
        if (depthCopyView != null) {
            depthCopyView.close();
            depthCopyView = null;
        }
        if (depthCopy != null) {
            depthCopy.close();
            depthCopy = null;
        }
        sceneWidth = -1;
        sceneHeight = -1;
    }

    private static void closeUniform() {
        if (uniformBuffer != null) {
            uniformBuffer.close();
            uniformBuffer = null;
        }
    }

    static {
        sceneWidth = -1;
        sceneHeight = -1;
    }
}

