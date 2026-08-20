package org.privates.client.screen.clickgui.component.setting.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.alt.Scissor;
import org.privates.client.managers.module.settings.impl.MultiItemSetting;
import org.privates.client.screen.clickgui.component.setting.SettingComponent;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiItemSettingComponent extends SettingComponent {
    private final MultiItemSetting value;
    private boolean expanded = false;
    private String searchQuery = "";
    private boolean searching = false;
    private float scrollOffset = 0f;
    private List<Item> allItems = new ArrayList<>();
    private List<Item> filteredItems = new ArrayList<>();

    public MultiItemSettingComponent(MultiItemSetting value) {
        super(value);
        this.value = value;
        this.allItems = Registry.ITEM.stream()
                .filter(item -> item != Items.AIR)
                .collect(Collectors.toList());
        this.filteredItems.addAll(allItems);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);
        value.getAnimation().update();

        float valueHeight = drawName(matrix, mouseX, mouseY, size.x - 40);

        String countText = value.getValue().size() + " items";
        Fonts.SF_MEDIUM.drawRight(matrix, countText, position.x + size.x - margin - 20,
                position.y + margin, ColorUtil.replAlpha(getWhite(), alpha()), 7);

        value.getAnimation().run(expanded ? 1 : 0, 0.25, Easings.SINE_OUT, true);
        float expandAnim = value.getAnimation().get();

        float buttonWidth = 16f;
        float buttonX = position.x + size.x - buttonWidth - margin;
        float buttonY = position.y + margin;

        RenderUtil.Rounded.roundedWithOutline(matrix, buttonX, buttonY, buttonWidth, 10f, 1f,
                ColorUtil.multAlpha(accentColor(), 0.15f * alphaPC()),
                ColorUtil.multAlpha(accentColor(), 0.25f * alphaPC()),
                Round.of(3f));

        Fonts.SF_BOLD.drawRight(matrix, "...", position.x + size.x - margin, buttonY + 1.5f,
                ColorUtil.replAlpha(getWhite(), alpha()), 7);

        float previewY = position.y + margin + valueHeight + margin;
        if (!value.getValue().isEmpty()) {
            int itemsPerRow = Math.max(1, (int) ((size.x - 8f) / 18f));
            int previewRows = Math.min(2, (int) Math.ceil((double) value.getValue().size() / itemsPerRow));

            int currentX = (int) (position.x + margin);
            int currentY = (int) previewY;
            int count = 0;

            for (Item item : value.getValue()) {
                if (count >= itemsPerRow * previewRows) break;

                mc.getItemRenderer().renderItemIntoGUI(new ItemStack(item), currentX, currentY);

                currentX += 18;
                count++;
                if (count % itemsPerRow == 0) {
                    currentX = (int) (position.x + margin);
                    currentY += 18;
                }
            }
            previewY = currentY + 18;
        }

        if (expandAnim > 0.01f) {
            float panelHeight = 120f * expandAnim;

            RenderUtil.Rounded.roundedWithOutline(matrix, position.x, previewY, size.x, panelHeight, 1f,
                    ColorUtil.multAlpha(ColorUtil.getColor(20, 20, 20), expandAnim * alphaPC()),
                    ColorUtil.multAlpha(ColorUtil.getColor(40, 40, 40), expandAnim * alphaPC()),
                    Round.of(3f));

            float searchBarY = previewY + 2f;
            RenderUtil.Rounded.roundedRect(matrix, position.x + 2f, searchBarY, size.x - 4f, 10f,
                    ColorUtil.multAlpha(ColorUtil.getColor(10, 10, 10), expandAnim * alphaPC()),
                    Round.of(2f));

            String searchText = searching ? searchQuery + (System.currentTimeMillis() % 1000 < 500 ? "_" : "") : "Search...";
            Fonts.SF_BOLD.draw(matrix, searchText, position.x + 4f, searchBarY + 1.5f,
                    ColorUtil.getColor(searching ? 255 : 120, (int) (expandAnim * alpha())), 6.5f);

            float saveButtonY = previewY + panelHeight - 16f;
            float saveButtonWidth = 50f;
            float saveButtonX = position.x + (size.x - saveButtonWidth) / 2f;

            boolean saveHovered = isHover(mouseX, mouseY, saveButtonX, saveButtonY, saveButtonWidth, 12f);
            RenderUtil.Rounded.roundedRect(matrix, saveButtonX, saveButtonY, saveButtonWidth, 12f,
                    ColorUtil.multAlpha(saveHovered ? accentColor() : ColorUtil.getColor(40, 40, 40), expandAnim * alphaPC()),
                    Round.of(3f));

            Fonts.SF_BOLD.drawCenter(matrix, "Save", saveButtonX + saveButtonWidth / 2f, saveButtonY + 2f,
                    ColorUtil.getColor(255, (int) (expandAnim * alpha())), 7);

            Scissor.push();
            Scissor.setFromComponentCoordinates((int) position.x, (int) (searchBarY + 12f),
                    (int) size.x, (int) (panelHeight - 30f));

            float itemStartY = searchBarY + 12f + scrollOffset;
            int itemsPerRow = Math.max(1, (int) ((size.x - 8f) / 18f));
            int currentX = (int) (position.x + 4f);
            int currentY = (int) itemStartY;

            List<Item> sortedItems = new ArrayList<>();
            sortedItems.addAll(filteredItems.stream().filter(value::contains).collect(Collectors.toList()));
            sortedItems.addAll(filteredItems.stream().filter(item -> !value.contains(item)).collect(Collectors.toList()));

            for (Item item : sortedItems) {
                boolean isSelected = value.contains(item);
                boolean isHovered = isHover(mouseX, mouseY, currentX, currentY, 16, 16);

                if (isSelected || isHovered) {
                    RenderUtil.Rounded.roundedRect(matrix, currentX - 1f, currentY - 1f, 18f, 18f,
                            ColorUtil.multAlpha(isSelected ? accentColor() : ColorUtil.getColor(255, 255, 255, 50),
                                    expandAnim * alphaPC()),
                            Round.of(2f));
                }

                mc.getItemRenderer().renderItemIntoGUI(new ItemStack(item), currentX, currentY);

                currentX += 18;
                if (currentX + 18 > position.x + size.x - 4f) {
                    currentX = (int) (position.x + 4f);
                    currentY += 18;
                }
            }

            Scissor.unset();
            Scissor.pop();
        }

        float totalPreviewHeight = !value.getValue().isEmpty() ?
                (Math.min(2, (int) Math.ceil((double) value.getValue().size() / Math.max(1, (int) ((size.x - 8f) / 18f)))) * 18f) : 0f;

        size.y = margin + valueHeight + margin + totalPreviewHeight +
                (expandAnim > 0.01f ? 120f * expandAnim : 0f) + margin;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float valueHeight = valueHeight();
        float buttonWidth = 16f;
        float buttonX = position.x + size.x - buttonWidth - margin;
        float buttonY = position.y + margin;

        if (isLClick(button) && isHover(mouseX, mouseY, buttonX, buttonY, buttonWidth, 10f)) {
            expanded = !expanded;
            if (!expanded) {
                searching = false;
                searchQuery = "";
                updateFilter();
            }
            return true;
        }

        if (expanded) {
            float previewY = position.y + margin + valueHeight + margin;
            if (!value.getValue().isEmpty()) {
                int itemsPerRow = Math.max(1, (int) ((size.x - 8f) / 18f));
                int previewRows = Math.min(2, (int) Math.ceil((double) value.getValue().size() / itemsPerRow));
                previewY += previewRows * 18f;
            }

            float panelHeight = 120f * value.getAnimation().get();
            float searchBarY = previewY + 2f;

            if (isLClick(button) && isHover(mouseX, mouseY, position.x + 2f, searchBarY, size.x - 4f, 10f)) {
                searching = true;
                return true;
            }

            float saveButtonY = previewY + panelHeight - 16f;
            float saveButtonWidth = 50f;
            float saveButtonX = position.x + (size.x - saveButtonWidth) / 2f;

            if (isLClick(button) && isHover(mouseX, mouseY, saveButtonX, saveButtonY, saveButtonWidth, 12f)) {
                expanded = false;
                searching = false;
                searchQuery = "";
                updateFilter();
                return true;
            }

            float itemStartY = searchBarY + 12f + scrollOffset;
            int itemsPerRow = Math.max(1, (int) ((size.x - 8f) / 18f));
            int currentX = (int) (position.x + 4f);
            int currentY = (int) itemStartY;

            List<Item> sortedItems = new ArrayList<>();
            sortedItems.addAll(filteredItems.stream().filter(value::contains).collect(Collectors.toList()));
            sortedItems.addAll(filteredItems.stream().filter(item -> !value.contains(item)).collect(Collectors.toList()));

            for (Item item : sortedItems) {
                if (isLClick(button) && isHover(mouseX, mouseY, currentX, currentY, 16, 16)) {
                    value.toggle(item);
                    return true;
                }

                currentX += 18;
                if (currentX + 18 > position.x + size.x - 4f) {
                    currentX = (int) (position.x + 4f);
                    currentY += 18;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searching) {
            if (keyCode == Keyboard.KEY_ESCAPE.getKey()) {
                searching = false;
                searchQuery = "";
                updateFilter();
                return true;
            } else if (keyCode == Keyboard.KEY_BACKSPACE.getKey()) {
                if (!searchQuery.isEmpty()) {
                    searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                    updateFilter();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searching) {
            searchQuery += codePoint;
            updateFilter();
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        expanded = false;
        searching = false;
        searchQuery = "";
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (expanded) {
            scrollOffset += (float) (delta * 10f);
            scrollOffset = Mathf.clamp(scrollOffset, -Math.max(0, (filteredItems.size() / 5) * 18f - 90f), 0);
            return true;
        }
        return false;
    }

    private void updateFilter() {
        if (searchQuery.isEmpty()) {
            filteredItems.clear();
            filteredItems.addAll(allItems);
        } else {
            filteredItems = allItems.stream()
                    .filter(item -> {
                        String name = item.getName().getString().toLowerCase();
                        return name.contains(searchQuery.toLowerCase());
                    })
                    .collect(Collectors.toList());
        }
        scrollOffset = 0f;
    }
}