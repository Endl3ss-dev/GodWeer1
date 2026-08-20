package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.ITextComponent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.HolyWorld;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.autobuy.AutoBuyItem;
import org.privates.client.utils.autobuy.AutoBuyManager;
import org.privates.client.utils.autobuy.CustomAutoBuyItem;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.text.TextBox;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Category1
@HolyWorld
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoSetupHW", category = Category.MISC, description = "Авто ставка самых дешевых цен")
public class AutoSetup extends Module {

    public static AutoSetup getInstance() {
        return Instance.get(AutoSetup.class);
    }

    private final SliderSetting delaySlider = new SliderSetting(this, "Задержка поиска", 1500.0F, 1000.0F, 5000.0F, 100.0F);
    private final SliderSetting percentageSlider = new SliderSetting(this, "Скидка %", 30.0F, 0.0F, 100.0F, 1.0F);
    private final SliderSetting defaultPriceSlider = new SliderSetting(this, "Цена по умолчанию", 30000000.0F, 1000000.0F, 99000000.0F, 1000000.0F);

    private final StopWatch timer = new StopWatch();
    private int id = 0;
    private List<AutoBuyItem> selectedItems;
    private boolean waitingForResult = false;

    @Override
    protected void onEnable() {
        super.onEnable();

        this.id = 0;
        this.timer.reset();
        this.waitingForResult = false;

        this.selectedItems = new ArrayList<>();
        for (Map.Entry<AutoBuyItem, Boolean> entry : AutoBuyHWGui.itemEnabled.entrySet()) {
            if (entry.getValue()) {
                this.selectedItems.add(entry.getKey());
            }
        }

        if (this.selectedItems.isEmpty()) {
            ChatUtil.addTextWithError("Нет предметов для настройки!");
            this.setEnabled(false);
            return;
        }

        for (AutoBuyItem item : this.selectedItems) {
            TextBox priceField = AutoBuyHWGui.priceFields.get(item);
            if (priceField != null) {
                priceField.setText("0");
            }
            item.setPrice(0);
        }

        ChatUtil.addText("§4[AutoSetupHW] §fНачинаем настройку " + this.selectedItems.size() + " предметов");
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        AutoBuyManager.save();
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (!event.isReceive()) return;

        if (event.getPacket() instanceof SChatPacket chatPacket) {
            String message = chatPacket.getChatComponent().getString();

            if (message.contains("▶ По вашему запросу не было найдено ни одного фильтра.")) {
                ChatUtil.addTextWithError("AutoSetupHW отключен: фильтры не найдены");
            } else if (message.contains("▶ Фильтров под это название не найдено. Вы можете заполнить форму на добавление нового ключевого слова") && this.id < this.selectedItems.size()) {
                AutoBuyItem currentItem = this.selectedItems.get(this.id);
                ChatUtil.addText(String.format("§eПредмет \"%s\" не найден", getSearchName(currentItem)));
                setupItemWithDefaultPrice(currentItem);
                this.id++;

                if (this.id >= this.selectedItems.size()) {
                } else {
                    this.timer.reset();
                }
            }
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (this.timer.finished((double) this.delaySlider.getValue().longValue())) {
            if (this.selectedItems != null && this.id < this.selectedItems.size()) {
                AutoBuyItem currentItem = this.selectedItems.get(this.id);
                String searchName = getSearchName(currentItem);

                if (!this.waitingForResult) {
                    String itemName = currentItem.getName();
                    ChatUtil.addText("§4Поиск предмета: §f" + itemName + (searchName.equals(itemName) ? "" : " §7(Поисковое имя: §f" + searchName + "§7)"));
                }

                if (searchName != null && !searchName.trim().isEmpty()) {
                    if (searchName.equalsIgnoreCase("kk")) {
                        int price = 30000000;
                        setupItemWithPrice(currentItem, price);
                        ChatUtil.addText("§cНастроен предмет: §6" + currentItem.getName() + "§2 за §b" + price + "§c ¤ (kk)");
                        this.id++;
                        this.waitingForResult = false;
                        this.timer.reset();
                    } else if (!this.waitingForResult) {
                        mc.player.sendChatMessage("/ah search " + searchName);
                        this.waitingForResult = true;
                        this.timer.reset();
                    } else {
                        Screen screen = mc.currentScreen;
                        if (screen instanceof ContainerScreen<?> containerScreen) {
                            int minPrice = Integer.MAX_VALUE;

                            for (int i = 0; i < containerScreen.getContainer().inventorySlots.size(); i++) {
                                ItemStack stack = containerScreen.getContainer().getSlot(i).getStack();
                                if (stack != null && !stack.isEmpty()) {
                                    int price = getPriceMinimal(stack);
                                    if (price > 0 && price < minPrice) {
                                        minPrice = price;
                                    }
                                }
                            }

                            if (minPrice == Integer.MAX_VALUE) {
                                setupItemWithDefaultPrice(currentItem);
                            } else {
                                int discountedPrice = calculateDiscountedPrice(minPrice);
                                setupItemWithPrice(currentItem, discountedPrice);
                            }

                            this.id++;
                            this.waitingForResult = false;
                            this.timer.reset();
                        }
                    }
                } else {
                    ChatUtil.addText("§4Пропускаем предмет с пустым именем: §6" + currentItem.getName());
                    this.id++;
                    this.waitingForResult = false;
                    this.timer.reset();
                }
            } else {
                ChatUtil.addText("§cНастройка завершена!");
                this.setEnabled(false);
            }
        }
    }

    private String getSearchName(AutoBuyItem item) {
        if (item instanceof CustomAutoBuyItem customItem) {
            if (customItem.ahName() != null && !customItem.ahName().trim().isEmpty()) {
                return customItem.ahName();
            }
        }
        return item.getName();
    }

    private void setupItemWithDefaultPrice(AutoBuyItem item) {
        int price = this.defaultPriceSlider.getValue().intValue();
        setupItemWithPrice(item, price);
        ChatUtil.addText("§cНастроен предмет: §2" + item.getName() + "§c за §4" + price + "§c ¤ (по умолчанию)");
    }

    private void setupItemWithPrice(AutoBuyItem item, int price) {
        item.setPrice(price);

        TextBox priceField = AutoBuyHWGui.priceFields.get(item);
        if (priceField != null) {
            priceField.setText(String.valueOf(price));
        }

        ChatUtil.addText("§cНастроен предмет: §4" + item.getName() + "§c за §4" + price + "§8 ¤");
    }

    private int calculateDiscountedPrice(int originalPrice) {
        int discountPercent = this.percentageSlider.getValue().intValue();
        double multiplier = 1.0F - (double) discountPercent / 100.0F;
        int discountedPrice = (int) ((double) originalPrice * multiplier);
        return Math.min(discountedPrice, 99999999);
    }

    private int getPriceMinimal(ItemStack stack) {
        try {
            for (ITextComponent component : stack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.NORMAL)) {
                String text = component.getString();
                if (text.contains("Цена за 1 ед.:")) {
                    String priceStr = text.split("Цена за 1 ед.:", 2)[1]
                            .replace("¤", "")
                            .replace(" ", "")
                            .split("\\.")[0]
                            .trim();
                    return Integer.parseInt(priceStr);
                }
            }
        } catch (Exception ignored) {
        }
        return 99999999;
    }
}