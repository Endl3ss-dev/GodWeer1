/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import sLM._4ZXYEo;

public class EventMouseRelease
implements Event {
    static String 诶ы;

    float field10385;
    float field10386;
    int field10387;

    public EventMouseRelease(float param1, float param2, int param3) {
        this.field10385 = param1;
        this.field10386 = param2;
        this.field10387 = param3;
    }

    public float method2998() {
        return this.field10385;
    }

    @Override
    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method2998());
        v2 = v2 * 59 + Float.floatToIntBits(this.method3001());
        v2 = v2 * 59 + this.method3000();
        return v2;
    }

    public void method2999(float param1) {
        this.field10386 = param1;
    }

    public int method3000() {
        return this.field10387;
    }

    public float method3001() {
        return this.field10386;
    }

    @Override
    public String toString() {
        return _4ZXYEo.и2("makeConcatWithConstants", new Object[]{}, (float)this.method2998(), (float)this.method3001(), (int)this.method3000());
    }

    protected boolean method3002(Object param1) {
        return param1 instanceof EventMouseRelease;
    }

    @Override
    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventMouseRelease)) {
            return false;
        }
        EventMouseRelease v2 = (EventMouseRelease)param1;
        if (!v2.method3002(this)) {
            return false;
        }
        if (Float.compare(this.method2998(), v2.method2998()) != 0) {
            return false;
        }
        if (Float.compare(this.method3001(), v2.method3001()) != 0) {
            return false;
        }
        return this.method3000() == v2.method3000();
    }

    public void method3003(int param1) {
        this.field10387 = param1;
    }

    public void cfr_underscore(float param1) {
        this.field10385 = param1;
    }

    static {
        诶ы = "EventMouseRealese(x=\u0001, y=\u0001, button=\u0001)";
    }
}



