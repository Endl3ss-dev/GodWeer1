// deobf by pusia
package su.catlean.mixins.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.Release2DEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.RenderScreenEvent;

@Mixin({GuiRenderer.class})
public abstract class GuiRendererMixin {

   @Unique
   private static boolean catlean$constructingRenderer;
   @Unique
   private boolean catlean$auxiliaryRenderer;
   @Unique
   private GuiRenderState catlean$renderState;
   @Unique
   private GuiRenderer catlean$renderer;

   public GuiRendererMixin() {
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void catlean$init(
      GuiRenderState guiRenderState, FeatureRenderDispatcher featureRenderDispatcher, List<PictureInPictureRenderer<?>> items, CallbackInfo callbackInfo
   ) {
      if (catlean$constructingRenderer) {
         this.catlean$auxiliaryRenderer = true;
      } else {
         this.catlean$renderState = new GuiRenderState();
         catlean$constructingRenderer = true;

         try {
            this.catlean$renderer = new GuiRenderer(this.catlean$renderState, featureRenderDispatcher, items);
         } finally {
            catlean$constructingRenderer = false;
         }
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void catlean$render(CallbackInfo callbackInfo) {
      if (!this.catlean$auxiliaryRenderer) {
         Minecraft minecraft = Minecraft.getInstance();
         int scaledXPos = (int)minecraft.mouseHandler.getScaledXPos(minecraft.getWindow());
         int scaledYPos = (int)minecraft.mouseHandler.getScaledYPos(minecraft.getWindow());
         float gameTimeDeltaTicks = minecraft.getDeltaTracker().getGameTimeDeltaTicks();
         GuiGraphicsExtractor guiGraphicsExtractor = new GuiGraphicsExtractor(minecraft, this.catlean$renderState, scaledXPos, scaledYPos);
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(minecraft.gameRenderer.mainRenderTarget().getDepthTexture(), 1.0);
         if (minecraft.player != null && minecraft.level != null) {
            Render2DEvent.INSTANCE.call(guiGraphicsExtractor);
         }

         RenderScreenEvent.INSTANCE.call(guiGraphicsExtractor, scaledXPos, scaledYPos, gameTimeDeltaTicks);
         this.catlean$renderer.render();
         this.catlean$renderer.endFrame();
         Release2DEvent.INSTANCE.call();
      }
   }
}