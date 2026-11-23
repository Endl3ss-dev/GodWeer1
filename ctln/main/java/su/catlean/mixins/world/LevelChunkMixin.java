// deobf by pusia
package su.catlean.mixins.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.world.BlockStateEvent;

@Mixin({LevelChunk.class})
public class LevelChunkMixin {

   @Shadow
   @Final
   Level level;

   public LevelChunkMixin() {
   }

   @Inject(
      method = {"setBlockState"},
      at = {@At("RETURN")}
   )
   private void setBlockStateHook(BlockPos blockPos, BlockState blockState, int value, CallbackInfoReturnable<BlockState> callbackInfoReturnable) {
      if (this.level.isClientSide()) {
         BlockStateEvent.INSTANCE.call(blockPos, blockState, (BlockState)callbackInfoReturnable.getReturnValue());
      }
   }
}