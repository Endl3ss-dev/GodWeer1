// deobf by pusia
package su.catlean.mixins.player;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.player.EventPlayerTravel;
import su.catlean.api.event.events.player.EventPostPlayerTravel;
import su.catlean.api.event.events.player.PostAttackEvent;
import su.catlean.api.event.events.player.ReachStateEvent;
import su.catlean.api.event.events.player.ShouldDismountEvent;

@Mixin({Player.class})
public class PlayerMixin {

   public PlayerMixin() {
   }

   @Inject(
      method = {"blockInteractionRange"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getBlockInteractionRangeHook(CallbackInfoReturnable<Double> callbackInfoReturnable) {
      if (ReachStateEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue((double)ReachStateEvent.INSTANCE.getBlockRange());
      }
   }

   @Inject(
      method = {"entityInteractionRange"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getEntityInteractionRangeHook(CallbackInfoReturnable<Double> callbackInfoReturnable) {
      if (ReachStateEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue((double)ReachStateEvent.INSTANCE.getEntityRange());
      }
   }

   @Inject(
      method = {"attack"},
      at = {@At("RETURN")}
   )
   public void attackAHook(CallbackInfo callbackInfo) {
      PostAttackEvent.INSTANCE.call();
   }

   @Inject(
      method = {"travel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onTravelhookPre(Vec3 vec3, CallbackInfo callbackInfo) {
      if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null && EventPlayerTravel.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"travel"},
      at = {@At("RETURN")}
   )
   private void onTravelhookPost(Vec3 vec3, CallbackInfo callbackInfo) {
      EventPostPlayerTravel.INSTANCE.call();
   }

   @Inject(
      method = {"wantsToStopRiding"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void shouldDismountHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (ShouldDismountEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }
}