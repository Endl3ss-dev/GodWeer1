/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.EventSprint;
import minced.core.Module;
import minced.enums.Combat;

public class Sprint
extends Module {
    static String field1521 = "Sprint";
    static String field1522 = "Автоматически активирует режим бега, когда игрок начинает двигаться";

    @EventHandler(2)
    private void method517(EventSprint param1) {
        param1.method2298(true);
    }

    public Sprint() {
        super(field1521, field1522, Combat.Movement);
    }
}

