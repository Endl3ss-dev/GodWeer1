package tech.lightclient.modules.impl.player;

import net.minecraft.util.math.MathHelper;

final class WardenFarmRotation {
    final float floatValue;
    final float floatValue2;
    WardenFarmRotation(float yaw, float pitch) { this.floatValue = yaw; this.floatValue2 = pitch; }
    WardenFarmRotation(net.minecraft.entity.Entity entity) { this(entity.getYaw(), entity.getPitch()); }
    float measure(WardenFarmRotation other) { return (float)Math.hypot(MathHelper.wrapDegrees(other.floatValue - floatValue), other.floatValue2 - floatValue2); }
}
