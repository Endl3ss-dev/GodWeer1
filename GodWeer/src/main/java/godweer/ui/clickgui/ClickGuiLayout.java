package godweer.ui.clickgui;

import godweer.modules.Module;
import godweer.modules.api.setting.BindSetting;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ListSetting;
import godweer.modules.api.setting.ModeSetting;
import godweer.modules.api.setting.Setting;
import godweer.modules.api.setting.TextSetting;
import godweer.utils.render.msdf.FontRenderer;
import godweer.utils.render.msdf.Fonts;

import java.util.List;

public final class ClickGuiLayout {

    public static final float CATEGORY_GAP = 10.0f;
    public static final float CATEGORY_WIDTH = 115.0f;
    public static final float PANEL_HEIGHT = 245.0f;
    public static final float MODULE_PADDING = 3f;
    public static final float MODULE_GAP = 2f;
    public static final float MODULE_HEADER_HEIGHT = 17f;
    public static final float SETTING_START_Y = 20f;
    public static final float SETTING_PADDING = 4f;
    public static final float SETTING_BOTTOM_PADDING = 3f;
    public static final float SETTING_LEFT = 10f;
    public static final float TEXT_SETTING_WIDTH = 42f;
    public static final float TAG_START_Y = 10f;
    public static final float TAG_GAP = 3f;
    public static final float TAG_ROW_GAP = 2f;
    public static final int SEARCH_MAX_CHARS = 24;
    public static final float SEARCH_HEIGHT = 18f;
    public static final float SEARCH_GAP = 3f;
    public static final float SEARCH_ICON_X = 3.5f;
    public static final float SEARCH_TEXT_X = 19f;
    public static final float SEARCH_RIGHT_PADDING = 8f;

    private ClickGuiLayout() {
    }

    public static float getWidth() {
        return CATEGORY_WIDTH;
    }

    public static float getHeight() {
        return PANEL_HEIGHT;
    }

    public static float getCategoryGap() {
        return CATEGORY_GAP;
    }

    public static float getModuleInnerWidth() {
        return getWidth() - 6.5f;
    }

    public static float getSettingRight() {
        return getWidth() - 11.0f;
    }

    public static float getSliderWidth() {
        return getSettingRight() - SETTING_LEFT;
    }

    public static float getBooleanToggleX(float panelX) {
        return panelX + getWidth() - 25.0f;
    }

    public static float getBooleanTextEndX(float panelX) {
        return getBooleanToggleX(panelX) - 2.0f;
    }

    public static float getTextSettingX(float panelX) {
        return panelX + getWidth() - TEXT_SETTING_WIDTH - 9.0f;
    }

    public static float getTotalCategoriesWidth(int categoryCount) {
        if (categoryCount <= 0) {
            return 0.0f;
        }
        return getWidth() * categoryCount + getCategoryGap() * (categoryCount - 1);
    }

    public static float getCategoryPanelX(float x, int index) {
        return x + index * (getWidth() + getCategoryGap());
    }

    public static float getContentY(float y) {
        return y + 25f;
    }

    public static float getContentHeight() {
        return getHeight() - 30f;
    }

    public static float getSearchX(float x) {
        return x;
    }

    public static float getSearchY(float y) {
        return y + getHeight() + SEARCH_GAP;
    }

    public static boolean hasVisibleSettings(List<Setting> settings) {
        for (int i = 0, size = settings.size(); i < size; i++) {
            Setting setting = settings.get(i);
            if (setting != null && setting.visible()) {
                return true;
            }
        }
        return false;
    }

    public static float getTagAvailableWidth() {
        return getSettingRight() - SETTING_LEFT + 2.0f;
    }

    public static float getTagHeight() {
        FontRenderer font = chipFont();
        return (font != null ? font.getHeight() : 6.0f) + 6.0f;
    }

    public static float getTagChipHeight() {
        return getTagHeight() - 7.0f;
    }

    public static float calculateTagWidth(String text) {
        FontRenderer font = chipFont();
        float textWidth = font != null ? font.getWidth(text) : (text != null ? text.length() * 6.0f : 0.0f);
        return textWidth + 5.0f;
    }

    public static float calculateTagChipWidth(String text) {
        return Math.max(24.0f, calculateTagWidth(text) + 2.0f);
    }

    public static float calculateModeSettingHeight(ModeSetting modeSetting) {
        List<String> mods = modeSetting.getMods();
        FontRenderer font = chipFont();
        float chipHeight = getTagChipHeight();
        float availableWidth = getTagAvailableWidth();
        float offsetX = 0.0f;
        float totalHeight = chipHeight;

        for (int i = 0, size = mods.size(); i < size; i++) {
            String entry = mods.get(i);
            float chipWidth = (font != null ? font.getWidth(entry) : entry.length() * 6f) + 8f;
            if (offsetX > 0.0f && offsetX + chipWidth > availableWidth) {
                offsetX = 0.0f;
                totalHeight += chipHeight + TAG_ROW_GAP;
            }
            offsetX += chipWidth + TAG_GAP;
        }

        return totalHeight + 12f;
    }

    public static float calculateListSettingHeight(ListSetting listSetting) {
        List<BooleanSetting> entries = listSetting.getSettings();
        FontRenderer font = chipFont();
        float chipHeight = getTagChipHeight();
        float availableWidth = getTagAvailableWidth();
        float offsetX = 0.0f;
        float totalHeight = chipHeight;

        for (int i = 0, size = entries.size(); i < size; i++) {
            BooleanSetting entry = entries.get(i);
            if (!entry.visible()) {
                continue;
            }
            String label = entry.name();
            float chipWidth = (font != null ? font.getWidth(label) : label.length() * 6f) + 8f;
            if (offsetX > 0.0f && offsetX + chipWidth > availableWidth) {
                offsetX = 0.0f;
                totalHeight += chipHeight + TAG_ROW_GAP;
            }
            offsetX += chipWidth + TAG_GAP;
        }

        return totalHeight + 12f;
    }

    public static float calculateSettingsHeight(Module module) {
        List<Setting> settings = module.getSettings();
        if (settings.isEmpty()) {
            return 0f;
        }

        float height = 0f;
        boolean hasVisibleSetting = false;

        for (int i = 0, size = settings.size(); i < size; i++) {
            Setting setting = settings.get(i);
            if (setting == null || !setting.visible()) {
                continue;
            }

            hasVisibleSetting = true;
            if (setting instanceof BooleanSetting || setting instanceof BindSetting) {
                height += 12f;
            } else if (setting instanceof TextSetting || setting instanceof FloatSetting) {
                height += 22f;
            } else if (setting instanceof ModeSetting modeSetting) {
                height += calculateModeSettingHeight(modeSetting);
            } else if (setting instanceof ListSetting listSetting) {
                height += calculateListSettingHeight(listSetting);
            }
        }

        if (hasVisibleSetting) {
            height += SETTING_BOTTOM_PADDING;
        }
        return height;
    }

    public static float getModuleHeight(Module module, float openProgress) {
        return MODULE_HEADER_HEIGHT + calculateSettingsHeight(module) * openProgress;
    }

    private static FontRenderer chipFont() {
        return Fonts.Font5.get(12);
    }
}
