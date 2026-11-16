/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1294
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1294;
import ru.decide.manager.event_impl.EventUpdate;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;

@ModuleInfo(name="Gamma", desc="\u041f\u043e\u043b\u043d\u0430\u044f \u044f\u0440\u043a\u043e\u0441\u0442\u044c (\u0444\u0443\u043b\u043b\u0431\u0440\u0430\u0439\u0442)", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class Gamma
extends Module {
    @Override
    public void onEnable() {
        super.onEnable();
        if (Gamma.mc.field_1769 != null) {
            Gamma.mc.field_1769.method_3279();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (Gamma.mc.field_1769 != null) {
            Gamma.mc.field_1769.method_3279();
        }
    }

    @EventHandler
    public void onUpdate(EventUpdate e) {
        if (Gamma.mc.field_1724 == null) {
            return;
        }
        Gamma.mc.field_1724.method_6016(class_1294.field_5925);
    }
}

