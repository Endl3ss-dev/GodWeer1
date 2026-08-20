/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.combat;
import minced.core.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventInput;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.RotationManager;
import minced.core.TimerUtil;
import minced.core.AimRotation;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.movement.AirStuck;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TargetStrafe
extends Module
implements Supplier {
    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field5664;
    Vec3d field5665 = null;
    ModeSetting field5666 = new ModeSetting(field5701, field5702, field5703, field5704);
    static String field5667;
    static double field5668;
    static float field5669;
    static float field5670;
    static float field5671;
    static float field5672;
    static float field5673;
    static float field5674;
    static float field5675;
    static float field5676;
    static float field5677;
    static double field5678;
    static float field5679;
    static float field5680;
    static float field5681;
    static float field5682;
    static float field5683;
    static float field5684;
    static long field5685;
    static double field5686;
    static double field5687;
    static double field5688;
    static double field5689;
    static double field5690;
    static double field5691;
    static double field5692;
    static double field5693;
    static String field5694;
    static String field5695;
    static String field5696;
    Map<LivingEntity, Vec3d> field5697;
    static String field5698;
    static String field5699;
    static String field5700;
    static String field5701;
    static String field5702;
    static String field5703;
    static String field5704;
    static String field5705;
    static String field5706;
    static float field5707;
    static float field5708;
    static float field5709;
    SliderSetting field5710;
    static String field5711;
    static float field5712;
    static float field5713;
    static float field5714;
    SliderSetting field5715;
    static String field5716;
    static float field5717;
    static float field5718;
    static float field5719;
    static float field5720;
    SliderSetting field5721;
    static double field5722;
    static String field5723;

    private void method1357(EventInput param1) {
        if (TargetStrafe.I.player == null || TargetStrafe.I.world == null) {
            this.method1368();
            return;
        }
        LivingEntity v2 = this.method1358();
        if (v2 == null) {
            this.method1368();
            return;
        }
        Vec3d v3 = v2.getPos();
        if (((Boolean)this.field5664.getValue()).booleanValue()) {
            v3 = this.method1363(v2, v3);
        } else {
            this.field5665 = null;
        }
        float v4 = this.method1369();
        this.method1361(param1, v3, v4);
    }

    private LivingEntity method1358() {
        AttackAura v1 = Minced.getInstance().getModuleManager().method177();
        if (v1 == null || !v1.isEnabled()) {
            return null;
        }
        AirStuck v2 = (AirStuck)Minced.getInstance().getModuleManager().method187(AirStuck.class);
        if (v2 != null && v2.isEnabled()) {
            return null;
        }
        LivingEntity v3 = AttackAura.method861();
        if (v3 == null || !v3.isAlive()) {
            return null;
        }
        return v3;
    }

    public LivingEntity method1360() {
        return this.method1358();
    }

    private void method1361(EventInput param1, Vec3d param2, float param3) {
        double v4 = param2.x - TargetStrafe.I.player.getX();
        double v6 = param2.z - TargetStrafe.I.player.getZ();
        double v8 = Math.toDegrees(Math.atan2(v6, v4)) - field5668;
        v8 = MathHelper.wrapDegrees((double)v8);
        float v10 = 0.0f;
        float v11 = 0.0f;
        float v12 = field5669;
        for (float v13 = field5670; v13 <= 1.0f; v13 += 1.0f) {
            for (float v14 = field5671; v14 <= 1.0f; v14 += 1.0f) {
                double v15;
                double v17;
                if (v13 == 0.0f && v14 == 0.0f || !((v17 = Math.abs(v8 - MathHelper.wrapDegrees((double)(v15 = Math.toDegrees(TimerUtil.method3548(param3, v13, v14)))))) < (double)v12)) continue;
                v12 = (float)v17;
                v10 = v13;
                v11 = v14;
            }
        }
        param1.method1973(v10);
        param1.method1972(v11);
    }

    private void method1362() {
        LivingEntity v1 = AttackAura.method861();
        if (v1 == null || TargetStrafe.I.player.distanceTo((Entity)v1) > field5672) {
            return;
        }
        Vec3d v2 = TargetStrafe.I.player.getPos();
        Vec3d v3 = v1.getPos();
        Vec3d v4 = v3.subtract(v2).normalize();
        BlockPos v5 = BlockPos.ofFloored((double)(TargetStrafe.I.player.getX() + TargetStrafe.I.player.getVelocity().x), (double)(TargetStrafe.I.player.getY() + TargetStrafe.I.player.getVelocity().y), (double)(TargetStrafe.I.player.getZ() + TargetStrafe.I.player.getVelocity().z));
        float v6 = TargetStrafe.I.world.getBlockState(v5).getBlock().getSlipperiness();
        float v7 = TargetStrafe.I.player.isOnGround() ? v6 : field5673;
        float v8 = TargetStrafe.I.player.isOnGround() ? v6 : field5674;
        double v9 = TargetStrafe.I.player.getVelocity().y;
        float v11 = field5675;
        float v12 = field5676;
        float v13 = field5677;
        double v14 = TargetStrafe.I.player.distanceTo((Entity)v1);
        if (v14 < field5678) {
            v11 *= field5679;
            v12 *= field5680;
            v13 *= field5681;
        } else if (v14 < 1.0) {
            v11 *= field5682;
            v12 *= field5683;
            v13 *= field5684;
        }
        float v16 = System.currentTimeMillis() / field5685;
        float cfr_ignored_0 = (float)Math.cos(Math.toDegrees(v16)) * 0.0f;
        float cfr_ignored_1 = (float)Math.sin(Math.toDegrees(v16)) * 0.0f;
        v4 = v4.add((double)0.0f, 0.0, (double)0.0f);
        double v20 = TargetStrafe.I.player.isOnGround() ? (double)v11 : (TargetStrafe.I.player.fallDistance > 0.0f ? (double)v12 : (double)v13);
        double v22 = v4.x * v20 * (double)v8 / (double)v7;
        double v24 = v4.z * v20 * (double)v8 / (double)v7;
        TargetStrafe.I.player.setVelocity(TargetStrafe.I.player.getVelocity().x + v22, v9, TargetStrafe.I.player.getVelocity().z + v24);
    }

    private Vec3d method1363(LivingEntity param1, Vec3d param2) {
        double v7;
        double v5;
        double v9;
        double v3 = this.method1370(param1);
        if (v3 >= field5686 && (v9 = Math.sqrt((v5 = param1.getX() - param1.prevX) * v5 + (v7 = param1.getZ() - param1.prevZ) * v7)) > field5687) {
            double v11 = v5 / v9;
            double v13 = v7 / v9;
            double v15 = v3 >= field5688 ? field5689 : (v3 >= field5690 ? 1.0 : (v3 >= field5691 ? field5692 : field5693));
            this.field5665 = param2.add(v11 * v15, 0.0, v13 * v15);
            return this.field5665;
        }
        this.field5665 = param2;
        return this.field5665;
    }

    @EventHandler(2)
    public void method1365(EventUpdate param1) {
        if (this.field5666.isMode(field5695)) {
            this.method1362();
        }
    }

    @EventHandler(2)
    public void method1366(EventInput param1) {
        if (this.field5666.isMode(field5696)) {
            this.method1357(param1);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.method1368();
        this.field5697.clear();
    }

    private void method1368() {
        this.field5665 = null;
    }

    private float method1369() {
        if (AimRotation.method357().method368() && AttackAura.method861() != null) {
            return RotationManager.method255();
        }
        return TargetStrafe.I.player.getYaw();
    }

    public TargetStrafe() {
        super(field5699, field5700, Combat.Combat);
        this.field5664 = new BooleanSetting(field5705, true, () -> this.field5666.isMode(field5667));
        this.field5710 = new SliderSetting(field5706, field5707, 0.0f, field5708, field5709, () -> this.field5666.isMode(field5694));
        this.field5715 = new SliderSetting(field5711, 2.0f, field5712, field5713, field5714, () -> this.field5666.isMode(field5698));
        this.field5721 = new SliderSetting(field5716, field5717, field5718, field5719, field5720, () -> this.field5666.isMode(field5723));
        this.field5697 = new HashMap<>();
        this.addSettings(this.field5666, this.field5664, this.field5710, this.field5715, this.field5721);
    }

    private double method1370(LivingEntity param1) {
        Vec3d v2 = param1.getPos();
        Vec3d v3 = this.field5697.getOrDefault(param1, v2);
        double v4 = v2.x - v3.x;
        double v6 = v2.z - v3.z;
        double v8 = Math.sqrt(v4 * v4 + v6 * v6) * field5722;
        this.field5697.put(param1, v2);
        return v8;
    }

    @Override
    public Object get() {
        return Boolean.valueOf(this.method1358() != null);
    }

    public boolean method1371() {
        return this.field5666.isMode(field5667);
    }

    static {
        field5699 = "Target Strafe";
        field5700 = "Позволяет двигаться вокруг цели по кругу, улучшая маневренность в бою";
        field5701 = "Режим";
        field5702 = "Collision";
        field5703 = "Collision";
        field5704 = "HolyWorld New";
        field5705 = "Обгон";
        field5706 = "Предсказание";
        field5707 = 5.0f;
        field5708 = 10.0f;
        field5709 = 0.1f;
        field5711 = "Радиус ускорения";
        field5712 = 0.5f;
        field5713 = 5.0f;
        field5714 = 0.1f;
        field5716 = "Сила";
        field5717 = 0.03f;
        field5718 = 0.01f;
        field5719 = 0.1f;
        field5720 = 0.01f;
        field5696 = "HolyWorld New";
        field5695 = "Collision";
        field5672 = 1.5f;
        field5673 = 0.91f;
        field5674 = 0.99f;
        field5675 = 0.05f;
        field5676 = 0.05f;
        field5677 = 0.05f;
        field5678 = 0.8f;
        field5679 = 0.3f;
        field5680 = 0.3f;
        field5681 = 0.3f;
        field5682 = 0.65f;
        field5683 = 0.65f;
        field5684 = 0.65f;
        field5685 = 100L;
        field5686 = 4.0;
        field5687 = 0.01;
        field5688 = 7.0;
        field5689 = 1.25;
        field5690 = 6.0;
        field5691 = 5.0;
        field5692 = 0.75;
        field5693 = 0.5;
        field5668 = 90.0;
        field5669 = Float.MAX_VALUE;
        field5670 = -1.0f;
        field5671 = -1.0f;
        field5722 = 20.0;
        field5723 = "HolyWorld New";
        field5698 = "HolyWorld New";
        field5694 = "HolyWorld New";
        field5667 = "HolyWorld New";
    }
}



