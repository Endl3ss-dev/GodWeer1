/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.Packet
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import minced.enums.RECEIVE;
import net.minecraft.network.packet.Packet;

public class EventPacket
extends EventBase
implements CancellableEvent {
    RECEIVE field8962;
    Packet field8963;

    public boolean method2465() {
        return this.field8962 == RECEIVE.SEND;
    }

    public boolean method2466() {
        return this.field8962 == RECEIVE.RECEIVE;
    }

    public Packet method2467() {
        return this.field8963;
    }

    public EventPacket(Packet param1, RECEIVE param2) {
        this.field8963 = param1;
        this.field8962 = param2;
    }

    public void method2468(Packet param1) {
        this.field8963 = param1;
    }

    public RECEIVE method2469() {
        return this.field8962;
    }
}



