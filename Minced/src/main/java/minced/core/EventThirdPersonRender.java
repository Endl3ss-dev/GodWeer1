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

public class EventThirdPersonRender
extends EventBase
implements CancellableEvent {
    static String Iи;

    boolean field9043;

    public boolean method2536() {
        return this.field9043;
    }

    public EventThirdPersonRender(boolean param1) {
        this.field9043 = param1;
    }

    public String toString() {
        return _4ZXYEo.弟("makeConcatWithConstants", new Object[]{}, (boolean)this.method2536());
    }

    protected boolean method2537(Object param1) {
        return param1 instanceof EventThirdPersonRender;
    }

    public int hashCode() {
        int v2 = 59 + (this.method2536() ? 79 : 97);
        return v2;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventThirdPersonRender)) {
            return false;
        }
        EventThirdPersonRender v2 = (EventThirdPersonRender)param1;
        if (!v2.method2537(this)) {
            return false;
        }
        return this.method2536() == v2.method2536();
    }

    public void method2538(boolean param1) {
        this.field9043 = param1;
    }

    static {
        Iи = "EventThirdPersonRender(thirdperson=\u0001)";
    }
}



