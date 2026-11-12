/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_9976
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9976;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.Optimizer;
import ru.decide.optimization.velotune.VeloTuneManager;
import ru.decide.optimization.velotune.perf.ExtremeRenderBudget;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_9976.class})
abstract class WeatherRenderingMixin {
    WeatherRenderingMixin() {
    }

    @Inject(method={"method_62320"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$disablePrecipitation(CallbackInfo ci) {
        if (Optimizer.isNoWeather()) {
            ci.cancel();
            return;
        }
        if (ExtremeRenderBudget.active() && VeloTuneManager.config().extreme.disableWeather) {
            ci.cancel();
        }
    }

    @Inject(method={"method_62319"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$disableWeatherParticles(CallbackInfo ci) {
        if (Optimizer.isNoWeather()) {
            ci.cancel();
            return;
        }
        if (ExtremeRenderBudget.active() && VeloTuneManager.config().extreme.disableWeather) {
            ci.cancel();
        }
    }
}

