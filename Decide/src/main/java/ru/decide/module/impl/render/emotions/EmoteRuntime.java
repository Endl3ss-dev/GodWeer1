/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_591
 *  net.minecraft.class_630
 *  net.minecraft.class_746
 */
package ru.decide.module.impl.render.emotions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_591;
import net.minecraft.class_630;
import net.minecraft.class_746;
import ru.decide.module.impl.render.emotions.EmoteCatalog;
import ru.decide.module.impl.render.emotions.EmotePose;
import ru.decide.module.impl.render.emotions.EmoteTiming;
import ru.decide.module.impl.render.emotions.LegacyEmotion;

@Environment(value=EnvType.CLIENT)
public final class EmoteRuntime {
    private static JsonObject rig;
    private static EmotePose active;
    private static LegacyEmotion activeLegacy;
    private static double activeDurationTicks;
    private static double clock;
    private static double x;
    private static double y;
    private static double z;
    private static long lastTick;
    private static int activeId;
    private static Object level;
    private static boolean initialized;
    public static boolean cancelOnMove;
    private static final float FADE_IN_MS = 280.0f;
    private static final float FADE_OUT_MS = 320.0f;
    private static final String[] BONE_NAMES;
    private static final WeakHashMap<class_591, float[][]> previous;

    private EmoteRuntime() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        try {
            rig = EmoteRuntime.resource("rig.json");
        }
        catch (Exception e) {
            System.err.println("[DecideVisual Emotions] rig: " + e.getMessage());
        }
    }

    private static JsonObject resource(String name) throws Exception {
        try (InputStream s = EmoteRuntime.class.getResourceAsStream("/assets/decide/emotes/" + name);){
            if (s == null) {
                throw new IllegalStateException(name);
            }
            JsonObject jsonObject = JsonParser.parseReader((Reader)new InputStreamReader(s, StandardCharsets.UTF_8)).getAsJsonObject();
            return jsonObject;
        }
    }

    public static boolean playing() {
        return active != null || activeLegacy != null;
    }

    public static int activeId() {
        return activeId;
    }

    public static void stop() {
        active = null;
        activeLegacy = null;
        activeDurationTicks = 0.0;
        clock = 0.0;
        activeId = -1;
    }

    public static boolean play(EmoteCatalog.Entry entry) {
        LegacyEmotion legacy;
        if (entry == null) {
            return false;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return false;
        }
        LegacyEmotion legacyEmotion = legacy = entry.isLegacy() ? LegacyEmotion.byId(entry.id()) : null;
        if (!entry.isLegacy()) {
            EmoteRuntime.initialize();
            if (rig == null) {
                return false;
            }
        }
        try {
            if (legacy != null) {
                active = null;
                activeLegacy = legacy;
                activeDurationTicks = legacy.durationTicks();
            } else {
                active = EmotePose.parse(rig, EmoteRuntime.resource(entry.id() + ".json"));
                activeLegacy = null;
                activeDurationTicks = EmoteRuntime.active.duration;
            }
            clock = 0.0;
            lastTick = System.nanoTime();
            activeId = entry.id();
            x = mc.field_1724.method_23317();
            y = mc.field_1724.method_23318();
            z = mc.field_1724.method_23321();
            level = mc.field_1687;
            return true;
        }
        catch (Exception e) {
            EmoteRuntime.stop();
            System.err.println("[DecideVisual Emotions] emote " + entry.id() + ": " + e.getMessage());
            return false;
        }
    }

    public static void tick() {
        boolean moved;
        class_310 mc = class_310.method_1551();
        if (active == null && activeLegacy == null) {
            return;
        }
        if (mc.field_1724 == null || mc.field_1687 != level) {
            EmoteRuntime.stop();
            return;
        }
        if (mc.method_1493()) {
            return;
        }
        class_746 p = mc.field_1724;
        boolean looping = active != null && EmoteRuntime.active.loop;
        boolean bl = moved = cancelOnMove && (!p.method_5805() || p.method_6115() || p.method_5681() || p.method_6128() || EmoteRuntime.sq(p.method_23317() - x) + EmoteRuntime.sq(p.method_23321() - z) > 4.0E-4 || Math.abs(p.method_23318() - y) > 0.02);
        if (moved) {
            EmoteRuntime.stop();
            return;
        }
        clock += 1.0;
        lastTick = System.nanoTime();
        if (!looping && activeDurationTicks > 0.0 && clock >= activeDurationTicks) {
            EmoteRuntime.stop();
            return;
        }
        x = p.method_23317();
        y = p.method_23318();
        z = p.method_23321();
    }

    private static double sq(double v) {
        return v * v;
    }

    private static class_630[] parts(class_591 m) {
        return new class_630[]{m.field_3398, m.field_3391, m.field_3401, m.field_27433, m.field_3392, m.field_3397};
    }

    public static void restore(class_591 model) {
        float[][] s = previous.remove(model);
        if (s == null) {
            return;
        }
        class_630[] p = EmoteRuntime.parts(model);
        for (int i = 0; i < p.length; ++i) {
            p[i].field_3657 = s[i][0];
            p[i].field_3656 = s[i][1];
            p[i].field_3655 = s[i][2];
            p[i].field_3654 = s[i][3];
            p[i].field_3675 = s[i][4];
            p[i].field_3674 = s[i][5];
            p[i].field_37938 = s[i][6];
            p[i].field_37939 = s[i][7];
            p[i].field_37940 = s[i][8];
        }
    }

    public static void apply(class_591 model, class_10055 state) {
        class_310 mc = class_310.method_1551();
        if (active == null && activeLegacy == null || mc.field_1724 == null || state.field_53528 != mc.field_1724.method_5628() || state.field_53333 || state.field_53542) {
            return;
        }
        if (activeLegacy != null) {
            EmoteRuntime.applyLegacy(model, state, mc);
            return;
        }
        try {
            int i;
            class_630[] parts = EmoteRuntime.parts(model);
            float[][] saved = new float[parts.length][9];
            for (int i2 = 0; i2 < parts.length; ++i2) {
                class_630 p = parts[i2];
                saved[i2] = new float[]{p.field_3657, p.field_3656, p.field_3655, p.field_3654, p.field_3675, p.field_3674, p.field_37938, p.field_37939, p.field_37940};
            }
            previous.put(model, saved);
            double frameTick = EmoteTiming.frame(clock, lastTick, System.nanoTime(), mc.method_1493());
            float weight = EmoteTiming.intro(frameTick);
            double[][] poses = active.pose(frameTick);
            for (i = 0; i < parts.length; ++i) {
                int idx = active.bone(BONE_NAMES[i]);
                if (idx < 0) continue;
                double[] d = poses[idx];
                class_630 p = parts[i];
                double wx = (double)p.field_3657 / 16.0;
                double wy = 1.5 - (double)p.field_3656 / 16.0;
                double wz = (double)(-p.field_3655) / 16.0;
                double tx = d[0] * wx + d[1] * wy + d[2] * wz + d[3];
                double ty = d[4] * wx + d[5] * wy + d[6] * wz + d[7];
                double tz = d[8] * wx + d[9] * wy + d[10] * wz + d[11];
                p.field_3657 = (float)(tx * 16.0);
                p.field_3656 = (float)((1.5 - ty) * 16.0);
                p.field_3655 = (float)(-tz * 16.0);
                double sx = Math.sqrt(d[0] * d[0] + d[4] * d[4] + d[8] * d[8]);
                double sy = Math.sqrt(d[1] * d[1] + d[5] * d[5] + d[9] * d[9]);
                double sz = Math.sqrt(d[2] * d[2] + d[6] * d[6] + d[10] * d[10]);
                sx = Math.max(sx, 1.0E-8);
                sy = Math.max(sy, 1.0E-8);
                sz = Math.max(sz, 1.0E-8);
                p.field_37938 = (float)sx;
                p.field_37939 = (float)sy;
                p.field_37940 = (float)sz;
                double ry = Math.asin(Math.max(-1.0, Math.min(1.0, d[8] / sx)));
                p.field_3675 = (float)ry;
                p.field_3654 = (float)Math.atan2(d[9] / sy, d[10] / sz);
                p.field_3674 = (float)Math.atan2(-d[4] / sx, d[0] / sx);
            }
            for (i = 0; i < parts.length; ++i) {
                class_630 p = parts[i];
                float[] a = saved[i];
                p.field_3657 = a[0] + (p.field_3657 - a[0]) * weight;
                p.field_3656 = a[1] + (p.field_3656 - a[1]) * weight;
                p.field_3655 = a[2] + (p.field_3655 - a[2]) * weight;
                p.field_3654 = EmoteTiming.angle(a[3], p.field_3654, weight);
                p.field_3675 = EmoteTiming.angle(a[4], p.field_3675, weight);
                p.field_3674 = EmoteTiming.angle(a[5], p.field_3674, weight);
                p.field_37938 = a[6] + (p.field_37938 - a[6]) * weight;
                p.field_37939 = a[7] + (p.field_37939 - a[7]) * weight;
                p.field_37940 = a[8] + (p.field_37940 - a[8]) * weight;
            }
            EmoteRuntime.copyIfIndependent(model.field_3398, model.field_3394);
            EmoteRuntime.copyIfIndependent(model.field_3391, model.field_3483);
            EmoteRuntime.copyIfIndependent(model.field_3401, model.field_3486);
            EmoteRuntime.copyIfIndependent(model.field_27433, model.field_3484);
            EmoteRuntime.copyIfIndependent(model.field_3392, model.field_3479);
            EmoteRuntime.copyIfIndependent(model.field_3397, model.field_3482);
        }
        catch (RuntimeException e) {
            EmoteRuntime.stop();
            EmoteRuntime.restore(model);
            System.err.println("[DecideVisual Emotions] pose stopped: " + e.getMessage());
        }
    }

    private static void applyLegacy(class_591 model, class_10055 state, class_310 mc) {
        class_1657 pe;
        class_1297 class_12972;
        long now = System.nanoTime();
        double frameTick = EmoteTiming.frame(clock, lastTick, now, mc.method_1493());
        long elapsedMs = (long)(frameTick * 50.0);
        float b = EmoteRuntime.legacyBlend(elapsedMs, activeLegacy.durationMs());
        if (b <= 0.001f) {
            return;
        }
        class_1657 player = mc.field_1687 != null && (class_12972 = mc.field_1687.method_8469(state.field_53528)) instanceof class_1657 ? (pe = (class_1657)class_12972) : null;
        boolean hands = EmoteRuntime.canAnimateHands(player);
        class_630 head = model.field_3398;
        class_630 hat = model.field_3394;
        class_630 body = model.field_3391;
        class_630 rightArm = model.field_3401;
        class_630 leftArm = model.field_27433;
        class_630 rightLeg = model.field_3392;
        class_630 leftLeg = model.field_3397;
        switch (activeLegacy) {
            case DEB: {
                EmoteRuntime.poseDeb(b, hands, head, rightArm, leftArm);
                break;
            }
            case FLOSS: {
                EmoteRuntime.poseFloss(b, elapsedMs, hands, body, rightArm, leftArm, rightLeg, leftLeg);
                break;
            }
            case MASTURBATE: {
                EmoteRuntime.poseMasturbate(b, elapsedMs, hands, player, body, rightArm, leftArm, rightLeg, leftLeg);
                break;
            }
            case HELLO: {
                EmoteRuntime.poseHello(b, elapsedMs, hands, rightArm);
                break;
            }
            case GET_GRIDDY: {
                EmoteRuntime.poseGriddy(b, elapsedMs, hands, head, body, rightArm, leftArm, rightLeg, leftLeg);
                break;
            }
            case HAPPY: {
                EmoteRuntime.poseHappy(b, hands, rightArm, leftArm);
                break;
            }
            case SIX_SEVEN: {
                EmoteRuntime.poseSixSeven(b, elapsedMs, hands, rightArm, leftArm);
            }
        }
        hat.field_3654 = head.field_3654;
        hat.field_3675 = head.field_3675;
        hat.field_3674 = head.field_3674;
    }

    private static float legacyBlend(long elapsedMs, long durationMs) {
        double left = Math.max(0.0, (double)(durationMs - elapsedMs));
        float in = class_3532.method_15363((float)((float)elapsedMs / 280.0f), (float)0.0f, (float)1.0f);
        float out = class_3532.method_15363((float)((float)(left / 320.0)), (float)0.0f, (float)1.0f);
        float v = Math.min(in, out);
        return v * v * (3.0f - 2.0f * v);
    }

    private static void poseDeb(float b, boolean hands, class_630 head, class_630 rightArm, class_630 leftArm) {
        if (hands) {
            EmoteRuntime.armXZ(rightArm, b, -0.7f, -2.0f);
            EmoteRuntime.armXZ(leftArm, b, -0.7f, -2.0f);
        }
        head.field_3654 = class_3532.method_16439((float)b, (float)head.field_3654, (float)0.7f);
        head.field_3675 = class_3532.method_16439((float)b, (float)head.field_3675, (float)0.5f);
        head.field_3674 = class_3532.method_16439((float)b, (float)head.field_3674, (float)0.0f);
    }

    private static void poseFloss(float b, long t, boolean hands, class_630 body, class_630 rightArm, class_630 leftArm, class_630 rightLeg, class_630 leftLeg) {
        float action = EmoteRuntime.tri(t, 250L);
        float rightSide = EmoteRuntime.tri(t, 500L);
        float leftSide = EmoteRuntime.tri(t + 250L, 500L);
        if (hands) {
            EmoteRuntime.armXZ(rightArm, b, 0.5f - rightSide, 1.0f - 2.0f * action);
            EmoteRuntime.armXZ(leftArm, b, 0.5f - leftSide, 1.5f - 2.0f * action);
        }
        body.field_3674 = class_3532.method_16439((float)b, (float)body.field_3674, (float)(0.3f * action - 0.15f));
        rightLeg.field_3657 = class_3532.method_16439((float)b, (float)rightLeg.field_3657, (float)(-0.5f - 3.0f * action));
        leftLeg.field_3657 = class_3532.method_16439((float)b, (float)leftLeg.field_3657, (float)(3.0f - 2.5f * action));
    }

    private static void poseHello(float b, long t, boolean hands, class_630 rightArm) {
        if (!hands) {
            return;
        }
        float wave = EmoteRuntime.tri(t, 250L);
        EmoteRuntime.armXZ(rightArm, b, -0.2f, 2.75f - 0.25f * wave);
    }

    private static void poseHappy(float b, boolean hands, class_630 rightArm, class_630 leftArm) {
        if (!hands) {
            return;
        }
        EmoteRuntime.armXZ(rightArm, b, 0.0f, 2.75f);
        EmoteRuntime.armXZ(leftArm, b, 0.0f, -2.75f);
    }

    private static void poseSixSeven(float b, long elapsedMs, boolean hands, class_630 rightArm, class_630 leftArm) {
        if (!hands) {
            return;
        }
        float phase = EmoteRuntime.tri(elapsedMs, 150L);
        float rightUp = EmoteRuntime.tri(elapsedMs + 75L, 150L);
        float leftUp = EmoteRuntime.tri(elapsedMs, 150L);
        EmoteRuntime.armXZ(rightArm, b, rightUp * 2.5f - 0.3f, 0.0f);
        EmoteRuntime.armXZ(leftArm, b, leftUp * 2.5f - 0.3f, 0.0f);
    }

    private static void poseMasturbate(float b, long t, boolean hands, class_1657 player, class_630 body, class_630 rightArm, class_630 leftArm, class_630 rightLeg, class_630 leftLeg) {
        boolean sneaking;
        float action = EmoteRuntime.tri(t, 100L);
        boolean bl = sneaking = player != null && player.method_5715();
        if (!sneaking) {
            if (hands) {
                EmoteRuntime.armXZ(rightArm, b, -0.3f - 0.7f * action, -0.55f);
                EmoteRuntime.armXZ(leftArm, b, 0.3f, 1.0f);
            }
            body.field_3654 = class_3532.method_16439((float)b, (float)body.field_3654, (float)-0.1f);
            rightLeg.field_3655 = class_3532.method_16439((float)b, (float)rightLeg.field_3655, (float)-1.0f);
            leftLeg.field_3655 = class_3532.method_16439((float)b, (float)leftLeg.field_3655, (float)-1.0f);
        } else if (hands) {
            EmoteRuntime.armXZ(rightArm, b, 0.7f - 0.7f * action, -0.55f);
        }
    }

    private static void poseGriddy(float b, long t, boolean hands, class_630 head, class_630 body, class_630 rightArm, class_630 leftArm, class_630 rightLeg, class_630 leftLeg) {
        float action = EmoteRuntime.tri(t, 350L);
        float leg = EmoteRuntime.tri(t, 300L);
        float eye = EmoteRuntime.griddyEye(t);
        if (hands) {
            float armX = 1.0f - 1.5f * action - 3.5f * eye;
            EmoteRuntime.armXZ(rightArm, b, armX, 0.5f - 0.75f * action);
            EmoteRuntime.armXZ(leftArm, b, armX, -0.5f + 0.75f * action);
        }
        body.field_3654 = class_3532.method_16439((float)b, (float)body.field_3654, (float)(0.2f - 0.05f * action));
        rightLeg.field_3655 = class_3532.method_16439((float)b, (float)rightLeg.field_3655, (float)(2.0f - 0.5f * action));
        leftLeg.field_3655 = class_3532.method_16439((float)b, (float)leftLeg.field_3655, (float)(2.0f - 0.5f * action));
        rightLeg.field_3654 = class_3532.method_16439((float)b, (float)rightLeg.field_3654, (float)(0.3f * leg - 0.3f));
        leftLeg.field_3654 = class_3532.method_16439((float)b, (float)leftLeg.field_3654, (float)(-0.3f * leg - 0.1f));
        head.field_3675 = class_3532.method_16439((float)b, (float)head.field_3675, (float)(-0.4f + 0.4f * eye));
        head.field_3654 = class_3532.method_16439((float)b, (float)head.field_3654, (float)(-0.3f * eye));
        head.field_3674 = class_3532.method_16439((float)b, (float)head.field_3674, (float)0.0f);
    }

    private static void armXZ(class_630 arm, float blend, float xRot, float zRot) {
        float a = class_3532.method_15363((float)blend, (float)0.0f, (float)1.0f);
        arm.field_3654 = class_3532.method_16439((float)a, (float)arm.field_3654, (float)xRot);
        arm.field_3674 = class_3532.method_16439((float)a, (float)arm.field_3674, (float)zRot);
    }

    private static float tri(long elapsed, long halfMs) {
        if (halfMs <= 0L) {
            return 0.0f;
        }
        long cycle = halfMs * 2L;
        long m = elapsed % cycle;
        return m < halfMs ? (float)m / (float)halfMs : 2.0f - (float)m / (float)halfMs;
    }

    private static float griddyEye(long elapsed) {
        long cycle = elapsed % 3100L;
        if (cycle < 2100L) {
            return 0.0f;
        }
        return EmoteRuntime.tri(cycle - 2100L, 500L);
    }

    private static boolean canAnimateHands(class_1657 target) {
        class_310 mc = class_310.method_1551();
        return mc.field_1724 == null || target == null || target != mc.field_1724 || !mc.field_1690.method_31044().method_31034();
    }

    private static void copyIfIndependent(class_630 core, class_630 outer) {
        for (class_630 child : core.method_32088()) {
            if (child != outer) continue;
            return;
        }
        outer.field_3657 = core.field_3657;
        outer.field_3656 = core.field_3656;
        outer.field_3655 = core.field_3655;
        outer.field_3654 = core.field_3654;
        outer.field_3675 = core.field_3675;
        outer.field_3674 = core.field_3674;
        outer.field_37938 = core.field_37938;
        outer.field_37939 = core.field_37939;
        outer.field_37940 = core.field_37940;
    }

    static {
        activeId = -1;
        cancelOnMove = true;
        BONE_NAMES = new String[]{"head", "low_body", "right_arm", "left_arm", "right_leg", "left_leg"};
        previous = new WeakHashMap();
    }
}

