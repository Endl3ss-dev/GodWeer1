/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package minced.mixin.accessor;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={LivingEntity.class})
public interface ILivingEntityMixin {
    @Invoker(value="getGravity")
    public double invokeGetGravity();

    @Accessor(value="lastAttackedTicks")
    public int getLastAttackedTicks();

    @Accessor(value="jumpingCooldown")
    public void setJumpCooldown(int var1);

    @Invoker(value="getEffectiveGravity")
    public double invokeGetEffectiveGravity();

    @Accessor(value="serverX")
    public double getServerX();

    @Accessor(value="serverY")
    public double getServerY();

    @Accessor(value="serverZ")
    public double getServerZ();

    @Accessor(value="serverYaw")
    public double getServerYaw();

    @Accessor(value="serverPitch")
    public double getServerPitch();
}



