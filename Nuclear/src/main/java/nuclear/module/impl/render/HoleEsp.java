package nuclear.module.impl.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.ColorSetting;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.RenderUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@ModuleDescriptor(name = "HoleEsp", type = ModuleCategory.Render, desc = "Подсветка безопасных дыр 1x1")
public class HoleEsp extends Module {

    private static final long REFRESH_DELAY_MS = 150L;
    private static final int VERTICAL_SCAN_RANGE = 2;
    private static final Direction[] SUPPORT_DIRECTIONS = {
        Direction.DOWN,
        Direction.NORTH,
        Direction.SOUTH,
        Direction.WEST,
        Direction.EAST
    };

    private final Map<BlockPos, HoleType> holeCache = new LinkedHashMap<>();

    private final ColorSetting bedrockColor = new ColorSetting("Цвет бедрока", ColorUtils.rgba(0, 255, 0, 180));
    private final ColorSetting obsidianColor = new ColorSetting("Цвет обсидиана", ColorUtils.rgba(255, 140, 0, 180));
    private final ColorSetting mixedColor = new ColorSetting("Цвет смешанной дыры", ColorUtils.rgba(255, 255, 0, 180));
    private final ColorSetting unsafeColor = new ColorSetting("Цвет небезопасной дыры", ColorUtils.rgba(255, 60, 60, 180));
    private final SliderSetting range = new SliderSetting("Дистанция", 6.0f, 2.0f, 12.0f, 0.5f);
    private final BooleanSetting onlySafe = new BooleanSetting("Только безопасные", true);

    private long lastScanTime;

    public HoleEsp() {
        addSettings(range, onlySafe, bedrockColor, obsidianColor, mixedColor, unsafeColor);
    }

    @Override
    public void init() {
        refreshHoleCache();
    }

    @Override
    public boolean onEvent(Event event) {
        if (mc.player == null || mc.world == null) {
            holeCache.clear();
            return false;
        }

        if (event instanceof EventUpdate) {
            updateHoleCache();
            return false;
        }

        if (event instanceof EventRender render && render.isRender3D()) {
            updateHoleCache();
            renderHoles();
        }

        return false;
    }

    private void updateHoleCache() {
        long now = System.currentTimeMillis();
        if (!holeCache.isEmpty() && now - lastScanTime < REFRESH_DELAY_MS) {
            return;
        }

        refreshHoleCache();
        lastScanTime = now;
    }

    private void refreshHoleCache() {
        holeCache.clear();

        BlockPos playerPos = mc.player.getPosition();
        int scanRange = Math.max(1, Math.round(range.getValue().floatValue()));
        double maxDistanceSq = scanRange * scanRange;

        for (int x = -scanRange; x <= scanRange; x++) {
            for (int y = -VERTICAL_SCAN_RANGE; y <= VERTICAL_SCAN_RANGE; y++) {
                for (int z = -scanRange; z <= scanRange; z++) {
                    BlockPos candidate = playerPos.add(x, y, z);
                    if (playerPos.distanceSq(candidate) > maxDistanceSq) {
                        continue;
                    }

                    HoleType holeType = classifyHole(candidate);
                    if (holeType == null) {
                        continue;
                    }

                    if (onlySafe.get() && !holeType.isSafe()) {
                        continue;
                    }

                    holeCache.put(new BlockPos(candidate.getX(), candidate.getY(), candidate.getZ()), holeType);
                }
            }
        }
    }

    private HoleType classifyHole(BlockPos pos) {
        if (!hasAirColumn(pos)) {
            return null;
        }

        int bedrockBlocks = 0;
        int blastResistantBlocks = 0;

        for (Direction direction : SUPPORT_DIRECTIONS) {
            BlockState supportState = mc.world.getBlockState(pos.offset(direction));
            if (!supportState.isSolid()) {
                return null;
            }

            Block supportBlock = supportState.getBlock();
            if (supportBlock == Blocks.BEDROCK) {
                bedrockBlocks++;
            }
            if (isBlastResistant(supportBlock)) {
                blastResistantBlocks++;
            }
        }

        if (bedrockBlocks == SUPPORT_DIRECTIONS.length) {
            return HoleType.BEDROCK;
        }

        if (blastResistantBlocks == SUPPORT_DIRECTIONS.length) {
            return bedrockBlocks == 0 ? HoleType.OBSIDIAN : HoleType.MIXED;
        }

        return HoleType.UNSAFE;
    }

    private boolean hasAirColumn(BlockPos pos) {
        return mc.world.getBlockState(pos).isAir()
            && mc.world.getBlockState(pos.up()).isAir()
            && mc.world.getBlockState(pos.up(2)).isAir();
    }

    private boolean isBlastResistant(Block block) {
        return block == Blocks.BEDROCK
            || block == Blocks.OBSIDIAN
            || block == Blocks.CRYING_OBSIDIAN
            || block == Blocks.ENDER_CHEST
            || block == Blocks.RESPAWN_ANCHOR
            || block == Blocks.ANVIL
            || block == Blocks.CHIPPED_ANVIL
            || block == Blocks.DAMAGED_ANVIL
            || block == Blocks.NETHERITE_BLOCK
            || block == Blocks.ANCIENT_DEBRIS;
    }

    private void renderHoles() {
        if (holeCache.isEmpty()) {
            return;
        }

        Vector3d camera = mc.getRenderManager().info.getProjectedView();
        for (Map.Entry<BlockPos, HoleType> entry : holeCache.entrySet()) {
            renderHole(entry.getKey(), getColorForType(entry.getValue()), camera);
        }
    }

    private void renderHole(BlockPos pos, int color, Vector3d camera) {
        AxisAlignedBB box = new AxisAlignedBB(
            pos.getX() + 0.05,
            pos.getY(),
            pos.getZ() + 0.05,
            pos.getX() + 0.95,
            pos.getY() + 1.0,
            pos.getZ() + 0.95
        ).offset(-camera.x, -camera.y, -camera.z);

        RenderUtils.Render3D.drawBox(box, color);
    }

    private int getColorForType(HoleType type) {
        switch (type) {
            case BEDROCK:
                return bedrockColor.get();
            case OBSIDIAN:
                return obsidianColor.get();
            case MIXED:
                return mixedColor.get();
            case UNSAFE:
            default:
                return unsafeColor.get();
        }
    }

    private enum HoleType {
        BEDROCK(true),
        OBSIDIAN(true),
        MIXED(true),
        UNSAFE(false);

        private final boolean safe;

        HoleType(boolean safe) {
            this.safe = safe;
        }

        public boolean isSafe() {
            return safe;
        }
    }
}
