/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import java.util.function.BiFunction;
import sLM._4ZXYEo;

public class EventFogDistance
{
    static String Тф;

    float field10389;
    float field10390;

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method3017());
        v2 = v2 * 59 + Float.floatToIntBits(this.method3021());
        return v2;
    }

    public String toString() {
        return _4ZXYEo.cfr_renamed_1("makeConcatWithConstants", new Object[]{}, (float)this.method3017(), (float)this.method3021());
    }

    public EventFogDistance(float param1, float param2) {
        this.field10389 = param1;
        this.field10390 = param2;
    }

    public float method3017() {
        return this.field10389;
    }

    public void method3018(float param1) {
        this.field10389 = param1;
    }

    public void method3019(float param1) {
        this.field10390 = param1;
    }

    protected boolean method3020(Object param1) {
        return param1 instanceof EventFogDistance;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventFogDistance)) {
            return false;
        }
        EventFogDistance v2 = (EventFogDistance)param1;
        if (!v2.method3020(this)) {
            return false;
        }
        if (Float.compare(this.method3017(), v2.method3017()) != 0) {
            return false;
        }
        return Float.compare(this.method3021(), v2.method3021()) == 0;
    }

    public float method3021() {
        return this.field10390;
    }

    static {
        Тф = "EventFogDistance(fogStart=\u0001, fogEnd=\u0001)";
    }
}



