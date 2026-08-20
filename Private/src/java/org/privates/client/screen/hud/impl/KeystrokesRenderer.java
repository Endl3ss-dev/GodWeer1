package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.settings.KeyBinding;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeystrokesRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private final DragSetting drag;
    private final Map<KeyBinding, Animation> keyAnimations = new HashMap<>();

    @Setter
    @Getter
    public float scale = 1.0f;

    private static final float KEY = 19.53f;
    private static final float GAP = 1.17f;
    private static final float SPACE_H = 9.76f;
    private static final float RAD = 3.9f;
    private static final float BAR_W = 39.06f;
    private static final float BAR_H = 1.95f;
    private static final float BAR_RAD = 0.78f;
    private static final float FONT = 6.64f;

    public KeystrokesRenderer(DragSetting drag) {
        this.drag = drag;
        keyAnimations.put(mc.gameSettings.keyBindForward, new Animation());
        keyAnimations.put(mc.gameSettings.keyBindBack, new Animation());
        keyAnimations.put(mc.gameSettings.keyBindLeft, new Animation());
        keyAnimations.put(mc.gameSettings.keyBindRight, new Animation());
        keyAnimations.put(mc.gameSettings.keyBindJump, new Animation());
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return List.of(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER,
                        () -> this.scale, v -> this.scale = v, 0.5f, 2.0f, 0.1f)
        );
    }

    @Override
    public void render(Render2DEvent event) {
        float s = scale;
        float k = KEY * s;
        float g = GAP * s;
        float sh = SPACE_H * s;
        float r = RAD * s;
        float totalW = k * 3 + g * 2;
        float totalH = k * 2 + g * 2 + sh;

        drag.size.set(totalW, totalH);

        float x = drag.position.x;
        float y = drag.position.y;

        if (x == 0 && y == 0) {
            float sw = mc.getMainWindow().getScaledWidth();
            float sH = mc.getMainWindow().getScaledHeight();
            x = sw - totalW - 4;
            y = sH - totalH - 4;
            drag.position.set(x, y);
        }

        for (Map.Entry<KeyBinding, Animation> e : keyAnimations.entrySet()) {
            e.getValue().update();
            e.getValue().run(e.getKey().isKeyDown() ? 1 : 0, 0.15, Easings.EXPO_OUT);
        }

        MatrixStack m = event.getMatrix();
        int themeColor = Theme.getInstance().clientColor();
        float fontSize = FONT * s;

        float wX = x + k + g;
        float wY = y;
        drawKey(m, wX, wY, k, k, Round.of(4), mc.gameSettings.keyBindForward, "W", fontSize, themeColor, s);

        float row2Y = y + k + g;
        drawKey(m, x, row2Y, k, k, Round.of(4), mc.gameSettings.keyBindLeft, "A", fontSize, themeColor, s);
        drawKey(m, x + k + g, row2Y, k, k, Round.of(4), mc.gameSettings.keyBindBack, "S", fontSize, themeColor, s);
        drawKey(m, x + k * 2 + g * 2, row2Y, k, k, Round.of(4), mc.gameSettings.keyBindRight, "D", fontSize, themeColor, s);

        float spaceY = row2Y + k + g;
        drawSpace(m, x, spaceY, totalW, sh, Round.of(r, 0, r, 0), mc.gameSettings.keyBindJump, themeColor, s);
    }

    private void drawKey(MatrixStack m, float x, float y, float w, float h, Round round,
                         KeyBinding key, String text, float fontSize, int themeColor, float s) {
        float anim = (float) keyAnimations.get(key).getValue();
        RenderUtil.CustomStyled.draw(m, x, y, w, h, round, true, 7.5f);
        int pressColor = ColorUtil.getColor(255, (int) (40 * anim));
        RenderUtil.Rounded.roundedRect(m, x, y, w, h, pressColor, round);
        int textColor = ColorUtil.interpolateColor(0xFFFFFFFF, themeColor, anim * 0.6f);
        Fonts.SF_BOLD.drawCenter(m, text, x + w / 2f, y + h / 2f - fontSize / 2f, textColor, fontSize);
    }

    private void drawSpace(MatrixStack m, float x, float y, float w, float h, Round round,
                           KeyBinding key, int themeColor, float s) {
        float anim = (float) keyAnimations.get(key).getValue();
        RenderUtil.CustomStyled.draw(m, x, y, w, h, round, true, 7.5f);
        int pressColor = ColorUtil.getColor(255, (int) (40 * anim));
        RenderUtil.Rounded.roundedRect(m, x, y, w, h, pressColor, round);
        float bw = BAR_W * s;
        float bh = BAR_H * s;
        float br = BAR_RAD * s;
        float bx = x + (w - bw) / 2f;
        float by = y + (h - bh) / 2f;
        int barColor = ColorUtil.interpolateColor(ColorUtil.multAlpha(themeColor, 0.5f), themeColor, anim);
        RenderUtil.Rounded.roundedRect(m, bx, by, bw, bh, barColor, Round.of(2));
    }
}