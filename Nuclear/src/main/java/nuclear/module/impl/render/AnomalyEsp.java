package nuclear.module.impl.render;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.ColorSetting;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.List;

@ModuleDescriptor(name = "AnomalyEsp", type = ModuleCategory.Render, desc = "Подсвечивает аномальные блоки которые не естественно стоят")
public class AnomalyEsp extends Module {
    private static final long SCAN_DELAY_MS = 250L;

    private final SliderSetting range = new SliderSetting("Дистанция", 8.0f, 4.0f, 24.0f, 1.0f);
    private final ColorSetting color = new ColorSetting("Цвет", ColorUtils.rgba(255, 90, 90, 180));

    private final List<BlockPos> anomalies = new ArrayList<>();
    private long lastScanTime;

    public AnomalyEsp() {
        addSettings(range, color);
    }

    @Override
    public boolean onEvent(Event event) {
        if (mc.player == null || mc.world == null) {
            anomalies.clear();
            return false;
        }

        if (event instanceof EventUpdate) {
            updateCache();
        } else if (event instanceof EventRender render && render.isRender3D()) {
            updateCache();
            renderAnomalies();
        }
        return false;
    }

    private void updateCache() {
        long now = System.currentTimeMillis();
        if (now - lastScanTime < SCAN_DELAY_MS) {
            return;
        }

        anomalies.clear();
        BlockPos playerPos = mc.player.getPosition();
        int scanRange = Math.round(range.getValue().floatValue());

        for (int x = -scanRange; x <= scanRange; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -scanRange; z <= scanRange; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (isAnomalous(pos)) {
                        anomalies.add(pos.toImmutable());
                    }
                }
            }
        }

        lastScanTime = now;
    }

    private boolean isAnomalous(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        if (state.isAir() || !state.isSolid()) {
            return false;
        }

        if (state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.BARRIER) {
            return false;
        }

        BlockState below = mc.world.getBlockState(pos.down());
        if (!below.isAir()) {
            return false;
        }

        return mc.world.getBlockState(pos.up()).isAir();
    }

    private void renderAnomalies() {
        for (BlockPos pos : anomalies) {
            RenderUtils.Render3D.drawBlockBox(pos, color.get());
        }
    }
}
