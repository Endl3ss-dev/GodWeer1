/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.item.ItemStack;
import sLM._4ZXYEo;

public class EventThrowPearl
extends EventBase
implements CancellableEvent {
    ItemStack cfr_renamed_136;

    public EventThrowPearl(ItemStack param1) {
        this.cfr_renamed_136 = param1;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventThrowPearl)) {
            return false;
        }
        EventThrowPearl v2 = (EventThrowPearl)param1;
        if (!v2.method2561(this)) {
            return false;
        }
        ItemStack v3 = this.method2563();
        ItemStack v4 = v2.method2563();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    protected boolean method2561(Object param1) {
        return param1 instanceof EventThrowPearl;
    }

    public void method2562(ItemStack param1) {
        this.cfr_renamed_136 = param1;
    }

    public int hashCode() {
        ItemStack v3 = this.method2563();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public String toString() {
        return _4ZXYEo.cfr_renamed_214("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2563()));
    }

    public ItemStack method2563() {
        return this.cfr_renamed_136;
    }
}



