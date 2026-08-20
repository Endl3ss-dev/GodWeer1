package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.inventory.container.Slot;
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
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.screen.autobuy.AutoBuyUI;
import org.privates.client.screen.autobuy.history.SelectedItem;
import org.privates.client.utils.autobuy.AutoBuyManager;
import org.privates.client.utils.autobuy.CustomAutoBuyItem;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Category1
@HolyWorld
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoSell", category = Category.MISC)
public class AutoSell extends Module {
    public static AutoSell getInstance() {
        return Instance.get(AutoSell.class);
    }

    private final StopWatch sellTimer = new StopWatch();
    private boolean waitingConfirm = false;
    private int lastSlot = -1;
    private CustomAutoBuyItem lastSoldItem = null;
    private final SliderSetting percent = new SliderSetting(this, "Процент", 30f, 1f, 99f, 1f);
    private final BooleanSetting autoSet = new BooleanSetting(this, "AuctionKiller", true);
    private final Pattern pricePattern = Pattern.compile("за 1 шт\\.: ([\\d ]+)¤");

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null || !isEnabled()) return;
        if (waitingConfirm) {
            if (sellTimer.finished(400)) {
                mc.player.sendChatMessage("/ah sell auto confirm");
                waitingConfirm = false;
                sellTimer.reset();
            }
            return;
        }
        if (!sellTimer.finished(6000)) return;
        for (Slot slot : InvUtil.getMainInventorySlots()) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;
            for (CustomAutoBuyItem item : AutoBuyManager.getCustomAutoBuyItemList()) {
                if (stack.getItem().equals(item.getItem())) {
                    // Запоминаем какой предмет продаем
                    lastSoldItem = item;
                    InvUtil.swapHand(slot, net.minecraft.util.Hand.MAIN_HAND, true);
                    mc.player.sendChatMessage("/ah sell auto");
                    waitingConfirm = true;
                    lastSlot = slot.slotNumber;
                    sellTimer.reset();
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onChat(PacketEvent event) {
        if (!(event.getPacket() instanceof SChatPacket)) return;
        ITextComponent comp = ((SChatPacket) event.getPacket()).getChatComponent();
        String msg = comp.getString();

        // Обрабатываем сообщение с ценой
        if (msg.contains("за 1 шт.:") && autoSet.getValue() && lastSoldItem != null) {
            Matcher matcher = pricePattern.matcher(msg);
            if (matcher.find()) {
                String priceStr = matcher.group(1).replace(" ", "");
                try {
                    int price = Integer.parseInt(priceStr);
                    int newPrice = (int) Math.round(price * (1.0 - percent.getValue() / 100.0));

                    AutoBuyUI autoBuyUI = AutoBuyUI.getInstance();
                    if (autoBuyUI != null) {
                        boolean anySet = false;

                        for (SelectedItem selectedItem : autoBuyUI.getSelectedItems()) {
                            if (selectedItem.getItemStack() != null &&
                                    selectedItem.getName().equalsIgnoreCase(lastSoldItem.name)) {
                                selectedItem.setPrice(newPrice); // Устанавливаем цену покупки
                                ChatUtil.addText("[AhKiller] Установлена цена покупки для: " + selectedItem.getName() +
                                        " - " + newPrice + "¤ (" + percent.getValue() + "% скидка)");
                                anySet = true;
                                break;
                            }
                        }

                        if (!anySet) {
                            // Если предмет не найден, создаем новую запись
                            SelectedItem newItem = new SelectedItem(
                                    lastSoldItem.getItem().getDefaultInstance(),
                                    lastSoldItem.name,
                                    newPrice,
                                    0,
                                    System.currentTimeMillis()
                            );
                            autoBuyUI.getSelectedItems().add(newItem);
                            ChatUtil.addText("[AhKiller] Добавлен новый предмет: " + lastSoldItem.name +
                                    " с ценой " + newPrice + "¤ (" + percent.getValue() + "% скидка)");
                        }
                    }
                    lastSoldItem = null; // Сбрасываем после обработки
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }
}