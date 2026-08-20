/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;

public class EventJump
extends EventBase
implements CancellableEvent {
    static String field8486;

    protected boolean method2332(Object param1) {
        return param1 instanceof EventJump;
    }

    public String toString() {
        return field8486;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventJump)) {
            return false;
        }
        EventJump v2 = (EventJump)param1;
        return v2.method2332(this);
    }

    public int hashCode() {
        return 1;
    }

    static {
        field8486 = "EventJump()";
    }
}

