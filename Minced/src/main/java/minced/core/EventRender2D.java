/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  sLM.4ZXYEo
 */
package minced.core;

import net.minecraft.client.gui.DrawContext;
import sLM._4ZXYEo;

public class EventRender2D
implements Event {
    static String cfr_renamed_256;

    DrawContext field10383;
    float field10384;

    @Override
    public String toString() {
        return _4ZXYEo.cfr_renamed_255("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2997()), (float)this.method2996());
    }

    @Override
    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method2996());
        DrawContext v3 = this.method2997();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public EventRender2D(DrawContext param1, float param2) {
        this.field10383 = param1;
        this.field10384 = param2;
    }

    public float method2996() {
        return this.field10384;
    }

    public DrawContext method2997() {
        return this.field10383;
    }

    @Override
    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventRender2D)) {
            return false;
        }
        EventRender2D v2 = (EventRender2D)param1;
        if (!v2.I(this)) {
            return false;
        }
        if (Float.compare(this.method2996(), v2.method2996()) != 0) {
            return false;
        }
        DrawContext v3 = this.method2997();
        DrawContext v4 = v2.method2997();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    protected boolean I(Object param1) {
        return param1 instanceof EventRender2D;
    }

    static {
        cfr_renamed_256 = "EventRender2D(graphics=\u0001, partialTicks=\u0001)";
    }
}



