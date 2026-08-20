/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInboundHandler
 *  net.minecraft.network.listener.PacketListener
 *  net.minecraft.registry.DynamicRegistryManager$Immutable
 */
package minced.core;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandler;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.registry.DynamicRegistryManager;

public interface ConfigNetworkHandler
extends ChannelHandler,
ChannelInboundHandler {
    public PacketListener mincedbots$createPlayPacketListener(DynamicRegistryManager.Immutable var1);

    default public void mincedbots$afterReadyTransition() {
    }
}



