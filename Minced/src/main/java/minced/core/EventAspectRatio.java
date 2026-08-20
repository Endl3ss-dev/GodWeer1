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

public class EventAspectRatio
extends EventBase
implements CancellableEvent {
    static String _Ю;

    float field8941;

    public String toString() {
        return _4ZXYEo.弟П("makeConcatWithConstants", new Object[]{}, (float)this.method2449());
    }

    public EventAspectRatio(float param1) {
        this.field8941 = param1;
    }

    public void method2447(float param1) {
        this.field8941 = param1;
    }

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method2449());
        return v2;
    }

    protected boolean method2448(Object param1) {
        return param1 instanceof EventAspectRatio;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventAspectRatio)) {
            return false;
        }
        EventAspectRatio v2 = (EventAspectRatio)param1;
        if (!v2.method2448(this)) {
            return false;
        }
        return Float.compare(this.method2449(), v2.method2449()) == 0;
    }

    public float method2449() {
        return this.field8941;
    }

    static {
        _Ю = "EventAspectRatio(aspectRatio=\u0001)";
    }
}



