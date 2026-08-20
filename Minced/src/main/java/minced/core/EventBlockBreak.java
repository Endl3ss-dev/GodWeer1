/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.util.math.BlockPos
 *  sLM.4ZXYEo
 *  sg.ec.2б$Ч
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import sLM._4ZXYEo;
import sg.ec._2b;

public class EventBlockBreak
extends EventBase
implements CancellableEvent {
    private BlockState sh;
    private BlockPos cfr_renamed_216;
    private _2b.ch н;

    public BlockPos method2582() {
        return this.cfr_renamed_216;
    }

    public String toString() {
        return _4ZXYEo.Цф("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2583()), (String)String.valueOf(this.method2582()), (String)String.valueOf(this.method2584()));
    }

    public BlockState method2583() {
        return this.sh;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventBlockBreak)) {
            return false;
        }
        EventBlockBreak v2 = (EventBlockBreak)param1;
        if (!v2.method2587(this)) {
            return false;
        }
        BlockState v3 = this.method2583();
        BlockState v4 = v2.method2583();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        BlockPos v5 = this.method2582();
        BlockPos v6 = v2.method2582();
        if (v5 == null ? v6 != null : !v5.equals(v6)) {
            return false;
        }
        _2b.ch v7 = this.method2584();
        _2b.ch v8 = v2.method2584();
        return !(v7 == null ? v8 != null : !v7.equals(v8));
    }

    public EventBlockBreak(BlockState param1, BlockPos param2, _2b.ch param3) {
        this.sh = param1;
        this.cfr_renamed_216 = param2;
        this.н = param3;
    }

    public _2b.ch method2584() {
        return this.н;
    }

    public void method2585(_2b.ch param1) {
        this.н = param1;
    }

    public void method2586(BlockState param1) {
        this.sh = param1;
    }

    protected boolean method2587(Object param1) {
        return param1 instanceof EventBlockBreak;
    }

    public void cfr_underscore(BlockPos param1) {
        this.cfr_renamed_216 = param1;
    }

    public int hashCode() {
        BlockState v3 = this.method2583();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        BlockPos v4 = this.method2582();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        _2b.ch v5 = this.method2584();
        v2 = v2 * 59 + (v5 == null ? 43 : v5.hashCode());
        return v2;
    }
}

