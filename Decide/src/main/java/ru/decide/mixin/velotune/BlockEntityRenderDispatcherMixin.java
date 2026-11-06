/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11683$class_11792
 *  net.minecraft.class_11954
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_2586
 *  net.minecraft.class_310
 *  net.minecraft.class_824
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11683;
import net.minecraft.class_11954;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_310;
import net.minecraft.class_824;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.module.impl.render.Optimizer;
import ru.decide.optimization.velotune.perf.ExtremeRenderBudget;
import ru.decide.optimization.velotune.perf.Metrics;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_824.class})
abstract class BlockEntityRenderDispatcherMixin {
    private static final class_310 mc = class_310.method_1551();

    BlockEntityRenderDispatcherMixin() {
    }

    @Inject(method={"method_74348"}, at={@At(value="HEAD")}, cancellable=true)
    private <E extends class_2586, S extends class_11954> void velotune$cullBlockEntity(E blockEntity, float tickDelta, class_11683.class_11792 crumblingOverlay, CallbackInfoReturnable<S> cir) {
        class_2338 pos;
        float cullDist = Optimizer.blockEntityCullDist();
        if (cullDist > 0.0f && BlockEntityRenderDispatcherMixin.mc.field_1687 != null && (pos = blockEntity.method_11016()) != null) {
            double dz;
            double dy;
            class_243 cam = BlockEntityRenderDispatcherMixin.mc.field_1773.method_19418().method_71156();
            double dx = (double)pos.method_10263() + 0.5 - cam.field_1352;
            if (dx * dx + (dy = (double)pos.method_10264() + 0.5 - cam.field_1351) * dy + (dz = (double)pos.method_10260() + 0.5 - cam.field_1350) * dz > (double)cullDist * (double)cullDist) {
                cir.setReturnValue(null);
                return;
            }
        }
        if (ExtremeRenderBudget.cullBlockEntity(blockEntity)) {
            Metrics.blockEntityCull();
            cir.setReturnValue(null);
        }
    }
}

