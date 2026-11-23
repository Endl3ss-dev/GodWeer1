// deobf by pusia
package su.catlean.mixins.world;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.player.TridentUseEvent;
import su.catlean.api.event.events.player.TridentWeatherEvent;

@Mixin({TridentItem.class})
public abstract class TridentItemMixin {

   public TridentItemMixin() {
   }

   @Inject(
      method = {"releaseUsing"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onStoppedUsingHook(
      ItemStack itemStack, Level level, LivingEntity livingEntity, int value, CallbackInfoReturnable<Boolean> callbackInfoReturnable
   ) {
      if (livingEntity == Minecraft.getInstance().player
         && EnchantmentHelper.getTridentSpinAttackStrength(itemStack, Minecraft.getInstance().player) > 0.0F
         && TridentUseEvent.INSTANCE.call()) {
         callbackInfoReturnable.cancel();
         callbackInfoReturnable.setReturnValue(true);
      }
   }

   @Inject(
      method = {"use"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void useHook(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
      ItemStack itemInHand = player.getItemInHand(interactionHand);
      if (EnchantmentHelper.getTridentSpinAttackStrength(itemInHand, player) > 0.0F && !player.isInWaterOrRain() && TridentWeatherEvent.INSTANCE.call()) {
         player.startUsingItem(interactionHand);
         callbackInfoReturnable.setReturnValue(InteractionResult.CONSUME);
      }
   }
}