/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_4604
 *  net.minecraft.class_898
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_4604;
import net.minecraft.class_898;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.module.impl.render.Optimizer;
import ru.decide.optimization.velotune.VeloTuneManager;
import ru.decide.optimization.velotune.perf.ExtremeRenderBudget;
import ru.decide.optimization.velotune.perf.Metrics;
import ru.decide.optimization.velotune.perf.PlayerLodPolicy;
import ru.decide.optimization.velotune.perf.VeloTuneConfig;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_898.class})
abstract class EntityRenderDispatcherMixin {
    EntityRenderDispatcherMixin() {
    }

    @Inject(method={"method_3950"}, at={@At(value="RETURN")}, cancellable=true)
    private <E extends class_1297> void velotune$cullEmergencyPlayer(E entity, class_4604 frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            return;
        }
        class_898 dispatcher = (class_898)this;
        double distanceSquared = dispatcher.method_23168(entity);
        float cullDist = Optimizer.entityCullDist();
        if (cullDist > 0.0f && distanceSquared > (double)cullDist * (double)cullDist) {
            cir.setReturnValue((Object)false);
            return;
        }
        if (!VeloTuneManager.enabled()) {
            return;
        }
        boolean player = entity instanceof class_1657;
        if (ExtremeRenderBudget.cullEntity(entity, distanceSquared, player)) {
            if (player) {
                Metrics.playerCull();
            } else {
                Metrics.entityCull();
            }
            cir.setReturnValue((Object)false);
            return;
        }
        if (!player || entity == dispatcher.field_4678) {
            return;
        }
        VeloTuneConfig config = VeloTuneManager.config();
        if (PlayerLodPolicy.cullPlayer(distanceSquared, VeloTuneManager.governor().pressureLevel(), config.players)) {
            Metrics.playerCull();
            cir.setReturnValue((Object)false);
        }
    }
}

