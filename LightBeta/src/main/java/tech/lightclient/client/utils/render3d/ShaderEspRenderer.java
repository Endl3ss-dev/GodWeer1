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
import tech.lightclient.LightClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import tech.lightclient.mixins.accessors.GameRendererAccessor;
import tech.lightclient.client.utils.render.main.world.WorldGeometryEmitter;
import tech.lightclient.client.utils.render.main.world.WorldRenderLayers;
import tech.lightclient.client.utils.render.main.world.WorldRenderer;
import tech.lightclient.modules.impl.render.ShaderEsp;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

/**
 * Пост-обработка ShaderEsp: маска силуэтов игроков захватывается в отдельный
 * framebuffer, размывается каскадом Kawase и композитится обратно аддитивным
 * свечением. Копия пайплайна HandsRenderer без трейлов, зеркал и динамики -
 * эффект полностью статичный.
 */
public final class ShaderEspRenderer {
    private static final Identifier QUAD_SHADER = Identifier.of("light", "core/handsfx_quad");
    private static final int MAX_MIPS = 6;
    private static final int DATA_UBO_SIZE = new Std140SizeCalculator()
            .putVec4().putVec4().putVec4().putVec4().putVec4()
            .putVec4().putVec4().putVec4().putVec4().putVec4().get();
    private static final int KAWASE_UBO_SIZE = new Std140SizeCalculator().putVec4().get();

    private static RenderPipeline glowPipeline;
    private static RenderPipeline fillPipeline;
    private static RenderPipeline outlinePipeline;
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

    private static SimpleFramebuffer maskFbo;
    private static SimpleFramebuffer effectFbo;
    private static final List<SimpleFramebuffer> mips = new java.util.ArrayList<>();

    private static int lastWidth = -1;
    private static int lastHeight = -1;
    private static boolean disabledAfterError;

    private ShaderEspRenderer() {
    }

    public static void render(ShaderEsp esp, List<AbstractClientPlayerEntity> players,
                              Camera camera, float tickDelta,
                              MatrixStack worldStack, Vec3d cameraPos) {
        if (disabledAfterError || mc.getFramebuffer() == null || players.isEmpty()) {
            return;
        }

        try {
            ensureReady();

            // ==== Захват маски силуэтов в отдельный буфер ====
            clearFramebuffer(maskFbo, true);
            RenderSystem.outputColorTextureOverride = maskFbo.getColorAttachmentView();
            RenderSystem.outputDepthTextureOverride = maskFbo.getDepthAttachmentView();
            try {
                drawSilhouettes(esp, players, camera, tickDelta, worldStack, cameraPos);
            } finally {
                RenderSystem.outputColorTextureOverride = null;
                RenderSystem.outputDepthTextureOverride = null;
            }

            // ==== Статичный пост-процесс (копия Hands) ====
            writeData(esp, lastWidth, lastHeight);

            if (esp.useFill()) {
                String fillMode = esp.getFillMode();
                if ("Caustic".equals(fillMode) || "Water".equals(fillMode)) {
                    renderSkyEffect(esp, fillMode, camera, esp.getGlowColor1());
                    runMaskedFill(effectFbo);
                } else {
                    runFill();
                }
            }

            Framebuffer bloom = runKawase(maskFbo, esp.getGlowRadiusPasses());
            drawGlow(bloom);

            if (esp.useOutline()) {
                drawOutline(bloom);
            }
        } catch (Throwable throwable) {
            fail(throwable);
        }
    }

    public static void shutdown() {
        closeFramebuffers();
        disabledAfterError = false;
    }

    // ==== Захват маски: залитые силуэты игроков по контуру модели ====

    private static void drawSilhouettes(ShaderEsp esp, List<AbstractClientPlayerEntity> players,
                                        Camera camera, float tickDelta,
                                        MatrixStack worldStack, Vec3d cameraPos) {
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);
        Matrix4f positionMatrix = new Matrix4f(worldStack.peek().getPositionMatrix());
        Matrix4f projectionMatrix = new Matrix4f(mc.gameRenderer.getBasicProjectionMatrix(fov));

        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                positionMatrix, projectionMatrix)) {
            WorldGeometryEmitter emitter = new WorldGeometryEmitter(
                    camera, new MatrixStack().peek(),
                    renderer.getBuffer(WorldRenderLayers.POSITION_COLOR_QUADS_TRANSLUCENT()));

            int maskColor = 0xFFFFFFFF;
            for (AbstractClientPlayerEntity player : players) {
                PlayerPose pose = buildPose(cameraPos, player, tickDelta);
                emitFilledBody(emitter, pose, maskColor);
            }

            renderer.flush();
        }
    }

    private static PlayerPose buildPose(Vec3d cam, AbstractClientPlayerEntity player, float tickDelta) {
        double x = player.lastRenderX + (player.getX() - player.lastRenderX) * tickDelta;
        double y = player.lastRenderY + (player.getY() - player.lastRenderY) * tickDelta;
        double z = player.lastRenderZ + (player.getZ() - player.lastRenderZ) * tickDelta;

        float bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, player.lastBodyYaw, player.bodyYaw);
        float headYaw = MathHelper.lerpAngleDegrees(tickDelta, player.lastHeadYaw, player.headYaw);
        float headPitch = MathHelper.lerp(tickDelta, player.lastPitch, player.getPitch());

        boolean isFlyingElytra = player.isGliding();

        MatrixStack matrices = new MatrixStack();
        matrices.translate(x - cam.x, y - cam.y, z - cam.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f - bodyYaw));

        // Элитра: тело наклоняется горизонтально, поворот зависит от pitch взгляда
        float elytraPitch = 0f;
        if (isFlyingElytra) {
            // Используем вектор скорости для расчёта угла наклона (как ваниль)
            net.minecraft.util.math.Vec3d vel = player.getVelocity();
            float horizSpeed = MathHelper.sqrt((float)(vel.x * vel.x + vel.z * vel.z));
            float speedAngle = (float)(-(Math.atan2(vel.y, horizSpeed) * 57.29577951308232));
            elytraPitch = MathHelper.lerp(0.5f, headPitch, speedAngle);
            elytraPitch = MathHelper.clamp(elytraPitch, -90f, 90f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(elytraPitch));
            matrices.translate(0, -1.0, 0);
        } else if (player.isSwimming()) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(player.isSubmergedInWater()
                    ? -90f - headPitch : -90f));
            matrices.translate(0, -1.0, 0.3);
        }
        matrices.scale(-1f, -1f, 1f);
        matrices.scale(0.9375f, 0.9375f, 0.9375f);
        matrices.translate(0, -1.501, 0);

        // Анимация ходьбы и удара
        boolean sneaking = player.isInSneakingPose();
        float limbPos = player.limbAnimator.getAnimationProgress(tickDelta);
        float limbSpeed = Math.min(player.limbAnimator.getAmplitude(tickDelta), 1.0f);
        float base = MathHelper.sin(limbPos * 0.6662f);
        float walkSwingArm = base * 1.2f * limbSpeed;
        float walkSwingLeg = base * 1.4f * limbSpeed;
        float handSwing = player.getHandSwingProgress(tickDelta);
        float attackSwing = -(float) (Math.sin(Math.sqrt(handSwing) * Math.PI) * 1.2f);
        boolean mainRight = player.getMainArm() == Arm.RIGHT;
        float idleTime = (player.age + tickDelta) * 0.05f;
        float armSwayZ = MathHelper.sin(idleTime) * 0.04f + 0.03f * limbSpeed;

        float sneakArm = sneaking ? 0.4f : 0.0f;

        // При полёте на элитрах руки прижаты к телу
        float rightArmX, leftArmX, rightLegX, leftLegX;
        if (isFlyingElytra) {
            rightArmX = (float) Math.toRadians(-10f);
            leftArmX  = (float) Math.toRadians(-10f);
            rightLegX = (float) Math.toRadians(10f);
            leftLegX  = (float) Math.toRadians(-10f);
            armSwayZ  = 0f;
        } else {
            rightArmX = walkSwingArm + (mainRight ? attackSwing : 0f) + sneakArm;
            leftArmX  = -walkSwingArm + (mainRight ? 0f : attackSwing) + sneakArm;
            rightLegX = -walkSwingLeg;
            leftLegX  =  walkSwingLeg;
        }

        boolean rightItem = mainRight
                ? !player.getMainHandStack().isEmpty()
                : !player.getOffHandStack().isEmpty();
        boolean leftItem = mainRight
                ? !player.getOffHandStack().isEmpty()
                : !player.getMainHandStack().isEmpty();

        boolean slim = player.getSkin().model() == PlayerSkinType.SLIM;
        return new PlayerPose(matrices, slim, sneaking && !isFlyingElytra,
                MathHelper.wrapDegrees(headYaw - bodyYaw), headPitch,
                rightArmX, leftArmX, rightLegX, leftLegX, armSwayZ,
                rightItem, leftItem, isFlyingElytra, elytraPitch);
    }

    /** Залитая модель с полной анимацией: повороты головы, махи рук и шаг ног. */
    private static void emitFilledBody(WorldGeometryEmitter emitter, PlayerPose pose, int color) {
        float u = 1f / 16f;
        float armW = pose.slim() ? 3 : 4;
        MatrixStack m = pose.matrices();

        // При полёте на элитрах голова поднимается (смотрит вперёд по курсу)
        float headPitchForElytra = pose.elytra() ? pose.headPitch() - pose.elytraPitch() : pose.headPitch();

        // Голова (поворот за взглядом: рыскание + наклон вниз/вверх)
        MatrixStack part = cloneStack(m);
        if (!pose.elytra()) applySneak(part, pose.sneak(), u);
        part.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(pose.headYaw()));
        part.multiply(RotationAxis.POSITIVE_X.rotationDegrees(headPitchForElytra));
        emitPartFaces(emitter, part, -4 * u, -8 * u, -4 * u, 8 * u, 8 * u, 8 * u, color);

        // Корпус
        part = cloneStack(m);
        if (!pose.elytra()) applySneak(part, pose.sneak(), u);
        emitPartFaces(emitter, part, -4 * u, 0, -2 * u, 8 * u, 12 * u, 4 * u, color);

        // Правая рука
        part = cloneStack(m);
        if (!pose.elytra()) applySneak(part, pose.sneak(), u);
        part.translate(-5 * u, 2 * u, 0);
        part.multiply(RotationAxis.POSITIVE_X.rotation(pose.elytra()
                ? pose.rightArmX()
                : itemArmPitch(pose.rightArmX(), pose.rightItem())));
        if (!pose.elytra()) part.multiply(RotationAxis.POSITIVE_Z.rotation(pose.armSwayZ()));
        emitPartFaces(emitter, part, -(armW - 1) * u, -2 * u, -2 * u, armW * u, 12 * u, 4 * u, color);

        // Левая рука
        part = cloneStack(m);
        if (!pose.elytra()) applySneak(part, pose.sneak(), u);
        part.translate(5 * u, 2 * u, 0);
        part.multiply(RotationAxis.POSITIVE_X.rotation(pose.elytra()
                ? pose.leftArmX()
                : itemArmPitch(pose.leftArmX(), pose.leftItem())));
        if (!pose.elytra()) part.multiply(RotationAxis.POSITIVE_Z.rotation(-pose.armSwayZ()));
        emitPartFaces(emitter, part, -1 * u, -2 * u, -2 * u, armW * u, 12 * u, 4 * u, color);

        // Правая нога
        part = cloneStack(m);
        part.translate(-2 * u, 12 * u, pose.sneak() ? 4 * u : 0);
        part.multiply(RotationAxis.POSITIVE_X.rotation(pose.rightLegX()));
        emitPartFaces(emitter, part, -2 * u, 0, -2 * u, 4 * u, 12 * u, 4 * u, color);

        // Левая нога
        part = cloneStack(m);
        part.translate(2 * u, 12 * u, pose.sneak() ? 4 * u : 0);
        part.multiply(RotationAxis.POSITIVE_X.rotation(pose.leftLegX()));
        emitPartFaces(emitter, part, -2 * u, 0, -2 * u, 4 * u, 12 * u, 4 * u, color);
    }

    private static float itemArmPitch(float swingPitch, boolean holdingItem) {
        return holdingItem ? swingPitch * 0.5f - (float) (Math.PI / 10.0) : swingPitch;
    }

    private static void applySneak(MatrixStack part, boolean sneak, float u) {
        if (!sneak) return;
        // Верхняя группа целиком: немного вниз и назад, чтобы голова не висела
        // слишком высоко, а торс не выпирал вперёд; затем наклон туловища
        part.translate(0, 2.5f * u, 1.5f * u);
        part.translate(0, 12 * u, 0);
        part.multiply(RotationAxis.POSITIVE_X.rotationDegrees(28.64f));
        part.translate(0, -12 * u, 0);
    }

    private static void runFill() {
        try (RenderPass pass = beginPass("shaderesp_fill", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(fillPipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("HandSampler", maskFbo.getColorAttachmentView(), sampler);
            pass.bindTexture("MaskSampler", maskFbo.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    /** Заливка Caustic/Water: процедурный эффект подставляется внутрь силуэта через HandSampler. */
    private static void runMaskedFill(Framebuffer effectSource) {
        try (RenderPass pass = beginPass("shaderesp_fill_masked", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(fillPipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("HandSampler", effectSource.getColorAttachmentView(), sampler);
            pass.bindTexture("MaskSampler", maskFbo.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    /**
     * Рендер шейдера из SkyShader (skyshader_caustic / skyshader_water) в отдельный
     * буфер - полный экран, дальше handsfx_fill вырезает его по маске силуэта.
     */
    private static void renderSkyEffect(ShaderEsp esp, String fillMode, Camera camera, int tintColor) {
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
                    .putVec4(1.0f, esp.getEffectSpeed(), esp.getEffectScale(), esp.getEffectIntensity()).get());
            encoder.writeToBuffer(cameraBuffer.slice(), Std140Builder.onStack(stack, 16).putVec4(yawRad, pitchRad, fov, 0f).get());
        }

        try (RenderPass pass = beginPass("shaderesp_skyeffect", effectFbo)) {
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
        try (RenderPass pass = beginPass("shaderesp_glow", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(glowPipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("BloomSampler", bloom.getColorAttachmentView(), sampler);
            pass.bindTexture("MaskSampler", maskFbo.getColorAttachmentView(), sampler);
            pass.draw(0, 3);
        }
    }

    private static void drawOutline(Framebuffer bloom) {
        try (RenderPass pass = beginPass("shaderesp_outline", mc.getFramebuffer())) {
            RenderSystem.bindDefaultUniforms(pass);
            pass.setPipeline(outlinePipeline);
            pass.setUniform("HandsFxData", dataUbo.slice());
            pass.bindTexture("MaskSampler", maskFbo.getColorAttachmentView(), sampler);
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
            try (RenderPass pass = beginPass("shaderesp_kawase_down_" + i, dst)) {
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
            try (RenderPass pass = beginPass("shaderesp_kawase_up_" + i, dst)) {
                RenderSystem.bindDefaultUniforms(pass);
                pass.setPipeline(kawaseUpPipeline);
                pass.setUniform("HandsFxKawase", kawaseUbo.slice());
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

    private static void ensureReady() {
        if (glowPipeline == null) {
            initPipelines();
        }
        int width = Math.max(1, mc.getFramebuffer().textureWidth);
        int height = Math.max(1, mc.getFramebuffer().textureHeight);
        if (width != lastWidth || height != lastHeight || maskFbo == null) {
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
                .withLocation(Identifier.of("light", "pipeline/shaderesp_glow"))
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
                .withLocation(Identifier.of("light", "pipeline/shaderesp_fill"))
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
        outlinePipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/shaderesp_outline"))
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
        kawaseDownPipeline = RenderPipeline.builder()
                .withLocation(Identifier.of("light", "pipeline/shaderesp_kawase_down"))
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
                .withLocation(Identifier.of("light", "pipeline/shaderesp_kawase_up"))
                .withVertexShader(QUAD_SHADER)
                .withFragmentShader(Identifier.of("light", "core/handsfx_kawase_up"))
                .withSampler("InputSampler")
                .withUniform("HandsFxKawase", UniformType.UNIFORM_BUFFER)
                .withoutBlend()
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                .build();

        dataUbo = device.createBuffer(() -> "light_shaderesp_data", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, DATA_UBO_SIZE);
        kawaseUbo = device.createBuffer(() -> "light_shaderesp_kawase", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, KAWASE_UBO_SIZE);

        // Пайплайны Caustic/Water из SkyShader + фуллскрин-треугольник
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

        causticPipeline = buildSkyPipeline("shaderesp_sky_caustic", "skyshader_caustic");
        waterPipeline = buildSkyPipeline("shaderesp_sky_water", "skyshader_water");

        timeBuffer = device.createBuffer(() -> "light_shaderesp_time", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        resolutionBuffer = device.createBuffer(() -> "light_shaderesp_resolution", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        tintColorBuffer = device.createBuffer(() -> "light_shaderesp_tint", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        paramsBuffer = device.createBuffer(() -> "light_shaderesp_params", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        cameraBuffer = device.createBuffer(() -> "light_shaderesp_camera", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
    }

    /** Копия SkyShader.buildPipeline, но без теста глубины: рисуем в свой буфер. */
    private static RenderPipeline buildSkyPipeline(String location, String shaderName) {
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
        maskFbo = new SimpleFramebuffer("shaderesp_mask", width, height, true);
        effectFbo = new SimpleFramebuffer("shaderesp_effect", width, height, false);

        int mipWidth = width;
        int mipHeight = height;
        for (int i = 0; i < MAX_MIPS; i++) {
            mipWidth = Math.max(2, mipWidth / 2);
            mipHeight = Math.max(2, mipHeight / 2);
            mips.add(new SimpleFramebuffer("shaderesp_mip_" + i, mipWidth, mipHeight, false));
        }

        lastWidth = width;
        lastHeight = height;
        clearFramebuffer(maskFbo, true);
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

    /** Тот же layout UBO, что у HandsRenderer - шейдеры handsfx читают его напрямую. */
    private static void writeData(ShaderEsp esp, int width, int height) {
        int glow1 = esp.getGlowColor1();
        int glow2 = esp.getGlowColor2();
        int fill = esp.getFillColor();
        int outline = esp.getOutlineColor();
        float time = (System.currentTimeMillis() % 100_000L) / 1000.0f;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            var builder = Std140Builder.onStack(stack, DATA_UBO_SIZE)
                    .putVec4(red(glow1), green(glow1), blue(glow1), esp.getGlowExposure())
                    .putVec4(red(glow2), green(glow2), blue(glow2), esp.getGlowSaturation())
                    .putVec4(red(fill), green(fill), blue(fill), esp.getFillAlpha())
                    .putVec4(red(outline), green(outline), blue(outline), alpha(outline))
                    .putVec4(0.0f, 0.0f, 0.0f, 0.0f)
                    .putVec4(0.0f, esp.getOutlineThickness(), 0.0f, 0.0f)
                    .putVec4(time, 1.0f / 60.0f, 0.0f, 0.0f)
                    .putVec4(0.0f, 0.0f, 1.0f, 0.0f)
                    .putVec4(width, height, 1.0f / Math.max(1, width), 1.0f / Math.max(1, height))
                    .putVec4(0.0f, esp.getGlowExposure() * 1.2f, 1.0f, 0.0f);
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

    private static MatrixStack cloneStack(MatrixStack source) {
        MatrixStack copy = new MatrixStack();
        copy.multiplyPositionMatrix(new Matrix4f(source.peek().getPositionMatrix()));
        copy.peek().getNormalMatrix().set(source.peek().getNormalMatrix());
        return copy;
    }

    private static Vec3d transform(MatrixStack matrices, double x, double y, double z) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Vector4f vec = new Vector4f((float) x, (float) y, (float) z, 1f);
        matrix.transform(vec);
        return new Vec3d(vec.x, vec.y, vec.z);
    }

    private static void emitQuad(WorldGeometryEmitter emitter, Vec3d a, Vec3d b, Vec3d c, Vec3d d, int color) {
        // Двусторонняя заливка: слой может отсекать задние грани
        emitter.emitQuad(a, b, c, d, color);
        emitter.emitQuad(d, c, b, a, color);
    }

    private static void emitPartFaces(WorldGeometryEmitter emitter, MatrixStack m,
                                      float x, float y, float z, float sx, float sy, float sz, int color) {
        Vec3d p000 = transform(m, x, y, z);
        Vec3d p001 = transform(m, x, y, z + sz);
        Vec3d p010 = transform(m, x, y + sy, z);
        Vec3d p011 = transform(m, x, y + sy, z + sz);
        Vec3d p100 = transform(m, x + sx, y, z);
        Vec3d p101 = transform(m, x + sx, y, z + sz);
        Vec3d p110 = transform(m, x + sx, y + sy, z);
        Vec3d p111 = transform(m, x + sx, y + sy, z + sz);

        emitQuad(emitter, p000, p001, p101, p100, color);
        emitQuad(emitter, p010, p011, p111, p110, color);
        emitQuad(emitter, p000, p010, p110, p100, color);
        emitQuad(emitter, p001, p011, p111, p101, color);
        emitQuad(emitter, p000, p001, p011, p010, color);
        emitQuad(emitter, p100, p101, p111, p110, color);
    }

    private record PlayerPose(MatrixStack matrices, boolean slim, boolean sneak,
                              float headYaw, float headPitch,
                              float rightArmX, float leftArmX,
                              float rightLegX, float leftLegX,
                              float armSwayZ,
                              boolean rightItem, boolean leftItem,
                              boolean elytra, float elytraPitch) {
    }

    private static void closeFramebuffers() {
        if (maskFbo != null) {
            safeDeleteFramebuffer(maskFbo);
            maskFbo = null;
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
        LightClient.LOGGER.error("ShaderEspRenderer disabled after render error", throwable);
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
}
