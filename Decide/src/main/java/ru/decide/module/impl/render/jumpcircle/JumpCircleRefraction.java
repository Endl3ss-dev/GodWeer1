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
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_12137
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.system.MemoryStack
 */
package ru.decide.module.impl.render.jumpcircle;

import com.mojang.blaze3d.buffers.GpuBuffer;
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
import java.nio.ByteBuffer;
import java.util.List;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_12137;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public final class JumpCircleRefraction {
    public static final int UNIFORM_SIZE = 2416;
    public static final int MAX_RINGS = 16;
    public static final int WALL_COUNT = 32;
    private static final class_2960 PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/jump_circle");
    private static final class_2960 SHADER_ID = class_2960.method_60655((String)"decide", (String)"core/jump_circle");
    private static RenderPipeline pipeline;
    private static GpuBuffer uniform;
    private static GpuTexture sceneTexture;
    private static GpuTextureView sceneView;
    private static int sceneWidth;
    private static int sceneHeight;
    private static boolean disabled;

    private JumpCircleRefraction() {
    }

    public static void render(class_276 target, Matrix4f position, Matrix4f projection, List<Ring> rings, Params params, long now) {
        if (disabled || target == null || position == null || projection == null) {
            return;
        }
        if (target.method_30277() == null || target.method_71639() == null || target.method_30278() == null || target.method_71640() == null) {
            return;
        }
        if (target.field_1482 <= 0 || target.field_1481 <= 0) {
            return;
        }
        int count = Math.min(16, rings.size());
        if (count == 0) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1773 == null || client.field_1773.method_19418() == null) {
            return;
        }
        JumpCircleRefraction.init();
        if (pipeline == null || uniform == null) {
            return;
        }
        if (!JumpCircleRefraction.ensureScene(target.field_1482, target.field_1481)) {
            return;
        }
        class_4184 camera = client.field_1773.method_19418();
        class_243 cameraPos = camera.method_71156();
        try {
            CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
            encoder.copyTextureToTexture(target.method_30277(), sceneTexture, 0, 0, 0, 0, 0, target.field_1482, target.field_1481);
            try (MemoryStack stack = MemoryStack.stackPush();){
                ByteBuffer data = stack.calloc(2416);
                new Matrix4f((Matrix4fc)projection).mul((Matrix4fc)position).invert().get(0, data);
                data.putFloat(64, params.radius());
                data.putFloat(68, params.glow());
                data.putFloat(76, params.distortion());
                int color = params.color();
                data.putFloat(80, (float)(color >> 16 & 0xFF) / 255.0f);
                data.putFloat(84, (float)(color >> 8 & 0xFF) / 255.0f);
                data.putFloat(88, (float)(color & 0xFF) / 255.0f);
                data.putFloat(92, (float)(color >>> 24 & 0xFF) / 255.0f);
                data.putFloat(96, params.wave() ? 1.0f : 0.0f);
                data.putFloat(100, params.water() ? 1.0f : 0.0f);
                float life = Math.max(1.0f, params.lifeMs());
                int written = 0;
                for (Ring ring : rings) {
                    if (written >= count) break;
                    long age = Math.max(0L, now - ring.bornMs());
                    float progress = class_3532.method_15363((float)((float)age / life), (float)0.0f, (float)1.0f);
                    int base = 112 + written * 16;
                    data.putFloat(base, (float)(ring.x() - cameraPos.field_1352));
                    data.putFloat(base + 4, (float)(ring.y() - cameraPos.field_1351));
                    data.putFloat(base + 8, (float)(ring.z() - cameraPos.field_1350));
                    data.putFloat(base + 12, progress);
                    int wallBase = 368 + written * 128;
                    float[] walls = ring.walls();
                    for (int i = 0; i < 32; ++i) {
                        data.putFloat(wallBase + i * 4, walls != null && i < walls.length ? walls[i] : 999.0f);
                    }
                    ++written;
                }
                if (written == 0) {
                    return;
                }
                data.putFloat(72, written);
                data.position(0);
                encoder.writeToBuffer(uniform.slice(0L, 2416L), data);
            }
            class_12137 nearest = RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST);
            class_12137 linear = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
            try (RenderPass pass = encoder.createRenderPass(() -> "decide:jump_circle", target.method_71639(), OptionalInt.empty());){
                pass.setPipeline(pipeline);
                pass.bindTexture("DepthSampler", target.method_71640(), nearest);
                pass.bindTexture("ColorSampler", sceneView, linear);
                pass.setUniform("JumpCircleParams", uniform);
                pass.draw(0, 6);
            }
        }
        catch (Throwable throwable) {
            disabled = true;
            JumpCircleRefraction.closeScene();
            JumpCircleRefraction.closeUniform();
        }
    }

    public static void clear() {
        JumpCircleRefraction.closeScene();
        JumpCircleRefraction.closeUniform();
        disabled = false;
    }

    private static void init() {
        if (disabled || pipeline != null) {
            return;
        }
        try {
            pipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(PIPELINE_ID).withVertexShader(SHADER_ID).withFragmentShader(SHADER_ID).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withSampler("DepthSampler").withSampler("ColorSampler").withUniform("JumpCircleParams", class_10789.field_60031).withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
            uniform = RenderSystem.getDevice().createBuffer(() -> "decide:jump_circle_uniforms", 136, 2416L);
        }
        catch (Throwable throwable) {
            disabled = true;
            pipeline = null;
            JumpCircleRefraction.closeUniform();
        }
    }

    private static boolean ensureScene(int width, int height) {
        if (sceneTexture != null && sceneView != null && sceneWidth == width && sceneHeight == height) {
            return true;
        }
        JumpCircleRefraction.closeScene();
        try {
            sceneTexture = RenderSystem.getDevice().createTexture(() -> "decide:jump_circle_scene", 5, TextureFormat.RGBA8, width, height, 1, 1);
            sceneView = RenderSystem.getDevice().createTextureView(sceneTexture);
            sceneWidth = width;
            sceneHeight = height;
            return true;
        }
        catch (Throwable throwable) {
            disabled = true;
            JumpCircleRefraction.closeScene();
            return false;
        }
    }

    private static void closeScene() {
        if (sceneView != null) {
            sceneView.close();
            sceneView = null;
        }
        if (sceneTexture != null) {
            sceneTexture.close();
            sceneTexture = null;
        }
        sceneWidth = -1;
        sceneHeight = -1;
    }

    private static void closeUniform() {
        if (uniform != null) {
            uniform.close();
            uniform = null;
        }
    }

    static {
        sceneWidth = -1;
        sceneHeight = -1;
    }

    @Environment(value=EnvType.CLIENT)
    public record Params(float radius, float glow, float distortion, boolean wave, boolean water, int color, float lifeMs) {
    }

    @Environment(value=EnvType.CLIENT)
    public record Ring(double x, double y, double z, long bornMs, float[] walls) {
    }
}

