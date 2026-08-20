package fun.volon.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import fun.volon.api.utils.namespaced.Namespaced;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import fun.volon.api.QClient;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.Priority;
import fun.volon.api.events.implement.Event3DRender;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.animation.Easings;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.impl.combat.Aura;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;
import fun.volon.client.modules.settings.implement.ModeSetting;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class TargetESP extends Module {

    public static TargetESP INSTANCE = new TargetESP();
    private static final float GHOST_ALPHA_MULT = 0.6f;
    private static final float CELKA_SPEED_MULT = 1.2f;
    private static final float SCALE_FACTOR = 0.007f;
    static final long CUBE_ATTACH_LIFE_MS = 560L;
    static final long CUBE_FADE_LIFE_MS = 320L;
    static final int MAX_CUBE_PARTICLES = 72;
    static final byte[][] CUBE_EDGES = {
            {-1, -1, -1, 1, -1, -1}, {1, -1, -1, 1, -1, 1}, {1, -1, 1, -1, -1, 1}, {-1, -1, 1, -1, -1, -1},
            {-1, 1, -1, 1, 1, -1}, {1, 1, -1, 1, 1, 1}, {1, 1, 1, -1, 1, 1}, {-1, 1, 1, -1, 1, -1},
            {-1, -1, -1, -1, 1, -1}, {1, -1, -1, 1, 1, -1}, {1, -1, 1, 1, 1, 1}, {-1, -1, 1, -1, 1, 1}
    };

    private final ModeSetting mode = new ModeSetting("Режим", "Ромб", "Ромб", "Кубы","Кольцо", "Души", "Кристаллы", "Треугольник", "калестиал", "призракиследаки", "ДефПризраки");
    private final FloatSetting size = new FloatSetting("Размер", 1.15f, 0.6f, 2.5f, 0.05f);
    private final FloatSetting ringRadius = new FloatSetting("Радиус кольца", 0.5f, 0.3f, 1.5f, 0.05f);
    private final FloatSetting ringSpeed = new FloatSetting("Скорость кольца", 1.0f, 0.3f, 3.0f, 0.1f);
    private final FloatSetting rotateSpeed = new FloatSetting("Скорость вращения", 1.2f, 0.2f, 4.0f, 0.05f);
    private final BooleanSetting hurtColor = new BooleanSetting("Окрашивание при ударе", true);
    private final FloatSetting bmwGhostCount = new FloatSetting("Кол-во призраков", 3.0f, 2.0f, 5.0f, 1.0f);
    private final FloatSetting bmwGhostLife = new FloatSetting("Время жизни (мс)", 350.0f, 150.0f, 500.0f, 25.0f);
    private final FloatSetting bmwStrengthXZ = new FloatSetting("Цикл XZ", 2000.0f, 1000.0f, 5000.0f, 100.0f);
    private final FloatSetting bmwStrengthY = new FloatSetting("Цикл Y", 1700.0f, 1000.0f, 5000.0f, 100.0f);
    private float appearValue = 0f;
    private float scaleValue = 0f;
    private float rotProgress = 0f;
    private float rotFrom = -280f;
    private float rotTo = 280f;
    private long lastRotateUpdate = System.currentTimeMillis();
    private LivingEntity lastTarget = null;
    private LivingEntity lastHandledTarget = null;
    private Vec3d lastTargetPos = null;
    private float lastTargetHeight = 1.8f;
    private float lastTargetWidth = 0.6f;
    private final CopyOnWriteArrayList<GlowPoint> bmwPoints = new CopyOnWriteArrayList<>();
    private float crystalRotationAngle = 0f;
    private float crystalAnimation = 0f;
    private float spawnAccumulator = 0f;
    private long lastCubeTime = 0L;
    private final ArrayList<CubeParticle> cubeParticles = new ArrayList<>();
    private final ArrayList<CubeParticle> renderCubeParticles = new ArrayList<>();
    private static final float SPAWN_INTERVAL = 0.022f;
    private static final int PARTICLES_PER_SPAWN = 1;

    // Призраки 2 fields
    private double ghostAge = 0.0;

    // Призраки 3 fields
    private final java.util.Map<LivingEntity, java.util.List<Vec3d>> phantomTrail1 = new java.util.WeakHashMap<>();
    private final java.util.Map<LivingEntity, java.util.List<Vec3d>> phantomTrail2 = new java.util.WeakHashMap<>();
    private final java.util.Map<LivingEntity, java.util.List<Vec3d>> phantomTrail3 = new java.util.WeakHashMap<>();
    private int ghost3LastHurtTime = 0;
    private long ghost3LastHurtTimeMs = 0L;

    // Nursultan Prizrak fields
    private long nursultanTimestamp = System.currentTimeMillis();

    public TargetESP() {
        super("TargetESP", "Отображения таргета", ModuleCategory.RENDER);
        size.visible(this::isImageMode);
        rotateSpeed.visible(this::isImageMode);
        bmwGhostCount.visible(() -> mode.is("Райдер"));
        bmwGhostLife.visible(() -> mode.is("Райдер"));
        bmwStrengthXZ.visible(() -> mode.is("Райдер"));
        bmwStrengthY.visible(() -> mode.is("Райдер"));
        ringRadius.visible(() -> mode.is("Кольцо"));
        ringSpeed.visible(() -> mode.is("Кольцо"));
        addSettings(mode, size, rotateSpeed, hurtColor, ringRadius, ringSpeed, bmwGhostCount, bmwGhostLife, bmwStrengthXZ, bmwStrengthY);
    }

    @Override
    public void onDisable() {
        appearValue = 0f;
        scaleValue = 0f;
        lastTarget = null;
        lastHandledTarget = null;
        lastTargetPos = null;
        rotProgress = 0f;
        rotFrom = -280f;
        rotTo = 280f;
        bmwPoints.clear();
        crystalRotationAngle = 0f;
        crystalAnimation = 0f;
        spawnAccumulator = 0f;
        lastCubeTime = 0L;
        cubeParticles.clear();
        renderCubeParticles.clear();
        ghostAge = 0.0;
        phantomTrail1.clear();
        phantomTrail2.clear();
        phantomTrail3.clear();
        ghost3LastHurtTime = 0;
        ghost3LastHurtTimeMs = 0L;
        super.onDisable();
    }

    private boolean isImageMode() {
        return mode.is("Картинка 1") || mode.is("Ромб");
    }

    private Identifier getCaptureTexture() {
        if (mode.is("Ромб")) {
            return Namespaced.of("textures/targetesp/targetesp_3.png");
        }
        return Namespaced.of("textures/targetesp/targetesp_2.png");
    }

    private Identifier getBloomTexture() {
        return Namespaced.of("textures/targetesp/bloom.png");
    }

    private int getESPColor() {
        int color = ColorUtils.getThemeColor();
        if (((color >> 24) & 0xFF) == 0) {
            color = color | 0xFF000000;
        }
        return color;
    }

    private float animateTo(float current, float target, float delta) {
        if (current < target) {
            current = Math.min(current + delta, target);
        } else if (current > target) {
            current = Math.max(current - delta, target);
        }
        return current;
    }

    private float getDistanceScale(Vec3d cameraPos, double worldX, double worldY, double worldZ) {
        double dx = worldX - cameraPos.x;
        double dy = worldY - cameraPos.y;
        double dz = worldZ - cameraPos.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return (float) Math.max(0.1, distance * SCALE_FACTOR);
    }

    @EventLink(priority = Priority.LOW)
    public void onRender3D(Event3DRender event) {
        if (mc == null || mc.player == null || mc.world == null) return;

        Aura aura = ModuleClass.INSTANCE.aura;
        boolean auraEnabled = aura != null && aura.isEnable();
        LivingEntity target = auraEnabled ? aura.getTarget() : null;
        boolean hasTarget = target != null && target.isAlive();
        float speed = 0.05f;
        appearValue = animateTo(appearValue, hasTarget ? 1f : 0f, speed);
        scaleValue = animateTo(scaleValue, hasTarget ? 1f : 0.5f, speed);

        if (hasTarget) {
            this.lastTarget = target;
            this.lastHandledTarget = target;
        }
        if (mode.is("Кристаллы")) {
            float crystalSpeed = hasTarget ? 0.07f : 0.045f;
            crystalAnimation = animateTo(crystalAnimation, hasTarget ? 1f : 0f, crystalSpeed);
            if (hasTarget) {
                crystalRotationAngle += 0.8f;
            }
        }

        if (appearValue <= 0.001f && !hasTarget) {
            if (!mode.is("Кристаллы") || crystalAnimation <= 0.001f) {
                lastTarget = null;
                lastTargetPos = null;
                return;
            }
        }
        if (hasTarget && target != null) {
            float td = event.getTickDelta();
            lastTargetPos = new Vec3d(
                    MathHelper.lerp(td, target.lastRenderX, target.getX()),
                    MathHelper.lerp(td, target.lastRenderY, target.getY()),
                    MathHelper.lerp(td, target.lastRenderZ, target.getZ())
            );
            lastTargetHeight = target.getHeight();
            lastTargetWidth = target.getWidth();
        }
        if (lastTargetPos == null) return;

        if (mode.is("Райдер")) {
            if (hasTarget && target != null) {
                addBMWGhosts(target, event.getTickDelta(),
                        Math.max(1, Math.round(bmwGhostCount.getValue().floatValue())),
                        Math.max(1, Math.round(bmwGhostLife.getValue().floatValue())),
                        getESPColor());
            }
            bmwPoints.removeIf(GlowPoint::shouldRemove);
            drawBMW3D(event);
            return;
        }
        if (mode.is("Кристаллы")) {
            LivingEntity crystalTarget = hasTarget ? target : lastTarget;
            if ((crystalTarget != null || lastTargetPos != null) && crystalAnimation > 0.01f) {
                renderCrystals3D(event.getMatrices(), crystalTarget, event.getTickDelta());
            }
            return;
        }
        if (isImageMode()) {
            renderMarker3D(event);
        }
        if (mode.is("Души")) {
            drawSouls3D(event);
        }
        if (mode.is("Призраки")) {
            drawCelka3D(event);
        }
        if (mode.is("Кольцо")) {
            drawRing3D(event);
        }
        if (mode.is("Кубы")) {
            renderCubes(event, target, hasTarget);
        }
        if (mode.is("Треугольник")) {
            drawTriangle3D(event);
        }
        if (mode.is("калестиал")) {
            ghostAge++;
            drawGhosts2_3D(event);
        }
        if (mode.is("призракиследаки")) {
            drawGhosts3_3D(event);
        }
        if (mode.is("ДефПризраки")) {
            drawNursultanPrizrak_3D(event);
        }
    }

    private void renderCubes(Event3DRender event, LivingEntity target, boolean hasTarget) {
        long now = System.currentTimeMillis();
        if (lastCubeTime == 0L) lastCubeTime = now;
        float dt = Math.min((now - lastCubeTime) / 1000.0f, 0.1f);
        lastCubeTime = now;
        if (!Float.isFinite(dt) || mc.gameRenderer == null || mc.gameRenderer.getCamera() == null) {
            return;
        }

        if (hasTarget && target != null) {
            lastTarget = target;
            spawnAccumulator += dt;
            while (spawnAccumulator >= SPAWN_INTERVAL) {
                spawnAccumulator -= SPAWN_INTERVAL;
                if (cubeParticles.size() >= MAX_CUBE_PARTICLES) {
                    break;
                }
                for (int i = 0; i < PARTICLES_PER_SPAWN; i++) {
                    double rand = Math.random() * 360.0;
                    double px = Math.cos(Math.toRadians(rand)) * 0.7;
                    double py = 0.02 + Math.random() * 0.10;
                    double pz = Math.sin(Math.toRadians(rand)) * 0.7;
                    cubeParticles.add(new CubeParticle(target, px, py, pz));
                }
            }
        } else {
            spawnAccumulator = 0f;
        }

        renderCubeParticles.clear();
        for (int i = cubeParticles.size() - 1; i >= 0; i--) {
            CubeParticle particle = cubeParticles.get(i);
            try {
                particle.update(dt, now, hasTarget ? target : null);
                if (particle.shouldRemove(now)) {
                    cubeParticles.remove(i);
                } else {
                    renderCubeParticles.add(particle);
                }
            } catch (Throwable ignored) {
                cubeParticles.remove(i);
            }
        }

        if (renderCubeParticles.isEmpty()) {
            return;
        }

        float partialTicks = event.getTickDelta();
        MatrixStack matrices = event.getMatrices();
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();
        LivingEntity colorTarget = hasTarget ? target : lastTarget;
        float hurtPC = getHurtPC(colorTarget);
        int baseColor = getESPColor();
        int redColor = ColorUtils.rgb(255, 3, 3);

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        BufferBuilder faceBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        boolean hasFaces = false;
        for (int i = 0, size = renderCubeParticles.size(); i < size; i++) {
            CubeParticle particle = renderCubeParticles.get(i);
            try {
                int particleColor = particle.getRenderColor(baseColor, redColor, hurtPC, now);
                if (((particleColor >> 24) & 0xFF) <= 0) continue;
                if (particle.appendCubeFaces(faceBuilder, matrices, camPos, partialTicks, particleColor)) {
                    hasFaces = true;
                }
            } catch (Throwable ignored) {
            }
        }
        if (hasFaces) BufferRenderer.drawWithGlobalProgram(faceBuilder.end());

        BufferBuilder lineBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        boolean hasLines = false;
        for (int i = 0, size = renderCubeParticles.size(); i < size; i++) {
            CubeParticle particle = renderCubeParticles.get(i);
            try {
                int particleColor = particle.getRenderColor(baseColor, redColor, hurtPC, now);
                if (((particleColor >> 24) & 0xFF) <= 0) continue;
                if (particle.appendCubeLines(lineBuilder, matrices, camPos, partialTicks, particleColor)) {
                    hasLines = true;
                }
            } catch (Throwable ignored) {
            }
        }
        if (hasLines) BufferRenderer.drawWithGlobalProgram(lineBuilder.end());

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, getBloomTexture());
        BufferBuilder bloomBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        boolean hasBloom = false;
        float camYaw = mc.gameRenderer.getCamera().getYaw();
        float camPitch = mc.gameRenderer.getCamera().getPitch();
        for (int i = 0, size = renderCubeParticles.size(); i < size; i++) {
            CubeParticle particle = renderCubeParticles.get(i);
            try {
                int particleColor = particle.getRenderColor(baseColor, redColor, hurtPC, now);
                if (particle.appendBloom(bloomBuilder, matrices, camPos, camYaw, camPitch, partialTicks, particleColor, now)) {
                    hasBloom = true;
                }
            } catch (Throwable ignored) {
            }
        }
        if (hasBloom) BufferRenderer.drawWithGlobalProgram(bloomBuilder.end());

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    private void drawRing3D(Event3DRender event) {
        if (appearValue <= 0.001f || lastTargetPos == null) return;

        float partialTicks = mc.getRenderTickCounter().getTickDelta(true);
        Vec3d vec;
        float entityHeight;
        LivingEntity target = lastTarget;

        if (target != null && target.isAlive()) {
            vec = new Vec3d(
                    MathHelper.lerp(partialTicks, target.lastRenderX, target.getX()),
                    MathHelper.lerp(partialTicks, target.lastRenderY, target.getY()),
                    MathHelper.lerp(partialTicks, target.lastRenderZ, target.getZ())
            );
            entityHeight = target.getHeight();
        } else {
            vec = lastTargetPos;
            entityHeight = lastTargetHeight;
        }

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        double x = vec.x - cam.x;
        double y = vec.y - cam.y;
        double z = vec.z - cam.z;

        double duration = 2000.0 / ringSpeed.get();
        double elapsed = (double) (System.currentTimeMillis() % (long) duration);
        boolean side = elapsed > duration / 2.0;
        double progress = elapsed / (duration / 2.0);

        if (side) {
            progress -= 1.0;
        } else {
            progress = 1.0 - progress;
        }

        progress = progress < 0.5 ? 2.0 * progress * progress : 1.0 - Math.pow(-2.0 * progress + 2.0, 2.0) / 2.0;
        double eased = (double) entityHeight / 1.2 * (progress > 0.5 ? 1.0 - progress : progress) * (double) (side ? -1 : 1);

        int baseCol = getESPColor();
        float hurtPC = getHurtPC(target);
        int redCol = ColorUtils.rgb(255, 3, 3);
        int mainColor = overCol(baseCol, redCol, hurtPC);

        int colorWithAlpha = setAlpha(mainColor, 225.0f / 255.0f * appearValue);
        int colorTransparent = setAlpha(mainColor, 1.0f / 255.0f * appearValue);
        int colorFull = setAlpha(mainColor, appearValue);
        double radius = ringRadius.get();

        MatrixStack matrices = event.getMatrices();
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; i++) {
            double rad = Math.toRadians(i);
            float px = (float) (x + Math.cos(rad) * radius);
            float pz = (float) (z + Math.sin(rad) * radius);
            float py1 = (float) (y + entityHeight * progress);
            float py2 = (float) (y + entityHeight * progress + eased);

            buffer.vertex(matrix, px, py1, pz).color(colorWithAlpha);
            buffer.vertex(matrix, px, py2, pz).color(colorTransparent);
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.lineWidth(1.5f);
        BufferBuilder lineBuffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; i++) {
            double rad = Math.toRadians(i);
            float px = (float) (x + Math.cos(rad) * radius);
            float pz = (float) (z + Math.sin(rad) * radius);
            float py = (float) (y + entityHeight * progress);

            lineBuffer.vertex(matrix, px, py, pz).color(colorFull);
        }
        BufferRenderer.drawWithGlobalProgram(lineBuffer.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private int setAlpha(int color, float alpha) {
        alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        return (color & 0x00FFFFFF) | ((int) (alpha * 255) << 24);
    }

    private void drawTriangle3D(Event3DRender event) {
        if (appearValue <= 0.001f || lastTargetPos == null) return;

        float partialTicks = mc.getRenderTickCounter().getTickDelta(true);
        Vec3d vec;
        float entityHeight;
        LivingEntity target = lastTarget;

        if (target != null && target.isAlive()) {
            vec = new Vec3d(
                    MathHelper.lerp(partialTicks, target.lastRenderX, target.getX()),
                    MathHelper.lerp(partialTicks, target.lastRenderY, target.getY()),
                    MathHelper.lerp(partialTicks, target.lastRenderZ, target.getZ())
            );
            entityHeight = target.getHeight();
        } else {
            vec = lastTargetPos;
            entityHeight = lastTargetHeight;
        }

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        double x = vec.x - cam.x;
        double y = vec.y - cam.y + entityHeight + 0.8; // Position above head
        double z = vec.z - cam.z;

        int baseCol = getESPColor();
        float hurtPC = getHurtPC(target);
        int redCol = ColorUtils.rgb(255, 3, 3);
        int mainColor = overCol(baseCol, redCol, hurtPC);

        // Треугольник перевернут вершиной вниз и вытянут в высоту
        float triangleWidth = 0.55f;  // Ширина основания
        float triangleHeight = 0.75f; // Высота (вытянутый)

        MatrixStack matrices = event.getMatrices();
        matrices.push();
        matrices.translate(x, y, z);

        // Rotate to face camera
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch()));

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        // Создаем градиент от яркого сверху к прозрачному снизу
        int topColor = setAlpha(mainColor, 0.95f * appearValue);
        int midColor = setAlpha(mainColor, 0.7f * appearValue);
        int bottomColor = setAlpha(mainColor, 0.3f * appearValue);

        // Рисуем несколько слоев для 3D эффекта с глубиной
        for (int layer = 0; layer < 5; layer++) {
            float layerOffset = layer * 0.015f;
            float layerAlpha = 1.0f - (layer * 0.15f);
            
            int layerTopColor = setAlpha(mainColor, 0.95f * appearValue * layerAlpha);
            int layerMidColor = setAlpha(mainColor, 0.7f * appearValue * layerAlpha);
            int layerBottomColor = setAlpha(mainColor, 0.3f * appearValue * layerAlpha);

            BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
            
            // Передняя грань с градиентом (вершина внизу)
            // Верхняя левая точка
            buffer.vertex(matrix, -triangleWidth, layerOffset, layerOffset).color(layerTopColor);
            // Верхняя правая точка
            buffer.vertex(matrix, triangleWidth, layerOffset, layerOffset).color(layerTopColor);
            // Нижняя точка (вершина)
            buffer.vertex(matrix, 0, -triangleHeight, layerOffset).color(layerBottomColor);

            // Задняя грань (для видимости со всех сторон)
            buffer.vertex(matrix, -triangleWidth, layerOffset, -layerOffset).color(layerTopColor);
            buffer.vertex(matrix, 0, -triangleHeight, -layerOffset).color(layerBottomColor);
            buffer.vertex(matrix, triangleWidth, layerOffset, -layerOffset).color(layerTopColor);

            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }

        // Добавляем горизонтальные полосы для эффекта как на фото
        int stripeCount = 6;
        for (int i = 0; i < stripeCount; i++) {
            float stripeProgress = (float) i / (stripeCount - 1);
            float stripeY = stripeProgress * triangleHeight;
            float stripeWidth = triangleWidth * (1.0f - stripeProgress);
            
            float stripeAlpha = 0.6f * (1.0f - stripeProgress * 0.5f);
            int stripeColor = setAlpha(mainColor, stripeAlpha * appearValue);

            RenderSystem.lineWidth(2.5f);
            BufferBuilder stripeBuffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            
            stripeBuffer.vertex(matrix, -stripeWidth, -stripeY, 0).color(stripeColor);
            stripeBuffer.vertex(matrix, stripeWidth, -stripeY, 0).color(stripeColor);

            BufferRenderer.drawWithGlobalProgram(stripeBuffer.end());
        }

        // Рисуем контур треугольника
        RenderSystem.lineWidth(2.5f);
        BufferBuilder lineBuffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        
        int outlineColor = setAlpha(mainColor, 0.9f * appearValue);
        lineBuffer.vertex(matrix, -triangleWidth, 0, 0).color(outlineColor);
        lineBuffer.vertex(matrix, triangleWidth, 0, 0).color(outlineColor);
        lineBuffer.vertex(matrix, 0, -triangleHeight, 0).color(outlineColor);
        lineBuffer.vertex(matrix, -triangleWidth, 0, 0).color(outlineColor);

        BufferRenderer.drawWithGlobalProgram(lineBuffer.end());

        // Добавляем свечение по краям
        for (int glow = 0; glow < 3; glow++) {
            float glowSize = 1.0f + (glow * 0.08f);
            float glowAlpha = 0.25f * (1.0f - glow * 0.3f);
            int glowColor = setAlpha(mainColor, glowAlpha * appearValue);

            BufferBuilder glowBuffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
            
            glowBuffer.vertex(matrix, -triangleWidth * glowSize, 0.02f * glow, 0).color(glowColor);
            glowBuffer.vertex(matrix, triangleWidth * glowSize, 0.02f * glow, 0).color(glowColor);
            glowBuffer.vertex(matrix, 0, -triangleHeight * glowSize, 0).color(glowColor);

            BufferRenderer.drawWithGlobalProgram(glowBuffer.end());
        }

        matrices.pop();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }


    // -------------------------------------------------------------------------
    // Призраки 2 — 4 spiral trails orbiting the entity (ported from Shade)
    // -------------------------------------------------------------------------
    private void drawGhosts2_3D(Event3DRender event) {
        if (appearValue <= 0.001f || lastTargetPos == null) return;

        LivingEntity entity = lastTarget;
        if (entity == null || !entity.isAlive()) return;

        float easedAnim = 1f - (float) Math.pow(1f - Math.min(appearValue, 1f), 3.0);
        if (easedAnim == 0f) return;

        float partialTicks = event.getTickDelta();
        Vec3d entityPos = new Vec3d(
                MathHelper.lerp(partialTicks, entity.lastRenderX, entity.getX()),
                MathHelper.lerp(partialTicks, entity.lastRenderY, entity.getY()),
                MathHelper.lerp(partialTicks, entity.lastRenderZ, entity.getZ())
        );
        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        float camYaw   = mc.gameRenderer.getCamera().getYaw();
        float camPitch = mc.gameRenderer.getCamera().getPitch();

        // relative position of entity feet from camera
        double vecX = entityPos.x - cam.x;
        double vecY = entityPos.y - cam.y;
        double vecZ = entityPos.z - cam.z;

        double iAge = ghostAge - 0.5; // interpolated age
        float movingAngle = (float) iAge * 0.6f; // Замедлено с 3.2f до 1.6f
        float globalAngle = (float) Math.toRadians((float) iAge * -2f); // Замедлено с -4.4f до -2.2f
        float radius    = entity.getWidth() * 1f;
        float centerY   = entity.getHeight() / 1.6f;
        float amplitude = entity.getHeight() / 4.5f;
        int   trailLength = 68;
        float baseSize  = 0f;
        float sizeStep  = 0.00615f;

        int baseCol = getESPColor();
        float hurtPC = getHurtPC(entity);
        int redCol = ColorUtils.rgb(255, 3, 3);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, getBloomTexture());

        MatrixStack matrices = event.getMatrices();

        for (int j = 0; j < 4; j++) {
            for (int seg = 0; seg < trailLength; seg++) {
                float segAngle = movingAngle + (j * 90f) - seg;
                float rad = (float) Math.toRadians(segAngle);
                float px = (float) Math.sin(rad) * radius;
                float pz = (float) Math.cos(rad) * radius;
                float y  = centerY + (float) (Math.cos(Math.toRadians(segAngle * 2f)) * amplitude);
                float x  = px * (float) Math.cos(globalAngle) - pz * (float) Math.sin(globalAngle);
                float z  = px * (float) Math.sin(globalAngle) + pz * (float) Math.cos(globalAngle);

                float offset = (float) (trailLength - seg) / trailLength;
                float scale  = baseSize + sizeStep * (trailLength - seg);

                // fade: blend theme color with a dim version, then apply alpha
                int col = overCol(baseCol, redCol, hurtPC);
                int ghostColor = multAlpha(col, offset * easedAnim * 0.85f);
                if (((ghostColor >> 24) & 0xFF) <= 0) continue;

                double worldX = entityPos.x + x;
                double worldY = entityPos.y + y;
                double worldZ = entityPos.z + z;

                // draw twice for glow effect (same as Shade source)
                drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, scale, ghostColor, 0);
                drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, scale, ghostColor, 0);
            }
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    // -------------------------------------------------------------------------
    // Призраки 3 — 3 orbiting trail dots with hurt-flash (ported from Shade V2)
    // -------------------------------------------------------------------------
    private void drawGhosts3_3D(Event3DRender event) {
        if (appearValue <= 0.001f || lastTargetPos == null) return;

        LivingEntity entity = lastTarget;
        if (entity == null || !entity.isAlive()) return;

        float easedAnim = 1f - (float) Math.pow(1f - Math.min(appearValue, 1f), 3.0);
        if (easedAnim == 0f) return;

        float partialTicks = event.getTickDelta();
        Vec3d entityPos = new Vec3d(
                MathHelper.lerp(partialTicks, entity.lastRenderX, entity.getX()),
                MathHelper.lerp(partialTicks, entity.lastRenderY, entity.getY()),
                MathHelper.lerp(partialTicks, entity.lastRenderZ, entity.getZ())
        );
        Vec3d cam = mc.gameRenderer.getCamera().getPos();

        final double bx = entityPos.x;
        final double by = entityPos.y;
        final double bz = entityPos.z;

        final float baseSizePx = 0.3f;
        final float radius = 0.7f;
        final int trailLength = 55;

        // ── УБРАНА ЛОГИКА BURST НА УДАР: призраки двигаются плавно всегда ──
        // Постоянная скорость движения без рывков
        final double t = System.currentTimeMillis() / 350.0;

        // Цвет при ударе (только цвет меняется, не поведение)
        float hurtFactor = MathHelper.clamp(entity.hurtTime / 10f, 0f, 1f);
        final float hurtPC = (float) Math.sin(entity.hurtTime * (18f * Math.PI / 180f));

        final float[] baseHeights = {
                entity.getHeight() * 0.85f,
                entity.getHeight() * 0.55f,
                entity.getHeight() * 0.25f
        };
        final double[] angles = {
                Math.toRadians(0),
                Math.toRadians(120),
                Math.toRadians(240)
        };

        @SuppressWarnings("unchecked")
        java.util.List<Vec3d>[] trails = new java.util.List[]{
                phantomTrail1.computeIfAbsent(entity, k -> new java.util.ArrayList<>()),
                phantomTrail2.computeIfAbsent(entity, k -> new java.util.ArrayList<>()),
                phantomTrail3.computeIfAbsent(entity, k -> new java.util.ArrayList<>())
        };

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, getBloomTexture());

        MatrixStack matrices = event.getMatrices();
        int baseCol = getESPColor();

        for (int k = 0; k < 3; k++) {
            // Плавное вертикальное движение БЕЗ изменения на удар
            float verticalAmp = 0.16f;
            double verticalMove = Math.sin(t * 1.5 + k * 2.0) * verticalAmp;
            double height = baseHeights[k] + verticalMove;
            
            // Плавное вращение БЕЗ ускорения на удар
            double rotSpeed = t * 1.3;
            double angle = angles[k] + rotSpeed;
            double baseX = Math.cos(angle) * radius;
            double baseZ = Math.sin(angle) * radius;

            Vec3d currentWorldPos = new Vec3d(bx + baseX, by + height, bz + baseZ);
            java.util.List<Vec3d> trail = trails[k];
            trail.add(0, currentWorldPos);
            while (trail.size() > trailLength) trail.remove(trail.size() - 1);

            for (int j = 0; j < trail.size(); j++) {
                Vec3d pos = trail.get(j);
                float progress = j / (float) trailLength;
                float alphaMul = (float) Math.pow(1.0f - progress, 1.5);
                
                // ── ФИКСИРОВАННЫЙ РАЗМЕР: не меняется при ударе ──
                float dynSize = baseSizePx * (1.0f - progress * 0.3f); // Плавное уменьшение вдоль следа
                int dynAlpha = MathHelper.clamp((int) (255 * easedAnim * alphaMul), 0, 255);

                int dotColor;
                if (hurtFactor > 0.05f) {
                    // При ударе - красноватый цвет
                    int r = 255;
                    int g = 80 + (int) (80 * (1 - hurtFactor));
                    int b = 80 + (int) (80 * (1 - hurtFactor));
                    dotColor = ColorUtils.rgba(r, g, b, dynAlpha);
                } else {
                    // Обычный цвет темы
                    dotColor = ColorUtils.rgba(
                            ColorUtils.red(baseCol),
                            ColorUtils.green(baseCol),
                            ColorUtils.blue(baseCol),
                            dynAlpha
                    );
                }

                // Легкий переход к красному при ударе
                int redOverlay = ColorUtils.rgba(255, 80, 80, dynAlpha);
                float overFactor = MathHelper.clamp(hurtPC * hurtFactor, 0f, 1f);
                int mixedColor = overCol(dotColor, redOverlay, overFactor);
                int color = ColorUtils.rgba(
                        ColorUtils.red(mixedColor),
                        ColorUtils.green(mixedColor),
                        ColorUtils.blue(mixedColor),
                        dynAlpha
                );

                // Гало свечение (постоянный размер)
                float glowSize = dynSize * 1.3f;
                int haloAlpha = MathHelper.clamp((int) (dynAlpha * 0.4f), 0, 255);
                int haloColor = ColorUtils.rgba(
                        ColorUtils.red(color), ColorUtils.green(color), ColorUtils.blue(color), haloAlpha);
                drawStaticBillboard(matrices, cam, pos.x, pos.y, pos.z, glowSize, haloColor, 0);

                // Основная точка
                drawStaticBillboard(matrices, cam, pos.x, pos.y, pos.z, dynSize, color, 0);
            }
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    @EventLink(priority = Priority.LOW)
    public void onRender2D(EventRender.Default event) {        if (!mode.is("Кристаллы") || crystalAnimation <= 0.001f || lastTargetPos == null) return;
        LivingEntity crystalTarget = lastTarget != null && lastTarget.isAlive() ? lastTarget : null;
        drawCrystalGlow2D(event.getContext().getMatrices(), crystalTarget);
    }

    private int multAlpha(int color, float mult) {
        int a = (int) (((color >> 24) & 0xFF) * mult);
        a = Math.max(0, Math.min(255, a));
        return (a << 24) | (color & 0x00FFFFFF);
    }

    private int replAlpha(int color, int alpha) {
        alpha = Math.max(0, Math.min(255, alpha));
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    int overCol(int color1, int color2, float factor) {
        factor = Math.max(0f, Math.min(1f, factor));
        int r1 = (color1 >> 16) & 0xFF, g1 = (color1 >> 8) & 0xFF, b1 = color1 & 0xFF, a1 = (color1 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF, g2 = (color2 >> 8) & 0xFF, b2 = color2 & 0xFF, a2 = (color2 >> 24) & 0xFF;
        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);
        int a = (int) (a1 + (a2 - a1) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private float getHurtPC(LivingEntity target) {
        if (!hurtColor.isState() || target == null) return 0f;
        float partialTicks = mc != null ? mc.getRenderTickCounter().getTickDelta(true) : 0f;
        float hurtTicks = MathHelper.clamp(target.hurtTime - partialTicks, 0.0f, 10.0f);
        float progress = hurtTicks / 10.0f;
        return progress * progress * (3.0f - 2.0f * progress);
    }

    private void drawBillboard(MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float baseScreenSize, int color, float rotation) {
        float distScale = getDistanceScale(cameraPos, worldX, worldY, worldZ);
        float half = baseScreenSize * distScale * 0.5f;
        drawBillboardInternal(matrices, cameraPos, worldX, worldY, worldZ, half, color, rotation);
    }

    private void drawStaticBillboard(MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float worldSize, int color, float rotation) {
        float half = worldSize * 0.5f;
        drawBillboardInternal(matrices, cameraPos, worldX, worldY, worldZ, half, color, rotation);
    }

    private void drawBillboardInternal(MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float half, int color, float rotation) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        if (a <= 0) return;

        matrices.push();
        matrices.translate(worldX - cameraPos.x, worldY - cameraPos.y, worldZ - cameraPos.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch()));
        if (rotation != 0) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
        }

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, -half, -half, 0).texture(0, 1).color(r, g, b, a);
        buffer.vertex(matrix, -half, half, 0).texture(0, 0).color(r, g, b, a);
        buffer.vertex(matrix, half, half, 0).texture(1, 0).color(r, g, b, a);
        buffer.vertex(matrix, half, -half, 0).texture(1, 1).color(r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        matrices.pop();
    }

    private void renderMarker3D(Event3DRender event) {
        if (lastTargetPos == null || appearValue <= 0.001f) return;

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        double worldX = lastTargetPos.x;
        double worldY = lastTargetPos.y + ((lastTargetHeight + 0.4f) * 0.5f);
        double worldZ = lastTargetPos.z;

        float baseSize = size.getValue().floatValue() * 12f;
        float renderSize = baseSize * scaleValue;

        long now = System.currentTimeMillis();
        float dt = Math.max(0.001f, (now - lastRotateUpdate) / 1000f);
        lastRotateUpdate = now;
        float cycleDuration = Math.max(0.35f, 2.2f / rotateSpeed.getValue().floatValue());
        rotProgress += dt / cycleDuration;
        while (rotProgress >= 1f) {
            rotProgress -= 1f;
            rotFrom = rotTo;
            rotTo = rotTo > 0f ? -280f : 280f;
        }

        float accel = (float) Easings.SINE_IN_OUT.ease(rotProgress);
        float rotation = MathHelper.lerp(accel, rotFrom, rotTo);

        float hurtPC = getHurtPC(lastTarget);
        int baseColor = multAlpha(getESPColor(), appearValue);
        int redColor = multAlpha(ColorUtils.rgb(255, 3, 3), appearValue);
        int color = overCol(baseColor, redColor, hurtPC);

        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, getCaptureTexture());

        drawBillboard(event.getMatrices(), cam, worldX, worldY, worldZ, renderSize, color, rotation);

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void drawSouls3D(Event3DRender event) {
        if (appearValue <= 0.001f || lastTargetPos == null) return;

        float partialTicks = mc.getRenderTickCounter().getTickDelta(true);
        Vec3d vec;
        float height;
        LivingEntity target = lastTarget;
        if (target != null && target.isAlive()) {
            vec = new Vec3d(
                    MathHelper.lerp(partialTicks, target.lastRenderX, target.getX()),
                    MathHelper.lerp(partialTicks, target.lastRenderY, target.getY()),
                    MathHelper.lerp(partialTicks, target.lastRenderZ, target.getZ())
            );
            height = target.getHeight();
        } else {
            vec = lastTargetPos;
            height = lastTargetHeight;
        }

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        double baseX = vec.x;
        double baseY = vec.y + (height / 2.0f);
        double baseZ = vec.z;
        double radius = 0.7;
        float fixedSize = 4.0f;
        long time = System.currentTimeMillis();
        float hurtPC = getHurtPC(target);
        int baseCol = getESPColor();
        int redCol = ColorUtils.rgb(255, 3, 3);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, getBloomTexture());

        MatrixStack matrices = event.getMatrices();

        for (int i = 0; i < 20; i++) {
            float trailFactor = 1.0f - (float) i / 20.0f * 0.7f;
            double angle = 0.15 * (time - i * 10.0) / 25.0;
            double s = Math.sin(angle) * radius;
            double c = Math.cos(angle) * radius;
            double worldX = baseX + s;
            double worldY = baseY + c;
            double worldZ = baseZ - c;

            float sz = fixedSize * trailFactor;
            float alphaTrail = appearValue * GHOST_ALPHA_MULT;
            int col = multAlpha(baseCol, alphaTrail * appearValue);
            int red = multAlpha(redCol, alphaTrail * appearValue);
            int color = overCol(col, red, hurtPC);

            drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, sz * 0.12f, color, 0);
            int glowColor = multAlpha(color, 0.45f);
            drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, sz * 0.21f, glowColor, 0);
        }

        for (int i = 0; i < 20; i++) {
            float trailFactor = 1.0f - (float) i / 20.0f * 0.7f;
            double angle = 0.15 * (time - i * 10.0) / 25.0;
            double s = Math.sin(angle) * radius;
            double c = Math.cos(angle) * radius;
            double worldX = baseX - s;
            double worldY = baseY + s;
            double worldZ = baseZ - c;

            float sz = fixedSize * trailFactor;
            float alphaTrail = appearValue * GHOST_ALPHA_MULT;
            int col = multAlpha(baseCol, alphaTrail * appearValue);
            int red = multAlpha(ColorUtils.rgb(235, 7, 7), alphaTrail * appearValue);
            int color = overCol(col, red, hurtPC);

            drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, sz * 0.12f, color, 0);
            int glowColor = multAlpha(color, 0.45f);
            drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, sz * 0.21f, glowColor, 0);
        }

        for (int i = 0; i < 20; i++) {
            float trailFactor = 1.0f - (float) i / 20.0f * 0.7f;
            double angle = 0.15 * (time - i * 10.0) / 25.0;
            double s = Math.sin(angle) * radius;
            double c = Math.cos(angle) * radius;
            double worldX = baseX - s;
            double worldY = baseY - s;
            double worldZ = baseZ + c;

            float sz = fixedSize * trailFactor;
            float alphaTrail = appearValue * GHOST_ALPHA_MULT;
            int col = multAlpha(baseCol, alphaTrail * appearValue);
            int red = multAlpha(redCol, alphaTrail * appearValue);
            int color = overCol(col, red, hurtPC);

            drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, sz * 0.12f, color, 0);
            int glowColor = multAlpha(color, 0.45f);
            drawStaticBillboard(matrices, cam, worldX, worldY, worldZ, sz * 0.21f, glowColor, 0);
        }

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    private void addBMWGhosts(LivingEntity entity, float partialTicks, int cornersCount, int maxTime, int colorBase) {
        float xzRange = 0.7f;
        float yRange = entity.getHeight();
        int delayXZ = (int) bmwStrengthXZ.getValue().floatValue();
        int delayY = (int) bmwStrengthY.getValue().floatValue();
        long time = System.currentTimeMillis();
        float rotateProgress = (float) (time % delayXZ) / (float) delayXZ;
        float xzRotate = rotateProgress * 360.0f;
        float yProgress = (float) (time % delayY) / (float) delayY;
        float yLrpPC = 0.5f - 0.5f * MathHelper.cos(yProgress * (float) (Math.PI * 2.0));

        for (int corner = 0; corner < cornersCount; corner++) {
            float cornersPC = (float) corner / (float) cornersCount;
            double yawRad = Math.toRadians(MathHelper.wrapDegrees(cornersPC * 360.0f + xzRotate));
            float offsetX = -(float) Math.sin(yawRad) * xzRange;
            float offsetY = yRange * yLrpPC;
            float offsetZ = (float) Math.cos(yawRad) * xzRange;
            bmwPoints.add(new GlowPoint(offsetX, offsetY, offsetZ, maxTime, colorBase));
        }
    }

    private void drawBMW3D(Event3DRender event) {
        if (bmwPoints.isEmpty() || appearValue <= 0.001f) return;

        LivingEntity renderTarget = lastTarget != null ? lastTarget : lastHandledTarget;
        if (renderTarget == null && lastTargetPos == null) return;

        float partialTicks = mc.getRenderTickCounter().getTickDelta(true);
        Vec3d basePos;
        if (renderTarget != null && renderTarget.isAlive()) {
            basePos = new Vec3d(
                    MathHelper.lerp(partialTicks, renderTarget.lastRenderX, renderTarget.getX()),
                    MathHelper.lerp(partialTicks, renderTarget.lastRenderY, renderTarget.getY()),
                    MathHelper.lerp(partialTicks, renderTarget.lastRenderZ, renderTarget.getZ())
            );
        } else {
            basePos = lastTargetPos;
        }

        if (basePos == null) return;

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        float hurtPC = getHurtPC(renderTarget);
        float fixedScreenSize = 6f;

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, getBloomTexture());

        MatrixStack matrices = event.getMatrices();

        for (GlowPoint point : bmwPoints) {
            float timePC = point.getTimeProgress();
            float trailFactor = 1.0f - timePC * 0.6f;

            double worldX = basePos.x + point.x;
            double worldY = basePos.y + point.y;
            double worldZ = basePos.z + point.z;

            float sz = fixedScreenSize * trailFactor;
            int alpha = (int) (255 * appearValue * trailFactor * 0.8f);
            alpha = Math.max(0, Math.min(255, alpha));
            int col = replAlpha(point.baseColor, alpha);
            int red = replAlpha(ColorUtils.rgb(255, 3, 3), alpha);
            int finalColor = overCol(col, red, hurtPC);

            drawBillboard(matrices, cam, worldX, worldY, worldZ, sz, finalColor, 0);
        }

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    private void drawCelka3D(Event3DRender event) {
        if (appearValue <= 0.001f || lastTargetPos == null) return;

        float partialTicks = mc.getRenderTickCounter().getTickDelta(true);
        Vec3d vec;
        float entityHeight;
        LivingEntity target = lastTarget;
        if (target != null && target.isAlive()) {
            vec = new Vec3d(
                    MathHelper.lerp(partialTicks, target.lastRenderX, target.getX()),
                    MathHelper.lerp(partialTicks, target.lastRenderY, target.getY()),
                    MathHelper.lerp(partialTicks, target.lastRenderZ, target.getZ())
            );
            entityHeight = target.getHeight();
        } else {
            vec = lastTargetPos;
            entityHeight = lastTargetHeight;
        }

        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        double bx = vec.x;
        double by = vec.y;
        double bz = vec.z;
        double t = (System.currentTimeMillis() / 384.61539872299335) * CELKA_SPEED_MULT;
        double tv = (System.currentTimeMillis() / 666.6666666666666) * CELKA_SPEED_MULT;
        int baseCol = getESPColor();
        float fixedSize = 4.0f;

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, getBloomTexture());

        MatrixStack matrices = event.getMatrices();

        float radius = 0.65f;
        for (int k = 0; k < 4; k++) {
            for (int j = 0; j < 20; j++) {
                float kf = (float) j / 20.0f;
                float sizeFactor = 1.0f - kf * 0.55f;

                double tj = t - j * 0.05;
                double tvj = tv - j * 0.05;
                double cyc = (Math.sin(tvj) + 1.0) * 0.5;

                double baseAngle = Math.toRadians(k * 90.0 + (tj * 50.0) % 360.0);
                double offX = Math.cos(baseAngle) * radius;
                double offZ = Math.sin(baseAngle) * radius;
                double offY = k % 2 == 0
                        ? 0.1 + 1.7 * cyc
                        : 1.8 - 1.7 * cyc;

                double worldX = bx + offX;
                double worldY = by + offY;
                double worldZ = bz + offZ;

                float sz = fixedSize * sizeFactor;
                int finalAlpha = (int) (255.0f * appearValue * GHOST_ALPHA_MULT);
                int color = replAlpha(baseCol, finalAlpha);

                drawBillboard(matrices, cam, worldX, worldY, worldZ, sz, color, 0);
                int glowColor = multAlpha(color, 0.45f);
                drawBillboard(matrices, cam, worldX, worldY, worldZ, sz * 1.75f, glowColor, 0);
            }
            radius *= -1.0f;
        }

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }



    private void drawNursultanPrizrak_3D(Event3DRender event) {
        if (appearValue <= 0.001f || lastTargetPos == null) return;

        LivingEntity entity = lastTarget;
        if (entity == null || !entity.isAlive()) return;

        MatrixStack matrices = event.getMatrices();
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();

        long currentTimeMs = System.currentTimeMillis();
        float animationNurik = (float) (currentTimeMs - nursultanTimestamp) / 120.0F;

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, getBloomTexture());
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        double x = MathHelper.lerp(event.getTickDelta(), entity.lastRenderX, entity.getX()) - camPos.x;
        double y = MathHelper.lerp(event.getTickDelta(), entity.lastRenderY, entity.getY()) - camPos.y;
        double z = MathHelper.lerp(event.getTickDelta(), entity.lastRenderZ, entity.getZ()) - camPos.z;

        int n2 = 3, n3 = 12, n4 = 3 * n2;

        matrices.push();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        int baseCol = getESPColor();
        int r = (baseCol >> 16) & 0xFF;
        int g = (baseCol >> 8) & 0xFF;
        int b = baseCol & 0xFF;

        for (int i = 0; i < n4; i += n2) {
            for (int j = 0; j < n3; ++j) {
                float f2 = animationNurik + (float) j * 0.1F;
                int n5 = (int) Math.pow((double) i, 2.0D);

                matrices.push();
                matrices.translate(
                    x + (double) (0.8F * MathHelper.sin(f2 + (float) n5)),
                    y + 0.5 + (double) (0.3F * MathHelper.sin(animationNurik + (float) j * 0.2F)) + (double) (0.2F * (float) i),
                    z + (double) (0.8F * MathHelper.cos(f2 - (float) n5))
                );

                float scale = appearValue * (0.005F + (float) j / 2000.0F);
                matrices.scale(scale, scale, scale);
                matrices.multiply(mc.gameRenderer.getCamera().getRotation());

                int n7 = -25, n8 = 50;
                int alpha = (int) (appearValue * 600.0F);
                alpha = Math.max(0, Math.min(255, alpha));
                int rgba = ColorUtils.rgba(r, g, b, alpha);

                Matrix4f matrix = matrices.peek().getPositionMatrix();
                buffer.vertex(matrix, (float) n7, (float) (n7 + n8), 0.0F).texture(0.0F, 1.0F).color(rgba);
                buffer.vertex(matrix, (float) (n7 + n8), (float) (n7 + n8), 0.0F).texture(1.0F, 1.0F).color(rgba);
                buffer.vertex(matrix, (float) (n7 + n8), (float) n7, 0.0F).texture(1.0F, 0.0F).color(rgba);
                buffer.vertex(matrix, (float) n7, (float) n7, 0.0F).texture(0.0F, 0.0F).color(rgba);

                matrices.pop();
            }
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        matrices.pop();

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableCull();
    }

    private void renderCrystals3D(MatrixStack ms, LivingEntity target, float partialTicks) {
        if (lastTargetPos == null || crystalAnimation <= 0.01f) return;

        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
        int baseColor = ColorUtils.getThemeColor();
        int color = multAlpha(baseColor, crystalAnimation);
        int glowColor = multAlpha(baseColor, crystalAnimation * 0.28f);
        float hurtPC = getHurtPC(target);
        if (hurtPC > 0.0f) {
            int hurtColor = multAlpha(ColorUtils.rgb(255, 3, 3), crystalAnimation);
            color = overCol(color, hurtColor, hurtPC);
            glowColor = overCol(glowColor, multAlpha(hurtColor, 0.65f), hurtPC);
        }

        float entityWidth = target != null ? target.getWidth() : lastTargetWidth;
        float entityHeight = target != null ? target.getHeight() : lastTargetHeight;
        float width = entityWidth * 1.5f;

        Vec3d renderPos;
        if (target != null && target.isAlive()) {
            renderPos = new Vec3d(
                    MathHelper.lerp(partialTicks, target.lastRenderX, target.getX()),
                    MathHelper.lerp(partialTicks, target.lastRenderY, target.getY()),
                    MathHelper.lerp(partialTicks, target.lastRenderZ, target.getZ())
            );
        } else {
            renderPos = lastTargetPos;
        }

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        float orbitScale = 1.2f - 0.5f * crystalAnimation;
        ms.push();
        ms.translate(renderPos.x - cameraPos.x, renderPos.y - cameraPos.y, renderPos.z - cameraPos.z);

        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

        for (int i = 0; i < 360; i += 20) {
            float angleRad = (float) Math.toRadians(i + crystalRotationAngle);
            float sin = (float) (Math.sin(angleRad) * width * orbitScale);
            float cos = (float) (Math.cos(angleRad) * width * orbitScale);
            float crystalSize = 0.1f;
            float yOffset = 0.1f + entityHeight * Math.abs(MathHelper.sin(i));

            float offsetX = sin;
            float offsetY = yOffset;
            float offsetZ = cos;
            float targetCenterY = entityHeight / 2.0f;
            float dirX = -offsetX;
            float dirY = targetCenterY - offsetY;
            float dirZ = -offsetZ;

            float length = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
            if (length < 0.001f) continue;

            dirX /= length;
            dirY /= length;
            dirZ /= length;
            ms.push();
            ms.translate(offsetX, offsetY, offsetZ);
            Vector3f initial = new Vector3f(0, 1, 0);
            Vector3f dir = new Vector3f(dirX, dirY, dirZ);
            Vector3f axis = new Vector3f();
            initial.cross(dir, axis);
            float axisLen = axis.length();
            if (axisLen >= 0.001f) {
                axis.div(axisLen);
                float dot = Math.max(-1f, Math.min(1f, initial.dot(dir)));
                float angle = (float) Math.acos(dot);
                ms.multiply(new Quaternionf().setAngleAxis(angle, axis.x, axis.y, axis.z));
            }
            renderCrystalShape(buffer, ms.peek().getPositionMatrix(), crystalSize, color);

            ms.pop();
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        ms.pop();

        float glowBaseSize = 4.5f + entityWidth * 3.0f;
        float outerGlowSize = glowBaseSize * 1.28f;
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, getBloomTexture());

        for (int i = 0; i < 360; i += 20) {
            float angleRad = (float) Math.toRadians(i + crystalRotationAngle);
            float sin = (float) (Math.sin(angleRad) * width * orbitScale);
            float cos = (float) (Math.cos(angleRad) * width * orbitScale);
            float yOffset = 0.1f + entityHeight * Math.abs(MathHelper.sin(i));

            double worldX = renderPos.x + sin;
            double worldY = renderPos.y + yOffset;
            double worldZ = renderPos.z + cos;
            drawBillboard(ms, cameraPos, worldX, worldY, worldZ, outerGlowSize, multAlpha(glowColor, 0.24f), crystalRotationAngle + i);
            drawBillboard(ms, cameraPos, worldX, worldY, worldZ, glowBaseSize, glowColor, -(crystalRotationAngle + i * 0.5f));
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    private void renderCrystalShape(BufferBuilder buffer, Matrix4f matrix, float size, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        float w = 0.34f * size / 0.1f;
        float h = 1.15f * size / 0.1f;
        w = 0.06f;
        h = 0.2f;
        tri(buffer, matrix, 0, h, 0, w, 0, 0, 0, 0, w, r, g, b, a);
        tri(buffer, matrix, 0, h, 0, 0, 0, w, -w, 0, 0, r, g, b, a);
        tri(buffer, matrix, 0, h, 0, -w, 0, 0, 0, 0, -w, r, g, b, a);
        tri(buffer, matrix, 0, h, 0, 0, 0, -w, w, 0, 0, r, g, b, a);
        tri(buffer, matrix, 0, -h, 0, w, 0, 0, 0, 0, w, r, g, b, a);
        tri(buffer, matrix, 0, -h, 0, 0, 0, w, -w, 0, 0, r, g, b, a);
        tri(buffer, matrix, 0, -h, 0, -w, 0, 0, 0, 0, -w, r, g, b, a);
        tri(buffer, matrix, 0, -h, 0, 0, 0, -w, w, 0, 0, r, g, b, a);
    }

    private float[] project2D(double worldX, double worldY, double worldZ) {
        return null;
    }

    private double getScale(double worldX, double worldY, double worldZ) {
        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        double dx = worldX - cam.x;
        double dy = worldY - cam.y;
        double dz = worldZ - cam.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return Math.max(0.5, 8.0 / Math.max(0.1, distance));
    }

    private void drawTexturedRect2D(MatrixStack matrix, float x, float y, float width, float height, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        if (a <= 0) return;
        Matrix4f mat = matrix.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(mat, x, y, 0).texture(0, 0).color(r, g, b, a);
        buffer.vertex(mat, x, y + height, 0).texture(0, 1).color(r, g, b, a);
        buffer.vertex(mat, x + width, y + height, 0).texture(1, 1).color(r, g, b, a);
        buffer.vertex(mat, x + width, y, 0).texture(1, 0).color(r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    private void drawCrystalGlow2D(MatrixStack matrix, LivingEntity target) {
        return;
    }

    private void tri(BufferBuilder buffer, Matrix4f matrix,
                     float x1, float y1, float z1,
                     float x2, float y2, float z2,
                     float x3, float y3, float z3,
                     int r, int g, int b, int a) {
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x3, y3, z3).color(r, g, b, a);
    }

    private static class GlowPoint {
        final float x, y, z;
        final long startTime;
        final int maxLife;
        final int baseColor;

        GlowPoint(float x, float y, float z, int maxLife, int baseColor) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.startTime = System.currentTimeMillis();
            this.maxLife = maxLife;
            this.baseColor = baseColor;
        }

        boolean shouldRemove() {
            return System.currentTimeMillis() - startTime >= maxLife;
        }

        float getTimeProgress() {
            return MathHelper.clamp((System.currentTimeMillis() - startTime) / (float) maxLife, 0f, 1f);
        }

        int getColor(float timePC) {
            int a = (int) (((baseColor >> 24) & 0xFF) * (1.0f - timePC));
            a = Math.max(0, Math.min(255, a));
            return (a << 24) | (baseColor & 0x00FFFFFF);
        }
    }
}

class CubeParticle implements QClient {
    double x, y, z;
    double worldX, worldY, worldZ;
    long time;
    LivingEntity entity;
    boolean fading;
    long fadeStartTime;
    float vx, vy, vz;
    float rotX, rotY, rotZ;
    float rotSpeedX, rotSpeedY, rotSpeedZ;

    public CubeParticle(LivingEntity entity, double x, double y, double z) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = System.currentTimeMillis();
        this.rotX = (float) (Math.random() * 360.0);
        this.rotY = (float) (Math.random() * 360.0);
        this.rotZ = (float) (Math.random() * 360.0);
        this.rotSpeedX = 1.4f + (float) Math.random() * 3.4f;
        this.rotSpeedY = 1.4f + (float) Math.random() * 3.4f;
        this.rotSpeedZ = 1.4f + (float) Math.random() * 3.4f;
        this.vx = (float) ((Math.random() - 0.5) * 0.0022);
        this.vy = 0.031f + (float) Math.random() * 0.020f;
        this.vz = (float) ((Math.random() - 0.5) * 0.0022);
    }

    public void update(float dt, long now, LivingEntity currentTarget) {
        float step = dt * 60.0f;
        rotX += rotSpeedX * step;
        rotY += rotSpeedY * step;
        rotZ += rotSpeedZ * step;

        if (!fading) {
            x += vx * step;
            y += vy * step;
            z += vz * step;
            vx *= 0.992f;
            vz *= 0.992f;
            vy *= 0.989f;

            if (entity != null) {
                double shoulderHeight = Math.max(2.2, entity.getHeight() * 1.85);
                if (y >= shoulderHeight) {
                    y = shoulderHeight;
                    beginFade(now);
                    return;
                }
            }

            boolean targetLost = currentTarget == null || entity == null || !entity.isAlive() || entity != currentTarget;
            if (targetLost || now - time >= TargetESP.CUBE_ATTACH_LIFE_MS) {
                beginFade(now);
            }
            return;
        }
    }

    public boolean shouldRemove(long now) {
        return fading && now - fadeStartTime >= TargetESP.CUBE_FADE_LIFE_MS;
    }

    public int getRenderColor(int baseColor, int redColor, float hurtPC, long now) {
        float alpha = getAlpha(now);
        if (alpha <= 0.001f) {
            return 0;
        }
        int color = ColorUtils.replAlpha(baseColor, (int) (alpha * 255.0f));
        int hurt = ColorUtils.replAlpha(redColor, (int) (alpha * 255.0f));
        return TargetESP.INSTANCE.overCol(color, hurt, hurtPC);
    }

    public boolean appendCubeFaces(BufferBuilder faceBuilder, MatrixStack ms, Vec3d cam,
                                   float partialTicks, int color) {
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        if (alpha <= 0.001f) return false;

        Vec3d renderPos = getRenderPos(partialTicks);
        if (renderPos == null) return false;

        float fadeScale = fading
                ? MathHelper.lerp(MathHelper.clamp((System.currentTimeMillis() - fadeStartTime) / (float) TargetESP.CUBE_FADE_LIFE_MS, 0.0f, 1.0f), 1.0f, 0.45f)
                : 1.0f;
        float scale = 0.12f * fadeScale;

        ms.push();
        ms.translate(renderPos.x - cam.x, renderPos.y - cam.y, renderPos.z - cam.z);
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotX));
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotY));
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotZ));
        ms.scale(scale, scale, scale);
        Matrix4f m = ms.peek().getPositionMatrix();

        appendFaces(faceBuilder, m, color);
        ms.pop();
        return true;
    }

    public boolean appendCubeLines(BufferBuilder lineBuilder, MatrixStack ms, Vec3d cam,
                                   float partialTicks, int color) {
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        if (alpha <= 0.001f) return false;

        Vec3d renderPos = getRenderPos(partialTicks);
        if (renderPos == null) return false;

        float fadeScale = fading
                ? MathHelper.lerp(MathHelper.clamp((System.currentTimeMillis() - fadeStartTime) / (float) TargetESP.CUBE_FADE_LIFE_MS, 0.0f, 1.0f), 1.0f, 0.45f)
                : 1.0f;
        float scale = 0.12f * fadeScale;

        ms.push();
        ms.translate(renderPos.x - cam.x, renderPos.y - cam.y, renderPos.z - cam.z);
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotX));
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotY));
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotZ));
        ms.scale(scale, scale, scale);
        Matrix4f m = ms.peek().getPositionMatrix();

        appendEdges(lineBuilder, m, ColorUtils.replAlpha(color, Math.max(1, (int) (((color >> 24) & 0xFF) * 0.7f))));
        ms.pop();
        return true;
    }

    public boolean appendBloom(BufferBuilder builder, MatrixStack ms, Vec3d camPos, float camYaw, float camPitch,
                               float partialTicks, int colorInt, long now) {
        float alpha = getAlpha(now);
        if (alpha <= 0.001f) return false;

        Vec3d renderPos = getRenderPos(partialTicks);
        if (renderPos == null) return false;

        float fadeScale = fading
                ? MathHelper.lerp(MathHelper.clamp((now - fadeStartTime) / (float) TargetESP.CUBE_FADE_LIFE_MS, 0.0f, 1.0f), 1.0f, 0.55f)
                : 1.0f;
        float glowScale = 0.95f * fadeScale;
        int ai = (int) (alpha * 0.15f * 255.0f);
        if (ai <= 0) return false;

        int r = (colorInt >> 16) & 0xFF;
        int g = (colorInt >> 8) & 0xFF;
        int b = colorInt & 0xFF;

        ms.push();
        ms.translate(renderPos.x - camPos.x, renderPos.y - camPos.y, renderPos.z - camPos.z);
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camYaw));
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camPitch));
        ms.scale(glowScale, glowScale, glowScale);
        Matrix4f m = ms.peek().getPositionMatrix();

        builder.vertex(m, -0.5f, 0.5f, 0).texture(0f, 1f).color(r, g, b, ai);
        builder.vertex(m, 0.5f, 0.5f, 0).texture(1f, 1f).color(r, g, b, ai);
        builder.vertex(m, 0.5f, -0.5f, 0).texture(1f, 0f).color(r, g, b, ai);
        builder.vertex(m, -0.5f, -0.5f, 0).texture(0f, 0f).color(r, g, b, ai);
        ms.pop();
        return true;
    }

    private void beginFade(long now) {
        if (fading) return;
        Vec3d renderPos = getRenderPos(1.0f);
        if (renderPos != null) {
            worldX = renderPos.x;
            worldY = renderPos.y;
            worldZ = renderPos.z;
        }
        fadeStartTime = now;
        fading = true;
        entity = null;
    }

    private float getAlpha(long now) {
        if (!fading) {
            float fadeIn = MathHelper.clamp((now - time) / 140.0f, 0.0f, 1.0f);
            float preFade = 1.0f - MathHelper.clamp((now - time - (TargetESP.CUBE_ATTACH_LIFE_MS - 120L)) / 120.0f, 0.0f, 0.35f);
            return fadeIn * preFade;
        }
        return 1.0f - MathHelper.clamp((now - fadeStartTime) / (float) TargetESP.CUBE_FADE_LIFE_MS, 0.0f, 1.0f);
    }

    private Vec3d getRenderPos(float partialTicks) {
        if (fading || entity == null) {
            return new Vec3d(worldX, worldY, worldZ);
        }
        return new Vec3d(
                MathHelper.lerp(partialTicks, entity.lastRenderX, entity.getX()) + x,
                MathHelper.lerp(partialTicks, entity.lastRenderY, entity.getY()) + y,
                MathHelper.lerp(partialTicks, entity.lastRenderZ, entity.getZ()) + z
        );
    }

    private void appendFaces(BufferBuilder fb, Matrix4f m, int color) {
        float min = -0.5f, max = 0.5f;
        int fillColor = ColorUtils.replAlpha(color, Math.max(1, (int) (((color >> 24) & 0xFF) * 0.16f)));
        addFace(fb, m, min, min, min, max, max, max, fillColor);
    }

    private void appendEdges(BufferBuilder buf, Matrix4f m, int color) {
        for (byte[] edge : TargetESP.CUBE_EDGES) {
            buf.vertex(m, edge[0] * 0.5f, edge[1] * 0.5f, edge[2] * 0.5f).color(color);
            buf.vertex(m, edge[3] * 0.5f, edge[4] * 0.5f, edge[5] * 0.5f).color(color);
        }
    }

    private void addFace(BufferBuilder buf, Matrix4f m,
                         float x1, float y1, float z1, float x2, float y2, float z2,
                         int color) {
        buf.vertex(m, x1, y1, z1).color(color);
        buf.vertex(m, x2, y1, z1).color(color);
        buf.vertex(m, x2, y1, z2).color(color);
        buf.vertex(m, x1, y1, z2).color(color);

        buf.vertex(m, x1, y2, z1).color(color);
        buf.vertex(m, x1, y2, z2).color(color);
        buf.vertex(m, x2, y2, z2).color(color);
        buf.vertex(m, x2, y2, z1).color(color);

        buf.vertex(m, x1, y1, z1).color(color);
        buf.vertex(m, x1, y2, z1).color(color);
        buf.vertex(m, x2, y2, z1).color(color);
        buf.vertex(m, x2, y1, z1).color(color);

        buf.vertex(m, x1, y1, z2).color(color);
        buf.vertex(m, x2, y1, z2).color(color);
        buf.vertex(m, x2, y2, z2).color(color);
        buf.vertex(m, x1, y2, z2).color(color);

        buf.vertex(m, x1, y1, z1).color(color);
        buf.vertex(m, x1, y1, z2).color(color);
        buf.vertex(m, x1, y2, z2).color(color);
        buf.vertex(m, x1, y2, z1).color(color);

        buf.vertex(m, x2, y1, z1).color(color);
        buf.vertex(m, x2, y2, z1).color(color);
        buf.vertex(m, x2, y2, z2).color(color);
        buf.vertex(m, x2, y1, z2).color(color);
    }
}
