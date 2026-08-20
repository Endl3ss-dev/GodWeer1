/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 *  sg.ec.Чы$Ч
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import sLM._4ZXYEo;
import sg.ec.chy;

public class EventVelocity
extends EventBase
implements CancellableEvent {
    public chy.ch н;

    protected boolean method2392(Object param1) {
        return param1 instanceof EventVelocity;
    }

    public String toString() {
        return _4ZXYEo.Д("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2393()));
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventVelocity)) {
            return false;
        }
        EventVelocity v2 = (EventVelocity)param1;
        if (!v2.method2392(this)) {
            return false;
        }
        chy.ch v3 = this.method2393();
        chy.ch v4 = v2.method2393();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public EventVelocity(chy.ch param1) {
        this.н = param1;
    }

    public chy.ch method2393() {
        return this.н;
    }

    public int hashCode() {
        chy.ch v3 = this.method2393();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }
}

