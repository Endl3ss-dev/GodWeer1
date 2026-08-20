package minced.core;


import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import sLM._4ZXYEo;

public class EventSlotClick
extends EventBase
implements CancellableEvent {
    public Slot slot;
    public int g2;
    public int г;
    public SlotActionType slotActionType;

    public EventSlotClick(Slot param1, int param2, int param3, SlotActionType param4) {
        this.slot = param1;
        this.g2 = param2;
        this.г = param3;
        this.slotActionType = param4;
    }

    public Slot net_minecraft_screen_slot_Slot_I() {
        return this.slot;
    }

    public SlotActionType net_minecraft_screen_slot_SlotActionType_I() {
        return this.slotActionType;
    }

    public void cfr_renamed_4(SlotActionType param1) {
        this.slotActionType = param1;
    }

    public String toString() {
        return _4ZXYEo.f13("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.net_minecraft_screen_slot_Slot_I()), (int)this.method2524(), (int)this.method2526(), (String)String.valueOf(this.net_minecraft_screen_slot_SlotActionType_I()));
    }

    public void method2522(int param1) {
        this.г = param1;
    }

    public int hashCode() {
        int v2 = 59 + this.method2524();
        v2 = v2 * 59 + this.method2526();
        Slot v3 = this.net_minecraft_screen_slot_Slot_I();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        SlotActionType v4 = this.net_minecraft_screen_slot_SlotActionType_I();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        return v2;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventSlotClick)) {
            return false;
        }
        EventSlotClick v2 = (EventSlotClick)param1;
        if (!v2.method2525(this)) {
            return false;
        }
        if (this.method2524() != v2.method2524()) {
            return false;
        }
        if (this.method2526() != v2.method2526()) {
            return false;
        }
        Slot v3 = this.net_minecraft_screen_slot_Slot_I();
        Slot v4 = v2.net_minecraft_screen_slot_Slot_I();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        SlotActionType v5 = this.net_minecraft_screen_slot_SlotActionType_I();
        SlotActionType v6 = v2.net_minecraft_screen_slot_SlotActionType_I();
        return !(v5 == null ? v6 != null : !v5.equals(v6));
    }

    public void I(Slot param1) {
        this.slot = param1;
    }

    public void method2523(int param1) {
        this.g2 = param1;
    }

    public int method2524() {
        return this.g2;
    }

    protected boolean method2525(Object param1) {
        return param1 instanceof EventSlotClick;
    }

    public int method2526() {
        return this.г;
    }
}


