// deobf by pusia
package su.catlean.mixins.world;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.player.AfterElytraEvent;
import su.catlean.api.event.events.player.FallFlyingEvent;
import su.catlean.api.event.events.player.HealthEvent;
import su.catlean.api.event.events.player.JumpEvent;
import su.catlean.api.event.events.player.PreElytraEvent;
import su.catlean.api.event.events.player.SprintEvent;
import su.catlean.api.event.events.render.SwingDurationEvent;

@Mixin({LivingEntity.class})
public abstract class EntityLivingMixin {

   public EntityLivingMixin() {
   }

   @Shadow
   protected abstract float getJumpPower();

   @Inject(
      method = {"travel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void travelHook(Vec3 vec3, CallbackInfo callbackInfo) {
      if ((LivingEntity)(Object)this == Minecraft.getInstance().player) {
         if (PreElytraEvent.INSTANCE.call(Minecraft.getInstance().player.getDeltaMovement())) {
            Minecraft.getInstance().player.move(MoverType.SELF, PreElytraEvent.INSTANCE.getVec());
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"travel"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void travelPostHook(Vec3 vec3, CallbackInfo callbackInfo) {
      if ((LivingEntity)(Object)this == Minecraft.getInstance().player) {
         if (AfterElytraEvent.INSTANCE.call(Minecraft.getInstance().player.getDeltaMovement())) {
            Minecraft.getInstance().player.move(MoverType.SELF, AfterElytraEvent.INSTANCE.getVec());
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"isFallFlying"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void isGlidingHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if ((LivingEntity)(Object)this == Minecraft.getInstance().player) {
         if (FallFlyingEvent.INSTANCE.call((Boolean)callbackInfoReturnable.getReturnValue())) {
            callbackInfoReturnable.setReturnValue(FallFlyingEvent.INSTANCE.getValue());
         }
      }
   }

   @Inject(
      method = {"jumpFromGround"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void jumpHook(CallbackInfo callbackInfo) {
      if ((LivingEntity)(Object)this == Minecraft.getInstance().player) {
         LivingEntity thisValue = (LivingEntity)(Object)this;
         if (JumpEvent.INSTANCE.call(thisValue.getYRot(), this.getJumpPower(), 0.2F)) {
            callbackInfo.cancel();
            if (JumpEvent.INSTANCE.getY() > 1.0E-5F) {
               Vec3 deltaMovement = thisValue.getDeltaMovement();
               thisValue.setDeltaMovement(deltaMovement.x, Math.max((double)JumpEvent.INSTANCE.getY(), deltaMovement.y), deltaMovement.z);
               if (thisValue.isSprinting()) {
                  float yaw = JumpEvent.INSTANCE.getYaw() * (float) (Math.PI / 180.0);
                  thisValue.addDeltaMovement(new Vec3(-Math.sin(yaw) * JumpEvent.INSTANCE.getXz(), 0.0, Math.cos(yaw) * JumpEvent.INSTANCE.getXz()));
               }

               thisValue.needsSync = true;
            }
         }
      }
   }

   @ModifyVariable(
      method = {"setSprinting"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private boolean setSprintingHook(boolean flag) {
      SprintEvent.INSTANCE.setSprinting(flag);
      SprintEvent.INSTANCE.call();
      return SprintEvent.INSTANCE.getSprinting();
   }

   @Inject(
      method = {"setHealth"},
      at = {@At("HEAD")}
   )
   private void setHealth(float amount, CallbackInfo callbackInfo) {
      if ((Object)this == Minecraft.getInstance().player) {
         HealthEvent.INSTANCE.call(amount);
      }
   }

   @Inject(
      method = {"getCurrentSwingDuration"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getArmSwingAnimationEnd(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
      if (SwingDurationEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue(SwingDurationEvent.INSTANCE.getValue());
      }
   }
}