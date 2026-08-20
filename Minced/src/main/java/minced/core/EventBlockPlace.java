/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import sLM._4ZXYEo;

public class EventBlockPlace
extends EventBase
implements CancellableEvent {
    private Block sh;
    private BlockPos I;

    public int hashCode() {
        Block v3 = this.method2395();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        BlockPos v4 = this.method2394();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        return v2;
    }

    public BlockPos method2394() {
        return this.I;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventBlockPlace)) {
            return false;
        }
        EventBlockPlace v2 = (EventBlockPlace)param1;
        if (!v2.method2396(this)) {
            return false;
        }
        Block v3 = this.method2395();
        Block v4 = v2.method2395();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        BlockPos v5 = this.method2394();
        BlockPos v6 = v2.method2394();
        return !(v5 == null ? v6 != null : !v5.equals(v6));
    }

    public Block method2395() {
        return this.sh;
    }

    protected boolean method2396(Object param1) {
        return param1 instanceof EventBlockPlace;
    }

    public EventBlockPlace(Block param1, BlockPos param2) {
        this.sh = param1;
        this.I = param2;
    }

    public String toString() {
        return _4ZXYEo.弟必("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2395()), (String)String.valueOf(this.method2394()));
    }
}



