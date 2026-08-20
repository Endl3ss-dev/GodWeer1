/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.entity.LivingEntity;
import sLM._4ZXYEo;

public class EventTravel
extends EventBase
implements CancellableEvent {
    static String Ьа;

    LivingEntity field9132;

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventTravel)) {
            return false;
        }
        EventTravel v2 = (EventTravel)param1;
        if (!v2.cfr_underscore(this)) {
            return false;
        }
        LivingEntity v3 = this.method2611();
        LivingEntity v4 = v2.method2611();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public int hashCode() {
        LivingEntity v3 = this.method2611();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public void method2610(LivingEntity param1) {
        this.field9132 = param1;
    }

    protected boolean cfr_underscore(Object param1) {
        return param1 instanceof EventTravel;
    }

    public String toString() {
        return _4ZXYEo.弟Э("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2611()));
    }

    public LivingEntity method2611() {
        return this.field9132;
    }

    public EventTravel(LivingEntity param1) {
        this.field9132 = param1;
    }

    static {
        Ьа = "EventTravel(entity=\u0001)";
    }
}

