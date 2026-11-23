// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.player.AttackBlockEvent;
import su.catlean.api.event.events.player.BreakBlockEvent;
import su.catlean.api.event.events.player.ClickSlotEvent;
import su.catlean.api.event.events.player.InteractBlockEvent;
import su.catlean.api.event.events.player.PickUpBlockEvent;
import su.catlean.api.event.events.player.PostInteractItemEvent;
import su.catlean.api.event.events.player.PreInteractItemEvent;
import su.catlean.api.event.events.player.StopUsingItemEvent;
import su.catlean.api.event.events.world.UpdateBlockBreakingProgressEvent;

@Mixin({MultiPlayerGameMode.class})
public class MultiPlayerGameModeMixin {

   public MultiPlayerGameModeMixin() {
   }

   @Inject(
      method = {"continueDestroyBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void updateBlockBreakingProgress(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (UpdateBlockBreakingProgressEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Inject(
      method = {"startDestroyBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void attackBlockHook(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (AttackBlockEvent.INSTANCE.call(direction, blockPos)) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Inject(
      method = {"destroyBlock"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void breakBlockHook(BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (BreakBlockEvent.INSTANCE.call(blockPos)) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Inject(
      method = {"handleContainerInput"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void clickSlotHook(int value, int value2, int value3, ContainerInput containerInput, Player player, CallbackInfo callbackInfo) {
      if (ClickSlotEvent.INSTANCE.call(containerInput, value2, value3, value)) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"releaseUsingItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void stopUsingItemHook(Player player, CallbackInfo callbackInfo) {
      if (StopUsingItemEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"useItemOn"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void interactBlockHook(
      LocalPlayer localPlayer, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable
   ) {
      if (InteractBlockEvent.INSTANCE.call(interactionHand, blockHitResult)) {
         callbackInfoReturnable.setReturnValue(InteractionResult.PASS);
      }
   }

   @Inject(
      method = {"useItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void preInteractItemHook(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
      if (PreInteractItemEvent.INSTANCE.call(interactionHand)) {
         callbackInfoReturnable.setReturnValue(InteractionResult.PASS);
         callbackInfoReturnable.cancel();
      }
   }

   @Inject(
      method = {"useItem"},
      at = {@At("RETURN")}
   )
   private void postInteractItemHook(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
      PostInteractItemEvent.INSTANCE.call();
   }

   @Inject(
      method = {"handlePickItemFromBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pickItemFromBlockHook(BlockPos blockPos, boolean flag, CallbackInfo callbackInfo) {
      if (PickUpBlockEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}