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

public class EventAddEntity
extends EventBase
implements CancellableEvent {
    static String ею;

    Entity field9028;

    public Entity method2497() {
        return this.field9028;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventAddEntity)) {
            return false;
        }
        EventAddEntity v2 = (EventAddEntity)param1;
        if (!v2.method2498(this)) {
            return false;
        }
        Entity v3 = this.method2497();
        Entity v4 = v2.method2497();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    protected boolean method2498(Object param1) {
        return param1 instanceof EventAddEntity;
    }

    public String toString() {
        return _4ZXYEo.Чй("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2497()));
    }

    public int hashCode() {
        Entity v3 = this.method2497();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public EventAddEntity(Entity param1) {
        this.field9028 = param1;
    }

    public void method2499(Entity param1) {
        this.field9028 = param1;
    }

    static {
        ею = "EventAddEntity(entity=\u0001)";
    }
}

