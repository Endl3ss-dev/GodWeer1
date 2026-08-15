package godweer.ui.clickgui;

import com.mojang.blaze3d.platform.Window;

import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BindSetting;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ListSetting;
import godweer.modules.api.setting.ModeSetting;
import godweer.modules.api.setting.Setting;
import godweer.modules.api.setting.TextSetting;
import godweer.utils.input.KeyBoardUtils;
import godweer.utils.math.HoveringUtils;
import godweer.utils.render.msdf.FontRenderer;
import godweer.utils.render.msdf.Fonts;

import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ClickGuiInputHandler {

    private final ClickGuiState state;
    private final ClickGuiThemeSelector themeSelector;

    public ClickGuiInputHandler(ClickGuiState state, ClickGuiThemeSelector themeSelector) {
        this.state = state;
        this.themeSelector = themeSelector;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, Window window) {
        if (window != null && button == 0) {
            int categoryCount = ModuleCategory.values().length;
            float searchW = getSearchWidth();
            float searchX = ClickGuiLayout.getSearchX(state.getX(), categoryCount, searchW) - 10;
            float searchY = ClickGuiLayout.getSearchY(state.getY() + state.getRenderOffsetY());

            boolean searchHovered = HoveringUtils.isHovered(mouseX, mouseY, searchX, searchY, searchW,
                    ClickGuiLayout.SEARCH_HEIGHT);
            state.setSearchActive(searchHovered);
            if (searchHovered) {
                state.setEditingTextSetting(null);
                state.startSearchSelection(getSearchIndexAt(mouseX, searchX));
                return true;
            }
        }

        if (state.getBindingModule() != null && button >= 2) {
            state.getBindingModule().setKey(KeyBoardUtils.createMouseBind(button));
            state.setBindingModule(null);
            return true;
        }

        if (state.getBindingSetting() != null && button >= 2) {
            state.getBindingSetting().setKey(KeyBoardUtils.createMouseBind(button));
            state.setBindingSetting(null);
            return true;
        }

        state.setEditingTextSetting(null);

        if (themeSelector.handleClick(window, mouseX, mouseY, button, state.getRenderOffsetY())) {
            return true;
        }

        ModuleCategory[] categories = ModuleCategory.values();
        for (int i = 0; i < categories.length; i++) {
            ModuleCategory category = categories[i];
            float panelX = ClickGuiLayout.getCategoryPanelX(state.getX(), i);
            float contentY = ClickGuiLayout.getContentY(state.getY() + state.getRenderOffsetY());
            float contentHeight = ClickGuiLayout.getContentHeight();

            if (!HoveringUtils.isHovered(mouseX, mouseY, panelX, contentY, ClickGuiLayout.WIDTH, contentHeight)) {
                continue;
            }

            float moduleY = contentY + state.getScroll(category);
            List<Module> modules = state.getModules(category);
            for (int j = 0, size = modules.size(); j < size; j++) {
                Module module = modules.get(j);
                float openProgress = state.getOpenProgress(module);
                float moduleHeight = ClickGuiLayout.getModuleHeight(module, openProgress);

                if (HoveringUtils.isHovered(mouseX, mouseY, panelX + ClickGuiLayout.MODULE_PADDING, moduleY,
                        ClickGuiLayout.MODULE_INNER_WIDTH, ClickGuiLayout.MODULE_HEADER_HEIGHT)) {
                    if (button == 0) {
                        module.toggle();
                    } else if (button == 1) {
                        module.setOpen(!module.isOpen());
                        state.clampScroll(category, contentHeight);
                    } else if (button == 2) {
                        state.setBindingModule(module);
                    }
                    return true;
                }

                if (module.isOpen() && openProgress > 0.1f) {
                    List<Setting> settings = module.getSettings();
                    if (!settings.isEmpty() && handleSettingClick(mouseX, mouseY, button, panelX, moduleY, settings)) {
                        return true;
                    }
                }

                moduleY += ClickGuiLayout.MODULE_GAP + moduleHeight;
            }
        }

        return false;
    }

    public boolean mouseReleased(int button) {
        state.stopSearchSelection();
        if (button != 0) {
            return false;
        }

        List<Module> modules = state.getAllModules();
        for (int i = 0, size = modules.size(); i < size; i++) {
            List<Setting> settings = modules.get(i).getSettings();
            for (int j = 0, settingCount = settings.size(); j < settingCount; j++) {
                if (settings.get(j) instanceof FloatSetting floatSetting) {
                    floatSetting.setActive(false);
                    state.endSliderDrag(floatSetting);
                }
            }
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (button != 0 || !state.isSearchActive() || !state.isSearchDragging()) {
            return false;
        }

        int categoryCount = ModuleCategory.values().length;
        float searchX = ClickGuiLayout.getSearchX(state.getX(), categoryCount, getSearchWidth()) - 10;
        state.updateSearchSelection(getSearchIndexAt(mouseX, searchX));
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double verticalAmount) {
        Window window = Minecraft.getInstance().getWindow();
        if (window != null && themeSelector.handleScroll(window, mouseX, mouseY, verticalAmount)) {
            return true;
        }

        ModuleCategory[] categories = ModuleCategory.values();
        for (int i = 0; i < categories.length; i++) {
            ModuleCategory category = categories[i];
            float panelX = ClickGuiLayout.getCategoryPanelX(state.getX(), i);
            float contentY = ClickGuiLayout.getContentY(state.getY() + state.getRenderOffsetY());
            float contentHeight = ClickGuiLayout.getContentHeight();
            if (HoveringUtils.isHovered(mouseX, mouseY, panelX, contentY, ClickGuiLayout.WIDTH, contentHeight)) {
                state.addScroll(category, verticalAmount, contentHeight);
                return true;
            }
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int modifiers) {
        if (state.getEditingTextSetting() != null) {
            TextSetting textSetting = state.getEditingTextSetting();
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                state.setEditingTextSetting(null);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                String current = textSetting.get();
                if (current != null && !current.isEmpty()) {
                    textSetting.setText(current.substring(0, current.length() - 1));
                }
            }
            return true;
        }

        if (state.isSearchActive()) {
            Minecraft mc = Minecraft.getInstance();
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
                if (keyCode == GLFW.GLFW_KEY_A) {
                    state.selectAllSearchText();
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_C) {
                    if (state.hasSearchSelection() && mc.keyboardHandler != null) {
                        mc.keyboardHandler.setClipboard(state.getSelectedSearchText());
                    }
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_V) {
                    if (mc.keyboardHandler != null) {
                        state.replaceSearchSelection(mc.keyboardHandler.getClipboard());
                    }
                    return true;
                }
                if (keyCode == GLFW.GLFW_KEY_Z) {
                    state.restoreSearchUndo();
                    return true;
                }
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                state.setSearchActive(false);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                state.removeLastSearchChar();
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_DELETE) {
                state.clearSearchText();
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_LEFT) {
                state.setSearchCursor(state.getSearchCursor() - 1, (modifiers & GLFW.GLFW_MOD_SHIFT) != 0);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                state.setSearchCursor(state.getSearchCursor() + 1, (modifiers & GLFW.GLFW_MOD_SHIFT) != 0);
                return true;
            }
        }

        if (state.getBindingModule() != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                state.setBindingModule(null);
            } else if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                state.getBindingModule().setKey(-1);
                state.setBindingModule(null);
            } else {
                state.getBindingModule().setKey(keyCode);
                state.setBindingModule(null);
            }
            return true;
        }

        if (state.getBindingSetting() != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                state.setBindingSetting(null);
            } else if (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                state.getBindingSetting().setKey(-1);
                state.setBindingSetting(null);
            } else {
                state.getBindingSetting().setKey(keyCode);
                state.setBindingSetting(null);
            }
            return true;
        }

        return false;
    }

    public boolean charTyped(char chr) {
        if (state.getEditingTextSetting() != null) {
            if (!Character.isISOControl(chr)) {
                TextSetting textSetting = state.getEditingTextSetting();
                textSetting.setText(textSetting.get() + chr);
            }
            return true;
        }

        if (!state.isSearchActive()) {
            return false;
        }
        state.appendSearchChar(chr);
        return true;
    }

    private int getSearchIndexAt(double mouseX, float searchX) {
        String text = state.getSearchText();
        FontRenderer font = issue(14);
        float textX = searchX + ClickGuiLayout.SEARCH_TEXT_X;
        float localX = (float) mouseX - textX;
        if (localX <= 0f || text.isEmpty() || font == null) {
            return 0;
        }

        float previousWidth = 0f;
        for (int i = 1; i <= text.length(); i++) {
            float currentWidth = font.getWidth(text.substring(0, i));
            float midpoint = previousWidth + (currentWidth - previousWidth) * 0.5f;
            if (localX < midpoint) {
                return i - 1;
            }
            previousWidth = currentWidth;
        }
        return text.length();
    }

    private float getSearchWidth() {
        String query = state.getSearchText();
        String text = query.isEmpty() ? "Search..." : query;
        FontRenderer font = issue(14);
        float textWidth = font == null ? text.length() * 6f : font.getWidth(text);
        float contentWidth = ClickGuiLayout.SEARCH_TEXT_X + textWidth + ClickGuiLayout.SEARCH_RIGHT_PADDING;
        return Math.max(ClickGuiLayout.SEARCH_WIDTH, contentWidth);
    }

    private boolean handleSettingClick(double mouseX, double mouseY, int button, float panelX, float moduleY,
                                       List<Setting> settings) {
        float settingYoffset = ClickGuiLayout.SETTING_START_Y;
        FontRenderer chipFont = issue(12);

        for (int i = 0, size = settings.size(); i < size; i++) {
            Setting setting = settings.get(i);
            if (setting == null || !setting.visible()) {
                continue;
            }

            float settingY = moduleY + settingYoffset + ClickGuiLayout.SETTING_PADDING;

            if (setting instanceof BooleanSetting booleanSetting) {
                float toggleX = panelX + 75;
                float toggleY = settingY - 5;
                if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, toggleX, toggleY, 16, 9)) {
                    booleanSetting.setState(!booleanSetting.isState());
                    return true;
                }
                settingYoffset += 12f;

            } else if (setting instanceof TextSetting textSetting) {
                float boxX = panelX + 49f;
                float boxY = settingY - 5.5f;
                if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, boxX, boxY,
                        ClickGuiLayout.TEXT_SETTING_WIDTH, 9f)) {
                    state.setSearchActive(false);
                    state.stopSearchSelection();
                    state.setEditingTextSetting(textSetting);
                    return true;
                }
                settingYoffset += 22f;

            } else if (setting instanceof FloatSetting floatSetting) {
                float sliderX = panelX + ClickGuiLayout.SETTING_LEFT;
                float sliderY = settingY + 6;
                if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, sliderX, sliderY,
                        ClickGuiLayout.SLIDER_WIDTH, 4.5f)) {
                    floatSetting.setActive(true);
                    floatSetting.setValue(state.getSliderValue(floatSetting, sliderX, mouseX));
                    state.beginSliderDrag(floatSetting, mouseX);
                    return true;
                }
                settingYoffset += 22f;

            } else if (setting instanceof ModeSetting modeSetting) {
                float chipX = panelX + ClickGuiLayout.SETTING_LEFT - 1.5f;
                float chipY = settingY + ClickGuiLayout.TAG_START_Y - 0.5f;
                float chipHeight = ClickGuiLayout.getTagChipHeight();
                float availableWidth = ClickGuiLayout.getTagAvailableWidth();
                float offsetX = 0.0f;
                float offsetY = 0.0f;

                List<String> mods = modeSetting.getMods();
                for (int j = 0, modeCount = mods.size(); j < modeCount; j++) {
                    String mode = mods.get(j);
                    float chipWidth = (chipFont != null ? chipFont.getWidth(mode) : mode.length() * 6f) + 8f;
                    if (offsetX > 0.0f && offsetX + chipWidth > availableWidth) {
                        offsetX = 0.0f;
                        offsetY += chipHeight + ClickGuiLayout.TAG_ROW_GAP;
                    }
                    if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, chipX + offsetX, chipY + offsetY - 5,
                            chipWidth, chipHeight)) {
                        modeSetting.set(mode);
                        return true;
                    }
                    offsetX += chipWidth + ClickGuiLayout.TAG_GAP;
                }
                settingYoffset += ClickGuiLayout.calculateModeSettingHeight(modeSetting);

            } else if (setting instanceof ListSetting listSetting) {
                float chipX = panelX + ClickGuiLayout.SETTING_LEFT - 1.5f;
                float chipY = settingY + ClickGuiLayout.TAG_START_Y - 0.5f;
                float chipHeight = ClickGuiLayout.getTagChipHeight();
                float availableWidth = ClickGuiLayout.getTagAvailableWidth();
                float offsetX = 0.0f;
                float offsetY = 0.0f;

                List<BooleanSetting> entries = listSetting.getSettings();
                for (int j = 0, entryCount = entries.size(); j < entryCount; j++) {
                    BooleanSetting entry = entries.get(j);
                    if (!entry.visible()) {
                        continue;
                    }
                    String label = entry.name();
                    float chipWidth = (chipFont != null ? chipFont.getWidth(label) : label.length() * 6f) + 8f;
                    if (offsetX > 0.0f && offsetX + chipWidth > availableWidth) {
                        offsetX = 0.0f;
                        offsetY += chipHeight + ClickGuiLayout.TAG_ROW_GAP;
                    }
                    if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, chipX + offsetX, chipY + offsetY - 5,
                            chipWidth, chipHeight)) {
                        entry.setState(!entry.isState());
                        return true;
                    }
                    offsetX += chipWidth + ClickGuiLayout.TAG_GAP;
                }
                settingYoffset += ClickGuiLayout.calculateListSettingHeight(listSetting);

            } else if (setting instanceof BindSetting bindSetting) {
                String bindString = state.getBindingSetting() == bindSetting
                        ? "..."
                        : state.toEnglish(KeyBoardUtils.getBindName(bindSetting.getKey()));
                float bindWidth = (chipFont != null ? chipFont.getWidth(bindString) : bindString.length() * 6f) + 6f;
                float bindX = panelX + ClickGuiLayout.SETTING_RIGHT - bindWidth;
                float bindY = settingY - 5.5f;
                if (button == 0 && HoveringUtils.isHovered(mouseX, mouseY, bindX, bindY, bindWidth, 9)) {
                    state.setBindingSetting(bindSetting);
                    return true;
                }
                settingYoffset += 12f;
            }
        }
        return false;
    }

    private FontRenderer issue(int size) {
        return Fonts.Font5.get(size);
    }
}
