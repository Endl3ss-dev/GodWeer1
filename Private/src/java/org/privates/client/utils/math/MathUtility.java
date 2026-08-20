package org.privates.client.utils.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class MathUtility {

    /* цю хуню не я писал это все какой-то lamzy */
    public static Vector2f rotationToEntity(Entity from, Entity to) {
        Vector3d eyePos = new Vector3d(from.getPosX(), from.getPosY() + from.getEyeHeight(), from.getPosZ());
        Vector3d targetPos = new Vector3d(to.getPosX(), to.getPosY() + to.getEyeHeight(), to.getPosZ());

        double dx = targetPos.x - eyePos.x;
        double dy = targetPos.y - eyePos.y;
        double dz = targetPos.z - eyePos.z;
        double distHorizontal = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
        float pitch = (float) (-Math.toDegrees(Math.atan2(dy, distHorizontal)));

        return new Vector2f(yaw, pitch);
    }

    public static Vector2f rotationToEntity(Entity entity) {
        return rotationToEntity(entity, entity);
    }
}
