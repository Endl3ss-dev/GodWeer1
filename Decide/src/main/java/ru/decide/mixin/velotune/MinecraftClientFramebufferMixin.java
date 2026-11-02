/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.optimization.velotune.hud.HudFramebufferRouter;
import ru.decide.optimization.velotune.hud.ScreenFramebufferCache;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_310.class})
abstract class MinecraftClientFramebufferMixin {
    MinecraftClientFramebufferMixin() {
    }

    @Inject(method={"method_1522"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$routeHudFramebuffer(CallbackInfoReturnable<class_276> cir) {
        class_276 target = HudFramebufferRouter.captureTarget();
        if (target != null) {
            cir.setReturnValue((Object)target);
        }
    }

    @Inject(method={"method_1507"}, at={@At(value="HEAD")})
    private void velotune$invalidateScreenBuffer(class_437 screen, CallbackInfo ci) {
        ScreenFramebufferCache.invalidate();
    }
}

