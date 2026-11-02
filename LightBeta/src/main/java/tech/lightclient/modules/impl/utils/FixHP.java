package tech.lightclient.modules.impl.utils;

import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.ModeSetting;

@FunctionAdd(name = "FixHP", alias = "Fix HP", category = Category.Utilities, description = "Фикс хп для серверов с рандомными хп")
public class FixHP extends Function {
    public static ModeSetting mode = new ModeSetting("Режим", "FunTime", "FunTime", "ReallyWorld");

    public FixHP() {
        addSettings(mode);
    }
}
