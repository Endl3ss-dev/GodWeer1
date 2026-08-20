/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 */
package minced.core;

import minced.core.AuraModeHandler;
import minced.core.AuraItemUtil;
import minced.module.combat.AttackAura;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AuraModeVanilla
implements AuraModeHandler {
    AuraItemUtil field11160 = new AuraItemUtil();
    static String field11161 = "Polar";

    public AuraItemUtil method3147() {
        return this.field11160;
    }

    @Override
    public void reset(AttackAura param1) {
        this.field11160.method3084();
    }

    @Override
    public void onAttack(AttackAura param1, LivingEntity param2) {
        this.field11160.method3099(param1);
        this.field11160.method3088(param1);
    }

    @Override
    public void beforeAttack(AttackAura param1, LivingEntity param2) {
        if (param2 == null) {
            this.field11160.method3084();
            return;
        }
        this.field11160.method3074(param1, param2);
    }

    @Override
    public void onActivate(AttackAura param1) {
        this.field11160.method3078();
    }

    @Override
    public void onDeactivate(AttackAura param1) {
        this.field11160.method3084();
    }

    @Override
    public void updateRotation(AttackAura param1, LivingEntity param2) {
        if (param2 == null) {
            return;
        }
        Vec3d eye = ClientUtil.I.player.getEyePos();
        Vec3d target = param2.getBoundingBox().getCenter();
        Vec3d delta = target.subtract(eye);
        float yaw = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0);
        float pitch = (float)-Math.toDegrees(Math.atan2(delta.y, Math.hypot(delta.x, delta.z)));
        AimRotation.method367(new Rotation(yaw, pitch), 180.0f, 1, 6);
    }

    @Override
    public String getName() {
        return field11161;
    }
}



