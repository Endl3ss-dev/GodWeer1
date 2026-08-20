package org.privates.client.managers.module.impl.render;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.world.BossInfo;
import net.mojang.blaze3d.matrix.MatrixStack;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BossBarRenderer extends AbstractHud implements IRenderer, IConfigurableHud {

    private final DragSetting drag;
    private float animatedWidth;
    private float animatedHeight;
    @Getter
    @Setter
    private boolean blurEnabled = true;
    @Getter
    @Setter
    private float blurStrength = 10f;
    private float scaleV;

    public float scale = 1.0f;
    public float barThickness = 2.0f;
    public boolean showSeconds = false;

    public BossBarRenderer(DragSetting drag) {
        this.drag = drag;
    }

    @Getter
    @Setter
    private boolean enableOutline = false;
    @Getter
    @Setter
    private float outlineWidth = 1.0f;

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER,
                        () -> this.scale,
                        v -> this.scale = v,
                        0.5f, 2.0f, 0.1f),

                new DragComponent.HudSetting("Bar Thickness", DragComponent.SettingType.SLIDER,
                        () -> this.barThickness,
                        v -> this.barThickness = v,
                        1.0f, 5.0f, 0.5f),

                new DragComponent.HudSetting("Show Seconds", DragComponent.SettingType.BOOLEAN,
                        () -> this.showSeconds ? 1f : 0f,
                        v -> this.showSeconds = v > 0.5f),

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
            return 1.5f;
        } else if (width >= 2560 && height >= 1440) {
            return 1.0f;
        } else if (width >= 1920 || height >= 1080) {
            return 0.85f;
        } else {
            return 0.75f;
        }
    }

    @Override
    public void render(Render2DEvent event) {
        MatrixStack matrix = event.getMatrix();
        boolean isPreview = mc.currentScreen instanceof ChatScreen;
        float resScale = getResolutionScale();
        float finalScale = resScale * scale;
        float x = drag.position.x;
        float y = drag.position.y;

        Collection<ClientBossInfo> bossInfos = mc.ingameGUI.getBossOverlay().getActiveBossBars();
        boolean hasBoss = !bossInfos.isEmpty();

        scaleV = Mathf.fast(scaleV, (hasBoss || isPreview) ? 1 : 0, 16);
        if (scaleV <= 0.01F) return;

        BossInfo boss = hasBoss ? bossInfos.iterator().next() : null;
        String bossName = isPreview ? "Вы играете с PrivateBeta 1.16.5" : (hasBoss ? boss.getName().getString() : "Boss Bar");
        float progress = isPreview ? 1.0f : (hasBoss ? boss.getPercent() : 1.0f);

        float fontSize = 7f * finalScale;
        float titleBoxHeight = 18f * finalScale;
        float progressBarHeight = 10f * finalScale;

        float targetWidth = Math.max(120 * finalScale, font.getWidth(bossName, fontSize) + 20 * finalScale);

        if (showSeconds && hasBoss) {
            int seconds = (int) (boss.getPercent() * 100);
            String timeText = seconds + "s";
            targetWidth = Math.max(targetWidth, font.getWidth(bossName + " " + timeText, fontSize) + 20 * finalScale);
        }

        float targetHeight = titleBoxHeight + progressBarHeight;

        animatedWidth = Mathf.fast(animatedWidth, targetWidth, 14);
        animatedHeight = Mathf.fast(animatedHeight, targetHeight, 14);

        if (x == 0 && y == 0) {
            float screenWidth = mc.getMainWindow().getScaledWidth();
            float newX = screenWidth - (animatedWidth > 0 ? animatedWidth : 120 * finalScale) - 4 * finalScale;
            float newY = 4 * finalScale;
            drag.position.set(newX, newY);
            x = newX;
            y = newY;
        }

        matrix.push();
        matrix.translate((x + animatedWidth / 2F), (y + animatedHeight / 2f), 0);
        matrix.scale(scaleV, scaleV, scaleV);
        matrix.translate(-(x + animatedWidth / 2F), -(y + animatedHeight / 2f), 0);

        RenderUtil.CustomStyled.draw(matrix, x, y, animatedWidth, animatedHeight, Round.of(6f * finalScale), blurEnabled, blurStrength);

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
                    Round.of(6f * finalScale));
        }

        String displayText = bossName;
        if (showSeconds && hasBoss) {
            int seconds = (int) (boss.getPercent() * 100);
            displayText = bossName + " §7(" + seconds + "s)";
        }

        font.drawCenter(matrix, displayText, x + animatedWidth / 2, y + titleBoxHeight / 2 - fontSize / 2, -1, fontSize);

        float barWidth = animatedWidth - 10 * finalScale;
        float barHeight = barThickness * finalScale;
        float barX = x + 5 * finalScale;
        float barY = y + titleBoxHeight + (progressBarHeight - barHeight) / 2;

        int trackColor = ColorUtil.getColor(255, 255, 255, 30);
        int progressColor = theme().clientColor();

        RenderUtil.Rounded.roundedRect(matrix, barX, barY, barWidth, barHeight, trackColor, Round.of(1 * finalScale));
        if (progress > 0) {
            RenderUtil.Rounded.roundedRect(matrix, barX, barY, barWidth * progress, barHeight, progressColor, Round.of(1 * finalScale));
        }

        drag.size.set(animatedWidth, animatedHeight);
        matrix.pop();
    }
}