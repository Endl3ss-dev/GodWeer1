package org.privates.client.managers.module.impl.render;

import net.minecraft.util.Namespaced;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Category2
@ModuleInfo(name = "Kagune", category = Category.RENDER, description = "Оставляет след после ходьбы из частиц glow")
public class Kagune extends Module {

    private final SliderSetting trailLength = new SliderSetting(this, "Длина шлейфа", 25, 10, 50, 1);
    private final SliderSetting particleSize = new SliderSetting(this, "Размер частиц", 0.6f, 0.3f, 1.2f, 0.05f);
    private final SliderSetting speed = new SliderSetting(this, "Скорость", 3.0f, 1.0f, 10.0f, 0.5f);

    private final Namespaced bloom = new Namespaced("texture/glow.png");
    private final List<TrailPoint> trail = new ArrayList<>();
    private Vector3d lastPos = null;

    private static class TrailPoint {
        Vector3d position;
        long createTime;

        TrailPoint(Vector3d position) {
            this.position = position;
            this.createTime = System.currentTimeMillis();
        }

        float getLifeProgress(float maxLifeTime) {
            long age = System.currentTimeMillis() - createTime;
            return Math.min(1.0f, age / maxLifeTime);
        }
    }

    @Override
    public void toggle() {
        super.toggle();
        trail.clear();
        lastPos = null;
    }

    @EventHandler
    public void onEvent(WorldChangeEvent event) {
        trail.clear();
        lastPos = null;
    }

    @EventHandler
    public void onEvent(MotionEvent event) {
        if (mc.player == null) {
            trail.clear();
            lastPos = null;
            return;
        }

        Vector3d currentPos = new Vector3d(
                mc.player.getPosX(),
                mc.player.getPosY(),
                mc.player.getPosZ()
        );

        if (lastPos == null) {
            lastPos = currentPos;
            trail.add(new TrailPoint(currentPos));
            return;
        }

        double distance = currentPos.distanceTo(lastPos);
        double stepSize = 0.03;

        if (distance > stepSize) {
            int steps = (int) Math.ceil(distance / stepSize);
            for (int i = 1; i <= steps; i++) {
                double progress = i / (double) steps;
                Vector3d interpolated = new Vector3d(
                        MathHelper.lerp(progress, lastPos.x, currentPos.x),
                        MathHelper.lerp(progress, lastPos.y, currentPos.y),
                        MathHelper.lerp(progress, lastPos.z, currentPos.z)
                );
                trail.add(0, new TrailPoint(interpolated));
            }
            lastPos = currentPos;
        } else {
            trail.add(0, new TrailPoint(currentPos));
            lastPos = currentPos;
        }

        float maxLifeTime = 800f;
        Iterator<TrailPoint> iterator = trail.iterator();
        while (iterator.hasNext()) {
            TrailPoint point = iterator.next();
            if (point.getLifeProgress(maxLifeTime) >= 1.0f) {
                iterator.remove();
            }
        }

        int maxTrailSize = trailLength.getValue().intValue() * 30;
        while (trail.size() > maxTrailSize) {
            trail.remove(trail.size() - 1);
        }
    }

    @EventHandler
    public void onEvent(Render3DPosedEvent event) {
        if (mc.player == null || trail.isEmpty()) return;

        MatrixStack ms = event.getMatrix();
        float partialTicks = event.getPartialTicks();

        final float speedValue = speed.getValue();
        final float verticalSpeed = speedValue * 2;
        final float baseSizePx = particleSize.getValue();
        final int trailSize = trail.size();
        final float radiusConst = 0.65F;
        final float upperPosition = 1.8F;
        final float lowerPosition = 0.1F;
        final float maxLifeTime = 800f;

        final Quaternion rotation = mc.getRenderManager().info.getRotation().copy();
        final Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();

        final double t = System.currentTimeMillis() / (500.0 / speedValue);
        final double tv = System.currentTimeMillis() / (1000.0 / verticalSpeed);

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        float radius = radiusConst;
        for (int k = 0; k < 3; k++) {
            for (int j = 0; j < trailSize; j++) {
                TrailPoint point = trail.get(j);
                Vector3d trailPos = point.position;

                float lifeProgress = point.getLifeProgress(maxLifeTime);
                float kf = j / (float) trailSize;

                double tj = t - j * 0.015;
                double tvj = tv - j * 0.015;
                double cyc = (Math.sin(tvj) + 1.0) * 0.5;

                double baseAngle = Math.toRadians(k * 90.0 + (tj * 50.0 % 360.0));

                double offX = Math.cos(baseAngle) * radius;
                double offZ = Math.sin(baseAngle) * radius;
                double offY = (k % 2 == 0)
                        ? lowerPosition + (upperPosition - lowerPosition) * cyc
                        : upperPosition - (upperPosition - lowerPosition) * cyc;

                double finalX = trailPos.x - cameraPos.x + offX;
                double finalY = trailPos.y - cameraPos.y + offY;
                double finalZ = trailPos.z - cameraPos.z + offZ;

                float ageFade = 1.0f - (lifeProgress * lifeProgress);
                float sizeFactor = (1.0f - (kf * 0.5f)) * ageFade;
                float dynSize = baseSizePx * sizeFactor;
                int dynAlpha = (int) (220 * sizeFactor);

                if (dynAlpha <= 5) continue;

                int color = ColorUtil.replAlpha(ColorUtil.multAlpha(ColorUtil.fade(j * 2), 1.0f), MathHelper.clamp(dynAlpha, 0, 255));
                int color2 = ColorUtil.replAlpha(ColorUtil.multAlpha(ColorUtil.fade(j * 4), 1.0f), MathHelper.clamp(dynAlpha, 0, 255));
                int color3 = ColorUtil.replAlpha(ColorUtil.multAlpha(ColorUtil.fade(j * 8), 1.0f), MathHelper.clamp(dynAlpha, 0, 255));
                int color4 = ColorUtil.replAlpha(ColorUtil.multAlpha(ColorUtil.fade(j * 16), 1.0f), MathHelper.clamp(dynAlpha, 0, 255));

                ms.push();
                ms.translate(finalX, finalY, finalZ);
                ms.translate(-dynSize / 12f, -dynSize / 12f, 0f);
                ms.rotate(rotation);
                ms.translate(dynSize / 12f, dynSize / 12f, 0f);

                RenderUtil.bindTexture(bloom);
                RectUtil.drawRect(ms, -dynSize / 2f, -dynSize / 12f,
                        dynSize, dynSize,
                        color, color2, color3, color4, true, true);

                ms.pop();
            }
            radius *= -1.0f;
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }
}