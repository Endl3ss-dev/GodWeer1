/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 */
package ru.decide.screen.editor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import ru.decide.lang.Lang;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.Setting;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.screen.editor.EditorWidgets;
import ru.decide.screen.editor.Rect;
import ru.decide.utils.animation.satoshi.Animation;
import ru.decide.utils.animation.satoshi.Direction;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.MathUtil;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.Scissor;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public final class SettingsPanel {
    private Module module;
    private String title = "";
    private float x;
    private float y;
    private boolean placed;
    private Rect bounds = Rect.EMPTY;
    private Rect headerBounds = Rect.EMPTY;
    private Rect enableBounds = Rect.EMPTY;
    private final List<PanelRow> rows = new ArrayList<PanelRow>();
    private final Set<String> expandedModes = new HashSet<String>();
    private final HashMap<String, Animation> expandAnims = new HashMap();
    private float scroll;
    private float scrollTarget;
    private float maxScroll;
    private float bottomInset = 82.0f;
    private boolean showEnableRow = true;
    private boolean draggingSelf;
    private float dragOffsetX;
    private float dragOffsetY;
    private SliderSetting draggingSlider;
    private Rect draggingSliderBounds = Rect.EMPTY;
    private ColorSetting draggingColor;
    private int draggingColorBar;
    private Rect draggingColorBounds = Rect.EMPTY;

    private Animation expandAnim(String key) {
        return this.expandAnims.computeIfAbsent(key, k -> {
            EaseInOutQuad a = new EaseInOutQuad(300, 1.0);
            a.setDirection(Direction.BACKWARDS);
            return a;
        });
    }

    public Module module() {
        return this.module;
    }

    public Rect bounds() {
        return this.bounds;
    }

    public void setBottomInset(float bottomInset) {
        this.bottomInset = bottomInset;
    }

    public void setShowEnableRow(boolean showEnableRow) {
        this.showEnableRow = showEnableRow;
    }

    public void reset() {
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
        this.releaseDrag();
        this.closePickers();
    }

    public void unplace() {
        this.placed = false;
    }

    public boolean isDragging() {
        return this.draggingSelf || this.draggingSlider != null || this.draggingColor != null;
    }

    public void releaseDrag() {
        this.draggingSelf = false;
        this.draggingSlider = null;
        this.draggingColor = null;
    }

    public void closePickers() {
        for (PanelRow row : this.rows) {
            Setting<?> setting = row.setting;
            if (!(setting instanceof ColorSetting)) continue;
            ColorSetting colorSetting = (ColorSetting)setting;
            colorSetting.pickerOpen = false;
        }
        this.draggingColor = null;
    }

    public void layout(Module module, String title, float defaultX, float defaultY, float screenW, float screenH, EditorWidgets widgets) {
        this.scroll += (this.scrollTarget - this.scroll) * 0.2f;
        this.rows.clear();
        this.module = module;
        this.title = title;
        if (module == null) {
            this.bounds = Rect.EMPTY;
            this.enableBounds = Rect.EMPTY;
            this.headerBounds = Rect.EMPTY;
            return;
        }
        if (!this.placed) {
            this.x = defaultX;
            this.y = defaultY;
            this.placed = true;
        }
        this.x = MathUtil.clamp(this.x, 4.0f, Math.max(4.0f, screenW - 168.0f - 4.0f));
        this.y = MathUtil.clamp(this.y, 4.0f, Math.max(4.0f, screenH - 16.0f - 30.0f));
        float rowX = this.x + 6.0f;
        float rowW = 156.0f;
        float top = this.y + 5.0f + 16.0f + 3.0f;
        this.headerBounds = new Rect(rowX, this.y + 5.0f, rowW, 16.0f);
        float maxBottom = screenH - this.bottomInset;
        float viewH = Math.max(60.0f, maxBottom - top);
        float rowY = top - this.scroll;
        if (this.showEnableRow) {
            this.enableBounds = new Rect(rowX, rowY, rowW, 13.0f);
            rowY += 16.0f;
        } else {
            this.enableBounds = Rect.EMPTY;
        }
        for (Setting<?> setting : module.getSettings()) {
            ModeSetting mode;
            boolean supported;
            boolean bl = supported = setting instanceof BooleanSetting || setting instanceof SliderSetting || setting instanceof ModeSetting || setting instanceof ColorSetting || setting instanceof ButtonSetting || setting instanceof DelimiterSetting;
            if (!supported) continue;
            Animation appear = widgets.anim(module.getName() + ":" + setting.getConfigKey() + ":vis");
            appear.setDirection(setting.getVisible().get() != false ? Direction.FORWARDS : Direction.BACKWARDS);
            float vis = appear.getOutput();
            if (vis <= 0.01f) continue;
            PanelRow row = new PanelRow(setting, setting.getName());
            row.vis = vis;
            float height = 13.0f;
            if (setting instanceof SliderSetting) {
                height = 20.0f;
            } else if (setting instanceof DelimiterSetting) {
                height = 11.0f;
            } else if (setting instanceof ModeSetting) {
                mode = (ModeSetting)setting;
                boolean expanded = this.expandedModes.contains(mode.getName());
                Animation ea = this.expandAnim(module.getName() + ":" + mode.getName());
                ea.setDirection(expanded ? Direction.FORWARDS : Direction.BACKWARDS);
                float expandT = ea.getOutput();
                height = 13.0f + (2.0f + this.chipsHeight(mode.values, rowW - 12.0f) + 2.0f) * expandT;
            } else if (setting instanceof ColorSetting) {
                ColorSetting color = (ColorSetting)setting;
                if (color.pickerOpen) {
                    height = 40.0f;
                }
            }
            row.bounds = new Rect(rowX, rowY, rowW, height);
            if (setting instanceof ModeSetting) {
                mode = (ModeSetting)setting;
                Animation ea = this.expandAnim(module.getName() + ":" + mode.getName());
                ea.setDirection(this.expandedModes.contains(mode.getName()) ? Direction.FORWARDS : Direction.BACKWARDS);
                if (ea.getOutput() > 0.01f) {
                    this.layoutChips(row, mode, rowX + 6.0f, rowY + 13.0f + 2.0f, rowW - 12.0f);
                }
            }
            this.rows.add(row);
            rowY += (height + 3.0f) * vis;
        }
        float content = rowY + this.scroll - top;
        this.maxScroll = Math.max(0.0f, content - viewH);
        this.scrollTarget = MathUtil.clamp(this.scrollTarget, 0.0f, this.maxScroll);
        float panelH = 24.0f + Math.min(content, viewH) + 5.0f;
        this.bounds = new Rect(this.x, this.y, 168.0f, panelH);
    }

    private void layoutChips(PanelRow row, ModeSetting mode, float chipX, float chipY, float width) {
        row.chips.clear();
        Font font = Fonts.sf_regular;
        float px = 0.0f;
        float py = 0.0f;
        for (String value : mode.values) {
            float tw = font.getWidth(Lang.tr(value), 5.5f) + 7.0f;
            if (px + tw > width && px > 0.0f) {
                px = 0.0f;
                py += 10.5f;
            }
            row.chips.add(new Rect(chipX + px, chipY + py, tw, 9.0f));
            px += tw + 2.5f;
        }
    }

    private float chipsHeight(List<String> values, float width) {
        Font font = Fonts.sf_regular;
        float px = 0.0f;
        float py = 0.0f;
        for (String value : values) {
            float tw = font.getWidth(Lang.tr(value), 5.5f) + 7.0f;
            if (px + tw > width && px > 0.0f) {
                px = 0.0f;
                py += 10.5f;
            }
            px += tw + 2.5f;
        }
        return py + 9.0f;
    }

    public void draw(EditorWidgets widgets, float alpha) {
        if (this.module == null || this.bounds.width() <= 0.0f) {
            return;
        }
        Rect b = this.bounds;
        RenderUtil.Render2D.glow(b.x(), b.y(), b.width(), b.height() - 0.5f, ColorUtil.getColor(0, 0.15f * alpha), 8.0f, 15.0f, 1.0f);
        RenderUtil.Blur.blur(b.x(), b.y(), b.width(), b.height(), alpha, 8.0f, ColorUtil.multAlpha(ColorUtil.multDark(ColorUtil.background(), 0.6f), alpha));
        RenderUtil.Render2D.outline(b.x(), b.y(), b.width(), b.height(), 0.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * 0.35f), 8.0f);
        Rect header = this.headerBounds;
        widgets.drawRowBackground(header, widgets.rowHover(this.module.getName() + ":header", header), alpha);
        Fonts.category.draw("U", header.x() + 6.0f, header.y() + 5.2f, 6.5f, ColorUtil.multAlpha(ColorUtil.client(), alpha));
        Fonts.sf_regular.draw(this.title, header.x() + 17.0f, header.y() + 4.4f, 6.5f, ColorUtil.getColor(200, alpha));
        Scissor.enable(b.x(), b.y() + 5.0f + 16.0f + 3.0f, b.width(), b.height() - 24.0f - 5.0f, 2.0f);
        if (this.showEnableRow) {
            widgets.drawToggleRow(this.enableBounds, Lang.tr("\u0412\u043a\u043b/\u0412\u044b\u043a\u043b"), this.module.isEnabled(), this.module.getName() + ":enabled", alpha);
        }
        for (PanelRow row : this.rows) {
            this.drawRow(widgets, row, alpha);
        }
        Scissor.disable();
    }

    private void drawRow(EditorWidgets widgets, PanelRow row, float parentAlpha) {
        String key = this.module.getName() + ":" + row.label;
        Setting<?> setting = row.setting;
        Rect b = row.bounds;
        float alpha = parentAlpha * row.vis;
        if (alpha <= 0.01f) {
            return;
        }
        if (setting instanceof DelimiterSetting) {
            Font font = Fonts.sf_regular;
            String label = Lang.tr(row.label);
            float labelW = font.getWidth(label, 5.5f);
            font.draw(label, b.x() + 2.0f, b.y() + 4.0f, 5.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * 0.85f));
            RenderUtil.Render2D.rect(b.x() + labelW + 6.0f, b.y() + 5.75f, Math.max(0.0f, b.width() - labelW - 8.0f), 0.5f, ColorUtil.getColor(255, alpha * 0.12f), 0.25f);
            return;
        }
        if (setting instanceof BooleanSetting) {
            BooleanSetting booleanSetting = (BooleanSetting)setting;
            widgets.drawToggleRow(b, Lang.tr(row.label), booleanSetting.getValue(), key, alpha);
            return;
        }
        if (setting instanceof SliderSetting) {
            SliderSetting slider = (SliderSetting)setting;
            float hover = widgets.rowHover(key, b);
            widgets.drawRowBackground(b, hover, alpha);
            Font font = Fonts.sf_regular;
            font.draw(Lang.tr(row.label), b.x() + 6.0f, b.y() + 3.2f, 6.0f, ColorUtil.getColor(255, alpha * (0.5f + 0.5f * hover)));
            String value = String.valueOf((double)Math.round((double)slider.getValue().floatValue() * 100.0) / 100.0);
            font.draw(value, b.x() + b.width() - 6.0f - font.getWidth(value, 5.5f), b.y() + 3.5f, 5.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * (0.6f + 0.4f * hover)));
            float track = b.width() - 12.0f;
            float percent = class_3532.method_15363((float)((slider.getValue().floatValue() - slider.min) / (slider.max - slider.min)), (float)0.0f, (float)1.0f);
            float shown = widgets.smooth(key + ":pc", percent);
            RenderUtil.Render2D.rect(b.x() + 6.0f, b.y() + 13.0f, track, 2.5f, ColorUtil.getColor(0, alpha * 0.15f), 1.25f);
            RenderUtil.Render2D.rect(b.x() + 6.0f, b.y() + 13.0f, track * shown, 2.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * (0.5f + 0.5f * hover)), 1.25f);
            RenderUtil.Render2D.rect(b.x() + 6.0f + track * shown - 2.5f, b.y() + 11.75f, 5.0f, 5.0f, ColorUtil.getColor((int)(200.0f + 55.0f * hover), alpha), 5.0f);
            RenderUtil.Render2D.rect(b.x() + 6.0f + track * shown - 1.75f, b.y() + 12.5f, 3.5f, 3.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * (0.7f + 0.3f * hover)), 5.0f);
            return;
        }
        if (setting instanceof ModeSetting) {
            ModeSetting mode = (ModeSetting)setting;
            float hover = widgets.rowHover(key, b);
            boolean expanded = this.expandedModes.contains(mode.getName());
            Animation ea = this.expandAnim(this.module.getName() + ":" + mode.getName());
            ea.setDirection(expanded ? Direction.FORWARDS : Direction.BACKWARDS);
            float expandT = ea.getOutput();
            widgets.drawRowBackground(b, hover, alpha);
            Font font = Fonts.sf_regular;
            String current = Lang.tr(mode.getValue());
            font.drawFadingText(Lang.tr(row.label), b.x() + 6.0f, b.y() + 3.2f, b.width() - font.getWidth(current, 5.5f) - 16.0f, ColorUtil.getColor(255, alpha * (0.5f + 0.5f * hover)), 6.0f);
            String arrow = expanded ? "\u25b2" : "\u25bc";
            font.draw(arrow + " " + current, b.x() + b.width() - 6.0f - font.getWidth(arrow + " " + current, 5.5f), b.y() + 3.5f, 5.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * (0.6f + 0.4f * hover)));
            if (expandT > 0.01f) {
                for (int i = 0; i < row.chips.size(); ++i) {
                    Rect chip = row.chips.get(i);
                    String value = mode.values.get(i);
                    Animation anim = widgets.anim(key + ":chip:" + value);
                    anim.setDirection(value.equals(mode.getValue()) ? Direction.FORWARDS : Direction.BACKWARDS);
                    float selected = anim.getOutput();
                    RenderUtil.Render2D.rect(chip.x(), chip.y(), chip.width(), chip.height(), ColorUtil.overCol(ColorUtil.getColor(0, 0.25f * alpha * expandT), ColorUtil.replAlpha(ColorUtil.client(), alpha * (0.5f + 0.5f * hover) * expandT), selected), 2.5f);
                    font.drawCentered(Lang.tr(value), chip.centerX(), chip.y() + 1.4f, 5.5f, ColorUtil.getColor(255, alpha * (0.35f + 0.65f * selected) * (0.7f + 0.3f * hover) * expandT));
                }
            }
            return;
        }
        if (setting instanceof ColorSetting) {
            ColorSetting color = (ColorSetting)setting;
            float hover = widgets.rowHover(key, b);
            widgets.drawRowBackground(b, hover, alpha);
            Font font = Fonts.sf_regular;
            font.drawFadingText(Lang.tr(row.label), b.x() + 6.0f, b.y() + 3.2f, b.width() - 30.0f, ColorUtil.getColor(255, alpha * (0.5f + 0.5f * hover)), 6.0f);
            int argb = color.getValue();
            int r = (int)widgets.smooth(key + ":r", argb >> 16 & 0xFF);
            int g = (int)widgets.smooth(key + ":g", argb >> 8 & 0xFF);
            int bl = (int)widgets.smooth(key + ":b", argb & 0xFF);
            float px = b.x() + b.width() - 6.0f - 7.0f;
            float py = b.y() + 3.0f;
            RenderUtil.Render2D.rect(px, py, 7.0f, 7.0f, ColorUtil.getColor(r, g, bl, alpha), 7.0f);
            RenderUtil.Render2D.outline(px - 0.75f, py - 0.75f, 8.5f, 8.5f, 0.25f, ColorUtil.getColor(r, g, bl, alpha * (0.4f + 0.6f * hover)), 7.0f);
            RenderUtil.Render2D.glow(px, py, 7.0f, 7.0f, ColorUtil.getColor(r, g, bl, 0.1f * alpha), 7.0f, 6.0f, 1.0f);
            if (!color.pickerOpen) {
                return;
            }
            float[] hsb = Color.RGBtoHSB(argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF, null);
            float hue = widgets.smooth(key + ":h", hsb[0]);
            float sat = widgets.smooth(key + ":s", hsb[1]);
            float bri = widgets.smooth(key + ":v", hsb[2]);
            float[] smoothHsb = new float[]{hue, sat, bri};
            float barX = b.x() + 6.0f;
            float barW = b.width() - 12.0f;
            int segments = 16;
            float segment = barW / (float)segments;
            for (int bar = 0; bar < 3; ++bar) {
                float barY = b.y() + 13.0f + 2.0f + (float)bar * 10.0f;
                for (int i = 0; i < segments; ++i) {
                    float t0 = (float)i / (float)segments;
                    float t1 = (float)(i + 1) / (float)segments;
                    int c0 = switch (bar) {
                        case 0 -> Color.HSBtoRGB(t0, 1.0f, 1.0f);
                        case 1 -> Color.HSBtoRGB(hue, t0, bri);
                        default -> Color.HSBtoRGB(hue, sat, t0);
                    };
                    int c1 = switch (bar) {
                        case 0 -> Color.HSBtoRGB(t1, 1.0f, 1.0f);
                        case 1 -> Color.HSBtoRGB(hue, t1, bri);
                        default -> Color.HSBtoRGB(hue, sat, t1);
                    };
                    int a0 = ColorUtil.replAlpha(c0, alpha);
                    int a1 = ColorUtil.replAlpha(c1, alpha);
                    RenderUtil.Render2D.gradientRect(barX + (float)i * segment, barY, segment + (i == segments - 1 ? 0.0f : 0.5f), 3.5f, new int[]{a0, a1, a1, a0}, i == 0 ? 2.0f : 0.0f, i == segments - 1 ? 2.0f : 0.0f, i == segments - 1 ? 2.0f : 0.0f, i == 0 ? 2.0f : 0.0f);
                }
                float knobX = barX + barW * smoothHsb[bar];
                int knobColor = bar == 0 ? Color.HSBtoRGB(hue, 1.0f, 1.0f) : Color.HSBtoRGB(hue, sat, bri);
                RenderUtil.Render2D.rect(knobX - 2.5f, barY - 0.75f, 5.0f, 5.0f, ColorUtil.getColor(255, alpha), 5.0f);
                RenderUtil.Render2D.rect(knobX - 1.75f, barY, 3.5f, 3.5f, ColorUtil.replAlpha(knobColor, alpha), 5.0f);
            }
            return;
        }
        if (setting instanceof ButtonSetting) {
            ButtonSetting buttonSetting = (ButtonSetting)setting;
            float hover = widgets.rowHover(key, b);
            buttonSetting.pressAnim.update();
            float press = buttonSetting.pressAnim.get();
            float accent = Math.max(hover, press);
            RenderUtil.Render2D.glow(b.x(), b.y(), b.width(), b.height(), ColorUtil.getColor(0, 0.06f * alpha), 5.0f, 7.0f, 1.0f);
            RenderUtil.Render2D.rect(b.x(), b.y(), b.width(), b.height(), ColorUtil.overCol(ColorUtil.getColor(40, 0.15f * alpha), ColorUtil.replAlpha(ColorUtil.client(), 0.25f * alpha), accent), 5.0f);
            RenderUtil.Render2D.outline(b.x(), b.y(), b.width(), b.height(), 0.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * accent), 4.0f);
            Fonts.sf_regular.drawCentered(Lang.tr(row.label), b.centerX(), b.y() + 3.2f, 6.0f, ColorUtil.getColor(255, alpha * (0.5f + 0.5f * hover)));
        }
    }

    public boolean handleClick(float mouseX, float mouseY, int button) {
        if (this.module == null || !this.bounds.contains(mouseX, mouseY)) {
            return false;
        }
        if (this.headerBounds.contains(mouseX, mouseY)) {
            if (button == 0) {
                this.draggingSelf = true;
                this.dragOffsetX = mouseX - this.x;
                this.dragOffsetY = mouseY - this.y;
            }
            return true;
        }
        if (this.showEnableRow && this.enableBounds.contains(mouseX, mouseY)) {
            if (button == 0) {
                this.module.setEnabled(!this.module.isEnabled(), false);
            }
            return true;
        }
        for (PanelRow row : this.rows) {
            if (!row.bounds.contains(mouseX, mouseY)) continue;
            Setting<?> setting = row.setting;
            if (setting instanceof BooleanSetting) {
                BooleanSetting booleanSetting = (BooleanSetting)setting;
                if (button == 0) {
                    booleanSetting.set(booleanSetting.getValue() == false);
                }
            } else if (setting instanceof SliderSetting) {
                SliderSetting slider = (SliderSetting)setting;
                if (button == 0) {
                    this.draggingSlider = slider;
                    this.draggingSliderBounds = row.bounds;
                    this.applySlider(slider, row.bounds, mouseX);
                }
            } else if (setting instanceof ModeSetting) {
                ModeSetting mode = (ModeSetting)setting;
                if (button == 0) {
                    if (mouseY <= row.bounds.y() + 13.0f) {
                        String key = mode.getName();
                        if (this.expandedModes.contains(key)) {
                            this.expandedModes.remove(key);
                        } else {
                            this.expandedModes.add(key);
                        }
                    } else {
                        for (int i = 0; i < row.chips.size(); ++i) {
                            if (!row.chips.get(i).contains(mouseX, mouseY)) continue;
                            mode.set(mode.values.get(i));
                            break;
                        }
                    }
                }
            } else if (setting instanceof ColorSetting) {
                ColorSetting color = (ColorSetting)setting;
                if (button == 0) {
                    if (mouseY <= row.bounds.y() + 13.0f) {
                        color.pickerOpen = !color.pickerOpen;
                    } else if (color.pickerOpen) {
                        for (int bar = 0; bar < 3; ++bar) {
                            float barY = row.bounds.y() + 13.0f + 2.0f + (float)bar * 10.0f;
                            if (!MathUtil.isHovered(mouseX, mouseY, row.bounds.x() + 6.0f, barY - 2.5f, row.bounds.width() - 12.0f, 9.0f)) continue;
                            this.draggingColor = color;
                            this.draggingColorBar = bar;
                            this.draggingColorBounds = row.bounds;
                            this.applyColorDrag(mouseX);
                        }
                    }
                }
            } else if (setting instanceof ButtonSetting) {
                ButtonSetting buttonSetting = (ButtonSetting)setting;
                if (button == 0) {
                    buttonSetting.press();
                }
            }
            return true;
        }
        return true;
    }

    public boolean handleScroll(float mouseX, float mouseY, double verticalAmount) {
        if (this.module == null || !this.bounds.contains(mouseX, mouseY)) {
            return false;
        }
        this.scrollTarget = MathUtil.clamp((float)((double)this.scrollTarget - verticalAmount * 18.0), 0.0f, this.maxScroll);
        return true;
    }

    public boolean rawMouseMoved(float mouseX, float mouseY) {
        if (this.draggingSelf) {
            this.x = mouseX - this.dragOffsetX;
            this.y = mouseY - this.dragOffsetY;
            return true;
        }
        if (this.draggingSlider != null) {
            this.applySlider(this.draggingSlider, this.draggingSliderBounds, mouseX);
            return true;
        }
        if (this.draggingColor != null) {
            this.applyColorDrag(mouseX);
            return true;
        }
        return false;
    }

    private void applySlider(SliderSetting setting, Rect sliderBounds, float mouseX) {
        float trackX = sliderBounds.x() + 6.0f;
        float trackW = Math.max(1.0f, sliderBounds.width() - 12.0f);
        float progress = class_3532.method_15363((float)((mouseX - trackX) / trackW), (float)0.0f, (float)1.0f);
        setting.set(Float.valueOf(class_3532.method_15363((float)MathUtil.round(setting.min + (setting.max - setting.min) * progress, setting.increment), (float)setting.min, (float)setting.max)));
    }

    private void applyColorDrag(float mouseX) {
        if (this.draggingColor == null) {
            return;
        }
        int argb = this.draggingColor.getValue();
        float[] hsb = Color.RGBtoHSB(argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF, null);
        float t = MathUtil.clamp((mouseX - (this.draggingColorBounds.x() + 6.0f)) / Math.max(1.0f, this.draggingColorBounds.width() - 12.0f), 0.0f, 1.0f);
        float[] next = new float[]{hsb[0], hsb[1], hsb[2]};
        next[this.draggingColorBar] = t;
        int rgb = Color.HSBtoRGB(next[0], next[1], next[2]);
        this.draggingColor.set(argb & 0xFF000000 | rgb & 0xFFFFFF);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class PanelRow {
        private final Setting<?> setting;
        private final String label;
        private final List<Rect> chips = new ArrayList<Rect>();
        private Rect bounds = Rect.EMPTY;
        private float vis = 1.0f;

        private PanelRow(Setting<?> setting, String label) {
            this.setting = setting;
            this.label = label;
        }
    }
}

