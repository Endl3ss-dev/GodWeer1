package swerve.utils.rotation;

import net.minecraft.client.Minecraft;

/**
 * Квантование поворота под шаг реальной чувствительности мыши (GCD).
 *
 * <p>Порт из Private/src/java/org/privates/client/utils/rotation/SensUtil.java
 * (он же дублировался в utils/other/GCDUtils.java). Формула ванильная и от версии
 * игры не зависит; в 1.21.4 чувствительность читается из {@code Options}.
 *
 * <p>Без этого шага ротация получается «слишком ровной» и выдаёт автокликер:
 * живой игрок всегда двигает мышь кратно одному и тому же углу.
 */
public final class GcdUtil {

    private GcdUtil() {
    }

    public static float getGCD() {
        double sensitivity = Minecraft.getInstance().options.sensitivity().get();
        return (float) (Math.pow(sensitivity * 0.6D + 0.2D, 3.0D) * 8.0D);
    }

    public static float getGCDValue() {
        return (float) (getGCD() * 0.15D);
    }

    public static float getDeltaMouse(float delta) {
        return Math.round(delta / getGCDValue());
    }

    public static float getSensitivity(float delta) {
        return getDeltaMouse(delta) * getGCDValue();
    }
}
