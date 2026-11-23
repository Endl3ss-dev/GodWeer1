// deobf by pusia
package su.catlean.mixins.world;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import su.catlean.api.event.events.player.BoxContractEvent;
import su.catlean.api.event.events.player.EntityPushEvent;
import su.catlean.api.event.events.player.FixVelocityEvent;
import su.catlean.api.event.events.player.SetPoseEvent;
import su.catlean.interfaces.IEntity;

@Mixin({Entity.class})
public abstract class EntityMixin implements IEntity {

   @Shadow
   private Vec3 position;

   public EntityMixin() {
   }

   @Shadow
   public abstract AABB getBoundingBox();

   @Override
   public Vec3 catLean$getPosition() {
      return this.position;
   }

   @Inject(
      method = {"getPose"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void setPoseHook(CallbackInfoReturnable<Pose> callbackInfoReturnable) {
      if ((Object)this == Minecraft.getInstance().player && SetPoseEvent.INSTANCE.call()) {
         callbackInfoReturnable.cancel();
         callbackInfoReturnable.setReturnValue(SetPoseEvent.INSTANCE.getPose());
      }
   }

   @Inject(
      method = {"moveRelative"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void updateVelocityHook(float amount, Vec3 vec3, CallbackInfo callbackInfo) {
      if ((Object)this == Minecraft.getInstance().player && FixVelocityEvent.INSTANCE.call(vec3, amount, Minecraft.getInstance().player.getYRot())) {
         callbackInfo.cancel();
         Minecraft.getInstance().player.setDeltaMovement(Minecraft.getInstance().player.getDeltaMovement().add(FixVelocityEvent.INSTANCE.getVelocity()));
      }
   }

   @ModifyArgs(
      method = {"push"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;push(DDD)V"
      )
   )
   public void pushAwayFromHook(Args args) {
      if ((Entity)(Object)this == Minecraft.getInstance().player && EntityPushEvent.INSTANCE.call()) {
         args.set(0, 0.0);
         args.set(1, 0.0);
         args.set(2, 0.0);
      }
   }

   @Inject(
      method = {"getBoundingBox"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getBoundingBoxHook(CallbackInfoReturnable<AABB> callbackInfoReturnable) {
      if ((Entity)(Object)this == Minecraft.getInstance().player && BoxContractEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue(BoxContractEvent.INSTANCE.getValue());
         callbackInfoReturnable.cancel();
      }
   }
}