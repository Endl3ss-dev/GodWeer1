package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import org.privates.client.api.annotations.Category3;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.MoveUtil;
import org.privates.client.utils.player.PlayerUtil;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "LongJump", category = Category.MOVEMENT)
public class LongJump extends Module {
    public static LongJump getInstance() {
        return Instance.get(LongJump.class);
    }

    double pol;
    boolean boost;

    public void onEvent(UpdateEvent event) {
        if (event instanceof UpdateEvent && MoveUtil.isMoving()) {
            if (mc.player.isOnGround()) {
                boost = false;
            }
            if (mc.player.isOnGround() && PlayerUtil.getBlock(0, -1, 0) == Blocks.AIR) {
                if (mc.player.isOnGround() || (mc.player.fallDistance < mc.player.stepHeight && !mc.world.hasNoCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, mc.player.fallDistance - mc.player.stepHeight, 0.0D)))) {
                    mc.player.jump();
                    boost = true;
                }
            }
            if (boost && mc.player.fallDistance < 0.5f) {
                mc.player.setMotion(mc.player.getMotion().mul(1.05f, 1, 1.05f));
            }
        }
    }

    public void onEnable() {
        pol = mc.player.rotationPitch;
    }

    public void onDisable() {
        mc.gameSettings.keyBindSneak.setPressed(false);
    }
}