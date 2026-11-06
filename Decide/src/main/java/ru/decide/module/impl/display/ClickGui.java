/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_437
 */
package ru.decide.module.impl.display;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_437;
import ru.decide.Client;
import ru.decide.manager.event_impl.EventKey;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.MultiBooleanSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Click Gui", category=Category.HUD, key=344, desc="\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u043d\u0430\u0441\u0442\u0440\u043e\u0438\u0442\u044c \u0432\u0438\u0434 GUI \u043a\u043b\u0438\u0435\u043d\u0442\u0430", autoEnabled=true, allowDisable=false)
@Environment(value=EnvType.CLIENT)
public class ClickGui
extends Module {
    public MultiBooleanSetting effect = new MultiBooleanSetting((Module)this, "\u042d\u0444\u0444\u0435\u043a\u0442\u044b", new BooleanSetting("\u0421\u0435\u0440\u044b\u0439 \u0444\u043e\u043d", false), new BooleanSetting("\u0417\u0430\u0442\u0435\u043c\u043d\u044f\u0442\u044c \u0444\u043e\u043d", true), new BooleanSetting("\u0420\u0430\u0437\u043c\u044b\u0432\u0430\u0442\u044c \u0444\u043e\u043d", true), new BooleanSetting("\u0428\u0435\u0439\u0434\u0435\u0440", false), new BooleanSetting("\u0427\u0430\u0441\u0442\u0438\u0446\u044b", true), new BooleanSetting("\u0421\u043a\u0430\u043d \u043b\u0438\u043d\u0438\u0438", true), new BooleanSetting("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", true), new BooleanSetting("\u0422\u043e\u0447\u043a\u0438", true));
    public ModeSetting closeAnimation = new ModeSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0437\u0430\u043a\u0440\u044b\u0442\u0438\u044f", "Default", "3D", "Shatter");
    public SliderSetting size = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440", 1.0f, 0.5f, 1.5f, 0.1f);
    public BooleanSetting runWithGui = new BooleanSetting((Module)this, "\u0411\u0435\u0433\u0430\u0442\u044c \u0441 \u043e\u0442\u043a\u0440\u044b\u0442\u044b\u043c GUI", false);

    public static ClickGui getInstance() {
        return Instance.get(ClickGui.class);
    }

    @EventHandler
    public void onKey(EventKey event) {
        if (event.getKey() == this.getKey()) {
            mc.method_1507((class_437)Client.get.clickGuiScreen());
        }
    }
}

