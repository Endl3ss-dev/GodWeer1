package org.privates.client.managers.module.impl.movement;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.MoveUtil;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "DragonFly", description = "Ускоряет ваш полёт", category = Category.MOVEMENT)
public class DragonFly extends Module {
    public static DragonFly getInstance() {
        return Instance.get(DragonFly.class);
    }

    private final SliderSetting xzSpeed = new SliderSetting(this, "Скорость по X", 1, 0, 10, 0.1F);
    private final SliderSetting ySpeed = new SliderSetting(this, "Скорость по Y", 1, 0, 10, 0.1F);

    @EventHandler
    public void onEvent(UpdateEvent event) {
        if (mc.player.abilities.isFlying) {
            MoveUtil.setSpeed(xzSpeed.getValue());
            mc.player.getMotion().y = 0.0;
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.getMotion().y = 0.25;
                if (mc.player.moveForward == 0.0f && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown()) {
                    mc.player.getMotion().y = ySpeed.getValue();
                }
            }
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.getMotion().y = -0.25;
                if (mc.player.moveForward == 0.0f && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown()) {
                    mc.player.getMotion().y = -ySpeed.getValue();
                }
            }
        }
    }
}
