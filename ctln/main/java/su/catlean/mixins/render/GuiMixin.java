// deobf by pusia
package su.catlean.mixins.render;

import net.minecraft.world.effect.MobEffectInstance;
import java.util.Collection;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.CrosshairRenderEvent;
import su.catlean.api.event.events.render.HotBarRenderEventPost;
import su.catlean.api.event.events.render.HotBarRenderEventPre;
import su.catlean.api.event.events.render.ItemNameRenderEvent;
import su.catlean.api.event.events.render.NauseaRenderEvent;
import su.catlean.api.event.events.render.RenderPortalOverlayEvent;
import su.catlean.api.event.events.render.RenderVignetteOverlayEvent;
import su.catlean.api.event.events.render.ScoreBoardRenderEvent;
import su.catlean.api.event.events.render.StatusEffectsRenderEvent;

@Mixin({Hud.class})
public abstract class GuiMixin {

   public GuiMixin() {
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"extractSelectedItemName"},
      cancellable = true
   )
   public void renderHeldItemTooltipHook(GuiGraphicsExtractor guiGraphicsExtractor, CallbackInfo callbackInfo) {
      if (ItemNameRenderEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/scores/Objective;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderScoreboardSidebarHook(GuiGraphicsExtractor guiGraphicsExtractor, Objective objective, CallbackInfo callbackInfo) {
      if (ScoreBoardRenderEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"extractVignette"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderVignetteOverlayHook(GuiGraphicsExtractor guiGraphicsExtractor, Entity entity, CallbackInfo callbackInfo) {
      if (RenderVignetteOverlayEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"extractPortalOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderPortalOverlayHook(GuiGraphicsExtractor guiGraphicsExtractor, float amount, CallbackInfo callbackInfo) {
      if (RenderPortalOverlayEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"extractEffects"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderStatusEffectOverlayHook(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      Collection<MobEffectInstance> activeEffects = Minecraft.getInstance().player.getActiveEffects();
      if (!activeEffects.isEmpty() && Minecraft.getInstance().gui.screen() == null && StatusEffectsRenderEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"extractConfusionOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderNauseaOverlayHook(GuiGraphicsExtractor guiGraphicsExtractor, float amount, CallbackInfo callbackInfo) {
      if (NauseaRenderEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"extractCrosshair"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderCrosshair(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      if (CrosshairRenderEvent.INSTANCE.call()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"extractItemHotbar"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderHotbarHookPre(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      HotBarRenderEventPre.INSTANCE.call();
   }

   @Inject(
      method = {"extractItemHotbar"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void renderHotbarHookPost(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
      HotBarRenderEventPost.INSTANCE.call();
   }
}