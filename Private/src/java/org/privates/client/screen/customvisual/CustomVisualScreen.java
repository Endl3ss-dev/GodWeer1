package org.privates.client.screen.customvisual;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.render.Hud;
import org.privates.client.screen.customvisual.components.*;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CustomVisualScreen extends Screen {
    private final Animation openAnimation = new Animation();
    private final Animation sidebarAnimation = new Animation();

    // UI состояние
    private HudElementWrapper selectedElement = null;
    private boolean sidebarOpen = true;
    private double dragOffsetX, dragOffsetY;
    private Screen previousScreen = null; // Для возврата к настройкам
    private boolean isClosing = false; // Флаг для предотвращения рекурсии

    // Размеры UI
    private static final float SIDEBAR_WIDTH = 280f;
    private static final float TOOLBAR_HEIGHT = 40f;

    // Панели
    private ElementPropertiesPanel propertiesPanel;
    private ElementLibraryPanel libraryPanel;
    private ToolbarPanel toolbarPanel;

    // Карта реальных HUD элементов
    private final Map<String, HudElementWrapper> hudElements = new HashMap<>();
    private final List<HudElementWrapper> visibleElements = new ArrayList<>();

    // Карта кастомных элементов
    private final Map<String, CustomizableElement> customElements = new HashMap<>();
    private int customElementCounter = 0;

    // --- Для resize/drag ---
    private ResizeHandle activeResizeHandle = null;
    private boolean draggingElement = false;
    private static final float RESIZE_HANDLE_SIZE = 8f;

    private enum ResizeHandle {NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT}

    public CustomVisualScreen() {
        super(new StringTextComponent("Custom Visual Editor"));
        initializeHudElements();
        initializePanels();
    }

    public CustomVisualScreen(Screen previousScreen) {
        this();
        this.previousScreen = previousScreen;
    }

    private void initializeHudElements() {
        Hud hudModule = Hud.getInstance();


        updateVisibleElements();
    }

    private void addHudElement(String id, String displayName, Object renderer, Object dragSetting) {
        if (renderer != null && dragSetting != null) {
            try {
                HudElementWrapper wrapper = new HudElementWrapper(id, displayName,
                        (org.privates.client.screen.hud.IRenderer) renderer,
                        (org.privates.client.managers.module.settings.impl.DragSetting) dragSetting);
                hudElements.put(id, wrapper);
            } catch (Exception e) {
                System.err.println("Failed to add HUD element: " + id + " - " + e.getMessage());
            }
        }
    }

    private void updateVisibleElements() {
        visibleElements.clear();
        for (HudElementWrapper element : hudElements.values()) {
            if (element.isVisible()) {
                visibleElements.add(element);
            }
        }
    }

    private void initializePanels() {
        propertiesPanel = new ElementPropertiesPanel(this);
        libraryPanel = new ElementLibraryPanel(this);
        toolbarPanel = new ToolbarPanel(this);
    }

    @Override
    protected void init() {
        super.init();
        openAnimation.set(0);
        openAnimation.run(1, 0.4, Easings.EXPO_OUT);
        sidebarAnimation.set(sidebarOpen ? 1 : 0);
        updateVisibleElements();
        isClosing = false; // Сбрасываем флаг при инициализации
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        openAnimation.update();
        sidebarAnimation.update();

        float scale = openAnimation.get();
        if (scale <= 0.01f) return;

        matrix.push();
        matrix.translate(width / 2f, height / 2f, 0);
        matrix.scale(scale, scale, scale);
        matrix.translate(-width / 2f, -height / 2f, 0);

        renderBackground(matrix);
        renderWorkspace(matrix, mouseX, mouseY, partialTicks);
        renderUI(matrix, mouseX, mouseY);

        matrix.pop();
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderBackground(MatrixStack matrix) {
        // Градиентный фон
        fill(matrix, 0, 0, width, height, ColorUtil.getColor(15, 15, 25, 200));

        // Сетка в рабочей области
        renderGrid(matrix);

        // Blur эффект
        try {
            BlurRenderer.registerRenderCall(() -> {
                fill(matrix, 0, 0, width, height, ColorUtil.getColor(255, 255, 255, 50));
            });
            BlurRenderer.draw(5);
        } catch (Exception e) {
            // Игнорируем ошибки blur эффекта
        }
    }

    private void renderGrid(MatrixStack matrix) {
        float sidebarWidth = sidebarOpen ? SIDEBAR_WIDTH * sidebarAnimation.get() : 0;
        float workspaceX = sidebarWidth;

        int gridSize = 20;
        int gridColor = ColorUtil.getColor(255, 255, 255, 20);

        // Вертикальные линии
        for (int x = (int) workspaceX; x < width; x += gridSize) {
            fill(matrix, x, (int) TOOLBAR_HEIGHT, x + 1, height, gridColor);
        }

        // Горизонтальные линии
        for (int y = (int) TOOLBAR_HEIGHT; y < height; y += gridSize) {
            fill(matrix, (int) workspaceX, y, width, y + 1, gridColor);
        }
    }

    private void renderWorkspace(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        float sidebarWidth = sidebarOpen ? SIDEBAR_WIDTH * sidebarAnimation.get() : 0;
        enableScissor(sidebarWidth, TOOLBAR_HEIGHT, width - sidebarWidth, height - TOOLBAR_HEIGHT);

        // Рендерим все видимые HUD элементы с их реальной отрисовкой
        for (HudElementWrapper element : visibleElements) {
            renderHudElement(matrix, element, mouseX, mouseY, partialTicks);
        }

        disableScissor();
    }

    private void renderHudElement(MatrixStack matrix, HudElementWrapper element, int mouseX, int mouseY, float partialTicks) {
        float x = element.getX();
        float y = element.getY();
        float width = element.getWidth();
        float height = element.getHeight();

        boolean isSelected = selectedElement == element;
        boolean isHovered = isElementHovered(element, mouseX, mouseY);

        // Рендерим реальное содержимое элемента используя оригинальный рендерер
        matrix.push();
        try {
            boolean originalActive = element.getDragSetting().active;
            element.getDragSetting().active = false;
            element.renderInEditor(matrix, mouseX, mouseY, partialTicks);
            element.getDragSetting().active = originalActive;
        } catch (Exception e) {
            renderElementPlaceholder(matrix, element);
        }
        matrix.pop();

        // Рендерим границу выделения ПОВЕРХ элемента
        if (isSelected || isHovered) {
            int borderColor = isSelected ? Theme.getInstance().clientColor() : ColorUtil.getColor(255, 255, 255, 100);
            RenderUtil.Rounded.roundedOutline(matrix, x - 2, y - 2, width + 4, height + 4, 2f, borderColor, Round.of(4));
            // Рендерим точки изменения размера для всех элементов
            renderResizeHandles(matrix, element, mouseX, mouseY);
        }
        if (isHovered) {
            String name = element.getDisplayName();
            float nameWidth = Fonts.SF_MEDIUM.getWidth(name, 8);
            float nameX = x + (width - nameWidth) / 2;
            float nameY = y - 15;
            RenderUtil.Rounded.roundedRect(matrix, nameX - 4, nameY - 2, nameWidth + 8, 12, ColorUtil.getColor(0, 0, 0, 180), Round.of(3));
            Fonts.SF_MEDIUM.draw(matrix, name, nameX, nameY, ColorUtil.getColor(255, 255, 255), 8);
        }
    }

    private void renderElementPlaceholder(MatrixStack matrix, HudElementWrapper element) {
        float x = element.getX();
        float y = element.getY();
        float width = element.getWidth();
        float height = element.getHeight();

        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height,
                ColorUtil.getColor(50, 50, 50, 150), Round.of(4));

        String name = element.getDisplayName();
        float nameWidth = Fonts.SF_MEDIUM.getWidth(name, 8);
        Fonts.SF_MEDIUM.draw(matrix, name, x + (width - nameWidth) / 2, y + height / 2 - 4,
                ColorUtil.getColor(255, 255, 255), 8);
    }

    private void renderResizeHandles(MatrixStack matrix, HudElementWrapper element, int mouseX, int mouseY) {
        float x = element.getX();
        float y = element.getY();
        float width = element.getWidth();
        float height = element.getHeight();
        float handleSize = RESIZE_HANDLE_SIZE;
        int handleColor = Theme.getInstance().clientColor();
        int hoverColor = ColorUtil.getColor(255, 255, 255, 180);
        // 4 угла
        boolean hoverTL = isInRect(mouseX, mouseY, x - handleSize / 2, y - handleSize / 2, handleSize, handleSize);
        boolean hoverTR = isInRect(mouseX, mouseY, x + width - handleSize / 2, y - handleSize / 2, handleSize, handleSize);
        boolean hoverBL = isInRect(mouseX, mouseY, x - handleSize / 2, y + height - handleSize / 2, handleSize, handleSize);
        boolean hoverBR = isInRect(mouseX, mouseY, x + width - handleSize / 2, y + height - handleSize / 2, handleSize, handleSize);
        RenderUtil.Rounded.roundedRect(matrix, x - handleSize / 2, y - handleSize / 2, handleSize, handleSize, hoverTL ? hoverColor : handleColor, Round.of(handleSize / 2));
        RenderUtil.Rounded.roundedRect(matrix, x + width - handleSize / 2, y - handleSize / 2, handleSize, handleSize, hoverTR ? hoverColor : handleColor, Round.of(handleSize / 2));
        RenderUtil.Rounded.roundedRect(matrix, x - handleSize / 2, y + height - handleSize / 2, handleSize, handleSize, hoverBL ? hoverColor : handleColor, Round.of(handleSize / 2));
        RenderUtil.Rounded.roundedRect(matrix, x + width - handleSize / 2, y + height - handleSize / 2, handleSize, handleSize, hoverBR ? hoverColor : handleColor, Round.of(handleSize / 2));
    }

    private void renderUI(MatrixStack matrix, int mouseX, int mouseY) {
        // Рендерим боковую панель
        if (sidebarOpen) {
            renderSidebar(matrix, mouseX, mouseY);
        }

        // Рендерим тулбар
        toolbarPanel.render(matrix, mouseX, mouseY);
    }

    private void renderSidebar(MatrixStack matrix, int mouseX, int mouseY) {
        float sidebarWidth = SIDEBAR_WIDTH * sidebarAnimation.get();

        // Фон боковой панели
        RenderUtil.Rounded.roundedRect(matrix, 0, 0, sidebarWidth, height,
                ColorUtil.getColor(25, 25, 35, 220), Round.of(0, 8, 8, 0));

        // Разделительная линия
        fill(matrix, (int) sidebarWidth - 1, 0, (int) sidebarWidth, height,
                ColorUtil.getColor(60, 60, 80, 150));

        // Рендерим содержимое панели
        if (selectedElement != null) {
            propertiesPanel.render(matrix, mouseX, mouseY);
        } else {
            libraryPanel.render(matrix, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && selectedElement != null && selectedElement.isResizable()) {
            ResizeHandle handle = getResizeHandleAt(selectedElement, mouseX, mouseY);
            if (handle != ResizeHandle.NONE) {
                activeResizeHandle = handle;
                draggingElement = false;
                return true;
            }
            if (isInElementCenter(selectedElement, mouseX, mouseY)) {
                dragOffsetX = mouseX - selectedElement.getX();
                dragOffsetY = mouseY - selectedElement.getY();
                draggingElement = true;
                activeResizeHandle = null;
                return true;
            }
        }
        // --- Обычный выбор элемента, если не начат drag/resize ---
        if (button == 1) { // ПКМ
            HudElementWrapper clickedElement = getElementAt(mouseX, mouseY);
            if (clickedElement != null) {
                if (selectedElement != clickedElement) {
                    selectedElement = clickedElement;
                    propertiesPanel = new ElementPropertiesPanel(this);
                }
                return true;
            }
        } else if (button == 0) {
            // UI панели
            if (sidebarOpen && mouseX <= SIDEBAR_WIDTH * sidebarAnimation.get()) {
                if (selectedElement != null && propertiesPanel != null) {
                    if (propertiesPanel.mouseClicked(mouseX, mouseY, button)) {
                        return true;
                    }
                } else if (libraryPanel != null) {
                    if (libraryPanel.mouseClicked(mouseX, mouseY, button)) {
                        return true;
                    }
                }
            }
            if (mouseY <= TOOLBAR_HEIGHT && toolbarPanel.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            HudElementWrapper clickedElement = getElementAt(mouseX, mouseY);
            if (clickedElement != null) {
                selectedElement = clickedElement;
                dragOffsetX = mouseX - clickedElement.getX();
                dragOffsetY = mouseY - clickedElement.getY();
                return true;
            } else {
                selectedElement = null;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (selectedElement != null && button == 0) {
            if (activeResizeHandle != null) {
                float minWidth = 24f, minHeight = 16f;
                float x = selectedElement.getX();
                float y = selectedElement.getY();
                float w = selectedElement.getWidth();
                float h = selectedElement.getHeight();
                float mx = (float) mouseX, my = (float) mouseY;
                switch (activeResizeHandle) {
                    case TOP_LEFT:
                        float newW1 = w + (x - mx);
                        float newH1 = h + (y - my);
                        if (newW1 >= minWidth) {
                            selectedElement.setX(mx);
                            selectedElement.setWidth(newW1);
                        }
                        if (newH1 >= minHeight) {
                            selectedElement.setY(my);
                            selectedElement.setHeight(newH1);
                        }
                        break;
                    case TOP_RIGHT:
                        float newW2 = mx - x;
                        float newH2 = h + (y - my);
                        if (newW2 >= minWidth) selectedElement.setWidth(newW2);
                        if (newH2 >= minHeight) {
                            selectedElement.setY(my);
                            selectedElement.setHeight(newH2);
                        }
                        break;
                    case BOTTOM_LEFT:
                        float newW3 = w + (x - mx);
                        float newH3 = my - y;
                        if (newW3 >= minWidth) {
                            selectedElement.setX(mx);
                            selectedElement.setWidth(newW3);
                        }
                        if (newH3 >= minHeight) selectedElement.setHeight(newH3);
                        break;
                    case BOTTOM_RIGHT:
                        float newW4 = mx - x;
                        float newH4 = my - y;
                        if (newW4 >= minWidth) selectedElement.setWidth(newW4);
                        if (newH4 >= minHeight) selectedElement.setHeight(newH4);
                        break;
                }
                return true;
            } else if (draggingElement) {
                float newX = (float) (mouseX - dragOffsetX);
                float newY = (float) (mouseY - dragOffsetY);
                float sidebarWidth = sidebarOpen ? SIDEBAR_WIDTH * sidebarAnimation.get() : 0;
                newX = Math.max(sidebarWidth, Math.min(newX, width - selectedElement.getWidth()));
                newY = Math.max(TOOLBAR_HEIGHT, Math.min(newY, height - selectedElement.getHeight()));
                selectedElement.setPosition(newX, newY);
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            activeResizeHandle = null;
            draggingElement = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC - умное закрытие
        if (keyCode == 256) { // ESC
            if (selectedElement != null && propertiesPanel != null && propertiesPanel.isEditingProperty()) {
                propertiesPanel.finishEditing();
                return true;
            } else if (selectedElement != null) {
                selectedElement = null;
                return true;
            } else {
                closeScreen();
                return true;
            }
        }

        // Delete - скрыть выбранный элемент
        if (keyCode == 261 && selectedElement != null) { // Delete
            selectedElement.setVisible(false);
            updateVisibleElements();
            selectedElement = null;
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // Безопасное закрытие экрана без рекурсии
    public void closeScreen() {
        if (isClosing) {
            return; // Предотвращаем повторные вызовы
        }

        isClosing = true;

        try {
            // Сохраняем изменения
            saveElementPositions();

            // Запускаем анимацию закрытия
            openAnimation.run(0, 0.2, Easings.EXPO_IN);

            // Определяем, на какой экран вернуться
            Screen targetScreen = null;

            if (previousScreen != null && previousScreen != this) {
                // Возвращаемся к предыдущему экрану, но только если это не тот же экран
                targetScreen = previousScreen;
            }

            // Устанавливаем экран напрямую без вызова onClose()
            minecraft.displayScreen(targetScreen);

        } catch (Exception e) {
            // В случае ошибки просто закрываем экран
            minecraft.displayScreen(null);
        } finally {
            isClosing = false;
        }
    }

    @Override
    public void onClose() {
        // Вызываем безопасное закрытие вместо стандартного поведения
        closeScreen();
    }

    private void saveElementPositions() {
        // Позиции уже сохраняются в DragSettings автоматически
        // Дополнительных действий не требуется
    }

    private HudElementWrapper getElementAt(double mouseX, double mouseY) {
        // Проверяем элементы в обратном порядке (сверху вниз)
        for (int i = visibleElements.size() - 1; i >= 0; i--) {
            HudElementWrapper element = visibleElements.get(i);
            if (isElementHovered(element, mouseX, mouseY)) {
                return element;
            }
        }
        return null;
    }

    private boolean isElementHovered(HudElementWrapper element, double mouseX, double mouseY) {
        return mouseX >= element.getX() && mouseX <= element.getX() + element.getWidth() &&
                mouseY >= element.getY() && mouseY <= element.getY() + element.getHeight();
    }

    private void enableScissor(float x, float y, float width, float height) {
        try {
            double scale = minecraft.getMainWindow().getScaleFactor();
            org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
            org.lwjgl.opengl.GL11.glScissor(
                    (int) (x * scale),
                    (int) ((minecraft.getMainWindow().getScaledHeight() - (y + height)) * scale),
                    (int) (width * scale),
                    (int) (height * scale)
            );
        } catch (Exception e) {
            // Игнорируем ошибки OpenGL
        }
    }

    private void disableScissor() {
        try {
            org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        } catch (Exception e) {
            // Игнорируем ошибки OpenGL
        }
    }

    public void toggleSidebar() {
        sidebarOpen = !sidebarOpen;
        sidebarAnimation.run(sidebarOpen ? 1 : 0, 0.3, Easings.EXPO_OUT);
    }

    public void resetElementPositions() {
        // Сбрасываем позиции всех элементов к значениям по умолчанию
        int offsetX = 10;
        int offsetY = 50;

        for (HudElementWrapper element : hudElements.values()) {
            element.setPosition(offsetX, offsetY);
            offsetY += 60; // Сдвигаем следующий элемент вниз
            if (offsetY > height - 100) {
                offsetY = 50;
                offsetX += 150; // Новая колонка
            }
        }
    }

    // Добавляем метод для создания кастомных элементов
    public void addCustomElement(ElementType elementType) {
        String id = "custom_" + elementType.name().toLowerCase() + "_" + (++customElementCounter);
        CustomizableElement element = new CustomizableElement(id, elementType.getDisplayName(),
                width / 2f, height / 2f, elementType);
        customElements.put(id, element);
        // Здесь можно добавить дополнительную логику для интеграции с HUD системой
    }

    // Геттеры для панелей
    public List<HudElementWrapper> getAllHudElements() {
        return new ArrayList<>(hudElements.values());
    }

    public List<HudElementWrapper> getVisibleElements() {
        return new ArrayList<>(visibleElements);
    }

    // Добавляем геттеры для размеров экрана
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Геттер для minecraft instance
    public net.minecraft.client.Minecraft getMinecraft() {
        return minecraft;
    }

    // Геттеры для всех кастомных элементов
    public List<CustomizableElement> getAllCustomElements() {
        return new ArrayList<>(customElements.values());
    }

    // Метод для установки предыдущего экрана
    public void setPreviousScreen(Screen screen) {
        this.previousScreen = screen;
    }

    private boolean isInElementCenter(HudElementWrapper element, double mouseX, double mouseY) {
        float x = element.getX(), y = element.getY(), w = element.getWidth(), h = element.getHeight();
        float cx = x + w / 2f, cy = y + h / 2f;
        float centerW = w * 0.6f, centerH = h * 0.6f;
        return mouseX >= cx - centerW / 2 && mouseX <= cx + centerW / 2 && mouseY >= cy - centerH / 2 && mouseY <= cy + centerH / 2;
    }

    private ResizeHandle getResizeHandleAt(HudElementWrapper element, double mouseX, double mouseY) {
        float x = element.getX(), y = element.getY(), w = element.getWidth(), h = element.getHeight();
        float s = RESIZE_HANDLE_SIZE;
        if (isInRect(mouseX, mouseY, x - s / 2, y - s / 2, s, s)) return ResizeHandle.TOP_LEFT;
        if (isInRect(mouseX, mouseY, x + w - s / 2, y - s / 2, s, s)) return ResizeHandle.TOP_RIGHT;
        if (isInRect(mouseX, mouseY, x - s / 2, y + h - s / 2, s, s)) return ResizeHandle.BOTTOM_LEFT;
        if (isInRect(mouseX, mouseY, x + w - s / 2, y + h - s / 2, s, s)) return ResizeHandle.BOTTOM_RIGHT;
        return ResizeHandle.NONE;
    }

    private boolean isInRect(double mx, double my, float x, float y, float w, float h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}