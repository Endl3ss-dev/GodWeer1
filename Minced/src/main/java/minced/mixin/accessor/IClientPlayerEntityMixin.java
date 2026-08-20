/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.ClientPlayerEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package minced.mixin.accessor;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ClientPlayerEntity.class})
public interface IClientPlayerEntityMixin {
    @Accessor(value="lastOnGround")
    public boolean lastOnGround();

    @Accessor(value="lastSprinting")
    public void setLastSprinting(boolean var1);

    @Accessor(value="lastSprinting")
    public boolean getLastSprinting();

    @Accessor(value="lastYaw")
    public float getLastYaw();

    @Accessor(value="lastPitch")
    public float getLastPitch();

    @Accessor(value="lastX")
    public void setLastX(double var1);

    @Accessor(value="lastBaseY")
    public void setLastBaseY(double var1);

    @Accessor(value="lastZ")
    public void setLastZ(double var1);

    @Accessor(value="lastYaw")
    public void setLastYaw(float var1);

    @Accessor(value="lastPitch")
    public void setLastPitch(float var1);

    @Accessor(value="lastOnGround")
    public void setLastOnGround(boolean var1);

    @Accessor(value="lastSneaking")
    public void setLastSneaking(boolean var1);

    @Accessor(value="lastHorizontalCollision")
    public void setLastHorizontalCollision(boolean var1);

    @Accessor(value="ticksSinceLastPositionPacketSent")
    public void setTicksSinceLastPositionPacketSent(int var1);
}



