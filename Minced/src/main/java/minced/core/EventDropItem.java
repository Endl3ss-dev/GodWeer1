/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import sLM._4ZXYEo;

public class EventDropItem
extends EventBase
implements CancellableEvent {
    private int cfr_renamed_204;

    public String toString() {
        return _4ZXYEo.cfr_renamed_203("makeConcatWithConstants", new Object[]{}, (int)this.method2419());
    }

    public EventDropItem(int param1) {
        this.cfr_renamed_204 = param1;
    }

    public int hashCode() {
        int v2 = 59 + this.method2419();
        return v2;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventDropItem)) {
            return false;
        }
        EventDropItem v2 = (EventDropItem)param1;
        if (!v2.method2417(this)) {
            return false;
        }
        return this.method2419() == v2.method2419();
    }

    protected boolean method2417(Object param1) {
        return param1 instanceof EventDropItem;
    }

    public void method2418(int param1) {
        this.cfr_renamed_204 = param1;
    }

    public int method2419() {
        return this.cfr_renamed_204;
    }
}



