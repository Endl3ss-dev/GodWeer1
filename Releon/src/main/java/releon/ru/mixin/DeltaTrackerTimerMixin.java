package releon.ru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.RenderTickCounter.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import releon.ru.api.module.impl.movement.Speed;
import releon.ru.api.module.impl.movement.Timer;

@Mixin({Dynamic.class})
public abstract class DeltaTrackerTimerMixin {
   public DeltaTrackerTimerMixin() {
   }

   @ModifyExpressionValue(
      method = {"beginRenderTick(J)I"},
      at = {@At(
         value = "INVOKE",
         target = "Lit/unimi/dsi/fastutil/floats/FloatUnaryOperator;apply(F)F"
      )}
   )
   private float releon$applyTimerMultiplier(float millisecondsPerTick) {
      float multiplier = Speed.hasActiveBoostCycle() ? Speed.getReallyWorldMultiplier() : Timer.getCurrentMultiplier();
      return millisecondsPerTick / multiplier;
   }
}
