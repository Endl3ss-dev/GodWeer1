// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.world.WaterPushEvent;

@Mixin({FlowingFluid.class})
public abstract class FlowingFluidMixin {

   public FlowingFluidMixin() {
   }

   @Inject(
      method = {"getFlow"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getVelocityHook(BlockGetter blockGetter, BlockPos blockPos, FluidState fluidState, CallbackInfoReturnable<Vec3> callbackInfoReturnable) {
      if (WaterPushEvent.INSTANCE.call(blockPos, (Fluid)(Object)this, fluidState, Vec3.ZERO)) {
         callbackInfoReturnable.setReturnValue(WaterPushEvent.INSTANCE.getVec());
      }
   }
}