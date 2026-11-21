/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10185
 *  net.minecraft.class_1041
 *  net.minecraft.class_241
 *  net.minecraft.class_243
 *  net.minecraft.class_3532
 *  net.minecraft.class_3675
 */
package ru.decide.utils.player;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10185;
import net.minecraft.class_1041;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3675;
import ru.decide.manager.event_impl.EventMoveInput;
import ru.decide.utils.annotation.IMinecraft;

@Environment(value=EnvType.CLIENT)
public final class MoveUtil
implements IMinecraft {
    public static boolean isMoving() {
        if (MoveUtil.mc.field_1724 == null) {
            return false;
        }
        return MoveUtil.mc.field_1724.field_3913.method_3128().field_1342 != 0.0f || MoveUtil.mc.field_1724.field_3913.method_3128().field_1343 != 0.0f;
    }

    public static float getDirection() {
        float rotationYaw = MoveUtil.mc.field_1724.method_36454();
        float strafeFactor = 0.0f;
        if (MoveUtil.mc.field_1724.field_3913.field_54155.comp_3159()) {
            strafeFactor = 1.0f;
        }
        if (MoveUtil.mc.field_1724.field_3913.field_54155.comp_3160()) {
            strafeFactor = -1.0f;
        }
        if (strafeFactor == 0.0f) {
            if (MoveUtil.mc.field_1724.field_3913.field_54155.comp_3161()) {
                rotationYaw -= 90.0f;
            }
            if (MoveUtil.mc.field_1724.field_3913.field_54155.comp_3162()) {
                rotationYaw += 90.0f;
            }
        } else {
            if (MoveUtil.mc.field_1724.field_3913.field_54155.comp_3161()) {
                rotationYaw -= 45.0f * strafeFactor;
            }
            if (MoveUtil.mc.field_1724.field_3913.field_54155.comp_3162()) {
                rotationYaw += 45.0f * strafeFactor;
            }
        }
        if (strafeFactor < 0.0f) {
            rotationYaw -= 180.0f;
        }
        return (float)Math.toRadians(rotationYaw);
    }

    public static double getDegreesRelativeToView(class_243 positionRelativeToPlayer, float yaw) {
        float optimalYaw = (float)Math.atan2(-positionRelativeToPlayer.field_1352, positionRelativeToPlayer.field_1350);
        double currentYaw = Math.toRadians(class_3532.method_15393((float)yaw));
        return Math.toDegrees(class_3532.method_15338((double)((double)optimalYaw - currentYaw)));
    }

    public static class_10185 getDirectionalInputForDegrees(class_10185 input, double dgs) {
        return MoveUtil.getDirectionalInputForDegrees(input, dgs, 20.0f);
    }

    public static void setSpeed(double speed) {
        if (MoveUtil.mc.field_1724 == null) {
            return;
        }
        float yaw = MoveUtil.mc.field_1724.method_36454();
        double rad = Math.toRadians(yaw);
        double x = 0.0;
        double z = 0.0;
        class_241 move = MoveUtil.mc.field_1724.field_3913.method_3128();
        float forward = move.field_1342;
        float strafe = move.field_1343;
        if (forward > 0.0f) {
            x -= Math.sin(rad) * speed;
            z += Math.cos(rad) * speed;
        }
        if (forward < 0.0f) {
            x += Math.sin(rad) * speed;
            z -= Math.cos(rad) * speed;
        }
        if (strafe > 0.0f) {
            x += Math.cos(rad) * speed;
            z += Math.sin(rad) * speed;
        }
        if (strafe < 0.0f) {
            x -= Math.cos(rad) * speed;
            z -= Math.sin(rad) * speed;
        }
        if (strafe > 0.0f && forward > 0.0f) {
            x = Math.cos(Math.toRadians(yaw + 45.0f)) * speed;
            z = Math.sin(Math.toRadians(yaw + 45.0f)) * speed;
        }
        if (strafe < 0.0f && forward > 0.0f) {
            x = -Math.cos(Math.toRadians(yaw - 45.0f)) * speed;
            z = -Math.sin(Math.toRadians(yaw - 45.0f)) * speed;
        }
        if (strafe > 0.0f && forward < 0.0f) {
            x = -Math.cos(Math.toRadians(yaw + 135.0f)) * speed;
            z = -Math.sin(Math.toRadians(yaw + 135.0f)) * speed;
        }
        if (strafe < 0.0f && forward < 0.0f) {
            x = Math.cos(Math.toRadians(yaw - 135.0f)) * speed;
            z = Math.sin(Math.toRadians(yaw - 135.0f)) * speed;
        }
        MoveUtil.mc.field_1724.method_18800(x, MoveUtil.mc.field_1724.method_18798().field_1351, z);
    }

    public static class_10185 getDirectionalInputForDegrees(class_10185 input, double dgs, float deadAngle) {
        boolean forwards = input.comp_3159();
        boolean backwards = input.comp_3160();
        boolean left = input.comp_3161();
        boolean right = input.comp_3162();
        if (dgs >= (double)(-90.0f + deadAngle) && dgs <= (double)(90.0f - deadAngle)) {
            forwards = true;
        } else if (dgs < (double)(-90.0f - deadAngle) || dgs > (double)(90.0f + deadAngle)) {
            backwards = true;
        }
        if (dgs >= (double)(0.0f + deadAngle) && dgs <= (double)(180.0f - deadAngle)) {
            right = true;
        } else if (dgs >= (double)(-180.0f + deadAngle) && dgs <= (double)(0.0f - deadAngle)) {
            left = true;
        }
        return new class_10185(forwards, backwards, left, right, input.comp_3163(), input.comp_3164(), input.comp_3165());
    }

    public static float[] getMovementFromKeys() {
        float forward = 0.0f;
        float strafe = 0.0f;
        if (class_3675.method_15987((class_1041)mc.method_22683(), (int)MoveUtil.mc.field_1690.field_1894.method_1429().method_1444())) {
            forward += 1.0f;
        }
        if (class_3675.method_15987((class_1041)mc.method_22683(), (int)MoveUtil.mc.field_1690.field_1881.method_1429().method_1444())) {
            forward -= 1.0f;
        }
        if (class_3675.method_15987((class_1041)mc.method_22683(), (int)MoveUtil.mc.field_1690.field_1913.method_1429().method_1444())) {
            strafe += 1.0f;
        }
        if (class_3675.method_15987((class_1041)mc.method_22683(), (int)MoveUtil.mc.field_1690.field_1849.method_1429().method_1444())) {
            strafe -= 1.0f;
        }
        return new float[]{forward, strafe};
    }

    public static double[] calculateDirection(double distance) {
        float[] movement = MoveUtil.getMovementFromKeys();
        return MoveUtil.calculateDirection(movement[0], movement[1], distance);
    }

    public static double[] calculateDirection(float forward, float sideways, double distance) {
        float yaw = MoveUtil.mc.field_1724.method_36454();
        if (forward != 0.0f) {
            if (sideways > 0.0f) {
                yaw += forward > 0.0f ? -45.0f : 45.0f;
            } else if (sideways < 0.0f) {
                yaw += forward > 0.0f ? 45.0f : -45.0f;
            }
            sideways = 0.0f;
            forward = forward > 0.0f ? 1.0f : -1.0f;
        }
        double sinYaw = Math.sin(Math.toRadians(yaw + 90.0f));
        double cosYaw = Math.cos(Math.toRadians(yaw + 90.0f));
        double xMovement = (double)forward * distance * cosYaw + (double)sideways * distance * sinYaw;
        double zMovement = (double)forward * distance * sinYaw - (double)sideways * distance * cosYaw;
        return new double[]{xMovement, zMovement};
    }

    public static void fixMovementTest(EventMoveInput event, float yaw, float targetYaw) {
        float forward = event.getForward();
        float strafe = event.getStrafe();
        double angle = class_3532.method_15338((double)Math.toDegrees(MoveUtil.direction(MoveUtil.mc.field_1724.method_6128() ? yaw : targetYaw, forward, strafe)));
        if (forward == 0.0f && strafe == 0.0f) {
            return;
        }
        float closestForward = 0.0f;
        float closestStrafe = 0.0f;
        float closestDifference = Float.MAX_VALUE;
        for (float predictedForward = -1.0f; predictedForward <= 1.0f; predictedForward += 1.0f) {
            for (float predictedStrafe = -1.0f; predictedStrafe <= 1.0f; predictedStrafe += 1.0f) {
                double predictedAngle;
                double difference;
                if (predictedStrafe == 0.0f && predictedForward == 0.0f || !((difference = Math.abs(angle - (predictedAngle = class_3532.method_15338((double)Math.toDegrees(MoveUtil.direction(yaw, predictedForward, predictedStrafe)))))) < (double)closestDifference)) continue;
                closestDifference = (float)difference;
                closestForward = predictedForward;
                closestStrafe = predictedStrafe;
            }
        }
        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }

    public static void fixMovement(EventMoveInput event, float yaw, float targetYaw) {
        float forward = event.getForward();
        float strafe = event.getStrafe();
        double angle = class_3532.method_15338((double)Math.toDegrees(MoveUtil.direction(MoveUtil.mc.field_1724.method_6128() ? yaw : targetYaw, forward, strafe)));
        if (forward == 0.0f && strafe == 0.0f) {
            return;
        }
        float closestForward = 0.0f;
        float closestStrafe = 0.0f;
        float closestDifference = Float.MAX_VALUE;
        for (float predictedForward = -1.0f; predictedForward <= 1.0f; predictedForward += 1.0f) {
            for (float predictedStrafe = -1.0f; predictedStrafe <= 1.0f; predictedStrafe += 1.0f) {
                double predictedAngle;
                double difference;
                if (predictedStrafe == 0.0f && predictedForward == 0.0f || !((difference = Math.abs(angle - (predictedAngle = class_3532.method_15338((double)Math.toDegrees(MoveUtil.direction(yaw, predictedForward, predictedStrafe)))))) < (double)closestDifference)) continue;
                closestDifference = (float)difference;
                closestForward = predictedForward;
                closestStrafe = predictedStrafe;
            }
        }
        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }

    public static double direction(float rotationYaw, float moveForward, float moveStrafing) {
        if (moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (moveForward < 0.0f) {
            forward = -0.5f;
        }
        if (moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    @Generated
    private MoveUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

