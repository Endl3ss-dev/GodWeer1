package nuclear.module.impl.movement;

import net.minecraft.tags.FluidTags;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.utils.move.MoveUtil;

@ModuleDescriptor(name = "AutoJump", type = ModuleCategory.Movement, desc = "Автоматически прыгает")
public class AutoJump extends Module {
    private final BooleanSetting onlyMoving = new BooleanSetting("Только в движении", true);
    private final BooleanSetting ignoreLiquids = new BooleanSetting("Игнорировать воду", true);
    private final BooleanSetting respectJumpKey = new BooleanSetting("Не мешать прыжку", true);

    public AutoJump() {
        addSettings(onlyMoving, ignoreLiquids, respectJumpKey);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null || mc.world == null) {
            return false;
        }

        if (shouldJump()) {
            mc.player.jump();
        }
        return false;
    }

    private boolean shouldJump() {
        if (mc.player.abilities.isFlying || mc.player.isPassenger() || mc.player.isElytraFlying()) {
            return false;
        }
        if (!mc.player.isOnGround() || mc.player.isSneaking() || mc.player.isInLava()) {
            return false;
        }
        if (respectJumpKey.get() && mc.gameSettings.keyBindJump.isKeyDown()) {
            return false;
        }
        if (ignoreLiquids.get() && (mc.player.isInWater() || mc.player.areEyesInFluid(FluidTags.WATER))) {
            return false;
        }
        return !onlyMoving.get() || MoveUtil.isMoving();
    }
}
