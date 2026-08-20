package fun.volon.api.utils.combat;

import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import fun.volon.api.QClient;

@UtilityClass
public class CrystalAuraDamage implements QClient {

    private static final double MAX_DISTANCE = 12.0;

    public float getCrystalDamage(Vec3d explosionPos, LivingEntity target) {
        if (mc.world == null || target == null || explosionPos == null) {
            return 0f;
        }

        double dist = Math.sqrt(target.squaredDistanceTo(explosionPos));
        if (dist > MAX_DISTANCE) {
            return 0f;
        }

        float exposure = getExposure(explosionPos, target);
        double impact = (1.0 - (dist / MAX_DISTANCE)) * exposure;
        return (float) ((impact * impact + impact) / 2.0 * 7.0 * MAX_DISTANCE + 1.0);
    }

    public float getSelfDamage(Vec3d explosionPos) {
        return mc.player == null ? 0f : getCrystalDamage(explosionPos, mc.player);
    }

    public boolean canSee(Vec3d pos) {
        if (mc.player == null || mc.world == null || pos == null) {
            return false;
        }
        return RayTraceUtil.raycast(mc.player.getEyePos(), pos, RaycastContext.ShapeType.COLLIDER).getType()
                != HitResult.Type.BLOCK;
    }

    private float getExposure(Vec3d source, Entity entity) {
        Box box = entity.getBoundingBox();
        Vec3d[] samples = {
                box.getCenter(),
                new Vec3d(box.minX, box.minY, box.minZ),
                new Vec3d(box.minX, box.minY, box.maxZ),
                new Vec3d(box.minX, box.maxY, box.minZ),
                new Vec3d(box.minX, box.maxY, box.maxZ),
                new Vec3d(box.maxX, box.minY, box.minZ),
                new Vec3d(box.maxX, box.minY, box.maxZ),
                new Vec3d(box.maxX, box.maxY, box.minZ),
                new Vec3d(box.maxX, box.maxY, box.maxZ)
        };

        float exposure = 0f;
        for (Vec3d sample : samples) {
            HitResult hit = mc.world.raycast(new RaycastContext(
                    source,
                    sample,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    entity
            ));
            if (hit.getType() == HitResult.Type.MISS) {
                exposure += 1f;
            }
        }
        return exposure / samples.length;
    }
}
