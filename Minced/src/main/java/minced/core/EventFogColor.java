/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import sLM._4ZXYEo;

public class EventFogColor
{
    static String ЯА;

    float field919;
    float field920;
    float field921;

    public String toString() {
        return _4ZXYEo.г("makeConcatWithConstants", new Object[]{}, (float)this.method304(), (float)this.method303(), (float)this.method302());
    }

    public void method301(float param1) {
        this.field919 = param1;
    }

    public float method302() {
        return this.field919;
    }

    public EventFogColor(float param1, float param2, float param3) {
        this.field920 = param1;
        this.field921 = param2;
        this.field919 = param3;
    }

    public float method303() {
        return this.field921;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventFogColor)) {
            return false;
        }
        EventFogColor v2 = (EventFogColor)param1;
        if (!v2.method306(this)) {
            return false;
        }
        if (Float.compare(this.method304(), v2.method304()) != 0) {
            return false;
        }
        if (Float.compare(this.method303(), v2.method303()) != 0) {
            return false;
        }
        return Float.compare(this.method302(), v2.method302()) == 0;
    }

    public float method304() {
        return this.field920;
    }

    public void method305(float param1) {
        this.field921 = param1;
    }

    protected boolean method306(Object param1) {
        return param1 instanceof EventFogColor;
    }

    public void l(float param1) {
        this.field920 = param1;
    }

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method304());
        v2 = v2 * 59 + Float.floatToIntBits(this.method303());
        v2 = v2 * 59 + Float.floatToIntBits(this.method302());
        return v2;
    }

    static {
        ЯА = "EventFogColor(red=\u0001, green=\u0001, blue=\u0001)";
    }
}



