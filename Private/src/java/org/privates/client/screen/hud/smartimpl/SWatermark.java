package org.privates.client.screen.hud.smartimpl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.player.MoveUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

public class SWatermark implements IRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    private final DragSetting drag;

    @Getter
    @Setter
    private boolean blurEnabled = true;

    public SWatermark(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public void render(Render2DEvent event) {
        MatrixStack stack = event.getMatrix();

        String cross = "†";
        String clientName = "Private";
        String buildType = "RELEASE SOON";
        String fps = Minecraft.getDebugFPS() + " FPS";
        String ping = (mc.isSingleplayer() ? "0" :
                mc.getConnection().getPlayerInfo(mc.player.getGameProfile().getId()).getResponseTime()) + " MS";
        String bps = String.format("%.1f BPS", MoveUtil.speedSqrt() * 20);

        final float fontSize = 12f; // 8 * 1.5
        final float padding = 6f;
        final float spacing = 2f;

        // Top row widths
        final float crossClientWidth = Fonts.SF_MEDIUM.getWidth(cross + " " + clientName, fontSize) + padding * 2;
        final float buildWidth = Fonts.SF_MEDIUM.getWidth(buildType, fontSize) + padding * 2;
        final float fpsWidth = Fonts.SF_MEDIUM.getWidth(fps, fontSize) + padding * 2;

        // Bottom row widths
        final float pingWidth = Fonts.SF_MEDIUM.getWidth(ping, fontSize) + padding * 2;
        final float bpsWidth = Fonts.SF_MEDIUM.getWidth(bps, fontSize) + padding * 2;

        final float topRowWidth = crossClientWidth + buildWidth + fpsWidth + spacing * 2;
        final float bottomRowWidth = pingWidth + bpsWidth + spacing;
        final float totalWidth = Math.max(topRowWidth, bottomRowWidth);
        final float height = fontSize + padding;
        final float totalHeight = height * 2 + spacing;

        float x = drag.position.x;
        float y = drag.position.y;

        if (x == 0 || y == 0) {
            drag.position.set(10, 10);
            x = 10;
            y = 10;
        }

        final float finalX = x;
        final float finalY = y;

        drag.size.set(totalWidth, totalHeight);

        if (blurEnabled) {
            BlurRenderer.registerRenderCall(() -> {
                // Top row
                float drawX = finalX;
                RenderUtil.Rounded.roundedRect(stack, drawX, finalY, crossClientWidth, height,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(4));
                drawX += crossClientWidth + spacing;
                RenderUtil.Rounded.roundedRect(stack, drawX, finalY, buildWidth, height,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(4));
                drawX += buildWidth + spacing;
                RenderUtil.Rounded.roundedRect(stack, drawX, finalY, fpsWidth, height,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(4));

                // Bottom row
                drawX = finalX;
                float bottomY = finalY + height + spacing;
                RenderUtil.Rounded.roundedRect(stack, drawX, bottomY, pingWidth, height,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(4));
                drawX += pingWidth + spacing;
                RenderUtil.Rounded.roundedRect(stack, drawX, bottomY, bpsWidth, height,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(4));
            });
            BlurRenderer.draw(2);
        }

        // Top row rendering
        float drawX = finalX;
        float centerY = finalY + height / 2f - fontSize / 2f;

        // Cross + Private
        RenderUtil.Rounded.roundedRect(stack, drawX, finalY, crossClientWidth, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(4));
        Fonts.SF_MEDIUM.draw(stack, cross, drawX + padding, centerY,
                Theme.getInstance().textColor(), fontSize);
        Fonts.SF_MEDIUM.draw(stack, clientName, drawX + padding + Fonts.SF_MEDIUM.getWidth(cross + " ", fontSize), centerY,
                ColorUtil.getColor(255, 255, 255), fontSize);
        drawX += crossClientWidth + spacing;

        // RELEASE SOON
        RenderUtil.Rounded.roundedRect(stack, drawX, finalY, buildWidth, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(4));
        Fonts.SF_MEDIUM.drawCenter(stack, buildType, drawX + buildWidth / 2, centerY,
                ColorUtil.getColor(255, 255, 255), fontSize);
        drawX += buildWidth + spacing;

        // FPS
        RenderUtil.Rounded.roundedRect(stack, drawX, finalY, fpsWidth, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(4));
        Fonts.SF_MEDIUM.drawCenter(stack, fps, drawX + fpsWidth / 2, centerY,
                ColorUtil.getColor(255, 255, 255), fontSize);

        // Bottom row rendering
        drawX = finalX;
        float bottomY = finalY + height + spacing;
        float bottomCenterY = bottomY + height / 2f - fontSize / 2f;

        // PING
        RenderUtil.Rounded.roundedRect(stack, drawX, bottomY, pingWidth, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(4));
        Fonts.SF_MEDIUM.drawCenter(stack, ping, drawX + pingWidth / 2, bottomCenterY,
                ColorUtil.getColor(255, 255, 255), fontSize);
        drawX += pingWidth + spacing;

        // BPS
        RenderUtil.Rounded.roundedRect(stack, drawX, bottomY, bpsWidth, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(4));
        Fonts.SF_MEDIUM.drawCenter(stack, bps, drawX + bpsWidth / 2, bottomCenterY,
                ColorUtil.getColor(255, 255, 255), fontSize);
    }
}