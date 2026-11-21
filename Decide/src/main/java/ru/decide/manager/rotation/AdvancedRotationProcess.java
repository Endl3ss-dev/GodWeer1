/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_3532
 */
package ru.decide.manager.rotation;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_3532;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.manager.rotation.Component;
import ru.decide.manager.rotation.FreeLookUtil;
import ru.decide.manager.rotation.Rotation;
import ru.decide.utils.aura.GCDUtil;

@Environment(value=EnvType.CLIENT)
public class AdvancedRotationProcess
extends Component {
    public static RotationTask currentTask = RotationTask.IDLE;
    public static int currentPriority;
    public static int currentTimeout;
    public static int idleTicks;
    public static Rotation targetRotation;
    public static float currentYawSpeed;
    public static float currentPitchSpeed;
    public static float currentYawReturnSpeed;
    public static float currentPitchReturnSpeed;
    public static float shakeYaw;
    public static float shakePitch;
    public static float resetShakeAmplitudeYaw;
    public static float resetShakeAmplitudePitch;
    public static int shakeDuration;
    private static int resetShakeTicks;
    private static final Random RNG;

    public static boolean isRotating() {
        return !currentTask.equals((Object)RotationTask.IDLE);
    }

    @EventHandler
    public void onEvent(EventTick event) {
        if (currentTask.equals((Object)RotationTask.AIM) && idleTicks > currentTimeout) {
            currentTask = RotationTask.RESET;
            resetShakeTicks = 0;
        }
        if (currentTask.equals((Object)RotationTask.RESET)) {
            ++resetShakeTicks;
            this.doResetRotation();
        }
        ++idleTicks;
    }

    private void doResetRotation() {
        boolean reached;
        Rotation target = new Rotation(FreeLookUtil.freeYaw, FreeLookUtil.freePitch);
        float sYaw = 0.0f;
        float sPitch = 0.0f;
        if (shakeDuration > 0 && resetShakeTicks <= shakeDuration) {
            float fade = 1.0f - (float)resetShakeTicks / (float)shakeDuration;
            sYaw = (RNG.nextFloat() * 2.0f - 1.0f) * resetShakeAmplitudeYaw * fade;
            sPitch = (RNG.nextFloat() * 2.0f - 1.0f) * resetShakeAmplitudePitch * fade;
        }
        if ((reached = AdvancedRotationProcess.updateRotation(target, currentYawReturnSpeed, currentPitchReturnSpeed, sYaw, sPitch)) && (shakeDuration <= 0 || resetShakeTicks > shakeDuration)) {
            this.stopRotation();
        }
    }

    public static void update(AdvancedRotationConfig cfg) {
        if (currentPriority > cfg.priority) {
            return;
        }
        if (currentTask.equals((Object)RotationTask.IDLE) && !cfg.clientRotation) {
            FreeLookUtil.active = true;
        }
        currentYawSpeed = cfg.yawSpeed;
        currentPitchSpeed = cfg.pitchSpeed;
        currentYawReturnSpeed = cfg.yawReturnSpeed;
        currentPitchReturnSpeed = cfg.pitchReturnSpeed;
        currentTimeout = cfg.timeout;
        currentPriority = cfg.priority;
        shakeYaw = cfg.shakeYaw;
        shakePitch = cfg.shakePitch;
        resetShakeAmplitudeYaw = cfg.resetShakeAmplitudeYaw;
        resetShakeAmplitudePitch = cfg.resetShakeAmplitudePitch;
        shakeDuration = cfg.shakeDuration;
        currentTask = RotationTask.AIM;
        targetRotation = cfg.target;
        AdvancedRotationProcess.updateRotation(cfg.target, cfg.yawSpeed, cfg.pitchSpeed, cfg.shakeYaw, cfg.shakePitch);
    }

    public static void update(Rotation target, float turnSpeed, float returnSpeed, int timeout, int priority) {
        AdvancedRotationProcess.update(new AdvancedRotationConfig(target).speed(turnSpeed, returnSpeed).timeout(timeout).priority(priority));
    }

    public static void update(Rotation target, float yawSpeed, float pitchSpeed, float yawReturnSpeed, float pitchReturnSpeed, int timeout, int priority, boolean clientRotation) {
        AdvancedRotationProcess.update(new AdvancedRotationConfig(target).speed(yawSpeed, pitchSpeed, yawReturnSpeed, pitchReturnSpeed).timeout(timeout).priority(priority).clientRotation(clientRotation));
    }

    public static void resetParentTimeout() {
        currentTimeout = 0;
        currentTask = RotationTask.IDLE;
        currentPriority = 0;
        FreeLookUtil.setActive(false);
    }

    static boolean updateRotation(Rotation targetRot, float yawSpeed, float pitchSpeed, float sYaw, float sPitch) {
        if (AdvancedRotationProcess.mc.field_1724 == null) {
            return false;
        }
        Rotation current = new Rotation((class_1297)AdvancedRotationProcess.mc.field_1724);
        float yawDelta = class_3532.method_15393((float)(targetRot.yaw - current.yaw));
        float pitchDelta = targetRot.pitch - current.pitch;
        float clampedYaw = Math.min(Math.abs(yawDelta), yawSpeed);
        float clampedPitch = Math.min(Math.abs(pitchDelta), pitchSpeed);
        float moveYaw = GCDUtil.getSensitivity(class_3532.method_15363((float)yawDelta, (float)(-clampedYaw), (float)clampedYaw)) + sYaw;
        float movePitch = GCDUtil.getSensitivity(class_3532.method_15363((float)pitchDelta, (float)(-clampedPitch), (float)clampedPitch)) + sPitch;
        AdvancedRotationProcess.mc.field_1724.method_36456(AdvancedRotationProcess.mc.field_1724.field_6241 += moveYaw);
        AdvancedRotationProcess.mc.field_1724.method_36457(class_3532.method_15363((float)(AdvancedRotationProcess.mc.field_1724.method_36455() + movePitch), (float)-90.0f, (float)90.0f));
        idleTicks = 0;
        return new Rotation((class_1297)AdvancedRotationProcess.mc.field_1724).getDelta(targetRot) < 1.0f;
    }

    public void stopRotation() {
        currentTask = RotationTask.IDLE;
        currentPriority = 0;
        FreeLookUtil.setActive(false);
    }

    static {
        RNG = new Random();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum RotationTask {
        AIM,
        RESET,
        IDLE;

    }

    @Environment(value=EnvType.CLIENT)
    public static class AdvancedRotationConfig {
        public Rotation target;
        public float yawSpeed = 180.0f;
        public float pitchSpeed = 180.0f;
        public float yawReturnSpeed = 180.0f;
        public float pitchReturnSpeed = 180.0f;
        public float shakeYaw = 0.0f;
        public float shakePitch = 0.0f;
        public float resetShakeAmplitudeYaw = 0.0f;
        public float resetShakeAmplitudePitch = 0.0f;
        public int shakeDuration = 0;
        public int timeout = 1;
        public int priority = 0;
        public boolean clientRotation = false;

        public AdvancedRotationConfig(Rotation target) {
            this.target = target;
        }

        public AdvancedRotationConfig speed(float turnSpeed, float returnSpeed) {
            this.yawSpeed = turnSpeed;
            this.pitchSpeed = turnSpeed;
            this.yawReturnSpeed = returnSpeed;
            this.pitchReturnSpeed = returnSpeed;
            return this;
        }

        public AdvancedRotationConfig speed(float yawSpeed, float pitchSpeed, float yawReturnSpeed, float pitchReturnSpeed) {
            this.yawSpeed = yawSpeed;
            this.pitchSpeed = pitchSpeed;
            this.yawReturnSpeed = yawReturnSpeed;
            this.pitchReturnSpeed = pitchReturnSpeed;
            return this;
        }

        public AdvancedRotationConfig shake(float yaw, float pitch) {
            this.shakeYaw = yaw;
            this.shakePitch = pitch;
            return this;
        }

        public AdvancedRotationConfig resetShake(float amplitudeYaw, float amplitudePitch, int durationTicks) {
            this.resetShakeAmplitudeYaw = amplitudeYaw;
            this.resetShakeAmplitudePitch = amplitudePitch;
            this.shakeDuration = durationTicks;
            return this;
        }

        public AdvancedRotationConfig timeout(int ticks) {
            this.timeout = ticks;
            return this;
        }

        public AdvancedRotationConfig priority(int p) {
            this.priority = p;
            return this;
        }

        public AdvancedRotationConfig clientRotation(boolean v) {
            this.clientRotation = v;
            return this;
        }
    }
}

