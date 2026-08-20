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

public class EventFreecamWorldRender
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_188;

    boolean field8485;

    public String toString() {
        return _4ZXYEo.弟9("makeConcatWithConstants", new Object[]{}, (boolean)this.method2329());
    }

    public EventFreecamWorldRender(boolean param1) {
        this.field8485 = param1;
    }

    public boolean method2329() {
        return this.field8485;
    }

    protected boolean method2330(Object param1) {
        return param1 instanceof EventFreecamWorldRender;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventFreecamWorldRender)) {
            return false;
        }
        EventFreecamWorldRender v2 = (EventFreecamWorldRender)param1;
        if (!v2.method2330(this)) {
            return false;
        }
        return this.method2329() == v2.method2329();
    }

    public void method2331(boolean param1) {
        this.field8485 = param1;
    }

    public int hashCode() {
        int v2 = 59 + (this.method2329() ? 79 : 97);
        return v2;
    }

    static {
        cfr_renamed_188 = "EventFreecamWorldRender(isFreecam=\u0001)";
    }
}

