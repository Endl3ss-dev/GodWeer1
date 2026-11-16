package net.skycore.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;

/**
 * Настройки самого меню (страница «Настройки»), портированные из Arcanic Visuals.
 *
 * <p>Повторяет {@code net.arcanic.visuals.core.MenuPrefs}: язык, звуки,
 * внешнее поведение, анимации, размер и позицию панели.
 *
 * <p>Адаптировано из {@code net.arcanic.visuals.core.MenuPrefs}
 * (Arcanic Visuals, GPL-3.0).
 */
public final class SkyMenuPrefs {

    private SkyMenuPrefs() {
    }

    /** Язык интерфейса. */
    public static final ModeSetting language =
            new ModeSetting("language", "Русский", "Русский", "English");

    /** Звуки меню. */
    public static final BooleanSetting sounds = new BooleanSetting("sounds", "Звуки меню", true);
    /** Громкость (показывается только если звуки включены). */
    public static final NumberSetting volume =
            new NumberSetting("volume", "Громкость", 70, 0, 100, 5, "%");
    /** Звук при наведении. */
    public static final BooleanSetting hoverSounds = new BooleanSetting("hover_sounds", "Звук при наведении", true);
    /** Щелчки ползунков. */
    public static final BooleanSetting sliderSounds = new BooleanSetting("slider_sounds", "Щелчки ползунков", true);

    /** Размытие мира за меню. */
    public static final BooleanSetting blur = new BooleanSetting("blur", "Размытие мира", true);
    /** Затемнение фона. */
    public static final NumberSetting dim = new NumberSetting("dim", "Затемнение", 40, 0, 90, 5, "%");
    /** Свет за курсором. */
    public static final BooleanSetting spotlight = new BooleanSetting("spotlight", "Свет за курсором", true);
    /** Волны от клика. */
    public static final BooleanSetting ripples = new BooleanSetting("ripples", "Волны от клика", true);
    /** Подсказки. */
    public static final BooleanSetting tips = new BooleanSetting("tips", "Подсказки", true);
    /** Уведомления биндов. */
    public static final BooleanSetting notify = new BooleanSetting("notify", "Уведомления биндов", true);

    /** Анимации. */
    public static final BooleanSetting animations = new BooleanSetting("animations", "Анимации", true);
    /** Скорость анимаций. */
    public static final NumberSetting animSpeed = new NumberSetting("anim_speed", "Скорость анимации", 1, 0.5, 2, 0.1, "×");
    /** Размер меню. */
    public static final NumberSetting scale = new NumberSetting("scale", "Размер меню", 100, 70, 130, 5, "%");
    /** Перетаскивание панели. */
    public static final BooleanSetting drag = new BooleanSetting("drag", "Перетаскивание", true);
    /** Помнить позицию. */
    public static final BooleanSetting remember = new BooleanSetting("remember", "Помнить позицию", true);

    /** Позиция панели (хранится здесь, применяется в ArcanicMenu). */
    public static double posX, posY;
    public static boolean compactSidebar;

    /** Все настройки меню для отображения в UI. */
    public static List<Setting<?>> allSettings() {
        List<Setting<?>> list = new ArrayList<>();
        list.add(language);
        list.add(sounds);
        list.add(volume);
        list.add(hoverSounds);
        list.add(sliderSounds);
        list.add(blur);
        list.add(dim);
        list.add(spotlight);
        list.add(ripples);
        list.add(tips);
        list.add(notify);
        list.add(animations);
        list.add(animSpeed);
        list.add(scale);
        list.add(drag);
        list.add(remember);
        return list;
    }

    /**
     * Синхронизирует настройки с состоянием клиента (аналог MenuPrefs.sync()).
     * Вызывается из ArcanicMenu при отрисовке страницы настроек.
     */
    public static void sync() {
        SkyPalette.animations = animations.get();
        SkyPalette.menuScale = scale.get() / 100.0;
    }

    /** Применяет настройки к текущему состоянию. */
    public static void apply() {
        sync();
    }

    /**
     * Модуль настроек меню (для регистрации в ModuleManager при необходимости).
     * tick() в ClientModule — final, поэтому синхронизация вызывается вручную
     * из ArcanicMenu, а не здесь.
     */
    public static ClientModule asModule() {
        return new ClientModule("menu", "Меню", ModuleCategory.MISC) {
            {
                addSettings(language, sounds, volume, hoverSounds, sliderSounds,
                        blur, dim, spotlight, ripples, tips, notify,
                        animations, animSpeed, scale, drag, remember);
            }
        };
    }
}
