package org.privates.client.screen.hud.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.RenderScoreBoardEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IScoreBoardRenerer;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreBoardRenderer implements IScoreBoardRenerer, IConfigurableHud {
    private final DragSetting drag;

    @Setter
    @Getter
    private boolean blurEnabled = true;
    @Setter
    @Getter
    private float blurStrength = 10f;

    public float scale = 0.9f;
    public boolean showCount = true;

    @Setter
    @Getter
    private boolean enableOutline = false;
    @Setter
    @Getter
    private float outlineWidth = 1.0f;

    // Drag animation fields
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

    public ScoreBoardRenderer(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER,
                        () -> this.scale,
                        v -> this.scale = v,
                        0.5f, 2.0f, 0.1f),

                new DragComponent.HudSetting("Show Count", DragComponent.SettingType.BOOLEAN,
                        () -> this.showCount ? 1f : 0f,
                        v -> this.showCount = v > 0.5f),

                new DragComponent.HudSetting("Enable Outline", DragComponent.SettingType.BOOLEAN,
                        () -> this.enableOutline ? 1f : 0f,
                        v -> this.enableOutline = v > 0.5f),

                new DragComponent.HudSetting("Outline Width", DragComponent.SettingType.SLIDER,
                        () -> this.outlineWidth,
                        v -> this.outlineWidth = v,
                        0.5f, 3.0f, 0.1f)
        );
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
    public void renderScoreBoard(RenderScoreBoardEvent event) {
        event.cancel();
        MatrixStack matrix = event.getMatrix();
        FontRenderer fontRenderer = event.getFontRenderer();
        ScoreObjective objective = event.getObjective();
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<Score> collection = scoreboard.getSortedScores(objective);
        List<Score> list = collection.stream().filter((p_lambda$renderScoreboard$1_0_) -> !p_lambda$renderScoreboard$1_0_.getPlayerName().startsWith("#")).collect(Collectors.toList());
        collection = list.size() > 15 ? Lists.newArrayList(Iterables.skip(list, collection.size() - 15)) : list;
        if (collection.isEmpty()) return;

        List<Pair<Score, ITextComponent>> list1 = Lists.newArrayListWithCapacity(collection.size());
        ITextComponent itextcomponent = objective.getDisplayName();
        int i = fontRenderer.getStringPropertyWidth(itextcomponent);
        int width = i;
        int k = fontRenderer.getStringWidth(": ");

        for (Score score : collection) {
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            ITextComponent itextcomponent1 = ScorePlayerTeam.func_237500_a_(scoreplayerteam, new StringTextComponent(score.getPlayerName()));
            list1.add(Pair.of(score, itextcomponent1));
            width = Math.max(width, fontRenderer.getStringPropertyWidth(itextcomponent1) + k + fontRenderer.getStringWidth(Integer.toString(score.getScorePoints())));
        }

        width = (int) ((width + 8) * scale);
        float titleBoxHeight = 18 * scale;
        int height = (int) (collection.size() * 10 * scale + titleBoxHeight);
        float x = drag.position.x;
        float y = drag.position.y;
        drag.size.set(width, height);

        if (x == 0 && y == 0) {
            float screenWidth = mc.getMainWindow().getScaledWidth();
            float screenHeight = mc.getMainWindow().getScaledHeight();
            float newX = screenWidth - width - 4;
            float newY = screenHeight / 2f - height / 2f;
            drag.position.set(newX, newY);
            x = newX;
            y = newY;
            lastX = x;
            lastY = y;
        }

        // Update drag animation
        updateDragAnimation(x, y, width, height);

        // Apply drag transform
        matrix.push();
        applyDragTransform(matrix, x, y, width, height);

        RenderUtil.CustomStyled.draw(matrix, x, y, width, height, Round.of(6f * scale), blurEnabled, blurStrength);

        if (enableOutline) {
            int themeColor = Theme.getInstance().clientColor();
            int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
            float halfWidth = outlineWidth * scale / 2f;
            RenderUtil.Rounded.roundedOutline(matrix,
                    x - halfWidth,
                    y - halfWidth,
                    width + outlineWidth * scale,
                    height + outlineWidth * scale,
                    outlineWidth * scale,
                    outlineColor,
                    Round.of(6 * scale));
        }

        ScissorUtil.enableScissor(x, y, width, titleBoxHeight);
        RenderUtil.Rounded.smooth(matrix, x, y, width, height,
                ColorUtil.replAlpha(new Color(0xFF000000, true).getRGB(), 0.2F),
                Round.of(6 * scale, 6 * scale, 6 * scale, 6 * scale));
        ScissorUtil.disableScissor();

        float padding = 6 * scale;
        int clientColor = Theme.getInstance().clientColor();

        matrix.push();
        matrix.scale(scale, scale, scale);
        fontRenderer.drawString(matrix, itextcomponent, (x + padding) / scale, (y + 5 * scale) / scale, -1);
        matrix.pop();

        if (showCount) {
            String countText = String.valueOf(collection.size());
            float countWidth = fontRenderer.getStringWidth(countText) * scale;
            float countRectX = x + width - padding - countWidth - 4 * scale;
            float countRectY = y + (titleBoxHeight - 10 * scale) / 2f;
            float countRectWidth = countWidth + 8 * scale;
            float countRectHeight = 10 * scale;

            RenderUtil.CustomStyled.draw(matrix, countRectX, countRectY,
                    countRectWidth, countRectHeight, Round.of(3 * scale), blurEnabled, blurStrength * 0.6f);

            matrix.push();
            matrix.scale(scale, scale, scale);
            fontRenderer.drawString(matrix, countText, (countRectX + 4 * scale) / scale, (countRectY + 1 * scale) / scale, clientColor);
            matrix.pop();
        }

        RenderUtil.Rounded.roundedRect(matrix, x + 4 * scale, y + titleBoxHeight - 1 * scale, width - 8 * scale, 0.5f * scale, ColorUtil.getColor(255, 255, 255, 30), Round.of(1 * scale));

        int l = 0;
        for (Pair<Score, ITextComponent> pair : list1) {
            ++l;
            ITextComponent itextcomponent2 = pair.getSecond();
            float y1 = y + titleBoxHeight + (l - 1) * 10 * scale;
            String s = "" + pair.getFirst().getScorePoints();

            matrix.push();
            matrix.scale(scale, scale, scale);
            fontRenderer.drawString(matrix, itextcomponent2, (x + 4 * scale) / scale, y1 / scale, ColorUtil.getColor(255, 255, 255, 235));
            matrix.pop();

            float scoreWidth = fontRenderer.getStringWidth(s) * scale;
            float scoreX = x + width - scoreWidth - 4 * scale;
            float scoreRectX = scoreX - 2 * scale;
            float scoreRectY = y1 - 1 * scale;
            float scoreRectWidth = scoreWidth + 4 * scale;
            float scoreRectHeight = 9 * scale;

            RenderUtil.CustomStyled.draw(matrix, scoreRectX, scoreRectY,
                    scoreRectWidth, scoreRectHeight, Round.of(3 * scale), blurEnabled, blurStrength * 0.6f);

            matrix.push();
            matrix.scale(scale, scale, scale);
            fontRenderer.drawString(matrix, s, scoreX / scale, y1 / scale, clientColor);
            matrix.pop();
        }

        // Pop drag transform
        matrix.pop();
    }
}