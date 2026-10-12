package swerve.ui.hud;

import swerve.modules.Module;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

// Отложенное открытие меню настроек HUD-элемента: заявка приходит из рендер-кадра
// (ПКМ по элементу), а смена экрана выполняется в клиентском тике — так же, как в
// ClickGuiController, чтобы не менять Minecraft.screen во время отрисовки.
// Вместе с модулем передаётся прямоугольник элемента: меню появляется ровно в его точке.
public final class HudSettingsController {

    private static Module pending;
    private static float anchorX;
    private static float anchorY;
    private static float anchorWidth;
    private static float anchorHeight;
    private static boolean initialized;

    private HudSettingsController() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Module module = pending;
            if (module == null) {
                return;
            }
            pending = null;

            // Меню уже открыто — повторную заявку игнорируем,
            // иначе экран пересоздавался бы при каждом нажатии ПКМ поверх меню.
            if (client.screen instanceof HudSettingsScreen) {
                return;
            }

            client.setScreen(new HudSettingsScreen(module, anchorX, anchorY, anchorWidth, anchorHeight));
        });
    }

    public static void requestOpen(Module module, float anchorX, float anchorY,
                                   float anchorWidth, float anchorHeight) {
        if (module == null) {
            return;
        }
        pending = module;
        HudSettingsController.anchorX = anchorX;
        HudSettingsController.anchorY = anchorY;
        HudSettingsController.anchorWidth = anchorWidth;
        HudSettingsController.anchorHeight = anchorHeight;
    }
}
