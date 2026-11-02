/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_757
 *  net.minecraft.class_9779
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_757;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.optimization.FrameSyncHandler;
import ru.decide.optimization.velotune.VeloTuneManager;
import ru.decide.optimization.velotune.perf.ExtremeRenderBudget;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_757.class})
public abstract class FrameSyncGameRendererMixin {
    @Inject(method={"method_3192"}, at={@At(value="HEAD")}, cancellable=true)
    private void framesync$skipRender(class_9779 tickCounter, boolean tick, CallbackInfo ci) {
        ExtremeRenderBudget.beginFrame();
        VeloTuneManager.governor().beginFrame();
        if (!FrameSyncHandler.allowDraw()) {
            ci.cancel();
        }
    }

    @Inject(method={"method_3192"}, at={@At(value="RETURN")})
    private void framesync$endFrame(class_9779 tickCounter, boolean tick, CallbackInfo ci) {
        VeloTuneManager.governor().endFrame();
    }
}

