/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 */
package minced.core;

import net.minecraft.util.Identifier;
import sLM._4ZXYEo;

public class EventItemCooldown
{
    public Identifier I;
    public int cfr_renamed_7;

    public void method200(int param1) {
        this.cfr_renamed_7 = param1;
    }

    protected boolean method201(Object param1) {
        return param1 instanceof EventItemCooldown;
    }

    public String toString() {
        return _4ZXYEo.Ч4("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method203()), (int)this.method202());
    }

    public int hashCode() {
        int v2 = 59 + this.method202();
        Identifier v3 = this.method203();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public int method202() {
        return this.cfr_renamed_7;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventItemCooldown)) {
            return false;
        }
        EventItemCooldown v2 = (EventItemCooldown)param1;
        if (!v2.method201(this)) {
            return false;
        }
        if (this.method202() != v2.method202()) {
            return false;
        }
        Identifier v3 = this.method203();
        Identifier v4 = v2.method203();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    public Identifier method203() {
        return this.I;
    }

    public EventItemCooldown(Identifier param1, int param2) {
        this.I = param1;
        this.cfr_renamed_7 = param2;
    }
}



