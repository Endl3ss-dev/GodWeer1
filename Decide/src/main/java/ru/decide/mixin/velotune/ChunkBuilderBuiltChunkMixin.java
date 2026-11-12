/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 *  net.minecraft.class_846$class_851
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_846;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.optimization.velotune.perf.ExtremeRenderBudget;
import ru.decide.optimization.velotune.perf.Metrics;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_846.class_851.class})
abstract class ChunkBuilderBuiltChunkMixin {
    ChunkBuilderBuiltChunkMixin() {
    }

    @Shadow
    public abstract class_2338 method_3670();

    @Inject(method={"method_3672"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$skipHiddenSectionBuild(CallbackInfoReturnable<Boolean> cir) {
        if (ExtremeRenderBudget.cullChunkSection(this.method_3670())) {
            Metrics.chunkSectionCull();
            cir.setReturnValue((Object)false);
        }
    }
}

