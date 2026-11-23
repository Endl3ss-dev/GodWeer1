// deobf by pusia
package su.catlean.mixins.render;

import net.minecraft.client.renderer.ShaderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.ShaderApplyEvent;

@Mixin({ShaderManager.class})
public abstract class ShaderManagerMixin {

   public ShaderManagerMixin() {
   }

   @Inject(
      method = {"apply(Lnet/minecraft/client/renderer/ShaderManager$Configs;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V"},
      at = {@At("TAIL")}
   )
   private void applyResourcesHook(CallbackInfo callbackInfo) {
      ShaderApplyEvent.INSTANCE.call();
   }
}