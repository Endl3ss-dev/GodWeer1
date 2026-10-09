package never.lose;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public final class SwerveClient {

    private static final float PI = (float) Math.PI;
    private static final float DROP_CORNER = 12.0f;
    private static final float DROP_SLANT = 12.0f;
    private static final float RIPPLE_STRENGTH_SCALE = 0.625f;
    private static final float SWING_START_THRESHOLD = 0.01f;

    private static boolean swingActive;
    private static boolean alternateSide = true;
    private static int realismSide = -1;
    private static boolean realismReady = true;

    private SwerveClient() {
    }

    public enum SwingStyle {
        SMOOTH("Плавный"),
        WAVE_2("Взмах 2"),
        SHIFT("Сдвиг"),
        BREAK_ARM("Слом"),
        LUNGE("Выпад"),
        SPEAR("Копьё"),
        DOWN("Вниз"),
        JELLY("Желе"),
        SLAP("Шлепок"),
        BONK("Бонк"),
        CIRCLE("Круг"),
        REALISM("Реализм"),
        STATIC("Статик"),
        POKE("Тычок"),
        FEAST("Пир"),
        AKRIEN("Акриен"),
        BLOCK("Блок"),
        TO_BACK("На спину"),
        BREAK("Брейк"),
        DROP_DOWN("Дроп даун"),
        PANDER("Пандер"),
        SLANT("Наклон"),
        CURT("Кёрт"),
        WHIP("Хлыст"),
        TWIST("Твист"),
        RIPPLE("Волна"),
        FROM_ME("От себя");

        public static final SwingStyle[] VALUES = values();

        public final String displayName;

        SwingStyle(String displayName) {
            this.displayName = displayName;
        }

        public static String[] names() {
            String[] names = new String[VALUES.length];
            for (int i = 0; i < VALUES.length; i++) {
                names[i] = VALUES[i].displayName;
            }
            return names;
        }
    }

    /**
     * Единая точка кастомных взмахов руки. Возвращает false, если нужно оставить ванильную анимацию.
     */
    public static boolean applySwing(MatrixStack matrices, Arm arm, SwingStyle style, float swingProgress,
                                     float equipProgress, float strength, boolean alternate) {
        boolean active = swingProgress > SWING_START_THRESHOLD;
        if (active && !swingActive && alternate) {
            alternateSide = !alternateSide;
        }
        swingActive = active;

        if (swingProgress <= 0.0f || swingProgress >= 1.0f) {
            return false;
        }

        int side = alternate ? (alternateSide ? 1 : -1) : (arm == Arm.RIGHT ? 1 : -1);
        float p = swingProgress;
        float s = strength;
        float sinSq = MathHelper.sin(p * p * PI);
        float sinSqrt = MathHelper.sin(MathHelper.sqrt(p) * PI);
        float sinLin = MathHelper.sin(p * PI);

        switch (style) {
            case SMOOTH -> {
                base(matrices, side, equipProgress);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * (45.0f - sinSq * 20.0f * s)));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * sinSqrt * -20.0f * s));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sinSqrt * -80.0f * s));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * -45.0f));
            }
            case WAVE_2 -> {
                base(matrices, side, equipProgress);
                matrices.scale(1.0f, 1.0f, sinLin + 1.0f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f * sinLin * (s * 0.3f)));
            }
            case SHIFT -> {
                base(matrices, side, equipProgress);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * 90.0f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * -60.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - 20.0f * sinLin * s));
            }
            case BREAK_ARM -> {
                base(matrices, side, equipProgress);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * 90.0f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * -30.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - 20.0f * sinLin * s));
            }
            case LUNGE -> {
                base(matrices, side, equipProgress);
                matrices.translate(0.0f, 0.0f, -sinLin * 0.2f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * sinSq * -20.0f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * sinSqrt * -20.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sinSqrt * -MathHelper.clamp(s * 55.0f, 55.0f, 80.0f)));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(10.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(60.0f * side));
            }
            case SPEAR -> {
                base(matrices, side, equipProgress);
                matrices.translate(0.0f, 0.0f, -sinLin * 0.2f);
                matrices.scale(1.0f, 1.0f, sinLin * (s * 0.35f) + 1.0f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
            }
            case DOWN -> {
                base(matrices, side, equipProgress);
                matrices.translate(-sinLin * 0.5f, sinLin * 0.25f, 0.0f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(65.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-110.0f - 80.0f * sinLin));
            }
            case JELLY -> {
                base(matrices, side, equipProgress);
                matrices.translate(0.0f, 0.0f, -sinLin * 0.2f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(35.0f * side));
                matrices.scale(1.0f, 1.0f, sinLin * (p * 0.65f * s * 0.2f) + 1.0f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
            }
            case SLAP -> {
                base(matrices, side, equipProgress);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-sinSqrt * 55.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sinSqrt * sinSq * -120.0f * s));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * sinSqrt * 60.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sinSqrt * 55.0f));
            }
            case BONK -> {
                base(matrices, side, equipProgress);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(15.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-95.0f * easeOutElastic(p * p)));
            }
            case CIRCLE -> {
                base(matrices, side, equipProgress);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(15.0f * side));
                float eased = easeOutBack(MathHelper.lerp(1.0f - p, p, p * 0.5f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-(360.0f * eased + 45.0f)));
            }
            case REALISM -> {
                if (p < 0.12f && realismReady) {
                    realismSide *= -1;
                    realismReady = false;
                } else if (p > 0.78f) {
                    realismReady = true;
                }
                base(matrices, side, equipProgress);
                matrices.scale(1.0f, 1.0f, sinLin + 1.0f);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * realismSide * 22.0f * sinLin * MathHelper.clamp(s, 0.5f, 2.0f)));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f * sinLin * (s * 0.3f)));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-45.0f * easeOutElastic(p * p) * sinLin));
            }
            case STATIC -> {
                matrices.translate(side * 0.56f, -0.42f, -0.72f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sinSqrt * -60.0f * s));
                matrices.translate(0.0f, -0.1f, 0.0f);
            }
            case POKE -> {
                float tilt = s / 3.0f;
                matrices.translate(side * 0.56f, -0.52f, -0.72f);
                matrices.translate(0.0f, 0.0f, tilt * -sinLin);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(75.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((-75.0f * (s / 4.0f) * sinLin - 60.0f) * side));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-75.0f));
            }
            case FEAST -> {
                matrices.translate(side * 0.56f, -0.32f, -0.72f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(30.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(sinSqrt * 75.0f * side * s));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sinSqrt * -65.0f * s));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(30.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(35.0f * side));
            }
            case AKRIEN -> {
                matrices.translate(side * 0.65f, -0.32f, -0.72f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(76.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(sinSqrt * -5.0f * s));
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(sinSqrt * -100.0f * s));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sinSqrt * -155.0f * s));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-100.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(sinSqrt * 25.0f * s));
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(sinSqrt * -25.0f * s));
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(sinSq * 15.0f * s));
                matrices.translate(sinSqrt * 0.18f * s, sinSqrt * 0.59f * s, 0.0f);
            }
            case BLOCK -> {
                matrices.translate(0.56f * side, equipProgress * -0.2f - 0.5f, -0.7f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sinSqrt * -85.0f * s));
                matrices.translate(-0.1f * side, 0.28f, 0.2f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-85.0f));
            }
            case TO_BACK -> {
                matrices.translate(0.65f * side, -0.45f, -0.9f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(50.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((-30.0f * (1.0f - sinSqrt * s) - 30.0f) * side));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(110.0f * side));
            }
            case BREAK -> {
                matrices.translate(0.66f * side, -0.3f, -0.38f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sinSqrt * 10.0f * s));
                matrices.scale(0.5f, 0.5f, 0.5f);
                matrices.translate(-0.1f * side, 0.2f, 0.0f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-10.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-105.0f * side));
            }
            case DROP_DOWN -> {
                base(matrices, side, 0.0f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(80.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(DROP_CORNER));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-DROP_SLANT * sinLin * s));
            }
            case PANDER -> {
                float pander = sinLin;
                float panderRest = 1.0f - equipProgress;
                matrices.translate(0.56f * side, -0.52f, -0.72f);
                matrices.translate((0.3f - pander * 0.15f) * side, 0.2f - panderRest * 0.12f, -0.15f - pander * 0.13f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((76.0f - 10.0f * pander) * side));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((-16.0f - 8.0f * pander) * side));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-83.0f - 26.0f * pander));
            }
            case SLANT -> {
                float anim = sinLin;
                float rotate = 35.0f * s;
                matrices.translate(0.56f * side, -0.52f, -0.72f);
                matrices.translate(0.0f, 0.0f, -0.3f * anim * s);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(anim * -rotate));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(anim * rotate));
            }
            case CURT -> {
                float sinMain = sinSqrt;
                float sinExtra = sinLin;
                matrices.translate(0.56f * side, -0.52f, -0.72f);
                matrices.translate((0.4f - sinMain * 0.2f) * side, -0.2f + sinMain * 0.3f, -0.5f - sinExtra * 0.2f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(91.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((-40.0f + sinMain * -100.0f) * side));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-60.0f));
            }
            case WHIP -> {
                float power = 50.0f * s;
                base(matrices, side, equipProgress);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(60.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * power * sinLin));
            }
            case TWIST -> {
                float power = 50.0f * s;
                base(matrices, side, equipProgress);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-75.0f * side));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-45.0f - power * sinLin));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * power * sinLin * 0.5f));
            }
            case RIPPLE -> {
                float ripple = RIPPLE_STRENGTH_SCALE * s;
                float swing = sinLin * sinLin;
                base(matrices, side, equipProgress);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * (45.0f + sinSq * -20.0f * ripple)));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * swing * -22.0f * ripple));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(swing * -85.0f * ripple));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45.0f * side));
            }
            case FROM_ME -> {
                float t = sinSqrt;
                base(matrices, side, equipProgress);
                matrices.translate(0.0f, 0.0f, -1.1f);
                matrices.translate(0.2f * side, 0.0f, -0.1f - 0.2f * t);
                matrices.multiply(new Quaternionf().rotationXYZ(
                        (float) Math.toRadians(-135.0f - 45.0f * t),
                        (float) Math.toRadians(45.0f * side),
                        (float) Math.toRadians(60.0f * side)));
                matrices.translate(0.0f, 0.0f, 1.1f);
            }
        }
        return true;
    }

    private static void base(MatrixStack matrices, int side, float equipProgress) {
        matrices.translate(side * 0.56f, -0.52f + equipProgress * -0.6f, -0.72f);
    }

    private static float easeOutBack(float x) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float d = x - 1.0f;
        return 1.0f + c3 * d * d * d + c1 * d * d;
    }

    private static float easeOutElastic(float x) {
        if (x <= 0.0f) {
            return 0.0f;
        }
        if (x >= 1.0f) {
            return 1.0f;
        }
        return (float) (Math.pow(2.0, -10.0 * x) * Math.sin((x * 10.0 - 0.75) * (2.0 * Math.PI / 3.0)) + 1.0);
    }
}
