// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.client.DeltaTracker.Timer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.client.TickFactorEvent;

@Mixin({Timer.class})
public class MixinDynamic {
   @Shadow
   private float deltaTicks;
   @Shadow
   private float deltaTickResidual;
   @Shadow
   private long lastMs;
   @Final
   @Shadow
   private float msPerTick;

   public MixinDynamic() {
   }

   @Inject(
      method = {"advanceGameTime(J)I"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void beginRenderTickHook(long time, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
      if (TickFactorEvent.INSTANCE.call()) {
         this.deltaTicks = (float)(time - this.lastMs) / this.msPerTick * TickFactorEvent.INSTANCE.getFactor();
         this.lastMs = time;
         this.deltaTickResidual = this.deltaTickResidual + this.deltaTicks;
         int deltaTickResidual2 = (int)this.deltaTickResidual;
         this.deltaTickResidual -= deltaTickResidual2;
         callbackInfoReturnable.setReturnValue(deltaTickResidual2);
      }
   }
}