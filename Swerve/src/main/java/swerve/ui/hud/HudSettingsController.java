package swerve.ui.hud;

import swerve.modules.Module;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.Minecraft;

// Отложенное открытие меню настроек HUD-элемента: заявка приходит из рендер-кадра
// (ПКМ по элементу), а смена экрана выполняется в клиентском тике — так же, как в
// ClickGuiController, чтобы не менять Minecraft.screen во время отрисовки.
public final class HudSettingsController {

    private static Module pending;
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

            client.setScreen(new HudSettingsScreen(module));
        });
    }

    public static void requestOpen(Module module) {
        if (module != null) {
            pending = module;
        }
    }
}
