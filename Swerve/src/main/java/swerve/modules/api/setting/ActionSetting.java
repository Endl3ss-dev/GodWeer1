package swerve.modules.api.setting;

import swerve.Swerve;

import java.util.function.Supplier;

/**
 * Кнопка в окне настроек модуля: строка, по нажатию на которую происходит действие,
 * а не значение, которое можно выбрать.
 *
 * <p>Нужна для тех возможностей, у которых нет устойчивого состояния: «скопировать
 * журнал», «сохранить отчёт», «очистить историю». Делать из этого {@code BooleanSetting}
 * нельзя — переключатель показывал бы «включено» после нажатия, а {@code ModeSetting} —
 * хранил бы выбор в конфиге, и при следующем запуске действие «выполнялось» само.
 *
 * <p>В {@code config.json} не попадает: {@link swerve.config.SettingCodec} знает про
 * этот тип и возвращает для него {@code null} — сохранять нечего.
 *
 * <p>Обработчик вызывается из потока, который обработал клик (то есть из игрового).
 * Долгое действие — обязанность обработчика: он должен уйти в фон сам, а не держать
 * игровой кадр.
 */
public class ActionSetting extends Setting {

    private final String label;

    private Runnable action;

    public ActionSetting(String name, String label) {
        super(name);
        this.label = label == null || label.isEmpty() ? name : label;
    }

    public ActionSetting(String name) {
        this(name, name);
    }

    /** Возвращает себя для цепочки: {@code new ActionSetting("…").action(this::copy)}. */
    public ActionSetting action(Runnable action) {
        this.action = action;
        return this;
    }

    /** Подпись на кнопке. */
    public String label() {
        return this.label;
    }

    /**
     * Выполняет действие. Повторное нажатие во время работы не запрещено: защита от
     * двойного запуска — задача самого действия (например, {@code AcdExporter}).
     */
    public void trigger() {
        Runnable current = this.action;
        if (current == null) {
            Swerve.LOGGER.warn("Настройка-кнопка '{}' без действия", name());
            return;
        }
        try {
            current.run();
        } catch (Exception exception) {
            // Кнопка живёт в окне настроек: наружу исключение уводить нельзя, иначе
            // клик роняет игру. Но и молча глотать его нельзя — иначе «не работает»
            // невозможно отличить от «не было чего копировать».
            Swerve.LOGGER.error("Действие '{}' завершилось ошибкой", name(), exception);
        }
    }

    public ActionSetting visible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
