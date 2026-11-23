// deobf by pusia
package su.catlean.mixins.world;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.player.FinishUsingItemEvent;
import su.catlean.api.event.events.player.StackCollectEvent;

@Mixin({ItemStack.class})
public class ItemStackMixin {

   public ItemStackMixin() {
   }

   @Inject(
      method = {"finishUsingItem"},
      at = {@At("HEAD")}
   )
   private void onFinishUsing(Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> callbackInfoReturnable) {
      if (livingEntity == Minecraft.getInstance().player) {
         FinishUsingItemEvent.INSTANCE.call((ItemStack)(Object)this);
      }
   }

   @Inject(
      method = {"setPopTime"},
      at = {@At("HEAD")}
   )
   private void onRenewItem(int value, CallbackInfo callbackInfo) {
      if (value == 5) {
         StackCollectEvent.INSTANCE.setItemStack((ItemStack)(Object)this);
         StackCollectEvent.INSTANCE.call();
      }
   }
}