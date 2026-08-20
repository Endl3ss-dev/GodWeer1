package org.privates.client.managers.module.impl.combat.auraelement.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.other.GCDUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpookyTimeNewRot extends AbstractRotationElement {

    private final NeuralRotationProcessor neuralProcessor = new NeuralRotationProcessor();
    private final MouseSimulator mouseSimulator = new MouseSimulator();
    private final HumanInterpolator humanInterpolator = new HumanInterpolator();

    private double circularJitterPhase = 0;
    private double circularJitterSpeed = 0;
    private double circularJitterRadius = 0;
    private long lastJitterUpdate = 0;
    private double targetJitterSpeed = 0;
    private double targetJitterRadius = 0;

    private float lastPitchChange = 0;
    private long lastPitchMoveTime = 0;
    private float pitchChangeAccumulator = 0;

    private final Random random = new Random();
    private long lastFullRandomization = 0;

    private Vector2f mouseOvershoot = new Vector2f(0, 0);
    private Vector2f mouseDrift = new Vector2f(0, 0);
    private long lastMouseNoiseUpdate = 0;

    private float currentSmoothness = 0.5f;
    private float targetSmoothness = 0.5f;
    private long lastSmoothnessChange = 0;

    private final List<Vector2f> mouseTrail = new ArrayList<>();
    private final int maxTrailLength = 8;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null) {
            return currentLerpRotation;
        }

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastFullRandomization > 8000 + random.nextInt(4000)) {
            fullRandomize();
            lastFullRandomization = currentTime;
        }

        Vector3d playerPos = mc.player.getEyePosition(1.0F);

        double stomachHeight = target.getHeight() * (0.45 + random.nextDouble() * 0.1);
        Vector3d baseTargetPos = target.getPositionVec().add(0, stomachHeight, 0);

        double dx = baseTargetPos.x - playerPos.x;
        double dy = baseTargetPos.y - playerPos.y;
        double dz = baseTargetPos.z - playerPos.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        float baseYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
        float basePitch = (float) (-Math.toDegrees(Math.atan2(dy, dist)));

        if (currentTime - lastJitterUpdate > 40 + random.nextInt(30)) {
            targetJitterSpeed = 0.15 + random.nextDouble() * 0.20;
            targetJitterRadius = 4.5 + random.nextDouble() * 7.5;
            lastJitterUpdate = currentTime;
        }

        double jitterLerp = 0.25 + random.nextDouble() * 0.15;
        circularJitterSpeed = MathHelper.lerp(jitterLerp, circularJitterSpeed, targetJitterSpeed);
        circularJitterRadius = MathHelper.lerp(jitterLerp, circularJitterRadius, targetJitterRadius);

        double stepSize = circularJitterSpeed;
        double randomStep = stepSize * (0.7 + random.nextDouble() * 0.6);
        circularJitterPhase += randomStep;

        if (circularJitterPhase > Math.PI * 2) {
            circularJitterPhase -= Math.PI * 2;
            circularJitterPhase += random.nextDouble() * 0.5 - 0.25;
        }

        double jitterAngleOffset = random.nextDouble() * Math.PI * 2;
        double jitterYaw = Math.cos(circularJitterPhase + jitterAngleOffset) * circularJitterRadius;
        double jitterPitch = Math.sin(circularJitterPhase + jitterAngleOffset * 1.3) * circularJitterRadius * 0.4;

        float targetYaw = baseYaw + (float) jitterYaw;
        float targetPitch = basePitch + (float) jitterPitch;

        mouseSimulator.update(currentTime);
        Vector2f mouseNoise = mouseSimulator.getMouseNoise();

        targetYaw += mouseNoise.x;
        targetPitch += mouseNoise.y;

        float pitchDelta = Math.abs(targetPitch - currentLerpRotation.y);
        pitchChangeAccumulator += pitchDelta;

        boolean shouldMovePitch = pitchChangeAccumulator > (35 + random.nextFloat() * 10);

        if (!shouldMovePitch) {
            targetPitch = currentLerpRotation.y;
        } else {
            pitchChangeAccumulator = 0;
            lastPitchMoveTime = currentTime;
        }

        targetPitch = MathHelper.clamp(targetPitch, -90.0F, 90.0F);

        if (currentTime - lastSmoothnessChange > 200 + random.nextInt(300)) {
            targetSmoothness = 0.65f + random.nextFloat() * 0.3f;
            lastSmoothnessChange = currentTime;
        }

        currentSmoothness = MathHelper.lerp(0.15f, currentSmoothness, targetSmoothness);

        Vector2f interpolated = humanInterpolator.interpolate(
                currentLerpRotation,
                new Vector2f(targetYaw, targetPitch),
                currentSmoothness,
                random
        );

        Vector2f neuralOutput = neuralProcessor.process(
                interpolated,
                currentLerpRotation,
                targetYaw,
                targetPitch,
                (float) dist,
                currentTime
        );

        mouseTrail.add(new Vector2f(neuralOutput.x, neuralOutput.y));
        if (mouseTrail.size() > maxTrailLength) {
            mouseTrail.remove(0);
        }

        Vector2f smoothedByTrail = averageTrail();

        float finalYaw = wrapLerp(0.55f + random.nextFloat() * 0.25f, currentLerpRotation.x, smoothedByTrail.x);
        float finalPitch = wrapLerp(0.50f + random.nextFloat() * 0.25f, currentLerpRotation.y, smoothedByTrail.y);

        finalYaw = GCDUtils.getSensitivity(finalYaw);
        finalPitch = GCDUtils.getSensitivity(finalPitch);

        Vector2f newLerpRotation = new Vector2f(finalYaw, finalPitch);

        applyRotation(new Rotation(newLerpRotation.x, newLerpRotation.y), 145, 45, 0, 4);
        parentModule.setLerpRotation(newLerpRotation);

        return newLerpRotation;
    }

    private Vector2f averageTrail() {
        if (mouseTrail.isEmpty()) {
            return new Vector2f(0, 0);
        }

        float sumYaw = 0;
        float sumPitch = 0;
        float totalWeight = 0;

        for (int i = 0; i < mouseTrail.size(); i++) {
            float weight = (i + 1.0f) / mouseTrail.size();
            sumYaw += mouseTrail.get(i).x * weight;
            sumPitch += mouseTrail.get(i).y * weight;
            totalWeight += weight;
        }

        return new Vector2f(sumYaw / totalWeight, sumPitch / totalWeight);
    }

    private void fullRandomize() {
        circularJitterPhase = random.nextDouble() * Math.PI * 2;
        circularJitterSpeed = 0.15 + random.nextDouble() * 0.20;
        circularJitterRadius = 4.5 + random.nextDouble() * 7.5;
        currentSmoothness = 0.65f + random.nextFloat() * 0.3f;
        mouseSimulator.randomize();
        humanInterpolator.randomize();
        neuralProcessor.randomize();
        mouseTrail.clear();
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        circularJitterPhase = 0;
        circularJitterSpeed = 0;
        circularJitterRadius = 0;
        lastJitterUpdate = 0;
        targetJitterSpeed = 0;
        targetJitterRadius = 0;

        lastPitchChange = 0;
        lastPitchMoveTime = 0;
        pitchChangeAccumulator = 0;

        lastFullRandomization = 0;

        mouseOvershoot = new Vector2f(0, 0);
        mouseDrift = new Vector2f(0, 0);
        lastMouseNoiseUpdate = 0;

        currentSmoothness = 0.5f;
        targetSmoothness = 0.5f;
        lastSmoothnessChange = 0;

        mouseTrail.clear();
        neuralProcessor.reset();
        mouseSimulator.reset();
        humanInterpolator.reset();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        mouseTrail.clear();
    }

    private static class NeuralRotationProcessor {
        private final double[][] weights1 = new double[8][4];
        private final double[] bias1 = new double[8];
        private final double[][] weights2 = new double[4][8];
        private final double[] bias2 = new double[4];
        private final double[][] weights3 = new double[2][4];
        private final double[] bias3 = new double[2];

        private final Random random = new Random();

        public NeuralRotationProcessor() {
            initializeWeights();
        }

        private void initializeWeights() {
            for (int i = 0; i < weights1.length; i++) {
                for (int j = 0; j < weights1[i].length; j++) {
                    weights1[i][j] = (random.nextDouble() - 0.5) * 0.4;
                }
                bias1[i] = (random.nextDouble() - 0.5) * 0.2;
            }

            for (int i = 0; i < weights2.length; i++) {
                for (int j = 0; j < weights2[i].length; j++) {
                    weights2[i][j] = (random.nextDouble() - 0.5) * 0.4;
                }
                bias2[i] = (random.nextDouble() - 0.5) * 0.2;
            }

            for (int i = 0; i < weights3.length; i++) {
                for (int j = 0; j < weights3[i].length; j++) {
                    weights3[i][j] = (random.nextDouble() - 0.5) * 0.4;
                }
                bias3[i] = (random.nextDouble() - 0.5) * 0.2;
            }
        }

        public Vector2f process(Vector2f interpolated, Vector2f current, float targetYaw, float targetPitch, float distance, long time) {
            double[] input = new double[4];
            input[0] = normalizeAngle(interpolated.x - current.x) / 180.0;
            input[1] = (interpolated.y - current.y) / 180.0;
            input[2] = Math.sin(time * 0.001) * 0.5;
            input[3] = Math.tanh(distance / 10.0);

            double[] layer1 = forwardLayer(input, weights1, bias1);
            applyActivation(layer1, ActivationType.LEAKY_RELU);

            double[] layer2 = forwardLayer(layer1, weights2, bias2);
            applyActivation(layer2, ActivationType.TANH);

            double[] output = forwardLayer(layer2, weights3, bias3);
            applyActivation(output, ActivationType.TANH);

            float adjustedYaw = interpolated.x + (float) (output[0] * 4.5);
            float adjustedPitch = interpolated.y + (float) (output[1] * 2.8);

            return new Vector2f(adjustedYaw, adjustedPitch);
        }

        private double[] forwardLayer(double[] input, double[][] weights, double[] bias) {
            double[] output = new double[weights.length];
            for (int i = 0; i < weights.length; i++) {
                double sum = bias[i];
                for (int j = 0; j < input.length; j++) {
                    sum += input[j] * weights[i][j];
                }
                output[i] = sum;
            }
            return output;
        }

        private void applyActivation(double[] values, ActivationType type) {
            for (int i = 0; i < values.length; i++) {
                switch (type) {
                    case RELU:
                        values[i] = Math.max(0, values[i]);
                        break;
                    case LEAKY_RELU:
                        values[i] = values[i] > 0 ? values[i] : values[i] * 0.01;
                        break;
                    case TANH:
                        values[i] = Math.tanh(values[i]);
                        break;
                    case SIGMOID:
                        values[i] = 1.0 / (1.0 + Math.exp(-values[i]));
                        break;
                }
            }
        }

        private double normalizeAngle(double angle) {
            while (angle > 180) angle -= 360;
            while (angle < -180) angle += 360;
            return angle;
        }

        public void randomize() {
            initializeWeights();
        }

        public void reset() {
            initializeWeights();
        }

        private enum ActivationType {
            RELU, LEAKY_RELU, TANH, SIGMOID
        }
    }

    private static class MouseSimulator {
        private float overshootX = 0;
        private float overshootY = 0;
        private float targetOvershootX = 0;
        private float targetOvershootY = 0;

        private float driftX = 0;
        private float driftY = 0;
        private float driftVelocityX = 0;
        private float driftVelocityY = 0;

        private float microMovementX = 0;
        private float microMovementY = 0;

        private long lastUpdate = 0;
        private long lastOvershootChange = 0;
        private long lastMicroMovement = 0;

        private final Random random = new Random();

        public void update(long currentTime) {
            if (currentTime - lastOvershootChange > 150 + random.nextInt(200)) {
                targetOvershootX = (random.nextFloat() - 0.5f) * 3.0f;
                targetOvershootY = (random.nextFloat() - 0.5f) * 1.8f;
                lastOvershootChange = currentTime;
            }

            overshootX = MathHelper.lerp(0.18f, overshootX, targetOvershootX);
            overshootY = MathHelper.lerp(0.18f, overshootY, targetOvershootY);

            float driftAccel = 0.06f;
            driftVelocityX += (random.nextFloat() - 0.5f) * driftAccel;
            driftVelocityY += (random.nextFloat() - 0.5f) * driftAccel;

            driftVelocityX *= 0.88f;
            driftVelocityY *= 0.88f;

            driftX += driftVelocityX;
            driftY += driftVelocityY;

            driftX *= 0.90f;
            driftY *= 0.90f;

            if (currentTime - lastMicroMovement > 50 + random.nextInt(100)) {
                microMovementX = (random.nextFloat() - 0.5f) * 0.8f;
                microMovementY = (random.nextFloat() - 0.5f) * 0.6f;
                lastMicroMovement = currentTime;
            }

            lastUpdate = currentTime;
        }

        public Vector2f getMouseNoise() {
            float totalX = overshootX + driftX + microMovementX;
            float totalY = overshootY + driftY + microMovementY;
            return new Vector2f(totalX, totalY);
        }

        public void randomize() {
            overshootX = (random.nextFloat() - 0.5f) * 3.0f;
            overshootY = (random.nextFloat() - 0.5f) * 1.8f;
            targetOvershootX = (random.nextFloat() - 0.5f) * 3.0f;
            targetOvershootY = (random.nextFloat() - 0.5f) * 1.8f;
            driftX = 0;
            driftY = 0;
            driftVelocityX = 0;
            driftVelocityY = 0;
        }

        public void reset() {
            overshootX = 0;
            overshootY = 0;
            targetOvershootX = 0;
            targetOvershootY = 0;
            driftX = 0;
            driftY = 0;
            driftVelocityX = 0;
            driftVelocityY = 0;
            microMovementX = 0;
            microMovementY = 0;
            lastUpdate = 0;
            lastOvershootChange = 0;
            lastMicroMovement = 0;
        }
    }

    private static class HumanInterpolator {
        private final double[] bezierFactors = new double[4];
        private long lastBezierChange = 0;
        private final Random random = new Random();

        public HumanInterpolator() {
            randomizeBezier();
        }

        private void randomizeBezier() {
            bezierFactors[0] = 0.1 + random.nextDouble() * 0.2;
            bezierFactors[1] = 0.3 + random.nextDouble() * 0.4;
            bezierFactors[2] = 0.5 + random.nextDouble() * 0.3;
            bezierFactors[3] = 0.8 + random.nextDouble() * 0.2;
        }

        public Vector2f interpolate(Vector2f current, Vector2f target, float smoothness, Random random) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastBezierChange > 300 + random.nextInt(400)) {
                randomizeBezier();
                lastBezierChange = currentTime;
            }

            double t = smoothness;

            double bezierT = cubicBezier(t, bezierFactors[0], bezierFactors[1], bezierFactors[2], bezierFactors[3]);

            double easeT = easeInOutCubic(bezierT);

            double finalT = easeT * (0.95 + random.nextDouble() * 0.1);

            float yaw = (float) MathHelper.lerp(finalT, current.x, target.x);
            float pitch = (float) MathHelper.lerp(finalT, current.y, target.y);

            return new Vector2f(yaw, pitch);
        }

        private double cubicBezier(double t, double p1, double p2, double p3, double p4) {
            double u = 1 - t;
            double tt = t * t;
            double uu = u * u;
            double uuu = uu * u;
            double ttt = tt * t;

            return uuu * 0 + 3 * uu * t * p1 + 3 * u * tt * p2 + ttt * 1;
        }

        private double easeInOutCubic(double t) {
            return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
        }

        public void randomize() {
            randomizeBezier();
        }

        public void reset() {
            randomizeBezier();
            lastBezierChange = 0;
        }
    }
}