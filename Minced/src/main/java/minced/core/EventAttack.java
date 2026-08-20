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

public class EventAttack
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_207;

    Entity field8964;

    protected boolean method2470(Object param1) {
        return param1 instanceof EventAttack;
    }

    public EventAttack(Entity param1) {
        this.field8964 = param1;
    }

    public Entity method2471() {
        return this.field8964;
    }

    public void method2472(Entity param1) {
        this.field8964 = param1;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventAttack)) {
            return false;
        }
        EventAttack v2 = (EventAttack)param1;
        if (!v2.method2470(this)) {
            return false;
        }
        Entity v3 = this.method2471();
        Entity v4 = v2.method2471();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public String toString() {
        return _4ZXYEo.弟з("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2471()));
    }

    public int hashCode() {
        Entity v3 = this.method2471();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    static {
        cfr_renamed_207 = "EventAttack(target=\u0001)";
    }
}

