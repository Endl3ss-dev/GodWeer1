/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1675
 *  net.minecraft.class_1937
 *  net.minecraft.class_238
 *  net.minecraft.class_239
 *  net.minecraft.class_243
 *  net.minecraft.class_3532
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3966
 */
package ru.decide.utils.aura;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1675;
import net.minecraft.class_1937;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3966;
import ru.decide.utils.annotation.IMinecraft;

@Environment(value=EnvType.CLIENT)
public final class RayTraceUtil
implements IMinecraft {
    public static class_3966 traceEntities(class_1297 shooter, class_243 startVector, class_243 endVector, class_238 boundingBox, Predicate<class_1297> filter, double distance) {
        class_1937 world = shooter.method_73183();
        double closestDistance = distance;
        class_1297 closestEntity = null;
        class_243 closestHitVector = null;
        for (class_1297 entity : world.method_8333(shooter, boundingBox, filter)) {
            class_238 entityBoundingBox = entity.method_5829().method_1014((double)entity.method_5871());
            Optional optional = entityBoundingBox.method_992(startVector, endVector);
            if (!entityBoundingBox.method_1006(startVector)) {
                if (!optional.isPresent()) continue;
            }
            double distanceToHit = optional.map(arg_0 -> ((class_243)startVector).method_1025(arg_0)).orElse(0.0);
            if (!((distanceToHit = Math.sqrt(distanceToHit)) < closestDistance) && closestDistance != 0.0 || entity.method_5668() == shooter.method_5668()) continue;
            closestEntity = entity;
            closestHitVector = optional.orElse(startVector);
            closestDistance = distanceToHit;
        }
        return closestEntity == null ? null : new class_3966(closestEntity, closestHitVector);
    }

    public static class_239 calculateRayTrace(double distance, float yaw, float pitch, class_1297 entity, boolean ignoreBlocks) {
        float tickDelta = mc.method_61966().method_60637(true);
        class_243 startVector = RayTraceUtil.mc.field_1724.method_5836(tickDelta);
        class_243 directionVector = RayTraceUtil.getVectorForRotation(pitch, yaw);
        class_243 endVector = startVector.method_1019(directionVector.method_1021(distance));
        class_239 blockResult = RayTraceUtil.traceBlock(startVector, endVector, class_3959.class_3960.field_17558, class_3959.class_242.field_1348);
        double entityDistance = blockResult.method_17784().method_1025(startVector);
        class_238 entityBoundingBox = entity.method_5829().method_18804(directionVector.method_1021(distance)).method_1014(1.0);
        class_3966 entityHitResult = class_1675.method_18075((class_1297)entity, (class_243)startVector, (class_243)endVector, (class_238)entityBoundingBox, x -> !x.method_7325() && x.method_5805() && x.method_5863(), (double)(distance * distance));
        if (entityHitResult != null && (ignoreBlocks || entityHitResult.method_17784().method_1025(startVector) < entityDistance)) {
            return entityHitResult;
        }
        return blockResult;
    }

    public static boolean rayTraceEntity(float yaw, float pitch, double distance, class_1297 entity) {
        float tickDelta = mc.method_61966().method_60637(true);
        class_243 eyeVec = RayTraceUtil.mc.field_1724.method_5836(tickDelta);
        class_243 lookVec = RayTraceUtil.getVectorForRotation(pitch, yaw);
        class_243 endVec = eyeVec.method_1019(lookVec.method_1021(distance));
        if (RayTraceUtil.mc.field_1724 != null && RayTraceUtil.mc.field_1724.method_6128() && (double)entity.method_5739(entity) > distance * 3.0) {
            return true;
        }
        class_238 entityBox = entity.method_5829();
        return entityBox.method_1006(eyeVec) || entityBox.method_992(eyeVec, endVec).isPresent();
    }

    public static class_243 getVectorForRotation(float pitch, float yaw) {
        float yawRadians = -yaw * ((float)Math.PI / 180) - (float)Math.PI;
        float pitchRadians = -pitch * ((float)Math.PI / 180);
        float cosYaw = class_3532.method_15362((double)yawRadians);
        float sinYaw = class_3532.method_15374((double)yawRadians);
        float cosPitch = -class_3532.method_15362((double)pitchRadians);
        float sinPitch = class_3532.method_15374((double)pitchRadians);
        return new class_243((double)(sinYaw * cosPitch), (double)sinPitch, (double)(cosYaw * cosPitch));
    }

    public static class_239 traceBlock(class_243 startVec, class_243 endVec, class_3959.class_3960 blockMode, class_3959.class_242 fluidMode) {
        return RayTraceUtil.mc.field_1687.method_17742(new class_3959(startVec, endVec, blockMode, fluidMode, (class_1297)RayTraceUtil.mc.field_1724));
    }

    public static class_243 calculateViewVector(float yaw, float pitch) {
        float pitchRad = (float)((double)pitch * (Math.PI / 180));
        float yawRad = (float)((double)(-yaw) * (Math.PI / 180));
        float cosYaw = class_3532.method_15362((double)yawRad);
        float sinYaw = class_3532.method_15374((double)yawRad);
        float cosPitch = class_3532.method_15362((double)pitchRad);
        float sinPitch = class_3532.method_15374((double)pitchRad);
        return new class_243((double)(sinYaw * cosPitch), (double)(-sinPitch), (double)(cosYaw * cosPitch));
    }

    @Generated
    private RayTraceUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

