/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler
 */
package minced.core;

import io.netty.channel.ChannelHandler;

public interface ViaLoaderCompat
extends ChannelHandler {
    public String mincedbots$consumeLastNetworkFailure();

    public boolean mincedbots$isBotConnection();

    public String mincedbots$getBotConnectionName();

    public String mincedbots$getLastNetworkFailure();

    public void mincedbots$markAsBotConnection(String var1);

    public void mincedbots$setLastNetworkFailure(String var1);
}

