package releon.ru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.api.events.impl.CameraPositionEvent;
import releon.ru.api.module.impl.combat.AuraModule;
import releon.ru.api.module.impl.combat.aura.Angle;
import releon.ru.api.module.impl.combat.aura.AngleConnection;
import releon.ru.api.module.impl.player.FreeLook;
import releon.ru.api.module.impl.visual.Animations;
import releon.ru.manager.Manager;
import releon.ru.utils.modules.warden.rotation.FreeLookController;

@Mixin({Camera.class})
public abstract class CameraMixin {
   public CameraMixin() {
   }

   @Shadow
   protected abstract void setPos(double var1, double var3, double var5);

   @Shadow
   protected abstract void moveBy(float var1, float var2, float var3);

   @ModifyExpressionValue(
      method = {"update"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;getYaw(F)F"
      )}
   )
   private float releon$freeLookYaw(float original) {
      if (FreeLookController.isActive()) {
         return FreeLookController.getFreeYaw();
      } else {
         Angle angle = FreeLook.getActiveAngle();
         if (angle != null) {
            return angle.getYaw();
         } else {
            Angle legitAngle = this.releon$getLegitAuraCameraAngle();
            return legitAngle != null ? legitAngle.getYaw() : original;
         }
      }
   }

   @ModifyExpressionValue(
      method = {"update"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;getPitch(F)F"
      )}
   )
   private float releon$freeLookPitch(float original) {
      if (FreeLookController.isActive()) {
         return FreeLookController.getFreePitch();
      } else {
         Angle angle = FreeLook.getActiveAngle();
         if (angle != null) {
            return angle.getPitch();
         } else {
            Angle legitAngle = this.releon$getLegitAuraCameraAngle();
            return legitAngle != null ? legitAngle.getPitch() : original;
         }
      }
   }

   @Unique
   private Angle releon$getLegitAuraCameraAngle() {
      AuraModule aura = AuraModule.getInstance();
      return aura != null && aura.isEnabled() && aura.isLegitMode() ? AngleConnection.INSTANCE.getCurrentAngle() : null;
   }

   @Inject(
      method = {"setPos(Lnet/minecraft/util/math/Vec3d;)V"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void releon$cameraPositionHook(Vec3d pos, CallbackInfo ci) {
      CameraPositionEvent event = Manager.postEvent(new CameraPositionEvent(pos));
      Vec3d nextPos = event.getPos();
      if (nextPos != null && Double.isFinite(nextPos.x) && Double.isFinite(nextPos.y) && Double.isFinite(nextPos.z)) {
         if (!(nextPos.squaredDistanceTo(pos) <= 1.0E-8)) {
            this.setPos(nextPos.x, nextPos.y, nextPos.z);
            ci.cancel();
         }
      }
   }

   @ModifyExpressionValue(
      method = {"update"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/Camera;clipToSpace(F)F"
      )}
   )
   private float releon$animateThirdPersonDistance(float zoom) {
      return zoom * Animations.perspectiveProgress(true);
   }

   @Inject(
      method = {"update"},
      at = {@At("TAIL")}
   )
   private void releon$animateFirstPersonReturn(World level, Entity entity, boolean detached, boolean reverse, float partialTick, CallbackInfo ci) {
      if (!detached && Animations.perspectiveEnabled()) {
         float remainingThirdPerson = Animations.perspectiveProgress(false);
         if (remainingThirdPerson > 0.001F) {
            this.moveBy(-4.0F * remainingThirdPerson, 0.0F, 0.0F);
         }
      }
   }
}
