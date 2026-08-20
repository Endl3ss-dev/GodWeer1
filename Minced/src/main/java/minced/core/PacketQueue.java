/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.CodecOutputList$CodecOutputListRecycler
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.packet.Packet
 */
package minced.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;

public final class PacketQueue {
    static String field12245;
    static MinecraftClient I = MinecraftClient.getInstance();
    static ObjectArrayList field12246;

    private PacketQueue() {
        throw new UnsupportedOperationException(field12245);
    }

    public static void method3502(Packet param0) {
        PacketQueue.I.player.networkHandler.sendPacket(param0);
    }

    public static void method3503() {
        field12246 = new ObjectArrayList();
    }

    public static ObjectArrayList method3504() {
        return field12246;
    }

    public static void method3505(Packet param0) {
        field12246.add((Object)param0);
        PacketQueue.I.player.networkHandler.sendPacket(param0);
    }

    static {
        field12245 = "This is a utility class and cannot be instantiated";
        PacketQueue.method3503();
    }
}



