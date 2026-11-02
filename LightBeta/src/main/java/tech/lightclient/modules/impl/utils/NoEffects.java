package tech.lightclient.modules.impl.utils;

import tech.lightclient.client.events.impl.client.OptimizedUpdateEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeListSetting;
import net.minecraft.entity.effect.StatusEffects;

@FunctionAdd(name = "NoEffects", alias = "No Effects", category = Category.Utilities, description = "Убирает выбранные эффекты")
public class NoEffects extends Function {
    public ModeListSetting modeListSetting = new ModeListSetting("Убирать",
            new BooleanSetting("Ночное зрение", false),
            new BooleanSetting("Тьма", false),
            new BooleanSetting("Свечение", false));

    public NoEffects() {
        addSettings(modeListSetting);
    }

    @EventHandler
    public void onUpdate(OptimizedUpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (modeListSetting.getByName("Ночное зрение").get()) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
        if (modeListSetting.getByName("Тьма").get()) {
            mc.player.removeStatusEffect(StatusEffects.DARKNESS);
        }
    }
}