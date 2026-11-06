/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.movement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.manager.event_impl.EventUpdate;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Sprint", desc="\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u0441\u043f\u0440\u0438\u043d\u0442", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class Sprint
extends Module {
    public int tick;

    public static Sprint get() {
        return Instance.get(Sprint.class);
    }

    @EventHandler
    public void onEvent(WorldLoadEvent e) {
        this.tick += 4;
    }

    @EventHandler
    public void onEvent(EventUpdate eventUpdate) {
        boolean canSprint;
        if (Sprint.mc.field_1724 == null || Sprint.mc.field_1687 == null) {
            return;
        }
        if (!Sprint.mc.field_1724.method_5805()) {
            this.tick += 2;
        }
        if (this.tick != 0) {
            Sprint.mc.field_1724.method_5728(false);
            Sprint.mc.field_1690.field_1867.method_23481(false);
            --this.tick;
            return;
        }
        boolean horizontal = Sprint.mc.field_1724.field_5976 && !Sprint.mc.field_1724.field_34927;
        boolean sneaking = Sprint.mc.field_1724.method_5715() && !Sprint.mc.field_1724.method_5681();
        boolean bl = canSprint = !horizontal && Sprint.mc.field_1724.field_6250 > 0.0f;
        if (sneaking) {
            return;
        }
        Sprint.mc.field_1690.field_1867.method_23481(true);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (Sprint.mc.field_1724 != null) {
            Sprint.mc.field_1724.method_5728(false);
        }
    }
}

