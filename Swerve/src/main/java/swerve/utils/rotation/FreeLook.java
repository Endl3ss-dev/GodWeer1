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
 * отмасштабированы чувствительностью, спроецированы со smoothing и инверсией Y,
 * так что домножать их на что-либо нельзя (в 1.16.5 множитель 0.15 жил снаружи).
 */
public final class FreeLook {

    private static boolean active;
    private static float yaw;
    private static float pitch;

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

    /** Включает свободный обзор, начиная отсчёт от текущего разворота игрока. */
    public static void begin(LocalPlayer player) {
        if (!active) {
            yaw = player.getYRot();
            pitch = player.getXRot();
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
    }

    /** Пока обзор свободен, держим свободные углы как есть; после — синхронизируем с телом. */
    public static void sync(LocalPlayer player) {
        if (!active) {
            yaw = player.getYRot();
            pitch = player.getXRot();
        }
    }
}
