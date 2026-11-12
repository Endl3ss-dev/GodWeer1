package tech.lightclient.modules.impl.render;

import org.lwjgl.glfw.GLFW;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;

/**
 * Модуль "HoldMyItems" — анимации живых рук от первого лица (порт мода Hold My Items).
 *
 * Сам движок живёт в пакете {@link tech.lightclient.hmi} (Lua-скрипты, миксины,
 * частицы). Этот модуль — пользовательская точка управления: включение/выключение,
 * смещения рук и режим анимации. Статические геттеры ниже читаются каждый кадр из
 * {@link tech.lightclient.hmi.resource_controller.LuaScriptCache} и миксинов.
 */
@FunctionAdd(name = "HoldMyItems", alias = "Hold My Items", description = "Анимации живых рук от первого лица (Hold My Items)", category = Category.Render)
public class HoldMyItems extends Function {

    public static HoldMyItems INSTANCE;

    /**
     * Флаг «последний удар был критическим». Выставляется из
     * {@link tech.lightclient.mixins.hmi.MinecraftClientMixin} в момент атаки (HEAD doAttack),
     * где ещё виден полный кулдаун и цель, и читается Lua-скриптом через {@link #getCrit()}.
     */
    private static volatile boolean lastAttackCrit = false;

    private final ModeSetting animationType = new ModeSetting("Тип анимации", "Дефолт", "Дефолт", "Новый");

    private final SliderSetting mainX = new SliderSetting("Основная рука X", 0.0f, -1.0f, 1.0f, 0.01f);
    private final SliderSetting mainY = new SliderSetting("Основная рука Y", 0.0f, -1.0f, 1.0f, 0.01f);
    private final SliderSetting mainZ = new SliderSetting("Основная рука Z", 0.0f, -1.0f, 1.0f, 0.01f);
    private final SliderSetting offX = new SliderSetting("Второстепенная рука X", 0.0f, -1.0f, 1.0f, 0.01f);
    private final SliderSetting offY = new SliderSetting("Второстепенная рука Y", 0.0f, -1.0f, 1.0f, 0.01f);
    private final SliderSetting offZ = new SliderSetting("Второстепенная рука Z", 0.0f, -1.0f, 1.0f, 0.01f);

    private final BindSetting inspectKey = new BindSetting("Клавиша осмотра", GLFW.GLFW_KEY_C);

    public HoldMyItems() {
        INSTANCE = this;
        addSettings(animationType, mainX, mainY, mainZ, offX, offY, offZ, inspectKey);
    }

    public static boolean isEnabled() {
        return INSTANCE != null && INSTANCE.isState();
    }

    public static int getSwingStyle() {
        return 0;
    }

    /**
     * Режим анимации для Lua-скрипта: 0 — «Дефолт» (ванильный порт HMI),
     * 1 — «Новый» (проработанная боевая анимация ударов и критов).
     */
    public static int getAnimationMode() {
        return (INSTANCE != null && INSTANCE.animationType.is("Новый")) ? 1 : 0;
    }

    /** Сообщить движку, что только что выполненная атака была критом (вызывается из миксина). */
    public static void notifyAttackCrit(boolean crit) {
        lastAttackCrit = crit;
    }

    /** Сила крита для Lua (0.0 — обычный удар, 1.0 — крит). */
    public static double getCrit() {
        return lastAttackCrit ? 1.0 : 0.0;
    }

    public static double getMainX() {
        return INSTANCE == null ? 0.0 : INSTANCE.mainX.get();
    }

    public static double getMainY() {
        return INSTANCE == null ? 0.0 : INSTANCE.mainY.get();
    }

    public static double getMainZ() {
        return INSTANCE == null ? 0.0 : INSTANCE.mainZ.get();
    }

    public static double getOffX() {
        return INSTANCE == null ? 0.0 : INSTANCE.offX.get();
    }

    public static double getOffY() {
        return INSTANCE == null ? 0.0 : INSTANCE.offY.get();
    }

    public static double getOffZ() {
        return INSTANCE == null ? 0.0 : INSTANCE.offZ.get();
    }

    public static int getInspectKey() {
        return INSTANCE == null ? GLFW.GLFW_KEY_C : INSTANCE.inspectKey.get();
    }
}
