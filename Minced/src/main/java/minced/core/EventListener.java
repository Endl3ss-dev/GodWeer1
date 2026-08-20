/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;

final class EventListener {
    Object field8329;
    Method field8330;
    byte field8331;

    public EventListener(Object param1, Method param2, byte param3) {
        this.field8329 = param1;
        this.field8330 = param2;
        this.field8331 = param3;
    }

    public byte method2234() {
        return this.field8331;
    }

    public Object method2235() {
        return this.field8329;
    }

    public Method method2236() {
        return this.field8330;
    }
}

