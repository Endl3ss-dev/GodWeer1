/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11228
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.velotune;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11228;
import net.minecraft.class_276;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.optimization.velotune.hud.ScreenFramebufferCache;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_11228.class})
abstract class GuiRendererMixin {
    GuiRendererMixin() {
    }

    @Inject(method={"method_70890"}, at={@At(value="HEAD")})
    private void velotune$beginGuiPass(GpuBufferSlice fogBuffer, CallbackInfo ci) {
        if (!ScreenFramebufferCache.active()) {
            return;
        }
        class_276 main = class_310.method_1551().method_1522();
        if (ScreenFramebufferCache.shouldCaptureFrame()) {
            ScreenFramebufferCache.beginRenderCapture();
        } else {
            ScreenFramebufferCache.composite(main);
        }
    }

    @Inject(method={"method_70890"}, at={@At(value="RETURN")})
    private void velotune$finishGuiPass(GpuBufferSlice fogBuffer, CallbackInfo ci) {
        if (!ScreenFramebufferCache.isRenderingCapture()) {
            return;
        }
        ScreenFramebufferCache.finishRenderCapture(System.nanoTime());
        ScreenFramebufferCache.composite(class_310.method_1551().method_1522());
    }
}

