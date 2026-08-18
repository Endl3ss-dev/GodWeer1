package nuclear.module.impl.render;

import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ModuleDescriptor(name = "ChorusTpEsp", type = ModuleCategory.Render, desc = "Показывает блоки на которые можно телепортироваться хорусом")
public class ChorusTpEsp extends Module {
    private static final int MAX_POSITIONS = 24;

    private final SliderSetting range = new SliderSetting("Дистанция", 8.0f, 4.0f, 16.0f, 1.0f);
    private final BooleanSetting onlyWithChorus = new BooleanSetting("Только с хорусом", true);
    private final ColorSetting color = new ColorSetting("Цвет", ColorUtils.rgba(170, 120, 255, 180));

    private final List<BlockPos> teleportPositions = new ArrayList<>();

    public ChorusTpEsp() {
        addSettings(range, onlyWithChorus, color);
    }

    @Override
    public boolean onEvent(Event event) {
        if (mc.player == null || mc.world == null) {
            teleportPositions.clear();
            return false;
        }

        if (event instanceof EventUpdate) {
            updatePositions();
        } else if (event instanceof EventRender render && render.isRender3D()) {
            updatePositions();
            renderPositions();
        }
        return false;
    }

    private void updatePositions() {
        if (onlyWithChorus.get()
            && mc.player.getHeldItemMainhand().getItem() != Items.CHORUS_FRUIT
            && mc.player.getHeldItemOffhand().getItem() != Items.CHORUS_FRUIT) {
            teleportPositions.clear();
            return;
        }

        teleportPositions.clear();
        BlockPos base = mc.player.getPosition();
        int scanRange = Math.round(range.getValue().floatValue());

        for (int x = -scanRange; x <= scanRange; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -scanRange; z <= scanRange; z++) {
                    BlockPos pos = base.add(x, y, z);
                    if (canTeleportTo(pos)) {
                        teleportPositions.add(pos.toImmutable());
                    }
                }
            }
        }

        teleportPositions.sort(Comparator.comparingDouble(base::distanceSq));
        if (teleportPositions.size() > MAX_POSITIONS) {
            teleportPositions.subList(MAX_POSITIONS, teleportPositions.size()).clear();
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        BlockState above = mc.world.getBlockState(pos.up());
        BlockState below = mc.world.getBlockState(pos.down());

        return state.isAir() && above.isAir() && below.isSolid();
    }

    private void renderPositions() {
        for (BlockPos pos : teleportPositions) {
            RenderUtils.Render3D.drawBlockBox(pos, color.get());
        }
    }
}
