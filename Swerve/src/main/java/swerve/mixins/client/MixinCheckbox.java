package swerve.mixins.client;

import swerve.mixins.accessor.AbstractWidgetAccessor;
import swerve.ui.newgui.VanillaWidgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Переключатели (ванильные {@link Checkbox}) в стиле клиента: капсульная дорожка со
 * скользящей головкой вместо квадрата 9x9.
 *
 * <p>Состояние читается из самого виджета, обработка нажатия не меняется — заменяется
 * только отрисовка, поэтому флажки работают в любых экранах: создание мира, настройки
 * чата, правила и т.п.
 */
@Mixin(Checkbox.class)
public abstract class MixinCheckbox {

    @Shadow
    private boolean selected;

    @Unique
    private float swerve$hover;

    @Unique
    private float swerve$knob;

    @Unique
    private long swerve$frameAt;

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void swerve$renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                                     CallbackInfo ci) {
        Checkbox self = (Checkbox) (Object) this;
        long now = System.nanoTime();
        swerve$hover = VanillaWidgets.ease(swerve$hover, self.isHoveredOrFocused(), now, swerve$frameAt);
        swerve$knob = VanillaWidgets.ease(swerve$knob, this.selected, now, swerve$frameAt);
        swerve$frameAt = now;

        graphics.flush();
        VanillaWidgets.checkbox(graphics, self.getX(), self.getY(), self.getWidth(), self.getHeight(),
                self.getMessage(), this.selected, swerve$knob, swerve$hover, self.active,
                ((AbstractWidgetAccessor) self).swerve$getAlpha());
        ci.cancel();
    }
}
