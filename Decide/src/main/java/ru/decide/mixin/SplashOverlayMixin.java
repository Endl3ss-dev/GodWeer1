/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_156
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_3532
 *  net.minecraft.class_4011
 *  net.minecraft.class_425
 *  net.minecraft.class_9848
 *  org.joml.Matrix3x2fStack
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_156;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_4011;
import net.minecraft.class_425;
import net.minecraft.class_9848;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.lang.Lang;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.Render2D;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.ScreenBlur;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_425.class})
public abstract class SplashOverlayMixin {
    @Shadow
    @Final
    private class_310 field_18217;
    @Shadow
    @Final
    private class_4011 field_17767;
    @Shadow
    @Final
    private boolean field_18219;
    @Shadow
    private float field_17770;
    @Shadow
    private long field_17771;
    @Shadow
    private long field_18220;
    @Unique
    private static final int DECIDEVISUALS_BG = 658964;
    @Unique
    private static final class_2960 MENU_BG = class_2960.method_60655((String)"decide", (String)"textures/frame/menu.png");
    @Unique
    private long decidevisualsStart = -1L;

    @Unique
    private static int withAlpha(int rgb, int alpha) {
        return rgb & 0xFFFFFF | alpha << 24;
    }

    @Inject(method={"method_25394"}, at={@At(value="HEAD")}, cancellable=true)
    private void decidevisualsRender(class_332 context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        float alpha;
        float g;
        int i = context.method_51421();
        int j = context.method_51443();
        long l = class_156.method_658();
        if (this.decidevisualsStart == -1L) {
            this.decidevisualsStart = l;
        }
        if (this.field_18219 && this.field_18220 == -1L) {
            this.field_18220 = l;
        }
        float f = this.field_17771 > -1L ? (float)(l - this.field_17771) / 1000.0f : -1.0f;
        float f2 = g = this.field_18220 > -1L ? (float)(l - this.field_18220) / 500.0f : -1.0f;
        if (f >= 1.0f) {
            if (this.field_18217.field_1755 != null) {
                this.field_18217.field_1755.method_47413(context, 0, 0, deltaTicks);
            } else {
                this.field_18217.field_1705.method_72738();
            }
            context.method_71048();
            alpha = 1.0f - class_3532.method_15363((float)(f - 1.0f), (float)0.0f, (float)1.0f);
        } else if (this.field_18219) {
            if (this.field_18217.field_1755 != null && g < 1.0f) {
                this.field_18217.field_1755.method_47413(context, mouseX, mouseY, deltaTicks);
            } else {
                this.field_18217.field_1705.method_72738();
            }
            context.method_71048();
            alpha = class_3532.method_15363((float)g, (float)0.0f, (float)1.0f);
        } else {
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.field_18217.method_1522().method_30277(), -16118252);
            alpha = 1.0f;
        }
        float rp = this.field_17767.method_18229();
        this.field_17770 = class_3532.method_15363((float)(this.field_17770 * 0.95f + rp * 0.050000012f), (float)0.0f, (float)1.0f);
        boolean drawn = false;
        try {
            drawn = this.drawDecideVisuals(context, alpha, l);
        }
        catch (Throwable ignored) {
            drawn = false;
        }
        if (!drawn) {
            this.drawFallback(context, i, j, alpha);
        }
        if (f >= 2.0f) {
            this.field_18217.method_18502(null);
        }
        ci.cancel();
    }

    @Unique
    private boolean drawDecideVisuals(class_332 context, float fade, long now) {
        double scaleFactor = this.field_18217.method_22683().method_4495();
        if (scaleFactor <= 0.0) {
            return false;
        }
        float scaleFix = 2.0f / (float)scaleFactor;
        int sw = (int)((float)this.field_18217.method_22683().method_4486() / scaleFix);
        int sh = (int)((float)this.field_18217.method_22683().method_4502() / scaleFix);
        if (sw <= 0 || sh <= 0) {
            return false;
        }
        float intro = class_3532.method_15363((float)((float)(now - this.decidevisualsStart) / 600.0f), (float)0.0f, (float)1.0f);
        float ease = 1.0f - (1.0f - intro) * (1.0f - intro) * (1.0f - intro);
        float a = class_3532.method_15363((float)(fade * ease), (float)0.0f, (float)1.0f);
        if (a <= 0.01f) {
            return true;
        }
        float t = (float)now / 1000.0f;
        float breathe = 1.0f + 0.03f * (float)Math.sin(t * 2.0f);
        float floatY = 2.0f * (float)Math.sin(t * 1.3f);
        float rise = (1.0f - ease) * 14.0f;
        context.method_51448().pushMatrix();
        Render2D.beginOverlay();
        RenderUtil.Render2D.rect(0.0f, 0.0f, sw, sh, ColorUtil.getColor(0, a));
        RenderUtil.Images.texture(MENU_BG, 0.0f, 0.0f, sw, sh, ColorUtil.getColor(255, a));
        RenderUtil.Render2D.rect(0.0f, 0.0f, sw, sh, ColorUtil.getColor(0, a * 0.2f));
        ScreenBlur.capture(2);
        float cx = (float)sw / 2.0f;
        float cy = (float)sh / 2.0f - 18.0f + floatY + rise;
        Fonts.sf_medium.drawCentered("a", cx + 0.25f, cy + 0.25f, 40.0f * breathe, ColorUtil.getColor(138, 161, 186, a * 0.9f));
        Fonts.sf_medium.drawCentered("a", cx, cy, 39.0f * breathe, ColorUtil.getColor(175, 199, 230, a));
        Fonts.sf_bold.drawCentered("DecideVisuals", cx, cy + 34.0f, 24.0f, ColorUtil.getColor(255, a));
        int dots = (int)(now / 400L % 4L);
        String sub = "\u0437\u0430\u0433\u0440\u0443\u0437\u043a\u0430 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432" + ".".repeat(dots);
        Fonts.sf_regular.drawCentered(sub, cx, cy + 68.0f, 9.0f, ColorUtil.getColor(154, 164, 184, a * 0.85f));
        this.drawBarBranded(sw, sh, a, t);
        Fonts.sf_bold.drawCentered(Lang.pick("DecideVisuals \u00b7 2026 \u00b7 \u0412\u0441\u0435 \u043f\u0440\u0430\u0432\u0430 \u0437\u0430\u0449\u0438\u0449\u0435\u043d\u044b", "DecideVisuals \u00b7 2026 \u00b7 All rights reserved"), cx, (float)sh - 22.0f, 8.0f, ColorUtil.getColor(255, a * 0.12f));
        Render2D.endOverlay();
        context.method_51448().popMatrix();
        return true;
    }

    @Unique
    private void drawBarBranded(int sw, int sh, float a, float t) {
        float barW = Math.min((float)sw * 0.36f, 280.0f);
        float barH = 4.0f;
        float x = (float)sw / 2.0f - barW / 2.0f;
        float y = (float)sh * 0.74f;
        RenderUtil.Render2D.rect(x, y, barW, barH, ColorUtil.getColor(35, 42, 58, a * 0.85f), barH / 2.0f);
        float fillW = barW * this.field_17770;
        if (fillW > 0.5f) {
            RenderUtil.Render2D.rect(x, y, fillW, barH, ColorUtil.getColor(125, 145, 244, a), barH / 2.0f);
            float sweep = t * 0.6f % 1.0f;
            float sx = x + sweep * fillW;
            RenderUtil.Render2D.rect(sx - 6.0f, y, 12.0f, barH, ColorUtil.getColor(210, 220, 255, a * 0.35f), barH / 2.0f);
        }
        String pct = (int)(this.field_17770 * 100.0f) + "%";
        Fonts.sf_bold.draw(pct, x + barW + 8.0f, y - 2.5f, 8.0f, ColorUtil.getColor(220, 226, 240, a));
    }

    @Unique
    private void drawFallback(class_332 context, int width, int height, float alpha) {
        int a = class_3532.method_15386((float)(class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f) * 255.0f));
        if (a <= 4) {
            return;
        }
        context.method_25294(0, 0, width, height, SplashOverlayMixin.withAlpha(658964, a));
        class_327 tr = this.field_18217.field_1772;
        Matrix3x2fStack matrices = context.method_51448();
        String title = "DECIDEVISUALS";
        float s = 4.0f;
        matrices.pushMatrix();
        matrices.scale(s, s);
        float cx = (float)width / 2.0f / s;
        float ty = ((float)height / 2.0f - 26.0f) / s;
        float half = (float)tr.method_1727(title) / 2.0f;
        context.method_51433(tr, title, (int)(cx - half), (int)ty, SplashOverlayMixin.withAlpha(8229364, a), false);
        matrices.popMatrix();
        int barW = (int)Math.min((float)width * 0.5f, 320.0f);
        int x = width / 2 - barW / 2;
        int y = (int)((float)height * 0.78f);
        context.method_25294(x, y, x + barW, y + 4, SplashOverlayMixin.withAlpha(0x232A3A, (int)((float)a * 0.85f)));
        int fillW = class_3532.method_15386((float)((float)(barW - 2) * this.field_17770));
        context.method_25294(x + 1, y + 1, x + 1 + fillW, y + 3, SplashOverlayMixin.withAlpha(8229364, a));
        String pct = (int)(this.field_17770 * 100.0f) + "%";
        context.method_51433(tr, pct, x + barW + 8, y - 2, class_9848.method_61324((int)a, (int)220, (int)226, (int)240), false);
    }
}

