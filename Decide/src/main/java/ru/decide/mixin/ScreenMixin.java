/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 *  net.minecraft.class_364
 *  net.minecraft.class_4264
 *  net.minecraft.class_437
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4264;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.MathUtil;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_437.class})
public abstract class ScreenMixin {
    @Inject(method={"method_25394"}, at={@At(value="RETURN")})
    private void decidevisuals$renderStyledButtons(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        class_437 self = (class_437)this;
        for (class_364 child : self.method_25396()) {
            float h;
            float w;
            float y;
            if (!(child instanceof class_4264)) continue;
            class_4264 widget = (class_4264)child;
            if (!widget.field_22764) continue;
            float x = widget.method_46426();
            boolean hovered = MathUtil.isHovered(mouseX, mouseY, x, y = (float)widget.method_46427(), w = (float)widget.method_25368(), h = (float)widget.method_25364());
            float hp = hovered ? 1.0f : 0.0f;
            float activeAlpha = widget.field_22763 ? 1.0f : 0.35f;
            RenderUtil.Render2D.rect(x, y, w, h, ColorUtil.getColor(0, activeAlpha * (0.7f + hp * 0.1f)), 6.0f);
            RenderUtil.Render2D.outline(x, y, w, h, 0.5f, ColorUtil.replAlpha(ColorUtil.getColor(255), activeAlpha * (0.03f + hp * 0.12f)), 6.0f);
            String text = widget.method_25369().getString();
            int textColor = ColorUtil.replAlpha(ColorUtil.getColor(255), activeAlpha * (0.5f + hp * 0.5f));
            Fonts.sf_regular.drawCentered(text, x + w / 2.0f, y + h / 2.0f - 3.8f, 7.0f, textColor);
        }
    }
}

