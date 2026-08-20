package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Rage;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;

@Category1
@Rage
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ElytraTweaks", category = Category.MOVEMENT, description = "Флай элитра на нагруднике")
public class ElytraTweaks extends Module {

    private final ModeSetting mode = new ModeSetting(this, "Mode", "New", "ReallyWorld", "Old");
    private final SliderSetting cooldown = new SliderSetting(this, "Cooldown", 500f, 300f, 2000f, 50f);
    private final SliderSetting cooldownOnElytra = new SliderSetting(this, "Cooldown On Elytra", 150f, 50f, 500f, 25f).setVisible(() -> mode.is("New"));

    private int oldSlot = -1;
    private long time = 0L;

    @EventHandler
    public void update(UpdateEvent event) {
        if (mode.is("New")) {
            int i;
            int elytraSlot = findItem(Items.ELYTRA);
            int fireworkSlot = findItem(Items.FIREWORK_ROCKET);

            if (elytraSlot >= 0 && time <= System.currentTimeMillis() && !mc.player.isOnGround()) {
                if (isInHotbar(elytraSlot)) {
                    mc.playerController.windowClick(0, 6, elytraSlot % 9, ClickType.SWAP, mc.player);
                } else {
                    for (i = 0; i < 36; ++i) {
                        if (mc.player.inventory.getStackInSlot(i).getItem() != Items.ELYTRA) continue;
                        mc.playerController.windowClick(0, i, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                        mc.playerController.windowClick(0, 6, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                        mc.playerController.windowClick(0, i, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                        this.oldSlot = i;
                        break;
                    }
                }

                mc.player.startFallFlying();
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));

                if (isInHotbar(Items.FIREWORK_ROCKET)) {
                    if (mc.player.isHandActive()) {
                        mc.playerController.windowClick(0, fireworkSlot, 40, ClickType.SWAP, mc.player);
                        mc.playerController.processRightClick(mc.player, mc.world, Hand.OFF_HAND);
                        mc.playerController.windowClick(0, fireworkSlot, 40, ClickType.SWAP, mc.player);
                    } else {
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(fireworkSlot % 9));
                        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                    }
                } else {
                    for (i = 0; i < 36; ++i) {
                        if (mc.player.inventory.getStackInSlot(i).getItem() != Items.FIREWORK_ROCKET) continue;
                        if (mc.player.isHandActive()) {
                            mc.playerController.windowClick(0, i, 40, ClickType.SWAP, mc.player);
                            mc.playerController.processRightClick(mc.player, mc.world, Hand.OFF_HAND);
                            mc.playerController.windowClick(0, i, 40, ClickType.SWAP, mc.player);
                            break;
                        }
                        mc.playerController.windowClick(0, i, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem % 8 + 1));
                        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                        mc.playerController.windowClick(0, i, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                        break;
                    }
                }

                if (this.oldSlot == -1) {
                    this.oldSlot = elytraSlot;
                }
                this.time = System.currentTimeMillis() + this.cooldownOnElytra.getValue().longValue();
            }

            if (time <= System.currentTimeMillis() && oldSlot != -1) {
                if (isInHotbar(oldSlot)) {
                    mc.playerController.windowClick(0, 6, oldSlot % 9, ClickType.SWAP, mc.player);
                } else {
                    for (i = 0; i < 36; ++i) {
                        if (i != oldSlot) continue;
                        mc.playerController.windowClick(0, i, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                        mc.playerController.windowClick(0, 6, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                        mc.playerController.windowClick(0, i, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                        break;
                    }
                }
                this.time = System.currentTimeMillis() + this.cooldown.getValue().longValue();
                this.oldSlot = -1;
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mode.is("New")) {
            time = System.currentTimeMillis();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA &&
                oldSlot != -1 && mode.is("New")) {
            if (isInHotbar(oldSlot)) {
                mc.playerController.windowClick(0, 6, oldSlot % 9, ClickType.SWAP, mc.player);
            } else {
                for (int i = 0; i < 36; ++i) {
                    if (i != oldSlot) continue;
                    mc.playerController.windowClick(0, i, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                    mc.playerController.windowClick(0, 6, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                    mc.playerController.windowClick(0, i, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                    break;
                }
            }
            oldSlot = -1;
        }
    }

    private int findItem(net.minecraft.item.Item item) {
        for (int i = 0; i < 36; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    private boolean isInHotbar(net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return true;
            }
        }
        return false;
    }

    private boolean isInHotbar(int slot) {
        return slot >= 0 && slot < 9;
    }
}