package godweer.utils.input;

import godweer.ui.MenuPanel;
import godweer.utils.client.ClientSoundPlayer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import org.lwjgl.glfw.GLFW;

public final class ClickGuiController {

    private static boolean openRequested;

    private ClickGuiController() {
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!openRequested) {
                return;
            }

            openRequested = false;
            if (client.screen != null) {
                return;
            }

            ClientSoundPlayer.playSound("opengui.wav", 0.6, 1.0f);
            client.setScreen(new MenuPanel());
        });
    }

    public static void requestOpen() {
        openRequested = true;
    }

    public static boolean isOpenKey(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT;
    }
}
