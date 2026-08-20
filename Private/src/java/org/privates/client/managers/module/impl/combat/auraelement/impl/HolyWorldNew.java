package org.privates.client.managers.module.impl.combat.auraelement.impl;

import com.adl.nativeprotect.Native;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;

import java.util.concurrent.ThreadLocalRandom;


public class HolyWorldNew extends AbstractRotationElement {

    private static final float[][] JITTER_PATTERN = {
            new float[]{-0.7501221f, 0.0f}, new float[]{-0.7501221f, 0.0f}, new float[]{-0.7501221f, 0.0f},
            new float[]{-0.7501221f, -0.30000305f}, new float[]{-0.7501221f, -0.15000153f}, new float[]{-1.0501709f, -0.6000061f},
            new float[]{-0.9001465f, -0.6000061f}, new float[]{-0.9001465f, -0.90000534f}, new float[]{-0.9001465f, -1.2000122f},
            new float[]{-0.90008545f, -1.3500099f}, new float[]{-0.90008545f, -1.6500053f}, new float[]{-1.2001953f, -2.1000137f},
            new float[]{-0.9001465f, -1.800003f}, new float[]{-0.7501221f, -1.5000038f}, new float[]{-0.60009766f, -1.5000019f},
            new float[]{-0.60009766f, -1.3499966f}, new float[]{-0.60009766f, -1.199997f}, new float[]{-0.45007324f, -1.3499966f},
            new float[]{-0.30004883f, -0.7499981f}, new float[]{0.0f, -0.7499981f}, new float[]{0.0f, -0.7499981f},
            new float[]{0.0f, -0.44999886f}, new float[]{0.30004883f, -0.44999886f}, new float[]{0.45007324f, -0.5999985f},
            new float[]{0.7501221f, -0.7499981f}, new float[]{0.9001465f, -0.44999886f}, new float[]{0.90008545f, -0.44999886f},
            new float[]{0.90008545f, -0.29999924f}, new float[]{0.75006104f, -0.14999962f}, new float[]{1.2001343f, 0.0f},
            new float[]{1.3502197f, 0.14999962f}, new float[]{1.0501099f, 0.29999924f}, new float[]{1.0501099f, 0.29999924f},
            new float[]{0.9001465f, 0.44999886f}, new float[]{1.0501709f, 0.44999886f}, new float[]{0.7501221f, 0.44999886f},
            new float[]{0.90008545f, 0.44999886f}, new float[]{1.3501587f, 0.5999985f}, new float[]{1.3501587f, 0.5999985f},
            new float[]{0.9001465f, 0.29999924f}, new float[]{1.2001343f, 0.29999924f}, new float[]{0.7501221f, 0.0f},
            new float[]{0.90008545f, 0.0f}, new float[]{1.2001343f, -0.29999924f}, new float[]{0.60009766f, -0.29999924f},
            new float[]{0.7501221f, -0.5999985f}, new float[]{0.60009766f, -0.7499981f}, new float[]{0.60009766f, -0.7499981f},
            new float[]{0.60009766f, -1.199997f}, new float[]{0.45007324f, -1.3499966f}, new float[]{0.45007324f, -1.0499973f},
            new float[]{0.30004883f, -1.199997f}, new float[]{0.15002441f, -1.199997f}, new float[]{0.0f, -0.8999977f},
            new float[]{0.0f, -1.0499992f}, new float[]{0.0f, -1.0499973f}, new float[]{-0.30004883f, -1.4999962f},
            new float[]{-0.30004883f, -0.5999985f}, new float[]{-0.45007324f, -0.89999866f}, new float[]{-0.45007324f, -0.7499981f},
            new float[]{-0.60009766f, -0.44999886f}, new float[]{-0.6000366f, -0.44999886f}, new float[]{-0.45007324f, -0.29999924f},
            new float[]{-1.8002319f, -0.5999985f}, new float[]{-1.0501099f, -0.14999962f}, new float[]{-1.0501099f, 0.0f},
            new float[]{-1.2000732f, 0.0f}, new float[]{-1.0501709f, 0.0f}, new float[]{-1.0501099f, 0.29999924f},
            new float[]{-1.6501465f, 0.29999924f}, new float[]{-1.0501099f, 0.29999924f}, new float[]{-1.500061f, 0.44999886f},
            new float[]{-1.0501099f, 0.29999924f}, new float[]{-1.2001343f, 0.44999886f}, new float[]{-1.0501099f, 0.5999985f},
            new float[]{-1.2001343f, 0.5999985f}, new float[]{-1.2001343f, 0.7499981f}, new float[]{-1.2001953f, 0.8999977f},
            new float[]{-0.75006104f, 0.74999905f}, new float[]{-0.9001465f, 1.199997f}, new float[]{-0.45007324f, 0.8999996f},
            new float[]{-0.45007324f, 1.3499966f}, new float[]{-0.15002441f, 1.7999954f}, new float[]{0.0f, 1.7999954f},
            new float[]{0.0f, 1.4999962f}, new float[]{0.0f, 1.3499966f}, new float[]{0.15002441f, 1.3499966f},
            new float[]{0.15002441f, 1.199997f}, new float[]{0.15002441f, 1.0499973f}, new float[]{0.0f, 1.199997f},
            new float[]{0.0f, 1.0499973f}, new float[]{-0.15002441f, 0.8999977f}, new float[]{-0.30004883f, 0.8999977f},
            new float[]{-0.45007324f, 0.90000534f}, new float[]{-0.30004883f, 0.6000061f}, new float[]{-0.45007324f, 0.6000061f},
            new float[]{-0.45007324f, 0.30000305f}, new float[]{-0.30004883f, 0.30000305f}, new float[]{-0.30004883f, 0.15000153f},
            new float[]{-0.45007324f, 0.15000153f}, new float[]{-0.15002441f, 0.0f}, new float[]{-0.45007324f, 0.0f},
            new float[]{-0.30004883f, -0.15000153f}, new float[]{-0.30004883f, -0.30000305f}, new float[]{-0.60009766f, -0.45000458f},
            new float[]{-0.45007324f, -0.7500076f}, new float[]{-0.7501221f, -1.0500107f}, new float[]{-0.45007324f, -0.8999977f},
            new float[]{-0.30004883f, -1.199997f}, new float[]{-0.45007324f, -1.0499973f}, new float[]{-0.15002441f, -1.0499973f},
            new float[]{-0.15002441f, -1.199997f}, new float[]{0.0f, -1.4999962f}, new float[]{0.15002441f, -1.199997f},
            new float[]{0.30004883f, -1.0499973f}, new float[]{0.7501221f, -0.8999977f}, new float[]{1.0501099f, -0.7499981f},
            new float[]{0.9001465f, -0.14999962f}, new float[]{1.0501099f, -0.14999962f}, new float[]{1.5001831f, 0.29999924f},
            new float[]{1.2000732f, 0.44999886f}, new float[]{1.2001343f, 0.7499981f}, new float[]{1.0501099f, 0.8999977f},
            new float[]{1.0501099f, 1.199997f}, new float[]{0.9001465f, 1.199997f}, new float[]{0.9001465f, 1.4999962f},
            new float[]{1.0501099f, 2.249998f}, new float[]{0.7501221f, 1.6499958f}, new float[]{0.75006104f, 1.9500008f},
            new float[]{1.2001343f, 2.4000015f}, new float[]{0.7501221f, 1.5000076f}, new float[]{1.0501709f, 1.650013f},
            new float[]{0.7501221f, 0.7500076f}, new float[]{0.30004883f, 0.6000061f}, new float[]{0.45007324f, 0.30000305f},
            new float[]{0.15002441f, 0.15000153f}, new float[]{0.15002441f, 0.0f}, new float[]{0.15002441f, 0.15000153f},
            new float[]{0.30004883f, 0.0f}, new float[]{0.45007324f, -0.30000305f}, new float[]{0.60009766f, -0.45000458f},
            new float[]{0.9001465f, -0.6000061f}, new float[]{0.9001465f, -0.6000061f}, new float[]{1.5001831f, -1.2000122f},
            new float[]{1.2001343f, -0.90000916f}, new float[]{1.2001953f, -1.0500069f}, new float[]{1.2001953f, -1.3500099f},
            new float[]{0.9001465f, -1.3500099f}, new float[]{1.0501709f, -1.8000011f}, new float[]{0.60009766f, -1.6499958f},
            new float[]{0.45007324f, -1.6499958f}, new float[]{0.30004883f, -1.7999954f}, new float[]{0.15002441f, -1.7999954f},
            new float[]{0.0f, -2.2499943f}, new float[]{-0.15002441f, -1.3499966f}, new float[]{-0.30004883f, -1.199997f},
            new float[]{-0.60009766f, -1.4999962f}, new float[]{-0.60009766f, -1.0499973f}, new float[]{-1.0501709f, -1.1999979f},
            new float[]{-0.9001465f, -0.8999977f}, new float[]{-0.9001465f, -0.5999985f}, new float[]{-1.3501587f, -0.5999985f},
            new float[]{-1.3502197f, -0.44999886f}, new float[]{-1.0501099f, -0.14999962f}, new float[]{-1.3501587f, -0.14999962f},
            new float[]{-0.90008545f, 0.0f}, new float[]{-0.9001465f, 0.0f}, new float[]{-0.9001465f, 0.0f},
            new float[]{-1.0501709f, 0.29999924f}, new float[]{-0.9001465f, 0.29999924f}, new float[]{-0.60009766f, 0.14999962f},
            new float[]{-1.2001343f, 0.7499981f}, new float[]{-0.7501221f, 0.5999985f}, new float[]{-0.60009766f, 0.74999905f},
            new float[]{-0.45007324f, 0.8999977f}, new float[]{-0.60009766f, 1.3499966f}, new float[]{-0.60009766f, 1.4999981f},
            new float[]{-0.60009766f, 1.4999962f}, new float[]{-0.60009766f, 2.0999966f}, new float[]{-0.45007324f, 1.6499977f},
            new float[]{-0.60009766f, 2.0999966f}, new float[]{-0.30004883f, 1.949997f}, new float[]{-0.30004883f, 1.7999954f},
            new float[]{-0.30004883f, 1.199997f}, new float[]{-0.15002441f, 1.2000046f}, new float[]{-0.15002441f, 0.90000534f},
            new float[]{-0.30004883f, 0.6000061f}, new float[]{-0.15002441f, 0.6000061f}, new float[]{-0.15002441f, 0.45000458f},
            new float[]{-0.15002441f, 0.30000305f}, new float[]{-0.15002441f, 0.15000153f}, new float[]{0.0f, 0.0f},
            new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f}, new float[]{-0.15002441f, -0.15000153f},
            new float[]{-0.30004883f, -0.15000153f}, new float[]{-0.30004883f, -0.7500076f}, new float[]{-0.45007324f, -0.7500076f},
            new float[]{-0.60009766f, -0.90000534f}, new float[]{-0.60009766f, -1.2000084f}, new float[]{-0.7501221f, -1.3499966f},
            new float[]{-0.60009766f, -1.0499973f}, new float[]{-0.9001465f, -2.0999947f}, new float[]{-0.60009766f, -1.4999962f},
            new float[]{-0.45007324f, -1.4999962f}, new float[]{-0.30004883f, -1.3499966f}, new float[]{-0.15002441f, -1.0499973f},
            new float[]{-0.15002441f, -1.0499973f}, new float[]{0.0f, -1.0499973f}, new float[]{0.0f, -0.7499981f},
            new float[]{0.15002441f, -0.5999985f}, new float[]{0.30004883f, -0.44999886f}, new float[]{0.30004883f, -0.29999924f},
            new float[]{0.60009766f, -0.44999886f}, new float[]{0.45007324f, -0.14999962f}, new float[]{1.2001343f, 0.0f},
            new float[]{0.7501221f, 0.0f}, new float[]{1.0501709f, 0.14999962f}, new float[]{0.9001465f, 0.14999962f},
            new float[]{0.9001465f, 0.44999886f}, new float[]{1.0501099f, 0.44999886f}, new float[]{1.2001343f, 0.8999977f},
            new float[]{1.0501099f, 1.199997f}, new float[]{0.90008545f, 1.0499973f}, new float[]{0.9001465f, 1.4999962f},
            new float[]{1.0501709f, 1.4999962f}, new float[]{0.9001465f, 1.3499966f}, new float[]{0.9001465f, 1.4999962f},
            new float[]{1.0501709f, 1.199997f}, new float[]{0.7501221f, 0.8999977f}, new float[]{0.45007324f, 0.44999886f},
            new float[]{0.60009766f, 0.5999985f}, new float[]{0.30004883f, 0.29999924f}, new float[]{0.45007324f, 0.29999924f},
            new float[]{0.30004883f, 0.14999962f}, new float[]{0.30004883f, 0.0f}, new float[]{0.45007324f, 0.0f},
            new float[]{0.45007324f, -0.14999962f}, new float[]{0.60009766f, -0.29999924f}, new float[]{0.7501221f, -0.44999886f},
            new float[]{0.7501221f, -0.5999985f}, new float[]{1.3502197f, -1.0499973f}, new float[]{1.3502197f, -0.8999977f},
            new float[]{1.3501587f, -1.3499966f}, new float[]{1.2001953f, -1.0499973f}, new float[]{0.90008545f, -1.0499973f},
            new float[]{0.90008545f, -0.8999977f}, new float[]{1.3502197f, -1.7999954f}, new float[]{0.7501221f, -1.199997f},
            new float[]{0.45007324f, -0.8999977f}, new float[]{0.45007324f, -1.0499973f}, new float[]{0.15002441f, -0.8999977f},
            new float[]{0.0f, -0.7499981f}, new float[]{0.0f, -0.44999886f}, new float[]{-0.30004883f, -0.44999886f},
            new float[]{-0.15002441f, -0.14999962f}, new float[]{-0.60009766f, -0.29999924f}, new float[]{-0.7501221f, -0.29999924f},
            new float[]{-1.3501587f, 0.0f}, new float[]{-1.3501587f, 0.0f}, new float[]{-1.0501099f, 0.29999924f},
            new float[]{-1.6500854f, 0.29999924f}, new float[]{-1.6501465f, 0.44999886f}, new float[]{-1.8001099f, 0.5999985f},
            new float[]{-2.1001587f, 0.5999985f}, new float[]{-1.2001343f, 0.44999886f}, new float[]{-1.0501709f, 0.5999985f},
            new float[]{-0.7501221f, 0.29999924f}, new float[]{-0.30004883f, 0.29999924f}, new float[]{-0.45007324f, 0.5999985f},
            new float[]{-0.30004883f, 0.5999985f}, new float[]{-0.30004883f, 0.7499981f}, new float[]{-0.30004883f, 0.8999977f},
            new float[]{-0.30004883f, 0.8999977f}, new float[]{-0.30004883f, 1.199997f}, new float[]{-0.45007324f, 1.6499958f},
            new float[]{-0.45007324f, 1.199997f}, new float[]{-0.45007324f, 1.199997f}, new float[]{-0.45007324f, 1.199997f},
            new float[]{-0.60009766f, 1.199997f}, new float[]{-0.7501221f, 1.3499966f}, new float[]{-0.60009766f, 0.90000534f},
            new float[]{-0.60009766f, 1.0500107f}, new float[]{-0.60009766f, 0.90000916f}, new float[]{-0.60009766f, 0.6000061f},
            new float[]{-0.60009766f, 0.45000458f}, new float[]{-0.60009766f, 0.45000458f}, new float[]{-0.45007324f, 0.15000153f},
            new float[]{-0.30004883f, 0.0f}, new float[]{-0.30004883f, 0.0f}, new float[]{-0.15002441f, 0.0f},
            new float[]{-0.45007324f, -0.30000305f}, new float[]{-0.45007324f, -0.45000458f}, new float[]{-0.30004883f, -0.6000061f},
            new float[]{-0.60009766f, -0.7500076f}, new float[]{-0.45007324f, -1.2000046f}, new float[]{-0.45007324f, -1.0500069f},
            new float[]{-0.45007324f, -2.0999947f}, new float[]{-0.15002441f, -1.3499966f}, new float[]{-0.15002441f, -1.949995f},
            new float[]{0.0f, -1.949997f}, new float[]{0.15002441f, -1.6499958f}, new float[]{0.60009766f, -1.949995f},
            new float[]{0.45007324f, -1.4999962f}, new float[]{0.7501221f, -1.0499973f}, new float[]{0.60009766f, -0.8999977f},
            new float[]{0.60009766f, -0.5999985f}, new float[]{0.7501221f, -0.5999985f}, new float[]{0.9001465f, -0.5999985f},
            new float[]{0.45007324f, -0.29999924f}, new float[]{0.60009766f, -0.14999962f}, new float[]{0.30004883f, -0.14999962f},
            new float[]{0.45007324f, 0.0f}, new float[]{0.30004883f, 0.14999962f}, new float[]{0.7501221f, 0.44999886f},
            new float[]{0.60009766f, 0.5999985f}, new float[]{0.7501221f, 1.0499973f}, new float[]{0.90008545f, 1.3499966f},
            new float[]{0.9001465f, 1.6499958f}, new float[]{0.9001465f, 1.4999962f}, new float[]{1.2001953f, 3.1499958f},
            new float[]{0.7501221f, 2.5499935f}, new float[]{0.60009766f, 1.7999973f}, new float[]{0.45007324f, 1.4999962f},
            new float[]{0.60009766f, 1.3500004f}, new float[]{0.60009766f, 1.3500137f}, new float[]{0.15002441f, 0.7500076f},
            new float[]{0.30004883f, 0.6000061f}, new float[]{0.15002441f, 0.15000153f}, new float[]{0.30004883f, 0.15000153f},
            new float[]{0.15002441f, 0.0f}, new float[]{0.60009766f, 0.0f}, new float[]{0.7501221f, -0.6000061f},
            new float[]{0.9001465f, -0.7500076f}, new float[]{1.2001953f, -1.0500107f}, new float[]{1.0501709f, -1.0500088f},
            new float[]{1.0501709f, -1.3499966f}, new float[]{0.9001465f, -1.3499966f}, new float[]{0.9001465f, -1.7999954f},
            new float[]{0.9001465f, -2.2499943f}, new float[]{0.30004883f, -1.3499966f}, new float[]{0.15002441f, -1.4999962f},
            new float[]{0.15002441f, -1.4999962f}, new float[]{-0.15002441f, -1.199997f}, new float[]{-0.45007324f, -1.0499973f},
            new float[]{-0.30004883f, -0.7499981f}, new float[]{-0.7501221f, -0.8999977f}, new float[]{-0.9001465f, -0.7499981f},
            new float[]{-0.7501221f, -0.44999886f}, new float[]{-1.2001953f, -0.29999924f}, new float[]{-1.0501099f, -0.14999962f},
            new float[]{-1.5001831f, 0.0f}, new float[]{-1.6502686f, 0.29999924f}, new float[]{-1.5001221f, 0.5999985f},
            new float[]{-1.3501587f, 0.44999886f}, new float[]{-1.0501099f, 0.7499981f}, new float[]{-1.2001953f, 0.8999977f},
            new float[]{-0.90008545f, 1.0499973f}, new float[]{-0.90008545f, 1.0499973f}, new float[]{-1.0501709f, 1.7999954f},
            new float[]{-0.60009766f, 1.199997f}, new float[]{-0.45007324f, 1.3499966f}, new float[]{-0.45007324f, 1.6499958f},
            new float[]{-0.30004883f, 1.3499966f}, new float[]{-0.15002441f, 1.7999954f}, new float[]{-0.15002441f, 1.199997f},
            new float[]{-0.30004883f, 1.6499996f}, new float[]{-0.30004883f, 1.8000069f}, new float[]{-0.45007324f, 1.3500137f},
            new float[]{-0.60009766f, 1.3500137f}, new float[]{-0.30004883f, 0.7500076f}, new float[]{-0.30004883f, 0.45000458f},
            new float[]{-0.15002441f, 0.30000305f}, new float[]{-0.30004883f, 0.0f}, new float[]{-0.15002441f, 0.15000153f},
            new float[]{0.0f, 0.0f}, new float[]{-0.15002441f, 0.0f}, new float[]{-0.30004883f, -0.30000305f},
            new float[]{-0.45007324f, -0.6000061f}
    };

    private enum RotationPhase {
        APPROACHING, LOCKED, ATTACKING, RECOVERING, IDLE
    }

    private long lastNs = 0L;
    private RotationPhase currentPhase = RotationPhase.IDLE;

    private float lastYawSpeed = 0.0f;
    private float lastPitchSpeed = 0.0f;
    private float overshootYaw = 0.0f;
    private float overshootPitch = 0.0f;
    private boolean hasOvershot = false;
    private float momentumYaw = 0.0f;
    private float momentumPitch = 0.0f;

    private int hesitationTicks = 0;
    private boolean isHesitating = false;
    private float hesitationSlowdown = 1.0f;

    private float postAttackYawDrift = 0.0f;
    private float postAttackPitchDrift = 0.0f;
    private int postAttackTicks = 0;
    private int postAttackDuration = 0;
    private boolean inPostAttack = false;
    private boolean isPerformingHighPitchFlick = false;

    private float preAttackTension = 0.0f;
    private int preAttackTicks = 0;

    private int attackCounter = 0;
    private float fatigueMultiplier = 1.0f;

    private int patternIndex = 0;
    private long lastJitterUpdateTime = 0;
    private Vector2f currentJitterOffset = Vector2f.ZERO;
    private Vector2f targetJitterOffset = Vector2f.ZERO;

    @Override
    @Native
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) {
            return currentLerpRotation;
        }

        long nowNs = System.nanoTime();
        float dt = lastNs == 0L ? 0.016f : MathHelper.clamp((nowNs - lastNs) / 1_000_000_000f, 0.008f, 0.05f);
        lastNs = nowNs;

        float attackProgress = parentModule.cooldownFromLastSwing();
        updateRotationPhase(attackProgress, mc.player.getDistance(target));
        updatePredefinedJitter(nowNs);
        updateHumanizedEffects(dt);
        updateFatigue();

        if (isPerformingHighPitchFlick && attackProgress > 0.85f) {
            isPerformingHighPitchFlick = false;
        }

        Vector3d eyePosition = mc.player.getEyePosition(mc.getRenderPartialTicks());
        double verticalAimPoint = target.getEyeHeight() * (0.50 + ThreadLocalRandom.current().nextDouble() * 0.35);
        Vector3d targetCenter = target.getPositionVec().add(0, verticalAimPoint, 0);

        Vector3d directionVec = targetCenter.subtract(eyePosition).normalize();

        float requiredYaw = (float) Math.toDegrees(Math.atan2(-directionVec.x, directionVec.z));
        float requiredPitch = (float) MathHelper.clamp(-Math.toDegrees(Math.atan2(directionVec.y, Math.hypot(directionVec.x, directionVec.z))), -90F, 90F);

        float targetYaw = requiredYaw + currentJitterOffset.x;
        float targetPitch = requiredPitch + currentJitterOffset.y;

        if (attackProgress > 0.65f && attackProgress < 0.85f && !inPostAttack && !isPerformingHighPitchFlick) {
            preAttackTicks++;
            preAttackTension = Math.min(preAttackTicks * 0.008f, 0.12f);
            targetYaw += (ThreadLocalRandom.current().nextFloat() - 0.5f) * preAttackTension;
            targetPitch += (ThreadLocalRandom.current().nextFloat() - 0.5f) * preAttackTension * 0.8f;
        } else {
            preAttackTicks = 0;
            preAttackTension = 0.0f;
        }

        boolean shouldOvershoot = attackProgress > 0.78f && !hasOvershot && !inPostAttack && !isPerformingHighPitchFlick && ThreadLocalRandom.current().nextFloat() < 0.12f;
        if (shouldOvershoot) {
            float yawDist = Math.abs(MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x));
            float pitchDist = Math.abs(targetPitch - currentLerpRotation.y);
            float speedFactor = (lastYawSpeed + lastPitchSpeed) / 2.0f;
            if ((yawDist > 2.5f || pitchDist > 1.8f) && speedFactor > 5.0f) {
                overshootYaw = (ThreadLocalRandom.current().nextFloat() - 0.5f) * (0.9f + speedFactor * 0.05f);
                overshootPitch = (ThreadLocalRandom.current().nextFloat() - 0.5f) * (0.7f + speedFactor * 0.04f);
                hasOvershot = true;
            }
        }

        if (hasOvershot) {
            float overshootDecay = 1f - (float) Math.exp(-5.5f * dt);
            overshootYaw *= (1.0f - overshootDecay);
            overshootPitch *= (1.0f - overshootDecay);
            targetYaw += overshootYaw;
            targetPitch += overshootPitch;
            if (Math.abs(overshootYaw) < 0.04f && Math.abs(overshootPitch) < 0.04f) {
                hasOvershot = false;
            }
        }

        if (attackProgress < 0.15f) {
            initiatePostAttack();
        }

        Vector2f finalTargetRotation;
        if (isPerformingHighPitchFlick) {
            finalTargetRotation = new Vector2f(currentLerpRotation.x, -0 - ThreadLocalRandom.current().nextFloat() * 5);
        } else if (inPostAttack) {
            updatePostAttack(dt);
            finalTargetRotation = new Vector2f(currentLerpRotation.x + postAttackYawDrift, currentLerpRotation.y + postAttackPitchDrift);
        } else {
            finalTargetRotation = new Vector2f(targetYaw, targetPitch);
        }

        Vector2f finalRotation = interpolateRotation(currentLerpRotation, finalTargetRotation, dt, attackProgress);

        float momentumDecay = 1f - (float) Math.exp(-6.5f * dt);
        momentumYaw *= (1.0f - momentumDecay);
        momentumPitch *= (1.0f - momentumDecay);
        finalRotation = new Vector2f(finalRotation.x + momentumYaw, MathHelper.clamp(finalRotation.y + momentumPitch, -90.0f, 90.0f));

        parentModule.setLerpRotation(finalRotation);
        applyRotation(new Rotation(finalRotation.x, finalRotation.y), 130, 45, 1, 3);
        return finalRotation;
    }

    private void updatePredefinedJitter(long nowNs) {
        long timeSinceLastUpdate = (nowNs - lastJitterUpdateTime) / 1_000_000;

        if (timeSinceLastUpdate > 5 + ThreadLocalRandom.current().nextInt(5)) {
            lastJitterUpdateTime = nowNs;
            patternIndex = (patternIndex + 1) % JITTER_PATTERN.length;
            float[] newTarget = JITTER_PATTERN[patternIndex];
            targetJitterOffset = new Vector2f(newTarget[0] * 0.8f, newTarget[1] * 0.8f);
        }

        float smoothFactor = 0.01f;
        float newX = currentJitterOffset.x + (targetJitterOffset.x - currentJitterOffset.x) * smoothFactor;
        float newY = currentJitterOffset.y + (targetJitterOffset.y - currentJitterOffset.y) * smoothFactor;
        currentJitterOffset = new Vector2f(newX, newY);
    }

    private Vector2f interpolateRotation(Vector2f current, Vector2f target, float dt, float attackProgress) {
        float yawDiff = MathHelper.wrapDegrees(target.x - current.x);
        float pitchDiff = target.y - current.y;
        float totalDiff = (float) Math.hypot(yawDiff, pitchDiff);
        if (totalDiff < 0.01f) return current;

        float absYawDiff = Math.abs(yawDiff);
        float absPitchDiff = Math.abs(pitchDiff);
        float yawPitchBalance = (absYawDiff > absPitchDiff) ? (0.6f + ThreadLocalRandom.current().nextFloat() * 0.25f) : (0.3f + ThreadLocalRandom.current().nextFloat() * 0.25f);

        float baseSpeed = 10.0f + ThreadLocalRandom.current().nextFloat() * 8.5f;
        float dynamicMultiplier = 1.0f + Math.min(totalDiff * 0.055f, 7.5f);

        float yawSpeedTarget = baseSpeed * dynamicMultiplier * (absYawDiff / totalDiff);
        float pitchSpeedTarget = baseSpeed * dynamicMultiplier * (absPitchDiff / totalDiff);

        if (absYawDiff > absPitchDiff) pitchSpeedTarget *= yawPitchBalance;
        else yawSpeedTarget *= yawPitchBalance;

        float aimFactor = (attackProgress > 0.75f || isPerformingHighPitchFlick) ? 1.0f : 0.45f;
        float speedVariation = 0.85f + ThreadLocalRandom.current().nextFloat() * 0.3f;
        yawSpeedTarget *= speedVariation * hesitationSlowdown * fatigueMultiplier * (inPostAttack ? 0.5f : 1.0f) * aimFactor;
        pitchSpeedTarget *= speedVariation * hesitationSlowdown * fatigueMultiplier * (inPostAttack ? 0.5f : 1.0f) * aimFactor;

        float speedSmoothFactor = 1.0f - (float) Math.exp(-5.0f * dt);
        float yawSpeed = lastYawSpeed + (yawSpeedTarget - lastYawSpeed) * speedSmoothFactor;
        float pitchSpeed = lastPitchSpeed + (pitchSpeedTarget - lastPitchSpeed) * speedSmoothFactor;
        lastYawSpeed = yawSpeed;
        lastPitchSpeed = pitchSpeed;

        float yawMaxDelta = yawSpeed * dt * 60.0f * (0.85f + ThreadLocalRandom.current().nextFloat() * 0.3f);
        float pitchMaxDelta = pitchSpeed * dt * 60.0f * (0.85f + ThreadLocalRandom.current().nextFloat() * 0.3f);

        float yawChange = MathHelper.clamp(yawDiff, -yawMaxDelta, yawMaxDelta);
        float pitchChange = MathHelper.clamp(pitchDiff, -pitchMaxDelta, pitchMaxDelta);

        if (ThreadLocalRandom.current().nextFloat() < 0.035f) {
            float stutter = 1.0f - ThreadLocalRandom.current().nextFloat() * 0.5f;
            yawChange *= stutter;
            pitchChange *= stutter;
        }

        float microAdjustment = 0.90f + ThreadLocalRandom.current().nextFloat() * 0.2f;
        yawChange *= microAdjustment;
        pitchChange *= microAdjustment;

        momentumYaw += yawChange * (0.05f + ThreadLocalRandom.current().nextFloat() * 0.04f);
        momentumPitch += pitchChange * (0.05f + ThreadLocalRandom.current().nextFloat() * 0.04f);

        return new Vector2f(current.x + yawChange, MathHelper.clamp(current.y + pitchChange, -90.0f, 90.0f));
    }

    private void updateHumanizedEffects(float dt) {
        if (!isHesitating && ThreadLocalRandom.current().nextFloat() < 0.003f) {
            isHesitating = true;
            hesitationTicks = 0;
        }
        if (isHesitating) {
            hesitationTicks++;
            if (hesitationTicks < ThreadLocalRandom.current().nextInt(2, 8)) {
                hesitationSlowdown = 0.3f + ThreadLocalRandom.current().nextFloat() * 0.4f;
            } else {
                isHesitating = false;
                hesitationSlowdown = 1.0f;
            }
        }
    }

    private void updateFatigue() {
        if (attackCounter > 0) {
            fatigueMultiplier = 1.0f - Math.min(attackCounter * 0.007f, 0.14f);
            attackCounter--;
        } else {
            fatigueMultiplier = 1.0f;
        }
    }

    private void initiatePostAttack() {
        if (inPostAttack || isPerformingHighPitchFlick) return;

        double rand = ThreadLocalRandom.current().nextDouble();

        if (rand < 0.05) {
            isPerformingHighPitchFlick = true;
        } else if (rand < 0.35) {
            inPostAttack = true;
            postAttackTicks = 0;
            postAttackDuration = ThreadLocalRandom.current().nextInt(15, 25);
            attackCounter += ThreadLocalRandom.current().nextInt(5, 10);
            float angle = 10f + ThreadLocalRandom.current().nextFloat() * 5f;
            postAttackYawDrift = (ThreadLocalRandom.current().nextBoolean() ? 1f : -1f) * angle;
            postAttackPitchDrift = (ThreadLocalRandom.current().nextBoolean() ? 1f : -1f) * angle;
        } else {
            inPostAttack = true;
            postAttackTicks = 0;
            postAttackDuration = ThreadLocalRandom.current().nextInt(4, 10);
            attackCounter += ThreadLocalRandom.current().nextInt(7, 14);
            float driftStrength = 0.2f + ThreadLocalRandom.current().nextFloat() * 0.5f;
            postAttackYawDrift = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2.0f * driftStrength;
            postAttackPitchDrift = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 1.5f * driftStrength;
        }
    }

    private void updatePostAttack(float dt) {
        postAttackTicks++;
        if (postAttackTicks >= postAttackDuration) {
            inPostAttack = false;
            postAttackYawDrift = 0.0f;
            postAttackPitchDrift = 0.0f;
            return;
        }
        float decayFactor = (1.0f - ((float) postAttackTicks / postAttackDuration));
        postAttackYawDrift *= decayFactor * 0.92f;
        postAttackPitchDrift *= decayFactor * 0.92f;
    }

    private void updateRotationPhase(float attackProgress, float distance) {
        if (inPostAttack || isPerformingHighPitchFlick) currentPhase = RotationPhase.RECOVERING;
        else if (attackProgress > 0.85f) currentPhase = RotationPhase.ATTACKING;
        else if (attackProgress > 0.5f || distance < 2.5) currentPhase = RotationPhase.LOCKED;
        else if (attackProgress > 0.2f) currentPhase = RotationPhase.APPROACHING;
        else currentPhase = RotationPhase.IDLE;
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        if (mc.player != null) {
            parentModule.setLerpRotation(new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch));
        }
        this.lastNs = 0L;
        this.currentPhase = RotationPhase.IDLE;
        this.lastYawSpeed = 0.0f;
        this.lastPitchSpeed = 0.0f;
        this.overshootYaw = 0.0f;
        this.overshootPitch = 0.0f;
        this.hasOvershot = false;
        this.momentumYaw = 0.0f;
        this.momentumPitch = 0.0f;
        this.hesitationTicks = 0;
        this.isHesitating = false;
        this.hesitationSlowdown = 1.0f;
        this.postAttackYawDrift = 0.0f;
        this.postAttackPitchDrift = 0.0f;
        this.postAttackTicks = 0;
        this.postAttackDuration = 0;
        this.inPostAttack = false;
        this.isPerformingHighPitchFlick = false;
        this.preAttackTension = 0.0f;
        this.preAttackTicks = 0;
        this.attackCounter = 0;
        this.fatigueMultiplier = 1.0f;
        this.patternIndex = 0;
        this.lastJitterUpdateTime = 0;
        this.currentJitterOffset = Vector2f.ZERO;
        this.targetJitterOffset = Vector2f.ZERO;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
    }
}