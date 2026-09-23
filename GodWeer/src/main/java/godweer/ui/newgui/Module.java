package godweer.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import godweer.modules.ModuleCategory;
import godweer.utils.color.ColorUtils;
import godweer.utils.input.KeyBoardUtils;
import godweer.utils.math.Mathf;
import godweer.utils.render.draw.RenderDraw;
import godweer.utils.render.draw.Round;
import godweer.utils.render.draw.ScissorExp;
import godweer.utils.render.font.GuiFonts;
import godweer.utils.render.font.GuiTheme;

import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class Module extends Builder {

    private final Panel panel;
    private final godweer.modules.Module module;
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

    public Module(Panel panel, godweer.modules.Module module, float x, float y) {
        this.panel = panel;
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = panel.getWidth() - 6 * panel.getGui().getResolutionScale();
        float sScale = panel.getGui().getResolutionScale();
        this.height = 14 * sScale;

        float offsetX = x + 3 * sScale;
        float offsetY = y + 14 * sScale;
        for (godweer.modules.api.setting.Setting s : module.getSettings()) {
            Setting setting = new Setting(this, s, offsetX, offsetY);
            settings.add(setting);
            offsetY += setting.getHeight() + 3 * sScale;
        }
    }

    @Override
    public void render(PoseStack stack, float mouseX, float mouseY) {
        float sScale = panel.getGui().getResolutionScale();
        final float baseHeight = 16f * sScale;
        final float yExpansion = baseHeight * 0.05f;
        final float renderY = y + delta - yExpansion - 1 * sScale;
        final float renderHeight = baseHeight + yExpansion * 2;

        hoverV = Mathf.fast(hoverV, isHovered(mouseX, mouseY, x, renderY, width, renderHeight) ? 1 : 0, 12);
        toggleV = Mathf.fast(toggleV, module.isEnable() ? 1 : 0, 12);
        selectedV = Mathf.fast(selectedV, selected ? 1 : 0, 16);
        settingsDeltaV = Mathf.fast(settingsDeltaV, settingsDelta, 16);
        iconOffsetV = Mathf.fast(iconOffsetV, module.isEnable() ? 1 : 0, 14);
        highlightAnim = Mathf.fast(highlightAnim, isHighlighted ? 1 : 0, 10);

        float targetHeight = baseHeight;
        height = Mathf.fast(height, targetHeight, 16);

        GuiTheme theme = GuiTheme.getInstance();
        float adaptiveModuleTextSize = panel.getGui().getAdaptiveModuleTextSize();

        int originalColor = theme.secondaryColor();
        int originalAlpha = (originalColor >> 24) & 0xFF;
        int newAlpha = Math.max(0, originalAlpha - 1000);
        int backgroundColor = (originalColor & 0x00FFFFFF) | (newAlpha << 24);
        backgroundColor = ColorUtils.multAlpha(backgroundColor, alpha);

        if (ColorUtils.alpha(backgroundColor) >= .25f)
            RenderDraw.Rounded.smooth(stack, x, y + delta, width, 14 * sScale, backgroundColor, Round.of(4 * sScale));

        if (highlightAnim > 0.01f) {
            int highlightColor = ColorUtils.multAlpha(theme.clientColor(), 0.3f * highlightAnim * alpha);
            RenderDraw.Rounded.roundedRect(stack, x, y + delta, width, 14 * sScale, highlightColor, Round.of(4 * sScale));

            int pulseColor = ColorUtils.multAlpha(theme.clientColor(), 0.5f * highlightAnim * alpha);
            float pulseWidth = 2f * sScale * highlightAnim;
            RenderDraw.Rounded.roundedOutline(stack, x, renderY, width, renderHeight, pulseWidth, pulseColor, Round.of(5 * sScale));
        }

        if (GuiSettings.getInstance().moduleOutline().getValueBool()) {
            float outlineW = GuiSettings.getInstance().outlineWidth().getValueFloat();
            float outlineA = GuiSettings.getInstance().outlineOpacity().getValueFloat();
            int outlineColor = ColorUtils.multAlpha(theme.clientColor(), outlineA * alpha);
            RenderDraw.Rounded.roundedOutline(stack, x, renderY, width, renderHeight, outlineW, outlineColor, Round.of(5 * sScale));
        }

        if (hoverV >= .1f)
            RenderDraw.Rounded.roundedOutline(stack, x, renderY, width, renderHeight, hoverV,
                    ColorUtils.overCol(ColorUtils.multAlpha(theme.clientColor(), 0), ColorUtils.multAlpha(theme.clientColor(), .3f * alpha), hoverV), Round.of(2 * sScale));

        String text = module.getName() + (input ? " [...]" : (isCtrlHeld() && module.getKey() > 0 ? " [" + KeyBoardUtils.getBindName(module.getKey()) + "]" : ""));

        String categoryIcon = module.getCategory().getIcons();
        float iconSize = adaptiveModuleTextSize * 1.15f;
        float iconWidth = GuiFonts.CLICKGUI.getWidth(categoryIcon, iconSize);
        float iconPadding = 4f * sScale;

        float iconXOffset, textXOffset, renderIconOffsetV;
        int textColor, iconColor;

        if (panel.isThemePanel()) {
            iconXOffset = 0;
            textXOffset = iconWidth + iconPadding;
            renderIconOffsetV = 1.0f;
<<<<<<< HEAD
            textColor = ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255), alpha);
            iconColor = ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255), alpha);
=======
            textColor = ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255, 255), alpha);
            iconColor = ColorUtils.multAlpha(ColorUtils.getColor(255, 255, 255, 255), alpha);
>>>>>>> be39d49e669e2142432a36b1bc0484f06041f13b
        } else {
            iconXOffset = -3f * sScale * (1f - iconOffsetV);
            textXOffset = (iconWidth + iconPadding) * iconOffsetV;
            renderIconOffsetV = iconOffsetV;

            int baseTextColor = ColorUtils.overCol(theme.disabledColor(), ColorUtils.multAlpha(theme.enabledColor(), 0.8f), toggleV);
            if (highlightAnim > 0.01f) {
                textColor = ColorUtils.multAlpha(ColorUtils.interpolateColor(baseTextColor, theme.clientColor(), highlightAnim * 0.6f), alpha);
            } else {
                textColor = ColorUtils.multAlpha(baseTextColor, alpha);
            }

            iconColor = ColorUtils.multAlpha(ColorUtils.overCol(
                    ColorUtils.multAlpha(theme.disabledColor(), 1.2f * (1f - iconOffsetV)),
                    ColorUtils.multAlpha(theme.enabledColor(), 2.5f * iconOffsetV),
                    toggleV), alpha);
        }

        boolean isCentered = GuiSettings.getInstance().textAlign().is("Центрично");
        float textWidth = GuiFonts.SF_BOLD.getWidth(text, adaptiveModuleTextSize);
        float textX;
        float iconX;
        float textY = y + delta + 7 * sScale - GuiFonts.SF_BOLD.getHeight(text, adaptiveModuleTextSize) / 1.5f;

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
            float iconY = y + delta + 6.5f * sScale - GuiFonts.CLICKGUI.getHeight(categoryIcon, iconSize) / 2f;
            int finalIconColor = ColorUtils.multAlpha(iconColor, renderIconOffsetV);

            if (module.getCategory() == ModuleCategory.COMBAT) {
                GuiFonts.ICONF.draw(stack, "c", iconX, iconY, finalIconColor, iconSize * renderIconOffsetV);
            } else if (module.getCategory() == ModuleCategory.MOVEMENT) {
                GuiFonts.ICONF.draw(stack, "e", iconX, iconY, finalIconColor, iconSize * renderIconOffsetV);
            } else {
                GuiFonts.CLICKGUI.draw(stack, categoryIcon, iconX, iconY, finalIconColor, iconSize * renderIconOffsetV);
            }
        }

        GuiFonts.SF_BOLD.draw(stack, text, textX, textY, textColor, adaptiveModuleTextSize);

        if (!module.getSettings().isEmpty() && !isCentered) {
            float dotsSize = adaptiveModuleTextSize * 0.75f;
            float dotsAlpha = (0.35f + (hoverV * 0.15f)) * alpha;
            GuiFonts.SF_REGULAR.draw(stack, "...", (x + width - GuiFonts.SF_REGULAR.getWidth("...", dotsSize) - 4.5f * sScale),
                    (y + delta + 8 * sScale - GuiFonts.SF_REGULAR.getHeight("...", dotsSize)),
                    ColorUtils.multAlpha(theme.enabledColor(), dotsAlpha), dotsSize);
        }
    }

    private boolean isCtrlHeld() {
        long window = Minecraft.getInstance().getWindow().getWindow();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
    }

    @Override
    protected void mouse(float mouseX, float mouseY, int button, int action) {
        float sScale = panel.getGui().getResolutionScale();
        final float baseHeight = 14f * sScale;
        final float yExpansion = baseHeight * 0.05f;
        final float renderY = y + delta - yExpansion;
        final float renderHeight = baseHeight + yExpansion * 2;

        if (isHovered(mouseX, mouseY, x, renderY, width, renderHeight) && action == 0) {
            if (button == 0) {
                module.toggle();
            } else if (button == 1) {
                selected = !selected;
                if (!selected) {
                    settingsDelta = 0;
                    settingsDeltaV = 0;
                }
            } else if (button == 2) {
                input = !input;
            }
        }

        if (selected) {
            for (Setting setting : settings)
                setting.mouse(mouseX, mouseY, button, action);
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

    public Panel getPanel() {
        return panel;
    }

    public godweer.modules.Module getModule() {
        return module;
    }

    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public float getDelta() {
        return delta;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public float getSelectedV() {
        return selectedV;
    }

    public float getSettingsDeltaV() {
        return settingsDeltaV;
    }

    public float getDescriptionAlphaAnim() {
        return descriptionAlphaAnim;
    }

    public void setDescriptionAlphaAnim(float v) {
        this.descriptionAlphaAnim = v;
    }

    public long getHoverStartTime() {
        return hoverStartTime;
    }

    public void setHoverStartTime(long t) {
        this.hoverStartTime = t;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
    }
}
