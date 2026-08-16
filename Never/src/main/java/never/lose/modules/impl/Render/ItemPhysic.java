package never.lose.modules.impl.Render;

import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.SliderSetting;

@ModuleInfo(name = "ItemPhysic", desc = "Физика для упавших предметов", category = ModuleCategory.RENDER)
public class ItemPhysic extends Module {

    public final SliderSetting rotateSpeed = new SliderSetting("Скорость вращения", 1.0, 0.1, 3.0, 0.1);
}
