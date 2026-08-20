package org.privates.client.screen.figmagui;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.screen.newgui.Builder;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;

@Getter
@Setter
public class FigmaModule extends Builder {

    private final FigmaPanel panel;
    private final org.privates.client.managers.module.Module module;
    private final ArrayList<FigmaSetting> settings = new ArrayList<>();

    private float delta;
    private float hoverV;
    private float alpha = 1f;
    private float expandV;
    private boolean expanded = false;
    private boolean input = false;
    private boolean lockExpanded = false;
    private float nameScrollX = 0f;
    private boolean nameScrollForward = true;
    private long nameScrollTick = 0L;
    private long nameScrollPauseUntil = 0L;
    private float enableV = 0f;
    private float shakeV = 0f;
    private long shakeStartMs = 0L;

    private static final float ROW_H_F = 26f;

    public FigmaModule(FigmaPanel panel, org.privates.client.managers.module.Module module, float x, float y) {
        this.panel = panel;
        this.module = module;
        this.x = x;
        this.y = y;
        float fs = panel.getGui().fs();
        this.width = panel.getWidth() - 14f * fs;
        this.height = ROW_H_F * fs;
        buildSettings();
    }

    private void buildSettings() {
        settings.clear();
        for (org.privates.client.managers.module.settings.Setting setting : module.getSettings()) {
            settings.add(new FigmaSetting(this, setting, x, y));
        }
    }

    private float expandedHeight(float fs) {
        float content = 0f;
        for (FigmaSetting setting : settings) {
            content += setting.getAnimatedHeight(fs) + 2f * fs;
        }
        if (content > 0f) {
            content -= 2f * fs;
        }
        return 12f * fs + Math.max(content, 0f) + 4f * fs;
    }

    private float currentHeight(float fs) {
        float base = ROW_H_F * fs;
        if (expandV <= 0.01f) {
            return base;
        }
        return base + 2f * fs + expandedHeight(fs) * expandV;
    }

    @Override
    public void render(MatrixStack ms, float mouseX, float mouseY) {
        float fs = panel.getGui().fs();
        Theme theme = Theme.getInstance();
        float rowH = ROW_H_F * fs;
        float rowY = y + delta;

        if (lockExpanded) {
            expanded = true;
        }

        hoverV = Mathf.fast(hoverV, isHovered(mouseX, mouseY, x, rowY, width, rowH) ? 1f : 0f, 12f);
        expandV = Mathf.fast(expandV, expanded ? 1f : 0f, 14f);
        enableV = Mathf.fast(enableV, module.isEnabled() ? 1f : 0f, 12f);
        height = currentHeight(fs);
        float shakeTarget = Math.max(0f, 1f - (System.currentTimeMillis() - shakeStartMs) / 280f);
        shakeV = Mathf.fast(shakeV, shakeTarget, 9f);
        float shakeOffset = (float) Math.sin((System.currentTimeMillis() - shakeStartMs) * 0.11f) * 2.2f * fs * shakeV;

        float drawX = x + shakeOffset;
        int rowBg = ColorUtil.getColor(0, 0, 0, (int) (0.65f * 255 * alpha));
        RenderUtil.Rounded.roundedRect(ms, drawX, rowY, width, rowH, rowBg, Round.of(6f * fs));
        if (enableV > 0.01f) {
            float activeW = width * enableV;
            float activeX = drawX + width / 2f - activeW / 2f;
            RenderUtil.Rounded.roundedRect(ms, activeX, rowY, activeW, rowH, ColorUtil.multAlpha(theme.clientColor(), 0.11f * alpha), Round.of(6f * fs));
        }
        if (hoverV > 0.01f) {
            RenderUtil.Rounded.roundedRect(ms, drawX, rowY, width, rowH, ColorUtil.multAlpha(theme.clientColor(), 0.08f * hoverV * alpha), Round.of(6f * fs));
        }
        if (settings.isEmpty() && shakeV > 0.08f) {
            float warnSize = 7.5f * fs;
            String warn = "[ Неа нельзя ]";
            float warnW = Fonts.SF_BOLD.getWidth(warn, warnSize);
            Fonts.SF_BOLD.draw(ms, warn, drawX + width - warnW - 14f * fs, rowY + rowH / 2f - warnSize / 2f, ColorUtil.multAlpha(ColorUtil.getColor(255, 125, 125), shakeV * alpha), warnSize);
        }

        float iconSize = 12f * fs;
        float iconX = drawX + 3f * fs;
        float iconY = rowY + (rowH - iconSize) / 2f;
        int iconColor = module.isEnabled() ? ColorUtil.multAlpha(theme.clientColor(), alpha) : ColorUtil.multAlpha(theme.clientColor(), 0.4f * alpha);
        drawCategoryIcon(ms, iconX, iconY, iconColor, iconSize);

        float dotsSize = 9f * fs;
        float dotsW = Fonts.SF_BOLD.getWidth("...", dotsSize);
        float dotsX = drawX + width - dotsW - 4f * fs;
        Fonts.SF_BOLD.draw(ms, "...", dotsX, rowY + (rowH - dotsSize) / 2f, ColorUtil.multAlpha(theme.clientColor(), alpha), dotsSize);

        float textSize = 12f * fs;
        float textX = drawX + 21f * fs;
        float textY = rowY + (rowH - textSize) / 2f - 0.5f * fs;
        String bindPart = input ? " [...]" : (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) && module.getKey() > 0 ? " [" + Keyboard.keyName(module.getKey()) + "]" : "");
        String text = module.getName() + bindPart;
        int textColor = module.isEnabled() ? ColorUtil.multAlpha(theme.enabledColor(), alpha) : ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 160), alpha);
        drawScrollingText(ms, text, textX, textY, (dotsX - 4f * fs) - textX, textColor, textSize, fs, rowY, rowH);

        if (expandV > 0.01f) {
            float blockX = drawX;
            float blockY = rowY + rowH + 2f * fs;
            float blockH = expandedHeight(fs) * expandV + 0.8f * fs;
            RenderUtil.Rounded.roundedRect(ms, blockX, blockY, width, blockH, ColorUtil.getColor(0, 0, 0, (int) (0.65f * 255 * alpha)), Round.of(6f * fs));

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(blockX, blockY, width, blockH);
            float sy = blockY + 6f * fs;
            for (FigmaSetting setting : settings) {
                float sh = setting.getAnimatedHeight(fs);
                if (sh < 0.4f * fs) {
                    continue;
                }
                setting.setX(drawX + 4f * fs);
                setting.setY(sy);
                setting.setWidth(width - 8f * fs);
                setting.setAlphaV(alpha * expandV);
                setting.render(ms, mouseX, mouseY);
                sy += sh + 2f * fs;
            }
            ScissorExp.unset();
            ScissorExp.pop();
        }
    }

    private void drawCategoryIcon(MatrixStack ms, float iconX, float iconY, int iconColor, float iconSize) {
        Category category = module.getCategory();
        if (category == Category.COMBAT) {
            Fonts.iconf.draw(ms, "c", iconX, iconY, iconColor, iconSize);
        } else if (category == Category.MOVEMENT) {
            Fonts.iconf.draw(ms, "e", iconX, iconY, iconColor, iconSize);
        } else {
            Fonts.CLICKGUI.draw(ms, category.getIcon(), iconX, iconY, iconColor, iconSize);
        }
    }

    private void drawScrollingText(MatrixStack ms, String text, float x, float y, float availableWidth, int color, float size, float fs, float rowY, float rowH) {
        float textWidth = Fonts.SF_BOLD.getWidth(text, size);
        if (textWidth > availableWidth) {
            long now = System.currentTimeMillis();
            if (now > nameScrollPauseUntil && now - nameScrollTick > 16L) {
                float speed = 0.65f * fs;
                float min = -(textWidth - availableWidth);
                if (nameScrollForward) {
                    nameScrollX -= speed;
                    if (nameScrollX <= min) {
                        nameScrollX = min;
                        nameScrollForward = false;
                        nameScrollPauseUntil = now + 900L;
                    }
                } else {
                    nameScrollX += speed;
                    if (nameScrollX >= 0f) {
                        nameScrollX = 0f;
                        nameScrollForward = true;
                        nameScrollPauseUntil = now + 900L;
                    }
                }
                nameScrollTick = now;
            }
        } else {
            nameScrollX = 0f;
        }

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, rowY, availableWidth, rowH);
        Fonts.SF_BOLD.draw(ms, text, x + nameScrollX, y, color, size);
        ScissorExp.unset();
        ScissorExp.pop();
    }

    @Override
    public void mouse(float mx, float my, int btn, int act) {
        float fs = panel.getGui().fs();
        float rowH = ROW_H_F * fs;
        float rowY = y + delta;

        if (act == GLFW.GLFW_PRESS && isHovered(mx, my, x, rowY, width, rowH)) {
            if (btn == 0) {
                module.toggle();
            } else if (btn == 1 && !lockExpanded) {
                if (settings.isEmpty()) {
                    shakeStartMs = System.currentTimeMillis();
                } else {
                    expanded = !expanded;
                }
            } else if (btn == 2) {
                input = !input;
            }
        }

        if (expandV > 0.01f) {
            float sy = rowY + rowH + 2f * fs + 4f * fs;
            for (FigmaSetting setting : settings) {
                float sh = setting.getAnimatedHeight(fs);
                if (sh < 0.4f * fs) {
                    continue;
                }
                setting.setX(x + 4f * fs);
                setting.setY(sy);
                setting.setWidth(width - 8f * fs);
                setting.mouse(mx, my, btn, act);
                sy += sh + 2f * fs;
            }
        }
    }

    @Override
    public void key(int key) {
        if (input) {
            module.setKey(key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_DELETE ? -1 : key);
            input = false;
        }
        if (expanded || lockExpanded) {
            for (FigmaSetting setting : settings) {
                setting.key(key);
            }
        }
    }

    @Override
    public void charTyped(char typed) {
        if (expanded || lockExpanded) {
            for (FigmaSetting setting : settings) {
                setting.charTyped(typed);
            }
        }
    }

    public boolean isAnyBindAwaiting() {
        for (FigmaSetting setting : settings) {
            if (setting.isInputBind()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnySettingInputActive() {
        for (FigmaSetting setting : settings) {
            if (setting.isInputBind() || setting.isInputString()) {
                return true;
            }
        }
        return false;
    }
}
