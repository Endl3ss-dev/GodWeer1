package swerve.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

import swerve.events.movement.MoveInputEvent;

/**
 * Пересчёт WASD при молчаливой ротации.
 *
 * <p>Порт из Private/src/java/org/privates/client/utils/player/MoveUtil.java
 * (метод {@code fixMovement(MoveInputEvent, float)}).
 *
 * <p>Зачем: тело игрока развернуто силой движения, а не взглядом. Когда клиент
 * крутит {@code setYRot} на цель, ванильный ввод «вперёд» считается от нового
 * yaw и игрок начинает бежать в сторону. Здесь подбираются forward/strafe,
 * которые дают то же направление движения, что и до подмены угла.
 *
 * <p>Собственная копия {@code ElytraFlightRotation.fixMovement} не переиспользуется:
 * там нужен ровно один вентиль на весь мод, иначе elytra-полёт начнёт дёргаться
 * от чужого yaw.
 */
public final class MoveUtil {

    private MoveUtil() {
    }

    public static void fixMovement(MoveInputEvent event, float yaw) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        float forward = event.forward;
        float strafe = event.strafe;
        if (forward == 0.0F && strafe == 0.0F) {
            return;
        }

        // В полёте Private считает направление от реального разворота тела, а не от
        // подставленного yaw: иначе элитра уходит в сторону от курса.
        double angle = Mth.wrapDegrees(Math.toDegrees(direction(
                player.isFallFlying() ? player.getYRot() : yaw, forward, strafe)));

        float bestForward = 0.0F;
        float bestStrafe = 0.0F;
        float smallestDifference = Float.MAX_VALUE;
        // Предсказание считается от РЕАЛЬНОГО разворота тела: именно его использует
        // ванильное движение, пока аура держит yaw на цели.
        float bodyYaw = player.getYRot();
        for (float testForward = -1.0F; testForward <= 1.0F; testForward++) {
            for (float testStrafe = -1.0F; testStrafe <= 1.0F; testStrafe++) {
                if (testForward == 0.0F && testStrafe == 0.0F) {
                    continue;
                }
                double testAngle = Mth.wrapDegrees(Math.toDegrees(direction(bodyYaw, testForward, testStrafe)));
                // В оригинале разница берётся без wrapDegrees — сохраняем как есть.
                float difference = (float) Math.abs(angle - testAngle);
                if (difference < smallestDifference) {
                    smallestDifference = difference;
                    bestForward = testForward;
                    bestStrafe = testStrafe;
                }
            }
        }

        event.forward = bestForward;
        event.strafe = bestStrafe;
    }

    /**
     * Режим «Грубая» (RotationComponent.applyGrubayaPull + fixMovementToTarget):
     * направление берётся от угла на цель, а не от камеры.
     */
    public static void fixMovementToTarget(MoveInputEvent event, float yaw, float target) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        float forward = event.forward;
        float strafe = event.strafe;
        if (forward == 0.0F && strafe == 0.0F) {
            return;
        }

        double angle = Mth.wrapDegrees(Math.toDegrees(direction(
                player.isFallFlying() ? yaw : target, forward, strafe)));

        float bestForward = 0.0F;
        float bestStrafe = 0.0F;
        float smallestDifference = Float.MAX_VALUE;
        for (float testForward = -1.0F; testForward <= 1.0F; testForward++) {
            for (float testStrafe = -1.0F; testStrafe <= 1.0F; testStrafe++) {
                if (testForward == 0.0F && testStrafe == 0.0F) {
                    continue;
                }
                double testAngle = Mth.wrapDegrees(Math.toDegrees(direction(yaw, testForward, testStrafe)));
                // В оригинале разница берётся без wrapDegrees — сохраняем как есть.
                float difference = (float) Math.abs(angle - testAngle);
                if (difference < smallestDifference) {
                    smallestDifference = difference;
                    bestForward = testForward;
                    bestStrafe = testStrafe;
                }
            }
        }

        event.forward = bestForward;
        event.strafe = bestStrafe;
    }

    /**
     * Режим «Сфокусированная» (MoveUtil.fixMovement2 из Private): ввод примагничивается
     * к ближайшему из восьми направлений (в оригинале второй аргумент yaw
     * не использовался — здесь он не нужен).
     */
    public static void fixMovement2(MoveInputEvent event) {
        float forward = event.forward;
        float strafe = event.strafe;
        if (forward == 0.0F && strafe == 0.0F) {
            return;
        }

        double angle = Math.toDegrees(Math.atan2(strafe, forward));
        angle = Math.round(angle / 45.0D) * 45.0D;

        double radians = Math.toRadians(angle);
        event.forward = (float) Math.round(Math.cos(radians));
        event.strafe = (float) Math.round(Math.sin(radians));
    }

    private static double direction(float yaw, float forward, float strafe) {
        if (forward < 0.0F) {
            yaw += 180.0F;
        }
        float factor = 1.0F;
        if (forward < 0.0F) {
            factor = -0.5F;
        }
        if (forward > 0.0F) {
            factor = 0.5F;
        }
        if (strafe > 0.0F) {
            yaw -= 90.0F * factor;
        }
        if (strafe < 0.0F) {
            yaw += 90.0F * factor;
        }
        return Math.toRadians(yaw);
    }
}
