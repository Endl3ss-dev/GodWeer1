package godweer.ui.clickgui;

import com.mojang.blaze3d.vertex.PoseStack;

import godweer.modules.Module;
import godweer.modules.api.setting.BindSetting;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ListSetting;
import godweer.modules.api.setting.ModeSetting;
import godweer.modules.api.setting.Setting;
import godweer.modules.api.setting.TextSetting;
import godweer.utils.animation.AnimationUtils;
import godweer.utils.color.ColorUtils;
import godweer.utils.input.KeyBoardUtils;
import godweer.utils.render.RenderUtils;
import godweer.utils.render.ScissorUtils;
import godweer.utils.render.msdf.FontRenderer;
import godweer.utils.render.msdf.Fonts;

import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class ClickGuiSettingRenderer {

    private static final float HOVER_SCROLL_OVERFLOW_THRESHOLD = 6.0f;
    private static final float OPTION_H = 11f;
    private static final float TAG_GAP = 2f;
    private static final float OPTION_RADIUS = 2.5f;

    public void render(GuiGraphics context, Module module, float panelX, float moduleY, float openProgress,
                       int colorTheme, double mouseX, double mouseY, ClickGuiState state) {
        List<Setting> settings = module.getSettings();
        if (settings.isEmpty() || openProgress <= 0.01f || issue(12) == null) {
            return;
        }

        float maxSettingHeight = ClickGuiLayout.calculateSettingsHeight(module);
        float settingsClipY = moduleY + ClickGuiLayout.SETTING_START_Y;
        float settingsClipHeight = maxSettingHeight * openProgress;

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(panelX + ClickGuiLayout.MODULE_PADDING, settingsClipY - 3,
                ClickGuiLayout.MODULE_INNER_WIDTH, settingsClipHeight);

        float settingYoffset = ClickGuiLayout.SETTING_START_Y;
        for (int i = 0, size = settings.size(); i < size; i++) {
            Setting setting = settings.get(i);
            if (setting == null || !setting.visible()) {
                continue;
            }

            float settingY = moduleY + settingYoffset + ClickGuiLayout.SETTING_PADDING;
            int alpha = (int) (255 * openProgress);

            if (setting instanceof BooleanSetting booleanSetting) {
                renderBooleanSetting(context, panelX, settingY, alpha, colorTheme, mouseX, mouseY, booleanSetting, state);
                settingYoffset += 12f;
            } else if (setting instanceof TextSetting textSetting) {
                renderTextSetting(context, panelX, settingY, alpha, colorTheme, mouseX, mouseY, textSetting, state);
                settingYoffset += 22f;
            } else if (setting instanceof FloatSetting floatSetting) {
                renderFloatSetting(context, panelX, settingY, alpha, colorTheme, mouseX, mouseY, floatSetting, state);
                settingYoffset += 22f;
            } else if (setting instanceof ModeSetting modeSetting) {
                renderModeSetting(context, panelX, settingY, alpha, colorTheme, mouseX, mouseY, modeSetting, state);
                settingYoffset += ClickGuiLayout.calculateModeSettingHeight(modeSetting);
            } else if (setting instanceof ListSetting listSetting) {
                renderListSetting(context, panelX, settingY, alpha, colorTheme, mouseX, mouseY, listSetting, state);
                settingYoffset += ClickGuiLayout.calculateListSettingHeight(listSetting);
            } else if (setting instanceof BindSetting bindSetting) {
                renderBindSetting(context, panelX, settingY, alpha, colorTheme, mouseX, mouseY, bindSetting, state);
                settingYoffset += 12f;
            }
        }

        ScissorUtils.pop();
    }

    private void renderBooleanSetting(GuiGraphics context, float panelX, float settingY, int alpha, int colorTheme,
                                      double mouseX, double mouseY, BooleanSetting booleanSetting, ClickGuiState state) {
        AnimationUtils backgroundAnimation = state.getBooleanBackgroundAnimation(booleanSetting);
        AnimationUtils circleAnimation = state.getBooleanCircleAnimation(booleanSetting);
        backgroundAnimation.update(booleanSetting.isState() ? 1f : 0f);
        circleAnimation.update(booleanSetting.isState() ? 1f : 0f);

        float backgroundProgress = backgroundAnimation.getValue();
        float circleProgress = circleAnimation.getValue();

        int offColor = ColorUtils.darken(colorTheme, 0.05f);
        int onColor = colorTheme;

        int r = (int) ((offColor >> 16 & 255) + ((onColor >> 16 & 255) - (offColor >> 16 & 255)) * backgroundProgress);
        int g = (int) ((offColor >> 8 & 255) + ((onColor >> 8 & 255) - (offColor >> 8 & 255)) * backgroundProgress);
        int b = (int) ((offColor & 255) + ((onColor & 255) - (offColor & 255)) * backgroundProgress);

        drawStringWithHoverScroll(issue(13), context.pose(), booleanSetting.displayName(),
                panelX + ClickGuiLayout.SETTING_LEFT, settingY - 3,
                (panelX + 73f) - (panelX + ClickGuiLayout.SETTING_LEFT), getPrimarySettingColor(alpha),
                mouseX, mouseY, state, getSettingTextKey(booleanSetting));

        RenderUtils.drawRoundedRect(context.pose(), panelX + 75, settingY - 5, 16, 9, 4.5f,
                ColorUtils.rgba(r, g, b, alpha));

        float circleX = panelX + 79.5f + (circleProgress * 6.2f);
        RenderUtils.drawRoundCircle(context.pose(), circleX + 0.5f, settingY - 0.5f, 7,
                ColorUtils.rgba(255, 255, 255, alpha));
    }

    private void renderFloatSetting(GuiGraphics context, float panelX, float settingY, int alpha, int colorTheme,
                                    double mouseX, double mouseY, FloatSetting floatSetting, ClickGuiState state) {
        if (floatSetting.isActive()) {
            floatSetting.setValue(state.updateActiveSliderValue(floatSetting, mouseX));
        }

        AnimationUtils sliderAnimation = state.getSliderAnimation(floatSetting);
        sliderAnimation.update(state.getSliderPos(floatSetting));
        float animatedPos = sliderAnimation.getValue();

        String valueString = formatSliderValue(floatSetting);
        float valueX = panelX + ClickGuiLayout.SETTING_RIGHT - issue(12).getWidth(valueString);
        float nameMaxWidth = (valueX - 4f) - (panelX + ClickGuiLayout.SETTING_LEFT);

        drawStringWithHoverScroll(issue(12), context.pose(), floatSetting.displayName(),
                panelX + ClickGuiLayout.SETTING_LEFT, settingY - 2, nameMaxWidth, getPrimarySettingColor(alpha),
                mouseX, mouseY, state, getSettingTextKey(floatSetting));
        issue(12).drawString(context.pose(), valueString, valueX, settingY - 2,
                ColorUtils.setAlphaColor(colorTheme, alpha));

        int sliderBackgroundColor = ColorUtils.setAlphaColor(ColorUtils.darken(colorTheme, 0.2f), alpha);
        RenderUtils.drawRoundedRect(context.pose(), panelX + ClickGuiLayout.SETTING_LEFT, settingY + 6,
                ClickGuiLayout.SLIDER_WIDTH, 4.5f, 2f, sliderBackgroundColor);

        int sliderFillColor = ColorUtils.setAlphaColor(colorTheme, alpha);
        RenderUtils.drawRoundedRect(context.pose(), panelX + ClickGuiLayout.SETTING_LEFT, settingY + 6,
                animatedPos * ClickGuiLayout.SLIDER_WIDTH, 4.5f, 2f, sliderFillColor);
        RenderUtils.drawRoundCircle(context.pose(),
                panelX + ClickGuiLayout.SETTING_LEFT + animatedPos * ClickGuiLayout.SLIDER_WIDTH,
                settingY + 8.25f, 6, ColorUtils.setAlphaColor(-1, alpha));
    }

    private void renderTextSetting(GuiGraphics context, float panelX, float settingY, int alpha, int colorTheme,
                                   double mouseX, double mouseY, TextSetting textSetting, ClickGuiState state) {
        String value = textSetting.get();
        boolean editing = state.getEditingTextSetting() == textSetting;
        String preview = value == null || value.isEmpty() ? "..." : value;
        String boxText = editing ? preview + "_" : preview;
        float boxWidth = ClickGuiLayout.TEXT_SETTING_WIDTH;
        float boxX = panelX + 49f;

        drawStringWithHoverScroll(issue(13), context.pose(), textSetting.displayName(),
                panelX + ClickGuiLayout.SETTING_LEFT, settingY - 3,
                (boxX - 1f) - (panelX + ClickGuiLayout.SETTING_LEFT), getPrimarySettingColor(alpha),
                mouseX, mouseY, state, getSettingTextKey(textSetting));

        int textColor = ColorUtils.setAlphaColor(-1, alpha);
        float boxY = settingY - 2.5f;
        RenderUtils.drawRoundedRect(context.pose(), boxX, boxY - 3, boxWidth, 9f, 2.5f,
                ColorUtils.rgba(0, 0, 0, 200));

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(boxX + 2f, boxY - 2f, boxWidth - 4f, 7f);
        drawStringWithHoverScroll(issue(12), context.pose(), boxText, boxX + 3f, settingY - 2.5f,
                boxWidth - 6f, textColor, mouseX, mouseY, state, getSettingTextKey(textSetting) + "_value");
        ScissorUtils.pop();
    }

    private void renderModeSetting(GuiGraphics context, float panelX, float settingY, int alpha, int colorTheme,
                                   double mouseX, double mouseY, ModeSetting modeSetting, ClickGuiState state) {
        drawStringWithHoverScroll(issue(12), context.pose(), modeSetting.displayName(),
                panelX + ClickGuiLayout.SETTING_LEFT, settingY - 2,
                ClickGuiLayout.SETTING_RIGHT - ClickGuiLayout.SETTING_LEFT, getPrimarySettingColor(alpha),
                mouseX, mouseY, state, getSettingTextKey(modeSetting));

        FontRenderer chipFont = issue(12);
        float chipX = panelX + ClickGuiLayout.SETTING_LEFT - 1.5f;
        float chipY = settingY + ClickGuiLayout.TAG_START_Y - 0.5f;
        float availableWidth = ClickGuiLayout.getTagAvailableWidth();
        float offsetX = 0.0f;
        float offsetY = 0.0f;

        List<String> mods = modeSetting.getMods();
        for (int i = 0, size = mods.size(); i < size; i++) {
            String mode = mods.get(i);
            float chipWidth = chipFont.getWidth(mode) + 8f;

            if (offsetX > 0.0f && offsetX + chipWidth > availableWidth) {
                offsetX = 0.0f;
                offsetY += OPTION_H + TAG_GAP;
            }

            boolean selected = modeSetting.getCurrent().equals(mode);
            AnimationUtils animation = state.getModeAnimation(getModeKey(modeSetting, mode), selected);
            animation.update(selected ? 1f : 0f);
            float progress = animation.getValue();

            int textColor = ColorUtils.setAlphaColor(
                    ColorUtils.interpolateColor(
                            ColorUtils.rgba(140, 139, 145, 255),
                            ColorUtils.rgba(255, 255, 255, 255),
                            progress
                    ),
                    alpha
            );

            float currentX = chipX + offsetX;
            float currentY = chipY + offsetY;

            RenderUtils.drawRoundedRect(context.pose(), currentX, currentY - 5, chipWidth, OPTION_H, OPTION_RADIUS,
                    ColorUtils.rgba(0, 0, 0, 200));
            chipFont.drawString(context.pose(), mode,
                    currentX + (chipWidth / 2.0f) - (chipFont.getWidth(mode) / 2.0f) + 0.5f,
                    currentY + (OPTION_H / 2.0f) - (chipFont.getHeight() / 2.0f) + 0.0f, textColor);

            offsetX += chipWidth + TAG_GAP;
        }
    }

    private void renderListSetting(GuiGraphics context, float panelX, float settingY, int alpha, int colorTheme,
                                   double mouseX, double mouseY, ListSetting listSetting, ClickGuiState state) {
        List<BooleanSetting> entries = listSetting.getSettings();
        int enabledCount = 0;
        for (int i = 0, size = entries.size(); i < size; i++) {
            if (entries.get(i).isState()) {
                enabledCount++;
            }
        }

        String counter = enabledCount + "/" + entries.size();
        float counterX = panelX + ClickGuiLayout.SETTING_RIGHT - issue(12).getWidth(counter);

        drawStringWithHoverScroll(issue(12), context.pose(), listSetting.displayName(),
                panelX + ClickGuiLayout.SETTING_LEFT, settingY - 2,
                (counterX - 4.0f) - (panelX + ClickGuiLayout.SETTING_LEFT), getPrimarySettingColor(alpha),
                mouseX, mouseY, state, getSettingTextKey(listSetting));
        issue(12).draw(context.pose(), counter, counterX, settingY - 2, getPrimarySettingColor(alpha));

        FontRenderer chipFont = issue(12);
        float chipX = panelX + ClickGuiLayout.SETTING_LEFT - 1.5f;
        float chipY = settingY + ClickGuiLayout.TAG_START_Y - 0.5f;
        float availableWidth = ClickGuiLayout.getTagAvailableWidth();
        float offsetX = 0.0f;
        float offsetY = 0.0f;

        for (int i = 0, size = entries.size(); i < size; i++) {
            BooleanSetting entry = entries.get(i);
            if (!entry.visible()) {
                continue;
            }

            String label = entry.name();
            float chipWidth = chipFont.getWidth(label) + 8f;

            if (offsetX > 0.0f && offsetX + chipWidth > availableWidth) {
                offsetX = 0.0f;
                offsetY += OPTION_H + TAG_GAP;
            }

            boolean selected = entry.isState();
            AnimationUtils animation = state.getListAnimation(getListKey(listSetting, entry), selected);
            animation.update(selected ? 1f : 0f);
            float progress = animation.getValue();

            int textColor = ColorUtils.setAlphaColor(
                    ColorUtils.interpolateColor(
                            ColorUtils.rgba(140, 139, 145, 255),
                            ColorUtils.rgba(255, 255, 255, 255),
                            progress
                    ),
                    alpha
            );

            float currentX = chipX + offsetX;
            float currentY = chipY + offsetY;

            RenderUtils.drawRoundedRect(context.pose(), currentX, currentY - 5, chipWidth, OPTION_H, OPTION_RADIUS,
                    ColorUtils.rgba(0, 0, 0, 200));
            chipFont.drawString(context.pose(), label,
                    currentX + (chipWidth / 2.0f) - (chipFont.getWidth(label) / 2.0f) + 0.5f,
                    currentY - 5 + (OPTION_H / 2.0f) - (chipFont.getHeight() / 2.0f) + 5.0f, textColor);

            offsetX += chipWidth + TAG_GAP;
        }
    }

    private void renderBindSetting(GuiGraphics context, float panelX, float settingY, int alpha, int colorTheme,
                                   double mouseX, double mouseY, BindSetting bindSetting, ClickGuiState state) {
        boolean binding = state.getBindingSetting() == bindSetting;
        AnimationUtils bindAnimation = state.getBindAnimation(getBindKey(bindSetting), binding);
        bindAnimation.update(binding ? 1f : 0f);
        float progress = bindAnimation.getValue();

        String bindString = binding ? "..." : state.toEnglish(KeyBoardUtils.getBindName(bindSetting.getKey()));
        float bindWidth = issue(12).getWidth(bindString) + 6f;
        float bindX = panelX + ClickGuiLayout.SETTING_RIGHT - bindWidth;

        int bindTextColor = ColorUtils.setAlphaColor(
                ColorUtils.interpolateColor(ColorUtils.rgb(140, 139, 145), -1, progress), alpha);

        RenderUtils.drawRoundedRect(context.pose(), bindX, settingY - 5.5f, bindWidth, 9, 2.5f,
                ColorUtils.rgba(0, 0, 0, 200));
        issue(12).drawString(context.pose(), bindString, bindX + 3, settingY - 2, bindTextColor);

        drawStringWithHoverScroll(issue(12), context.pose(), bindSetting.displayName(),
                panelX + ClickGuiLayout.SETTING_LEFT, settingY - 2,
                (bindX - 4f) - (panelX + ClickGuiLayout.SETTING_LEFT), getPrimarySettingColor(alpha),
                mouseX, mouseY, state, getSettingTextKey(bindSetting));
    }

    private String getModeKey(ModeSetting setting, String mode) {
        return System.identityHashCode(setting) + "_mode_" + mode;
    }

    private String getListKey(ListSetting setting, BooleanSetting entry) {
        return System.identityHashCode(setting) + "_list_" + entry.name();
    }

    private String getBindKey(BindSetting setting) {
        return System.identityHashCode(setting) + "_bind";
    }

    private String formatSliderValue(FloatSetting setting) {
        float value = setting.get();
        float increment = setting.getIncrement();
        if (increment >= 1f) {
            return String.valueOf((int) value);
        }
        if (increment >= 0.1f) {
            return String.format("%.1f", value);
        }
        return String.format("%.2f", value);
    }

    private void drawStringWithHoverScroll(FontRenderer font, PoseStack matrix, String text, float x, float y,
                                           float maxWidth, int color, double mouseX, double mouseY,
                                           ClickGuiState state, String animationKey) {
        if (font == null || text == null || text.isEmpty() || maxWidth <= 0f) {
            return;
        }

        float totalWidth = font.getWidth(text);
        float overflow = totalWidth - maxWidth;
        if (overflow <= HOVER_SCROLL_OVERFLOW_THRESHOLD) {
            font.draw(matrix, text, x, y, color);
            return;
        }

        boolean hovered = isTextHovered(x, y, maxWidth, font.getHeight(), mouseX, mouseY);
        float scrollPhase = state.advanceTextScrollPhase(animationKey, hovered);
        boolean scrollActive = state.isTextScrollActive(animationKey, hovered);
        AnimationUtils hoverAnimation = state.getTextHoverAnimation(animationKey, scrollActive);
        hoverAnimation.update(scrollActive ? 1f : 0f);
        float hoverProgress = hoverAnimation.getValue();
        float scrollOffset = getHoverScrollOffset(overflow, scrollPhase) * hoverProgress;

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(x, y - 2.0f, maxWidth, font.getHeight() + 4.0f);
        font.draw(matrix, text, x - scrollOffset, y, color);
        ScissorUtils.pop();
    }

    private int getPrimarySettingColor(int alpha) {
        return ColorUtils.rgba(245, 245, 248, alpha);
    }

    private boolean isTextHovered(float x, float y, float width, float height, double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y - 2.0f && mouseY <= y + height + 2.0f;
    }

    private float getHoverScrollOffset(float maxOffset, float phase) {
        if (maxOffset <= 0.0f) {
            return 0.0f;
        }
        float pingPong = phase < 0.5f ? (phase * 2.0f) : (2.0f - phase * 2.0f);
        float eased = pingPong * pingPong * (3.0f - 2.0f * pingPong);
        return maxOffset * eased;
    }

    private String getSettingTextKey(Setting setting) {
        return "setting_text_" + System.identityHashCode(setting);
    }

    private FontRenderer issue(int size) {
        return Fonts.Font5.get(size);
    }
}
