/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import sLM._4ZXYEo;

public class EventClickBlockRight
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_179;

    ClientPlayerEntity field8477;
    ClientWorld field8478;
    Hand field8479;
    BlockHitResult field8480;

    public ClientPlayerEntity method2302() {
        return this.field8477;
    }

    public int hashCode() {
        ClientPlayerEntity v3 = this.method2302();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        ClientWorld v4 = this.method2306();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        Hand v5 = this.method2304();
        v2 = v2 * 59 + (v5 == null ? 43 : v5.hashCode());
        BlockHitResult v6 = this.method2303();
        v2 = v2 * 59 + (v6 == null ? 43 : v6.hashCode());
        return v2;
    }

    public EventClickBlockRight(ClientPlayerEntity param1, ClientWorld param2, Hand param3, BlockHitResult param4) {
        this.field8477 = param1;
        this.field8478 = param2;
        this.field8479 = param3;
        this.field8480 = param4;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventClickBlockRight)) {
            return false;
        }
        EventClickBlockRight v2 = (EventClickBlockRight)param1;
        if (!v2.method2305(this)) {
            return false;
        }
        ClientPlayerEntity v3 = this.method2302();
        ClientPlayerEntity v4 = v2.method2302();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        ClientWorld v5 = this.method2306();
        ClientWorld v6 = v2.method2306();
        if (v5 == null ? v6 != null : !v5.equals(v6)) {
            return false;
        }
        Hand v7 = this.method2304();
        Hand v8 = v2.method2304();
        if (v7 == null ? v8 != null : !v7.equals(v8)) {
            return false;
        }
        BlockHitResult v9 = this.method2303();
        BlockHitResult v10 = v2.method2303();
        return !(v9 == null ? v10 != null : !v9.equals(v10));
    }

    public String toString() {
        return _4ZXYEo.ЦБ("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2302()), (String)String.valueOf(this.method2306()), (String)String.valueOf(this.method2304()), (String)String.valueOf(this.method2303()));
    }

    public BlockHitResult method2303() {
        return this.field8480;
    }

    public Hand method2304() {
        return this.field8479;
    }

    protected boolean method2305(Object param1) {
        return param1 instanceof EventClickBlockRight;
    }

    public ClientWorld method2306() {
        return this.field8478;
    }

    static {
        cfr_renamed_179 = "EventClickBlockRight(player=\u0001, world=\u0001, hand=\u0001, result=\u0001)";
    }
}

