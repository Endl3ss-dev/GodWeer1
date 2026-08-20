package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import sLM._4ZXYEo;

public class EventSwingArm
extends EventBase
implements CancellableEvent {
    public float нш;
    public Hand hand;
    public MatrixStack matrixStack;

    public Hand method2546() {
        return this.hand;
    }

    public void method2547(float param1) {
        this.нш = param1;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventSwingArm)) {
            return false;
        }
        EventSwingArm v2 = (EventSwingArm)param1;
        if (!v2.method2549(this)) {
            return false;
        }
        if (Float.compare(this.method2551(), v2.method2551()) != 0) {
            return false;
        }
        Hand v3 = this.method2546();
        Hand v4 = v2.method2546();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        MatrixStack v5 = this.method2550();
        MatrixStack v6 = v2.method2550();
        return !(v5 == null ? v6 != null : !v5.equals(v6));
    }

    public void method2548(Hand param1) {
        this.hand = param1;
    }

    public EventSwingArm(float param1, Hand param2, MatrixStack param3) {
        this.нш = param1;
        this.hand = param2;
        this.matrixStack = param3;
    }

    protected boolean method2549(Object param1) {
        return param1 instanceof EventSwingArm;
    }

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method2551());
        Hand v3 = this.method2546();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        MatrixStack v4 = this.method2550();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        return v2;
    }

    public String toString() {
        return _4ZXYEo.у("makeConcatWithConstants", new Object[]{}, (float)this.method2551(), (String)String.valueOf(this.method2546()), (String)String.valueOf(this.method2550()));
    }

    public MatrixStack method2550() {
        return this.matrixStack;
    }

    public float method2551() {
        return this.нш;
    }

    public void method2552(MatrixStack param1) {
        this.matrixStack = param1;
    }
}


