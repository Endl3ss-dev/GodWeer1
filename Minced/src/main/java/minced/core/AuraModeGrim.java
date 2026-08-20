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
 */
package minced.core;

import java.util.concurrent.ThreadLocalRandom;
import minced.core.RotationTargetUtil;
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

public class AuraModeGrim
implements AuraModeHandler {
    static MinecraftClient I = MinecraftClient.getInstance();
    int field10829;
    boolean field10830 = false;
    float field10831 = 0.0f;
    float field10832 = 0.0f;
    static double field10833;
    static double field10834;
    static double field10835;
    static double field10836;
    static double field10837;
    static float field10838;
    static float field10839;
    static float field10840;
    static float field10841;
    static float field10842;
    static float field10843;
    static float field10844;
    static float field10845;
    static float field10846;
    static float field10847;
    static float field10848;
    static float field10849;
    static float field10850;
    static float field10851;
    static float field10852;
    static float field10853;
    static float field10854;
    static float field10855;
    static float field10856;
    static float field10857;
    static float field10858;
    static float field10859;
    static float field10860;
    static float field10861;
    static float field10862;
    static float field10863;
    static float field10864;
    static float field10865;
    static float field10866;
    static float field10867;
    static float field10868;
    static float field10869;
    static float field10870;
    static float field10871;
    static float field10872;
    static float field10873;
    static float field10874;
    float field10875 = 0.0f;
    static float field10876;
    static float field10877;
    int field10878 = 0;
    static float field10879;
    static float field10880;
    static float field10881;
    static float field10882;
    int field10883 = 0;
    static float field10884;
    static float field10885;
    static float field10886;
    static float field10887;
    static float field10888;
    static float field10889;
    static float field10890;
    int field10891 = 0;
    static float field10892;
    static float field10893;
    static float field10894;
    int field10895 = 0;
    static float field10896;
    static float field10897;
    static float field10898;
    static float field10899;
    static float field10900;
    static float field10901;
    static float field10902;
    static float field10903;
    static float field10904;
    static float field10905;
    static float field10906;
    static float field10907;
    static float field10908;
    static float field10909;
    static float field10910;
    static float field10911;
    static float field10912;
    static float field10913;
    float field10914 = 0.0f;
    static float field10915;
    static float field10916;
    static float field10917;
    static float field10918;
    static float field10919;
    static float field10920;
    float field10921 = 0.0f;
    float field10922 = 0.0f;
    static float field10923;
    static float field10924;
    static float field10925;
    static float field10926;
    static float field10927;
    static float field10928;
    static float field10929;
    static float field10930;
    static float field10931;
    static float field10932;
    static float field10933;
    static float field10934;
    static float field10935;
    static float field10936;
    static float field10937;
    static float field10938;
    static float field10939;
    static float field10940;
    static float field10941;
    static float field10942;
    static float field10943;
    static float field10944;
    static float field10945;
    static float field10946;
    static float field10947;
    static float field10948;
    long field10949 = 0L;
    Vec3d field10950 = Vec3d.ZERO;
    static double field10951;
    static double field10952;
    static double field10953;
    static double field10954;
    static float field10955;
    static float field10956;
    static float field10957;
    static float field10958;
    static float field10959;
    static float field10960;
    static long field10961;
    static long field10962;
    static double field10963;
    static double field10964;
    static double field10965;
    int field10966 = 1;
    static float field10967;
    static float field10968;
    static float field10969;
    static float field10970;
    static float field10971;
    static String field10972 = "Grim";
    long field10973 = 0L;
    static float field10974;
    static float field10975;
    int O = 1;
    static long field10976;
    static long field10977;
    static long field10978;
    static long field10979;
    static float field10980;
    static float field10981;
    static float field10982;
    static float field10983;
    static float field10984;
    static float field10985;
    static float field10986;
    static float field10987;
    static float field10988;
    static float field10989;
    static float field10990;
    static float field10991;
    static float field10992;
    static float field10993;
    static float field10994;
    static float field10995;
    static float field10996;
    static float field10997;
    static float field10998;
    float field10999 = 0.0f;
    static float field11000;
    static float field11001;
    float field11002 = 0.0f;
    static float field11003;
    static float field11004;
    static float field11005;
    static double field11006;
    static double field11007;
    long field11008 = 0L;
    static float field11009;
    static float field11010;
    int field11011 = 1;
    static long field11012;
    static long field11013;
    static long field11014;
    static long field11015;
    static float field11016;
    static float field11017;
    static float field11018;
    static float field11019;
    static float field11020;
    static float field11021;
    static float field11022;
    static float field11023;
    static float field11024;
    static float field11025;
    static float field11026;
    static float field11027;
    static float field11028;
    static float field11029;
    float field11030 = 0.0f;
    float field11031 = 0.0f;
    static float field11032;
    static float field11033;
    static double field11034;
    static double field11035;
    static double field11036;
    static double field11037;
    static float field11038;
    static float field11039;
    static int field11040;
    static int field11041;

    private Vec3d method3104(LivingEntity param1) {
        Vec3d v11;
        double v9;
        Box v2 = param1.getBoundingBox();
        long v3 = System.currentTimeMillis();
        if (v3 >= this.field10949 || this.field10950.equals((Object)Vec3d.ZERO)) {
            double v5 = v2.getLengthX() * field10951;
            double v7 = v2.getLengthZ() * field10952;
            v9 = v2.getLengthY() * ThreadLocalRandom.current().nextDouble(field10953, field10954);
            v11 = new Vec3d(ThreadLocalRandom.current().nextDouble(-v5, v5), v9, ThreadLocalRandom.current().nextDouble(-v7, v7));
            this.field10950 = this.field10950.equals((Object)Vec3d.ZERO) ? v11 : new Vec3d(MathHelper.lerp((double)ThreadLocalRandom.current().nextFloat(field10955, field10956), (double)this.field10950.x, (double)v11.x), MathHelper.lerp((double)ThreadLocalRandom.current().nextFloat(field10957, field10958), (double)this.field10950.y, (double)v11.y), MathHelper.lerp((double)ThreadLocalRandom.current().nextFloat(field10959, field10960), (double)this.field10950.z, (double)v11.z));
            this.field10949 = v3 + ThreadLocalRandom.current().nextLong(field10961, field10962);
        }
        Vec3d v5 = AuraModeGrim.I.player.getEyePos().subtract(param1.getPos());
        Vec3d v6 = new Vec3d(v5.x, 0.0, v5.z);
        if (v6.lengthSquared() < field10963) {
            v6 = new Vec3d(0.0, 0.0, 1.0);
        }
        Vec3d v7 = v6.normalize();
        Vec3d v8 = new Vec3d(v7.z, 0.0, -v7.x);
        v9 = Math.max(field10964, v2.getLengthX() * field10965);
        v11 = v8.multiply(v9 * (double)this.field10966);
        Vec3d v12 = v8.multiply(this.field10950.x).add(v7.multiply(this.field10950.z)).add(0.0, this.field10950.y, 0.0);
        return param1.getPos().add(v11).add(v12);
    }

    private float method3105(float param1, boolean param2, float param3, float param4) {
        long v5 = System.currentTimeMillis();
        if (v5 >= this.field10973 || Math.abs(param3) > field10974) {
            this.O = Math.abs(param3) > field10975 ? (param3 > 0.0f ? 1 : -1) : (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
            this.field10973 = v5 + ThreadLocalRandom.current().nextLong(param2 ? field10976 : field10977, param2 ? field10978 : field10979);
        }
        float v7 = param2 ? ThreadLocalRandom.current().nextFloat(field10980, field10981) : ThreadLocalRandom.current().nextFloat(field10982, field10983);
        v7 *= field10984 + param4 * field10985;
        if (Math.abs(param1) < field10986) {
            v7 *= ThreadLocalRandom.current().nextFloat(field10987, field10988);
        }
        float v8 = Math.min(1.0f, Math.abs(param3) / field10989);
        float v9 = (float)this.O * (v7 += v8 * ThreadLocalRandom.current().nextFloat(field10990, field10991) * (field10992 + param4 * field10993));
        if (ThreadLocalRandom.current().nextFloat() < field10994 + param4 * field10995) {
            v9 *= field10996;
        }
        this.field10999 = MathHelper.lerp((float)ThreadLocalRandom.current().nextFloat(field10997, field10998), (float)this.field10999, (float)v9);
        float v10 = MathHelper.lerp((float)param4, (float)field11000, (float)field11001);
        this.field11002 += MathHelper.clamp((float)(this.field10999 - this.field11002), (float)(-v10), (float)v10);
        float v11 = MathHelper.lerp((float)param4, (float)field11003, (float)(param2 ? field11004 : field11005));
        float v12 = (float)(Math.sin((double)v5 / (param2 ? field11006 : field11007)) * ThreadLocalRandom.current().nextDouble(0.0, v11));
        return this.field11002 + v12;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void updateRotation(AttackAura param1, LivingEntity param2) {
        if (AuraModeGrim.I.player == null || param2 == null) {
            this.method3109();
            return;
        }
        if (this.field10829 != param2.getId()) {
            this.method3109();
            this.field10829 = param2.getId();
        }
        var v3 = new Vector2f(AuraModeGrim.I.player.getYaw(), AuraModeGrim.I.player.getPitch());        var v4 = AuraModeGrim.I.player.getCameraPosVec(AuraModeGrim.I.getRenderTickCounter().getTickDelta(false));        var v5 = Rotation.method3143();        var v6 = Rotation.method3134();        var v7 = 0.0f;        var v8 = 0.0f;        if (!this.field10830) {
            this.field10831 = v5;
            this.field10832 = v6;
            this.field10830 = true;
        } else {
            v7 = MathHelper.wrapDegrees((float)(v5 - this.field10831));
            v8 = v6 - this.field10832;
            this.field10831 = v5;
            this.field10832 = v6;
        }
        var v9 = param2.getPos().add(0.0, (double)param2.getHeight() * AuraModeGrim.field10833, 0.0);        var v10 = v9.subtract(v4);        if (v10.lengthSquared() < AuraModeGrim.field10834) {
            return;
        }
        var v11 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v10.z, v10.x)) - AuraModeGrim.field10835));        this.method3108(v3.x, v11, v7);
        var v12 = this.method3104(param2);        var v13 = v12.subtract(v4);        if (v13.lengthSquared() < AuraModeGrim.field10836) {
            return;
        }
        var v14 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v13.z, v13.x)) - AuraModeGrim.field10837));        var v15 = (float)MathHelper.wrapDegrees((double)(-Math.toDegrees(Math.atan2(v13.y, Math.hypot(v13.x, v13.z)))));        v15 = MathHelper.clamp((float)v15, (float)AuraModeGrim.field10838, (float)AuraModeGrim.field10839);
        var v16 = MathHelper.wrapDegrees((float)(v14 - v3.x));        var v17 = MathHelper.wrapDegrees((float)(v15 - v3.y));        var v18 = (float)v4.distanceTo(v12);        var v19 = AttackAura.method836(v3.x, v3.y, param1.method909() + ((Float)param1.method890().getValue()).floatValue(), (Entity)param2);        var v20 = v19 != false ? ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10840, AuraModeGrim.field10841) : ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10842, AuraModeGrim.field10843);        var v21 = Math.max(0.0f, Math.abs(v17) - v20);        var v22 = MathHelper.clamp((float)(v21 / AuraModeGrim.field10844), (float)0.0f, (float)1.0f);        var v23 = v19 != false ? ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10845, AuraModeGrim.field10846) : ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10847, AuraModeGrim.field10848);        var v24 = v19 != false ? ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10849, AuraModeGrim.field10850) : ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10851, AuraModeGrim.field10852);        v24 = MathHelper.lerp((float)v22, (float)(v24 * AuraModeGrim.field10853), (float)v24);
        float v25;
        float v26;
        if (v18 < AuraModeGrim.field10854) {
            v25 = ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10855, AuraModeGrim.field10856);
            v26 = ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10857, AuraModeGrim.field10858);
        } else if (v18 < AuraModeGrim.field10859) {
            v25 = ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10860, AuraModeGrim.field10861);
            v26 = ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10862, AuraModeGrim.field10863);
        } else {
            v25 = ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10864, AuraModeGrim.field10865);
            v26 = ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10866, AuraModeGrim.field10867);
        }
        var v27 = MathHelper.clamp((float)(v16 * v23), (float)(-(v25 += Math.min(AuraModeGrim.field10868, Math.abs(v7) * ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10869, AuraModeGrim.field10870)))), (float)v25);        var v28 = MathHelper.clamp((float)(v17 * v24), (float)(-(v26 += Math.min(AuraModeGrim.field10871, Math.abs(v8) * ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10872, AuraModeGrim.field10873)))), (float)v26);        if (v22 < AuraModeGrim.field10874) {
            v28 = 0.0f;
            this.field10875 *= ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10876, AuraModeGrim.field10877);
        }
        if (this.field10878 <= 0 && Math.abs(v16) < AuraModeGrim.field10879) {
            float randomHorizontalPause = ThreadLocalRandom.current().nextFloat();
            float horizontalPauseChance = v19 != false ? AuraModeGrim.field10880 : AuraModeGrim.field10881;
            if (randomHorizontalPause < horizontalPauseChance) {
                this.field10878 = ThreadLocalRandom.current().nextInt(1, 3);
            }
        }
        if (v22 > AuraModeGrim.field10882 && this.field10883 <= 0 && Math.abs(v17) < AuraModeGrim.field10884) {
            float randomVerticalPause = ThreadLocalRandom.current().nextFloat();
            float verticalPauseChance = v19 != false ? AuraModeGrim.field10885 : AuraModeGrim.field10886;
            if (randomVerticalPause < verticalPauseChance) {
                this.field10883 = ThreadLocalRandom.current().nextInt(1, 3);
            }
        }
        if (this.field10878 > 0) {
            v27 *= ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10887, AuraModeGrim.field10888);
            --this.field10878;
        }
        if (this.field10883 > 0) {
            v28 *= ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10889, AuraModeGrim.field10890);
            --this.field10883;
        }
        if (this.field10891 <= 0 && Math.abs(v16) < AuraModeGrim.field10892 && ThreadLocalRandom.current().nextFloat() < AuraModeGrim.field10893) {
            this.field10891 = ThreadLocalRandom.current().nextInt(1, 4);
        }
        if (v22 > AuraModeGrim.field10894 && this.field10895 <= 0 && Math.abs(v17) < AuraModeGrim.field10896 && ThreadLocalRandom.current().nextFloat() < AuraModeGrim.field10897) {
            this.field10895 = ThreadLocalRandom.current().nextInt(1, 4);
        }
        if (this.field10891 > 0) {
            v27 += -Math.copySign(ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10898, AuraModeGrim.field10899), v16);
            --this.field10891;
        }
        if (this.field10895 > 0) {
            v28 += -Math.copySign(ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10900, AuraModeGrim.field10901), v17);
            --this.field10895;
        }
        var v29 = v19 != false ? ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10902, AuraModeGrim.field10903) : ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10904, AuraModeGrim.field10905);        var v30 = v19 != false ? ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10906, AuraModeGrim.field10907) : ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10908, AuraModeGrim.field10909);        var v31 = v19 != false ? ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10910, AuraModeGrim.field10911) : ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10912, AuraModeGrim.field10913);        var v32 = MathHelper.lerp((float)v22, (float)v30, (float)v31);        var v33 = MathHelper.clamp((float)(v27 - this.field10914), (float)(-v29), (float)v29);        var v34 = MathHelper.clamp((float)(v28 - this.field10875), (float)(-v32), (float)v32);        this.field10914 += v33;
        this.field10875 += v34;
        var v35 = this.field10914 + this.method3107(v16, v19, v7);        var v36 = this.field10875 + this.method3105(v17, v19, v8, v22);        var v37 = ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10915, AuraModeGrim.field10916);        var v38 = ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10917, AuraModeGrim.field10918);        var v39 = ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10919, AuraModeGrim.field10920);        var v40 = MathHelper.lerp((float)v22, (float)v38, (float)v39);        var v41 = this.field10921 + MathHelper.clamp((float)(v35 - this.field10921), (float)(-v37), (float)v37);        var v42 = this.field10922 + MathHelper.clamp((float)(v36 - this.field10922), (float)(-v40), (float)v40);        var v43 = RotationTargetUtil.method1975();        var v44 = Math.max(v43 * 2.0f, ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10923, AuraModeGrim.field10924));        v41 = MathHelper.clamp((float)v41, (float)(-Math.abs(v16) - v44), (float)(Math.abs(v16) + v44));
        var v45 = Math.max(v43 * AuraModeGrim.field10925, ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10926, AuraModeGrim.field10927));        var v46 = Math.max(v43 * AuraModeGrim.field10928, ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10929, AuraModeGrim.field10930));        var v47 = MathHelper.lerp((float)v22, (float)v45, (float)v46);        v42 = MathHelper.clamp((float)v42, (float)(-Math.abs(v17) - v47), (float)(Math.abs(v17) + v47));
        var v48 = Math.abs(v16) < ThreadLocalRandom.current().nextFloat(AuraModeGrim.field10931, AuraModeGrim.field10932) || ThreadLocalRandom.current().nextFloat() < AuraModeGrim.field10933;        var v49 = this.method3106(v41, v16, v43, v48);        var v50 = v22 > AuraModeGrim.field10934 && ThreadLocalRandom.current().nextFloat() < (v19 != false ? AuraModeGrim.field10935 : AuraModeGrim.field10936);        var v51 = this.method3106(v42, v17, v43, v50);        if (v22 < AuraModeGrim.field10937) {
            v51 = Math.abs(v17) <= v43 * AuraModeGrim.field10938 ? 0.0f : Math.copySign(v43, v17);
        }
        if (Math.abs(v16) < AuraModeGrim.field10939 && Math.abs(v17) < AuraModeGrim.field10940 && ThreadLocalRandom.current().nextFloat() < AuraModeGrim.field10941) {
            v49 += (ThreadLocalRandom.current().nextBoolean() != false ? 1.0f : AuraModeGrim.field10942) * v43;
        }
        this.field10921 = v49;
        this.field10922 = v51;
        var v52 = v3.x + v49;        var v53 = MathHelper.clamp((float)(v3.y + v51), (float)AuraModeGrim.field10943, (float)AuraModeGrim.field10944);        AimRotation.method376(new Rotation(v52, v53), AuraModeGrim.field10945, AuraModeGrim.field10946, AuraModeGrim.field10947, AuraModeGrim.field10948, 1, 0, true);
    }

    private float method3106(float param1, float param2, float param3, boolean param4) {
        if (param3 <= 0.0f) {
            return param1;
        }
        float v5 = (float)Math.round(param1 / param3) * param3;
        if (v5 == 0.0f && Math.abs(param2) > param3 * field10967) {
            v5 = Math.copySign(param3, param2);
        }
        float v6 = Math.abs(param2);
        if (param4 && v6 <= param3 * field10968) {
            float v7 = param3 * ThreadLocalRandom.current().nextFloat(1.0f, field10969);
            if ((v5 = MathHelper.clamp((float)v5, (float)(-v7), (float)v7)) == 0.0f && ThreadLocalRandom.current().nextFloat() < field10970) {
                v5 = (ThreadLocalRandom.current().nextBoolean() ? 1.0f : field10971) * param3;
            }
            return v5;
        }
        if (Math.abs(v5) > v6) {
            if (v6 <= param3) {
                v5 = param2;
            } else {
                float v7 = (float)Math.floor(v6 / param3) * param3;
                if (v7 == 0.0f) {
                    v7 = param3;
                }
                v5 = Math.copySign(v7, param2);
            }
        }
        return v5;
    }

    @Override
    public String getName() {
        return field10972;
    }

    @Override
    public void reset(AttackAura param1) {
        this.method3109();
    }

    private float method3107(float param1, boolean param2, float param3) {
        float v6;
        long v4 = System.currentTimeMillis();
        if (v4 >= this.field11008 || Math.abs(param3) > field11009) {
            this.field11011 = Math.abs(param3) > field11010 ? (param3 > 0.0f ? 1 : -1) : (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
            this.field11008 = v4 + ThreadLocalRandom.current().nextLong(param2 ? field11012 : field11013, param2 ? field11014 : field11015);
        }
        float f = v6 = param2 ? ThreadLocalRandom.current().nextFloat(field11016, field11017) : ThreadLocalRandom.current().nextFloat(field11018, field11019);
        if (Math.abs(param1) < field11020) {
            v6 *= ThreadLocalRandom.current().nextFloat(field11021, field11022);
        }
        float v7 = Math.min(1.0f, Math.abs(param3) / field11023);
        float v8 = (float)this.field11011 * (v6 += v7 * ThreadLocalRandom.current().nextFloat(field11024, field11025));
        if (ThreadLocalRandom.current().nextFloat() < field11026) {
            v8 *= field11027;
        }
        this.field11030 = MathHelper.lerp((float)ThreadLocalRandom.current().nextFloat(field11028, field11029), (float)this.field11030, (float)v8);
        this.field11031 += MathHelper.clamp((float)(this.field11030 - this.field11031), (float)field11032, (float)field11033);
        float v9 = (float)(Math.sin((double)v4 / (param2 ? field11034 : field11035)) * ThreadLocalRandom.current().nextDouble(field11036, field11037));
        return this.field11031 + v9;
    }

    private void method3108(float param1, float param2, float param3) {
        float v5;
        float v4 = MathHelper.wrapDegrees((float)(param2 - param1));
        if (v4 > (v5 = field11038)) {
            this.field10966 = -1;
        } else if (v4 < -v5) {
            this.field10966 = 1;
        }
        if (Math.abs(param3) > field11039) {
            this.field10966 = param3 > 0.0f ? 1 : -1;
        }
    }

    @Override
    public void onDeactivate(AttackAura param1) {
        this.method3109();
    }

    public AuraModeGrim() {
        this.field10829 = field11040;
    }

    private void method3109() {
        this.field10950 = Vec3d.ZERO;
        this.field10914 = 0.0f;
        this.field10875 = 0.0f;
        this.field10921 = 0.0f;
        this.field10922 = 0.0f;
        this.field11031 = 0.0f;
        this.field11030 = 0.0f;
        this.field11002 = 0.0f;
        this.field10999 = 0.0f;
        this.field10831 = 0.0f;
        this.field10832 = 0.0f;
        this.field11011 = 1;
        this.O = 1;
        this.field10878 = 0;
        this.field10883 = 0;
        this.field10891 = 0;
        this.field10895 = 0;
        this.field11008 = 0L;
        this.field10973 = 0L;
        this.field10949 = 0L;
        this.field10829 = field11041;
        this.field10966 = 1;
        this.field10830 = false;
    }

    static {
        field11040 = Integer.MIN_VALUE;
        field10833 = 0.74;
        field10834 = 1.0E-4;
        field10835 = 90.0;
        field10836 = 1.0E-4;
        field10837 = 90.0;
        field10838 = -89.9f;
        field10839 = 89.9f;
        field10840 = 0.55f;
        field10841 = 1.1f;
        field10842 = 0.45f;
        field10843 = 0.95f;
        field10844 = 5.5f;
        field10845 = 0.24f;
        field10846 = 0.43f;
        field10847 = 0.37f;
        field10848 = 0.68f;
        field10849 = 0.16f;
        field10850 = 0.3f;
        field10851 = 0.24f;
        field10852 = 0.44f;
        field10853 = 0.35f;
        field10854 = 2.4f;
        field10855 = 1.9f;
        field10856 = 3.9f;
        field10857 = 0.55f;
        field10858 = 1.8f;
        field10859 = 4.6f;
        field10860 = 2.5f;
        field10861 = 5.2f;
        field10862 = 0.8f;
        field10863 = 2.7f;
        field10864 = 3.4f;
        field10865 = 6.9f;
        field10866 = 1.1f;
        field10867 = 3.6f;
        field10868 = 3.2f;
        field10869 = 0.18f;
        field10870 = 0.43f;
        field10871 = 1.8f;
        field10872 = 0.12f;
        field10873 = 0.34f;
        field10874 = 0.08f;
        field10876 = 0.25f;
        field10877 = 0.55f;
        field10879 = 14.0f;
        field10880 = 0.05f;
        field10881 = 0.08f;
        field10882 = 0.28f;
        field10884 = 12.0f;
        field10885 = 0.05f;
        field10886 = 0.09f;
        field10887 = 0.08f;
        field10888 = 0.34f;
        field10889 = 0.08f;
        field10890 = 0.24f;
        field10892 = 4.4f;
        field10893 = 0.17f;
        field10894 = 0.35f;
        field10896 = 4.0f;
        field10897 = 0.1f;
        field10898 = 0.07f;
        field10899 = 0.28f;
        field10900 = 0.06f;
        field10901 = 0.22f;
        field10902 = 0.1f;
        field10903 = 0.29f;
        field10904 = 0.14f;
        field10905 = 0.44f;
        field10906 = 0.03f;
        field10907 = 0.1f;
        field10908 = 0.05f;
        field10909 = 0.15f;
        field10910 = 0.09f;
        field10911 = 0.26f;
        field10912 = 0.13f;
        field10913 = 0.37f;
        field10915 = 0.15f;
        field10916 = 0.41f;
        field10917 = 0.03f;
        field10918 = 0.1f;
        field10919 = 0.1f;
        field10920 = 0.28f;
        field10923 = 0.24f;
        field10924 = 0.76f;
        field10925 = 0.75f;
        field10926 = 0.04f;
        field10927 = 0.15f;
        field10928 = 1.8f;
        field10929 = 0.18f;
        field10930 = 0.48f;
        field10931 = 3.8f;
        field10932 = 7.8f;
        field10933 = 0.08f;
        field10934 = 0.45f;
        field10935 = 0.02f;
        field10936 = 0.04f;
        field10937 = 0.08f;
        field10938 = 1.1f;
        field10939 = 7.0f;
        field10940 = 7.0f;
        field10941 = 0.16f;
        field10942 = -1.0f;
        field10943 = -89.9f;
        field10944 = 89.9f;
        field10945 = 360.0f;
        field10946 = 360.0f;
        field10947 = 3.0f;
        field10948 = 3.0f;
        field11038 = 1.4f;
        field11039 = 1.1f;
        field10951 = 0.035;
        field10952 = 0.045;
        field10953 = 0.7;
        field10954 = 0.79;
        field10955 = 0.16f;
        field10956 = 0.3f;
        field10957 = 0.08f;
        field10958 = 0.18f;
        field10959 = 0.16f;
        field10960 = 0.3f;
        field10961 = 170L;
        field10962 = 420L;
        field10963 = 1.0E-6;
        field10964 = 0.1;
        field10965 = 0.3;
        field11009 = 0.85f;
        field11010 = 0.85f;
        field11012 = 56L;
        field11013 = 38L;
        field11014 = 122L;
        field11015 = 92L;
        field11016 = 0.04f;
        field11017 = 0.14f;
        field11018 = 0.08f;
        field11019 = 0.27f;
        field11020 = 4.0f;
        field11021 = 1.18f;
        field11022 = 1.88f;
        field11023 = 5.2f;
        field11024 = 0.05f;
        field11025 = 0.2f;
        field11026 = 0.19f;
        field11027 = -1.0f;
        field11028 = 0.3f;
        field11029 = 0.54f;
        field11032 = -0.31f;
        field11033 = 0.31f;
        field11034 = 38.0;
        field11035 = 27.0;
        field11036 = 0.01;
        field11037 = 0.07;
        field10974 = 0.42f;
        field10975 = 0.42f;
        field10976 = 44L;
        field10977 = 30L;
        field10978 = 98L;
        field10979 = 78L;
        field10980 = 0.02f;
        field10981 = 0.1f;
        field10982 = 0.03f;
        field10983 = 0.15f;
        field10984 = 0.08f;
        field10985 = 0.92f;
        field10986 = 3.0f;
        field10987 = 0.45f;
        field10988 = 1.3f;
        field10989 = 1.4f;
        field10990 = 0.02f;
        field10991 = 0.1f;
        field10992 = 0.2f;
        field10993 = 0.8f;
        field10994 = 0.08f;
        field10995 = 0.1f;
        field10996 = -1.0f;
        field10997 = 0.34f;
        field10998 = 0.56f;
        field11000 = 0.03f;
        field11001 = 0.24f;
        field11003 = 0.002f;
        field11004 = 0.025f;
        field11005 = 0.04f;
        field11006 = 33.0;
        field11007 = 24.0;
        field10967 = 0.6f;
        field10968 = 4.0f;
        field10969 = 2.2f;
        field10970 = 0.35f;
        field10971 = -1.0f;
        field11041 = Integer.MIN_VALUE;
    }
}



