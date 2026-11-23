// deobf by pusia
package su.catlean.mixins.render;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.FrustrumEvent;

@Mixin({LevelExtractor.class})
public abstract class LevelExtractorMixin {

   public LevelExtractorMixin() {
   }

   @Inject(
      method = {"extractVisibleEntities"},
      at = {@At("HEAD")}
   )
   private void frustrumHook(Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState levelRenderState, CallbackInfo callbackInfo) {
      FrustrumEvent.INSTANCE.setFrustrum(frustum);
      FrustrumEvent.INSTANCE.call();
   }
}