package tech.lightclient.client.utils.render3d;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import tech.lightclient.LightClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.system.MemoryStack;
import tech.lightclient.modules.impl.render.HandBloom;
import tech.lightclient.modules.impl.render.Hands;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

public final class HandsRenderer {
    private static final Identifier QUAD_SHADER = Identifier.of("light", "core/handsfx_quad");
    private static final int MAX_MIPS = 6;
    private static final int MIRROR_PASSES = 4;
    private static final int DATA_UBO_SIZE = new Std140SizeCalculator()
            .putVec4().putVec4().putVec4().putVec4().putVec4()
            .putVec4().putVec4().putVec4().putVec4().putVec4().get();
    private static final int KAWASE_UBO_SIZE = new Std140SizeCalculator().putVec4().get();

    private static RenderPipeline glowPipeline;
    private static RenderPipeline fillPipeline;
    private static RenderPipeline mirrorPipeline;
    private static RenderPipeline outlinePipeline;
    private static RenderPipeline trailFadePipeline;
    private static RenderPipeline trailColorPipeline;
    private static RenderPipeline blitPipeline;
    private static RenderPipeline corePipeline;
    private static RenderPipeline kawaseDownPipeline;
    private static RenderPipeline kawaseUpPipeline;

    private static GpuSampler sampler;
    private static GpuBuffer dataUbo;
    private static GpuBuffer kawaseUbo;

    private static SimpleFramebuffer handFbo;
    private static SimpleFramebuffer sceneFbo;
    private static SimpleFramebuffer trailA;
    private static SimpleFramebuffer trailB;
    private static final List<SimpleFramebuffer> mips = new ArrayList<>();

    private static int lastWidth = -1;
    private static int lastHeight = -1;
    private static boolean needsPost;
    private static boolean disabledAfterError;

    private static long lastFrameMs;
    private static float smoothDt = 1.0f / 60.0f;
    private static float smoothRise;
    private static float smoothSway;
    private static float smoothBurst;
    private static float attackImpulse;
    private static float swapImpulse;
    private static long lastSwingMs = -10_000L;
    private static boolean wasSwinging;
    private static int lastItemHash;
    private static boolean cameraInit;
    private static float prevYaw;
    private static float prevPitch;
    private static float cameraOffsetX;
    private static float cameraOffsetY;

    private HandsRenderer() {
    }

    public static void preHandCapture() {
        if (disabledAfterError || mc.getFramebuffer() == null) {
            return;
        }
        try {
            ensureReady();
            clearFramebuffer(handFbo, true);
            RenderSystem.outputColorTextureOverride = handFbo.getColorAttachmentView();
            RenderSystem.outputDepthTextureOverride = handFbo.getDepthAttachmentView();
            needsPost = false;
        } catch (Throwable throwable) {
            fail(throwable);
        }
    }

    public static void postHandCapture() {
        if (disabledAfterError || handFbo == null || sceneFbo == null || mc.getFramebuffer() == null) {
            resetOverrides();
            return;
        }
        try {
            resetOverrides();
            copyFramebuffer(mc.getFramebuffer(), sceneFbo);
            needsPost = true;
        } catch (Throwable throwable) {
            fail(throwable);
        }
    }

    public static void postHandsRender(Hands hands, HandBloom bloom) {
        if (disabledAfterError || !needsPost || handFbo == null || mc.getFramebuffer() == null) {
            needsPost = false;
            return;
        }
        try {
            ensureReady();
            EffectConfig config = hands != null && hands.isState() ? EffectConfig.fromHands(hands) : EffectConfig.handBloom();
            if (config == null) {
                copyFramebuffer(handFbo, mc.getFramebuffer());
                needsPost = false;
                return;
            }

            updateDynamics(config);
            writeData(config, lastWidth, lastHeight, config.glowExposure);

            if (config.fillMode == 2) {
                runMirror(config);
            } else if (config.fillMode == 1) {
                runFill();
            } else {
                blit(handFbo, mc.getFramebuffer());
            }

            boolean glowActive = config.glowEnabled && config.outerGlow;
            boolean needBloom = glowActive || (config.outlineEnabled && config.outlineColorMode == 2);
            Framebuffer bloomSource = handFbo;
            SimpleFramebuffer bloomFbo = null;
            if (needBloom) {
                bloomFbo = runKawase(bloomSource, glowActive ? config.glowRadius : 3);
            }

            if (glowActive && config.trailEnabled && bloomFbo != null) {
                renderTrail(config, bloomFbo);
                if (attackImpulse > 0.005f) {
                    writeData(config, lastWidth, lastHeight, config.glowExposure * attackImpulse * 2.0f);
                    drawGlow(bloomFbo);
                }
            } else if (glowActive && bloomFbo != null) {
                writeData(config, lastWidth, lastHeight, config.glowExposure * (1.0f + attackImpulse * 0.8f));
                drawGlow(bloomFbo);
            }
            attackImpulse *= 0.82f;
            swapImpulse *= 0.88f;

            if (config.outlineEnabled) {
                drawOutline(bloomFbo != null ? bloomFbo : handFbo);
            }

            if (config.corePower > 0.005f) {
                writeData(config, lastWidth, lastHeight, config.glowExposure);
                drawCore();
            }
        } catch (Throwable throwable) {
            fail(throwable);
        } finally {
            needsPost = false;
            resetOverrides();
        }
    }

    public static boolean needsPost() {
        return needsPost && !disabledAfterError;
    }

    public static void clearTrail() {
        attackImpulse = 0.0f;
        swapImpulse = 0.0f;
        smoothRise = 0.0f;
        smoothSway = 0.0f;
        smoothBurst = 0.0f;
        cameraOffsetX = 0.0f;
        cameraOffsetY = 0.0f;
        cameraInit = false;
        if (trailA == null || trailB == null) {
            return;
        }
        try {
            clearFramebuffer(trailA, false);
            clearFramebuffer(trailB, false);
        } catch (Throwable throwable) {
            fail(throwable);
        }
    }

    public static void resetOverrides() {
        RenderSystem.outputColorTextureOverride = null;
        RenderSystem.outputDepthTextureOverride = null;
    }

    private static void runFill() {
        try (RenderPass pass = beginPass("hands_fill", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(fillPipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("HandSampler", handFbo.getColorAttachmentView(), sampler);
            pass.bindTexture("MaskSampler", handFbo.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    private static void runMirror(EffectConfig config) {
        SimpleFramebuffer blurred = runKawase(sceneFbo, MIRROR_PASSES);
        writeData(config, lastWidth, lastHeight, config.glowExposure);
        try (RenderPass pass = beginPass("hands_mirror", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(mirrorPipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("BlurredSampler", blurred.getColorAttachmentView(), sampler);
            pass.bindTexture("HandSampler", handFbo.getColorAttachmentView(), sampler);
            pass.bindTexture("MaskSampler", handFbo.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    private static void drawGlow(Framebuffer bloom) {
        try (RenderPass pass = beginPass("hands_glow", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(glowPipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("BloomSampler", bloom.getColorAttachmentView(), sampler);
            pass.bindTexture("MaskSampler", handFbo.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    private static void drawOutline(Framebuffer bloom) {
        try (RenderPass pass = beginPass("hands_outline", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(outlinePipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("MaskSampler", handFbo.getColorAttachmentView(), sampler);
            pass.bindTexture("BloomSampler", bloom.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    private static void drawCore() {
        try (RenderPass pass = beginPass("hands_core", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(corePipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("MaskSampler", handFbo.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    private static void renderTrail(EffectConfig config, Framebuffer bloom) {
        writeData(config, trailA.textureWidth, trailA.textureHeight, config.glowExposure);
        try (RenderPass pass = beginPass("hands_trail_fade", trailB)) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(trailFadePipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("PrevSampler", trailA.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
        try (RenderPass pass = beginPass("hands_trail_color", trailB)) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(trailColorPipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("BloomSampler", bloom.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
        SimpleFramebuffer swap = trailA;
        trailA = trailB;
        trailB = swap;

        writeData(config, lastWidth, lastHeight, config.glowExposure);
        try (RenderPass pass = beginPass("hands_trail_blit", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(blitPipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("InputSampler", trailA.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    private static SimpleFramebuffer runKawase(Framebuffer source, int passes) {
        passes = Math.max(1, Math.min(MAX_MIPS, passes));
        Framebuffer current = source;
        int curWidth = source.textureWidth;
        int curHeight = source.textureHeight;

        for (int i = 0; i < passes; i++) {
            SimpleFramebuffer dst = mips.get(i);
            writeKawase(0.5f / Math.max(1, curWidth), 0.5f / Math.max(1, curHeight), 1.0f + i);
            try (RenderPass pass = beginPass("hands_kawase_down_" + i, dst)) {
                RenderSystem.bindDefaultUniforms(pass);
                pass.setPipeline(kawaseDownPipeline);
                pass.setUniform("HandsFxKawase", kawaseUbo.slice());
                pass.bindTexture("InputSampler", current.getColorAttachmentView(), sampler);
                pass.draw(0, 3);
            }
            current = dst;
            curWidth = dst.textureWidth;
            curHeight = dst.textureHeight;
        }

        for (int i = passes - 1; i >= 1; i--) {
            SimpleFramebuffer src = mips.get(i);
            SimpleFramebuffer dst = mips.get(i - 1);
            writeKawase(0.5f / Math.max(1, dst.textureWidth), 0.5f / Math.max(1, dst.textureHeight), 1.0f + i);
            try (RenderPass pass = beginPass("hands_kawase_up_" + i, dst)) {
                RenderSystem.bindDefaultUniforms(pass);
                pass.setPipeline(kawaseUpPipeline);
                pass.setUniform("HandsFxKawase", kawaseUbo.slice());
                pass.bindTexture("InputSampler", src.getColorAttachmentView(), sampler);
                pass.draw(0, 3);
            }
        }
        return mips.get(0);
    }

    private static void copyFramebuffer(Framebuffer from, Framebuffer to) {
        blit(from, to);
    }

    private static void blit(Framebuffer from, Framebuffer to) {
        try (RenderPass pass = beginPass("hands_blit", to)) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(blitPipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("InputSampler", from.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    private static RenderPass beginPass(String name, Framebuffer framebuffer) {
        return RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                () -> "Light:" + name,
                framebuffer.getColorAttachmentView(),
                OptionalInt.empty(),
                framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null,
                OptionalDouble.empty()
        );
    }

    private static void ensureReady() {
        if (glowPipeline == null) {
            initPipelines();
        }
        int width = Math.max(1, mc.getFramebuffer().textureWidth);
        int height = Math.max(1, mc.getFramebuffer().textureHeight);
        if (width != lastWidth || height != lastHeight || handFbo == null || sceneFbo == null) {
            resize(width, height);
        }
    }

    private static void initPipelines() {
        GpuDevice device = RenderSystem.getDevice();
        sampler = device.createSampler(
                AddressMode.CLAMP_TO_EDGE,
                AddressMode.CLAMP_TO_EDGE,
                FilterMode.LINEAR,
                FilterMode.LINEAR,
                1,
                OptionalDouble.empty()
        );

        glowPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/handsfx_glow"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/handsfx_glow"))
                .withSampler("BloomSampler")
                .withSampler("MaskSampler")
                .withUniform("HandsFxData", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.LIGHTNING)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        fillPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/handsfx_fill"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/handsfx_fill"))
                .withSampler("HandSampler")
                .withSampler("MaskSampler")
                .withUniform("HandsFxData", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        mirrorPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/handsfx_mirror"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/handsfx_mirror"))
                .withSampler("BlurredSampler")
                .withSampler("HandSampler")
                .withSampler("MaskSampler")
                .withUniform("HandsFxData", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        outlinePipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/handsfx_outline"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/handsfx_outline"))
                .withSampler("MaskSampler")
                .withSampler("BloomSampler")
                .withUniform("HandsFxData", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        trailFadePipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/handsfx_trail_fade"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/handsfx_trail_fade"))
                .withSampler("PrevSampler")
                .withUniform("HandsFxData", UniformType.UNIFORM_BUFFER)
                .withoutBlend()
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        trailColorPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/handsfx_trail_color"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/handsfx_trail_color"))
                .withSampler("BloomSampler")
                .withUniform("HandsFxData", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        blitPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/handsfx_blit"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/handsfx_blit"))
                .withSampler("InputSampler")
                .withUniform("HandsFxData", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        corePipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/handsfx_core"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/handsfx_core"))
                .withSampler("MaskSampler")
                .withUniform("HandsFxData", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.LIGHTNING)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        kawaseDownPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/handsfx_kawase_down"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/handsfx_kawase_down"))
                .withSampler("InputSampler")
                .withUniform("HandsFxKawase", UniformType.UNIFORM_BUFFER)
                .withoutBlend()
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        kawaseUpPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/handsfx_kawase_up"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/handsfx_kawase_up"))
                .withSampler("InputSampler")
                .withUniform("HandsFxKawase", UniformType.UNIFORM_BUFFER)
                .withoutBlend()
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();

        dataUbo = device.createBuffer(() -> "light_handsfx_data", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, DATA_UBO_SIZE);
        kawaseUbo = device.createBuffer(() -> "light_handsfx_kawase", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, KAWASE_UBO_SIZE);
    }

    private static void resize(int width, int height) {
        closeFramebuffers();
        handFbo = new SimpleFramebuffer("hands_capture", width, height, true);
        sceneFbo = new SimpleFramebuffer("hands_scene", width, height, false);
        trailA = new SimpleFramebuffer("hands_trail_a", Math.max(2, width / 2), Math.max(2, height / 2), false);
        trailB = new SimpleFramebuffer("hands_trail_b", Math.max(2, width / 2), Math.max(2, height / 2), false);

        int mipWidth = width;
        int mipHeight = height;
        for (int i = 0; i < MAX_MIPS; i++) {
            mipWidth = Math.max(2, mipWidth / 2);
            mipHeight = Math.max(2, mipHeight / 2);
            mips.add(new SimpleFramebuffer("hands_mip_" + i, mipWidth, mipHeight, false));
        }

        lastWidth = width;
        lastHeight = height;
        clearFramebuffer(handFbo, true);
        clearFramebuffer(sceneFbo, false);
        clearFramebuffer(trailA, false);
        clearFramebuffer(trailB, false);
    }

    private static void clearFramebuffer(Framebuffer framebuffer, boolean clearDepth) {
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        if (clearDepth && framebuffer.useDepthAttachment) {
            encoder.clearColorAndDepthTextures(
                    framebuffer.getColorAttachment(), 0,
                    framebuffer.getDepthAttachment(), 1.0
            );
        } else {
            encoder.clearColorTexture(framebuffer.getColorAttachment(), 0);
        }
    }

    private static void writeData(EffectConfig config, int width, int height, float exposure) {
        long now = System.currentTimeMillis();
        float time = (now % 100_000L) / 1000.0f;
        float fadeAdd = smoothBurst * config.trailBurstPower * 0.012f;
        float offsetX = smoothSway + cameraOffsetX * 0.00015f;
        float offsetY = smoothRise + cameraOffsetY * 0.00015f;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            var builder = Std140Builder.onStack(stack, DATA_UBO_SIZE)
                    .putVec4(red(config.glowColor1), green(config.glowColor1), blue(config.glowColor1), exposure)
                    .putVec4(red(config.glowColor2), green(config.glowColor2), blue(config.glowColor2), config.glowSaturation)
                    .putVec4(red(config.fillColor), green(config.fillColor), blue(config.fillColor), config.fillAlpha)
                    .putVec4(red(config.outlineColor), green(config.outlineColor), blue(config.outlineColor), alpha(config.outlineColor))
                    .putVec4(config.autoGlowColor ? 1.0f : 0.0f, config.fillRainbow ? 1.0f : 0.0f, config.keepShading ? 1.0f : 0.0f, config.shadingStrength)
                    .putVec4(config.outlineColorMode, config.outlineWidth, config.rainbowSpeed, config.rainbowScale)
                    .putVec4(time, smoothDt, config.trailTurbulence, config.trailFlicker)
                    .putVec4(offsetX, offsetY, config.trailFade + fadeAdd, config.corePower)
                    .putVec4(width, height, 1.0f / Math.max(1, width), 1.0f / Math.max(1, height))
                    .putVec4(config.mirrorMix, config.glowExposure * 1.2f, 1.0f, 0.0f);
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(dataUbo.slice(), builder.get());
        }
    }

    private static void writeKawase(float halfPixelX, float halfPixelY, float offset) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var builder = Std140Builder.onStack(stack, KAWASE_UBO_SIZE).putVec4(halfPixelX, halfPixelY, offset, 0.0f);
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(kawaseUbo.slice(), builder.get());
        }
    }

    private static void updateDynamics(EffectConfig config) {
        long now = System.currentTimeMillis();
        float rawDt = lastFrameMs > 0L ? (now - lastFrameMs) / 1000.0f : 1.0f / 60.0f;
        lastFrameMs = now;
        if (rawDt <= 0.0f || rawDt > 0.05f) {
            rawDt = smoothDt;
        }
        rawDt = Math.max(1.0f / 144.0f, Math.min(1.0f / 60.0f, rawDt));
        smoothDt += (rawDt - smoothDt) * 0.08f;
        float dt = smoothDt;
        float time = (now % 100_000L) / 1000.0f;

        float riseDelta = config.trailRise * dt;
        float swayDelta = (float) (Math.sin(time * 3.6f) - Math.sin((time - dt) * 3.6f)) * config.trailSway;
        float trailSmooth = 1.0f - (float) Math.exp(-dt * 8.0f);
        smoothRise += (riseDelta - smoothRise) * trailSmooth;
        smoothSway += (swayDelta - smoothSway) * trailSmooth;

        boolean swinging = mc.player != null && mc.player.handSwinging;
        if (swinging && !wasSwinging) {
            lastSwingMs = now;
            attackImpulse = Math.max(attackImpulse, 0.9f);
        }
        wasSwinging = swinging;

        float burstAge = (now - lastSwingMs) / 1000.0f;
        float burst = config.trailBurst ? Math.max(0.0f, 1.0f - burstAge / 0.45f) : 0.0f;
        smoothBurst += (burst - smoothBurst) * (1.0f - (float) Math.exp(-dt * 6.0f));

        if (config.swapBurstEnabled && mc.player != null) {
            int hash = itemHash(mc.player.getMainHandStack(), mc.player.getOffHandStack());
            if (hash != lastItemHash && lastItemHash != 0) {
                swapImpulse = Math.max(swapImpulse, 0.7f);
                attackImpulse = Math.max(attackImpulse, (float) Math.sqrt(swapImpulse) * config.swapBurstPower * 0.25f);
            }
            lastItemHash = hash;
        }

        if (mc.player != null && config.cameraFollow > 0.0f) {
            float yaw = mc.player.getYaw();
            float pitch = mc.player.getPitch();
            if (!cameraInit) {
                prevYaw = yaw;
                prevPitch = pitch;
                cameraInit = true;
            } else {
                float deltaYaw = MathHelper.wrapDegrees(yaw - prevYaw);
                float deltaPitch = MathHelper.clamp(pitch - prevPitch, -45.0f, 45.0f);
                prevYaw = yaw;
                prevPitch = pitch;
                float targetX = deltaYaw * 1.05f * config.cameraFollow;
                float targetY = -deltaPitch * 1.05f * config.cameraFollow;
                cameraOffsetX += (targetX - cameraOffsetX) * 0.30f;
                cameraOffsetY += (targetY - cameraOffsetY) * 0.30f;
            }
        } else {
            cameraOffsetX *= 0.74f;
            cameraOffsetY *= 0.74f;
            cameraInit = false;
        }
    }

    private static int itemHash(ItemStack main, ItemStack off) {
        int mainHash = main.isEmpty() ? 0 : main.getItem().hashCode() * 31;
        if (!main.isEmpty() && main.getCustomName() != null) {
            mainHash += main.getCustomName().getString().hashCode();
        }
        int offHash = off.isEmpty() ? 0 : off.getItem().hashCode();
        return mainHash * 31 + offHash;
    }

    private static float red(int argb) {
        return ((argb >> 16) & 0xFF) / 255.0f;
    }

    private static float green(int argb) {
        return ((argb >> 8) & 0xFF) / 255.0f;
    }

    private static float blue(int argb) {
        return (argb & 0xFF) / 255.0f;
    }

    private static float alpha(int argb) {
        return ((argb >>> 24) & 0xFF) / 255.0f;
    }

    private static void closeFramebuffers() {
        if (handFbo != null) {
            safeDeleteFramebuffer(handFbo);
            handFbo = null;
        }
        if (sceneFbo != null) {
            safeDeleteFramebuffer(sceneFbo);
            sceneFbo = null;
        }
        if (trailA != null) {
            safeDeleteFramebuffer(trailA);
            trailA = null;
        }
        if (trailB != null) {
            safeDeleteFramebuffer(trailB);
            trailB = null;
        }
        for (SimpleFramebuffer mip : mips) {
            safeDeleteFramebuffer(mip);
        }
        mips.clear();
    }

    private static void fail(Throwable throwable) {
        disabledAfterError = true;
        resetOverrides();
        LightClient.LOGGER.error("HandsRenderer disabled after render error", throwable);
        throwable.printStackTrace();
        closeFramebuffers();
        if (sampler != null) {
            sampler.close();
            sampler = null;
        }
        if (dataUbo != null) {
            dataUbo.close();
            dataUbo = null;
        }
        if (kawaseUbo != null) {
            kawaseUbo.close();
            kawaseUbo = null;
        }
    }

    private static void safeDeleteFramebuffer(SimpleFramebuffer framebuffer) {
        if (framebuffer == null) {
            return;
        }
        if (framebuffer.getColorAttachment() != null) {
            framebuffer.getColorAttachment().close();
        }
        if (framebuffer.getDepthAttachment() != null) {
            framebuffer.getDepthAttachment().close();
        }
        framebuffer.delete();
    }

    private static final class EffectConfig {
        private final boolean glowEnabled;
        private final boolean outerGlow;
        private final int glowRadius;
        private final float glowExposure;
        private final int glowColor1;
        private final int glowColor2;
        private final boolean autoGlowColor;
        private final float glowSaturation;
        private final int fillMode;
        private final int fillColor;
        private final float fillAlpha;
        private final boolean fillRainbow;
        private final float rainbowSpeed;
        private final float rainbowScale;
        private final boolean keepShading;
        private final float shadingStrength;
        private final float mirrorMix;
        private final boolean outlineEnabled;
        private final float outlineWidth;
        private final int outlineColor;
        private final int outlineColorMode;
        private final boolean trailEnabled;
        private final float trailFade;
        private final float trailRise;
        private final float trailSway;
        private final float trailTurbulence;
        private final float trailFlicker;
        private final boolean trailBurst;
        private final float trailBurstPower;
        private final float cameraFollow;
        private final boolean swapBurstEnabled;
        private final float swapBurstPower;
        private final float corePower;

        private EffectConfig(boolean glowEnabled, boolean outerGlow, int glowRadius, float glowExposure,
                             int glowColor1, int glowColor2, boolean autoGlowColor, float glowSaturation,
                             int fillMode, int fillColor, float fillAlpha, boolean fillRainbow,
                             float rainbowSpeed, float rainbowScale, boolean keepShading, float shadingStrength,
                             float mirrorMix, boolean outlineEnabled, float outlineWidth, int outlineColor,
                             int outlineColorMode, boolean trailEnabled, float trailFade, float trailRise,
                             float trailSway, float trailTurbulence, float trailFlicker, boolean trailBurst,
                             float trailBurstPower, float cameraFollow, boolean swapBurstEnabled, float swapBurstPower,
                             float corePower) {
            this.glowEnabled = glowEnabled;
            this.outerGlow = outerGlow;
            this.glowRadius = glowRadius;
            this.glowExposure = glowExposure;
            this.glowColor1 = glowColor1;
            this.glowColor2 = glowColor2;
            this.autoGlowColor = autoGlowColor;
            this.glowSaturation = glowSaturation;
            this.fillMode = fillMode;
            this.fillColor = fillColor;
            this.fillAlpha = fillAlpha;
            this.fillRainbow = fillRainbow;
            this.rainbowSpeed = rainbowSpeed;
            this.rainbowScale = rainbowScale;
            this.keepShading = keepShading;
            this.shadingStrength = shadingStrength;
            this.mirrorMix = mirrorMix;
            this.outlineEnabled = outlineEnabled;
            this.outlineWidth = outlineWidth;
            this.outlineColor = outlineColor;
            this.outlineColorMode = outlineColorMode;
            this.trailEnabled = trailEnabled;
            this.trailFade = trailFade;
            this.trailRise = trailRise;
            this.trailSway = trailSway;
            this.trailTurbulence = trailTurbulence;
            this.trailFlicker = trailFlicker;
            this.trailBurst = trailBurst;
            this.trailBurstPower = trailBurstPower;
            this.cameraFollow = cameraFollow;
            this.swapBurstEnabled = swapBurstEnabled;
            this.swapBurstPower = swapBurstPower;
            this.corePower = corePower;
        }

        private static EffectConfig fromHands(Hands hands) {
            int glow1 = hands.useAutoGlowColor() ? hands.getHeldItemColor() : hands.getGlowColor1();
            int glow2 = hands.useAutoGlowColor() ? hands.getHeldItemColor() : hands.getGlowColor2();
            return new EffectConfig(
                    true,
                    hands.isOuterGlow(),
                    hands.getGlowRadiusPasses(),
                    hands.getGlowExposure(),
                    glow1,
                    glow2,
                    hands.useAutoGlowColor(),
                    hands.getGlowSaturation(),
                    hands.getFillMode(),
                    hands.getFillColor(),
                    hands.getFillAlpha(),
                    hands.useFillRainbow(),
                    hands.getRainbowSpeed(),
                    hands.getRainbowScale(),
                    hands.keepFillShading(),
                    hands.getShadingStrength(),
                    hands.getMirrorMix(),
                    hands.hasOutline(),
                    hands.getOutlineThickness(),
                    hands.getOutlineColor(),
                    hands.getOutlineColorMode(),
                    hands.hasTrail(),
                    hands.getTrailFade(),
                    hands.getTrailRise(),
                    hands.getTrailSway(),
                    hands.getTrailTurbulence(),
                    hands.getTrailFlicker(),
                    hands.useHitBurst(),
                    hands.getHitBurstStrength(),
                    hands.getCameraFollow(),
                    hands.useSwapBurst(),
                    hands.getSwapBurstStrength(),
                    hands.getCorePower()
            );
        }

        private static EffectConfig handBloom() {
            return new EffectConfig(
                    true, true, 4, 1.55f,
                    0xFFFFD45A, 0xFFFF8A2A, true, 1.10f,
                    0, 0, 0.0f, false,
                    0.0f, 0.0f, false, 0.0f,
                    0.0f, false, 1.0f, 0,
                    0, true, 0.05f, 0.10f,
                    0.02f, 0.0f, 0.0f, true,
                    2.0f, 0.0f, false, 0.0f,
                    0.36f
            );
        }
    }
}
