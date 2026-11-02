/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_703
 */
package ru.decide.optimization.velotune.perf;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_703;
import ru.decide.optimization.velotune.VeloTuneManager;
import ru.decide.optimization.velotune.perf.ExtremeRenderBudget;
import ru.decide.optimization.velotune.perf.Metrics;
import ru.decide.optimization.velotune.perf.PerformanceGovernor;
import ru.decide.optimization.velotune.perf.VeloTuneConfig;

@Environment(value=EnvType.CLIENT)
public final class ParticleBudget {
    private static long frame = Long.MIN_VALUE;
    private static int accepted;

    private ParticleBudget() {
    }

    public static boolean allow(class_703 particle) {
        double d;
        VeloTuneConfig config = VeloTuneManager.config();
        if (!config.enabled || !config.particles.enabled) {
            return true;
        }
        PerformanceGovernor governor = VeloTuneManager.governor();
        long currentFrame = governor.frameIndex();
        if (currentFrame != frame) {
            frame = currentFrame;
            accepted = 0;
        }
        if (ExtremeRenderBudget.active() && config.extreme.enabled) {
            if (ExtremeRenderBudget.cullParticle(particle)) {
                Metrics.particleDrop();
                return false;
            }
            if (accepted++ < config.extreme.particleBudgetPerFrame) {
                return true;
            }
            Metrics.particleDrop();
            return false;
        }
        int pressure = governor.pressureLevel();
        int budget = ParticleBudget.scaledBudget(config.particles.spawnBudgetPerFrame, pressure);
        if (accepted++ < budget) {
            return true;
        }
        double distanceSquared = ParticleBudget.distanceSquaredToCamera(particle);
        double nearSquared = config.particles.nearDistance * config.particles.nearDistance;
        if (distanceSquared <= d && accepted <= budget + Math.max(64, budget / 3)) {
            return true;
        }
        double hardSquared = config.particles.hardDistance * config.particles.hardDistance;
        if (pressure >= 2 && distanceSquared > hardSquared) {
            Metrics.particleDrop();
            return false;
        }
        int divisor = switch (pressure) {
            case 0 -> 2;
            case 1 -> 3;
            case 2 -> 5;
            default -> 8;
        };
        boolean keep = Math.floorMod(System.identityHashCode(particle) ^ (int)currentFrame, divisor) == 0;
        boolean bl = keep;
        if (!keep) {
            Metrics.particleDrop();
        }
        return keep;
    }

    private static int scaledBudget(int base, int pressure) {
        return switch (pressure) {
            case 0 -> base;
            case 1 -> Math.max(64, base * 3 / 4);
            case 2 -> Math.max(64, base * 2 / 5);
            default -> Math.max(64, base / 5);
        };
    }

    private static double distanceSquaredToCamera(class_703 particle) {
        class_310 client = class_310.method_1551();
        if (client.field_1773 == null) {
            return 0.0;
        }
        class_243 camera = client.field_1773.method_19418().method_71156();
        class_238 box = particle.method_3064();
        double x = (box.field_1323 + box.field_1320) * 0.5;
        double y = (box.field_1322 + box.field_1325) * 0.5;
        double z = (box.field_1321 + box.field_1324) * 0.5;
        double dx = x - camera.field_1352;
        double dy = y - camera.field_1351;
        double dz = z - camera.field_1350;
        return dx * dx + dy * dy + dz * dz;
    }
}

