/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.core;

import minced.core.RotationTargetUtil;
import minced.core.AuraModeHandler;
import minced.core.Rotation;
import minced.core.AimRotation;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AuraModeMatrix
implements AuraModeHandler {
    boolean field11134;
    float field11135 = 0.0f;
    static MinecraftClient I = MinecraftClient.getInstance();
    float field11136 = 0.0f;
    static float field11137;
    static float field11138;
    float field11139 = 0.0f;
    static String field11140 = "Matrix";
    static double field11141;
    static float field11142;
    static float field11143;
    static double field11144;
    static double field11145;
    static float field11146;
    static float field11147;
    static float field11148;
    static float field11149;
    static float field11150;
    static float field11151;
    static float field11152;
    static float field11153;
    static float field11154;
    static float field11155;
    static float field11156;
    static float field11157;
    static float field11158;

    @Override
    public void onDeactivate(AttackAura param1) {
        this.reset(param1);
    }

    public void method3145() {
        this.field11134 = false;
        this.field11135 = 0.0f;
        if (AuraModeMatrix.I.player != null) {
            this.field11136 = AuraModeMatrix.I.player.getYaw();
            this.field11139 = MathHelper.clamp((float)AuraModeMatrix.I.player.getPitch(), (float)field11137, (float)field11138);
        } else {
            this.field11136 = 0.0f;
            this.field11139 = 0.0f;
        }
    }

    private Box O(LivingEntity param1) {
        if (param1 == null) {
            return new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        }
        return param1.getBoundingBox();
    }

    @Override
    public void onActivate(AttackAura param1) {
        this.method3145();
    }

    @Override
    public String getName() {
        return field11140;
    }

    @Override
    public void reset(AttackAura param1) {
        this.field11135 = 0.0f;
        this.field11136 = 0.0f;
        this.field11139 = 0.0f;
    }

    @Override
    public void updateRotation(AttackAura param1, LivingEntity param2) {
        if (AuraModeMatrix.I.player == null || param2 == null) {
            return;
        }
        Box v3 = this.O(param2);
        Vec3d v4 = AuraModeMatrix.I.player.getCameraPosVec(1.0f);
        Vec3d v5 = v3.getCenter();
        Vec3d v6 = v5.subtract(v4);
        float v7 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v6.z, v6.x)) - field11141));
        float v8 = (float)(-Math.toDegrees(Math.atan2(v6.y, Math.hypot(v6.x, v6.z))));
        v8 = MathHelper.clamp((float)v8, (float)field11142, (float)field11143);
        Vec3d v9 = AuraModeMatrix.I.player.getRotationVector(AuraModeMatrix.I.player.getPitch(), AuraModeMatrix.I.player.getYaw());
        Vec3d v10 = v4.add(v9.multiply(field11144));
        Box v11 = v3.expand(field11145);
        boolean v12 = v11.raycast(v4, v10).isPresent();
        if (this.field11134) {
            if (this.field11135 >= field11146) {
                this.field11135 -= Math.abs(MathHelper.wrapDegrees((float)(v7 - this.field11136))) > field11147 ? field11148 : field11149;
            }
            if (this.field11135 <= field11150) {
                this.field11134 = false;
            }
        } else {
            this.field11135 += field11151;
            if (this.field11135 >= field11152 || v12) {
                this.field11134 = true;
            }
        }
        float v13 = MathHelper.wrapDegrees((float)(v7 - this.field11136));
        float v14 = v8 - this.field11139;
        float v15 = Math.max(this.field11135, 0.0f);
        float v16 = this.field11136 + v13 * MathHelper.clamp((float)(v15 * field11153), (float)0.0f, (float)1.0f);
        float v17 = this.field11139 + v14 * MathHelper.clamp((float)(v15 / field11154), (float)0.0f, (float)1.0f);
        float v18 = RotationTargetUtil.method1975();
        v16 -= (v16 - this.field11136) % v18;
        v17 -= (v17 - this.field11139) % v18;
        v17 = MathHelper.clamp((float)v17, (float)field11155, (float)field11156);
        this.field11136 = v16;
        this.field11139 = v17;
        AimRotation.method369(new Rotation(v16, v17), field11157, field11158, 1, 6);
    }

    static {
        field11141 = 90.0;
        field11142 = -40.0f;
        field11143 = 40.0f;
        field11144 = 999.0;
        field11145 = -0.5;
        field11146 = -0.01f;
        field11147 = 80.0f;
        field11148 = 0.055f;
        field11149 = 0.0055f;
        field11150 = -0.01f;
        field11151 = 0.0034f;
        field11152 = 0.36f;
        field11153 = 1.3f;
        field11154 = 1.7f;
        field11155 = -40.0f;
        field11156 = 40.0f;
        field11157 = 45.0f;
        field11158 = 15.0f;
        field11137 = -40.0f;
        field11138 = 40.0f;
    }
}



