package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.ITextComponent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;
import org.privates.lib.util.time.StopWatch;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoSellExp", category = Category.MISC, description = "Тест функция для создание новых")
public class AutoSellExp extends Module {

    public static AutoSellExp getInstance() {
        return Instance.get(AutoSellExp.class);
    }

    private final SliderSetting price = new SliderSetting(this, "Цена (тыс)", 100, 100, 400, 10);
    private final SliderSetting delay = new SliderSetting(this, "Задержка (мс)", 400, 100, 2000, 50);
    private final StopWatch sellTimer = new StopWatch();

    private boolean waitingConfirm = false;
    private boolean paused = false;

    @Override
    public void onEnable() {
        super.onEnable();
        waitingConfirm = false;
        paused = false;
        sellTimer.reset();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (paused) return;

        if (waitingConfirm) {
            if (sellTimer.finished(delay.getValue().longValue())) {
                int priceValue = price.getValue().intValue() * 1000;
                mc.player.sendChatMessage("/ah sell " + priceValue);
                waitingConfirm = false;
                sellTimer.reset();
            }
            return;
        }

        if (!sellTimer.finished(2500)) return;

        for (Slot slot : InvUtil.getMainInventorySlots()) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;

            if (stack.getItem() == Items.EXPERIENCE_BOTTLE) {
                InvUtil.swapHand(slot, net.minecraft.util.Hand.MAIN_HAND, true);
                waitingConfirm = true;
                sellTimer.reset();
                return;
            }
        }

        ChatUtil.addText("§c[AutoSellExp] Бутылочки опыта не найдены!");
        toggle();
    }

    @EventHandler
    public void onChat(PacketEvent event) {
        if (!(event.getPacket() instanceof SChatPacket)) return;
        ITextComponent comp = ((SChatPacket) event.getPacket()).getChatComponent();
        String msg = comp.getString();

        if (msg.contains("Освободите хранилище")) {
            paused = true;
            ChatUtil.addText("§e[AutoSellExp] Хранилище заполнено! Ожидание покупки...");
        }

        if (msg.contains("У Вас купили")) {
            if (paused) {
                paused = false;
                ChatUtil.addText("§a[AutoSellExp] Продолжаем работу!");
                sellTimer.reset();
            }
        }
    }
}