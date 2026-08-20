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

public class SInformation implements IRenderer {
    private final DragSetting drag;

    @Getter
    @Setter
    private boolean blurEnabled = true;

    public SInformation(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public void render(Render2DEvent event) {
        MatrixStack stack = event.getMatrix();

        String coords = String.format("%d %d %d",
                (int) mc.player.getPosX(),
                (int) mc.player.getPosY(),
                (int) mc.player.getPosZ());
        String fps = Minecraft.getDebugFPS() + " FPS";
        String ping = (mc.isSingleplayer() ? "0" :
                mc.getConnection().getPlayerInfo(mc.player.getGameProfile().getId()).getResponseTime()) + " MS";
        String bps = String.format("%.1f BPS", MoveUtil.speedSqrt() * 20);

        final float fontSize = 5f;
        final float padding = 4f;
        final float spacing = 4f;

        final float coordsWidth = Fonts.SF_MEDIUM.getWidth(coords, fontSize) + padding * 2;
        final float fpsWidth = Fonts.SF_MEDIUM.getWidth(fps, fontSize) + padding * 2;
        final float pingWidth = Fonts.SF_MEDIUM.getWidth(ping, fontSize) + padding * 2;
        final float bpsWidth = Fonts.SF_MEDIUM.getWidth(bps, fontSize) + padding * 2;

        final float totalWidth = coordsWidth + fpsWidth + pingWidth + bpsWidth + spacing * 3;
        final float height = fontSize + padding;

        float x = drag.position.x;
        float y = drag.position.y;

        if (x == 0 || y == 0) {
            drag.position.set(6, mc.getMainWindow().getScaledHeight() - height - 10);
            x = 6;
            y = mc.getMainWindow().getScaledHeight() - height - 10;
        }

        final float finalX = x;
        final float finalY = y;

        drag.size.set(totalWidth, height);

        if (blurEnabled) {
            BlurRenderer.registerRenderCall(() -> {
                float drawX = finalX;
                RenderUtil.Rounded.roundedRect(stack, drawX, finalY, coordsWidth, height,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(2));
                drawX += coordsWidth + spacing;
                RenderUtil.Rounded.roundedRect(stack, drawX, finalY, fpsWidth, height,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(2));
                drawX += fpsWidth + spacing;
                RenderUtil.Rounded.roundedRect(stack, drawX, finalY, pingWidth, height,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(2));
                drawX += pingWidth + spacing;
                RenderUtil.Rounded.roundedRect(stack, drawX, finalY, bpsWidth, height,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(2));
            });
            BlurRenderer.draw(2);
        }

        float drawX = finalX;

        RenderUtil.Rounded.roundedRect(stack, drawX, finalY, coordsWidth, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(2));
        Fonts.SF_MEDIUM.drawCenter(stack, coords, drawX + coordsWidth / 2, finalY + padding / 2,
                ColorUtil.getColor(255, 255, 255), fontSize);
        drawX += coordsWidth + spacing;

        RenderUtil.Rounded.roundedRect(stack, drawX, finalY, fpsWidth, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(2));
        Fonts.SF_MEDIUM.drawCenter(stack, fps, drawX + fpsWidth / 2, finalY + padding / 2,
                ColorUtil.getColor(255, 255, 255), fontSize);
        drawX += fpsWidth + spacing;

        RenderUtil.Rounded.roundedRect(stack, drawX, finalY, pingWidth, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(2));
        Fonts.SF_MEDIUM.drawCenter(stack, ping, drawX + pingWidth / 2, finalY + padding / 2,
                ColorUtil.getColor(255, 255, 255), fontSize);
        drawX += pingWidth + spacing;

        RenderUtil.Rounded.roundedRect(stack, drawX, finalY, bpsWidth, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(2));
        Fonts.SF_MEDIUM.drawCenter(stack, bps, drawX + bpsWidth / 2, finalY + padding / 2,
                ColorUtil.getColor(255, 255, 255), fontSize);
    }
}