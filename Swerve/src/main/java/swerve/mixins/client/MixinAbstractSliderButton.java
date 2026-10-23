package swerve.mixins.client;

import swerve.ui.newgui.VanillaWidgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Ползунки ванильных экранов в стиле клиента.
 *
 * <p>В 1.21.4 все ползунки настроек — это {@code OptionInstance$OptionInstanceSliderButton},
 * наследник {@link AbstractSliderButton}, который своего {@code renderWidget} не объявляет и
 * вызывает родительский. Поэтому перехвата в {@code AbstractSliderButton} достаточно и для
 * громкости, и для дальности прорисовки, и для ползунков в видео-настройках.
 *
 * <p>Поведение (перетаскивание, {@code applyValue()}, задержка применения) не меняется:
 * отрисовка заменяется только на время кадра.
 */
@Mixin(AbstractSliderButton.class)
public abstract class MixinAbstractSliderButton {

    @Shadow
    protected double value;

    @Shadow
    private boolean canChangeValue;

    @Shadow
    protected float alpha;

    @Unique
    private float swerve$hover;

    @Unique
    private long swerve$hoverAt;

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void swerve$renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                                     CallbackInfo ci) {
        AbstractSliderButton self = (AbstractSliderButton) (Object) this;
        long now = System.nanoTime();
        swerve$hover = VanillaWidgets.ease(swerve$hover, self.isHoveredOrFocused(), now, swerve$hoverAt);
        swerve$hoverAt = now;

        graphics.flush();
        VanillaWidgets.slider(graphics, self.getX(), self.getY(), self.getWidth(), self.getHeight(),
                self.getMessage(), this.value, swerve$hover, self.active, this.canChangeValue, this.alpha);
        ci.cancel();
    }
}
