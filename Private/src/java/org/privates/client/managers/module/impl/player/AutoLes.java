package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoLes", category = Category.PLAYER, description = "Автоматически ломает брёвна")
public class AutoLes extends Module {

    public static AutoLes getInstance() {
        return Instance.get(AutoLes.class);
    }

    final SliderSetting radiusXZ = new SliderSetting(this, "Радиус XZ", 5.0f, 1.0f, 8.0f, 1.0f);
    final SliderSetting radiusY = new SliderSetting(this, "Радиус Y", 5.0f, 1.0f, 8.0f, 1.0f);
    final SliderSetting range = new SliderSetting(this, "Дистанция", 4.5f, 1.0f, 6.0f, 0.1f);

    BlockPos targetPos;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        updateAutoLes();
    }

    private void updateAutoLes() {
        if (mc.player == null || mc.world == null) {
            targetPos = null;
            return;
        }

        if (targetPos != null) {
            if (!isInRange(targetPos) || !isLog(targetPos) || mc.world.getBlockState(targetPos).isAir()) {
                targetPos = null;
            } else {
                mineTarget(targetPos);
                return;
            }
        }

        BlockPos playerPos = mc.player.getPosition();
        int rx = radiusXZ.getValue().intValue();
        int ry = radiusY.getValue().intValue();

        BlockPos[] best = {null};
        double[] bestDist = {Double.MAX_VALUE};
        BlockPos.getAllInBox(playerPos.add(-rx, -ry, -rx), playerPos.add(rx, ry, rx)).forEach(pos -> {
            if (!isLog(pos) || !isInRange(pos)) return;
            double dist = mc.player.getDistanceSq(Vector3d.copyCentered(pos));
            if (dist < bestDist[0]) {
                bestDist[0] = dist;
                best[0] = pos.toImmutable();
            }
        });
        targetPos = best[0];

        if (targetPos != null) {
            mineTarget(targetPos);
        }
    }

    private void mineTarget(BlockPos pos) {
        mc.playerController.onPlayerDamageBlock(pos, Direction.UP);
        mc.player.swingArm(Hand.MAIN_HAND);
    }

    private boolean isInRange(BlockPos pos) {
        return mc.player != null && mc.player.getDistanceSq(Vector3d.copyCentered(pos)) <= range.getValue() * range.getValue();
    }

    private boolean isLog(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return state.isIn(BlockTags.LOGS);
    }
}
