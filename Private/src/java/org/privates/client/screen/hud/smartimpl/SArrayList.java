package org.privates.client.screen.hud.smartimpl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Module;
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

import java.util.List;

public class SArrayList implements IRenderer {
    private final Minecraft mc = Minecraft.getInstance();
    private final DragSetting drag;
    private final Animation scaleAnimation = new Animation();

    @Getter
    @Setter
    private boolean blurEnabled = true;

    public SArrayList(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public void render(Render2DEvent event) {
        MatrixStack stack = event.getMatrix();
        if (mc.player == null || mc.world == null) return;

        List<Module> enabledModules = Excellent.inst().moduleManager().values().stream()
                .filter(Module::isEnabled)
                .sorted((m1, m2) -> Float.compare(
                        Fonts.SF_MEDIUM.getWidth(m2.getName(), 6),
                        Fonts.SF_MEDIUM.getWidth(m1.getName(), 6)))
                .toList();

        scaleAnimation.update();
        boolean shouldShow = !enabledModules.isEmpty() || mc.currentScreen != null;
        scaleAnimation.run(shouldShow ? 1.0 : 0.0, 0.3, Easings.EXPO_OUT, false);

        if (scaleAnimation.getValue() <= 0.01) return;

        float maxWidth = enabledModules.stream()
                .map(m -> Fonts.SF_MEDIUM.getWidth(m.getName(), 6) + 6)
                .max(Float::compare)
                .orElse(0f);

        float moduleHeight = 8f;
        float totalHeight = enabledModules.size() * moduleHeight;

        float x = drag.position.x;
        float y = drag.position.y;

        if (x <= 0 || y <= 0) {
            drag.position.set(mc.getMainWindow().getScaledWidth() - maxWidth - 10, 40);
            x = mc.getMainWindow().getScaledWidth() - maxWidth - 10;
            y = 40;
        }

        final float finalX = x;
        final float finalY = y;

        drag.size.set(maxWidth, totalHeight);

        stack.push();
        stack.translate(finalX + maxWidth / 2, finalY + totalHeight / 2, 0);
        stack.scale((float) scaleAnimation.getValue(), (float) scaleAnimation.getValue(), 1);
        stack.translate(-(finalX + maxWidth / 2), -(finalY + totalHeight / 2), 0);

        if (blurEnabled) {
            BlurRenderer.registerRenderCall(() -> {
                float currentY = finalY;
                for (Module module : enabledModules) {
                    float moduleWidth = Fonts.SF_MEDIUM.getWidth(module.getName(), 6) + 6;
                    float moduleX = finalX + maxWidth - moduleWidth;
                    RenderUtil.Rounded.roundedRect(stack, moduleX, currentY, moduleWidth, moduleHeight,
                            ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(2));
                    currentY += moduleHeight;
                }
            });
            BlurRenderer.draw(2);
        }

        float currentY = finalY;
        for (Module module : enabledModules) {
            float moduleWidth = Fonts.SF_MEDIUM.getWidth(module.getName(), 6) + 6;
            float moduleX = finalX + maxWidth - moduleWidth;

            RenderUtil.Rounded.roundedRect(stack, moduleX, currentY, moduleWidth, moduleHeight,
                    ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(2));

            Fonts.SF_MEDIUM.draw(stack, module.getName(),
                    moduleX + 3, currentY + 2,
                    ColorUtil.getColor(255, 255, 255), 6);

            currentY += moduleHeight;
        }

        stack.pop();
    }
}