package swerve.modules.impl.misc;

import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.ui.script.ScriptScreen;

import net.minecraft.client.Minecraft;

/**
 * Вход в редактор скриптов.
 *
 * <p>Модуль — не «включалка фичи», а кнопка открытия окна: включение
 * показывает редактор, закрытие окна выключает модуль обратно. Поэтому
 * {@link #setEnabledQuiet(boolean)} намеренно игнорирует включение из конфига:
 * иначе при каждом запуске игры поверх главного меню открывался бы редактор.
 */
public final class Scripts extends Module {

    public Scripts() {
        super("Scripts", "Блочный редактор функций в стиле Scratch", ModuleCategory.MISC);
    }

    @Override
    public void onEnable() {
        Minecraft mc = Minecraft.getInstance();
        // Экран ставится в очередь клиента: onEnable может прийти из обработчика
        // клавиши, и подмена экрана посреди ввода ломает обработку нажатия.
        mc.execute(() -> mc.setScreen(new ScriptScreen()));
    }

    @Override
    public void setEnabledQuiet(boolean state) {
        if (!state) {
            super.setEnabledQuiet(false);
        }
    }

    @Override
    public boolean notifiesOnToggle() {
        return false;
    }
}
