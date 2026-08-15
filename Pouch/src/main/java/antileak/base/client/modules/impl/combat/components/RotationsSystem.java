package antileak.base.client.modules.impl.combat.components;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import antileak.base.api.QClient;
import antileak.base.client.modules.impl.combat.components.gcd.GCDUtil;

public abstract class RotationsSystem implements QClient {

    public Vec2f rotate = Vec2f.ZERO;

    public abstract void updateRotations(LivingEntity target);

    public static Vec2f correctRotation(float yaw, float pitch) {
        if ((yaw == -90 && pitch == 90) || yaw == -180) return new Vec2f(mc.player.getYaw(), mc.player.getPitch());

        float gcd = GCDUtil.getGCD();
        yaw -= yaw % gcd;
        pitch -= pitch % gcd;

        return new Vec2f(yaw, pitch);
    }

    protected Vec3d getPredictedPoint(LivingEntity target, Vec3d point) {
        return point;
    }

    protected Box getPredictedBox(LivingEntity target) {
        return target.getBoundingBox();
    }
}
