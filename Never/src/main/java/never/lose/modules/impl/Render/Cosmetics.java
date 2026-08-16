package never.lose.modules.impl.Render;

import net.minecraft.client.MinecraftClient;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.ui.CosmeticsScreen;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(
        name = "Cosmetics",
        desc = "Галерея аватаров Figura",
        category = ModuleCategory.RENDER,
        key = GLFW.GLFW_KEY_K
)
public class Cosmetics extends Module {

    @Override
    public void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!(client.currentScreen instanceof CosmeticsScreen)) {
            CosmeticsScreen screen = CosmeticsScreen.get();
            screen.playOpenAnimation();
            client.setScreen(screen);
        }

        setEnabled(false);
    }
}
