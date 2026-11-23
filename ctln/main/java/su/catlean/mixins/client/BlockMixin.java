// deobf by pusia
package su.catlean.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.render.XRayBlockEvent;
import su.catlean.api.event.events.world.SlipperinessEvent;
import su.catlean.api.event.events.world.VelocityMultiplierEvent;

@Mixin({Block.class})
public class BlockMixin {

   public BlockMixin() {
   }

   @Inject(
      method = {"getSpeedFactor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getVelocityMultiplierHook(CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (VelocityMultiplierEvent.INSTANCE.call((Block)(Object)this)) {
         callbackInfoReturnable.setReturnValue(Blocks.DIRT.getSpeedFactor());
      }
   }

   @Inject(
      method = {"getFriction"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getSlipperinessHook(CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (SlipperinessEvent.INSTANCE.call((Block)(Object)this)) {
         callbackInfoReturnable.setReturnValue(Blocks.DIRT.getFriction());
      }
   }

   @ModifyReturnValue(
      method = {"shouldRenderFace"},
      at = {@At("RETURN")}
   )
   private static boolean shouldDrawSideHook(boolean flag, BlockState blockState, BlockState blockState2, Direction direction) {
      if (GofraState.INSTANCE.getXray()) {
         XRayBlockEvent xRayBlockEvent = new XRayBlockEvent();
         xRayBlockEvent.call(blockState.getBlock());
         return xRayBlockEvent.getCancelled();
      } else {
         return flag;
      }
   }
}