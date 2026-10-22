package swerve.utils.rotation;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

/**
 * Свободный обзор при молчаливой ротации.
 *
 * <p>Порт из Private/src/java/org/privates/client/managers/component/impl/rotation/FreeLookComponent.java.
 *
 * <p>Идея та же, что в Private: тело игрока крутит аура, сервер видит именно эти
 * углы, а камера остаётся там, куда её увёл игрок мышью. Vanilla 1.21.4 крутит
 * игрока через {@code LocalPlayer.turn(yaw, pitch)}, поэтому перехват стоит на
 * вызове {@code turn} из {@code MouseHandler.turnPlayer} — дельты там уже
 * отмасштабированы чувствительностью, спроецированы с smoothing и инверсией Y,
 * так что домножать их на что-либо нельзя (в 1.16.5 множитель 0.15 жил снаружи).
 *
 * <p>Дополнение к оригиналу — «догон» камеры ({@link #setFollowIdle(boolean)}):
 * когда игрок не двигает мышь, свободные углы плавно подтягиваются к углам тела
 * (то есть к цели ротации). Так камера показывает, куда бьёт аура, но стоит
 * тронуть мышь — управление мгновенно возвращается игроку, и чувствительность
 * остаётся ванильной: аура больше не складывается с движением мыши.
 */
public final class FreeLook {

    /** Сколько миллисекунд после движения мыши камера не «догоняет» цель. */
    private static final long IDLE_BEFORE_FOLLOW_MS = 250L;
    /** Скорость догона: доля оставшегося угла за секунду (8 = ~12% за кадр при 60 FPS). */
    private static final float FOLLOW_SPEED = 8.0F;

    private static boolean active;
    private static boolean followIdle;
    private static float yaw;
    private static float pitch;
    private static long lastMouseMs;

    private FreeLook() {
    }

    public static boolean active() {
        return active;
    }

    public static float yaw() {
        return yaw;
    }

    public static float pitch() {
        return pitch;
    }

    /** Камера сама доворачивается на цель, пока мышь не двигается. */
    public static void setFollowIdle(boolean value) {
        followIdle = value;
    }

    /** Включает свободный обзор, начиная отсчёт от текущего разворота игрока. */
    public static void begin(LocalPlayer player) {
        if (!active) {
            yaw = player.getYRot();
            pitch = player.getXRot();
            lastMouseMs = System.currentTimeMillis();
            active = true;
        }
    }

    public static void end() {
        active = false;
    }

    /** Дельты мыши (в градусах, уже с чувствительностью). */
    public static void turn(double deltaYaw, double deltaPitch) {
        pitch = Mth.clamp((float) (pitch + deltaPitch), -90.0F, 90.0F);
        yaw = (float) (yaw + deltaYaw);
        lastMouseMs = System.currentTimeMillis();
    }

    /** Пока обзор свободен, держим свободные углы как есть; после — синхронизируем с телом. */
    public static void sync(LocalPlayer player) {
        if (!active) {
            yaw = player.getYRot();
            pitch = player.getXRot();
        }
    }

    /**
     * Покадровый догон камеры к углам тела (вызывается из камеры, до применения углов).
     *
     * @param dtSeconds время кадра в секундах — догон не зависит от FPS
     */
    public static void follow(LocalPlayer player, float dtSeconds) {
        if (!active || !followIdle) {
            return;
        }
        if (System.currentTimeMillis() - lastMouseMs < IDLE_BEFORE_FOLLOW_MS) {
            return;
        }
        if (dtSeconds <= 0.0F) {
            return;
        }
        float k = Math.min(1.0F, dtSeconds * FOLLOW_SPEED);
        yaw += Mth.wrapDegrees(player.getYRot() - yaw) * k;
        pitch += (player.getXRot() - pitch) * k;
    }
}
