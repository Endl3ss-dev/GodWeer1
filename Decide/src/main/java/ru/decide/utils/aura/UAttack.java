/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1743
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2596
 *  net.minecraft.class_2846
 *  net.minecraft.class_2846$class_2847
 *  net.minecraft.class_2868
 *  net.minecraft.class_2886
 *  net.minecraft.class_310
 *  net.minecraft.class_3486
 *  net.minecraft.class_3532
 *  net.minecraft.class_5134
 */
package ru.decide.utils.aura;

import java.security.SecureRandom;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2596;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_2886;
import net.minecraft.class_310;
import net.minecraft.class_3486;
import net.minecraft.class_3532;
import net.minecraft.class_5134;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.aura.AttackUtil;
import ru.decide.utils.aura.AuraUtil;
import ru.decide.utils.aura.RayTraceUtil;
import ru.decide.utils.aura.StopWatch;

@Environment(value=EnvType.CLIENT)
public final class UAttack
implements IMinecraft {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final class_310 mc = class_310.method_1551();
    public static long hitCounterCPSBypass;
    private static final StopWatch cooldownTimer;
    private static boolean missDetected;
    private static int counterTo0PostMissHits;

    public static void hitCounterCPSBypassNext() {
        ++hitCounterCPSBypass;
    }

    public static void hitCounterCPSBypassReset() {
        hitCounterCPSBypass = 0L;
    }

    public static boolean cpsBypassTrigger() {
        return hitCounterCPSBypass % 7L == 3L;
    }

    public static class_1657 getSelf() {
        return UAttack.mc.field_1724;
    }

    public static class_1937 getWorld() {
        return UAttack.mc.field_1687;
    }

    public static float applyGaussianJitter(float rotation) {
        float strength = 0.2f;
        return (float)((double)rotation + (secureRandom.nextGaussian() * (double)0.2f * 2.0 - (double)0.2f));
    }

    public static boolean randomBoolean(int chance010) {
        return (float)secureRandom.nextInt(chance010 + 1) >= 1.0f * (1.0f / Math.max((float)chance010, 1.0f));
    }

    public static boolean randomBoolean() {
        return secureRandom.nextInt(2) == 1;
    }

    public static float randomFloat(float min, float max) {
        return secureRandom.nextFloat(min, max);
    }

    public static float randomFloat() {
        return UAttack.randomFloat(0.0f, 1.0f);
    }

    public static int randomInt1PosibleOrNot() {
        return UAttack.randomBoolean() ? 1 : -1;
    }

    public static int getAxeSlot() {
        if (UAttack.mc.field_1724 == null) {
            return -1;
        }
        for (int i = 0; i < 9; ++i) {
            if (!(UAttack.mc.field_1724.method_31548().method_5438(i).method_7909() instanceof class_1743)) continue;
            return i;
        }
        return -1;
    }

    public static Runnable[] hitShieldBreakTaskForUse(class_1309 livingIn, boolean enabled) {
        Runnable[] pre$post = new Runnable[]{() -> {}, () -> {}};
        if (!enabled || UAttack.mc.field_1724 == null) {
            return pre$post;
        }
        if (livingIn instanceof class_1657) {
            class_1792 offItem;
            class_1657 player = (class_1657)livingIn;
            if (!player.method_6039()) {
                return pre$post;
            }
            class_1799 main = player.method_6047();
            class_1799 off = player.method_6079();
            class_1792 mainItem = main.method_7960() ? null : main.method_7909();
            class_1792 class_17922 = offItem = off.method_7960() ? null : off.method_7909();
            if (mainItem == class_1802.field_8255 || offItem == class_1802.field_8255) {
                int axeSlot = UAttack.getAxeSlot();
                int handSlot = UAttack.mc.field_1724.method_31548().method_67532();
                if (axeSlot != -1 && axeSlot != handSlot) {
                    pre$post[0] = () -> {
                        if (mc.method_1562() != null) {
                            mc.method_1562().method_52787((class_2596)new class_2868(axeSlot));
                        }
                    };
                    pre$post[1] = () -> {
                        if (mc.method_1562() != null) {
                            mc.method_1562().method_52787((class_2596)new class_2868(handSlot));
                        }
                    };
                }
            }
        }
        return pre$post;
    }

    public static Runnable[] resetShieldSilentTaskForUse(boolean enabled) {
        Runnable[] pre$post = new Runnable[]{() -> {}, () -> {}};
        if (!enabled || UAttack.mc.field_1724 == null) {
            return pre$post;
        }
        if (UAttack.mc.field_1724.method_6039()) {
            class_1268 active = UAttack.mc.field_1724.method_6058();
            if (active == null) {
                return pre$post;
            }
            pre$post[0] = () -> mc.method_1562().method_52787((class_2596)new class_2846(class_2846.class_2847.field_12974, class_2338.field_10980, class_2350.field_11033));
            pre$post[1] = () -> mc.method_1562().method_52787((class_2596)new class_2886(active, 0, UAttack.mc.field_1724.method_36454(), UAttack.mc.field_1724.method_36455()));
        }
        return pre$post;
    }

    public static boolean isBestMomentToHit(boolean fallCheck) {
        if (UAttack.mc.field_1724 == null) {
            return true;
        }
        if (UAttack.mc.field_1724.method_6047().method_7909() == class_1802.field_49814) {
            return true;
        }
        if (!fallCheck) {
            return true;
        }
        if (AttackUtil.isPlayerInCriticalState()) {
            return true;
        }
        boolean isCritState = AttackUtil.isPlayerInCriticalState();
        boolean skipFallCheck = isCritState || AttackUtil.hasMovementRestrictions();
        return skipFallCheck;
    }

    public static boolean useEntity(class_1309 livingIn, Runnable preHit, Runnable postHit, class_1268 hand, boolean cpsBypass) {
        if (preHit != null) {
            preHit.run();
        }
        if (livingIn != null && UAttack.mc.field_1761 != null && UAttack.mc.field_1724 != null) {
            UAttack.mc.field_1761.method_2918((class_1657)UAttack.mc.field_1724, (class_1297)livingIn);
            if (hand != null) {
                UAttack.mc.field_1724.method_6104(hand);
            }
            if (cpsBypass) {
                UAttack.hitCounterCPSBypassNext();
            } else {
                UAttack.hitCounterCPSBypassReset();
            }
            cooldownTimer.reset();
        }
        if (postHit != null) {
            postHit.run();
        }
        return livingIn != null;
    }

    public static long getMsCooldown() {
        if (UAttack.mc.field_1724 == null) {
            return 500L;
        }
        double attackSpeed = UAttack.mc.field_1724.method_45325(class_5134.field_23723);
        float maxDeviation = 0.2f;
        long msCooldown = (long)(1.0 / attackSpeed * 1000.0 * (double)(1.0f - Math.min(maxDeviation, 1.0f)));
        if (attackSpeed == 4.0 || attackSpeed == 4.4000000059604645 || attackSpeed == 4.800000011920929) {
            msCooldown = 450L;
        }
        msCooldown = Math.max(msCooldown, 450L);
        return msCooldown;
    }

    public static boolean msCooldownReached(long msOffset) {
        return cooldownTimer.finished(UAttack.getMsCooldown() + msOffset);
    }

    public static boolean msCooldownReached() {
        return UAttack.msCooldownReached(0L);
    }

    public static boolean msCooldownHasMs(long ms) {
        return cooldownTimer.finished(ms);
    }

    public static float msCooldownPC01() {
        return Math.min((float)cooldownTimer.elapsedTime() / (float)UAttack.getMsCooldown(), 1.0f);
    }

    public static float msCooldownReach() {
        return cooldownTimer.elapsedTime();
    }

    public static boolean anyEntityOnRay(class_1309 livingIn, double range) {
        return livingIn != null && RayTraceUtil.rayTraceEntity(class_3532.method_15393((float)UAttack.mc.field_1724.method_36454()), UAttack.mc.field_1724.method_36455(), (float)range, (class_1297)livingIn);
    }

    public static boolean shouldAttack(class_1309 livingTarget, boolean rayCast, boolean distanceCheck, boolean fallCheck, long cooldownMSOffset, float[] ranges) {
        if (distanceCheck && livingTarget != null && !AuraUtil.validDistance((class_1297)livingTarget, ranges[0], true)) {
            return false;
        }
        if (!UAttack.msCooldownReached(cooldownMSOffset)) {
            return false;
        }
        boolean validNext = UAttack.isBestMomentToHit(fallCheck);
        if (validNext && rayCast && !UAttack.anyEntityOnRay(livingTarget, ranges[0])) {
            validNext = false;
        }
        return validNext;
    }

    public static boolean shouldAttack(class_1309 livingTarget, boolean rayCast, boolean fallCheck, long cooldownMSOffset, float[] ranges) {
        return UAttack.shouldAttack(livingTarget, rayCast, true, fallCheck, cooldownMSOffset, ranges);
    }

    public static boolean resetSprintTickP(class_1309 targetIn, float[] ranges) {
        return targetIn != null && UAttack.shouldAttack(targetIn, false, false, -20L, ranges) && !UAttack.mc.field_1724.method_24828() && !UAttack.mc.field_1724.method_5777(class_3486.field_15517) && UAttack.mc.field_1724.method_18798().field_1351 <= 0.0030162615090425808;
    }

    public static boolean resetSprintTick(class_1309 targetIn, float[] ranges) {
        return targetIn != null && UAttack.shouldAttack(targetIn, false, false, -50L, ranges) && !UAttack.mc.field_1724.method_24828() && !UAttack.mc.field_1724.method_5777(class_3486.field_15517) && UAttack.mc.field_1724.method_18798().field_1351 <= 0.0030162615090425808;
    }

    private static int maxHitsCountOnMiss() {
        return 3;
    }

    public static void antiMissesHittingReset() {
        missDetected = false;
        counterTo0PostMissHits = 0;
    }

    public static void antiMissesHittingUpdate(class_1309 targetIn, boolean cpsBypass, boolean rayCastCheck, boolean enabled) {
        if (targetIn == null || counterTo0PostMissHits == 0 || !enabled || targetIn.field_6235 != 0) {
            UAttack.antiMissesHittingReset();
        }
        if (enabled && targetIn != null && UAttack.msCooldownHasMs(UAttack.cpsBypassTrigger() ? 250L : 150L) && UAttack.mc.field_1724.field_6252) {
            if (!missDetected && counterTo0PostMissHits == 0 && targetIn.field_6235 == 0) {
                missDetected = true;
                counterTo0PostMissHits = UAttack.maxHitsCountOnMiss();
            }
            if (missDetected && counterTo0PostMissHits > 0 && targetIn != null && (!rayCastCheck || UAttack.anyEntityOnRay(targetIn, 6.0)) && UAttack.useEntity(targetIn, () -> {}, () -> {}, class_1268.field_5808, cpsBypass)) {
                --counterTo0PostMissHits;
            }
        }
    }

    @Generated
    private UAttack() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @Generated
    public static StopWatch getCooldownTimer() {
        return cooldownTimer;
    }

    static {
        cooldownTimer = new StopWatch();
    }
}

