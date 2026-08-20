package org.privates.client.managers.module.impl.movement;

import com.adl.nativeprotect.Native;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Rage;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.EventFireworkMotion;
import org.privates.client.managers.events.player.MoveEvent;
import org.privates.client.managers.events.player.MoveInputEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;
import org.privates.client.utils.player.MoveUtil;
import org.privates.lib.util.time.StopWatch;

@Category1
@Rage
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ElytraJump", category = Category.MOVEMENT, description = "Вам нужно прыгнуть и включить эту функцию после чего на многих античитах вы полетите вверх со скорость 30-40 бпс")
public class ElytraJump extends Module {

    public static ElytraJump getInstance() {
        return Instance.get(ElytraJump.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Mode", "Fireworks", "Exploit");
    private final BooleanSetting auto = new BooleanSetting(this, "Авто", true).setVisible(() -> mode.is("Exploit"));
    private final BooleanSetting bypass = new BooleanSetting(this, "Bypass", false).setVisible(() -> mode.is("Exploit"));

    private final StopWatch timer = new StopWatch();

    @EventHandler
    @Native
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (mode.is("Exploit")) {
            if (mc.player.hurtTime > 0) {
                if (auto.getValue() && mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem().equals(Items.ELYTRA)) {
                    swap(false);
                }
                return;
            }

            if (mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem().equals(Items.ELYTRA)) {
                if (!mc.player.isElytraFlying() && mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA && MoveUtil.isMoving()) {
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                    mc.player.startFallFlying();
                }

                MoveUtil.setSpeed(0);

                if (mc.player.isElytraFlying()) {
                    if (timer.finished(500) && bypass.getValue()) {
                        Slot fireworkSlot = InvUtil.getSlot(Items.FIREWORK_ROCKET);
                        if (fireworkSlot != null) {
                            InvUtil.useItem(Hand.MAIN_HAND, mc.player.rotationYaw, mc.player.rotationPitch);
                        }
                        timer.reset();
                    }
                    mc.player.setMotion(mc.player.getMotion().x, mc.player.getMotion().y + (0.06 + Math.random() * 0.001), mc.player.getMotion().z);
                }
            } else {
                if (auto.getValue()) {
                    swap(false);
                }
            }
        } else if (mode.is("Fireworks")) {
            if (!mc.player.isElytraFlying() && mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA && MoveUtil.isMoving()) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                mc.player.startFallFlying();
            }

            if (!mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.gameSettings.keyBindJump.setPressed(true);
            }
        }

        AuraNew auraNew = AuraNew.getInstance();
        if (mc.player.isElytraFlying() && auraNew != null) {
            float yaw = auraNew.isEnabled() && auraNew.target() != null ?
                    auraNew.getLerpRotation().x : mc.player.rotationYaw;
            float pitch = mode.is("Fireworks") ? -90 : 0;
            RotationComponent.update(new Rotation(yaw, pitch), 360, 360, 0, 5);
        }
    }

    @EventHandler
    public void onMove(MoveEvent event) {
        if (mc.player == null || !mode.is("Fireworks")) return;

        Slot fireworkSlot = InvUtil.getSlot(Items.FIREWORK_ROCKET);
        if (fireworkSlot == null) {
            print("Для работы ElytraJump нужны фейерверки.");
            toggle();
            return;
        }

        Slot elytraSlot = InvUtil.getSlot(Items.ELYTRA);
        if (elytraSlot == null) {
            print("Для работы ElytraJump нужна элитра.");
            toggle();
            return;
        }

        if (elytraSlot.slotNumber > 9) {
            int hotbarSlot = 8 + 36;
            mc.playerController.windowClick(0, elytraSlot.slotNumber, hotbarSlot - 36, ClickType.SWAP, mc.player);
        }

        if (!mc.player.isOnGround() && mc.player.fallDistance > 0 && !mc.player.isInWater() && !mc.player.isInLava()) {
            if (timer.finished(500)) {
                mc.playerController.windowClick(0, 6, elytraSlot.slotNumber % 9, ClickType.SWAP, mc.player);
                InvUtil.useItem(Hand.MAIN_HAND, mc.player.rotationYaw, mc.player.rotationPitch);
                timer.reset();
            }
        }
    }

    @EventHandler
    public void onMoveInput(MoveInputEvent event) {
        if (mc.player == null || !mode.is("Exploit")) return;

        if (mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem().equals(Items.ELYTRA)) {
            event.setForward(0);
            event.setStrafe(0);
        }

        if (mc.player.inventory.getStackInSlot(38).getItem() == Items.ELYTRA &&
                mc.player.movementInput.jump && !mc.player.isElytraFlying()) {
            mc.player.movementInput.jump = false;
        }
    }

    @EventHandler
    public void onFireworkMotion(EventFireworkMotion event) {
        if (mode.is("Exploit") && bypass.getValue()) {
            event.setVector3d(Vector3d.ZERO);
            event.setCancelled(true);
        }
    }

    private void swap(boolean chestplate) {
        if (!mode.is("Exploit")) return;

        Slot targetSlot = chestplate ? findChestplate() : InvUtil.getSlot(Items.ELYTRA);

        if (targetSlot != null) {
            mc.playerController.windowClick(0, targetSlot.slotNumber, 6, ClickType.SWAP, mc.player);
        }
    }

    private Slot findChestplate() {
        return mc.player.openContainer.inventorySlots.stream()
                .filter(slot -> slot.getStack().getItem() instanceof ArmorItem)
                .filter(slot -> ((ArmorItem) slot.getStack().getItem()).getEquipmentSlot() == EquipmentSlotType.CHEST)
                .filter(slot -> slot.getStack().getItem() != Items.ELYTRA)
                .findFirst()
                .orElse(null);
    }

    private void print(String message) {
        if (mc.ingameGUI != null && mc.ingameGUI.getChatGUI() != null) {
            mc.ingameGUI.getChatGUI().printChatMessage(
                    new net.minecraft.util.text.StringTextComponent("§b[ElytraJump]§r " + message)
            );
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        timer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mode.is("Exploit") && auto.getValue()) {
            if (mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem().equals(Items.ELYTRA)) {
                swap(true);
            }
        }
    }
}