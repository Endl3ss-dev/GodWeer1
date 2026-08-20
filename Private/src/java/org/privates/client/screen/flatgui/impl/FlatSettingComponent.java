package org.privates.client.screen.flatgui.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.screen.flatgui.AbstractPanel;
import org.privates.client.screen.flatgui.FlatGuiScreen;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.scroll.ScrollUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.List;

@Getter
@Setter
@Accessors(fluent = true)
public class FlatSettingComponent extends AbstractPanel {
    private final FlatGuiScreen flatGuiScreen;
    private final ModuleListWidget moduleListWidget;
    private final ScrollUtil scrollUtil = new ScrollUtil();
    private float settingsX;
    private float settingsY;
    private float settingsWidth;
    private float settingsHeight;
    //    private final GifRender gifRender = new GifRender(new Namespaced("texture/cat-roll.gif"));
    private final StopWatch stopWatch = new StopWatch();

    public FlatSettingComponent(FlatGuiScreen flatGuiScreen, ModuleListWidget moduleListWidget) {
        this.flatGuiScreen = flatGuiScreen;
        this.moduleListWidget = moduleListWidget;
    }

    @Override
    public void init() {
        updateScrollHeight();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        updateScrollHeight();
    }

    private void updateScrollHeight() {
        FlatModuleComponent selectedComponent = getSelectedComponent();
        if (selectedComponent == null) {
            scrollUtil.setMax(0, settingsHeight);
            return;
        }

        float columnWidth = (settingsWidth - flatGuiScreen.margin() * 3) / 2;
        float offsetLeft = 0;
        float offsetRight = 0;
        List<AbstractPanel> settingComponents = selectedComponent.settingComponents();
        for (int i = 0; i < settingComponents.size(); i++) {
            AbstractPanel component = settingComponents.get(i);
            if (!component.visible()) continue;

            boolean isLeftColumn = i % 2 == 0;
            float componentHeight = component.size().y;

            component.size().set(columnWidth, componentHeight);

            if (isLeftColumn) {
                offsetLeft += componentHeight + flatGuiScreen.margin();
            } else {
                offsetRight += componentHeight + flatGuiScreen.margin();
            }
        }

        float maxHeight = Math.max(offsetLeft, offsetRight);
        scrollUtil.setMax(maxHeight + flatGuiScreen.margin(), settingsHeight);
    }

    private FlatModuleComponent getSelectedComponent() {
        for (FlatModuleComponent component : moduleListWidget.moduleComponents()) {
            if (component.module() == flatGuiScreen.selectedModule()) {
                component.expanded(true);
                return component;
            }
        }
        return null;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        FlatModuleComponent selectedComponent = getSelectedComponent();

        float panelX = flatGuiScreen.width() / 2F - flatGuiScreen.size().x / 2F;
        float panelY = flatGuiScreen.height() / 2F - flatGuiScreen.size().y / 2F;
        float moduleListX = panelX + flatGuiScreen.margin() + 24F + flatGuiScreen.margin() + flatGuiScreen.margin();
        float moduleListY = panelY + flatGuiScreen.margin() * 3 + 8F;
        float moduleListWidth = 150F;
        float separatorX = moduleListX + moduleListWidth + flatGuiScreen.margin();

        settingsX = separatorX + flatGuiScreen.margin() + 2F;
        settingsY = moduleListY;
        settingsWidth = flatGuiScreen.size().x - (flatGuiScreen.margin() + 24F + flatGuiScreen.margin() + flatGuiScreen.margin() * 2 + moduleListWidth + flatGuiScreen.margin() + 2F + flatGuiScreen.margin());
        settingsHeight = flatGuiScreen.size().y - (flatGuiScreen.margin() * 4 + 8F);

        scrollUtil.update();
        float scrollOffset = scrollUtil.getWheel();

        float guiTop = panelY;
        float guiBottom = panelY + flatGuiScreen.size().y;
        if (settingsY < guiTop) {
            settingsY = guiTop;
        }
        if (settingsY + settingsHeight > guiBottom) {
            settingsY = guiBottom - settingsHeight;
        }

        RenderUtil.Rounded.smooth(matrix, settingsX, settingsY, settingsWidth, settingsHeight, ColorUtil.getColor(25, 25, 35, alpha() / 2F), Round.of(8));

        if (selectedComponent == null) {

            float mainFontSize = 10F;
            String mainText = "Тут пустота...";
            float mainTextWidth = Fonts.SF_MEDIUM.getWidth(mainText, mainFontSize);
            float mainTextX = settingsX + (settingsWidth - mainTextWidth) / 2;
            float mainTextY = settingsY + (settingsHeight / 2) - mainFontSize;

            Fonts.SF_MEDIUM.draw(matrix, mainText, mainTextX, mainTextY,
                    ColorUtil.getColor(255, 255, 255, alpha()), mainFontSize);

            float subFontSize = 7F;
            String subText = "выберите модуль для настройки";
            float subTextWidth = Fonts.SF_MEDIUM.getWidth(subText, subFontSize);
            float subTextX = settingsX + (settingsWidth - subTextWidth) / 2;
            float subTextY = mainTextY + mainFontSize + 5;

            Fonts.SF_MEDIUM.draw(matrix, subText, subTextX, subTextY,
                    ColorUtil.getColor(169, 169, 169, alpha()), subFontSize);
            try {

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return;
        }

        if (selectedComponent.settingComponents().isEmpty()) {


            float mainFontSize = 10F;
            String mainText = "Пока что тут пусто...";
            float mainTextWidth = Fonts.SF_MEDIUM.getWidth(mainText, mainFontSize);
            float mainTextX = settingsX + (settingsWidth - mainTextWidth) / 2;
            float mainTextY = settingsY + (settingsHeight / 2) - mainFontSize;

            Fonts.SF_MEDIUM.draw(matrix, mainText, mainTextX, mainTextY,
                    ColorUtil.getColor(255, 255, 255, alpha()), mainFontSize);

            float subFontSize = 7F;
            String subText = "но вы можете выбрать другой модуль";
            float subTextWidth = Fonts.SF_MEDIUM.getWidth(subText, subFontSize);
            float subTextX = settingsX + (settingsWidth - subTextWidth) / 2;
            float subTextY = mainTextY + mainFontSize + 5;

            Fonts.SF_MEDIUM.draw(matrix, subText, subTextX, subTextY,
                    ColorUtil.getColor(169, 169, 169, alpha()), subFontSize);
            try {

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return;
        }

        enableClipping(settingsX, settingsY, settingsX + settingsWidth, settingsY + settingsHeight);

        float columnWidth = (settingsWidth - flatGuiScreen.margin() * 3) / 2;
        float offsetLeft = 0;
        float offsetRight = 0;
        List<AbstractPanel> settingComponents = selectedComponent.settingComponents();
        for (int i = 0; i < settingComponents.size(); i++) {
            AbstractPanel component = settingComponents.get(i);
            if (!component.visible()) continue;

            boolean isLeftColumn = i % 2 == 0;
            float columnX = isLeftColumn ? settingsX + flatGuiScreen.margin() : settingsX + flatGuiScreen.margin() * 2 + columnWidth;
            float columnY = (isLeftColumn ? settingsY + offsetLeft : settingsY + offsetRight) + scrollOffset + 10;

            if (columnY + component.size().y >= settingsY && columnY <= settingsY + settingsHeight) {
                component.position().set(columnX, columnY);
                component.size().set(columnWidth, component.size().y);
                component.alpha(alpha());

                RenderUtil.Rounded.smooth(matrix, component.position().x, component.position().y, component.size().x, component.size().y, ColorUtil.getColor(20, 20, 28, alpha()), Round.of(4));
                component.render(matrix, mouseX, mouseY, partialTicks);
            }

            if (isLeftColumn) {
                offsetLeft += component.size().y + flatGuiScreen.margin();
            } else {
                offsetRight += component.size().y + flatGuiScreen.margin();
            }
        }

        disableClipping();

        scrollUtil.renderV(matrix, new org.joml.Vector2f(settingsX + settingsWidth - 2, settingsY), settingsHeight, alpha());
    }

    private void enableClipping(float x1, float y1, float x2, float y2) {
        int scaleFactor = (int) Minecraft.getInstance().getMainWindow().getScaleFactor();
        int scissorX = (int) (x1 * scaleFactor);
        int scissorY = (int) (Minecraft.getInstance().getMainWindow().getHeight() - (y2 * scaleFactor));
        int scissorWidth = (int) ((x2 - x1) * scaleFactor);
        int scissorHeight = (int) ((y2 - y1) * scaleFactor);

        RenderSystem.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }

    private void disableClipping() {
        RenderSystem.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        FlatModuleComponent selectedComponent = getSelectedComponent();
        if (selectedComponent == null) return false;

        for (AbstractPanel component : selectedComponent.settingComponents()) {
            if (!component.visible()) continue;
            if (component.position().y + component.size().y >= settingsY && component.position().y <= settingsY + settingsHeight) {
                if (component.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        FlatModuleComponent selectedComponent = getSelectedComponent();
        if (selectedComponent == null) return false;

        for (AbstractPanel component : selectedComponent.settingComponents()) {
            if (!component.visible()) continue;
            if (component.position().y + component.size().y >= settingsY && component.position().y <= settingsY + settingsHeight) {
                if (component.mouseReleased(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        FlatModuleComponent selectedComponent = getSelectedComponent();
        if (selectedComponent == null) return false;

        for (AbstractPanel component : selectedComponent.settingComponents()) {
            if (!component.visible()) continue;
            if (component.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        FlatModuleComponent selectedComponent = getSelectedComponent();
        if (selectedComponent == null) return false;

        for (AbstractPanel component : selectedComponent.settingComponents()) {
            if (!component.visible()) continue;
            if (component.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        FlatModuleComponent selectedComponent = getSelectedComponent();
        if (selectedComponent == null) return false;

        for (AbstractPanel component : selectedComponent.settingComponents()) {
            if (!component.visible()) continue;
            if (component.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClose() {
    }
}