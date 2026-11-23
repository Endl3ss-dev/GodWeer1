// deobf by pusia
package su.catlean.mixins.render;

import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.catlean.api.event.events.render.DarknessEffectEvent;

@Mixin({LightmapRenderStateExtractor.class})
public class LightmapRenderStateExtractorMixin {

   public LightmapRenderStateExtractorMixin() {
   }

   @Inject(
      method = {"calculateDarknessScale"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getDarknessHook(LivingEntity livingEntity, float amount, float amount2, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (DarknessEffectEvent.INSTANCE.call()) {
         callbackInfoReturnable.setReturnValue(0.0F);
      }
   }
}