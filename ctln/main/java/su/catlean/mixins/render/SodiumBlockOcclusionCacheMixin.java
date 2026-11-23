// deobf by pusia
package su.catlean.mixins.render;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.render.XRayBlockEvent;

@Pseudo
@Mixin(
   targets = {"net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache"},
   remap = false
)
public class SodiumBlockOcclusionCacheMixin {

   public SodiumBlockOcclusionCacheMixin() {
   }

   @Inject(
      method = {"shouldDrawSide"},
      at = {@At("RETURN")},
      cancellable = true
   )
   void shouldDrawSideHook(
      BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> callbackInfoReturnable
   ) {
      if (GofraState.INSTANCE.getXray()) {
         XRayBlockEvent xRayBlockEvent = new XRayBlockEvent();
         xRayBlockEvent.call(blockState.getBlock());
         callbackInfoReturnable.setReturnValue(xRayBlockEvent.getCancelled());
      }
   }
}