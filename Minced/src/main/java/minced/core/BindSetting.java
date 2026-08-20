/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.function.Supplier;
import minced.core.Setting;

public class BindSetting
extends Setting {
    public BindSetting(String param1) {
        super(param1, -1);
    }

    public BindSetting(String param1, Supplier param2) {
        super(param1, -1);
        this.visibleWhen(param2);
    }
}

