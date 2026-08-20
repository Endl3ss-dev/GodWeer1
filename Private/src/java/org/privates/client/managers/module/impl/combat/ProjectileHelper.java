package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.annotations.Funtime;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.GCDUtils;
import org.privates.client.utils.other.Instance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.minecraft.util.math.MathHelper.clamp;

@Category3
@Funtime
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ProjectileHelper", category = Category.COMBAT, description = "А тут я не помню навайбкодил хуйни теперь сиди разбирай")
public class ProjectileHelper extends Module {
    public static ProjectileHelper getInstance() {
        return Instance.get(ProjectileHelper.class);
    }

    private final MultiBooleanSetting weapons = new MultiBooleanSetting(this, "Оружие",
            BooleanSetting.of("Лук", true),
            BooleanSetting.of("Трезубец", true));
    private final SliderSetting aimRange = new SliderSetting(this, "Дистанция наводки", 30.0f, 10.0f, 50.0f, 1.0f);
    
    private final SliderSetting aimSpeed = new SliderSetting(this, "Скорость наводки", 180.0f, 30.0f, 360.0f, 5.0f);
    private final SliderSetting returnSpeed = new SliderSetting(this, "Скорость отводки", 120.0f, 30.0f, 360.0f, 5.0f);
    private final SliderSetting smoothness = new SliderSetting(this, "Плавность", 0.3f, 0.0f, 1.0f, 0.05f);

    private final MultiBooleanSetting targets = new MultiBooleanSetting(this, "Цели",
            BooleanSetting.of("Игроки в броне", true),
            BooleanSetting.of("Игроки без брони", true),
            BooleanSetting.of("Мобы", false));

    private LivingEntity target;
    private Vector2f rotation = new Vector2f(0, 0);
    private Vector2f targetRotation = new Vector2f(0, 0);
    private boolean wasCharging = false;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            reset();
            return;
        }

        boolean charging = isValidItem() && mc.player.isHandActive() && mc.player.getActiveHand() == Hand.MAIN_HAND;

        if (charging) {
            // Continuously update target and apply rotation while charging
            updateTarget();
            
            if (target != null) {
                applySmoothRotation(true);
            }
            wasCharging = true;
        } else {
            if (wasCharging) {
                // Return to original rotation smoothly
                applySmoothRotation(false);
            }
            wasCharging = false;
        }
    }

    private void applySmoothRotation(boolean aiming) {
        if (aiming && target == null) return;

        float currentYaw = rotation.x;
        float currentPitch = rotation.y;
        float desiredYaw, desiredPitch;

        if (aiming) {
            // Calculate rotation to target
            Vector3d targetPos = target.getPositionVec().add(0, target.getHeight() * 0.5, 0);
            Vector3d aimVec = targetPos.subtract(mc.player.getEyePosition(1.0F));

            desiredYaw = (float) Math.toDegrees(Math.atan2(aimVec.z, aimVec.x)) - 90.0f;
            desiredPitch = (float) (-Math.toDegrees(Math.atan2(aimVec.y, Math.sqrt(aimVec.x * aimVec.x + aimVec.z * aimVec.z))));
        } else {
            // Return to original view
            desiredYaw = mc.player.rotationYaw;
            desiredPitch = mc.player.rotationPitch;
        }

        // Apply smoothness
        float smooth = smoothness.getValue();
        float speed = aiming ? aimSpeed.getValue() : returnSpeed.getValue();
        
        // Interpolate rotation
        float yawDiff = desiredYaw - currentYaw;
        float pitchDiff = desiredPitch - currentPitch;
        
        // Normalize yaw difference to -180 to 180
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;
        
        // Apply smoothness and speed
        float yawStep = yawDiff * (1.0f - smooth);
        float pitchStep = pitchDiff * (1.0f - smooth);
        
        // Clamp by speed
        float maxStep = speed / 20.0f; // Convert to per-tick speed
        yawStep = clamp(yawStep, -maxStep, maxStep);
        pitchStep = clamp(pitchStep, -maxStep, maxStep);
        
        float newYaw = currentYaw + yawStep;
        float newPitch = clamp(currentPitch + pitchStep, -90.0f, 90.0f);

        rotation = new Vector2f(
                GCDUtils.getSensitivity(newYaw),
                GCDUtils.getSensitivity(newPitch)
        );
        
        mc.player.rotationYaw = rotation.x;
        mc.player.rotationPitch = rotation.y;

        // Update rotation component
        RotationComponent.update(new Rotation(rotation.x, rotation.y), speed, speed * 0.5f, 0, 5);
    }

    private void updateTarget() {
        List<LivingEntity> potentialTargets = new ArrayList<>();

        for (Entity entity : mc.world.getAllEntities()) {
            if (isValidTarget(entity)) {
                potentialTargets.add((LivingEntity) entity);
            }
        }

        potentialTargets.sort(Comparator.comparingDouble(this::calculateTargetPriority));
        target = potentialTargets.isEmpty() ? null : potentialTargets.get(0);
    }

    private double calculateTargetPriority(LivingEntity entity) {
        double distance = mc.player.getDistanceSq(entity);

        double priority = distance;

        if (entity instanceof PlayerEntity) {
            priority *= 0.8;
        }

        double speed = Math.sqrt(
                Math.pow(entity.getPosX() - entity.lastTickPosX, 2) +
                        Math.pow(entity.getPosZ() - entity.lastTickPosZ, 2)
        );
        if (speed > 0.1) {
            priority *= 0.9;
        }

        float health = entity.getHealth() + entity.getAbsorptionAmount();
        priority *= (1.0 + health * 0.01);

        return priority;
    }

    private boolean isValidTarget(Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;
        if (entity instanceof ClientPlayerEntity) return false;
        if (!entity.isAlive() || entity.ticksExisted < 10 || entity.isInvulnerable()) return false;
        if (mc.player.getDistanceSq(entity) > aimRange.getValue() * aimRange.getValue()) return false;
        if (entity.getName().getString().equalsIgnoreCase(mc.player.getName().getString())) return false;

        if (entity instanceof PlayerEntity player) {
            if (player.getTotalArmorValue() > 0) {
                return targets.getValue("Игроки в броне");
            } else {
                return targets.getValue("Игроки без брони");
            }
        }

        if (entity instanceof MonsterEntity) {
            return targets.getValue("Мобы");
        }

        return false;
    }

    private boolean isValidItem() {
        ItemStack item = mc.player.getHeldItemMainhand();
        if (item.isEmpty()) return false;
        if (item.getItem() instanceof BowItem) return weapons.getValue("Лук");
        if (item.getItem() instanceof TridentItem) return weapons.getValue("Трезубец");
        return false;
    }

    private void reset() {
        if (mc.player != null) {
            rotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
            targetRotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        }
        target = null;
        wasCharging = false;
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        reset();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        reset();
    }
}