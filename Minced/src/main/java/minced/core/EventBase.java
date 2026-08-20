/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.core.CancellableEvent;

public abstract class EventBase
implements CancellableEvent {
    boolean field8564;

    protected EventBase() {
    }

    @Override
    public void method2298(boolean param1) {
        this.field8564 = param1;
    }

    @Override
    public boolean method2297() {
        return this.field8564;
    }
}

