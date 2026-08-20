/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.module.combat;
import minced.core.EventHandler;

import minced.core.EventEntityRayTrace;
import minced.core.Module;
import minced.enums.Combat;

public class NoEntityTrace
extends Module {
    static String field1418 = "No Entity Trace";
    static String field1419 = "Отключает взаимодействие с сущностями";

    public NoEntityTrace() {
        super(field1418, field1419, Combat.Combat);
    }

    @EventHandler(2)
    public void method494(EventEntityRayTrace param1) {
        param1.method2298(true);
    }
}

