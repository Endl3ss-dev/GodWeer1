/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_702
 *  net.minecraft.class_703
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_702;
import net.minecraft.class_703;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.Optimizer;
import ru.decide.optimization.velotune.perf.ParticleBudget;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_702.class})
abstract class ParticleManagerMixin {
    ParticleManagerMixin() {
    }

    @Inject(method={"method_3058"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$limitParticleQueue(class_703 particle, CallbackInfo ci) {
        if (!ParticleBudget.allow(particle)) {
            ci.cancel();
            return;
        }
        if (!Optimizer.allowParticle(particle)) {
            ci.cancel();
        }
    }
}

