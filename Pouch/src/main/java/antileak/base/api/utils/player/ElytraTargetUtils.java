package antileak.base.api.utils.player;

import net.minecraft.entity.LivingEntity;
import antileak.base.api.QClient;
public class ElytraTargetUtils implements QClient {
    public static void updateNumber() {
    }

    public static float getJitterYaw(LivingEntity entity) {
        return 0;
    }

    public static float getJitterPitch(LivingEntity entity) {
        return 0;
    }

    public static boolean canTarget(LivingEntity target) {
        if (target == null) return false;
        if (mc.player.getAttackCooldownProgress(1.5f) < 0.94f) return true;
        if (mc.player.isUsingItem()) return true;

        return false;
    }

    public static boolean fullCheck() {
        if (mc.player == null || mc.world == null) return false;
        return mc.player.isGliding();
    }
}
