// deobf by pusia
package su.catlean.mixins.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.player.CalcBlockBreakingDeltaEvent;

@Mixin({BlockBehaviour.class})
public class BlockBehaviourMixin {

   public BlockBehaviourMixin() {
   }

   @Inject(
      method = {"getDestroyProgress"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void calcBlockBreakingDeltaHook(
      BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Float> callbackInfoReturnable
   ) {
      if (CalcBlockBreakingDeltaEvent.INSTANCE.call(blockPos, blockState)) {
         callbackInfoReturnable.setReturnValue(CalcBlockBreakingDeltaEvent.INSTANCE.getDelta());
      }
   }

   @Inject(
      method = {"getShadeBrightness"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getShadeBrightnessHook(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (GofraState.INSTANCE.getXray()) {
         callbackInfoReturnable.setReturnValue(1.0F);
      }
   }
}