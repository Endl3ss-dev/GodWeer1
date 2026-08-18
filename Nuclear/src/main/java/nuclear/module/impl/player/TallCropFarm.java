package nuclear.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.RenderUtils;

@ModuleDescriptor(
    name = "TallCropFarm",
    type = ModuleCategory.Player,
    desc = "Automatically breaks the upper part of sugar cane and bamboo"
)
public class TallCropFarm extends Module {
    private final SliderSetting range = new SliderSetting("Range", 5.0f, 2.0f, 8.0f, 0.5f);
    private final BooleanSetting includeCactus = new BooleanSetting("Include cactus", false);
    private final BooleanSetting renderTarget = new BooleanSetting("Render target", true);

    private BlockPos targetPos;

    public TallCropFarm() {
        addSettings(range, includeCactus, renderTarget);
    }

    @Override
    public boolean onEvent(Event event) {
        if (mc.player == null || mc.world == null) {
            targetPos = null;
            return false;
        }

        if (event instanceof EventUpdate) {
            updateBreaking();
        } else if (event instanceof EventRender && ((EventRender) event).isRender3D()) {
            renderTarget();
        }
        return false;
    }

    @Override
    protected void onDisable() {
        targetPos = null;
        if (mc.playerController != null) {
            mc.playerController.resetBlockRemoving();
        }
    }

    private void updateBreaking() {
        targetPos = findNearestCrop();
        if (targetPos == null) {
            return;
        }

        mc.playerController.blockHitDelay = 0;
        mc.playerController.onPlayerDamageBlock(targetPos, Direction.UP);
        mc.player.swingArm(net.minecraft.util.Hand.MAIN_HAND);
    }

    private BlockPos findNearestCrop() {
        BlockPos playerPos = mc.player.getPosition();
        int radius = range.getValue().intValue();
        double bestDistance = Double.MAX_VALUE;
        BlockPos best = null;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -2; y <= 4; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (!isHarvestableSegment(pos)) {
                        continue;
                    }

                    double distance = mc.player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        best = pos.toImmutable();
                    }
                }
            }
        }

        return best;
    }

    private boolean isHarvestableSegment(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block == Blocks.SUGAR_CANE || block == Blocks.BAMBOO || (includeCactus.get() && block == Blocks.CACTUS))) {
            return false;
        }

        return mc.world.getBlockState(pos.down()).getBlock() == block;
    }

    private void renderTarget() {
        if (!renderTarget.get() || targetPos == null) {
            return;
        }
        RenderUtils.Render3D.drawBlockBox(targetPos, ColorUtils.rgba(120, 255, 120, 180));
    }
}
