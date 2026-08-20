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

public class EventMouseDrag
extends EventBase
implements CancellableEvent {
    int З;

    public String toString() {
        return _4ZXYEo.д弟("makeConcatWithConstants", new Object[]{}, (int)this.method2617());
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventMouseDrag)) {
            return false;
        }
        EventMouseDrag v2 = (EventMouseDrag)param1;
        if (!v2.method2619(this)) {
            return false;
        }
        return this.method2617() == v2.method2617();
    }

    public int method2617() {
        return this.З;
    }

    public int hashCode() {
        int v2 = 59 + this.method2617();
        return v2;
    }

    public EventMouseDrag(int param1) {
        this.З = param1;
    }

    public void method2618(int param1) {
        this.З = param1;
    }

    protected boolean method2619(Object param1) {
        return param1 instanceof EventMouseDrag;
    }
}

