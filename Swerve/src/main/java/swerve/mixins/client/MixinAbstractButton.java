package swerve.mixins.client;

import swerve.mixins.accessor.AbstractWidgetAccessor;
import swerve.ui.newgui.VanillaWidgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.SpriteIconButton;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Кнопки ванильных экранов в стиле клиента.
 *
 * <p>Под миксин попадают все потомки {@link AbstractButton}, которые не переопределяют
 * {@code renderWidget}: {@code Button} (главное меню, подэкраны настроек, подтверждения),
 * {@code CycleButton} (переключатели значений в настройках), кнопки справки и т.п.
 * Свои {@code renderWidget} есть у {@code ImageButton}, {@code LockIconButton},
 * {@code PageButton} и {@code PlainTextButton} — они рисуют собственные спрайты и остаются
 * ванильными.
 */
@Mixin(AbstractButton.class)
public abstract class MixinAbstractButton {

    @Unique
    private float swerve$hover;

    @Unique
    private long swerve$hoverAt;

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void swerve$renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick,
                                     CallbackInfo ci) {
        // SpriteIconButton (выбор наборов ресурсов, миров и т.п.) рисует спрайт мимо
        // renderWidget — плитка перекрыла бы иконку, поэтому такие кнопки не трогаем.
        if ((Object) this instanceof SpriteIconButton) {
            return;
        }

        AbstractButton self = (AbstractButton) (Object) this;
        long now = System.nanoTime();
        swerve$hover = VanillaWidgets.ease(swerve$hover, self.isHoveredOrFocused(), now, swerve$hoverAt);
        swerve$hoverAt = now;

        // Скругления рисуются своими шейдерами сразу, а текст GuiGraphics копит в буфере:
        // без flush плитка легла бы поверх уже нарисованного текста этого же экрана.
        graphics.flush();
        VanillaWidgets.button(graphics, self.getX(), self.getY(), self.getWidth(), self.getHeight(),
                self.getMessage(), swerve$hover, self.active, self.isFocused(),
                ((AbstractWidgetAccessor) self).swerve$getAlpha());
        ci.cancel();
    }
}
