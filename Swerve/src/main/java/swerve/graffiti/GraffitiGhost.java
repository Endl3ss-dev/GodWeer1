package swerve.graffiti;

/**
 * [+] «Призрак» граффити — полупрозрачный рисунок на грани под прицелом.
 *
 * <p>Пока игрок водит прицелом по блокам, цель меняется каждый кадр, а поза
 * догоняет её экспоненциальным сглаживанием: {@code t = 1 - e^(-k·dt)}.
 * Такая формула не зависит от FPS — на 60 и на 240 кадрах в секунду призрак
 * перелетает грань за одно и то же время (~150 мс при {@link #FOLLOW_K}).
 *
 * <p>Почему сглаживание считается в рендере, а не в тике: при 20 тиках в секунду
 * движение выглядело бы ступеньками, а кадров обычно в 5–10 раз больше.
 * Шаг кадра берётся из {@code Minecraft.getFrameTimeNs()}.
 *
 * <p>Когда прицел уходит в воздух, призрак плавно гаснет (альфа к нулю), а поза
 * сбрасывается: если снова навести на далёкий блок, квад не полетит через весь мир.
 */
public final class GraffitiGhost {

    /** Скорость наведения: ~95 % пути за 0.12 с. */
    private static final float FOLLOW_K = 25.0F;

    /** Скорость появления и исчезновения. */
    private static final float FADE_IN_K = 18.0F;
    private static final float FADE_OUT_K = 12.0F;

    /** Дальше этого расстояния «перелёт» выглядел бы как телепорт — поза ставится сразу. */
    private static final double SNAP_DISTANCE = 24.0D;

    /** Предел шага кадра: после лага/загрузки не должно быть рывка через полкарты. */
    private static final float MAX_FRAME_SECONDS = 0.1F;

    /** Ниже этой альфы призрак считается погасшим и не рисуется. */
    public static final float MIN_VISIBLE = 0.02F;

    private GraffitiQuad current;
    private GraffitiQuad target;
    private float alpha;
    private long lastFrameNanos;

    /** Наводится на грань: подтягивает позу и проявляет рисунок. */
    public void follow(GraffitiQuad quad, long frameNanos) {
        float dt = deltaSeconds(frameNanos);

        if (this.current == null || this.current.center().distanceTo(quad.center()) > SNAP_DISTANCE) {
            // Первая цель или телепорт: призрак появляется сразу на месте, без перелёта.
            this.current = quad;
            this.alpha = Math.max(this.alpha, 0.25F);
        } else {
            float step = 1.0F - (float) Math.exp(-FOLLOW_K * dt);
            this.current = GraffitiQuad.interpolate(this.current, quad, step);
        }

        this.target = quad;
        this.alpha = approach(this.alpha, 1.0F, FADE_IN_K * dt);
    }

    /** Прицел не на грани: призрак гаснет, поза забывается. */
    public void hide(long frameNanos) {
        float dt = deltaSeconds(frameNanos);
        this.alpha = approach(this.alpha, 0.0F, FADE_OUT_K * dt);
        if (this.alpha <= MIN_VISIBLE) {
            this.alpha = 0.0F;
            this.current = null;
            this.target = null;
        }
    }

    /** Сброс без анимации (выключение модуля, смена мира, открытие меню). */
    public void reset() {
        this.current = null;
        this.target = null;
        this.alpha = 0.0F;
        this.lastFrameNanos = 0L;
    }

    public GraffitiQuad quad() {
        return this.current;
    }

    public GraffitiQuad target() {
        return this.target;
    }

    public float alpha() {
        return this.alpha;
    }

    public boolean visible() {
        return this.current != null && this.alpha > MIN_VISIBLE;
    }

    private float deltaSeconds(long frameNanos) {
        if (this.lastFrameNanos == 0L) {
            this.lastFrameNanos = frameNanos;
            return 1.0F / 60.0F;
        }
        long elapsed = frameNanos - this.lastFrameNanos;
        this.lastFrameNanos = frameNanos;
        if (elapsed <= 0L) {
            return 1.0F / 240.0F;
        }
        float seconds = (float) (elapsed / 1_000_000_000.0D);
        return Math.min(seconds, MAX_FRAME_SECONDS);
    }

    /** Линейный подход к цели с шагом скорости: не даёт «перелёта» через значение. */
    private static float approach(float value, float goal, float step) {
        if (value < goal) {
            return Math.min(goal, value + step);
        }
        return Math.max(goal, value - step);
    }

    /** Расстояние между текущей позой и целью (0 — призрак на месте). */
    public double distanceToTarget() {
        if (this.current == null || this.target == null) {
            return 0.0D;
        }
        return this.current.center().distanceTo(this.target.center());
    }
}
