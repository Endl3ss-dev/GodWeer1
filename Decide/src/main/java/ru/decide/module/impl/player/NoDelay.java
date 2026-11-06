/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1802
 *  net.minecraft.class_310
 *  net.minecraft.class_7923
 */
package ru.decide.module.impl.player;

import java.lang.reflect.Field;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_7923;
import ru.decide.manager.event_impl.EventUpdate;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="No Delay", category=Category.UTILITIES, desc="\u0423\u0431\u0438\u0440\u0430\u0435\u0442 \u0437\u0430\u0434\u0435\u0440\u0436\u043a\u0438")
@Environment(value=EnvType.CLIENT)
public class NoDelay
extends Module {
    public BooleanSetting jump = new BooleanSetting((Module)this, "\u041f\u0440\u044b\u0436\u043e\u043a", true);
    public BooleanSetting expBottle = new BooleanSetting((Module)this, "\u041f\u0443\u0437\u044b\u0440\u0451\u043a \u043e\u043f\u044b\u0442\u0430", true);

    public static NoDelay get() {
        return Instance.get(NoDelay.class);
    }

    @EventHandler
    private void onUpdate(EventUpdate event) {
        if (NoDelay.mc.field_1724 == null) {
            return;
        }
        if (this.expBottle.getValue().booleanValue() && NoDelay.mc.field_1724.method_6047().method_7909() == class_1802.field_8287) {
            NoDelay.mc.field_1724.method_7357().method_7900(class_7923.field_41178.method_10221((Object)class_1802.field_8287));
            this.resetCooldown();
        }
    }

    private void resetCooldown() {
        try {
            Field f = class_310.class.getDeclaredField("itemUseCooldown");
            f.setAccessible(true);
            f.setInt(mc, 0);
        }
        catch (Exception e) {
            try {
                Field f = class_310.class.getDeclaredField("field_1752");
                f.setAccessible(true);
                f.setInt(mc, 0);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

