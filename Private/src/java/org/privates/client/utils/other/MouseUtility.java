package org.privates.client.utils.other;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.interfaces.IMinecraft;

import java.util.Optional;

public class MouseUtility implements IMinecraft {
    public static Entity getMouseOver(Entity target, float yaw, float pitch, double range) {
        if (mc.player == null || target == null) return null;

        Vector3d vec3d = mc.player.getEyePosition(1.0F);
        Vector3d lookVec = getVectorForRotation(pitch, yaw);
        Vector3d vec3d2 = vec3d.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);

        AxisAlignedBB axisalignedbb = mc.player.getBoundingBox().expand(lookVec.scale(range)).grow(1.0D, 1.0D, 1.0D);
        EntityRayTraceResult entityraytraceresult = rayTraceEntities(mc.player, vec3d, vec3d2, axisalignedbb, (entity) ->
                entity == target, range);

        return entityraytraceresult == null ? null : entityraytraceresult.getEntity();
    }

    private static Vector3d getVectorForRotation(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180F);
        float f1 = -yaw * ((float) Math.PI / 180F);
        float f2 = MathHelper.cos(f1);
        float f3 = MathHelper.sin(f1);
        float f4 = MathHelper.cos(f);
        float f5 = MathHelper.sin(f);
        return new Vector3d(f3 * f4, -f5, f2 * f4);
    }

    private static EntityRayTraceResult rayTraceEntities(Entity shooter, Vector3d start, Vector3d end,
                                                         AxisAlignedBB boundingBox, java.util.function.Predicate<Entity> filter, double distance) {
        Entity entity = null;
        Vector3d vector3d = null;
        double d0 = distance;

        AxisAlignedBB targetBox = boundingBox.grow(0.3);
        Optional<Vector3d> hit = targetBox.rayTrace(start, end);

        if (hit.isPresent()) {
            vector3d = hit.get();
            double d1 = start.squareDistanceTo(vector3d);
            if (d1 < d0 || d0 == 0.0D) {
                entity = shooter;
                d0 = d1;
            }
        }

        return entity == null ? null : new EntityRayTraceResult(entity, vector3d);
    }
}