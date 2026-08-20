/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.core;

import minced.core.MathUtil;
import minced.core.RotationTargetUtil;
import minced.core.AuraModeHandler;
import minced.core.Rotation;
import minced.core.AimRotation;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AuraModeSmooth
implements AuraModeHandler {
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field10789;
    static float field10790;
    static float field10791;
    static String field10792 = "Spooky Test";

    @Override
    public void updateRotation(AttackAura param1, LivingEntity param2) {
        if (AuraModeSmooth.I.player == null || param2 == null) {
            return;
        }
        Vec3d v3 = RotationTargetUtil.method1979((Entity)param2);
        float v4 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v3.z, v3.x)) - field10789));
        float v5 = (float)(-Math.toDegrees(Math.atan2(v3.y, Math.hypot(v3.x, v3.z))));
        float v6 = MathUtil.method1864(0.0f, field10790);
        float v8 = MathUtil.method1864(-v6, v6);
        AimRotation.method367(new Rotation(v4 + v8, v5 + 0.2958859f), field10791, 1, 6);
    }

    @Override
    public String getName() {
        return field10792;
    }

    static {
        field10789 = 90.0;
        field10790 = 3.0f;
        field10791 = 360.0f;
    }
}



