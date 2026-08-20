package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category2;
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

import java.util.ArrayList;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Torus", category = Category.RENDER, description = "Оставляет круг при ударе на противнике")
public class Torus extends Module {
    public static Torus getInstance() {
        return Instance.get(Torus.class);
    }

    private final SliderSetting sizeSetting = new SliderSetting(this, "Размер", 0.8f, 0.2f, 3.0f, 0.1f);
    private final SliderSetting thicknessSetting = new SliderSetting(this, "Толщина", 0.3f, 0.1f, 1.0f, 0.05f);

    private final ColorSetting colorSetting = new ColorSetting(this, "Цвет", 0xFF00AAFF);
    private final BooleanSetting gradientEnabled = new BooleanSetting(this, "Градиент", true);
    private final ColorSetting gradientColor = new ColorSetting(this, "Цвет градиента", 0xFFFF4400)
            .setVisible(gradientEnabled::getValue);
    private final SliderSetting alpha = new SliderSetting(this, "Прозрачность", 0.8f, 0.1f, 1.0f, 0.05f);

    private final SliderSetting lifeTimeSetting = new SliderSetting(this, "Время жизни", 1500, 500, 5000, 100);
    private final BooleanSetting fadeOut = new BooleanSetting(this, "Затухание", true);

    private final ModeSetting animationType = new ModeSetting(this, "Анимация", "Circ Out", "Linear", "Quad Out", "Cubic Out", "Sine Out", "Expo Out", "Back Out", "Elastic Out", "Bounce Out");

    private final SliderSetting segments = new SliderSetting(this, "Качество", 32, 8, 64, 4);
    private final ModeSetting renderMode = new ModeSetting(this, "Режим", "Fill", "Wireframe", "Fill + Wireframe");

    private final BooleanSetting outlineSetting = new BooleanSetting(this, "Обводка", true);
    private final SliderSetting outlineWidth = new SliderSetting(this, "Толщина обводки", 1.5f, 0.5f, 5.0f, 0.5f)
            .setVisible(outlineSetting::getValue);
    private final ColorSetting outlineColor = new ColorSetting(this, "Цвет обводки", 0xFFFFFFFF)
            .setVisible(outlineSetting::getValue);

    private final BooleanSetting enableRotation = new BooleanSetting(this, "Вращение", true);
    private final SliderSetting rotationSpeed = new SliderSetting(this, "Скорость вращения", 2.0f, 0.5f, 10.0f, 0.5f)
            .setVisible(enableRotation::getValue);

    private final ModeSetting positionMode = new ModeSetting(this, "Позиция", "Target Center", "Target Head", "Target Feet");
    private final SliderSetting offsetY = new SliderSetting(this, "Смещение Y", 0.0f, -2.0f, 2.0f, 0.1f);

    private final BooleanSetting multipleRings = new BooleanSetting(this, "Несколько колец", false);
    private final SliderSetting ringCount = new SliderSetting(this, "Количество", 3, 2, 5, 1)
            .setVisible(multipleRings::getValue);
    private final SliderSetting ringSpacing = new SliderSetting(this, "Расстояние", 0.3f, 0.1f, 0.8f, 0.1f)
            .setVisible(multipleRings::getValue);

    private final SliderSetting maxToruses = new SliderSetting(this, "Лимит", 20, 5, 50, 5);

    private final ArrayList<TorusObj> toruses = new ArrayList<>();
    private final StopWatch spawnTimer = new StopWatch();

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (event.getTarget() instanceof LivingEntity target) {
            Vector3d particlePos = getPositionForTarget(target);

            if (multipleRings.getValue()) {
                for (int i = 0; i < ringCount.getValue(); i++) {
                    Vector3d ringPos = particlePos.add(0, i * ringSpacing.getValue(), 0);
                    toruses.add(new TorusObj(ringPos));
                }
            } else {
                toruses.add(new TorusObj(particlePos));
            }

            while (toruses.size() > maxToruses.getValue()) {
                toruses.remove(0);
            }

            spawnTimer.reset();
        }
    }

    private Vector3d getPositionForTarget(LivingEntity target) {
        Vector3d basePos = target.getPositionVec();

        switch (positionMode.getValue()) {
            case "Target Head":
                basePos = basePos.add(0, target.getHeight(), 0);
                break;
            case "Target Feet":
                break;
            case "Target Center":
                basePos = basePos.add(0, target.getHeight() / 2F, 0);
                break;
        }

        return basePos.add(0, offsetY.getValue(), 0);
    }

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        toruses.removeIf(TorusObj::shouldRemove);

        if (toruses.isEmpty()) return;

        setupRenderState();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        MatrixStack ms = event.getMatrix();

        if (renderMode.getValue().equals("Fill") || renderMode.getValue().equals("Fill + Wireframe")) {
            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            for (TorusObj torus : toruses) {
                torus.render(ms, builder, false);
            }
            tessellator.draw();
        }

        if (renderMode.getValue().equals("Wireframe") || renderMode.getValue().equals("Fill + Wireframe")) {
            if (outlineSetting.getValue()) {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glLineWidth(outlineWidth.getValue());

                builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                for (TorusObj torus : toruses) {
                    torus.render(ms, builder, true);
                }
                tessellator.draw();

                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }
        }

        restoreRenderState();
    }

    private void setupRenderState() {
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
    }

    private void restoreRenderState() {
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.clearCurrentColor();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        toruses.clear();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        toruses.clear();
    }

    private Vector2f getRotation(Vector3d pos) {
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d diff = pos.subtract(playerPos);

        double distance = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        float yaw = (float) (Math.atan2(diff.z, diff.x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(diff.y, distance) * 180.0 / Math.PI));

        return new Vector2f(yaw, pitch);
    }

    private org.privates.client.utils.animation.util.Easing getEasingFromString(String easingName) {
        switch (easingName) {
            case "Linear":
                return Easings.LINEAR;
            case "Quad Out":
                return Easings.QUAD_OUT;
            case "Cubic Out":
                return Easings.CUBIC_OUT;
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
                return Easings.CIRC_OUT;
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    class TorusObj {
        final Vector2f rotation;
        final Animation sizing = new Animation();
        final StopWatch timer = new StopWatch();
        final Vector3d pos;
        final float size;
        final long lifeTime;
        float currentRotation = 0;

        public TorusObj(Vector3d pos) {
            this.pos = pos;
            this.size = sizeSetting.getValue();
            this.lifeTime = lifeTimeSetting.getValue().longValue();

            rotation = getRotation(pos);
            timer.reset();

            org.privates.client.utils.animation.util.Easing easing = getEasingFromString(animationType.getValue());
            sizing.run(1.0, lifeTime / 1000.0, easing);
        }

        public void render(MatrixStack matrices, BufferBuilder builder, boolean isWireframe) {
            sizing.update();

            if (enableRotation.getValue()) {
                currentRotation += rotationSpeed.getValue();
            }

            matrices.push();

            float animProgress = sizing.get();
            float currentSize = size * animProgress;
            float thickness = thicknessSetting.getValue() * animProgress;
            float innerRadius = 0.4f * currentSize;
            float outerRadius = currentSize;

            Matrix4f matrix = matrices.getLast().getMatrix();

            Vector3d camera = RenderUtil3D.cameraPos();
            double x = pos.getX() - camera.x;
            double y = pos.getY() - camera.y;
            double z = pos.getZ() - camera.z;

            matrices.translate(x, y, z);

            if (enableRotation.getValue()) {
                matrices.rotate(Vector3f.YN.rotationDegrees(currentRotation));
                matrices.rotate(Vector3f.XN.rotationDegrees(currentRotation * 0.5f));
            } else {
                matrices.rotate(Vector3f.YN.rotationDegrees(rotation.x));
                matrices.rotate(Vector3f.XN.rotationDegrees(-rotation.y + 90));
            }

            int segmentCount = segments.getValue().intValue();
            int ringSegments = 16;

            for (int i = 0; i < segmentCount; i++) {
                double theta1 = 2 * Math.PI * i / segmentCount;
                double theta2 = 2 * Math.PI * (i + 1) / segmentCount;

                for (int j = 0; j < ringSegments; j++) {
                    double phi1 = 2 * Math.PI * j / ringSegments;
                    double phi2 = 2 * Math.PI * (j + 1) / ringSegments;

                    float x1 = (float) ((outerRadius + innerRadius * Math.cos(phi1)) * Math.cos(theta1));
                    float y1 = (float) (innerRadius * Math.sin(phi1));
                    float z1 = (float) ((outerRadius + innerRadius * Math.cos(phi1)) * Math.sin(theta1));

                    float x2 = (float) ((outerRadius + innerRadius * Math.cos(phi2)) * Math.cos(theta1));
                    float y2 = (float) (innerRadius * Math.sin(phi2));
                    float z2 = (float) ((outerRadius + innerRadius * Math.cos(phi2)) * Math.sin(theta1));

                    float x3 = (float) ((outerRadius + innerRadius * Math.cos(phi2)) * Math.cos(theta2));
                    float y3 = (float) (innerRadius * Math.sin(phi2));
                    float z3 = (float) ((outerRadius + innerRadius * Math.cos(phi2)) * Math.sin(theta2));

                    float x4 = (float) ((outerRadius + innerRadius * Math.cos(phi1)) * Math.cos(theta2));
                    float y4 = (float) (innerRadius * Math.sin(phi1));
                    float z4 = (float) ((outerRadius + innerRadius * Math.cos(phi1)) * Math.sin(theta2));

                    int color = getColorForVertex(i, segmentCount, isWireframe);

                    builder.pos(matrix, x1, y1, z1).color(color).endVertex();
                    builder.pos(matrix, x2, y2, z2).color(color).endVertex();
                    builder.pos(matrix, x3, y3, z3).color(color).endVertex();
                    builder.pos(matrix, x4, y4, z4).color(color).endVertex();
                }
            }

            matrices.pop();
        }

        private int getColorForVertex(int segment, int totalSegments, boolean isWireframe) {
            float lifeProgress = 1.0f - (timer.elapsedTime() / (float) lifeTime);
            float currentAlpha = alpha.getValue();

            if (fadeOut.getValue()) {
                currentAlpha *= lifeProgress;
            }

            if (isWireframe && outlineSetting.getValue()) {
                return ColorUtil.replAlpha(outlineColor.getValue(), currentAlpha);
            }

            if (gradientEnabled.getValue()) {
                float gradientFactor = (float) segment / totalSegments;
                int color1 = colorSetting.getValue();
                int color2 = gradientColor.getValue();

                int r1 = (color1 >> 16) & 0xFF;
                int g1 = (color1 >> 8) & 0xFF;
                int b1 = color1 & 0xFF;

                int r2 = (color2 >> 16) & 0xFF;
                int g2 = (color2 >> 8) & 0xFF;
                int b2 = color2 & 0xFF;

                int r = (int) (r1 + (r2 - r1) * gradientFactor);
                int g = (int) (g1 + (g2 - g1) * gradientFactor);
                int b = (int) (b1 + (b2 - b1) * gradientFactor);

                return ColorUtil.getColor(r, g, b, currentAlpha);
            } else {
                return ColorUtil.replAlpha(colorSetting.getValue(), currentAlpha);
            }
        }

        public boolean shouldRemove() {
            return timer.finished(lifeTime);
        }
    }
}