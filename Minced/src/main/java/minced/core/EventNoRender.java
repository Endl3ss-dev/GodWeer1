/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import minced.enums.fire;
import sLM._4ZXYEo;

public class EventNoRender
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_212;

    public fire field9041;

    protected boolean method2531(Object param1) {
        return param1 instanceof EventNoRender;
    }

    public fire method2532() {
        return this.field9041;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventNoRender)) {
            return false;
        }
        EventNoRender v2 = (EventNoRender)param1;
        if (!v2.method2531(this)) {
            return false;
        }
        fire v3 = this.method2532();
        fire v4 = v2.method2532();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public EventNoRender(fire param1) {
        this.field9041 = param1;
    }

    public String toString() {
        return _4ZXYEo.cfr_renamed_211("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2532()));
    }

    public int hashCode() {
        fire v3 = this.method2532();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    static {
        cfr_renamed_212 = "EventNoRender(norenderType=\u0001)";
    }
}

