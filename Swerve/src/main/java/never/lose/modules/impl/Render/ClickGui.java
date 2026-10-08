package never.lose.modules.impl.Render;

import net.minecraft.client.MinecraftClient;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.SliderSetting;
import never.lose.ui.ClickGuiScreen;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(
        name = "ClickGui",
        desc = "Client click GUI",
        category = ModuleCategory.RENDER,
        key = GLFW.GLFW_KEY_RIGHT_SHIFT
)
public class ClickGui extends Module {

    public final SliderSetting size = new SliderSetting("Size", 1.0, 0.7, 1.4, 0.05);

    @Override
    public void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.currentScreen instanceof ClickGuiScreen)) {
            ClickGuiScreen screen = ClickGuiScreen.get();
            screen.playOpenAnimation();
            client.setScreen(screen);
        }
        setEnabled(false);
    }
}
