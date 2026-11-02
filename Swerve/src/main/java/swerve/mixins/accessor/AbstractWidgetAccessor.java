package swerve.mixins.accessor;

import net.minecraft.client.gui.components.AbstractWidget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Доступ к {@code AbstractWidget.alpha} — общему множителю прозрачности виджета.
 *
 * <p>Поле объявлено именно в {@code AbstractWidget}, а {@code @Shadow} находит только поля
 * самого целевого класса: тень на наследнике ({@code AbstractButton}, {@code Checkbox},
 * {@code AbstractSliderButton}) падает при загрузке с «@Shadow field alpha was not located in
 * the target class». Поэтому читаем поле через accessor, который примешивается к владельцу.
 */
@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccessor {

    @Accessor("alpha")
    float swerve$getAlpha();
}
