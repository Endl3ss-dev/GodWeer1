/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_1007
 *  net.minecraft.class_11659
 *  net.minecraft.class_12075
 *  net.minecraft.class_4587
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_4587;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.optimization.velotune.VeloTuneManager;
import ru.decide.optimization.velotune.perf.ExtremeRenderBudget;
import ru.decide.optimization.velotune.perf.Metrics;
import ru.decide.optimization.velotune.perf.PlayerLodPolicy;
import ru.decide.optimization.velotune.perf.VeloTuneConfig;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1007.class})
abstract class PlayerEntityRendererMixin {
    PlayerEntityRendererMixin() {
    }

    @Inject(method={"method_62606"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$limitFarFeatures(class_10055 state, CallbackInfoReturnable<Boolean> cir) {
        if (!VeloTuneManager.enabled()) {
            return;
        }
        VeloTuneConfig config = VeloTuneManager.config();
        if (config.extreme.preservePlayerEquipment) {
            return;
        }
        if (ExtremeRenderBudget.active() && config.extreme.hidePlayerFeatures) {
            Metrics.featureCull();
            cir.setReturnValue((Object)false);
            return;
        }
        if (PlayerLodPolicy.skipFeatures(state.field_53332, VeloTuneManager.governor().pressureLevel(), config.players)) {
            Metrics.featureCull();
            cir.setReturnValue((Object)false);
        }
    }

    @Inject(method={"method_4213"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$limitFarLabels(class_10055 state, class_4587 matrices, class_11659 commandQueue, class_12075 cameraState, CallbackInfo ci) {
        if (!VeloTuneManager.enabled()) {
            return;
        }
        VeloTuneConfig config = VeloTuneManager.config();
        if (config.extreme.preservePlayerLabels) {
            return;
        }
        if (ExtremeRenderBudget.active() && config.extreme.hidePlayerLabels) {
            Metrics.labelCull();
            ci.cancel();
            return;
        }
        if (PlayerLodPolicy.skipLabel(state.field_53332, VeloTuneManager.governor().pressureLevel(), config.players)) {
            Metrics.labelCull();
            ci.cancel();
        }
    }
}

