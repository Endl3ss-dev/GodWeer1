/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.core.CancellableEvent;

public class EventGameJoin
implements CancellableEvent {
    String field8566;

    public EventGameJoin(String param1) {
        this.field8566 = param1;
    }

    public String method2398() {
        return this.field8566;
    }
}

