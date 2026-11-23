// deobf by pusia
package su.catlean.mixins.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.network.SlowDownEvent;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PostPlayerUpdateEvent;
import su.catlean.api.event.events.player.PostSyncEvent;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.event.events.player.PushOutOfBlocksEvent;
import su.catlean.api.event.events.player.ShouldStopSprintingEvent;
import su.catlean.api.event.events.player.UsingItemEvent;

@Mixin(
   value = {LocalPlayer.class},
   priority = 800
)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

   public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
      super(clientLevel, gameProfile);
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void tickHook(CallbackInfo callbackInfo) {
      if (PlayerUpdateEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/AbstractClientPlayer;tick()V",
         shift = Shift.AFTER
      )}
   )
   public void postTickHook(CallbackInfo callbackInfo) {
      PostPlayerUpdateEvent.INSTANCE.call();
   }

   @Inject(
      method = {"sendPosition"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sendMovementPacketsHook(CallbackInfo callbackInfo) {
      if (PreSyncEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"sendPosition"},
      at = {@At("RETURN")}
   )
   private void sendMovementPacketsPostHook(CallbackInfo callbackInfo) {
      PostSyncEvent.INSTANCE.call();
   }

   @Inject(
      method = {"move"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/AbstractClientPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"
      )},
      cancellable = true
   )
   public void onMoveHook(MoverType moverType, @NotNull Vec3 vec3, CallbackInfo callbackInfo) {
      if (MoveEvent.INSTANCE.call(vec3.x, vec3.y, vec3.z)) {
         double value = this.getX();
         double value2 = this.getZ();
         super.move(moverType, new Vec3(MoveEvent.INSTANCE.getX(), MoveEvent.INSTANCE.getY(), MoveEvent.INSTANCE.getZ()));
         this.moveDist = this.moveDist + Mth.length((float)(this.getX() - value), (float)(this.getZ() - value2)) * 0.6F;
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"isMovingSlowly"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void shouldSlowDownHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (SlowDownEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Inject(
      method = {"isUsingItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isUsingItemHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (UsingItemEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue(true);
      }
   }

   @Inject(
      method = {"moveTowardsClosestSpace"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPushOutOfBlocksHook(double amount, double amount2, CallbackInfo callbackInfo) {
      if (PushOutOfBlocksEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"shouldStopRunSprinting"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void shouldStopSprintingHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (ShouldStopSprintingEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }
}