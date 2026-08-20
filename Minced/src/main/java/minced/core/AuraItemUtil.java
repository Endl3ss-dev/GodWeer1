/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.registry.tag.ItemTags
 */
package minced.core;

import java.util.concurrent.ThreadLocalRandom;
import minced.core.TpsUtil;
import minced.core.AuraModeHandler;
import minced.core.AuraRotationState;
import minced.core.ClientState;
import minced.core.Rotation;
import minced.core.BlockUtil;
import minced.core.AimRotation;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.ItemTags;

public final class AuraItemUtil
implements AuraModeHandler {
    static long щ;
    static long б;
    static long о;
    static float нй;
    static float нП;

    static MinecraftClient I = MinecraftClient.getInstance();
    static float field10807;
    static float field10808;
    static float field10809;
    static float field10810;
    long field10811;
    AttackAura field10812;
    boolean field10813 = true;
    int field10814 = 17;
    boolean field10815;
    float field10816;
    int field10817;
    boolean field10818;
    int field10819;
    long field10820;
    ClientState field10821;
    AuraRotationState field10822 = new AuraRotationState();
    float field10823;
    static float field10824;
    static float field10825;
    static long field10826;
    static long field10827;
    static long field10828;

    public void method3074(AttackAura param1, LivingEntity param2) {
        if (AuraItemUtil.I.player == null || param2 == null) {
            return;
        }
        this.method3076(param1);
        this.method3097(param1, param2);
        Rotation v3 = this.method3100(param1, param2);
        AimRotation.method376(v3, field10807, field10808, field10809, field10810, 1, 6, false);
    }

    public long method3075() {
        return this.field10811;
    }

    public void method3076(AttackAura param1) {
        this.field10812 = param1;
    }

    public boolean method3077() {
        return this.field10813;
    }

    public void method3078() {
        this.method3084();
    }

    public int method3079() {
        return this.field10814;
    }

    private static double method3080(AttackAura param0) {
        float v1 = param0.method909();
        if (AuraItemUtil.I.player != null && AuraItemUtil.I.player.isGliding()) {
            v1 += ((Float)param0.method794().getValue()).floatValue();
        }
        return v1 + ((Float)param0.method890().getValue()).floatValue();
    }

    public boolean method3081() {
        return this.field10815;
    }

    public float method3082() {
        return this.field10816;
    }

    public boolean method3083() {
        return AuraItemUtil.I.player != null && !AuraItemUtil.I.player.isGliding();
    }

    public void method3084() {
        this.field10817 = 0;
        this.field10815 = false;
        this.field10818 = false;
        this.field10813 = true;
        this.field10819 = 0;
        this.field10814 = 17;
        this.field10811 = 0L;
        this.field10820 = 0L;
        this.field10821.method3114();
        this.field10822.method3047();
        if (AuraItemUtil.I.player != null) {
            this.field10823 = AuraItemUtil.I.player.getYaw();
            this.field10816 = AuraItemUtil.I.player.getPitch();
        }
    }

    private boolean method3085(AttackAura param1) {
        if (AuraItemUtil.I.player == null) {
            return false;
        }
        if (!AuraItemUtil.I.player.getMainHandStack().isIn(ItemTags.SWORDS)) {
            return true;
        }
        float v2 = param1.method840();
        return AuraItemUtil.I.player.getAttackCooldownProgress(v2) >= field10824;
    }

    private long method3086(AttackAura param1, long param2) {
        long v4 = param2;
        if (((Boolean)param1.cfr_renamed_267().getValue()).booleanValue()) {
            float v6 = Math.max(TpsUtil.method2599(), 1.0f);
            v4 = (long)((float)v4 * (field10825 / v6));
        }
        return v4;
    }

    public long method3087() {
        return this.field10820;
    }

    public void method3088(AttackAura param1) {
        long v2 = this.method3086(param1, field10826);
        long v4 = System.currentTimeMillis();
        this.field10811 = v4 + v2;
        this.field10820 = this.field10811 - field10827;
    }

    public int method3089() {
        return this.field10819;
    }

    private boolean method3090() {
        return AuraItemUtil.I.player != null && (AuraItemUtil.I.player.isTouchingWater() || AuraItemUtil.I.player.isSubmergedInWater() || AuraItemUtil.I.player.isInLava());
    }

    public boolean method3091() {
        return this.field10818;
    }

    public boolean method3092(AttackAura param1) {
        if (System.currentTimeMillis() < this.field10811) {
            return false;
        }
        return this.method3085(param1);
    }

    public int method3093() {
        return this.field10817;
    }

    public AuraItemUtil() {
        this.field10821 = new ClientState();
    }

    private boolean method3094(AttackAura param1) {
        boolean v3;
        if (AuraItemUtil.I.player == null) {
            return false;
        }
        if (!this.method3085(param1)) {
            return false;
        }
        if (this.method3090()) {
            return true;
        }
        boolean v2 = AuraItemUtil.I.player.isClimbing() || AuraItemUtil.I.player.hasVehicle() || AuraItemUtil.I.player.getAbilities().flying;
        boolean bl = v3 = !AuraItemUtil.I.options.jumpKey.isPressed() && AuraItemUtil.I.player.isOnGround() && (Boolean)param1.method826().getValue() != false;
        if (!v2) {
            return v3 || !AuraItemUtil.I.player.isOnGround() || AuraItemUtil.I.player.fallDistance > 0.0f;
        }
        return false;
    }

    public AuraRotationState method3095() {
        return this.field10822;
    }

    public boolean method3096(LivingEntity param1) {
        if (param1 == null || AuraItemUtil.I.player == null || this.field10812 == null) {
            return false;
        }
        if (this.method3090()) {
            return false;
        }
        boolean v2 = (Boolean)this.field10812.method908().getValue() != false && AuraItemUtil.I.player.isUsingItem();
        return !this.field10818 && this.field10815 || v2;
    }

    private void method3097(AttackAura param1, LivingEntity param2) {
        long v3 = System.currentTimeMillis();
        boolean bl = this.field10818 = v3 >= this.field10820 && !this.field10815;
        this.field10817 = v3 >= this.field10820 || this.field10813 ? (this.field10818 ? 1 : 0) : 0;
        this.field10815 = this.field10821.method3113(field10828);
        if (this.method3094(param1)) {
            this.field10821.method3114();
        }
        if (AuraRotationState.field10766 && AuraRotationState.field10767 == 0L) {
            AuraRotationState.field10767 = v3;
        }
    }

    public ClientState method3098() {
        return this.field10821;
    }

    public void method3099(AttackAura param1) {
        if (!this.field10813) {
            ++this.field10819;
        }
        int v2 = 17;
        if (this.field10819 > 17 && !this.field10813) {
            this.field10819 = 0;
            this.field10814 = ThreadLocalRandom.current().nextInt(15, 19);
        }
        if (this.field10819 > 1) {
            v2 = this.field10814;
        }
        AuraRotationState.field10766 = this.field10819 > v2 - 1 && !this.field10813;
        this.field10813 = false;
        AuraRotationState.field10768 = new Rotation(this.field10823, this.field10816);
        this.field10821.method3114();
    }

    public Rotation method3100(AttackAura param1, LivingEntity param2) {
        this.field10822.method3072(this, param2);
        if (AuraItemUtil.I.player != null && this.field10813) {
            this.field10823 = AuraItemUtil.I.player.getYaw();
            this.field10816 = AuraItemUtil.I.player.getPitch();
        }
        Rotation v3 = new Rotation(this.field10823, this.field10816);
        Rotation v4 = this.field10822.method3048(v3, param2);
        this.field10823 = v4.method3140();
        this.field10816 = v4.method3136();
        return v4;
    }

    public boolean method3101(AttackAura param1, LivingEntity param2) {
        if (param2 == null || AuraItemUtil.I.player == null) {
            return false;
        }
        if (param1.method781(AuraItemUtil.method3080(param1))) {
            return true;
        }
        float v3 = this.field10823;
        float v4 = this.field10816;
        double v5 = AuraItemUtil.method3080(param1);
        if (this.method3090()) {
            if (BlockUtil.method3570(param2)) {
                return true;
            }
            return BlockUtil.method3566((Entity)param2, v5, v3, v4);
        }
        if (!(((Boolean)param1.method821().getValue()).booleanValue() || BlockUtil.method3569((Entity)param2) && !BlockUtil.method3567((Entity)param2))) {
            return false;
        }
        return BlockUtil.method3566((Entity)param2, v5, v3, v4);
    }

    public float method3102() {
        return this.field10823;
    }

    public AttackAura method3103() {
        return this.field10812;
    }

    static {
        щ = 0L;
        б = 0L;
        о = 0L;
        нй = 0.0f;
        нП = 0.0f;
        field10807 = 180.0f;
        field10808 = 180.0f;
        field10809 = 180.0f;
        field10810 = 180.0f;
        field10828 = 395L;
        field10826 = 500L;
        field10827 = 30L;
        field10824 = 0.5f;
        field10825 = 20.0f;
    }
}



