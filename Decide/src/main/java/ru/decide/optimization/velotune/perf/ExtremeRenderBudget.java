/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_2586
 *  net.minecraft.class_310
 *  net.minecraft.class_703
 */
package ru.decide.optimization.velotune.perf;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2586;
import net.minecraft.class_310;
import net.minecraft.class_703;
import ru.decide.optimization.velotune.VeloTuneManager;
import ru.decide.optimization.velotune.perf.ExtremeLodPolicy;
import ru.decide.optimization.velotune.perf.VeloTuneConfig;

@Environment(value=EnvType.CLIENT)
public final class ExtremeRenderBudget {
    private static boolean active;
    private static double playerX;
    private static double playerY;
    private static double playerZ;
    private static int renderedPlayers;
    private static int renderedEntities;

    private ExtremeRenderBudget() {
    }

    public static void beginFrame() {
        VeloTuneConfig config = VeloTuneManager.config();
        class_310 client = class_310.method_1551();
        active = config.enabled && config.extreme.enabled && client.field_1724 != null;
        renderedPlayers = 0;
        renderedEntities = 0;
        if (active) {
            playerX = client.field_1724.method_23317();
            playerY = client.field_1724.method_23318();
            playerZ = client.field_1724.method_23321();
        }
    }

    public static boolean active() {
        return active;
    }

    public static boolean cullEntity(class_1297 entity, double distanceSquared, boolean player) {
        if (!active) {
            return false;
        }
        VeloTuneConfig.Extreme config = VeloTuneManager.config().extreme;
        if (ExtremeLodPolicy.belowCutoff(entity.method_23318(), playerY, config.verticalCullDepth)) {
            return true;
        }
        double distance = player ? config.playerRenderDistance : config.entityRenderDistance;
        double d = distance;
        if (ExtremeLodPolicy.beyondDistance(distanceSquared, distance)) {
            return true;
        }
        if (player) {
            return renderedPlayers++ >= config.maxRenderedPlayers;
        }
        return renderedEntities++ >= config.maxRenderedEntities;
    }

    public static boolean cullBlockEntity(class_2586 blockEntity) {
        if (!active || !VeloTuneManager.config().extreme.cullBlockEntities) {
            return false;
        }
        VeloTuneConfig.Extreme config = VeloTuneManager.config().extreme;
        class_2338 pos = blockEntity.method_11016();
        if (ExtremeLodPolicy.belowCutoff(pos.method_10264(), playerY, config.verticalCullDepth)) {
            return true;
        }
        return ExtremeLodPolicy.beyondDistance(ExtremeRenderBudget.squaredDistance((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5), config.blockEntityRenderDistance);
    }

    public static boolean cullParticle(class_703 particle) {
        if (!active) {
            return false;
        }
        VeloTuneConfig.Extreme config = VeloTuneManager.config().extreme;
        class_238 box = particle.method_3064();
        double x = (box.field_1323 + box.field_1320) * 0.5;
        double y = (box.field_1322 + box.field_1325) * 0.5;
        double z = (box.field_1321 + box.field_1324) * 0.5;
        return ExtremeLodPolicy.belowCutoff(y, playerY, config.verticalCullDepth) || ExtremeLodPolicy.beyondDistance(ExtremeRenderBudget.squaredDistance(x, y, z), config.particleRenderDistance);
    }

    public static boolean cullChunkSection(class_2338 origin) {
        return ExtremeRenderBudget.cullChunkSectionY(origin.method_10264());
    }

    public static boolean cullChunkSectionY(int originY) {
        if (!active || !VeloTuneManager.config().extreme.cullChunkSections) {
            return false;
        }
        VeloTuneConfig.Extreme config = VeloTuneManager.config().extreme;
        return ExtremeLodPolicy.cullChunkSection(originY, playerY, config.verticalCullDepth);
    }

    private static double squaredDistance(double x, double y, double z) {
        double dx = x - playerX;
        double dy = y - playerY;
        double dz = z - playerZ;
        return dx * dx + dy * dy + dz * dz;
    }
}

