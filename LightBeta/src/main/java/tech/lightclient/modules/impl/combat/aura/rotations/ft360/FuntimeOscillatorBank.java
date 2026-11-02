package tech.lightclient.modules.impl.combat.aura.rotations.ft360;

import java.security.SecureRandom;
import net.minecraft.util.math.MathHelper;

/** Stateful, slowly detuned sine/cosine field sampled once per packet tick. */
final class FuntimeOscillatorBank {
    private final SecureRandom random;
    private final Oscillator orbitX;
    private final Oscillator orbitY;
    private final Oscillator orbitZ;
    private final Oscillator yawNoise;
    private final Oscillator yawCosine;
    private final Oscillator pitchNoise;
    private final Oscillator pitchSine;

    FuntimeOscillatorBank() {
        this(new SecureRandom());
    }

    FuntimeOscillatorBank(SecureRandom random) {
        this.random = random;
        this.orbitX = new Oscillator(random, Wave.SIN,
                0.045f, 0.078f, 0.14f, 0.31f);
        this.orbitY = new Oscillator(random, Wave.SIN,
                0.052f, 0.092f, 0.18f, 0.38f);
        this.orbitZ = new Oscillator(random, Wave.COS,
                0.044f, 0.082f, 0.12f, 0.27f);
        // LunoTOP FunTime does not use one tiny generic jitter. Its visible
        // float is a separately random-amplitude sine and cosine field. Keep
        // both quadratures on both axes, but drift their periods instead of
        // baking LunoTOP exact 90/100 ms constants into every client.
        this.yawNoise = new Oscillator(random, Wave.SIN,
                0.34f, 0.72f, 1.8f, 5.8f);
        this.yawCosine = new Oscillator(random, Wave.COS,
                0.19f, 0.57f, 0.8f, 3.2f);
        this.pitchNoise = new Oscillator(random, Wave.COS,
                0.29f, 0.64f, 1.3f, 4.5f);
        this.pitchSine = new Oscillator(random, Wave.SIN,
                0.17f, 0.52f, 0.5f, 2.2f);
    }

    Sample step() {
        return new Sample(
                this.orbitX.step(),
                this.orbitY.step(),
                this.orbitZ.step(),
                this.yawNoise.step() + this.yawCosine.step(),
                this.pitchNoise.step() + this.pitchSine.step());
    }

    void nudgeAfterAttack() {
        // A click perturbs the latent oscillation without resetting its phase.
        // Resetting phase on every hit would create a click-locked signature.
        if (this.random.nextFloat() < 0.31f) this.yawNoise.retarget(true);
        if (this.random.nextFloat() < 0.22f) this.yawCosine.retarget(true);
        if (this.random.nextFloat() < 0.24f) this.pitchNoise.retarget(true);
        if (this.random.nextFloat() < 0.17f) this.pitchSine.retarget(true);
        if (this.random.nextFloat() < 0.18f) this.orbitY.retarget(true);
    }

    void reset() {
        this.orbitX.reset();
        this.orbitY.reset();
        this.orbitZ.reset();
        this.yawNoise.reset();
        this.yawCosine.reset();
        this.pitchNoise.reset();
        this.pitchSine.reset();
    }

    record Sample(double orbitX, double orbitY, double orbitZ,
                  float yawNoise, float pitchNoise) {
    }

    private enum Wave { SIN, COS }

    private static final class Oscillator {
        private final SecureRandom random;
        private final Wave wave;
        private final float rateLow;
        private final float rateHigh;
        private final float amplitudeLow;
        private final float amplitudeHigh;
        private double phase;
        private double harmonicPhase;
        private float rate;
        private float rateTarget;
        private float amplitude;
        private float amplitudeTarget;
        private float harmonicShare;
        private float harmonicShareTarget;
        private float harmonicRatio;
        private float harmonicRatioTarget;
        private float gainNoise;
        private int retargetTicks;

        Oscillator(SecureRandom random, Wave wave,
                   float rateLow, float rateHigh,
                   float amplitudeLow, float amplitudeHigh) {
            this.random = random;
            this.wave = wave;
            this.rateLow = rateLow;
            this.rateHigh = rateHigh;
            this.amplitudeLow = amplitudeLow;
            this.amplitudeHigh = amplitudeHigh;
            this.reset();
        }

        float step() {
            if (--this.retargetTicks <= 0) this.retarget(false);
            this.rate += (this.rateTarget - this.rate)
                    * this.range(0.012f, 0.030f);
            this.amplitude += (this.amplitudeTarget - this.amplitude)
                    * this.range(0.010f, 0.026f);
            this.harmonicShare += (this.harmonicShareTarget
                    - this.harmonicShare) * this.range(0.009f, 0.022f);
            this.harmonicRatio += (this.harmonicRatioTarget
                    - this.harmonicRatio) * this.range(0.008f, 0.019f);
            this.phase += this.rate;
            this.harmonicPhase += this.rate * this.harmonicRatio;
            // LunoTOP redraws the 3..11 gain every call. White amplitude noise is
            // too sharp at 20 Hz, so retain the randomness as a correlated
            // gain: it visibly breathes from tick to tick without phase snaps.
            this.gainNoise = MathHelper.clamp(
                    this.gainNoise * 0.72f
                            + (float)this.random.nextGaussian() * 0.045f,
                    -0.18f, 0.18f);
            double primary = this.wave == Wave.SIN
                    ? Math.sin(this.phase) : Math.cos(this.phase);
            double harmonic = this.wave == Wave.SIN
                    ? Math.sin(this.harmonicPhase) : Math.cos(this.harmonicPhase);
            return (float)(this.amplitude * (1.0f + this.gainNoise)
                    * (primary + this.harmonicShare * harmonic)
                    / (1.0f + this.harmonicShare));
        }

        void retarget(boolean soon) {
            this.rateTarget = this.range(this.rateLow, this.rateHigh);
            this.amplitudeTarget = this.triangular(
                    this.amplitudeLow, this.amplitudeHigh);
            // The secondary wave is part of the signal too. Changing its
            // share or frequency immediately creates the exact rare one-frame
            // discontinuity that a long session can eventually expose.
            this.harmonicShareTarget = this.range(0.08f, 0.24f);
            this.harmonicRatioTarget = this.range(1.61f, 2.47f);
            this.retargetTicks = soon
                    ? this.random.nextInt(14, 39)
                    : this.random.nextInt(45, 151);
        }

        void reset() {
            this.phase = this.range(0.0f, (float)(Math.PI * 2.0));
            this.harmonicPhase = this.range(0.0f, (float)(Math.PI * 2.0));
            this.rate = this.rateTarget = this.range(this.rateLow, this.rateHigh);
            this.amplitude = this.amplitudeTarget = this.triangular(
                    this.amplitudeLow, this.amplitudeHigh);
            this.harmonicShare = this.harmonicShareTarget =
                    this.range(0.08f, 0.24f);
            this.harmonicRatio = this.harmonicRatioTarget =
                    this.range(1.61f, 2.47f);
            this.gainNoise = this.range(-0.06f, 0.06f);
            this.retargetTicks = this.random.nextInt(45, 151);
        }

        private float triangular(float low, float high) {
            float unit = (this.random.nextFloat() + this.random.nextFloat()) * 0.5f;
            return low + (high - low) * unit;
        }

        private float range(float low, float high) {
            return low + this.random.nextFloat() * (high - low);
        }
    }
}
