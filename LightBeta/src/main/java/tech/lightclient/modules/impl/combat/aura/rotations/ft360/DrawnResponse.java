package tech.lightclient.modules.impl.combat.aura.rotations.ft360;

import java.security.SecureRandom;
import net.minecraft.util.math.MathHelper;

/**
 * A response curve that is drawn rather than written.
 *
 * <h2>What this replaces, and why the old thing was the problem</h2>
 *
 * <p>Every shaping law in this client used to be a fixed expression with
 * drawn coefficients: {@code 0.68 * tanh(0.82 * lead + ...)} for the lateral
 * aim, {@code 0.56 + 0.30 * tanh(field)} for the height, {@code (2ad)^p} for
 * the camera return, {@code sqrt(2gd)} for the motor braking. Drawing the
 * coefficients moves a point along a curve. It does not change the curve.
 *
 * <p>Which means the curve was a constant of the binary. Every install, every
 * account, every session produced offsets lying on the same small family, and
 * anything that collects the map from what the opponent did to where the aim
 * went, over enough samples, recovers that family. It does not need a repeated
 * window; it needs a shape, and the shape was the same for everybody. That is
 * a far better handle than any short-window repeat, and it survives restarts,
 * which is exactly the wrong property for it to have.
 *
 * <p>So the curve itself is drawn here: how many terms, which terms, at what
 * gains, through what input warp. The result is a genuinely different
 * nonlinearity per draw -- steep then flat, gentle then hard, near linear,
 * softly dead-zoned -- and there is no expression to recover because no two
 * clients are evaluating the same one.
 *
 * <h2>Random shape, guaranteed behaviour</h2>
 *
 * <p>A drawn law is worthless if it can also be a broken law. Randomising the
 * shape must not randomise whether the aim tracks, so both properties the
 * consumers depend on are structural rather than checked:
 *
 * <ul>
 *   <li><b>Bounded.</b> Every primitive is bounded by one in magnitude and the
 *       weights are positive and sum to one, so the output is bounded by one.
 *       The aim point cannot leave the safe interior because the law drew
 *       badly.</li>
 *   <li><b>Strictly increasing.</b> Every primitive is odd and strictly
 *       increasing, positive weights preserve that, and the input warp is
 *       constrained to a positive derivative. So a faster strafe always moves
 *       the lead further the same way, and the return envelope always asks for
 *       less speed as it closes. Neither can invert on an unlucky draw.</li>
 * </ul>
 *
 * <p>There is no rejection sampling and no validation pass, because there is
 * nothing to reject: the constraints are on the generator, not on its output.
 *
 * <h2>Odd on purpose</h2>
 *
 * <p>{@code f(-u) == -f(u)}, so a strafe left and a strafe right of the same
 * speed are answered with the same magnitude. The asymmetry a hand has belongs
 * to the terms feeding this -- the height bias, the drifting latent state, the
 * crossfeed between axes -- and those are drawn separately. Putting it here as
 * well would double it, and an aim that leans one way twice as hard as the
 * other is a signature of its own.
 */
public final class DrawnResponse {
    /** How many saturating terms a draw may mix. */
    private static final int MIN_TERMS = 2;
    private static final int MAX_TERMS = 4;

    /** How many distinct saturating shapes a draw can pick from. */
    private static final int PRIMITIVES = 5;

    /**
     * Input gain bounds.
     *
     * <p>The floor is what keeps a draw from being flat: at the low end the
     * softest primitive still answers a unit input with about a third of its
     * range, so no draw can collapse into a law that barely moves. The ceiling
     * keeps the knee inside the working domain rather than turning the law into
     * a step, which would read as a snap.
     */
    private static final float GAIN_LOW = 0.45f;
    private static final float GAIN_HIGH = 3.2f;

    /** Smallest share of the mixture any single term may hold. */
    private static final float WEIGHT_FLOOR = 0.12f;

    /**
     * Warp strength, as the product of the two numbers the warp is built from.
     *
     * <p>The warp is {@code u + m * tanh(n * u)}, whose derivative is
     * {@code 1 + m * n * sech^2(n * u)} and therefore bottoms out at
     * {@code 1 + m * n} at the origin. Holding that product above -0.80 leaves
     * at least a fifth of the slope everywhere, so a flattening warp can make
     * the law lazy near zero -- which is a real thing hands do -- without ever
     * making it stop or turn around.
     */
    private static final float WARP_LOW = -0.80f;
    private static final float WARP_HIGH = 1.60f;
    private static final float WARP_KNEE_LOW = 0.5f;
    private static final float WARP_KNEE_HIGH = 2.5f;

    private final int[] kinds;
    private final float[] gains;
    private final float[] weights;
    private final float warpAmount;
    private final float warpKnee;
    private final float originScale;

    private DrawnResponse(
            int[] kinds, float[] gains, float[] weights,
            float warpAmount, float warpKnee) {
        this.kinds = kinds;
        this.gains = gains;
        this.weights = weights;
        this.warpAmount = warpAmount;
        this.warpKnee = warpKnee;
        float probe = 1.0e-3f;
        float slope = this.apply(probe) / probe;
        this.originScale = 1.0f / Math.max(slope, 1.0e-3f);
    }

    /**
     * Draws one curve.
     *
     * @return an odd, strictly increasing map from the reals into (-1, 1)
     */
    public static DrawnResponse draw(SecureRandom random) {
        int terms = MIN_TERMS + random.nextInt(MAX_TERMS - MIN_TERMS + 1);
        int[] kinds = new int[terms];
        float[] gains = new float[terms];
        float[] weights = new float[terms];

        // Distinct primitives where possible: two draws of tanh at different
        // gains is still a sum of tanhs, and mixing knees is the whole point.
        int offset = random.nextInt(PRIMITIVES);
        float total = 0.0f;
        for (int term = 0; term < terms; term++) {
            kinds[term] = (offset + term) % PRIMITIVES;
            gains[term] = GAIN_LOW
                    + random.nextFloat() * (GAIN_HIGH - GAIN_LOW);
            weights[term] = WEIGHT_FLOOR + random.nextFloat();
            total += weights[term];
        }
        for (int term = 0; term < terms; term++) {
            weights[term] /= total;
        }

        float knee = WARP_KNEE_LOW
                + random.nextFloat() * (WARP_KNEE_HIGH - WARP_KNEE_LOW);
        float product = WARP_LOW + random.nextFloat() * (WARP_HIGH - WARP_LOW);
        return new DrawnResponse(kinds, gains, weights, product / knee, knee);
    }

    /**
     * How far out the input is taken literally.
     *
     * <p>Every draw is saturated to within float precision long before this, so
     * folding the domain here costs nothing that can be observed and removes
     * the one arithmetic hazard in the class: a gain of up to 3.2 applied to an
     * input near {@link Float#MAX_VALUE} overflows to infinity, and the
     * algebraic primitives then evaluate infinity over infinity and hand the
     * consumer a NaN. No aim field will ever pass 1e38 -- but the aim field is
     * not the only caller, the motor divides by a gravity it clamps at 1e-4,
     * and a quotient like that is exactly where a number that large comes
     * from.
     */
    private static final float DOMAIN = 1.0e3f;

    /** The curve at {@code u}, in (-1, 1). */
    public float apply(float u) {
        if (Float.isNaN(u)) {
            return 0.0f;
        }
        float folded = MathHelper.clamp(u, -DOMAIN, DOMAIN);
        float warped = folded
                + this.warpAmount * (float)Math.tanh(this.warpKnee * folded);
        float sum = 0.0f;
        for (int term = 0; term < this.kinds.length; term++) {
            sum += this.weights[term]
                    * primitive(this.kinds[term], this.gains[term] * warped);
        }
        // The bound is exact in the construction -- positive weights summing
        // to one, primitives that never leave the unit interval -- and it is
        // one ulp short of exact in float. The weights are normalised by
        // dividing through their own sum, which lands on 1 plus or minus an
        // ulp, so far enough out that every primitive has saturated to
        // exactly one the mixture reads 1.0000001. That is nothing as an aim
        // offset and everything as a promise: the consumers cannot check
        // which curve they got, and they multiply this by a span and call the
        // result a share of the interior. So the guarantee is made exact here
        // rather than left true only in real arithmetic.
        return MathHelper.clamp(sum, -1.0f, 1.0f);
    }

    /**
     * The curve at {@code u}, rescaled so its slope at the origin is one.
     *
     * <h2>What must not be drawn</h2>
     *
     * <p>This is the form to use wherever the old code said {@code tanh}, and
     * the reason is that {@code tanh} was doing two jobs. One was shape, which
     * should be drawn. The other was a promise: unit slope at the origin, so a
     * small error produces a proportional response and the thing converges. A
     * raw draw here can have an origin slope anywhere from about 0.06 to 8, and
     * at the low end that is an aim which barely answers a slow strafe and a
     * motor which crawls the last degree. That is not a different style of
     * hand, that is a broken one, and the owner has already reported what it
     * feels like from inside the game.
     *
     * <p>So the input is scaled by the reciprocal of the measured origin slope.
     * Boundedness survives untouched -- scaling the input of a bounded function
     * cannot unbound it -- and so does monotonicity, since the scale is
     * positive. What is left drawn is everything past the origin: where the
     * knee sits, how hard it turns, how the tail approaches the limit. Which is
     * the part that carries a signature. The part that carries the guarantee is
     * pinned.
     */
    public float applyNormalised(float u) {
        if (Float.isNaN(u)) {
            return 0.0f;
        }
        // Folded before the scale rather than after it: the scale can reach
        // about seventeen, so scaling first is what overflows, and letting
        // apply() catch the infinity afterwards would answer a very large
        // input with zero -- which is bounded, and finite, and the one thing
        // every consumer here is entitled to assume cannot happen.
        return this.apply(
                MathHelper.clamp(u, -DOMAIN, DOMAIN) * this.originScale);
    }

    /**
     * One saturating shape.
     *
     * <p>All five are odd, strictly increasing and bounded by one, and they
     * differ in where the knee sits and how the tail approaches the limit: the
     * algebraic ones reach it slowly and the hyperbolic ones quickly, so a
     * mixture of them is not itself any one of them. Two are deliberately
     * cheap, because this runs per frame on several laws at once.
     */
    private static float primitive(int kind, float x) {
        switch (kind) {
            case 0:
                return (float)Math.tanh(x);
            case 1:
                return x / (1.0f + Math.abs(x));
            case 2:
                return (float)(Math.atan(x) * (2.0 / Math.PI));
            case 3:
                return (float)(x / Math.sqrt(1.0 + (double)x * x));
            default: {
                double a = Math.abs((double)x);
                return (float)(x / Math.cbrt(1.0 + a * a * a));
            }
        }
    }

    /**
     * A curve that is quietly replaced by another one while it is in use.
     *
     * <h2>Why a crossfade and not a swap</h2>
     *
     * <p>Drawing a new law mid-fight is the point -- a law held for a whole
     * session is a per-session constant, which is a weaker version of the
     * per-binary constant this class exists to remove. But swapping it outright
     * moves the aim point in one frame by however far the two laws disagree,
     * and a discontinuity locked to nothing in the world is worse than the
     * fingerprint it was trying to erase.
     *
     * <p>So the new law is faded in over a drawn stretch of ticks. The fade is
     * a convex combination of two curves that are each bounded and strictly
     * increasing, so every intermediate law is bounded and strictly increasing
     * too, and the consumers keep the guarantees they were written against all
     * the way through.
     *
     * <p>The redraw clock is lognormal and per instance, so the laws driving
     * lateral, height and depth do not turn over together and there is no
     * moment where everything about the aim changes at once.
     */
    public static final class Drifting {
        private final SecureRandom random;
        private final double logMean;
        private final double logSigma = 0.55;
        private final long minInterval;
        private final long maxInterval;

        private DrawnResponse from;
        private DrawnResponse to;
        private float mix = 1.0f;
        private float fade;
        private long nextDraw = Long.MIN_VALUE;

        public Drifting(SecureRandom random, double meanTicks) {
            this.random = random;
            this.logMean = Math.log(meanTicks);
            this.minInterval = Math.max(12L, Math.round(meanTicks * 0.32));
            this.maxInterval = Math.round(meanTicks * 3.1);
            this.from = DrawnResponse.draw(random);
            this.to = this.from;
            this.fade = this.drawFade();
        }

        /**
         * The current law at {@code u}, at unit slope through the origin.
         *
         * <p>Both endpoints of the crossfade are normalised, and a convex
         * combination of two curves that each pass through the origin at slope
         * one does too. So the response to a small error is the same before a
         * redraw, during it, and after it: the shape changes underneath and the
         * responsiveness does not move at all.
         */
        public float apply(float u) {
            if (this.mix >= 1.0f) {
                return this.to.applyNormalised(u);
            }
            return MathHelper.lerp(this.mix,
                    this.from.applyNormalised(u), this.to.applyNormalised(u));
        }

        /**
         * Moves the crossfade on, and starts a new one when the clock says so.
         *
         * @param ticks how many logic ticks have passed
         * @param now   the current logic tick
         */
        public void advance(long ticks, long now) {
            if (this.nextDraw == Long.MIN_VALUE) {
                this.schedule(now);
            }
            if (this.mix < 1.0f) {
                this.mix = Math.min(1.0f, this.mix + this.fade * ticks);
                if (this.mix >= 1.0f) {
                    this.from = this.to;
                }
            } else if (now >= this.nextDraw) {
                this.from = this.to;
                this.to = DrawnResponse.draw(this.random);
                this.mix = 0.0f;
                this.fade = this.drawFade();
                this.schedule(now);
            }
        }

        /**
         * Draws a new law now, because a few more swings have gone by.
         *
         * <p>The wall clock this class was built with turns a law over on a
         * mean of a few hundred ticks, which is fifteen to twenty-five swings
         * of the same aiming law. {@link SwingRewrite} calls this to pull the
         * redraw onto the rhythm the opponent is actually measured against,
         * and the fade is drawn short to match -- a fade of forty to a hundred
         * and twenty ticks would still be running when the next rewrite
         * arrived, and the law would live permanently half way between two
         * draws instead of ever being either of them.
         *
         * <p>A rewrite that lands while a fade is still in flight does not
         * stack a second one on top of it. There is no representation here for
         * the blend of two mixtures, so the honest options are to step the law
         * or to hurry the fade already running, and stepping the law is out.
         * The fade is therefore accelerated instead, which is exact, and costs
         * nothing real: the law arriving is itself only a few ticks old, so
         * finishing it early and drawing again next time is the same hand
         * either way.
         */
        public void rewrite(long now) {
            float quick = 1.0f / (10.0f + this.random.nextFloat() * 22.0f);
            if (this.mix < 1.0f) {
                this.fade = Math.max(this.fade, quick);
                return;
            }
            this.from = this.to;
            this.to = DrawnResponse.draw(this.random);
            this.mix = 0.0f;
            this.fade = quick;
            this.schedule(now);
        }

        /** Throws both laws away, for a fresh engagement. */
        public void reseed(long now) {
            this.from = DrawnResponse.draw(this.random);
            this.to = this.from;
            this.mix = 1.0f;
            this.fade = this.drawFade();
            this.schedule(now);
        }

        /** Share of the fade completed per tick: 40 to 120 ticks of it. */
        private float drawFade() {
            return 1.0f / (40.0f + this.random.nextFloat() * 80.0f);
        }

        private void schedule(long now) {
            long interval = Math.round(Math.exp(this.logMean));
            for (int attempt = 0; attempt < 8; attempt++) {
                long sample = Math.round(Math.exp(
                        this.logMean
                                + this.random.nextGaussian() * this.logSigma));
                if (sample >= this.minInterval && sample <= this.maxInterval) {
                    interval = sample;
                    break;
                }
            }
            this.nextDraw = now + interval;
        }

        /** Diagnostics: true while a new law is being faded in. */
        public boolean probeFading() {
            return this.mix < 1.0f;
        }
    }
}
