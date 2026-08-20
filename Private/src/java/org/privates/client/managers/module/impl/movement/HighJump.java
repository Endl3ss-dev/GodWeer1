package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.privates.client.api.annotations.Beta;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;

@Category3
@Beta
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "HighJump", category = Category.MOVEMENT, description = "Подбрасывает вас в верх рядом с открытыи шалкером")
public class HighJump extends Module {

    public static HighJump getInstance() {
        return Instance.get(HighJump.class);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        for (TileEntity tile : mc.world.loadedTileEntityList) {
            if (tile instanceof ShulkerBoxTileEntity shulker) {
                double distanceXZ = Math.sqrt(
                        Math.pow(mc.player.getPosX() - (tile.getPos().getX() + 0.5), 2) +
                                Math.pow(mc.player.getPosZ() - (tile.getPos().getZ() + 0.5), 2)
                );

                double maxYDistance = mc.player.motion.y > 2 ? 30 : 2;
                double distanceY = Math.abs(mc.player.getPosY() - (tile.getPos().getY() + 0.5));

                if (distanceXZ <= 1 && distanceY <= maxYDistance && mc.player.fallDistance == 0) {
                    float progress = shulker.getProgress(1.0f);

                    if (progress > 0.0f && progress != 2.0f) {
                        mc.player.motion.y = 2.3f;
                        ChatUtil.addText("&4ТАКСИ АШОТ В ВАШЕМ РАСПОРЯЖЕНИЕ");
                    }
                }
            }
        }
    }
}