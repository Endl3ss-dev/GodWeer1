package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.util.Namespaced;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
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
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.common.impl.fastrandom.FastRandom;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ShotFireWorks", category = Category.RENDER, description = "Пускает красивый залп феерверков кушает много фпс")
public class ShotFireWorks extends Module implements IRender {

    public static ShotFireWorks getInstance() {
        return Instance.get(ShotFireWorks.class);
    }

    private final SliderSetting fireworkCount = new SliderSetting(this, "Количество", 8, 1, 30, 1);
    private final SliderSetting launchDelay = new SliderSetting(this, "Задержка запуска (мс)", 800, 200, 4000, 100);
    private final SliderSetting minLaunchRadius = new SliderSetting(this, "Мин. радиус запуска", 5, 1, 80, 1);
    private final SliderSetting maxLaunchRadius = new SliderSetting(this, "Макс. радиус запуска", 35, 1, 80, 1);
    private final SliderSetting minLaunchHeight = new SliderSetting(this, "Мин. высота спавна", -3.0f, -15.0f, 80.0f, 1f);
    private final SliderSetting maxLaunchHeight = new SliderSetting(this, "Макс. высота спавна", 3.0f, -15.0f, 80.0f, 1f);
    private final SliderSetting explosionHeight = new SliderSetting(this, "Высота взрыва", 35, 15, 70, 1);
    private final SliderSetting ascentSpeed = new SliderSetting(this, "Скорость подъема", 1.8f, 0.8f, 4.0f, 0.1f);

    private final SliderSetting explosionParticles = new SliderSetting(this, "Частиц во взрыве", 120, 40, 250, 10);
    private final SliderSetting explosionSize = new SliderSetting(this, "Размер взрыва", 2.0f, 0.8f, 4.0f, 0.1f);
    private final SliderSetting explosionLife = new SliderSetting(this, "Жизнь частиц (мс)", 3500, 1000, 6000, 100);
    private final SliderSetting gravity = new SliderSetting(this, "Гравитация", 0.055f, 0.01f, 0.15f, 0.005f);

    private final BooleanSetting trail = new BooleanSetting(this, "Искры при полете", true);
    private final SliderSetting trailParticles = new SliderSetting(this, "Кол-во искр", 2, 1, 8, 1).setVisible(trail::getValue);
    private final BooleanSetting twinkle = new BooleanSetting(this, "Мерцание", true);
    private final BooleanSetting fadeEffect = new BooleanSetting(this, "Эффект затухания", true);
    private final BooleanSetting randomSpawn = new BooleanSetting(this, "Случайный спавн", true);
    private final SliderSetting spawnVariation = new SliderSetting(this, "Разброс спавна", 0.4f, 0.1f, 1.0f, 0.05f).setVisible(randomSpawn::getValue);

    private final ModeSetting colorMode = new ModeSetting(this, "Цвет", "Радужный", "Радужный", "Клиентский", "Свой", "Случайный");
    private final ColorSetting customColor = new ColorSetting(this, "Свой цвет").setVisible(() -> colorMode.is("Свой"));

    private final List<Firework> fireworks = new CopyOnWriteArrayList<>();
    private final StopWatch launchTimer = new StopWatch();
    private final FastRandom random = new FastRandom();
    private long lastFrameTime = System.currentTimeMillis();

    @Override
    public void toggle() {
        super.toggle();
        clear();
    }

    @EventHandler
    public void onWorldChange(WorldChangeEvent event) {
        clear();
    }

    private void clear() {
        fireworks.clear();
        launchTimer.reset();
        lastFrameTime = System.currentTimeMillis();
    }

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        if (mc.player == null) return;

        long currentTime = System.currentTimeMillis();
        float deltaTime = Math.min((currentTime - lastFrameTime) / 1000.0f, 0.1f);
        lastFrameTime = currentTime;

        long currentDelay = launchDelay.getValue().longValue();
        if (randomSpawn.getValue()) {
            currentDelay = (long) (currentDelay * (0.6f + random.nextFloat() * spawnVariation.getValue() * 0.8f));
        }

        if (launchTimer.finished(currentDelay) && fireworks.size() < fireworkCount.getValue().intValue()) {
            int launchCount = randomSpawn.getValue() ? (random.nextFloat() < 0.3f ? 2 : 1) : 1;
            for (int i = 0; i < launchCount && fireworks.size() < fireworkCount.getValue().intValue(); i++) {
                launchFirework();
            }
            launchTimer.reset();
        }

        if (fireworks.isEmpty()) return;

        MatrixStack matrix = event.getMatrix();
        setupRenderState();

        for (Firework firework : fireworks) {
            firework.update(deltaTime);
            renderFirework(matrix, firework);
        }

        fireworks.removeIf(Firework::isDead);

        restoreRenderState();
    }

    private void launchFirework() {
        Vector3d playerPos = mc.player.getPositionVec();

        double angle = random.nextDouble() * Math.PI * 2;
        float minRadius = Math.min(minLaunchRadius.getValue(), maxLaunchRadius.getValue());
        float maxRadius = Math.max(minLaunchRadius.getValue(), maxLaunchRadius.getValue());
        double distance = minRadius + random.nextDouble() * (maxRadius - minRadius);

        double launchX = playerPos.x + Math.cos(angle) * distance;
        double launchZ = playerPos.z + Math.sin(angle) * distance;

        if (randomSpawn.getValue()) {
            launchX += (random.nextDouble() - 0.5) * distance * 0.25;
            launchZ += (random.nextDouble() - 0.5) * distance * 0.25;
        }

        float minH = Math.min(minLaunchHeight.getValue(), maxLaunchHeight.getValue());
        float maxH = Math.max(minLaunchHeight.getValue(), maxLaunchHeight.getValue());
        double launchY = playerPos.y + minH + random.nextDouble() * (maxH - minH);

        Vector3d startPos = new Vector3d(launchX, launchY, launchZ);
        fireworks.add(new Firework(startPos));
    }

    private void setupRenderState() {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }

    private void restoreRenderState() {
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.popMatrix();
    }

    private void renderFirework(MatrixStack matrix, Firework firework) {
        Vector3d camera = RenderUtil3D.cameraPos();
        matrix.push();
        matrix.translate(-camera.x, -camera.y, -camera.z);

        List<Particle> particles = firework.getParticles();
        for (int i = 0, size = particles.size(); i < size; i++) {
            renderParticle(matrix, particles.get(i));
        }

        matrix.pop();
    }

    private void renderParticle(MatrixStack matrix, Particle particle) {
        Vector3d pos = particle.getPosition();
        float life = particle.getLifePercent();
        if (life <= 0.01f) return;

        float particleSize = particle.getSize();

        if (fadeEffect.getValue()) {
            float fadeMultiplier = life < 0.3f ? life / 0.3f : 1.0f;
            particleSize *= fadeMultiplier;
        }

        if (twinkle.getValue() && particle.canTwinkle()) {
            long time = System.currentTimeMillis();
            float twinklePhase = (float) Math.sin(time * 0.01f + pos.x * 100 + pos.z * 100) * 0.5f + 0.5f;
            particleSize *= (0.75f + twinklePhase * 0.5f);
        }

        int color = particle.getColor();
        float alpha = life;

        if (fadeEffect.getValue() && life < 0.2f) {
            alpha *= (life / 0.2f);
        }

        color = ColorUtil.replAlpha(color, alpha);

        matrix.push();
        RenderUtil3D.setupOrientationMatrix(matrix, (float) pos.x, (float) pos.y, (float) pos.z);
        matrix.rotate(mc.getRenderManager().getCameraOrientation());
        matrix.rotate(Vector3f.ZP.rotationDegrees(180F));

        RenderUtil.bindTexture(particle.getTexture());
        RectUtil.drawRect(matrix, -particleSize / 2, -particleSize / 2, particleSize, particleSize, color, true, true);

        matrix.pop();
    }

    private int getFireworkColor() {
        return switch (colorMode.getValue()) {
            case "Клиентский" -> Theme.getInstance().clientColor();
            case "Свой" -> customColor.getValue().hashCode();
            case "Случайный" -> {
                float hue = random.nextFloat();
                yield ColorUtil.hslToRgb(hue, 0.9f, 0.6f, 1.0f);
            }
            default -> ColorUtil.rainbow(1, (int) (random.nextFloat() * 1000), 0.85f, 1.0f, 1.0f);
        };
    }

    private interface Particle {
        void update(float deltaTime);

        boolean isDead();

        float getLifePercent();

        Vector3d getPosition();

        int getColor();

        float getSize();

        Namespaced getTexture();

        boolean canTwinkle();
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    private class Firework {
        enum State {RISING, EXPLODED, DEAD}

        @NonFinal
        State state = State.RISING;
        final List<Particle> particles = new CopyOnWriteArrayList<>();
        final int baseColor;
        final double explosionHeightTarget;
        final float windResistance;
        Vector3d position;
        Vector3d velocity;

        public Firework(Vector3d startPos) {
            this.position = startPos;
            this.baseColor = getFireworkColor();
            this.windResistance = 0.98f + random.nextFloat() * 0.015f;

            double heightVariation = randomSpawn.getValue() ? (0.75 + random.nextDouble() * 0.5) : 1.0;
            this.explosionHeightTarget = startPos.y + explosionHeight.getValue().doubleValue() * heightVariation;

            double horizontalSpeed = randomSpawn.getValue() ? (random.nextDouble() - 0.5) * 0.15 : (random.nextDouble() - 0.5) * 0.08;
            this.velocity = new Vector3d(
                    horizontalSpeed,
                    ascentSpeed.getValue().doubleValue() * 0.12,
                    (random.nextDouble() - 0.5) * 0.08
            );

            particles.add(new RocketParticle(this));
        }

        public void update(float deltaTime) {
            if (state == State.RISING) {
                velocity = velocity.add(0, 0.008, 0);
                velocity = velocity.scale(windResistance);
                position = position.add(velocity.scale(deltaTime * 60));

                if (trail.getValue() && random.nextFloat() < 0.6f) {
                    int trailCount = trailParticles.getValue().intValue();
                    for (int i = 0; i < trailCount; i++) {
                        particles.add(new TrailParticle(position, baseColor));
                    }
                }

                if (position.y >= explosionHeightTarget) {
                    explode();
                }
            }

            List<Particle> particleList = new ArrayList<>(particles);
            for (Particle particle : particleList) {
                particle.update(deltaTime);
            }
            particles.removeIf(Particle::isDead);

            if (state == State.EXPLODED && particles.isEmpty()) {
                state = State.DEAD;
            }
        }

        private void explode() {
            this.state = State.EXPLODED;

            int particleCount = explosionParticles.getValue().intValue();
            float goldenAngle = (float) Math.PI * (3.0f - (float) Math.sqrt(5.0f));

            for (int i = 0; i < particleCount; i++) {
                int particleColor = baseColor;

                if (colorMode.is("Радужный")) {
                    float hueOffset = (float) i / particleCount;
                    particleColor = ColorUtil.rainbow(1, (int) (hueOffset * 1000), 0.85f, 1.0f, 1.0f);
                } else if (colorMode.is("Случайный")) {
                    particleColor = random.nextFloat() < 0.15f ? ColorUtil.WHITE : baseColor;
                } else if (random.nextFloat() < 0.12f) {
                    particleColor = ColorUtil.WHITE;
                }

                particles.add(new ExplosionParticle(position, particleColor, i, particleCount));
            }

            int sparkCount = 15 + random.nextInt(10);
            for (int i = 0; i < sparkCount; i++) {
                particles.add(new SparkParticle(position, ColorUtil.WHITE));
            }

            if (random.nextFloat() < 0.4f) {
                for (int i = 0; i < 8; i++) {
                    particles.add(new SecondaryExplosionParticle(position, baseColor));
                }
            }
        }

        public boolean isDead() {
            return state == State.DEAD;
        }

        public List<Particle> getParticles() {
            return particles;
        }
    }

    private class RocketParticle implements Particle {
        final Firework parent;

        public RocketParticle(Firework parent) {
            this.parent = parent;
        }

        @Override
        public void update(float deltaTime) {
        }

        @Override
        public boolean isDead() {
            return parent.state != Firework.State.RISING;
        }

        @Override
        public float getLifePercent() {
            return 1.0f;
        }

        @Override
        public Vector3d getPosition() {
            return parent.position;
        }

        @Override
        public int getColor() {
            return ColorUtil.interpolateColor(parent.baseColor, 0xFFFFFFFF, 0.3f);
        }

        @Override
        public float getSize() {
            return 2.5f;
        }

        @Override
        public Namespaced getTexture() {
            return new Namespaced("particle/bloom.png");
        }

        @Override
        public boolean canTwinkle() {
            return false;
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    private class TrailParticle implements Particle {
        final StopWatch lifeTimer = new StopWatch();
        final long maxLife = 500 + random.nextInt(300);
        final int color;
        Vector3d position;
        Vector3d velocity;
        final float initialSize;

        public TrailParticle(Vector3d startPos, int color) {
            this.position = startPos.add(
                    (random.nextDouble() - 0.5) * 0.1,
                    (random.nextDouble() - 0.5) * 0.1,
                    (random.nextDouble() - 0.5) * 0.1
            );
            this.color = ColorUtil.interpolateColor(color, 0xFFFF8800, 0.4f);
            this.velocity = new Vector3d(
                    (random.nextDouble() - 0.5) * 0.025,
                    -0.04 - random.nextDouble() * 0.02,
                    (random.nextDouble() - 0.5) * 0.025
            );
            this.initialSize = 0.6f + random.nextFloat() * 0.4f;
        }

        @Override
        public void update(float deltaTime) {
            velocity = velocity.add(0, -0.002, 0);
            position = position.add(velocity.scale(deltaTime * 60));
        }

        @Override
        public boolean isDead() {
            return lifeTimer.finished(maxLife);
        }

        @Override
        public float getLifePercent() {
            return Math.max(0, 1.0f - (float) lifeTimer.elapsedTime() / maxLife);
        }

        @Override
        public Vector3d getPosition() {
            return position;
        }

        @Override
        public int getColor() {
            float life = getLifePercent();
            return ColorUtil.interpolateColor(0xFF660000, color, life);
        }

        @Override
        public float getSize() {
            return initialSize * (0.4f + getLifePercent() * 0.6f);
        }

        @Override
        public Namespaced getTexture() {
            return new Namespaced("particle/flame.png");
        }

        @Override
        public boolean canTwinkle() {
            return true;
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    private class ExplosionParticle implements Particle {
        final StopWatch lifeTimer = new StopWatch();
        final long maxLife;
        final int color;
        final float baseSize;
        Vector3d position;
        Vector3d velocity;
        final float airResistance;

        public ExplosionParticle(Vector3d startPos, int color, int index, int totalParticles) {
            this.position = startPos;
            this.color = color;
            this.maxLife = explosionLife.getValue().longValue() + random.nextInt(1000) - 500;
            this.baseSize = 1.2f + random.nextFloat() * 2.0f;
            this.airResistance = 0.975f + random.nextFloat() * 0.015f;

            float phi = (float) Math.acos(1.0f - 2.0f * (index + 0.5f) / totalParticles);
            float goldenAngle = (float) Math.PI * (3.0f - (float) Math.sqrt(5.0f));
            float theta = goldenAngle * index;

            double speed = (0.12 + random.nextDouble() * 0.08) * explosionSize.getValue();

            this.velocity = new Vector3d(
                    Math.sin(phi) * Math.cos(theta) * speed,
                    Math.cos(phi) * speed,
                    Math.sin(phi) * Math.sin(theta) * speed
            );
        }

        @Override
        public void update(float deltaTime) {
            velocity = velocity.add(0, -gravity.getValue() * 0.12 * deltaTime, 0);
            velocity = velocity.scale(Math.pow(airResistance, deltaTime));
            position = position.add(velocity.scale(deltaTime * 60));
        }

        @Override
        public boolean isDead() {
            return lifeTimer.finished(maxLife);
        }

        @Override
        public float getLifePercent() {
            return Math.max(0, 1.0f - (float) lifeTimer.elapsedTime() / maxLife);
        }

        @Override
        public Vector3d getPosition() {
            return position;
        }

        @Override
        public int getColor() {
            float life = getLifePercent();
            if (life > 0.85f) {
                return ColorUtil.interpolateColor(0xFFFFFFFF, color, (life - 0.85f) * 6.67f);
            }
            return color;
        }

        @Override
        public float getSize() {
            float life = getLifePercent();
            if (life > 0.92f) {
                return baseSize * 1.6f * ((1.0f - life) / 0.08f);
            }
            return baseSize * (0.3f + life * 0.7f);
        }

        @Override
        public Namespaced getTexture() {
            return new Namespaced("particle/circle.png");
        }

        @Override
        public boolean canTwinkle() {
            return twinkle.getValue();
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    private class SparkParticle implements Particle {
        final StopWatch lifeTimer = new StopWatch();
        final long maxLife = 1200 + random.nextInt(600);
        Vector3d position;
        Vector3d velocity;
        final float sparkLength;
        final float rotationSpeed;
        float rotation;

        public SparkParticle(Vector3d startPos, int color) {
            this.position = startPos;
            this.sparkLength = 2.5f + random.nextFloat() * 2.5f;
            this.rotationSpeed = (random.nextFloat() - 0.5f) * 10.0f;

            double angle = random.nextDouble() * Math.PI * 2;
            double pitch = (random.nextDouble() - 0.5) * Math.PI * 0.8;
            double speed = 0.25 + random.nextDouble() * 0.25;

            this.velocity = new Vector3d(
                    Math.cos(angle) * Math.cos(pitch) * speed,
                    Math.sin(pitch) * speed,
                    Math.sin(angle) * Math.cos(pitch) * speed
            );
        }

        @Override
        public void update(float deltaTime) {
            velocity = velocity.add(0, -gravity.getValue() * 0.18 * deltaTime, 0);
            velocity = velocity.scale(Math.pow(0.94, deltaTime));
            position = position.add(velocity.scale(deltaTime * 60));
            rotation += rotationSpeed * deltaTime;
        }

        @Override
        public boolean isDead() {
            return lifeTimer.finished(maxLife);
        }

        @Override
        public float getLifePercent() {
            return Math.max(0, 1.0f - (float) lifeTimer.elapsedTime() / maxLife);
        }

        @Override
        public Vector3d getPosition() {
            return position;
        }

        @Override
        public int getColor() {
            float life = getLifePercent();
            int white = 0xFFFFFFFF;
            int yellow = 0xFFFFDD00;
            int orange = 0xFFFF6600;
            int red = 0xFFCC0000;

            if (life > 0.75f) {
                return ColorUtil.interpolateColor(white, yellow, (life - 0.75f) * 4.0f);
            } else if (life > 0.4f) {
                return ColorUtil.interpolateColor(yellow, orange, (0.75f - life) / 0.35f);
            } else if (life > 0.15f) {
                return ColorUtil.interpolateColor(orange, red, (0.4f - life) / 0.25f);
            } else {
                return ColorUtil.interpolateColor(red, 0xFF330000, (0.15f - life) / 0.15f);
            }
        }

        @Override
        public float getSize() {
            float life = getLifePercent();
            return sparkLength * life * (0.6f + (float) Math.sin(life * Math.PI * 4) * 0.4f);
        }

        @Override
        public Namespaced getTexture() {
            return new Namespaced("particle/spark.png");
        }

        @Override
        public boolean canTwinkle() {
            return false;
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    private class SecondaryExplosionParticle implements Particle {
        final StopWatch lifeTimer = new StopWatch();
        final StopWatch delayTimer = new StopWatch();
        final long maxLife = 800 + random.nextInt(400);
        final long delay = 200 + random.nextInt(300);
        final int color;
        Vector3d position;
        Vector3d velocity;
        final float size;
        boolean activated = false;

        public SecondaryExplosionParticle(Vector3d startPos, int color) {
            this.position = startPos;
            this.color = ColorUtil.interpolateColor(color, ColorUtil.WHITE, 0.5f);
            this.size = 0.8f + random.nextFloat();

            double angle = random.nextDouble() * Math.PI * 2;
            double pitch = random.nextDouble() * Math.PI - Math.PI / 2;
            double speed = 0.08 + random.nextDouble() * 0.12;

            this.velocity = new Vector3d(
                    Math.cos(angle) * Math.cos(pitch) * speed,
                    Math.sin(pitch) * speed,
                    Math.sin(angle) * Math.cos(pitch) * speed
            );
        }

        @Override
        public void update(float deltaTime) {
            if (!activated && delayTimer.finished(delay)) {
                activated = true;
            }

            if (activated) {
                velocity = velocity.add(0, -gravity.getValue() * 0.1 * deltaTime, 0);
                velocity = velocity.scale(Math.pow(0.97, deltaTime));
                position = position.add(velocity.scale(deltaTime * 60));
            }
        }

        @Override
        public boolean isDead() {
            return activated && lifeTimer.finished(maxLife);
        }

        @Override
        public float getLifePercent() {
            if (!activated) return 1.0f;
            return Math.max(0, 1.0f - (float) lifeTimer.elapsedTime() / maxLife);
        }

        @Override
        public Vector3d getPosition() {
            return position;
        }

        @Override
        public int getColor() {
            return color;
        }

        @Override
        public float getSize() {
            if (!activated) return size * 0.5f;
            return size * getLifePercent();
        }

        @Override
        public Namespaced getTexture() {
            return new Namespaced("particle/circle.png");
        }

        @Override
        public boolean canTwinkle() {
            return true;
        }
    }
}