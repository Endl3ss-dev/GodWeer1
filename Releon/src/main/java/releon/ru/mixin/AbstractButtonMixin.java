package releon.ru.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.api.module.impl.visual.Animations;
import releon.ru.screens.mainmenu.VanillaMenuTheme;

@Mixin({PressableWidget.class})
public abstract class AbstractButtonMixin {
   @Unique
   private float releon$hover;
   @Unique
   private long releon$lastFrame;
   @Unique
   private boolean releon$animationPosePushed;

   public AbstractButtonMixin() {
   }

   @Inject(
      method = {"renderWidget"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void releon$renderThemedButton(DrawContext graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      if (VanillaMenuTheme.active()) {
         PressableWidget button = (PressableWidget)(Object)this;
         if (button instanceof ButtonWidget || button instanceof CyclingButtonWidget) {
            long now = System.nanoTime();
            float delta = this.releon$lastFrame == 0L ? 0.0F : Math.min(0.05F, (float)(now - this.releon$lastFrame) / 1.0E9F);
            this.releon$lastFrame = now;
            float target = button.isSelected() && button.active ? 1.0F : 0.0F;
            float rate = Animations.buttonsEnabled() ? Animations.buttonRate() : 7.5F;
            this.releon$hover = approach(this.releon$hover, target, delta * rate);
            if (Animations.buttonsEnabled()) {
               float scale = 1.0F + 0.025F * this.releon$hover;
               float centerX = (float)button.getX() + (float)button.getWidth() * 0.5F;
               float centerY = (float)button.getY() + (float)button.getHeight() * 0.5F;
               graphics.getMatrices().pushMatrix();
               graphics.getMatrices().translate(centerX, centerY);
               graphics.getMatrices().scale(scale, scale);
               graphics.getMatrices().translate(-centerX, -centerY);
               this.releon$animationPosePushed = true;
            }

            VanillaMenuTheme.renderButton(graphics, button, this.releon$hover);
            this.releon$popAnimationPose(graphics);
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"renderWidget"},
      at = {@At("HEAD")}
   )
   private void releon$beginButtonAnimation(DrawContext graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      if (!VanillaMenuTheme.active() && Animations.buttonsEnabled()) {
         PressableWidget button = (PressableWidget)(Object)this;
         long now = System.nanoTime();
         float delta = this.releon$lastFrame == 0L ? 0.0F : Math.min(0.05F, (float)(now - this.releon$lastFrame) / 1.0E9F);
         this.releon$lastFrame = now;
         float target = button.isSelected() && button.active ? 1.0F : 0.0F;
         this.releon$hover = approach(this.releon$hover, target, delta * Animations.buttonRate());
         float scale = 1.0F + 0.025F * this.releon$hover;
         float centerX = (float)button.getX() + (float)button.getWidth() * 0.5F;
         float centerY = (float)button.getY() + (float)button.getHeight() * 0.5F;
         graphics.getMatrices().pushMatrix();
         graphics.getMatrices().translate(centerX, centerY);
         graphics.getMatrices().scale(scale, scale);
         graphics.getMatrices().translate(-centerX, -centerY);
         this.releon$animationPosePushed = true;
      }
   }

   @Inject(
      method = {"renderWidget"},
      at = {@At("RETURN")}
   )
   private void releon$finishButtonAnimation(DrawContext graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      this.releon$popAnimationPose(graphics);
   }

   @Unique
   private void releon$popAnimationPose(DrawContext graphics) {
      if (this.releon$animationPosePushed) {
         graphics.getMatrices().popMatrix();
         this.releon$animationPosePushed = false;
      }
   }

   @Unique
   private static float approach(float current, float target, float amount) {
      return current < target ? Math.min(target, current + amount) : Math.max(target, current - amount);
   }
}

