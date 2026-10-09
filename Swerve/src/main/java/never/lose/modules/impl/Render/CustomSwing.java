package never.lose.modules.impl.Render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import never.lose.SwerveClient;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.modules.api.setting.ModeSetting;
import never.lose.modules.api.setting.SliderSetting;

@ModuleInfo(name = "CustomSwing", desc = "Кастомные анимации взмаха руки", category = ModuleCategory.RENDER)
public class CustomSwing extends Module {

    public static CustomSwing INSTANCE;

    public final ModeSetting mode = new ModeSetting("Режим", SwerveClient.SwingStyle.SMOOTH.displayName, SwerveClient.SwingStyle.names());
    public final SliderSetting strength = new SliderSetting("Сила", 1.0, 0.1, 3.0, 0.05);
    public final BooleanSetting alternate = new BooleanSetting("Чередовать направление", false);

    public CustomSwing() {
        INSTANCE = this;
    }

    public boolean applyTo(MatrixStack matrices, Arm arm, float swingProgress, float equipProgress) {
        int index = mode.getIndex();
        if (index < 0) {
            return false;
        }
        return SwerveClient.applySwing(matrices, arm, SwerveClient.SwingStyle.VALUES[index],
                swingProgress, equipProgress, strength.getFloatValue(), alternate.getValue());
    }
}
