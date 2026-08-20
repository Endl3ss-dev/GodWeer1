package org.privates.client.managers.module.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;

@Category1

@ModuleInfo(name = "ElytraFly", category = Category.MOVEMENT, description = "Элитра флай на motion")
public class MotionElytraFlySunrise extends Module {
    private final SliderSetting speed = new SliderSetting(this, "Скорость", 1.4F, 0.1F, 5.0F, 0.05f);
    private final SliderSetting upSpeed = new SliderSetting(this, "Скорость вверх", 1.1F, 0.1F, 3.0F, 0.05f);
    private final SliderSetting downSpeed = new SliderSetting(this, "Скорость вниз", 1.1F, 0.1F, 3.0F, 0.05f);
    private final SliderSetting fireworkDelay = new SliderSetting(this, "Firework Delay (ms)", 2200, 500, 7000, 50);
    private final SliderSetting kickBypass = new SliderSetting(this, "Kick Bypass (sec)", 2.5f, 0.7f, 7f, 0.1f);
    private final BooleanSetting autoStart = new BooleanSetting(this, "Авто Elytra", true);
    private final BooleanSetting autoFirework = new BooleanSetting(this, "Авто Firework", true);
    private final BooleanSetting silentFirework = new BooleanSetting(this, "Silent Firework", false);
    private final BooleanSetting legitMode = new BooleanSetting(this, "Легит режим", false);
    private final ModeSetting pitchMode = new ModeSetting(this, "Pitch Mode", "Vanilla", "Sunrise", "AAC", "Matrix");
    private final BooleanSetting strictYaw = new BooleanSetting(this, "Фиксировать Yaw", false);

    private long lastKickBypass = 0;
    private long lastFirework = 0;

    @EventHandler
    private void onUpdate(UpdateEvent e) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;

        if (autoStart.getValue() && !player.isElytraFlying() && player.isAirBorne && player.getMotion().y < -0.2 && player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA) {
            player.jump();
            return;
        }
        if (!player.isElytraFlying()) return;

        double spd = speed.getValue();
        float baseYaw = player.rotationYaw;
        float basePitch = player.rotationPitch;

        boolean forward = Minecraft.getInstance().gameSettings.keyBindForward.isKeyDown();
        boolean back = Minecraft.getInstance().gameSettings.keyBindBack.isKeyDown();
        boolean left = Minecraft.getInstance().gameSettings.keyBindLeft.isKeyDown();
        boolean right = Minecraft.getInstance().gameSettings.keyBindRight.isKeyDown();
        boolean jump = Minecraft.getInstance().gameSettings.keyBindJump.isKeyDown();
        boolean sneak = Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown();

        double moveX = 0, moveZ = 0;
        if (forward) moveZ += 1;
        if (back) moveZ -= 1;
        if (left) moveX -= 1;
        if (right) moveX += 1;
        boolean anyMove = moveX != 0 || moveZ != 0;

        float moveYaw = baseYaw;
        float movePitch = 0;

        if (anyMove) {
            double angle = Math.atan2(moveX, moveZ);
            moveYaw = (float) (baseYaw + Math.toDegrees(angle));
        }

        switch (pitchMode.getValue()) {
            case "Sunrise":
                if (jump && !sneak) movePitch = -22.5f;
                else if (sneak && !jump) movePitch = 22.5f;
                else movePitch = 0;
                break;
            case "AAC":
                if (jump && !sneak) movePitch = -42f;
                else if (sneak && !jump) movePitch = 42f;
                else movePitch = 0;
                break;
            case "Matrix":
                if (jump && !sneak) movePitch = -31f;
                else if (sneak && !jump) movePitch = 31f;
                else movePitch = 0;
                break;
            default:
                if (jump && !sneak) movePitch = -45f;
                else if (sneak && !jump) movePitch = 45f;
                else movePitch = 0;
                break;
        }

        if (legitMode.getValue()) {
            moveYaw = baseYaw;
            movePitch = basePitch;
        } else if (strictYaw.getValue()) {
            moveYaw = Math.round(moveYaw / 45f) * 45f;
        }

        if (anyMove || jump || sneak) {
            RotationComponent.update(new Rotation(moveYaw, movePitch), 360, 360, 0, 5);
        }

        double radYaw = Math.toRadians(moveYaw);
        double radPitch = Math.toRadians(movePitch);

        double mx = -Math.sin(radYaw) * Math.cos(radPitch) * spd;
        double my = -Math.sin(radPitch) * (jump ? upSpeed.getValue() : (sneak ? downSpeed.getValue() : spd));
        double mz = Math.cos(radYaw) * Math.cos(radPitch) * spd;

        if (!(anyMove || jump || sneak)) {
            mx = 0;
            my = 0;
            mz = 0;
        }

        if (System.currentTimeMillis() - lastKickBypass > (long) (kickBypass.getValue() * 1000)) {
            my -= 0.08;
            lastKickBypass = System.currentTimeMillis();
        }

        player.setMotion(mx, my, mz);

        if (autoFirework.getValue()) {
            double motionLen = Math.sqrt(player.getMotion().x * player.getMotion().x + player.getMotion().y * player.getMotion().y + player.getMotion().z * player.getMotion().z);
            if ((motionLen < (spd * 0.6) || System.currentTimeMillis() - lastFirework > fireworkDelay.getValue())) {
                if (player.getHeldItemMainhand().getItem() == Items.FIREWORK_ROCKET) {
                    if (silentFirework.getValue()) {

                        player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    } else {
                        player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    }
                    lastFirework = System.currentTimeMillis();
                }
            }
        }
    }
}