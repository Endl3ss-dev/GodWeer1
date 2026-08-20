package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.Legit;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.target.TargetComponent;
import org.privates.client.managers.events.other.GameUpdateEvent;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.MoveEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.events.world.WorldLoadEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.math.PerfectDelay;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.rotation.AuraUtil;
import org.privates.client.utils.rotation.RayTraceUtil;
import org.privates.client.utils.rotation.SensUtil;
import org.privates.lib.util.time.StopWatch;

@Category2

@Legit
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "LegitAura", category = Category.COMBAT, description = "Тоже самое що аура ток наводится по silent и она легитная")
public class LegitAura extends Module {

    public static LegitAura getInstance() {
        return Instance.get(LegitAura.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Default", "Snap", "Nexus");

    private final SliderSetting distance = new SliderSetting(this, "Дистанция", 4.5F, 3F, 6F, 0.1F);
    private final SliderSetting lazinessH = new SliderSetting(this, "Плавность yaw", 10F, 1F, 100F, 1F);
    private final SliderSetting lazinessV = new SliderSetting(this, "Плавность pitch", 10F, 1F, 100F, 1F);

    private final BooleanSetting onlyCrits = new BooleanSetting(this, "Только криты", true);
    private final BooleanSetting smartCrits = new BooleanSetting(this, "Умные криты", false).setVisible(onlyCrits::getValue);

    private final PerfectDelay perfectDelay = new PerfectDelay();
    private final StopWatch snapTimer = new StopWatch();
    private final StopWatch nexusTimer = new StopWatch();

    LivingEntity target;
    boolean canCrit;
    boolean shouldSnap;
    boolean shouldReleaseNexus;

    float savedYaw;
    float savedPitch;

    @Override
    public void toggle() {
        super.toggle();
        reset();
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        IPacket<?> packet = event.getPacket();
        if (packet instanceof CHeldItemChangePacket) {
            perfectDelay.reset(650L);
        } else if (packet instanceof CAnimateHandPacket) {
            perfectDelay.reset(500L);
        }
    }

    @EventHandler
    public void onEvent(WorldLoadEvent event) {
        reset();
    }

    @EventHandler
    public void onEvent(WorldChangeEvent event) {
        reset();
    }

    @EventHandler
    public void onEvent(MoveEvent event) {
        if (mc.player == null || mc.world == null) {
            canCrit = false;
            return;
        }
        final boolean fallCheck = mc.player.nextFallDistance != 0F;
        canCrit = !event.isToGround() && event.getFrom().y > event.getTo().y && fallCheck;
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            reset();
            return;
        }

        this.target = TargetComponent.getTarget(this.distance.getValue());

        if (mode.is("Default")) {
            handleDefaultMode();
        } else if (mode.is("Snap")) {
            handleSnapMode();
        } else if (mode.is("Nexus")) {
            handleNexusMode();
        }
    }

    @EventHandler
    public void onEvent(GameUpdateEvent event) {
        if (mode.is("Default")) {
            updateRotation();
        } else if (mode.is("Snap")) {
            if (shouldSnap && !snapTimer.finished(50)) {
                updateRotation();
            } else {
                shouldSnap = false;
            }
        } else if (mode.is("Nexus")) {
            if (!shouldReleaseNexus) {
                updateRotation();
            } else {
                if (nexusTimer.finished(50)) {
                    shouldReleaseNexus = false;
                }
            }
        }
    }

    private void handleDefaultMode() {
        if (mc.pointedEntity instanceof LivingEntity pointedTarget && !(mc.pointedEntity instanceof ArmorStandEntity)) {
            if (pointedTarget instanceof PlayerEntity player && Excellent.inst().friendManager().isFriend(player.getGameProfile().getName())) {
                return;
            }
            updateAttack(pointedTarget);
        }
    }

    private void handleSnapMode() {
        if (target != null && shouldAttack()) {
            if (RayTraceUtil.rayTraceEntity(mc.player.rotationYaw, mc.player.rotationPitch, attackDistance(), target) || mc.player.isElytraFlying()) {
                if (AuraUtil.getStrictDistance(target) < attackDistance()) {
                    shouldSnap = true;
                    snapTimer.reset();
                    performAttack(target);
                }
            }
        }
    }

    private void handleNexusMode() {
        if (mc.pointedEntity instanceof LivingEntity pointedTarget && !(mc.pointedEntity instanceof ArmorStandEntity)) {
            if (pointedTarget instanceof PlayerEntity player && Excellent.inst().friendManager().isFriend(player.getGameProfile().getName())) {
                return;
            }
            if (shouldAttack()) {
                if (RayTraceUtil.rayTraceEntity(mc.player.rotationYaw, mc.player.rotationPitch, attackDistance(), pointedTarget) || mc.player.isElytraFlying()) {
                    if (AuraUtil.getStrictDistance(pointedTarget) < attackDistance()) {
                        shouldReleaseNexus = true;
                        nexusTimer.reset();
                        performAttack(pointedTarget);
                    }
                }
            }
        }
    }

    private void updateAttack(LivingEntity target) {
        if (shouldAttack() && (RayTraceUtil.rayTraceEntity(mc.player.rotationYaw, mc.player.rotationPitch, attackDistance(), target) || mc.player.isElytraFlying()) && AuraUtil.getStrictDistance(target) < attackDistance()) {
            performAttack(target);
        }
    }

    private void performAttack(LivingEntity target) {
        boolean isInLiquid = mc.player.isActualySwimming() || mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA);
        boolean sprinting = mc.player.isSprinting();
        if (!isInLiquid && sprinting) {
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
            mc.player.setServerSprintState(false);
            mc.player.setSprinting(false);
        }
        attackEntity(target);
        canCrit = false;
    }

    private void attackEntity(Entity entity) {
        mc.playerController.attackEntity(mc.player, entity);
        mc.player.swingArm(Hand.MAIN_HAND);
    }

    private void updateRotation() {
        if (this.target == null) {
            return;
        }
        this.rotate(this.target, this.lazinessH.getValue(), this.lazinessV.getValue());
    }

    public void rotate(LivingEntity target, float lazinessH, float lazinessV) {
        final Vector4f rotation = AuraUtil.calculateRotation(target);

        mc.player.rotationYaw = smoothRotation(mc.player.rotationYaw, rotation.x, lazinessH);
        mc.player.rotationPitch = MathHelper.clamp(smoothRotation(mc.player.rotationPitch, rotation.y, lazinessV), -90F, 90F);
    }

    private float smoothRotation(float currentAngle, double targetAngle, float smoothFactor) {
        float angleDifference = (float) MathHelper.wrapDegrees(targetAngle - currentAngle);
        float adjustmentSpeed = Math.abs(angleDifference / smoothFactor);
        float angleAdjustment = (adjustmentSpeed * Math.signum(angleDifference));

        if (Math.abs(angleAdjustment) > Math.abs(angleDifference)) {
            angleAdjustment = angleDifference;
        }

        return currentAngle + SensUtil.getSens(angleAdjustment);
    }

    private boolean shouldAttack() {
        if (!perfectDelay.cooldownComplete() || !cooldownComplete()) return false;

        boolean isBlockAboveHead = PlayerUtil.isBlockAboveHead();

        boolean isInLiquid = mc.player.isActualySwimming() || mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA);

        if (isInLiquid) return true;

        if (onlyCrits.getValue() && !smartCrits.getValue()) {
            return shouldCritical()
                    && mc.player.fallDistance > 0
                    && canCrit
                    && isBlockAboveHead;
        }

        if (smartCrits.getValue()) {
            boolean canSmartCrit = isBlockAboveHead && !mc.player.movementInput.jump && !canCrit
                    && mc.player.isOnGround()
                    && mc.player.collidedVertically;

            return (shouldCritical() && mc.player.fallDistance > 0 && canCrit && isBlockAboveHead) || canSmartCrit;
        }

        return true;
    }

    private boolean shouldCritical() {
        boolean isDeBuffed = mc.player.isPotionActive(Effects.LEVITATION) || mc.player.isPotionActive(Effects.BLINDNESS) || mc.player.isPotionActive(Effects.SLOW_FALLING);
        boolean isInLiquid = mc.player.isActualySwimming() || mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER) || mc.player.areEyesInFluid(FluidTags.LAVA);
        boolean isFlying = mc.player.abilities.isFlying || mc.player.isElytraFlying();
        boolean isClimbing = mc.player.isOnLadder();
        boolean isCantJump = mc.player.isPassenger();
        boolean isOnWeb = PlayerUtil.isPlayerInWeb();

        return !(isDeBuffed || isInLiquid || isFlying || isClimbing || isCantJump || isOnWeb);
    }

    public boolean cooldownComplete() {
        return mc.player.getCooledAttackStrength(1.5F) >= 0.93F;
    }

    public double attackDistance() {
        return mc.playerController.extendedReach() ? 6.0D : 3.0D;
    }

    private void reset() {
        TargetComponent.clearTarget();
        TargetComponent.updateTargetList();
        this.target = null;
        canCrit = false;
        shouldSnap = false;
        shouldReleaseNexus = false;
        savedYaw = 0;
        savedPitch = 0;
    }
}