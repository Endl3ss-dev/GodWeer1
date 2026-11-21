/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 *  net.minecraft.class_437
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.optimization.velotune.hud.ScreenFramebufferCache;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_437.class})
abstract class ScreenMixin {
    ScreenMixin() {
    }

    @Inject(method={"method_25394"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$limitScreenRate(class_332 context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (ScreenFramebufferCache.active() && !ScreenFramebufferCache.shouldCaptureFrame()) {
            ci.cancel();
        }
    }

    @Inject(method={"method_25420"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$skipBufferedScreenBlur(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (ScreenFramebufferCache.active()) {
            ci.cancel();
        }
    }
}

