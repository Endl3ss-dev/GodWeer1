package tech.lightclient.client.utils.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import tech.lightclient.mixins.accessors.GameRendererAccessor;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public final class WetSurfaceRenderer implements AutoCloseable {
    private static final WetSurfaceRenderer INSTANCE = new WetSurfaceRenderer();
    private static final float EPSILON = 1.0E-4f;
    // vec2 + 7 float + 2 int = 44 байта, std140 округляет размер блока до 48
    private static final int PARAMS_SIZE = 48;

    private SimpleFramebuffer sceneFbo;
    private GpuSampler sampler;
    private GpuSampler depthSampler;
    private RenderPipeline copyPipeline;
    private RenderPipeline wetPipeline;
    private GpuBuffer matricesBuffer;
    private GpuBuffer paramsBuffer;
    private boolean initialized;
    private boolean disabled;
    private int width = -1;
    private int height = -1;

    private WetSurfaceRenderer() {}

    public static WetSurfaceRenderer getInstance() { return INSTANCE; }

    public static final class Parameters {
        public float wetness = 1.0f;
        public float puddleCoverage = 0.82f;
        public float reflectionStrength = 1.25f;
        public float maxDistance = 56.0f;
        public float rippleStrength = 0.8f;
        public float rainAmount = 1.0f;
        public int reflectionSteps = 10;
        public boolean ripples = true;
    }

    public void apply(MinecraftClient mc, Camera camera, Matrix4f viewMatrix, Matrix4f projectionMatrix, Parameters parameters) {
        if (disabled || mc == null || camera == null || viewMatrix == null || projectionMatrix == null || parameters == null) return;
        if (mc.world == null || mc.player == null || !isWindowValid(mc) || parameters.wetness <= EPSILON || parameters.reflectionStrength <= EPSILON) return;
        Window window = mc.getWindow();
        int w = window.getFramebufferWidth();
        int h = window.getFramebufferHeight();
        if (w <= 1 || h <= 1) return;
        var fb = mc.getFramebuffer();
        if (fb == null) return;
        try {
            ensureInitialized();
            if (disabled) return;
            ensureScene(w, h);
            if (sceneFbo == null) return;
            // copy world color to sceneFbo via pipeline (ensures correct ordering)
            copyScene(fb, sceneFbo);
            // prepare matrices
            Matrix4f inverseProjection = new Matrix4f(projectionMatrix).invert();
            Matrix4f inverseView = new Matrix4f(viewMatrix).invert();
            Vec3d camPos = camera.getCameraPos();
            inverseView.m30((float) camPos.x);
            inverseView.m31((float) camPos.y);
            inverseView.m32((float) camPos.z);
            // apply wet effect
            renderWet(fb, sceneFbo, camera, viewMatrix, projectionMatrix, inverseProjection, inverseView, parameters, w, h);
        } catch (Throwable t) {
            disabled = true;
            System.err.println("[WetSurface] disabled: " + t.getMessage());
            t.printStackTrace();
        }
    }

    public void applyWithFallback(MinecraftClient mc, Camera camera, Parameters parameters) {
        if (mc == null || camera == null || parameters == null) return;
        try {
            float tickDelta = mc.getRenderTickCounter().getTickProgress(false);
            float fov = 70.0f;
            try { fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true); } catch (Throwable ignored) {}
            Matrix4f projection = new Matrix4f(mc.gameRenderer.getBasicProjectionMatrix(fov));
            Matrix4f view = new Matrix4f().rotate(camera.getRotation().conjugate(new org.joml.Quaternionf()))
                    .translate((float) -camera.getCameraPos().x, (float) -camera.getCameraPos().y, (float) -camera.getCameraPos().z);
            apply(mc, camera, view, projection, parameters);
        } catch (Throwable t) {
            System.err.println("[WetSurface] fallback failed: " + t.getMessage());
        }
    }

    private void ensureInitialized() {
        if (initialized) return;
        try {
            GpuDevice device = RenderSystem.getDevice();
            sampler = device.createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
            // Буфер глубины нельзя фильтровать линейно: интерполяция между соседними
            // пикселями даёт несуществующие глубины и ломает реконструкцию позиции.
            depthSampler = device.createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, 1, OptionalDouble.empty());
            copyPipeline = RenderPipeline.builder()
                    .withLocation(Identifier.of("light", "wet_copy"))
                    .withVertexShader("core/screenquad")
                    .withFragmentShader("core/blit_screen")
                    .withSampler("InSampler")
                    .withoutBlend()
                    .withDepthWrite(false)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withVertexFormat(VertexFormats.EMPTY, com.mojang.blaze3d.vertex.VertexFormat.DrawMode.TRIANGLES)
                    .build();
            wetPipeline = RenderPipeline.builder()
                    .withLocation(Identifier.of("light", "wet_surface"))
                    .withVertexShader("core/screenquad")
                    .withFragmentShader(Identifier.of("light", "core/wet_simple"))
                    .withSampler("u_SceneTexture")
                    .withSampler("u_DepthTexture")
                    .withUniform("WetMatrices", UniformType.UNIFORM_BUFFER)
                    .withUniform("WetParams", UniformType.UNIFORM_BUFFER)
                    .withoutBlend()
                    .withDepthWrite(false)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withVertexFormat(VertexFormats.EMPTY, com.mojang.blaze3d.vertex.VertexFormat.DrawMode.TRIANGLES)
                    .build();
            int matricesSize = new Std140SizeCalculator().putMat4f().putMat4f().putMat4f().putMat4f().get();
            int paramsSize = PARAMS_SIZE;
            matricesBuffer = device.createBuffer(() -> "wet_matrices", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, matricesSize);
            paramsBuffer = device.createBuffer(() -> "wet_params", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, paramsSize);
            initialized = true;
            System.out.println("[WetSurface] pipeline initialized");
        } catch (Throwable t) {
            disabled = true;
            System.err.println("[WetSurface] init failed: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private void ensureScene(int w, int h) {
        if (sceneFbo != null && width == w && height == h) return;
        if (sceneFbo != null) {
            try { sceneFbo.delete(); } catch (Throwable ignored) {}
            sceneFbo = null;
        }
        sceneFbo = new SimpleFramebuffer("wet_scene", w, h, false);
        width = w; height = h;
    }

    private void copyScene(Framebuffer src, SimpleFramebuffer dst) {
        try {
            GpuDevice device = RenderSystem.getDevice();
            CommandEncoder encoder = device.createCommandEncoder();
            try (RenderPass pass = encoder.createRenderPass(() -> "wet_copy", dst.getColorAttachmentView(), OptionalInt.empty())) {
                pass.setPipeline(copyPipeline);
                pass.bindTexture("InSampler", src.getColorAttachmentView(), sampler);
                RenderSystem.bindDefaultUniforms(pass);
                pass.draw(0, 3);
            }
        } catch (Throwable t) {
            System.err.println("[WetSurface] copy failed: " + t.getMessage());
        }
    }

    private void renderWet(Framebuffer target, SimpleFramebuffer scene, Camera camera,
                           Matrix4f viewMatrix, Matrix4f projectionMatrix,
                           Matrix4f inverseProjection, Matrix4f inverseView,
                           Parameters p, int w, int h) {
        try {
            GpuDevice device = RenderSystem.getDevice();
            CommandEncoder encoder = device.createCommandEncoder();
            // upload matrices
            try (MemoryStack stack = MemoryStack.stackPush()) {
                ByteBuffer buf = Std140Builder.onStack(stack, 256)
                        .putMat4f(viewMatrix)
                        .putMat4f(projectionMatrix)
                        .putMat4f(inverseProjection)
                        .putMat4f(inverseView)
                        .get();
                encoder.writeToBuffer(matricesBuffer.slice(), buf);
            }
            try (MemoryStack stack = MemoryStack.stackPush()) {
                Std140Builder builder = Std140Builder.onStack(stack, PARAMS_SIZE);
                builder.putVec2(w, h);
                builder.putFloat((System.currentTimeMillis() % 1_000_000L) / 1000.0f);
                builder.putFloat(clamp(p.wetness, 0.0f, 2.0f));
                builder.putFloat(clamp(p.puddleCoverage, 0.0f, 1.0f));
                builder.putFloat(clamp(p.reflectionStrength, 0.0f, 2.5f));
                builder.putFloat(clamp(p.maxDistance, 4.0f, 128.0f));
                builder.putFloat(clamp(p.rippleStrength, 0.0f, 2.0f));
                builder.putFloat(clamp(p.rainAmount, 0.0f, 2.0f));
                builder.putInt(Math.max(4, Math.min(16, p.reflectionSteps)));
                builder.putInt(p.ripples ? 1 : 0);
                // Добор до кратного 16 (44 -> 48), иначе размер записи не совпадёт с буфером.
                builder.putInt(0);
                ByteBuffer buf = builder.get();
                encoder.writeToBuffer(paramsBuffer.slice(), buf);
            }
            var depthView = target.getDepthAttachmentView();
            if (depthView == null) {
                // no depth, skip effect to avoid black screen
                return;
            }
            try (RenderPass pass = encoder.createRenderPass(() -> "wet_surface", target.getColorAttachmentView(), OptionalInt.empty())) {
                pass.setPipeline(wetPipeline);
                pass.bindTexture("u_SceneTexture", scene.getColorAttachmentView(), sampler);
                pass.bindTexture("u_DepthTexture", depthView, depthSampler);
                pass.setUniform("WetMatrices", matricesBuffer.slice());
                pass.setUniform("WetParams", paramsBuffer.slice());
                RenderSystem.bindDefaultUniforms(pass);
                pass.draw(0, 3);
            }
        } catch (Throwable t) {
            System.err.println("[WetSurface] render failed: " + t.getMessage());
            t.printStackTrace();
            // fallback: do not blacken screen, just skip
        }
    }

    public void reset() { disabled = false; }

    private static boolean isWindowValid(MinecraftClient mc) {
        Window w = mc == null ? null : mc.getWindow();
        return w != null && w.getFramebufferWidth() > 0 && w.getFramebufferHeight() > 0;
    }
    private static float clamp(float v, float min, float max) { return !Float.isFinite(v) ? min : Math.max(min, Math.min(max, v)); }

    @Override
    public void close() {
        try { if (sceneFbo != null) sceneFbo.delete(); } catch (Throwable ignored) {}
        sceneFbo = null;
        try { if (sampler != null) sampler.close(); } catch (Throwable ignored) {}
        sampler = null;
        try { if (depthSampler != null) depthSampler.close(); } catch (Throwable ignored) {}
        depthSampler = null;
        try { if (matricesBuffer != null) matricesBuffer.close(); } catch (Throwable ignored) {}
        matricesBuffer = null;
        try { if (paramsBuffer != null) paramsBuffer.close(); } catch (Throwable ignored) {}
        paramsBuffer = null;
        copyPipeline = null; wetPipeline = null;
        initialized = false; disabled = false; width = -1; height = -1;
    }
}
