/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import sLM._4ZXYEo;

public class EventKey {
    static String cfr_renamed_292;

    boolean field12563;
    int field12564;
    boolean field12565;

    public void method3593(boolean param1) {
        this.field12563 = param1;
    }

    public boolean method3594() {
        return this.field12563;
    }

    public int method3595() {
        return this.field12564;
    }

    public void method3596(int param1) {
        this.field12564 = param1;
    }

    protected boolean method3597(Object param1) {
        return param1 instanceof EventKey;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventKey)) {
            return false;
        }
        EventKey v2 = (EventKey)param1;
        if (!v2.method3597(this)) {
            return false;
        }
        if (this.method3595() != v2.method3595()) {
            return false;
        }
        if (this.method3599() != v2.method3599()) {
            return false;
        }
        return this.method3594() == v2.method3594();
    }

    public void method3598(boolean param1) {
        this.field12565 = param1;
    }

    public EventKey(int param1, boolean param2, boolean param3) {
        this.field12564 = param1;
        this.field12565 = param2;
        this.field12563 = param3;
    }

    public int hashCode() {
        int v2 = 59 + this.method3595();
        v2 = v2 * 59 + (this.method3599() ? 79 : 97);
        v2 = v2 * 59 + (this.method3594() ? 79 : 97);
        return v2;
    }

    public boolean method3599() {
        return this.field12565;
    }

    public String toString() {
        return _4ZXYEo.ЦЩ("makeConcatWithConstants", new Object[]{}, (int)this.method3595(), (boolean)this.method3599(), (boolean)this.method3594());
    }

    static {
        cfr_renamed_292 = "EventKey(key=\u0001, hold=\u0001, screenWasOpen=\u0001)";
    }
}

