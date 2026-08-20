package org.privates.client.managers.module.impl.misc;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.glfw.GLFW;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.privates.client.api.interfaces.IMinecraft.mc;

@Getter
public class AutoBuySpookyGui extends Screen {

    private final AutoBuySpooky autoBuyModule;
    private final Map<AutoBuySpooky.AutoBuyItemData, ItemSettings> itemSettingsMap = new HashMap<>();
    private final ArrayList<ItemDisplay> items = new ArrayList<>();

    private float x, y, width, height;
    private ItemDisplay hoveredItem;

    private boolean drag = false;
    private float startX, startY;
    private float alpha, alphaV;

    private String searchQuery = "";
    private boolean searchActive = false;
    private boolean searchExpanded = false;
    private float searchBarAlpha = 0f;
    private float searchExpandAnim = 0f;

    private String currentDescription = "";
    private float descriptionAlpha = 0f;
    private int typingProgress = 0;
    private long lastTypeTime = 0L;
    private float descBoxWidthAnim = 0f;

    private float scrollOffset = 0f;
    private float targetScrollOffset = 0f;
    private int highlightedIndex = -1;

    public AutoBuySpookyGui() {
        super(new StringTextComponent("AutoBuy GUI"));
        this.autoBuyModule = AutoBuySpooky.getInstance();
    }

    @Override
    protected void init() {
        if (width == 0 || height == 0) {
            this.width = 471.912f;
            this.height = 291.2f;
            this.x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
            this.y = mc.getMainWindow().getScaledHeight() / 2f - height / 2f;
        }

        if (items.isEmpty()) {
            for (AutoBuySpooky.AutoBuyItemData itemData : autoBuyModule.itemsData()) {
                items.add(new ItemDisplay(itemData, 0, 0));
            }
        }

        alpha = 1;
        super.init();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        alphaV = Mathf.fast(alphaV, alpha, 28);
        searchBarAlpha = Mathf.fast(searchBarAlpha, searchActive ? 1f : 0.6f, 16);
        searchExpandAnim = Mathf.fast(searchExpandAnim, searchExpanded ? 1f : 0f, 14);
        scrollOffset = Mathf.fast(scrollOffset, targetScrollOffset, 14);

        if (alphaV <= .1f && alpha == 0) {
            super.closeScreen();
            return;
        }

        Theme theme = Theme.getInstance();
        int bgColor = theme.background().getValue();

        BlurRenderer2.registerRenderCall(() -> {
            RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.multAlpha(bgColor, 0.5f), Round.of(12));
        });
        BlurRenderer2.draw(8);

        RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.multAlpha(bgColor, alphaV), Round.of(12));
        RenderUtil.Rounded.roundedOutline(stack, x, y, width, height, 1f, ColorUtil.multAlpha(new Color(255, 255, 255, 15).getRGB(), alphaV), Round.of(12));

        renderLogo(stack, mouseX, mouseY);
        renderSearchIcon(stack, mouseX, mouseY);

        float lineY = y + 35;
        RenderUtil.Rounded.roundedRect(stack, x + 8, lineY, width - 16, 1f, ColorUtil.multAlpha(new Color(255, 255, 255, 20).getRGB(), alphaV), Round.of(0.5f));

        renderDescription(stack);

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y + 41, width, height - 41);

        hoveredItem = null;
        recalculate();
        for (ItemDisplay item : items) {
            item.setScrollOffset(scrollOffset);
            item.render(stack, mouseX, mouseY);
            if (Mathf.isHover(mouseX, mouseY, item.getX(), item.getY() + item.getDelta() - scrollOffset, item.getWidth(), item.getHeight())) {
                hoveredItem = item;
            }
        }

        ScissorExp.unset();
        ScissorExp.pop();

        if (drag) {
            float diffX = mouseX - startX, diffY = mouseY - startY;
            float scaledWidth = mc.getMainWindow().getScaledWidth(), scaleHeight = mc.getMainWindow().getScaledHeight();
            diffX = (x + diffX + width) <= scaledWidth && (x + diffX) >= 0 ? diffX : 0;
            diffY = (y + diffY + height) <= scaleHeight && (y + diffY) >= 0 ? diffY : 0;
            x += diffX;
            y += diffY;

            if ((x + diffX + width) <= scaledWidth) {
                startX = mouseX;
                startY = mouseY;
            }
        }

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderLogo(MatrixStack stack, float mouseX, float mouseY) {
        Theme theme = Theme.getInstance();
        float logoSize = 8.5f;
        String logoText = "AutoBuy";
        float logoX = x + 12.4f;
        float logoY = y + 10.4f + (22.37f - logoSize) / 2f;
        int accentCol = theme.clientColor();

        Fonts.SF_MEDIUM.draw(stack, logoText, logoX, logoY, ColorUtil.multAlpha(accentCol, alphaV), logoSize);
    }

    private void renderSearchIcon(MatrixStack stack, float mouseX, float mouseY) {
        Theme theme = Theme.getInstance();
        float searchIconSize = 8f;
        float searchIconX = x + width - searchIconSize - 16f;
        float searchIconY = y + 17f;
        float maxSearchWidth = 120f;
        float currentSearchWidth = searchIconSize + 8f + (maxSearchWidth - (searchIconSize + 8f)) * searchExpandAnim;
        float searchBarX = searchIconX - currentSearchWidth + searchIconSize + 8f;

        boolean iconHovered = Mathf.isHover(mouseX, mouseY, searchIconX - 4f, searchIconY - 4f, searchIconSize + 8f, searchIconSize + 8f);
        searchBarAlpha = Mathf.fast(searchBarAlpha, (searchExpanded || iconHovered) ? 1f : 0.7f, 16);

        if (searchExpandAnim > 0.01f) {
            int bgAlpha = (int) ((35 + searchBarAlpha * 25) * searchExpandAnim);
            int borderAlpha = (int) ((70 + searchBarAlpha * 50) * searchExpandAnim);
            int accentCol = theme.clientColor();
            int bgColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(20, 20, 20, bgAlpha),
                    ColorUtil.multAlpha(accentCol, searchActive ? .06f : .03f),
                    searchActive ? 1f : 0f), alpha(searchExpandAnim));
            int borderColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(40, 40, 40, borderAlpha),
                    ColorUtil.multAlpha(accentCol, searchActive ? .12f : .06f),
                    searchActive ? 1f : 0f), alpha(searchExpandAnim));

            RenderUtil.Rounded.roundedWithOutline(stack, searchBarX, searchIconY - 4f, currentSearchWidth, searchIconSize + 8f, 0.65f,
                    bgColor, borderColor, Round.of(4f));

            if (searchExpandAnim > 0.3f) {
                String textToRender = searchQuery.isEmpty() && !searchActive ? "Поиск..." : searchQuery;
                float textX = searchBarX + 6f;
                float textY = searchIconY + (searchIconSize + 8f) / 2f - Fonts.SF_MEDIUM.getHeight(textToRender, 6.5f) / 2f - 4f;
                int textColor = searchQuery.isEmpty() && !searchActive ?
                        ColorUtil.getColor(140, 140, 140, alpha(searchBarAlpha * 0.7f * searchExpandAnim)) :
                        ColorUtil.getColor(255, 255, 255, alpha(searchBarAlpha * searchExpandAnim));

                ScissorExp.push();
                ScissorExp.setFromComponentCoordinates(textX, searchIconY - 4f, currentSearchWidth - (searchIconSize + 16f), searchIconSize + 8f);
                Fonts.SF_MEDIUM.draw(stack, textToRender, textX, textY, textColor, 6.5f);

                if (searchActive && System.currentTimeMillis() % 1000 > 500) {
                    float cursorX = textX + Fonts.SF_MEDIUM.getWidth(searchQuery, 6.5f);
                    float cursorAlpha = (float) Math.sin(System.currentTimeMillis() * 0.004) * 0.3f + 0.7f;
                    Fonts.SF_MEDIUM.draw(stack, "|", cursorX, textY - 0.5f,
                            ColorUtil.multAlpha(accentCol, alpha(cursorAlpha * searchBarAlpha * searchExpandAnim)), 6.5f);
                }
                ScissorExp.unset();
                ScissorExp.pop();
            }
        }

        int bgAlpha = (int) (35 + searchBarAlpha * 25);
        int borderAlpha = (int) (70 + searchBarAlpha * 50);
        RenderUtil.Rounded.roundedWithOutline(stack, searchIconX - 4f, searchIconY - 4f, searchIconSize + 8f, searchIconSize + 8f, 0.65f,
                ColorUtil.multAlpha(ColorUtil.getColor(20, 20, 20, bgAlpha), alpha(searchBarAlpha)),
                ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40, borderAlpha), alpha(searchBarAlpha)),
                Round.of(4f));

        int textCol = theme.textColor();
        Fonts.icon.draw(stack, "G", searchIconX, searchIconY, ColorUtil.multAlpha(textCol, alpha(searchBarAlpha)), searchIconSize);
    }

    private void renderDescription(MatrixStack stack) {
        if (hoveredItem != null) {
            long currentTime = System.currentTimeMillis();
            long hoverDuration = currentTime - hoveredItem.getHoverStartTime();

            if (hoverDuration >= 250L) {
                String description = hoveredItem.getItemData().displayName;
                if (description == null || description.isEmpty()) {
                    description = "Описание отсутствует";
                }

                if (!currentDescription.equals(description)) {
                    currentDescription = description;
                    typingProgress = 0;
                    lastTypeTime = System.currentTimeMillis();
                    descBoxWidthAnim = 12f;
                }

                descriptionAlpha = Mathf.fast(descriptionAlpha, 1f, 6);
            }
        } else {
            descriptionAlpha = Mathf.fast(descriptionAlpha, 0f, 6);
        }

        if (descriptionAlpha > 0.01f && !currentDescription.isEmpty()) {
            Theme theme = Theme.getInstance();
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTypeTime > 30 && typingProgress < currentDescription.length()) {
                typingProgress++;
                lastTypeTime = currentTime;
            }

            String displayText = currentDescription.substring(0, Math.min(typingProgress, currentDescription.length()));
            float fullDescWidth = Fonts.SF_MEDIUM.getWidth(currentDescription, 7f) + 12f;
            float currentTextWidth = Fonts.SF_MEDIUM.getWidth(displayText, 7f) + 12f;
            float targetBoxWidth = currentTextWidth + 6f;
            descBoxWidthAnim = Mathf.fast(descBoxWidthAnim, targetBoxWidth, 20);

            float descWidth = Math.min(descBoxWidthAnim, fullDescWidth);
            float descHeight = Fonts.SF_MEDIUM.getHeight(currentDescription, 7f) + 8f;
            float descX = x + (width - fullDescWidth) / 2f;
            float descY = y - descHeight - 8f;

            stack.push();
            stack.translate(descX + descWidth / 2f, descY + descHeight / 2f, 0);
            stack.scale(descriptionAlpha, descriptionAlpha, 1);
            stack.translate(-(descX + descWidth / 2f), -(descY + descHeight / 2f), 0);

            int bgAlpha = (int) ((35 + descriptionAlpha * 25) * descriptionAlpha);
            int borderAlpha = (int) ((70 + descriptionAlpha * 50) * descriptionAlpha);
            int bgColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(20, 20, 20, bgAlpha),
                    ColorUtil.multAlpha(theme.clientColor(), .06f),
                    descriptionAlpha), alphaV * descriptionAlpha);
            int borderColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(40, 40, 40, borderAlpha),
                    ColorUtil.multAlpha(theme.clientColor(), .12f),
                    descriptionAlpha), alphaV * descriptionAlpha);
            int textColor = ColorUtil.multAlpha(ColorUtil.overCol(
                    ColorUtil.getColor(180, 180, 180),
                    theme.textColor(),
                    descriptionAlpha * 0.8f), alphaV * descriptionAlpha);

            RenderUtil.Rounded.roundedWithOutline(stack, descX, descY, descWidth, descHeight, 0.65f,
                    bgColor, borderColor, Round.of(4f));

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(descX + 3f, descY + 2f, descWidth - 6f, descHeight - 4f);
            Fonts.SF_MEDIUM.draw(stack, displayText, descX + 6f, descY + 4f, textColor, 7f);

            if (typingProgress < currentDescription.length() && System.currentTimeMillis() % 600 < 300) {
                float cursorX = descX + 6f + Fonts.SF_MEDIUM.getWidth(displayText, 7f);
                Fonts.SF_MEDIUM.draw(stack, "_", cursorX, descY + 4f, textColor, 7f);
            }

            ScissorExp.unset();
            ScissorExp.pop();
            stack.pop();
        } else if (descriptionAlpha <= 0.01f) {
            typingProgress = 0;
            descBoxWidthAnim = 0f;
        }
    }

    private void recalculate() {
        int col = 0;
        float rowY = y + 44;
        float maxRowHeight = 0;

        for (int i = 0; i < items.size(); i++) {
            ItemDisplay item = items.get(i);
            int column = col % 3;

            if (column == 0) {
                if (col > 0) {
                    rowY += maxRowHeight + 8;
                }
                maxRowHeight = 0;
                item.setX(x + 10f);
            } else {
                int prevIdx = i - 1;
                if (prevIdx >= 0) {
                    ItemDisplay prev = items.get(prevIdx);
                    item.setX(prev.getX() + prev.getWidth() + 7);
                }
            }

            item.setY(rowY);
            item.recalculateHeight();
            maxRowHeight = Math.max(maxRowHeight, item.getTotalHeight());
            col++;
        }

        float contentHeight = height - 44;
        float totalContentHeight = 0;
        float tempRowY = 0;
        float tempMaxH = 0;
        for (int i = 0; i < items.size(); i++) {
            if (i % 3 == 0) {
                totalContentHeight += tempMaxH + (i > 0 ? 8 : 0);
                tempMaxH = 0;
            }
            tempMaxH = Math.max(tempMaxH, items.get(i).getTotalHeight());
        }
        totalContentHeight += tempMaxH;

        float maxScroll = Math.max(0, totalContentHeight - contentHeight);
        targetScrollOffset = Math.max(0, Math.min(targetScrollOffset, maxScroll));
    }

    private void updateSearchHighlight() {
        highlightedIndex = -1;
        if (searchQuery.isEmpty()) return;

        for (int i = 0; i < items.size(); i++) {
            ItemDisplay item = items.get(i);
            if (item.getItemData().displayName.toLowerCase().contains(searchQuery.toLowerCase())) {
                highlightedIndex = i;
                break;
            }
        }

        if (highlightedIndex != -1) {
            ItemDisplay target = items.get(highlightedIndex);
            float itemTop = target.getY() - (y + 44);
            float itemBottom = itemTop + target.getTotalHeight();
            float visibleHeight = height - 44;

            if (itemTop < scrollOffset) {
                targetScrollOffset = itemTop - 4;
            } else if (itemBottom > scrollOffset + visibleHeight) {
                targetScrollOffset = itemBottom - visibleHeight + 4;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float searchIconSize = 8f;
        float searchIconX = x + width - searchIconSize - 16f;
        float searchIconY = y + 17f;

        if (Mathf.isHover(mouseX, mouseY, searchIconX - 4f, searchIconY - 4f, searchIconSize + 8f, searchIconSize + 8f)) {
            searchExpanded = !searchExpanded;
            if (searchExpanded) searchActive = true;
            if (!searchExpanded) {
                searchQuery = "";
                highlightedIndex = -1;
            }
            return true;
        }

        if (searchExpanded) {
            float maxSearchWidth = 120f;
            float currentSearchWidth = searchIconSize + 8f + (maxSearchWidth - (searchIconSize + 8f)) * searchExpandAnim;
            float searchBarX = searchIconX - currentSearchWidth + searchIconSize + 8f;

            if (Mathf.isHover(mouseX, mouseY, searchBarX, searchIconY - 4f, currentSearchWidth, searchIconSize + 8f)) {
                searchActive = true;
                return true;
            } else {
                searchActive = false;
            }
        }

        startX = (float) mouseX;
        startY = (float) mouseY;

        for (ItemDisplay item : items)
            item.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);

        if (!anyHovered((float) mouseX, (float) mouseY) && Mathf.isHover(mouseX, mouseY, x, y, width, height))
            drag = true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (ItemDisplay item : items)
            item.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_RELEASE);
        drag = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (Mathf.isHover(mouseX, mouseY, x, y, width, height)) {
            targetScrollOffset -= delta * 12;
            float contentHeight = height - 44;
            float totalContentHeight = 0;
            float tempMaxH = 0;
            for (int i = 0; i < items.size(); i++) {
                if (i % 3 == 0) {
                    totalContentHeight += tempMaxH + (i > 0 ? 8 : 0);
                    tempMaxH = 0;
                }
                tempMaxH = Math.max(tempMaxH, items.get(i).getTotalHeight());
            }
            totalContentHeight += tempMaxH;
            float maxScroll = Math.max(0, totalContentHeight - contentHeight);
            targetScrollOffset = Math.max(0, Math.min(targetScrollOffset, maxScroll));
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchActive && !anyInputActive()) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                updateSearchHighlight();
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                searchQuery = "";
                searchActive = false;
                searchExpanded = false;
                highlightedIndex = -1;
            }
            return true;
        }

        for (ItemDisplay item : items) {
            if (item.settings.keyPressed(keyCode)) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchActive && !anyInputActive()) {
            searchQuery += codePoint;
            updateSearchHighlight();
            return true;
        }

        for (ItemDisplay item : items) {
            if (item.settings.charTyped(codePoint)) {
                return true;
            }
        }

        return super.charTyped(codePoint, modifiers);
    }

    private boolean anyInputActive() {
        for (ItemDisplay item : items) {
            if (item.settings.priceInputActive) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void closeScreen() {
        alpha = 0;
        for (ItemDisplay item : items) {
            ItemSettings settings = itemSettingsMap.get(item.getItemData());
            if (settings != null) {
                settings.saveToModule();
            }
        }
        autoBuyModule.savePrices();
    }

    private boolean anyHovered(float mouseX, float mouseY) {
        for (ItemDisplay item : items) {
            if (Mathf.isHover(mouseX, mouseY, item.getX(), item.getY() + item.getDelta() - scrollOffset, item.getWidth(), item.getHeight()) &&
                    Mathf.isHover(mouseX, mouseY, x, y, width, height))
                return true;
        }
        return false;
    }

    public float alpha(float alpha) {
        return alpha * alphaV;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Getter
    @Setter
    public class ItemDisplay {
        private final AutoBuySpooky.AutoBuyItemData itemData;

        private float x, y, width, height;
        private float delta;
        private float interpolate;
        private float dimInterpolate = 0f;
        private long hoverStartTime = 0L;
        private float scrollOffset = 0f;

        private ItemSettings settings;

        public ItemDisplay(AutoBuySpooky.AutoBuyItemData itemData, float x, float y) {
            this.itemData = itemData;
            this.x = x;
            this.y = y;
            this.width = 145;
            this.height = 20;

            this.settings = itemSettingsMap.computeIfAbsent(itemData, k -> new ItemSettings(this));
        }

        void render(MatrixStack stack, float mouseX, float mouseY) {
            float drawY = y - scrollOffset;
            float guiY = AutoBuySpookyGui.this.y;
            float guiHeight = AutoBuySpookyGui.this.height;
            if (!inRange(drawY, getTotalHeight(), guiY + 41, guiHeight - 41)) return;

            boolean isHighlighted = isCurrentlyHighlighted();
            boolean hasActiveSearch = !searchQuery.isEmpty();

            float targetDim = hasActiveSearch && !isHighlighted ? 1f : 0f;
            dimInterpolate = Mathf.fast(dimInterpolate, targetDim, 12);

            boolean hovered = isHovered(mouseX, mouseY, x, drawY, width, height);
            if (hovered) {
                if (hoverStartTime == 0L) hoverStartTime = System.currentTimeMillis();
            } else {
                hoverStartTime = 0L;
            }

            interpolate = Mathf.fast(interpolate, settings.enabled ? 1 : 0, 16);

            Theme theme = Theme.getInstance();

            int bgColor = ColorUtil.multAlpha(new Color(25, 25, 25, 200).getRGB(), alpha(1f));
            int outlineColor = ColorUtil.multAlpha(ColorUtil.overCol(new Color(255, 255, 255, 10).getRGB(),
                    ColorUtil.multAlpha(theme.clientColor(), 0.3f), interpolate), alpha(1f));

            if (dimInterpolate > 0.01f) {
                bgColor = ColorUtil.multAlpha(bgColor, 1f - dimInterpolate * 0.6f);
                outlineColor = ColorUtil.multAlpha(outlineColor, 1f - dimInterpolate * 0.7f);
            }

            RenderUtil.Rounded.roundedRect(stack, x, drawY, width, height, bgColor, Round.of(6));
            RenderUtil.Rounded.roundedOutline(stack, x, drawY, width, height, 0.8f, outlineColor, Round.of(6));

            float iconAlpha = 1f - dimInterpolate * 0.55f;
            RenderSystem.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            RenderSystem.color4f(1f, 1f, 1f, iconAlpha);
            mc.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(itemData.item), (int) (x + 4f), (int) (drawY + 12f));
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            RenderHelper.disableStandardItemLighting();
            RenderSystem.popMatrix();

            String displayText = itemData.displayName;
            int textColor = ColorUtil.multAlpha(ColorUtil.overCol(new Color(150, 150, 150).getRGB(),
                    new Color(220, 220, 220).getRGB(), interpolate), alpha(1f));
            if (dimInterpolate > 0.01f) {
                textColor = ColorUtil.multAlpha(textColor, 1f - dimInterpolate * 0.6f);
            }
            Fonts.SF_MEDIUM.draw(stack, displayText, x + 24f, drawY + 6f, textColor, 7.5f);

            if (inRange(drawY + 6f, 8, guiY + 41, guiHeight - 41)) {
                int toggleBgColor = ColorUtil.multAlpha(ColorUtil.overCol(new Color(40, 40, 40).getRGB(),
                        ColorUtil.multDark(theme.clientColor(), .7f), interpolate), alpha(1f));
                int toggleKnobColor = ColorUtil.multAlpha(ColorUtil.overCol(new Color(80, 80, 80).getRGB(),
                        theme.clientColor(), interpolate), alpha(1f));

                if (dimInterpolate > 0.01f) {
                    toggleBgColor = ColorUtil.multAlpha(toggleBgColor, 1f - dimInterpolate * 0.6f);
                    toggleKnobColor = ColorUtil.multAlpha(toggleKnobColor, 1f - dimInterpolate * 0.6f);
                }

                RenderUtil.Rounded.roundedRect(stack, x + width - 17f, drawY + 6f, 13, 8, toggleBgColor, Round.of(4));
                RenderUtil.Rounded.roundedRect(stack, x + width - 16f + interpolate * 5.5f, drawY + 7f, 6, 6, toggleKnobColor, Round.of(3f));
            }

            settings.render(stack, mouseX, mouseY, dimInterpolate);
        }

        private boolean isCurrentlyHighlighted() {
            if (searchQuery.isEmpty()) return true;
            return itemData.displayName.toLowerCase().contains(searchQuery.toLowerCase());
        }

        void mouse(float mouseX, float mouseY, int button, int action) {
            float drawY = y - scrollOffset;
            if (isHovered(mouseX, mouseY, x + width - 17f, drawY + 6f, 13, 8) && action == GLFW.GLFW_PRESS && button == 0) {
                settings.enabled = !settings.enabled;
                if (!settings.enabled) {
                    settings.price = 0;
                    settings.priceInput = "0";
                }
            }
            settings.mouse(mouseX, mouseY, button, action);
        }

        void recalculateHeight() {
            this.height = 20 + settings.getTotalHeight();
        }

        public float getTotalHeight() {
            return height;
        }

        public boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }

        public boolean inRange(float y, float height, float rangeY, float rangeHeight) {
            return y <= rangeY + rangeHeight && y + height >= rangeY;
        }

        public float alpha(float alpha) {
            return AutoBuySpookyGui.this.alpha(alpha);
        }
    }

    @Getter
    @Setter
    public class ItemSettings {
        private final ItemDisplay itemDisplay;
        private boolean enabled = true;
        private int price = 0;
        private String priceInput = "";
        private boolean priceInputActive = false;
        private float priceFieldWidth = 0f;

        public ItemSettings(ItemDisplay itemDisplay) {
            this.itemDisplay = itemDisplay;
            this.price = itemDisplay.getItemData().price;
            this.priceInput = String.valueOf(price);
        }

        void render(MatrixStack stack, float mouseX, float mouseY, float dimInterpolate) {
            Theme theme = Theme.getInstance();
            float drawY = itemDisplay.getY() - itemDisplay.getScrollOffset();
            float baseY = drawY + 20f;

            float currentY = baseY + 4.5f;
            float settingWidth = itemDisplay.getWidth() - 9f;

            int interpolate = ColorUtil.multAlpha(ColorUtil.overCol(theme.disabledColor(), theme.enabledColor(),
                    itemDisplay.getInterpolate()), itemDisplay.alpha(1));

            if (dimInterpolate > 0.01f) {
                interpolate = ColorUtil.multAlpha(interpolate, 1f - dimInterpolate * 0.6f);
            }

            Fonts.SF_MEDIUM.draw(stack, "Цена:", itemDisplay.getX() + 24f, currentY, interpolate, 8f);

            priceFieldWidth = Mathf.fast(priceFieldWidth, priceInputActive ? 80f : 60f, 16);

            float fieldX = itemDisplay.getX() + settingWidth - priceFieldWidth + 4.5f;
            float fieldY = currentY - 2f;
            float fieldHeight = 15f;

            RenderUtil.Rounded.roundedWithOutline(stack, fieldX, fieldY, priceFieldWidth, fieldHeight, 1f,
                    ColorUtil.multAlpha(interpolate, .1f), ColorUtil.multAlpha(interpolate, .2f), Round.of(2));

            String displayText = priceInputActive || !priceInput.isEmpty() ? priceInput : "0";
            if (priceInputActive && System.currentTimeMillis() % 1000 < 500) {
                displayText += "_";
            }

            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(fieldX + 2, fieldY, priceFieldWidth - 4, fieldHeight);
            Fonts.SF_MEDIUM.draw(stack, displayText, fieldX + 4f, fieldY + 4f, interpolate, 8f);
            ScissorExp.unset();
            ScissorExp.pop();
        }

        void mouse(float mouseX, float mouseY, int button, int action) {
            float drawY = itemDisplay.getY() - itemDisplay.getScrollOffset();
            float currentY = drawY + 20f + 4.5f;
            float settingWidth = itemDisplay.getWidth() - 9f;
            float fieldX = itemDisplay.getX() + settingWidth - priceFieldWidth + 4.5f;
            float fieldY = currentY - 2f;
            float fieldHeight = 15f;

            if (action == GLFW.GLFW_PRESS && button == 0) {
                if (Mathf.isHover(mouseX, mouseY, fieldX, fieldY, priceFieldWidth, fieldHeight)) {
                    priceInputActive = true;
                } else {
                    if (priceInputActive) {
                        priceInputActive = false;
                        try {
                            String cleanInput = priceInput.replaceAll("[^0-9]", "");
                            price = cleanInput.isEmpty() ? 0 : Integer.parseInt(cleanInput);
                        } catch (NumberFormatException e) {
                            price = 0;
                        }
                        priceInput = String.valueOf(price);
                    }
                }
            }
        }

        boolean keyPressed(int keyCode) {
            if (!priceInputActive) return false;

            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (!priceInput.isEmpty()) {
                    priceInput = priceInput.substring(0, priceInput.length() - 1);
                }
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                priceInputActive = false;
                try {
                    String cleanInput = priceInput.replaceAll("[^0-9]", "");
                    price = cleanInput.isEmpty() ? 0 : Integer.parseInt(cleanInput);
                } catch (NumberFormatException e) {
                    price = 0;
                }
                priceInput = String.valueOf(price);
                return true;
            }
            return false;
        }

        boolean charTyped(char codePoint) {
            if (!priceInputActive) return false;

            if (Character.isDigit(codePoint)) {
                priceInput += codePoint;
                return true;
            }
            return false;
        }

        float getTotalHeight() {
            return 15f + 4.5f;
        }

        void saveToModule() {
            int itemIndex = autoBuyModule.itemsData().indexOf(itemDisplay.getItemData());
            if (itemIndex != -1) {
                autoBuyModule.setPriceForItem(itemIndex, price);
            }
        }
    }
}