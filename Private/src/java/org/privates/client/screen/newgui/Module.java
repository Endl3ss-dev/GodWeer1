package org.privates.client.screen.newgui;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.annotations.*;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.FlatGui;
import org.privates.client.managers.module.impl.client.Theme;
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
public class Module extends Builder implements IMinecraft {

    private final Panel panel;
    private final org.privates.client.managers.module.Module module;
    private final ArrayList<Setting> settings = new ArrayList<>();

    private float delta;
    private float hoverV, toggleV, selectedV;
    private float settingsDelta, settingsDeltaV;
    private float iconOffsetV;
    private float alpha = 1f;

    private float descriptionWidthAnim = 0f;
    private float descriptionAlphaAnim = 0f;
    private long hoverStartTime = 0L;
    private boolean selected = false;
    private boolean input = false;

    private float highlightAnim = 0f;
    private boolean isHighlighted = false;

    public Module(Panel panel, org.privates.client.managers.module.Module module, float x, float y) {
        this.panel = panel;
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = panel.getWidth() - 6 * panel.getGui().getResolutionScale();
        float sScale = panel.getGui().getResolutionScale();
        this.height = 14 * sScale;

        float offsetY = mc.getMainWindow().getScaledHeight() / 2f - 170 * sScale;
        for (org.privates.client.managers.module.settings.Setting s : module.getSettings()) {
            Setting setting = new Setting(this, s, mc.getMainWindow().getScaledWidth() / 2f - 135 * sScale, offsetY);
            settings.add(setting);
            offsetY += setting.getHeight() + 3 * sScale;
        }
    }

    public ModuleInfo getModuleInfo() {
        return this.getClass().getAnnotation(ModuleInfo.class);
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        float sScale = panel.getGui().getResolutionScale();
        final float baseHeight = 16f * sScale;
        final float yExpansion = baseHeight * 0.05f;
        final float renderY = y + delta - yExpansion - 1 * sScale;
        final float renderHeight = baseHeight + yExpansion * 2;

        hoverV = Mathf.fast(hoverV, isHovered(mouseX, mouseY, x, renderY, width, renderHeight) ? 1 : 0, 12);
        toggleV = Mathf.fast(toggleV, module.isEnabled() ? 1 : 0, 12);
        selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 16);
        settingsDeltaV = Mathf.fast(settingsDeltaV, settingsDelta, 16);
        iconOffsetV = Mathf.fast(iconOffsetV, module.isEnabled() ? 1 : 0, 14);
        highlightAnim = Mathf.fast(highlightAnim, isHighlighted ? 1 : 0, 10);

        float settingsHeight = 0;
        if (FlatGui.getInstance().settingsDisplay().is("Под модулем")) {
            for (Setting s : settings) {
                if ((boolean) s.getSetting().getVisible().get()) {
                    settingsHeight += s.getHeight() + 3 * sScale;
                }
            }
        }
        float targetHeight = baseHeight + (selected ? settingsHeight + 3 * sScale : 0);
        height = Mathf.fast(height, targetHeight, 16);

        Theme theme = Theme.getInstance();
        float adaptiveModuleTextSize = panel.getGui().getAdaptiveModuleTextSize();

        int originalColor = theme.secondaryColor();
        int originalAlpha = (originalColor >> 24) & 0xFF;
        int newAlpha = Math.max(0, originalAlpha - 1000);
        int backgroundColor = (originalColor & 0x00FFFFFF) | (newAlpha << 24);
        backgroundColor = ColorUtil.multAlpha(backgroundColor, alpha);

        if (ColorUtil.alpha(backgroundColor) >= .25f)
            RenderUtil.Rounded.smooth(stack, x, y + delta, width, 14 * sScale, backgroundColor, Round.of(4 * sScale));

        if (highlightAnim > 0.01f) {
            int highlightColor = ColorUtil.multAlpha(theme.clientColor(), 0.3f * highlightAnim * alpha);
            RenderUtil.Rounded.roundedRect(stack, x, y + delta, width, 14 * sScale, highlightColor, Round.of(4 * sScale));

            int pulseColor = ColorUtil.multAlpha(theme.clientColor(), 0.5f * highlightAnim * alpha);
            float pulseWidth = 2f * sScale * highlightAnim;
            RenderUtil.Rounded.roundedOutline(stack, x, renderY, width, renderHeight, pulseWidth, pulseColor, Round.of(5 * sScale));
        }

        if (FlatGui.getInstance().moduleOutline().getValue()) {
            float outlineW = FlatGui.getInstance().outlineWidth().getValue();
            float outlineA = FlatGui.getInstance().outlineOpacity().getValue();
            int outlineColor = ColorUtil.multAlpha(theme.clientColor(), outlineA * alpha);
            RenderUtil.Rounded.roundedOutline(stack, x, renderY, width, renderHeight, outlineW, outlineColor, Round.of(5 * sScale));
        }

        if (hoverV >= .1f)
            RenderUtil.Rounded.roundedOutline(stack, x, renderY, width, renderHeight, hoverV,
                    ColorUtil.overCol(ColorUtil.multAlpha(theme.clientColor(), 0), ColorUtil.multAlpha(theme.clientColor(), .3f * alpha), hoverV), Round.of(2 * sScale));

        String text = module.getName() + (input ? " [...]" : (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) && module.getKey() > 0 ? " [" + Keyboard.keyName(module.getKey()) + "]" : ""));

        String categoryIcon = module.getCategory().getIcon();
        float iconSize = adaptiveModuleTextSize * 1.15f;
        float iconWidth = Fonts.CLICKGUI.getWidth(categoryIcon, iconSize);
        float iconPadding = 4f * sScale;

        float iconXOffset, textXOffset, renderIconOffsetV;
        int textColor, iconColor;

        if (panel.isThemePanel()) {
            iconXOffset = 0;
            textXOffset = iconWidth + iconPadding;
            renderIconOffsetV = 1.0f;
            textColor = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha);
            iconColor = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha);
        } else {
            iconXOffset = -3f * sScale * (1f - iconOffsetV);
            textXOffset = (iconWidth + iconPadding) * iconOffsetV;
            renderIconOffsetV = iconOffsetV;

            int baseTextColor = ColorUtil.overCol(theme.disabledColor(), ColorUtil.multAlpha(theme.enabledColor(), 0.8f), toggleV);
            if (highlightAnim > 0.01f) {
                textColor = ColorUtil.multAlpha(ColorUtil.interpolateColor(baseTextColor, theme.clientColor(), highlightAnim * 0.6f), alpha);
            } else {
                textColor = ColorUtil.multAlpha(baseTextColor, alpha);
            }

            iconColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.multAlpha(theme.disabledColor(), 1.2f * (1f - iconOffsetV)),
                    ColorUtil.multAlpha(theme.enabledColor(), 2.5f * iconOffsetV),
                    toggleV), alpha);
        }

        boolean isCentered = FlatGui.getInstance().textAlign().is("Центрично");
        float textWidth = Fonts.SF_BOLD.getWidth(text, adaptiveModuleTextSize);
        float textX;
        float iconX;
        float textY = y + delta + 7 * sScale - Fonts.SF_BOLD.getHeight(text, adaptiveModuleTextSize) / 1.5f;

        if (isCentered) {
            float totalContentWidth = textWidth + (renderIconOffsetV > 0.01f ? textXOffset : 0);
            float centerX = x + width / 2f - totalContentWidth / 2f;

            if (renderIconOffsetV > 0.01f) {
                iconX = centerX + iconXOffset;
                textX = centerX + textXOffset;
            } else {
                textX = x + width / 2f - textWidth / 2f;
                iconX = 0;
            }
        } else {
            textX = x + 4 * sScale + textXOffset;
            iconX = x + 4 * sScale + iconXOffset;
        }

        if (renderIconOffsetV > 0.01f) {
            float iconY = y + delta + 6.5f * sScale - Fonts.CLICKGUI.getHeight(categoryIcon, iconSize) / 2f;
            int finalIconColor = ColorUtil.multAlpha(iconColor, renderIconOffsetV);

            if (module.getCategory() == Category.COMBAT) {
                Fonts.iconf.draw(stack, "c", iconX, iconY, finalIconColor, iconSize * renderIconOffsetV);
            } else if (module.getCategory() == Category.MOVEMENT) {
                Fonts.iconf.draw(stack, "e", iconX, iconY, finalIconColor, iconSize * renderIconOffsetV);
            } else {
                Fonts.CLICKGUI.draw(stack, categoryIcon, iconX, iconY, finalIconColor, iconSize * renderIconOffsetV);
            }
        }

        Fonts.SF_BOLD.draw(stack, text, textX, textY, textColor, adaptiveModuleTextSize);

        float annotationX;
        if (isCentered) {
            float totalContentWidth = textWidth + (renderIconOffsetV > 0.01f ? textXOffset : 0);
            float centerX = x + width / 2f - totalContentWidth / 2f;
            annotationX = centerX + (renderIconOffsetV > 0.01f ? textXOffset : 0) + textWidth + 3 * sScale;
        } else {
            annotationX = textX + textWidth + 3 * sScale;
        }

        float annotationSize = adaptiveModuleTextSize * 0.6f;
        float annotationY = textY + (Fonts.SF_BOLD.getHeight(text, adaptiveModuleTextSize) / 2f) - (Fonts.SF_BOLD.getHeight("FT", annotationSize) / 2f);

        if (module.getClass().isAnnotationPresent(Funtime.class)) {
            drawGradientText(stack, "FT", annotationX, annotationY + 1, annotationSize,
                    ColorUtil.multAlpha(ColorUtil.getColor(255, 0, 0), alpha),
                    ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha));
            annotationX += Fonts.SF_BOLD.getWidth("FT", annotationSize) + 2 * sScale;
        }

        if (module.getClass().isAnnotationPresent(HolyWorld.class)) {
            drawGradientText(stack, "HW", annotationX, annotationY + 1, annotationSize,
                    ColorUtil.multAlpha(ColorUtil.getColor(105, 212, 255), alpha),
                    ColorUtil.multAlpha(ColorUtil.getColor(245, 181, 71), alpha));
            annotationX += Fonts.SF_BOLD.getWidth("HW", annotationSize) + 2 * sScale;
        }

        if (module.getClass().isAnnotationPresent(Rage.class)) {
            drawGradientText(stack, "RAGE", annotationX, annotationY + 1, annotationSize,
                    ColorUtil.multAlpha(ColorUtil.getColor(255, 0, 0), alpha),
                    ColorUtil.multAlpha(ColorUtil.getColor(139, 0, 0), alpha));
            annotationX += Fonts.SF_BOLD.getWidth("RAGE", annotationSize) + 2 * sScale;
        }

        if (module.getClass().isAnnotationPresent(Legit.class)) {
            drawGradientText(stack, "LEGIT", annotationX, annotationY + 1, annotationSize,
                    ColorUtil.multAlpha(ColorUtil.getColor(114, 174, 252), alpha),
                    ColorUtil.multAlpha(ColorUtil.getColor(112, 243, 255), alpha));
            annotationX += Fonts.SF_BOLD.getWidth("LEGIT", annotationSize) + 2 * sScale;
        }

        if (module.getClass().isAnnotationPresent(Customize.class)) {
            drawGradientText(stack, "CUSTOM", annotationX, annotationY + 1, annotationSize,
                    ColorUtil.multAlpha(ColorUtil.getColor(186, 85, 211), alpha),
                    ColorUtil.multAlpha(ColorUtil.getColor(255, 105, 180), alpha));
            annotationX += Fonts.SF_BOLD.getWidth("CUSTOM", annotationSize) + 2 * sScale;
        }

        if (module.getClass().isAnnotationPresent(PVE.class)) {
            drawGradientText(stack, "PVE", annotationX, annotationY + 1, annotationSize,
                    ColorUtil.multAlpha(ColorUtil.getColor(255, 215, 0), alpha),
                    ColorUtil.multAlpha(ColorUtil.getColor(255, 165, 0), alpha));
            annotationX += Fonts.SF_BOLD.getWidth("PVE", annotationSize) + 2 * sScale;
        }

        if (module.getClass().isAnnotationPresent(Test.class)) {
            drawGradientText(stack, "TEST", annotationX, annotationY + 1, annotationSize,
                    ColorUtil.multAlpha(ColorUtil.getColor(169, 169, 169), alpha),
                    ColorUtil.multAlpha(ColorUtil.getColor(211, 211, 211), alpha));
            annotationX += Fonts.SF_BOLD.getWidth("TEST", annotationSize) + 2 * sScale;
        }

        if (module.getClass().isAnnotationPresent(Beta.class)) {
            Fonts.SF_BOLD.draw(stack, "BETA", annotationX, annotationY + 1, ColorUtil.multAlpha(Theme.getInstance().clientColor(), alpha), annotationSize);
        }

        if (!module.getSettings().isEmpty() && !isCentered) {
            float dotsSize = adaptiveModuleTextSize * 0.75f;
            float dotsAlpha = (0.35f + (hoverV * 0.15f)) * alpha;
            Fonts.SF_REGULAR.draw(stack, "...", (x + width - Fonts.SF_REGULAR.getWidth("...", dotsSize) - 4.5f * sScale),
                    (y + delta + 8 * sScale - Fonts.SF_REGULAR.getHeight("...", dotsSize)),
                    ColorUtil.multAlpha(theme.enabledColor(), dotsAlpha), dotsSize);
        }

        if (FlatGui.getInstance().settingsDisplay().is("Под модулем") && selectedV > 0.01f) {
            float settingsBgHeight = height - baseHeight;
            if (settingsBgHeight > 0) {
                RenderUtil.Rounded.roundedRect(stack, x, y + delta + baseHeight, width, settingsBgHeight,
                        ColorUtil.multAlpha(backgroundColor, selectedV * 0.95f), Round.of(4 * sScale));

                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(x, y + delta + baseHeight, width, settingsBgHeight);

                float settingYOffset = y + delta + baseHeight + 3 * sScale;
                for (Setting setting : settings) {
                    if (!(boolean) setting.getSetting().getVisible().get()) continue;
                    setting.setX(this.x + 3.5f * sScale);
                    setting.setY(settingYOffset);
                    setting.setWidth(this.width - 7 * sScale);
                    setting.setDeltaV(settingsDeltaV);
                    setting.setAlphaV(this.selectedV * alpha);
                    setting.render(stack, mouseX, mouseY);
                    settingYOffset += setting.getHeight() + 3 * sScale;
                }

                ScissorExp.unset();
                ScissorExp.pop();
            }
        }
    }

    private void drawGradientText(MatrixStack stack, String text, float x, float y, float size, int color1, int color2) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            float progress = (float) i / (float) (text.length() - 1);
            int color = ColorUtil.interpolateColor(color1, color2, progress);
            Fonts.SF_BOLD.draw(stack, String.valueOf(c), x, y, color, size);
            x += Fonts.SF_BOLD.getWidth(String.valueOf(c), size);
        }
    }

    @Override
    protected void mouse(float mouseX, float mouseY, int button, int action) {
        float sScale = panel.getGui().getResolutionScale();
        final float baseHeight = 14f * sScale;
        final float yExpansion = baseHeight * 0.05f;
        final float renderY = y + delta - yExpansion;
        final float renderHeight = baseHeight + yExpansion * 2;

        String displayMode = FlatGui.getInstance().settingsDisplay().getValue();

        if (selected && (displayMode.equals("Отдельно") || displayMode.equals("Рядом с модулем") || displayMode.equals("Сверху GUI"))) {
            for (Setting setting : settings)
                setting.mouse(mouseX, mouseY, button, action);
        }

        if (isHovered(mouseX, mouseY, x, renderY, width, renderHeight) && action == 0) {
            if (button == 0) {
                module.toggle();
            } else if (button == 1) {
                selected = !selected;
                if (!selected) {
                    settingsDelta = 0;
                    settingsDeltaV = 0;
                }
                if ((displayMode.equals("Отдельно") || displayMode.equals("Рядом с модулем") || displayMode.equals("Сверху GUI")) && selected) {
                    for (Panel p : panel.getGui().getPanels()) {
                        for (Module m : p.getModules()) {
                            if (m != this) {
                                m.setSelected(false);
                            }
                        }
                    }
                }
            } else if (button == 2) {
                input = !input;
            }
        } else if (displayMode.equals("Под модулем") && selected && isHovered(mouseX, mouseY, x, y + delta + baseHeight, width, height - baseHeight)) {
            float settingYOffset = y + delta + baseHeight + 3 * sScale;
            for (Setting setting : settings) {
                if (!(boolean) setting.getSetting().getVisible().get()) continue;
                setting.setX(this.x + 3.5f * sScale);
                setting.setY(settingYOffset);
                setting.setWidth(this.width - 7 * sScale);
                setting.setDeltaV(settingsDeltaV);
                setting.mouse(mouseX, mouseY, button, action);
                settingYOffset += setting.getHeight() + 3 * sScale;
            }
        } else if (displayMode.equals("Модулем") && selected) {
            float settingsStartY = y + delta + 16 * sScale;
            float settingY = settingsStartY + 3 * sScale;
            for (Setting setting : settings) {
                if (!(boolean) setting.getSetting().getVisible().get()) continue;
                if (isHovered(mouseX, mouseY, x + 5 * sScale, settingY, width - 10 * sScale, setting.getHeight())) {
                    setting.mouse(mouseX, mouseY, button, action);
                    return;
                }
                settingY += setting.getHeight() + 3 * sScale;
            }
        }
    }

    @Override
    protected void key(int key) {
        if (selected) {
            for (Setting setting : settings)
                setting.key(key);
        }

        if (input) {
            module.setKey(key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_ESCAPE ? -1 : key);
            input = false;
        }
    }

    @Override
    public void charTyped(char typed) {
        if (selected) {
            for (Setting setting : settings)
                setting.charTyped(typed);
        }
    }

    public void recalculate() {
        if (settings.isEmpty()) return;
        float sScale = panel.getGui().getResolutionScale();
        Setting prevVisibleSetting = settings.get(0);
        for (int i = 0; i < settings.size(); i++) {
            settings.get(i).setY((i == 0) ? mc.getMainWindow().getScaledHeight() / 2f - 170 * sScale : prevVisibleSetting.getY() + (prevVisibleSetting.getHeight() + 3 * sScale) * settings.get(i).getOffsetY());
            if (settings.get(i).getOffsetY() >= .1f)
                prevVisibleSetting = settings.get(i);
        }
    }

    public void updateWidth(float newWidth) {
        this.width = newWidth;
        float sScale = panel.getGui().getResolutionScale();
        for (Setting setting : settings) {
            setting.setWidth(270 * sScale);
        }
    }
}