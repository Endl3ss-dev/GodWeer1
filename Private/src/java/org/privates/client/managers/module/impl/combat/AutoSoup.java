package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

@Category3

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoSoup", category = Category.COMBAT, description = "Авто хавает супчик на здоровье")
public class AutoSoup extends Module {
    public static AutoSoup getInstance() {
        return Instance.get(AutoSoup.class);
    }

    private final StopWatch eatTimer = new StopWatch();
    private final SliderSetting health = new SliderSetting(this, "Здоровье", 19, 1, 20, 0.5f);

    @EventHandler

    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= health.getValue() &&
                eatTimer.finished(1200L)) {

            int soupSlot = findSoup();

            if (soupSlot != -1) {
                if (soupSlot == 40) {
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
                } else if (soupSlot >= 36) {
                    int hotbarSlot = soupSlot - 36;
                    int currentSlot = mc.player.inventory.currentItem;

                    mc.player.connection.sendPacket(new CHeldItemChangePacket(hotbarSlot));
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                            CPlayerDiggingPacket.Action.DROP_ITEM,
                            BlockPos.ZERO,
                            Direction.DOWN
                    ));
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(currentSlot));
                    mc.player.connection.sendPacket(new CCloseWindowPacket(mc.player.openContainer.windowId));

                    eatTimer.reset();
                } else {
                    mc.playerController.windowClick(0, soupSlot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, 44, 0, ClickType.PICKUP, mc.player);
                }
            }
        }
    }

    private int findSoup() {
        if (mc.player.getHeldItemOffhand().getItem() == Items.MUSHROOM_STEW) {
            return 40;
        }

        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.MUSHROOM_STEW) {
                return i + 36;
            }
        }

        for (int i = 9; i < 36; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.MUSHROOM_STEW) {
                return i;
            }
        }

        return -1;
    }
}