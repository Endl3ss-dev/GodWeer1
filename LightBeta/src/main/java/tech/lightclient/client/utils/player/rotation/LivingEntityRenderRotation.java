package tech.lightclient.client.utils.player.rotation;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.entity.EntityRenderEvent;
import net.minecraft.util.math.MathHelper;

public class LivingEntityRenderRotation {

    public static float getYaw(EntityRenderEvent light$entityRenderEvent) {
        var RotationRenderModule = LightClient.getFunctionManager().getRotationRender();

        if (RotationTask.isRenderResetting() && RotationRenderModule.isState()) {
            return MathHelper.lerpAngleDegrees(
                    light$entityRenderEvent.getDelta(),
                    light$entityRenderEvent.getPrevYaw(),
                    light$entityRenderEvent.getYaw()
            );
        }
        return RotationTask.getRenderYaw();
    }

    public static float getPitch(EntityRenderEvent light$entityRenderEvent) {
        var RotationRenderModule = LightClient.getFunctionManager().getRotationRender();

        if (RotationTask.isRenderResetting() && RotationRenderModule.isState()) {
            return MathHelper.lerp(
                    light$entityRenderEvent.getDelta(),
                    light$entityRenderEvent.getPrevPitch(),
                    light$entityRenderEvent.getPitch()
            );
        }
        return RotationTask.getRenderPitch();
    }

    public static boolean shouldUseOriginalRotation() {
        var RotationRenderModule = LightClient.getFunctionManager().getRotationRender();

        return !RotationRenderModule.isState();
    }
}