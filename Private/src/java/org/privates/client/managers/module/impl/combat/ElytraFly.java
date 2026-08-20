package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Rage;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.lib.util.time.StopWatch;

@Category1

@Rage
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Elytra(Fly)", category = Category.MOVEMENT, description = "Элитра флай которые работает на нагруднике")
public class ElytraFly extends Module {

    private final StopWatch timerUtil = new StopWatch();
    private final StopWatch timerUtil1 = new StopWatch();
    private final StopWatch timerUtil2 = new StopWatch();
    private final SliderSetting timerStartFireWork = new SliderSetting(this, "Задержка фейверка", 400f, 50f, 1500f, 1f);
    private final BooleanSetting tpssync = new BooleanSetting(this, "Синхронизация с тпс", false);
    public final ModeSetting mode = new ModeSetting(this, "Mode", "Старый", "РилиВили");

    int oldItem = -1;
    public float spee = 0.0f;
    public static long lastStartFalling;

    @EventHandler
    public void weslogovno(UpdateEvent eventUpdate) {
        if (this.oldItem != -1 && mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA &&
                mc.player.inventory.getStackInSlot(this.oldItem).getItem() instanceof ArmorItem && this.timerUtil2.finished(550)) {
            mc.playerController.windowClick(0, 6, this.oldItem, ClickType.SWAP, mc.player);
            this.oldItem = -1;
            this.timerUtil2.reset();
            this.toggle();
        }
    }

    @EventHandler

    public void update(UpdateEvent eventUpdate) {
        if (getItemSlot(Items.FIREWORK_ROCKET) == -1) {
            return;
        }

        int timeSwap = 610;
        if (this.tpssync.getValue()) {
            timeSwap = 610;
        } else if (this.mode.is("Старый")) {
            timeSwap = 185;
        }

        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() != Items.ELYTRA ||
                    mc.player.isOnGround() ||
                    mc.player.isInWater() ||
                    mc.player.isInLava() ||
                    mc.player.isElytraFlying()) continue;

            if (this.timerUtil1.finished(timeSwap)) {
                this.timerUtil2.reset();
                mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                mc.player.startFallFlying();
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                mc.playerController.windowClick(0, 6, i, ClickType.SWAP, mc.player);
                this.oldItem = i;
                this.timerUtil1.reset();
            }

            if (!this.timerUtil.finished((long) this.timerStartFireWork.getValue().floatValue()) || !mc.player.isElytraFlying())
                continue;

            if (mc.player.isHandActive()) {
                int fireworkSlot = getItemSlot(Items.FIREWORK_ROCKET);
                if (fireworkSlot != -1) {
                    mc.playerController.windowClick(0, fireworkSlot, 40, ClickType.SWAP, mc.player);
                    mc.playerController.processRightClick(mc.player, mc.world, Hand.OFF_HAND);
                    mc.playerController.windowClick(0, fireworkSlot, 40, ClickType.SWAP, mc.player);
                }
            } else {
                inventorySwapClick(Items.FIREWORK_ROCKET, false);
            }
            this.timerUtil.reset();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.oldItem != -1) {
            if (mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA &&
                    mc.player.inventory.getStackInSlot(this.oldItem).getItem() instanceof ArmorItem) {
                mc.playerController.windowClick(0, 6, this.oldItem, ClickType.SWAP, mc.player);
            }
            this.oldItem = -1;
        }
        mc.gameSettings.keyBindSneak.setPressed(false);
    }

    public static int getItemSlot(Item input) {
        for (ItemStack stack : mc.player.getArmorInventoryList()) {
            if (stack.getItem() == input) {
                return -2;
            }
        }

        int slot = -1;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() == input) {
                slot = i;
                break;
            }
        }

        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public static void inventorySwapClick(Item item, boolean rotation) {
        if (getItemIndex(item) != -1) {
            int i;
            if (doesHotbarHaveItem(item)) {
                for (i = 0; i < 9; ++i) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() != item) continue;

                    if (i != mc.player.inventory.currentItem) {
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(i));
                    }

                    if (rotation) {
                        AuraNew AuraNew = org.privates.client.managers.module.impl.combat.AuraNew.getInstance();
                        if (AuraNew != null && AuraNew.target() != null) {
                            mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(mc.player.rotationYaw, mc.player.rotationPitch, false));
                        }
                    }

                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));

                    if (i == mc.player.inventory.currentItem) break;
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                    break;
                }
            }

            if (!doesHotbarHaveItem(item)) {
                for (i = 0; i < 36; ++i) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() != item) continue;

                    mc.playerController.windowClick(0, i, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem % 8 + 1));

                    if (rotation) {
                        AuraNew auraNew = AuraNew.getInstance();
                        if (auraNew != null && auraNew.target() != null) {
                            mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(mc.player.rotationYaw, mc.player.rotationPitch, false));
                        }
                    }

                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                    mc.playerController.windowClick(0, i, mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, mc.player);
                    break;
                }
            }
        }
    }

    public static boolean doesHotbarHaveItem(Item item) {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return true;
            }
        }
        return false;
    }

    public static int getItemIndex(Item item) {
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }
}