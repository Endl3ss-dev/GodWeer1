package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.Namespaced;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "LineGlyphs", category = Category.RENDER, description = "Падающие line-глифы в мире")
public class LineGlyphs extends Module {

    final BooleanSetting glowing = new BooleanSetting(this, "Свечение", true);
    final BooleanSetting dashed = new BooleanSetting(this, "Пунктир", false);
    final ModeSetting colorMode = new ModeSetting(this, "Режим цвета", "Client", "Picker", "DoublePicker");
    final ColorSetting pickColor1 = new ColorSetting(this, "Цвет 1", ColorUtil.getColor(100, 200, 255))
            .setVisible(() -> colorMode.is("Picker") || colorMode.is("DoublePicker"));
    final ColorSetting pickColor2 = new ColorSetting(this, "Цвет 2", ColorUtil.getColor(255, 100, 200))
            .setVisible(() -> colorMode.is("DoublePicker"));
    final SliderSetting lineCount = new SliderSetting(this, "Количество", 120f, 20f, 400f, 1f);

    final List<FallingLine> lines = new ArrayList<>();
    final Random random = new Random();
    final Namespaced bloom = new Namespaced("texture/glow.png");

    private static final float FALL_SPEED = 0.07f;
    private static final float LINE_LENGTH = 17f;
    private static final float MAX_RENDER_DISTANCE = 160f;

    @Override
    protected void onEnable() {
        super.onEnable();
        generateLines();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        lines.clear();
    }

    private void generateLines() {
        lines.clear();
        if (mc.player == null || mc.gameRenderer == null) return;
        Vector3d anchor = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
        int targetCount = lineCount.getValue().intValue();
        for (int i = 0; i < targetCount; i++) {
            lines.add(new FallingLine(anchor));
        }
    }

    @EventHandler
    public void onRender3D(Render3DPosedEvent e) {
        if (mc.player == null || mc.world == null || mc.gameRenderer == null) return;

        int targetCount = lineCount.getValue().intValue();
        int linesToSpawn = targetCount - lines.size();
        Vector3d anchor = mc.gameRenderer.getActiveRenderInfo().getProjectedView();

        Iterator<FallingLine> iterator = lines.iterator();
        while (iterator.hasNext()) {
            FallingLine line = iterator.next();
            line.update();
            if (line.shouldRespawn(anchor)) {
                iterator.remove();
                linesToSpawn++;
            }
        }

        for (int i = 0; i < linesToSpawn; i++) {
            lines.add(new FallingLine(anchor));
        }

        renderLines(e.getMatrix());
    }

    private void renderLines(MatrixStack stack) {
        if (lines.isEmpty() || mc.gameRenderer == null) return;

        ActiveRenderInfo camera = mc.gameRenderer.getActiveRenderInfo();
        Vector3d camPos = camera.getProjectedView();
        boolean isDashed = dashed.getValue();
        boolean isGlowing = glowing.getValue();

        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            FallingLine line = lines.get(lineIndex);
            if (line.points.size() < 2) continue;

            int color = getColor(lineIndex);
            for (int i = 0; i < line.points.size() - 1; i++) {
                Vector3d start = line.points.get(i);
                Vector3d end = line.points.get(i + 1);

                if (start.distanceTo(camPos) > MAX_RENDER_DISTANCE || end.distanceTo(camPos) > MAX_RENDER_DISTANCE) continue;

                float segmentAlpha = (float) (i + 1) / line.points.size();
                int segmentColor = ColorUtil.replAlpha(color, (int) (segmentAlpha * 255));
                float width = isGlowing ? 2.5f : 1.5f;

                if (isDashed) {
                    drawDashedLine(stack, start, end, segmentColor, width);
                    if (isGlowing) {
                        int glowColor = ColorUtil.replAlpha(color, (int) (segmentAlpha * 100));
                        drawDashedLine(stack, start, end, glowColor, 4.0f);
                    }
                } else {
                    RenderUtil3D.drawLine(stack,
                            (float) start.x, (float) start.y, (float) start.z,
                            (float) end.x, (float) end.y, (float) end.z,
                            segmentColor, width, true, true);
                    if (isGlowing) {
                        int glowColor = ColorUtil.replAlpha(color, (int) (segmentAlpha * 100));
                        RenderUtil3D.drawLine(stack,
                                (float) start.x, (float) start.y, (float) start.z,
                                (float) end.x, (float) end.y, (float) end.z,
                                glowColor, 4.0f, true, true);
                    }
                }
            }
            renderTurnCircles(stack, line, color, camPos);
        }
    }

    private void drawDashedLine(MatrixStack stack, Vector3d start, Vector3d end, int color, float width) {
        double dashLength = 0.15;
        double gapLength = 0.1;
        double totalPattern = dashLength + gapLength;

        Vector3d direction = end.subtract(start);
        double lineLength = direction.length();
        if (lineLength < 1.0E-4) return;
        Vector3d normalizedDir = direction.normalize();

        double currentDistance = 0;
        while (currentDistance < lineLength) {
            double dashEnd = Math.min(currentDistance + dashLength, lineLength);
            Vector3d dashStart = start.add(normalizedDir.scale(currentDistance));
            Vector3d dashEndPos = start.add(normalizedDir.scale(dashEnd));
            RenderUtil3D.drawLine(stack,
                    (float) dashStart.x, (float) dashStart.y, (float) dashStart.z,
                    (float) dashEndPos.x, (float) dashEndPos.y, (float) dashEndPos.z,
                    color, width, true, true);
            currentDistance += totalPattern;
        }
    }

    private void renderTurnCircles(MatrixStack stack, FallingLine line, int color, Vector3d camPos) {
        if (line.turnPoints.isEmpty() || mc.gameRenderer == null) return;
        Quaternion rotation = mc.getRenderManager().info.getRotation().copy();

        for (Vector3d turnPoint : line.turnPoints) {
            if (turnPoint.distanceTo(camPos) > MAX_RENDER_DISTANCE) continue;

            float alpha = 1.0f;
            if (!line.points.isEmpty()) {
                Vector3d lastPoint = line.points.get(line.points.size() - 1);
                double distanceFromEnd = turnPoint.distanceTo(lastPoint);
                alpha = Math.max(0.3f, 1.0f - (float) (distanceFromEnd / LINE_LENGTH));
            }

            int particleColor = ColorUtil.replAlpha(color, (int) (alpha * 255));
            float size = 0.2f;
            float posX = (float) (turnPoint.x - camPos.x);
            float posY = (float) (turnPoint.y - camPos.y);
            float posZ = (float) (turnPoint.z - camPos.z);

            stack.push();
            stack.translate(posX, posY, posZ);
            stack.rotate(rotation);

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

            RenderUtil.bindTexture(bloom);
            RectUtil.drawRect(stack, -size / 2f, -size / 2f, size, size,
                    particleColor, particleColor, particleColor, particleColor, true, true);

            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableAlphaTest();
            RenderSystem.disableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.popMatrix();
            stack.pop();
        }
    }

    private int getColor(int index) {
        if (colorMode.is("Picker")) {
            return pickColor1.getValue();
        }
        if (colorMode.is("DoublePicker")) {
            float f = (float) ((Math.sin((System.currentTimeMillis() * 0.002) + index * 0.2) + 1.0) * 0.5);
            return ColorUtil.interpolateColor(pickColor1.getValue(), pickColor2.getValue(), f);
        }
        return Theme.getInstance().clientColor();
    }

    private final class FallingLine {
        final List<Vector3d> points = new ArrayList<>();
        final List<Vector3d> turnPoints = new ArrayList<>();
        Vector3d currentDirection;
        double distanceTraveled = 0;
        double currentSegmentLength;

        FallingLine(Vector3d anchor) {
            points.add(spawnInLoadedWorld(anchor));
            currentDirection = getRandomDirection();
            currentSegmentLength = 0.5 + random.nextDouble() * 1.5;
        }

        private Vector3d spawnInLoadedWorld(Vector3d anchor) {
            int renderChunks = mc.gameSettings.renderDistanceChunks;
            double worldRadius = Math.max(32.0, renderChunks * 16.0);
            double x = anchor.x + (random.nextDouble() - 0.5) * worldRadius * 2.0;
            double z = anchor.z + (random.nextDouble() - 0.5) * worldRadius * 2.0;
            double yBase = mc.player != null ? mc.player.getPosY() : anchor.y;
            double y = yBase + (random.nextDouble() - 0.5) * 28.0;
            return new Vector3d(x, y, z);
        }

        void update() {
            if (points.isEmpty()) return;

            Vector3d lastPoint = points.get(points.size() - 1);
            Vector3d movement = currentDirection.scale(FALL_SPEED);
            Vector3d newPoint = lastPoint.add(movement);
            points.add(newPoint);
            distanceTraveled += FALL_SPEED;

            if (distanceTraveled >= currentSegmentLength) {
                turnPoints.add(newPoint);
                currentDirection = getRandomDirection();
                distanceTraveled = 0;
                currentSegmentLength = 0.5 + random.nextDouble() * 1.5;
            }

            double totalLength = calculateTotalLength();
            while (totalLength > LINE_LENGTH && points.size() > 2) {
                points.remove(0);
                totalLength = calculateTotalLength();
            }

            if (!points.isEmpty()) {
                Vector3d firstPoint = points.get(0);
                turnPoints.removeIf(turnPoint -> turnPoint.distanceTo(firstPoint) > LINE_LENGTH);
            }
        }

        boolean shouldRespawn(Vector3d anchor) {
            if (points.isEmpty()) return true;
            Vector3d lastPoint = points.get(points.size() - 1);
            int renderChunks = mc.gameSettings.renderDistanceChunks;
            double worldRadius = Math.max(32.0, renderChunks * 16.0);

            if (Math.abs(lastPoint.x - anchor.x) > worldRadius * 1.35) return true;
            if (Math.abs(lastPoint.z - anchor.z) > worldRadius * 1.35) return true;
            return lastPoint.distanceTo(anchor) > worldRadius * 1.5;
        }

        private Vector3d getRandomDirection() {
            return switch (random.nextInt(6)) {
                case 0 -> new Vector3d(1, 0, 0);
                case 1 -> new Vector3d(-1, 0, 0);
                case 2 -> new Vector3d(0, 1, 0);
                case 3 -> new Vector3d(0, -1, 0);
                case 4 -> new Vector3d(0, 0, 1);
                default -> new Vector3d(0, 0, -1);
            };
        }

        private double calculateTotalLength() {
            double len = 0;
            for (int i = 0; i < points.size() - 1; i++) {
                len += points.get(i).distanceTo(points.get(i + 1));
            }
            return len;
        }
    }
}
