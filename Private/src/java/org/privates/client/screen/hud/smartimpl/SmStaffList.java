package org.privates.client.screen.hud.smartimpl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SmStaffList implements IRenderer {
    private final Pattern prefixMatches = Pattern.compile(".*(mod|der|adm|help|wne|хелп|адм|поддержка|кура|own|taf|curat|dev|supp|yt|сотруд|Адм|осн|миум).*");
    private final DragSetting drag;
    private final Animation scaleAnimation = new Animation();

    @Getter
    @Setter
    private boolean blurEnabled = true;

    public SmStaffList(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public void render(Render2DEvent event) {
        MatrixStack matrix = event.getMatrix();
        if (mc.player == null || mc.world == null || mc.getConnection() == null) return;

        List<NetworkPlayerInfo> staff = new ArrayList<>();
        for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
            ScorePlayerTeam team = info.getPlayerTeam();
            String prefix = team != null ? team.getPrefix().getString() : "";
            if (prefixMatches.matcher(prefix.toLowerCase()).find()) {
                staff.add(info);
            }
        }

        scaleAnimation.update();
        boolean shouldShow = !staff.isEmpty() || mc.currentScreen != null;
        scaleAnimation.run(shouldShow ? 1.0 : 0.0, 0.3, Easings.EXPO_OUT, false);

        if (scaleAnimation.getValue() <= 0.01) return;

        float x = drag.position.x;
        float y = drag.position.y;
        final float width = 52f; // 36 * 1.45
        final float titleHeight = 16f;
        final float itemHeight = 12f;
        final float spacing = 4f;
        final float totalHeight = titleHeight + spacing + staff.size() * itemHeight;

        if (x == 0 || y == 0) {
            drag.position.set(10, 50);
            x = 10;
            y = 50;
        }

        final float finalX = x;
        final float finalY = y;

        drag.size.set(width, totalHeight);

        matrix.push();
        matrix.translate(finalX + width / 2, finalY + totalHeight / 2, 0);
        matrix.scale((float) scaleAnimation.getValue(), (float) scaleAnimation.getValue(), 1);
        matrix.translate(-(finalX + width / 2), -(finalY + totalHeight / 2), 0);

        if (blurEnabled) {
            BlurRenderer.registerRenderCall(() -> {
                RenderUtil.Rounded.roundedRect(matrix, finalX, finalY, width, titleHeight,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(4));
                float offsetY = finalY + titleHeight + spacing;
                for (int i = 0; i < staff.size(); i++) {
                    RenderUtil.Rounded.roundedRect(matrix, finalX, offsetY, width, itemHeight,
                            ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(3));
                    offsetY += itemHeight;
                }
            });
            BlurRenderer.draw(2);
        }

        RenderUtil.Rounded.roundedRect(matrix, finalX, finalY, width, titleHeight,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(4));

        String title = "Staff List";
        Fonts.SF_MEDIUM.drawCenter(matrix, title, finalX + width / 2, finalY + 6,
                ColorUtil.getColor(255, 255, 255), 6);

        float offsetY = finalY + titleHeight + spacing;
        for (NetworkPlayerInfo info : staff) {
            renderStaffEntry(matrix, info, finalX, offsetY, width, itemHeight);
            offsetY += itemHeight;
        }

        matrix.pop();
    }

    private void renderStaffEntry(MatrixStack matrix, NetworkPlayerInfo info, float x, float y, float width, float height) {
        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(3));

        ScorePlayerTeam team = info.getPlayerTeam();
        String name = info.getGameProfile().getName();
        if (name.length() > 6) name = name.substring(0, 6);

        Fonts.SF_MEDIUM.drawCenter(matrix, name, x + width / 2, y + height / 2 - 2,
                ColorUtil.getColor(255, 255, 255), 5);
    }
}