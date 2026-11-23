// deobf by pusia
package su.catlean.mixins.render;

import java.util.Map;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.BossBarRenderEvent;

@Mixin({BossHealthOverlay.class})
public class BossHealthOverlayMixin {

   @Final
   @Shadow
   Map<UUID, LerpingBossEvent> events;

   public BossHealthOverlayMixin() {
   }

   @Inject(
      method = {"extractRenderState"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderHook(GuiGraphicsExtractor guiGraphicsExtractor, CallbackInfo callbackInfo) {
      if (!this.events.isEmpty() && BossBarRenderEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }
}