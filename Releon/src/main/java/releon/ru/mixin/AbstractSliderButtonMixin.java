package releon.ru.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.screens.mainmenu.VanillaMenuTheme;

@Mixin({SliderWidget.class})
public abstract class AbstractSliderButtonMixin {
   @Shadow
   protected double value;

   public AbstractSliderButtonMixin() {
   }

   @Inject(
      method = {"renderWidget"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void releon$renderThemedSlider(DrawContext graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      if (VanillaMenuTheme.active()) {
         SliderWidget slider = (SliderWidget)(Object)this;
         VanillaMenuTheme.renderSlider(graphics, slider, this.value, slider.isSelected() ? 1.0F : 0.0F);
         ci.cancel();
      }
   }
}

