package godweer.ui.clickgui;

import com.mojang.blaze3d.platform.Window;

import godweer.managers.ModuleManager;
import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BindSetting;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.TextSetting;
import godweer.utils.animation.AnimationUtils;
import godweer.utils.animation.Easings;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ClickGuiState {

    private static final Map<Character, Character> RU_TO_EN = new HashMap<>();

    static {
        String ru = "йцукенгшщзхъфывапролджэячсмитьбю"
                + "ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ";
        String en = "qwertyuiop[]asdfghjkl;'zxcvbnm,.QWERTYUIOP[]ASDFGHJKL;'ZXCVBNM,.";
        int length = Math.min(ru.length(), en.length());
        for (int i = 0; i < length; i++) {
            RU_TO_EN.put(ru.charAt(i), en.charAt(i));
        }
    }

    private final Map<Module, Float> dotsRotation = new HashMap<>();
    private final Map<Module, AnimationUtils> moduleOpenAnimation = new HashMap<>();
    private final Map<BooleanSetting, AnimationUtils> booleanBackgroundAnimation = new HashMap<>();
    private final Map<BooleanSetting, AnimationUtils> booleanCircleAnimation = new HashMap<>();
    private final Map<FloatSetting, AnimationUtils> sliderAnimation = new HashMap<>();
    private final Map<FloatSetting, Double> sliderDragMouseX = new HashMap<>();
    private final Map<FloatSetting, Double> sliderDragRemainder = new HashMap<>();
    private final Map<String, AnimationUtils> modeAnimation = new HashMap<>();
    private final Map<String, AnimationUtils> listAnimation = new HashMap<>();
    private final Map<String, AnimationUtils> bindAnimation = new HashMap<>();
    private final Map<String, AnimationUtils> textHoverAnimation = new HashMap<>();
    private final Map<String, Float> textScrollPhase = new HashMap<>();
    private final Map<String, Boolean> textScrollFinishing = new HashMap<>();
    private final Map<String, Boolean> textScrollHovered = new HashMap<>();
    private final Map<ModuleCategory, Float> categoryScrollTarget = new EnumMap<>(ModuleCategory.class);
    private final Map<ModuleCategory, AnimationUtils> categoryScrollAnimation = new EnumMap<>(ModuleCategory.class);
    private final Map<ModuleCategory, List<Module>> modulesByCategory = new EnumMap<>(ModuleCategory.class);
    private final Map<ModuleCategory, List<Module>> filteredByCategory = new EnumMap<>(ModuleCategory.class);
    private final List<Module> allModules = new ArrayList<>();

    private String filterQuery = "";

    private float x;
    private float y;
    private BindSetting bindingSetting;
    private TextSetting editingTextSetting;
    private Module bindingModule;
    private float renderOffsetY;
    private boolean searchActive;
    private String searchText = "";
    private String undoSearchText = "";
    private int searchCursor;
    private int searchSelectionAnchor;
    private int searchSelectionCursor;
    private boolean searchDragging;

    public ClickGuiState() {
        refreshModules();
    }

    public void refreshModules() {
        allModules.clear();
        allModules.addAll(ModuleManager.getModules());
        filterQuery = "";
        filteredByCategory.clear();

        for (ModuleCategory category : ModuleCategory.values()) {
            List<Module> categoryModules = new ArrayList<>();
            for (int i = 0, size = allModules.size(); i < size; i++) {
                Module module = allModules.get(i);
                if (module.getCategory() == category) {
                    categoryModules.add(module);
                }
            }
            modulesByCategory.put(category, categoryModules);
            categoryScrollTarget.putIfAbsent(category, 0f);
            categoryScrollAnimation.putIfAbsent(category, new AnimationUtils(0f, 8f, Easings.CUBIC_OUT));
        }
    }

    public void updatePosition(Window window, int categoryCount) {
        float totalCategoriesWidth = ClickGuiLayout.getTotalCategoriesWidth(categoryCount);
        this.x = (window.getGuiScaledWidth() / 2f) - (totalCategoriesWidth / 2f);
        this.y = (window.getGuiScaledHeight() / 2f) - (ClickGuiLayout.HEIGHT / 2f);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getRenderOffsetY() {
        return this.renderOffsetY;
    }

    public void setRenderOffsetY(float renderOffsetY) {
        this.renderOffsetY = renderOffsetY;
    }

    // Метод зовётся из render/input-циклов много раз за кадр, поэтому результат фильтра
    // кэшируется и пересобирается только при смене поискового запроса.
    public List<Module> getModules(ModuleCategory category) {
        List<Module> modules = modulesByCategory.get(category);
        if (modules == null) {
            return List.of();
        }
        if (searchText.isBlank()) {
            return modules;
        }

        if (!searchText.equals(filterQuery)) {
            rebuildFilter();
        }

        List<Module> filtered = filteredByCategory.get(category);
        return filtered == null ? List.of() : filtered;
    }

    private void rebuildFilter() {
        filterQuery = searchText;
        String query = searchText.toLowerCase(Locale.ROOT);

        for (ModuleCategory category : ModuleCategory.values()) {
            List<Module> modules = modulesByCategory.get(category);
            List<Module> target = filteredByCategory.computeIfAbsent(category, key -> new ArrayList<>());
            target.clear();
            if (modules == null) {
                continue;
            }
            for (int i = 0, size = modules.size(); i < size; i++) {
                Module module = modules.get(i);
                if (matchesQuery(module, query)) {
                    target.add(module);
                }
            }
        }
    }

    private boolean matchesQuery(Module module, String query) {
        if (module.getName().toLowerCase(Locale.ROOT).contains(query)) {
            return true;
        }
        if (module.getDisplayName().toLowerCase(Locale.ROOT).contains(query)) {
            return true;
        }
        String description = module.getDisplayDescription();
        return description != null && description.toLowerCase(Locale.ROOT).contains(query);
    }

    public List<Module> getAllModules() {
        return this.allModules;
    }

    public String toEnglish(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char chr = text.charAt(i);
            result.append(RU_TO_EN.getOrDefault(chr, chr));
        }
        return result.toString();
    }

    public float getSliderPos(FloatSetting setting) {
        float delta = setting.getMax() - setting.getMin();
        if (delta <= 0f) {
            return 0f;
        }
        return (setting.get() - setting.getMin()) / delta;
    }

    public float getSliderValue(FloatSetting setting, float posX, double mouseX) {
        float delta = setting.getMax() - setting.getMin();
        float clickedX = (float) mouseX - posX;
        float value = Math.max(0f, Math.min(1f, clickedX / ClickGuiLayout.SLIDER_WIDTH));
        float outValue = setting.getMin() + delta * value;
        float increment = setting.getIncrement();
        outValue = Math.round(outValue / increment) * increment;
        return Math.max(setting.getMin(), Math.min(setting.getMax(), outValue));
    }

    public void beginSliderDrag(FloatSetting setting, double mouseX) {
        sliderDragMouseX.put(setting, mouseX);
        sliderDragRemainder.put(setting, 0.0D);
    }

    public void endSliderDrag(FloatSetting setting) {
        sliderDragMouseX.remove(setting);
        sliderDragRemainder.remove(setting);
    }

    public float updateActiveSliderValue(FloatSetting setting, double mouseX) {
        double lastMouseX = sliderDragMouseX.getOrDefault(setting, mouseX);
        sliderDragMouseX.put(setting, mouseX);

        double deltaX = mouseX - lastMouseX;
        if (Math.abs(deltaX) < 0.0001D) {
            return setting.get();
        }

        float range = setting.getMax() - setting.getMin();
        float increment = setting.getIncrement();
        if (range <= 0.0f || increment <= 0.0f) {
            return setting.get();
        }

        double steps = range / increment;
        if (steps <= 0.0D) {
            return setting.get();
        }

        double pixelsPerStep = ClickGuiLayout.SLIDER_WIDTH / steps;
        if (pixelsPerStep <= 0.0D) {
            return setting.get();
        }

        double accumulated = sliderDragRemainder.getOrDefault(setting, 0.0D) + deltaX;
        int wholeSteps = (int) (accumulated / pixelsPerStep);
        if (wholeSteps == 0) {
            sliderDragRemainder.put(setting, accumulated);
            return setting.get();
        }

        sliderDragRemainder.put(setting, accumulated - wholeSteps * pixelsPerStep);

        float value = setting.get() + wholeSteps * increment;
        value = Math.round(value / increment) * increment;
        return Math.max(setting.getMin(), Math.min(setting.getMax(), value));
    }

    public float getScroll(ModuleCategory category) {
        AnimationUtils animation = categoryScrollAnimation.computeIfAbsent(
                category, key -> new AnimationUtils(0f, 8f, Easings.CUBIC_OUT));
        animation.update(categoryScrollTarget.getOrDefault(category, 0f));
        return animation.getValue();
    }

    public void clampScroll(ModuleCategory category, float contentHeight) {
        float totalHeight = getTotalModulesHeight(category);
        float maxScroll = Math.min(0f, contentHeight - totalHeight);
        float currentTarget = categoryScrollTarget.getOrDefault(category, 0f);
        if (currentTarget < maxScroll || currentTarget > 0f) {
            categoryScrollTarget.put(category, Math.max(maxScroll, Math.min(0f, currentTarget)));
        }
    }

    public void addScroll(ModuleCategory category, double verticalAmount, float contentHeight) {
        float totalHeight = getTotalModulesHeight(category);
        float maxScroll = Math.min(0f, contentHeight - totalHeight);
        float currentTarget = categoryScrollTarget.getOrDefault(category, 0f);
        float newTarget = currentTarget + (float) (verticalAmount * 20);
        categoryScrollTarget.put(category, Math.max(maxScroll, Math.min(0f, newTarget)));
    }

    public float getTotalModulesHeight(ModuleCategory category) {
        List<Module> modules = getModules(category);
        float totalHeight = 0f;
        for (int i = 0, size = modules.size(); i < size; i++) {
            Module module = modules.get(i);
            totalHeight += ClickGuiLayout.MODULE_GAP + ClickGuiLayout.getModuleHeight(module, getOpenProgress(module));
        }
        return totalHeight;
    }

    public float getOpenProgress(Module module) {
        AnimationUtils animation = moduleOpenAnimation.computeIfAbsent(
                module,
                key -> new AnimationUtils(module.isOpen() ? 1f : 0f, 14f, Easings.CUBIC_OUT)
        );
        animation.update(module.isOpen() ? 1f : 0f);
        return animation.getValue();
    }

    public float updateDotsRotation(Module module, float targetAngle) {
        float currentAngle = dotsRotation.getOrDefault(module, targetAngle);
        currentAngle += (targetAngle - currentAngle) * 0.06f;
        if (Math.abs(targetAngle - currentAngle) < 0.001f) {
            currentAngle = targetAngle;
        }
        dotsRotation.put(module, currentAngle);
        return currentAngle;
    }

    public AnimationUtils getBooleanBackgroundAnimation(BooleanSetting setting) {
        return booleanBackgroundAnimation.computeIfAbsent(
                setting,
                key -> new AnimationUtils(setting.isState() ? 1f : 0f, 15.0f, Easings.CUBIC_OUT)
        );
    }

    public AnimationUtils getBooleanCircleAnimation(BooleanSetting setting) {
        return booleanCircleAnimation.computeIfAbsent(
                setting,
                key -> new AnimationUtils(setting.isState() ? 1f : 0f, 8.2f, Easings.BACK_OUT)
        );
    }

    public AnimationUtils getSliderAnimation(FloatSetting setting) {
        return sliderAnimation.computeIfAbsent(
                setting,
                key -> new AnimationUtils(getSliderPos(setting), 12.0f, Easings.CUBIC_OUT)
        );
    }

    public AnimationUtils getModeAnimation(String key, boolean selected) {
        return modeAnimation.computeIfAbsent(
                key,
                unused -> new AnimationUtils(selected ? 1f : 0f, 10.0f, Easings.CUBIC_OUT)
        );
    }

    public AnimationUtils getListAnimation(String key, boolean selected) {
        return listAnimation.computeIfAbsent(
                key,
                unused -> new AnimationUtils(selected ? 1f : 0f, 10.0f, Easings.CUBIC_OUT)
        );
    }

    public AnimationUtils getBindAnimation(String key, boolean binding) {
        return bindAnimation.computeIfAbsent(
                key,
                unused -> new AnimationUtils(binding ? 1f : 0f, 10.0f, Easings.CUBIC_OUT)
        );
    }

    public AnimationUtils getTextHoverAnimation(String key, boolean hovered) {
        return textHoverAnimation.computeIfAbsent(
                key,
                unused -> new AnimationUtils(hovered ? 1f : 0f, 9.0f, Easings.CUBIC_OUT)
        );
    }

    public float advanceTextScrollPhase(String key, boolean hovered) {
        float phase = textScrollPhase.getOrDefault(key, 0.0f);
        boolean wasHovered = textScrollHovered.getOrDefault(key, false);
        boolean finishing = textScrollFinishing.getOrDefault(key, false);

        if (hovered) {
            phase += 0.004f;
            if (phase > 1.0f) {
                phase -= 1.0f;
            }
            finishing = false;
        } else {
            if (wasHovered && phase > 0.0f) {
                finishing = true;
            }
            if (finishing) {
                phase += 0.004f;
                if (phase >= 1.0f) {
                    phase = 0.0f;
                    finishing = false;
                }
            }
        }

        textScrollHovered.put(key, hovered);
        textScrollFinishing.put(key, finishing);
        textScrollPhase.put(key, phase);
        return phase;
    }

    public boolean isTextScrollActive(String key, boolean hovered) {
        return hovered || textScrollFinishing.getOrDefault(key, false);
    }

    public BindSetting getBindingSetting() {
        return this.bindingSetting;
    }

    public void setBindingSetting(BindSetting bindingSetting) {
        this.bindingSetting = bindingSetting;
    }

    public Module getBindingModule() {
        return this.bindingModule;
    }

    public void setBindingModule(Module bindingModule) {
        this.bindingModule = bindingModule;
    }

    public TextSetting getEditingTextSetting() {
        return this.editingTextSetting;
    }

    public void setEditingTextSetting(TextSetting editingTextSetting) {
        this.editingTextSetting = editingTextSetting;
    }

    public boolean isSearchActive() {
        return this.searchActive;
    }

    public void setSearchActive(boolean searchActive) {
        this.searchActive = searchActive;
    }

    public String getSearchText() {
        return this.searchText;
    }

    public void appendSearchChar(char chr) {
        if (Character.isISOControl(chr)
                || (searchText.length() >= ClickGuiLayout.SEARCH_MAX_CHARS && !hasSearchSelection())) {
            return;
        }
        replaceSearchSelection(String.valueOf(chr));
    }

    public void removeLastSearchChar() {
        if (hasSearchSelection()) {
            replaceSearchSelection("");
            return;
        }
        if (searchCursor > 0) {
            rememberSearchUndo();
            searchText = searchText.substring(0, searchCursor - 1) + searchText.substring(searchCursor);
            searchCursor--;
            clearSearchSelection();
        }
    }

    public void clearSearchText() {
        rememberSearchUndo();
        searchText = "";
        searchCursor = 0;
        clearSearchSelection();
    }

    public void setSearchText(String searchText) {
        rememberSearchUndo();
        this.searchText = sanitizeSearchText(searchText);
        this.searchCursor = this.searchText.length();
        clearSearchSelection();
    }

    public void restoreSearchUndo() {
        String current = searchText;
        searchText = undoSearchText == null ? "" : undoSearchText;
        undoSearchText = current;
        searchCursor = searchText.length();
        clearSearchSelection();
    }

    public int getSearchCursor() {
        return this.searchCursor;
    }

    public int getSearchSelectionStart() {
        return Math.min(searchSelectionAnchor, searchSelectionCursor);
    }

    public int getSearchSelectionEnd() {
        return Math.max(searchSelectionAnchor, searchSelectionCursor);
    }

    public boolean hasSearchSelection() {
        return getSearchSelectionStart() != getSearchSelectionEnd();
    }

    public String getSelectedSearchText() {
        if (!hasSearchSelection()) {
            return "";
        }
        return searchText.substring(getSearchSelectionStart(), getSearchSelectionEnd());
    }

    public void selectAllSearchText() {
        searchSelectionAnchor = 0;
        searchSelectionCursor = searchText.length();
        searchCursor = searchText.length();
    }

    public void setSearchCursor(int cursor, boolean keepSelection) {
        searchCursor = clampSearchIndex(cursor);
        if (keepSelection) {
            searchSelectionCursor = searchCursor;
        } else {
            searchSelectionAnchor = searchCursor;
            searchSelectionCursor = searchCursor;
        }
    }

    public void startSearchSelection(int index) {
        searchCursor = clampSearchIndex(index);
        searchSelectionAnchor = searchCursor;
        searchSelectionCursor = searchCursor;
        searchDragging = true;
    }

    public void updateSearchSelection(int index) {
        if (!searchDragging) {
            return;
        }
        searchCursor = clampSearchIndex(index);
        searchSelectionCursor = searchCursor;
    }

    public void stopSearchSelection() {
        searchDragging = false;
    }

    public boolean isSearchDragging() {
        return this.searchDragging;
    }

    public void replaceSearchSelection(String text) {
        rememberSearchUndo();
        String insert = sanitizeSearchText(text);
        int selectionStart = getSearchSelectionStart();
        int selectionEnd = getSearchSelectionEnd();
        if (!hasSearchSelection()) {
            selectionStart = searchCursor;
            selectionEnd = searchCursor;
        }
        int available = Math.max(0,
                ClickGuiLayout.SEARCH_MAX_CHARS - (searchText.length() - (selectionEnd - selectionStart)));
        if (insert.length() > available) {
            insert = insert.substring(0, available);
        }
        searchText = searchText.substring(0, selectionStart) + insert + searchText.substring(selectionEnd);
        searchCursor = selectionStart + insert.length();
        clearSearchSelection();
    }

    private void clearSearchSelection() {
        searchSelectionAnchor = searchCursor;
        searchSelectionCursor = searchCursor;
        searchDragging = false;
    }

    private int clampSearchIndex(int index) {
        return Math.max(0, Math.min(searchText.length(), index));
    }

    private void rememberSearchUndo() {
        undoSearchText = searchText;
    }

    private String sanitizeSearchText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length() && builder.length() < ClickGuiLayout.SEARCH_MAX_CHARS; i++) {
            char chr = text.charAt(i);
            if (!Character.isISOControl(chr)) {
                builder.append(chr);
            }
        }
        return builder.toString();
    }
}
