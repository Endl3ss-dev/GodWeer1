/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.function.Supplier;
import minced.core.Setting;

public class ItemSetting
extends Setting {
    public ItemSetting(String param1) {
        super(param1, "");
    }

    public ItemSetting(String param1, Supplier param2) {
        super(param1, "");
        this.visibleWhen(param2);
    }
}



