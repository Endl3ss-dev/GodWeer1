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

public class EventCooldownTracker
extends EventBase
implements CancellableEvent {
    static String Ьр;

    ItemStack field8568;
    float field8569;

    public void I(ItemStack param1) {
        this.field8568 = param1;
    }

    public EventCooldownTracker(ItemStack param1, float param2) {
        this.field8568 = param1;
        this.field8569 = param2;
    }

    public float method2402() {
        return this.field8569;
    }

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method2402());
        ItemStack v3 = this.method2405();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        return v2;
    }

    public String toString() {
        return _4ZXYEo.弟2("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2405()), (float)this.method2402());
    }

    protected boolean method2403(Object param1) {
        return param1 instanceof EventCooldownTracker;
    }

    public void method2404(float param1) {
        this.field8569 = param1;
    }

    public ItemStack method2405() {
        return this.field8568;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventCooldownTracker)) {
            return false;
        }
        EventCooldownTracker v2 = (EventCooldownTracker)param1;
        if (!v2.method2403(this)) {
            return false;
        }
        if (Float.compare(this.method2402(), v2.method2402()) != 0) {
            return false;
        }
        ItemStack v3 = this.method2405();
        ItemStack v4 = v2.method2405();
        return !(v3 == null ? v4 != null : !v3.equals(v4));
    }

    static {
        Ьр = "EventCooldownTracker(item=\u0001, partialTicks=\u0001)";
    }
}



