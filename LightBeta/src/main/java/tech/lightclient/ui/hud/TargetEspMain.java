package tech.lightclient.ui.hud;

import tech.lightclient.LightClient;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.color.basic.ColorUtils;
import tech.lightclient.client.utils.render.main.world.WorldGeometryEmitter;
import tech.lightclient.client.utils.render.main.world.WorldLineRenderer;
import tech.lightclient.client.utils.render.main.world.WorldRenderLayers;
import tech.lightclient.client.utils.render.main.world.WorldRenderer;
import tech.lightclient.mixins.accessors.GameRendererAccessor;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import tech.lightclient.modules.impl.combat.AimBot;
import tech.lightclient.modules.impl.combat.AuraModule;
import tech.lightclient.modules.impl.combat.ThrowableAim;
import tech.lightclient.modules.impl.render.TargetESP;
import tech.lightclient.ui.hud.api.HudElement;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TargetEspMain implements HudElement {
    private static final Identifier SOUL_TEXTURE = Identifier.of("light", "images/etc/bloom.png");
    private static final Identifier STAR_TEXTURE = Identifier.of("light", "images/etc/starnew.png");
    private static final Identifier CHAIN_TEXTURE = Identifier.of("light", "images/targetesp/chain.png");
    private static final Identifier CROSSHAIR_TEXTURE = Identifier.of("light", "targetesp/crosshair_generated");
    private static final Identifier SCANNER_TEXTURE = Identifier.of("light", "targetesp/scanner_generated");
    private static final long HURT_FLASH_HALF_MS = 150L;
    private static final long PRIV_COLOR_CYCLE_MS = 2000L;
    private static final long MODE_ERROR_LOG_INTERVAL_MS = 3000L;
    private static final float[] PRIV_BASE_ANGLES = {45f, 135f, 225f, 315f};
    private static final float HURT_PULSE_MAX_SCALE = 1.2f;
    private static final long HURT_PULSE_RISE_MS = 80L;
    private static final long HURT_PULSE_HOLD_MS = 250L;
    private static final long HURT_PULSE_FALL_MS = 80L;
    private static final java.util.Map<String, Long> MODE_ERROR_LOG_TIME = new java.util.concurrent.ConcurrentHashMap<>();
    private static final long KOLCO_STAR_LIFETIME_MS = 520L;
    private static final long KOLCO_STAR_SPAWN_INTERVAL_MS = 55L;
    private static final Random KOLCO_RANDOM = new Random();
    private static volatile boolean crosshairTextureReady;
    private static volatile boolean scannerTextureReady;
    private static volatile LivingEntity scannerTarget;
    private static volatile float scannerAlpha;
    private static volatile float scannerProgress;
    private static NativeImageBackedTexture scannerTexture;
    private static long scannerTextureUploadMs;

    public static ModeSetting targetEspType = new ModeSetting("Target ESP", "Шейдер", "Шейдер", "Квадрат", "Прицел", "Цепи", "Кристалы", "Кристалы Новые");
    public static ModeSetting imageType = new ModeSetting("Image", "Квадрат", "Квадрат", "Полу квадрат");
    public static ModeSetting shaderType = new ModeSetting("Shader", "Призраки", "Призраки", "Души", "Души 2", "Колечко");
    public static SliderSetting sizeDush = new SliderSetting("Size", 0.22f, 0.1f, 0.4f, 0.01f);
    public static SliderSetting dlinaDush = new SliderSetting("Length", 6f, 1f, 12f, 0.1f);
    public static SliderSetting factorDush = new SliderSetting("Factor", 12f, 0f, 22f, 0.1f);
    public static SliderSetting particleDensityDush = new SliderSetting("Density", 1.7f, 1f, 3f, 0.1f);
    public static SliderSetting chainSpeed = new SliderSetting("Chain Speed", 1.0f, 0.1f, 3.0f, 0.1f);

    public static SliderSetting boltCount = new SliderSetting("Bolt Count", 5f, 1f, 30f, 1f);
    public static SliderSetting boltSegments = new SliderSetting("Bolt Segments", 10f, 4f, 30f, 1f);
    public static SliderSetting boltJitter = new SliderSetting("Bolt Jitter", 0.25f, 0.05f, 1.0f, 0.05f);
    public static SliderSetting boltRefresh = new SliderSetting("Bolt Refresh", 90f, 10f, 400f, 5f);
    public static SliderSetting boltSpeed = new SliderSetting("Bolt Speed", 2.0f, 0.1f, 8.0f, 0.1f);
    public static SliderSetting boltWidth = new SliderSetting("Bolt Width", 1.3f, 0.5f, 5.0f, 0.1f);
    public static SliderSetting boltVolume = new SliderSetting("Bolt Volume", 1.0f, 0.0f, 3.0f, 0.1f);
    public static BooleanSetting boltGlow = new BooleanSetting("Bolt Glow", true);
    public static BooleanSetting boltBranches = new BooleanSetting("Bolt Branches", true);
    public static BooleanSetting boltFlicker = new BooleanSetting("Bolt Flicker", true);

    private final SimpleLinearAnimation alphaAnim = new SimpleLinearAnimation(400);
    private float currentAlpha = 0.0f;
    private float targetAlpha = 0.0f;
    private long lastAlphaUpdate = 0;
    private static final float ALPHA_ANIMATION_SPEED = 2.0f;
    private float rotationAngle = 0.0f;
    private long lastRotationUpdate = 0;
    private float currentScale = 0.0f;
    private float targetScale = 0.0f;
    private Vec3d lastTargetPos = null;
    private float lastCircleStep;
    private float circleStep;
    private final List<KolcoStarParticle> kolcoStars = new ArrayList<>();
    private long lastKolcoStarSpawnMs;

    private final List<Bolt> activeBolts = new ArrayList<>();
    private final Random boltRandom = new Random();
    private long lastBoltRefresh = 0L;
    private float lastBoltHeight = 1.8f;
    private float lastBoltWidth = 0.6f;

    private LivingEntity lastTarget;
    private LivingEntity lastHurtTarget;
    private int lastHurtTime;
    private long lastHurtAt;
    private int lastHurtPulseHurtTime;
    private long hurtPulseAnimStartMs;
    private float hurtPulseIntensity;

    private Vec3d previousTargetPos = null;
    private Vec3d currentMovementDirection = null;
    private double movementSpeed = 0.0;
    private long lastMovementUpdate = 0;
    private static final double MOVEMENT_THRESHOLD = 0.01;
    private static final long MOVEMENT_UPDATE_INTERVAL = 50;

    private static boolean isAimBotActive() {
        var manager = LightClient.getFunctionManager();
        return manager != null && manager.getAimBot() != null && manager.getAimBot().isState();
    }

    private static boolean isAuraEspActive() {
        var manager = LightClient.getFunctionManager();
        return manager != null && manager.getAttackAura() != null && manager.getAttackAura().isState();
    }

    private static boolean isThrowableAimEspActive() {
        var manager = LightClient.getFunctionManager();
        return manager != null && manager.getThrowableAim() != null && manager.getThrowableAim().isState();
    }

    private static void syncActiveSettings() {
        var manager = LightClient.getFunctionManager();
        TargetESP settings = manager == null ? null : manager.getTargetESP();
        if (settings == null) {
            return;
        }

        targetEspType = settings.targetEspType;
        imageType = settings.imageType;
        shaderType = settings.shaderType;
        sizeDush = settings.sizeDush;
        dlinaDush = settings.dlinaDush;
        factorDush = settings.factorDush;
        particleDensityDush = settings.particleDensityDush;
        chainSpeed = settings.chainSpeed;
        boltCount = settings.boltCount;
        boltSegments = settings.boltSegments;
        boltJitter = settings.boltJitter;
        boltRefresh = settings.boltRefresh;
        boltSpeed = settings.boltSpeed;
        boltWidth = settings.boltWidth;
        boltVolume = settings.boltVolume;
        boltGlow = settings.boltGlow;
        boltBranches = settings.boltBranches;
        boltFlicker = settings.boltFlicker;
    }

    private LivingEntity getActiveTarget() {
        syncActiveSettings();

        if (isAimBotActive()) {
            LivingEntity aimbotTarget = AimBot.getTarget();
            if (aimbotTarget != null) {
                return aimbotTarget;
            }
        }

        if (isAuraEspActive()) {
            LivingEntity auraTarget = AuraModule.getTarget();
            if (auraTarget != null) {
                return auraTarget;
            }
        }

        if (isThrowableAimEspActive()) {
            var manager = LightClient.getFunctionManager();
            LivingEntity throwableAimTarget = manager != null && manager.getThrowableAim() != null ? ThrowableAim.target : null;
            if (throwableAimTarget != null) {
                return throwableAimTarget;
            }
        }

        return null;
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        if (mc.world == null)
            return;
    }

    @EventHandler
    public void renderWorld(EventRender.World event) {
        if (mc.world == null)
            return;

        LivingEntity currentTarget = getActiveTarget();
        updateTargetState(currentTarget, event.getTicks());
        updateAlpha();
        updateRotation();

        boolean shaderActive = targetEspType.is("Шейдер");
        boolean imageActive = targetEspType.is("Квадрат");
        boolean crosshairActive = targetEspType.is("Прицел");
        boolean scannerActive = targetEspType.is("Сканер");
        boolean chainsActive = targetEspType.is("Цепи");
        boolean crystalsActive = targetEspType.is("Кристалы");
        boolean crystalsNewActive = targetEspType.is("Кристалы Новые");
        boolean lightningActive = targetEspType.is("Молнии");

        boolean hasImageMode = imageActive;
        boolean hasCrosshairMode = crosshairActive;
        boolean hasScannerMode = scannerActive;
        boolean hasGhostsMode = shaderActive && shaderType.is("Призраки");
        boolean hasPrivMode = shaderActive && shaderType.is("Души");
        boolean hasPriv2Mode = shaderActive && shaderType.is("Души 2");
        boolean hasKolcoMode = shaderActive && shaderType.is("Колечко");
        boolean hasChainsMode = chainsActive;
        boolean hasCrystalsMode = crystalsActive;
        boolean hasCrystalsNewMode = crystalsNewActive;
        boolean hasLightningMode = lightningActive;

        boolean active = currentTarget != null && (hasImageMode || hasCrosshairMode || hasScannerMode || hasGhostsMode || hasPrivMode || hasPriv2Mode
                || hasKolcoMode || hasChainsMode || hasCrystalsMode || hasCrystalsNewMode || hasLightningMode);

        if (active) {
            alphaAnim.show();
            lastTarget = currentTarget;
        } else {
            alphaAnim.hide();
        }

        float alpha = alphaAnim.getProgress();
        if (alpha <= 0f) {
            lastTarget = null;
            scannerTarget = null;
            scannerAlpha = 0.0f;
            return;
        }

        LivingEntity renderTarget = active ? currentTarget : lastTarget;
        if (renderTarget == null) {
            scannerTarget = null;
            scannerAlpha = 0.0f;
            return;
        }

        if (hasScannerMode) {
            scannerTarget = renderTarget;
            scannerAlpha = alpha;
            scannerProgress = scannerWaveProgress();
            ensureScannerTexture();
            updateScannerTexture(scannerProgress);
        } else {
            scannerTarget = null;
            scannerAlpha = 0.0f;
        }

        if (hasGhostsMode) {
            safeRenderMode("ghosts", () -> renderGhostsWorld(event, renderTarget, alpha));
        }
        if (hasPrivMode) {
            safeRenderMode("souls", () -> renderPrivWorld(event, renderTarget, alpha));
        }
        if (hasPriv2Mode) {
            safeRenderMode("souls2", () -> renderCircleParticlesWorld(event, renderTarget, alpha));
        }
        if (hasKolcoMode) {
            safeRenderMode("ring", () -> renderKolcoWorld(event, renderTarget, alpha));
        }
        if (hasImageMode) {
            safeRenderMode("image", () -> renderTargetEspWorld(event, renderTarget, alpha));
        }
        if (hasCrosshairMode) {
            safeRenderMode("crosshair", () -> renderCrosshairWorld(event, renderTarget, alpha));
        }
        if (hasChainsMode) {
            safeRenderMode("chains", () -> renderChainsWorld(event, renderTarget, alpha));
        }
        if (hasCrystalsMode) {
            safeRenderMode("crystals", () -> renderCrystalWorld(event, renderTarget, alpha, false));
        }
        if (hasCrystalsNewMode) {
            safeRenderMode("crystals_new", () -> renderCrystalWorld(event, renderTarget, alpha, true));
        }
        if (hasLightningMode) {
            boolean spawning = currentTarget != null;
            safeRenderMode("lightning", () -> renderLightningWorld(event, renderTarget, alpha, spawning));
        }
    }

    private void safeRenderMode(String modeKey, Runnable renderCall) {
        try {
            renderCall.run();
        } catch (Throwable t) {
            logModeErrorThrottled(modeKey, t);
        }
    }

    private void logModeErrorThrottled(String modeKey, Throwable throwable) {
        long now = System.currentTimeMillis();
        Long lastLog = MODE_ERROR_LOG_TIME.get(modeKey);
        if (lastLog != null && now - lastLog < MODE_ERROR_LOG_INTERVAL_MS) {
            return;
        }
        MODE_ERROR_LOG_TIME.put(modeKey, now);
        String message = throwable.getMessage();
        System.err.println("[TargetEsp] Render mode '" + modeKey + "' failed: "
                + (message == null ? throwable.getClass().getSimpleName() : message));
    }

    public void onWorldChange() {
        activeBolts.clear();
        lastBoltRefresh = 0L;
    }

    private void updateTargetState(LivingEntity target, float ticks) {
        if (target == null || mc.player.distanceTo(target) > 50) {
            targetAlpha = 0.0f;
            targetScale = 0.0f;
        } else {
            targetAlpha = 1.0f;
            targetScale = 1.0f;

            Vec3d newTargetPos = new Vec3d(
                    target.lastX + (target.getX() - target.lastX) * ticks,
                    target.lastY + (target.getY() - target.lastY) * ticks + target.getHeight() / 2,
                    target.lastZ + (target.getZ() - target.lastZ) * ticks);

            updateMovementTracking(newTargetPos);

            lastTargetPos = newTargetPos;
        }
    }

    private void updateMovementTracking(Vec3d currentPos) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastMovementUpdate > MOVEMENT_UPDATE_INTERVAL) {
            if (previousTargetPos != null) {
                Vec3d movementVector = currentPos.subtract(previousTargetPos);

                movementSpeed = movementVector.length() / (MOVEMENT_UPDATE_INTERVAL / 1000.0);

                if (movementSpeed > MOVEMENT_THRESHOLD) {
                    currentMovementDirection = movementVector.normalize();
                } else {
                    if (currentMovementDirection == null) {
                        currentMovementDirection = new Vec3d(0, 0, 0);
                    }
                }
            }

            previousTargetPos = currentPos;
            lastMovementUpdate = currentTime;
        }
    }

    private void updateAlpha() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastAlphaUpdate) / 1000.0f;
        lastAlphaUpdate = currentTime;

        float change = deltaTime * ALPHA_ANIMATION_SPEED;
        currentAlpha = Math.max(0.0f, Math.min(1.0f, currentAlpha + (currentAlpha < targetAlpha ? change : -change)));
        currentScale = Math.max(0.0f, Math.min(1.0f, currentScale + (currentScale < targetScale ? change : -change)));
    }

    private void updateRotation() {
        long currentTime = System.currentTimeMillis();
        float cycleProgress = (currentTime % 3660) / 3660.0f;
        boolean isRightPhase = cycleProgress < 0.5f;
        float phaseProgress = cycleProgress % 0.5f / 0.5f;

        float rotationPhase = 1.5f / 1.53f;
        float progress = phaseProgress > rotationPhase ? 1.0f : phaseProgress / rotationPhase;
        float easedProgress = 0.5f - 0.5f * (float) Math.cos(progress * Math.PI);

        rotationAngle = (isRightPhase ? easedProgress : 1.0f - easedProgress) * 720.0f;
        lastRotationUpdate = currentTime;
    }

    private void renderTargetEspWorld(EventRender.World event, LivingEntity target, float alpha) {
        if (lastTargetPos == null)
            return;

        MatrixStack stack = event.getMatrixStack();
        Camera camera = mc.gameRenderer.getCamera();
        float tickDelta = event.getTicks();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);

        int baseStart = ClientColors.ICON.getRGB();
        int baseEnd = ClientColors.ICON.getRGB();
        float flash = getHurtFlash(target);
        int tintStart = applyDamageTint(baseStart, flash);
        int tintEnd = applyDamageTint(baseEnd, flash);
        int a = Math.max(0, Math.min(255, Math.round(alpha * 255f)));
        int colorStart = ColorUtils.rgba(ColorUtils.red(tintStart), ColorUtils.green(tintStart),
                ColorUtils.blue(tintStart), a);
        int colorEnd = ColorUtils.rgba(ColorUtils.red(tintEnd), ColorUtils.green(tintEnd), ColorUtils.blue(tintEnd), a);
        int colorStartSoft = ColorUtils.rgba(ColorUtils.red(tintStart), ColorUtils.green(tintStart),
                ColorUtils.blue(tintStart), Math.max(0, Math.min(255, Math.round(a * 0.8f))));
        int colorEndSoft = ColorUtils.rgba(ColorUtils.red(tintEnd), ColorUtils.green(tintEnd), ColorUtils.blue(tintEnd),
                Math.max(0, Math.min(255, Math.round(a * 0.8f))));

        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                stack.peek().getPositionMatrix(), mc.gameRenderer.getBasicProjectionMatrix(fov))) {
            Identifier textureId = getImageIdentifier();
            VertexConsumer consumer = renderer.getBuffer(WorldRenderLayers.TEXTURED_QUADS_ADDITIVE_NO_DEPTH(textureId));
            MatrixStack identity = new MatrixStack();
            WorldGeometryEmitter emitter = new WorldGeometryEmitter(camera, identity.peek(), consumer);

            MatrixStack matrices = new MatrixStack();
            Vec3d cameraPos = camera.getCameraPos();
            double tX = lastTargetPos.x - cameraPos.x;
            double tY = lastTargetPos.y - cameraPos.y;
            double tZ = lastTargetPos.z - cameraPos.z;
            matrices.translate(tX, tY, tZ);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationAngle));

            float hurtScale = getHurtPulseScale(target);
            float baseSize = imageType.is("Полу квадрат") ? 0.97f : 1.07f;
            float size = baseSize * hurtScale;
            matrices.scale(size, size, size);
            matrices.translate(-0.5f, -0.5f, 0f);

            Matrix4f localTransform = matrices.peek().getPositionMatrix();
            Vec3d v0 = transform(localTransform, 0, 1, 0);
            Vec3d v1 = transform(localTransform, 1, 1, 0);
            Vec3d v2 = transform(localTransform, 1, 0, 0);
            Vec3d v3 = transform(localTransform, 0, 0, 0);

            emitter.emitTexturedQuad(
                    v0, v1, v2, v3,
                    0f, 1f,
                    1f, 1f,
                    1f, 0f,
                    0f, 0f,
                    colorStart, colorEnd, colorStart, colorEnd);
            emitter.emitTexturedQuad(
                    v0, v1, v2, v3,
                    0f, 1f,
                    1f, 1f,
                    1f, 0f,
                    0f, 0f,
                    colorStartSoft, colorEndSoft, colorStartSoft, colorEndSoft);

            renderer.flush();
        }
    }

    /**
     * Белый прицел вокруг цели. Геометрия лежит в той же обращённой к камере
     * плоскости, что и «Квадрат», поэтому прицел всегда читается поверх NPC.
     * Небольшая детерминированная неровность кольца создаёт эффект ручного
     * наброска, а отдельный широкий additive-pass добавляет мягкое свечение.
     */
    private void renderCrosshairWorld(EventRender.World event, LivingEntity target, float alpha) {
        if (lastTargetPos == null || target == null) {
            return;
        }

        ensureCrosshairTexture();
        if (!crosshairTextureReady) {
            return;
        }

        MatrixStack stack = event.getMatrixStack();
        Camera camera = mc.gameRenderer.getCamera();
        float tickDelta = event.getTicks();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);
        Vec3d cameraPos = camera.getCameraPos();

        double centerX = lastTargetPos.x - cameraPos.x;
        double centerY = lastTargetPos.y - cameraPos.y;
        double centerZ = lastTargetPos.z - cameraPos.z;
        // Компенсация увеличенного прозрачного поля сохраняет размер круга,
        // одновременно оставляя лучам дополнительные 50% длины.
        float size = Math.max(0.9f, Math.min(1.8f, target.getWidth() * 2.0f)) * 1.1f;
        MatrixStack matrices = new MatrixStack();
        matrices.translate(centerX, centerY, centerZ);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        // Тот же вращающийся billboard, что используется режимом «Квадрат».
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationAngle));
        matrices.scale(size, size, size);
        matrices.translate(-0.5f, -0.5f, 0.0f);
        Matrix4f localTransform = matrices.peek().getPositionMatrix();

        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                stack.peek().getPositionMatrix(), mc.gameRenderer.getBasicProjectionMatrix(fov))) {
            MatrixStack identity = new MatrixStack();

            VertexConsumer consumer = renderer.getBuffer(
                    WorldRenderLayers.TEXTURED_QUADS_ADDITIVE_NO_DEPTH(CROSSHAIR_TEXTURE));
            WorldGeometryEmitter emitter = new WorldGeometryEmitter(camera, identity.peek(), consumer);
            Vec3d v0 = transform(localTransform, 0.0f, 1.0f, 0.0f);
            Vec3d v1 = transform(localTransform, 1.0f, 1.0f, 0.0f);
            Vec3d v2 = transform(localTransform, 1.0f, 0.0f, 0.0f);
            Vec3d v3 = transform(localTransform, 0.0f, 0.0f, 0.0f);
            int themeColor = ClientColors.GRADIENT_START.getRGB();
            int tint = ColorUtils.rgba(ColorUtils.red(themeColor), ColorUtils.green(themeColor),
                    ColorUtils.blue(themeColor),
                    Math.max(0, Math.min(255, Math.round(alpha * 255.0f))));
            emitter.emitTexturedQuad(v0, v1, v2, v3,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,
                    tint);

            renderer.flush();
        }
    }

    /** Создаёт прозрачный texture-ассет при первом использовании режима. */
    private static void ensureCrosshairTexture() {
        if (crosshairTextureReady || mc == null) {
            return;
        }
        try {
            int size = 160;
            int center = size / 2;
            NativeImage image = new NativeImage(size, size, true);
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    float dx = x - center + 0.5f;
                    float dy = y - center + 0.5f;
                    float distance = (float) Math.sqrt(dx * dx + dy * dy);
                    float angle = (float) Math.atan2(dy, dx);
                    float handDrawnRadius = 43.2f
                            + 2.0f * (float) Math.sin(angle * 3.0f + 0.4f)
                            + 1.1f * (float) Math.sin(angle * 7.0f - 0.9f);
                    float ringDistance = Math.abs(distance - handDrawnRadius);
                    float alpha = 0.0f;

                    if (ringDistance < 1.65f) {
                        alpha = 0.95f;
                    } else if (ringDistance < 5.5f) {
                        alpha = 0.32f * (1.0f - (ringDistance - 1.65f) / 3.85f);
                    }

                    float absX = Math.abs(dx);
                    float absY = Math.abs(dy);
                    // Лучи стали в 1.5 раза длиннее: длина 27px вместо 18px.
                    // Их core- и glow-толщина совпадает с кольцом.
                    boolean horizontalCore = absY <= 1.65f && absX >= 46.5f && absX <= 77.5f;
                    boolean verticalCore = absX <= 1.65f && absY >= 46.5f && absY <= 77.5f;
                    if (horizontalCore || verticalCore) {
                        float coreDistance = horizontalCore ? absY : absX;
                        alpha = Math.max(alpha, coreDistance < 0.8f ? 0.9f : 0.28f);
                    } else {
                        boolean horizontalGlow = absY <= 5.5f && absX >= 40.5f && absX <= 83.5f;
                        boolean verticalGlow = absX <= 5.5f && absY >= 40.5f && absY <= 83.5f;
                        if (horizontalGlow || verticalGlow) {
                            float glowDistance = horizontalGlow ? absY : absX;
                            alpha = Math.max(alpha, 0.32f * (1.0f - glowDistance / 5.5f));
                        }
                    }

                    int a = Math.max(0, Math.min(255, Math.round(alpha * 255.0f)));
                    image.setColorArgb(x, y, (a << 24) | 0x00FFFFFF);
                }
            }

            NativeImageBackedTexture texture = new NativeImageBackedTexture(
                    () -> "target_crosshair_generated", image);
            mc.getTextureManager().registerTexture(CROSSHAIR_TEXTURE, texture);
            crosshairTextureReady = true;
        } catch (Throwable throwable) {
            System.err.println("[TargetEsp] Failed to create crosshair texture: " + throwable.getMessage());
        }
    }

    /**
     * Вертикально двигающийся лазерный сканер. В отличие от статичного
     * примера ассет здесь остаётся тонким, анимация выполняется перемещением
     * луча по высоте цели.
     */
    private void renderScannerWorld(EventRender.World event, LivingEntity target, float alpha) {
        if (lastTargetPos == null || target == null) {
            return;
        }

        ensureScannerTexture();
        if (!scannerTextureReady) {
            return;
        }

        MatrixStack stack = event.getMatrixStack();
        Camera camera = mc.gameRenderer.getCamera();
        float tickDelta = event.getTicks();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);
        Vec3d cameraPos = camera.getCameraPos();

        double centerX = lastTargetPos.x - cameraPos.x;
        double centerY = lastTargetPos.y - cameraPos.y;
        double centerZ = lastTargetPos.z - cameraPos.z;
        float phase = (System.currentTimeMillis() % 2000L) / 2000.0f;
        float scanProgress = 0.5f + 0.5f * (float) Math.sin(phase * Math.PI * 2.0f - Math.PI * 0.5f);
        // Скан проходит от нижней части тела к верхней, не покидая габариты цели.
        float scanY = (scanProgress - 0.5f) * target.getHeight() * 0.88f;

        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                stack.peek().getPositionMatrix(), mc.gameRenderer.getBasicProjectionMatrix(fov))) {
            MatrixStack identity = new MatrixStack();
            WorldGeometryEmitter emitter = new WorldGeometryEmitter(camera, identity.peek(),
                    renderer.getBuffer(WorldRenderLayers.TEXTURED_QUADS_ADDITIVE_NO_DEPTH(SCANNER_TEXTURE)));

            int themeColor = ClientColors.GRADIENT_START.getRGB();
            int tint = ColorUtils.rgba(ColorUtils.red(themeColor), ColorUtils.green(themeColor),
                    ColorUtils.blue(themeColor),
                    Math.max(0, Math.min(255, Math.round(alpha * 255.0f))));
            // Один непрерывный контур вместо нескольких наложенных колец:
            // так скан выглядит как лазер, который обтекает тело, а не как
            // набор отдельных геометрических фигур.
            float baseThickness = Math.max(0.045f, target.getWidth() * 0.085f);
            int segments = 128;
            double[] profile = scannerBodyProfile(scanProgress, target.getWidth());
            double halfX = profile[0];
            double halfZ = profile[1];
            double corner = profile[2];
            for (int i = 0; i < segments; i++) {
                double[] p0 = scannerBodyPoint(i / (double) segments, halfX, halfZ, corner);
                double[] p1 = scannerBodyPoint((i + 1) / (double) segments, halfX, halfZ, corner);
                double x0 = centerX + p0[0];
                double z0 = centerZ + p0[1];
                double x1 = centerX + p1[0];
                double z1 = centerZ + p1[1];
                double layerY = centerY + (scanProgress - 0.5f) * target.getHeight() * 0.88f;
                double yTop = layerY + baseThickness * 0.5f;
                double yBottom = layerY - baseThickness * 0.5f;

                Vec3d v0 = new Vec3d(x0, yTop, z0);
                Vec3d v1 = new Vec3d(x1, yTop, z1);
                Vec3d v2 = new Vec3d(x1, yBottom, z1);
                Vec3d v3 = new Vec3d(x0, yBottom, z0);
                emitter.emitTexturedQuad(v0, v1, v2, v3,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 0.0f,
                        tint);
            }
            renderer.flush();
        }
    }

    /** Возвращает плавное горизонтальное сечение тела на высоте сканера. */
    private static double[] scannerBodyProfile(double progress, float entityWidth) {
        double p = Math.max(0.0, Math.min(1.0, progress));
        double width = Math.max(0.6, entityWidth);
        double legToTorso = smoothstep(p / 0.30);
        double shoulderIn = smoothstep((p - 0.52) / 0.18);
        double shoulderOut = smoothstep((p - 0.78) / 0.16);
        double shoulder = shoulderIn * (1.0 - shoulderOut);
        double head = smoothstep((p - 0.80) / 0.20);

        double halfX = width * (0.56 + 0.18 * legToTorso + 0.10 * shoulder);
        double halfZ = width * (0.46 + 0.13 * legToTorso + 0.035 * shoulder);
        halfX = halfX * (1.0 - head) + width * 0.53 * head;
        halfZ = halfZ * (1.0 - head) + width * 0.48 * head;
        double corner = Math.max(0.08, Math.min(0.20, Math.min(halfX, halfZ) * 0.19));
        return new double[]{halfX, halfZ, corner};
    }

    private static double smoothstep(double value) {
        double t = Math.max(0.0, Math.min(1.0, value));
        return t * t * (3.0 - 2.0 * t);
    }

    /** Сглаженная точка телесного сечения, обходящая его по замкнутому контуру. */
    private static double[] scannerBodyPoint(double progress, double halfX, double halfZ, double corner) {
        double angle = Math.PI * 2.0 * Math.max(0.0, Math.min(1.0, progress));
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        // Показатель 3.2 даёт плавные почти плоские стороны без квадратных
        // углов. Передняя/задняя часть чуть глубже, боковые части чуть шире,
        // поэтому контур воспринимается как сечение тела, а не как овал.
        double exponent = 3.2;
        double x = Math.copySign(halfX * Math.pow(Math.abs(cos), 2.0 / exponent), cos);
        double z = Math.copySign(halfZ * Math.pow(Math.abs(sin), 2.0 / exponent), sin);
        double bodyRelief = 1.0 + 0.022 * Math.sin(angle * 2.0) - 0.016 * Math.cos(angle * 4.0);
        double sideRelief = 1.0 + 0.018 * Math.abs(sin) - 0.012 * Math.abs(cos);
        x *= bodyRelief;
        z *= sideRelief;
        return new double[]{x, z};
    }

    /** Создаёт runtime-ассет лазерной ленты с ярким ядром и мягким ореолом. */
    private static void ensureScannerTexture() {
        if (scannerTextureReady || mc == null) {
            return;
        }
        try {
            // Стандартная UV-развёртка player/NPC: мы используем только
            // переднюю часть корпуса, чтобы полоса не дублировалась на руках.
            // Четырёхкратное разрешение даёт тонкую сглаженную полоску на
            // UV-поверхности модели вместо заметной пиксельной заливки.
            int width = 256;
            int height = 256;
            NativeImage image = new NativeImage(width, height, true);
            scannerTexture = new NativeImageBackedTexture(() -> "target_scanner_generated", image);
            mc.getTextureManager().registerTexture(SCANNER_TEXTURE, scannerTexture);
            scannerTextureReady = true;
            updateScannerTexture(0.0f);
        } catch (Throwable throwable) {
            System.err.println("[TargetEsp] Failed to create scanner texture: " + throwable.getMessage());
        }
    }

    private static void updateScannerTexture(float progress) {
        if (!scannerTextureReady || scannerTexture == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - scannerTextureUploadMs < 16L) {
            return;
        }
        scannerTextureUploadMs = now;

        NativeImage image = scannerTexture.getImage();
        if (image == null) {
            return;
        }
        int w = image.getWidth();
        int uv = w / 64;
        float scan = Math.max(0.0f, Math.min(1.0f, progress));

        float worldY = scan * 2.0f;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < w; y++) {
                image.setColorArgb(x, y, 0);
            }
        }

        paintScanLine(image, uv, worldY, 1.5f, 2.0f,
                new int[][]{{0, 8, 8, 16}, {8, 16, 8, 16}, {16, 24, 8, 16}, {24, 32, 8, 16}});
        paintScanLine(image, uv, worldY, 0.75f, 1.5f,
                new int[][]{{16, 20, 20, 32}, {20, 28, 20, 32}, {28, 32, 20, 32}, {32, 40, 20, 32}});
        paintScanLine(image, uv, worldY, 0.75f, 1.5f,
                new int[][]{{40, 44, 20, 32}, {44, 48, 20, 32}, {48, 52, 20, 32}, {52, 56, 20, 32}});
        paintScanLine(image, uv, worldY, 0.75f, 1.5f,
                new int[][]{{32, 36, 52, 64}, {36, 40, 52, 64}, {40, 44, 52, 64}, {44, 48, 52, 64}});
        paintScanLine(image, uv, worldY, 0.0f, 0.75f,
                new int[][]{{0, 4, 20, 32}, {4, 8, 20, 32}, {8, 12, 20, 32}, {12, 16, 20, 32}});
        paintScanLine(image, uv, worldY, 0.0f, 0.75f,
                new int[][]{{16, 20, 52, 64}, {20, 24, 52, 64}, {24, 28, 52, 64}, {28, 32, 52, 64}});

        scannerTexture.upload();
    }

    private static void paintScanLine(NativeImage image, int uv, float worldY,
                                      float bodyBottom, float bodyTop, int[][] faces) {
        if (worldY < bodyBottom || worldY > bodyTop) {
            return;
        }
        float localT = (worldY - bodyBottom) / Math.max(0.001f, bodyTop - bodyBottom);
        for (int[] face : faces) {
            int u0 = face[0] * uv;
            int u1 = face[1] * uv;
            int v0 = face[2] * uv;
            int v1 = face[3] * uv;
            float lineV = v1 - localT * (v1 - v0);
            for (int y = v0; y < v1; y++) {
                float dist = (y + 0.5f - lineV) / (float) uv;
                float core = Math.abs(dist) <= 0.25f ? 1.0f : 0.0f;
                float glow = (dist > 0.25f && dist <= 2.0f)
                        ? (1.0f - (dist - 0.25f) / 1.75f) * 0.5f : 0.0f;
                int alpha = Math.max(0, Math.min(255, Math.round(Math.max(core, glow) * 255.0f)));
                if (alpha == 0) {
                    continue;
                }
                for (int x = u0; x < u1; x++) {
                    image.setColorArgb(x, y, (alpha << 24) | 0x00FFFFFF);
                }
            }
        }
    }

    private static float scannerWaveProgress() {
        // Быстрый ping-pong с Ease In Out: новый проход начинается сразу
        // после завершения предыдущего, без остановки на краях.
        float phase = (System.currentTimeMillis() % 1600L) / 1600.0f;
        float linear = phase < 0.5f ? phase * 2.0f : (1.0f - phase) * 2.0f;
        return linear * linear * (3.0f - 2.0f * linear);
    }

    public static boolean isScannerTarget(LivingEntity entity) {
        if (entity == null || !targetEspType.is("Сканер")) {
            return false;
        }
        if (entity == scannerTarget && scannerAlpha > 0.01f) {
            return true;
        }
        var manager = LightClient.getFunctionManager();
        if (manager == null) {
            return false;
        }
        if (isAimBotActive() && AimBot.getTarget() == entity) {
            return true;
        }
        if (isAuraEspActive() && AuraModule.getTarget() == entity) {
            return true;
        }
        return isThrowableAimEspActive() && ThrowableAim.target == entity;
    }

    public static float getScannerAlpha() {
        return scannerAlpha;
    }

    public static Identifier getScannerTexture() {
        return SCANNER_TEXTURE;
    }

    public static int getScannerTint() {
        return ClientColors.GRADIENT_START.getRGB();
    }

    private Identifier getImageIdentifier() {
        if (imageType.is("Квадрат")) {
            return Identifier.of("light", "images/targetesp/target2.png");
        }
        return Identifier.of("light", "images/targetesp/target1.png");
    }

    private Vec3d getTargetCenter(LivingEntity target) {
        float tickDelta = mc.getRenderTickCounter().getTickProgress(true);

        double x = lerp(target.lastX, target.getX(), tickDelta);
        double y = lerp(target.lastY, target.getY(), tickDelta) + target.getHeight() * 0.5f;
        double z = lerp(target.lastZ, target.getZ(), tickDelta);
        return new Vec3d(x, y, z);
    }

    private float getHurtFlash(LivingEntity target) {
        if (target == null) {
            return 0f;
        }
        if (target != lastHurtTarget) {
            lastHurtTarget = target;
            lastHurtTime = 0;
            lastHurtAt = 0L;
        }

        int currentHurt = target.hurtTime;
        if (currentHurt > lastHurtTime) {
            lastHurtAt = System.currentTimeMillis();
        }
        lastHurtTime = currentHurt;

        if (lastHurtAt <= 0L) {
            return 0f;
        }
        long elapsed = System.currentTimeMillis() - lastHurtAt;
        long total = HURT_FLASH_HALF_MS * 2L;
        if (elapsed < 0L || elapsed > total) {
            return 0f;
        }

        float t = elapsed / (float) total;
        float phase = t < 0.5f ? t / 0.5f : 1f - ((t - 0.5f) / 0.5f);
        return smoothstep(smoothstep(phase));
    }

    private int applyDamageTint(int baseColor, float factor) {
        if (factor <= 0f) {
            return baseColor;
        }
        int a = (baseColor >>> 24) & 0xFF;
        int r = (baseColor >>> 16) & 0xFF;
        int g = (baseColor >>> 8) & 0xFF;
        int b = baseColor & 0xFF;
        int nr = Math.min(255, (int) (r + (255 - r) * factor));
        int ng = Math.max(0, (int) (g * (1f - factor)));
        int nb = Math.max(0, (int) (b * (1f - factor)));
        return (a << 24) | (nr << 16) | (ng << 8) | nb;
    }

    private float getHurtPulseScale(LivingEntity target) {
        if (target == null) return 1.0f;

        int currentHurt = target.hurtTime;
        if (currentHurt > lastHurtPulseHurtTime && currentHurt > 0) {
            hurtPulseAnimStartMs = System.currentTimeMillis();
            hurtPulseIntensity = Math.min(currentHurt / 11f, 1.0f);
        }
        lastHurtPulseHurtTime = currentHurt;

        if (hurtPulseAnimStartMs == 0) return 1.0f;

        long elapsed = System.currentTimeMillis() - hurtPulseAnimStartMs;
        long riseEnd = HURT_PULSE_RISE_MS;
        long holdEnd = riseEnd + HURT_PULSE_HOLD_MS;
        long total = holdEnd + HURT_PULSE_FALL_MS;

        if (elapsed >= total) {
            hurtPulseAnimStartMs = 0L;
            return 1.0f;
        }

        if (elapsed < riseEnd) {
            float t = elapsed / (float) HURT_PULSE_RISE_MS;
            return 1.0f + (HURT_PULSE_MAX_SCALE - 1.0f) * t * hurtPulseIntensity;
        } else if (elapsed < holdEnd) {
            return 1.0f + (HURT_PULSE_MAX_SCALE - 1.0f) * hurtPulseIntensity;
        } else {
            float t = (elapsed - holdEnd) / (float) HURT_PULSE_FALL_MS;
            float animT = 1.0f - t;
            return 1.0f + (HURT_PULSE_MAX_SCALE - 1.0f) * animT * hurtPulseIntensity;
        }
    }

    private float smoothstep(float t) {
        if (t <= 0f) {
            return 0f;
        }
        if (t >= 1f) {
            return 1f;
        }
        return t * t * (3f - 2f * t);
    }

    private static double lerp(double start, double end, float t) {
        return start + (end - start) * t;
    }

    public void renderCircleParticles(EventRender.World event, LivingEntity target, float alpha) {

        float espLength = 5.5f;
        float factor = factorDush.get();
        float particleDensity = 10;

        MatrixStack stack = event.getMatrixStack();
        Camera camera = mc.gameRenderer.getCamera();
        float tickDelta = event.getTicks();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);

        int baseAlpha = Math.max(0, Math.min(255, Math.round(alpha * 255f)));
        int rFirst = ClientColors.GRADIENT_START.getRGB() >> 16 & 0xFF;
        int gFirst = ClientColors.GRADIENT_START.getRGB() >> 8 & 0xFF;
        int bFirst = ClientColors.GRADIENT_START.getRGB() & 0xFF;
        int rTwin = ClientColors.GRADIENT_END.getRGB() >> 16 & 0xFF;
        int gTwin = ClientColors.GRADIENT_END.getRGB() >> 8 & 0xFF;
        int bTwin = ClientColors.GRADIENT_END.getRGB() & 0xFF;

        float time = (float) (System.currentTimeMillis() % 2000) / 2000.0f;
        float phaseShift = (float) (2.0 * Math.PI / 4.0);

        float fixedRadius = target.getWidth() * 0.8f;
        float verticalOffset = (float) (Math.sin(time * Math.PI * 2) * 0.5);
        float rotationAngle = time * (float) Math.PI * 2;

        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                stack.peek().getPositionMatrix(), mc.gameRenderer.getBasicProjectionMatrix(fov))) {

            VertexConsumer consumer = renderer
                    .getBuffer(WorldRenderLayers.TEXTURED_QUADS_ADDITIVE_NO_DEPTH(SOUL_TEXTURE));
            MatrixStack identity = new MatrixStack();
            WorldGeometryEmitter emitter = new WorldGeometryEmitter(camera, identity.peek(), consumer);

            double tPosX = lerp(target.lastX, target.getX(), tickDelta) - camera.getCameraPos().x;
            double tPosY = lerp(target.lastY, target.getY(), tickDelta) - camera.getCameraPos().y;
            double tPosZ = lerp(target.lastZ, target.getZ(), tickDelta) - camera.getCameraPos().z;
            float iAge = (float) lerp(target.age - 1, target.age, tickDelta);

            for (int j = 0; j < 4; j++) {
                float baseAngle = (j * 90f + rotationAngle * 180f / (float) Math.PI) % 360f;

                float colorLerp = (float) (Math.sin(2.0 * Math.PI * time + j * phaseShift) * 0.5 + 0.5);

                int r = (int) (rFirst + (rTwin - rFirst) * colorLerp);
                int g = (int) (gFirst + (gTwin - gFirst) * colorLerp);
                int b = (int) (bFirst + (bTwin - bFirst) * colorLerp);

                int particleCount = (int) (espLength * particleDensity);

                for (int i = 0; i < particleCount; i++) {
                    float offset = (float) i / particleCount;
                    double radians = Math.toRadians((baseAngle + (offset * espLength + iAge) * 9.5f) % 360);
                    double sinQuad = verticalOffset;

                    float alphaFactor = 1.0f - offset * 0.3f;
                    int gradientAlpha = (int) (baseAlpha * alphaFactor);

                    float scale = 1 * 0.11f;

                    MatrixStack matrices = new MatrixStack();
                    matrices.translate(
                            tPosX + Math.cos(radians) * fixedRadius,
                            tPosY + 1 + sinQuad,
                            tPosZ + Math.sin(radians) * fixedRadius);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

                    int color = ColorUtils.rgba(r, g, b, gradientAlpha);
                    Matrix4f localTransform = matrices.peek().getPositionMatrix();
                    Vec3d v0 = transform(localTransform, -scale, scale, 0);
                    Vec3d v1 = transform(localTransform, scale, scale, 0);
                    Vec3d v2 = transform(localTransform, scale, -scale, 0);
                    Vec3d v3 = transform(localTransform, -scale, -scale, 0);

                    emitter.emitTexturedQuad(
                            v0, v1, v2, v3,
                            0f, 1f,
                            1f, 1f,
                            1f, 0f,
                            0f, 0f,
                            color);
                }
            }

            renderer.flush();
        }
    }

    private void renderGhostsWorld(EventRender.World event, Entity target, float alpha) {
        if (!shaderType.is("Призраки"))
            return;

        float espLength = dlinaDush.get();
        float factor = factorDush.get();
        float shaking = 1.32f;
        float amplitude = 3f;

        MatrixStack stack = event.getMatrixStack();
        Camera camera = mc.gameRenderer.getCamera();
        float tickDelta = event.getTicks();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);
        double timeSeconds = System.currentTimeMillis() * 0.001;
        double iAge = timeSeconds * 20.0;

        int baseAlpha = Math.max(0, Math.min(255, Math.round(alpha * 255f)));
        int rFirst = ClientColors.GRADIENT_START.getRGB() >> 16 & 0xFF;
        int gFirst = ClientColors.GRADIENT_START.getRGB() >> 8 & 0xFF;
        int bFirst = ClientColors.GRADIENT_START.getRGB() & 0xFF;
        int rTwin = ClientColors.GRADIENT_END.getRGB() >> 16 & 0xFF;
        int gTwin = ClientColors.GRADIENT_END.getRGB() >> 8 & 0xFF;
        int bTwin = ClientColors.GRADIENT_END.getRGB() & 0xFF;

        float time = (float) (System.currentTimeMillis() % 2000) / 2000.0f;
        float phaseShift = (float) (2.0 * Math.PI / 4.0);
        float particleDensity = particleDensityDush.get();
        float[] angles = {45f, 135f, 225f, 315f};
        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                stack.peek().getPositionMatrix(), mc.gameRenderer.getBasicProjectionMatrix(fov))) {
            VertexConsumer consumer = renderer
                    .getBuffer(WorldRenderLayers.TEXTURED_QUADS_ADDITIVE_NO_DEPTH(SOUL_TEXTURE));
            MatrixStack identity = new MatrixStack();
            WorldGeometryEmitter emitter = new WorldGeometryEmitter(camera, identity.peek(), consumer);

            double tPosX = lerp(target.lastX, target.getX(), tickDelta) - camera.getCameraPos().x;
            double tPosY = lerp(target.lastY, target.getY(), tickDelta) - camera.getCameraPos().y;
            double tPosZ = lerp(target.lastZ, target.getZ(), tickDelta) - camera.getCameraPos().z;

            for (int j = 0; j < 4; j++) {
                float colorLerp = (float) (Math.sin(2.0 * Math.PI * time + j * phaseShift) * 0.5 + 0.5);

                int r = (int) (rFirst + (rTwin - rFirst) * colorLerp);
                int g = (int) (gFirst + (gTwin - gFirst) * colorLerp);
                int b = (int) (bFirst + (bTwin - bFirst) * colorLerp);
                int particleCount = (int) (espLength * particleDensity);

                for (int i = 0; i < particleCount; i++) {
                    float offset = (float) i / particleCount;
                    double radians = Math.toRadians((angles[j] + (offset * espLength + iAge) * factor) % 360);
                    double sinQuad = Math
                            .sin(Math.toRadians(iAge * 2.5 + offset * espLength * 2.0 + j * 90) * amplitude) / shaking;

                    float alphaFactor = 1.0f - offset * 0.3f;
                    int gradientAlpha = (int) (baseAlpha * alphaFactor);
                    float scale = sizeDush.get();

                    MatrixStack matrices = new MatrixStack();
                    matrices.translate(
                            tPosX + Math.cos(radians) * target.getWidth(),
                            tPosY + 1 + sinQuad,
                            tPosZ + Math.sin(radians) * target.getWidth());
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

                    int color = ColorUtils.rgba(r, g, b, gradientAlpha);
                    Matrix4f localTransform = matrices.peek().getPositionMatrix();
                    Vec3d v0 = transform(localTransform, -scale, scale, 0);
                    Vec3d v1 = transform(localTransform, scale, scale, 0);
                    Vec3d v2 = transform(localTransform, scale, -scale, 0);
                    Vec3d v3 = transform(localTransform, -scale, -scale, 0);

                    emitter.emitTexturedQuad(
                            v0, v1, v2, v3,
                            0f, 1f,
                            1f, 1f,
                            1f, 0f,
                            0f, 0f,
                            color);
                }
            }

            renderer.flush();
        }
    }

    private void renderPrivWorld(EventRender.World event, Entity target, float alpha) {
        if (!shaderType.is("Души"))
            return;

        float espLength = dlinaDush.get();
        float factor = factorDush.get();
        float shaking = 1.32f;
        float amplitude = 3f;

        MatrixStack stack = event.getMatrixStack();
        Camera camera = mc.gameRenderer.getCamera();
        float tickDelta = event.getTicks();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);
        double tPosX = lerp(target.lastX, target.getX(), tickDelta) - camera.getCameraPos().x;
        double tPosY = lerp(target.lastY, target.getY(), tickDelta) - camera.getCameraPos().y;
        double tPosZ = lerp(target.lastZ, target.getZ(), tickDelta) - camera.getCameraPos().z;
        float iAge = (float) lerp(target.age - 1, target.age, tickDelta);

        int firstColor = ClientColors.GRADIENT_START.getRGB();
        int secondColor = ClientColors.GRADIENT_END.getRGB();
        int rFirst = ColorUtils.red(firstColor);
        int gFirst = ColorUtils.green(firstColor);
        int bFirst = ColorUtils.blue(firstColor);
        int rSecond = ColorUtils.red(secondColor);
        int gSecond = ColorUtils.green(secondColor);
        int bSecond = ColorUtils.blue(secondColor);
        int rDiff = rSecond - rFirst;
        int gDiff = gSecond - gFirst;
        int bDiff = bSecond - bFirst;

        float time = (System.currentTimeMillis() % PRIV_COLOR_CYCLE_MS) / (float) PRIV_COLOR_CYCLE_MS;
        float particleDensity = particleDensityDush.get();
        int particleCount = Math.max(1, (int) (espLength * particleDensity));
        float sizeBase = sizeDush.get();
        double targetWidth = target.getWidth();
        float invShaking = shaking == 0f ? 0f : 1f / shaking;
        float invParticleCount = 1f / particleCount;
        double orbitAngleStep = Math.toRadians(espLength * factor * invParticleCount);
        double waveAngleStep = Math.toRadians(espLength * 2.0f * invParticleCount) * amplitude;
        float alphaStep = alpha * 0.15f * invParticleCount;
        float scaleStep = sizeBase * 0.75f * invParticleCount;
        float baseScale = sizeBase * 0.25f;
        float flash = target instanceof LivingEntity living ? getHurtFlash(living) : 0f;

        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                stack.peek().getPositionMatrix(), mc.gameRenderer.getBasicProjectionMatrix(fov))) {
            VertexConsumer consumer = renderer
                    .getBuffer(WorldRenderLayers.TEXTURED_QUADS_ADDITIVE_NO_DEPTH(SOUL_TEXTURE));
            MatrixStack identity = new MatrixStack();
            WorldGeometryEmitter emitter = new WorldGeometryEmitter(camera, identity.peek(), consumer);

            for (int j = 0; j < PRIV_BASE_ANGLES.length; j++) {
                float colorLerp = (float) (Math.sin((Math.PI * 2.0) * time + j * (Math.PI * 0.5)) * 0.5 + 0.5);
                int r = rFirst + (int) (rDiff * colorLerp);
                int g = gFirst + (int) (gDiff * colorLerp);
                int b = bFirst + (int) (bDiff * colorLerp);

                int tinted = applyDamageTint(ColorUtils.rgba(r, g, b, 255), flash);
                r = ColorUtils.red(tinted);
                g = ColorUtils.green(tinted);
                b = ColorUtils.blue(tinted);

                double orbitAngle = Math.toRadians(PRIV_BASE_ANGLES[j] + iAge * factor);
                double waveAngle = Math.toRadians(iAge * 2.5f + j * 90f) * amplitude;
                float alphaFactor = alpha;
                float scale = baseScale;

                for (int i = 0; i < particleCount; i++) {
                    double sinQuad = Math.sin(waveAngle) * invShaking;
                    int particleAlpha = Math.max(0, Math.min(255, Math.round(alphaFactor * 255f)));
                    int color = ColorUtils.rgba(r, g, b, particleAlpha);

                    MatrixStack matrices = new MatrixStack();
                    matrices.translate(
                            tPosX + Math.cos(orbitAngle) * targetWidth,
                            tPosY + 1 + sinQuad,
                            tPosZ + Math.sin(orbitAngle) * targetWidth);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

                    Matrix4f localTransform = matrices.peek().getPositionMatrix();
                    Vec3d v0 = transform(localTransform, -scale, scale, 0);
                    Vec3d v1 = transform(localTransform, scale, scale, 0);
                    Vec3d v2 = transform(localTransform, scale, -scale, 0);
                    Vec3d v3 = transform(localTransform, -scale, -scale, 0);

                    emitter.emitTexturedQuad(
                            v0, v1, v2, v3,
                            0f, 1f,
                            1f, 1f,
                            1f, 0f,
                            0f, 0f,
                            color);

                    orbitAngle += orbitAngleStep;
                    waveAngle += waveAngleStep;
                    alphaFactor = Math.max(0f, alphaFactor - alphaStep);
                    scale += scaleStep;
                }
            }

            renderer.flush();
        }
    }

    private void renderKolcoWorld(EventRender.World event, Entity target, float alpha) {
        if (!shaderType.is("Колечко") || mc.world == null) {
            return;
        }

        renderAnimatedRingPasses(event, target, alpha);
    }

    private void renderAnimatedRingPasses(EventRender.World event, Entity target, float alpha) {
        if (target == null || mc.world == null) {
            kolcoStars.clear();
            return;
        }

        MatrixStack stack = event.getMatrixStack();
        Camera camera = mc.gameRenderer.getCamera();
        float tickDelta = event.getTicks();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);

        double timeSeconds = System.currentTimeMillis() * 0.001;
        double cs = timeSeconds * 2.2;
        double sinAnim = (Math.sin(cs) + 1.0) * 0.5;
        double sinAnimNext = (Math.sin(cs + 0.45) + 1.0) * 0.5;

        double baseY = lerp(target.lastY, target.getY(), tickDelta);
        double y = baseY + sinAnim * target.getHeight();
        double nextY = baseY + sinAnimNext * target.getHeight();

        double baseX = lerp(target.lastX, target.getX(), tickDelta);
        double baseZ = lerp(target.lastZ, target.getZ(), tickDelta);
        double radius = target.getWidth() * 0.8;
        int segments = 128;

        int baseStart = ClientColors.GRADIENT_START.getRGB();
        int baseEnd = ClientColors.GRADIENT_END.getRGB();
        int alphaValue = Math.max(0, Math.min(255, Math.round(alpha * 255f)));
        float time = (float) (System.currentTimeMillis() % 2500) / 2500.0f;

        Vec3d cameraPos = camera.getCameraPos();
        double tX = baseX - cameraPos.x;
        double tZ = baseZ - cameraPos.z;
        double tY = y - cameraPos.y;
        double tNextY = nextY - cameraPos.y;

        double outlineRadius = radius + 0.003;
        int outlineAlpha = Math.max(0, Math.min(255, Math.round(alpha * 255f)));
        int glowAlpha = Math.max(0, Math.min(255, Math.round(alpha * 240f)));
        int endColor = ColorUtils.rgba(
                ColorUtils.red(baseEnd),
                ColorUtils.green(baseEnd),
                ColorUtils.blue(baseEnd),
                outlineAlpha);
        int endGlow = ColorUtils.rgba(
                ColorUtils.red(baseEnd),
                ColorUtils.green(baseEnd),
                ColorUtils.blue(baseEnd),
                glowAlpha);

        Vec3d[] quad0 = new Vec3d[segments];
        Vec3d[] quad1 = new Vec3d[segments];
        Vec3d[] quad2 = new Vec3d[segments];
        Vec3d[] quad3 = new Vec3d[segments];
        Vec3d[] outlinePoints = new Vec3d[segments];
        int[] topColors = new int[segments];
        int[] lineColors = new int[segments];
        int[] glowColors = new int[segments];

        for (int i = 0; i < segments; i++) {
            double a0 = i * (Math.PI * 2.0) / segments;
            double a1 = (i + 1) * (Math.PI * 2.0) / segments;
            double x0 = tX + Math.cos(a0) * radius;
            double z0 = tZ + Math.sin(a0) * radius;
            double x1 = tX + Math.cos(a1) * radius;
            double z1 = tZ + Math.sin(a1) * radius;

            float colorLerp = (float) (Math.sin(a0 + 2.0 * Math.PI * time) * 0.5 + 0.5);
            int r = (int) (ColorUtils.red(baseStart)
                    + (ColorUtils.red(baseEnd) - ColorUtils.red(baseStart)) * colorLerp);
            int g = (int) (ColorUtils.green(baseStart)
                    + (ColorUtils.green(baseEnd) - ColorUtils.green(baseStart)) * colorLerp);
            int b = (int) (ColorUtils.blue(baseStart)
                    + (ColorUtils.blue(baseEnd) - ColorUtils.blue(baseStart)) * colorLerp);

            quad0[i] = new Vec3d(x0, tNextY, z0);
            quad1[i] = new Vec3d(x1, tNextY, z1);
            quad2[i] = new Vec3d(x1, tY, z1);
            quad3[i] = new Vec3d(x0, tY, z0);
            outlinePoints[i] = new Vec3d(tX + Math.cos(a0) * outlineRadius, tNextY, tZ + Math.sin(a0) * outlineRadius);
            topColors[i] = ColorUtils.rgba(r, g, b, alphaValue);
            lineColors[i] = ColorUtils.rgba(r, g, b, outlineAlpha);
            glowColors[i] = ColorUtils.rgba(r, g, b, glowAlpha);
        }

        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                stack.peek().getPositionMatrix(), mc.gameRenderer.getBasicProjectionMatrix(fov))) {
            MatrixStack identity = new MatrixStack();

            updateKolcoStars(baseX, y, baseZ, radius, baseStart, baseEnd, alpha);
            emitKolcoStars(renderer, camera, tickDelta);

            VertexConsumer consumer = renderer.getBuffer(WorldRenderLayers.POSITION_COLOR_QUADS_ADDITIVE());
            WorldGeometryEmitter emitter = new WorldGeometryEmitter(camera, identity.peek(), consumer);
            for (int i = 0; i < segments; i++) {
                int topColor = topColors[i];
                int bottomColor = ColorUtils.injectAlpha(topColor, 0);
                emitter.emitQuad(quad0[i], quad1[i], quad2[i], quad3[i], topColor, topColor, bottomColor, bottomColor);
            }

            VertexConsumer lineConsumer = renderer.getBuffer(WorldRenderLayers.LINES(1.0));
            WorldGeometryEmitter lineEmitter = new WorldGeometryEmitter(camera, identity.peek(), lineConsumer);
            for (int i = 1; i < segments; i++) {
                lineEmitter.emitLine(outlinePoints[i - 1], outlinePoints[i], lineColors[i]);
            }
            if (segments > 1) {
                lineEmitter.emitLine(outlinePoints[segments - 1], outlinePoints[0], endColor);
            }

            VertexConsumer glowLineConsumer = renderer.getBuffer(WorldRenderLayers.LINES_ADDITIVE_NO_DEPTH(1.5));
            WorldGeometryEmitter glowLineEmitter = new WorldGeometryEmitter(camera, identity.peek(), glowLineConsumer);
            for (int i = 1; i < segments; i++) {
                glowLineEmitter.emitLine(outlinePoints[i - 1], outlinePoints[i], glowColors[i]);
            }
            if (segments > 1) {
                glowLineEmitter.emitLine(outlinePoints[segments - 1], outlinePoints[0], endGlow);
            }

            renderer.flush();
        }
    }

    private void updateKolcoStars(double baseX, double ringY, double baseZ, double radius, int baseStart, int baseEnd, float alpha) {
        long now = System.currentTimeMillis();
        Iterator<KolcoStarParticle> iterator = kolcoStars.iterator();
        while (iterator.hasNext()) {
            KolcoStarParticle particle = iterator.next();
            if (now - particle.spawnMs >= KOLCO_STAR_LIFETIME_MS) {
                iterator.remove();
                continue;
            }
            particle.tick();
        }

        if (now - lastKolcoStarSpawnMs < KOLCO_STAR_SPAWN_INTERVAL_MS || alpha <= 0.05f) {
            return;
        }

        lastKolcoStarSpawnMs = now;
        int spawnCount = 2 + KOLCO_RANDOM.nextInt(2);
        for (int i = 0; i < spawnCount; i++) {
            double angle = KOLCO_RANDOM.nextDouble() * Math.PI * 2.0;
            double spread = radius * (0.78 + KOLCO_RANDOM.nextDouble() * 0.42);
            double x = baseX + Math.cos(angle) * spread;
            double z = baseZ + Math.sin(angle) * spread;
            double y = ringY - 0.08 - KOLCO_RANDOM.nextDouble() * 0.06;
            double motionX = (KOLCO_RANDOM.nextDouble() - 0.5) * 0.006;
            double motionZ = (KOLCO_RANDOM.nextDouble() - 0.5) * 0.006;
            double motionY = -0.00028 - KOLCO_RANDOM.nextDouble() * 0.0008;
            float size = 0.075f + KOLCO_RANDOM.nextFloat() * 0.025f;
            float colorLerp = KOLCO_RANDOM.nextFloat();
            int r = (int) (ColorUtils.red(baseStart) + (ColorUtils.red(baseEnd) - ColorUtils.red(baseStart)) * colorLerp);
            int g = (int) (ColorUtils.green(baseStart) + (ColorUtils.green(baseEnd) - ColorUtils.green(baseStart)) * colorLerp);
            int b = (int) (ColorUtils.blue(baseStart) + (ColorUtils.blue(baseEnd) - ColorUtils.blue(baseStart)) * colorLerp);
            int color = ColorUtils.rgba(r, g, b, Math.max(0, Math.min(255, Math.round(alpha * 255f))));
            kolcoStars.add(new KolcoStarParticle(x, y, z, motionX, motionY, motionZ, size, color, now,
                    KOLCO_RANDOM.nextFloat() * 360f, (KOLCO_RANDOM.nextFloat() - 0.5f) * 360f));
        }
    }

    private void emitKolcoStars(WorldRenderer renderer, Camera camera, float tickDelta) {
        if (kolcoStars.isEmpty()) {
            return;
        }

        VertexConsumer consumer = renderer.getBuffer(WorldRenderLayers.TEXTURED_QUADS_ADDITIVE_NO_DEPTH(STAR_TEXTURE));
        MatrixStack identity = new MatrixStack();
        WorldGeometryEmitter emitter = new WorldGeometryEmitter(camera, identity.peek(), consumer);
        Vec3d cameraPos = camera.getCameraPos();

        long now = System.currentTimeMillis();
        for (KolcoStarParticle particle : kolcoStars) {
            float age = Math.min(1f, (now - particle.spawnMs) / (float) KOLCO_STAR_LIFETIME_MS);
            float fade = 1f - smoothstep(age);
            int baseAlpha = (particle.color >>> 24) & 0xFF;
            int color = ColorUtils.injectAlpha(particle.color, Math.max(0, Math.min(255, Math.round(baseAlpha * fade))));
            if (((color >>> 24) & 0xFF) <= 2) {
                continue;
            }

            double x = lerp(particle.prevX, particle.x, tickDelta) - cameraPos.x;
            double y = lerp(particle.prevY, particle.y, tickDelta) - cameraPos.y;
            double z = lerp(particle.prevZ, particle.z, tickDelta) - cameraPos.z;
            float size = particle.size * (1 + 1f * fade);
            float rotation = particle.rotation + (now - particle.spawnMs) * 0.001f * particle.rotationSpeed;
            MatrixStack matrices = new MatrixStack();
            matrices.translate(x, y, z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));

            Matrix4f localTransform = matrices.peek().getPositionMatrix();
            Vec3d v0 = transform(localTransform, -size, size, 0);
            Vec3d v1 = transform(localTransform, size, size, 0);
            Vec3d v2 = transform(localTransform, size, -size, 0);
            Vec3d v3 = transform(localTransform, -size, -size, 0);

            emitter.emitTexturedQuad(
                    v0, v1, v2, v3,
                    1f, 0f,
                    0f, 0f,
                    0f, 1f,
                    1f, 1f,
                    color);
        }
    }

    private static final class KolcoStarParticle {
        private double x, y, z;
        private double prevX, prevY, prevZ;
        private double motionX, motionY, motionZ;
        private final float size;
        private final int color;
        private final long spawnMs;
        private final float rotation;
        private final float rotationSpeed;

        private KolcoStarParticle(double x, double y, double z, double motionX, double motionY, double motionZ,
                                  float size, int color, long spawnMs, float rotation, float rotationSpeed) {
            this.x = this.prevX = x;
            this.y = this.prevY = y;
            this.z = this.prevZ = z;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.size = size;
            this.color = color;
            this.spawnMs = spawnMs;
            this.rotation = rotation;
            this.rotationSpeed = rotationSpeed;
        }

        private void tick() {
            prevX = x;
            prevY = y;
            prevZ = z;
            x += motionX;
            y += motionY;
            z += motionZ;
            motionX *= 0.92;
            motionZ *= 0.92;
            motionY -= 0.00001;

        }
    }

    private void renderCircleParticlesWorld(EventRender.World event, Entity target, float alpha) {
        if (!shaderType.is("Души 2"))
            return;

        float espLength = dlinaDush.get();
        float factor = factorDush.get();
        float shaking = 1.1f;
        float amplitude = 2.2f;

        MatrixStack stack = event.getMatrixStack();
        Camera camera = mc.gameRenderer.getCamera();
        float tickDelta = event.getTicks();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);
        double tPosX = lerp(target.lastX, target.getX(), tickDelta) - camera.getCameraPos().x;
        double tPosY = lerp(target.lastY, target.getY(), tickDelta) - camera.getCameraPos().y;
        double tPosZ = lerp(target.lastZ, target.getZ(), tickDelta) - camera.getCameraPos().z;
        float iAge = (float) lerp(target.age - 1, target.age, tickDelta);

        int firstColor = ClientColors.GRADIENT_START.getRGB();
        int secondColor = ClientColors.GRADIENT_END.getRGB();
        int rFirst = ColorUtils.red(firstColor);
        int gFirst = ColorUtils.green(firstColor);
        int bFirst = ColorUtils.blue(firstColor);
        int rSecond = ColorUtils.red(secondColor);
        int gSecond = ColorUtils.green(secondColor);
        int bSecond = ColorUtils.blue(secondColor);
        int rDiff = rSecond - rFirst;
        int gDiff = gSecond - gFirst;
        int bDiff = bSecond - bFirst;

        float time = (System.currentTimeMillis() % PRIV_COLOR_CYCLE_MS) / (float) PRIV_COLOR_CYCLE_MS;
        float particleDensity = particleDensityDush.get() + 0.45f;
        int particleCount = Math.max(1, (int) (espLength * particleDensity));
        float sizeBase = sizeDush.get() * 1.25f;
        double targetWidth = target.getWidth() * 0.75 + 0.12;
        float invShaking = shaking == 0f ? 0f : 1f / shaking;
        float invParticleCount = 1f / particleCount;
        double orbitAngleStep = Math.toRadians(espLength * factor * invParticleCount);
        double waveAngleStep = Math.toRadians(espLength * 1.6f * invParticleCount) * amplitude;
        float alphaStep = alpha * 0.12f * invParticleCount;
        float scaleStep = sizeBase * 0.55f * invParticleCount;
        float baseScale = sizeBase * 0.2f;
        float flash = target instanceof LivingEntity living ? getHurtFlash(living) : 0f;

        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                stack.peek().getPositionMatrix(), mc.gameRenderer.getBasicProjectionMatrix(fov))) {
            VertexConsumer consumer = renderer
                    .getBuffer(WorldRenderLayers.TEXTURED_QUADS_ADDITIVE_NO_DEPTH(SOUL_TEXTURE));
            MatrixStack identity = new MatrixStack();
            WorldGeometryEmitter emitter = new WorldGeometryEmitter(camera, identity.peek(), consumer);

            for (int j = 0; j < PRIV_BASE_ANGLES.length; j++) {
                float colorLerp = (float) (Math.sin((Math.PI * 2.0) * time + j * (Math.PI * 0.5)) * 0.5 + 0.5);
                int r = rFirst + (int) (rDiff * colorLerp);
                int g = gFirst + (int) (gDiff * colorLerp);
                int b = bFirst + (int) (bDiff * colorLerp);

                int tinted = applyDamageTint(ColorUtils.rgba(r, g, b, 255), flash);
                r = ColorUtils.red(tinted);
                g = ColorUtils.green(tinted);
                b = ColorUtils.blue(tinted);

                double orbitAngle = Math.toRadians(PRIV_BASE_ANGLES[j] + iAge * factor);
                double waveAngle = Math.toRadians(iAge * 2.0f + j * 90f) * amplitude;
                float alphaFactor = alpha;
                float scale = baseScale;

                for (int i = 0; i < particleCount; i++) {
                    double sinQuad = Math.sin(waveAngle) * invShaking;
                    int particleAlpha = Math.max(0, Math.min(255, Math.round(alphaFactor * 255f)));
                    int color = ColorUtils.rgba(r, g, b, particleAlpha);

                    MatrixStack matrices = new MatrixStack();
                    matrices.translate(
                            tPosX + Math.cos(orbitAngle) * targetWidth,
                            tPosY + target.getHeight() * 0.5 + sinQuad,
                            tPosZ + Math.sin(orbitAngle) * targetWidth);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

                    Matrix4f localTransform = matrices.peek().getPositionMatrix();
                    Vec3d v0 = transform(localTransform, -scale, scale, 0);
                    Vec3d v1 = transform(localTransform, scale, scale, 0);
                    Vec3d v2 = transform(localTransform, scale, -scale, 0);
                    Vec3d v3 = transform(localTransform, -scale, -scale, 0);

                    emitter.emitTexturedQuad(
                            v0, v1, v2, v3,
                            0f, 1f,
                            1f, 1f,
                            1f, 0f,
                            0f, 0f,
                            color);

                    orbitAngle += orbitAngleStep;
                    waveAngle += waveAngleStep;
                    alphaFactor = Math.max(0f, alphaFactor - alphaStep);
                    scale += scaleStep;
                }
            }

            renderer.flush();
        }
    }

    private void renderCrystalWorld(EventRender.World event, Entity target, float alpha, boolean modern) {
        if (target == null || mc.world == null) {
            return;
        }

        MatrixStack stack = event.getMatrixStack();
        Camera camera = mc.gameRenderer.getCamera();
        float tickDelta = event.getTicks();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);

        double baseX = lerp(target.lastX, target.getX(), tickDelta) - camera.getCameraPos().x;
        double baseY = lerp(target.lastY, target.getY(), tickDelta) - camera.getCameraPos().y
                + target.getHeight() * (modern ? 0.55 : 0.5);
        double baseZ = lerp(target.lastZ, target.getZ(), tickDelta) - camera.getCameraPos().z;
        int crystalCount = modern ? 4 : 3;
        double orbitRadius = target.getWidth() * (modern ? 0.95 : 0.75);
        double yWave = modern ? 0.22 : 0.14;
        float crystalScale = modern ? 0.22f : 0.18f;
        double timeSeconds = System.nanoTime() * 1.0E-9;
        double orbitSpeedDegPerSec = modern ? 52.0 : 36.0;

        CrystalGeometry[] crystals = new CrystalGeometry[crystalCount];
        for (int i = 0; i < crystalCount; i++) {
            float progress = (float) i / (float) crystalCount;
            double angle = Math.toRadians(timeSeconds * orbitSpeedDegPerSec + 360.0 * progress);
            double x = baseX + Math.cos(angle) * orbitRadius;
            double z = baseZ + Math.sin(angle) * orbitRadius;
            double y = baseY + Math.sin(angle * (modern ? 1.7 : 1.25)) * yWave;
            Color color = ColorUtils.interpolateColor(ClientColors.GRADIENT_START, ClientColors.GRADIENT_END,
                    progress);
            int alphaColor = ColorUtils.injectAlpha(color.getRGB(),
                    Math.max(0, Math.min(255, (int) (alpha * (modern ? 210f : 180f)))));
            crystals[i] = createCrystalGeometry(x, y, z, crystalScale, alphaColor);
        }

        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                stack.peek().getPositionMatrix(), mc.gameRenderer.getBasicProjectionMatrix(fov))) {
            MatrixStack identity = new MatrixStack();

            VertexConsumer quadConsumer = renderer.getBuffer(WorldRenderLayers.POSITION_COLOR_QUADS_ADDITIVE());
            WorldGeometryEmitter quadEmitter = new WorldGeometryEmitter(camera, identity.peek(), quadConsumer);
            for (CrystalGeometry crystal : crystals) {
                emitCrystalQuads(quadEmitter, crystal);
            }

            VertexConsumer lineConsumer = renderer
                    .getBuffer(WorldRenderLayers.LINES_ADDITIVE_NO_DEPTH(modern ? 2.0 : 1.35));
            WorldGeometryEmitter lineEmitter = new WorldGeometryEmitter(camera, identity.peek(), lineConsumer);
            for (CrystalGeometry crystal : crystals) {
                emitCrystalLines(lineEmitter, crystal);
            }

            renderer.flush();
        }
    }

    private CrystalGeometry createCrystalGeometry(double x, double y, double z, float scale, int color) {
        Vec3d top = new Vec3d(x, y + scale, z);
        Vec3d bottom = new Vec3d(x, y - scale, z);
        Vec3d east = new Vec3d(x + scale * 0.55f, y, z);
        Vec3d west = new Vec3d(x - scale * 0.55f, y, z);
        Vec3d south = new Vec3d(x, y, z + scale * 0.55f);
        Vec3d north = new Vec3d(x, y, z - scale * 0.55f);
        int fade = ColorUtils.injectAlpha(color, Math.max(0, ColorUtils.alpha(color) / 5));
        return new CrystalGeometry(top, bottom, east, west, south, north, color, fade);
    }

    private void emitCrystalQuads(WorldGeometryEmitter emitter, CrystalGeometry crystal) {
        emitter.emitQuad(crystal.top, crystal.east, crystal.bottom, crystal.south, crystal.color, crystal.color,
                crystal.fade, crystal.fade);
        emitter.emitQuad(crystal.top, crystal.south, crystal.bottom, crystal.west, crystal.color, crystal.color,
                crystal.fade, crystal.fade);
        emitter.emitQuad(crystal.top, crystal.west, crystal.bottom, crystal.north, crystal.color, crystal.color,
                crystal.fade, crystal.fade);
        emitter.emitQuad(crystal.top, crystal.north, crystal.bottom, crystal.east, crystal.color, crystal.color,
                crystal.fade, crystal.fade);
    }

    private void emitCrystalLines(WorldGeometryEmitter lineEmitter, CrystalGeometry crystal) {
        lineEmitter.emitLine(crystal.top, crystal.east, crystal.color);
        lineEmitter.emitLine(crystal.top, crystal.south, crystal.color);
        lineEmitter.emitLine(crystal.top, crystal.west, crystal.color);
        lineEmitter.emitLine(crystal.top, crystal.north, crystal.color);
        lineEmitter.emitLine(crystal.bottom, crystal.east, crystal.color);
        lineEmitter.emitLine(crystal.bottom, crystal.south, crystal.color);
        lineEmitter.emitLine(crystal.bottom, crystal.west, crystal.color);
        lineEmitter.emitLine(crystal.bottom, crystal.north, crystal.color);
    }

    private static final class CrystalGeometry {
        private final Vec3d top;
        private final Vec3d bottom;
        private final Vec3d east;
        private final Vec3d west;
        private final Vec3d south;
        private final Vec3d north;
        private final int color;
        private final int fade;

        private CrystalGeometry(Vec3d top, Vec3d bottom, Vec3d east, Vec3d west, Vec3d south, Vec3d north, int color,
                                int fade) {
            this.top = top;
            this.bottom = bottom;
            this.east = east;
            this.west = west;
            this.south = south;
            this.north = north;
            this.color = color;
            this.fade = fade;
        }
    }

    private void renderChainsWorld(EventRender.World event, LivingEntity target, float alpha) {
        if (target == null || mc.world == null)
            return;

        MatrixStack stack = event.getMatrixStack();
        Camera camera = mc.gameRenderer.getCamera();
        float tickDelta = event.getTicks();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);
        Vec3d cameraPos = camera.getCameraPos();

        double targetX = lerp(target.lastX, target.getX(), tickDelta);
        double targetY = lerp(target.lastY, target.getY(), tickDelta);
        double targetZ = lerp(target.lastZ, target.getZ(), tickDelta);

        double tX = targetX - cameraPos.x;
        double tY = targetY - cameraPos.y;
        double tZ = targetZ - cameraPos.z;

        float speed = chainSpeed.get();

        float movingValue = (float) (System.currentTimeMillis() % 360000L) * 0.15f * speed;
        float rotSpeed = 0.3f;
        float width = target.getWidth() * 1.2f;

        int baseAlpha = Math.max(0, Math.min(255, Math.round(alpha * 255f)));

        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                stack.peek().getPositionMatrix(), mc.gameRenderer.getBasicProjectionMatrix(fov))) {
            VertexConsumer consumer = renderer
                    .getBuffer(WorldRenderLayers.TEXTURED_QUADS_ADDITIVE_NO_DEPTH(CHAIN_TEXTURE));
            MatrixStack identity = new MatrixStack();
            WorldGeometryEmitter emitter = new WorldGeometryEmitter(camera, identity.peek(), consumer);

            for (int chain = 0; chain < 6; chain++) {
                float gradusX = 20.0f * Math.min(1.0f + (float) Math.sin(Math.toRadians(movingValue)), 1.0f);
                float gradusZ = 20.0f * (Math.min(1.0f + (float) Math.sin(Math.toRadians(movingValue)), 2.0f) - 1.0f);

                if (chain == 1) {
                    gradusX = -gradusX;
                    gradusZ = -gradusZ;
                }

                MatrixStack matrices = new MatrixStack();
                matrices.translate(tX, tY + target.getHeight() / 2.0, tZ);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(chain == 0 ? gradusX : -gradusX));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(chain == 0 ? gradusZ : -gradusZ));

                float val = 1.2f - 0.5f * alpha;
                float chainAlpha = baseAlpha * 0.65f;
                int color = ColorUtils.rgba(ClientColors.ICON.getRed(), ClientColors.ICON.getGreen(),
                        ClientColors.ICON.getBlue(), (int) chainAlpha);

                Matrix4f matrix = matrices.peek().getPositionMatrix();
                float down = 1f;
                float x = 0.0f;
                float y = -0.45f;
                float z = 0.0f;

                int modif = 22;
                for (int i = 0; i < 744; i += modif) {
                    float prevSin = x + (chain == 0 ? gradusX : -gradusX) / 100.0f
                            + (float) Math.sin(Math.toRadians((i - modif) + movingValue * rotSpeed)) * width * val;
                    float prevCos = z + (chain == 0 ? -gradusZ : gradusZ) / 100.0f
                            + (float) Math.cos(Math.toRadians((i - modif) + movingValue * rotSpeed)) * width * val;
                    float sin = x + (chain == 0 ? gradusX : -gradusX) / 100.0f
                            + (float) Math.sin(Math.toRadians(i + movingValue * rotSpeed)) * width * val;
                    float cos = z + (chain == 0 ? -gradusZ : gradusZ) / 100.0f
                            + (float) Math.cos(Math.toRadians(i + movingValue * rotSpeed)) * width * val;

                    Vec3d v0 = transform(matrix, prevSin, y, prevCos);
                    Vec3d v1 = transform(matrix, sin, y, cos);
                    Vec3d v2 = transform(matrix, sin, y + down, cos);
                    Vec3d v3 = transform(matrix, prevSin, y + down, prevCos);

                    float u0 = 0.0027777778f * (i - modif) * 4.0f;
                    float u1 = 0.0027777778f * i * 4.0f;
                    float v = 0.99f;

                    emitter.emitTexturedQuad(
                            v0, v1, v2, v3,
                            u0, 0.0f,
                            u1, 0.0f,
                            u1, v,
                            u0, v,
                            color);
                }
            }

            renderer.flush();
        }
    }

    private static final double BOLT_PIXEL_TO_WORLD = WorldLineRenderer.PIXEL_TO_WORLD;
    private static final double BOLT_MIN_HALF_WIDTH = WorldLineRenderer.MIN_HALF_WIDTH;

    private void renderLightningWorld(EventRender.World event, LivingEntity target, float alpha, boolean spawning) {
        if (mc.gameRenderer == null || mc.gameRenderer.getCamera() == null) return;
        if (lastTargetPos == null) return;

        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getCameraPos();
        float tickDelta = event.getTicks();
        long now = System.currentTimeMillis();

        float targetH = lastBoltHeight;
        float targetW = lastBoltWidth;
        Vec3d centerWorld = lastTargetPos;

        boolean hasLive = target != null && target.isAlive();
        if (hasLive) {
            targetH = target.getHeight();
            targetW = target.getWidth();
            lastBoltHeight = targetH;
            lastBoltWidth = targetW;
            centerWorld = new Vec3d(
                    lerp(target.lastX, target.getX(), tickDelta),
                    lerp(target.lastY, target.getY(), tickDelta) + targetH * 0.5f,
                    lerp(target.lastZ, target.getZ(), tickDelta));
            lastTargetPos = centerWorld;
        }

        double cx = centerWorld.x - camPos.x;
        double cy = centerWorld.y - camPos.y;
        double cz = centerWorld.z - camPos.z;

        long refreshMs = Math.max(10L, (long) boltRefresh.get().floatValue());
        int maxBolts = Math.max(2, Math.round(boltCount.get()));
        int segN = Math.max(4, Math.round(boltSegments.get()));
        float jit = boltJitter.get();
        boolean branches = boltBranches.get();
        float speedFactor = Math.max(0.1f, boltSpeed.get());
        float volume = boltVolume.get();

        if (spawning && (now - lastBoltRefresh) >= refreshMs) {
            lastBoltRefresh = now;
            int spawnCount = 1 + boltRandom.nextInt(2);
            for (int i = 0; i < spawnCount; i++) {
                if (activeBolts.size() >= maxBolts) break;
                long seed = boltRandom.nextLong();
                List<LightningSegment> segs = new ArrayList<>();
                generateWrappingLightning(segs, Vec3d.ZERO, targetH, targetW,
                        segN, jit, seed, branches, volume);
                long life = (long) (170L / speedFactor) + (long) boltRandom.nextInt(90);
                activeBolts.add(new Bolt(segs, now, life, seed));
            }
        }

        activeBolts.removeIf(bolt -> (now - bolt.spawnTime) >= bolt.lifetime);
        if (activeBolts.isEmpty()) return;

        int activeCount = activeBolts.size();
        float[] boltAlphas = new float[activeCount];
        int validCount = 0;
        for (int i = 0; i < activeCount; i++) {
            float ba = getBoltAlpha(activeBolts.get(i), now) * alpha;
            boltAlphas[i] = ba;
            if (ba > 0.01f) validCount++;
        }
        if (validCount == 0) return;

        float lineW = boltWidth.get();
        boolean glow = boltGlow.get();
        float flash = getHurtFlash(target);
        int baseColor = ClientColors.ICON.getRGB();
        int r1 = (baseColor >> 16) & 0xFF;
        int g1 = (baseColor >> 8) & 0xFF;
        int b1 = baseColor & 0xFF;
        int r2 = 255, g2 = 60, b2 = 60;
        int r = (int) (r1 + (r2 - r1) * flash);
        int g = (int) (g1 + (g2 - g1) * flash);
        int b = (int) (b1 + (b2 - b1) * flash);
        int wr = (int) (r + (255 - r) * 0.7f);
        int wg = (int) (g + (255 - g) * 0.7f);
        int wb = (int) (b + (255 - b) * 0.7f);

        MatrixStack stack = event.getMatrixStack();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true);

        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                stack.peek().getPositionMatrix(), mc.gameRenderer.getBasicProjectionMatrix(fov))) {
            MatrixStack identity = new MatrixStack();

            VertexConsumer ribbonConsumer = renderer.getBuffer(WorldRenderLayers.POSITION_COLOR_QUADS_ADDITIVE_NO_DEPTH());
            WorldGeometryEmitter ribbonEmitter = new WorldGeometryEmitter(camera, identity.peek(), ribbonConsumer);

            if (glow) {
                float[] glowMul = {6.0f, 3.5f, 2.0f, 1.2f};
                float[] glowA = {0.10f, 0.18f, 0.35f, 0.7f};
                for (int p = 0; p < glowMul.length; p++) {
                    float px = lineW * glowMul[p];
                    for (int i = 0; i < activeCount; i++) {
                        float ba = boltAlphas[i];
                        if (ba <= 0.01f) continue;
                        emitBoltRibbon(ribbonEmitter, activeBolts.get(i).segments,
                                cx, cy, cz, px, glowA[p] * ba, r, g, b);
                    }
                }
            }

            for (int i = 0; i < activeCount; i++) {
                float ba = boltAlphas[i];
                if (ba <= 0.01f) continue;
                emitBoltRibbon(ribbonEmitter, activeBolts.get(i).segments,
                        cx, cy, cz, lineW, ba, wr, wg, wb);
            }

            renderer.flush();
        }
    }

    private void emitBoltRibbon(WorldGeometryEmitter emitter, List<LightningSegment> segments,
                                double cx, double cy, double cz,
                                float px, float alphaMul, int r, int g, int b) {
        if (segments.isEmpty()) return;
        for (int i = 0, n = segments.size(); i < n; i++) {
            LightningSegment seg = segments.get(i);
            float edgeFade = computeBoltEdgeFade(seg.progress);
            int a = (int) MathHelper.clamp(255.0f * alphaMul * edgeFade, 0f, 255f);
            if (a <= 0) continue;
            int color = ColorUtils.rgba(r, g, b, a);

            double rax = cx + seg.x1, ray = cy + seg.y1, raz = cz + seg.z1;
            double rbx = cx + seg.x2, rby = cy + seg.y2, rbz = cz + seg.z2;

            double dx = rbx - rax, dy = rby - ray, dz = rbz - raz;
            double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (len < 1.0E-9) continue;
            double inv = 1.0 / len;
            dx *= inv; dy *= inv; dz *= inv;

            double mx = (rax + rbx) * 0.5, my = (ray + rby) * 0.5, mz = (raz + rbz) * 0.5;
            double dist = Math.sqrt(mx * mx + my * my + mz * mz);
            double tcx, tcy, tcz;
            if (dist < 1.0E-6) {
                tcx = 0.0; tcy = 0.0; tcz = 1.0;
            } else {
                double invD = -1.0 / dist;
                tcx = mx * invD; tcy = my * invD; tcz = mz * invD;
            }

            double sx = dy * tcz - dz * tcy;
            double sy = dz * tcx - dx * tcz;
            double sz = dx * tcy - dy * tcx;
            double sLen = Math.sqrt(sx * sx + sy * sy + sz * sz);
            if (sLen < 1.0E-6) {
                sx = -dz; sy = 0.0; sz = dx;
                sLen = Math.sqrt(sx * sx + sz * sz);
                if (sLen < 1.0E-6) { sx = 1.0; sy = 0.0; sz = 0.0; sLen = 1.0; }
            }
            double half = Math.max(px * dist * BOLT_PIXEL_TO_WORLD, BOLT_MIN_HALF_WIDTH);
            double sf = half / sLen;
            sx *= sf; sy *= sf; sz *= sf;

            Vec3d c0 = new Vec3d(rax + sx, ray + sy, raz + sz);
            Vec3d c1 = new Vec3d(rbx + sx, rby + sy, rbz + sz);
            Vec3d c2 = new Vec3d(rbx - sx, rby - sy, rbz - sz);
            Vec3d c3 = new Vec3d(rax - sx, ray - sy, raz - sz);
            emitter.emitQuad(c0, c1, c2, c3, color);
        }
    }

    private float getBoltAlpha(Bolt bolt, long now) {
        float age = MathHelper.clamp((now - bolt.spawnTime) / (float) bolt.lifetime, 0f, 1f);
        float alpha;
        if (age < 0.12f) {
            alpha = age / 0.12f;
        } else if (age > 0.6f) {
            alpha = Math.max(0f, (1f - age) / 0.4f);
        } else {
            alpha = 1f;
        }
        if (boltFlicker.get()) {
            float flick = 0.65f + 0.35f * (float) Math.sin((now - bolt.spawnTime) * 0.06 + bolt.seed);
            alpha *= flick;
        }
        return alpha;
    }

    private float computeBoltEdgeFade(float progress) {
        float edge = 0.15f;
        float fade;
        if (progress <= edge) {
            fade = progress / edge;
        } else if (progress >= 1.0f - edge) {
            fade = (1.0f - progress) / edge;
        } else {
            fade = 1.0f;
        }
        return fade * fade * (3f - 2f * fade);
    }

    private Vec3d pickWrapAnchor(Vec3d targetCenter, float height, float width,
                                 double angle, double yProgress, float volume) {
        double radius = width * 0.6 + Math.max(0.05, volume);
        double y = (yProgress - 0.5) * height;
        return targetCenter.add(Math.cos(angle) * radius, y, Math.sin(angle) * radius);
    }

    private void generateWrappingLightning(List<LightningSegment> out, Vec3d targetCenter,
                                           float height, float width, int segN, float jitter,
                                           long seed, boolean branches, float volume) {
        Random rng = new Random(seed);
        double startAngle = rng.nextDouble() * Math.PI * 2.0;
        double angleSpan = (rng.nextDouble() * 0.5 + 0.6) * Math.PI * 2.0;
        if (rng.nextBoolean()) angleSpan = -angleSpan;
        int anchors = 3 + rng.nextInt(2);
        List<Vec3d> anchorPoints = new ArrayList<>();
        for (int i = 0; i <= anchors; i++) {
            double t = (double) i / anchors;
            double angle = startAngle + angleSpan * t;
            anchorPoints.add(pickWrapAnchor(targetCenter, height, width, angle, t, volume));
        }
        int subSegN = Math.max(3, segN / 2);
        for (int i = 0; i < anchorPoints.size() - 1; i++) {
            float t1 = (float) i / anchors;
            float t2 = (float) (i + 1) / anchors;
            generateRealLightning(out, anchorPoints.get(i), anchorPoints.get(i + 1),
                    subSegN, jitter * 0.7f, rng.nextLong(), branches, 0, t1, t2);
        }
    }

    private void generateRealLightning(List<LightningSegment> out, Vec3d start, Vec3d end,
                                       int detail, float jitter, long seed, boolean branches, int depth,
                                       float tStart, float tEnd) {
        Vec3d dir = end.subtract(start);
        double length = dir.length();
        if (length < 1.0E-4) return;
        Vec3d forward = dir.multiply(1.0 / length);
        Vec3d up = Math.abs(forward.y) < 0.99 ? new Vec3d(0, 1, 0) : new Vec3d(1, 0, 0);
        Vec3d right = forward.crossProduct(up).normalize();
        Vec3d realUp = right.crossProduct(forward).normalize();

        Random rng = new Random(seed);
        detail = MathHelper.clamp(detail, 1, 4);

        List<Vec3d> points = new ArrayList<>();
        points.add(start);
        points.add(end);

        double displacement = jitter * length * 0.18;
        for (int iter = 0; iter < detail; iter++) {
            List<Vec3d> newPoints = new ArrayList<>();
            newPoints.add(points.get(0));
            for (int i = 0; i < points.size() - 1; i++) {
                Vec3d p1 = points.get(i);
                Vec3d p2 = points.get(i + 1);
                Vec3d mid = p1.add(p2).multiply(0.5);
                double offR = (rng.nextDouble() * 2.0 - 1.0) * displacement;
                double offU = (rng.nextDouble() * 2.0 - 1.0) * displacement;
                mid = mid.add(right.multiply(offR)).add(realUp.multiply(offU));
                newPoints.add(mid);
                newPoints.add(p2);
            }
            points = newPoints;
            displacement *= 0.52;
        }

        List<Vec3d> renderPoints = roundCorners(points, 0.65f);
        int totalR = Math.max(1, renderPoints.size() - 1);
        for (int i = 0; i < totalR; i++) {
            Vec3d p1 = renderPoints.get(i);
            Vec3d p2 = renderPoints.get(i + 1);
            float p1prog = MathHelper.lerp(tStart, tEnd, (float) i / totalR);
            float p2prog = MathHelper.lerp(tStart, tEnd, (float) (i + 1) / totalR);
            float segProg = (p1prog + p2prog) * 0.5f;
            out.add(new LightningSegment(
                    (float) p1.x, (float) p1.y, (float) p1.z,
                    (float) p2.x, (float) p2.y, (float) p2.z, segProg));
        }
        int totalP = Math.max(1, points.size() - 1);
        for (int i = 0; i < totalP; i++) {
            if (branches && depth < 2 && i > 1 && i < totalP - 1 && rng.nextFloat() < 0.10f) {
                Vec3d bp = points.get(i);
                float bpProg = MathHelper.lerp(tStart, tEnd, (float) i / totalP);
                double branchLen = length * (0.12 + rng.nextDouble() * 0.18);
                double bRight = (rng.nextDouble() * 2.0 - 1.0) * 0.8;
                double bUp = (rng.nextDouble() * 2.0 - 1.0) * 0.8;
                Vec3d branchDir = forward.add(right.multiply(bRight)).add(realUp.multiply(bUp)).normalize();
                Vec3d branchEnd = bp.add(branchDir.multiply(branchLen));
                generateRealLightning(out, bp, branchEnd, Math.max(2, detail - 2),
                        jitter * 0.5f, rng.nextLong(), false, depth + 1, bpProg, bpProg);
            }
        }
    }

    private List<Vec3d> roundCorners(List<Vec3d> points, float roundFactor) {
        if (points.size() < 3) return points;
        List<Vec3d> out = new ArrayList<>();
        out.add(points.get(0));
        for (int i = 1; i < points.size() - 1; i++) {
            Vec3d prev = points.get(i - 1);
            Vec3d cur = points.get(i);
            Vec3d next = points.get(i + 1);
            Vec3d v1 = cur.subtract(prev);
            double len1 = v1.length();
            Vec3d v2 = next.subtract(cur);
            double len2 = v2.length();
            if (len1 < 1.0E-4 || len2 < 1.0E-4) {
                out.add(cur);
                continue;
            }
            double round = Math.min(len1, len2) * roundFactor;
            Vec3d b1 = cur.subtract(v1.multiply(round / len1));
            Vec3d b2 = cur.add(v2.multiply(round / len2));
            out.add(b1);
            int subdiv = 4;
            for (int s = 1; s < subdiv; s++) {
                float t = s / (float) subdiv;
                float mt = 1.0f - t;
                double x = mt * mt * b1.x + 2 * mt * t * cur.x + t * t * b2.x;
                double y = mt * mt * b1.y + 2 * mt * t * cur.y + t * t * b2.y;
                double z = mt * mt * b1.z + 2 * mt * t * cur.z + t * t * b2.z;
                out.add(new Vec3d(x, y, z));
            }
            out.add(b2);
        }
        out.add(points.get(points.size() - 1));
        return out;
    }

    private static final class LightningSegment {
        final float x1, y1, z1, x2, y2, z2;
        final float progress;
        LightningSegment(float x1, float y1, float z1, float x2, float y2, float z2, float progress) {
            this.x1 = x1; this.y1 = y1; this.z1 = z1;
            this.x2 = x2; this.y2 = y2; this.z2 = z2;
            this.progress = progress;
        }
    }

    private static final class Bolt {
        final List<LightningSegment> segments;
        final long spawnTime;
        final long lifetime;
        final long seed;
        Bolt(List<LightningSegment> segments, long spawnTime, long lifetime, long seed) {
            this.segments = segments;
            this.spawnTime = spawnTime;
            this.lifetime = lifetime;
            this.seed = seed;
        }
    }

    private static Vec3d transform(Matrix4f matrix, double x, double y, double z) {
        Vector4f vec = new Vector4f((float) x, (float) y, (float) z, 1f);
        matrix.transform(vec);
        return new Vec3d(vec.x, vec.y, vec.z);
    }

    public void updateCircleESP() {
        lastCircleStep = circleStep;
        circleStep += 0.08f;
    }
}
