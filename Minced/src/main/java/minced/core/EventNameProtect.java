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

public class EventNameProtect
extends EventBase
implements CancellableEvent {
    static String н28;

    String field9108;

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventNameProtect)) {
            return false;
        }
        EventNameProtect v2 = (EventNameProtect)param1;
        if (!v2.method2589(this)) {
            return false;
        }
        String v3 = this.method2590();
        String v4 = v2.method2590();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public EventNameProtect(String param1) {
        this.field9108 = param1;
    }

    public int hashCode() {
        String v3 = this.method2590();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public void method2588(String param1) {
        this.field9108 = param1;
    }

    public String toString() {
        return _4ZXYEo.cfr_renamed_217("makeConcatWithConstants", new Object[]{}, (String)this.method2590());
    }

    protected boolean method2589(Object param1) {
        return param1 instanceof EventNameProtect;
    }

    public String method2590() {
        return this.field9108;
    }

    static {
        н28 = "EventNameProtect(text=\u0001)";
    }
}

