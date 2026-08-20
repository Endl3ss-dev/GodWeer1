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

public class EventLook
extends EventBase
implements CancellableEvent {
    static String Ыи;

    double field9065;
    double field9066;

    public void method2556(double param1) {
        this.field9065 = param1;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventLook)) {
            return false;
        }
        EventLook v2 = (EventLook)param1;
        if (!v2.method2560(this)) {
            return false;
        }
        if (Double.compare(this.method2559(), v2.method2559()) != 0) {
            return false;
        }
        return Double.compare(this.method2557(), v2.method2557()) == 0;
    }

    public int hashCode() {
        long v3 = Double.doubleToLongBits(this.method2559());
        int v2 = 59 + (int)(v3 >>> 32 ^ v3);
        long v5 = Double.doubleToLongBits(this.method2557());
        v2 = v2 * 59 + (int)(v5 >>> 32 ^ v5);
        return v2;
    }

    public double method2557() {
        return this.field9066;
    }

    public void method2558(double param1) {
        this.field9066 = param1;
    }

    public double method2559() {
        return this.field9065;
    }

    public String toString() {
        return _4ZXYEo.弟7("makeConcatWithConstants", new Object[]{}, (double)this.method2559(), (double)this.method2557());
    }

    public EventLook(double param1, double param3) {
        this.field9065 = param1;
        this.field9066 = param3;
    }

    protected boolean method2560(Object param1) {
        return param1 instanceof EventLook;
    }

    static {
        Ыи = "EventLook(yaw=\u0001, pitch=\u0001)";
    }
}

