package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Timer", category = Category.MOVEMENT, description = "Позволяет изменить скорость типов игры")
public class Timer extends Module {
    private final ModeSetting mode = new ModeSetting(this, "Режим", "Ванилла", "Грим");
    private final SliderSetting timerSpeed = new SliderSetting(this, "Скорость", 1.0F, 0.1F, 4.0F, 0.1F).setVisible(() -> mode.getValue().equals("Ванилла"));
    private final Minecraft mc = Minecraft.getInstance();
    private int tickCounter = 0;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        try {
            java.lang.reflect.Field timerField = Minecraft.class.getDeclaredField("timer");
            timerField.setAccessible(true);
            Object timer = timerField.get(mc);

            java.lang.reflect.Field speedField = timer.getClass().getDeclaredField("speed");
            speedField.setAccessible(true);

            if (mode.getValue().equals("Грим")) {
                tickCounter++;
                if (tickCounter == 1) {
                    speedField.setFloat(timer, 0.3F);
                } else if (tickCounter >= 1 && tickCounter <= 20) {
                    speedField.setFloat(timer, 1.6F);
                }
                if (tickCounter >= 7) {
                    tickCounter = 0;
                }
            } else {
                speedField.setFloat(timer, timerSpeed.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) return;

        try {
            java.lang.reflect.Field timerField = Minecraft.class.getDeclaredField("timer");
            timerField.setAccessible(true);
            Object timer = timerField.get(mc);

            java.lang.reflect.Field speedField = timer.getClass().getDeclaredField("speed");
            speedField.setAccessible(true);
            speedField.setFloat(timer, 1.0F);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}