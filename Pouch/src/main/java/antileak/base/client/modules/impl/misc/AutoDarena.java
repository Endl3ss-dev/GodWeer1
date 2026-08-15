package antileak.base.client.modules.impl.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import antileak.base.Punch;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.api.utils.math.TimerUtils;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.FloatSetting;

public class AutoDarena extends Module {

    public static AutoDarena INSTANCE = new AutoDarena();

    private final TimerUtils timerUtil = new TimerUtils();
    private final BooleanSetting checkPlayers = new BooleanSetting("Проверять игроков", true);
    private final FloatSetting checkRadius = new FloatSetting("Радиус проверки", 10.0f, 5.0f, 50.0f, 1.0f);
    private boolean hasMessageSent = false;

    public AutoDarena() {
        super("AutoDarena", "Автоматически заходит на арену", ModuleCategory.MISC);
        addSettings(checkPlayers, checkRadius);
    }

    @Override
    public void onEnable() {
        hasMessageSent = false;
        timerUtil.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        hasMessageSent = false;
        super.onDisable();
    }

    @EventLink
    public void onUpdate(EventUpdate e) {
        if (mc.player == null || mc.world == null) return;

        // Проверяем, не в PVP ли игрок, и отправляем команду только один раз
        if (!isPlayerInPvP() && !hasMessageSent) {
            mc.player.networkHandler.sendChatCommand("darena");
            hasMessageSent = true;
        }

        // Если открыт контейнер (сундук), ищем иглобрюха и кликаем на него
        ScreenHandler container = mc.player.currentScreenHandler;
        if (container != null && container != mc.player.playerScreenHandler) {
            if (container instanceof GenericContainerScreenHandler) {
                if (timerUtil.finished(250)) {
                    for (Slot slot : container.slots) {
                        ItemStack itemStack = slot.getStack();
                        if (!itemStack.isEmpty() && itemStack.getItem().toString().contains("pufferfish")) {
                            if (mc.interactionManager != null) {
                                mc.interactionManager.clickSlot(
                                    container.syncId,
                                    slot.id,
                                    0,
                                    SlotActionType.QUICK_MOVE,
                                    mc.player
                                );
                            }
                            break;
                        }
                    }
                    timerUtil.reset();
                }
            }
        }
    }

    /**
     * Проверяет, находится ли игрок в PVP-режиме
     * Проверяет наличие других игроков поблизости (исключая друзей)
     */
    private boolean isPlayerInPvP() {
        if (!checkPlayers.isState()) {
            return false;
        }

        if (mc.player == null || mc.world == null) {
            return false;
        }

        double radius = checkRadius.get();

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (player.isSpectator() || player.isCreative()) continue;

            // Проверяем, не является ли игрок другом
            if (Punch.INSTANCE.friendStorage != null && Punch.INSTANCE.friendStorage.isFriend(player.getName().getString())) {
                continue;
            }

            // Проверяем расстояние до игрока
            double distance = mc.player.squaredDistanceTo(player);
            if (distance <= radius * radius) {
                return true;
            }
        }

        return false;
    }
}
