package tech.lightclient.modules.impl.combat.aura.rotations.ft360;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

/**
 * Reproduces the brief arrests between human aiming sub-movements.
 *
 * <p>A hand does not sweep continuously. Measured over the recorded fight,
 * the camera is completely still on 11.42% of combat frames, and that is not
 * sensor quantisation: 3.17% of frames are still while the hand is demanding
 * more than eight mouse counts, where a quantiser would produce none. Nor is
 * it dropped motion that gets made up later -- the step that ends an arrest
 * is 0.15x a normal step, not a catch-up burst.
 *
 * <p>The velocity profile around an arrest says what it really is, in mouse
 * counts per frame:
 *
 * <pre>
 *   offset  -5     -4     -3     -2     -1      0      1      2      3
 *   speed   13.78  12.64  10.93  9.66   6.74   3.45   0.00   2.15   3.46
 * </pre>
 *
 * The hand decelerates into a stop and accelerates out of it. These are the
 * boundaries between ballistic sub-movements, and they are what makes a real
 * aim look segmented rather than servo-driven.
 *
 * <p>Because arrests begin at 16% of normal speed, reproducing them verbatim
 * costs 1.78% of angular throughput rather than the 11.42% the frame count
 * suggests. That is the whole reason this is safe to run inside a combat
 * rotation: the gate only stops the aim at moments where it had almost
 * stopped anyway.
 *
 * <p>Both hazards were fitted against a causal speed estimate -- what the
 * runtime can actually know -- rather than a centred average that peeks past
 * the arrest it is trying to predict.
 */
public final class HumanSubmovementGate {
    /**
     * Frame length the hazards below were fitted at: the recording's median
     * inter-frame time. Everything is rescaled from here so the arrest rate
     * per second is the same at 30 and at 400 FPS.
     */
    private static final float REFERENCE_FRAME_SECONDS = 0.01201f;

    /** q(v) = SCALE * v^-EXPONENT, v in mouse counts per reference frame. */
    private static final float HAZARD_SCALE = 0.1747f;
    private static final float HAZARD_EXPONENT = 0.6820f;

    /**
     * Ceiling on the entry hazard. The fit runs away below one count per
     * frame, where the observed hazard flattens at 0.145 instead; the aim is
     * also effectively parked there, so an unbounded hazard would lock it.
     */
    private static final float HAZARD_CEILING = 0.16f;

    /** P(still arrested next frame) = 0.6825 -> mean run 3.15 frames. */
    private static final float PERSISTENCE = 0.6825f;

    /** Speed EMA weight, per reference frame. */
    private static final float SPEED_RESPONSE = 0.35f;

    /**
     * Recovery time constant leaving an arrest. The recorded speed climbs
     * 0 -> 6.55 counts over five frames against a 19.65 mean, which is a
     * third of normal after roughly 60 ms.
     */
    private static final float RECOVERY_SECONDS = 0.055f;

    private final Random random;

    private float speedCounts;
    private float release = 1.0f;
    private boolean arrested;

    public HumanSubmovementGate(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Advances the gate and returns the factor to apply to this frame's step.
     *
     * @param lastStepDegrees magnitude of the step actually emitted last
     *                        frame, used as the causal speed estimate
     * @param quantumDegrees  degrees per mouse count at the live sensitivity
     * @param frameSeconds    wall-clock length of this frame
     * @param allowArrest     false while the aim must not be interrupted at
     *                        all, such as a committed strike
     */
    public float advance(
            float lastStepDegrees,
            float quantumDegrees,
            float frameSeconds,
            boolean allowArrest) {
        float dt = MathHelper.clamp(frameSeconds, 0.0005f, 0.25f);
        float frames = dt / REFERENCE_FRAME_SECONDS;

        // Express the recent step as counts per reference frame so the
        // hazard sees the same number regardless of the current frame rate.
        float quantum = Math.max(1.0E-5f, quantumDegrees);
        float sample = Math.abs(lastStepDegrees) / quantum / Math.max(0.05f, frames);
        float speedResponse = 1.0f - (float)Math.pow(1.0f - SPEED_RESPONSE, frames);
        this.speedCounts += (sample - this.speedCounts) * speedResponse;

        if (!allowArrest) {
            this.arrested = false;
        } else if (this.arrested) {
            // Survival over dt, not a per-frame coin flip, or the arrests
            // would last four times longer at 400 FPS than at 100.
            this.arrested = this.random.nextFloat()
                    < (float)Math.pow(PERSISTENCE, frames);
        } else {
            float v = Math.max(0.05f, this.speedCounts);
            float perFrame = Math.min(
                    HAZARD_CEILING,
                    HAZARD_SCALE * (float)Math.pow(v, -HAZARD_EXPONENT));
            float hazard = 1.0f - (float)Math.pow(1.0f - perFrame, frames);
            this.arrested = this.random.nextFloat() < hazard;
        }

        if (this.arrested) {
            // A hard stop is faithful here precisely because entry is
            // conditioned on low speed: the discontinuity it introduces is
            // the 3.45 counts/frame the recording shows, not a full sweep.
            this.release = 0.0f;
        } else {
            this.release += (1.0f - this.release)
                    * (1.0f - (float)Math.exp(-dt / RECOVERY_SECONDS));
        }
        return this.release;
    }

    public boolean arrested() {
        return this.arrested;
    }

    public void reset() {
        this.speedCounts = 0.0f;
        this.release = 1.0f;
        this.arrested = false;
    }

    /**
     * Degrees of yaw per mouse count at the given sensitivity. Matches the
     * quantiser that actually lands the step.
     */
    public static float quantumDegrees(double sensitivity) {
        double f = sensitivity * 0.6000000238418579D + 0.20000000298023224D;
        return (float)(f * f * f * 8.0D * 0.15000000596046448D);
    }
}
