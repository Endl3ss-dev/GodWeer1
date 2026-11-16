package net.skycore.client.gui;

import net.minecraft.client.Minecraft;
import net.skycore.client.module.ClientModule;
import org.lwjgl.glfw.GLFW;

/**
 * Бинды клавиш модулей: метки клавиш для чипов и переключение модулей при нажатии клавиши в игре.
 *
 * <p>Повторяет {@code net.arcanic.visuals.core.Keys} из Arcanic Visuals,
 * адаптировано под инфраструктуру SkyCore 26.2 (использует {@link net.skycore.client.module.KeyBinds}
 * и {@link net.skycore.client.module.ModuleManager} вместо собственных реализаций).
 *
 * <p>Адаптировано из {@code net.arcanic.visuals.core.Keys}
 * (Arcanic Visuals, GPL-3.0).
 */
public final class SkyKeys {

    private SkyKeys() {
    }

    public static final int MOUSE = 1000;

    /**
     * Возвращает читаемую метку клавиши для чипа в меню.
     *
     * @param code код клавиши (GLFW или MOUSE_BASE + номер мыши)
     * @return строка метки, например "Space", "F5", "M2", "—" если не привязано
     */
    public static String label(int code) {
        if (code < 0) return "—";
        if (code >= MOUSE)
            return switch (code - MOUSE) {
                case 2 -> "СКМ";
                default -> "M" + (code - MOUSE + 1);
            };
        return net.skycore.client.module.KeyBinds.name(code);
    }

    /**
     * Обрабатывает нажатие клавиши: переключает модули с соответствующим биндом.
     *
     * @param code код нажатой клавиши
     */
    public static void pressed(int code) {
        Minecraft mc = Minecraft.getInstance();
        if (code < 0 || mc.gui.screen() != null || mc.player == null) return;
        var mm = net.skycore.client.SkycoreClient.modules();
        for (ClientModule m : mm.getModules()) {
            if (m.getKeyBind() != code) continue;
            m.toggle();
            net.skycore.client.SkycoreClient.config().save();
        }
    }
}
