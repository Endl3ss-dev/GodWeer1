/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.entity.Entity;
import sLM._4ZXYEo;

public class EventEntityRayTrace
extends EventBase
implements CancellableEvent {
    static String 衣诶;

    Entity field8773;

    protected boolean method2433(Object param1) {
        return param1 instanceof EventEntityRayTrace;
    }

    public String toString() {
        return _4ZXYEo.Ч诶("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2434()));
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventEntityRayTrace)) {
            return false;
        }
        EventEntityRayTrace v2 = (EventEntityRayTrace)param1;
        if (!v2.method2433(this)) {
            return false;
        }
        Entity v3 = this.method2434();
        Entity v4 = v2.method2434();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public int hashCode() {
        Entity v3 = this.method2434();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public EventEntityRayTrace(Entity param1) {
        this.field8773 = param1;
    }

    public Entity method2434() {
        return this.field8773;
    }

    static {
        衣诶 = "EventEntityRayTrace(entity=\u0001)";
    }
}

