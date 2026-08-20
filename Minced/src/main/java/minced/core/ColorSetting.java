/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.function.Supplier;
import minced.core.Setting;

public class ColorSetting
extends Setting {
    public int field7598;
    boolean field7599;

    public ColorSetting(String param1, boolean param2, Integer param3, Supplier param4) {
        super(param1, param3);
        this.field7598 = param3;
        this.field7599 = param2;
        this.visibleWhen(param4);
    }

    public float method1861() {
        return (Integer)this.getValue() >> 24 & 0xFF;
    }

    public ColorSetting(String param1, boolean param2, Integer param3) {
        super(param1, param3);
        this.field7598 = param3;
        this.field7599 = param2;
    }
}



