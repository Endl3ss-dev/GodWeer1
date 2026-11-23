// deobf by pusia
package su.catlean.mixins.render;

import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.GofraState;

@Mixin({VisGraph.class})
public class VisGraphMixin {

   public VisGraphMixin() {
   }

   @Inject(
      method = {"setOpaque"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void markClosedHook(BlockPos blockPos, CallbackInfo callbackInfo) {
      if (GofraState.INSTANCE.getXray()) {
         callbackInfo.cancel();
      }
   }
}