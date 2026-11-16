/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1792
 *  net.minecraft.class_310
 *  net.minecraft.class_742
 */
package ru.decide.cosmetics.melee;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_310;
import net.minecraft.class_742;
import ru.decide.cosmetics.melee.BakedMeleeModel;
import ru.decide.cosmetics.melee.MeleeClip;
import ru.decide.cosmetics.melee.MeleeClipPlayer;
import ru.decide.cosmetics.melee.MeleePlayMode;
import ru.decide.cosmetics.melee.MeleePreloader;
import ru.decide.cosmetics.melee.MeleeRegistry;
import ru.decide.cosmetics.melee.MeleeSkin;
import ru.decide.cosmetics.melee.MeleeVariant;

@Environment(value=EnvType.CLIENT)
public final class MeleeController {
    private static final MeleeController INSTANCE = new MeleeController();
    private static final MeleeController OFF_HAND = new MeleeController();
    private static final String CLIP_STATIC_IDLE = "static_idle";
    private static final String CLIP_DRAW = "draw";
    private static final String CLIP_DRAW_ALTERNATE = "draw_1";
    private static final String CLIP_INSPECT = "inspect";
    private static final String CLIP_INSPECT_ALTERNATE = "inspect_1";
    private static final String CLIP_MELEE_PREFIX = "melee_";
    private static final String CLIP_MELEE_SECONDARY = "melee_3";
    private static final String CLIP_RUN = "run";
    private static final String CLIP_RUN_START = "run_start";
    private static final String CLIP_RUN_END = "run_end";
    private static final float TRANSITION_ACTION = 0.2f;
    private static final float TRANSITION_RUN_START = 0.2f;
    private static final float TRANSITION_RUN_LOOP = 0.2f;
    private static final float TRANSITION_RUN_END = 0.3f;
    private static final float MAX_DELTA_SECONDS = 0.25f;
    private static final float NANOS_PER_SECOND = 1.0E9f;
    private static final float RUN_CYCLE_DISTANCE = 2.0f;
    private static final int PRIMARY_ATTACK_VARIANTS = 2;
    private static final int RUN_MODE_IDLE = -1;
    private static final int RUN_MODE_CYCLE = 0;
    private final MeleeClipPlayer baseTrack = new MeleeClipPlayer();
    private final MeleeClipPlayer mainTrack = new MeleeClipPlayer();
    private final MeleeClipPlayer movementTrack = new MeleeClipPlayer();
    private BakedMeleeModel model;
    private String modelKey;
    private boolean renderedSinceTick;
    private MeleeVariant variant = MeleeVariant.DEFAULT;
    private float[] staticPose = new float[0];
    private float[] additivePose = new float[0];
    private float[] staticFade = new float[0];
    private float[] additiveFade = new float[0];
    private float[] composedPose = new float[0];
    private boolean[] hidden = new boolean[0];
    private float staticFadeRemaining;
    private float staticFadeDuration;
    private float additiveFadeRemaining;
    private float additiveFadeDuration;
    private MainState mainState = MainState.START;
    private MovementState movementState = MovementState.IDLE;
    private int attackIndex;
    private int runMode = -1;
    private float runAnchor;
    private long lastNanos;
    private class_1792 heldItem;
    private int heldSlot = -1;
    private double previousX;
    private double previousZ;
    private boolean hasPrevious;
    private float walkDistance;

    private MeleeController() {
    }

    public static MeleeController getInstance() {
        return INSTANCE;
    }

    public static MeleeController offHand() {
        return OFF_HAND;
    }

    public static void resetAll() {
        INSTANCE.reset();
        OFF_HAND.reset();
    }

    public void reset() {
        this.model = null;
        this.modelKey = null;
        this.baseTrack.clear();
        this.mainTrack.clear();
        this.movementTrack.clear();
        this.mainState = MainState.START;
        this.movementState = MovementState.IDLE;
        this.runMode = -1;
        this.attackIndex = 0;
        this.heldItem = null;
        this.heldSlot = -1;
        this.staticFadeRemaining = 0.0f;
        this.additiveFadeRemaining = 0.0f;
        this.lastNanos = 0L;
        this.hasPrevious = false;
        this.walkDistance = 0.0f;
    }

    public void stop() {
        if (this.model != null) {
            this.reset();
        }
    }

    public void restart() {
        this.mainState = MainState.START;
        this.movementState = MovementState.IDLE;
        this.runMode = -1;
        this.mainTrack.clear();
        this.movementTrack.clear();
        this.staticFadeRemaining = 0.0f;
        this.additiveFadeRemaining = 0.0f;
    }

    public float[] pose() {
        return this.composedPose;
    }

    public boolean[] hidden() {
        return this.hidden;
    }

    public boolean consumeRendered() {
        boolean rendered = this.renderedSinceTick;
        this.renderedSinceTick = false;
        return rendered;
    }

    public BakedMeleeModel prepare(class_742 player, class_1268 hand) {
        MeleeSkin skin = MeleeRegistry.equipped();
        if (skin == null || player == null) {
            return null;
        }
        BakedMeleeModel baked = MeleePreloader.get(skin);
        if (baked == null) {
            return null;
        }
        this.renderedSinceTick = true;
        if (baked != this.model || !skin.getKey().equals(this.modelKey)) {
            this.bind(skin, baked);
        } else if (this.heldChanged(player, hand)) {
            this.restart();
        }
        this.advance(player, this.delta());
        this.compose();
        return this.model;
    }

    public void onAttack(boolean primary) {
        if (this.model == null || this.mainState != MainState.IDLE) {
            return;
        }
        if (primary) {
            String clip = this.variantName(CLIP_MELEE_PREFIX + (this.attackIndex + 1));
            this.attackIndex = (this.attackIndex + 1) % 2;
            this.playMain(clip, 0.2f);
        } else {
            this.playMain(this.variantName(CLIP_MELEE_SECONDARY), 0.2f);
        }
    }

    public void onInspect() {
        if (this.model != null && this.mainState == MainState.IDLE) {
            String alternate = this.variantName(CLIP_INSPECT_ALTERNATE);
            boolean useAlternate = this.model.hasClip(alternate) && ThreadLocalRandom.current().nextBoolean();
            this.playMain(useAlternate ? alternate : this.variantName(CLIP_INSPECT), 0.2f);
        }
    }

    private boolean heldChanged(class_742 player, class_1268 hand) {
        int slot;
        class_1792 item = player.method_5998(hand).method_7909();
        int n = slot = hand == class_1268.field_5808 ? player.method_31548().method_67532() : 40;
        if (item == this.heldItem && slot == this.heldSlot) {
            return false;
        }
        this.heldItem = item;
        this.heldSlot = slot;
        return true;
    }

    private void bind(MeleeSkin skin, BakedMeleeModel baked) {
        this.heldItem = null;
        this.heldSlot = -1;
        this.model = baked;
        this.modelKey = skin.getKey();
        int size = baked.boneCount() * 9;
        this.staticPose = new float[size];
        this.additivePose = new float[size];
        this.staticFade = new float[size];
        this.additiveFade = new float[size];
        this.composedPose = new float[size];
        this.hidden = new boolean[baked.boneCount()];
        List<MeleeVariant> variants = skin.getVariants();
        this.variant = variants.isEmpty() ? MeleeVariant.DEFAULT : variants.get(ThreadLocalRandom.current().nextInt(variants.size()));
        baked.markBranches(this.variant.hiddenBones(), this.hidden);
        this.restart();
        this.baseTrack.play(baked.clip(this.variantName(CLIP_STATIC_IDLE)), MeleePlayMode.LOOP);
        this.lastNanos = 0L;
    }

    private String variantName(String clip) {
        String suffix = this.variant.suffix();
        if (suffix.isEmpty()) {
            return clip;
        }
        String candidate = clip + suffix;
        return this.model != null && this.model.hasClip(candidate) ? candidate : clip;
    }

    private float delta() {
        long now = System.nanoTime();
        if (this.lastNanos == 0L) {
            this.lastNanos = now;
            return 0.0f;
        }
        float dt = (float)(now - this.lastNanos) / 1.0E9f;
        this.lastNanos = now;
        if (class_310.method_1551().method_1493()) {
            return 0.0f;
        }
        return Math.max(0.0f, Math.min(dt, 0.25f));
    }

    private void advance(class_742 player, float delta) {
        this.baseTrack.advance(delta);
        this.mainTrack.advance(delta);
        this.movementTrack.advance(delta);
        this.staticFadeRemaining = Math.max(0.0f, this.staticFadeRemaining - delta);
        this.additiveFadeRemaining = Math.max(0.0f, this.additiveFadeRemaining - delta);
        this.updateMovement(player);
        this.updateMain();
    }

    private void updateMain() {
        if (this.mainState == MainState.START) {
            String alternate = this.variantName(CLIP_DRAW_ALTERNATE);
            boolean useAlternate = this.model.hasClip(alternate) && ThreadLocalRandom.current().nextBoolean();
            this.playMain(useAlternate ? alternate : this.variantName(CLIP_DRAW), 0.0f);
            this.mainState = MainState.IDLE;
        }
    }

    private void updateMovement(class_742 player) {
        boolean wantsRun;
        double x = player.method_23317();
        double z = player.method_23321();
        if (this.hasPrevious) {
            this.walkDistance += (float)Math.hypot(x - this.previousX, z - this.previousZ);
        }
        this.previousX = x;
        this.previousZ = z;
        this.hasPrevious = true;
        boolean bl = wantsRun = player.method_5624() && this.mainTrack.isStopped();
        if (this.movementState == MovementState.IDLE) {
            if (!wantsRun) {
                return;
            }
            if (!this.model.hasClip(this.variantName(CLIP_RUN_START))) {
                return;
            }
            this.movementState = MovementState.RUN;
            this.runMode = -1;
            this.playMovement(this.variantName(CLIP_RUN_START), MeleePlayMode.ONCE_HOLD, 0.2f);
            return;
        }
        if (!wantsRun) {
            this.movementState = MovementState.IDLE;
            this.runMode = -1;
            this.playMovement(this.variantName(CLIP_RUN_END), MeleePlayMode.ONCE_HOLD, 0.3f);
            return;
        }
        if (this.runMode == 0) {
            if (!player.method_24828()) {
                return;
            }
            float cycle = (this.walkDistance - this.runAnchor) % 2.0f;
            if (cycle < 0.0f) {
                cycle += 2.0f;
            }
            this.movementTrack.setProgress(cycle / 2.0f);
            return;
        }
        if (this.movementTrack.isHolding()) {
            this.runMode = 0;
            this.runAnchor = this.walkDistance;
            this.playMovement(this.variantName(CLIP_RUN), MeleePlayMode.LOOP, 0.2f);
        }
    }

    private void playMain(String clip, float transition) {
        MeleeClip value = this.model.clip(clip);
        if (value == null) {
            return;
        }
        this.beginStaticFade(transition);
        this.mainTrack.play(value, MeleePlayMode.ONCE_STOP);
    }

    private void playMovement(String clip, MeleePlayMode mode, float transition) {
        MeleeClip value = this.model.clip(clip);
        if (value == null) {
            this.movementTrack.clear();
            return;
        }
        this.beginAdditiveFade(transition);
        this.movementTrack.play(value, mode);
    }

    private void beginStaticFade(float duration) {
        if (duration <= 0.0f) {
            this.staticFadeRemaining = 0.0f;
            return;
        }
        System.arraycopy(this.staticPose, 0, this.staticFade, 0, this.staticPose.length);
        this.staticFadeDuration = duration;
        this.staticFadeRemaining = duration;
    }

    private void beginAdditiveFade(float duration) {
        if (duration <= 0.0f) {
            this.additiveFadeRemaining = 0.0f;
            return;
        }
        System.arraycopy(this.additivePose, 0, this.additiveFade, 0, this.additivePose.length);
        this.additiveFadeDuration = duration;
        this.additiveFadeRemaining = duration;
    }

    private void compose() {
        MeleeController.fillNeutral(this.staticPose);
        this.baseTrack.evaluate(this.staticPose);
        if (this.mainTrack.isActive()) {
            this.mainTrack.evaluate(this.staticPose);
        }
        MeleeController.blend(this.staticPose, this.staticFade, this.staticFadeRemaining, this.staticFadeDuration);
        MeleeController.fillNeutral(this.additivePose);
        if (this.movementTrack.isActive()) {
            this.movementTrack.evaluate(this.additivePose);
        }
        MeleeController.blend(this.additivePose, this.additiveFade, this.additiveFadeRemaining, this.additiveFadeDuration);
        for (int bone = 0; bone < this.composedPose.length; bone += 9) {
            this.composedPose[bone] = this.staticPose[bone] + this.additivePose[bone];
            this.composedPose[bone + 1] = this.staticPose[bone + 1] + this.additivePose[bone + 1];
            this.composedPose[bone + 2] = this.staticPose[bone + 2] + this.additivePose[bone + 2];
            this.composedPose[bone + 3] = this.staticPose[bone + 3] + this.additivePose[bone + 3];
            this.composedPose[bone + 4] = this.staticPose[bone + 4] + this.additivePose[bone + 4];
            this.composedPose[bone + 5] = this.staticPose[bone + 5] + this.additivePose[bone + 5];
            this.composedPose[bone + 6] = this.staticPose[bone + 6] * this.additivePose[bone + 6];
            this.composedPose[bone + 7] = this.staticPose[bone + 7] * this.additivePose[bone + 7];
            this.composedPose[bone + 8] = this.staticPose[bone + 8] * this.additivePose[bone + 8];
        }
    }

    private static void blend(float[] pose, float[] from, float remaining, float duration) {
        if (remaining <= 0.0f || duration <= 0.0f) {
            return;
        }
        float factor = 1.0f - remaining / duration;
        for (int i = 0; i < pose.length; ++i) {
            float origin = from[i];
            pose[i] = origin + (pose[i] - origin) * factor;
        }
    }

    private static void fillNeutral(float[] pose) {
        Arrays.fill(pose, 0.0f);
        for (int i = 6; i < pose.length; i += 9) {
            pose[i] = 1.0f;
            pose[i + 1] = 1.0f;
            pose[i + 2] = 1.0f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static enum MainState {
        START,
        IDLE;

    }

    @Environment(value=EnvType.CLIENT)
    private static enum MovementState {
        IDLE,
        RUN;

    }
}

