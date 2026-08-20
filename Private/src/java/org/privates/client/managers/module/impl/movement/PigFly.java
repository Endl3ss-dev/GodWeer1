package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.network.play.client.CEntityActionPacket;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.MoveUtil;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "PigFly", category = Category.MOVEMENT, description = "Полет на свинке")
public class PigFly extends Module {

    public static PigFly getInstance() {
        return Instance.get(PigFly.class);
    }

    private final SliderSetting speed = new SliderSetting(this, "Скорость", 10.0f, 1.0f, 20.0f, 0.05f);
    private final BooleanSetting noDismount = new BooleanSetting(this, "Не вылезать", true);
    private final BooleanSetting savePig = new BooleanSetting(this, "Спасать свинью", true);

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.player.getRidingEntity() == null) return;

        if (mc.player.getRidingEntity() instanceof PigEntity pig) {
            pig.setMotion(pig.getMotion().x, 0, pig.getMotion().z);

            if (mc.player.isPassenger()) {
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    pig.setMotion(pig.getMotion().x, 1, pig.getMotion().z);
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    pig.setMotion(pig.getMotion().x, -1, pig.getMotion().z);
                }

                if (MoveUtil.isMoving()) {
                    final double yaw = getDirection();

                    pig.setMotion(
                            -Math.sin(yaw) * speed.getValue(),
                            pig.getMotion().y,
                            Math.cos(yaw) * speed.getValue()
                    );
                } else {
                    pig.setMotion(0, pig.getMotion().y, 0);
                }

                if ((!isBlockUnder(4f) || mc.player.collidedHorizontally || mc.player.collidedVertically) && savePig.getValue()) {
                    pig.setMotion(pig.getMotion().x, pig.getMotion().y + 1, pig.getMotion().z);
                }
            }
        }
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof CEntityActionPacket actionPacket) {
            if (!noDismount.getValue() || !(mc.player.getRidingEntity() instanceof PigEntity)) return;

            CEntityActionPacket.Action action = actionPacket.getAction();
            if (action == CEntityActionPacket.Action.PRESS_SHIFT_KEY || action == CEntityActionPacket.Action.RELEASE_SHIFT_KEY) {
                event.cancel();
            }
        }
    }

    private double getDirection() {
        float rotationYaw = mc.player.rotationYaw;

        if (mc.player.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.player.moveForward < 0) {
            forward = -0.5f;
        } else if (mc.player.moveForward > 0) {
            forward = 0.5f;
        }

        if (mc.player.moveStrafing > 0) {
            rotationYaw -= 90 * forward;
        }

        if (mc.player.moveStrafing < 0) {
            rotationYaw += 90 * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    private boolean isBlockUnder(float distance) {
        if (mc.player == null || mc.world == null) return false;

        for (int i = 0; i < distance; i++) {
            if (!mc.world.getBlockState(mc.player.getPosition().down(i)).isAir()) {
                return true;
            }
        }
        return false;
    }

    public boolean notStopRiding() {
        return isEnabled() && noDismount.getValue();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}