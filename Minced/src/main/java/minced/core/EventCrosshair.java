/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.client.gui.DrawContext;
import sLM._4ZXYEo;

public class EventCrosshair
extends EventBase
implements CancellableEvent {
    static String l8;

    DrawContext I;
    float field8476;

    public DrawContext method2299() {
        return this.I;
    }

    protected boolean method2300(Object param1) {
        return param1 instanceof EventCrosshair;
    }

    public String toString() {
        return _4ZXYEo.cfr_underscore("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2299()), (float)this.method2301());
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventCrosshair)) {
            return false;
        }
        EventCrosshair v2 = (EventCrosshair)param1;
        if (!v2.method2300(this)) {
            return false;
        }
        if (Float.compare(this.method2301(), v2.method2301()) != 0) {
            return false;
        }
        DrawContext v3 = this.method2299();
        DrawContext v4 = v2.method2299();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public EventCrosshair(DrawContext param1, float param2) {
        this.I = param1;
        this.field8476 = param2;
    }

    public float method2301() {
        return this.field8476;
    }

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method2301());
        DrawContext v3 = this.method2299();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    static {
        l8 = "EventCrosshair(context=\u0001, partialTicks=\u0001)";
    }
}



