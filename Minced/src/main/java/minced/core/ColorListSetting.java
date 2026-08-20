/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.core.Setting;
import minced.core.Animation;
import minced.core.Easings;

public class ColorListSetting
extends Setting {
    static long field8332 = 150L;
    Animation field8333 = new Animation(Easings.field11969, 0.0f, field8332);

    public String method2237() {
        return (String)this.getValue();
    }

    public void method2238(String param1) {
        this.setValue(param1);
    }

    public ColorListSetting(String param1) {
        super(param1, "");
    }
}

