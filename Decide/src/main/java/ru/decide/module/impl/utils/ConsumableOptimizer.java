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
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.impl.utils.consumable.ConsumableHandler;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Consumable Optimizer", category=Category.UTILITIES, desc="\u041e\u043f\u0442\u0438\u043c\u0438\u0437\u0438\u0440\u0443\u0435\u0442 \u043f\u043e\u0442\u0440\u0435\u0431\u043b\u0435\u043d\u0438\u0435 \u0435\u0434\u044b \u0438 \u0437\u0435\u043b\u0438\u0439")
@Environment(value=EnvType.CLIENT)
public class ConsumableOptimizer
extends Module {
    public BooleanSetting fastConsume = new BooleanSetting((Module)this, "\u0411\u044b\u0441\u0442\u0440\u043e\u0435 \u043f\u043e\u0442\u0440\u0435\u0431\u043b\u0435\u043d\u0438\u0435", true);
    public BooleanSetting soundFix = new BooleanSetting((Module)this, "\u0418\u0441\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0437\u0432\u0443\u043a\u0430", true);
    public BooleanSetting animationSkip = new BooleanSetting((Module)this, "\u041f\u0440\u043e\u043f\u0443\u0441\u043a \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438", true);

    public static ConsumableOptimizer get() {
        return Instance.get(ConsumableOptimizer.class);
    }

    @Override
    public void onEnable() {
        ConsumableHandler.STATE.startServerWait();
    }

    @Override
    public void onDisable() {
        ConsumableHandler.STATE.stopServerWait();
        ConsumableHandler.STATE.resetEquipmentAnimation();
        ConsumableHandler.STATE.resetBurpSuppression();
    }
}

