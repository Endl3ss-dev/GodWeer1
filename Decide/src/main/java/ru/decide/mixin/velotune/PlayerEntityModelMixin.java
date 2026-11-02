/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_310
 *  net.minecraft.class_591
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.velotune;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_310;
import net.minecraft.class_591;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.optimization.velotune.VeloTuneManager;
import ru.decide.optimization.velotune.perf.CachedPlayerPose;
import ru.decide.optimization.velotune.perf.Metrics;
import ru.decide.optimization.velotune.perf.PlayerLodPolicy;
import ru.decide.optimization.velotune.perf.PlayerPoseThrottle;
import ru.decide.optimization.velotune.perf.VeloTuneConfig;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_591.class})
abstract class PlayerEntityModelMixin {
    @Unique
    private final Map<Integer, CachedPlayerPose> velotune$poseCache = new HashMap<Integer, CachedPlayerPose>();

    PlayerEntityModelMixin() {
    }

    @Inject(method={"method_62110"}, at={@At(value="HEAD")}, cancellable=true)
    private void velotune$reusePose(class_10055 state, CallbackInfo ci) {
        List parts;
        VeloTuneConfig config = VeloTuneManager.config();
        if (!config.enabled || !config.players.poseCache || state.field_53528 == 0 || PlayerEntityModelMixin.velotune$isLocalPlayer(state)) {
            return;
        }
        int pressure = VeloTuneManager.governor().pressureLevel();
        int interval = PlayerLodPolicy.poseInterval(state.field_53332, pressure, config.players);
        if (config.extreme.enabled) {
            interval = Math.max(interval, config.extreme.playerPoseInterval);
        }
        if (interval <= 1 && !config.extreme.enabled) {
            return;
        }
        if (this.velotune$poseCache.size() > config.players.maxCachedPlayers) {
            this.velotune$poseCache.clear();
        }
        CachedPlayerPose cached = this.velotune$poseCache.get(state.field_53528);
        int signature = PlayerEntityModelMixin.velotune$signature(state);
        long frame = VeloTuneManager.governor().frameIndex();
        long nowNanos = System.nanoTime();
        if (cached != null && PlayerPoseThrottle.canReuse(config.extreme.enabled, cached.signature(), signature, frame - cached.frame(), interval, nowNanos - cached.capturedNanos(), config.extreme.playerPoseUpdatesPerSecond, PlayerEntityModelMixin.velotune$urgent(state)) && cached.apply(parts = ((class_591)this).method_63513())) {
            Metrics.poseHit();
            ci.cancel();
            return;
        }
        Metrics.poseMiss();
    }

    @Inject(method={"method_62110"}, at={@At(value="RETURN")})
    private void velotune$capturePose(class_10055 state, CallbackInfo ci) {
        VeloTuneConfig config = VeloTuneManager.config();
        if (!config.enabled || !config.players.poseCache || state.field_53528 == 0 || PlayerEntityModelMixin.velotune$isLocalPlayer(state)) {
            return;
        }
        int interval = PlayerLodPolicy.poseInterval(state.field_53332, VeloTuneManager.governor().pressureLevel(), config.players);
        if (config.extreme.enabled) {
            interval = Math.max(interval, config.extreme.playerPoseInterval);
        }
        if (interval <= 1 && !config.extreme.enabled) {
            return;
        }
        List parts = ((class_591)this).method_63513();
        CachedPlayerPose cached = this.velotune$poseCache.computeIfAbsent(state.field_53528, ignored -> new CachedPlayerPose());
        cached.capture(parts, PlayerEntityModelMixin.velotune$signature(state), VeloTuneManager.governor().frameIndex(), System.nanoTime());
    }

    @Unique
    private static boolean velotune$isLocalPlayer(class_10055 state) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null) {
            return false;
        }
        return state.field_53528 == mc.field_1724.method_5628();
    }

    @Unique
    private static boolean velotune$urgent(class_10055 state) {
        return state.field_53410 || state.field_63604 > 0.0f || state.field_53451 > 0.0f || state.field_53412 || state.field_53414;
    }

    @Unique
    private static int velotune$signature(class_10055 state) {
        int result = Objects.hashCode(state.field_53465);
        result = 31 * result + Objects.hashCode(Float.valueOf(state.field_53403));
        result = 31 * result + Objects.hashCode(Float.valueOf(state.field_53450));
        result = 31 * result + (state.field_53543 ? 1 : 0);
        result = 31 * result + (state.field_53544 ? 1 : 0);
        result = 31 * result + (state.field_53530 ? 1 : 0);
        result = 31 * result + (state.field_53531 ? 1 : 0);
        result = 31 * result + (state.field_53545 ? 1 : 0);
        result = 31 * result + (state.field_53546 ? 1 : 0);
        return result;
    }
}

