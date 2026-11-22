package tech.lightclient.modules.impl.combat.aura.rotations.ft360;

import java.security.SecureRandom;
import net.minecraft.util.math.MathHelper;

/**
 * Continuous FunTime hand motor. A target may refresh at 20 Hz, but velocity,
 * acceleration and the drawn response laws survive that boundary.
 */
final class FuntimeContinuousMotor {
    private static final float FRAME_TICKS = 1.0f / 12.0f;

    private final SecureRandom random;
    private final DrawnResponse.Drifting yawLaw;
    private final DrawnResponse.Drifting pitchLaw;
    private final HumanSubmovementGate submovementGate;
    private final Axis yaw = new Axis();
    private final Axis pitch = new Axis();
    private long logicTick;
    private float yawLimit = 42.0f;
    private float pitchLimit = 8.0f;
    private float lastTravel;
    private boolean urgent;

    FuntimeContinuousMotor() {
        this(new SecureRandom());
    }

    FuntimeContinuousMotor(SecureRandom random) {
        this.random = random;
        this.yawLaw = new DrawnResponse.Drifting(random, 260.0);
        this.pitchLaw = new DrawnResponse.Drifting(random, 310.0);
        this.submovementGate = new HumanSubmovementGate(random.nextLong());
        this.drawHand();
    }

    void plan(float yawLimit, float pitchLimit, boolean urgent) {
        ++this.logicTick;
        this.yawLaw.advance(1L, this.logicTick);
        this.pitchLaw.advance(1L, this.logicTick);
        this.yawLimit += (Math.max(1.0f, yawLimit) - this.yawLimit)
                * this.range(0.075f, 0.16f);
        this.pitchLimit += (Math.max(0.45f, pitchLimit) - this.pitchLimit)
                * this.range(0.065f, 0.14f);
        this.urgent = urgent;
        this.yaw.drift(this.random);
        this.pitch.drift(this.random);
    }

    Rotation step(Rotation current, Rotation target, float mouseQuantum) {
        float yawError = MathHelper.wrapDegrees(
                target.getYaw() - current.getYaw());
        float pitchError = target.getPitch() - current.getPitch();
        float requestedYaw = this.yaw.advance(
                yawError, this.yawLimit, this.yawLaw, FRAME_TICKS);
        float requestedPitch = this.pitch.advance(
                pitchError, this.pitchLimit, this.pitchLaw, FRAME_TICKS);

        float error = (float)Math.hypot(yawError, pitchError);
        boolean allowArrest = !this.urgent
                && error > Math.max(0.75f, mouseQuantum * 3.0f)
                && error < 38.0f;
        float release = this.submovementGate.advance(
                this.lastTravel, mouseQuantum,
                FRAME_TICKS / 20.0f, allowArrest);
        float yawStep = requestedYaw * release;
        float pitchStep = requestedPitch * release;
        this.lastTravel = (float)Math.hypot(yawStep, pitchStep);
        return new Rotation(
                current.getYaw() + yawStep,
                MathHelper.clamp(current.getPitch() + pitchStep,
                        -90.0f, 90.0f));
    }

    void onAttack() {
        // A click does not force a new stroke. Occasionally it shifts how the
        // same hand responds, crossfaded by DrawnResponse rather than swapped.
        if (this.random.nextFloat() < 0.24f) {
            this.yawLaw.rewrite(this.logicTick);
        }
        if (this.random.nextFloat() < 0.18f) {
            this.pitchLaw.rewrite(this.logicTick);
        }
    }

    void reset() {
        this.yaw.reset();
        this.pitch.reset();
        this.submovementGate.reset();
        this.lastTravel = 0.0f;
        this.urgent = false;
        this.drawHand();
        this.yawLaw.reseed(this.logicTick);
        this.pitchLaw.reseed(this.logicTick);
    }

    float yawVelocity() {
        return this.yaw.velocity;
    }

    float pitchVelocity() {
        return this.pitch.velocity;
    }

    private void drawHand() {
        this.yaw.configure(
                this.range(9.0f, 17.5f),
                this.range(46.0f, 78.0f),
                this.range(24.0f, 46.0f),
                this.range(0.62f, 0.90f));
        this.pitch.configure(
                this.range(5.0f, 10.5f),
                this.range(24.0f, 46.0f),
                this.range(15.0f, 30.0f),
                this.range(0.55f, 0.84f));
    }

    private float range(float low, float high) {
        float unit = (this.random.nextFloat() + this.random.nextFloat()
                + this.random.nextFloat()) / 3.0f;
        return low + (high - low) * unit;
    }

    private static final class Axis {
        private float response;
        private float accelerationLimit;
        private float jerkLimit;
        private float damping;
        private float responseTarget;
        private float accelerationTarget;
        private float jerkTarget;
        private float dampingTarget;
        private float responseHome;
        private float accelerationHome;
        private float jerkHome;
        private float dampingHome;
        private float velocity;
        private float acceleration;
        private int previousErrorSign;

        void configure(float response, float acceleration,
                       float jerk, float damping) {
            this.response = this.responseTarget = response;
            this.accelerationLimit = this.accelerationTarget = acceleration;
            this.jerkLimit = this.jerkTarget = jerk;
            this.damping = this.dampingTarget = damping;
            this.responseHome = response;
            this.accelerationHome = acceleration;
            this.jerkHome = jerk;
            this.dampingHome = damping;
            this.velocity = 0.0f;
            this.acceleration = 0.0f;
            this.previousErrorSign = 0;
        }

        void drift(SecureRandom random) {
            if (random.nextFloat() < 0.055f) {
                // Draw around the hand that was selected for this engagement.
                // Multiplying the previous target would perform an unbounded
                // random walk and eventually create one extreme, obvious
                // stroke after a long otherwise-clean fight.
                this.responseTarget = this.responseHome
                        * range(random, 0.84f, 1.18f);
                this.accelerationTarget = this.accelerationHome
                        * range(random, 0.80f, 1.22f);
                this.jerkTarget = this.jerkHome
                        * range(random, 0.78f, 1.25f);
                this.dampingTarget = MathHelper.clamp(
                        this.dampingHome * range(random, 0.91f, 1.09f),
                        0.46f, 0.94f);
            }
            this.response += (this.responseTarget - this.response) * 0.035f;
            this.accelerationLimit += (this.accelerationTarget
                    - this.accelerationLimit) * 0.028f;
            this.jerkLimit += (this.jerkTarget - this.jerkLimit) * 0.024f;
            this.damping += (this.dampingTarget - this.damping) * 0.032f;
        }

        float advance(float error, float limit,
                      DrawnResponse.Drifting law, float dt) {
            int errorSign = error > 0.0f ? 1 : (error < 0.0f ? -1 : 0);
            if (this.previousErrorSign != 0 && errorSign != 0
                    && errorSign != this.previousErrorSign) {
                this.velocity *= 0.18f + 0.24f * this.damping;
                this.acceleration *= 0.12f + 0.20f * this.damping;
            }
            if (errorSign != 0) {
                this.previousErrorSign = errorSign;
            }

            float knee = Math.max(2.0f, limit / Math.max(2.0f, this.response));
            float desiredVelocity = limit * law.apply(error / knee);
            float desiredAcceleration = MathHelper.clamp(
                    (desiredVelocity - this.velocity) * this.response,
                    -this.accelerationLimit, this.accelerationLimit);
            float jerk = Math.max(1.0f, this.jerkLimit) * dt;
            this.acceleration += MathHelper.clamp(
                    desiredAcceleration - this.acceleration, -jerk, jerk);
            this.velocity += this.acceleration * dt;
            this.velocity = MathHelper.clamp(
                    this.velocity, -Math.abs(limit), Math.abs(limit));

            float near = MathHelper.clamp(Math.abs(error) / 1.4f, 0.0f, 1.0f);
            this.velocity *= MathHelper.lerp(
                    near, 0.72f + this.damping * 0.18f, 1.0f);
            float step = this.velocity * dt;
            float allowedOvershoot = 0.08f
                    + 0.16f * Math.min(1.0f, Math.abs(this.velocity) / 18.0f);
            if (Math.abs(step) > Math.abs(error) + allowedOvershoot) {
                step = Math.copySign(
                        Math.abs(error) + allowedOvershoot, step);
            }
            return step;
        }

        void reset() {
            this.velocity = 0.0f;
            this.acceleration = 0.0f;
            this.previousErrorSign = 0;
        }

        private static float range(SecureRandom random, float low, float high) {
            return low + random.nextFloat() * (high - low);
        }
    }
}
