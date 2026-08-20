package org.privates.client.screen.mainmenu;

import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.interfaces.IScreen;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.HashMap;
import java.util.Map;

public class CustomizePanel implements IScreen {
    private final Animation panelAnimation = new Animation();
    private String selectedElement = "";
    private final float panelX;
    private final float panelY;
    private final float panelWidth = 250;
    private final float panelHeight = 400;
    private final MainMenu mainMenu;

    private final Map<String, Boolean> elementVisibility = new HashMap<>();

    public CustomizePanel(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        this.panelX = mainMenu.width - panelWidth - 20;
        this.panelY = 50;
        initElementVisibility();
    }

    private void initElementVisibility() {
        elementVisibility.put("title", true);
        elementVisibility.put("description", true);
        elementVisibility.put("menuBar", true);
        elementVisibility.put("infoButton", true);
        elementVisibility.put("customizeButton", true);
        elementVisibility.put("flags", true);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        panelAnimation.update();

        renderElementsTab(matrix, mouseX, mouseY, partialTicks);
    }

    private void renderElementsTab(MatrixStack matrix, int mouseX, int mouseY, float startY) {
        float currentY = startY;

        String[] elements = {"Заголовок", "Описание", "Меню", "Информация", "Кастомизация", "Флаги"};
        String[] elementIds = {"title", "description", "menuBar", "infoButton", "customizeButton", "flags"};

        for (int i = 0; i < elements.length; i++) {
            float btnY = currentY + i * 35;
            String elementId = elementIds[i];
            boolean visible = elementVisibility.getOrDefault(elementId, true);

            Fonts.SF_REGULAR.draw(matrix, elements[i], panelX + 15, btnY + 5,
                    ColorUtil.getColor(200, 200, 200), 10);

            float toggleX = panelX + panelWidth - 40;
            float toggleY = btnY;
            float toggleW = 30;
            float toggleH = 15;

            boolean toggleHovered = isHover(mouseX, mouseY, toggleX, toggleY, toggleW, toggleH);
            int toggleColor = visible ?
                    (toggleHovered ? ColorUtil.getColor(0, 200, 0, 255) : ColorUtil.getColor(0, 150, 0, 200)) :
                    (toggleHovered ? ColorUtil.getColor(200, 0, 0, 255) : ColorUtil.getColor(150, 0, 0, 200));

            RenderUtil.Rounded.smooth(matrix, toggleX, toggleY, toggleW, toggleH, toggleColor, Round.of(7));

            String toggleText = visible ? "ON" : "OFF";
            Fonts.SF_REGULAR.drawCenter(matrix, toggleText, toggleX + toggleW / 2f, toggleY + toggleH / 2f - 4f,
                    ColorUtil.getColor(255, 255, 255), 8);

            if (visible) {
                float moveX = panelX + 15;
                float moveY = btnY + 20;
                float moveW = 60;
                float moveH = 12;

                boolean moveHovered = isHover(mouseX, mouseY, moveX, moveY, moveW, moveH);
                int moveColor = moveHovered ? ColorUtil.getColor(100, 100, 200, 200) : ColorUtil.getColor(80, 80, 150, 150);

                RenderUtil.Rounded.smooth(matrix, moveX, moveY, moveW, moveH, moveColor, Round.of(4));
                Fonts.SF_REGULAR.drawCenter(matrix, "Move", moveX + moveW / 2f, moveY + moveH / 2f - 3f,
                        ColorUtil.getColor(255, 255, 255), 7);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            String[] elementIds = {"title", "description", "menuBar", "infoButton", "customizeButton", "flags"};
            float startY = panelY + 40;

            for (int i = 0; i < elementIds.length; i++) {
                String elementId = elementIds[i];
                float btnY = startY + i * 35;

                float toggleX = panelX + panelWidth - 40;
                float toggleY = btnY;
                float toggleW = 30;
                float toggleH = 15;

                if (isHover(mouseX, mouseY, toggleX, toggleY, toggleW, toggleH)) {
                    elementVisibility.put(elementId, !elementVisibility.getOrDefault(elementId, true));
                    return true;
                }

                if (elementVisibility.getOrDefault(elementId, true)) {
                    float moveX = panelX + 15;
                    float moveY = btnY + 20;
                    float moveW = 60;
                    float moveH = 12;

                    if (isHover(mouseX, mouseY, moveX, moveY, moveW, moveH)) {
                        selectedElement = elementId;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isElementVisible(String elementId) {
        return elementVisibility.getOrDefault(elementId, true);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    @Override
    public void onClose() {
    }

    private boolean isHover(double mouseX, double mouseY, float x, float y, float width, float height) {
        return Mathf.isHover(mouseX, mouseY, x, y, width, height);
    }
}