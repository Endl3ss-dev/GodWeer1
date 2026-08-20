/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import minced.enums.Block;
import sLM._4ZXYEo;

public class EventNoPush
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_208;

    public Block field9025;

    protected boolean method2490(Object param1) {
        return param1 instanceof EventNoPush;
    }

    public Block method2491() {
        return this.field9025;
    }

    public EventNoPush(Block param1) {
        this.field9025 = param1;
    }

    public int hashCode() {
        Block v3 = this.method2491();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventNoPush)) {
            return false;
        }
        EventNoPush v2 = (EventNoPush)param1;
        if (!v2.method2490(this)) {
            return false;
        }
        Block v3 = this.method2491();
        Block v4 = v2.method2491();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public String toString() {
        return _4ZXYEo.фЛ("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2491()));
    }

    static {
        cfr_renamed_208 = "EventNoPush(noPushType=\u0001)";
    }
}

