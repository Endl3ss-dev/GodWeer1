package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.opengl.GL11;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MotionGraphRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private final DragSetting drag;
    private final List<Double> speedHistory = new ArrayList<>();
    private Vector3d lastPosition;
    private long lastTime;

    @Setter
    @Getter
    private boolean blurEnabled = true;
    @Setter
    @Getter
    private float blurStrength = 10f;
    @Setter
    @Getter
    private float scaleV;
    @Setter
    @Getter
    private boolean enableOutline = false;
    @Setter
    @Getter
    private float outlineWidth = 1.0f;

    private float animatedWidth;
    private float animatedHeight;

    private double maxSpeedEver = 0;

    public float scale = 0.9f;
    public float titleDarkness = 0.0f;
    public boolean showTitleIcon = true;
    public boolean showActualBPS = true;
    public boolean showMaxBPS = true;
    public boolean showTitle = true;

    private float lastX = 0;
    private float lastY = 0;
    private float smoothVelocityX = 0;
    private float smoothVelocityY = 0;
    private float currentRotation = 0;
    private float targetRotation = 0;
    private float springOffsetX = 0;
    private float springOffsetY = 0;
    private float dragScaleEffect = 1.0f;
    private boolean isDragging = false;
    private long lastMoveTime = 0;

    public MotionGraphRenderer(DragSetting drag) {
        this.drag = drag;
        lastTime = System.currentTimeMillis();
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER,
                        () -> this.scale,
                        v -> this.scale = v,
                        0.5f, 2.0f, 0.1f),

                new DragComponent.HudSetting("Title Darkness", DragComponent.SettingType.SLIDER,
                        () -> this.titleDarkness,
                        v -> this.titleDarkness = v,
                        0.0f, 1.0f, 0.05f),

                new DragComponent.HudSetting("Title Icon", DragComponent.SettingType.BOOLEAN,
                        () -> this.showTitleIcon ? 1f : 0f,
                        v -> this.showTitleIcon = v > 0.5f),

                new DragComponent.HudSetting("Show Actual BPS", DragComponent.SettingType.BOOLEAN,
                        () -> this.showActualBPS ? 1f : 0f,
                        v -> this.showActualBPS = v > 0.5f),

                new DragComponent.HudSetting("Show Max BPS", DragComponent.SettingType.BOOLEAN,
                        () -> this.showMaxBPS ? 1f : 0f,
                        v -> this.showMaxBPS = v > 0.5f),

                new DragComponent.HudSetting("Show Title", DragComponent.SettingType.BOOLEAN,
                        () -> this.showTitle ? 1f : 0f,
                        v -> this.showTitle = v > 0.5f),

                new DragComponent.HudSetting("Enable Outline", DragComponent.SettingType.BOOLEAN,
                        () -> this.enableOutline ? 1f : 0f,
                        v -> this.enableOutline = v > 0.5f),

                new DragComponent.HudSetting("Outline Width", DragComponent.SettingType.SLIDER,
                        () -> this.outlineWidth,
                        v -> this.outlineWidth = v,
                        0.5f, 3.0f, 0.1f)
        );
    }

    private float getResolutionScale() {
        int width = mc.getMainWindow().getWidth();
        int height = mc.getMainWindow().getHeight();

        if (width >= 3840 && height >= 2160) {
            return 1f;
        } else if (width >= 2560 && height >= 1440) {
            return 1.0f;
        } else if (width >= 1920 || height >= 1080) {
            return 1f;
        } else {
            return 1f;
        }
    }

    private float smoothLerp(float current, float target, float speed) {
        float diff = target - current;
        float factor = 1.0f - (float) Math.pow(1.0f - speed, 1.5f);
        return current + diff * factor;
    }

    private float elasticLerp(float current, float target, float speed, float elasticity) {
        float diff = target - current;
        float velocity = diff * speed;
        if (Math.abs(diff) < 0.5f) {
            velocity += diff * elasticity * 0.3f;
        }
        return current + velocity;
    }

    private void updateDragAnimation(float x, float y, float width, float height) {
        float instantVelocityX = x - lastX;
        float instantVelocityY = y - lastY;

        boolean isMoving = Math.abs(instantVelocityX) > 0.01f || Math.abs(instantVelocityY) > 0.01f;

        if (isMoving) {
            lastMoveTime = System.currentTimeMillis();
            isDragging = true;
        } else if (System.currentTimeMillis() - lastMoveTime > 50) {
            isDragging = false;
        }

        float velocitySmoothing = isDragging ? 0.25f : 0.08f;
        smoothVelocityX = smoothLerp(smoothVelocityX, instantVelocityX, velocitySmoothing);
        smoothVelocityY = smoothLerp(smoothVelocityY, instantVelocityY, velocitySmoothing);

        float tiltSensitivity = 1.8f;
        float maxTilt = 15.0f;

        float horizontalInfluence = smoothVelocityX * tiltSensitivity;
        float verticalInfluence = smoothVelocityY * 0.3f;

        targetRotation = horizontalInfluence - verticalInfluence;
        targetRotation = Math.max(-maxTilt, Math.min(maxTilt, targetRotation));

        float rotationSpeed = isDragging ? 0.18f : 0.06f;
        currentRotation = elasticLerp(currentRotation, targetRotation, rotationSpeed, 0.15f);

        if (!isDragging && Math.abs(currentRotation) < 0.1f) {
            currentRotation *= 0.9f;
        }

        float springStrength = 0.12f;
        float springDamping = 0.85f;

        float targetSpringX = -smoothVelocityX * 0.3f;
        float targetSpringY = -smoothVelocityY * 0.2f;

        springOffsetX = springOffsetX * springDamping + (targetSpringX - springOffsetX) * springStrength;
        springOffsetY = springOffsetY * springDamping + (targetSpringY - springOffsetY) * springStrength;

        float maxSpring = 5.0f;
        springOffsetX = Math.max(-maxSpring, Math.min(maxSpring, springOffsetX));
        springOffsetY = Math.max(-maxSpring, Math.min(maxSpring, springOffsetY));

        float targetDragScale = isDragging ? 1.02f : 1.0f;
        dragScaleEffect = smoothLerp(dragScaleEffect, targetDragScale, 0.15f);

        lastX = x;
        lastY = y;
    }

    private void applyDragTransform(MatrixStack matrix, float x, float y, float width, float height) {
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        float pivotX = centerX;
        float pivotY = y + height * 0.15f;

        matrix.translate(centerX, centerY, 0);
        matrix.scale(dragScaleEffect, dragScaleEffect, 1);
        matrix.translate(-centerX, -centerY, 0);

        matrix.translate(springOffsetX, springOffsetY, 0);

        if (Math.abs(currentRotation) > 0.01f) {
            matrix.translate(pivotX, pivotY, 0);
            matrix.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotationDegrees(currentRotation));
            matrix.translate(-pivotX, -pivotY, 0);
        }
    }

    @Override
    public void render(Render2DEvent event) {
        float resScale = getResolutionScale();
        float finalScale = resScale * scale;
        boolean isPreview = mc.currentScreen instanceof ChatScreen;

        if (isPreview) {
            updatePreviewData();
        } else {
            updateSpeedData();
        }

        MatrixStack matrix = event.getMatrix();
        float x = drag.position.x;
        float y = drag.position.y;

        float targetWidth = 104.5f * finalScale;
        float titleBoxHeight = showTitle ? 18f * finalScale : 0f;
        float bottomBoxHeight = (showActualBPS || showMaxBPS) ? 14f * finalScale : 0f;
        float targetHeight = titleBoxHeight + 28.5f * finalScale + bottomBoxHeight;

        animatedWidth = Mathf.fast(animatedWidth, targetWidth, 14);
        animatedHeight = Mathf.fast(animatedHeight, targetHeight, 14);

        drag.size.set(animatedWidth, animatedHeight);

        if (x == 0 && y == 0) {
            float newX = 6 * finalScale;
            float newY = mc.getMainWindow().getScaledHeight() - drag.size.y - 6 * finalScale - 50 * finalScale;
            drag.position.set(newX, newY);
            x = newX;
            y = newY;
            lastX = x;
            lastY = y;
        }

        boolean shouldRender = isPreview || !speedHistory.isEmpty();
        scaleV = Mathf.fast(scaleV, shouldRender ? 1 : 0, 16);

        if (Mathf.round(scaleV, .1d) <= 0) return;

        updateDragAnimation(x, y, animatedWidth, animatedHeight);

        matrix.push();
        matrix.translate((x + animatedWidth / 2F), (y + animatedHeight / 2f), 0);
        matrix.scale(scaleV, scaleV, scaleV);
        matrix.translate(-(x + animatedWidth / 2F), -(y + animatedHeight / 2f), 0);

        matrix.push();
        applyDragTransform(matrix, x, y, animatedWidth, animatedHeight);

        float containerRadius = 6f * finalScale;
        RenderUtil.CustomStyled.draw(matrix, x, y, animatedWidth, animatedHeight, Round.of(containerRadius), blurEnabled, blurStrength);

        if (enableOutline) {
            int themeColor = Theme.getInstance().clientColor();
            int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
            float halfWidth = outlineWidth * finalScale / 2f;
            RenderUtil.Rounded.roundedOutline(matrix,
                    x - halfWidth,
                    y - halfWidth,
                    animatedWidth + outlineWidth * finalScale,
                    animatedHeight + outlineWidth * finalScale,
                    outlineWidth * finalScale,
                    outlineColor,
                    Round.of(containerRadius));
        }

        float currentY = y;

        if (showTitle) {
            ScissorUtil.enableScissor(x, currentY, animatedWidth, titleBoxHeight);
            RenderUtil.Rounded.smooth(matrix, x, y, animatedWidth, animatedHeight, ColorUtil.replAlpha(new Color(0xFF000000, true).getRGB(), titleDarkness), Round.of(containerRadius, containerRadius, containerRadius, containerRadius));
            ScissorUtil.disableScissor();

            renderTitleContent(matrix, x, currentY, animatedWidth, titleBoxHeight, finalScale);
            currentY += titleBoxHeight;
        }

        float graphHeight = animatedHeight - (showTitle ? titleBoxHeight : 0) - bottomBoxHeight;
        renderGraph(matrix, x, currentY, animatedWidth, graphHeight, finalScale);
        currentY += graphHeight;

        if (showActualBPS || showMaxBPS) {
            renderBottomContent(matrix, x, currentY, animatedWidth, bottomBoxHeight, finalScale);
        }

        matrix.pop();
        matrix.pop();
    }

    private void updatePreviewData() {
        if (speedHistory.isEmpty() || speedHistory.size() < 100) {
            speedHistory.clear();
            for (int i = 0; i < 100; i++) {
                double speed = Math.sin(i * 0.1) * 20 + 35;
                speedHistory.add(speed);
                if (speed > maxSpeedEver) {
                    maxSpeedEver = speed;
                }
            }
        } else {
            double last = speedHistory.remove(0);
            speedHistory.add(last);
        }
    }

    private void updateSpeedData() {
        if (mc.player == null) {
            speedHistory.clear();
            maxSpeedEver = 0;
            return;
        }

        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastTime;

        if (timeDiff > 50) {
            if (lastPosition != null) {
                Vector3d currentPosition = mc.player.getPositionVec();
                double distance = lastPosition.distanceTo(currentPosition);
                double speedBPS = (distance / timeDiff) * 1000.0;

                speedHistory.add(speedBPS);
                if (speedBPS > maxSpeedEver) {
                    maxSpeedEver = speedBPS;
                }

                if (speedHistory.size() > 100) {
                    speedHistory.remove(0);
                }
            }
            lastPosition = mc.player.getPositionVec();
            lastTime = currentTime;
        }
    }

    private void renderTitleContent(MatrixStack matrix, float x, float y, float width, float height, float finalScale) {
        float padding = 6f * finalScale;
        float fontSize = 7f * finalScale;
        float iconSize = 10f * finalScale;
        float offsetX = 2f * finalScale;
        float offsetY = 0.25f * finalScale;

        float currentX = x + padding - offsetX;
        float iconY = y + (height - iconSize) / 2f - offsetY;

        float textY = y + (height - fontSize) / 2f - offsetY;
        font.draw(matrix, "Motion", currentX, textY, -1, fontSize);

        if (showTitleIcon) {
            float runIconSize = 18f * finalScale;
            float runIconX = x + width - padding - runIconSize + 2f * finalScale;
            float runIconY = y + (height - runIconSize) / 2f - offsetY;
            Fonts.iconf.draw(matrix, "e", runIconX + 3, runIconY + 3.5f, theme().clientColor(), runIconSize - 6.5f);
        }
    }

    private void renderBottomContent(MatrixStack matrix, float x, float y, float width, float height, float finalScale) {
        float padding = 6f * finalScale;
        float fontSize = 6f * finalScale;

        double currentSpeed = speedHistory.isEmpty() ? 0 : speedHistory.get(speedHistory.size() - 1);

        if (showActualBPS) {
            String actualText = String.format("Actual: %.0fBPS", currentSpeed);
            float actualX = x + padding;
            float actualY = y + (height - fontSize) / 2f;
            font.draw(matrix, actualText, actualX, actualY, ColorUtil.getColor(180, 180, 180), fontSize);
        }

        if (showMaxBPS) {
            String maxText = String.format("Max: %.0fBPS", maxSpeedEver);
            float maxWidth = font.getWidth(maxText, fontSize);
            float maxX = x + width - padding - maxWidth;
            float maxY = y + (height - fontSize) / 2f;
            font.draw(matrix, maxText, maxX, maxY, ColorUtil.getColor(180, 180, 180), fontSize);
        }
    }

    private void renderGraph(MatrixStack matrix, float x, float y, float width, float height, float finalScale) {
        if (speedHistory.isEmpty()) return;

        float padding = 6f * finalScale;
        double maxSpeed = Collections.max(speedHistory);
        maxSpeed = Math.max(maxSpeed, 10);

        float graphX = x + padding;
        float graphY = y + height - padding;
        float graphWidth = width - padding * 2;
        float graphHeight = height - padding * 2;

        RenderUtil.start();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(1.5f * finalScale);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 0; i < speedHistory.size(); i++) {
            double speed = speedHistory.get(i);
            float pointX = graphX + ((float) i / Math.max(1, speedHistory.size() - 1)) * graphWidth;
            float pointY = graphY - (float) (speed / maxSpeed) * graphHeight;
            RenderUtil.setColor(theme().clientColor());
            GL11.glVertex2f(pointX, pointY);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderUtil.stop();
    }
}