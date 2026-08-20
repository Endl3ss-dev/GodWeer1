/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 */
package minced.core;

import minced.core.CancellableEvent;
import net.minecraft.item.Item;

public class MiningStats
implements CancellableEvent {
    private String cfr_renamed_180;
    private Item eh;

    public MiningStats(String param1, Item param2) {
        this.cfr_renamed_180 = param1;
        this.eh = param2;
    }

    public String method2307() {
        return this.cfr_renamed_180;
    }

    public Item method2308() {
        return this.eh;
    }
}



