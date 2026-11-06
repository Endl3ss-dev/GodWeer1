/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_243
 *  net.minecraft.class_4063
 *  net.minecraft.class_761
 *  net.minecraft.class_9909
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.velotune;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_4063;
import net.minecraft.class_761;
import net.minecraft.class_9909;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.optimization.velotune.VeloTuneManager;
import ru.decide.optimization.velotune.perf.ExtremeRenderBudget;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_761.class})
abstract class WorldRendererMixin {
    WorldRendererMixin() {
    }

    @Inject(method={"method_62204"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$disableClouds(class_9909 frameGraphBuilder, class_4063 cloudRenderMode, class_243 cameraPos, long cloudTime, float tickDelta, int color, float cloudHeight, CallbackInfo ci) {
        if (ExtremeRenderBudget.active() && VeloTuneManager.config().extreme.disableClouds) {
            ci.cancel();
        }
    }

    @Inject(method={"method_62203"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$disableWeather(class_9909 frameGraphBuilder, GpuBufferSlice fogBuffer, CallbackInfo ci) {
        if (ExtremeRenderBudget.active() && VeloTuneManager.config().extreme.disableWeather) {
            ci.cancel();
        }
    }
}

