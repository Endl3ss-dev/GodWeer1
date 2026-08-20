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

public class EventInteractEntity
extends EventBase
implements CancellableEvent {
    private Entity Е;

    public Entity method2357() {
        return this.Е;
    }

    public void method2358(Entity param1) {
        this.Е = param1;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventInteractEntity)) {
            return false;
        }
        EventInteractEntity v2 = (EventInteractEntity)param1;
        if (!v2.method2359(this)) {
            return false;
        }
        Entity v3 = this.method2357();
        Entity v4 = v2.method2357();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public int hashCode() {
        Entity v3 = this.method2357();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    protected boolean method2359(Object param1) {
        return param1 instanceof EventInteractEntity;
    }

    public EventInteractEntity(Entity param1) {
        this.Е = param1;
    }

    public String toString() {
        return _4ZXYEo.cfr_renamed_190("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2357()));
    }
}



