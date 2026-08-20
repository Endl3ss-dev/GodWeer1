/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import sLM._4ZXYEo;

public class EventMouseClick
implements Event {
    static String cfr_renamed_254;

    int field10380;
    float field10381;
    float field10382;

    public int method2989() {
        return this.field10380;
    }

    @Override
    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventMouseClick)) {
            return false;
        }
        EventMouseClick v2 = (EventMouseClick)param1;
        if (!v2.method2994(this)) {
            return false;
        }
        if (Float.compare(this.method2992(), v2.method2992()) != 0) {
            return false;
        }
        if (Float.compare(this.method2993(), v2.method2993()) != 0) {
            return false;
        }
        return this.method2989() == v2.method2989();
    }

    @Override
    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method2992());
        v2 = v2 * 59 + Float.floatToIntBits(this.method2993());
        v2 = v2 * 59 + this.method2989();
        return v2;
    }

    public void method2990(int param1) {
        this.field10380 = param1;
    }

    public void method2991(float param1) {
        this.field10381 = param1;
    }

    public float method2992() {
        return this.field10381;
    }

    @Override
    public String toString() {
        return _4ZXYEo.弟У("makeConcatWithConstants", new Object[]{}, (float)this.method2992(), (float)this.method2993(), (int)this.method2989());
    }

    public float method2993() {
        return this.field10382;
    }

    public EventMouseClick(float param1, float param2, int param3) {
        this.field10381 = param1;
        this.field10382 = param2;
        this.field10380 = param3;
    }

    protected boolean method2994(Object param1) {
        return param1 instanceof EventMouseClick;
    }

    public void method2995(float param1) {
        this.field10382 = param1;
    }

    static {
        cfr_renamed_254 = "EventMouseClick(x=\u0001, y=\u0001, button=\u0001)";
    }
}



