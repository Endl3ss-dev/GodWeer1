// deobf by pusia
package su.catlean.mixins.world;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.player.CollisionEvent;

@Mixin(
   value = {BlockCollisions.class},
   priority = 800
)
public abstract class BlockCollisionsMixin {

   public BlockCollisionsMixin() {
   }

   @Redirect(
      method = {"computeNext"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
      )
   )
   private BlockState computeNextHook(BlockGetter blockGetter, BlockPos blockPos) {
      if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null && GofraState.INSTANCE.getModifyCollisions()) {
         CollisionEvent collisionEvent = new CollisionEvent();
         collisionEvent.call(blockGetter.getBlockState(blockPos), blockPos);
         return collisionEvent.getState();
      } else {
         return blockGetter.getBlockState(blockPos);
      }
   }
}