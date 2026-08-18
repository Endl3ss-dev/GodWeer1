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
import nuclear.module.settings.imp.MultiBoxSetting;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.RenderUtils;

@ModuleDescriptor(
    name = "AutoMine",
    type = ModuleCategory.Player,
    desc = "Automatically mines the nearest configured ore block"
)
public class AutoMine extends Module {
    private final MultiBoxSetting blocks = new MultiBoxSetting(
        "Blocks",
        new BooleanSetting("Coal", true),
        new BooleanSetting("Iron", true),
        new BooleanSetting("Gold", true),
        new BooleanSetting("Diamond", true),
        new BooleanSetting("Emerald", true),
        new BooleanSetting("Redstone", true),
        new BooleanSetting("Lapis", true),
        new BooleanSetting("Quartz", true),
        new BooleanSetting("Ancient debris", true),
        new BooleanSetting("Obsidian", false)
    );
    private final SliderSetting range = new SliderSetting("Range", 5.0f, 2.0f, 8.0f, 0.5f);
    private final SliderSetting verticalRange = new SliderSetting("Vertical range", 4.0f, 1.0f, 8.0f, 1.0f);
    private final BooleanSetting renderTarget = new BooleanSetting("Render target", true);

    private BlockPos targetPos;

    public AutoMine() {
        addSettings(blocks, range, verticalRange, renderTarget);
    }

    @Override
    public boolean onEvent(Event event) {
        if (mc.player == null || mc.world == null) {
            targetPos = null;
            return false;
        }

        if (event instanceof EventUpdate) {
            updateMining();
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

    private void updateMining() {
        targetPos = findNearestBlock();
        if (targetPos == null) {
            return;
        }

        mc.playerController.blockHitDelay = 0;
        mc.playerController.onPlayerDamageBlock(targetPos, Direction.UP);
        mc.player.swingArm(net.minecraft.util.Hand.MAIN_HAND);
    }

    private BlockPos findNearestBlock() {
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
                    if (!isTrackedBlock(block)) {
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

    private boolean isTrackedBlock(Block block) {
        return (blocks.get("Coal") && block == Blocks.COAL_ORE)
            || (blocks.get("Iron") && block == Blocks.IRON_ORE)
            || (blocks.get("Gold") && block == Blocks.GOLD_ORE)
            || (blocks.get("Diamond") && block == Blocks.DIAMOND_ORE)
            || (blocks.get("Emerald") && block == Blocks.EMERALD_ORE)
            || (blocks.get("Redstone") && block == Blocks.REDSTONE_ORE)
            || (blocks.get("Lapis") && block == Blocks.LAPIS_ORE)
            || (blocks.get("Quartz") && block == Blocks.NETHER_QUARTZ_ORE)
            || (blocks.get("Ancient debris") && block == Blocks.ANCIENT_DEBRIS)
            || (blocks.get("Obsidian") && block == Blocks.OBSIDIAN);
    }

    private void renderTarget() {
        if (!renderTarget.get() || targetPos == null) {
            return;
        }
        RenderUtils.Render3D.drawBlockBox(targetPos, ColorUtils.rgba(255, 160, 70, 180));
    }
}
