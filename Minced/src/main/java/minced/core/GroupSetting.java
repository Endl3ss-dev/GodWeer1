/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.function.Function;
import minced.core.Setting;

public class GroupSetting
extends Setting
{
    static int О;

    public String method1988() {
        return (String)this.getValue();
    }

    public void method1989(String param1) {
        this.setValue(param1);
    }

    public GroupSetting(String param1) {
        super(param1, "");
    }

    static {
        О = 0;
    }
}

