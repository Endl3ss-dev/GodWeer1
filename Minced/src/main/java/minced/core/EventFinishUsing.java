/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import sLM._4ZXYEo;

public class EventFinishUsing
extends EventBase
implements CancellableEvent {
    private World н;
    private ItemStack ф;
    private LivingEntity Щ;

    public World method2340() {
        return this.н;
    }

    public ItemStack method2341() {
        return this.ф;
    }

    public String toString() {
        return _4ZXYEo.ЧЙ("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2341()), (String)String.valueOf(this.method2340()), (String)String.valueOf(this.method2346()));
    }

    public void method2342(World param1) {
        this.н = param1;
    }

    public void method2343(ItemStack param1) {
        this.ф = param1;
    }

    public void method2344(LivingEntity param1) {
        this.Щ = param1;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventFinishUsing)) {
            return false;
        }
        EventFinishUsing v2 = (EventFinishUsing)param1;
        if (!v2.method2345(this)) {
            return false;
        }
        ItemStack v3 = this.method2341();
        ItemStack v4 = v2.method2341();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        World v5 = this.method2340();
        World v6 = v2.method2340();
        if (v5 == null ? v6 != null : !v5.equals(v6)) {
            return false;
        }
        LivingEntity v7 = this.method2346();
        LivingEntity v8 = v2.method2346();
        return !(v7 == null ? v8 != null : !v7.equals(v8));
    }

    protected boolean method2345(Object param1) {
        return param1 instanceof EventFinishUsing;
    }

    public int hashCode() {
        ItemStack v3 = this.method2341();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        World v4 = this.method2340();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        LivingEntity v5 = this.method2346();
        v2 = v2 * 59 + (v5 == null ? 43 : v5.hashCode());
        return v2;
    }

    public LivingEntity method2346() {
        return this.Щ;
    }

    public EventFinishUsing(ItemStack param1, World param2, LivingEntity param3) {
        this.ф = param1;
        this.н = param2;
        this.Щ = param3;
    }
}



