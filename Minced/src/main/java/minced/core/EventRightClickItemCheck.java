/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Hand
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import sLM._4ZXYEo;

public class EventRightClickItemCheck
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_206;

    ItemStack field8772;
    Hand I;

    public String toString() {
        return _4ZXYEo.弟Ж("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2430()), (String)String.valueOf(this.method2429()));
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventRightClickItemCheck)) {
            return false;
        }
        EventRightClickItemCheck v2 = (EventRightClickItemCheck)param1;
        if (!v2.cfr_renamed_0(this)) {
            return false;
        }
        ItemStack v3 = this.method2430();
        ItemStack v4 = v2.method2430();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        Hand v5 = this.method2429();
        Hand v6 = v2.method2429();
        return !(v5 == null ? v6 != null : !v5.equals(v6));
    }

    public Hand method2429() {
        return this.I;
    }

    protected boolean cfr_renamed_0(Object param1) {
        return param1 instanceof EventRightClickItemCheck;
    }

    public ItemStack method2430() {
        return this.field8772;
    }

    public void method2431(ItemStack param1) {
        this.field8772 = param1;
    }

    public void method2432(Hand param1) {
        this.I = param1;
    }

    public EventRightClickItemCheck(ItemStack param1, Hand param2) {
        this.field8772 = param1;
        this.I = param2;
    }

    public int hashCode() {
        ItemStack v3 = this.method2430();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        Hand v4 = this.method2429();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        return v2;
    }

    static {
        cfr_renamed_206 = "EventRightClickItemCheck(itemStack=\u0001, hand=\u0001)";
    }
}



