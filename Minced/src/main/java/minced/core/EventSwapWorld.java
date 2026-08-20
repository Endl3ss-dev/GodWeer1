/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import minced.enums.LOAD;
import sLM._4ZXYEo;

public class EventSwapWorld
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_205;

    public LOAD field8581;

    public String toString() {
        return _4ZXYEo.弟т("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2421()));
    }

    public int hashCode() {
        LOAD v3 = this.method2421();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public void method2420(LOAD param1) {
        this.field8581 = param1;
    }

    public EventSwapWorld(LOAD param1) {
        this.field8581 = param1;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventSwapWorld)) {
            return false;
        }
        EventSwapWorld v2 = (EventSwapWorld)param1;
        if (!v2.method2422(this)) {
            return false;
        }
        LOAD v3 = this.method2421();
        LOAD v4 = v2.method2421();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public LOAD method2421() {
        return this.field8581;
    }

    protected boolean method2422(Object param1) {
        return param1 instanceof EventSwapWorld;
    }

    static {
        cfr_renamed_205 = "EventSwapWorld(state=\u0001)";
    }
}

