/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  net.minecraft.item.ItemStack
 *  net.minecraft.screen.slot.SlotActionType
 *  sLM.4ZXYEo
 */
package minced.core;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import sLM._4ZXYEo;

public class EventClickSlot
extends EventBase
implements CancellableEvent {
    int cfr_renamed_182;
    int cfr_renamed_183;
    int cfr_renamed_185;
    int cfr_renamed_184;
    SlotActionType eh;
    ItemStack ь;
    Int2ObjectMap н;

    public void method2312(Int2ObjectMap param1) {
        this.н = param1;
    }

    public SlotActionType method2313() {
        return this.eh;
    }

    public Int2ObjectMap method2314() {
        return this.н;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventClickSlot)) {
            return false;
        }
        EventClickSlot v2 = (EventClickSlot)param1;
        if (!v2.O(this)) {
            return false;
        }
        if (this.method2315() != v2.method2315()) {
            return false;
        }
        if (this.method2319() != v2.method2319()) {
            return false;
        }
        if (this.method2320() != v2.method2320()) {
            return false;
        }
        if (this.method2317() != v2.method2317()) {
            return false;
        }
        SlotActionType v3 = this.method2313();
        SlotActionType v4 = v2.method2313();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        ItemStack v5 = this.method2323();
        ItemStack v6 = v2.method2323();
        if (v5 == null ? v6 != null : !v5.equals(v6)) {
            return false;
        }
        Int2ObjectMap v7 = this.method2314();
        Int2ObjectMap v8 = v2.method2314();
        return !(v7 == null ? v8 != null : !v7.equals(v8));
    }

    public int method2315() {
        return this.cfr_renamed_182;
    }

    public void method2316(int param1) {
        this.cfr_renamed_183 = param1;
    }

    public int method2317() {
        return this.cfr_renamed_184;
    }

    public EventClickSlot(int param1, int param2, int param3, int param4, SlotActionType param5, ItemStack param6, Int2ObjectMap param7) {
        this.cfr_renamed_182 = param1;
        this.cfr_renamed_183 = param2;
        this.cfr_renamed_185 = param3;
        this.cfr_renamed_184 = param4;
        this.eh = param5;
        this.ь = param6;
        this.н = param7;
    }

    public void method2318(int param1) {
        this.cfr_renamed_185 = param1;
    }

    public int method2319() {
        return this.cfr_renamed_183;
    }

    public void cfr_underscore(ItemStack param1) {
        this.ь = param1;
    }

    public int method2320() {
        return this.cfr_renamed_185;
    }

    public void cfr_underscore(SlotActionType param1) {
        this.eh = param1;
    }

    public void method2321(int param1) {
        this.cfr_renamed_184 = param1;
    }

    public void method2322(int param1) {
        this.cfr_renamed_182 = param1;
    }

    public String toString() {
        return _4ZXYEo.cfr_renamed_186("makeConcatWithConstants", new Object[]{}, (int)this.method2315(), (int)this.method2319(), (int)this.method2320(), (int)this.method2317(), (String)String.valueOf(this.method2313()), (String)String.valueOf(this.method2323()), (String)String.valueOf(this.method2314()));
    }

    protected boolean O(Object param1) {
        return param1 instanceof EventClickSlot;
    }

    public int hashCode() {
        int v2 = 59 + this.method2315();
        v2 = v2 * 59 + this.method2319();
        v2 = v2 * 59 + this.method2320();
        v2 = v2 * 59 + this.method2317();
        SlotActionType v3 = this.method2313();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        ItemStack v4 = this.method2323();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        Int2ObjectMap v5 = this.method2314();
        v2 = v2 * 59 + (v5 == null ? 43 : v5.hashCode());
        return v2;
    }

    public ItemStack method2323() {
        return this.ь;
    }
}



