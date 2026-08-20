package org.privates.client.managers.module.impl.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.text.TextAlign;
import org.privates.client.utils.render.text.TextBox;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.privates.client.api.interfaces.IMinecraft.mc;

public class SpookyFarmGui extends Screen {

    private final SpookyFarm farmModule;

    private static class PriceSetting {
        final String name;
        final String description;
        final net.minecraft.item.Item item;
        final TextBox textField;
        final Animation fieldAnimation;
        final Animation hoverAnimation;
        boolean fieldFocused;
        int minValue;
        int maxValue;

        PriceSetting(String name, String description, net.minecraft.item.Item item, int currentValue, int minValue, int maxValue) {
            this.name = name;
            this.description = description;
            this.item = item;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.textField = new TextBox(new org.joml.Vector2f(), Fonts.SF_REGULAR, 7, Theme.getInstance().textColor(), TextAlign.LEFT, String.valueOf(currentValue), 0, false, false);
            this.fieldAnimation = new Animation();
            this.fieldAnimation.set(0.0);
            this.hoverAnimation = new Animation();
            this.hoverAnimation.set(0.0);
            this.fieldFocused = false;
        }
    }

    private final Map<String, PriceSetting> priceSettings = new LinkedHashMap<>();
    private final Map<PriceSetting, Animation> itemAnimations = new LinkedHashMap<>();

    private final Animation openAnimation = new Animation();
    private final Animation closeAnimation = new Animation();
    private final Animation scrollbarAnimation = new Animation();
    private boolean closing = false;

    private float scrollOffset = 0;
    private float targetScrollOffset = 0;
    private boolean draggingScroll = false;
    private float dragStartY = 0;
    private float dragStartOffset = 0;
    private static final int ITEM_HEIGHT = 50;
    private static final int VISIBLE_ITEMS_COUNT = 7;

    private boolean wasGuiKeyPressed = false;

    public SpookyFarmGui() {
        super(new StringTextComponent("SpookyFarm GUI"));
        this.farmModule = SpookyFarm.getInstance();
        setupPriceSettings();
    }

    private void setupPriceSettings() {
        priceSettings.put("diamond", new PriceSetting("Алмаз", "Максимальная цена за 64 алмаза", Items.DIAMOND, farmModule.getDiamondMaxPrice(), 200, 1000));
        priceSettings.put("goldIngot", new PriceSetting("Золотой слиток", "Максимальная цена за 64 слитка", Items.GOLD_INGOT, farmModule.getGoldIngotMaxPrice(), 35, 400));
        priceSettings.put("sellExp", new PriceSetting("Продажа Exp", "Цена продажи бутылки опыта", Items.EXPERIENCE_BOTTLE, farmModule.getSellExpPrice(), 85, 10000));
    }

    @Override
    protected void init() {
        super.init();
        this.openAnimation.run(1.0, 0.4, Easings.EXPO_OUT);
        this.closeAnimation.set(0.0);
        this.closing = false;
        this.scrollbarAnimation.set(0.0);
        this.scrollOffset = 0;
        this.targetScrollOffset = 0;

        for (PriceSetting setting : priceSettings.values()) {
            setting.textField.setText(String.valueOf(getCurrentValue(setting)));
            setting.fieldAnimation.set(0.0);
            setting.hoverAnimation.set(0.0);
            setting.fieldFocused = false;

            Animation itemAnim = new Animation();
            itemAnim.set(0.0);
            itemAnim.run(1.0, 0.3 + Math.random() * 0.2, Easings.EXPO_OUT);
            itemAnimations.put(setting, itemAnim);
        }

        this.children.clear();
    }

    private int getCurrentValue(PriceSetting setting) {
        if (setting.name.equals("Алмаз")) return farmModule.getDiamondMaxPrice();
        if (setting.name.equals("Золотой слиток")) return farmModule.getGoldIngotMaxPrice();
        if (setting.name.equals("Продажа Exp")) return farmModule.getSellExpPrice();
        return 0;
    }

    @Override
    public void onClose() {
        if (this.closing) return;
        this.closing = true;

        for (Map.Entry<String, PriceSetting> entry : priceSettings.entrySet()) {
            PriceSetting setting = entry.getValue();
            int value = getIntValue(setting);

            switch (entry.getKey()) {
                case "diamond":
                    farmModule.setDiamondMaxPrice(value);
                    break;
                case "goldIngot":
                    farmModule.setGoldIngotMaxPrice(value);
                    break;
                case "sellExp":
                    farmModule.setSellExpPrice(value);
                    break;

            }
        }

        this.closeAnimation.run(1.0, 0.3, Easings.EXPO_IN);
        this.openAnimation.run(0.0, 0.3, Easings.EXPO_IN);
    }

    private int getIntValue(PriceSetting setting) {
        try {
            String text = setting.textField.getText();
            if (!text.isEmpty() && !text.equals("0")) {
                int value = Integer.parseInt(text.replaceAll("[^0-9]", ""));
                return Math.max(setting.minValue, Math.min(setting.maxValue, value));
            }
        } catch (NumberFormatException ignored) {
        }
        return setting.minValue;
    }

    private void updateAnimations() {
        openAnimation.update();
        closeAnimation.update();
        scrollbarAnimation.update();

        for (PriceSetting setting : priceSettings.values()) {
            setting.fieldAnimation.update();
            setting.hoverAnimation.update();
        }

        for (Animation anim : itemAnimations.values()) {
            anim.update();
        }

        float maxScroll = Math.max(0, priceSettings.size() * ITEM_HEIGHT - VISIBLE_ITEMS_COUNT * ITEM_HEIGHT + 25);
        targetScrollOffset = Math.max(0, Math.min(maxScroll, targetScrollOffset));
        if (Math.abs(scrollOffset - targetScrollOffset) > 0.1f) {
            scrollOffset += (targetScrollOffset - scrollOffset) * 0.15f;
        } else {
            scrollOffset = targetScrollOffset;
        }

        boolean isGuiKeyPressed = GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), farmModule.openGuiBind().getValue()) == GLFW.GLFW_PRESS;
        if (isGuiKeyPressed && !wasGuiKeyPressed) {
            this.onClose();
        }
        wasGuiKeyPressed = isGuiKeyPressed;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (closing && closeAnimation.isFinished() && openAnimation.isFinished()) {
            mc.displayScreen(null);
            return;
        }

        this.renderBackground(matrix);
        updateAnimations();

        Theme theme = Theme.getInstance();
        int clientColor = theme.clientColor();

        float easedAnim = openAnimation.get() * (1.0f - closeAnimation.get());
        if (easedAnim <= 0.01f) return;

        float mainWidth = 500;
        float mainHeight = (ITEM_HEIGHT * VISIBLE_ITEMS_COUNT) + 10;
        float headerHeight = 35;
        float totalHeight = headerHeight + 4 + mainHeight;

        float centerX = this.width / 2.0f;
        float centerY = this.height / 2.0f;

        float mainX = centerX - mainWidth / 2;
        float mainY = centerY - totalHeight / 2 + headerHeight + 4;
        float headerX = mainX;
        float headerY = mainY - headerHeight - 4;

        matrix.push();
        matrix.translate(centerX, centerY, 0);
        matrix.scale(easedAnim, easedAnim, 1);
        matrix.translate(-centerX, -centerY, 0);
        RenderSystem.color4f(1, 1, 1, easedAnim);

        RenderUtil.Shadow.drawShadow(matrix, headerX, headerY, mainWidth, headerHeight, 6, 0.7f, ColorUtil.getColor(0, 0, 0, 90), Round.of(8, 8, 0, 0));
        RenderUtil.Rounded.roundedRect(matrix, headerX, headerY, mainWidth, headerHeight, new Color(12, 12, 12, (int) (230 * easedAnim)).getRGB(), Round.of(8, 8, 0, 0));

        GlStateManager.pushMatrix();
        GlStateManager.translatef((headerX + 25), (headerY + headerHeight / 2), 1);
        GlStateManager.rotatef(180, 0, 0, 1);
        GlStateManager.translatef(-(headerX + 25), -(headerY + headerHeight / 2), 1);
        RenderUtil.Texture.roundedHead(mc.player.getLocationSkin(), headerX + 15, headerY + 7.5f, 20, 20, 8, easedAnim, 0);
        GlStateManager.popMatrix();

        Fonts.SF_MEDIUM.draw(matrix, "SpookyFarm", headerX + 45, headerY + 10, clientColor, 20f);

        RenderUtil.Shadow.drawShadow(matrix, mainX, mainY, mainWidth, mainHeight, 6, 0.7f, ColorUtil.getColor(0, 0, 0, 90), Round.of(0, 0, 8, 8));
        RenderUtil.Rounded.roundedRect(matrix, mainX, mainY, mainWidth, mainHeight, new Color(12, 12, 12, (int) (230 * easedAnim)).getRGB(), Round.of(0, 0, 8, 8));

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.makeScissorBox(mainX, mainY, mainX + mainWidth, mainY + mainHeight);

        float currentItemY = mainY + 8 - scrollOffset;
        int itemIndex = 0;

        for (Map.Entry<String, PriceSetting> entry : priceSettings.entrySet()) {
            PriceSetting setting = entry.getValue();
            Animation itemAnim = itemAnimations.get(setting);
            float itemAlpha = itemAnim != null ? itemAnim.get() * easedAnim : easedAnim;

            if (currentItemY > mainY - ITEM_HEIGHT && currentItemY < mainY + mainHeight && itemAlpha > 0.01f) {
                float itemCenterY = currentItemY + ITEM_HEIGHT / 2;
                boolean isHovered = mouseX >= mainX + 10 && mouseX <= mainX + mainWidth - 10 && mouseY >= currentItemY && mouseY <= currentItemY + ITEM_HEIGHT;

                setting.hoverAnimation.run(isHovered ? 1.0 : 0.0, 0.2, Easings.EXPO_OUT);

                float itemRectX = mainX + 8;
                float itemRectY = currentItemY + 2;
                float itemRectWidth = mainWidth - 16;
                float itemRectHeight = ITEM_HEIGHT - 4;

                int baseColor = new Color(18, 18, 18, (int) (150 * itemAlpha)).getRGB();
                int hoveredColor = new Color(25, 25, 35, (int) (200 * itemAlpha)).getRGB();
                int finalColor = ColorUtil.interpolate(baseColor, hoveredColor, setting.hoverAnimation.get());

                RenderUtil.Shadow.drawShadow(matrix, itemRectX, itemRectY, itemRectWidth, itemRectHeight, 3, 0.3f, ColorUtil.getColor(0, 0, 0, 60), Round.of(6));
                RenderUtil.Rounded.roundedRect(matrix, itemRectX, itemRectY, itemRectWidth, itemRectHeight, finalColor, Round.of(6));

                float hoverScale = 1.0f + setting.hoverAnimation.get() * 0.4f;
                matrix.push();
                matrix.translate(mainX + 35, itemCenterY, 0);
                matrix.scale(hoverScale, hoverScale, 1);
                matrix.translate(-(mainX + 35), -itemCenterY, 0);
                drawItemIconWithShadow(new ItemStack(setting.item), (int) mainX + 27, (int) (itemCenterY - 8));
                matrix.pop();

                float textY = itemCenterY - 8;
                Fonts.SF_REGULAR.draw(matrix, setting.name, mainX + 65, textY - 8, ColorUtil.multAlpha(clientColor, itemAlpha), 15f);
                Fonts.SF_REGULAR.draw(matrix, setting.description, mainX + 65, textY + 8, ColorUtil.multAlpha(Color.GRAY.getRGB(), itemAlpha), 13f);

                float fieldX = mainX + mainWidth - 150;
                float fieldY = itemCenterY - 9;
                float fieldWidth = 120;
                float fieldHeight = 18;

                boolean fieldHovered = mouseX >= fieldX && mouseX <= fieldX + fieldWidth && mouseY >= fieldY && mouseY <= fieldY + fieldHeight;

                setting.fieldAnimation.run(setting.fieldFocused || fieldHovered ? 1.0 : 0.0, 0.2, Easings.EXPO_OUT);

                float fieldAlpha = 0.4f + setting.fieldAnimation.get() * 0.6f;
                int fieldBgColor = ColorUtil.multAlpha(new Color(40, 40, 50, 220).getRGB(), itemAlpha * fieldAlpha);

                RenderUtil.Rounded.roundedRect(matrix, fieldX, fieldY, fieldWidth, fieldHeight, fieldBgColor, Round.of(4));

                if (setting.fieldFocused) {
                    RenderUtil.Rounded.roundedOutline(matrix, fieldX, fieldY, fieldWidth, fieldHeight, 1.0f, ColorUtil.multAlpha(clientColor, itemAlpha), Round.of(4));
                }

                setting.textField.position.set(fieldX + 5, fieldY + 3);
                setting.textField.setWidth((int) fieldWidth - 10);
                setting.textField.setColor(ColorUtil.multAlpha(theme.textColor(), itemAlpha));
                setting.textField.draw(matrix);


                if (itemIndex < priceSettings.size() - 1) {
                    RenderUtil.Rounded.roundedRect(matrix, mainX + 15, currentItemY + ITEM_HEIGHT - 3, mainWidth - 30, 1, ColorUtil.multAlpha(ColorUtil.getColor(35, 35, 35), itemAlpha), Round.of(1));
                }
            }

            currentItemY += ITEM_HEIGHT;
            itemIndex++;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        int maxScroll = Math.max(0, priceSettings.size() * ITEM_HEIGHT - (int) mainHeight + 16);
        if (maxScroll > 0) {
            boolean scrollbarVisible = mouseX >= mainX + mainWidth - 18 && mouseX <= mainX + mainWidth && mouseY >= mainY && mouseY <= mainY + mainHeight;
            scrollbarAnimation.run(scrollbarVisible ? 1.0 : 0.4, 0.2, Easings.EXPO_OUT);

            float scrollBarHeight = Math.max(25, (mainHeight / (float) (priceSettings.size() * ITEM_HEIGHT)) * mainHeight);
            float scrollBarY = mainY + (scrollOffset / (float) maxScroll) * (mainHeight - scrollBarHeight);
            float scrollbarAlpha = scrollbarAnimation.get() * easedAnim;

            int scrollbarColor = ColorUtil.interpolate(ColorUtil.multAlpha(ColorUtil.getColor(70, 70, 70), scrollbarAlpha), ColorUtil.multAlpha(clientColor, scrollbarAlpha), Math.min(1.0f, scrollbarAnimation.get() * 2.5f));

            RenderUtil.Rounded.roundedRect(matrix, mainX + mainWidth - 10, mainY, 6, mainHeight, ColorUtil.multAlpha(ColorUtil.getColor(25, 25, 25), scrollbarAlpha * 0.6f), Round.of(3));
            RenderUtil.Shadow.drawShadow(matrix, mainX + mainWidth - 10, scrollBarY, 6, scrollBarHeight, 2, 0.4f, ColorUtil.getColor(0, 0, 0, 100), Round.of(3));
        }

        matrix.pop();
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    private void drawItemIconWithShadow(ItemStack stack, int x, int y) {
        RenderUtil.Shadow.drawShadow(new MatrixStack(), x - 2, y - 2, 20, 20, 3, 0.4f, ColorUtil.getColor(0, 0, 0, 120), Round.of(3));
        RenderSystem.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x, y, null);
        RenderHelper.disableStandardItemLighting();
        RenderSystem.popMatrix();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float mainWidth = 500;
        float mainHeight = (ITEM_HEIGHT * VISIBLE_ITEMS_COUNT) + 10;
        float mainX = (this.width / 2.0f) - mainWidth / 2;
        float mainY = (this.height / 2.0f) - mainHeight / 2 + 39 / 2.0f;

        if (mouseX >= mainX && mouseX <= mainX + mainWidth && mouseY >= mainY && mouseY <= mainY + mainHeight) {
            for (PriceSetting setting : priceSettings.values()) {
                setting.fieldFocused = false;
                setting.fieldAnimation.run(0.0, 0.2, Easings.EXPO_OUT);
            }

            float currentItemY = mainY + 8 - scrollOffset;

            for (Map.Entry<String, PriceSetting> entry : priceSettings.entrySet()) {
                PriceSetting setting = entry.getValue();

                if (currentItemY > mainY - ITEM_HEIGHT && currentItemY < mainY + mainHeight) {
                    float itemCenterY = currentItemY + ITEM_HEIGHT / 2;
                    float fieldX = mainX + mainWidth - 150;
                    float fieldY = itemCenterY - 9;
                    float fieldWidth = 120;
                    float fieldHeight = 18;

                    if (mouseX >= fieldX && mouseX <= fieldX + fieldWidth && mouseY >= fieldY && mouseY <= fieldY + fieldHeight) {
                        setting.fieldFocused = true;
                        setting.fieldAnimation.run(1.0, 0.2, Easings.EXPO_OUT);
                        setting.textField.mouse(mouseX, mouseY, button);
                        return true;
                    }

                    float resetButtonX = mainX + mainWidth - 90;
                    float resetButtonY = itemCenterY - 10;
                    float resetButtonSize = 20;

                    if (mouseX >= resetButtonX && mouseX <= resetButtonX + resetButtonSize && mouseY >= resetButtonY && mouseY <= resetButtonY + resetButtonSize) {
                        setting.textField.setText(String.valueOf(setting.minValue));
                        return true;
                    }
                }

                currentItemY += ITEM_HEIGHT;
            }

            int maxScroll = Math.max(0, priceSettings.size() * ITEM_HEIGHT - (int) mainHeight + 16);
            if (maxScroll > 0 && mouseX >= mainX + mainWidth - 10 && mouseX <= mainX + mainWidth - 4) {
                draggingScroll = true;
                dragStartY = (float) mouseY;
                dragStartOffset = targetScrollOffset;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingScroll = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingScroll) {
            float mainHeight = (ITEM_HEIGHT * VISIBLE_ITEMS_COUNT) + 10;
            int totalContentHeight = priceSettings.size() * ITEM_HEIGHT;
            float scrollableAreaHeight = mainHeight;
            float dy = (float) mouseY - dragStartY;
            targetScrollOffset = dragStartOffset + (dy / scrollableAreaHeight) * totalContentHeight;
            targetScrollOffset = Math.max(0, Math.min(Math.max(0, totalContentHeight - (int) mainHeight + 16), targetScrollOffset));
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        float mainHeight = (ITEM_HEIGHT * VISIBLE_ITEMS_COUNT) + 10;
        int maxScroll = Math.max(0, priceSettings.size() * ITEM_HEIGHT - (int) mainHeight + 16);
        if (maxScroll > 0) {
            targetScrollOffset -= (float) (delta * ITEM_HEIGHT * 1.5);
            targetScrollOffset = Math.max(0, Math.min(maxScroll, targetScrollOffset));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (PriceSetting setting : priceSettings.values()) {
            if (setting.fieldFocused) {
                setting.textField.keyPressed(keyCode);
                if (keyCode == 257) {
                    try {
                        String text = setting.textField.getText();
                        if (!text.isEmpty() && !text.equals("0")) {
                            int value = Integer.parseInt(text.replaceAll("[^0-9]", ""));
                            value = Math.max(setting.minValue, Math.min(setting.maxValue, value));
                            setting.textField.setText(String.valueOf(value));
                        } else {
                            setting.textField.setText(String.valueOf(getCurrentValue(setting)));
                        }
                    } catch (NumberFormatException ignored) {
                        setting.textField.setText(String.valueOf(getCurrentValue(setting)));
                    }
                    setting.fieldFocused = false;
                    setting.fieldAnimation.run(0.0, 0.2, Easings.EXPO_OUT);
                }
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (PriceSetting setting : priceSettings.values()) {
            if (setting.fieldFocused) {
                if (Character.isDigit(codePoint) || codePoint == 8) {
                    setting.textField.charTyped(codePoint);
                }
                return true;
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}