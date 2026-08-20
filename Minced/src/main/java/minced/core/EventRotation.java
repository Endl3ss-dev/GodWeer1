/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import sLM._4ZXYEo;

public class EventRotation
extends EventBase
implements CancellableEvent {
    static String ай;

    Vec3d field9039;
    Vec2f field9040;

    public void method2527(Vec3d param1) {
        this.field9039 = param1;
    }

    public String toString() {
        return _4ZXYEo.У("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2529()), (String)String.valueOf(this.method2528()));
    }

    public Vec2f method2528() {
        return this.field9040;
    }

    public Vec3d method2529() {
        return this.field9039;
    }

    public void method2530(Vec2f param1) {
        this.field9040 = param1;
    }

    protected boolean l(Object param1) {
        return param1 instanceof EventRotation;
    }

    public EventRotation(Vec3d param1, Vec2f param2) {
        this.field9039 = param1;
        this.field9040 = param2;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventRotation)) {
            return false;
        }
        EventRotation v2 = (EventRotation)param1;
        if (!v2.l(this)) {
            return false;
        }
        Vec3d v3 = this.method2529();
        Vec3d v4 = v2.method2529();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        Vec2f v5 = this.method2528();
        Vec2f v6 = v2.method2528();
        return !(v5 == null ? v6 != null : !v5.equals(v6));
    }

    public int hashCode() {
        Vec3d v3 = this.method2529();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        Vec2f v4 = this.method2528();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        return v2;
    }

    static {
        ай = "EventRotation(position=\u0001, rotation=\u0001)";
    }
}

