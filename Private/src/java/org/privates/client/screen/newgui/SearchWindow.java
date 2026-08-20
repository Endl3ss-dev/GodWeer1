package org.privates.client.screen.newgui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.Excellent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuppressWarnings("unchecked")
public class SearchWindow {
    private final ClickGuiScreen parent;
    private float x, y, width, height;
    private float alpha;
    private float animation;
    private float scaleAnimation = 0f;
    private boolean open;
    private String searchText = "";
    private final List<SearchResult> searchResults = new ArrayList<>();
    private static final List<Module> recentModules = new ArrayList<>();
    private float scrollY;
    private float targetScrollY;
    private int selectedIndex = 0;
    private float cursorBlinkTimer = 0;
    private final float[] itemAnimations = new float[128];
    private final float[] itemSlideAnimations = new float[128];
    private final float[] itemScaleAnimations = new float[128];
    private Module highlightedModule = null;
    private float inputFocusAnimation = 0f;
    private float contentHeightAnimation = 0f;
    private float targetContentHeight = 0f;

    public SearchWindow(ClickGuiScreen parent) {
        this.parent = parent;
    }

    public void init() {
        searchText = "";
        searchResults.clear();
        scrollY = 0;
        targetScrollY = 0;
        selectedIndex = 0;
        cursorBlinkTimer = 0;
        highlightedModule = null;
        inputFocusAnimation = 0f;
        contentHeightAnimation = 0f;
        targetContentHeight = 0f;
        scaleAnimation = 0f;
        for (int i = 0; i < itemAnimations.length; i++) {
            itemAnimations[i] = 0;
            itemSlideAnimations[i] = 0;
            itemScaleAnimations[i] = 0;
        }
    }

    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (!open) {
            animation = Mathf.fast(animation, 0f, 14f);
            scaleAnimation = Mathf.fast(scaleAnimation, 0f, 16f);
            if (animation < 0.01f) return;
        } else {
            animation = Mathf.fast(animation, 1f, 14f);
            scaleAnimation = Mathf.fast(scaleAnimation, 1f, 12f);
        }

        alpha = animation;
        if (alpha < 0.01f) return;

        cursorBlinkTimer += partialTicks;
        inputFocusAnimation = Mathf.fast(inputFocusAnimation, open ? 1f : 0f, 10f);

        float sScale = parent.getResolutionScale();
        width = parent.getDynamicIsland().getWidth();

        float itemHeight = 22 * sScale;
        float itemSpacing = 2 * sScale;
        float padding = 10 * sScale;
        float inputHeight = 32 * sScale;

        int itemCount = searchText.isEmpty() ? recentModules.size() : searchResults.size();
        targetContentHeight = itemCount > 0 ? itemCount * (itemHeight + itemSpacing) - itemSpacing : 50 * sScale;
        contentHeightAnimation = Mathf.fast(contentHeightAnimation, targetContentHeight, 16f);

        height = inputHeight + contentHeightAnimation + padding * 2;

        x = Minecraft.getInstance().getMainWindow().getScaledWidth() / 2f - width / 2f;
        y = parent.getDynamicIsland().getY() + parent.getDynamicIsland().getHeight() + 6 * sScale;

        float centerX = x + width / 2f;
        float centerY = y + height / 2f;

        stack.push();
        stack.translate(centerX, centerY, 0);
        float easedScale = easeOutBack(scaleAnimation);
        stack.scale(easedScale, easedScale, 1f);
        stack.translate(-centerX, -centerY, 0);

        Theme theme = Theme.getInstance();

        // Фон с блюром
        RenderUtil.CustomStyled.draw(stack, x, y, width, height, Round.of(10 * sScale), true, 15f);

        // Обводка окна
        int borderColor = ColorUtil.getColor(255, 255, 255, (int) (10 * alpha));
        RenderUtil.Rounded.roundedOutline(stack, x, y, width, height, 0.5f * sScale, borderColor, Round.of(10 * sScale));

        float inputY = y + padding;
        float inputX = x + padding;
        float inputW = width - padding * 2;

        // Фон поля ввода (немного ярче при фокусе)
        int inputBgColor = ColorUtil.getColor(255, 255, 255, (int) ((5 + inputFocusAnimation * 5) * alpha));
        RenderUtil.Rounded.roundedRect(stack, inputX, inputY, inputW, inputHeight, inputBgColor, Round.of(8 * sScale));

        // Обводка поля ввода цветом темы при фокусе
        if (inputFocusAnimation > 0.01f) {
            int focusColor = ColorUtil.multAlpha(theme.clientColor(), inputFocusAnimation * alpha);
            RenderUtil.Rounded.roundedOutline(stack, inputX, inputY, inputW, inputHeight, sScale, focusColor, Round.of(8 * sScale));
        }

        float textStartX = inputX + 12 * sScale;
        float fontSize = 8.5f * sScale;

        if (searchText.isEmpty()) {
            int placeholderColor = ColorUtil.getColor(120, 120, 120, (int) (255 * alpha));
            String placeholder = "Search modules...";
            float phY = inputY + inputHeight / 2f - Fonts.SF_REGULAR.getHeight(placeholder, fontSize) / 2f;
            Fonts.SF_REGULAR.draw(stack, placeholder, textStartX, phY, placeholderColor, fontSize);
        } else {
            int textColor = ColorUtil.getColor(255, 255, 255, (int) (255 * alpha));
            float tY = inputY + inputHeight / 2f - Fonts.SF_REGULAR.getHeight(searchText, fontSize) / 2f;
            Fonts.SF_REGULAR.draw(stack, searchText, textStartX, tY, textColor, fontSize);
        }

        // Мигающий курсор
        boolean showCursor = open && ((int) (cursorBlinkTimer * 2.5f)) % 2 == 0;
        if (showCursor) {
            float cursorX = textStartX + (searchText.isEmpty() ? 0 : Fonts.SF_REGULAR.getWidth(searchText, fontSize)) + 1.5f * sScale;
            float cursorY2 = inputY + inputHeight / 2f - 6 * sScale;
            int cursorColor = ColorUtil.multAlpha(theme.clientColor(), alpha);
            RenderUtil.Rounded.roundedRect(stack, cursorX, cursorY2, sScale, 12 * sScale, cursorColor, Round.of(sScale));
        }

        float listStartY = inputY + inputHeight + 6 * sScale;
        float listHeight = contentHeightAnimation;

        if (listHeight > 1f) {
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, listStartY, width, listHeight);
            renderList(stack, x, listStartY, width, listHeight, sScale, mouseX, mouseY, itemHeight, itemSpacing);
            ScissorExp.unset();
            ScissorExp.pop();
        }

        stack.pop();
    }

    private float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1f + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    }

    private float easeOutCubic(float t) {
        return 1f - (float) Math.pow(1 - t, 3);
    }

    private void renderList(MatrixStack stack, float x, float startY, float width, float height,
                            float sScale, int mouseX, int mouseY, float itemHeight, float itemSpacing) {
        Theme theme = Theme.getInstance();
        List<SearchResult> listToRender = searchText.isEmpty()
                ? recentModules.stream().map(m -> new SearchResult(m, null, null)).toList()
                : new ArrayList<>(searchResults);

        if (listToRender.isEmpty()) {
            float centerX = x + width / 2f;
            float centerY = startY + height / 2f;

            String emptyTitle = searchText.isEmpty() ? "No recent searches" : "No results found";
            String emptySubtitle = searchText.isEmpty() ? "Start typing to search" : "Try different keywords";

            int titleColor = ColorUtil.getColor(180, 180, 180, (int) (255 * alpha));
            int subtitleColor = ColorUtil.getColor(120, 120, 120, (int) (255 * alpha));

            float titleWidth = Fonts.SF_REGULAR.getWidth(emptyTitle, 8 * sScale);
            float subtitleWidth = Fonts.SF_REGULAR.getWidth(emptySubtitle, 6.5f * sScale);

            Fonts.SF_REGULAR.draw(stack, emptyTitle, centerX - titleWidth / 2f, centerY - 8 * sScale, titleColor, 8 * sScale);
            Fonts.SF_REGULAR.draw(stack, emptySubtitle, centerX - subtitleWidth / 2f, centerY + 2 * sScale, subtitleColor, 6.5f * sScale);
            return;
        }

        float totalHeight = listToRender.size() * (itemHeight + itemSpacing);
        float maxScroll = Math.max(0, totalHeight - height);
        targetScrollY = Math.min(0, Math.max(targetScrollY, -maxScroll));
        scrollY = Mathf.fast(scrollY, targetScrollY, 18);

        float itemY = startY + scrollY;
        float itemWidth = width - 20 * sScale;
        float itemX = x + 10 * sScale;

        int i = 0;
        for (SearchResult result : listToRender) {
            if (itemY + itemHeight >= startY - itemHeight && itemY <= startY + height + itemHeight) {
                boolean hovered = mouseX >= itemX && mouseX <= itemX + itemWidth
                        && mouseY >= itemY && mouseY <= itemY + itemHeight;
                boolean selected = i == selectedIndex;

                float targetAnim = (hovered || selected) ? 1f : 0f;

                if (i < itemAnimations.length) {
                    itemAnimations[i] = Mathf.fast(itemAnimations[i], targetAnim, 14);
                    itemSlideAnimations[i] = Mathf.fast(itemSlideAnimations[i], 1f, 10);
                    itemScaleAnimations[i] = Mathf.fast(itemScaleAnimations[i], 1f, 12);
                }

                float itemAnim = i < itemAnimations.length ? itemAnimations[i] : 0;
                float slideAnim = i < itemSlideAnimations.length ? easeOutCubic(itemSlideAnimations[i]) : 1f;
                float scaleAnim = i < itemScaleAnimations.length ? itemScaleAnimations[i] : 1f;

                float slideOffset = (1f - slideAnim) * 20 * sScale;
                float currentItemY = itemY + slideOffset;

                stack.push();
                float itemCenterX = itemX + itemWidth / 2f;
                float itemCenterY = currentItemY + itemHeight / 2f;
                stack.translate(itemCenterX, itemCenterY, 0);
                stack.scale(scaleAnim, scaleAnim, 1f);
                stack.translate(-itemCenterX, -itemCenterY, 0);

                // Подсветка при наведении или выборе
                if (itemAnim > 0.01f) {
                    int selectColor;
                    if (selected) {
                        selectColor = ColorUtil.multAlpha(theme.clientColor(), (0.15f + itemAnim * 0.1f) * alpha);
                    } else {
                        selectColor = ColorUtil.getColor(255, 255, 255, (int) ((20 * itemAnim) * alpha));
                    }
                    RenderUtil.Rounded.roundedRect(stack, itemX, currentItemY, itemWidth, itemHeight, selectColor, Round.of(6 * sScale));
                }

                float textX = itemX + 8 * sScale;
                float textY = currentItemY + itemHeight / 2f;

                String moduleName = result.module.getName();
                float nameSize = 8f * sScale;
                int nameColor = ColorUtil.getColor(255, 255, 255,
                        (int) ((selected ? 255 : 220) * alpha * slideAnim));

                if (result.foundIn != null) {
                    // Имя модуля сверху
                    float nameY = textY - nameSize - 1 * sScale;
                    Fonts.SF_REGULAR.draw(stack, moduleName, textX, nameY, nameColor, nameSize);

                    // Подпись: откуда нашли (человекочитаемый текст)
                    float subtitleSize = 6.5f * sScale;
                    int subtitleColor = ColorUtil.getColor(150, 150, 150,
                            (int) (255 * alpha * slideAnim));
                    Fonts.SF_REGULAR.draw(stack, result.foundInLabel, textX, textY + 1 * sScale, subtitleColor, subtitleSize);
                } else {
                    // Просто имя по центру (из недавних)
                    float nameY = textY - Fonts.SF_REGULAR.getHeight(moduleName, nameSize) / 2f;
                    Fonts.SF_REGULAR.draw(stack, moduleName, textX, nameY, nameColor, nameSize);
                }

                // Стрелка-индикатор для выбранного элемента
                if (selected && itemAnim > 0.5f) {
                    float arrowX = itemX + itemWidth - 12 * sScale;
                    float arrowY = currentItemY + itemHeight / 2f - 4 * sScale;
                    int arrowColor = ColorUtil.multAlpha(theme.clientColor(), itemAnim * alpha * slideAnim);
                }

                stack.pop();
            }
            itemY += itemHeight + itemSpacing;
            i++;
        }
    }

    public void updateSearch() {
        searchResults.clear();
        highlightedModule = null;

        // Сброс анимаций при новом поиске
        for (int i = 0; i < itemSlideAnimations.length; i++) {
            itemSlideAnimations[i] = 0;
            itemScaleAnimations[i] = 0.8f;
        }

        if (searchText.isEmpty()) return;

        String lower = searchText.toLowerCase();

        for (Category category : Category.values()) {
            for (Module m : Excellent.inst().moduleManager().get(category)) {
                String foundIn = null;
                String foundInLabel = null;

                String moduleLower = m.getName().toLowerCase();
                String descLower = m.getModuleInfo().description().toLowerCase();

                if (moduleLower.equals(lower)) {
                    // Точное совпадение по имени — самый высокий приоритет
                    foundIn = "name_exact";
                    foundInLabel = "Exact match";
                } else if (moduleLower.startsWith(lower)) {
                    // Имя начинается с запроса
                    foundIn = "name_starts";
                    foundInLabel = "Matches name";
                } else if (moduleLower.contains(lower)) {
                    // Имя содержит запрос
                    foundIn = "name_contains";
                    foundInLabel = "Found in name";
                } else if (descLower.contains(lower)) {
                    // Найдено в описании модуля
                    foundIn = "description";
                    foundInLabel = "Found in description";
                }

                if (foundIn != null) {
                    searchResults.add(new SearchResult(m, foundIn, foundInLabel));
                }
            }
        }

        // Сортировка: сначала точные совпадения по имени, потом по началу, потом содержащие, потом по описанию
        searchResults.sort((a, b) -> {
            int pa = matchPriority(a.foundIn);
            int pb = matchPriority(b.foundIn);
            if (pa != pb) return Integer.compare(pa, pb);
            return a.module.getName().compareToIgnoreCase(b.module.getName());
        });

        if (!searchResults.isEmpty()) {
            highlightedModule = searchResults.get(0).module;
        }

        selectedIndex = 0;
        targetScrollY = 0;
    }

    private int matchPriority(String foundIn) {
        return switch (foundIn) {
            case "name_exact" -> 0;
            case "name_starts" -> 1;
            case "name_contains" -> 2;
            case "description" -> 3;
            default -> 4;
        };
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (!open) return;

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (!searchText.isEmpty()) {
                searchText = searchText.substring(0, searchText.length() - 1);
                updateSearch();
            }
        } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
            activateSelected();
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            int max = searchText.isEmpty() ? recentModules.size() : searchResults.size();
            if (max > 0) {
                selectedIndex = Math.max(0, selectedIndex - 1);
                updateHighlight();
                ensureSelectedVisible();
            }
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            int max = searchText.isEmpty() ? recentModules.size() : searchResults.size();
            if (max > 0) {
                selectedIndex = Math.min(max - 1, selectedIndex + 1);
                updateHighlight();
                ensureSelectedVisible();
            }
        } else if (keyCode != GLFW.GLFW_KEY_ESCAPE && keyCode != GLFW.GLFW_KEY_LEFT_CONTROL) {
            if (typedChar >= 32 && typedChar != 127) {
                searchText += typedChar;
                updateSearch();
            }
        }
    }

    private void updateHighlight() {
        if (searchText.isEmpty()) {
            if (selectedIndex >= 0 && selectedIndex < recentModules.size()) {
                highlightedModule = recentModules.get(selectedIndex);
            }
        } else {
            if (selectedIndex >= 0 && selectedIndex < searchResults.size()) {
                highlightedModule = searchResults.get(selectedIndex).module;
            }
        }
    }

    private void ensureSelectedVisible() {
        float sScale = parent.getResolutionScale();
        float itemHeight = 22 * sScale;
        float itemSpacing = 2 * sScale;
        float selectedTop = selectedIndex * (itemHeight + itemSpacing);
        float selectedBottom = selectedTop + itemHeight;

        int itemCount = searchText.isEmpty() ? recentModules.size() : searchResults.size();
        float contentHeight = itemCount > 0 ? itemCount * (itemHeight + itemSpacing) - itemSpacing : 50 * sScale;
        float visibleHeight = contentHeight;

        float currentTop = -scrollY;
        float currentBottom = currentTop + visibleHeight;

        if (selectedTop < currentTop) {
            targetScrollY = -selectedTop;
        } else if (selectedBottom > currentBottom) {
            targetScrollY = -(selectedBottom - visibleHeight);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!open) return;

        float sScale = parent.getResolutionScale();

        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) return;

        float padding = 10 * sScale;
        float inputHeight = 32 * sScale;
        float listStartY = y + padding + inputHeight + 6 * sScale;

        List<SearchResult> listToRender = searchText.isEmpty()
                ? recentModules.stream().map(m -> new SearchResult(m, null, null)).toList()
                : new ArrayList<>(searchResults);

        float itemHeight = 22 * sScale;
        float itemSpacing = 2 * sScale;
        float currentY = listStartY + scrollY;

        for (SearchResult result : listToRender) {
            if (mouseY >= currentY && mouseY <= currentY + itemHeight) {
                if (mouseX >= x + 10 * sScale && mouseX <= x + width - 10 * sScale) {
                    addToRecent(result.module);
                    openModuleLocation(result.module);
                    return;
                }
            }
            currentY += itemHeight + itemSpacing;
        }
    }

    private void activateSelected() {
        List<SearchResult> list = searchText.isEmpty()
                ? recentModules.stream().map(m -> new SearchResult(m, null, null)).toList()
                : new ArrayList<>(searchResults);
        if (selectedIndex >= 0 && selectedIndex < list.size()) {
            Module m = list.get(selectedIndex).module;
            addToRecent(m);
            openModuleLocation(m);
        }
    }

    private void addToRecent(Module m) {
        recentModules.remove(m);
        recentModules.add(0, m);
        if (recentModules.size() > 5) {
            recentModules.remove(recentModules.size() - 1);
        }
    }

    private void openModuleLocation(Module module) {
        highlightedModule = null;
        setOpen(false);

        parent.getDynamicIsland().setSelectedTab(DynamicIsland.IslandTab.UI);
        parent.setCurrentViewMode(ClickGuiScreen.ViewMode.UI);

        for (Panel p : parent.getPanels()) {
            if (p.getCategory() == module.getCategory()) {
                SubCategory targetSub = null;
                for (SubCategory sc : SubCategory.values()) {
                    if (sc.getCategory() == module.getCategory()) {
                        Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) sc.getAnnotationClass();
                        if (module.getClass().isAnnotationPresent(annotationClass)) {
                            targetSub = sc;
                            break;
                        }
                    }
                }
                if (targetSub != null) {
                    p.setCurrentSubCategory(targetSub);
                    p.loadModules("");
                }
            }
        }
    }

    public void scroll(double delta) {
        if (open) {
            targetScrollY += delta * 20;
        }
    }

    public Module getHighlightedModule() {
        return highlightedModule;
    }

    private static class SearchResult {
        Module module;
        // Внутренний ключ для сортировки (name_exact, name_starts, name_contains, description)
        String foundIn;
        // Человекочитаемый текст для отображения под именем модуля
        String foundInLabel;

        SearchResult(Module module, String foundIn, String foundInLabel) {
            this.module = module;
            this.foundIn = foundIn;
            this.foundInLabel = foundInLabel;
        }
    }
}