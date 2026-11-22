package tech.lightclient.client.utils.render.main.postfx;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * World-only camera motion blur.
 *
 * <p>It runs before Minecraft draws the HUD: the rendered world is copied to
 * a private framebuffer, composited with the previous world frame, and only
 * then is the HUD/NameTags pass allowed to start. This uses Minecraft's GPU
 * device throughout; raw OpenGL framebuffer attachment is intentionally not
 * used here.</p>
 */
public final class MotionBlurRenderer {
    private static final MotionBlurRenderer INSTANCE = new MotionBlurRenderer();

    private boolean initialized;
    private SimpleFramebuffer sceneFrame;
    private SimpleFramebuffer historyFrame;
    private GpuSampler sampler;
    private RenderPipeline copyPipeline;
    private RenderPipeline motionPipeline;
    private GpuBuffer resolutionBuffer;
    private GpuBuffer motionBuffer;
    private int width = -1;
    private int height = -1;
    private float previousYaw;
    private float previousPitch;
    private boolean hasPrevious;

    private MotionBlurRenderer() {
    }

    public static MotionBlurRenderer getInstance() {
        return INSTANCE;
    }

    /** Applies blur to the already rendered world, before the HUD is drawn. */
    public void apply(MinecraftClient client, Camera camera, Settings settings) {
        if (client == null || camera == null || settings == null || client.world == null || client.player == null) {
            reset();
            return;
        }

        int frameWidth = client.getWindow().getFramebufferWidth();
        int frameHeight = client.getWindow().getFramebufferHeight();
        if (frameWidth <= 1 || frameHeight <= 1 || settings.strength <= 0.00001f) {
            reset();
            return;
        }

        if (!ensureResources(frameWidth, frameHeight)) {
            return;
        }

        float yaw = camera.getYaw();
        float pitch = camera.getPitch();
        float yawDelta = hasPrevious ? wrapDegrees(yaw - previousYaw) : 0.0f;
        float pitchDelta = hasPrevious ? pitch - previousPitch : 0.0f;

        try {
            GpuDevice device = RenderSystem.getDevice();
            CommandEncoder encoder = device.createCommandEncoder();

            // Preserve the clean current world before writing the composite
            // back into Minecraft's framebuffer. This removes all read/write
            // feedback from the main render target.
            copy(encoder, "motionblur_capture", client.getFramebuffer().getColorAttachmentView(),
                    sceneFrame.getColorAttachmentView());

            if (hasPrevious) {
                writeMotionUniform(encoder, frameWidth, frameHeight, yawDelta, pitchDelta, settings);
                try (RenderPass pass = encoder.createRenderPass(
                        () -> "motionblur_world_composite",
                        client.getFramebuffer().getColorAttachmentView(),
                        OptionalInt.empty())) {
                    pass.setPipeline(motionPipeline);
                    pass.bindTexture("u_Current", sceneFrame.getColorAttachmentView(), sampler);
                    pass.bindTexture("u_History", historyFrame.getColorAttachmentView(), sampler);
                    pass.setUniform("u_Resolution", resolutionBuffer.slice());
                    pass.setUniform("u_Motion", motionBuffer.slice());
                    RenderSystem.bindDefaultUniforms(pass);
                    pass.draw(0, 3);
                }
            }

            // History must receive the unblurred current world; otherwise
            // temporal feedback would make the whole screen progressively
            // darker and smear even while the camera is stationary.
            copy(encoder, "motionblur_history", sceneFrame.getColorAttachmentView(),
                    historyFrame.getColorAttachmentView());
            previousYaw = yaw;
            previousPitch = pitch;
            hasPrevious = true;
        } catch (Throwable ignored) {
            reset();
        }
    }

    public void reset() {
        hasPrevious = false;
    }

    public void resize(int requestedWidth, int requestedHeight) {
        if (requestedWidth != width || requestedHeight != height) {
            releaseFrames();
            width = -1;
            height = -1;
            reset();
        }
    }

    public void close() {
        reset();
        releaseFrames();
        closeQuietly(sampler);
        closeQuietly(resolutionBuffer);
        closeQuietly(motionBuffer);
        sampler = null;
        resolutionBuffer = null;
        motionBuffer = null;
        copyPipeline = null;
        motionPipeline = null;
        initialized = false;
        width = -1;
        height = -1;
    }

    private boolean ensureResources(int requestedWidth, int requestedHeight) {
        try {
            if (!initialized) {
                init();
            }
            if (!initialized) {
                return false;
            }
            if (requestedWidth != width || requestedHeight != height || sceneFrame == null || historyFrame == null) {
                releaseFrames();
                sceneFrame = new SimpleFramebuffer("motionblur_scene", requestedWidth, requestedHeight, false);
                historyFrame = new SimpleFramebuffer("motionblur_history", requestedWidth, requestedHeight, false);
                width = requestedWidth;
                height = requestedHeight;
                reset();
            }
            return true;
        } catch (Throwable ignored) {
            close();
            return false;
        }
    }

    private void init() {
        GpuDevice device = RenderSystem.getDevice();
        sampler = device.createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE,
                FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());

        copyPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "motionblur_copy"))
                .withVertexShader("core/screenquad")
                .withFragmentShader("core/blit_screen")
                .withSampler("InSampler")
                .withoutBlend()
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, com.mojang.blaze3d.vertex.VertexFormat.DrawMode.TRIANGLES)
                .build();

        motionPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "motionblur_world"))
                .withVertexShader("core/screenquad")
                .withFragmentShader(Identifier.of("light", "core/motionblur_world"))
                .withSampler("u_Current")
                .withSampler("u_History")
                .withUniform("u_Resolution", UniformType.UNIFORM_BUFFER)
                .withUniform("u_Motion", UniformType.UNIFORM_BUFFER)
                .withoutBlend()
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, com.mojang.blaze3d.vertex.VertexFormat.DrawMode.TRIANGLES)
                .build();

        resolutionBuffer = device.createBuffer(() -> "motionblur_resolution",
                GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        motionBuffer = device.createBuffer(() -> "motionblur_motion",
                GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        initialized = true;
    }

    private void copy(CommandEncoder encoder, String name,
                      com.mojang.blaze3d.textures.GpuTextureView source,
                      com.mojang.blaze3d.textures.GpuTextureView destination) {
        try (RenderPass pass = encoder.createRenderPass(() -> name, destination, OptionalInt.empty())) {
            pass.setPipeline(copyPipeline);
            pass.bindTexture("InSampler", source, sampler);
            RenderSystem.bindDefaultUniforms(pass);
            pass.draw(0, 3);
        }
    }

    private void writeMotionUniform(CommandEncoder encoder, int frameWidth, int frameHeight,
                                    float yawDelta, float pitchDelta, Settings settings) {
        float maxRadius = clamp(settings.maxRadius, 1.0f, 128.0f);
        float scale = clamp(settings.temporalScale, 0.05f, 4.0f);
        float motionX = clamp(yawDelta * 2.20f * scale, -maxRadius, maxRadius);
        float motionY = clamp(-pitchDelta * 2.45f * scale, -maxRadius, maxRadius);
        float distance = (float) Math.hypot(motionX, motionY);
        float activation = Math.max(0.05f, settings.activation * 0.45f);
        float blend = distance < activation ? 0.0f
                : clamp(0.16f + clamp(settings.strength, 0.0f, 1.0f) * 0.34f, 0.0f, 0.52f);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer resolution = Std140Builder.onStack(stack, 16)
                    .putVec2(frameWidth, frameHeight)
                    .get();
            encoder.writeToBuffer(resolutionBuffer.slice(), resolution);

            ByteBuffer motion = Std140Builder.onStack(stack, 16)
                    .putVec2(motionX / frameWidth, motionY / frameHeight)
                    .putFloat(blend)
                    .putFloat(0.0f)
                    .get();
            encoder.writeToBuffer(motionBuffer.slice(), motion);
        }
    }

    private void releaseFrames() {
        deleteFrame(sceneFrame);
        deleteFrame(historyFrame);
        sceneFrame = null;
        historyFrame = null;
    }

    private static void deleteFrame(SimpleFramebuffer frame) {
        if (frame == null) return;
        try {
            frame.delete();
        } catch (Throwable ignored) {
        }
    }

    private static void closeQuietly(AutoCloseable resource) {
        if (resource == null) return;
        try {
            resource.close();
        } catch (Exception ignored) {
        }
    }

    private static float wrapDegrees(float value) {
        value %= 360.0f;
        if (value >= 180.0f) value -= 360.0f;
        if (value < -180.0f) value += 360.0f;
        return value;
    }

    private static float clamp(float value, float min, float max) {
        return Float.isFinite(value) ? Math.max(min, Math.min(max, value)) : min;
    }

    public static final class Settings {
        public float strength = 0.72f;
        public float temporalScale = 1.05f;
        public int samples = 7;
        public float maxRadius = 34.0f;
        public float edgeFocus = 0.54f;
        public float chromaticPhase = 0.58f;
        public float depthGuard = 0.72f;
        public float decay = 2.25f;
        public float activation = 0.42f;
    }
}
