package tech.lightclient.modules.impl.movement;

import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

@FunctionAdd(name = "WaterWalk", alias = "Water Walk", category = Category.Movement, description = "Автоматически зажимает прыжок под водой, отпуская у поверхности")
public class WaterWalk extends Function {

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (nullCheck()) return;
        if (!mc.player.isTouchingWater()) return;

        BlockPos feetPos = mc.player.getBlockPos();
        BlockPos checkPos = BlockPos.ofFloored(feetPos.getX(), feetPos.getY(), feetPos.getZ());
        boolean submerged = mc.world.getFluidState(checkPos).isIn(FluidTags.WATER);

        mc.options.jumpKey.setPressed(submerged);
    }

    @Override
    public void onDisable() {
        if (mc.options != null) {
            mc.options.jumpKey.setPressed(false);
        }
        super.onDisable();
    }
}
