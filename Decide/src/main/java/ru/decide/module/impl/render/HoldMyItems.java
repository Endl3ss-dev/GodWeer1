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
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;

@ModuleInfo(name="Hold My Items", desc="\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0435 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u0440\u0443\u043a \u043e\u0442 \u043f\u0435\u0440\u0432\u043e\u0433\u043e \u043b\u0438\u0446\u0430", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class HoldMyItems
extends Module {
    private static HoldMyItems INSTANCE;
    public final BooleanSetting swapHands = new BooleanSetting((Module)this, "\u0421\u043c\u0435\u043d\u0430 \u0440\u0443\u043a", false);
    public final SliderSetting smoothness = new SliderSetting(this, "\u041f\u043b\u0430\u0432\u043d\u043e\u0441\u0442\u044c", 1.0f, 0.35f, 2.5f, 0.05f);
    public final SliderSetting mainHandX = new SliderSetting(this, "\u0413\u043b\u0430\u0432\u043d\u0430\u044f X", 0.0f, -1.0f, 1.0f, 0.05f);
    public final SliderSetting mainHandY = new SliderSetting(this, "\u0413\u043b\u0430\u0432\u043d\u0430\u044f Y", 0.0f, -1.0f, 1.0f, 0.05f);
    public final SliderSetting mainHandZ = new SliderSetting(this, "\u0413\u043b\u0430\u0432\u043d\u0430\u044f Z", 0.0f, -2.5f, 2.5f, 0.05f);
    public final SliderSetting offHandX = new SliderSetting(this, "\u0412\u0442\u043e\u0440\u0430\u044f X", 0.0f, -1.0f, 1.0f, 0.05f);
    public final SliderSetting offHandY = new SliderSetting(this, "\u0412\u0442\u043e\u0440\u0430\u044f Y", 0.0f, -1.0f, 1.0f, 0.05f);
    public final SliderSetting offHandZ = new SliderSetting(this, "\u0412\u0442\u043e\u0440\u0430\u044f Z", 0.0f, -2.5f, 2.5f, 0.05f);
    public final BooleanSetting climbAndCrawl = new BooleanSetting((Module)this, "\u041b\u0430\u0437\u0430\u043d\u0438\u0435 \u0438 \u043f\u043e\u043b\u0437\u0430\u043d\u0438\u0435", true);
    public final BooleanSetting swimmingAnimation = new BooleanSetting((Module)this, "\u041f\u043b\u0430\u0432\u0430\u043d\u0438\u0435", true);
    public final BooleanSetting mb3DCompat = new BooleanSetting((Module)this, "3D \u0421\u043e\u0432\u043c\u0435\u0441\u0442\u0438\u043c\u043e\u0441\u0442\u044c", false);
    public final ModeSetting animationType = new ModeSetting((Module)this, "\u0422\u0438\u043f \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438", "\u0428\u0430\u0440\u043f", "\u041e\u0431\u044b\u0447\u043d\u044b\u0439");

    public HoldMyItems() {
        INSTANCE = this;
    }

    public static HoldMyItems getInstance() {
        return INSTANCE;
    }

    public boolean isSharpAnimation() {
        return this.animationType.is("\u0428\u0430\u0440\u043f");
    }
}

