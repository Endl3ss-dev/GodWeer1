/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Vector2f
 *  sg.ec.ТЕ$2
 */
package minced.core;

import minced.module.movement.Speed;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;
import minced.core.RotationTargetUtil;
import minced.core.NeuroSampler;
import minced.core.NeuroProfileStorage;
import minced.core.InterpolationUtil;
import minced.core.AngleUtil;
import minced.core.RotationManager;
import minced.core.EasingUtil;
import minced.core.NeuroPoint;
import minced.core.AuraModeHandler;
import minced.core.Rotation;
import minced.core.AimRotation;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import sg.ec.te;

public class AuraModeNeuro
implements AuraModeHandler {
    static int ШМ;
    static long cfr_renamed_268;
    static double cfr_renamed_240;
    static double cfr_renamed_168;
    static int Шп;
    static int ШО;

    int field11044 = -1;
    long field11045;
    static int field11046;
    int field11047 = field11046;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field11048;
    static double field11049;
    static double field11050;
    static double field11051;
    static float field11052;
    static float field11053;
    static double field11054;
    static double field11055;
    static double field11056;
    boolean field11057;
    float field11058;
    float field11059;
    float field11060;
    float field11061;
    float field11062;
    float field11063;
    static String field11064;
    static String field11065;
    static String field11066;
    static float field11067;
    static float field11068;
    static float field11069;
    static float field11070;
    static double field11071;
    static String field11072;
    static float field11073;
    static float field11074;
    static String field11075;
    static float field11076;
    static float field11077;
    static String field11078;
    boolean field11079;
    float field11080;
    float field11081;
    static float field11082;
    static float field11083;
    static int field11084;
    static float field11085;
    static float field11086;
    static double field11087;
    static float field11088;
    static float field11089;
    static float field11090;
    static float field11091;
    static float field11092;
    static float field11093;
    static float field11094;
    static float field11095;
    static float field11096;
    static float field11097;
    static float field11098;
    static float field11099;
    static float field11100;
    static float field11101;
    static float field11102;
    static float field11103;
    static float field11104;
    static float field11105;
    static float field11106;
    static float field11107;
    static float field11108;
    static String field11109 = "Neuro";
    static float field11110;
    static float field11111;
    static float field11112;
    static float field11113;
    static float field11114;
    static String field11115;
    static double field11116;
    static double field11117;
    static long field11118;
    static double field11119;
    static double field11120;
    static double field11121;
    static double field11122;
    static double field11123;
    static double field11124;
    static float field11125;
    static float field11126;
    static float field11127;
    static float field11128;

    @Override
    public void onActivate(AttackAura param1) {
        this.method3123();
    }

    private void method3117(AttackAura param1) {
        if (!((Boolean)param1.method818().getValue()).booleanValue()) {
            return;
        }
        NeuroProfileStorage.method2290();
        param1.method818().setValue(false);
        this.field11044 = -1;
        this.field11045 = 0L;
        this.method3123();
    }

    @Override
    public void onAttack(AttackAura param1, LivingEntity param2) {
        if (AuraModeNeuro.I.player == null || param2 == null) {
            return;
        }
        NeuroSampler v3 = NeuroProfileStorage.method2292();
        if (v3.method2286() || !param1.method896().method1898(field11048).booleanValue()) {
            return;
        }
        NeuroPoint v4 = this.method3120(param2, AuraModeNeuro.I.player.getYaw(), AuraModeNeuro.I.player.getPitch(), param1.method909() + ((Float)param1.method890().getValue()).floatValue(), field11049);
        if (v4 == null) {
            return;
        }
        v3.hitPoint.addSample(v4.field9281, v4.field9282, v4.field9283);
        this.method3129(v3);
    }

    private Vector2f method3119(Vec3d param1) {
        Vec3d v2 = AuraModeNeuro.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(false));
        Vec3d v3 = param1.subtract(v2);
        if (v3.lengthSquared() < field11050) {
            return null;
        }
        float v4 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v3.z, v3.x)) - field11051));
        float v5 = (float)MathHelper.wrapDegrees((double)(-Math.toDegrees(Math.atan2(v3.y, Math.hypot(v3.x, v3.z)))));
        return new Vector2f(v4, MathHelper.clamp((float)v5, (float)field11052, (float)field11053));
    }

    private NeuroPoint method3120(LivingEntity param1, float param2, float param3, double param4, double param6) {
        Vec3d v8 = AuraModeNeuro.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(false));
        Vec3d v9 = AuraModeNeuro.I.player.getRotationVector(param3, param2);
        Vec3d v10 = v8.add(v9.multiply(param4));
        Box v11 = param1.getBoundingBox();
        Box v12 = v11.expand(param6);
        Vec3d v13 = v12.raycast(v8, v10).orElse(null);
        if (v13 == null) {
            return null;
        }
        double v14 = MathHelper.clamp((double)v13.x, (double)v11.minX, (double)v11.maxX);
        double v16 = MathHelper.clamp((double)v13.y, (double)v11.minY, (double)v11.maxY);
        double v18 = MathHelper.clamp((double)v13.z, (double)v11.minZ, (double)v11.maxZ);
        float v20 = (float)((v14 - v11.minX) / Math.max(v11.getLengthX(), field11054));
        float v21 = (float)((v16 - v11.minY) / Math.max(v11.getLengthY(), field11055));
        float v22 = (float)((v18 - v11.minZ) / Math.max(v11.getLengthZ(), field11056));
        return new NeuroPoint(v20, v21, v22);
    }

    @Override
    public void updateRotation(AttackAura param1, LivingEntity param2) {
        if (AuraModeNeuro.I.player == null || param2 == null) {
            this.method3123();
            return;
        }
        this.method3117(param1);
        NeuroSampler v3 = NeuroProfileStorage.method2292();
        v3.method2285((int)((Float)param1.method844().getValue()).floatValue());
        if (this.field11047 != param2.getId()) {
            this.field11047 = param2.getId();
            this.field11057 = false;
            this.field11058 = 0.0f;
            this.field11059 = 0.0f;
            this.field11060 = 0.0f;
            this.field11061 = 0.0f;
        }
        if (!this.field11057) {
            this.field11062 = AuraModeNeuro.I.player.getYaw();
            this.field11063 = AuraModeNeuro.I.player.getPitch();
            this.field11057 = true;
        }
        this.method3128(v3, param1, param2);
        if (!v3.method2286()) {
            this.method3121(v3, param1, param2);
            this.method3129(v3);
            AimRotation v4 = AimRotation.method357();
            if (v4 != null) {
                v4.method370();
            }
            this.field11062 = AuraModeNeuro.I.player.getYaw();
            this.field11063 = AuraModeNeuro.I.player.getPitch();
            return;
        }
        this.method3124(v3, param1, param2);
        this.field11062 = AuraModeNeuro.I.player.getYaw();
        this.field11063 = AuraModeNeuro.I.player.getPitch();
    }

    @Override
    public boolean isReadyToAttack(AttackAura param1, LivingEntity param2) {
        if (AuraModeNeuro.I.player == null || param2 == null) {
            return false;
        }
        NeuroSampler v3 = NeuroProfileStorage.method2292();
        if (v3.method2286()) {
            return false;
        }
        double v4 = param1.method909() + ((Float)param1.method890().getValue()).floatValue();
        return !AttackAura.method836(AuraModeNeuro.I.player.getYaw(), AuraModeNeuro.I.player.getPitch(), v4, (Entity)param2);
    }

    private void method3121(NeuroSampler param1, AttackAura param2, LivingEntity param3) {
        boolean v14;
        if (!(param2.method896().method1898(field11064).booleanValue() || param2.method896().method1898(field11065).booleanValue() || param2.method896().method1898(field11066).booleanValue())) {
            return;
        }
        Vec3d v4 = this.method3131(param3, param1, true);
        Vector2f v5 = this.method3119(v4);
        if (v5 == null) {
            return;
        }
        float v6 = AuraModeNeuro.I.player.getYaw();
        float v7 = AuraModeNeuro.I.player.getPitch();
        float v8 = MathHelper.wrapDegrees((float)(v6 - this.field11062));
        float v9 = v7 - this.field11063;
        float v10 = MathHelper.wrapDegrees((float)(v5.x - this.field11062));
        float v11 = v5.y - this.field11063;
        float v12 = RotationTargetUtil.method1975();
        boolean v13 = Math.abs(v8) >= v12 * field11067 || Math.abs(v9) >= v12 * field11068;
        boolean bl = v14 = Math.abs(v10) >= v12 * field11069 || Math.abs(v11) >= v12 * field11070;
        if (!v13 || !v14) {
            return;
        }
        InterpolationUtil v15 = param1.behavior;
        EasingUtil v16 = new EasingUtil();
        v16.distance = (float)AuraModeNeuro.I.player.getEyePos().distanceTo(v4);
        v16.yawError = Math.abs(v10);
        v16.pitchError = Math.abs(v11);
        v16.targetSpeed = (float)MathHelper.clamp((double)param2.method732(), (double)0.0, (double)field11071);
        v16.yawGain = param2.method896().method1898(field11072) != false ? MathHelper.clamp((float)(Math.abs(v8) / Math.max(Math.abs(v10), v12)), (float)field11073, (float)field11074) : v15.yawResponse;
        v16.pitchGain = param2.method896().method1898(field11075) != false ? MathHelper.clamp((float)(Math.abs(v9) / Math.max(Math.abs(v11), v12)), (float)field11076, (float)field11077) : v15.pitchResponse;
        v16.yawStepAbs = Math.abs(v8);
        v16.pitchStepAbs = Math.abs(v9);
        v16.rayAligned = AttackAura.method836(this.field11062, this.field11063, param2.method909() + ((Float)param2.method890().getValue()).floatValue(), (Entity)param3);
        if (this.method3126(param1, v16)) {
            return;
        }
        if (param1.method2280(v16) && param2.method896().method1898(field11078).booleanValue()) {
            v15.method2294(v10, v11, v8, v9, this.field11060, this.field11061, v12);
        }
        this.field11060 = v8;
        this.field11061 = v9;
    }

    private void method3122(AttackAura param1) {
        if (AuraModeNeuro.I.player == null || !((Boolean)param1.method868().getValue()).booleanValue() || !this.field11079) {
            return;
        }
        AuraModeNeuro.I.player.setYaw(this.field11080);
        AuraModeNeuro.I.player.setPitch(MathHelper.clamp((float)this.field11081, (float)field11082, (float)field11083));
        RotationManager.method252(AuraModeNeuro.I.player.getYaw());
        RotationManager.method257(AuraModeNeuro.I.player.getPitch());
        AimRotation v2 = AimRotation.method357();
        if (v2 != null) {
            v2.method370();
        }
    }

    private void method3123() {
        this.field11057 = false;
        this.field11047 = field11084;
        this.field11058 = 0.0f;
        this.field11059 = 0.0f;
        this.field11060 = 0.0f;
        this.field11061 = 0.0f;
        this.field11079 = false;
    }

    private void method3124(NeuroSampler param1, AttackAura param2, LivingEntity param3) {
        Vec3d v4 = this.method3131(param3, param1, true);
        Vector2f v5 = this.method3119(v4);
        if (v5 == null) {
            return;
        }
        this.field11080 = v5.x;
        this.field11081 = v5.y;
        this.field11079 = true;
        float v6 = AuraModeNeuro.I.player.getYaw();
        float v7 = AuraModeNeuro.I.player.getPitch();
        float v8 = MathHelper.wrapDegrees((float)(v5.x - v6));
        float v9 = v5.y - v7;
        float v10 = RotationTargetUtil.method1975();
        if (Math.abs(v8) < v10 * field11085 && Math.abs(v9) < v10 * field11086) {
            return;
        }
        boolean v11 = AttackAura.method836(v6, v7, param2.method909() + ((Float)param2.method890().getValue()).floatValue(), (Entity)param3);
        te._2 v12 = this.method3130(param1, Math.abs(v8), Math.abs(v9), (float)AuraModeNeuro.I.player.getEyePos().distanceTo(v4), (float)MathHelper.clamp((double)param2.method732(), (double)0.0, (double)field11087), v11);
        InterpolationUtil v13 = param1.behavior;
        float v14 = MathHelper.lerp((float)field11088, (float)(Math.abs(v8) * v12.нО), (float)v12.на);
        float v15 = MathHelper.lerp((float)field11089, (float)(Math.abs(v9) * v12.нД), (float)v12.нС);
        if (Math.abs(v8) < field11090 && ThreadLocalRandom.current().nextFloat() < v13.holdChance) {
            v14 *= ThreadLocalRandom.current().nextFloat(field11091, field11092);
        }
        if (Math.abs(v9) < field11093 && ThreadLocalRandom.current().nextFloat() < v13.holdChance * field11094) {
            v15 *= ThreadLocalRandom.current().nextFloat(field11095, field11096);
        }
        float v16 = (ThreadLocalRandom.current().nextFloat() - field11097) * 2.0f * v13.yawJitter * (v11 ? field11098 : field11099);
        float v17 = (ThreadLocalRandom.current().nextFloat() - field11100) * 2.0f * v13.pitchJitter * (v11 ? field11101 : field11102);
        float v18 = Math.copySign(v14 + v16, v8);
        float v19 = Math.copySign(v15 + v17, v9);
        float v20 = MathHelper.clamp((float)v13.smoothness, (float)field11103, (float)field11104);
        float v21 = MathHelper.lerp((float)v20, (float)v18, (float)this.field11058);
        float v22 = MathHelper.lerp((float)v20, (float)v19, (float)this.field11059);
        float v23 = Math.abs(v8) + Math.max(v10, v13.yawJitter * field11105);
        float v24 = Math.abs(v9) + Math.max(v10, v13.pitchJitter * field11106);
        v21 = MathHelper.clamp((float)v21, (float)(-v23), (float)v23);
        v22 = MathHelper.clamp((float)v22, (float)(-v24), (float)v24);
        v21 = this.method3127(v21, v8, v10);
        v22 = this.method3127(v22, v9, v10);
        this.field11058 = v21;
        this.field11059 = v22;
        Rotation v25 = new Rotation(v6 + v21, MathHelper.clamp((float)(v7 + v22), (float)field11107, (float)field11108));
        AimRotation.method376(v25, ThreadLocalRandom.current().nextInt(106, 151), ThreadLocalRandom.current().nextInt(96, 147), ThreadLocalRandom.current().nextInt(42, 76), ThreadLocalRandom.current().nextInt(47, 67), 5, 5, false);
    }

    private Vec3d method3125(Box param1, AngleUtil param2) {
        return new Vec3d((double)MathHelper.lerp((float)param2.meanX, (float)((float)param1.minX), (float)((float)param1.maxX)), (double)MathHelper.lerp((float)param2.meanY, (float)((float)param1.minY), (float)((float)param1.maxY)), (double)MathHelper.lerp((float)param2.meanZ, (float)((float)param1.minZ), (float)((float)param1.maxZ)));
    }

    @Override
    public String getName() {
        return field11109;
    }

    private boolean method3126(NeuroSampler param1, EasingUtil param2) {
        ArrayList v3 = param1.samples;
        int v4 = v3.size();
        if (v4 == 0) {
            return false;
        }
        int v5 = Math.min(8, v4);
        for (int v6 = v4 - 1; v6 >= v4 - v5; --v6) {
            EasingUtil v7 = (EasingUtil)v3.get(v6);
            if (v7 == null || v7.rayAligned != param2.rayAligned || Math.abs(v7.yawError - param2.yawError) > field11110 || Math.abs(v7.pitchError - param2.pitchError) > field11111 || Math.abs(v7.distance - param2.distance) > field11112 || Math.abs(v7.yawGain - param2.yawGain) > field11113 || Math.abs(v7.pitchGain - param2.pitchGain) > field11114) continue;
            return true;
        }
        return false;
    }

    private float method3127(float param1, float param2, float param3) {
        if (Math.abs(param2) <= param3) {
            return param2;
        }
        float v4 = Math.max(param3, (float)Math.round(Math.abs(param1) / param3) * param3);
        if (v4 > Math.abs(param2) + param3 * 2.0f) {
            v4 = Math.abs(param2) + param3 * 2.0f;
        }
        return Math.copySign(v4, param2);
    }

    private void method3128(NeuroSampler param1, AttackAura param2, LivingEntity param3) {
        if (param1.method2286() || !param2.method896().method1898(field11115).booleanValue()) {
            return;
        }
        NeuroPoint v4 = this.method3120(param3, AuraModeNeuro.I.player.getYaw(), AuraModeNeuro.I.player.getPitch(), (double)(param2.method909() + ((Float)param2.method890().getValue()).floatValue()) + field11116, field11117);
        if (v4 != null) {
            param1.rotationPoint.addSample(v4.field9281, v4.field9282, v4.field9283);
        }
    }

    private void method3129(NeuroSampler param1) {
        boolean v7;
        int v2 = param1.method2282();
        long v3 = System.currentTimeMillis();
        if (param1.method2286()) {
            if (v2 != this.field11044) {
                NeuroProfileStorage.method2287();
                this.field11044 = v2;
                this.field11045 = v3;
            }
            return;
        }
        if (v2 == this.field11044) {
            return;
        }
        boolean v5 = v2 / 900 > this.field11044 / 900;
        boolean v6 = v3 - this.field11045 >= field11118;
        boolean bl = v7 = this.field11044 < 0 && v2 > 0;
        if (v7 || v5 || v6) {
            NeuroProfileStorage.method2287();
            this.field11044 = v2;
            this.field11045 = v3;
        }
    }

    private te._2 method3130(NeuroSampler param12, float param2, float param3, float param4, float param5, boolean param6) {
        double v15;
        double v13;
        ArrayList v7 = param12.samples;
        int v8 = v7.size();
        int v9 = Math.max(1, v8 / 2400);
        PriorityQueue<double[]> v10 = new PriorityQueue<double[]>(25, (param0, param1) -> Double.compare(param0[0], param1[0]));
        for (int v11 = 0; v11 < v8; v11 += v9) {
            EasingUtil v12 = (EasingUtil)v7.get(v11);
            if (v12 == null || !v12.method2680()) continue;
            v13 = (double)Math.abs(v12.yawError - param2) * field11119 + (double)Math.abs(v12.pitchError - param3) * field11120 + (double)Math.abs(v12.distance - param4) * field11121 + (double)Math.abs(v12.targetSpeed - param5) * field11122 + (v12.rayAligned == param6 ? 0.0 : field11123);
            v15 = 1.0 / (1.0 + v13 * v13);
            if (v15 < field11124) continue;
            if (v10.size() < 24) {
                v10.offer(new double[]{v15, v12.yawGain, v12.pitchGain, v12.yawStepAbs, v12.pitchStepAbs});
                continue;
            }
            if (!(v15 > ((double[])v10.peek())[0])) continue;
            v10.poll();
            v10.offer(new double[]{v15, v12.yawGain, v12.pitchGain, v12.yawStepAbs, v12.pitchStepAbs});
        }
        double v11 = 0.0;
        v13 = 0.0;
        v15 = 0.0;
        double v17 = 0.0;
        double v19 = 0.0;
        for (double[] v22 : v10) {
            double v23 = v22[0];
            v11 += v23;
            v13 += v22[1] * v23;
            v15 += v22[2] * v23;
            v17 += v22[3] * v23;
            v19 += v22[4] * v23;
        }
        if (v11 <= 0.0) {
            return new te._2(param12.behavior.yawResponse, param12.behavior.pitchResponse, Math.max(field11125, param2 * param12.behavior.yawResponse), Math.max(field11126, param3 * param12.behavior.pitchResponse));
        }
        return new te._2((float)(v13 / v11), (float)(v15 / v11), (float)(v17 / v11), (float)(v19 / v11));
    }

    private Vec3d method3131(LivingEntity param1, NeuroSampler param2, boolean param3) {
        Box v4 = param1.getBoundingBox();
        Vec3d v5 = this.method3125(v4, param2.rotationPoint);
        if (!param3) {
            return v5;
        }
        Vector2f v6 = this.method3119(v5);
        if (v6 == null) {
            return v5;
        }
        float v7 = Math.abs(MathHelper.wrapDegrees((float)(v6.x - AuraModeNeuro.I.player.getYaw()))) + Math.abs(v6.y - AuraModeNeuro.I.player.getPitch());
        float v8 = MathHelper.clamp((float)(1.0f - v7 / field11127), (float)0.0f, (float)field11128);
        Vec3d v9 = this.method3125(v4, param2.hitPoint);
        return v5.lerp(v9, (double)v8);
    }

    @Override
    public void onDeactivate(AttackAura param1) {
        this.method3122(param1);
        NeuroProfileStorage.method2287();
        this.method3123();
    }

    @Override
    public void reset(AttackAura param1) {
        this.method3122(param1);
        this.method3123();
    }

    static {
        ШМ = 0;
        cfr_renamed_268 = 0L;
        cfr_renamed_240 = 0.0;
        cfr_renamed_168 = 0.0;
        Шп = 0;
        ШО = 0;
        field11046 = Integer.MIN_VALUE;
        field11048 = "Hit Point";
        field11049 = 0.05;
        field11082 = -89.9f;
        field11083 = 89.9f;
        field11064 = "Yaw Speed";
        field11065 = "Pitch Speed";
        field11066 = "Behavior";
        field11067 = 0.45f;
        field11068 = 0.45f;
        field11069 = 0.75f;
        field11070 = 0.75f;
        field11071 = 50.0;
        field11072 = "Yaw Speed";
        field11073 = 0.02f;
        field11074 = 1.35f;
        field11075 = "Pitch Speed";
        field11076 = 0.02f;
        field11077 = 1.2f;
        field11078 = "Behavior";
        field11115 = "Rotation Point";
        field11116 = 7.5;
        field11117 = 0.36;
        field11085 = 0.5f;
        field11086 = 0.5f;
        field11087 = 50.0;
        field11088 = 0.45f;
        field11089 = 0.45f;
        field11090 = 6.5f;
        field11091 = 0.08f;
        field11092 = 0.35f;
        field11093 = 5.0f;
        field11094 = 0.85f;
        field11095 = 0.08f;
        field11096 = 0.3f;
        field11097 = 0.5f;
        field11098 = 0.22f;
        field11099 = 0.46f;
        field11100 = 0.5f;
        field11101 = 0.18f;
        field11102 = 0.4f;
        field11103 = 0.1f;
        field11104 = 0.92f;
        field11105 = 0.3f;
        field11106 = 0.25f;
        field11107 = -89.9f;
        field11108 = 89.9f;
        field11119 = 0.22;
        field11120 = 0.28;
        field11121 = 0.8;
        field11122 = 0.08;
        field11123 = 1.25;
        field11124 = 0.015;
        field11125 = 0.18f;
        field11126 = 0.1f;
        field11127 = 14.0f;
        field11128 = 0.65f;
        field11054 = 1.0E-4;
        field11055 = 1.0E-4;
        field11056 = 1.0E-4;
        field11050 = 1.0E-6;
        field11051 = 90.0;
        field11052 = -89.9f;
        field11053 = 89.9f;
        field11118 = 10000L;
        field11110 = 0.35f;
        field11111 = 0.3f;
        field11112 = 0.2f;
        field11113 = 0.04f;
        field11114 = 0.04f;
        field11084 = Integer.MIN_VALUE;
    }
}


