package tech.lightclient.modules.impl.render;

import java.nio.charset.StandardCharsets;

import tech.lightclient.LightClient;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeListSetting;

@FunctionAdd(name = "NoRender", alias = "No Render", category = Category.Render, description = "Убирает ненужные оверлеи")
public class NoRender extends Function {
    public static final String SCOREBOARD_NUMBERS_SETTING_NAME = "Цифры в скорборде";

    public static ModeListSetting noRenderElements = new ModeListSetting("Не отображать",
            new BooleanSetting("Огонь", true),
            new BooleanSetting("Плохие эффекты", true),
            new BooleanSetting("Оверлей блоков", true),
            new BooleanSetting("Погоду", false),
            new BooleanSetting(SCOREBOARD_NUMBERS_SETTING_NAME, true),
            new BooleanSetting("Тотем на экране", true),
            new BooleanSetting("Фон инвентаря", false)
    );

    public NoRender() {
        addSettings(noRenderElements);
    }

    public static boolean isEnabled(String name) {
        if (LightClient.getFunctionManager() == null || LightClient.getFunctionManager().getNoRender() == null) {
            return false;
        }

        if (!LightClient.getFunctionManager().getNoRender().isState()) {
            return false;
        }

        BooleanSetting setting = findSetting(name);
        return setting != null && setting.get();
    }

    private static BooleanSetting findSetting(String name) {
        if (noRenderElements == null || name == null || name.isBlank()) {
            return null;
        }

        BooleanSetting exact = noRenderElements.getByName(name);
        if (exact != null) {
            return exact;
        }

        String mojibake = new String(name.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        BooleanSetting encoded = noRenderElements.getByName(mojibake);
        if (encoded != null) {
            return encoded;
        }

        for (BooleanSetting setting : noRenderElements.get()) {
            if (setting == null || setting.getName() == null) {
                continue;
            }

            String settingName = setting.getName();
            if (settingName.equalsIgnoreCase(name) || settingName.equalsIgnoreCase(mojibake)) {
                return setting;
            }
        }

        return null;
    }
}
