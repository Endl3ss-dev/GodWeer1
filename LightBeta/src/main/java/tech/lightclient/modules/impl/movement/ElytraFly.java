package tech.lightclient.modules.impl.movement;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.player.MoveInputEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.rotation.RotationTask;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;

@FunctionAdd(name = "ElytraFly", alias = "Elytra Fly", category = Category.Movement, description = "Авто-взлёт и удержание высоты на элитрах")
public class ElytraFly extends Function {
    private static final String ROTATION_TASK = "elytrafly";
    private static final int ROTATION_PRIORITY = 50;

    private boolean wasOnGround;

    @Override
    public void onDisable() {
        wasOnGround = false;
        resetRotation();
        super.onDisable();
    }

    @EventHandler
    public void onInput(MoveInputEvent event) {
        if (mc.player == null) {
            return;
        }

        ItemStack chestStack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (!chestStack.isOf(Items.ELYTRA)) {
            return;
        }

        if (mc.player.isOnGround()) {
            event.setOverrideJump(true);
            event.setJumpPressed(true);
            wasOnGround = true;
        } else if (!mc.player.isGliding()) {
            if (!wasOnGround) {
                startFallFlying();
            } else {
                wasOnGround = false;
            }
        } else {
            wasOnGround = false;
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        ItemStack chestStack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
        if (!chestStack.isOf(Items.ELYTRA)) {
            return;
        }

        if (!mc.player.isGliding()) {
            return;
        }

        RotationTask.create(ROTATION_TASK, ROTATION_PRIORITY);
        RotationTask.setTargetRotation(mc.player.getYaw(), 0.0F, 180.0F, ROTATION_PRIORITY);

        mc.player.setVelocity(
                mc.player.getVelocity().x,
                mc.player.getVelocity().y + 0.0305D,
                mc.player.getVelocity().z
        );
    }

    private void startFallFlying() {
        if (mc.player == null || mc.getNetworkHandler() == null) {
            return;
        }

        mc.player.startGliding();
        mc.getNetworkHandler().sendPacket(
                new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
    }

    private void resetRotation() {
        if (RotationTask.rotationPriority > ROTATION_PRIORITY) {
            RotationTask.remove(ROTATION_TASK);
            return;
        }

        RotationTask.rotationState = RotationTask.RotationState.RESET;
        RotationTask.returnStartTime = System.currentTimeMillis() - 100L;
        RotationTask.rotationPriority = 0;
        RotationTask.inactiveMs = 0L;
        RotationTask.needSmoothReset = true;
        RotationTask.resetDelayMs = 400L;
        RotationTask.remove(ROTATION_TASK);
    }
}
