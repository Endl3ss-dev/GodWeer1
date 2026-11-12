/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="No Render", desc="\u0421\u0443\u0445\u0430\u0440\u043a \u0441\u043e\u0442\u0440\u0451\u0442 \u0432\u0441\u044e \u0445\u0443\u0439\u043d\u044e \u0441 \u0432\u0430\u0448\u0435\u0433\u043e \u044d\u043a\u0440\u0430\u043d\u0430", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class NoRender
extends Module {
    public BooleanSetting ignoreFire = new BooleanSetting((Module)this, "\u0423\u0431\u0438\u0440\u0430\u0442\u044c \u043e\u0433\u043e\u043d\u044c", true);
    public BooleanSetting ignoreLava = new BooleanSetting((Module)this, "\u0423\u0431\u0438\u0440\u0430\u0442\u044c \u0442\u0443\u043c\u0430\u043d \u043b\u0430\u0432\u044b", true);
    public BooleanSetting ignoreZalupa = new BooleanSetting((Module)this, "\u0423\u0431\u0438\u0440\u0430\u0442\u044c \u043f\u043b\u043e\u0445\u0438\u0435 \u044d\u0444\u0444\u0435\u043a\u0442\u044b", true);
    public BooleanSetting ignoreScoreboard = new BooleanSetting((Module)this, "\u0423\u0431\u0438\u0440\u0430\u0442\u044c \u0421\u043a\u043e\u0440\u0431\u043e\u0440\u0442", false);
    public BooleanSetting ignoreBossBar = new BooleanSetting((Module)this, "\u0423\u0431\u0438\u0440\u0430\u0442\u044c \u0411\u043e\u0441\u0441 \u0431\u0430\u0440", false);
    public BooleanSetting noCameraClip = new BooleanSetting((Module)this, "\u041a\u0430\u043c\u0435\u0440\u0430 \u0441\u043a\u0432\u043e\u0437\u044c \u0431\u043b\u043e\u043a\u0438", false);
    public BooleanSetting ignoreTotemPop = new BooleanSetting((Module)this, "\u0423\u0431\u0438\u0440\u0430\u0442\u044c \u0442\u043e\u0442\u0435\u043c \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435", true);
    public BooleanSetting removeCamreZalupa = new BooleanSetting((Module)this, "\u0423\u0431\u0438\u0440\u0430\u0442\u044c \u0434\u0435\u0440\u0433\u0430\u043d\u0435\u0435 \u043a\u0430\u043c\u0435\u0440\u044b", true);

    public static NoRender getInstance() {
        return Instance.get(NoRender.class);
    }
}

