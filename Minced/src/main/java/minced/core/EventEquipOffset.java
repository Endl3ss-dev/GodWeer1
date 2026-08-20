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

public class EventEquipOffset
implements CancellableEvent {
    private Arm eh;
    private float ШХ;
    private float Шж;

    public float method2539() {
        return this.Шж;
    }

    public void method2540(Arm param1) {
        this.eh = param1;
    }

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method2543());
        v2 = v2 * 59 + Float.floatToIntBits(this.method2539());
        Arm v3 = this.method2544();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public String toString() {
        return _4ZXYEo.chk("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2544()), (float)this.method2543(), (float)this.method2539());
    }

    public void method2541(float param1) {
        this.Шж = param1;
    }

    public void method2542(float param1) {
        this.ШХ = param1;
    }

    public float method2543() {
        return this.ШХ;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventEquipOffset)) {
            return false;
        }
        EventEquipOffset v2 = (EventEquipOffset)param1;
        if (!v2.method2545(this)) {
            return false;
        }
        if (Float.compare(this.method2543(), v2.method2543()) != 0) {
            return false;
        }
        if (Float.compare(this.method2539(), v2.method2539()) != 0) {
            return false;
        }
        Arm v3 = this.method2544();
        Arm v4 = v2.method2544();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public Arm method2544() {
        return this.eh;
    }

    protected boolean method2545(Object param1) {
        return param1 instanceof EventEquipOffset;
    }

    public EventEquipOffset(Arm param1, float param2, float param3) {
        this.eh = param1;
        this.ШХ = param2;
        this.Шж = param3;
    }
}



