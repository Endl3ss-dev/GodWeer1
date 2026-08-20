package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.AirItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;

@Category3
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ElytraFix", description = "Делает так чтобы не выкинуть случайно нагрудник/элитру", category = Category.MISC)
public class ElytraFix extends Module {
    long delay;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (!(mc.currentScreen instanceof ContainerScreen)) {
            ItemStack stack = mc.player.inventory.getItemStack();
            if (stack.getItem() == Items.ELYTRA) {
                int nule = findNullSlot();
                mc.playerController.windowClick(0, nule, 1, ClickType.PICKUP, mc.player);
            }

            if (stack.getItem() == Items.NETHERITE_CHESTPLATE || stack.getItem() == Items.DIAMOND_CHESTPLATE) {
                mc.playerController.windowClick(0, 6, 1, ClickType.PICKUP, mc.player);
            }
        }
    }

    public static int findNullSlot() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof AirItem) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }

        return 999;
    }
}