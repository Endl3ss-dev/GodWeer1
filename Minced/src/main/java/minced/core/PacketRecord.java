/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.Packet
 */
package minced.core;

import java.util.function.Consumer;
import net.minecraft.network.packet.Packet;

public class PacketRecord
implements Consumer {
    public Packet field8074;
    public long field8075;

    public PacketRecord(Packet param1, long param2) {
        this.field8074 = param1;
        this.field8075 = param2;
    }

    @Override
    public void accept(Object param1) {
    }
}

