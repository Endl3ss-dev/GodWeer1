// deobf by pusia
package su.catlean.mixins.world;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import su.catlean.api.event.events.world.FireWorkVectorEvent;
import su.catlean.api.event.events.world.FireWorkVelocityEvent;

@Mixin({FireworkRocketEntity.class})
public class FireWorkEntityMixin {

   @Shadow
   private LivingEntity attachedToEntity;

   public FireWorkEntityMixin() {
   }

   @Redirect(
      method = {"tick"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getLookAngle()Lnet/minecraft/world/phys/Vec3;"
      )
   )
   private Vec3 tickHook(LivingEntity livingEntity) {
      return this.attachedToEntity == Minecraft.getInstance().player && FireWorkVectorEvent.INSTANCE.call(this.attachedToEntity.getLookAngle())
         ? FireWorkVectorEvent.INSTANCE.getVector()
         : this.attachedToEntity.getLookAngle();
   }

   @ModifyArgs(
      method = {"tick"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
      )
   )
   public void tickHook2(Args args) {
      if (this.attachedToEntity == Minecraft.getInstance().player && FireWorkVelocityEvent.INSTANCE.call()) {
         args.set(0, FireWorkVelocityEvent.INSTANCE.getVelocity());
      }
   }
}