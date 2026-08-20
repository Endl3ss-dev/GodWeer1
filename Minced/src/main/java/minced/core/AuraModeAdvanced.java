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

public class AuraModeAdvanced
implements AuraModeHandler {
    static MinecraftClient I = MinecraftClient.getInstance();
    int field10518;
    static String field10519;
    static float field10520;
    static float field10521;
    boolean field10522;
    float field10523;
    float field10524;
    static float field10525;
    static float field10526;
    static double field10527;
    static double field10528;
    static double field10529;
    static float field10530;
    static float field10531;
    static float field10532;
    static float field10533;
    static float field10534;
    static float field10535;
    static float field10536;
    static float field10537;
    static float field10538;
    static float field10539;
    static float field10540;
    static float field10541;
    static float field10542;
    static float field10543;
    static float field10544;
    static float field10545;
    static float field10546;
    static float field10547;
    static float field10548;
    static float field10549;
    static float field10550;
    static float field10551;
    static float field10552;
    static float field10553;
    static float field10554;
    static float field10555;
    static float field10556;
    static float field10557;
    static float field10558;
    static float field10559;
    static float field10560;
    int field10561;
    static float field10562;
    static float field10563;
    static float field10564;
    static float field10565;
    int field10566;
    static float field10567;
    static float field10568;
    static float field10569;
    static float field10570;
    static float field10571;
    static float field10572;
    static float field10573;
    float field10574;
    static float field10575;
    static float field10576;
    static float field10577;
    static float field10578;
    static float field10579;
    static float field10580;
    static float field10581;
    static float field10582;
    float field10583;
    static float field10584;
    static float field10585;
    static float field10586;
    static float field10587;
    static float field10588;
    float field10589;
    float field10590;
    static float field10591;
    static float field10592;
    static float field10593;
    static float field10594;
    static float field10595;
    static float field10596;
    static float field10597;
    static float field10598;
    static float field10599;
    static float field10600;
    static float field10601;
    static float field10602;
    static float field10603;
    static float field10604;
    static float field10605;
    static float field10606;
    static float field10607;
    static float field10608;
    static float field10609;
    static float field10610;
    static float field10611;
    static float field10612;
    static float field10613;
    static float field10614;
    static float field10615;
    static float field10616;
    static float field10617;
    static float field10618;
    static float field10619;
    static float field10620;
    static float field10621;
    static float field10622;
    static float field10623;
    static float field10624;
    static float field10625;
    float field10626;
    static float field10627;
    static float field10628;
    static float field10629;
    static float field10630;
    long field10631;
    static float field10632;
    static float field10633;
    static float field10634;
    static float field10635;
    static float field10636;
    static float field10637;
    static float field10638;
    static float field10639;
    static float field10640;
    float field10641;
    static long field10642;
    static long field10643;
    static float field10644;
    static float field10645;
    static float field10646;
    static double field10647;
    static double field10648;
    static double field10649;
    static double field10650;
    static double field10651;
    static float field10652;
    static float field10653;
    static float field10654;
    static float field10655;
    static float field10656;
    static float field10657;
    static float field10658;
    static float field10659;
    static float field10660;
    static float field10661;
    float field10662;
    static float field10663;
    static float field10664;
    static float field10665;
    static float field10666;
    long field10667;
    static float field10668;
    static float field10669;
    static float field10670;
    static float field10671;
    static float field10672;
    static float field10673;
    static float field10674;
    float field10675;
    static long field10676;
    static long field10677;
    static float field10678;
    static float field10679;
    static float field10680;
    static double field10681;
    static double field10682;
    static double field10683;
    static double field10684;
    static double field10685;
    static double field10686;
    static double field10687;
    static float field10688;
    static float field10689;
    static float field10690;
    static float field10691;
    static float field10692;
    static float field10693;
    static float field10694;
    static float field10695;
    static float field10696;
    static float field10697;
    static float field10698;
    static float field10699;
    Vec3d field10700 = Vec3d.ZERO;
    long field10701;
    static int field10702;
    int field10703 = 1;
    int field10704;
    float field10705;
    float field10706;
    static float field10707;
    static float field10708;
    static float field10709;
    static double field10710;
    static double field10711;
    static double field10712;
    static double field10713;
    static double field10714;
    static double field10715;
    static float field10716;
    static float field10717;
    static double field10718;
    static double field10719;
    static float field10720;
    static float field10721;
    static float field10722;
    static double field10723;
    static double field10724;
    static double field10725;
    static int field10726;
    static float field10727;
    static float field10728;
    static double field10729;
    static double field10730;
    static float field10731;
    static float field10732;
    static float field10733;
    static float field10734;
    static float field10735;
    static float field10736;
    static float field10737;
    static float field10738;
    static float field10739;
    static float field10740;
    static float field10741;
    static double field10742;
    static double field10743;
    static double field10744;
    static double field10745;
    static double field10746;
    static double field10747;
    static float field10748;
    static float field10749;
    static float field10750;
    static float field10751;
    static float field10752;
    static float field10753;
    static float field10754;
    static float field10755;
    static float field10756;
    static float field10757;
    static float field10758;
    static float field10759;
    static float field10760;
    static String field10761 = "ReallyWorld";
    static float field10762;
    static float field10763;
    static float field10764;

    private Vec3d method3040(AttackAura param1, LivingEntity param2, float param3) {
        Vec3d v142;
        Box v4 = param2.getBoundingBox();
        float v5 = ((Float)param1.minced_core_ClsClass276_I().getValue()).floatValue() / field10707;
        float v6 = ((Float)param1.method784().getValue()).floatValue() / field10708;
        float v7 = ((Float)param1.method809().getValue()).floatValue() / field10709;
        long v8 = System.currentTimeMillis();
        long v10 = Math.round(((Float)param1.method736().getValue()).floatValue());
        long v12 = Math.round(((Float)param1.method812().getValue()).floatValue());
        if (v12 < v10) {
            long delayTmp = v10;
            v10 = v12;
            v12 = delayTmp;
        }
        if (v8 >= this.field10701 || this.field10700.equals((Object)Vec3d.ZERO)) {
            double v14 = v4.getLengthX() * field10710 * (field10711 + (double)v5 * field10712);
            double v16 = v4.getLengthZ() * field10713 * (field10714 + (double)v6 * field10715);
            float v18 = MathHelper.clamp((float)(((Float)param1.method897().getValue()).floatValue() / field10716), (float)field10717, (float)1.0f);
            double v19 = v4.getLengthY() * (double)v18 * (field10718 + (double)param3 * field10719);
            Vec3d v21 = new Vec3d(ThreadLocalRandom.current().nextDouble(-v14, v14), v19, ThreadLocalRandom.current().nextDouble(-v16, v16));
            if (this.field10700.equals((Object)Vec3d.ZERO)) {
                this.field10700 = v21;
            } else {
                float v22 = field10720 + param3 * field10721;
                this.field10700 = new Vec3d(MathHelper.lerp((double)v22, (double)this.field10700.x, (double)v21.x), MathHelper.lerp((double)(v22 * field10722), (double)this.field10700.y, (double)v21.y), MathHelper.lerp((double)v22, (double)this.field10700.z, (double)v21.z));
            }
            this.field10701 = v8 + ThreadLocalRandom.current().nextLong(v10, Math.max(v10 + 1L, v12 + 1L));
        }
        if ((v142 = new Vec3d(AuraModeAdvanced.I.player.getX() - param2.getX(), 0.0, AuraModeAdvanced.I.player.getZ() - param2.getZ())).lengthSquared() < field10723) {
            v142 = new Vec3d(0.0, 0.0, 1.0);
        }
        Vec3d v15 = v142.normalize();
        Vec3d v16 = new Vec3d(v15.z, 0.0, -v15.x);
        double v17 = Math.max(field10724, v4.getLengthX() * field10725) * (double)v7;
        Vec3d v19 = v16.multiply(v17 * (double)this.field10703);
        Vec3d v20 = v16.multiply(this.field10700.x).add(v15.multiply(this.field10700.z)).add(0.0, this.field10700.y, 0.0);
        return param2.getPos().add(v19).add(v20);
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void updateRotation(AttackAura param1, LivingEntity param2) {
        if (AuraModeAdvanced.I.player == null || param2 == null) {
            this.method3044();
            return;
        }
        if (this.field10518 != param2.getId()) {
            this.method3044();
            this.field10518 = param2.getId();
        }
        if (param1.method822().isMode(AuraModeAdvanced.field10519)) {
            this.method3045(param1, param2);
            return;
        }
        var v3 = MathHelper.clamp((float)(((Float)param1.minced_core_ClsClass276_l().getValue()).floatValue() / AuraModeAdvanced.field10520), (float)0.0f, (float)AuraModeAdvanced.field10521);        var v4 = new Vector2f(AuraModeAdvanced.I.player.getYaw(), AuraModeAdvanced.I.player.getPitch());        var v5 = AuraModeAdvanced.I.player.getCameraPosVec(AuraModeAdvanced.I.getRenderTickCounter().getTickDelta(false));        var v6 = Rotation.method3143();        var v7 = Rotation.method3134();        var v8 = 0.0f;        var v9 = 0.0f;        if (!this.field10522) {
            this.field10523 = v6;
            this.field10524 = v7;
            this.field10522 = true;
        } else {
            v8 = MathHelper.wrapDegrees((float)(v6 - this.field10523));
            v9 = v7 - this.field10524;
            this.field10523 = v6;
            this.field10524 = v7;
        }
        var v10 = MathHelper.clamp((float)(((Float)param1.method897().getValue()).floatValue() / AuraModeAdvanced.field10525), (float)AuraModeAdvanced.field10526, (float)1.0f);        var v11 = param2.getPos().add(0.0, (double)(param2.getHeight() * v10), 0.0);        var v12 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v11.z - v5.z, v11.x - v5.x)) - AuraModeAdvanced.field10527));        this.method3046(v4.x, v12, v8);
        var v13 = this.method3040(param1, param2, v3);        var v14 = v13.subtract(v5);        if (v14.lengthSquared() < AuraModeAdvanced.field10528) {
            return;
        }
        var v15 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v14.z, v14.x)) - AuraModeAdvanced.field10529));        var v16 = (float)MathHelper.wrapDegrees((double)(-Math.toDegrees(Math.atan2(v14.y, Math.hypot(v14.x, v14.z)))));        v16 = MathHelper.clamp((float)v16, (float)AuraModeAdvanced.field10530, (float)AuraModeAdvanced.field10531);
        var v17 = MathHelper.wrapDegrees((float)(v15 - v4.x));        var v18 = MathHelper.wrapDegrees((float)(v16 - v4.y));        var v19 = (float)v5.distanceTo(v13);        var v20 = AttackAura.method836(v4.x, v4.y, param1.method909() + ((Float)param1.method890().getValue()).floatValue(), (Entity)param2);        var v21 = ((Float)param1.method862().getValue()).floatValue() + v3 * (v20 != false ? AuraModeAdvanced.field10532 : AuraModeAdvanced.field10533);        var v22 = Math.max(0.0f, Math.abs(v18) - v21);        var v23 = MathHelper.clamp((float)(v22 / AuraModeAdvanced.field10534), (float)0.0f, (float)1.0f);        var v24 = ((Float)param1.cfr_renamed_266().getValue()).floatValue() / AuraModeAdvanced.field10535;        var v25 = ((Float)param1.minced_core_ClsClass276__().getValue()).floatValue() / AuraModeAdvanced.field10536;        var v26 = v24 * (v20 != false ? AuraModeAdvanced.field10537 + v3 * AuraModeAdvanced.field10538 : 1.0f + v3 * AuraModeAdvanced.field10539);        var v27 = MathHelper.lerp((float)v23, (float)(v25 * AuraModeAdvanced.field10540), (float)(v25 * (v20 != false ? AuraModeAdvanced.field10541 + v3 * AuraModeAdvanced.field10542 : AuraModeAdvanced.field10543 + v3 * AuraModeAdvanced.field10544)));        var v28 = ((Float)param1.method746().getValue()).floatValue();        var v29 = ((Float)param1.method815().getValue()).floatValue();        if (v19 < AuraModeAdvanced.field10545) {
            v28 *= AuraModeAdvanced.field10546 + v3 * AuraModeAdvanced.field10547;
            v29 *= AuraModeAdvanced.field10548 + v3 * AuraModeAdvanced.field10549;
        } else if (v19 < AuraModeAdvanced.field10550) {
            v28 *= AuraModeAdvanced.field10551 + v3 * AuraModeAdvanced.field10552;
            v29 *= AuraModeAdvanced.field10553 + v3 * AuraModeAdvanced.field10554;
        }
        var v30 = ((Float)param1.method766().getValue()).floatValue() / AuraModeAdvanced.field10555;        var v31 = MathHelper.clamp((float)(v17 * v26), (float)(-(v28 += Math.min(AuraModeAdvanced.field10556, Math.abs(v8) * v30 * AuraModeAdvanced.field10557))), (float)v28);        var v32 = MathHelper.clamp((float)(v18 * v27), (float)(-(v29 += Math.min(AuraModeAdvanced.field10558, Math.abs(v9) * v30 * AuraModeAdvanced.field10559))), (float)v29);        var v33 = ((Float)param1.method880().getValue()).floatValue() / AuraModeAdvanced.field10560;        if (this.field10561 <= 0 && Math.abs(v17) < AuraModeAdvanced.field10562) {
            var v0 = ThreadLocalRandom.current().nextFloat();            var v1 = v20 != false ? AuraModeAdvanced.field10563 : AuraModeAdvanced.field10564;            if (v0 < v33 * v1) {
                this.field10561 = ThreadLocalRandom.current().nextInt(1, 3);
            }
        }
        if (v23 > AuraModeAdvanced.field10565 && this.field10566 <= 0 && Math.abs(v18) < AuraModeAdvanced.field10567 && ThreadLocalRandom.current().nextFloat() < v33 * AuraModeAdvanced.field10568) {
            this.field10566 = ThreadLocalRandom.current().nextInt(1, 3);
        }
        if (this.field10561 > 0) {
            v31 *= AuraModeAdvanced.field10569 + v3 * AuraModeAdvanced.field10570;
            --this.field10561;
        }
        if (this.field10566 > 0) {
            v32 *= AuraModeAdvanced.field10571 + v3 * AuraModeAdvanced.field10572;
            --this.field10566;
        }
        if (v23 < AuraModeAdvanced.field10573) {
            v32 = 0.0f;
            this.field10574 *= AuraModeAdvanced.field10575 + v3 * AuraModeAdvanced.field10576;
        }
        var v34 = MathHelper.clamp((float)(((Float)param1.method833().getValue()).floatValue() / AuraModeAdvanced.field10577), (float)AuraModeAdvanced.field10578, (float)AuraModeAdvanced.field10579);        var v35 = MathHelper.clamp((float)(((Float)param1.method819().getValue()).floatValue() / AuraModeAdvanced.field10580), (float)AuraModeAdvanced.field10581, (float)AuraModeAdvanced.field10582);        this.field10583 *= v34;
        this.field10574 *= v35;
        var v36 = ((Float)param1.method757().getValue()).floatValue() * (AuraModeAdvanced.field10584 + v3 * AuraModeAdvanced.field10585);        var v37 = ((Float)param1.method761().getValue()).floatValue() * (AuraModeAdvanced.field10586 + v3 * AuraModeAdvanced.field10587);        var v38 = MathHelper.lerp((float)v23, (float)(v37 * AuraModeAdvanced.field10588), (float)v37);        this.field10583 += MathHelper.clamp((float)(v31 - this.field10583), (float)(-v36), (float)v36);
        this.field10574 += MathHelper.clamp((float)(v32 - this.field10574), (float)(-v38), (float)v38);
        var v39 = ((Float)param1.method914().getValue()).floatValue();        var v40 = ((Float)param1.method805().getValue()).floatValue();        var v41 = ((Float)param1.method853().getValue()).floatValue();        var v42 = ((Float)param1.method813().getValue()).floatValue();        var v43 = ((Float)param1.method811().getValue()).floatValue();        var v44 = this.field10589 + MathHelper.clamp((float)(this.field10583 - this.field10589), (float)(-v42), (float)v42);        var v45 = this.field10590 + MathHelper.clamp((float)(this.field10574 - this.field10590), (float)(-v43), (float)v43);        var v46 = RotationTargetUtil.method1975();        var v47 = Math.max(v46 * 2.0f, AuraModeAdvanced.field10591 + v3 * AuraModeAdvanced.field10592);        var v48 = Math.max(v46 * AuraModeAdvanced.field10593, AuraModeAdvanced.field10594 + v3 * AuraModeAdvanced.field10595);        var v49 = Math.max(v46 * AuraModeAdvanced.field10596, AuraModeAdvanced.field10597 + v3 * AuraModeAdvanced.field10598);        var v50 = MathHelper.lerp((float)v23, (float)v48, (float)v49);        var v51 = AuraModeAdvanced.field10599 + v39 / AuraModeAdvanced.field10600 * AuraModeAdvanced.field10601;        var v52 = AuraModeAdvanced.field10602 + v40 / AuraModeAdvanced.field10603 * AuraModeAdvanced.field10604;        v44 = MathHelper.clamp((float)v44, (float)(-Math.abs(v17) - v47 - v51), (float)(Math.abs(v17) + v47 + v51));
        v45 = MathHelper.clamp((float)v45, (float)(-Math.abs(v18) - v50 - v52), (float)(Math.abs(v18) + v50 + v52));
        var v53 = Math.abs(v17) < AuraModeAdvanced.field10605 + v3 * AuraModeAdvanced.field10606 || ThreadLocalRandom.current().nextFloat() < AuraModeAdvanced.field10607 + v3 * AuraModeAdvanced.field10608;        var v54 = (Boolean)param1.method831().getValue();        var v55 = v54 != false ? this.method3043(v44, v17, v46, v53) : v44;        var v56 = v23 > AuraModeAdvanced.field10609 && ThreadLocalRandom.current().nextFloat() < (v20 != false ? AuraModeAdvanced.field10610 : AuraModeAdvanced.field10611);        var v57 = v54 != false ? this.method3043(v45, v18, v46, v56) : v45;
        if (v23 < AuraModeAdvanced.field10612) {
            if (Math.abs(v18) <= v46 * AuraModeAdvanced.field10613) {
                v57 = 0.0f;            } else if (v54) {
                v57 = Math.copySign(v46, v18);
            }
        }
        var v58 = this.method3042(v39, v3, v20, v17, v8, v41);        var v59 = this.method3041(v40, v3, v20, v18, v9, v23, v41);        var v60 = v55 + v58;        var v61 = v57 + v59;        if (Math.abs(v17) < AuraModeAdvanced.field10614 && Math.abs(v18) < AuraModeAdvanced.field10615 && ThreadLocalRandom.current().nextFloat() < AuraModeAdvanced.field10616 + v3 * AuraModeAdvanced.field10617) {
            v60 += (ThreadLocalRandom.current().nextBoolean() != false ? 1.0f : AuraModeAdvanced.field10618) * v46;
        }
        this.field10589 = v55;
        this.field10590 = v57;
        var v62 = v4.x + v60;        var v63 = MathHelper.clamp((float)(v4.y + v61), (float)AuraModeAdvanced.field10619, (float)AuraModeAdvanced.field10620);        var v64 = MathHelper.clamp((int)Math.round(((Float)param1.method905().getValue()).floatValue()), (int)0, (int)5);        var v65 = Math.max(0, Math.round(((Float)param1.method775().getValue()).floatValue()));        var v66 = (Boolean)param1.method767().getValue();        AimRotation.method376(new Rotation(v62, v63), AuraModeAdvanced.field10621, AuraModeAdvanced.field10622, AuraModeAdvanced.field10623, AuraModeAdvanced.field10624, v65, v64, v66);
    }

    @Override
    public void onDeactivate(AttackAura param1) {
        this.method3044();
    }

    private float method3041(float param1, float param2, boolean param3, float param4, float param5, float param6, float param7) {
        float v11;
        if (param1 < field10625) {
            this.field10626 *= field10627;
            return this.field10626 * Math.max(field10628, param1 / field10629);
        }
        float v8 = param1 / field10630;
        long v9 = System.currentTimeMillis();
        if (v9 >= this.field10631) {
            v11 = v8 * (param3 ? field10632 : field10633) * (field10634 + param6 * field10635) * (field10636 + param2 * field10637);
            if (Math.abs(param4) < field10638) {
                v11 *= field10639 + param2 * field10640;
            }
            this.field10641 = ThreadLocalRandom.current().nextFloat(-v11, v11);
            this.field10631 = v9 + ThreadLocalRandom.current().nextLong(field10642, field10643);
        }
        v11 = MathHelper.clamp((float)(param7 / field10644), (float)field10645, (float)field10646);
        this.field10626 += (this.field10641 - this.field10626) * v11;
        double v12 = (double)v9 * field10647;
        float v14 = (float)(Math.sin(v12 * field10648) * field10649 + Math.sin(v12 * field10650) * field10651);
        float v15 = v8 * (param3 ? field10652 : field10653) * (field10654 + param6 * field10655) * (field10656 + param2 * field10657);
        float v16 = Math.min(field10658, Math.abs(param5) / field10659) * v8 * field10660;
        return this.field10626 + v14 * v15 + v16 * Math.signum(param5 == 0.0f ? 1.0f : param5);
    }

    private float method3042(float param1, float param2, boolean param3, float param4, float param5, float param6) {
        float v10;
        if (param1 < field10661) {
            this.field10662 *= field10663;
            return this.field10662 * Math.max(field10664, param1 / field10665);
        }
        float v7 = param1 / field10666;
        long v8 = System.currentTimeMillis();
        if (v8 >= this.field10667) {
            v10 = v7 * (param3 ? field10668 : field10669) * (field10670 + param2 * field10671);
            if (Math.abs(param4) < field10672) {
                v10 *= field10673 + param2 * field10674;
            }
            this.field10675 = ThreadLocalRandom.current().nextFloat(-v10, v10);
            this.field10667 = v8 + ThreadLocalRandom.current().nextLong(field10676, field10677);
        }
        v10 = MathHelper.clamp((float)(param6 / field10678), (float)field10679, (float)field10680);
        this.field10662 += (this.field10675 - this.field10662) * v10;
        double v11 = (double)v8 * field10681;
        float v13 = (float)(Math.sin(v11 * field10682) * field10683 + Math.sin(v11 * field10684) * field10685 + Math.sin(v11 * field10686) * field10687);
        float v14 = v7 * (param3 ? field10688 : field10689) * (field10690 + param2 * field10691);
        float v15 = Math.min(field10692, Math.abs(param5) / field10693) * v7 * field10694;
        return this.field10662 + v13 * v14 + v15 * Math.signum(param5 == 0.0f ? 1.0f : param5);
    }

    private float method3043(float param1, float param2, float param3, boolean param4) {
        if (param3 <= 0.0f) {
            return param1;
        }
        float v5 = (float)Math.round(param1 / param3) * param3;
        if (v5 == 0.0f && Math.abs(param2) > param3 * field10695) {
            v5 = Math.copySign(param3, param2);
        }
        float v6 = Math.abs(param2);
        if (param4 && v6 <= param3 * field10696) {
            float v7 = param3 * ThreadLocalRandom.current().nextFloat(1.0f, field10697);
            if ((v5 = MathHelper.clamp((float)v5, (float)(-v7), (float)v7)) == 0.0f && ThreadLocalRandom.current().nextFloat() < field10698) {
                v5 = (ThreadLocalRandom.current().nextBoolean() ? 1.0f : field10699) * param3;
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

    private void method3044() {
        this.field10700 = Vec3d.ZERO;
        this.field10574 = 0.0f;
        this.field10583 = 0.0f;
        this.field10590 = 0.0f;
        this.field10589 = 0.0f;
        this.field10626 = 0.0f;
        this.field10662 = 0.0f;
        this.field10641 = 0.0f;
        this.field10675 = 0.0f;
        this.field10524 = 0.0f;
        this.field10523 = 0.0f;
        this.field10566 = 0;
        this.field10561 = 0;
        this.field10701 = 0L;
        this.field10631 = 0L;
        this.field10667 = 0L;
        this.field10518 = field10702;
        this.field10703 = 1;
        this.field10522 = false;
        this.field10704 = 0;
        this.field10705 = 0.0f;
        this.field10706 = 0.0f;
    }

    @Override
    public void reset(AttackAura param1) {
        this.method3044();
    }

    public AuraModeAdvanced() {
        this.field10518 = field10726;
    }

    private void method3045(AttackAura param1, LivingEntity param2) {
        float v31;
        float v30;
        float v29;
        Vec3d v3 = AuraModeAdvanced.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(false));
        float v4 = MathHelper.clamp((float)(((Float)param1.method911().getValue()).floatValue() / field10727), (float)field10728, (float)1.0f);
        float v5 = ((Float)param1.method852().getValue()).floatValue();
        Vec3d v6 = new Vec3d(param2.getX() - param2.prevX, param2.getY() - param2.prevY, param2.getZ() - param2.prevZ);
        Vec3d v7 = param2.getPos().add(0.0, (double)(param2.getHeight() * v4), 0.0).add(v6.multiply((double)v5));
        Vec3d v8 = v7.subtract(v3);
        if (v8.lengthSquared() < field10729) {
            return;
        }
        float v9 = AuraModeAdvanced.I.player.getYaw();
        float v10 = AuraModeAdvanced.I.player.getPitch();
        float v11 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v8.z, v8.x)) - field10730));
        float v12 = (float)MathHelper.wrapDegrees((double)(-Math.toDegrees(Math.atan2(v8.y, Math.hypot(v8.x, v8.z)))));
        v12 = MathHelper.clamp((float)v12, (float)field10731, (float)field10732);
        float v13 = MathHelper.wrapDegrees((float)(v11 - v9));
        float v14 = MathHelper.wrapDegrees((float)(v12 - v10));
        float v15 = ((Float)param1.method762().getValue()).floatValue();
        if (Math.abs(v13) < v15) {
            v13 = 0.0f;
        }
        if (Math.abs(v14) < v15 * field10733) {
            v14 = 0.0f;
        }
        float v16 = ((Float)param1.method839().getValue()).floatValue();
        float v17 = ((Float)param1.method881().getValue()).floatValue();
        float v18 = v13 * v16;
        float v19 = v14 * v17;
        float v20 = ((Float)param1.method764().getValue()).floatValue();
        float v21 = ((Float)param1.method869().getValue()).floatValue();
        v18 = MathHelper.clamp((float)v18, (float)(-v20), (float)v20);
        v19 = MathHelper.clamp((float)v19, (float)(-v21), (float)v21);
        float v22 = ((Float)param1.method754().getValue()).floatValue() / field10734;
        if (this.field10704 > 0) {
            v18 *= field10735;
            v19 *= field10736;
            --this.field10704;
        } else if (Math.abs(v13) < field10737 && Math.abs(v14) < field10738 && ThreadLocalRandom.current().nextFloat() < v22 * field10739) {
            this.field10704 = ThreadLocalRandom.current().nextInt(1, 4);
        }
        float v23 = ((Float)param1.method806().getValue()).floatValue();
        float v24 = ((Float)param1.method857().getValue()).floatValue();
        if (v23 > 0.0f) {
            v18 += (ThreadLocalRandom.current().nextFloat() - field10740) * 2.0f * v23;
        }
        if (v24 > 0.0f) {
            v19 += (ThreadLocalRandom.current().nextFloat() - field10741) * 2.0f * v24;
        }
        float v25 = ((Float)param1.method789().getValue()).floatValue();
        float v26 = ((Float)param1.method787().getValue()).floatValue();
        if (v26 > 0.0f) {
            double v27 = (double)System.currentTimeMillis() * field10742;
            v29 = (float)Math.sin(v27 * (double)v25 * field10743 * field10744) * v26;
            v30 = (float)Math.cos(v27 * (double)v25 * field10745 * field10746 * field10747) * v26 * field10748;
            v18 += v29;
            v19 += v30;
        }
        float v27 = field10749;
        float v28 = field10750;
        v18 = MathHelper.lerp((float)v27, (float)v18, (float)this.field10705);
        v19 = MathHelper.lerp((float)v28, (float)v19, (float)this.field10706);
        v29 = v20 * field10751 + field10752;
        v30 = v21 * field10753 + field10754;
        v18 = MathHelper.clamp((float)v18, (float)(-Math.abs(v13) - v29), (float)(Math.abs(v13) + v29));
        v19 = MathHelper.clamp((float)v19, (float)(-Math.abs(v14) - v30), (float)(Math.abs(v14) + v30));
        if (((Boolean)param1.method886().getValue()).booleanValue()) {
            v31 = RotationTargetUtil.method1975();
            v18 = this.method3043(v18, v13, v31, false);
            v19 = this.method3043(v19, v14, v31, false);
        }
        this.field10705 = v18;
        this.field10706 = v19;
        v31 = v9 + v18;
        float v32 = MathHelper.clamp((float)(v10 + v19), (float)field10755, (float)field10756);
        int v33 = MathHelper.clamp((int)Math.round(((Float)param1.method779().getValue()).floatValue()), (int)0, (int)5);
        int v34 = Math.max(0, Math.round(((Float)param1.method834().getValue()).floatValue()));
        boolean v35 = (Boolean)param1.method866().getValue();
        AimRotation.method376(new Rotation(v31, v32), field10757, field10758, field10759, field10760, v34, v33, v35);
    }

    @Override
    public String getName() {
        return field10761;
    }

    private void method3046(float param1, float param2, float param3) {
        float v4 = MathHelper.wrapDegrees((float)(param2 - param1));
        if (v4 > field10762) {
            this.field10703 = -1;
        } else if (v4 < field10763) {
            this.field10703 = 1;
        }
        if (Math.abs(param3) > field10764) {
            this.field10703 = param3 > 0.0f ? 1 : -1;
        }
    }

    static {
        field10726 = Integer.MIN_VALUE;
        field10519 = "Новый";
        field10520 = 100.0f;
        field10521 = 0.55f;
        field10525 = 100.0f;
        field10526 = 0.12f;
        field10527 = 90.0;
        field10528 = 1.0E-4;
        field10529 = 90.0;
        field10530 = -89.9f;
        field10531 = 89.9f;
        field10532 = 0.35f;
        field10533 = 0.2f;
        field10534 = 5.5f;
        field10535 = 100.0f;
        field10536 = 100.0f;
        field10537 = 0.62f;
        field10538 = 0.55f;
        field10539 = 0.22f;
        field10540 = 0.32f;
        field10541 = 0.52f;
        field10542 = 0.48f;
        field10543 = 0.92f;
        field10544 = 0.2f;
        field10545 = 2.4f;
        field10546 = 0.7f;
        field10547 = 0.18f;
        field10548 = 0.64f;
        field10549 = 0.2f;
        field10550 = 4.6f;
        field10551 = 0.86f;
        field10552 = 0.12f;
        field10553 = 0.8f;
        field10554 = 0.14f;
        field10555 = 100.0f;
        field10556 = 3.2f;
        field10557 = 0.48f;
        field10558 = 1.85f;
        field10559 = 0.34f;
        field10560 = 100.0f;
        field10562 = 13.0f;
        field10563 = 0.55f;
        field10564 = 0.9f;
        field10565 = 0.22f;
        field10567 = 11.0f;
        field10568 = 0.85f;
        field10569 = 0.1f;
        field10570 = 0.15f;
        field10571 = 0.09f;
        field10572 = 0.12f;
        field10573 = 0.07f;
        field10575 = 0.28f;
        field10576 = 0.4f;
        field10577 = 100.0f;
        field10578 = 0.25f;
        field10579 = 0.995f;
        field10580 = 100.0f;
        field10581 = 0.25f;
        field10582 = 0.995f;
        field10584 = 0.75f;
        field10585 = 0.35f;
        field10586 = 0.7f;
        field10587 = 0.35f;
        field10588 = 0.45f;
        field10591 = 0.18f;
        field10592 = 0.52f;
        field10593 = 0.75f;
        field10594 = 0.035f;
        field10595 = 0.1f;
        field10596 = 1.8f;
        field10597 = 0.14f;
        field10598 = 0.3f;
        field10599 = 0.4f;
        field10600 = 100.0f;
        field10601 = 6.0f;
        field10602 = 0.3f;
        field10603 = 100.0f;
        field10604 = 5.0f;
        field10605 = 5.0f;
        field10606 = 3.0f;
        field10607 = 0.05f;
        field10608 = 0.06f;
        field10609 = 0.42f;
        field10610 = 0.02f;
        field10611 = 0.035f;
        field10612 = 0.07f;
        field10613 = 1.1f;
        field10614 = 6.5f;
        field10615 = 6.5f;
        field10616 = 0.1f;
        field10617 = 0.05f;
        field10618 = -1.0f;
        field10619 = -89.9f;
        field10620 = 89.9f;
        field10621 = 360.0f;
        field10622 = 360.0f;
        field10623 = 3.0f;
        field10624 = 3.0f;
        field10762 = 1.4f;
        field10763 = -1.4f;
        field10764 = 1.1f;
        field10707 = 100.0f;
        field10708 = 100.0f;
        field10709 = 100.0f;
        field10710 = 0.034;
        field10711 = 0.15;
        field10712 = 1.85;
        field10713 = 0.044;
        field10714 = 0.15;
        field10715 = 1.85;
        field10716 = 100.0f;
        field10717 = 0.12f;
        field10718 = 0.9;
        field10719 = 0.1;
        field10720 = 0.12f;
        field10721 = 0.22f;
        field10722 = 0.55f;
        field10723 = 1.0E-6;
        field10724 = 0.08;
        field10725 = 0.28;
        field10661 = 0.5f;
        field10663 = 0.88f;
        field10664 = 0.02f;
        field10665 = 100.0f;
        field10666 = 100.0f;
        field10668 = 2.8f;
        field10669 = 5.2f;
        field10670 = 0.65f;
        field10671 = 0.55f;
        field10672 = 4.0f;
        field10673 = 1.15f;
        field10674 = 0.35f;
        field10676 = 35L;
        field10677 = 110L;
        field10678 = 100.0f;
        field10679 = 0.08f;
        field10680 = 0.98f;
        field10681 = 0.001;
        field10682 = 11.7;
        field10683 = 0.55;
        field10684 = 19.3;
        field10685 = 0.35;
        field10686 = 7.1;
        field10687 = 0.25;
        field10688 = 0.55f;
        field10689 = 1.05f;
        field10690 = 0.4f;
        field10691 = 0.5f;
        field10692 = 1.2f;
        field10693 = 4.0f;
        field10694 = 0.45f;
        field10625 = 0.5f;
        field10627 = 0.88f;
        field10628 = 0.02f;
        field10629 = 100.0f;
        field10630 = 100.0f;
        field10632 = 1.6f;
        field10633 = 3.4f;
        field10634 = 0.12f;
        field10635 = 0.88f;
        field10636 = 0.55f;
        field10637 = 0.5f;
        field10638 = 3.0f;
        field10639 = 0.85f;
        field10640 = 0.4f;
        field10642 = 32L;
        field10643 = 95L;
        field10644 = 100.0f;
        field10645 = 0.08f;
        field10646 = 0.98f;
        field10647 = 0.001;
        field10648 = 14.2;
        field10649 = 0.5;
        field10650 = 21.0;
        field10651 = 0.3;
        field10652 = 0.35f;
        field10653 = 0.75f;
        field10654 = 0.15f;
        field10655 = 0.85f;
        field10656 = 0.35f;
        field10657 = 0.45f;
        field10658 = 0.9f;
        field10659 = 2.5f;
        field10660 = 0.35f;
        field10695 = 0.6f;
        field10696 = 4.0f;
        field10697 = 2.15f;
        field10698 = 0.33f;
        field10699 = -1.0f;
        field10727 = 100.0f;
        field10728 = 0.2f;
        field10729 = 1.0E-4;
        field10730 = 90.0;
        field10731 = -89.9f;
        field10732 = 89.9f;
        field10733 = 0.85f;
        field10734 = 100.0f;
        field10735 = 0.06f;
        field10736 = 0.06f;
        field10737 = 5.0f;
        field10738 = 5.0f;
        field10739 = 0.12f;
        field10740 = 0.5f;
        field10741 = 0.5f;
        field10742 = 0.001;
        field10743 = Math.PI;
        field10744 = 2.0;
        field10745 = 1.3;
        field10746 = Math.PI;
        field10747 = 2.0;
        field10748 = 0.65f;
        field10749 = 0.35f;
        field10750 = 0.4f;
        field10751 = 0.18f;
        field10752 = 0.4f;
        field10753 = 0.18f;
        field10754 = 0.3f;
        field10755 = -89.9f;
        field10756 = 89.9f;
        field10757 = 360.0f;
        field10758 = 360.0f;
        field10759 = 3.0f;
        field10760 = 3.0f;
        field10702 = Integer.MIN_VALUE;
    }
}



