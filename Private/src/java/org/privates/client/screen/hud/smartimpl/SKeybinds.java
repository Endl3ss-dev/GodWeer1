package org.privates.client.screen.hud.smartimpl;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.List;

public class SKeybinds extends AbstractHud implements IRenderer {
    private final DragSetting drag;
    private final Animation scaleAnimation = new Animation();

    @Getter
    @Setter
    private boolean blurEnabled = true;

    public SKeybinds(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public void render(Render2DEvent event) {
        MatrixStack matrix = event.getMatrix();

        List<Module> activeModules = Excellent.inst().moduleManager().values().stream()
                .filter(this::shouldDisplay)
                .toList();

        scaleAnimation.update();
        boolean shouldShow = !activeModules.isEmpty() || mc.currentScreen != null;
        scaleAnimation.run(shouldShow ? 1.0 : 0.0, 0.3, Easings.EXPO_OUT, false);

        if (scaleAnimation.getValue() <= 0.01) return;

        float x = drag.position.x;
        float y = drag.position.y;
        final float width = 58f; // 40 * 1.45
        final float titleHeight = 16f;
        final float itemHeight = 12f;
        final float spacing = 4f;
        final float totalHeight = titleHeight + spacing + activeModules.size() * itemHeight;

        if (x == 0 || y == 0) {
            drag.position.set(mc.getMainWindow().getScaledWidth() - width - 10, 150);
            x = mc.getMainWindow().getScaledWidth() - width - 10;
            y = 150;
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
                for (int i = 0; i < activeModules.size(); i++) {
                    RenderUtil.Rounded.roundedRect(matrix, finalX, offsetY, width, itemHeight,
                            ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(3));
                    offsetY += itemHeight;
                }
            });
            BlurRenderer.draw(2);
        }

        RenderUtil.Rounded.roundedRect(matrix, finalX, finalY, width, titleHeight,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(4));

        String title = "Hot Keys";
        Fonts.SF_MEDIUM.drawCenter(matrix, title, finalX + width / 2, finalY + 6,
                ColorUtil.getColor(255, 255, 255), 6);

        float offsetY = finalY + titleHeight + spacing;
        for (Module module : activeModules) {
            renderKeybind(matrix, module, finalX, offsetY, width, itemHeight);
            offsetY += itemHeight;
        }

        matrix.pop();
    }

    private void renderKeybind(MatrixStack matrix, Module module, float x, float y, float width, float height) {
        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(3));

        String name = module.getName();
        if (name.length() > 6) name = name.substring(0, 6);
        String key = "[" + Keyboard.keyName(module.getKey()) + "]";

        float nameY = y + 2;
        float keyY = y + height - 8;

        Fonts.SF_MEDIUM.draw(matrix, name, x + 2, nameY,
                ColorUtil.getColor(255, 255, 255), 7.5f); // 5 * 1.5
        Fonts.SF_MEDIUM.draw(matrix, key, x + 2, keyY,
                ColorUtil.getColor(255, 255, 255), 6); // 4 * 1.5
    }

    public boolean shouldDisplay(Module module) {
        return module.isAllowDisable()
                && module.isEnabled()
                && module.getKey() != Keyboard.KEY_NONE.getKey();
    }
}