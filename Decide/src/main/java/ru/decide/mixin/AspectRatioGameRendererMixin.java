/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_757
 *  net.minecraft.class_9779
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_757;
import net.minecraft.class_9779;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.module.impl.render.AspectRatio;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_757.class})
public abstract class AspectRatioGameRendererMixin {
    @Shadow
    public abstract float method_32796();

    @Inject(method={"method_3192"}, at={@At(value="HEAD")})
    private void decide$aspectRatioFrame(class_9779 tickCounter, boolean tick, CallbackInfo ci) {
        AspectRatio aspectRatio = AspectRatio.getInstance();
        if (aspectRatio != null && aspectRatio.isEnabled()) {
            aspectRatio.beginFrame();
        }
    }

    @Inject(method={"method_22973"}, at={@At(value="RETURN")}, cancellable=true)
    private void decide$aspectRatioProjection(float fovDegrees, CallbackInfoReturnable<Matrix4f> cir) {
        AspectRatio aspectRatio = AspectRatio.getInstance();
        if (aspectRatio == null || !aspectRatio.isEnabled()) {
            return;
        }
        float ratio = aspectRatio.currentRatio();
        if (ratio <= 0.0f || Float.isNaN(ratio) || Float.isInfinite(ratio)) {
            return;
        }
        Matrix4f matrix = new Matrix4f().perspective(fovDegrees * ((float)Math.PI / 180), ratio, 0.05f, this.method_32796());
        cir.setReturnValue((Object)matrix);
    }
}

