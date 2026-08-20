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

public class EventInventoryClose
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_209;

    int field9026;

    public void method2492(int param1) {
        this.field9026 = param1;
    }

    public int hashCode() {
        int v2 = 59 + this.method2493();
        return v2;
    }

    public String toString() {
        return _4ZXYEo.дд("makeConcatWithConstants", new Object[]{}, (int)this.method2493());
    }

    public EventInventoryClose(int param1) {
        this.field9026 = param1;
    }

    public int method2493() {
        return this.field9026;
    }

    protected boolean method2494(Object param1) {
        return param1 instanceof EventInventoryClose;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventInventoryClose)) {
            return false;
        }
        EventInventoryClose v2 = (EventInventoryClose)param1;
        if (!v2.method2494(this)) {
            return false;
        }
        return this.method2493() == v2.method2493();
    }

    static {
        cfr_renamed_209 = "EventInventoryClose(windowId=\u0001)";
    }
}



