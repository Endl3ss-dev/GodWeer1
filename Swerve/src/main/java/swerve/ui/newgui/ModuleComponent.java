package swerve.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.Setting;
import swerve.utils.color.ColorUtils;
import swerve.utils.render.ScissorUtils;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

// Компонент модуля newgui: строка с иконкой категории, анимацией включения,
// биндом под CTRL и настройками, раскрывающимися под модулем.
// Бейджи-аннотации (FT/HW/RAGE/...) исходника не переносятся: у модулей Swerve их нет.
public class ModuleComponent extends Builder {

    private final Panel panel;
    private final Module module;
    private final ArrayList<SettingComponent> settings = new ArrayList<>();

    private float delta;
    private float hoverV, toggleV, selectedV;
    private float settingsDelta, settingsDeltaV;
    private float iconOffsetV;
    private float alpha = 1.0f;

    private float descriptionAlphaAnim = 0.0f;
    private long hoverStartTime = 0L;
    private boolean selected = false;
    private boolean input = false;

    private float highlightAnim = 0.0f;
    private boolean isHighlighted = false;

    public ModuleComponent(Panel panel, Module module, float x, float y) {
        this.panel = panel;
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = panel.getWidth() - 6.0f * panel.getGui().getResolutionScale();
        float sScale = panel.getGui().getResolutionScale();
        this.height = 14.0f * sScale;

        for (int i = 0; i < module.getSettings().size(); i++) {
            Setting s = module.getSettings().get(i);
            SettingComponent setting = new SettingComponent(this, s, 0.0f, 0.0f);
            settings.add(setting);
        }
    }

    public Panel getPanel() {
        return panel;
    }

    public Module getModule() {
        return module;
    }

    public ArrayList<SettingComponent> getSettings() {
        return settings;
    }

    public float getDelta() {
        return delta;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getSelectedV() {
        return selectedV;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public float getSettingsDeltaV() {
        return settingsDeltaV;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
    }

    public long getHoverStartTime() {
        return hoverStartTime;
    }

    public void setHoverStartTime(long hoverStartTime) {
        this.hoverStartTime = hoverStartTime;
    }

    public float getDescriptionAlphaAnim() {
        return descriptionAlphaAnim;
    }

    public void setDescriptionAlphaAnim(float descriptionAlphaAnim) {
        this.descriptionAlphaAnim = descriptionAlphaAnim;
    }

    @Override
    public void render(PoseStack stack, float mouseX, float mouseY) {
        float sScale = panel.getGui().getResolutionScale();
        float moduleHeight = GuiStyle.moduleHeight() * sScale;
        float baseHeight = moduleHeight + 2.0f * sScale;
        float moduleRadius = GuiStyle.moduleRadius() * sScale;

        hoverV = Mathf.fast(hoverV,
                isHovered(mouseX, mouseY, x, y + delta, width, moduleHeight) ? 1.0f : 0.0f, GuiStyle.anim(12.0f));
        toggleV = Mathf.fast(toggleV, module.isEnable() ? 1.0f : 0.0f, GuiStyle.anim(12.0f));
        selectedV = Mathf.fast(selectedV, selected ? 1.0f : 0.0f, GuiStyle.anim(16.0f));
        settingsDeltaV = Mathf.fast(settingsDeltaV, settingsDelta, GuiStyle.anim(16.0f));
        iconOffsetV = Mathf.fast(iconOffsetV, module.isEnable() ? 1.0f : 0.0f, GuiStyle.anim(14.0f));
        highlightAnim = Mathf.fast(highlightAnim, isHighlighted ? 1.0f : 0.0f, GuiStyle.anim(10.0f));

        float settingsHeight = 0.0f;
        for (int i = 0; i < settings.size(); i++) {
            SettingComponent s = settings.get(i);
            if (s.getSetting().visible()) {
                settingsHeight += s.getHeight() + 3.0f * sScale;
            }
        }
        float targetHeight = baseHeight + (selected ? settingsHeight + GuiStyle.moduleGap() * sScale : 0.0f);
        height = Mathf.fast(height, targetHeight, GuiStyle.anim(16.0f));

        // Фон строки: в исходнике вторичный цвет обнуляется по альфе и строка остаётся прозрачной.
        int backgroundColor = ColorUtils.multAlpha(GuiTheme.secondaryColor() & 0x00FFFFFF, alpha);
        if (ColorUtils.alpha(backgroundColor) >= 0.25f) {
            GuiRender.smooth(stack, x, y + delta, width, moduleHeight, moduleRadius, backgroundColor);
        }

        // Стиль включённых: заливка строки или полоска слева — под цвет темы.
        if (module.isEnable() && !panel.isThemePanel()) {
            if (GuiStyle.enabledStyle().is("Заливка")) {
                GuiRender.smooth(stack, x, y + delta, width, moduleHeight, moduleRadius,
                        ColorUtils.multAlpha(GuiTheme.clientColor(), 0.10f * alpha));
            } else if (GuiStyle.enabledStyle().is("Полоска")) {
                GuiRender.roundedRect(stack, x + sScale, y + delta + 2.0f * sScale,
                        2.0f * sScale, moduleHeight - 4.0f * sScale, sScale,
                        ColorUtils.multAlpha(GuiTheme.clientColor(), (int) (0.8f * alpha * 255.0f)));
            }
        }

        if (highlightAnim > 0.01f) {
            int highlightColor = ColorUtils.multAlpha(GuiTheme.clientColor(), 0.3f * highlightAnim * alpha);
            GuiRender.roundedRect(stack, x, y + delta, width, moduleHeight, moduleRadius, highlightColor);

            float yExpansion = baseHeight * 0.05f;
            float renderY = y + delta - yExpansion - 1.0f * sScale;
            float renderHeight = baseHeight + yExpansion * 2.0f;
            int pulseColor = ColorUtils.multAlpha(GuiTheme.clientColor(), 0.5f * highlightAnim * alpha);
            float pulseWidth = 2.0f * sScale * highlightAnim;
            GuiRender.roundedOutline(stack, x, renderY, width, renderHeight, pulseWidth, pulseColor, 5.0f * sScale);
        }

        if (hoverV >= 0.1f) {
            float yExpansion = baseHeight * 0.05f;
            float renderY = y + delta - yExpansion - 1.0f * sScale;
            float renderHeight = baseHeight + yExpansion * 2.0f;
            GuiRender.roundedOutline(stack, x, renderY, width, renderHeight, hoverV,
                    ColorUtils.overCol(ColorUtils.multAlpha(GuiTheme.clientColor(), 0.0f),
                            ColorUtils.multAlpha(GuiTheme.clientColor(), 0.3f * alpha), hoverV),
                    2.0f * sScale);
        }

        String text = module.getName() + (input ? " [...]"
                : (GuiStyle.showBinds() && GuiKeys.isControlDown() && module.getKey() > 0
                ? " [" + GuiKeys.keyName(module.getKey()) + "]"
                : ""));

        String categoryIcon = categoryIcon(module.getCategory());
        float iconSize = panel.getGui().getAdaptiveModuleTextSize() * 1.15f;
        float iconWidth = moduleIconWidth(module.getCategory(), iconSize);
        float iconPadding = 4.0f * sScale;

        float iconXOffset, textXOffset, renderIconOffsetV;
        int textColor, iconColor;

        if (panel.isThemePanel()) {
            iconXOffset = 0.0f;
            textXOffset = iconWidth + iconPadding;
            renderIconOffsetV = 1.0f;
            textColor = ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255, 255), alpha);
            iconColor = ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255, 255), alpha);
        } else {
            iconXOffset = -3.0f * sScale * (1.0f - iconOffsetV);
            textXOffset = (iconWidth + iconPadding) * iconOffsetV;
            renderIconOffsetV = iconOffsetV;

            int baseTextColor = ColorUtils.overCol(ColorUtils.multAlpha(GuiTheme.disabledColor(), GuiStyle.disabledAlpha()),
                    ColorUtils.multAlpha(GuiTheme.enabledColor(), 0.8f), toggleV);
            if (highlightAnim > 0.01f) {
                textColor = ColorUtils.multAlpha(
                        ColorUtils.interpolateColor(baseTextColor, GuiTheme.clientColor(), highlightAnim * 0.6f),
                        alpha);
            } else {
                textColor = ColorUtils.multAlpha(baseTextColor, alpha);
            }

            iconColor = ColorUtils.multAlpha(ColorUtils.overCol(
                    ColorUtils.multAlpha(GuiTheme.disabledColor(), 1.2f * (1.0f - iconOffsetV)),
                    ColorUtils.multAlpha(GuiTheme.enabledColor(), 2.5f * iconOffsetV),
                    toggleV), alpha);
        }

        float adaptiveModuleTextSize = panel.getGui().getAdaptiveModuleTextSize();
        float textWidth = GuiFonts.SF_BOLD.getWidth(text, adaptiveModuleTextSize);
        float textX;
        float iconX;
        float textY = y + delta + moduleHeight / 2.0f
                - GuiFonts.SF_BOLD.getHeight(text, adaptiveModuleTextSize) / 1.5f;

        // Выравнивание зафиксировано на дефолте исходника — «Центрично».
        float totalContentWidth = textWidth + (renderIconOffsetV > 0.01f ? textXOffset : 0.0f);
        float centerX = x + width / 2.0f - totalContentWidth / 2.0f;

        if (renderIconOffsetV > 0.01f) {
            iconX = centerX + iconXOffset;
            textX = centerX + textXOffset;
        } else {
            textX = x + width / 2.0f - textWidth / 2.0f;
            iconX = 0.0f;
        }

        if (renderIconOffsetV > 0.01f) {
            float iconY = y + delta + (moduleHeight - sScale) / 2.0f
                    - moduleIconHeight(module.getCategory(), iconSize) / 2.0f;
            int finalIconColor = ColorUtils.multAlpha(iconColor, renderIconOffsetV);
            float finalIconSize = iconSize * renderIconOffsetV;
            drawCategoryIcon(stack, module.getCategory(), iconX, iconY, finalIconColor, finalIconSize);
        }

        GuiFonts.SF_BOLD.draw(stack, text, textX, textY, textColor, adaptiveModuleTextSize);

        // Настройки «Под модулем» — дефолт исходника.
        if (selectedV > 0.01f) {
            float settingsTop = y + delta + baseHeight;
            float settingsBgHeight = height - baseHeight;
            if (settingsBgHeight > 0.0f) {
                // Блюр мира под областью настроек — сила регулируется слайдером «Сила Blur настроек».
                float settingsBlur = GuiConfig.settingsBlurStrength().get();
                if (settingsBlur > 0.0f) {
                    GuiBlur.draw(stack, x, settingsTop, width, settingsBgHeight,
                            GuiStyle.settingsBlurRadius() * sScale, settingsBlur);
                }
                GuiRender.roundedRect(stack, x, settingsTop, width, settingsBgHeight,
                        moduleRadius,
                        ColorUtils.multAlpha(backgroundColor, selectedV * GuiStyle.settingsBgAlpha()));

                ScissorUtils.push();
                ScissorUtils.setFromComponentCoordinates(x, settingsTop, width, settingsBgHeight);

                float settingYOffset = settingsTop + 3.0f * sScale;
                for (int i = 0; i < settings.size(); i++) {
                    SettingComponent setting = settings.get(i);
                    if (!setting.getSetting().visible()) {
                        continue;
                    }
                    setting.setX(x + 3.5f * sScale);
                    setting.setY(settingYOffset);
                    setting.setWidth(width - 7.0f * sScale);
                    setting.setDeltaV(settingsDeltaV);
                    setting.setAlphaV(selectedV * alpha);
                    setting.render(stack, mouseX, mouseY);
                    settingYOffset += setting.getHeight() + GuiStyle.moduleGap() * sScale;
                }

                ScissorUtils.unset();
                ScissorUtils.pop();
            }
        }
    }

    static String categoryIcon(ModuleCategory category) {
        // Как в исходнике: у Combat и Movement — иконки из iconf, остальные из clickgui.
        if (category == ModuleCategory.COMBAT) {
            return "c";
        }
        if (category == ModuleCategory.MOVEMENT) {
            return "e";
        }
        String icon = category.getIcons();
        if (icon == null || icon.isEmpty()) {
            return "a";
        }
        return icon.toLowerCase();
    }

    private static float moduleIconWidth(ModuleCategory category, float iconSize) {
        if (category == ModuleCategory.COMBAT || category == ModuleCategory.MOVEMENT
                || category == ModuleCategory.INTERFACE) {
            return GuiFonts.ICONF.getWidth(categoryIcon(category), iconSize);
        }
        return GuiFonts.CLICKGUI.getWidth(categoryIcon(category), iconSize);
    }

    private static float moduleIconHeight(ModuleCategory category, float iconSize) {
        if (category == ModuleCategory.COMBAT || category == ModuleCategory.MOVEMENT
                || category == ModuleCategory.INTERFACE) {
            return GuiFonts.ICONF.getHeight(categoryIcon(category), iconSize);
        }
        return GuiFonts.CLICKGUI.getHeight(categoryIcon(category), iconSize);
    }

    private static void drawCategoryIcon(PoseStack stack, ModuleCategory category,
                                         float iconX, float iconY, int color, float iconSize) {
        if (category == ModuleCategory.COMBAT || category == ModuleCategory.MOVEMENT
                || category == ModuleCategory.INTERFACE) {
            GuiFonts.ICONF.draw(stack, categoryIcon(category), iconX, iconY, color, iconSize);
        } else {
            GuiFonts.CLICKGUI.draw(stack, categoryIcon(category), iconX, iconY, color, iconSize);
        }
    }

    @Override
    protected void mouse(float mouseX, float mouseY, int button, int action) {
        float sScale = panel.getGui().getResolutionScale();
        float baseHeight = GuiStyle.moduleHeight() * sScale;
        float yExpansion = baseHeight * 0.05f;
        float renderY = y + delta - yExpansion;
        float renderHeight = baseHeight + yExpansion * 2.0f;

        if (isHovered(mouseX, mouseY, x, renderY, width, renderHeight) && action == 0) {
            if (button == 0) {
                module.toggle();
            } else if (button == 1) {
                selected = !selected;
                if (!selected) {
                    settingsDelta = 0.0f;
                    settingsDeltaV = 0.0f;
                }
            } else if (button == 2) {
                input = !input;
            }
        } else if (selected && isHovered(mouseX, mouseY, x, y + delta + baseHeight, width, height - baseHeight)) {
            float settingYOffset = y + delta + baseHeight + 3.0f * sScale;
            for (int i = 0; i < settings.size(); i++) {
                SettingComponent setting = settings.get(i);
                if (!setting.getSetting().visible()) {
                    continue;
                }
                setting.setX(x + 3.5f * sScale);
                setting.setY(settingYOffset);
                setting.setWidth(width - 7.0f * sScale);
                setting.setDeltaV(settingsDeltaV);
                setting.mouse(mouseX, mouseY, button, action);
                settingYOffset += setting.getHeight() + GuiStyle.moduleGap() * sScale;
            }
        }
    }

    // Пересылает колесо мыши в раскрытую сетку тем, если курсор над ней.
    public boolean scrollThemeGrid(float mouseX, float mouseY, float delta) {
        if (!selected) {
            return false;
        }
        for (int i = 0; i < settings.size(); i++) {
            if (settings.get(i).scrollThemeGrid(mouseX, mouseY, delta)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void key(int key) {
        if (selected) {
            for (int i = 0; i < settings.size(); i++) {
                settings.get(i).key(key);
            }
        }

        if (input) {
            module.setKey(key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_ESCAPE ? -1 : key);
            input = false;
        }
    }

    @Override
    public void charTyped(char typed) {
        if (selected) {
            for (int i = 0; i < settings.size(); i++) {
                settings.get(i).charTyped(typed);
            }
        }
    }
}
