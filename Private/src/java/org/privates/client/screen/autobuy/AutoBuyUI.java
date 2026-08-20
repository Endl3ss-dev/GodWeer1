package org.privates.client.screen.autobuy;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.screen.autobuy.history.HistoryItem;
import org.privates.client.screen.autobuy.history.ItemBuy;
import org.privates.client.screen.autobuy.history.SelectedItem;
import org.privates.client.utils.autobuy.AutoBuyManager;
import org.privates.client.utils.autobuy.CustomAutoBuyItem;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;
import org.lwjgl.glfw.GLFW;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

@Getter
public class AutoBuyUI {

    private static AutoBuyUI INSTANCE;

    public static AutoBuyUI getInstance() {
        return INSTANCE;
    }

    public AutoBuyUI() {
        INSTANCE = this;
    }

    private final ArrayList<HistoryItem> historyItems = new ArrayList<>();
    private final ArrayList<ItemBuy> items = new ArrayList<>();
    private final ArrayList<SelectedItem> selectedItems = new ArrayList<>();
    private final Minecraft mc = Minecraft.getInstance();
    private float itemDelta, itemDeltaV,
            historyDelta, historyDeltaV,
            selectedDelta, selectedDeltaV,
            filterV,
            sortedAZV, sortedZAV, sortedSellV, sortedSetupV;

    private boolean filter;
    private boolean sortedAZ, sortedZA, sortedSell, sortedSetup;

    // Для обновления истории
    private long lastHistoryUpdate = 0;
    private static final long HISTORY_UPDATE_INTERVAL = 1000; // Обновляем каждую секунду

    public void render(MatrixStack stack, float mouseX, float mouseY) {
        // Обновляем историю периодически
        updateHistoryIfNeeded();

        items.clear();
        float offsetX = mc.getMainWindow().getScaledWidth() / 2f + 225;
        float offsetYItems = mc.getMainWindow().getScaledHeight() / 2f - 110;
        // Сначала все CustomAutoBuyItem
        for (CustomAutoBuyItem customAutoBuyItem : AutoBuyManager.getCustomAutoBuyItemList()) {
        }


        float x = mc.getMainWindow().getScaledWidth() / 2f - 218,
                y = mc.getMainWindow().getScaledHeight() / 2f - 110,
                width = 123,
                height = 220;

        if (historyDelta != historyDeltaV)
            historyDeltaV = (float) Mathf.lerp(historyDelta, historyDeltaV, .9);

        if (historyItems.size() * 26 + historyDelta < height && historyDelta < 0)
            historyDelta = (float) Mathf.lerp(-(historyItems.size() * 26 - (height - 20)), historyDelta, .9);
        if (historyDelta > 0)
            historyDelta = (float) Mathf.lerp(0, historyDelta, .9);

        if (itemDelta != itemDeltaV)
            itemDeltaV = (float) Mathf.lerp(itemDelta, itemDeltaV, .9);

        if (selectedDelta != selectedDeltaV)
            selectedDeltaV = (float) Mathf.lerp(selectedDelta, selectedDeltaV, .9);

        if (selectedItems.size() * 20 + selectedDelta < height && selectedDelta < 0)
            selectedDelta = (float) Mathf.lerp(-(selectedItems.size() * 20 - (height - 20)), selectedDelta, .9);
        if (selectedDelta > 0)
            selectedDelta = (float) Mathf.lerp(0, selectedDelta, .9);

        if (items.size() * 20 + itemDelta < height)
            itemDelta = (float) Mathf.lerp(-(items.size() * 20 - (height - 20)), itemDelta, .9);
        if (itemDelta > 0)
            itemDelta = (float) Mathf.lerp(0, itemDelta, .9);


        { // HISTORY
            RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.getColor(15, 15, 15, 194), Round.of(5));
            Fonts.SF_MEDIUM.draw(stack, "История", x + width / 2f - Fonts.SF_MEDIUM.getWidth("История", 8) / 2f + Fonts.ICOMOON.getWidth("a", 8) / 2f, y + 5, ColorUtil.getColor(225, 225, 225), 8);
            Fonts.ICOMOON.draw(stack, "a", x + width / 2f - Fonts.SF_MEDIUM.getWidth("История", 8) / 2f - Fonts.ICOMOON.getWidth("a", 8), y + 6, ColorUtil.getColor(225, 225, 225), 8);
            float offsetYHistory = y + 16 + historyDeltaV;
            ScissorUtil.enableScissor(x, y + 16, width, height - 16);


            ScissorUtil.disableScissor();
        }

        x += 315;
        { // SELECTED

            filterV = (float) Mathf.lerp(filter ? 105 : 0, filterV, .8);

            Collator collator = Collator.getInstance(new Locale("ru", "RU"));
            collator.setStrength(Collator.PRIMARY);

            Comparator<SelectedItem> comparator = sortedAZ || sortedZA
                    ? Comparator.comparing(SelectedItem::getName, collator)
                    : Comparator.comparingLong(SelectedItem::getTime);

            selectedItems.sort(sortedZA ? comparator.reversed() : comparator);

            RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.getColor(15, 15, 15, 194), Round.of(5));
            Fonts.SF_MEDIUM.draw(stack, "Выбранные предметы", x + width / 2f - Fonts.SF_MEDIUM.getWidth("Выбранные предметы", 8) / 2f + Fonts.ICOMOON.getWidth("b", 8) / 2f, y + 5, ColorUtil.getColor(225, 225, 225), 8);
            Fonts.ICOMOON.draw(stack, "b", x + width / 2f - Fonts.SF_MEDIUM.getWidth("Выбранные предметы", 8) / 2f - Fonts.ICOMOON.getWidth("b", 8), y + 6, ColorUtil.getColor(225, 225, 225), 8);
            Fonts.ICOMOON.draw(stack, "c", x + width - Fonts.ICOMOON.getWidth("c", 7) - 4, y + 7, ColorUtil.getColor(100, 100, 100), 7);

            if (filterV > .1f) {

                sortedAZV = (float) Mathf.lerp(sortedAZ ? 1 : 0, sortedAZV, .7);
                sortedZAV = (float) Mathf.lerp(sortedZA ? 1 : 0, sortedZAV, .7);
                sortedSetupV = (float) Mathf.lerp(sortedSetup ? 1 : 0, sortedSetupV, .7);
                sortedSellV = (float) Mathf.lerp(sortedSell ? 1 : 0, sortedSellV, .7);

                RenderUtil.Rounded.roundedRect(stack, x + width + 5, y, filterV, 50, ColorUtil.getColor(15, 15, 15, 194), Round.of(5));
                ScissorUtil.enableScissor(x + width + 5, y, filterV, 50);
                Fonts.SF_MEDIUM.draw(stack, "Фильтры", x + width + 50 - Fonts.SF_MEDIUM.getWidth("Фильтры", 7) / 2f + Fonts.ICOMOON.getWidth("c", 7) / 2f, y + 3, ColorUtil.getColor(225, 225, 225), 7);
                Fonts.ICOMOON.draw(stack, "c", x + width + 48 - Fonts.SF_MEDIUM.getWidth("Фильтры", 7) / 2f - Fonts.ICOMOON.getWidth("c", 7) / 2f, y + 4, ColorUtil.getColor(225, 225, 225), 7);
                Fonts.SF_MEDIUM.draw(stack, "По названию A-Z", x + width + 8, y + 7 + Fonts.SF_MEDIUM.getHeight("Фильтры", 7), ColorUtil.overCol(ColorUtil.getColor(225, 225, 225), Theme.getInstance().textColor(), sortedAZV), 7);
                Fonts.SF_MEDIUM.draw(stack, "По названию Z-A", x + width + 8, y + 9 + Fonts.SF_MEDIUM.getHeight("Фильтры", 7) + Fonts.SF_MEDIUM.getHeight("По названию A-Z", 7), ColorUtil.overCol(ColorUtil.getColor(225, 225, 225), Theme.getInstance().textColor(), sortedZAV), 7);
                Fonts.SF_MEDIUM.draw(stack, "Выключить с AutoSetup", x + width + 8, y + 11 + Fonts.SF_MEDIUM.getHeight("Фильтры", 7) + Fonts.SF_MEDIUM.getHeight("По названию A-Z", 7) * 2, ColorUtil.overCol(ColorUtil.getColor(225, 225, 225), Theme.getInstance().textColor(), sortedSetupV), 7);
                Fonts.SF_MEDIUM.draw(stack, "Выключить с авто продажей", x + width + 8, y + 13 + Fonts.SF_MEDIUM.getHeight("Фильтры", 7) + Fonts.SF_MEDIUM.getHeight("По названию A-Z", 7) * 2 + Fonts.SF_MEDIUM.getHeight("Выключить с AutoSetup", 7), ColorUtil.overCol(ColorUtil.getColor(225, 225, 225), Theme.getInstance().textColor(), sortedSellV), 7);
                ScissorUtil.disableScissor();
            }

            float offsetYSelected = y + 16 + selectedDeltaV;
            ScissorUtil.enableScissor(x, offsetYSelected - selectedDeltaV, width, height - 16);
            for (SelectedItem selectedItem : selectedItems) {
                if (inRange(offsetYSelected, 16, y + 16, height - 16) && (!sortedSetup || !selectedItem.isAutoSetup()) && (!sortedSell || !selectedItem.isAutoSell())) {
                    RectUtil.drawItemStack(selectedItem.getItemStack(), x + 4, offsetYSelected, false, 16, 16);
                    Fonts.SF_MEDIUM.draw(stack, selectedItem.getName(), x + 22, offsetYSelected, ColorUtil.getColor(225, 225, 225), 6);
                    Fonts.SF_MEDIUM.draw(stack, "Покупка: ", x + 22, offsetYSelected + Fonts.SF_MEDIUM.getHeight(selectedItem.getName(), 6), ColorUtil.getColor(137, 137, 137), 6);
                    Fonts.SF_MEDIUM.draw(stack, selectedItem.getPrice() + "¤", x + 22 + Fonts.SF_SEMIBOLD.getWidth("Покупка: ", 6), offsetYSelected + Fonts.SF_MEDIUM.getHeight(selectedItem.getName(), 6), Theme.getInstance().textColor(), 6);
                    Fonts.SF_MEDIUM.draw(stack, "Продажа: ", x + 22, offsetYSelected + Fonts.SF_MEDIUM.getHeight(selectedItem.getName(), 6) + Fonts.SF_MEDIUM.getHeight("Покупка: ", 6), ColorUtil.getColor(137, 137, 137), 6);
                    Fonts.SF_MEDIUM.draw(stack, selectedItem.getSellPrice() + "¤", x + 22 + Fonts.SF_SEMIBOLD.getWidth("Продажа: ", 6), offsetYSelected + Fonts.SF_MEDIUM.getHeight(selectedItem.getName(), 6) + Fonts.SF_MEDIUM.getHeight("Покупка: ", 6), Theme.getInstance().textColor(), 6);

                    selectedItem.setAnim((isHovered(mouseX, mouseY, x, offsetYSelected, width, 16) ? 1 : 0));
                    selectedItem.setAnimV((float) Mathf.lerp(selectedItem.getAnim(), selectedItem.getAnimV(), .7f));
                    selectedItem.setSelectedV((float) Mathf.lerp((selectedItem.isSelected()) ? 1 : 0, selectedItem.getSelectedV(), .7f));

                    if (selectedItem.getAnimV() > .01f) {
                        RenderUtil.Rounded.roundedRect(stack, x + width + 5.5f - (29 * selectedItem.getAnimV()), offsetYSelected + 3, 9, 10, (isHovered(mouseX, mouseY, x + width - 24, offsetYSelected + 3, 9, 10)) ? ColorUtil.getColor(47, 47, 47, 130) : ColorUtil.getColor(77, 77, 77, 130), Round.of(4, 4, 0, 0));
                        RenderUtil.Rounded.roundedRect(stack, x + width + 4.5f - (19 * selectedItem.getAnimV()), offsetYSelected + 3, 9, 10, (isHovered(mouseX, mouseY, x + width - 15, offsetYSelected + 3, 9, 10)) ? ColorUtil.getColor(235, 68, 68, 133) : ColorUtil.getColor(255, 98, 98, 133), Round.of(0, 0, 4, 4));
                        Fonts.ICOMOON.draw(stack, "d", x + width + 5.5f - (24.5f * selectedItem.getAnimV()) - Fonts.ICOMOON.getWidth("d", 6.5f) / 2f, offsetYSelected + 8 - Fonts.ICOMOON.getHeight("d", 6.5f) / 2.5f, ColorUtil.getColor(211, 211, 211), 6.5f);
                        Fonts.ICOMOON.draw(stack, "e", x + width + 5.5f - (15.5f * selectedItem.getAnimV()) - Fonts.ICOMOON.getWidth("e", 6.5f) / 2f, offsetYSelected + 8 - Fonts.ICOMOON.getHeight("d", 6.5f) / 2.5f, ColorUtil.getColor(211, 211, 211), 6.5f);
                    }

                }
                if ((!sortedSetup || !selectedItem.isAutoSetup()) && (!sortedSell || !selectedItem.isAutoSell()))
                    offsetYSelected += selectedItem.getHeight() + 4;

            }
            ScissorUtil.disableScissor();
            offsetYSelected = y + 16 + selectedDeltaV;
            for (SelectedItem item : selectedItems) {
                if (item.getSelectedV() > .1f) {
                    RenderUtil.Rounded.roundedRect(stack, x + width + 5, offsetYSelected - 1, 70 * item.getSelectedV(), 18, ColorUtil.getColor(15, 15, 15, 194), Round.of(5));
                    ScissorUtil.enableScissor(x + 5, offsetYSelected - 1, width - 18 + 70 * item.getSelectedV(), 18);
                    Fonts.SF_MEDIUM.draw(stack, "AutoSetup: ", x + width + 8, offsetYSelected + 2, ColorUtil.getColor(137, 137, 137), 6);
                    Fonts.SF_MEDIUM.draw(stack, "Авто продажа: ", x + width + 8, offsetYSelected + 3 + Fonts.SF_MEDIUM.getHeight("AutoSetup: ", 6), ColorUtil.getColor(137, 137, 137), 6);
                    ScissorUtil.disableScissor();

                    item.setAutoSellV((float) Mathf.lerp(item.isAutoSell() ? 1 : 0, item.getAutoSellV(), .7));
                    item.setAutoSetupV((float) Mathf.lerp(item.isAutoSetup() ? 1 : 0, item.getAutoSetupV(), .7));

                    RenderUtil.Rounded.roundedRect(stack, x + width - 9 + 70 * item.getSelectedV(), offsetYSelected + 2, 12, 6, ColorUtil.overCol(ColorUtil.getColor(25, 25, 25, 194), Theme.getInstance().clientColor(), item.getAutoSetupV()), Round.of(4));
                    RenderUtil.Rounded.roundedRect(stack, x + width - 9 + 70 * item.getSelectedV(), offsetYSelected + 3 + Fonts.SF_MEDIUM.getHeight("AutoSetup: ", 6), 12, 6, ColorUtil.overCol(ColorUtil.getColor(25, 25, 25, 194), Theme.getInstance().clientColor(), item.getAutoSellV()), Round.of(4));

                    RenderUtil.Rounded.roundedRect(stack, x + width - 7 + 70 * item.getSelectedV() + (item.getAutoSetupV() * 4), offsetYSelected + 3, 4, 4, ColorUtil.getColor(225, 225, 225), Round.of(3.5f));
                    RenderUtil.Rounded.roundedRect(stack, x + width - 7 + 70 * item.getSelectedV() + (item.getAutoSellV() * 4), offsetYSelected + 4 + Fonts.SF_MEDIUM.getHeight("AutoSetup: ", 6), 4, 4, ColorUtil.getColor(225, 225, 225), Round.of(3.5f));
                }

                offsetYSelected += item.getHeight() + 4;
            }
        }
        x += width + 5 + Math.max(filterV + 5, width());
        {
            RenderUtil.Rounded.roundedRect(stack, x, y, width, height, ColorUtil.getColor(15, 15, 15, 194), Round.of(5));
            Fonts.SF_MEDIUM.draw(stack, "Предметы", x + width / 2f - Fonts.SF_MEDIUM.getWidth("Предметы", 8) / 2f, y + 5, ColorUtil.getColor(225, 225, 225), 8);
            float offsetYItemsSelected = y + 16 + itemDeltaV;
            ScissorUtil.enableScissor(x, offsetYItemsSelected - itemDeltaV, width, height - 16);
            for (ItemBuy itemBuy : items) {
                if (inRange(offsetYItemsSelected, 16, y, height)) {
                    RectUtil.drawItemStack(itemBuy.getItemStack(), x + 4, offsetYItemsSelected, false, 16, 16);
                    Fonts.SF_MEDIUM.draw(stack, itemBuy.getName() != null ? itemBuy.getName() : "null", x + 22, offsetYItemsSelected + 8 - Fonts.SF_MEDIUM.getHeight(itemBuy.getName(), 6) / 2f, ColorUtil.getColor(225, 225, 225), 6);
                }
                offsetYItemsSelected += itemBuy.getHeight() + 4;
            }
            ScissorUtil.disableScissor();
        }
    }

    /**
     * Отображает элемент истории покупок
     */


    /**
     * Обновляет историю покупок
     */
    private void updateHistoryIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastHistoryUpdate > HISTORY_UPDATE_INTERVAL) {
            try {
                // История уже обновляется автоматически через HistoryManager
                // Просто обновляем время последней проверки
                lastHistoryUpdate = now;
            } catch (Exception e) {
                // Игнорируем ошибки обновления истории
            }
        }
    }

    public void click(float mouseX, float mouseY, int button, int action) {
        if (action == GLFW.GLFW_PRESS) {
            float x = mc.getMainWindow().getScaledWidth() / 2f - 218,
                    y = mc.getMainWindow().getScaledHeight() / 2f - 110,
                    width = 123,
                    height = 220;

            x += 315;
            { // SELECTED
                float offsetYSelected = y + 16 + selectedDeltaV;
                if (isHovered(mouseX, mouseY, x + width - Fonts.ICOMOON.getWidth("c", 8) - 4, y + 5, Fonts.ICOMOON.getWidth("c", 10), Fonts.ICOMOON.getHeight("c", 10))) {
                    filter = !filter;
                }
                for (SelectedItem selectedItem : selectedItems) {

                    if (isHovered(mouseX, mouseY, x, offsetYSelected, width, 16)) {
                        if (isHovered(mouseX, mouseY, x + width + 4.5f - (19 * selectedItem.getAnimV()), offsetYSelected + 3, 9, 10)) {
                            selectedItems.removeIf(item -> item.getName().equals(selectedItem.getName()));
                            return;
                        } else if (isHovered(mouseX, mouseY, x + width + 5.5f - (29 * selectedItem.getAnimV()), offsetYSelected + 3, 9, 10)) {
                            selectedItem.setSelected(!selectedItem.isSelected());
                        } else if (isHovered(mouseX, mouseY, x + 22, offsetYSelected + Fonts.SF_MEDIUM.getHeight(selectedItem.getName(), 6), width - 22, Fonts.SF_MEDIUM.getHeight("Покупка: ", 6))) {
                            selectedItem.setSelectedBuy(!selectedItem.isSelectedBuy());
                            selectedItem.setSelectedSell(false);
                        } else if (isHovered(mouseX, mouseY, x + 22, offsetYSelected + Fonts.SF_MEDIUM.getHeight(selectedItem.getName(), 6) + Fonts.SF_MEDIUM.getHeight("Покупка: ", 6), width - 22, Fonts.SF_MEDIUM.getHeight("Продажа: ", 6))) {
                            selectedItem.setSelectedSell(!selectedItem.isSelectedSell());
                            selectedItem.setSelectedBuy(false);
                        }
                    } else if (isHovered(mouseX, mouseY, x + width + 5, offsetYSelected + 2, 70 * selectedItem.getSelectedV(), 6))
                        selectedItem.setAutoSetup(!selectedItem.isAutoSetup());
                    else if (isHovered(mouseX, mouseY, x + width + 5, offsetYSelected + 3 + Fonts.SF_MEDIUM.getHeight("AutoSetup: ", 6), 70 * selectedItem.getSelectedV(), 6))
                        selectedItem.setAutoSell(!selectedItem.isAutoSell());

                    offsetYSelected += selectedItem.getHeight() + 4;
                }
                if (filter) {
                    if (isHovered(mouseX, mouseY, x + width + 5, y + 7 + Fonts.SF_MEDIUM.getHeight("Фильтры", 7), filterV, Fonts.SF_MEDIUM.getHeight("По названию A-Z", 7))) {
                        sortedAZ = !sortedAZ;
                        sortedZA = false;
                    } else if (isHovered(mouseX, mouseY, x + width + 5, y + 9 + Fonts.SF_MEDIUM.getHeight("Фильтры", 7) + Fonts.SF_MEDIUM.getHeight("По названию A-Z", 7), filterV, Fonts.SF_MEDIUM.getHeight("По названию A-Z", 7))) {
                        sortedZA = !sortedZA;
                        sortedAZ = false;
                    } else if (isHovered(mouseX, mouseY, x + width + 5, y + 11 + Fonts.SF_MEDIUM.getHeight("Фильтры", 7) + Fonts.SF_MEDIUM.getHeight("По названию A-Z", 7) * 2, filterV, Fonts.SF_MEDIUM.getHeight("Выключить с AutoSetup", 7)))
                        sortedSetup = !sortedSetup;
                    else if (isHovered(mouseX, mouseY, x + width + 5, y + 13 + Fonts.SF_MEDIUM.getHeight("Фильтры", 7) + Fonts.SF_MEDIUM.getHeight("По названию A-Z", 7) * 2 + Fonts.SF_MEDIUM.getHeight("Выключить с AutoSetup", 7), filterV, Fonts.SF_MEDIUM.getHeight("Выключить с авто продажей", 7)))
                        sortedSell = !sortedSell;
                }
            }
            x += width + 5 + Math.max(filterV, width());
            {
                y += 16 + itemDeltaV;
                for (ItemBuy itemBuy : items) {
                    if (isHovered(mouseX, mouseY, x, y, width, 16)) {
                        if (button == 0 && selectedItems.stream().filter(item -> item.getName().equals(itemBuy.getName())).toList().isEmpty())
                            selectedItems.add(new SelectedItem(itemBuy.getItemStack(), itemBuy.getName(), 10, 1000000, System.currentTimeMillis()));
                        else if (button == 1 && !selectedItems.stream().filter(item -> item.getName().equals(itemBuy.getName())).toList().isEmpty())
                            selectedItems.removeIf(item -> item.getName().equals(itemBuy.getName()));
                        else if (button == 2) {
                            mc.player.sendChatMessage("/ah search " + itemBuy.getName());
                            new Thread(() -> {
                                try {
                                    Thread.sleep(400L);
                                    if (mc.currentScreen != null && mc.currentScreen.getTitle().getString().contains("Аукцион")) {
                                        int minPrice = Integer.MAX_VALUE;
                                        int slots = mc.player.openContainer.inventorySlots.size();
                                        for (int j = 0; j < slots; j++) {
                                            ItemStack stack = mc.player.openContainer.getSlot(j).getStack();
                                            if (!stack.isEmpty()) {
                                                java.util.List<net.minecraft.util.text.ITextComponent> tooltip = stack.getTooltip(mc.player, net.minecraft.client.util.ITooltipFlag.TooltipFlags.NORMAL);
                                                for (net.minecraft.util.text.ITextComponent line : tooltip) {
                                                    if (line.getString().contains("Цена за 1 ед.:")) {
                                                        String priceStr = line.getString().split("Цена за 1 ед.:")[1].trim();
                                                        priceStr = priceStr.replace("¤", "").replace(" ", "").split("\\.")[0];
                                                        try {
                                                            int unitPrice = Integer.parseInt(priceStr);
                                                            if (unitPrice < minPrice) {
                                                                minPrice = unitPrice;
                                                            }
                                                        } catch (Exception ignore) {
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if (minPrice != Integer.MAX_VALUE) {
                                            boolean found = false;
                                            for (SelectedItem selectedItem : selectedItems) {
                                                if (stripColorCodes(selectedItem.getName()).equalsIgnoreCase(stripColorCodes(itemBuy.getName()))) {
                                                    int newPrice = (int) (minPrice * 0.65);
                                                    selectedItem.setPrice(newPrice);
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if (!found && minPrice > 0) {
                                                selectedItems.add(new SelectedItem(itemBuy.getItemStack(), itemBuy.getName(), (int) (minPrice * 0.65), 1000000, System.currentTimeMillis()));
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    y += itemBuy.getHeight() + 4;
                }
            }
        }
    }

    public void key(int key) {
        for (SelectedItem selectedItem : selectedItems.stream().filter(selectedItem -> selectedItem.isSelectedSell() || selectedItem.isSelectedBuy()).toList()) {
            if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER) {
                if (selectedItem.isSelectedBuy())
                    selectedItem.setSelectedBuy(false);
                else
                    selectedItem.setSelectedSell(false);
            } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                if (selectedItem.isSelectedBuy())
                    selectedItem.setPrice(Integer.parseInt(String.valueOf(selectedItem.getPrice()).length() == 1 ? "0" : String.valueOf(selectedItem.getPrice()).substring(0, String.valueOf(selectedItem.getPrice()).length() - 1)));
                else
                    selectedItem.setSellPrice(Integer.parseInt(String.valueOf(selectedItem.getSellPrice()).length() == 1 ? "0" : String.valueOf(selectedItem.getSellPrice()).substring(0, String.valueOf(selectedItem.getSellPrice()).length() - 1)));
            }
        }
    }

    public void charTyped(char typed) {
        if (!Character.isDigit(typed)) return;
        for (SelectedItem selectedItem : selectedItems.stream().filter(selectedItem -> selectedItem.isSelectedBuy() || selectedItem.isSelectedSell()).toList()) {
            if (selectedItem.isSelectedSell())
                selectedItem.setSellPrice(Math.min(Integer.parseInt(String.valueOf(selectedItem.getSellPrice()) + typed), Integer.MAX_VALUE));
            else
                selectedItem.setPrice(Math.min(Integer.parseInt(String.valueOf(selectedItem.getPrice()) + typed), Integer.MAX_VALUE));
        }
    }

    public void scroll(float mouseX, float mouseY, float delta) {
        float x = mc.getMainWindow().getScaledWidth() / 2f - 218,
                y = mc.getMainWindow().getScaledHeight() / 2f - 110,
                width = 123,
                height = 220;
        { // HISTORY
            if (isHovered(mouseX, mouseY, x, y, width, height))
                historyDelta += delta * 20;
        }

        x += 315;
        { // SELECTED
            if (isHovered(mouseX, mouseY, x, y, width, height))
                selectedDelta += delta * 20;
        }
        x += width + 5 + Math.max(filterV, width());
        { // ITEMS
            if (isHovered(mouseX, mouseY, x, y + 16, width, height - 16))
                itemDelta += delta * 20;
        }
    }

    private float width() {
        if (selectedItems.stream().filter(item -> item.getSelectedV() > 0).toList().isEmpty()) return 0;
        float width = 0;
        for (SelectedItem item : selectedItems.stream().filter(item -> item.getSelectedV() > 0).toList()) {
            width = Math.max(width, item.getSelectedV() * 75);
        }
        return width;
    }

    private boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return (mouseX >= x && mouseX <= x + width) && (mouseY >= y && mouseY <= y + height);
    }

    private boolean inRange(float y, float height, float rangeY, float rangeHeight) {
        return (y + height >= rangeY) && (y <= rangeY + rangeHeight);
    }

    /**
     * Возвращает список выбранных предметов для интеграции с AutoBuy
     */
    public ArrayList<SelectedItem> getSelectedItems() {
        return selectedItems;
    }

    /**
     * Проверяет, должен ли автобай купить предмет
     *
     * @param itemStack предмет
     * @param price     цена предмета
     * @param itemName  название предмета
     * @return true если нужно покупать
     */
    public boolean shouldAutoBuy(ItemStack itemStack, int price, String itemName) {
        if (itemStack == null || itemName == null) return false;

        for (SelectedItem selectedItem : selectedItems) {
            // Проверяем цену - покупаем только если цена меньше или равна установленной в GUI
            if (price > selectedItem.getPrice()) continue;

            // Сравниваем названия (убираем цветовые коды)
            String guiName = stripColorCodes(selectedItem.getName()).toLowerCase().trim();
            String stackName = stripColorCodes(itemName).toLowerCase().trim();
            String stackDisplayName = stripColorCodes(itemStack.getDisplayName().getString()).toLowerCase().trim();

            // Проверяем совпадение по названию
            boolean nameMatch = guiName.equals(stackName) ||
                    guiName.equals(stackDisplayName) ||
                    stackName.contains(guiName) ||
                    stackDisplayName.contains(guiName);

            if (nameMatch) {
                return true;
            }
        }

        return false;
    }

    private int getPriceMinimal(ItemStack itemStack) {
        CompoundNBT tag = itemStack.getTag();
        if (tag == null) return -1;
        String price = org.apache.commons.lang3.StringUtils.substringBetween(tag.toString(), "Цена за 1 ед.:\"},{\"italic\":false,\"color\":\"#00d8ff\",\"extra\":[{\"text\":\"", "\"},{\"text\":\"¤\"");
        if (price == null || price.isEmpty()) return -1;
        try {
            price = price.replaceAll(" ", "").replaceAll(",", "");
            return Integer.parseInt(price);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String stripColorCodes(String input) {
        if (input == null) return "";
        return input.replaceAll("§[0-9a-fklmnor]", "").trim();
    }
}