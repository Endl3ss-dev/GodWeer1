// deobf by pusia
package su.catlean.mixins.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.client.CloseEvent;
import su.catlean.api.event.events.client.InitEvent;
import su.catlean.api.event.events.client.PostTickEvent;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.event.events.network.DisconnectEvent;
import su.catlean.api.event.events.network.PostTasksProcessEvent;
import su.catlean.api.event.events.player.AttackEvent;
import su.catlean.api.event.events.player.ItemUseEvent;
import su.catlean.api.event.events.player.UpdateCrosshairTarget;
import su.catlean.api.event.events.render.FlipFrameEvent;

@Mixin({Minecraft.class})
public class MinecraftMixin {

   public MinecraftMixin() {
   }

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   void postWindowInit(GameConfig gameConfig, CallbackInfo callbackInfo) {
      InitEvent.INSTANCE.call();
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   void preTickHook(CallbackInfo callbackInfo) {
      TickEvent.INSTANCE.call();
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   void postTickHook(CallbackInfo callbackInfo) {
      PostTickEvent.INSTANCE.call();
   }

   @Inject(
      method = {"renderFrame"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/LevelRenderer;endFrame()V",
         shift = Shift.AFTER
      )}
   )
   private void afterRenderFrame(boolean flag, CallbackInfo callbackInfo) {
      FlipFrameEvent.INSTANCE.call();
   }

   @Inject(
      method = {"startAttack"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void doAttackHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (AttackEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Inject(
      method = {"close"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void closeHook(CallbackInfo callbackInfo) {
      if (CloseEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V"},
      at = {@At("HEAD")}
   )
   public void disconnect(Screen screen, boolean flag, boolean flag2, CallbackInfo callbackInfo) {
      DisconnectEvent.INSTANCE.call();
   }

   @Inject(
      method = {"runTick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Minecraft;runAllTasks()V",
         shift = Shift.AFTER
      )}
   )
   private void render(boolean flag, CallbackInfo callbackInfo, @Local int value) {
      PostTasksProcessEvent.INSTANCE.call();
   }

   @Inject(
      method = {"startUseItem"},
      at = {@At("HEAD")}
   )
   private void doItemUse(CallbackInfo callbackInfo) {
      ItemUseEvent.INSTANCE.call();
   }

   @Inject(
      method = {"pick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;raycastHitResult(FLnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/HitResult;"
      )},
      cancellable = true
   )
   private void onUpdateTargetedEntity(float amount, CallbackInfo callbackInfo) {
      if (UpdateCrosshairTarget.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}