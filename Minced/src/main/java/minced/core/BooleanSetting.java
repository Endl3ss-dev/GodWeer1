/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import minced.core.Setting;
import minced.core.Animation;
import minced.core.Easings;
import minced.enums.TOGGLE;

public class BooleanSetting
extends Setting {
    TOGGLE field7738 = TOGGLE.TOGGLE;
    int field7739 = -100;
    static long field7740 = 150L;
    Animation field7741;

    public TOGGLE method1901() {
        return this.field7738;
    }

    public void cfr_renamed_0(int param1) {
        this.field7739 = param1;
    }

    public BooleanSetting(String param1, boolean param2, Supplier param3) {
        super(param1, param2);
        this.field7741 = new Animation(Easings.field11969, param2 ? 1.0f : 0.0f, field7740);
        if (param3 != null) {
            this.visibleWhen(param3);
        }
    }

    public Animation method1902() {
        return this.field7741;
    }

    public int method1903() {
        return this.field7739;
    }

    public BooleanSetting(String param1, boolean param2) {
        this(param1, param2, null);
    }

    public void method1904(TOGGLE param1) {
        this.field7738 = param1;
    }
}

