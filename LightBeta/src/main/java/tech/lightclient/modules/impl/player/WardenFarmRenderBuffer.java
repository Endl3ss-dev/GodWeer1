package tech.lightclient.modules.impl.player;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;

final class WardenFarmRenderBuffer {
    private static final BufferAllocator ALLOCATOR = new BufferAllocator(1 << 16);
    private static final VertexConsumerProvider.Immediate IMMEDIATE = VertexConsumerProvider.immediate(ALLOCATOR);
    static VertexConsumerProvider.Immediate getImmediate() { return IMMEDIATE; }
    static void flush() { IMMEDIATE.draw(); ALLOCATOR.clear(); }
}
