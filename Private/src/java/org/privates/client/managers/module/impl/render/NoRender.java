package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.potion.Effects;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.MoveInputEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.utils.other.Instance;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "NoRender", category = Category.RENDER, description = "Выключает не нужный рендер игры")
public class NoRender extends Module {
    public static NoRender getInstance() {
        return Instance.get(NoRender.class);
    }

    private final MultiBooleanSetting elements = new MultiBooleanSetting(this, "Элементы",
            BooleanSetting.of("Огонь", true),
            BooleanSetting.of("Тряска", true),
            BooleanSetting.of("Боссбар", false),
            BooleanSetting.of("Плохие эффекты", true),
            BooleanSetting.of("Скорборд", false),
            BooleanSetting.of("Анимация тотема", false)
    );

    @EventHandler
    public void onMoveInput(MoveInputEvent event) {
        if (mc.player == null) return;

        if (mc.player.isPotionActive(Effects.BLINDNESS)) {
            mc.gameSettings.keyBindSprint.setPressed(false);
            mc.player.setSprinting(false);
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;

        if (mc.player.isPotionActive(Effects.BLINDNESS)) {
            mc.player.setSprinting(false);
            mc.player.sprintingTicksLeft = 0;
        }
    }
}