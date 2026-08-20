/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.client.util.math.MatrixStack;
import sLM._4ZXYEo;

public class EventRender3D
extends EventBase
implements CancellableEvent {
    static String IЙ;

    float field8490;
    MatrixStack field8491;

    public float O() {
        return this.field8490;
    }

    public String toString() {
        return _4ZXYEo.ch("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2348()), (float)this.O());
    }

    protected boolean method2347(Object param1) {
        return param1 instanceof EventRender3D;
    }

    public EventRender3D(MatrixStack param1, float param2) {
        this.field8491 = param1;
        this.field8490 = param2;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventRender3D)) {
            return false;
        }
        EventRender3D v2 = (EventRender3D)param1;
        if (!v2.method2347(this)) {
            return false;
        }
        if (Float.compare(this.O(), v2.O()) != 0) {
            return false;
        }
        MatrixStack v3 = this.method2348();
        MatrixStack v4 = v2.method2348();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public MatrixStack method2348() {
        return this.field8491;
    }

    public void method2349(MatrixStack param1) {
        this.field8491 = param1;
    }

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.O());
        MatrixStack v3 = this.method2348();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    static {
        IЙ = "EventRender3D(matrixStack=\u0001, partialTicks=\u0001)";
    }
}



