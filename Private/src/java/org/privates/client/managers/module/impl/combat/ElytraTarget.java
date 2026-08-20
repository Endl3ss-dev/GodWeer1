package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShieldItem;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.Excellent;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.rotation.RayTraceUtil;
import org.privates.client.utils.rotation.SensUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ElytraTarget", category = Category.COMBAT, description = "Таргет далбаебов на хвх элитрах заебался уже эти дескрипты писать")
public class ElytraTarget extends Module {

    public static ElytraTarget getInstance() {
        return Instance.get(ElytraTarget.class);
    }

    final MultiBooleanSetting targetOptions = new MultiBooleanSetting(this, "Options",
            BooleanSetting.of("Elytra Predict", true),
            BooleanSetting.of("Always Hit", false)
    );

    final ModeSetting predictOption = new ModeSetting(this, "Predict Option", "Auto Distance", "Custom Distance");
    final ModeSetting bypass = new ModeSetting(this, "Bypass", "Default", "Legit");

    final SliderSetting predictDistance = new SliderSetting(this, "Predict Distance", 4.7f, 1.0f, 10.0f, 0.1f);
    final SliderSetting distance = new SliderSetting(this, "Distance", 3.0f, 1.0f, 5.0f, 0.1f);
    final SliderSetting preDistance = new SliderSetting(this, "Pre Distance", 15.0f, 1.0f, 30.0f, 1.0f);
    final SliderSetting delay = new SliderSetting(this, "Delay", 200.0f, 100.0f, 400.0f, 50.0f);
    final SliderSetting lazinessH = new SliderSetting(this, "Плавность H", 1.5F, 1F, 4F, 0.1F);
    final SliderSetting lazinessV = new SliderSetting(this, "Плавность V", 1.5F, 1F, 4F, 0.1F);

    PlayerEntity currentTarget = null;
    PlayerEntity lastTarget = null;
    Vector2f rotateVector = new Vector2f(0, 0);
    public final StopWatch timer = new StopWatch();
    public final StopWatch snapBypass = new StopWatch();
    public static float entityDistance = 100.0f;

    public double speed = 0.0;
    public double prevSpeed = 0.0;

    public ElytraTarget() {
        predictOption.setVisible(() -> targetOptions.getValue("Elytra Predict"));
        predictDistance.setVisible(() -> targetOptions.getValue("Elytra Predict") && predictOption.is("Custom Distance"));
        delay.setVisible(() -> bypass.is("Snap"));
        lazinessH.setVisible(() -> bypass.is("Legit"));
        lazinessV.setVisible(() -> bypass.is("Legit"));
    }

    @EventHandler
    private void onMotion(MotionEvent e) {
        if (currentTarget == null || !mc.player.isElytraFlying()) return;

        float yaw = rotateVector.x;
        float pitch = rotateVector.y;

        RotationComponent.update(new Rotation(yaw, pitch), 360, 360, 0, 5);

        e.setYaw(yaw);
        e.setPitch(pitch);
    }

    @EventHandler
    private void onUpdate(UpdateEvent e) {
        if (!mc.player.isElytraFlying()) {
            resetRotation();
            return;
        }

        updateTarget();

        if (currentTarget == null) {
            entityDistance = 100.0f;
            return;
        }

        if (entityDistance < 1.0f && bypass.is("Snap")) {
            snapBypass.reset();
        }

        updateRotation();
    }

    private void updateTarget() {
        currentTarget = sortEntities(currentTarget, distance.getValue(), preDistance.getValue());
        if (currentTarget != null) {
            lastTarget = currentTarget;
        }
    }

    private PlayerEntity sortEntities(PlayerEntity target, float distance, float preDistance) {
        return validEntities(distance, preDistance).stream()
                .min(Comparator.comparing((PlayerEntity e) -> e != target)
                        .thenComparingDouble(mc.player::getDistanceSq)
                        .thenComparingInt(Entity::getEntityId))
                .orElse(null);
    }

    private List<PlayerEntity> validEntities(float distance, float preDistance) {
        List<PlayerEntity> validTargets = new ArrayList<>();

        for (Entity entity : mc.world.getAllEntities()) {
            if (!(entity instanceof PlayerEntity playerEntity)) continue;

            if (playerEntity.getHealth() <= 0.0) continue;

            if (Excellent.inst().friendManager().isFriend(playerEntity.getGameProfile().getName())) {
                continue;
            }

            if (AntiBot.getInstance().isBot(playerEntity)) {
                continue;
            }

            if (isValidEntity(playerEntity, distance, preDistance)) {
                validTargets.add(playerEntity);
            }
        }

        return validTargets;
    }

    private boolean isValidEntity(PlayerEntity entity, float distance, float preDistance) {
        if (entity == mc.player) {
            return false;
        }
        return mc.player.getDistance(entity) <= distance + preDistance;
    }

    private void updateRotation() {
        if (currentTarget == null) return;

        boolean forward = targetOptions.getValue("Elytra Predict");
        Vector2f rawRotation = rotationAngles(currentTarget, forward);
        if (bypass.is("Legit")) {
            float smoothedYaw = smoothRotation(mc.player.rotationYaw, rawRotation.x, lazinessH.getValue());
            float smoothedPitch = MathHelper.clamp(smoothRotation(mc.player.rotationPitch, rawRotation.y, lazinessV.getValue()), -90f, 90f);
            rotateVector = new Vector2f(smoothedYaw, smoothedPitch);
        } else {
            rotateVector = rawRotation;
        }
        entityDistance = mc.player.getDistance(currentTarget);
    }

    public Vector2f rotationAngles(LivingEntity target, boolean forward) {
        if (timer.finished(200)) {
            double dx = target.getPosX() - target.prevPosX;
            double dy = target.getPosY() - target.prevPosY;
            double dz = target.getPosZ() - target.prevPosZ;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            prevSpeed = speed;
            speed = dist * 20.0;
            timer.reset();
        }

        float value = predictOption.is("Custom Distance") ? predictDistance.getValue() : (float) Math.sqrt(speed);
        Vector3d forwards = target.getLookVec().normalize().scale(value);
        Vector3d playerEyes = mc.player.getEyePosition(1.0f);

        boolean anti = speed >= 20.0 || (speed != prevSpeed && speed == 0.0);
        Vector3d entityPos = target.getPositionVec().add(0.0, target.getHeight() / 2.0, 0.0);

        if (forward && mc.player.isElytraFlying() && target.isElytraFlying() && anti && snapBypass.finished(delay.getValue().longValue())) {
            entityPos = target.getPositionVec().add(forwards);
        }

        if (mc.player.isElytraFlying()) {
            Vector3d difference = new Vector3d(
                    entityPos.getX() - mc.player.getPosX(),
                    entityPos.getY() - mc.player.getPosY(),
                    entityPos.getZ() - mc.player.getPosZ()
            );
            float rawYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0);
            float rawPitch = (float) MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(difference.y, Math.hypot(difference.x, difference.z))));
            return new Vector2f(rawYaw, rawPitch);
        }

        Vector3d direction = entityPos.subtract(playerEyes).normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float pitch = (float) Math.toDegrees(Math.asin(direction.y));

        return new Vector2f(yaw, -pitch);
    }

    private float smoothRotation(float currentAngle, float targetAngle, float smoothFactor) {
        float angleDifference = MathHelper.wrapDegrees(targetAngle - currentAngle);
        float adjustmentSpeed = Math.abs(angleDifference / smoothFactor);
        float angleAdjustment = adjustmentSpeed * Math.signum(angleDifference);

        if (Math.abs(angleAdjustment) > Math.abs(angleDifference)) {
            angleAdjustment = angleDifference;
        }

        return currentAngle + SensUtil.getSens(angleAdjustment);
    }

    public boolean canAttack(LivingEntity entity, float distance, boolean criticals) {
        AuraNew auraNew = AuraNew.getInstance();
        if (auraNew == null) return false;

        boolean negative = mc.player.isPotionActive(Effects.LEVITATION) ||
                mc.player.isPotionActive(Effects.SLOW_FALLING) ||
                mc.player.isPotionActive(Effects.BLINDNESS) ||
                mc.player.isPassenger() ||
                mc.player.isOnLadder();

        boolean waterCrits = mc.player.isInWater() && !mc.gameSettings.keyBindJump.isKeyDown();

        boolean flag = !criticals ||
                mc.player.areEyesInFluid(FluidTags.WATER) ||
                mc.player.isInLava() ||
                waterCrits ||
                negative ||
                mc.player.getBlockState().isIn(Blocks.COBWEB) ||
                mc.player.abilities.isFlying ||
                mc.player.isRidingHorse() ||
                (mc.player.fallDistance > 0.0f && !mc.player.isInWater() && !mc.player.isOnGround() && !mc.player.isOnLadder() && !mc.player.isPassenger());

        boolean flag2 = entityDistance <= distance || (targetOptions.getValue("Always Hit") && mc.player.getDistance(entity) <= distance);

        boolean flag3 = !mc.player.isHandActive() ||
                (mc.player.isHandActive() && mc.player.getActiveHand() == Hand.OFF_HAND && mc.player.getHeldItemOffhand().getItem() instanceof ShieldItem);

        Vector2f selfRotation = auraNew.getLerpRotation();
        boolean flag4 = blockResult(distance, selfRotation.x, selfRotation.y) != RayTraceResult.Type.BLOCK;

        boolean flag5 = (RayTraceUtil.rayTraceEntity(selfRotation.x, selfRotation.y, distance, entity) && mc.player.isElytraFlying()) ||
                (!mc.player.isElytraFlying() && flag) ||
                !bypass.is("Snap");

        return mc.player.getCooledAttackStrength(0.5f) > 0.9f && flag && flag2 && flag3 && flag4 && flag5;
    }

    private RayTraceResult.Type blockResult(float distance, float yaw, float pitch) {
        Vector3d startVec = mc.player.getEyePosition(1.0F);
        Vector3d direction = getVectorForRotation(pitch, yaw);
        Vector3d endVec = startVec.add(direction.x * distance, direction.y * distance, direction.z * distance);

        RayTraceResult result = mc.world.rayTraceBlocks(new RayTraceContext(
                startVec, endVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, mc.player
        ));

        return result.getType();
    }

    private Vector3d getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vector3d(f1 * f2, f3, f * f2);
    }


    private AxisAlignedBB smoothedBoundingBox(LivingEntity player, float delta) {
        Vector3d lerpedPos = player.getPositionVec();
        AxisAlignedBB originalBoundingBox = player.getBoundingBox();
        Vector3d offset = lerpedPos.subtract(player.getPosX(), player.getPosY(), player.getPosZ());
        return originalBoundingBox.offset(offset);
    }


    private void resetRotation() {
        currentTarget = null;
        rotateVector = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        entityDistance = 100.0f;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetRotation();
    }

    public PlayerEntity getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(PlayerEntity target) {
        this.currentTarget = target;
        if (target != null) {
            this.lastTarget = target;
        }
    }
}