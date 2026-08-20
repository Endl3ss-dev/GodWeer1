package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.lib.util.time.StopWatch;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "KillEffect", category = Category.RENDER)
public class KillEffect extends Module {
    public static KillEffect getInstance() {
        return Instance.get(KillEffect.class);
    }

    private final ModeSetting effectType = new ModeSetting(this, "Эффект", "Взрыв частиц",
            "Взрыв частиц", "Кольца энергии", "Молнии", "Огненный вихрь", "Ледяные кристаллы",
            "Кровавый фонтан", "Звездный взрыв", "Темная материя", "Радужная спираль",
            "Атомный распад", "Портал в пустоту", "Священный луч", "Магическая руна",
            "Плазменный шар", "Гравитационный коллапс", "Лазерная решетка", "Хаотичный разлом");

    private final SliderSetting duration = new SliderSetting(this, "Длительность", 2000, 500, 10000, 100);
    private final SliderSetting size = new SliderSetting(this, "Размер", 1.0f, 0.2f, 5.0f, 0.1f);
    private final SliderSetting intensity = new SliderSetting(this, "Интенсивность", 1.0f, 0.1f, 3.0f, 0.1f);
    private final SliderSetting particleCount = new SliderSetting(this, "Количество частиц", 50, 10, 200, 5);
    private final SliderSetting alpha = new SliderSetting(this, "Прозрачность", 0.8f, 0.1f, 1.0f, 0.01f);

    private final ColorSetting primaryColor = new ColorSetting(this, "Основной цвет", 0xFFFF0000);
    private final ColorSetting secondaryColor = new ColorSetting(this, "Дополнительный цвет", 0xFFFFFF00);
    private final ColorSetting accentColor = new ColorSetting(this, "Акцентный цвет", 0xFF00FFFF);

    private final BooleanSetting rainbowMode = new BooleanSetting(this, "Радужный режим", false);
    private final BooleanSetting fadeOut = new BooleanSetting(this, "Затухание", true);
    private final BooleanSetting soundEffect = new BooleanSetting(this, "Звуковой эффект", true);
    private final SliderSetting soundVolume = new SliderSetting(this, "Громкость", 0.5f, 0.0f, 1.0f, 0.1f);

    private final BooleanSetting multiLayer = new BooleanSetting(this, "Многослойность", true);
    private final SliderSetting layerCount = new SliderSetting(this, "Количество слоев", 3, 1, 8, 1);
    private final SliderSetting layerOffset = new SliderSetting(this, "Смещение слоев", 0.3f, 0.1f, 1.0f, 0.1f);

    private final BooleanSetting physics = new BooleanSetting(this, "Физика", true);
    private final SliderSetting gravity = new SliderSetting(this, "Гравитация", 0.02f, 0.0f, 0.1f, 0.01f);
    private final SliderSetting airResistance = new SliderSetting(this, "Сопротивление воздуха", 0.98f, 0.9f, 1.0f, 0.01f);

    private final ModeSetting animationType = new ModeSetting(this, "Анимация", "Bounce Out",
            "Linear", "Quad Out", "Cubic Out", "Quart Out", "Sine Out", "Expo Out",
            "Circ Out", "Back Out", "Elastic Out", "Bounce Out");

    private final BooleanSetting screenShake = new BooleanSetting(this, "Тряска экрана", false);
    private final SliderSetting shakeIntensity = new SliderSetting(this, "Сила тряски", 2.0f, 0.5f, 10.0f, 0.5f);

    private final BooleanSetting customParticles = new BooleanSetting(this, "Кастомные частицы", true);
    private final ModeSetting particleShape = new ModeSetting(this, "Форма частиц", "Сфера",
            "Сфера", "Куб", "Звезда", "Сердце", "Череп", "Молния", "Огонь", "Лед");

    private final List<KillEffectInstance> effects = new ArrayList<>();
    private final Random random = new Random();

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (!(event.getTarget() instanceof LivingEntity target)) return;

        if (target.getHealth() <= 0 || !target.isAlive()) {
            Vector3d pos = target.getPositionVec().add(0, target.getHeight() / 2, 0);
            effects.add(new KillEffectInstance(pos, effectType.getValue()));

            if (soundEffect.getValue()) {
                playKillSound();
            }

            if (screenShake.getValue()) {
                applyScreenShake();
            }
        }
    }

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        if (effects.isEmpty()) return;

        Iterator<KillEffectInstance> iterator = effects.iterator();
        while (iterator.hasNext()) {
            KillEffectInstance effect = iterator.next();
            if (effect.shouldRemove()) {
                iterator.remove();
                continue;
            }
            effect.render(event.getMatrix());
        }
    }

    private void playKillSound() {
        String effect = effectType.getValue();
        switch (effect) {
            case "Взрыв частиц":
                mc.player.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, soundVolume.getValue(), 1.0f);
                break;
            case "Молнии":
                mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, soundVolume.getValue(), 1.5f);
                break;
            case "Огненный вихрь":
                mc.player.playSound(SoundEvents.ITEM_FIRECHARGE_USE, soundVolume.getValue(), 0.8f);
                break;
            case "Ледяные кристаллы":
                mc.player.playSound(SoundEvents.BLOCK_GLASS_BREAK, soundVolume.getValue(), 1.2f);
                break;
            default:
                mc.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, soundVolume.getValue(), 1.0f);
                break;
        }
    }

    private void applyScreenShake() {
        float shake = shakeIntensity.getValue();
        if (mc.getRenderViewEntity() != null) {
            mc.getRenderViewEntity().rotationPitch += (random.nextFloat() - 0.5f) * shake;
            mc.getRenderViewEntity().rotationYaw += (random.nextFloat() - 0.5f) * shake;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        effects.clear();
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    class KillEffectInstance {
        final Vector3d pos;
        final String effectType;
        final StopWatch timer = new StopWatch();
        final Animation animation = new Animation();
        final List<EffectParticle> particles = new ArrayList<>();

        public KillEffectInstance(Vector3d pos, String effectType) {
            this.pos = pos;
            this.effectType = effectType;
            this.animation.run(1.0, duration.getValue() / 1000.0, getEasing());
            initializeParticles();
        }

        private void initializeParticles() {
            int count = particleCount.getValue().intValue();
            for (int i = 0; i < count; i++) {
                particles.add(new EffectParticle(pos, effectType, i));
            }
        }

        public void render(MatrixStack matrices) {
            animation.update();

            matrices.push();
            Vector3d camera = RenderUtil3D.cameraPos();
            matrices.translate(pos.x - camera.x, pos.y - camera.y, pos.z - camera.z);

            setupRenderState();

            switch (effectType) {
                case "Взрыв частиц":
                    renderParticleExplosion(matrices);
                    break;
                case "Кольца энергии":
                    renderEnergyRings(matrices);
                    break;
                case "Молнии":
                    renderLightning(matrices);
                    break;
                case "Огненный вихрь":
                    renderFireVortex(matrices);
                    break;
                case "Ледяные кристаллы":
                    renderIceCrystals(matrices);
                    break;
                case "Кровавый фонтан":
                    renderBloodFountain(matrices);
                    break;
                case "Звездный взрыв":
                    renderStarBurst(matrices);
                    break;
                case "Темная материя":
                    renderDarkMatter(matrices);
                    break;
                case "Радужная спираль":
                    renderRainbowSpiral(matrices);
                    break;
                case "Атомный распад":
                    renderAtomicDecay(matrices);
                    break;
                case "Портал в пустоту":
                    renderVoidPortal(matrices);
                    break;
                case "Священный луч":
                    renderHolyBeam(matrices);
                    break;
                case "Магическая руна":
                    renderMagicRune(matrices);
                    break;
                case "Плазменный шар":
                    renderPlasmaBall(matrices);
                    break;
                case "Гравитационный коллапс":
                    renderGravityCollapse(matrices);
                    break;
                case "Лазерная решетка":
                    renderLaserGrid(matrices);
                    break;
                case "Хаотичный разлом":
                    renderChaoticRift(matrices);
                    break;
            }

            restoreRenderState();
            matrices.pop();
        }

        private void renderParticleExplosion(MatrixStack matrices) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            for (EffectParticle particle : particles) {
                particle.update();
                int color = getParticleColor(particle);
                float particleSize = 0.1f * size.getValue();

                Vector3d pPos = particle.position;
                builder.pos(matrix, (float) (pPos.x - particleSize), (float) (pPos.y - particleSize), (float) (pPos.z - particleSize)).color(color).endVertex();
                builder.pos(matrix, (float) (pPos.x + particleSize), (float) (pPos.y - particleSize), (float) (pPos.z - particleSize)).color(color).endVertex();
                builder.pos(matrix, (float) (pPos.x + particleSize), (float) (pPos.y + particleSize), (float) (pPos.z + particleSize)).color(color).endVertex();
                builder.pos(matrix, (float) (pPos.x - particleSize), (float) (pPos.y + particleSize), (float) (pPos.z + particleSize)).color(color).endVertex();
            }

            tessellator.draw();
        }

        private void renderEnergyRings(MatrixStack matrices) {
            float progress = animation.get();
            int layers = multiLayer.getValue() ? layerCount.getValue().intValue() : 1;

            for (int layer = 0; layer < layers; layer++) {
                float layerProgress = progress - layer * layerOffset.getValue() / layers;
                if (layerProgress <= 0) continue;

                float radius = layerProgress * size.getValue() * 3.0f;
                int segments = 64;

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder builder = tessellator.getBuffer();
                Matrix4f matrix = matrices.getLast().getMatrix();

                builder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

                for (int i = 0; i <= segments; i++) {
                    float angle = (float) (2 * Math.PI * i / segments);
                    float x1 = (float) (radius * Math.cos(angle));
                    float z1 = (float) (radius * Math.sin(angle));
                    float x2 = (float) ((radius - 0.2f) * Math.cos(angle));
                    float z2 = (float) ((radius - 0.2f) * Math.sin(angle));

                    int color = getRingColor(layer, i, segments);

                    builder.pos(matrix, x1, 0, z1).color(color).endVertex();
                    builder.pos(matrix, x2, 0, z2).color(ColorUtil.multAlpha(color, 0)).endVertex();
                }

                tessellator.draw();
            }
        }

        private void renderLightning(MatrixStack matrices) {
            GL11.glLineWidth(3.0f * intensity.getValue());
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

            int bolts = 8;
            for (int i = 0; i < bolts; i++) {
                float angle = (float) (2 * Math.PI * i / bolts);
                float length = size.getValue() * 2.0f * animation.get();

                float x = 0, y = 0, z = 0;
                float targetX = (float) (length * Math.cos(angle));
                float targetZ = (float) (length * Math.sin(angle));

                int segments = 10;
                for (int j = 0; j < segments; j++) {
                    float progress = (float) j / segments;
                    float nextProgress = (float) (j + 1) / segments;

                    float currentX = x + (targetX - x) * progress;
                    float currentZ = z + (targetZ - z) * progress;
                    float currentY = y + (random.nextFloat() - 0.5f) * 0.5f;

                    float nextX = x + (targetX - x) * nextProgress;
                    float nextZ = z + (targetZ - z) * nextProgress;
                    float nextY = y + (random.nextFloat() - 0.5f) * 0.5f;

                    int color = getLightningColor();

                    builder.pos(matrix, currentX, currentY, currentZ).color(color).endVertex();
                    builder.pos(matrix, nextX, nextY, nextZ).color(color).endVertex();
                }
            }

            tessellator.draw();
        }

        private void renderFireVortex(MatrixStack matrices) {
            float time = timer.elapsedTime() / 1000.0f;
            int layers = 5;

            for (int layer = 0; layer < layers; layer++) {
                matrices.push();
                matrices.rotate(Vector3f.YP.rotationDegrees(time * 90 + layer * 72));

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder builder = tessellator.getBuffer();
                Matrix4f matrix = matrices.getLast().getMatrix();

                builder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

                float baseRadius = 0.5f + layer * 0.3f;
                float height = size.getValue() * 2.0f;
                int segments = 32;

                for (int i = 0; i <= segments; i++) {
                    float angle = (float) (2 * Math.PI * i / segments);
                    float radius = baseRadius * (1.0f - (float) layer / layers) * animation.get();

                    float x = (float) (radius * Math.cos(angle));
                    float z = (float) (radius * Math.sin(angle));

                    int bottomColor = getFireColor(layer, 0.8f);
                    int topColor = getFireColor(layer, 0.2f);

                    builder.pos(matrix, x, 0, z).color(bottomColor).endVertex();
                    builder.pos(matrix, x * 0.5f, height, z * 0.5f).color(topColor).endVertex();
                }

                tessellator.draw();
                matrices.pop();
            }
        }

        private void renderIceCrystals(MatrixStack matrices) {
            int crystalCount = 12;
            for (int i = 0; i < crystalCount; i++) {
                matrices.push();

                float angle = (float) (2 * Math.PI * i / crystalCount);
                float distance = size.getValue() * animation.get();
                float x = (float) (distance * Math.cos(angle));
                float z = (float) (distance * Math.sin(angle));

                matrices.translate(x, 0, z);
                matrices.rotate(Vector3f.YP.rotationDegrees(angle * 180 / (float) Math.PI));

                renderCrystal(matrices, i);
                matrices.pop();
            }
        }

        private void renderCrystal(MatrixStack matrices, int index) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            builder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

            float height = 1.0f * size.getValue();
            float width = 0.3f * size.getValue();
            int color = getIceColor(index);

            builder.pos(matrix, 0, height, 0).color(color).endVertex();
            builder.pos(matrix, -width, 0, 0).color(ColorUtil.multAlpha(color, 0.3f)).endVertex();
            builder.pos(matrix, width, 0, 0).color(ColorUtil.multAlpha(color, 0.3f)).endVertex();

            builder.pos(matrix, 0, height, 0).color(color).endVertex();
            builder.pos(matrix, 0, 0, -width).color(ColorUtil.multAlpha(color, 0.3f)).endVertex();
            builder.pos(matrix, 0, 0, width).color(ColorUtil.multAlpha(color, 0.3f)).endVertex();

            tessellator.draw();
        }

        private void renderBloodFountain(MatrixStack matrices) {
            for (EffectParticle particle : particles) {
                particle.update();
                if (particle.velocity.y < 0) continue;

                matrices.push();
                Vector3d pPos = particle.position;
                matrices.translate(pPos.x, pPos.y, pPos.z);

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder builder = tessellator.getBuffer();
                Matrix4f matrix = matrices.getLast().getMatrix();

                builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

                float particleSize = 0.05f * size.getValue();
                int color = getBloodColor();

                builder.pos(matrix, -particleSize, -particleSize, 0).color(color).endVertex();
                builder.pos(matrix, particleSize, -particleSize, 0).color(color).endVertex();
                builder.pos(matrix, particleSize, particleSize, 0).color(color).endVertex();
                builder.pos(matrix, -particleSize, particleSize, 0).color(color).endVertex();

                tessellator.draw();
                matrices.pop();
            }
        }

        private void renderStarBurst(MatrixStack matrices) {
            int rays = 16;
            float time = timer.elapsedTime() / 1000.0f;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            builder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

            for (int i = 0; i < rays; i++) {
                float angle = (float) (2 * Math.PI * i / rays);
                float length = size.getValue() * 2.0f * animation.get();
                float width = 0.1f * size.getValue();

                float x1 = (float) (length * Math.cos(angle));
                float z1 = (float) (length * Math.sin(angle));
                float x2 = (float) (width * Math.cos(angle + Math.PI / 2));
                float z2 = (float) (width * Math.sin(angle + Math.PI / 2));

                int color = getStarColor(i, time);

                builder.pos(matrix, 0, 0, 0).color(color).endVertex();
                builder.pos(matrix, x1 + x2, 0, z1 + z2).color(ColorUtil.multAlpha(color, 0)).endVertex();
                builder.pos(matrix, x1 - x2, 0, z1 - z2).color(ColorUtil.multAlpha(color, 0)).endVertex();
            }

            tessellator.draw();
        }

        private void renderDarkMatter(MatrixStack matrices) {
            float time = timer.elapsedTime() / 1000.0f;
            int particles = 100;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            builder.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
            GL11.glPointSize(2.0f * size.getValue());

            for (int i = 0; i < particles; i++) {
                float angle = (float) (2 * Math.PI * i / particles);
                float radius = (float) (size.getValue() * (1.0f - animation.get()) +
                        size.getValue() * 0.5f * Math.sin(time * 5 + i * 0.1f));

                float x = (float) (radius * Math.cos(angle + time));
                float z = (float) (radius * Math.sin(angle + time));
                float y = (float) (Math.sin(time * 3 + i * 0.2f) * 0.5f);

                int color = getDarkMatterColor(i, time);

                builder.pos(matrix, x, y, z).color(color).endVertex();
            }

            tessellator.draw();
        }

        private void renderRainbowSpiral(MatrixStack matrices) {
            float time = timer.elapsedTime() / 1000.0f;
            int segments = 200;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            builder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            GL11.glLineWidth(3.0f * size.getValue());

            for (int i = 0; i < segments; i++) {
                float progress = (float) i / segments;
                float angle = progress * 6 * (float) Math.PI + time * 2;
                float radius = size.getValue() * (1.0f - progress) * animation.get();
                float height = progress * size.getValue() * 2.0f;

                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));

                int color = ColorUtil.rainbow(8, i * 2, 0.8f, 1.0f, alpha.getValue());

                builder.pos(matrix, x, height, z).color(color).endVertex();
            }

            tessellator.draw();
        }

        private void renderAtomicDecay(MatrixStack matrices) {
            float time = timer.elapsedTime() / 1000.0f;

            matrices.push();
            matrices.rotate(Vector3f.YP.rotationDegrees(time * 45));

            for (int orbit = 0; orbit < 3; orbit++) {
                matrices.push();
                matrices.rotate(Vector3f.XP.rotationDegrees(orbit * 60));

                float orbitRadius = (0.5f + orbit * 0.3f) * size.getValue();
                int electrons = 4 + orbit * 2;

                for (int electron = 0; electron < electrons; electron++) {
                    float angle = (float) (2 * Math.PI * electron / electrons + time * (orbit + 1));
                    float x = (float) (orbitRadius * Math.cos(angle));
                    float z = (float) (orbitRadius * Math.sin(angle));

                    matrices.push();
                    matrices.translate(x, 0, z);

                    renderElectron(matrices, orbit);
                    matrices.pop();
                }

                matrices.pop();
            }

            renderNucleus(matrices);
            matrices.pop();
        }

        private void renderElectron(MatrixStack matrices, int orbit) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            float size = 0.05f;
            int color = getElectronColor(orbit);

            builder.pos(matrix, -size, -size, 0).color(color).endVertex();
            builder.pos(matrix, size, -size, 0).color(color).endVertex();
            builder.pos(matrix, size, size, 0).color(color).endVertex();
            builder.pos(matrix, -size, size, 0).color(color).endVertex();

            tessellator.draw();
        }

        private void renderNucleus(MatrixStack matrices) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            float nucleusSize = 0.15f * size.getValue();
            int color = primaryColor.getValue();

            for (int i = 0; i < 6; i++) {
                float angle = (float) (i * Math.PI / 3);
                matrices.push();
                matrices.rotate(Vector3f.YP.rotationDegrees(angle * 180 / (float) Math.PI));

                builder.pos(matrix, -nucleusSize, -nucleusSize, 0).color(color).endVertex();
                builder.pos(matrix, nucleusSize, -nucleusSize, 0).color(color).endVertex();
                builder.pos(matrix, nucleusSize, nucleusSize, 0).color(color).endVertex();
                builder.pos(matrix, -nucleusSize, nucleusSize, 0).color(color).endVertex();

                matrices.pop();
            }

            tessellator.draw();
        }

        private void renderVoidPortal(MatrixStack matrices) {
            float time = timer.elapsedTime() / 1000.0f;
            int rings = 8;

            for (int ring = 0; ring < rings; ring++) {
                matrices.push();
                matrices.rotate(Vector3f.ZP.rotationDegrees(time * (30 + ring * 10)));

                float ringRadius = (ring * 0.2f + 0.3f) * size.getValue() * animation.get();
                int segments = 32 - ring * 2;

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder builder = tessellator.getBuffer();
                Matrix4f matrix = matrices.getLast().getMatrix();

                builder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

                for (int i = 0; i <= segments; i++) {
                    float angle = (float) (2 * Math.PI * i / segments);
                    float innerRadius = ringRadius * 0.8f;
                    float outerRadius = ringRadius;

                    float x1 = (float) (innerRadius * Math.cos(angle));
                    float z1 = (float) (innerRadius * Math.sin(angle));
                    float x2 = (float) (outerRadius * Math.cos(angle));
                    float z2 = (float) (outerRadius * Math.sin(angle));

                    int innerColor = getVoidColor(ring, true);
                    int outerColor = getVoidColor(ring, false);

                    builder.pos(matrix, x1, 0, z1).color(innerColor).endVertex();
                    builder.pos(matrix, x2, 0, z2).color(outerColor).endVertex();
                }

                tessellator.draw();
                matrices.pop();
            }
        }

        private void renderHolyBeam(MatrixStack matrices) {
            float beamHeight = size.getValue() * 5.0f * animation.get();
            float beamRadius = size.getValue() * 0.5f;
            int segments = 16;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            builder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

            for (int i = 0; i <= segments; i++) {
                float angle = (float) (2 * Math.PI * i / segments);
                float x = (float) (beamRadius * Math.cos(angle));
                float z = (float) (beamRadius * Math.sin(angle));

                int bottomColor = getHolyColor(1.0f);
                int topColor = getHolyColor(0.0f);

                builder.pos(matrix, x, 0, z).color(bottomColor).endVertex();
                builder.pos(matrix, 0, beamHeight, 0).color(topColor).endVertex();
            }

            tessellator.draw();

            for (int layer = 0; layer < 3; layer++) {
                matrices.push();
                matrices.rotate(Vector3f.YP.rotationDegrees(timer.elapsedTime() * 0.1f * (layer + 1)));

                builder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

                float layerRadius = beamRadius * (1.0f + layer * 0.3f);
                for (int i = 0; i <= segments; i++) {
                    float angle = (float) (2 * Math.PI * i / segments);
                    float x = (float) (layerRadius * Math.cos(angle));
                    float z = (float) (layerRadius * Math.sin(angle));

                    int color = ColorUtil.multAlpha(getHolyColor(0.5f), 0.3f / (layer + 1));

                    builder.pos(matrix, x, 0, z).color(color).endVertex();
                    builder.pos(matrix, 0, beamHeight * 0.8f, 0).color(ColorUtil.multAlpha(color, 0)).endVertex();
                }

                tessellator.draw();
                matrices.pop();
            }
        }

        private void renderMagicRune(MatrixStack matrices) {
            float time = timer.elapsedTime() / 1000.0f;
            matrices.rotate(Vector3f.YP.rotationDegrees(time * 45));

            renderRuneCircle(matrices, size.getValue() * animation.get(), 0);
            renderRuneCircle(matrices, size.getValue() * 0.7f * animation.get(), 45);
            renderRuneSymbols(matrices);
        }

        private void renderRuneCircle(MatrixStack matrices, float radius, float rotation) {
            matrices.push();
            matrices.rotate(Vector3f.YP.rotationDegrees(rotation));

            int segments = 64;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            GL11.glLineWidth(2.0f);
            builder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

            for (int i = 0; i <= segments; i++) {
                float angle = (float) (2 * Math.PI * i / segments);
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));

                int color = getRuneColor(i);
                builder.pos(matrix, x, 0, z).color(color).endVertex();
            }

            tessellator.draw();
            matrices.pop();
        }

        private void renderRuneSymbols(MatrixStack matrices) {
            int symbolCount = 6;
            float symbolRadius = size.getValue() * 0.5f;

            for (int i = 0; i < symbolCount; i++) {
                matrices.push();

                float angle = (float) (2 * Math.PI * i / symbolCount);
                float x = (float) (symbolRadius * Math.cos(angle));
                float z = (float) (symbolRadius * Math.sin(angle));

                matrices.translate(x, 0, z);
                matrices.rotate(Vector3f.YP.rotationDegrees(angle * 180 / (float) Math.PI + 90));

                renderSymbol(matrices, i);
                matrices.pop();
            }
        }

        private void renderSymbol(MatrixStack matrices, int symbolType) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            GL11.glLineWidth(3.0f);
            builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

            int color = getSymbolColor(symbolType);
            float symbolSize = 0.2f * size.getValue();

            switch (symbolType % 6) {
                case 0:
                    builder.pos(matrix, -symbolSize, 0, 0).color(color).endVertex();
                    builder.pos(matrix, symbolSize, 0, 0).color(color).endVertex();
                    builder.pos(matrix, 0, 0, -symbolSize).color(color).endVertex();
                    builder.pos(matrix, 0, 0, symbolSize).color(color).endVertex();
                    break;
                case 1:
                    for (int i = 0; i < 3; i++) {
                        float angle = (float) (i * 2 * Math.PI / 3);
                        float nextAngle = (float) ((i + 1) * 2 * Math.PI / 3);
                        float x1 = (float) (symbolSize * Math.cos(angle));
                        float z1 = (float) (symbolSize * Math.sin(angle));
                        float x2 = (float) (symbolSize * Math.cos(nextAngle));
                        float z2 = (float) (symbolSize * Math.sin(nextAngle));
                        builder.pos(matrix, x1, 0, z1).color(color).endVertex();
                        builder.pos(matrix, x2, 0, z2).color(color).endVertex();
                    }
                    break;
                case 2:
                    builder.pos(matrix, -symbolSize, 0, -symbolSize).color(color).endVertex();
                    builder.pos(matrix, symbolSize, 0, symbolSize).color(color).endVertex();
                    builder.pos(matrix, -symbolSize, 0, symbolSize).color(color).endVertex();
                    builder.pos(matrix, symbolSize, 0, -symbolSize).color(color).endVertex();
                    break;
                case 3:
                    int points = 5;
                    for (int i = 0; i < points; i++) {
                        float angle = (float) (i * 2 * Math.PI / points);
                        float nextAngle = (float) ((i + 2) % points * 2 * Math.PI / points);
                        float x1 = (float) (symbolSize * Math.cos(angle));
                        float z1 = (float) (symbolSize * Math.sin(angle));
                        float x2 = (float) (symbolSize * Math.cos(nextAngle));
                        float z2 = (float) (symbolSize * Math.sin(nextAngle));
                        builder.pos(matrix, x1, 0, z1).color(color).endVertex();
                        builder.pos(matrix, x2, 0, z2).color(color).endVertex();
                    }
                    break;
                case 4:
                    for (int i = 0; i < 8; i++) {
                        float angle = (float) (i * Math.PI / 4);
                        float x = (float) (symbolSize * Math.cos(angle) * 0.5f);
                        float z = (float) (symbolSize * Math.sin(angle) * 0.5f);
                        builder.pos(matrix, 0, 0, 0).color(color).endVertex();
                        builder.pos(matrix, x, 0, z).color(color).endVertex();
                    }
                    break;
                case 5:
                    for (int i = 0; i < 6; i++) {
                        float angle = (float) (i * Math.PI / 3);
                        float nextAngle = (float) ((i + 1) * Math.PI / 3);
                        float x1 = (float) (symbolSize * Math.cos(angle));
                        float z1 = (float) (symbolSize * Math.sin(angle));
                        float x2 = (float) (symbolSize * Math.cos(nextAngle));
                        float z2 = (float) (symbolSize * Math.sin(nextAngle));
                        builder.pos(matrix, x1, 0, z1).color(color).endVertex();
                        builder.pos(matrix, x2, 0, z2).color(color).endVertex();
                    }
                    break;
            }

            tessellator.draw();
        }

        private void renderPlasmaBall(MatrixStack matrices) {
            float time = timer.elapsedTime() / 1000.0f;
            int layers = 5;

            for (int layer = 0; layer < layers; layer++) {
                matrices.push();
                matrices.rotate(Vector3f.XP.rotationDegrees(time * 30 * (layer + 1)));
                matrices.rotate(Vector3f.YP.rotationDegrees(time * 45 * (layer + 1)));

                float layerRadius = size.getValue() * (1.0f - layer * 0.15f) * animation.get();
                int segments = 16 - layer;

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder builder = tessellator.getBuffer();
                Matrix4f matrix = matrices.getLast().getMatrix();

                builder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

                for (int i = 0; i <= segments; i++) {
                    for (int j = 0; j <= segments / 2; j++) {
                        float theta = (float) (2 * Math.PI * i / segments);
                        float phi = (float) (Math.PI * j / (segments / 2));
                        float nextPhi = (float) (Math.PI * (j + 1) / (segments / 2));

                        float x1 = (float) (layerRadius * Math.sin(phi) * Math.cos(theta));
                        float y1 = (float) (layerRadius * Math.cos(phi));
                        float z1 = (float) (layerRadius * Math.sin(phi) * Math.sin(theta));

                        float x2 = (float) (layerRadius * Math.sin(nextPhi) * Math.cos(theta));
                        float y2 = (float) (layerRadius * Math.cos(nextPhi));
                        float z2 = (float) (layerRadius * Math.sin(nextPhi) * Math.sin(theta));

                        int color = getPlasmaColor(layer, time);

                        builder.pos(matrix, x1, y1, z1).color(color).endVertex();
                        builder.pos(matrix, x2, y2, z2).color(color).endVertex();
                    }
                }

                tessellator.draw();
                matrices.pop();
            }
        }

        private void renderGravityCollapse(MatrixStack matrices) {
            float collapseProgress = animation.get();
            int particleRings = 10;

            for (int ring = 0; ring < particleRings; ring++) {
                float ringRadius = size.getValue() * (1.0f - collapseProgress) * (1.0f + ring * 0.2f);
                float ringHeight = (ring - particleRings / 2f) * 0.3f * size.getValue();
                int particlesPerRing = 20 - ring;

                for (int i = 0; i < particlesPerRing; i++) {
                    matrices.push();

                    float angle = (float) (2 * Math.PI * i / particlesPerRing + timer.elapsedTime() * 0.001f * (ring + 1));
                    float x = (float) (ringRadius * Math.cos(angle));
                    float z = (float) (ringRadius * Math.sin(angle));

                    matrices.translate(x, ringHeight, z);

                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder builder = tessellator.getBuffer();
                    Matrix4f matrix = matrices.getLast().getMatrix();

                    builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

                    float particleSize = 0.05f * size.getValue() * (1.0f - collapseProgress);
                    int color = getGravityColor(ring, collapseProgress);

                    builder.pos(matrix, -particleSize, -particleSize, 0).color(color).endVertex();
                    builder.pos(matrix, particleSize, -particleSize, 0).color(color).endVertex();
                    builder.pos(matrix, particleSize, particleSize, 0).color(color).endVertex();
                    builder.pos(matrix, -particleSize, particleSize, 0).color(color).endVertex();

                    tessellator.draw();
                    matrices.pop();
                }
            }

            if (collapseProgress > 0.7f) {
                renderBlackHole(matrices, (collapseProgress - 0.7f) / 0.3f);
            }
        }

        private void renderBlackHole(MatrixStack matrices, float intensity) {
            int rings = 8;

            for (int ring = 0; ring < rings; ring++) {
                matrices.push();
                matrices.rotate(Vector3f.YP.rotationDegrees(timer.elapsedTime() * 0.1f * (ring + 1) * intensity));

                float ringRadius = size.getValue() * 0.2f * (ring + 1) * intensity;
                int segments = 32;

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder builder = tessellator.getBuffer();
                Matrix4f matrix = matrices.getLast().getMatrix();

                builder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

                for (int i = 0; i <= segments; i++) {
                    float angle = (float) (2 * Math.PI * i / segments);
                    float x1 = (float) (ringRadius * Math.cos(angle));
                    float z1 = (float) (ringRadius * Math.sin(angle));
                    float x2 = (float) ((ringRadius * 0.8f) * Math.cos(angle));
                    float z2 = (float) ((ringRadius * 0.8f) * Math.sin(angle));

                    int outerColor = ColorUtil.multAlpha(0xFF000000, intensity * 0.8f);
                    int innerColor = ColorUtil.multAlpha(0xFF440044, intensity);

                    builder.pos(matrix, x1, 0, z1).color(outerColor).endVertex();
                    builder.pos(matrix, x2, 0, z2).color(innerColor).endVertex();
                }

                tessellator.draw();
                matrices.pop();
            }
        }

        private void renderLaserGrid(MatrixStack matrices) {
            float gridSize = size.getValue() * 2.0f;
            int gridLines = 10;
            float time = timer.elapsedTime() / 1000.0f;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            GL11.glLineWidth(2.0f * intensity.getValue());
            builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

            for (int i = 0; i <= gridLines; i++) {
                float pos = -gridSize / 2 + (gridSize * i / gridLines);
                float pulse = (float) (0.5f + 0.5f * Math.sin(time * 5 + i * 0.5f));
                int color = ColorUtil.multAlpha(getLaserColor(), pulse * alpha.getValue());

                builder.pos(matrix, pos, 0, -gridSize / 2).color(color).endVertex();
                builder.pos(matrix, pos, 0, gridSize / 2).color(color).endVertex();

                builder.pos(matrix, -gridSize / 2, 0, pos).color(color).endVertex();
                builder.pos(matrix, gridSize / 2, 0, pos).color(color).endVertex();
            }

            for (int i = 0; i < 5; i++) {
                float angle = (float) (2 * Math.PI * i / 5);
                float radius = gridSize * 0.3f * animation.get();
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));

                int centerColor = getLaserColor();

                builder.pos(matrix, 0, 0, 0).color(centerColor).endVertex();
                builder.pos(matrix, x, 0, z).color(ColorUtil.multAlpha(centerColor, 0)).endVertex();
            }

            tessellator.draw();
        }

        private void renderChaoticRift(MatrixStack matrices) {
            float time = timer.elapsedTime() / 1000.0f;
            int riftSegments = 20;

            for (int layer = 0; layer < 3; layer++) {
                matrices.push();
                matrices.rotate(Vector3f.YP.rotationDegrees(time * 20 * (layer + 1)));
                matrices.rotate(Vector3f.ZP.rotationDegrees((float) Math.sin(time * 3) * 10));

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder builder = tessellator.getBuffer();
                Matrix4f matrix = matrices.getLast().getMatrix();

                builder.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

                for (int i = 0; i < riftSegments; i++) {
                    float progress = (float) i / riftSegments;
                    float chaosOffset = (float) (Math.sin(time * 8 + i * 0.5f) * 0.3f * size.getValue());
                    float width = size.getValue() * (0.1f + layer * 0.05f) * (1.0f - progress);

                    float x1 = chaosOffset + width;
                    float x2 = chaosOffset - width;
                    float y = (progress - 0.5f) * size.getValue() * 3.0f;
                    float z = (float) (Math.cos(time * 5 + i * 0.3f) * 0.2f * size.getValue());

                    int color = getChaoticColor(i, time, layer);

                    builder.pos(matrix, x1, y, z).color(color).endVertex();
                    builder.pos(matrix, x2, y, z).color(color).endVertex();
                }

                tessellator.draw();
                matrices.pop();
            }

            renderChaoticSparks(matrices, time);
        }

        private void renderChaoticSparks(MatrixStack matrices, float time) {
            int sparkCount = 15;

            for (int i = 0; i < sparkCount; i++) {
                if ((time * 10 + i) % 3 < 1) {
                    matrices.push();

                    float angle = (float) (Math.random() * 2 * Math.PI);
                    float distance = (float) (Math.random() * size.getValue() * 2);
                    float x = (float) (distance * Math.cos(angle));
                    float z = (float) (distance * Math.sin(angle));
                    float y = (float) ((Math.random() - 0.5f) * size.getValue());

                    matrices.translate(x, y, z);

                    renderSpark(matrices, i);
                    matrices.pop();
                }
            }
        }

        private void renderSpark(MatrixStack matrices, int sparkIndex) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            Matrix4f matrix = matrices.getLast().getMatrix();

            GL11.glLineWidth(1.5f);
            builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

            float sparkSize = 0.1f * size.getValue();
            int color = getSparkColor(sparkIndex);

            builder.pos(matrix, -sparkSize, 0, 0).color(color).endVertex();
            builder.pos(matrix, sparkSize, 0, 0).color(color).endVertex();
            builder.pos(matrix, 0, -sparkSize, 0).color(color).endVertex();
            builder.pos(matrix, 0, sparkSize, 0).color(color).endVertex();
            builder.pos(matrix, 0, 0, -sparkSize).color(color).endVertex();
            builder.pos(matrix, 0, 0, sparkSize).color(color).endVertex();

            tessellator.draw();
        }

        private void setupRenderState() {
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        }

        private void restoreRenderState() {
            RenderSystem.enableDepthTest();
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        private org.privates.client.utils.animation.util.Easing getEasing() {
            switch (animationType.getValue()) {
                case "Linear":
                    return Easings.LINEAR;
                case "Quad Out":
                    return Easings.QUAD_OUT;
                case "Cubic Out":
                    return Easings.CUBIC_OUT;
                case "Quart Out":
                    return Easings.QUART_OUT;
                case "Sine Out":
                    return Easings.SINE_OUT;
                case "Expo Out":
                    return Easings.EXPO_OUT;
                case "Circ Out":
                    return Easings.CIRC_OUT;
                case "Back Out":
                    return Easings.BACK_OUT;
                case "Elastic Out":
                    return Easings.ELASTIC_OUT;
                case "Bounce Out":
                    return Easings.BOUNCE_OUT;
                default:
                    return Easings.BOUNCE_OUT;
            }
        }

        private int getParticleColor(EffectParticle particle) {
            if (rainbowMode.getValue()) {
                return ColorUtil.rainbow(8, particle.index * 15, 0.8f, 1.0f, alpha.getValue());
            }

            float lifeProgress = 1.0f - (timer.elapsedTime() / duration.getValue());
            float currentAlpha = alpha.getValue();
            if (fadeOut.getValue()) {
                currentAlpha *= lifeProgress;
            }

            return ColorUtil.replAlpha(primaryColor.getValue(), currentAlpha);
        }

        private int getRingColor(int layer, int segment, int totalSegments) {
            if (rainbowMode.getValue()) {
                return ColorUtil.rainbow(8, segment * 5 + layer * 20, 0.8f, 1.0f, alpha.getValue());
            }

            float progress = (float) segment / totalSegments;
            return ColorUtil.overCol(primaryColor.getValue(), secondaryColor.getValue(), progress);
        }

        private int getLightningColor() {
            if (rainbowMode.getValue()) {
                return ColorUtil.rainbow(2, (int) (timer.elapsedTime() / 50), 1.0f, 1.0f, alpha.getValue());
            }
            return ColorUtil.replAlpha(accentColor.getValue(), alpha.getValue());
        }

        private int getFireColor(int layer, float intensity) {
            int baseColor = layer % 2 == 0 ? 0xFFFF4400 : 0xFFFFAA00;
            return ColorUtil.replAlpha(baseColor, intensity * alpha.getValue());
        }

        private int getIceColor(int index) {
            if (rainbowMode.getValue()) {
                return ColorUtil.rainbow(6, index * 30, 0.6f, 1.0f, alpha.getValue());
            }
            return ColorUtil.replAlpha(0xFF00AAFF, alpha.getValue());
        }

        private int getBloodColor() {
            return ColorUtil.replAlpha(0xFF8B0000, alpha.getValue());
        }

        private int getStarColor(int ray, float time) {
            if (rainbowMode.getValue()) {
                return ColorUtil.rainbow(4, ray * 20 + (int) (time * 100), 0.9f, 1.0f, alpha.getValue());
            }
            return ColorUtil.replAlpha(primaryColor.getValue(), alpha.getValue());
        }

        private int getDarkMatterColor(int index, float time) {
            float pulse = (float) (0.3f + 0.7f * Math.sin(time * 4 + index * 0.2f));
            return ColorUtil.replAlpha(0xFF220022, pulse * alpha.getValue());
        }

        private int getElectronColor(int orbit) {
            int[] colors = {0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00};
            return ColorUtil.replAlpha(colors[orbit % colors.length], alpha.getValue());
        }

        private int getVoidColor(int ring, boolean inner) {
            float intensity = inner ? 0.8f : 0.2f;
            return ColorUtil.replAlpha(0xFF440044, intensity * alpha.getValue());
        }

        private int getHolyColor(float intensity) {
            return ColorUtil.replAlpha(0xFFFFFFAA, intensity * alpha.getValue());
        }

        private int getRuneColor(int segment) {
            if (rainbowMode.getValue()) {
                return ColorUtil.rainbow(10, segment * 3, 0.7f, 1.0f, alpha.getValue());
            }
            return ColorUtil.replAlpha(primaryColor.getValue(), alpha.getValue());
        }

        private int getSymbolColor(int symbolType) {
            int[] colors = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00, 0xFFFF00FF, 0xFF00FFFF};
            return ColorUtil.replAlpha(colors[symbolType % colors.length], alpha.getValue());
        }

        private int getPlasmaColor(int layer, float time) {
            float hue = (time + layer * 0.2f) % 1.0f;
            int rgb = Color.HSBtoRGB(hue, 0.8f, 1.0f);
            return ColorUtil.replAlpha(rgb, alpha.getValue());
        }

        private int getGravityColor(int ring, float progress) {
            float intensity = 1.0f - progress;
            return ColorUtil.replAlpha(0xFF660066, intensity * alpha.getValue());
        }

        private int getLaserColor() {
            if (rainbowMode.getValue()) {
                return ColorUtil.rainbow(3, (int) (timer.elapsedTime() / 30), 1.0f, 1.0f, alpha.getValue());
            }
            return ColorUtil.replAlpha(accentColor.getValue(), alpha.getValue());
        }

        private int getChaoticColor(int segment, float time, int layer) {
            float chaos = (float) Math.sin(time * 7 + segment * 0.8f + layer * 1.5f);
            if (chaos > 0.5f) {
                return ColorUtil.replAlpha(primaryColor.getValue(), alpha.getValue());
            } else if (chaos > 0) {
                return ColorUtil.replAlpha(secondaryColor.getValue(), alpha.getValue());
            } else {
                return ColorUtil.replAlpha(accentColor.getValue(), alpha.getValue());
            }
        }

        private int getSparkColor(int index) {
            int[] colors = {0xFFFFFFFF, 0xFFFFFF00, 0xFFFF8800, 0xFFFF0000};
            return ColorUtil.replAlpha(colors[index % colors.length], alpha.getValue());
        }

        public boolean shouldRemove() {
            return timer.finished(duration.getValue().longValue());
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    class EffectParticle {
        Vector3d position;
        Vector3d velocity;
        final int index;
        final StopWatch lifeTimer = new StopWatch();

        public EffectParticle(Vector3d startPos, String effectType, int index) {
            this.index = index;
            this.position = startPos;

            switch (effectType) {
                case "Взрыв частиц":
                    float angle = (float) (Math.random() * 2 * Math.PI);
                    float speed = 0.1f + (float) Math.random() * 0.2f;
                    velocity = new Vector3d(
                            Math.cos(angle) * speed,
                            Math.random() * 0.3f,
                            Math.sin(angle) * speed
                    );
                    break;

                case "Кровавый фонтан":
                    velocity = new Vector3d(
                            (Math.random() - 0.5) * 0.1,
                            0.2 + Math.random() * 0.3,
                            (Math.random() - 0.5) * 0.1
                    );
                    break;

                case "Ледяные кристаллы":
                    velocity = new Vector3d(
                            (Math.random() - 0.5) * 0.05,
                            -0.05 - Math.random() * 0.1,
                            (Math.random() - 0.5) * 0.05
                    );
                    break;

                case "Звездный взрыв":
                    float starAngle = (float) (index * 2 * Math.PI / particleCount.getValue());
                    float starSpeed = 0.05f + (float) Math.random() * 0.15f;
                    velocity = new Vector3d(
                            Math.cos(starAngle) * starSpeed,
                            (Math.random() - 0.5) * 0.1,
                            Math.sin(starAngle) * starSpeed
                    );
                    break;

                default:
                    velocity = new Vector3d(
                            (Math.random() - 0.5) * 0.2,
                            (Math.random() - 0.5) * 0.2,
                            (Math.random() - 0.5) * 0.2
                    );
                    break;
            }
        }

        public void update() {
            if (physics.getValue()) {
                velocity = velocity.mul(airResistance.getValue());
                velocity = velocity.add(0, -gravity.getValue(), 0);
            }

            position = position.add(velocity);
        }
    }
}