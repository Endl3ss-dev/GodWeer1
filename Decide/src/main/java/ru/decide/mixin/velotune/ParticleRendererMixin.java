/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11938
 *  net.minecraft.class_703
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11938;
import net.minecraft.class_703;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.optimization.velotune.perf.ExtremeRenderBudget;
import ru.decide.optimization.velotune.perf.Metrics;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_11938.class})
abstract class ParticleRendererMixin {
    ParticleRendererMixin() {
    }

    @Inject(method={"method_74285"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$removeCulledParticle(class_703 particle, CallbackInfo ci) {
        if (ExtremeRenderBudget.cullParticle(particle)) {
            Metrics.particleDrop();
            particle.method_3085();
            ci.cancel();
        }
    }
}

