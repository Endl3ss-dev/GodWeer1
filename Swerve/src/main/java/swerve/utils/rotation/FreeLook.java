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
 * вызове {@code turn} из {@code MouseHandler.turnPlayer}. Дельты туда приходят
 * уже с чувствительностью и smoothing, но БЕЗ внутреннего множителя
 * {@code Entity.turn}: ванильный метод сам умножает оба угла на 0.15. Перехват
 * обязан повторить этот шаг, иначе свободный обзор крутится в 6.67 раза быстрее
 * мыши — именно так и выглядела «слишком быстрая камера» при включённой ауре.
 *
 * <p>Дополнение к оригиналу — «догон» камеры ({@link #setFollowIdle(boolean)}):
 * когда игрок не двигает мышь, свободные углы плавно подтягиваются к углам тела
 * (то есть к цели ротации). Так камера показывает, куда бьёт аура, но стоит
 * тронуть мышь — управление мгновенно возвращается игроку, и чувствительность
 * остаётся ванильной: аура больше не складывается с движением мыши.
 */
public final class FreeLook {

    /** Ванильный множитель {@code Entity.turn}: 0.15 на каждую ось. */
    private static final double VANILLA_TURN_SCALE = 0.15D;
    /** Сколько миллисекунд после движения мыши камера не «догоняет» цель. */
    private static final long IDLE_BEFORE_FOLLOW_MS = 400L;
    /** Скорость догона: доля оставшегося угла за секунду (3 = ~5% за кадр при 60 FPS). */
    private static final float FOLLOW_SPEED = 3.0F;
    /** Потолок угловой скорости догона, градусы в секунду: камера не «телепортируется». */
    private static final float MAX_FOLLOW_DEG_PER_SECOND = 220.0F;

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

    /**
     * Дельты мыши (в градусах, уже с чувствительностью и smoothing).
     *
     * <p>Масштаб 0.15 повторяет {@code Entity.turn}: без него камера свободного
     * обзора поворачивалась в 6.67 раза быстрее, чем без ауры.
     */
    public static void turn(double deltaYaw, double deltaPitch) {
        pitch = Mth.clamp((float) (pitch + deltaPitch * VANILLA_TURN_SCALE), -90.0F, 90.0F);
        yaw = Mth.wrapDegrees((float) (yaw + deltaYaw * VANILLA_TURN_SCALE));
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
        float maxStep = MAX_FOLLOW_DEG_PER_SECOND * dtSeconds;
        float yawStep = Mth.clamp(Mth.wrapDegrees(player.getYRot() - yaw) * k, -maxStep, maxStep);
        float pitchStep = Mth.clamp((player.getXRot() - pitch) * k, -maxStep, maxStep);
        yaw = Mth.wrapDegrees(yaw + yawStep);
        pitch = Mth.clamp(pitch + pitchStep, -90.0F, 90.0F);
    }
}
