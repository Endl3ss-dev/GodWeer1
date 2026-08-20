package org.privates.client.screen.mainmenu;

import com.google.common.collect.Lists;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.List;
import java.util.Map;

import static org.privates.client.utils.math.Mathf.isHover;

public class MainMenuRenderer {

    public static void drawStaticBackground(MatrixStack matrix, float width, float height, float alpha, MainMenuConfig.ColorSettings colors) {
        RenderUtil.drawBackground(matrix);
    }

    public static void renderTopMenuBar(MatrixStack matrixStack, int mouseX, int mouseY, MainMenuConfig.CustomizeData data, float menuBarAnimation, float alpha, CustomizePanel customizePanel, Map<String, Animation> hoverAnimations) {
        if (data == null || data.menuBar == null || data.colors == null) return;
        if (customizePanel != null && !customizePanel.isElementVisible("menuBar")) return;

        MainMenuConfig.MenuBarElement menuBar = data.menuBar;
        float barWidth = menuBar.width * menuBarAnimation;
        float barHeight = menuBar.height;
        float barX = menuBar.x;
        float barY = menuBar.y;

        if (menuBarAnimation > 0.1f) {
            int bgColor = ColorUtil.multAlpha(data.colors.menuBarBg, alpha);
            RenderUtil.Rounded.smooth(matrixStack, barX, barY, barWidth, barHeight, bgColor, Round.of(menuBar.cornerRadius));
            int borderColor = ColorUtil.multAlpha(data.colors.accent, alpha * 0.5f);
            RenderUtil.Rounded.roundedOutline(matrixStack, barX, barY, barWidth, barHeight, 1f, borderColor, Round.of(menuBar.cornerRadius));

            if (data.menuItems != null) {
                renderHorizontalMenu(matrixStack, mouseX, mouseY, data.menuItems.toArray(new String[0]), barX, barY, barWidth, barHeight, menuBar.fontSize, alpha, data.colors, hoverAnimations);
            }
        }
    }

    private static void renderHorizontalMenu(MatrixStack matrixStack, int mouseX, int mouseY, String[] menuItems, float barX, float barY, float barWidth, float barHeight, float fontSize, float alpha, MainMenuConfig.ColorSettings colors, Map<String, Animation> hoverAnimations) {
        float itemWidth = barWidth / menuItems.length;
        for (int i = 0; i < menuItems.length; i++) {
            String menuItem = menuItems[i];
            float itemX = barX + i * itemWidth;

            Animation hoverAnim = hoverAnimations.computeIfAbsent("menu_" + menuItem, k -> new Animation());
            hoverAnim.run(isHover(mouseX, mouseY, itemX, barY, itemWidth, barHeight) ? 1.0 : 0.0, 0.2);
            float hoverProgress = hoverAnim.get();

            if (hoverProgress > 0.01f) {
                int hoverBg = ColorUtil.multAlpha(colors.accent, alpha * 0.4f * hoverProgress);
                RenderUtil.Rounded.smooth(matrixStack, itemX + 4, barY + 4, itemWidth - 8, barHeight - 8, hoverBg, Round.of(14));
            }

            int textColor = ColorUtil.interpolateColor(colors.primaryText, colors.accent, hoverProgress);
            Fonts.SF_MEDIUM.drawCenter(matrixStack, menuItem, itemX + itemWidth / 2f, barY + barHeight / 2f - 6f, ColorUtil.multAlpha(textColor, alpha), fontSize);

            if (i < menuItems.length - 1) {
                int sepColor = ColorUtil.multAlpha(colors.accent, alpha * 0.3f);
                RenderUtil.drawRect(matrixStack, itemX + itemWidth - 0.5f, barY + 10, 0.5f, barHeight - 20, sepColor, sepColor, sepColor, sepColor, false, true);
            }
        }
    }

    public static void renderTitle(MatrixStack matrixStack, MainMenuConfig.TextElement title, float titleAlpha, float alpha, MainMenuConfig.ColorSettings colors, CustomizePanel customizePanel) {
        if (customizePanel != null && !customizePanel.isElementVisible("title")) return;

        matrixStack.push();
        matrixStack.translate(title.x, title.y, 0);
        matrixStack.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotationDegrees(title.rotation));
        matrixStack.translate(-title.x, -title.y, 0);

        int primaryColor = ColorUtil.multAlpha(colors.primaryText, alpha * titleAlpha);
        RenderUtil.Shadow.drawShadow(matrixStack, title.x - Fonts.SERATI.getWidth(title.text, title.fontSize) / 2f, title.y - 4,
                Fonts.SERATI.getWidth(title.text, title.fontSize), Fonts.SERATI.getHeight(title.text, title.fontSize),
                14f, 0.8f, ColorUtil.multAlpha(colors.accent, alpha * 0.3f), Round.of(8));

        Fonts.SERATI.drawCenter(matrixStack, title.text, title.x, title.y, primaryColor, title.fontSize);
        matrixStack.pop();
    }

    public static void renderDescription(MatrixStack matrixStack, MainMenuConfig.TextElement description, String currentText, float alpha, float textAnimation, MainMenuConfig.ColorSettings colors, CustomizePanel customizePanel) {
        if (customizePanel != null && !customizePanel.isElementVisible("description")) return;

        matrixStack.push();
        matrixStack.translate(description.x, description.y, 0);
        matrixStack.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotationDegrees(description.rotation));
        matrixStack.translate(-description.x, -description.y, 0);

        String[] lines = wrapText(currentText, 75);
        for (int i = 0; i < lines.length; i++) {
            int textColor = ColorUtil.multAlpha(colors.secondaryText, alpha * textAnimation);
            Fonts.SF_REGULAR.drawCenter(matrixStack, lines[i], description.x, description.y + i * 16f, textColor, description.fontSize);
        }
        matrixStack.pop();
    }

    public static void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, MainMenuConfig.ButtonElement button, String defaultText, float alpha, MainMenuConfig.ColorSettings colors, CustomizePanel customizePanel, String elementId, Map<String, Animation> hoverAnimations) {
        if (customizePanel != null && !customizePanel.isElementVisible(elementId)) return;

        Animation hoverAnim = hoverAnimations.getOrDefault(elementId, new Animation());
        float hoverProgress = hoverAnim.get();

        float scale = 1.0f + 0.05f * hoverProgress;
        float btnX = button.x + button.width / 2f * (1 - scale);
        float btnY = button.y + button.height / 2f * (1 - scale);
        float btnW = button.width * scale;
        float btnH = button.height * scale;

        int buttonColor = ColorUtil.interpolateColor(colors.buttonBg, ColorUtil.multAlpha(colors.accent, 0.5f), hoverProgress);
        RenderUtil.Rounded.smooth(matrixStack, btnX, btnY, btnW, btnH, ColorUtil.multAlpha(buttonColor, alpha), Round.of(button.cornerRadius * scale));

        int borderColor = ColorUtil.interpolateColor(ColorUtil.multAlpha(colors.accent, 0.6f), colors.accent, hoverProgress);
        RenderUtil.Rounded.roundedOutline(matrixStack, btnX, btnY, btnW, btnH, 1.5f, ColorUtil.multAlpha(borderColor, alpha), Round.of(button.cornerRadius * scale));

        if (hoverProgress > 0.01f) {
            RenderUtil.Shadow.drawShadow(matrixStack, btnX, btnY, btnW, btnH, 10f, 0.6f * hoverProgress, ColorUtil.multAlpha(colors.accent, alpha * 0.3f), Round.of(button.cornerRadius * scale));
        }

        String text = (button.text != null && !button.text.isEmpty()) ? button.text : defaultText;
        int textColor = ColorUtil.multAlpha(colors.primaryText, alpha);
        Fonts.SF_MEDIUM.drawCenter(matrixStack, text, button.x + button.width / 2f, button.y + button.height / 2f - 6f, textColor, button.fontSize);
    }

    public static void renderFlags(MatrixStack matrixStack, int mouseX, int mouseY, MainMenuConfig.FlagsContainer flagsContainer, float alpha, String currentFlagMessage, MainMenuConfig.ColorSettings colors, CustomizePanel customizePanel, Map<String, Animation> hoverAnimations) {
        if (!flagsContainer.enabled || (customizePanel != null && !customizePanel.isElementVisible("flags"))) return;

        float flagSize = flagsContainer.width;
        float spacing = 12f;
        float startX = flagsContainer.x;
        float startY = flagsContainer.y;

        for (int i = 0; i < 2; i++) {
            Animation hoverAnim = hoverAnimations.getOrDefault("flag_" + i, new Animation());
            float hoverProgress = hoverAnim.get();
            float scale = 1.0f + 0.1f * hoverProgress;

            float flagX = startX + i * (flagSize + spacing);
            float flagY = startY;

            float scaledW = flagSize * scale;
            float scaledH = flagsContainer.height * scale;
            float scaledX = flagX + (flagSize - scaledW) / 2f;
            float scaledY = flagY + (flagsContainer.height - scaledH) / 2f;

            if (hoverProgress > 0.01f) {
                int glowColor = ColorUtil.multAlpha(colors.accent, alpha * 0.4f * hoverProgress);
                RenderUtil.Rounded.smooth(matrixStack, scaledX - 6, scaledY - 6, scaledW + 12, scaledH + 12, glowColor, Round.of(8 * scale));
            }

            int flagBg = ColorUtil.multAlpha(ColorUtil.getColor(240, 245, 255), alpha);
            RenderUtil.Rounded.smooth(matrixStack, scaledX, scaledY, scaledW, scaledH, flagBg, Round.of(6 * scale));
            drawFlag(matrixStack, scaledX, scaledY, scaledW, scaledH, i, alpha);
        }

        if (!currentFlagMessage.isEmpty()) {
            float msgWidth = Fonts.SF_REGULAR.getWidth(currentFlagMessage, 10) + 20;
            float msgX = startX;
            float msgY = startY - 28 - 10;
            int msgBg = ColorUtil.multAlpha(ColorUtil.getColor(15, 25, 45), alpha);
            RenderUtil.Rounded.smooth(matrixStack, msgX, msgY, msgWidth, 28, msgBg, Round.of(6));
            RenderUtil.Rounded.roundedOutline(matrixStack, msgX, msgY, msgWidth, 28, 1f, ColorUtil.multAlpha(colors.accent, alpha * 0.5f), Round.of(6));
            Fonts.SF_REGULAR.draw(matrixStack, currentFlagMessage, msgX + 10, msgY + 28 / 2f - 5f, ColorUtil.multAlpha(colors.primaryText, alpha), 10);
        }
    }

    private static void drawFlag(MatrixStack matrixStack, float x, float y, float width, float height, int flagIndex, float alpha) {
        switch (flagIndex) {
            case 0:
                RenderUtil.drawRect(matrixStack, x, y, width, height / 2f, ColorUtil.multAlpha(0xFF005BBB, alpha), ColorUtil.multAlpha(0xFF005BBB, alpha), ColorUtil.multAlpha(0xFF005BBB, alpha), ColorUtil.multAlpha(0xFF005BBB, alpha), false, true);
                RenderUtil.drawRect(matrixStack, x, y + height / 2f, width, height / 2f, ColorUtil.multAlpha(0xFFFFD500, alpha), ColorUtil.multAlpha(0xFFFFD500, alpha), ColorUtil.multAlpha(0xFFFFD500, alpha), ColorUtil.multAlpha(0xFFFFD500, alpha), false, true);
                break;
            case 1:
                RenderUtil.drawRect(matrixStack, x, y, width, height / 3f, ColorUtil.multAlpha(0xFFFFFFFF, alpha), ColorUtil.multAlpha(0xFFFFFFFF, alpha), ColorUtil.multAlpha(0xFFFFFFFF, alpha), ColorUtil.multAlpha(0xFFFFFFFF, alpha), false, true);
                RenderUtil.drawRect(matrixStack, x, y + height / 3f, width, height / 3f, ColorUtil.multAlpha(0xFF0052CC, alpha), ColorUtil.multAlpha(0xFF0052CC, alpha), ColorUtil.multAlpha(0xFF0052CC, alpha), ColorUtil.multAlpha(0xFF0052CC, alpha), false, true);
                RenderUtil.drawRect(matrixStack, x, y + 2 * height / 3f, width, height / 3f, ColorUtil.multAlpha(0xFFCC0000, alpha), ColorUtil.multAlpha(0xFFCC0000, alpha), ColorUtil.multAlpha(0xFFCC0000, alpha), ColorUtil.multAlpha(0xFFCC0000, alpha), false, true);
                break;
        }
    }

    private static String[] wrapText(String text, int maxLength) {
        if (text.length() <= maxLength) return new String[]{text};
        List<String> lines = Lists.newArrayList();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxLength) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
            }
            if (currentLine.length() > 0) currentLine.append(" ");
            currentLine.append(word);
        }
        if (currentLine.length() > 0) lines.add(currentLine.toString());
        return lines.toArray(new String[0]);
    }
}