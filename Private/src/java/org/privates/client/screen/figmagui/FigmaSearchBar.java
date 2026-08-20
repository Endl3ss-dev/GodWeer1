package org.privates.client.screen.figmagui;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import static org.privates.client.api.interfaces.IMinecraft.mc;

@Getter
@Setter
public class FigmaSearchBar {

    private final FigmaGuiScreen parent;
    private float x;
    private float y;
    private float width;
    private float height;
    private String text = "";
    private float cursorTimer = 0f;
    private boolean focused = false;
    private float focusV = 0f;
    private String lastApplied = "";

    private static final float BAR_W_F = 101f;
    private static final float BAR_H_F = 26f;
    private static final float BAR_R_F = 6f;
    private static final float ICON_W_F = 26f;

    public FigmaSearchBar(FigmaGuiScreen parent) {
        this.parent = parent;
    }

    public void init() {
        text = "";
        focused = false;
        focusV = 0f;
        cursorTimer = 0f;
        lastApplied = "";
        float fs = parent.fs();
        width = BAR_W_F * fs;
        height = BAR_H_F * fs;
        x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
        y = mc.getMainWindow().getScaledHeight() / 2f + parent.getPanelHeight() / 2f + 8f * fs;
        applyIfChanged();
    }

    public void render(MatrixStack ms, int mouseX, int mouseY, float alpha) {
        cursorTimer += 0.05f;
        focusV = Mathf.fast(focusV, focused ? 1f : 0f, 14f);
        float fs = parent.fs();
        Theme theme = Theme.getInstance();

        int barBg = ColorUtil.getColor(0, 0, 0, (int) (0.65f * 255 * alpha));
        RenderUtil.Rounded.roundedRect(ms, x, y, width, height, barBg, Round.of(BAR_R_F * fs));
        if (focusV > 0.01f) {
            RenderUtil.Rounded.roundedOutline(ms, x, y, width, height, fs, ColorUtil.multAlpha(theme.clientColor(), focusV * alpha), Round.of(BAR_R_F * fs));
        }

        float iconBoxW = ICON_W_F * fs;
        RenderUtil.Rounded.roundedRect(ms, x, y, iconBoxW, height, barBg, Round.of(BAR_R_F * fs));
        float iconSize = 10f * fs;
        Fonts.icon.draw(ms, "G", x + (iconBoxW - iconSize) / 2f, y + (height - iconSize) / 2f, ColorUtil.multAlpha(theme.clientColor(), alpha), iconSize);

        float textSize = 12f * fs;
        float textX = x + iconBoxW + (36f - 26f) * fs;
        float textY = y + (height - textSize) / 2f - 0.5f;
        if (text.isEmpty() && !focused) {
            Fonts.SF_BOLD.draw(ms, "Search..", textX, textY, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 180), alpha), textSize);
        } else {
            Fonts.SF_BOLD.draw(ms, text, textX, textY, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha), textSize);
            if (focused && ((int) (cursorTimer * 2)) % 2 == 0) {
                float cw = Fonts.SF_BOLD.getWidth(text, textSize);
                RenderUtil.Rounded.roundedRect(ms, textX + cw + fs, textY, fs, textSize, ColorUtil.multAlpha(theme.clientColor(), alpha), Round.of(fs / 2f));
            }
        }
    }

    private void applyIfChanged() {
        if (text.equals(lastApplied)) {
            return;
        }
        for (FigmaPanel panel : parent.getPanels()) {
            panel.onSearchChanged(text);
        }
        lastApplied = text;
    }

    public void mouseClicked(int mx, int my, int btn) {
        if (parent.isAnyBindAwaiting()) {
            focused = false;
            return;
        }
        boolean hit = mx >= x && mx <= x + width && my >= y && my <= y + height;
        if (hit && btn == 0) {
            focused = true;
        } else if (!hit && btn == 0) {
            focused = false;
        }
    }

    public void keyPressed(int key) {
        if (!focused) {
            return;
        }
        if (parent.isAnyBindAwaiting()) {
            focused = false;
            return;
        }
        if (key == GLFW.GLFW_KEY_BACKSPACE) {
            if (!text.isEmpty()) {
                text = text.substring(0, text.length() - 1);
                applyIfChanged();
            }
        } else if (key == GLFW.GLFW_KEY_ESCAPE) {
            focused = false;
            text = "";
            applyIfChanged();
        }
    }

    public void charTyped(char c) {
        if (!focused) {
            return;
        }
        if (parent.isAnyBindAwaiting()) {
            focused = false;
            return;
        }
        if (c >= 32 && c != 127) {
            if (text.isEmpty() && parent.isSearchInputBlocked()) {
                parent.collapseAllModules();
            } else if (parent.isSearchInputBlocked()) {
                return;
            }
            text += c;
            applyIfChanged();
        }
    }
}
