/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Arm
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import net.minecraft.util.Arm;
import sLM._4ZXYEo;

public class SwingAnimation
implements CancellableEvent {
    Arm н;
    float cfr_renamed_215;

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method2568());
        Arm v3 = this.method2566();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public String toString() {
        return _4ZXYEo.э("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2566()), (float)this.method2568());
    }

    public void method2564(float param1) {
        this.cfr_renamed_215 = param1;
    }

    protected boolean method2565(Object param1) {
        return param1 instanceof SwingAnimation;
    }

    public Arm method2566() {
        return this.н;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof SwingAnimation)) {
            return false;
        }
        SwingAnimation v2 = (SwingAnimation)param1;
        if (!v2.method2565(this)) {
            return false;
        }
        if (Float.compare(this.method2568(), v2.method2568()) != 0) {
            return false;
        }
        Arm v3 = this.method2566();
        Arm v4 = v2.method2566();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public void method2567(Arm param1) {
        this.н = param1;
    }

    public float method2568() {
        return this.cfr_renamed_215;
    }

    public SwingAnimation(Arm param1, float param2) {
        this.н = param1;
        this.cfr_renamed_215 = param2;
    }
}



