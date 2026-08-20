package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.ItemStack;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.annotations.PVE;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.StringSetting;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

@Category3
@PVE
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "InvSave", category = Category.PLAYER, description = "Сохраняет инвентарь при смерти через телепортацию")
public class InvSave extends Module {

    public static InvSave getInstance() {
        return Instance.get(InvSave.class);
    }

    private final ModeSetting accountRole = new ModeSetting(this, "Роль", "Получатель", "Отправитель");
    private final StringSetting targetNick = new StringSetting(this, "Ник цели", "")
            .setVisible(() -> accountRole.is("Отправитель"));
    private final ModeSetting leaveMethod = new ModeSetting(this, "Способ выхода", "/hub", "/home home");

    private final StopWatch tpaTimer = new StopWatch();

    private boolean tpaSent = false;
    private float previousHealth = 20.0f;
    private int previousInventoryCount = 0;
    private boolean waitingForItems = false;
    private boolean deathHandled = false;

    @Override
    public void onEnable() {
        super.onEnable();
        tpaTimer.reset();
        tpaSent = false;
        deathHandled = false;
        waitingForItems = false;

        if (mc.player != null) {
            previousHealth = mc.player.getHealth();
            previousInventoryCount = countInventoryItems();

            if (accountRole.is("Отправитель")) {
                String nick = targetNick.getValue();
                if (nick != null && !nick.isEmpty()) {
                    mc.player.sendChatMessage("/tpa " + nick);
                    tpaSent = true;
                    waitingForItems = true;
                    tpaTimer.reset();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        tpaSent = false;
        deathHandled = false;
        waitingForItems = false;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (accountRole.is("Получатель")) {
            handleReceiverRole();
        } else {
            handleSenderRole();
        }
    }

    private void handleReceiverRole() {
        float currentHealth = mc.player.getHealth();
        boolean isDead = currentHealth <= 0 || mc.player.deathTime > 0;
        boolean wasDead = previousHealth <= 0;

        if (isDead && !wasDead && !deathHandled) {
            mc.player.sendChatMessage("/tpaccept");
            String leaveCmd = leaveMethod.is("/hub") ? "/hub" : "/home home";
            mc.player.sendChatMessage(leaveCmd);
            deathHandled = true;
        }

        if (currentHealth > 0 && mc.player.deathTime == 0) {
            deathHandled = false;
        }

        previousHealth = currentHealth;
    }

    private void handleSenderRole() {
        String nick = targetNick.getValue();
        if (nick == null || nick.isEmpty()) {
            return;
        }

        if (tpaTimer.finished(15000)) {
            mc.player.sendChatMessage("/tpa " + nick);
            tpaTimer.reset();
        }

        if (waitingForItems) {
            int currentCount = countInventoryItems();
            if (currentCount > previousInventoryCount) {
                String leaveCmd = leaveMethod.is("/hub") ? "/hub" : "/home home";
                mc.player.sendChatMessage(leaveCmd);
                waitingForItems = false;
                toggle();
            }
        }
    }

    private int countInventoryItems() {
        if (mc.player == null) return 0;

        int count = 0;
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                count += stack.getCount();
            }
        }
        return count;
    }
}