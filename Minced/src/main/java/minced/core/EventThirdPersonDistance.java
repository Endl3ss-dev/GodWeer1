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

public class EventThirdPersonDistance
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_213;

    float field9042;

    protected boolean method2533(Object param1) {
        return param1 instanceof EventThirdPersonDistance;
    }

    public String toString() {
        return _4ZXYEo.им("makeConcatWithConstants", new Object[]{}, (float)this.method2534());
    }

    public float method2534() {
        return this.field9042;
    }

    public EventThirdPersonDistance(float param1) {
        this.field9042 = param1;
    }

    public void method2535(float param1) {
        this.field9042 = param1;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventThirdPersonDistance)) {
            return false;
        }
        EventThirdPersonDistance v2 = (EventThirdPersonDistance)param1;
        if (!v2.method2533(this)) {
            return false;
        }
        return Float.compare(this.method2534(), v2.method2534()) == 0;
    }

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method2534());
        return v2;
    }

    static {
        cfr_renamed_213 = "EventThirdPersonDistance(distance=\u0001)";
    }
}



