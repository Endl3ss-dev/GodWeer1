package swerve.utils.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

/**
 * Молчаливая ротация: тело доворачивается на цель, камера остаётся свободной.
 *
 * <p>Порт из Private/src/java/org/privates/client/managers/component/impl/rotation/RotationComponent.java
 * без Baritone-вентиля, без FPS/«Продвинутого» режимов (они принадлежат Custom-ротации,
 * которая не портирована) и без тянущих состояний режима «Грубая».
 *
 * <p>Сохранена механика оригинала:
 * <ul>
 *   <li>задача AIM/RESET/IDLE и {@code idleTicks}: если цель не обновлялась дольше
 *       {@code timeout} тиков, ротация переходит в RESET и доворачивает тело назад
 *       к свободному обзору;</li>
 *   <li>приоритет (в Private это четвёртый аргумент {@code applyRotation},
 *       ошибочно названный «smoothTicks») — запрос с меньшим приоритетом игнорируется;</li>
 *   <li>шаг за тик квантуется по GCD мыши ({@link GcdUtil}) — иначе ротация
 *       выглядит как автокликер;</li>
 *   <li>{@code stopRotation} отдаёт камеру назад только после того, как тело
 *       догнало свободный обзор.</li>
 * </ul>
 */
public final class RotationController {

    public enum Task {
        AIM,
        RESET,
        IDLE
    }

    private static final float REACHED_EPSILON = 1.0F;

    private static Task task = Task.IDLE;
    private static float yawSpeed;
    private static float pitchSpeed;
    private static float yawReturnSpeed;
    private static float pitchReturnSpeed;
    private static int priority;
    private static int timeout;
    private static int idleTicks;
    private static LocalPlayer owner;

    private RotationController() {
    }

    public static boolean isRotating() {
        return task != Task.IDLE;
    }

    /** Сброс при смене мира/игрока: состояние не должно переезжать между сессиями. */
    public static void reset() {
        task = Task.IDLE;
        priority = 0;
        timeout = 0;
        idleTicks = 0;
        owner = null;
        FreeLook.end();
    }

    /**
     * Запрос ротации (RotationComponent.update из Private).
     *
     * @param timeout  сколько тиков без обновления держать цель до возврата
     * @param prio     приоритет задачи (в Private — «smoothTicks»)
     */
    public static void update(Rotation rotation, float yaw, float pitch, int timeout, int prio) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }
        if (owner != player) {
            owner = player;
            idleTicks = 0;
        }
        if (priority > prio) {
            return;
        }
        if (task == Task.IDLE) {
            FreeLook.begin(player);
        }
        yawSpeed = yaw;
        pitchSpeed = pitch;
        // В Private пяти-аргументный update подставляет скорость поворота в скорость
        // возврата; сохраняем это один в один.
        yawReturnSpeed = yaw;
        pitchReturnSpeed = pitch;
        RotationController.timeout = timeout;
        priority = prio;
        task = Task.AIM;
        stepTo(rotation.getYaw(), rotation.getPitch(), yawSpeed, pitchSpeed);
    }

    /** Тик компонента (UpdateEvent из Private): таймаут цели и возврат камеры. */
    public static void tick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            reset();
            return;
        }
        if (owner != player) {
            owner = player;
            idleTicks = 0;
            task = Task.IDLE;
            priority = 0;
        }
        if (task == Task.AIM && idleTicks > timeout) {
            task = Task.RESET;
        }
        if (task == Task.RESET) {
            resetRotation();
        } else if (task == Task.IDLE) {
            FreeLook.sync(player);
        }
        idleTicks++;
    }

    /** Возврат тела к свободному обзору обычной скоростью (resetRotation из Private). */
    public static void resetRotation() {
        stepTo(FreeLook.yaw(), FreeLook.pitch(), yawReturnSpeed * 0.6F, pitchReturnSpeed * 0.6F);
    }

    /** Ускоренный возврат — используется Matrix-ротацией при выключении. */
    public static void smoothResetRotation() {
        stepTo(FreeLook.yaw(), FreeLook.pitch(), yawReturnSpeed * 1.5F, pitchReturnSpeed * 1.5F);
    }

    public static void stopRotation() {
        task = Task.IDLE;
        priority = 0;
        FreeLook.end();
    }

    private static boolean stepTo(float targetYaw, float targetPitch, float maxYaw, float maxPitch) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return false;
        }
        if (maxYaw <= 0.0F && maxPitch <= 0.0F) {
            // Скорость возврата ещё не задана (ротацию ни разу не запрашивали):
            // не дёргаем углы нулевым шагом, просто завершаем возврат.
            stopRotation();
            return true;
        }

        float yawDelta = Mth.wrapDegrees(targetYaw - player.getYRot());
        float pitchDelta = targetPitch - player.getXRot();

        float clampedYaw = Math.min(Math.abs(yawDelta), maxYaw);
        float clampedPitch = Math.min(Math.abs(pitchDelta), maxPitch);

        player.setYRot(player.getYRot() + GcdUtil.getSensitivity(Mth.clamp(yawDelta, -clampedYaw, clampedYaw)));
        player.setXRot(Mth.clamp(
                player.getXRot() + GcdUtil.getSensitivity(Mth.clamp(pitchDelta, -clampedPitch, clampedPitch)),
                -90.0F, 90.0F));

        idleTicks = 0;
        boolean reached = Math.hypot(
                Math.abs(Mth.wrapDegrees(targetYaw - player.getYRot())),
                Math.abs(targetPitch - player.getXRot())) < REACHED_EPSILON;
        if (reached && task == Task.RESET) {
            stopRotation();
        }
        return reached;
    }
}
