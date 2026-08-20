package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.util.Hand;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.StringSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoJoin", category = Category.MISC, description = "Ну кароче крутит joint ой... авто заходит на заполненый сервер")
public class AutoJoin extends Module {
    public static AutoJoin getInstance() {
        return Instance.get(AutoJoin.class);
    }

    private final StopWatch timerSystem = new StopWatch();

    private final ModeSetting mode = new ModeSetting(this, "Режим", "ReallyWorld/FunTime",
            "Spooky Дуэли").set("Spooky Дуэли");

    private final StringSetting grief = new StringSetting(this, "Номер гриф/анархии", "105")
            .setVisible(() -> mode.is("ReallyWorld/FunTime"));

    @Override
    public void onEnable() {
        super.onEnable();
        timerSystem.reset();

        if (mode.is("ReallyWorld/FunTime") && isReallyWorldServer()) {
            joinerItem();
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        joinerLogic();
        checkGriefContainer();
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (event.isReceive()) {
            joinSuccessful(event);
        }
    }

    @EventHandler
    public void onWorldChange(WorldChangeEvent event) {
        if (!mode.is("Spooky Дуэли")) {
            this.toggle();
        }
    }

    private void joinSuccessful(PacketEvent event) {
        if (mode.is("Spooky Дуэли") && isSpookyServer()) {
            handleSpookyJoin(event);
        } else if (mode.is("ReallyWorld/FunTime") && isReallyWorldServer()) {
            handleReallyWorldJoin(event);
        } else if (mode.is("ReallyWorld/FunTime") && isFuntimeServer()) {
            handleFuntimeJoin(event);
        }
    }

    private void handleSpookyJoin(PacketEvent event) {
        if (event.getPacket() instanceof SChatPacket) {
            joinerItem();
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        } else if (event.getPacket() instanceof SOpenWindowPacket packet) {
            if (packet.getTitle().getString().contains("Выберите режим")) {
                mc.player.connection.sendPacket(new CClickWindowPacket(packet.getWindowId(),
                        14, 0, ClickType.PICKUP,
                        mc.player.openContainer.getSlot(14).getStack(),
                        mc.player.openContainer.getNextTransactionID(mc.player.inventory)));
                event.cancel();
            }
        }
    }

    private void handleReallyWorldJoin(PacketEvent event) {
        if (event.getPacket() instanceof SJoinGamePacket) {
            ChatUtil.addText("Вход на " + this.grief.getValue() + " гриф успешен");
            this.toggle();
        } else if (event.getPacket() instanceof SChatPacket) {
            joinerItem();
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        } else if (event.getPacket() instanceof SOpenWindowPacket packet) {
            if (packet.getTitle().getString().contains("Выбор сервера")) {
                mc.player.connection.sendPacket(new CClickWindowPacket(packet.getWindowId(),
                        21, 0, ClickType.PICKUP,
                        mc.player.openContainer.getSlot(21).getStack(),
                        mc.player.openContainer.getNextTransactionID(mc.player.inventory)));
                event.cancel();
            }
        }
    }

    private void handleFuntimeJoin(PacketEvent event) {
        if (event.getPacket() instanceof SJoinGamePacket) {
            ChatUtil.addText("Вход на " + this.grief.getValue() + " анархию успешен");
            this.toggle();
        } else if (event.getPacket() instanceof SChatPacket chatPacket) {
            String message = chatPacket.getChatComponent().getString()
                    .replaceAll("§[0-9a-fklmnor]", "");
            if (message.contains("Вы уже подключены")) {
                this.toggle();
            }
        }
    }


    private void joinerLogic() {
        if (mode.is("ReallyWorld/FunTime") && isFuntimeServer()) {
            mc.player.sendChatMessage("/an" + grief.getValue());
        } else if (mode.is("Spooky Дуэли") && isSpookyServer()) {
            joinerItem();
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));

            // Проверяем успешность входа по наличию алмазного меча
            if (findItem(Items.DIAMOND_SWORD) != -1) {
                ChatUtil.addText("Вход успешен");
                this.toggle();
            }
        }
    }

    private void joinerItem() {
        int slot = findItem(Items.COMPASS);
        if (slot == -1) return;
        mc.player.inventory.currentItem = slot;
        mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
    }

    private void checkGriefContainer() {
        if (mc.player.openContainer == null) return;

        for (int slot = 0; slot < mc.player.openContainer.inventorySlots.size(); slot++) {
            Slot containerSlot = mc.player.openContainer.getSlot(slot);
            if (containerSlot != null && containerSlot.getHasStack()) {
                ItemStack stack = containerSlot.getStack();
                String itemName = stack.getDisplayName().getString();

                if (itemName.contains("ГРИФ #" + grief.getValue() + " (1.16.5+)")) {
                    mc.player.connection.sendPacket(new CClickWindowPacket(mc.player.openContainer.windowId,
                            slot, 0, ClickType.PICKUP,
                            stack,
                            mc.player.openContainer.getNextTransactionID(mc.player.inventory)));

                    mc.player.connection.sendPacket(new CCloseWindowPacket(mc.player.openContainer.windowId));
                }
            }
        }
    }

    private int findItem(net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    // Методы для проверки серверов по IP
    private boolean isReallyWorldServer() {
        if (mc.getCurrentServerData() == null) return false;
        String serverIP = mc.getCurrentServerData().serverIP.toLowerCase();
        return serverIP.contains("reallyworld") || serverIP.contains("rw");
    }

    private boolean isFuntimeServer() {
        if (mc.getCurrentServerData() == null) return false;
        String serverIP = mc.getCurrentServerData().serverIP.toLowerCase();
        return serverIP.contains("funtime") || serverIP.contains("ft");
    }

    private boolean isSpookyServer() {
        if (mc.getCurrentServerData() == null) return false;
        String serverIP = mc.getCurrentServerData().serverIP.toLowerCase();
        return serverIP.contains("spooky");
    }
}