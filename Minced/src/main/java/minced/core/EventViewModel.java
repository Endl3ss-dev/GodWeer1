/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Arm
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import sLM._4ZXYEo;

public class EventViewModel
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_181;

    MatrixStack field8481;
    Arm field8482;

    protected boolean method2309(Object param1) {
        return param1 instanceof EventViewModel;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventViewModel)) {
            return false;
        }
        EventViewModel v2 = (EventViewModel)param1;
        if (!v2.method2309(this)) {
            return false;
        }
        MatrixStack v3 = this.method2311();
        MatrixStack v4 = v2.method2311();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        Arm v5 = this.cfr_renamed_0();
        Arm v6 = v2.cfr_renamed_0();
        return !(v5 == null ? v6 != null : !v5.equals(v6));
    }

    public String toString() {
        return _4ZXYEo.дЦ("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2311()), (String)String.valueOf(this.cfr_renamed_0()));
    }

    public EventViewModel(MatrixStack param1, Arm param2) {
        this.field8481 = param1;
        this.field8482 = param2;
    }

    public Arm cfr_renamed_0() {
        return this.field8482;
    }

    public void method2310(MatrixStack param1) {
        this.field8481 = param1;
    }

    public int hashCode() {
        MatrixStack v3 = this.method2311();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        Arm v4 = this.cfr_renamed_0();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        return v2;
    }

    public MatrixStack method2311() {
        return this.field8481;
    }

    public void cfr_renamed_0(Arm param1) {
        this.field8482 = param1;
    }

    static {
        cfr_renamed_181 = "EventViewModel(matrixStack=\u0001, handside=\u0001)";
    }
}

