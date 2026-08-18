package nuclear.module.impl.movement;

import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.utils.move.MoveUtil;

@ModuleDescriptor(name = "GrimGlide", type = ModuleCategory.Movement, desc = "Ускорение на элитре без фееров")
public class GrimGlide extends Module {
    private final SliderSetting glideSpeed = new SliderSetting("Скорость", 1.05f, 0.2f, 2.5f, 0.05f);

    public GrimGlide() {
        addSettings(glideSpeed);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null || mc.world == null) {
            return false;
        }
        if (!mc.player.isElytraFlying()) {
            return false;
        }

        if (MoveUtil.isMoving()) {
            MoveUtil.setMotion(glideSpeed.getValue().doubleValue());
        }

        double verticalSpeed = -0.01;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            verticalSpeed = 0.06;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            verticalSpeed = -0.08;
        }

        mc.player.setMotion(mc.player.getMotion().x, verticalSpeed, mc.player.getMotion().z);
        return false;
    }
}
