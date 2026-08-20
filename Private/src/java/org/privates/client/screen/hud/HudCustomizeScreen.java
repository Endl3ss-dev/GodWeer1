package org.privates.client.screen.hud;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.render.Hud;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.scroll.ScrollUtil;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class HudCustomizeScreen extends Screen {
    private final Hud hudModule = Hud.getInstance();
    private final List<HudElement> hudElements = new ArrayList<>();
    private final ScrollUtil leftScroll = new ScrollUtil();
    private final ScrollUtil rightScroll = new ScrollUtil();
    private final Animation openAnimation = new Animation();
    private final Animation rightPanelAnimation = new Animation();
    private final HudSettingsManager settingsManager = new HudSettingsManager();

    private HudElement selectedElement;
    private boolean isDragging = false;
    private HudElement draggingElement;
    private float dragOffsetX, dragOffsetY;
    private boolean isResizing = false;
    private String resizeHandle = "";

    private final float leftPanelWidth = 240f;
    private final float rightPanelWidth = 320f;
    private final float topBarHeight = 45f;
    private final float padding = 12f;

    private String searchText = "";
    private boolean isSearching = false;

    public HudCustomizeScreen() {
        super(new StringTextComponent("HUD Editor"));
        initElements();
        settingsManager.loadAllSettings();
    }

    private void initElements() {
        hudElements.clear();
        addElement("Watermark", "watermark", "💧");
        addElement("Target HUD", "targethud", "🎯");
        addElement("Keybinds", "keybinds", "⌨");
        addElement("Potions", "potions", "🧪");
        addElement("Information", "information", "ℹ");
        addElement("Armor HUD", "armorhud", "🛡");
        addElement("Staff List", "stafflist", "👥");
        addElement("Array List", "arraylist", "📋");
        addElement("Inventory HUD", "inventoryhud", "🎒");
        addElement("Motion Graph", "motiongraph", "📊");
        addElement("Cooldowns", "cooldowns", "⏱");
        addElement("Minimap", "minimap", "🗺");
        addElement("Nearby Players", "nearbyplayers", "👤");
        addElement("Key Strokes", "keystrokes", "⌨");
        addElement("Score Board", "scoreboard", "🏆");
    }

    private void addElement(String name, String id, String icon) {
        DragSetting drag = hudModule.getElementDrag(id);
        if (drag != null) {
            HudElement element = new HudElement(name, id, drag);
            element.icon = icon;
            hudElements.add(element);
            settingsManager.ensureElementSettings(id, name);
        }
    }

    @Override
    protected void init() {
        super.init();
        openAnimation.run(1.0, 0.5, Easings.EXPO_OUT, false);
        rightPanelAnimation.run(selectedElement != null ? 1.0 : 0.0, 0.4, Easings.EXPO_OUT, false);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        openAnimation.update();
        rightPanelAnimation.update();

        float animScale = openAnimation.get();
        if (animScale <= 0.01f) return;

        matrix.push();
        matrix.translate(width / 2f, height / 2f, 0);
        matrix.scale(animScale, animScale, animScale);
        matrix.translate(-width / 2f, -height / 2f, 0);

        renderBackground(matrix);
        renderTopBar(matrix, mouseX, mouseY);
        renderLeftPanel(matrix, mouseX, mouseY);
        renderMainArea(matrix, mouseX, mouseY);

        if (selectedElement != null) {
            renderRightPanel(matrix, mouseX, mouseY);
        }

        matrix.pop();
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    public void renderBackground(MatrixStack matrix) {
        RenderUtil.Rounded.roundedRect(matrix, 0, 0, width, height, ColorUtil.getColor(0, 0, 0, 140), Round.of(0));

        BlurRenderer.registerRenderCall(() -> {
            RenderUtil.Rounded.roundedRect(matrix, padding, padding, width - padding * 2, height - padding * 2, -1, Round.of(10));
        });
        BlurRenderer.draw(30);

        RenderUtil.Rounded.roundedRect(matrix, padding, padding, width - padding * 2, height - padding * 2,
                ColorUtil.getColor(12, 15, 22, 250), Round.of(10));

        RenderUtil.Rounded.roundedOutline(matrix, padding, padding, width - padding * 2, height - padding * 2, 1.5f,
                ColorUtil.getColor(40, 50, 70, 180), Round.of(10));
    }

    private void renderTopBar(MatrixStack matrix, int mouseX, int mouseY) {
        float x = padding;
        float y = padding;
        float w = width - padding * 2;

        RenderUtil.Rounded.roundedRect(matrix, x, y, w, topBarHeight, ColorUtil.getColor(8, 10, 16, 220), Round.of(8));

        Fonts.SF_MEDIUM.draw(matrix, "HUD Customize Editor", x + 20, y + 12, ColorUtil.getColor(255, 255, 255), 14);
        Fonts.SF_MEDIUM.draw(matrix, "Настройте каждый элемент интерфейса индивидуально", x + 20, y + 28, ColorUtil.getColor(160, 170, 190), 9);

        renderTopBarButtons(matrix, x, y, w, mouseX, mouseY);
    }

    private void renderTopBarButtons(MatrixStack matrix, float x, float y, float w, int mouseX, int mouseY) {
        float btnSize = 32f;
        float btnY = y + 6;
        float btnX = x + w - btnSize * 4 - 40;

        String[] icons = {"📂", "🔄", "💾", "✕"};
        String[] tooltips = {"Пресеты", "Сброс", "Сохранить", "Закрыть"};
        int[] colors = {
                ColorUtil.getColor(60, 100, 140),
                ColorUtil.getColor(140, 100, 60),
                ColorUtil.getColor(60, 140, 80),
                ColorUtil.getColor(140, 60, 60)
        };

        for (int i = 0; i < icons.length; i++) {
            boolean hovered = isHovered(mouseX, mouseY, btnX, btnY, btnSize, btnSize);
            int bgColor = hovered ? ColorUtil.multAlpha(colors[i], 1.3f) : colors[i];

            RenderUtil.Rounded.roundedRect(matrix, btnX, btnY, btnSize, btnSize, bgColor, Round.of(6));

            if (hovered) {
                RenderUtil.Shadow.drawShadow(matrix, btnX, btnY, btnSize, btnSize, 8f, 0.7f, colors[i], Round.of(6));
            }

            Fonts.SF_MEDIUM.drawCenter(matrix, icons[i], btnX + btnSize / 2, btnY + btnSize / 2 - 6, -1, 12);

            if (hovered) {
                renderTooltip(matrix, tooltips[i], mouseX, mouseY);
            }

            btnX += btnSize + 10;
        }
    }

    private void renderLeftPanel(MatrixStack matrix, int mouseX, int mouseY) {
        float x = padding;
        float y = padding + topBarHeight + 8;
        float w = leftPanelWidth;
        float h = height - y - padding;

        RenderUtil.Rounded.roundedRect(matrix, x, y, w, h, ColorUtil.getColor(8, 10, 16, 220), Round.of(8));

        renderSearchBar(matrix, x, y, w, mouseX, mouseY);
        renderElementsList(matrix, x, y + 50, w, h - 50, mouseX, mouseY);
    }

    private void renderSearchBar(MatrixStack matrix, float x, float y, float w, int mouseX, int mouseY) {
        float searchH = 35f;
        float searchX = x + 12;
        float searchY = y + 12;
        float searchW = w - 24;

        boolean searchHovered = isHovered(mouseX, mouseY, searchX, searchY, searchW, searchH);
        int searchBg = searchHovered || isSearching ? ColorUtil.getColor(25, 30, 40, 200) : ColorUtil.getColor(20, 25, 35, 150);

        RenderUtil.Rounded.roundedRect(matrix, searchX, searchY, searchW, searchH, searchBg, Round.of(6));

        String placeholder = searchText.isEmpty() ? "🔍 Поиск элементов..." : searchText;
        int textColor = searchText.isEmpty() ? ColorUtil.getColor(120, 130, 150) : ColorUtil.getColor(255, 255, 255);

        Fonts.SF_MEDIUM.draw(matrix, placeholder, searchX + 12, searchY + 12, textColor, 9);

        if (isSearching) {
            long time = System.currentTimeMillis();
            if ((time / 500) % 2 == 0) {
                float cursorX = searchX + 12 + Fonts.SF_MEDIUM.getWidth(searchText, 9);
                RenderUtil.Rounded.roundedRect(matrix, cursorX, searchY + 8, 1, 18, ColorUtil.getColor(255, 255, 255), Round.of(0));
            }
        }
    }

    private void renderElementsList(MatrixStack matrix, float x, float y, float w, float h, int mouseX, int mouseY) {
        leftScroll.update();

        List<HudElement> filteredElements = getFilteredElements();
        float totalHeight = filteredElements.size() * 42f;
        leftScroll.setMax(totalHeight, h - 20);

        ScissorUtil.enableScissor(x + 8, y, w - 16, h - 20);

        float offsetY = leftScroll.getWheel();
        float currentY = y + 10 + offsetY;

        for (HudElement element : filteredElements) {
            if (currentY + 40 >= y && currentY <= y + h) {
                renderElementEntry(matrix, element, x + 12, currentY, w - 24, 40, mouseX, mouseY);
            }
            currentY += 42f;
        }

        ScissorUtil.disableScissor();
        leftScroll.renderV(matrix, new Vector2f(x + w - 8, y), h - 20, 0.5f);
    }

    private List<HudElement> getFilteredElements() {
        if (searchText.isEmpty()) {
            return hudElements;
        }

        List<HudElement> filtered = new ArrayList<>();
        for (HudElement element : hudElements) {
            if (element.name.toLowerCase().contains(searchText.toLowerCase())) {
                filtered.add(element);
            }
        }
        return filtered;
    }

    private void renderElementEntry(MatrixStack matrix, HudElement element, float x, float y, float w, float h, int mouseX, int mouseY) {
        boolean hovered = isHovered(mouseX, mouseY, x, y, w, h);
        boolean selected = selectedElement == element;

        int bgColor = selected ? ColorUtil.getColor(70, 120, 180, 180) :
                hovered ? ColorUtil.getColor(40, 60, 90, 140) :
                        ColorUtil.getColor(20, 25, 35, 100);

        RenderUtil.Rounded.roundedRect(matrix, x, y, w, h, bgColor, Round.of(6));

        if (selected || hovered) {
            RenderUtil.Rounded.roundedOutline(matrix, x, y, w, h, 1.5f,
                    selected ? ColorUtil.getColor(100, 150, 255, 200) : ColorUtil.getColor(80, 100, 140, 150), Round.of(6));
        }


    }

    private void renderElementIcon(MatrixStack matrix, HudElement element, float x, float y, float h, boolean enabled) {
        float iconSize = 24f;
        float iconX = x + 10;
        float iconY = y + (h - iconSize) / 2;

        int iconBg = enabled ? ColorUtil.getColor(40, 80, 140, 180) : ColorUtil.getColor(60, 60, 60, 180);
        RenderUtil.Rounded.roundedRect(matrix, iconX, iconY, iconSize, iconSize, iconBg, Round.of(4));

        Fonts.SF_MEDIUM.drawCenter(matrix, element.icon, iconX + iconSize / 2, iconY + iconSize / 2 - 4, -1, 10);

        float statusSize = 8f;
        int statusColor = enabled ? ColorUtil.getColor(0, 255, 120) : ColorUtil.getColor(255, 80, 80);
        RenderUtil.Rounded.roundedRect(matrix, iconX + iconSize - statusSize, iconY, statusSize, statusSize, statusColor, Round.of(statusSize / 2));
    }

    private void renderElementInfo(MatrixStack matrix, HudElement element, float x, float y, float w, float h, boolean enabled) {
        float textX = x + 45;
        int nameColor = enabled ? ColorUtil.getColor(255, 255, 255) : ColorUtil.getColor(150, 150, 150);

        Fonts.SF_MEDIUM.draw(matrix, element.name, textX, y + 8, nameColor, 10);

        String status = enabled ? "Активен" : "Выключен";
        HudSettingsManager.ElementSettings settings = settingsManager.getSettings(element.id);
        if (settings != null && !settings.displayText.equals(element.name)) {
            status += " • " + settings.displayText;
        }

        Fonts.SF_MEDIUM.draw(matrix, status, textX, y + 22, ColorUtil.getColor(160, 170, 190), 8);
    }

    private void renderElementControls(MatrixStack matrix, HudElement element, float x, float y, float w, float h, int mouseX, int mouseY, boolean enabled) {
        float btnSize = 20f;
        float btnY = y + (h - btnSize) / 2;
        float btnX = x + w - btnSize * 2 - 15;

        boolean toggleHovered = isHovered(mouseX, mouseY, btnX, btnY, btnSize, btnSize);
        int toggleColor = enabled ?
                (toggleHovered ? ColorUtil.getColor(0, 200, 100) : ColorUtil.getColor(0, 180, 90)) :
                (toggleHovered ? ColorUtil.getColor(200, 80, 80) : ColorUtil.getColor(180, 60, 60));

        RenderUtil.Rounded.roundedRect(matrix, btnX, btnY, btnSize, btnSize, toggleColor, Round.of(4));
        Fonts.SF_MEDIUM.drawCenter(matrix, enabled ? "✓" : "✕", btnX + btnSize / 2, btnY + btnSize / 2 - 3, -1, 8);

        btnX += btnSize + 5;
        boolean settingsHovered = isHovered(mouseX, mouseY, btnX, btnY, btnSize, btnSize);
        int settingsColor = settingsHovered ? ColorUtil.getColor(100, 120, 160) : ColorUtil.getColor(80, 90, 120);

        RenderUtil.Rounded.roundedRect(matrix, btnX, btnY, btnSize, btnSize, settingsColor, Round.of(4));
        Fonts.SF_MEDIUM.drawCenter(matrix, "⚙", btnX + btnSize / 2, btnY + btnSize / 2 - 3, -1, 8);
    }

    private void renderMainArea(MatrixStack matrix, int mouseX, int mouseY) {
        float x = padding + leftPanelWidth + 8;
        float y = padding + topBarHeight + 8;
        float w = width - x - padding - (selectedElement != null ? rightPanelWidth + 8 : 0);
        float h = height - y - padding;

        RenderUtil.Rounded.roundedRect(matrix, x, y, w, h, ColorUtil.getColor(5, 7, 12, 200), Round.of(8));

        renderMainAreaHeader(matrix, x, y, w);
        renderPreviewArea(matrix, x + 15, y + 45, w - 30, h - 60, mouseX, mouseY);
    }

    private void renderMainAreaHeader(MatrixStack matrix, float x, float y, float w) {
        Fonts.SF_MEDIUM.drawCenter(matrix, "Область предпросмотра", x + w / 2, y + 15, ColorUtil.getColor(220, 230, 250), 12);

        String hint = selectedElement != null ?
                "Перетаскивайте элемент для изменения позиции • Используйте ручки для изменения размера" :
                "Выберите элемент из списка слева для редактирования";

        Fonts.SF_MEDIUM.drawCenter(matrix, hint, x + w / 2, y + 30, ColorUtil.getColor(120, 130, 150), 8);
    }

    private void renderPreviewArea(MatrixStack matrix, float x, float y, float w, float h, int mouseX, int mouseY) {
        RenderUtil.Rounded.roundedRect(matrix, x, y, w, h, ColorUtil.getColor(2, 4, 8, 160), Round.of(6));
        RenderUtil.Rounded.roundedOutline(matrix, x, y, w, h, 1f, ColorUtil.getColor(20, 30, 50, 120), Round.of(6));

        renderGrid(matrix, x, y, w, h);

        ScissorUtil.enableScissor(x, y, w, h);


        ScissorUtil.disableScissor();
    }

    private void renderGrid(MatrixStack matrix, float x, float y, float w, float h) {
        int gridColor = ColorUtil.getColor(255, 255, 255, 6);
        float gridSize = 12f;

        for (float gx = x; gx <= x + w; gx += gridSize) {
            RenderUtil.Rounded.roundedRect(matrix, gx, y, 0.5f, h, gridColor, Round.of(0));
        }
        for (float gy = y; gy <= y + h; gy += gridSize) {
            RenderUtil.Rounded.roundedRect(matrix, x, gy, w, 0.5f, gridColor, Round.of(0));
        }
    }

    private void renderElementPreview(MatrixStack matrix, HudElement element, float areaX, float areaY,
                                      float areaW, float areaH, int mouseX, int mouseY) {
        DragSetting drag = element.dragSetting;
        HudSettingsManager.ElementSettings settings = settingsManager.getSettings(element.id);
        if (settings == null) return;

        float previewScale = 0.8f;
        float elemX = areaX + drag.position.x * previewScale;
        float elemY = areaY + drag.position.y * previewScale;
        float elemW = drag.size.x * previewScale * settings.scale;
        float elemH = drag.size.y * previewScale * settings.scale;

        boolean hovered = isHovered(mouseX, mouseY, elemX, elemY, elemW, elemH);
        boolean selected = selectedElement == element;

        int bgColor = selected ? ColorUtil.getColor(80, 140, 220, 100) :
                hovered ? ColorUtil.getColor(255, 255, 255, 50) :
                        ColorUtil.getColor(255, 255, 255, 25);

        RenderUtil.Rounded.roundedRect(matrix, elemX, elemY, elemW, elemH, bgColor, Round.of(4));

        int borderColor = selected ? ColorUtil.getColor(100, 160, 255, 220) : ColorUtil.getColor(255, 255, 255, 100);
        float borderWidth = selected ? 2f : 1f;
        RenderUtil.Rounded.roundedOutline(matrix, elemX, elemY, elemW, elemH, borderWidth, borderColor, Round.of(4));

        renderElementContent(matrix, element, settings, elemX, elemY, elemW, elemH);

        if (selected) {
            renderResizeHandles(matrix, elemX, elemY, elemW, elemH);
            renderSnapGuides(matrix, element, areaX, areaY, areaW, areaH, previewScale);
        }
    }

    private void renderElementContent(MatrixStack matrix, HudElement element, HudSettingsManager.ElementSettings settings, float x, float y, float w, float h) {
        matrix.push();
        matrix.translate(x + settings.textOffsetX, y + settings.textOffsetY, 0);

        if (settings.showIcon) {
            float iconX = settings.iconOffsetX + 5;
            float iconY = settings.iconOffsetY + (h - 10) / 2f;
            Fonts.SF_MEDIUM.draw(matrix, element.icon, iconX, iconY, settings.iconColor, 10);
        }

        float textStartX = settings.showIcon ? 20 : 0;
        float nameX = textStartX + (w - textStartX - Fonts.SF_MEDIUM.getWidth(settings.displayText, settings.fontSize)) / 2f;
        float nameY = (h - settings.fontSize) / 2f;

        Fonts.SF_MEDIUM.draw(matrix, settings.displayText, nameX, nameY, settings.textColor, settings.fontSize);

        matrix.pop();
    }

    private void renderResizeHandles(MatrixStack matrix, float x, float y, float w, float h) {
        float handleSize = 8f;
        int handleColor = ColorUtil.getColor(100, 160, 255);

        String[] handles = {"nw", "ne", "sw", "se"};
        float[][] positions = {
                {x - handleSize / 2, y - handleSize / 2},
                {x + w - handleSize / 2, y - handleSize / 2},
                {x - handleSize / 2, y + h - handleSize / 2},
                {x + w - handleSize / 2, y + h - handleSize / 2}
        };

        for (int i = 0; i < positions.length; i++) {
            float[] pos = positions[i];
            RenderUtil.Rounded.roundedRect(matrix, pos[0], pos[1], handleSize, handleSize, handleColor, Round.of(handleSize / 2));
            RenderUtil.Rounded.roundedRect(matrix, pos[0] + 1, pos[1] + 1, handleSize - 2, handleSize - 2,
                    ColorUtil.getColor(255, 255, 255), Round.of((handleSize - 2) / 2));
        }
    }

    private void renderSnapGuides(MatrixStack matrix, HudElement element, float areaX, float areaY, float areaW, float areaH, float scale) {
        DragSetting drag = element.dragSetting;
        float elemCenterX = areaX + (drag.position.x + drag.size.x / 2) * scale;
        float elemCenterY = areaY + (drag.position.y + drag.size.y / 2) * scale;

        float areaCenterX = areaX + areaW / 2;
        float areaCenterY = areaY + areaH / 2;

        int guideColor = ColorUtil.getColor(255, 120, 120, 180);

        if (Math.abs(elemCenterX - areaCenterX) < 8) {
            RenderUtil.Rounded.roundedRect(matrix, areaCenterX - 0.5f, areaY, 1, areaH, guideColor, Round.of(0));
        }

        if (Math.abs(elemCenterY - areaCenterY) < 8) {
            RenderUtil.Rounded.roundedRect(matrix, areaX, areaCenterY - 0.5f, areaW, 1, guideColor, Round.of(0));
        }
    }

    private void renderRightPanel(MatrixStack matrix, int mouseX, int mouseY) {
        float x = width - padding - rightPanelWidth;
        float y = padding + topBarHeight + 8;
        float w = rightPanelWidth;
        float h = height - y - padding;

        float animScale = rightPanelAnimation.get();

        matrix.push();
        matrix.translate(x + w / 2, y + h / 2, 0);
        matrix.scale(animScale, animScale, animScale);
        matrix.translate(-(x + w / 2), -(y + h / 2), 0);

        RenderUtil.Rounded.roundedRect(matrix, x, y, w, h, ColorUtil.getColor(8, 10, 16, 220), Round.of(8));

        renderRightPanelHeader(matrix, x, y, w);

        if (selectedElement != null) {
            settingsManager.renderPropertiesPanel(matrix, selectedElement, x + 15, y + 50, w - 30, h - 65, mouseX, mouseY, rightScroll);
        }

        matrix.pop();
    }

    private void renderRightPanelHeader(MatrixStack matrix, float x, float y, float w) {
        String title = selectedElement != null ? selectedElement.name : "Настройки элемента";
        Fonts.SF_MEDIUM.drawCenter(matrix, title, x + w / 2, y + 15, ColorUtil.getColor(220, 230, 250), 12);

        if (selectedElement != null) {
            String subtitle = "Настройки • " + selectedElement.id;
            Fonts.SF_MEDIUM.drawCenter(matrix, subtitle, x + w / 2, y + 30, ColorUtil.getColor(120, 130, 150), 8);
        }
    }

    private void renderTooltip(MatrixStack matrix, String text, int mouseX, int mouseY) {
        float tooltipW = Fonts.SF_MEDIUM.getWidth(text, 8) + 16;
        float tooltipH = 20;
        float tooltipX = mouseX - tooltipW / 2;
        float tooltipY = mouseY - tooltipH - 8;

        RenderUtil.Rounded.roundedRect(matrix, tooltipX, tooltipY, tooltipW, tooltipH,
                ColorUtil.getColor(20, 25, 35, 240), Round.of(4));
        RenderUtil.Rounded.roundedOutline(matrix, tooltipX, tooltipY, tooltipW, tooltipH, 1f,
                ColorUtil.getColor(60, 70, 90), Round.of(4));

        Fonts.SF_MEDIUM.drawCenter(matrix, text, tooltipX + tooltipW / 2, tooltipY + 7, ColorUtil.getColor(255, 255, 255), 8);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (handleTopBarClicks(mouseX, mouseY)) return true;
            if (handleLeftPanelClicks(mouseX, mouseY)) return true;
            if (handleMainAreaClicks(mouseX, mouseY)) return true;
            if (selectedElement != null && handleRightPanelClicks(mouseX, mouseY)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleTopBarClicks(double mouseX, double mouseY) {
        float x = padding;
        float y = padding;
        float w = width - padding * 2;
        float btnSize = 32f;
        float btnY = y + 6;
        float btnX = x + w - btnSize * 4 - 40;

        for (int i = 0; i < 4; i++) {
            if (isHovered((int) mouseX, (int) mouseY, btnX, btnY, btnSize, btnSize)) {
                switch (i) {
                    case 0:
                        showPresets();
                        break;
                    case 1:
                        resetAllElements();
                        break;
                    case 2:
                        saveAllSettings();
                        break;
                    case 3:
                        this.onClose();
                        break;
                }
                return true;
            }
            btnX += btnSize + 10;
        }
        return false;
    }

    private boolean handleLeftPanelClicks(double mouseX, double mouseY) {
        float x = padding;
        float y = padding + topBarHeight + 8;
        float w = leftPanelWidth;

        float searchX = x + 12;
        float searchY = y + 12;
        float searchW = w - 24;
        float searchH = 35f;

        if (isHovered((int) mouseX, (int) mouseY, searchX, searchY, searchW, searchH)) {
            isSearching = true;
            return true;
        }

        List<HudElement> filteredElements = getFilteredElements();
        float offsetY = leftScroll.getWheel();
        float currentY = y + 60 + offsetY;

        for (HudElement element : filteredElements) {
            if (isHovered((int) mouseX, (int) mouseY, x + 12, currentY, w - 24, 40)) {
                float btnX = x + w - 45;
                float btnY = currentY + 10;

                if (isHovered((int) mouseX, (int) mouseY, btnX, btnY, 20, 20)) {
                    org.privates.client.managers.module.settings.impl.BooleanSetting bs = hudModule.checks.get(element.id);
                    boolean newVal = !element.equals(1);
                    if (bs != null) {
                        boolean applied = false;
                        try {
                            Class<?> cls = bs.getClass();
                            String[] boolMethodNames = {
                                    "setValue", "setEnabled", "set", "setState", "setChecked", "setToggled",
                                    "setBoolean", "setVal", "toggle", "setActive", "setSelected"
                            };
                            for (String name : boolMethodNames) {
                                try {
                                    java.lang.reflect.Method m = cls.getMethod(name, boolean.class);
                                    m.invoke(bs, newVal);
                                    applied = true;
                                    break;
                                } catch (NoSuchMethodException ignored) {
                                }
                            }
                            if (!applied) {
                                for (String name : boolMethodNames) {
                                    try {
                                        java.lang.reflect.Method m = cls.getMethod(name, Boolean.class);
                                        m.invoke(bs, Boolean.valueOf(newVal));
                                        applied = true;
                                        break;
                                    } catch (NoSuchMethodException ignored) {
                                    }
                                }
                            }
                            if (!applied) {
                                String[] fieldNames = {"value", "val", "enabled", "toggled", "state", "checked"};
                                for (String fname : fieldNames) {
                                    try {
                                        java.lang.reflect.Field f = cls.getDeclaredField(fname);
                                        f.setAccessible(true);
                                        if (f.getType() == boolean.class) {
                                            f.setBoolean(bs, newVal);
                                            applied = true;
                                            break;
                                        } else if (f.getType() == Boolean.class) {
                                            f.set(bs, Boolean.valueOf(newVal));
                                            applied = true;
                                            break;
                                        }
                                    } catch (NoSuchFieldException ignored) {
                                    }
                                }
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                        if (!applied) {
                            System.err.println("HudCustomizeScreen: failed to set BooleanSetting for element " + element.id);
                        } else {
                            try {
                                java.lang.reflect.Method syncMethod = hudModule.getClass().getMethod("setElementEnabled", String.class, boolean.class);
                                syncMethod.invoke(hudModule, element.id, newVal);
                            } catch (NoSuchMethodException | IllegalAccessException |
                                     java.lang.reflect.InvocationTargetException ignored) {
                            }
                        }
                    } else {
                        try {
                            java.lang.reflect.Method syncMethod = hudModule.getClass().getMethod("setElementEnabled", String.class, boolean.class);
                            syncMethod.invoke(hudModule, element.id, newVal);
                        } catch (NoSuchMethodException | IllegalAccessException |
                                 java.lang.reflect.InvocationTargetException ignored) {
                        }
                    }
                    return true;
                }

                btnX += 25;
                if (isHovered((int) mouseX, (int) mouseY, btnX, btnY, 20, 20)) {
                    selectElement(element);
                    return true;
                }

                selectElement(element);
                return true;
            }
            currentY += 42f;
        }

        isSearching = false;
        return false;
    }

    private boolean handleMainAreaClicks(double mouseX, double mouseY) {
        float x = padding + leftPanelWidth + 8;
        float y = padding + topBarHeight + 8;
        float w = width - x - padding - (selectedElement != null ? rightPanelWidth + 8 : 0);
        float h = height - y - padding;

        float previewX = x + 15;
        float previewY = y + 45;
        float previewW = w - 30;
        float previewH = h - 60;

        if (!isHovered((int) mouseX, (int) mouseY, previewX, previewY, previewW, previewH)) {
            return false;
        }


        return false;
    }

    private boolean handleRightPanelClicks(double mouseX, double mouseY) {
        float x = width - padding - rightPanelWidth;
        float y = padding + topBarHeight + 8;
        float w = rightPanelWidth;

        return settingsManager.handlePropertyClicks(selectedElement, mouseX, mouseY, x + 15, y + 50, w - 30);
    }

    private void selectElement(HudElement element) {
        selectedElement = element;
        rightPanelAnimation.run(1.0, 0.4, Easings.EXPO_OUT, false);
    }

    private void startDragging(HudElement element, double mouseX, double mouseY, float elemX, float elemY) {
        isDragging = true;
        draggingElement = element;
        dragOffsetX = (float) mouseX - elemX;
        dragOffsetY = (float) mouseY - elemY;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
            isResizing = false;
            draggingElement = null;
            resizeHandle = "";
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging && draggingElement != null) {
            updateElementPosition(mouseX, mouseY);
            return true;
        }

        if (isResizing && selectedElement != null) {
            updateElementSize(mouseX, mouseY, deltaX, deltaY);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void updateElementPosition(double mouseX, double mouseY) {
        float mainAreaX = padding + leftPanelWidth + 8;
        float mainAreaY = padding + topBarHeight + 8;
        float previewX = mainAreaX + 15;
        float previewY = mainAreaY + 45;
        float scale = 0.8f;

        float newX = ((float) mouseX - dragOffsetX - previewX) / scale;
        float newY = ((float) mouseY - dragOffsetY - previewY) / scale;

        newX = Math.max(0, Math.min(newX, width / scale));
        newY = Math.max(0, Math.min(newY, height / scale));

        draggingElement.dragSetting.position.x = snapToGrid(newX, 5);
        draggingElement.dragSetting.position.y = snapToGrid(newY, 5);
    }

    private void updateElementSize(double mouseX, double mouseY, double deltaX, double deltaY) {
        DragSetting drag = selectedElement.dragSetting;
        float scale = 0.8f;

        switch (resizeHandle) {
            case "nw":
                drag.size.x = Math.max(50, drag.size.x - (float) deltaX / scale);
                drag.size.y = Math.max(20, drag.size.y - (float) deltaY / scale);
                break;
            case "ne":
                drag.size.x = Math.max(50, drag.size.x + (float) deltaX / scale);
                drag.size.y = Math.max(20, drag.size.y - (float) deltaY / scale);
                break;
            case "sw":
                drag.size.x = Math.max(50, drag.size.x - (float) deltaX / scale);
                drag.size.y = Math.max(20, drag.size.y + (float) deltaY / scale);
                break;
            case "se":
                drag.size.x = Math.max(50, drag.size.x + (float) deltaX / scale);
                drag.size.y = Math.max(20, drag.size.y + (float) deltaY / scale);
                break;
        }
    }

    private float snapToGrid(float value, float gridSize) {
        return Math.round(value / gridSize) * gridSize;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        float leftPanelX = padding;
        float leftPanelY = padding + topBarHeight + 8;
        float leftPanelW = leftPanelWidth;
        float leftPanelH = height - leftPanelY - padding;

        if (isHovered((int) mouseX, (int) mouseY, leftPanelX, leftPanelY, leftPanelW, leftPanelH)) {
            leftScroll.scroll(delta * -4);
            return true;
        }

        if (selectedElement != null) {
            float rightPanelX = width - padding - rightPanelWidth;
            float rightPanelY = padding + topBarHeight + 8;
            float rightPanelW = rightPanelWidth;
            float rightPanelH = height - rightPanelY - padding;

            if (isHovered((int) mouseX, (int) mouseY, rightPanelX, rightPanelY, rightPanelW, rightPanelH)) {
                rightScroll.scroll(delta * -4);
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isSearching) {
            return handleSearchInput(keyCode, scanCode, modifiers);
        }

        if (keyCode == 256) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean handleSearchInput(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            isSearching = false;
            return true;
        }

        if (keyCode == 259 && !searchText.isEmpty()) {
            searchText = searchText.substring(0, searchText.length() - 1);
            return true;
        }

        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (isSearching && codePoint >= 32 && codePoint <= 126) {
            searchText += codePoint;
            return true;
        }

        return settingsManager.handleTextInput(selectedElement, codePoint);
    }

    @Override
    public void onClose() {
        openAnimation.run(0.0, 0.3, Easings.EXPO_IN, false);
        saveAllSettings();
        super.onClose();
    }

    private void showPresets() {

    }

    private void resetAllElements() {
        for (HudElement element : hudElements) {
            settingsManager.resetSettings(element.id);
            resetElementPosition(element);
        }
    }

    private void resetElementPosition(HudElement element) {
        DragSetting drag = element.dragSetting;
        switch (element.id) {
            case "watermark":
                drag.position.set(10, 10);
                break;
            case "targethud":
                drag.position.set(width / 2 - 50, height / 2 - 25);
                break;
            case "arraylist":
                drag.position.set(width - 150, 50);
                break;
            case "information":
                drag.position.set(10, height - 50);
                break;
            default:
                drag.position.set(100 + (hudElements.indexOf(element) * 20), 100 + (hudElements.indexOf(element) * 30));
                break;
        }
    }

    private void saveAllSettings() {
        settingsManager.saveAllSettings();
    }

    private boolean isHovered(int mouseX, int mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static class HudElement {
        public final String name;
        public final String id;
        public final DragSetting dragSetting;
        public String icon = "●";

        public HudElement(String name, String id, DragSetting dragSetting) {
            this.name = name;
            this.id = id;
            this.dragSetting = dragSetting;
        }
    }
}