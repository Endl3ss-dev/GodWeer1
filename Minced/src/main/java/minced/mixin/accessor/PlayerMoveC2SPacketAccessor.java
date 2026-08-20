/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package minced.mixin.accessor;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={PlayerMoveC2SPacket.class})
public interface PlayerMoveC2SPacketAccessor {
    @Accessor(value="onGround")
    @Mutable
    public void setOnGround(boolean var1);

    @Accessor(value="onGround")
    public boolean getOnGround();

    @Accessor(value="x")
    @Mutable
    public void setX(double var1);

    @Accessor(value="y")
    @Mutable
    public void setY(double var1);

    @Accessor(value="z")
    @Mutable
    public void setZ(double var1);

    @Accessor(value="yaw")
    @Mutable
    public void setYaw(float var1);

    @Accessor(value="pitch")
    @Mutable
    public void setPitch(float var1);
}

