package tech.lightclient.ui.gui.elements;

import tech.lightclient.client.utils.render.CursorHelper;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.color.ColorUtils;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.api.settings.impl.ColorSetting;
import org.lwjgl.glfw.GLFW;

public final class CsColorComponent extends CsSettingComponent<ColorSetting> {
    private final SimpleLinearAnimation pressAnim = new SimpleLinearAnimation(360);
    private final SimpleLinearAnimation colorChangeAnim = new SimpleLinearAnimation(280);
    private int lastColor;
    private long lastChangeMs = 0L;
    private long pressStart = 0L;

    CsColorComponent(ColorSetting setting, float width) {
        super(setting, width, 49f);
        pressAnim.setEasing(Easings.EASE_OUT_CUBIC);
        colorChangeAnim.setEasing(Easings.EASE_OUT_CUBIC);
        lastColor = setting.get();
    }

    @Override
    public void draw(Renderer2D render, float x, float y, int mouseX, int mouseY, int alpha) {
        float rowX = x + ROW_PAD_X;
        float rowW = width - ROW_PAD_X * 2f;
        Chip chip = layoutChip(x, y);

        int color = setting.get();
        if (color != lastColor) {
            lastColor = color;
            lastChangeMs = System.currentTimeMillis();
            colorChangeAnim.setDuration(80);
            colorChangeAnim.show();
            colorChangeAnim.setDuration(280);
        }
        if (System.currentTimeMillis() - lastChangeMs > 120L) colorChangeAnim.hide();
        if (System.currentTimeMillis() - pressStart > 160L) pressAnim.hide();

        int rgb = color & 0xFFFFFF;
        String hex = String.format("#%06X", rgb);
        float chipPad = 10f;
        float chipSize = 12f;
        boolean chipHov = hovered(mouseX, mouseY, chip.x(), chip.y(), chip.w(), chip.h());
        if (chipHov) hoverAnimation.show();
        else hoverAnimation.hide();

        drawAnimatedSettingText(render, rowX, y + 11.5f, rowW, 13.5f,
                setting.getName(), withAlpha(COLOR_NAME, alpha));

        if (chipHov) CursorHelper.setHand();

        float hoverP = hoverAnimation.getProgress();
        float pressP = pressAnim.getProgress();
        float changeP = colorChangeAnim.getProgress();
        float scaleBoost = 1f + 0.05f * hoverP - 0.06f * pressP;
        float ccx = chip.x() + chip.w() * 0.5f;
        float ccy = chip.y() + chip.h() * 0.5f;
        float cw = chip.w() * scaleBoost;
        float ch = chip.h() * scaleBoost;
        float cx = ccx - cw * 0.5f;
        float cy = ccy - ch * 0.5f;

        if (changeP > 0.05f) {
            int glow = ColorUtils.injectAlpha(color, (int) (alpha * 0.35f * changeP));
            float glowRadius = Math.max(0f, ch * 0.5f - 2f) + 3f;
            render.rect(cx - 3f, cy - 3f, cw + 6f, ch + 6f, glowRadius, glow);
        }

        float chipRadius = 7f;
        int idleBg = withAlpha(0xFFFFFF, (int) (alpha * 0.10f));
        int chipBg = blend(idleBg, ColorUtils.injectAlpha(color, Math.min(255, (int) (alpha * 0.95f))), 0.78f + 0.22f * hoverP);
        render.rect(cx, cy, cw, ch, chipRadius, chipBg);
        int chipBorder = withAlpha(0xFFFFFF, (int) (alpha * (0.18f + 0.22f * hoverP)));
        render.rectOutline(cx, cy, cw, ch, chipRadius, chipBorder, 1f);

        float dotR = ch * 0.3f;
        float dotX = cx + chipPad - 1f + dotR;
        float dotY = cy + ch * 0.5f;
        render.circle(dotX, dotY, dotR + changeP * 1.5f, 0f, 1f, withAlpha(0xFFFFFF, (int) (alpha * 0.9f)));
        render.circle(dotX, dotY, dotR - 1.4f, 0f, 1f, ColorUtils.injectAlpha(color, alpha));

        float hexX = dotX + dotR + 7f;
        int textCol = textOnColor(rgb, alpha);
        render.text(FontRegistry.DELTA_CLICKGUI, hexX, centeredTextBaseline(cy, ch, chipSize),
                chipSize, hex, textCol);
    }

    private int textOnColor(int rgb, int alpha) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        float lum = (0.299f * r + 0.587f * g + 0.114f * b) / 255f;
        return lum > 0.6f ? withAlpha(0x101218, alpha) : withAlpha(0xFFFFFF, alpha);
    }

    @Override
    public void mouseClicked(double mx, double my, int button, float x, float y) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return;
        float rowX = x + ROW_PAD_X;
        float rowW = width - ROW_PAD_X * 2f;
        Chip chip = layoutChip(x, y);

        if (hovered(mx, my, chip.x(), chip.y(), chip.w(), chip.h())) {
            CsColorPicker.open(setting, rowX, chip.y() + chip.h() + 4f, rowW);
            pressAnim.setDuration(160);
            pressAnim.show();
            pressAnim.setDuration(360);
            pressStart = System.currentTimeMillis();
        }
    }

    private Chip layoutChip(float x, float y) {
        float rowX = x + ROW_PAD_X;
        float rowW = width - ROW_PAD_X * 2f;
        return new Chip(rowX, y + 22f, rowW, 22f);
    }
}
