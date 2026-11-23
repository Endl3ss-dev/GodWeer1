// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.world.StepOnSlimeEvent;

@Mixin({SlimeBlock.class})
public class SlimeBlockMixin {

   public SlimeBlockMixin() {
   }

   @Inject(
      method = {"stepOn"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onSteppedOnHook(Level level, BlockPos blockPos, BlockState blockState, Entity entity, CallbackInfo callbackInfo) {
      if (StepOnSlimeEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}