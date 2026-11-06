package tech.lightclient.modules.impl.player;

import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.SliderSetting;

@FunctionAdd(name = "ElytraMotion", alias = "Elytra Motion", category = Category.Movement, description = "Настройка дистанции движения на элитре")
public class ElytraMotion extends Function {
    public static SliderSetting elytraMotionDistance = new SliderSetting("Дистанция", 2, 1, 3, 0.1f);

    public ElytraMotion() {
        addSettings(elytraMotionDistance);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
