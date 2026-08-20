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

public class EventVelocityMultiplier
extends EventBase
implements CancellableEvent {
    private Entity д;
    private float ШЪ;

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method2442());
        Entity v3 = this.method2446();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public float method2442() {
        return this.ШЪ;
    }

    public void method2443(float param1) {
        this.ШЪ = param1;
    }

    public EventVelocityMultiplier(Entity param1, float param2) {
        this.д = param1;
        this.ШЪ = param2;
    }

    public void method2444(Entity param1) {
        this.д = param1;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventVelocityMultiplier)) {
            return false;
        }
        EventVelocityMultiplier v2 = (EventVelocityMultiplier)param1;
        if (!v2.method2445(this)) {
            return false;
        }
        if (Float.compare(this.method2442(), v2.method2442()) != 0) {
            return false;
        }
        Entity v3 = this.method2446();
        Entity v4 = v2.method2446();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public String toString() {
        return _4ZXYEo.弟л("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2446()), (float)this.method2442());
    }

    protected boolean method2445(Object param1) {
        return param1 instanceof EventVelocityMultiplier;
    }

    public Entity method2446() {
        return this.д;
    }
}



