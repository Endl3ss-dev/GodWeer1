/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.core.CancellableEvent;

public class EventUpdate
implements CancellableEvent {
    static String field8565;

    public String toString() {
        return field8565;
    }

    protected boolean method2397(Object param1) {
        return param1 instanceof EventUpdate;
    }

    public int hashCode() {
        return 1;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventUpdate)) {
            return false;
        }
        EventUpdate v2 = (EventUpdate)param1;
        return v2.method2397(this);
    }

    static {
        field8565 = "EventUpdate()";
    }
}

