/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.util.Hand;
import sLM._4ZXYEo;

public class EventSwingSpeed
extends EventBase
implements CancellableEvent {
    static String йы;

    int field9029;
    Hand field9030;

    public void method2500(int param1) {
        this.field9029 = param1;
    }

    public Hand method2501() {
        return this.field9030;
    }

    public void method2502(Hand param1) {
        this.field9030 = param1;
    }

    protected boolean method2503(Object param1) {
        return param1 instanceof EventSwingSpeed;
    }

    public EventSwingSpeed(int param1, Hand param2) {
        this.field9029 = param1;
        this.field9030 = param2;
    }

    public int hashCode() {
        int v2 = 59 + this.method2504();
        Hand v3 = this.method2501();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventSwingSpeed)) {
            return false;
        }
        EventSwingSpeed v2 = (EventSwingSpeed)param1;
        if (!v2.method2503(this)) {
            return false;
        }
        if (this.method2504() != v2.method2504()) {
            return false;
        }
        Hand v3 = this.method2501();
        Hand v4 = v2.method2501();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public String toString() {
        return _4ZXYEo.а("makeConcatWithConstants", new Object[]{}, (int)this.method2504(), (String)String.valueOf(this.method2501()));
    }

    public int method2504() {
        return this.field9029;
    }

    static {
        йы = "EventSwingSpeed(swipeSpeed=\u0001, hand=\u0001)";
    }
}

