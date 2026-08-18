package nuclear.module.impl.movement;

import net.minecraft.potion.Effects;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.SliderSetting;

@ModuleDescriptor(name = "LevitationControl", type = ModuleCategory.Movement, desc = "Контроль левитации")
public class LevitationControl extends Module {
    private final BooleanSetting hover = new BooleanSetting("Зависать", true);
    private final SliderSetting upSpeed = new SliderSetting("Скорость вверх", 0.2f, 0.05f, 1.0f, 0.05f);
    private final SliderSetting downSpeed = new SliderSetting("Скорость вниз", 0.2f, 0.05f, 1.0f, 0.05f);

    public LevitationControl() {
        addSettings(hover, upSpeed, downSpeed);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null || mc.world == null) {
            return false;
        }
        if (!mc.player.isPotionActive(Effects.LEVITATION)) {
            return false;
        }

        double targetVerticalSpeed = mc.player.getMotion().y;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            targetVerticalSpeed = upSpeed.getValue().doubleValue();
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            targetVerticalSpeed = -downSpeed.getValue().doubleValue();
        } else if (hover.get()) {
            targetVerticalSpeed = 0.0;
        } else {
            return false;
        }

        if (Math.abs(mc.player.getMotion().y - targetVerticalSpeed) > 1.0E-3) {
            mc.player.setMotion(mc.player.getMotion().x, targetVerticalSpeed, mc.player.getMotion().z);
        }
        return false;
    }
}
