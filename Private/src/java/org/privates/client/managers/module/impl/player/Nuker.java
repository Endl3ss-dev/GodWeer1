package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

import java.util.Comparator;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Nuker", category = Category.PLAYER, description = "Авто копает територию")
public class Nuker extends Module {
    public static Nuker getInstance() {
        return Instance.get(Nuker.class);
    }

    private final SliderSetting radiusXZ = new SliderSetting(this, "Радиус XZ", 3, 1, 5, 1);
    private final SliderSetting radiusY = new SliderSetting(this, "Радиус Y", 1, 1, 6, 1);
    private final BooleanSetting orePriority = new BooleanSetting(this, "Приоритет на руды", true);
    private final BooleanSetting fastBreak = new BooleanSetting(this, "ФастБрик", true);
    private final BooleanSetting holyworld = new BooleanSetting(this, "Holyworld", false);

    private AxisAlignedBB box;
    private BlockPos pos;
    private boolean waitingRotation = false;

    @EventHandler
    public void onRender3D(Render3DLastEvent e) {
        if (pos != null) {
            RenderUtil3D.drawBoundingBox(e.getMatrix(), box, Theme.getInstance().clientColor(), 1.1f, false, false);
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        updateNuker();
    }

    private float wrapLerp(float speed, float from, float to) {
        float delta = net.minecraft.util.math.MathHelper.wrapDegrees(to - from);
        return from + delta * net.minecraft.util.math.MathHelper.clamp(speed, 0.0f, 1.0f);
    }

    public void updateNuker() {
        pos = PlayerUtil.getCube(mc.player.getPosition(), radiusXZ.getValue(), radiusY.getValue()).stream()
                .filter(this::validBlock)
                .sorted(Comparator.comparing(blockPos -> mc.player.getDistanceSq(Vector3d.copyCentered(blockPos))))
                .min(Comparator.comparing(blockPos -> orePriority.getValue() && mc.world.getBlockState(blockPos).getBlock() instanceof OreBlock ? 0 : 1))
                .orElse(null);

        if (pos != null) {
            box = mc.world.getBlockState(pos).getShape(mc.world, pos).getBoundingBox().offset(pos);

            if (holyworld.getValue()) {
                Vector3d center = Vector3d.copyCentered(pos);
                Vector3d playerPos = mc.player.getEyePosition(1.0F);
                Vector3d direction = center.subtract(playerPos);

                float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
                float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

                // Мгновенно выставляем ротацию головы и тела
                mc.player.rotationYaw = targetYaw;
                mc.player.rotationPitch = net.minecraft.util.math.MathHelper.clamp(targetPitch, -90f, 90f);
                mc.player.prevRotationYaw = targetYaw;
                mc.player.prevRotationPitch = net.minecraft.util.math.MathHelper.clamp(targetPitch, -90f, 90f);

                if (!waitingRotation) {
                    waitingRotation = true;
                    return;
                }

                if (RotationComponent.getInstance().isRotating()) {
                    return;
                }

                waitingRotation = false;
                mc.playerController.onPlayerDamageBlock(pos, Direction.UP);
                mc.player.swingArm(Hand.MAIN_HAND);
            } else {
                mc.playerController.onPlayerDamageBlock(pos, Direction.UP);
                mc.player.swingArm(Hand.MAIN_HAND);
                waitingRotation = false;
            }
        } else {
            waitingRotation = false;
        }
    }

    private boolean validBlock(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return !state.isAir() && state.getBlock() != Blocks.CAVE_AIR && state.getBlock() != Blocks.WATER && state.getBlock() != Blocks.LAVA && state.getBlock() != Blocks.BEDROCK && state.getBlock() != Blocks.BARRIER;
    }
}