package nuclear.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import nuclear.control.Manager;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventEntityHitBox;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.utils.misc.PredictUtility;

@ModuleDescriptor(
    name = "ElytraTarget",
    type = ModuleCategory.Combat,
    desc = "Expands the target hitbox while chasing on elytra"
)
public class ElytraTarget extends Module {
    private final BooleanSetting onlyWhenFlying = new BooleanSetting("Only while flying", true);
    private final BooleanSetting onlyAuraTarget = new BooleanSetting("Only aura target", true);
    private final BooleanSetting targetMustFly = new BooleanSetting("Target must fly", true);
    private final SliderSetting extraHitbox = new SliderSetting("Extra hitbox", 0.35f, 0.0f, 1.0f, 0.05f);
    private final SliderSetting predictionTicks = new SliderSetting("Prediction ticks", 4.0f, 0.0f, 12.0f, 1.0f);

    private PlayerEntity currentTarget;
    private Vector3d predictedPosition = Vector3d.ZERO;

    public ElytraTarget() {
        addSettings(onlyWhenFlying, onlyAuraTarget, targetMustFly, extraHitbox, predictionTicks);
    }

    @Override
    public boolean onEvent(Event event) {
        if (mc.player == null || mc.world == null) {
            currentTarget = null;
            predictedPosition = Vector3d.ZERO;
            return false;
        }

        if (event instanceof EventUpdate) {
            updateTarget();
        } else if (event instanceof EventEntityHitBox) {
            handleHitbox((EventEntityHitBox) event);
        }
        return false;
    }

    private void updateTarget() {
        currentTarget = resolveTarget();
        if (currentTarget != null) {
            predictedPosition = PredictUtility.predictElytraPos(currentTarget, predictionTicks.getValue().intValue());
        } else {
            predictedPosition = Vector3d.ZERO;
        }
    }

    private PlayerEntity resolveTarget() {
        if (onlyAuraTarget.get()) {
            LivingEntity auraTarget = AttackAura.target;
            if (auraTarget instanceof PlayerEntity) {
                return (PlayerEntity) auraTarget;
            }
            return null;
        }

        PlayerEntity best = null;
        double bestDistance = Double.MAX_VALUE;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive()) {
                continue;
            }
            if (Manager.FRIEND_MANAGER != null && Manager.FRIEND_MANAGER.isFriend(player.getName().getString())) {
                continue;
            }
            if (targetMustFly.get() && !player.isElytraFlying()) {
                continue;
            }

            double distance = mc.player.getDistanceSq(player);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = player;
            }
        }

        return best;
    }

    private void handleHitbox(EventEntityHitBox event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof PlayerEntity)) {
            return;
        }
        if (onlyWhenFlying.get() && !mc.player.isElytraFlying()) {
            return;
        }
        if (currentTarget == null || entity != currentTarget) {
            return;
        }
        if (targetMustFly.get() && !currentTarget.isElytraFlying()) {
            return;
        }

        float size = extraHitbox.getValue().floatValue();
        if (predictedPosition != null) {
            double leadDistance = currentTarget.getPositionVec().distanceTo(predictedPosition);
            size += (float) Math.min(0.35, leadDistance * 0.04);
        }

        if (event.getSize() < size) {
            event.setSize(size);
        }
    }
}
