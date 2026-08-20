/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import java.util.function.Function;
import sLM._4ZXYEo;

public class EventInput {
    static String cfr_renamed_128;

    float l;
    float field7931;
    boolean field7932;
    boolean field7933;

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method1969());
        v2 = v2 * 59 + Float.floatToIntBits(this.method1971());
        v2 = v2 * 59 + (this.method1968() ? 79 : 97);
        v2 = v2 * 59 + (this.method1967() ? 79 : 97);
        return v2;
    }

    public EventInput(float param1, float param2, boolean param3, boolean param4) {
        this.l = param1;
        this.field7931 = param2;
        this.field7932 = param3;
        this.field7933 = param4;
    }

    public boolean method1967() {
        return this.field7933;
    }

    public boolean method1968() {
        return this.field7932;
    }

    public float method1969() {
        return this.l;
    }

    public void method1970(boolean param1) {
        this.field7933 = param1;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventInput)) {
            return false;
        }
        EventInput v2 = (EventInput)param1;
        if (!v2.cfr_renamed_4(this)) {
            return false;
        }
        if (Float.compare(this.method1969(), v2.method1969()) != 0) {
            return false;
        }
        if (Float.compare(this.method1971(), v2.method1971()) != 0) {
            return false;
        }
        if (this.method1968() != v2.method1968()) {
            return false;
        }
        return this.method1967() == v2.method1967();
    }

    public float method1971() {
        return this.field7931;
    }

    public void method1972(float param1) {
        this.field7931 = param1;
    }

    protected boolean cfr_renamed_4(Object param1) {
        return param1 instanceof EventInput;
    }

    public String toString() {
        return _4ZXYEo.弟ц("makeConcatWithConstants", new Object[]{}, (float)this.method1969(), (float)this.method1971(), (boolean)this.method1968(), (boolean)this.method1967());
    }

    public void method1973(float param1) {
        this.l = param1;
    }

    public void method1974(boolean param1) {
        this.field7932 = param1;
    }

    static {
        cfr_renamed_128 = "EventInput(forward=\u0001, strafe=\u0001, jump=\u0001, sneak=\u0001)";
    }
}



