/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  net.minecraft.class_329
 *  net.minecraft.class_332
 *  net.minecraft.class_638
 *  net.minecraft.class_9779
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_638;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.optimization.velotune.VeloTuneManager;
import ru.decide.optimization.velotune.hud.ScreenFramebufferCache;
import ru.decide.optimization.velotune.perf.VeloTuneConfig;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_329.class})
abstract class InGameHudMixin {
    InGameHudMixin() {
    }

    @Inject(method={"method_1753"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$limitHudRate(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
        VeloTuneConfig config;
        class_310 client = class_310.method_1551();
        if (!InGameHudMixin.velotune$shouldBuffer(client, config = VeloTuneManager.config())) {
            ScreenFramebufferCache.disable();
            return;
        }
        class_276 main = client.method_1522();
        if (main.field_1482 <= 0 || main.field_1481 <= 0) {
            ScreenFramebufferCache.disable();
            return;
        }
        class_638 owner = client.field_1755 == null ? client.field_1687 : client.field_1755;
        ScreenFramebufferCache.beginFrame(owner, main.field_1482, main.field_1481, System.nanoTime(), config.extreme.hudUpdatesPerSecond);
        if (!ScreenFramebufferCache.shouldCaptureFrame()) {
            ci.cancel();
        }
    }

    private static boolean velotune$shouldBuffer(class_310 client, VeloTuneConfig config) {
        return config.enabled && config.extreme.enabled && config.extreme.hudBuffer && client.field_1687 != null && client.field_1724 != null && client.method_18506() == null;
    }
}

