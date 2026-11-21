package net.skycore.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.RotationController;
import net.skycore.client.module.movement.ScaffoldModule;
import net.skycore.client.module.combat.AttackAuraModule;
import net.skycore.client.module.combat.AuraCameraJitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraSyncCameraRotationMixin {
   @Inject(method = "alignWithEntity", at = @At("HEAD"))
   private void skycore$syncCameraRotation(float value, CallbackInfo callbackInfo) {
      Camera camera = (Camera)(Object)this;
      Entity entity = camera.entity();
      if (entity != null) {
         RotationController.applyRenderInterpolation();
         RotationController.syncFreeLook(entity.getViewYRot(value), entity.getViewXRot(value));
      }
   }

   // Apply once, after vanilla/F5/free-look have chosen the base angles. Never feed this offset into aim or packets.
   @Inject(method = "alignWithEntity", at = @At("RETURN"))
   private void skycore$applyAuraJitter(float partialTick, CallbackInfo ci) {
      Minecraft mc = Minecraft.getInstance();
      Camera camera = (Camera)(Object)this;
      if (mc.player == null || camera.entity() != mc.player) return;
      AuraCameraJitter.Offset offset = SkycoreClient.modules().get(AttackAuraModule.class)
         .map(AttackAuraModule::cameraJitter).orElse(AuraCameraJitter.Offset.NONE);
      if (offset.yaw() != 0 || offset.pitch() != 0) {
         ((CameraInvokeSetRotationAccessor)camera).skycore$invokeSetRotation(camera.yRot() + offset.yaw(), Mth.clamp(camera.xRot() + offset.pitch(), -90.0F, 90.0F));
      }
   }

   @Redirect(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
   private void skycore$applyVisualFreeLook(Camera camera, float angle, float angle2) {
      Minecraft instance = Minecraft.getInstance();
      ScaffoldModule scaffold = SkycoreClient.modules().get(ScaffoldModule.class).orElse(null);
      if (scaffold != null && scaffold.freeCameraActive() && camera.entity() == instance.player) {
         float yaw = scaffold.freeCameraYaw();
         float pitch = scaffold.freeCameraPitch();
         if (instance.options.getCameraType().isMirrored()) { yaw += 180; pitch = -pitch; }
         ((CameraInvokeSetRotationAccessor)camera).skycore$invokeSetRotation(yaw, pitch);
         return;
      }
      if (RotationController.isActive() && !RotationController.isClientLook() && camera.entity() != null && camera.entity() == instance.player) {
         float freeYaw = RotationController.getFreeYaw();
         float freePitch = RotationController.getFreePitch();
         if (instance.options.getCameraType().isMirrored()) {
            freeYaw += 180.0F;
            freePitch = -freePitch;
         }

         ((CameraInvokeSetRotationAccessor)camera).skycore$invokeSetRotation(freeYaw, freePitch);
      } else {
         ((CameraInvokeSetRotationAccessor)camera).skycore$invokeSetRotation(angle, angle2);
      }
   }
}
