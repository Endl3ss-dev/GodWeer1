package minced.core;

import net.minecraft.screen.slot.Slot;

public final class RecordClass233 {
    public final Slot slot;
    public final int price;
    public final int count;

    public RecordClass233(Slot slot, int price, int count) {
        this.slot = slot;
        this.price = price;
        this.count = count;
    }

    public Slot slot() { return slot; }
    public int price() { return price; }
    public int count() { return count; }
}
