package org.privates.client.managers.module.impl.render;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Namespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CometParticles", category = Category.RENDER, description = "Красивые комета частицы с длинным шлейфом")
public class CometParticles extends Module {
    private final List<CometEntity> particles = new ArrayList<>();

    private final SliderSetting count = new SliderSetting(this, "Количество", 100, 10, 300, 10);
    private final SliderSetting speed = new SliderSetting(this, "Скорость", 0.15f, 0.05f, 0.5f, 0.05f);
    private final SliderSetting radius = new SliderSetting(this, "Радиус спавна", 25, 10, 50, 5);
    private final SliderSetting trailLength = new SliderSetting(this, "Длина шлейфа", 20, 5, 40, 5);
    private final BooleanSetting randomColor = new BooleanSetting(this, "Рандомный цвет", true);

    private final ResourceLocation texture = new Namespaced("particle/bloom.png");

    @Override
    public void onEnable() {
        super.onEnable();
        particles.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        particles.clear();
    }

    private void spawnParticle(ClientPlayerEntity player) {
        double distance = Mathf.randomValue(5, radius.getValue().floatValue());
        double yawRad = Math.toRadians(Mathf.randomValue(0, 360));
        double xOffset = -Math.sin(yawRad) * distance;
        double zOffset = Math.cos(yawRad) * distance;
        double yOffset = Mathf.randomValue(-5, 10);

        double velocitySpeed = speed.getValue().floatValue();
        double velocityYaw = Math.toRadians(Mathf.randomValue(0, 360));
        double velocityPitch = Math.toRadians(Mathf.randomValue(-30, 30));

        Vector3d initialVelocity = new Vector3d(
                -Math.sin(velocityYaw) * Math.cos(velocityPitch) * velocitySpeed,
                Math.sin(velocityPitch) * velocitySpeed * 0.5,
                Math.cos(velocityYaw) * Math.cos(velocityPitch) * velocitySpeed
        );

        particles.add(new CometEntity(
                new Vector3d(
                        player.getPosX() + xOffset,
                        player.getPosY() + yOffset,
                        player.getPosZ() + zOffset
                ),
                initialVelocity,
                particles.size(),
                randomColor.getValue() ? ColorUtil.random().hashCode() : ColorUtil.fade(particles.size() * 50)
        ));
    }

    @EventHandler
    public void onChange(WorldChangeEvent e) {
        particles.clear();
    }

    @EventHandler
    public void onPreRender(Render3DPosedEvent event) {
        ClientPlayerEntity player = mc.player;

        particles.removeIf(particle ->
                particle.time.finished(8000) ||
                        particle.position.distanceTo(player.getPositionVec()) >= 60
        );

        while (particles.size() < count.getValue().intValue()) {
            spawnParticle(player);
        }

        MatrixStack matrix = event.getMatrix();
        boolean lightEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);

        RenderSystem.pushMatrix();
        matrix.push();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        if (lightEnabled) {
            RenderSystem.disableLighting();
        }

        GL11.glShadeModel(GL11.GL_SMOOTH);
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        RenderUtil.bindTexture(texture);

        for (CometEntity particle : particles) {
            particle.update();
            renderTrail(matrix, particle);
            renderParticle(matrix, particle);
        }

        cleanupRenderState(lightEnabled, matrix);
    }

    private void renderTrail(MatrixStack matrix, CometEntity particle) {
        List<Vector3d> trail = particle.trail;
        if (trail.size() < 2) return;

        updateParticleAlpha(particle);
        int baseAlpha = (int) particle.alpha.getValue();
        int color = particle.color;

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        for (int i = 0; i < trail.size(); i++) {
            Vector3d pos = trail.get(i);
            float fade = (float) i / (float) trail.size();
            float size = 0.15f * fade;
            int trailAlpha = (int) (baseAlpha * fade * 0.8f);
            int trailColor = ColorUtil.replAlpha(color, trailAlpha);

            matrix.push();
            RenderUtil3D.setupOrientationMatrix(matrix, (float) pos.x, (float) pos.y, (float) pos.z);
            matrix.rotate(mc.getRenderManager().getCameraOrientation());

            RectUtil.drawRect(matrix, -size, -size, size * 2, size * 2, trailColor, true, true);

            matrix.pop();

            if (i % 3 == 0 && fade > 0.3f) {
                int miniParticleCount = 2 + (int) (Math.random() * 3);
                for (int j = 0; j < miniParticleCount; j++) {
                    double offsetX = (Math.random() - 0.5) * 0.3;
                    double offsetY = (Math.random() - 0.5) * 0.3;
                    double offsetZ = (Math.random() - 0.5) * 0.3;

                    float miniSize = 0.04f + (float) (Math.random() * 0.03f);
                    int miniAlpha = (int) (trailAlpha * 0.6f);
                    int miniColor = ColorUtil.replAlpha(color, miniAlpha);

                    matrix.push();
                    RenderUtil3D.setupOrientationMatrix(matrix,
                            (float) (pos.x + offsetX),
                            (float) (pos.y + offsetY),
                            (float) (pos.z + offsetZ));
                    matrix.rotate(mc.getRenderManager().getCameraOrientation());

                    RectUtil.drawRect(matrix, -miniSize, -miniSize, miniSize * 2, miniSize * 2, miniColor, true, true);

                    matrix.pop();
                }
            }
        }
    }

    private void renderParticle(MatrixStack matrix, CometEntity particle) {
        updateParticleAlpha(particle);

        int baseAlpha = (int) particle.alpha.getValue();
        int pulseAlpha = particle.getPulseAlpha();
        int finalAlpha = Math.min(baseAlpha, pulseAlpha);

        int color = particle.color;
        Vector3d pos = particle.position;

        matrix.push();
        RenderUtil3D.setupOrientationMatrix(matrix, (float) pos.x, (float) pos.y, (float) pos.z);
        matrix.rotate(mc.getRenderManager().getCameraOrientation());

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        float glowSize = 0.35f;
        int glowColor = ColorUtil.replAlpha(color, (int) (finalAlpha * 0.6f));
        RectUtil.drawRect(matrix, -glowSize, -glowSize, glowSize * 2, glowSize * 2, glowColor, true, true);

        float mainSize = 0.22f;
        int mainColor = ColorUtil.replAlpha(color, finalAlpha);
        RectUtil.drawRect(matrix, -mainSize, -mainSize, mainSize * 2, mainSize * 2, mainColor, true, true);

        float coreSize = 0.10f;
        int coreColor = ColorUtil.replAlpha(0xFFFFFFFF, finalAlpha);
        RectUtil.drawRect(matrix, -coreSize, -coreSize, coreSize * 2, coreSize * 2, coreColor, true, true);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        matrix.pop();
    }

    private void updateParticleAlpha(CometEntity particle) {
        long fadeInDuration = 500;
        long fadeOutStart = 8000 - fadeInDuration;

        if (particle.alpha.getValue() < 255 && !particle.time.finished(fadeInDuration)) {
            particle.alpha.run(255, 0.5, Easings.CUBIC_OUT, true);
        }

        if (particle.alpha.getValue() > 0 && particle.time.finished(fadeOutStart)) {
            particle.alpha.run(0, 0.5, Easings.CUBIC_OUT, true);
        }
        particle.alpha.update();
    }

    private void cleanupRenderState(boolean lightEnabled, MatrixStack matrix) {
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        GlStateManager.clearCurrentColor();
        GL11.glShadeModel(GL11.GL_FLAT);

        if (lightEnabled) {
            RenderSystem.enableLighting();
        }

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableAlphaTest();
        matrix.pop();
        RenderSystem.popMatrix();
    }

    @Getter
    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private class CometEntity {
        final int index;
        final StopWatch time = new StopWatch();
        final Animation alpha = new Animation();
        final int color;
        Vector3d position;
        Vector3d velocity;
        final List<Vector3d> trail = new ArrayList<>();

        public CometEntity(Vector3d position, Vector3d velocity, int index, int color) {
            this.position = position;
            this.velocity = velocity;
            this.index = index;
            this.color = color;
            this.time.reset();
            this.alpha.set(0);
            this.trail.add(new Vector3d(position.x, position.y, position.z));
        }

        public void update() {
            double randomness = 0.01;
            velocity = velocity.add(
                    (Math.random() - 0.5) * randomness,
                    (Math.random() - 0.5) * randomness,
                    (Math.random() - 0.5) * randomness
            );

            double maxSpeed = speed.getValue().floatValue() * 1.5;
            velocity = new Vector3d(
                    MathHelper.clamp(velocity.x, -maxSpeed, maxSpeed),
                    MathHelper.clamp(velocity.y, -maxSpeed, maxSpeed),
                    MathHelper.clamp(velocity.z, -maxSpeed, maxSpeed)
            );

            position = position.add(velocity);

            trail.add(0, new Vector3d(position.x, position.y, position.z));

            int maxTrailLength = trailLength.getValue().intValue();
            while (trail.size() > maxTrailLength) {
                trail.remove(trail.size() - 1);
            }
        }

        public int getPulseAlpha() {
            double pulse = (Math.sin(time.elapsedTime() / 300.0) + 1.0) / 2.0;
            return (int) (pulse * 255);
        }
    }
}