package swerve.utils.render.anim;

/**
 * Затухающая пружина второго порядка для анимаций рук: позиция тянется к цели, импульсы
 * задают скорость. Полунеявный Эйлер с подшагами — устойчив при любом dt кадра.
 */
public final class HandSpring {

    private static final float MAX_STEP = 1.0F / 120.0F;

    public float pos;
    public float vel;

    public void update(float target, float stiffness, float zeta, float dt) {
        if (dt <= 0.0F) {
            return;
        }
        float damping = 2.0F * (float) Math.sqrt(stiffness) * zeta;
        int steps = dt > MAX_STEP ? (int) (dt / MAX_STEP) + 1 : 1;
        float h = dt / steps;
        float dampFactor = 1.0F - damping * h;
        if (dampFactor < 0.0F) {
            dampFactor = 0.0F;
        }
        for (int i = 0; i < steps; i++) {
            vel += (target - pos) * stiffness * h;
            vel *= dampFactor;
            pos += vel * h;
        }
    }

    public void impulse(float velocity) {
        vel += velocity;
    }

    public void reset(float value) {
        pos = value;
        vel = 0.0F;
    }

    public boolean idle() {
        return pos > -1.0E-4F && pos < 1.0E-4F && vel > -1.0E-3F && vel < 1.0E-3F;
    }
}
