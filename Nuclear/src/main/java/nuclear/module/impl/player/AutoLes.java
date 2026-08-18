package nuclear.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.RotatedPillarBlock;
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
    name = "AutoLes",
    type = ModuleCategory.Player,
    desc = "Automatically breaks nearby tree blocks"
)
public class AutoLes extends Module {
    private final SliderSetting range = new SliderSetting("Range", 5.0f, 2.0f, 8.0f, 0.5f);
    private final SliderSetting verticalRange = new SliderSetting("Vertical range", 5.0f, 1.0f, 12.0f, 1.0f);
    private final BooleanSetting includeLeaves = new BooleanSetting("Include leaves", false);
    private final BooleanSetting renderTarget = new BooleanSetting("Render target", true);

    private BlockPos targetPos;

    public AutoLes() {
        addSettings(range, verticalRange, includeLeaves, renderTarget);
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
        targetPos = findNearestTreeBlock();
        if (targetPos == null) {
            return;
        }

        mc.playerController.blockHitDelay = 0;
        mc.playerController.onPlayerDamageBlock(targetPos, Direction.UP);
        mc.player.swingArm(net.minecraft.util.Hand.MAIN_HAND);
    }

    private BlockPos findNearestTreeBlock() {
        BlockPos playerPos = mc.player.getPosition();
        int horizontal = range.getValue().intValue();
        int vertical = verticalRange.getValue().intValue();
        double bestDistance = Double.MAX_VALUE;
        BlockPos best = null;

        for (int x = -horizontal; x <= horizontal; x++) {
            for (int y = -vertical; y <= vertical; y++) {
                for (int z = -horizontal; z <= horizontal; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (!isTreeBlock(block)) {
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

    private boolean isTreeBlock(Block block) {
        if (block instanceof RotatedPillarBlock) {
            String path = block.getRegistryName() == null ? "" : block.getRegistryName().getPath();
            return path.contains("log") || path.contains("wood") || path.contains("stem") || path.contains("hyphae");
        }

        if (!includeLeaves.get()) {
            return false;
        }

        String path = block.getRegistryName() == null ? "" : block.getRegistryName().getPath();
        return path.contains("leaves") || path.contains("wart_block");
    }

    private void renderTarget() {
        if (!renderTarget.get() || targetPos == null) {
            return;
        }
        RenderUtils.Render3D.drawBlockBox(targetPos, ColorUtils.rgba(80, 220, 120, 180));
    }
}
