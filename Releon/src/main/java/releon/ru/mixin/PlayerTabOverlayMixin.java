package releon.ru.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.api.module.impl.visual.Animations;

@Mixin({PlayerListHud.class})
public abstract class PlayerTabOverlayMixin {
   @Unique
   private boolean releon$posePushed;

   public PlayerTabOverlayMixin() {
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void releon$beginTabAnimation(DrawContext graphics, int width, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
      if (Animations.tabEnabled()) {
         float scale = Animations.tabScale();
         graphics.getMatrices().pushMatrix();
         graphics.getMatrices().translate((float)width * 0.5F, 0.0F);
         graphics.getMatrices().scale(scale, scale);
         graphics.getMatrices().translate((float)(-width) * 0.5F, 0.0F);
         this.releon$posePushed = true;
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   private void releon$finishTabAnimation(DrawContext graphics, int width, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
      if (this.releon$posePushed) {
         graphics.getMatrices().popMatrix();
         this.releon$posePushed = false;
      }
   }
}
