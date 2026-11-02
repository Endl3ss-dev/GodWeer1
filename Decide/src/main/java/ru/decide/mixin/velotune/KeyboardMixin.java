/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_309
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_309;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.optimization.velotune.hud.ScreenFramebufferCache;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_309.class})
abstract class KeyboardMixin {
    KeyboardMixin() {
    }

    @Inject(method={"method_1466"}, at={@At(value="HEAD")})
    private void velotune$invalidateScreenOnKey(long window, int action, class_11908 input, CallbackInfo ci) {
        if (class_310.method_1551().field_1755 != null) {
            ScreenFramebufferCache.invalidate();
        }
    }

    @Inject(method={"method_1457"}, at={@At(value="HEAD")})
    private void velotune$invalidateScreenOnChar(long window, class_11905 input, CallbackInfo ci) {
        if (class_310.method_1551().field_1755 != null) {
            ScreenFramebufferCache.invalidate();
        }
    }
}

