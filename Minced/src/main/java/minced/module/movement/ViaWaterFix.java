/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.module.movement;
import java.util.function.BooleanSupplier;
import minced.core.Module;
import minced.enums.Combat;

public class ViaWaterFix
extends Module {
    static String field6061 = "Via Water Fix";
    static String field6062 = "Совместимость воды для ViaFabricPlus";

    public ViaWaterFix() {
        super(field6061, field6062, Combat.Movement);
    }
}

