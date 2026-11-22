package tech.lightclient.modules.impl.combat.aura.rotations.ft360;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.client.utils.player.rotation.SensUtility;

/** LunoTOP FunTime strategy (VVnuUunUv) with an LSTM-shaped multi-axis gesture. */
public final class LunoTOPPacketRotation {
    private static final float RETURN_DONE_YAW = 0.32f;
    private static final float RETURN_DONE_PITCH = 0.24f;
    /** Клиентный мотор работает на 240 Гц; скорости донора — градусы/тик 20 Гц. */
    private static final float FRAME_STEP_SCALE = 20.0f / 240.0f;
    /** Один шаг мотора 240 Гц в наносекундах. */
    private static final long FRAME_INTERVAL_NS = 4_166_666L;
    /** Максимальное число догоняющих шагов за один вызов. */
    private static final int MAX_CATCH_UP_FRAMES = 8;
    private final FtStLstmController lstm = new FtStLstmController();
    private final LstmPersona lstmPersona = LstmPersona.draw();
    private final FuntimeContinuousMotor motionMotor =
            new FuntimeContinuousMotor();
    private final FuntimeOscillatorBank oscillators =
            new FuntimeOscillatorBank();
    /**
     * Скрытый (серверный) угол. В этом клиенте пакетный угол живёт в
     * RotationTask, а визуальная камера остаётся под мышью игрока, поэтому
     * мотор двигает собственное состояние, а не {@code player.setYaw}.
     */
    private float hiddenYaw;
    private float hiddenPitch;
    private boolean hiddenReady;
    private float yawStepRemainder;
    private float pitchStepRemainder;
    private long lastFrameNanos;
    private long frameRemainderNanos;
    private float baseYaw;
    private float basePitch;
    private float lastYawNoise;
    private float lastPitchNoise;
    private Vec3d targetPoint;
    private Vec3d eyePoint;
    private Vec3d aimAnchor = Vec3d.ZERO;
    private Vec3d nextAimAnchor = Vec3d.ZERO;
    private int aimAnchorTicks;
    private float correlatedYawNoise;
    private float correlatedPitchNoise;
    private int ticksUntilTwitch = random().nextInt(90, 181);
    private float twitchYaw;
    private float twitchPitch;
    private boolean attackRecent;
    private long attackRecentUntilMillis;
    private int hitCounter;
    private int hitsUntilGesture = drawGestureInterval();
    private boolean gesture;
    private boolean gestureRecovering;
    private boolean gestureTargetReady;
    private int gestureTicksRemaining;
    private int gestureTicksElapsed;
    private int gestureOutsideTicks;
    private float gestureTargetYaw;
    private float gestureTargetPitch;
    private float gestureOffsetYaw;
    private float gestureOffsetPitch;
    private float gestureMargin;
    private float gestureYawVelocity;
    private float gesturePitchVelocity;
    private int lastGestureDirection = -1;
    private int targetId = Integer.MIN_VALUE;
    private boolean returning;
    private long returnStartedMillis;
    private long returnDurationMillis;
    private float returnArcYaw;
    private float returnArcPitch;
    private float returnArcSkew;
    private float anchorX;
    private float anchorY;
    private boolean motionReady;
    private float motionTargetYaw;
    private float motionTargetPitch;

    public void aim(LivingEntity target, boolean attackWindow) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || target == null) return;

        this.returning = false;
        this.ensureHidden(player);
        long now = System.currentTimeMillis();
        this.resetForTarget(target);
        Box box = target.getBoundingBox();
        this.updateAimAnchor();
        FuntimeOscillatorBank.Sample oscillator = this.oscillators.step();
        this.targetPoint = floatingAimPoint(
                box, this.aimAnchor,
                oscillator.orbitX(), oscillator.orbitY(), oscillator.orbitZ());
        this.eyePoint = player.getEyePos();

        Vec3d direction = this.targetPoint.subtract(this.eyePoint).normalize();
        float exactYaw = (float) Math.toDegrees(
                Math.atan2(-direction.x, direction.z));
        float exactPitch = (float) MathHelper.clamp(
                -Math.toDegrees(Math.atan2(direction.y,
                        Math.hypot(direction.x, direction.z))), -90.0, 90.0);
        float yawNoise = oscillator.yawNoise();
        float pitchNoise = oscillator.pitchNoise();
        this.correlatedYawNoise = correlatedNoiseStep(
                this.correlatedYawNoise,
                (float) random().nextGaussian() * 0.055f,
                0.82f, 0.24f);
        this.correlatedPitchNoise = correlatedNoiseStep(
                this.correlatedPitchNoise,
                (float) random().nextGaussian() * 0.035f,
                0.86f, 0.16f);
        if (--this.ticksUntilTwitch <= 0 && !this.gesture) {
            this.twitchYaw = between(0.38f, 0.92f)
                    * (random().nextBoolean() ? 1.0f : -1.0f);
            this.twitchPitch = between(0.12f, 0.42f)
                    * (random().nextBoolean() ? 1.0f : -1.0f);
            this.ticksUntilTwitch = random().nextInt(90, 181);
        } else {
            this.twitchYaw *= 0.46f;
            this.twitchPitch *= 0.42f;
        }
        this.lastYawNoise = yawNoise;
        this.lastPitchNoise = pitchNoise;

        if (this.gesture && !this.gestureTargetReady) {
            this.prepareGestureTarget(
                    player, box, exactYaw, exactPitch);
        }
        if (this.gesture) {
            AngularBounds bounds = angularBounds(
                    box, this.eyePoint, exactYaw, exactPitch);
            // Movement can enlarge the target's angular silhouette after the
            // gesture was dealt. Revalidate the same sector against the live
            // box; the continuous motor absorbs the changed setpoint without
            // emitting a second snap.
            GestureOffset liveOffset = escapeGestureOffset(
                    new GestureOffset(
                            this.gestureOffsetYaw, this.gestureOffsetPitch),
                    bounds, this.lastGestureDirection,
                    this.gestureMargin, 62.0f, 72.0f);
            this.gestureOffsetYaw = liveOffset.yaw();
            this.gestureOffsetPitch = liveOffset.pitch();
            boolean visiblyOutside = gestureOutsideBounds(
                    MathHelper.wrapDegrees(this.hiddenYaw - exactYaw),
                    this.hiddenPitch - exactPitch,
                    bounds, this.gestureMargin * 0.52f);
            this.gestureOutsideTicks = visiblyOutside
                    ? this.gestureOutsideTicks + 1 : 0;
            if ((this.gestureTicksRemaining <= 0
                    && this.gestureOutsideTicks >= 2)
                    || this.gestureTicksElapsed >= 16) {
                this.gesture = false;
                this.gestureRecovering = true;
            } else {
                // Keep the selected offset relative to the live target. A
                // fixed world angle can drift back into a strafing player's
                // hitbox before the gesture has completed.
                this.gestureTargetYaw = exactYaw + this.gestureOffsetYaw;
                this.gestureTargetPitch = MathHelper.clamp(
                        exactPitch + this.gestureOffsetPitch,
                        -84.0f, 84.0f);
                this.baseYaw = this.gestureTargetYaw;
                this.basePitch = this.gestureTargetPitch;
                this.recordGestureAnchor(
                        this.gestureOffsetYaw,
                        this.gestureOffsetPitch,
                        bounds);
                --this.gestureTicksRemaining;
                ++this.gestureTicksElapsed;
            }
        }
        if (!this.gesture) {
            this.recordAnchor(box, this.targetPoint);
            // The point floats continuously inside the body. Keeping the old
            // base angle until an attack window made that point cosmetic and
            // produced a visible 20 Hz correction only near the hit.
            this.baseYaw = exactYaw;
            this.basePitch = exactPitch;
        }
        if (this.gestureRecovering) {
            // Follow the moving target on the way back instead of returning to
            // the stale point from the hit tick.
            this.baseYaw = exactYaw;
            this.basePitch = exactPitch;
            float recoveryYaw = Math.abs(MathHelper.wrapDegrees(
                    exactYaw - this.hiddenYaw));
            float recoveryPitch = Math.abs(exactPitch - this.hiddenPitch);
            if (recoveryYaw <= 0.72f && recoveryPitch <= 0.58f) {
                this.gestureRecovering = false;
                this.resetGestureMotion();
            }
        }
        if (this.attackRecent && now >= this.attackRecentUntilMillis) {
            this.attackRecent = false;
        }

        float gestureNoise = this.gesture ? 0.16f
                : (this.gestureRecovering ? 0.28f : 1.0f);
        if (attackWindow && !this.gesture) {
            gestureNoise *= 0.52f;
        }
        float requestedYaw = this.baseYaw + (yawNoise
                + this.correlatedYawNoise + this.twitchYaw) * gestureNoise;
        float requestedPitch = MathHelper.clamp(
                this.basePitch + (pitchNoise + this.correlatedPitchNoise
                        + this.twitchPitch) * gestureNoise,
                -90.0f, 90.0f);
        requestedYaw = keepOldYawIfEquivalent(
                this.hiddenYaw, requestedYaw, 0.11f);
        requestedPitch = keepOldPitchIfEquivalent(
                this.hiddenPitch, requestedPitch, 0.08f);
        float yawError = MathHelper.wrapDegrees(requestedYaw - this.hiddenYaw);
        float pitchError = requestedPitch - this.hiddenPitch;
        float[] neural = this.lstm.predict(
                yawError / 180.0f, pitchError / 90.0f);
        float donorYawSpeed = between(40.124813f, 55.41284f);
        float donorPitchSpeed = between(4.412848f, 12.412894f);
        double distance = distanceToBox(this.eyePoint, box);
        float distanceScale = distanceSpeedScale(distance);
        double relativeHorizontalSpeed = target.getVelocity()
                .subtract(player.getVelocity()).horizontalLength();
        float movementScale = 1.0f + MathHelper.clamp(
                (float) relativeHorizontalSpeed * 0.24f, 0.0f, 0.18f);
        float fallScale = 1.0f + MathHelper.clamp(
                (float) player.fallDistance * 0.035f, 0.0f, 0.10f);
        float cooldown = player.getAttackCooldownProgress(0.0f);
        float settleScale = MathHelper.lerp(
                smoothstep(MathHelper.clamp(
                        (cooldown - 0.76f) / 0.24f, 0.0f, 1.0f)),
                1.0f, 0.92f);
        float yawSpeed = donorYawSpeed * distanceScale * movementScale
                * fallScale * settleScale
                * angleSpeedScale(yawError, 48.0f);
        float pitchSpeed = donorPitchSpeed * distanceScale
                * (1.0f + (movementScale - 1.0f) * 0.55f)
                * fallScale * settleScale
                * angleSpeedScale(pitchError, 28.0f);
        if (this.gesture || this.gestureRecovering) {
            float trust = MathHelper.clamp(
                    this.lstm.confidence() * this.lstmPersona.aimTrust,
                    0.0f, 0.70f);
            float yawDirectionTrust = neural[0] * yawError >= -0.001f
                    ? 1.0f : 0.08f;
            float pitchDirectionTrust = neural[1] * pitchError >= -0.001f
                    ? 1.0f : 0.08f;
            // The old 0.62 envelope spent almost the whole gesture merely
            // reaching the edge.  By the time it became geometrically
            // outside, recovery started and the excursion was only one
            // packet wide.  Give the outbound stroke enough velocity to show
            // the selected sector, then retain the softer recovery profile.
            float phaseScale = this.gesture ? 0.86f : 0.78f;
            float yawCruise = this.lstmPersona.returnYawCruise * phaseScale;
            float pitchCruise = this.lstmPersona.returnPitchCruise
                    * (this.gesture ? 0.88f : 0.84f);
            float yawBase = Math.min(yawCruise, brakingSpeed(
                    yawError, this.lstmPersona.returnYawBrake * 0.72f));
            float pitchBase = Math.min(pitchCruise, brakingSpeed(
                    pitchError, this.lstmPersona.returnPitchBrake * 0.72f));
            float neuralYawSpeed = MathHelper.clamp(
                    Math.abs(neural[0]) * 180.0f * this.lstmPersona.yawGain,
                    0.35f, yawCruise * 1.08f);
            float neuralPitchSpeed = MathHelper.clamp(
                    Math.abs(neural[1]) * 90.0f * this.lstmPersona.pitchGain,
                    0.24f, pitchCruise * 1.08f);
            float desiredYaw = MathHelper.lerp(
                    trust * yawDirectionTrust, yawBase, neuralYawSpeed);
            float desiredPitch = MathHelper.lerp(
                    trust * pitchDirectionTrust, pitchBase, neuralPitchSpeed);
            this.gestureYawVelocity = approach(
                    this.gestureYawVelocity, desiredYaw,
                    this.lstmPersona.returnYawRise * 0.58f,
                    this.lstmPersona.returnYawFall * 0.72f);
            this.gesturePitchVelocity = approach(
                    this.gesturePitchVelocity, desiredPitch,
                    this.lstmPersona.returnPitchRise * 0.62f,
                    this.lstmPersona.returnPitchFall * 0.76f);
            yawSpeed = Math.min(Math.abs(yawError),
                    Math.max(0.24f, this.gestureYawVelocity));
            pitchSpeed = Math.min(Math.abs(pitchError),
                    Math.max(0.18f, this.gesturePitchVelocity));
        }

        // Packet planning updates only the setpoint and broad rate envelope.
        // Velocity and acceleration live in motionMotor and deliberately cross
        // this 20 Hz boundary, so no packet is the guaranteed endpoint of a
        // freshly restarted easing template.
        this.motionTargetYaw = requestedYaw;
        this.motionTargetPitch = requestedPitch;
        this.motionMotor.plan(
                Math.max(1.0f, yawSpeed),
                Math.max(0.45f, pitchSpeed),
                this.gesture || this.gestureRecovering || attackWindow);
        this.motionReady = true;
    }

    /**
     * Шаги мотора 240 Гц в сторону последнего плана 20 Гц.
     *
     * <p>Вызывается из высокочастотного обработчика Aura. Внутри
     * восстанавливается фиксированный шаг 1/240 с, поэтому реальная частота
     * обработчика на поведение не влияет: лишние вызовы ничего не делают,
     * редкие — догоняют до восьми шагов.
     */
    public void frameStep() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            this.lastFrameNanos = 0L;
            return;
        }
        if (!this.returning && !this.motionReady) {
            // Пока плана нет, часы мотора не идут: иначе первый шаг после
            // простоя выдал бы догоняющий пакет шагов.
            this.lastFrameNanos = 0L;
            this.frameRemainderNanos = 0L;
            return;
        }

        for (int frame = 0, frames = this.pendingFrames(); frame < frames; ++frame) {
            if (this.returning) {
                this.returnStep();
            } else if (this.motionReady) {
                this.motionStep(client);
            } else {
                return;
            }
        }
    }

    /** Один срез мотора в сторону текущей боевой цели. */
    private void motionStep(MinecraftClient client) {
        this.ensureHidden(client.player);
        Rotation current = new Rotation(this.hiddenYaw, this.hiddenPitch);
        float mouseQuantum = HumanSubmovementGate.quantumDegrees(
                (Double) client.options.getMouseSensitivity().getValue());
        Rotation frameTarget = this.motionMotor.step(
                current,
                new Rotation(this.motionTargetYaw, this.motionTargetPitch),
                mouseQuantum);
        this.applyQuantisedStep(frameTarget);
    }

    /**
     * Число шагов 1/240 с, накопившихся с прошлого вызова.
     *
     * <p>Догоняющий бюджет ограничен, чтобы фриз кадра не проигрывался как
     * сотни шагов мотора.
     */
    private int pendingFrames() {
        long now = System.nanoTime();
        long previous = this.lastFrameNanos;
        this.lastFrameNanos = now;
        if (previous == 0L || now < previous) {
            this.frameRemainderNanos = 0L;
            return 1;
        }
        long maxCatchUp = FRAME_INTERVAL_NS * MAX_CATCH_UP_FRAMES;
        long elapsed = Math.min(now - previous, maxCatchUp);
        long accumulated = Math.min(
                this.frameRemainderNanos + elapsed, maxCatchUp);
        int frames = (int) (accumulated / FRAME_INTERVAL_NS);
        this.frameRemainderNanos = accumulated - (long) frames * FRAME_INTERVAL_NS;
        return frames;
    }

    /**
     * Переносит шаг на сетку mouse-count текущей чувствительности, сохраняя
     * дробный остаток между кадрами.
     *
     * <p>Реальная мышь выдаёт целые counts, и подсчётный остаток не исчезает,
     * а накапливается. Округление каждого кадра по отдельности выбрасывает
     * остаток, а на 240 Гц шаг почти всегда меньше одного count — это и есть
     * почти всё движение.
     */
    private void applyQuantisedStep(Rotation frameTarget) {
        float step = SensUtility.getGCDValue();
        float yawRequested = MathHelper.wrapDegrees(
                frameTarget.getYaw() - this.hiddenYaw);
        float pitchRequested = frameTarget.getPitch() - this.hiddenPitch;
        if (!Float.isFinite(step) || step <= 0.0f) {
            this.hiddenYaw += yawRequested;
            this.hiddenPitch = MathHelper.clamp(
                    this.hiddenPitch + pitchRequested, -90.0f, 90.0f);
            return;
        }
        double yawTotal = yawRequested + this.yawStepRemainder;
        double pitchTotal = pitchRequested + this.pitchStepRemainder;
        double yaw = Math.round(yawTotal / step) * step;
        double pitch = Math.round(pitchTotal / step) * step;
        this.yawStepRemainder = (float) (yawTotal - yaw);
        this.pitchStepRemainder = (float) (pitchTotal - pitch);
        this.hiddenYaw = (float) (this.hiddenYaw + yaw);
        this.hiddenPitch = MathHelper.clamp(
                (float) (this.hiddenPitch + pitch), -90.0f, 90.0f);
    }

    /** Текущий скрытый угол, который Aura отдаёт в RotationTask. */
    public Rotation currentRotation() {
        return new Rotation(this.hiddenYaw, this.hiddenPitch);
    }

    private void ensureHidden(ClientPlayerEntity player) {
        if (this.hiddenReady || player == null) return;
        this.hiddenYaw = player.getYaw();
        this.hiddenPitch = player.getPitch();
        this.hiddenReady = true;
    }

    /** Ставит скрытый угол в известную точку (перехват цели, включение). */
    public void seed(float yaw, float pitch) {
        this.hiddenYaw = yaw;
        this.hiddenPitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        this.hiddenReady = true;
        this.yawStepRemainder = 0.0f;
        this.pitchStepRemainder = 0.0f;
    }

    public void onAttack() {
        this.attackRecent = true;
        this.attackRecentUntilMillis = System.currentTimeMillis() + 150L;
        this.motionMotor.onAttack();
        this.oscillators.nudgeAfterAttack();
        float anchorChoice = random().nextFloat();
        if (anchorChoice < 0.18f) {
            this.retargetAimAnchor();
        } else if (anchorChoice < 0.46f) {
            this.nudgeAimAnchor();
        }
        if (++this.hitCounter >= this.hitsUntilGesture) {
            this.hitCounter = 0;
            this.hitsUntilGesture = drawGestureInterval();
            this.gesture = true;
            this.gestureRecovering = false;
            this.gestureTargetReady = false;
            this.resetGestureMotion();
        }
    }

    public void beginReturn() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null
                && (this.hasStrategyState() || this.returning)) {
            this.ensureHidden(client.player);
            if (!this.returning) {
                this.returning = true;
                this.returnStartedMillis = System.currentTimeMillis();
                this.returnDurationMillis = random().nextLong(680L, 921L);
                float yawError = MathHelper.wrapDegrees(
                        client.player.getYaw() - this.hiddenYaw);
                float pitchError = client.player.getPitch() - this.hiddenPitch;
                float verticalShare = Math.abs(pitchError)
                        / Math.max(1.0f, Math.abs(yawError) + Math.abs(pitchError));
                float side = Math.abs(yawError) > 0.35f
                        ? Math.signum(yawError)
                        : (random().nextBoolean() ? 1.0f : -1.0f);
                this.returnArcYaw = side * between(2.2f, 6.8f)
                        * (0.55f + verticalShare * 0.45f)
                        * this.lstmPersona.returnArcGain;
                this.returnArcPitch = between(-1.15f, 1.15f)
                        * (1.0f - verticalShare * 0.45f)
                        * this.lstmPersona.returnArcGain;
                this.returnArcSkew = between(-0.26f, 0.26f);
                // Keep the velocity and acceleration that existed at the end
                // of combat. The return is another sub-movement of the same
                // hand, not a new easing clip beginning at zero velocity.
                this.motionMotor.plan(
                        this.lstmPersona.returnYawCruise,
                        this.lstmPersona.returnPitchCruise, true);
            }
            this.clearStrategyState(false);
            return;
        }
        this.clearStrategyState(true);
    }

    public void returnStep() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!this.returning) return;
        if (client.player == null) {
            this.clearStrategyState(true);
            return;
        }
        this.ensureHidden(client.player);
        long now = System.currentTimeMillis();
        float linearProgress = MathHelper.clamp(
                (now - this.returnStartedMillis)
                        / (float) Math.max(1L, this.returnDurationMillis),
                0.0f, 1.0f);
        float progress = skewedProgress(linearProgress, this.returnArcSkew);
        // Визуальная камера в этом клиенте остаётся под мышью игрока, поэтому
        // точка возврата — собственный угол игрока, а не отдельный FreeLook.
        float cameraYaw = client.player.getYaw();
        float cameraPitch = client.player.getPitch();
        float targetYaw = cameraYaw
                + profiledReturnArc(progress, this.returnArcYaw);
        float targetPitch = MathHelper.clamp(
                cameraPitch + profiledReturnArc(progress, this.returnArcPitch),
                -90.0f, 90.0f);
        Rotation current = new Rotation(this.hiddenYaw, this.hiddenPitch);
        float mouseQuantum = HumanSubmovementGate.quantumDegrees(
                (Double) client.options.getMouseSensitivity().getValue());
        Rotation frameTarget = this.motionMotor.step(
                current, new Rotation(targetYaw, targetPitch), mouseQuantum);
        this.applyQuantisedStep(frameTarget);

        float remainingYaw = Math.abs(MathHelper.wrapDegrees(
                cameraYaw - this.hiddenYaw));
        float remainingPitch = Math.abs(cameraPitch - this.hiddenPitch);
        if (linearProgress >= 0.45f
                && remainingYaw <= RETURN_DONE_YAW
                && remainingPitch <= RETURN_DONE_PITCH) {
            this.clearStrategyState(true);
        }
    }

    /** Ротация снята: состояние сбрасывается, возврат отдаётся RotationTask. */
    public void stop() {
        this.clearStrategyState(true);
    }

    public boolean isActive() {
        return this.hasStrategyState() || this.returning;
    }
    public float getSentYaw() {
        return this.hiddenYaw;
    }
    public float getSentPitch() {
        return this.hiddenPitch;
    }
    public float getAnchorX() { return this.anchorX; }
    public float getAnchorY() { return this.anchorY; }

    private void resetForTarget(LivingEntity target) {
        if (this.targetId == target.getId()) return;
        this.lstm.reset();
        this.resetReturnMotion();
        this.targetId = target.getId();
        this.gesture = false;
        this.gestureRecovering = false;
        this.gestureTargetReady = false;
        this.baseYaw = this.hiddenYaw;
        this.basePitch = this.hiddenPitch;
        this.aimAnchor = drawAimAnchor();
        this.nextAimAnchor = this.aimAnchor;
        this.retargetAimAnchor();
        this.correlatedYawNoise = 0.0f;
        this.correlatedPitchNoise = 0.0f;
        this.twitchYaw = 0.0f;
        this.twitchPitch = 0.0f;
        this.ticksUntilTwitch = random().nextInt(90, 181);
        this.resetGestureMotion();
        this.motionMotor.reset();
        this.oscillators.reset();
    }

    private boolean hasStrategyState() {
        return this.targetId != Integer.MIN_VALUE || this.targetPoint != null
                || this.eyePoint != null
                || this.motionReady
                || this.attackRecent || this.gesture || this.lastYawNoise != 0.0f
                || this.lastPitchNoise != 0.0f;
    }

    private void clearStrategyState(boolean full) {
        this.lastYawNoise = 0.0f;
        this.lastPitchNoise = 0.0f;
        this.targetPoint = null;
        this.eyePoint = null;
        this.motionReady = false;
        this.aimAnchor = Vec3d.ZERO;
        this.nextAimAnchor = Vec3d.ZERO;
        this.aimAnchorTicks = 0;
        this.correlatedYawNoise = 0.0f;
        this.correlatedPitchNoise = 0.0f;
        this.twitchYaw = 0.0f;
        this.twitchPitch = 0.0f;
        this.attackRecent = false;
        this.attackRecentUntilMillis = 0L;
        this.hitCounter = 0;
        this.hitsUntilGesture = drawGestureInterval();
        this.gesture = false;
        this.gestureRecovering = false;
        this.gestureTargetReady = false;
        this.gestureTicksRemaining = 0;
        this.gestureTicksElapsed = 0;
        this.gestureOutsideTicks = 0;
        this.gestureTargetYaw = 0.0f;
        this.gestureTargetPitch = 0.0f;
        this.gestureOffsetYaw = 0.0f;
        this.gestureOffsetPitch = 0.0f;
        this.gestureMargin = 0.0f;
        this.resetGestureMotion();
        this.targetId = Integer.MIN_VALUE;
        if (full) {
            this.lstm.reset();
            this.resetReturnMotion();
            this.motionMotor.reset();
            this.oscillators.reset();
            this.yawStepRemainder = 0.0f;
            this.pitchStepRemainder = 0.0f;
            this.lastFrameNanos = 0L;
            this.frameRemainderNanos = 0L;
            this.hiddenReady = false;
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                this.baseYaw = player.getYaw();
                this.basePitch = player.getPitch();
            }
        }
    }

    private void recordAnchor(Box box, Vec3d point) {
        Vec3d center = box.getCenter();
        this.anchorX = (float) MathHelper.clamp((point.x - center.x)
                / Math.max(1.0e-4, box.getLengthX()) * 2.0, -1.0, 1.0);
        this.anchorY = (float) MathHelper.clamp((point.y - center.y)
                / Math.max(1.0e-4, box.getLengthY()) * 2.0, -1.0, 1.0);
    }

    private void updateAimAnchor() {
        if (this.aimAnchorTicks <= 0
                || this.aimAnchor.squaredDistanceTo(this.nextAimAnchor)
                        <= 0.0016) {
            this.retargetAimAnchor();
        }
        double response = 0.075 + 0.055 * smoothstep(
                (float) Math.min(1.0,
                        this.aimAnchor.distanceTo(this.nextAimAnchor) / 0.55));
        this.aimAnchor = this.aimAnchor.lerp(this.nextAimAnchor, response);
        --this.aimAnchorTicks;
    }

    private void retargetAimAnchor() {
        Vec3d candidate = drawAimAnchor();
        for (int attempt = 0; attempt < 8
                && candidate.squaredDistanceTo(this.aimAnchor) < 0.045;
                ++attempt) {
            candidate = drawAimAnchor();
        }
        this.nextAimAnchor = candidate;
        this.aimAnchorTicks = random().nextInt(10, 29);
    }

    private void nudgeAimAnchor() {
        this.nextAimAnchor = new Vec3d(
                MathHelper.clamp(this.nextAimAnchor.x
                        + triangular(-0.075f, 0.075f), -0.52, 0.52),
                MathHelper.clamp(this.nextAimAnchor.y
                        + triangular(-0.055f, 0.055f), -0.46, 0.52),
                MathHelper.clamp(this.nextAimAnchor.z
                        + triangular(-0.075f, 0.075f), -0.52, 0.52));
        this.aimAnchorTicks = Math.max(
                this.aimAnchorTicks, random().nextInt(6, 17));
    }

    private static Vec3d drawAimAnchor() {
        return new Vec3d(
                triangular(-0.44f, 0.44f),
                triangular(-0.38f, 0.46f),
                triangular(-0.44f, 0.44f));
    }

    static Vec3d floatingAimPoint(Box box,
                                  Vec3d anchor,
                                  double orbitX,
                                  double orbitY,
                                  double orbitZ) {
        Vec3d center = box.getCenter();
        double normalizedX = MathHelper.clamp(anchor.x + orbitX, -0.78, 0.78);
        double normalizedY = MathHelper.clamp(anchor.y + orbitY, -0.78, 0.78);
        double normalizedZ = MathHelper.clamp(anchor.z + orbitZ, -0.78, 0.78);
        return new Vec3d(
                center.x + box.getLengthX() * 0.5 * normalizedX,
                center.y + box.getLengthY() * 0.5 * normalizedY,
                center.z + box.getLengthZ() * 0.5 * normalizedZ);
    }

    private void prepareGestureTarget(ClientPlayerEntity player,
                                      Box box,
                                      float exactYaw,
                                      float exactPitch) {
        GestureOffset fallback = this.nextGestureFallback();
        // Ошибка до визуальной камеры: скрытый угол против угла игрока.
        float cameraYawError = shortestYaw(this.hiddenYaw, player.getYaw());
        float cameraPitchError = player.getPitch() - this.hiddenPitch;
        GestureOffset offset = towardCameraOffset(
                cameraYawError, cameraPitchError,
                fallback.yaw(), fallback.pitch(),
                between(29.0f, 43.0f), between(18.0f, 30.0f));
        AngularBounds bounds = angularBounds(
                box, player.getEyePos(), exactYaw, exactPitch);
        this.gestureMargin = between(9.0f, 14.5f);
        offset = escapeGestureOffset(
                offset, bounds, this.lastGestureDirection,
                this.gestureMargin, 62.0f, 72.0f);
        this.gestureOffsetYaw = offset.yaw();
        this.gestureOffsetPitch = offset.pitch();
        this.gestureTargetYaw = exactYaw + this.gestureOffsetYaw;
        this.gestureTargetPitch = MathHelper.clamp(
                exactPitch + this.gestureOffsetPitch, -84.0f, 84.0f);
        // A millisecond deadline could expire between two delayed 20 Hz
        // combat ticks. Count emitted steps instead so every gesture is
        // actually present in the packet trace.
        this.gestureTicksRemaining = random().nextInt(8, 12);
        this.gestureTicksElapsed = 0;
        this.gestureOutsideTicks = 0;
        this.gestureTargetReady = true;
    }

    private void recordGestureAnchor(float yawOffset,
                                     float pitchOffset,
                                     AngularBounds bounds) {
        float yawEdge = yawOffset < 0.0f
                ? bounds.negativeYaw() : bounds.positiveYaw();
        float pitchEdge = pitchOffset < 0.0f
                ? bounds.negativePitch() : bounds.positivePitch();
        this.anchorX = Math.copySign(
                Math.max(1.20f, Math.abs(yawOffset)
                        / Math.max(0.25f, yawEdge)), yawOffset);
        this.anchorY = Math.copySign(
                Math.max(1.20f, Math.abs(pitchOffset)
                        / Math.max(0.25f, pitchEdge)), pitchOffset);
    }

    private GestureOffset nextGestureFallback() {
        int direction = random().nextInt(8);
        // Avoid an immediate identical replay without creating an eight-item
        // shuffle cycle that becomes obvious when gestures accumulate.
        if (direction == this.lastGestureDirection) {
            direction = Math.floorMod(
                    direction + random().nextInt(1, 8), 8);
        }
        this.lastGestureDirection = direction;
        return gestureFallback(
                direction,
                between(19.0f, 36.0f),
                between(13.0f, 25.0f),
                between(-5.5f, 5.5f),
                between(-4.0f, 4.0f));
    }

    static GestureOffset gestureFallback(int direction,
                                         float yaw,
                                         float pitch,
                                         float minorYaw,
                                         float minorPitch) {
        return switch (Math.floorMod(direction, 8)) {
            case 0 -> new GestureOffset(-yaw, minorPitch);
            case 1 -> new GestureOffset(yaw, minorPitch);
            case 2 -> new GestureOffset(minorYaw, -pitch);
            case 3 -> new GestureOffset(minorYaw, pitch);
            case 4 -> new GestureOffset(-yaw * 0.76f, -pitch * 0.72f);
            case 5 -> new GestureOffset(yaw * 0.76f, -pitch * 0.72f);
            case 6 -> new GestureOffset(-yaw * 0.76f, pitch * 0.72f);
            default -> new GestureOffset(yaw * 0.76f, pitch * 0.72f);
        };
    }

    static GestureOffset towardCameraOffset(float cameraYawError,
                                             float cameraPitchError,
                                             float fallbackYaw,
                                             float fallbackPitch,
                                             float maxYaw,
                                             float maxPitch) {
        float angularDistance = (float) Math.hypot(
                cameraYawError, cameraPitchError * 1.2f);
        if (angularDistance < 4.5f) {
            return new GestureOffset(
                    MathHelper.clamp(fallbackYaw, -maxYaw, maxYaw),
                    MathHelper.clamp(fallbackPitch, -maxPitch, maxPitch));
        }
        float cameraYaw = MathHelper.clamp(
                cameraYawError * 0.55f, -maxYaw, maxYaw);
        float cameraPitch = MathHelper.clamp(
                cameraPitchError * 0.55f, -maxPitch, maxPitch);
        // The shuffled sector owns the gesture. The live camera only bends
        // that sector toward the shorter handoff route; it cannot collapse all
        // eight sectors back into the donor's permanent downward dive.
        float yaw = MathHelper.clamp(
                fallbackYaw * 0.72f + cameraYaw * 0.28f,
                -maxYaw, maxYaw);
        float pitch = MathHelper.clamp(
                fallbackPitch * 0.72f + cameraPitch * 0.28f,
                -maxPitch, maxPitch);
        yaw = preserveDirection(yaw, fallbackYaw, maxYaw);
        pitch = preserveDirection(pitch, fallbackPitch, maxPitch);
        return new GestureOffset(yaw, pitch);
    }

    static GestureOffset escapeGestureOffset(GestureOffset offset,
                                             AngularBounds bounds,
                                             int direction,
                                             float margin,
                                             float maxYaw,
                                             float maxPitch) {
        float safeMargin = Math.max(0.0f, margin);
        float yawNeed = (offset.yaw() < 0.0f
                ? bounds.negativeYaw() : bounds.positiveYaw()) + safeMargin;
        float pitchNeed = (offset.pitch() < 0.0f
                ? bounds.negativePitch() : bounds.positivePitch()) + safeMargin;
        float yaw = offset.yaw();
        float pitch = offset.pitch();
        int sector = Math.floorMod(direction, 8);

        if (sector <= 1) {
            yaw = Math.copySign(Math.max(Math.abs(yaw), yawNeed), yaw);
        } else if (sector <= 3) {
            pitch = Math.copySign(Math.max(Math.abs(pitch), pitchNeed), pitch);
        } else {
            float yawScale = yawNeed / Math.max(0.25f, Math.abs(yaw));
            float pitchScale = pitchNeed / Math.max(0.25f, Math.abs(pitch));
            // A diagonal gesture must clear both projected hitbox edges. The
            // previous min() stopped as soon as either axis escaped, leaving
            // the other axis visually glued to the body.
            float scale = Math.max(1.0f, Math.max(yawScale, pitchScale));
            yaw *= scale;
            pitch *= scale;
        }

        yaw = MathHelper.clamp(yaw, -maxYaw, maxYaw);
        pitch = MathHelper.clamp(pitch, -maxPitch, maxPitch);
        if (!gestureOutsideBounds(yaw, pitch, bounds, safeMargin * 0.75f)) {
            // If the preferred axis is geometrically impossible at very close
            // range, leave through the cheaper of the two remaining edges.
            float yawCost = yawNeed / Math.max(1.0f, maxYaw);
            float pitchCost = pitchNeed / Math.max(1.0f, maxPitch);
            if (yawCost <= pitchCost && yawNeed <= maxYaw) {
                yaw = Math.copySign(yawNeed, yaw == 0.0f ? 1.0f : yaw);
            } else if (pitchNeed <= maxPitch) {
                pitch = Math.copySign(
                        pitchNeed, pitch == 0.0f ? 1.0f : pitch);
            }
        }
        return new GestureOffset(yaw, pitch);
    }

    static boolean gestureOutsideBounds(float yawOffset,
                                        float pitchOffset,
                                        AngularBounds bounds,
                                        float margin) {
        float safeMargin = Math.max(0.0f, margin);
        return yawOffset < -(bounds.negativeYaw() + safeMargin)
                || yawOffset > bounds.positiveYaw() + safeMargin
                || pitchOffset < -(bounds.negativePitch() + safeMargin)
                || pitchOffset > bounds.positivePitch() + safeMargin;
    }

    private static AngularBounds angularBounds(Box box,
                                               Vec3d eye,
                                               float referenceYaw,
                                               float referencePitch) {
        float negativeYaw = 0.0f;
        float positiveYaw = 0.0f;
        float negativePitch = 0.0f;
        float positivePitch = 0.0f;
        double[] xs = {box.minX, box.maxX};
        double[] ys = {box.minY, box.maxY};
        double[] zs = {box.minZ, box.maxZ};
        for (double x : xs) {
            for (double y : ys) {
                for (double z : zs) {
                    Vec3d ray = new Vec3d(x, y, z).subtract(eye);
                    float yaw = (float) Math.toDegrees(
                            Math.atan2(-ray.x, ray.z));
                    float pitch = (float) -Math.toDegrees(Math.atan2(
                            ray.y, Math.hypot(ray.x, ray.z)));
                    float yawOffset = MathHelper.wrapDegrees(
                            yaw - referenceYaw);
                    float pitchOffset = pitch - referencePitch;
                    negativeYaw = Math.max(negativeYaw, -yawOffset);
                    positiveYaw = Math.max(positiveYaw, yawOffset);
                    negativePitch = Math.max(negativePitch, -pitchOffset);
                    positivePitch = Math.max(positivePitch, pitchOffset);
                }
            }
        }
        return new AngularBounds(
                negativeYaw, positiveYaw,
                negativePitch, positivePitch);
    }

    private static float preserveDirection(float blended,
                                           float selected,
                                           float limit) {
        if (Math.abs(selected) < 1.0f || blended * selected > 0.0f) {
            return blended;
        }
        return Math.copySign(
                Math.min(limit, Math.max(0.35f, Math.abs(selected) * 0.22f)),
                selected);
    }

    private static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }
    private static float between(float min, float max) {
        return random().nextFloat(min, max);
    }

    private static float triangular(float low, float high) {
        float unit = (random().nextFloat() + random().nextFloat()) * 0.5f;
        return low + (high - low) * unit;
    }

    static int drawGestureInterval() {
        // A bounded log-normal renewal clock has no narrow 8..17-hit cadence.
        // Most gestures remain uncommon, while occasional longer gaps break
        // click-locked accumulation without removing the gesture itself.
        double sample = Math.exp(Math.log(14.5)
                + random().nextGaussian() * 0.46);
        return MathHelper.clamp((int)Math.round(sample), 6, 38);
    }

    static double distanceToBox(Vec3d point, Box box) {
        double dx = Math.max(Math.max(box.minX - point.x, 0.0), point.x - box.maxX);
        double dy = Math.max(Math.max(box.minY - point.y, 0.0), point.y - box.maxY);
        double dz = Math.max(Math.max(box.minZ - point.z, 0.0), point.z - box.maxZ);
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    static float distanceSpeedScale(double distance) {
        float nearRise = smoothstep(MathHelper.clamp(
                ((float) distance - 1.15f) / 1.55f, 0.0f, 1.0f));
        float farFall = smoothstep(MathHelper.clamp(
                ((float) distance - 3.55f) / 2.85f, 0.0f, 1.0f));
        return (0.70f + 0.42f * nearRise) * (1.0f - 0.34f * farFall);
    }

    static float angleSpeedScale(float angularError, float fullSpeedAngle) {
        float normalized = MathHelper.clamp(
                Math.abs(angularError) / Math.max(1.0f, fullSpeedAngle),
                0.0f, 1.0f);
        return 0.30f + 0.70f * smoothstep(normalized);
    }

    static float keepOldYawIfEquivalent(float oldYaw,
                                        float requestedYaw,
                                        float threshold) {
        return Math.abs(MathHelper.wrapDegrees(requestedYaw - oldYaw))
                <= Math.max(0.0f, threshold) ? oldYaw : requestedYaw;
    }

    static float keepOldPitchIfEquivalent(float oldPitch,
                                          float requestedPitch,
                                          float threshold) {
        return Math.abs(requestedPitch - oldPitch)
                <= Math.max(0.0f, threshold) ? oldPitch : requestedPitch;
    }

    static float correlatedNoiseStep(float previous,
                                     float innovation,
                                     float memory,
                                     float limit) {
        float safePrevious = Float.isFinite(previous) ? previous : 0.0f;
        float safeInnovation = Float.isFinite(innovation) ? innovation : 0.0f;
        return MathHelper.clamp(
                safePrevious * MathHelper.clamp(memory, 0.0f, 0.98f)
                        + safeInnovation,
                -Math.abs(limit), Math.abs(limit));
    }
    static float shortestYaw(float from, float to) {
        return MathHelper.wrapDegrees(to - from);
    }

    static float interpolateYaw(float from, float to, float progress) {
        float t = MathHelper.clamp(progress, 0.0f, 1.0f);
        return from + MathHelper.wrapDegrees(to - from) * t;
    }

    static float frameAllowance(float degreesPerTick) {
        return Math.max(0.0f, degreesPerTick) * FRAME_STEP_SCALE;
    }

    static float returnArc(float progress, float amplitude) {
        float eased = smoothstep(MathHelper.clamp(progress, 0.0f, 1.0f));
        return (float) Math.sin(Math.PI * eased) * amplitude;
    }

    static float profiledReturnArc(float progress, float amplitude) {
        float safeProgress = MathHelper.clamp(progress, 0.0f, 1.0f);
        return (float) Math.sin(Math.PI * safeProgress) * amplitude;
    }

    static float skewedProgress(float progress, float skew) {
        float t = MathHelper.clamp(progress, 0.0f, 1.0f);
        // Endpoint-preserving time warp. The skew is drawn per return, while
        // the stateful motor provides the actual trajectory and continuity.
        return MathHelper.clamp(t + skew * t * (1.0f - t), 0.0f, 1.0f);
    }

    static float approach(float current, float target, float rise, float fall) {
        float safeCurrent = Float.isFinite(current) ? Math.max(0.0f, current) : 0.0f;
        float safeTarget = Float.isFinite(target) ? Math.max(0.0f, target) : 0.0f;
        float limit = safeTarget >= safeCurrent ? Math.max(0.0f, rise) : Math.max(0.0f, fall);
        return safeCurrent + MathHelper.clamp(
                safeTarget - safeCurrent, -limit, limit);
    }

    private static float brakingSpeed(float error, float acceleration) {
        return (float) Math.sqrt(Math.max(
                0.0f, 2.0f * acceleration * Math.abs(error)));
    }

    private static float smoothstep(float value) {
        float t = MathHelper.clamp(value, 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private void resetReturnMotion() {
        this.returning = false;
        this.returnStartedMillis = 0L;
        this.returnDurationMillis = 0L;
        this.returnArcYaw = 0.0f;
        this.returnArcPitch = 0.0f;
        this.returnArcSkew = 0.0f;
    }

    private void resetGestureMotion() {
        this.gestureYawVelocity = 0.0f;
        this.gesturePitchVelocity = 0.0f;
    }

    static record GestureOffset(float yaw, float pitch) { }
    static record AngularBounds(float negativeYaw,
                                float positiveYaw,
                                float negativePitch,
                                float positivePitch) { }

    private record LstmPersona(
            float yawGain,
            float pitchGain,
            float aimTrust,
            float returnTrust,
            float returnArcGain,
            float returnYawStart,
            float returnYawCruise,
            float returnPitchStart,
            float returnPitchCruise,
            float returnYawBrake,
            float returnPitchBrake,
            float returnYawRise,
            float returnYawFall,
            float returnPitchRise,
            float returnPitchFall) {
        private static LstmPersona draw() {
            return new LstmPersona(
                    triangular(0.84f, 1.18f),
                    triangular(0.82f, 1.20f),
                    triangular(0.58f, 0.82f),
                    triangular(0.54f, 0.78f),
                    triangular(0.82f, 1.16f),
                    triangular(2.6f, 4.8f),
                    triangular(17.0f, 25.0f),
                    triangular(1.4f, 2.8f),
                    triangular(8.0f, 13.0f),
                    triangular(18.0f, 30.0f),
                    triangular(12.0f, 21.0f),
                    triangular(3.0f, 5.2f),
                    triangular(4.8f, 8.0f),
                    triangular(1.6f, 3.0f),
                    triangular(2.8f, 5.0f));
        }

        private static float triangular(float low, float high) {
            float unit = (random().nextFloat() + random().nextFloat()) * 0.5f;
            return low + (high - low) * unit;
        }
    }
}
