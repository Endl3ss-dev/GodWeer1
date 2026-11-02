/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.Client;
import ru.decide.manager.event_impl.TextFactoryEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;

@ModuleInfo(name="Name Protect", category=Category.UTILITIES, desc="\u0421\u043a\u0440\u044b\u0432\u0430\u0435\u0442 \u0432\u0430\u0448 \u043d\u0438\u043a\u043d\u0435\u0439\u043c")
@Environment(value=EnvType.CLIENT)
public class NameProtect
extends Module {
    public BooleanSetting friends = new BooleanSetting((Module)this, "\u0421\u043a\u0440\u044b\u0432\u0430\u0442\u044c \u0434\u0440\u0443\u0437\u0435\u0439", true);
    public BooleanSetting anarhy = new BooleanSetting((Module)this, "\u0410\u043d\u0430\u0440\u0445\u0438\u044e", false);

    @EventHandler
    public void onEvent(TextFactoryEvent e) {
        if (this.anarhy.getValue().booleanValue()) {
            e.replaceRegex("(?ui)\u0410\u043d\u0430\u0440\u0445\u0438\u044f-(?:(?:1\\d{3})|(?:[1-9]\\d{0,2})|2000)", "decidevisuals.fun");
        }
        e.replaceText(mc.method_1548().method_1676(), "decidevisuals");
        e.replaceRegex("funtime", "\u0423\u0441\u043f\u0435\u0448\u043d\u044b\u0439 \u043f\u0440\u043e\u0435\u043a\u0442");
        e.replaceRegex("FunTime.su", "\u0423\u0441\u043f\u0435\u0448\u043d\u044b\u0439 \u043f\u0440\u043e\u0435\u043a\u0442");
        e.replaceRegex("\u0410\u043d\u0430\u0440\u0445\u0438\u044f", "HvH");
        if (this.friends.getValue().booleanValue()) {
            this.replaceFriendNames(e);
        }
    }

    private void replaceFriendNames(TextFactoryEvent e) {
        Client.get().friendManager().getFriends().forEach(friend -> e.replaceText((String)friend, "Friend"));
    }
}

