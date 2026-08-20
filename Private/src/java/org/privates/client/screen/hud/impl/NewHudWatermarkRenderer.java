package org.privates.client.screen.hud.impl;

import net.minecraft.util.Namespaced;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

public class NewHudWatermarkRenderer extends AbstractHud implements IRenderer {
    private static final float FIGMA_DIVIDER = 1.72f;
    private final DragSetting drag;

    private float scale = 1.0f;
    private boolean blurEnabled = true;
    private float blurStrength = 7.5f;

    private float animatedWidth;
    private float animatedHeight;
    private float lastX;
    private float lastY;
    private float smoothVelocityX;
    private float smoothVelocityY;
    private float currentRotation;
    private float targetRotation;
    private float springOffsetX;
    private float springOffsetY;
    private float dragScaleEffect = 1.0f;
    private boolean initialized;

    public NewHudWatermarkRenderer(DragSetting drag) {
        this.drag = drag;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setBlurEnabled(boolean blurEnabled) {
        this.blurEnabled = blurEnabled;
    }

    public void setBlurStrength(float blurStrength) {
        this.blurStrength = blurStrength;
    }

    @Override
    public void render(Render2DEvent event) {
        if (mc.player == null) return;

        MatrixStack matrix = event.getMatrix();
        int accent = Theme.getInstance().clientColor();

        String clientText = "Private";
        String userText = "@" + mc.player.getName().getString();
        String serverText = mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null && !mc.getCurrentServerData().serverIP.isEmpty()
                ? mc.getCurrentServerData().serverIP
                : "singleplayer";

        float sc = scale / FIGMA_DIVIDER;
        float height = 38f * sc;
        float radius = 10f * sc;
        float iconSize = 12f * sc;
        float textSize = 15f * sc;
        float lineH = 8f * sc;
        float glowSize = 18f * sc;
        float block1Min = 91f * sc;
        float block2Min = 81f * sc;
        float block3Min = 110f * sc;

        float textOffsetY = 9.5f * sc;
        float iconOffsetY = 13f * sc;
        float glowOffsetY = 10f * sc;
        float lineOffsetY = 14f * sc;

        float block1 = Math.max(block1Min, 22f * sc + Fonts.SF_REGULAR.getWidth(clientText, textSize) + 20f * sc);
        float block2 = Math.max(block2Min, 22f * sc + Fonts.SF_REGULAR.getWidth(userText, textSize) + 20f * sc);
        float block3 = Math.max(block3Min, 22f * sc + Fonts.SF_REGULAR.getWidth(serverText, textSize) + 20f * sc);
        float totalWidth = block1 + block2 + block3 + 2f * sc;

        animatedWidth = Mathf.fast(animatedWidth, totalWidth, 14f);
        animatedHeight = Mathf.fast(animatedHeight, height, 14f);

        float x = drag.position.x;
        float y = drag.position.y;

        if (x == 0 && y == 0) {
            drag.position.set(60f, 60f);
            x = 60f;
            y = 60f;
        }

        if (!initialized) {
            lastX = x;
            lastY = y;
            initialized = true;
        }

        drag.size.set(animatedWidth, animatedHeight);
        updateDragAnimation(x, y);

        float block2X = x + block1 + sc;
        float block3X = block2X + block2 + sc;

        matrix.push();
        applyDragTransform(matrix, x, y, animatedWidth, animatedHeight);

        int bgColor = ColorUtil.getColor(46, 44, 68, 102);
        int textColor = ColorUtil.getColor(255, 255, 255);
        int lineColor = ColorUtil.getColor(174, 174, 174, 180);
        int glowColor = ColorUtil.multAlpha(accent, 0.8f);

        RenderUtil.CustomStyled.draw(matrix, x, y, block1, height, Round.of(radius, radius, 0f, 0f), blurEnabled, blurStrength);
        RenderUtil.CustomStyled.draw(matrix, block2X, y, block2, height, Round.zero(), blurEnabled, blurStrength);
        RenderUtil.CustomStyled.draw(matrix, block3X, y, block3, height, Round.of(0f, 0f, radius, radius), blurEnabled, blurStrength);


        drawLogoBlock(matrix, x, y, clientText, accent, textColor, glowColor, lineColor, iconSize, textSize, glowSize, lineH, iconOffsetY, textOffsetY, glowOffsetY, lineOffsetY, 10f * sc, 27f * sc, 32f * sc);
        drawBlock(matrix, block2X, y, "e", userText, accent, textColor, glowColor, lineColor, iconSize, textSize, glowSize, lineH, iconOffsetY, textOffsetY, glowOffsetY, lineOffsetY, 10f * sc, 27f * sc, 32f * sc);
        drawBlock(matrix, block3X, y, "t", serverText, accent, textColor, glowColor, lineColor, iconSize, textSize, glowSize, lineH, iconOffsetY, textOffsetY, glowOffsetY, lineOffsetY, 10f * sc, 27f * sc, 32f * sc);

        matrix.pop();
    }

    private void drawLogoBlock(MatrixStack matrix, float x, float y, String text, int accent, int textColor, int glowColor, int lineColor,
                           float iconSize, float textSize, float glowSize, float lineH,
                           float iconOffsetY, float textOffsetY, float glowOffsetY, float lineOffsetY,
                           float iconX, float lineX, float textX) {
        float sc = scale / FIGMA_DIVIDER;
        float logoW = (12f + 8f) * sc;
        float logoH = (12f + 8f) * sc;
        float centerY = y + 19f * sc;
        RenderUtil.Shadow.drawShadow(matrix, x + 7f * sc, y + glowOffsetY, glowSize, glowSize, 4f * sc, 0.46f, glowColor, Round.of(8f * sc));
        RenderUtil.drawImage(matrix, new Namespaced("texture/logo.png"), x + iconX - 1f * sc, centerY - logoH / 2f, logoW, logoH, true, accent);
        RenderUtil.Rounded.roundedRect(matrix, x + lineX, y + lineOffsetY, 1f * sc, lineH, lineColor, Round.zero());
        Fonts.SF_REGULAR.draw(matrix, text, x + textX, y + textOffsetY, textColor, textSize);
    }

    private void drawBlock(MatrixStack matrix, float x, float y, String icon, String text, int accent, int textColor, int glowColor, int lineColor,
                           float iconSize, float textSize, float glowSize, float lineH,
                           float iconOffsetY, float textOffsetY, float glowOffsetY, float lineOffsetY,
                           float iconX, float lineX, float textX) {
        float sc = scale / FIGMA_DIVIDER;
        RenderUtil.Shadow.drawShadow(matrix, x + 7f * sc, y + glowOffsetY, glowSize, glowSize, 4f * sc, 0.46f, glowColor, Round.of(8f * sc));
        Fonts.icon_new.draw(matrix, icon, x + iconX, y + iconOffsetY, accent, iconSize);
        RenderUtil.Rounded.roundedRect(matrix, x + lineX, y + lineOffsetY, 1f * sc, lineH, lineColor, Round.zero());
        Fonts.SF_REGULAR.draw(matrix, text, x + textX, y + textOffsetY, textColor, textSize);
    }

    private float smoothLerp(float current, float target, float speed) {
        return current + (target - current) * (1.0f - (float) Math.pow(1.0f - speed, 1.5f));
    }

    private float elasticLerp(float current, float target, float speed, float elasticity) {
        float diff = target - current;
        float velocity = diff * speed;
        if (Math.abs(diff) < 0.5f) velocity += diff * elasticity * 0.3f;
        return current + velocity;
    }

    private void updateDragAnimation(float x, float y) {
        float instantVelocityX = x - lastX;
        float instantVelocityY = y - lastY;

        smoothVelocityX = smoothLerp(smoothVelocityX, instantVelocityX, 0.12f);
        smoothVelocityY = smoothLerp(smoothVelocityY, instantVelocityY, 0.12f);

        targetRotation = Math.max(-10f, Math.min(10f, smoothVelocityX * 1.5f - smoothVelocityY * 0.2f));
        currentRotation = elasticLerp(currentRotation, targetRotation, 0.12f, 0.12f);

        springOffsetX = springOffsetX * 0.88f + ((-smoothVelocityX * 0.25f) - springOffsetX) * 0.11f;
        springOffsetY = springOffsetY * 0.88f + ((-smoothVelocityY * 0.18f) - springOffsetY) * 0.11f;
        springOffsetX = Math.max(-4f, Math.min(4f, springOffsetX));
        springOffsetY = Math.max(-4f, Math.min(4f, springOffsetY));

        dragScaleEffect = smoothLerp(dragScaleEffect, (Math.abs(instantVelocityX) > 0.01f || Math.abs(instantVelocityY) > 0.01f) ? 1.01f : 1.0f, 0.1f);

        lastX = x;
        lastY = y;
    }

    private void applyDragTransform(MatrixStack matrix, float x, float y, float width, float height) {
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;

        matrix.translate(centerX, centerY, 0);
        matrix.scale(dragScaleEffect, dragScaleEffect, 1);
        matrix.translate(-centerX, -centerY, 0);
        matrix.translate(springOffsetX, springOffsetY, 0);

        if (Math.abs(currentRotation) > 0.01f) {
            float pivotY = y + height * 0.2f;
            matrix.translate(centerX, pivotY, 0);
            matrix.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotationDegrees(currentRotation));
            matrix.translate(-centerX, -pivotY, 0);
        }
    }
}
