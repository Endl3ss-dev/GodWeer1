package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import org.privates.baritone.api.BaritoneAPI;
import org.privates.baritone.api.IBaritone;
import org.privates.baritone.api.pathing.goals.GoalBlock;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.GameUpdateEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CoordCube", category = Category.RENDER, description = "Cube render + smart mine automation")
public class CoordCube extends Module {

    public static CoordCube getInstance() {
        return Instance.get(CoordCube.class);
    }

    private static final int X1 = -70;
    private static final int Y1 = 87;
    private static final int Z1 = 10;
    private static final int X2 = -52;
    private static final int Y2 = 78;
    private static final int Z2 = 26;

    private static final String[] ANARCHY_COMMANDS = {
            "/an204", "/an205", "/an206", "/an207", "/an208", "/an209", "/an210", "/an211", "/an212", "/an213",
            "/an214", "/an215", "/an216", "/an217", "/an218", "/an219", "/an220",
            "/an303", "/an304", "/an305", "/an306", "/an307", "/an308",
            "/an502", "/an503", "/an504", "/an505", "/an506",
            "/an602", "/an603", "/an604", "/an605", "/an606"
    };

    private static final Block[] MINE_TEMPLATE = {
            Blocks.STONE,
            Blocks.COAL_ORE,
            Blocks.IRON_ORE,
            Blocks.GOLD_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.LAPIS_ORE,
            Blocks.DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.NETHER_QUARTZ_ORE,
            Blocks.NETHER_GOLD_ORE,
            Blocks.ANCIENT_DEBRIS
    };

    enum WorkState {
        GOTO_ZONE,
        MINING_ZONE
    }

    final BooleanSetting fill = new BooleanSetting(this, "Fill", true);
    final SliderSetting fillAlpha = new SliderSetting(this, "Fill Alpha", 0.2F, 0.05F, 1.0F, 0.05F).setVisible(fill::getValue);
    final SliderSetting lineWidth = new SliderSetting(this, "Line Width", 1.5F, 0.5F, 5.0F, 0.1F);
    final BooleanSetting depth = new BooleanSetting(this, "Depth Test", false);

    final BooleanSetting autoMine = new BooleanSetting(this, "Auto Mine", true);
    final BooleanSetting autoSwitchAnarchy = new BooleanSetting(this, "Auto Switch Anarchy", true).setVisible(autoMine::getValue);
    final SliderSetting remineDelay = new SliderSetting(this, "Remine Delay (sec)", 3F, 1F, 15F, 1F).setVisible(autoMine::getValue);
    final SliderSetting scanDelay = new SliderSetting(this, "Scan Delay (sec)", 2F, 1F, 10F, 1F).setVisible(autoMine::getValue);
    final SliderSetting gotoRepeatDelay = new SliderSetting(this, "Goto Repeat (sec)", 4F, 2F, 15F, 1F).setVisible(autoMine::getValue);

    final SliderSetting minDiamondsToStay = new SliderSetting(this, "Min Diamonds To Stay", 10F, 0F, 64F, 1F)
            .setVisible(() -> autoMine.getValue() && autoSwitchAnarchy.getValue());
    final SliderSetting minTargetBlocksToStay = new SliderSetting(this, "Min Stone+Ores To Stay", 120F, 0F, 3000F, 10F)
            .setVisible(() -> autoMine.getValue() && autoSwitchAnarchy.getValue());
    final SliderSetting switchDelay = new SliderSetting(this, "Switch Delay (sec)", 8F, 3F, 45F, 1F)
            .setVisible(() -> autoMine.getValue() && autoSwitchAnarchy.getValue());

    long lastMineStartMs = 0L;
    long lastScanMs = 0L;
    long lastGotoMs = 0L;
    long lastSwitchMs = 0L;

    int diamondsInCube = 0;
    int targetBlocksInCube = 0;
    Set<Block> scannedTargets = new HashSet<>();

    int anarchyIndex = 0;
    WorkState workState = WorkState.GOTO_ZONE;

    boolean originalCaptured = false;
    boolean prevAllowBreak;
    boolean prevAllowPlace;
    boolean prevAllowParkourAscend;
    boolean prevAllowWaterBucketFall;
    List<Item> prevThrowawayItems = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();
        captureOriginalBaritoneSettings();
        workState = WorkState.GOTO_ZONE;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        IBaritone baritone = getBaritone();
        if (baritone != null) {
            baritone.getMineProcess().cancel();
            baritone.getPathingBehavior().cancelEverything();
        }
        if (mc.player != null) {
            mc.player.sendChatMessage("#stop");
        }
        restoreOriginalBaritoneSettings();
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (mc.world == null) return;

        int base = Theme.getInstance().clientColor();
        int outlineColor = ColorUtil.replAlpha(base, 255);
        int fillColor = ColorUtil.replAlpha(base, (int) (fillAlpha.getValue() * 255.0F));

        RenderUtil3D.drawCubeFromCorners(
                event.getMatrix(),
                X1, Y1, Z1,
                X2, Y2, Z2,
                fillColor,
                outlineColor,
                lineWidth.getValue(),
                depth.getValue(),
                true,
                fill.getValue()
        );
    }

    @EventHandler
    public void onUpdate(GameUpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        IBaritone baritone = getBaritone();
        if (baritone == null) return;

        if (!autoMine.getValue()) {
            baritone.getMineProcess().cancel();
            restoreOriginalBaritoneSettings();
            return;
        }

        long now = System.currentTimeMillis();
        long scanDelayMs = (long) (scanDelay.getValue() * 1000L);
        if (now - lastScanMs >= scanDelayMs) {
            scanCube();
            lastScanMs = now;
        }

        boolean inside = isPlayerInsideCube();

        if (inside) {
            workState = WorkState.MINING_ZONE;
            applyMiningRestrictions();

            if (autoSwitchAnarchy.getValue() && shouldSwitchAnarchy()) {
                long switchDelayMs = (long) (switchDelay.getValue() * 1000L);
                if (now - lastSwitchMs >= switchDelayMs) {
                    switchToNextAnarchy(baritone);
                    lastSwitchMs = now;
                    workState = WorkState.GOTO_ZONE;
                }
                return;
            }

            startMineIfNeeded(baritone, now);
            return;
        }

        workState = WorkState.GOTO_ZONE;
        applyGotoRestrictions();
        if (baritone.getMineProcess().isActive()) {
            baritone.getMineProcess().cancel();
        }
        handleGotoToCube(baritone, now);
    }

    private void startMineIfNeeded(IBaritone baritone, long now) {
        if (scannedTargets.isEmpty()) return;
        if (baritone.getMineProcess().isActive()) return;

        long delayMs = (long) (remineDelay.getValue() * 1000L);
        if (now - lastMineStartMs < delayMs) return;

        // Start mining only when we are already in the cube.
        Block[] targets = scannedTargets.toArray(new Block[0]);
        baritone.getMineProcess().mine(0, targets);
        lastMineStartMs = now;

        ChatUtil.addText("CoordCube: mine start inside cube (stone + ores)");
    }

    private void handleGotoToCube(IBaritone baritone, long now) {
        BlockPos center = getCubeCenter();
        long gotoDelayMs = (long) (gotoRepeatDelay.getValue() * 1000L);
        if (now - lastGotoMs < gotoDelayMs) return;

        // Command-style goto, as requested.
        mc.player.sendChatMessage("#goto " + center.getX() + " " + center.getY() + " " + center.getZ());
        // API fallback for stability.
        baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(center));

        lastGotoMs = now;
    }

    private void switchToNextAnarchy(IBaritone baritone) {
        if (mc.player == null) return;

        baritone.getMineProcess().cancel();
        baritone.getPathingBehavior().cancelEverything();
        mc.player.sendChatMessage("#stop");

        String command = ANARCHY_COMMANDS[anarchyIndex % ANARCHY_COMMANDS.length];
        anarchyIndex++;

        ChatUtil.addText("CoordCube: resources low, switch -> " + command);
        mc.player.sendChatMessage(command);
    }

    private void scanCube() {
        int minX = Math.min(X1, X2);
        int minY = Math.min(Y1, Y2);
        int minZ = Math.min(Z1, Z2);
        int maxX = Math.max(X1, X2);
        int maxY = Math.max(Y1, Y2);
        int maxZ = Math.max(Z1, Z2);

        diamondsInCube = 0;
        targetBlocksInCube = 0;
        scannedTargets.clear();

        Set<Block> templateSet = new HashSet<>();
        for (Block b : MINE_TEMPLATE) templateSet.add(b);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block == Blocks.DIAMOND_ORE) diamondsInCube++;
                    if (!templateSet.contains(block)) continue;
                    if (!isMineable(state, pos)) continue;

                    targetBlocksInCube++;
                    scannedTargets.add(block);
                }
            }
        }
    }

    private boolean shouldSwitchAnarchy() {
        // Stay if cube still has enough diamonds or enough target blocks (stone+ores).
        return diamondsInCube < minDiamondsToStay.getValue() && targetBlocksInCube < minTargetBlocksToStay.getValue();
    }

    private boolean isPlayerInsideCube() {
        BlockPos p = mc.player.getPosition();
        int minX = Math.min(X1, X2);
        int minY = Math.min(Y1, Y2);
        int minZ = Math.min(Z1, Z2);
        int maxX = Math.max(X1, X2);
        int maxY = Math.max(Y1, Y2);
        int maxZ = Math.max(Z1, Z2);
        return p.getX() >= minX && p.getX() <= maxX
                && p.getY() >= minY && p.getY() <= maxY
                && p.getZ() >= minZ && p.getZ() <= maxZ;
    }

    private BlockPos getCubeCenter() {
        int minX = Math.min(X1, X2);
        int minY = Math.min(Y1, Y2);
        int minZ = Math.min(Z1, Z2);
        int maxX = Math.max(X1, X2);
        int maxY = Math.max(Y1, Y2);
        int maxZ = Math.max(Z1, Z2);
        return new BlockPos((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
    }

    private boolean isMineable(BlockState state, BlockPos pos) {
        if (state.isAir()) return false;
        if (!state.getFluidState().isEmpty() &&
                (state.getFluidState().isTagged(FluidTags.WATER) || state.getFluidState().isTagged(FluidTags.LAVA))) {
            return false;
        }

        Block block = state.getBlock();
        if (block == Blocks.BEDROCK || block == Blocks.BARRIER) return false;

        return state.getBlockHardness(mc.world, pos) >= 0.0F;
    }

    private IBaritone getBaritone() {
        return BaritoneAPI.getProvider().getBaritoneForPlayer(mc.player);
    }

    private void captureOriginalBaritoneSettings() {
        if (originalCaptured) return;
        prevAllowBreak = BaritoneAPI.getSettings().allowBreak.value;
        prevAllowPlace = BaritoneAPI.getSettings().allowPlace.value;
        prevAllowParkourAscend = BaritoneAPI.getSettings().allowParkourAscend.value;
        prevAllowWaterBucketFall = BaritoneAPI.getSettings().allowWaterBucketFall.value;
        prevThrowawayItems = new ArrayList<>(BaritoneAPI.getSettings().acceptableThrowawayItems.value);
        originalCaptured = true;
    }

    private void applyGotoRestrictions() {
        // During goto: no break, no place, just movement/jumps/pathing.
        BaritoneAPI.getSettings().allowBreak.value = false;
        BaritoneAPI.getSettings().allowPlace.value = false;
        BaritoneAPI.getSettings().allowParkourAscend.value = false;
        BaritoneAPI.getSettings().allowWaterBucketFall.value = false;
        BaritoneAPI.getSettings().acceptableThrowawayItems.value.clear();
    }

    private void applyMiningRestrictions() {
        // During mining in region: break allowed, but no placing/pillaring.
        BaritoneAPI.getSettings().allowBreak.value = true;
        BaritoneAPI.getSettings().allowPlace.value = false;
        BaritoneAPI.getSettings().allowParkourAscend.value = false;
        BaritoneAPI.getSettings().allowWaterBucketFall.value = false;
        BaritoneAPI.getSettings().acceptableThrowawayItems.value.clear();
    }

    private void restoreOriginalBaritoneSettings() {
        if (!originalCaptured) return;
        BaritoneAPI.getSettings().allowBreak.value = prevAllowBreak;
        BaritoneAPI.getSettings().allowPlace.value = prevAllowPlace;
        BaritoneAPI.getSettings().allowParkourAscend.value = prevAllowParkourAscend;
        BaritoneAPI.getSettings().allowWaterBucketFall.value = prevAllowWaterBucketFall;
        BaritoneAPI.getSettings().acceptableThrowawayItems.value.clear();
        BaritoneAPI.getSettings().acceptableThrowawayItems.value.addAll(prevThrowawayItems);
    }
}
