package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.interfaces.IRender;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.common.impl.fastrandom.FastRandom;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "WorldFireWorks", category = Category.RENDER, description = "Кушает примерно 40 фпс но очень красивый визуальный модуль спавнит линии по миру")
public class WorldLines extends Module implements IRender {
    public static WorldLines getInstance() {
        return Instance.get(WorldLines.class);
    }

    final SliderSetting lineCount = new SliderSetting(this, "Количество линий", 5, 3, 50, 2);
    final SliderSetting lineLength = new SliderSetting(this, "Длина линий", 25.0f, 10.0f, 100.0f, 2.5f);
    final SliderSetting speed = new SliderSetting(this, "Скорость", 2.0f, 0.5f, 8.0f, 0.25f);
    final SliderSetting thickness = new SliderSetting(this, "Толщина", 3.0f, 1.0f, 8.0f, 0.5f);
    final SliderSetting opacity = new SliderSetting(this, "Прозрачность", 0.8f, 0.1f, 1.0f, 0.05f);
    final SliderSetting particleCount = new SliderSetting(this, "Кол-во частиц", 15, 5, 50, 2);
    final BooleanSetting smoothMotion = new BooleanSetting(this, "Плавное движение", true);
    final BooleanSetting fadeEffect = new BooleanSetting(this, "Эффект затухания", true);
    final BooleanSetting colorGradient = new BooleanSetting(this, "Градиент цвета", true);

    final List<WorldLine> lines = new ArrayList<>();
    final FastRandom random = new FastRandom();

    @Override
    public void toggle() {
        super.toggle();
        clear();
        if (isEnabled()) {
            initializeLines();
        }
    }

    @EventHandler
    public void onWorldChange(WorldChangeEvent event) {
        clear();
        if (isEnabled()) {
            initializeLines();
        }
    }

    private void clear() {
        lines.clear();
    }

    private void initializeLines() {
        for (int i = 0; i < lineCount.getValue(); i++) {
            lines.add(new WorldLine());
        }
    }

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        if (mc.player == null || lines.isEmpty()) return;

        MatrixStack matrix = event.getMatrix();
        setupRenderState();

        for (WorldLine line : lines) {
            line.update();
            renderLine(matrix, line);
        }

        restoreRenderState();
    }

    private void setupRenderState() {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }

    private void restoreRenderState() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void renderLine(MatrixStack matrix, WorldLine line) {
        Vector3d camera = RenderUtil3D.cameraPos();

        matrix.push();
        matrix.translate(-camera.x, -camera.y, -camera.z);

        GL11.glLineWidth(thickness.getValue());
        BUFFER.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        List<Vector3d> points = line.getPoints();
        for (int i = 0; i < points.size(); i++) {
            Vector3d point = points.get(i);
            float alpha = opacity.getValue();

            if (fadeEffect.getValue()) {
                alpha *= (float) i / points.size();
            }

            int color = getLineColor(line, i, points.size());
            color = ColorUtil.replAlpha(color, alpha);

            BUFFER.pos(matrix.getLast().getMatrix(), (float) point.x, (float) point.y, (float) point.z)
                    .color(color).endVertex();
        }

        TESSELLATOR.draw();

        renderParticles(matrix, line);
        matrix.pop();
    }

    private void renderParticles(MatrixStack matrix, WorldLine line) {
        List<Particle> particles = line.getParticles();
        if (particles.isEmpty()) return;

        GL11.glPointSize(2.0f);
        BUFFER.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);

        for (Particle particle : particles) {
            float lifePercent = particle.getLifePercent();
            float alpha = opacity.getValue() * lifePercent * 0.6f;

            int color = ColorUtil.replAlpha(Theme.getInstance().clientColor(), alpha);
            Vector3d pos = particle.getPosition();

            BUFFER.pos(matrix.getLast().getMatrix(), (float) pos.x, (float) pos.y, (float) pos.z)
                    .color(color).endVertex();
        }

        TESSELLATOR.draw();
    }

    private int getLineColor(WorldLine line, int pointIndex, int totalPoints) {
        int baseColor = Theme.getInstance().clientColor();

        if (colorGradient.getValue()) {
            float gradient = (float) pointIndex / totalPoints;
            return ColorUtil.fade(line.colorOffset + (int) (gradient * 100));
        }

        return ColorUtil.fade(line.colorOffset);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    class WorldLine {
        final List<Vector3d> points = new ArrayList<>();
        final List<Particle> particles = new ArrayList<>();
        final StopWatch particleTimer = new StopWatch();
        final int colorOffset;
        Vector3d currentPos;
        Vector3d velocity;
        Vector3d targetVelocity;

        public WorldLine() {
            colorOffset = random.nextInt(360);
            initializePosition();
        }

        private void initializePosition() {
            Vector3d playerPos = mc.player.getPositionVec();
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = 15 + random.nextDouble() * 20;

            currentPos = new Vector3d(
                    playerPos.x + Math.cos(angle) * distance,
                    Math.max(playerPos.y + 5, playerPos.y + random.nextDouble() * 15),
                    playerPos.z + Math.sin(angle) * distance
            );

            velocity = new Vector3d(
                    (random.nextDouble() - 0.5) * 0.2,
                    (random.nextDouble() - 0.5) * 0.1,
                    (random.nextDouble() - 0.5) * 0.2
            );

            targetVelocity = velocity;
            points.add(currentPos);
        }

        public void update() {
            updateMovement();
            updatePoints();
            updateParticles();
            spawnParticles();
        }

        private void updateMovement() {
            Vector3d playerPos = mc.player.getPositionVec();

            if (random.nextFloat() < 0.02f) {
                double angle = random.nextDouble() * Math.PI * 2;
                double pitch = (random.nextDouble() - 0.5) * Math.PI * 0.3;
                double speedMult = 0.15 + random.nextDouble() * 0.15;

                targetVelocity = new Vector3d(
                        Math.cos(angle) * Math.cos(pitch) * speedMult,
                        Math.sin(pitch) * speedMult * 0.5,
                        Math.sin(angle) * Math.cos(pitch) * speedMult
                );
            }

            if (smoothMotion.getValue()) {
                velocity = velocity.scale(0.95).add(targetVelocity.scale(0.05));
            } else {
                velocity = targetVelocity;
            }

            Vector3d nextPos = currentPos.add(velocity.scale(speed.getValue()));

            if (nextPos.y < playerPos.y + 2) {
                velocity = new Vector3d(velocity.x, Math.abs(velocity.y) + 0.1, velocity.z);
            }

            double maxDistance = 80;
            double distanceToPlayer = nextPos.distanceTo(playerPos);
            if (distanceToPlayer > maxDistance) {
                Vector3d toPlayer = playerPos.subtract(nextPos).normalize().scale(0.02);
                velocity = velocity.add(toPlayer);
            }

            currentPos = currentPos.add(velocity.scale(speed.getValue()));
            points.add(0, currentPos);

            if (points.size() > lineLength.getValue()) {
                points.remove(points.size() - 1);
            }
        }

        private void updatePoints() {
        }

        private void spawnParticles() {
            if (particleTimer.finished(50)) {
                for (int i = 0; i < particleCount.getValue() / 5; i++) {
                    if (points.size() > 2) {
                        Vector3d basePos = points.get(random.nextInt(Math.min(5, points.size())));
                        Vector3d offset = new Vector3d(
                                (random.nextDouble() - 0.5) * 0.5,
                                (random.nextDouble() - 0.5) * 0.3,
                                (random.nextDouble() - 0.5) * 0.5
                        );
                        particles.add(new Particle(basePos.add(offset)));
                    }
                }
                particleTimer.reset();
            }
        }

        private void updateParticles() {
            Iterator<Particle> iterator = particles.iterator();
            while (iterator.hasNext()) {
                Particle particle = iterator.next();
                particle.update();
                if (particle.isDead()) {
                    iterator.remove();
                }
            }
        }

        public List<Vector3d> getPoints() {
            return points;
        }

        public List<Particle> getParticles() {
            return particles;
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    class Particle {
        Vector3d position;
        Vector3d velocity;
        final StopWatch lifeTimer = new StopWatch();
        final long maxLife = 1000 + random.nextLong(1500);

        public Particle(Vector3d position) {
            this.position = position;
            this.velocity = new Vector3d(
                    (random.nextDouble() - 0.5) * 0.05,
                    random.nextDouble() * 0.02,
                    (random.nextDouble() - 0.5) * 0.05
            );
        }

        public void update() {
            velocity = velocity.scale(0.98);
            position = position.add(velocity);
        }

        public boolean isDead() {
            return lifeTimer.finished(maxLife);
        }

        public float getLifePercent() {
            return Math.max(0, 1.0f - (float) lifeTimer.elapsedTime() / maxLife);
        }

        public Vector3d getPosition() {
            return position;
        }
    }
}