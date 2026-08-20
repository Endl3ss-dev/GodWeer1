package fun.volon.client.modules.impl.movement;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import fun.volon.api.QClient;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.combat.PredictUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.impl.combat.Aura;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;

public class SpeedAres extends Module implements QClient {

    public static SpeedAres INSTANCE = new SpeedAres();

    private final FloatSetting speed = new FloatSetting("Скорость", 1.05f, 1.f, 1.6f, 0.1f);
    private final FloatSetting radius = new FloatSetting("Радиус", 1.0f, 0.01f, 3.0f, 0.1f);
    private final FloatSetting predict = new FloatSetting("Предикт", 1.0f, 0.0f, 5.0f, 0.1f);
    private final BooleanSetting onlyElytra = new BooleanSetting("Только на элитре", false);

    public SpeedAres() {
        super("SpeedAres", "Ускорение при столкновении с целью", ModuleCategory.MOVEMENT);
        addSettings(speed, radius, predict, onlyElytra);
    }

    @EventLink
    private void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;
        collisionSpeed();
    }

    private void collisionSpeed() {
        Aura aura = ModuleClass.INSTANCE.aura;
        if (aura == null || !aura.isEnable()) return;

        LivingEntity target = aura.getTarget();
        if (target == null || target == mc.player) return;

        if (onlyElytra.isState() && !mc.player.isGliding()) return;

        Box expandedBox = mc.player.getBoundingBox().expand(radius.getValue().doubleValue());

        boolean canSpeed = false;

        if (mc.player.isGliding() || target.getBoundingBox().intersects(expandedBox)) {
            if (mc.player.isGliding()) {
                Vec3d predictedPos = PredictUtils.predict(target, target.getPos(), predict.getValue().intValue());
                double distanceToPredict = mc.player.getEyePos().distanceTo(predictedPos);
                double distanceToTarget = mc.player.getEyePos().distanceTo(target.getBoundingBox().getCenter());

                if (distanceToPredict <= 2.5D || distanceToTarget <= 2.5D) {
                    canSpeed = true;
                }
            } else {
                canSpeed = true;
            }
        }

        if (canSpeed) {
            Vec3d newVelocity = calculateVelocity(target);
            mc.player.setVelocity(newVelocity);
        }
    }

    @NotNull
    private Vec3d calculateVelocity(LivingEntity target) {
        Vec3d predictedPos = PredictUtils.predict(target, target.getPos(), predict.getValue().intValue());
        double deltaX = predictedPos.x - mc.player.getX();
        double deltaZ = predictedPos.z - mc.player.getZ();

        float targetYaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0D);
        double radYaw = Math.toRadians(targetYaw);

        double force = 0.072D * speed.getValue().doubleValue();

        Vec3d currentVelocity = mc.player.getVelocity();

        return new Vec3d(
                currentVelocity.x + -Math.sin(radYaw) * force,
                currentVelocity.y,
                currentVelocity.z + Math.cos(radYaw) * force
        );
    }
}
