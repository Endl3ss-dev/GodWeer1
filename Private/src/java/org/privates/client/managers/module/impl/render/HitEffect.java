package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.EntityRenderMatrixEvent;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easing;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.other.SoundUtil;
import org.privates.client.utils.player.RotationUtility;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtilOLD;
import org.privates.client.utils.render.draw.RenderUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "HitEffect", category = Category.RENDER, description = "Различные эффекты при ударе")
public class HitEffect extends Module {
    public static HitEffect getInstance() {
        return Instance.get(HitEffect.class);
    }

    public final MultiBooleanSetting setting = new MultiBooleanSetting(this, "Эффекты",
            BooleanSetting.of("Уменьшать при ударе", true),
            BooleanSetting.of("Хит баблс", true),
            BooleanSetting.of("Дух", true),
            BooleanSetting.of("Волна блоков", true),
            BooleanSetting.of("Звук удара", true));

    private final SliderSetting size = new SliderSetting(this, "Сила уменьшения", 0.1F, 0.05F, 0.3F, 0.05F)
            .setVisible(() -> setting.getValue("Уменьшать при ударе"));

    private final SliderSetting riseHeight = new SliderSetting(this, "Высота подъема духа", 3.0f, 0.5f, 6.0f, 0.1f)
            .setVisible(() -> setting.getValue("Дух"));
    private final SliderSetting ghostDuration = new SliderSetting(this, "Время жизни духа", 2.0f, 0.5f, 5.0f, 0.1f)
            .setVisible(() -> setting.getValue("Дух"));
    private final SliderSetting ghostScale = new SliderSetting(this, "Размер духа", 0.6f, 0.2f, 1.5f, 0.1f)
            .setVisible(() -> setting.getValue("Дух"));
    private final ColorSetting ghostColor = new ColorSetting(this, "Цвет духа", 0xFF9F7A99)
            .setVisible(() -> setting.getValue("Дух"));

    private final ModeSetting ghostDirection = new ModeSetting(this, "Направление духа", "Вверх", "Насквозь", "Вправо", "Влево", "По прицелу")
            .setVisible(() -> setting.getValue("Дух"));

    private final MultiBooleanSetting ghostParts = new MultiBooleanSetting(this, "Части тела духа",
            BooleanSetting.of("Голова", true),
            BooleanSetting.of("Туловище", true),
            BooleanSetting.of("Руки", true),
            BooleanSetting.of("Ноги", true))
            .setVisible(() -> setting.getValue("Дух"));

    private final SliderSetting waveMaxRadius = new SliderSetting(this, "Макс. радиус волны", 5F, 1F, 15F, 0.5F)
            .setVisible(() -> setting.getValue("Волна блоков"));
    private final SliderSetting waveRingWidth = new SliderSetting(this, "Толщина кольца волны", 1.5F, 0.5F, 5F, 0.25F)
            .setVisible(() -> setting.getValue("Волна блоков"));
    private final SliderSetting waveDuration = new SliderSetting(this, "Время волны", 1500F, 750F, 5000F, 50F)
            .setVisible(() -> setting.getValue("Волна блоков"));
    private final SliderSetting waveFillAlpha = new SliderSetting(this, "Прозрачность заливки волны", 0.4F, 0.05F, 1F, 0.05F)
            .setVisible(() -> setting.getValue("Волна блоков"));
    private final SliderSetting waveOutlineAlpha = new SliderSetting(this, "Прозрачность контура волны", 0.8F, 0.05F, 1F, 0.05F)
            .setVisible(() -> setting.getValue("Волна блоков"));
    private final BooleanSetting waveShowOutline = new BooleanSetting(this, "Контур блоков волны", true)
            .setVisible(() -> setting.getValue("Волна блоков"));
    private final BooleanSetting waveShowFill = new BooleanSetting(this, "Заливка блоков волны", true)
            .setVisible(() -> setting.getValue("Волна блоков"));
    private final SliderSetting waveLineThickness = new SliderSetting(this, "Толщина линий волны", 1.5F, 0.5F, 5F, 0.1F)
            .setVisible(() -> setting.getValue("Волна блоков") && waveShowOutline.getValue());

    private final ModeSetting soundMode = new ModeSetting(this, "Звук", "select", "sound", "punch", "skeethitsound", "awp", "bass", "glu", "glu2", "bonk", "beep")
            .setVisible(() -> setting.getValue("Звук удара"));

    private final SliderSetting soundVolume = new SliderSetting(this, "Громкость звука", 30, 0, 100, 1)
            .setVisible(() -> setting.getValue("Звук удара"));

    private final Animation rhombHit = new Animation();

    private LivingEntity target;
    private final List<Particle> particles = new ArrayList<>();
    private final List<HitGhost> ghosts = new CopyOnWriteArrayList<>();
    private final List<WaveCircle> waves = new ArrayList<>();
    private final ResourceLocation texture = new ResourceLocation("private/texture/bubble.png");
    private final Tessellator tessellator = Tessellator.getInstance();
    private final BufferBuilder bufferBuilder = tessellator.getBuffer();
    private static final float U = 1.0f / 16.0f;
    long maxTime = 0;

    @Override
    public void toggle() {
        super.toggle();
        particles.clear();
        ghosts.clear();
        waves.clear();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        particles.clear();
        ghosts.clear();
        waves.clear();
    }

    @EventHandler
    public void onEvent(AttackEvent event) {
        if (event.getTarget() == null || event.getTarget() == mc.player) return;
        if (!(event.getTarget() instanceof LivingEntity)) return;

        target = (LivingEntity) event.getTarget();

        if (setting.getValue("Звук удара")) {
            playHitSound();
        }

        if (setting.getValue("Хит баблс")) {
            addBubbleParticles(target);
        }

        if (setting.getValue("Дух")) {
            addGhost(target);
        }

        if (setting.getValue("Волна блоков")) {
            addWave(target);
        }
    }

    private void playHitSound() {
        String soundFile = soundMode.getValue() + ".wav";
        float volume = soundVolume.getValue() / 100F;
        SoundUtil.playSound(soundFile, volume);
    }

    private void addBubbleParticles(LivingEntity target) {
        ClientPlayerEntity player = mc.player;
        double distance = player.getDistance(target) - 1F + player.getWidth() / 2.F;
        double yawRad = Math.toRadians(RotationUtility.calculate(target).x);
        double xOffset = -Math.sin(yawRad) * distance;
        double zOffset = Math.cos(yawRad) * distance;
        Vector3d particlePosition = new Vector3d(player.getPosX() + xOffset, target.getPosY() + target.getEyeHeight(), player.getPosZ() + zOffset);
        for (Particle p : particles) {
            maxTime = p.time;
        }
        particles.add(new Particle(particles.size(), particlePosition, maxTime));
    }

    private void addGhost(LivingEntity target) {
        Vector3d pos = target.getPositionVec();
        float bodyYaw = target.renderYawOffset;
        boolean sneaking = target.isSneaking();
        float walkPhase = target.ticksExisted;
        Vector3d motion = target.getMotion();
        float horizontalSpeed = (float) Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        float walkSpeed = MathHelper.clamp(horizontalSpeed * 8.0f, 0.0f, 1.0f);

        // Calculate direction based on setting
        Vector3d direction = calculateGhostDirection(target);

        ghosts.add(new HitGhost(pos, bodyYaw, sneaking, walkPhase, walkSpeed, System.currentTimeMillis(), direction));
    }

    private Vector3d calculateGhostDirection(LivingEntity target) {
        String dirMode = ghostDirection.getValue();

        switch (dirMode) {
            case "Вверх":
                return new Vector3d(0, 1, 0);

            case "Насквозь":
                // Through the player - from attacker to target
                if (mc.player != null) {
                    Vector3d toTarget = target.getPositionVec().subtract(mc.player.getPositionVec()).normalize();
                    return toTarget;
                }
                return new Vector3d(0, 1, 0);

            case "Вправо":
                // Right relative to target's facing
                double yawRad = Math.toRadians(target.renderYawOffset + 90);
                return new Vector3d(-Math.sin(yawRad), 0, Math.cos(yawRad));

            case "Влево":
                // Left relative to target's facing
                double yawRadLeft = Math.toRadians(target.renderYawOffset - 90);
                return new Vector3d(-Math.sin(yawRadLeft), 0, Math.cos(yawRadLeft));

            case "По прицелу":
                // Based on where player is aiming relative to target center
                if (mc.player != null) {
                    Vector3d targetCenter = target.getPositionVec().add(0, target.getEyeHeight() / 2, 0);
                    Vector3d playerEye = mc.player.getEyePosition(1.0f);
                    Vector3d lookVec = mc.player.getLookVec();

                    // Find closest point on look ray to target center
                    Vector3d toTarget = targetCenter.subtract(playerEye);
                    double projection = toTarget.dotProduct(lookVec);
                    Vector3d closestPoint = playerEye.add(lookVec.scale(projection));

                    // Direction from target center to closest point
                    Vector3d aimOffset = closestPoint.subtract(targetCenter);

                    // If aiming right of center, go right; if left, go left
                    if (Math.abs(aimOffset.x) > Math.abs(aimOffset.z)) {
                        return aimOffset.x > 0 ? new Vector3d(1, 0, 0) : new Vector3d(-1, 0, 0);
                    } else {
                        return aimOffset.z > 0 ? new Vector3d(0, 0, 1) : new Vector3d(0, 0, -1);
                    }
                }
                return new Vector3d(0, 1, 0);

            default:
                return new Vector3d(0, 1, 0);
        }
    }

    private void addWave(LivingEntity target) {
        Vector3d pos = target.getPositionVec().add(0.D, .005D, 0.D);
        waves.add(new WaveCircle(pos, System.currentTimeMillis(), waveDuration.getValue().intValue()));
    }

    @EventHandler
    public void onEvent(EntityRenderMatrixEvent event) {
        if (!setting.getValue("Уменьшать при ударе")) return;
        rhombHit.update();
        boolean atts = target != null && target.hurtTime > 0;
        rhombHit.run(atts ? 0.0F : 1.0F, 0.15F, Easings.LINEAR, false);
        float xzya = -size.getValue() + size.getValue() * rhombHit.get();
        if (event.getEntity() == target) {
            event.getMatrix().scale(1 + xzya, 1 + xzya, 1F + xzya);
        }
    }

    @EventHandler
    public void onRender(Render3DLastEvent event) {
        if (setting.getValue("Хит баблс")) {
            renderBubbles(event);
        }
        if (setting.getValue("Волна блоков")) {
            renderWaves(event);
        }
    }

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        if (setting.getValue("Дух")) {
            renderGhosts(event);
        }
    }

    private void renderBubbles(Render3DLastEvent event) {
        if (particles.isEmpty()) return;
        boolean bloom = true;
        MatrixStack matrixStack = event.getMatrix();

        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();

            if (System.currentTimeMillis() - p.time > 1500) {
                iterator.remove();
                continue;
            }

            if (System.currentTimeMillis() - p.time > 1000 && !p.isBack) {
                p.animation.animate(0, 0.5, Easings.EXPO_IN, false);
                p.isBack = true;
            }

            p.animation.update();
            float maxScale = (float) (0.75F * p.animation.getValue());
            float alphaPC = (float) (1 * p.animation.getValue());

            if (p.renderRule()) {
                setRenderingTexture3D(() -> drawParticle(p, matrixStack, maxScale, alphaPC, bloom), matrixStack, texture);
            }
        }
    }

    private void drawParticle(Particle particle, MatrixStack matrix, float maxScale, float alphaPC, boolean bloom) {
        int[] colors = getColorsParticles(particle, alphaPC);
        float extendXY = maxScale;
        setParticleOrientation(particle, () -> {
            for (int i = 0; i < 2; ++i)
                RectUtilOLD.drawRect(matrix, -extendXY, -extendXY, extendXY, extendXY, colors[0], colors[1], colors[2], colors[3], bloom, true);
        }, matrix);
    }

    private void setRenderingTexture3D(Runnable render, MatrixStack matrix, ResourceLocation resourceLocation) {
        boolean light = GL11.glIsEnabled(GL11.GL_LIGHTING);
        RenderSystem.pushMatrix();
        matrix.push();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        if (light) RenderSystem.disableLighting();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        RenderUtil.bindTexture(resourceLocation);
        matrix.push();
        render.run();
        matrix.pop();
        GlStateManager.clearCurrentColor();
        GL11.glShadeModel(GL11.GL_FLAT);
        if (light) RenderSystem.enableLighting();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableAlphaTest();
        matrix.pop();
        RenderSystem.popMatrix();
    }

    private int[] getColorsParticles(Particle particle, float alphaPC) {
        int[] colors = new int[4];
        int indexAppend = particle.index * 45;
        colors[0] = ColorUtil.multAlpha(ColorUtil.fade(indexAppend), alphaPC);
        colors[1] = ColorUtil.multAlpha(ColorUtil.fade(90 + indexAppend), alphaPC);
        colors[2] = ColorUtil.multAlpha(ColorUtil.fade(180 + indexAppend), alphaPC);
        colors[3] = ColorUtil.multAlpha(ColorUtil.fade(270 + indexAppend), alphaPC);
        return colors;
    }

    private void setParticleOrientation(Particle particle, Runnable render, MatrixStack matrix) {
        matrix.push();
        RectUtilOLD.setupOrientationMatrix(matrix, particle.pos.getX(), particle.pos.getY(), particle.pos.getZ());
        matrix.rotate(particle.quaternion);
        RenderUtil.bindTexture(texture);
        matrix.push();
        final float rotatePC01 = (float) (1 * particle.animation.getValue());
        matrix.rotate(Vector3f.ZP.rotationDegrees(rotatePC01 * -360.F));
        render.run();
        matrix.pop();
        matrix.pop();
    }

    private void renderGhosts(Render3DPosedEvent event) {
        if (ghosts.isEmpty() || mc.world == null) return;
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();
        long now = System.currentTimeMillis();
        List<HitGhost> toRemove = new ArrayList<>();

        setupGhostRender();

        for (HitGhost ghost : ghosts) {
            float progress = (now - ghost.startTime) / (ghostDuration.getValue() * 1000.0f);
            if (progress >= 1.0f) {
                toRemove.add(ghost);
                continue;
            }

            double motionY = riseHeight.getValue() * easeOutCubic(progress);

            // Apply direction-based motion
            Vector3d motion = ghost.direction.scale(riseHeight.getValue() * easeOutCubic(progress));
            double renderX = ghost.position.x - cameraPos.x + motion.x;
            double renderY = ghost.position.y - cameraPos.y + motion.y;
            double renderZ = ghost.position.z - cameraPos.z + motion.z;

            float alpha = (float) easeOutAlpha(progress);

            int col = resolveGhostColor();
            float r = ((col >> 16) & 0xFF) / 255f;
            float g = ((col >> 8) & 0xFF) / 255f;
            float b = (col & 0xFF) / 255f;

            MatrixStack matrices = event.getMatrix();
            matrices.push();

            matrices.translate(renderX, renderY, renderZ);
            matrices.rotate(Vector3f.YP.rotationDegrees(180.0f - ghost.bodyYaw));

            float scale = ghostScale.getValue() * (1.0f + progress * 0.3f);
            matrices.scale(scale, scale, scale);

            float effectiveAlpha = MathHelper.clamp(alpha, 0.0f, 0.8f);

            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            renderFlatHumanoid(matrices, r, g, b, effectiveAlpha, ghost.sneaking, ghost.walkPhase, ghost.walkSpeed);
            tessellator.draw();

            matrices.pop();
        }

        releaseGhostRender();

        if (!toRemove.isEmpty()) {
            ghosts.removeAll(toRemove);
        }
    }

    private int resolveGhostColor() {
        int c = ghostColor.getValue();
        if (c == 0xFF9F7A99 && Theme.getInstance() != null) {
            return Theme.getInstance().clientColor();
        }
        return c;
    }

    private void renderFlatHumanoid(MatrixStack matrices, float r, float g, float b, float a,
                                    boolean sneaking, float walkPhase, float walkSpeed) {
        matrices.push();
        matrices.scale(-1f, -1f, 1f);
        matrices.translate(0.0D, -1.501D, 0.0D);

        if (sneaking) {
            matrices.translate(0.0D, 0.2D, 0.0D);
            matrices.rotate(Vector3f.XP.rotationDegrees(28.0f));
        }

        Matrix4f matrix = matrices.getLast().getMatrix();

        // Render body parts based on settings
        if (ghostParts.getValue("Туловище")) {
            drawBox(matrix, -4 * U, 0, -2 * U, 8 * U, 12 * U, 4 * U, r, g, b, a);
        }
        if (ghostParts.getValue("Голова")) {
            drawBox(matrix, -4 * U, -8 * U, -4 * U, 8 * U, 8 * U, 8 * U, r, g, b, a);
        }

        float swing = MathHelper.sin(walkPhase * 0.6662f) * 1.4f * walkSpeed;

        if (ghostParts.getValue("Руки")) {
            renderArm(matrices, r, g, b, a, true, -swing);
            renderArm(matrices, r, g, b, a, false, swing);
        }
        if (ghostParts.getValue("Ноги")) {
            renderLeg(matrices, r, g, b, a, true, swing);
            renderLeg(matrices, r, g, b, a, false, -swing);
        }

        matrices.pop();
    }

    private void renderArm(MatrixStack matrices, float r, float g, float b, float a,
                           boolean left, float pitchRad) {
        matrices.push();
        float pivotX = (left ? -6.0f : 6.0f) * U;
        float pivotY = 2.0f * U;
        matrices.translate(pivotX, pivotY, 0.0f);
        matrices.rotate(Vector3f.XP.rotation(pitchRad));
        matrices.translate(-pivotX, -pivotY, 0.0f);
        Matrix4f matrix = matrices.getLast().getMatrix();
        float boxX = (left ? -8.0f : 4.0f) * U;
        drawBox(matrix, boxX, -2 * U, -2 * U, 4 * U, 12 * U, 4 * U, r, g, b, a);
        matrices.pop();
    }

    private void renderLeg(MatrixStack matrices, float r, float g, float b, float a,
                           boolean left, float pitchRad) {
        matrices.push();
        float pivotX = (left ? -2.0f : 2.0f) * U;
        float pivotY = 12.0f * U;
        matrices.translate(pivotX, pivotY, 0.0f);
        matrices.rotate(Vector3f.XP.rotation(pitchRad));
        matrices.translate(-pivotX, -pivotY, 0.0f);
        Matrix4f matrix = matrices.getLast().getMatrix();
        float boxX = (left ? -4.0f : 0.0f) * U;
        drawBox(matrix, boxX, 12 * U, -2 * U, 4 * U, 12 * U, 4 * U, r, g, b, a);
        matrices.pop();
    }

    private void drawBox(Matrix4f matrix, float x, float y, float z,
                         float sx, float sy, float sz,
                         float r, float g, float b, float a) {
        float x2 = x + sx, y2 = y + sy, z2 = z + sz;

        bufferBuilder.pos(matrix, x, y, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y2, z2).color(r, g, b, a).endVertex();

        bufferBuilder.pos(matrix, x2, y, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y2, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z).color(r, g, b, a).endVertex();

        bufferBuilder.pos(matrix, x, y, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y2, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y2, z).color(r, g, b, a).endVertex();

        bufferBuilder.pos(matrix, x2, y, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z2).color(r, g, b, a).endVertex();

        bufferBuilder.pos(matrix, x, y2, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y2, z).color(r, g, b, a).endVertex();

        bufferBuilder.pos(matrix, x, y, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y, z2).color(r, g, b, a).endVertex();
    }

    private double easeOutCubic(double t) {
        t = MathHelper.clamp(t, 0.0D, 1.0D);
        return 1.0D - Math.pow(1.0D - t, 3);
    }

    private double easeOutAlpha(double t) {
        t = MathHelper.clamp(t, 0.0D, 1.0D);
        double invT = 1.0D - t;
        return 0.8D * Math.pow(invT, 2.5);
    }

    private void setupGhostRender() {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
    }

    private void releaseGhostRender() {
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    private void renderWaves(Render3DLastEvent event) {
        if (waves.isEmpty() || mc.world == null) return;

        waves.removeIf(wave -> wave.getDeltaTime() >= 1.0F);
        if (waves.isEmpty()) return;

        MatrixStack matrixStack = event.getMatrix();

        matrixStack.push();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.getLast().getMatrix());

        double renderX = mc.getRenderManager().renderPosX();
        double renderY = mc.getRenderManager().renderPosY();
        double renderZ = mc.getRenderManager().renderPosZ();
        RenderSystem.translated(-renderX, -renderY, -renderZ);

        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();

        if (waveShowOutline.getValue()) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            RenderSystem.lineWidth(waveLineThickness.getValue());
        }

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        int baseColor = Theme.getInstance().clientColor();

        Easing expandEase = Easings.EXPO_OUT;
        Easing fadeInEase = Easings.EXPO_OUT;
        Easing fadeOutEase = Easings.EXPO_IN;

        float fadeInDur = 0.15F;
        float fadeOutSt = 0.5F;
        float expSpeed = 1.5F;

        for (WaveCircle wave : waves) {
            float rawProgress = wave.getDeltaTime();
            rawProgress = Math.min(rawProgress, 1.0F);

            float expandInput = Math.min(rawProgress * expSpeed, 1.0F);
            float easedExpand = (float) expandEase.ease(expandInput);

            float currentRadius = easedExpand * waveMaxRadius.getValue();
            float innerRadius = Math.max(0, currentRadius - waveRingWidth.getValue());

            float fadeAlpha;
            if (rawProgress < fadeInDur) {
                float fadeInProgress = rawProgress / fadeInDur;
                fadeAlpha = (float) fadeInEase.ease(Math.min(fadeInProgress, 1.0F));
            } else if (rawProgress > fadeOutSt) {
                float fadeOutProgress = 1.0F - (rawProgress - fadeOutSt) / (1.0F - fadeOutSt);
                fadeAlpha = (float) fadeOutEase.ease(Math.max(Math.min(fadeOutProgress, 1.0F), 0.0F));
            } else {
                fadeAlpha = 1.0F;
            }

            fadeAlpha = Math.max(0, Math.min(1, fadeAlpha));

            if (fadeAlpha < 0.005F || currentRadius < 0.01F) continue;

            int blockY = (int) Math.floor(wave.pos.y - 0.5);
            int centerBlockX = (int) Math.floor(wave.pos.x);
            int centerBlockZ = (int) Math.floor(wave.pos.z);
            int scanRadius = (int) Math.ceil(currentRadius) + 1;

            for (int dx = -scanRadius; dx <= scanRadius; dx++) {
                for (int dz = -scanRadius; dz <= scanRadius; dz++) {
                    BlockPos pos = new BlockPos(centerBlockX + dx, blockY, centerBlockZ + dz);
                    BlockState state = mc.world.getBlockState(pos);

                    if (state.isAir()) continue;

                    VoxelShape shape = state.getShape(mc.world, pos);
                    if (shape.isEmpty()) continue;

                    for (AxisAlignedBB aabb : shape.toBoundingBoxList()) {
                        double blockMinX = pos.getX() + aabb.minX;
                        double blockMinZ = pos.getZ() + aabb.minZ;
                        double blockMaxX = pos.getX() + aabb.maxX;
                        double blockMaxZ = pos.getZ() + aabb.maxZ;
                        double blockMaxY = pos.getY() + aabb.maxY;

                        double closestX = Math.max(blockMinX, Math.min(wave.pos.x, blockMaxX));
                        double closestZ = Math.max(blockMinZ, Math.min(wave.pos.z, blockMaxZ));

                        double distSq = (closestX - wave.pos.x) * (closestX - wave.pos.x)
                                + (closestZ - wave.pos.z) * (closestZ - wave.pos.z);

                        double farthestX = (Math.abs(blockMinX - wave.pos.x) > Math.abs(blockMaxX - wave.pos.x)) ? blockMinX : blockMaxX;
                        double farthestZ = (Math.abs(blockMinZ - wave.pos.z) > Math.abs(blockMaxZ - wave.pos.z)) ? blockMinZ : blockMaxZ;
                        double farDistSq = (farthestX - wave.pos.x) * (farthestX - wave.pos.x)
                                + (farthestZ - wave.pos.z) * (farthestZ - wave.pos.z);

                        double innerRadSq = (double) innerRadius * innerRadius;
                        double outerRadSq = (double) currentRadius * currentRadius;

                        if (farDistSq < innerRadSq) continue;
                        if (distSq > outerRadSq) continue;

                        float blockAlpha = fadeAlpha;

                        double dist = Math.sqrt(distSq);
                        float ringCenter = (innerRadius + currentRadius) / 2.0F;
                        float halfRing = (currentRadius - innerRadius) / 2.0F;

                        if (halfRing > 0.01F) {
                            float distFromCenter = Math.abs((float) dist - ringCenter) / halfRing;
                            distFromCenter = Math.min(distFromCenter, 1.0F);
                            float smoothFade = 1.0F - distFromCenter * distFromCenter * distFromCenter;
                            blockAlpha *= smoothFade;
                        }

                        if (blockAlpha < 0.01F) continue;

                        if (waveShowFill.getValue()) {
                            int fc = ColorUtil.setAlpha(baseColor, (int) (waveFillAlpha.getValue() * 255 * blockAlpha));
                            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                            renderTopFace(buffer, blockMinX, blockMaxY, blockMinZ, blockMaxX, blockMaxY, blockMaxZ, fc);
                            tess.draw();
                        }

                        if (waveShowOutline.getValue()) {
                            int oc = ColorUtil.setAlpha(baseColor, (int) (waveOutlineAlpha.getValue() * 255 * blockAlpha));
                            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                            renderTopFaceOutline(buffer, blockMinX, blockMaxY, blockMinZ, blockMaxX, blockMaxY, blockMaxZ, oc);
                            tess.draw();
                        }
                    }
                }
            }
        }

        if (waveShowOutline.getValue()) {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();

        RenderSystem.translated(renderX, renderY, renderZ);
        RenderSystem.popMatrix();
        matrixStack.pop();
    }

    private void renderTopFace(BufferBuilder bufferBuilder, double minX, double y, double minZ, double maxX, double maxY, double maxZ, int color) {
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;

        bufferBuilder.pos(minX, y, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, y, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, y, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, y, minZ).color(red, green, blue, alpha).endVertex();
    }

    private void renderTopFaceOutline(BufferBuilder bufferBuilder, double minX, double y, double minZ, double maxX, double maxY, double maxZ, int color) {
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;

        bufferBuilder.pos(minX, y, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, y, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, y, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, y, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, y, minZ).color(red, green, blue, alpha).endVertex();
    }

    private static class Particle {
        private final Animation animation = new Animation();
        private final long time;
        private final int index;
        private final Vector3d pos;
        private final Quaternion quaternion = mc.getRenderManager().getCameraOrientation().copy();
        private boolean isBack;

        public Particle(int index, Vector3d pos, long maxTime) {
            this.index = index;
            this.pos = pos;
            time = System.currentTimeMillis();
            animation.animate(1.0D, 1.0D, Easings.EXPO_OUT, false);
        }

        public boolean isToRemove() {
            return mc.player.getPositionVec().distanceTo(pos) > 100;
        }

        public boolean renderRule() {
            return !isToRemove();
        }
    }

    private record HitGhost(Vector3d position, float bodyYaw, boolean sneaking,
                            float walkPhase, float walkSpeed, long startTime, Vector3d direction) {
    }

    private record WaveCircle(Vector3d pos, long time, int aliveTime) {
        public float getDeltaTime() {
            return (System.currentTimeMillis() - time) / (float) aliveTime;
        }
    }
}
