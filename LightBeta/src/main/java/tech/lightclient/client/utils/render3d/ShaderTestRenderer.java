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
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryStack;
import tech.lightclient.LightClient;
import tech.lightclient.modules.impl.render.ShaderTest;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

/**
 * Пост-обработка ShaderTest: полная копия пайплайна ShaderEsp, но маской
 * выступает не силуэт игрока, а руки/предмет от первого лица, захваченные
 * в собственный framebuffer. Заливка (обычная/Caustic/Water), Kawase-bloom,
 * аддитивное свечение и контур - всё как в ShaderEsp.
 */
public final class ShaderTestRenderer {
    private static final Identifier QUAD_SHADER = Identifier.of("light", "core/shadertest_quad");
    private static final int MAX_MIPS = 6;
    private static final int DATA_UBO_SIZE = new Std140SizeCalculator()
            .putVec4().putVec4().putVec4().putVec4().putVec4()
            .putVec4().putVec4().putVec4().putVec4().putVec4().get();
    private static final int KAWASE_UBO_SIZE = new Std140SizeCalculator().putVec4().get();

    private static RenderPipeline glowPipeline;
    private static RenderPipeline fillPipeline;
    private static RenderPipeline outlinePipeline;
    private static RenderPipeline blitPipeline;
    private static RenderPipeline kawaseDownPipeline;
    private static RenderPipeline kawaseUpPipeline;
    private static RenderPipeline causticPipeline;
    private static RenderPipeline waterPipeline;

    private static GpuSampler sampler;
    private static GpuBuffer dataUbo;
    private static GpuBuffer kawaseUbo;
    private static GpuBuffer fullscreenBuffer;
    private static GpuBuffer timeBuffer;
    private static GpuBuffer resolutionBuffer;
    private static GpuBuffer tintColorBuffer;
    private static GpuBuffer paramsBuffer;
    private static GpuBuffer cameraBuffer;
    private static long startMillis = -1L;

    private static SimpleFramebuffer handFbo;
    private static SimpleFramebuffer effectFbo;
    private static final List<SimpleFramebuffer> mips = new ArrayList<>();

    private static int lastWidth = -1;
    private static int lastHeight = -1;
    private static boolean disabledAfterError;
    private static boolean capturing;
    private static boolean needsPost;

    private ShaderTestRenderer() {
    }

    // ==== Захват рук в отдельный буфер ====

    public static void preHandCapture() {
        capturing = false;
        needsPost = false;
        if (disabledAfterError || mc.getFramebuffer() == null) {
            return;
        }
        try {
            ensureReady();
            clearFramebuffer(handFbo, true);
            RenderSystem.outputColorTextureOverride = handFbo.getColorAttachmentView();
            RenderSystem.outputDepthTextureOverride = handFbo.getDepthAttachmentView();
            capturing = true;
        } catch (Throwable throwable) {
            fail(throwable);
        }
    }

    public static void postHandCapture() {
        if (!capturing) {
            return;
        }
        capturing = false;
        RenderSystem.outputColorTextureOverride = null;
        RenderSystem.outputDepthTextureOverride = null;
        needsPost = true;
    }

    public static boolean needsPost() {
        return needsPost && !disabledAfterError;
    }

    /** Основной проход: возвращает руки на экран и накладывает весь эффект ShaderEsp. */
    public static void postHandsRender(ShaderTest mod) {
        if (disabledAfterError || !needsPost || mod == null || !mod.isState()
                || handFbo == null || mc.getFramebuffer() == null) {
            needsPost = false;
            return;
        }
        try {
            ensureReady();
            writeData(mod, lastWidth, lastHeight);

            // Руки были отрисованы в свой FBO - копируем их обратно на экран
            blit(handFbo, mc.getFramebuffer());

            if (mod.useFill()) {
                String fillMode = mod.getFillMode();
                if ("Caustic".equals(fillMode) || "Water".equals(fillMode)) {
                    renderSkyEffect(mod, fillMode, mc.gameRenderer.getCamera(), mod.getGlowColor1());
                    runMaskedFill(effectFbo);
                } else {
                    runFill();
                }
            }

            Framebuffer bloom = runKawase(handFbo, mod.getGlowRadiusPasses());
            drawGlow(bloom);

            if (mod.useOutline()) {
                drawOutline(bloom);
            }
        } catch (Throwable throwable) {
            fail(throwable);
        } finally {
            needsPost = false;
            RenderSystem.outputColorTextureOverride = null;
            RenderSystem.outputDepthTextureOverride = null;
        }
    }

    public static void shutdown() {
        closeFramebuffers();
        capturing = false;
        needsPost = false;
        disabledAfterError = false;
        RenderSystem.outputColorTextureOverride = null;
        RenderSystem.outputDepthTextureOverride = null;
    }

    // ==== Пассы ====

    private static void blit(Framebuffer from, Framebuffer to) {
        try (RenderPass pass = beginPass("shadertest_blit", to)) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(blitPipeline);
            pass.setUniform("ShaderTestData", dataUbo.slice());
            pass.bindTexture("InputSampler", from.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    private static void runFill() {
        try (RenderPass pass = beginPass("shadertest_fill", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(fillPipeline);
            pass.setUniform("ShaderTestData", dataUbo.slice());
            pass.bindTexture("HandSampler", handFbo.getColorAttachmentView(), sampler);
            pass.bindTexture("MaskSampler", handFbo.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    /** Заливка Caustic/Water: процедурный эффект подставляется внутрь рук через HandSampler. */
    private static void runMaskedFill(Framebuffer effectSource) {
        try (RenderPass pass = beginPass("shadertest_fill_masked", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(fillPipeline);
            pass.setUniform("ShaderTestData", dataUbo.slice());
            pass.bindTexture("HandSampler", effectSource.getColorAttachmentView(), sampler);
            pass.bindTexture("MaskSampler", handFbo.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    /** Рендер процедурного эффекта на полный экран - дальше fill вырежет его по маске рук. */
    private static void renderSkyEffect(ShaderTest mod, String fillMode, Camera camera, int tintColor) {
        if (startMillis < 0L) {
            startMillis = System.currentTimeMillis();
        }

        float time = (System.currentTimeMillis() - startMillis) / 1000.0f;
        float yawRad = (float) Math.toRadians(-camera.getYaw());
        float pitchRad = (float) Math.toRadians(camera.getPitch());
        float fov = mc.options.getFov().getValue().floatValue();

        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            encoder.writeToBuffer(timeBuffer.slice(), Std140Builder.onStack(stack, 16).putFloat(time).get());
            encoder.writeToBuffer(resolutionBuffer.slice(), Std140Builder.onStack(stack, 16).putVec2(lastWidth, lastHeight).get());
            encoder.writeToBuffer(tintColorBuffer.slice(), Std140Builder.onStack(stack, 16)
                    .putVec3(red(tintColor), green(tintColor), blue(tintColor)).get());
            encoder.writeToBuffer(paramsBuffer.slice(), Std140Builder.onStack(stack, 16)
                    .putVec4(1.0f, mod.getEffectSpeed(), mod.getEffectScale(), mod.getEffectIntensity()).get());
            encoder.writeToBuffer(cameraBuffer.slice(), Std140Builder.onStack(stack, 16).putVec4(yawRad, pitchRad, fov, 0f).get());
        }

        try (RenderPass pass = beginPass("shadertest_skyeffect", effectFbo)) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline("Caustic".equals(fillMode) ? causticPipeline : waterPipeline);
            pass.setUniform("uTime", timeBuffer.slice());
            pass.setUniform("uResolution", resolutionBuffer.slice());
            pass.setUniform("uColor", tintColorBuffer.slice());
            pass.setUniform("uParams", paramsBuffer.slice());
            pass.setUniform("uCamera", cameraBuffer.slice());
            pass.setVertexBuffer(0, fullscreenBuffer);
            pass.draw(0, 3);
        }
    }

    private static void drawGlow(Framebuffer bloom) {
        try (RenderPass pass = beginPass("shadertest_glow", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(glowPipeline);
            pass.setUniform("ShaderTestData", dataUbo.slice());
            pass.bindTexture("BloomSampler", bloom.getColorAttachmentView(), sampler);
            pass.bindTexture("MaskSampler", handFbo.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    private static void drawOutline(Framebuffer bloom) {
        try (RenderPass pass = beginPass("shadertest_outline", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(outlinePipeline);
            pass.setUniform("ShaderTestData", dataUbo.slice());
            pass.bindTexture("MaskSampler", handFbo.getColorAttachmentView(), sampler);
            pass.bindTexture("BloomSampler", bloom.getColorAttachmentView(), sampler);
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
            try (RenderPass pass = beginPass("shadertest_kawase_down_" + i, dst)) {
                RenderSystem.bindDefaultUniforms(pass);
                pass.setPipeline(kawaseDownPipeline);
                pass.setUniform("ShaderTestKawase", kawaseUbo.slice());
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
            try (RenderPass pass = beginPass("shadertest_kawase_up_" + i, dst)) {
                RenderSystem.bindDefaultUniforms(pass);
                pass.setPipeline(kawaseUpPipeline);
                pass.setUniform("ShaderTestKawase", kawaseUbo.slice());
                pass.bindTexture("InputSampler", src.getColorAttachmentView(), sampler);
                pass.draw(0, 3);
            }
        }
        return mips.get(0);
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

    // ==== Инициализация ====

    private static void ensureReady() {
        if (glowPipeline == null) {
            initPipelines();
        }
        int width = Math.max(1, mc.getFramebuffer().textureWidth);
        int height = Math.max(1, mc.getFramebuffer().textureHeight);
        if (width != lastWidth || height != lastHeight || handFbo == null) {
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
                .withLocation(Identifier.of("light", "pipeline/shadertest_glow"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/shadertest_glow"))
                .withSampler("BloomSampler")
                .withSampler("MaskSampler")
                .withUniform("ShaderTestData", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.LIGHTNING)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        fillPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/shadertest_fill"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/shadertest_fill"))
                .withSampler("HandSampler")
                .withSampler("MaskSampler")
                .withUniform("ShaderTestData", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        outlinePipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/shadertest_outline"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/shadertest_outline"))
                .withSampler("MaskSampler")
                .withSampler("BloomSampler")
                .withUniform("ShaderTestData", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        blitPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/shadertest_blit"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/shadertest_blit"))
                .withSampler("InputSampler")
                .withUniform("ShaderTestData", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        kawaseDownPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/shadertest_kawase_down"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/shadertest_kawase_down"))
                .withSampler("InputSampler")
                .withUniform("ShaderTestKawase", UniformType.UNIFORM_BUFFER)
                .withoutBlend()
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();
        kawaseUpPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/shadertest_kawase_up"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/shadertest_kawase_up"))
                .withSampler("InputSampler")
                .withUniform("ShaderTestKawase", UniformType.UNIFORM_BUFFER)
                .withoutBlend()
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();

        dataUbo = device.createBuffer(() -> "light_shadertest_data", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, DATA_UBO_SIZE);
        kawaseUbo = device.createBuffer(() -> "light_shadertest_kawase", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, KAWASE_UBO_SIZE);

        // Пайплайны Caustic/Water + фуллскрин-треугольник с явным вершинным буфером
        VertexFormat positionFormat = VertexFormat.builder()
                .add("Position", VertexFormatElement.POSITION)
                .build();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer buffer = stack.malloc(3 * 3 * Float.BYTES);
            buffer.asFloatBuffer()
                    .put(-1f).put(-1f).put(1f)
                    .put(3f).put(-1f).put(1f)
                    .put(-1f).put(3f).put(1f);
            fullscreenBuffer = positionFormat.uploadImmediateVertexBuffer(buffer);
        }

        causticPipeline = buildEffectPipeline("pipeline/shadertest_sky_caustic", "shadertest_caustic");
        waterPipeline = buildEffectPipeline("pipeline/shadertest_sky_water", "shadertest_water");

        timeBuffer = device.createBuffer(() -> "light_shadertest_time", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        resolutionBuffer = device.createBuffer(() -> "light_shadertest_resolution", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        tintColorBuffer = device.createBuffer(() -> "light_shadertest_tint", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        paramsBuffer = device.createBuffer(() -> "light_shadertest_params", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        cameraBuffer = device.createBuffer(() -> "light_shadertest_camera", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
    }

    private static RenderPipeline buildEffectPipeline(String location, String shaderName) {
        Identifier shader = Identifier.of("light", "core/" + shaderName);
        return RenderPipeline.builder()
                .withLocation(Identifier.of("light", location))
                .withVertexShader(shader)
                .withFragmentShader(shader)
                .withVertexFormat(VertexFormats.POSITION, VertexFormat.DrawMode.TRIANGLES)
                .withUniform("uTime", UniformType.UNIFORM_BUFFER)
                .withUniform("uResolution", UniformType.UNIFORM_BUFFER)
                .withUniform("uColor", UniformType.UNIFORM_BUFFER)
                .withUniform("uParams", UniformType.UNIFORM_BUFFER)
                .withUniform("uCamera", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withCull(false)
                .build();
    }

    private static void resize(int width, int height) {
        closeFramebuffers();
        handFbo = new SimpleFramebuffer("shadertest_hands", width, height, true);
        effectFbo = new SimpleFramebuffer("shadertest_effect", width, height, false);

        int mipWidth = width;
        int mipHeight = height;
        for (int i = 0; i < MAX_MIPS; i++) {
            mipWidth = Math.max(2, mipWidth / 2);
            mipHeight = Math.max(2, mipHeight / 2);
            mips.add(new SimpleFramebuffer("shadertest_mip_" + i, mipWidth, mipHeight, false));
        }

        lastWidth = width;
        lastHeight = height;
        clearFramebuffer(handFbo, true);
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

    /** Тот же layout UBO, что у ShaderEsp - шейдеры shadertest_* читают его напрямую. */
    private static void writeData(ShaderTest mod, int width, int height) {
        int glow1 = mod.getGlowColor1();
        int glow2 = mod.getGlowColor2();
        int fill = mod.getFillColor();
        int outline = mod.getOutlineColor();
        float time = (System.currentTimeMillis() % 100_000L) / 1000.0f;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            var builder = Std140Builder.onStack(stack, DATA_UBO_SIZE)
                    .putVec4(red(glow1), green(glow1), blue(glow1), mod.getGlowExposure())
                    .putVec4(red(glow2), green(glow2), blue(glow2), mod.getGlowSaturation())
                    .putVec4(red(fill), green(fill), blue(fill), mod.getFillAlpha())
                    .putVec4(red(outline), green(outline), blue(outline), alpha(outline))
                    .putVec4(0.0f, 0.0f, 0.0f, 0.0f)
                    .putVec4(0.0f, mod.getOutlineThickness(), 0.0f, 0.0f)
                    .putVec4(time, 1.0f / 60.0f, 0.0f, 0.0f)
                    .putVec4(0.0f, 0.0f, 1.0f, 0.0f)
                    .putVec4(width, height, 1.0f / Math.max(1, width), 1.0f / Math.max(1, height))
                    .putVec4(0.0f, mod.getGlowExposure() * 1.2f, 1.0f, 0.0f);
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(dataUbo.slice(), builder.get());
        }
    }

    private static void writeKawase(float halfPixelX, float halfPixelY, float offset) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var builder = Std140Builder.onStack(stack, KAWASE_UBO_SIZE).putVec4(halfPixelX, halfPixelY, offset, 0.0f);
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(kawaseUbo.slice(), builder.get());
        }
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
        if (effectFbo != null) {
            safeDeleteFramebuffer(effectFbo);
            effectFbo = null;
        }
        for (SimpleFramebuffer mip : mips) {
            safeDeleteFramebuffer(mip);
        }
        mips.clear();
        lastWidth = -1;
        lastHeight = -1;
    }

    private static void fail(Throwable throwable) {
        disabledAfterError = true;
        capturing = false;
        needsPost = false;
        RenderSystem.outputColorTextureOverride = null;
        RenderSystem.outputDepthTextureOverride = null;
        LightClient.LOGGER.error("ShaderTestRenderer disabled after render error", throwable);
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
}
