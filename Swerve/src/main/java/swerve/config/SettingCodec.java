package swerve.config;

import swerve.modules.api.setting.BindSetting;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ListSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.modules.api.setting.Setting;
import swerve.modules.api.setting.TextSetting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.List;

/**
 * Перевод значения настройки в JSON и обратно.
 *
 * <p>Формат намеренно «плоский»: примитив для скалярных настроек и объект
 * {@code {опция: bool}} для списков. За счёт этого файл конфига читается глазами
 * и правится руками:
 *
 * <pre>
 * "Дистанция": 3.0,
 * "Цель": "Все",
 * "Учитывать": {"Игроки": true, "Мобы": false}
 * </pre>
 *
 * <p>Декодирование никогда не бросает исключений и никогда не портит настройку:
 * значение неверного типа просто игнорируется, а вышедшее за границы число
 * обрезается сеттером самой настройки ({@link FloatSetting#setValue}),
 * неизвестный режим отбрасывается {@link ModeSetting#set}, длинный текст
 * обрезается {@link TextSetting#setText}. Поэтому старый конфиг от другой версии
 * клиента не может сломать запуск.
 */
public final class SettingCodec {

    private SettingCodec() {
    }

    /**
     * Значение настройки в виде JSON-элемента.
     *
     * @return элемент либо {@code null}, если тип настройки неизвестен
     *         (новая настройка без поддержки кодека не попадёт в файл и не сломает сохранение)
     */
    public static JsonElement encode(Setting setting) {
        if (setting instanceof BooleanSetting bool) {
            return new JsonPrimitive(bool.get());
        }
        if (setting instanceof FloatSetting number) {
            return new JsonPrimitive(number.get());
        }
        if (setting instanceof ModeSetting mode) {
            return new JsonPrimitive(mode.getCurrent());
        }
        if (setting instanceof TextSetting text) {
            return new JsonPrimitive(text.get());
        }
        if (setting instanceof BindSetting bind) {
            return new JsonPrimitive(bind.getKey());
        }
        if (setting instanceof ListSetting list) {
            JsonObject options = new JsonObject();
            List<BooleanSetting> entries = list.getSettings();
            for (int i = 0, size = entries.size(); i < size; i++) {
                BooleanSetting option = entries.get(i);
                options.addProperty(option.name(), option.get());
            }
            return options;
        }
        return null;
    }

    /** Применяет значение из конфига к настройке; неверные данные игнорируются. */
    public static void decode(Setting setting, JsonElement value) {
        if (setting == null || value == null || value.isJsonNull()) {
            return;
        }

        if (setting instanceof BooleanSetting bool) {
            if (isBoolean(value)) {
                bool.setState(value.getAsBoolean());
            }
            return;
        }
        if (setting instanceof FloatSetting number) {
            if (isNumber(value)) {
                number.setValue(value.getAsFloat());
            }
            return;
        }
        if (setting instanceof ModeSetting mode) {
            if (isString(value)) {
                mode.set(value.getAsString());
            }
            return;
        }
        if (setting instanceof TextSetting text) {
            if (isString(value)) {
                text.setText(value.getAsString());
            }
            return;
        }
        if (setting instanceof BindSetting bind) {
            if (isNumber(value)) {
                bind.setKey(value.getAsInt());
            }
            return;
        }
        if (setting instanceof ListSetting list) {
            if (value.isJsonObject()) {
                decodeList(list, value.getAsJsonObject());
            }
        }
    }

    private static void decodeList(ListSetting list, JsonObject values) {
        List<BooleanSetting> entries = list.getSettings();
        for (int i = 0, size = entries.size(); i < size; i++) {
            BooleanSetting option = entries.get(i);
            JsonElement stored = values.get(option.name());
            if (stored != null && isBoolean(stored)) {
                option.setState(stored.getAsBoolean());
            }
        }
    }

    private static boolean isBoolean(JsonElement value) {
        return value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean();
    }

    private static boolean isNumber(JsonElement value) {
        return value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber();
    }

    private static boolean isString(JsonElement value) {
        return value.isJsonPrimitive() && value.getAsJsonPrimitive().isString();
    }

    /**
     * Ключ настройки в файле. Имя, а не индекс: вставка новой настройки в середину
     * списка не сдвигает сохранённые значения. Одинаковые имена внутри одного модуля
     * получат одно значение — таких модулей в клиенте нет (проверяет
     * {@code tests/check_config.py}).
     */
    static String keyOf(Setting setting) {
        return setting.name();
    }
}
