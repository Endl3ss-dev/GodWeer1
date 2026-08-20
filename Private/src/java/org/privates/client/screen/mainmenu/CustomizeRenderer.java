package org.privates.client.screen.mainmenu;

import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.Arrays;
import java.util.List;

import static org.privates.client.utils.math.Mathf.isHover;

public class CustomizeRenderer {

    public static void renderDraggableElements(MatrixStack matrixStack, int mouseX, int mouseY, MainMenuConfig.CustomizeData data) {
        List<DraggableElement> elements = Arrays.asList(
                new DraggableElement("menuBar", data.menuBar.x, data.menuBar.y, data.menuBar.width, data.menuBar.height),
                new DraggableElement("title", data.title.x - 150, data.title.y - 15, 300, 30),
                new DraggableElement("description", data.description.x - 200, data.description.y - 30, 400, 60),
                new DraggableElement("infoButton", data.infoButton.x, data.infoButton.y, data.infoButton.width, data.infoButton.height),
                new DraggableElement("customizeButton", data.customizeButton.x, data.customizeButton.y, data.customizeButton.width, data.customizeButton.height),
                new DraggableElement("flags", data.flagsContainer.x, data.flagsContainer.y, data.flagsContainer.width + 82, data.flagsContainer.height)
        );

        for (DraggableElement element : elements) {
            boolean hovered = isHover(mouseX, mouseY, element.x, element.y, element.width, element.height);
            int borderColor = hovered ? ColorUtil.getColor(100, 180, 255, 200) : ColorUtil.getColor(60, 120, 200, 150);
            int bgColor = ColorUtil.getColor(20, 40, 80, hovered ? 100 : 60);

            RenderUtil.Rounded.smooth(matrixStack, element.x, element.y, element.width, element.height, bgColor, Round.of(4));
            RenderUtil.Rounded.roundedOutline(matrixStack, element.x, element.y, element.width, element.height, 2f, borderColor, Round.of(4));

            String label = getElementLabel(element.id);
            Fonts.SF_MEDIUM.drawCenter(matrixStack, label, element.x + element.width / 2f, element.y + element.height / 2f - 5f,
                    ColorUtil.getColor(200, 220, 255), 10);

            renderSnapGrid(matrixStack, element.x, element.y, element.width, element.height);
        }
    }

    private static void renderSnapGrid(MatrixStack matrixStack, float x, float y, float width, float height) {
        float snapSize = 5f;
        int gridColor = ColorUtil.getColor(100, 180, 255, 50);

        for (float gx = x; gx <= x + width; gx += snapSize) {
            RenderUtil.drawRect(matrixStack, gx, y, 0.5f, height, gridColor, gridColor, gridColor, gridColor, false, true);
        }

        for (float gy = y; gy <= y + height; gy += snapSize) {
            RenderUtil.drawRect(matrixStack, x, gy, width, 0.5f, gridColor, gridColor, gridColor, gridColor, false, true);
        }
    }

    public static DraggableElement getHoveredElement(double mouseX, double mouseY, MainMenuConfig.CustomizeData data) {
        List<DraggableElement> elements = Arrays.asList(
                new DraggableElement("menuBar", data.menuBar.x, data.menuBar.y, data.menuBar.width, data.menuBar.height),
                new DraggableElement("title", data.title.x - 150, data.title.y - 15, 300, 30),
                new DraggableElement("description", data.description.x - 200, data.description.y - 30, 400, 60),
                new DraggableElement("infoButton", data.infoButton.x, data.infoButton.y, data.infoButton.width, data.infoButton.height),
                new DraggableElement("customizeButton", data.customizeButton.x, data.customizeButton.y, data.customizeButton.width, data.customizeButton.height),
                new DraggableElement("flags", data.flagsContainer.x, data.flagsContainer.y, data.flagsContainer.width + 82, data.flagsContainer.height)
        );

        for (DraggableElement element : elements) {
            if (isHover(mouseX, mouseY, element.x, element.y, element.width, element.height)) {
                return element;
            }
        }
        return null;
    }

    private static String getElementLabel(String id) {
        switch (id) {
            case "menuBar":
                return "Меню";
            case "title":
                return "Заголовок";
            case "description":
                return "Описание";
            case "infoButton":
                return "Информация";
            case "customizeButton":
                return "Кастомизация";
            case "flags":
                return "Флаги";
            default:
                return id;
        }
    }
}