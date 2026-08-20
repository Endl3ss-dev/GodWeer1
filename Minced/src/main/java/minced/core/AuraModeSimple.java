/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.core;

import minced.core.RotationManager;
import minced.core.AuraModeHandler;
import minced.core.AimRotation;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AuraModeSimple
implements AuraModeHandler {
    static float cfr_renamed_257;
    static float cfr_renamed_258;
    static float cfr_renamed_259;
    static float cfr_renamed_260;
    static float cfr_renamed_261;
    static int cfr_renamed_138;
    static int cfr_renamed_180;
    static float cfr_renamed_262;
    static float cfr_renamed_263;
    static float cfr_renamed_264;
    static float cfr_renamed_265;

    static MinecraftClient I = MinecraftClient.getInstance();
    static float field10447;
    static float field10448;
    boolean field10449;
    float field10450;
    float field10451;
    static float field10452;
    static float field10453;
    long field10454 = 0L;
    long field10455 = 0L;
    float field10456 = 0.0f;
    float field10457 = 0.0f;
    float field10458 = 1.0f;
    float field10459 = 1.0f;
    long l = 0L;
    float field10460 = 0.0f;
    float field10461 = 0.0f;
    static double field10462;
    static double field10463;
    static float field10464;
    static float field10465;
    static double field10466;
    static double field10467;
    static double field10468;
    static double field10469;
    boolean field10470;
    static String field10471 = "Builder";
    static float field10472;
    static float field10473;
    static double field10474;
    static double field10475;
    static double field10476;
    static double field10477;
    static long field10478;
    static double field10479;
    static float field10480;
    static float field10481;
    static float field10482;
    static float field10483;
    static double field10484;
    static float field10485;
    static float field10486;
    static float field10487;
    static float field10488;
    static long field10489;
    static double field10490;
    static float field10491;
    static float field10492;
    static float field10493;
    static float field10494;
    static float field10495;
    static float field10496;
    static float field10497;
    static float field10498;
    static float field10499;
    static float field10500;
    static double field10501;
    static float field10502;
    static float field10503;
    static float field10504;
    static float field10505;
    static float field10506;
    static float field10507;
    static float field10508;
    static float field10509;
    static float field10510;
    static float field10511;
    static float field10512;
    static float field10513;
    static double field10514;
    static double field10515;
    static double field10516;
    static double field10517;

    private void method3025(float param1, float param2) {
        param1 = AuraModeSimple.method3035(AuraModeSimple.I.player.getYaw(), param1);
        param2 = MathHelper.clamp((float)AuraModeSimple.method3035(AuraModeSimple.I.player.getPitch(), param2), (float)field10447, (float)field10448);
        AuraModeSimple.I.player.setYaw(param1);
        AuraModeSimple.I.player.setPitch(param2);
        AuraModeSimple.I.player.setHeadYaw(param1);
        AuraModeSimple.I.player.setBodyYaw(param1);
        RotationManager.method252(param1);
        RotationManager.method257(param2);
    }

    private void method3026(AttackAura param1) {
        if (AuraModeSimple.I.player == null || !((Boolean)param1.method873().getValue()).booleanValue() || !this.field10449) {
            return;
        }
        AuraModeSimple.I.player.setYaw(this.field10450);
        AuraModeSimple.I.player.setPitch(MathHelper.clamp((float)this.field10451, (float)field10452, (float)field10453));
        RotationManager.method252(AuraModeSimple.I.player.getYaw());
        RotationManager.method257(AuraModeSimple.I.player.getPitch());
        AimRotation v2 = AimRotation.method357();
        if (v2 != null) {
            v2.method370();
        }
    }

    @Override
    public void reset(AttackAura param1) {
        this.method3026(param1);
        this.method3030();
    }

    private static float method3028(float param0, float param1) {
        return param0 + 0.80275816f * (param1 - param0);
    }

    private Vec3d method3029(LivingEntity param1, float param2) {
        Vec3d v3 = AuraModeSimple.I.player.getEyePos();
        Vec3d v4 = param1.getPos();
        double v5 = param1.getHeight();
        Vec3d v7 = new Vec3d(v4.x, v4.y + v5 * field10462, v4.z);
        Vec3d v8 = v7.subtract(v3);
        float v9 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v8.z, v8.x)) - field10463));
        float v10 = MathHelper.wrapDegrees((float)(v9 - param2));
        float v11 = MathHelper.clamp((float)(Math.abs(v10) / field10464), (float)0.0f, (float)1.0f);
        if (v11 <= field10465) {
            return v8;
        }
        Vec3d v12 = new Vec3d(v8.x, 0.0, v8.z);
        if (v12.lengthSquared() > field10466) {
            v12 = v12.normalize();
        }
        Vec3d v13 = new Vec3d(-v12.z, 0.0, v12.x);
        double v14 = v10 > 0.0f ? field10467 : 1.0;
        double v16 = (double)param1.getWidth() * field10468;
        Vec3d v18 = new Vec3d(v7.x + v13.x * v16 * v14, v4.y + v5 * field10469, v7.z + v13.z * v16 * v14);
        Vec3d v19 = v18.subtract(v3);
        return new Vec3d(MathHelper.lerp((double)v11, (double)v8.x, (double)v19.x), MathHelper.lerp((double)v11, (double)v8.y, (double)v19.y), MathHelper.lerp((double)v11, (double)v8.z, (double)v19.z));
    }

    private void method3030() {
        this.field10454 = 0L;
        this.field10456 = 0.0f;
        this.field10457 = 0.0f;
        this.l = 0L;
        this.field10470 = false;
        this.field10449 = false;
    }

    @Override
    public String getName() {
        return field10471;
    }

    private void method3032() {
        this.field10454 = 0L;
        this.field10456 = 0.0f;
        this.field10457 = 0.0f;
        this.field10458 = this.field10459 = AuraModeSimple.method3028(field10472, field10473);
        this.l = 0L;
        this.field10460 = (float)(Math.random() * field10474 * field10475);
        this.field10461 = (float)(Math.random() * field10476 * field10477);
        this.field10455 = System.currentTimeMillis() + field10478 + (long)(Math.random() * field10479);
    }

    @Override
    public void updateRotation(AttackAura param1, LivingEntity param2) {
        float v20;
        float v19;
        float v17;
        AimRotation v3;
        if (AuraModeSimple.I.player == null || param2 == null) {
            return;
        }
        if (!this.field10470) {
            this.method3032();
            this.field10470 = true;
        }
        if ((v3 = AimRotation.method357()) != null) {
            v3.method370();
        }
        long v4 = System.nanoTime();
        float v6 = this.field10454 == 0L ? field10480 : (float)(v4 - this.field10454) / field10481;
        this.field10454 = v4;
        v6 = MathHelper.clamp((float)v6, (float)field10482, (float)field10483);
        float v7 = AuraModeSimple.I.player.getYaw();
        float v8 = AuraModeSimple.I.player.getPitch();
        long v9 = System.currentTimeMillis();
        if (v9 >= this.l) {
            this.field10459 = Math.random() < field10484 ? AuraModeSimple.method3028(field10485, field10486) : AuraModeSimple.method3028(field10487, field10488);
            this.l = v9 + field10489 + (long)(Math.random() * field10490);
        }
        this.field10458 += (this.field10459 - this.field10458) * MathHelper.clamp((float)(field10491 * v6), (float)0.0f, (float)1.0f);
        if (v9 < this.field10455) {
            this.field10456 *= Math.max(0.0f, 1.0f - field10492 * v6);
            this.field10457 *= Math.max(0.0f, 1.0f - field10493 * v6);
            this.method3025(v7 + this.field10456 * v6, MathHelper.clamp((float)(v8 + this.field10457 * v6), (float)field10494, (float)field10495));
            return;
        }
        Vec3d v11 = this.method3029(param2, v7);
        this.field10460 += v6 * field10496;
        this.field10461 += v6 * field10497;
        float v12 = (float)Math.sin(this.field10460) * field10498;
        float v13 = (float)Math.sin(this.field10461) * field10499 * field10500;
        float v14 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v11.z, v11.x)) - field10501)) + v12;
        float v15 = MathHelper.clamp((float)((float)(-Math.toDegrees(Math.atan2(v11.y, Math.hypot(v11.x, v11.z)))) + v13), (float)field10502, (float)field10503);
        float v16 = MathHelper.wrapDegrees((float)(v14 - v7));
        float v18 = (float)Math.sqrt(v16 * v16 + (v17 = v15 - v8) * v17);
        if (v18 > field10504) {
            v19 = field10505 * this.field10458;
            v20 = Math.min(v19, v18 * field10506);
            float v22 = v16 * v18 * v20;
            float v23 = v17 * v18 * v20 * field10507;
            float v24 = MathHelper.clamp((float)(field10508 * v6), (float)0.0f, (float)1.0f);
            this.field10456 += (v22 - this.field10456) * v24;
            this.field10457 += (v23 - this.field10457) * v24 * field10509;
        } else {
            this.field10456 *= Math.max(0.0f, 1.0f - field10510 * v6);
            this.field10457 *= Math.max(0.0f, 1.0f - field10511 * v6);
        }
        v19 = v7 + this.field10456 * v6;
        v20 = MathHelper.clamp((float)(v8 + this.field10457 * v6), (float)field10512, (float)field10513);
        this.method3025(v19, v20);
        this.field10450 = v14;
        this.field10451 = v15;
        this.field10449 = true;
    }

    @Override
    public void onDeactivate(AttackAura param1) {
        this.method3026(param1);
        this.method3030();
    }

    private static float method3035(float param0, float param1) {
        double v2 = (Double)AuraModeSimple.I.options.getMouseSensitivity().getValue() * field10514 + field10515;
        double v4 = v2 * v2 * v2 * field10516 * field10517;
        return (float)((double)param0 + (double)Math.round((double)(param1 - param0) / v4) * v4);
    }

    static {
        cfr_renamed_257 = 0.0f;
        cfr_renamed_258 = 0.0f;
        cfr_renamed_259 = 0.0f;
        cfr_renamed_260 = 0.0f;
        cfr_renamed_261 = 0.0f;
        cfr_renamed_138 = 0;
        cfr_renamed_180 = 0;
        cfr_renamed_262 = 0.0f;
        cfr_renamed_263 = 0.0f;
        cfr_renamed_264 = 0.0f;
        cfr_renamed_265 = 0.0f;
        field10480 = 0.016666668f;
        field10481 = 1.0E9f;
        field10482 = 0.001f;
        field10483 = 0.1f;
        field10484 = 0.12f;
        field10485 = 0.1f;
        field10486 = 0.3f;
        field10487 = 0.75f;
        field10488 = 1.2f;
        field10489 = 250L;
        field10490 = 550.0;
        field10491 = 4.0f;
        field10492 = 6.0f;
        field10493 = 6.0f;
        field10494 = -89.0f;
        field10495 = 89.0f;
        field10496 = 1.5f;
        field10497 = 1.1f;
        field10498 = 0.18f;
        field10499 = 0.18f;
        field10500 = 0.5f;
        field10501 = 90.0;
        field10502 = -89.0f;
        field10503 = 89.0f;
        field10504 = 1.0E-4f;
        field10505 = 140.0f;
        field10506 = 5.5f;
        field10507 = 0.78f;
        field10508 = 10.0f;
        field10509 = 0.85f;
        field10510 = 8.0f;
        field10511 = 8.0f;
        field10512 = -89.0f;
        field10513 = 89.0f;
        field10452 = -89.9f;
        field10453 = 89.9f;
        field10462 = 0.52;
        field10463 = 90.0;
        field10464 = 40.0f;
        field10465 = 0.01f;
        field10466 = 1.0E-6;
        field10467 = -1.0;
        field10468 = 0.32;
        field10469 = 0.78;
        field10514 = 0.6;
        field10515 = 0.2;
        field10516 = 8.0;
        field10517 = 0.15;
        field10447 = -89.0f;
        field10448 = 89.0f;
        field10472 = 0.75f;
        field10473 = 1.2f;
        field10474 = Math.PI;
        field10475 = 2.0;
        field10476 = Math.PI;
        field10477 = 2.0;
        field10478 = 120L;
        field10479 = 160.0;
    }
}



