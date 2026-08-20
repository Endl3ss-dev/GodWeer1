/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.util.math.BlockPos
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import sLM._4ZXYEo;

public class EventBlockRayTrace
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_187;

    BlockPos field8483;
    BlockState field8484;

    protected boolean method2324(Object param1) {
        return param1 instanceof EventBlockRayTrace;
    }

    public void method2325(BlockPos param1) {
        this.field8483 = param1;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventBlockRayTrace)) {
            return false;
        }
        EventBlockRayTrace v2 = (EventBlockRayTrace)param1;
        if (!v2.method2324(this)) {
            return false;
        }
        BlockState v3 = this.method2327();
        BlockState v4 = v2.method2327();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        BlockPos v5 = this.method2328();
        BlockPos v6 = v2.method2328();
        return !(v5 == null ? v6 != null : !v5.equals(v6));
    }

    public String toString() {
        return _4ZXYEo.дЖ("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2327()), (String)String.valueOf(this.method2328()));
    }

    public void method2326(BlockState param1) {
        this.field8484 = param1;
    }

    public EventBlockRayTrace(BlockState param1, BlockPos param2) {
        this.field8484 = param1;
        this.field8483 = param2;
    }

    public int hashCode() {
        BlockState v3 = this.method2327();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        BlockPos v4 = this.method2328();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        return v2;
    }

    public BlockState method2327() {
        return this.field8484;
    }

    public BlockPos method2328() {
        return this.field8483;
    }

    static {
        cfr_renamed_187 = "EventBlockRayTrace(state=\u0001, pos=\u0001)";
    }
}

