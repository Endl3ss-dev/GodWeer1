package antileak.base.client.modules.impl.render;

import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.ColorSetting;
import antileak.base.client.modules.settings.implement.ModeSetting;

public class CustomModels extends Module {

    public static CustomModels INSTANCE = new CustomModels();

    public final BooleanSetting onlySelf = new BooleanSetting("OnlySelf", false);
    public final BooleanSetting friends = new BooleanSetting("Friends", false);
    public final ModeSetting mode = new ModeSetting("Модель", "Зайчик", "Зайчик");
    public final ColorSetting eyeColor = new ColorSetting("Глаза", "Цвет глаз");
    public final ColorSetting bodyColor = new ColorSetting("Тело", "Цвет тела");
    public final ColorSetting legsColor = new ColorSetting("Ноги", "Цвет ног");

    public CustomModels() {
        super("CustomModels", "Замена модели игрока", ModuleCategory.RENDER);
        eyeColor.setColor(-2009289807);
        bodyColor.setColor(-2009289807);
        legsColor.setColor(-2009289807);
        addSettings(onlySelf, friends, mode, eyeColor, bodyColor, legsColor);
    }
}
