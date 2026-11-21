/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10041
 *  net.minecraft.class_1538
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_919
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10041;
import net.minecraft.class_1538;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_919;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.utils.render.killeffect.KillEffectColorAccess;
import ru.decide.utils.render.killeffect.LightningFlash;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_919.class})
public abstract class LightningRendererTintMixin {
    @Shadow
    private static void method_23183(Matrix4f matrix, class_4588 vertices, float x1, float y1, int color, float f6, float f7, float f8, float f9, float f10, float f11, float f12, boolean b1, boolean b2, boolean b3, boolean b4) {
        throw new AssertionError();
    }

    @Inject(method={"method_62481"}, at={@At(value="TAIL")})
    private void decide$updateKillEffectColor(class_1538 bolt, class_10041 state, float tickDelta, CallbackInfo ci) {
        ((KillEffectColorAccess)state).decide$setKillEffectColor(LightningFlash.colorOf(bolt.method_5667()));
    }

    @Redirect(method={"method_72989"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_919;method_23183(Lorg/joml/Matrix4f;Lnet/minecraft/class_4588;FFIFFFFFFFZZZZ)V"))
    private static void decide$drawKillEffectLightning(Matrix4f matrix, class_4588 vertices, float x1, float y1, int color, float f6, float f7, float f8, float f9, float f10, float f11, float f12, boolean b1, boolean b2, boolean b3, boolean b4, class_10041 state, float[] segs1, float width1, float[] segs2, float width2, class_4587.class_4665 entry, class_4588 buffer) {
        Integer tint = ((KillEffectColorAccess)state).decide$getKillEffectColor();
        if (tint != null) {
            f8 = (float)(tint >> 16 & 0xFF) / 255.0f;
            f9 = (float)(tint >> 8 & 0xFF) / 255.0f;
            f10 = (float)(tint & 0xFF) / 255.0f;
        }
        LightningRendererTintMixin.method_23183(matrix, vertices, x1, y1, color, f6, f7, f8, f9, f10, f11, f12, b1, b2, b3, b4);
    }
}

