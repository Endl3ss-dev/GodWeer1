package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.Legit;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.EntityHitBoxEvent;
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
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.math.PerfectDelay;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Category2

@Legit
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "SlothAura", category = Category.COMBAT, description = "AttackAura с обходом античита Sloth (CakeWorld & WellMine)")
public class SlothAura extends Module {

    public static SlothAura getInstance() {
        return Instance.get(SlothAura.class);
    }

    private final MultiBooleanSetting targets = new MultiBooleanSetting(this, "Цели",
            BooleanSetting.of("Игроки", true),
            BooleanSetting.of("Мобы", true)
    );

    private final SliderSetting attackRange = new SliderSetting(this, "Дистанция атаки", 3.0f, 2.0f, 6.0f, 0.1f);

    private final BooleanSetting hitboxExpand = new BooleanSetting(this, "Расширение хитбоксов", true);
    private final SliderSetting hitboxMinSize = new SliderSetting(this, "Мин. хитбокс", 0.45f, 0.1f, 1.0f, 0.01f)
            .setVisible(hitboxExpand::getValue);
    private final SliderSetting hitboxMaxSize = new SliderSetting(this, "Макс. хитбокс", 0.50f, 0.1f, 1.0f, 0.01f)
            .setVisible(hitboxExpand::getValue);

    private final ModeSetting cooldownMode = new ModeSetting(this, "Режим КД", "Ванильный", "Кастомный", "Динамический");
    private final SliderSetting customCooldown = new SliderSetting(this, "Кастомный КД", 0.93f, 0.5f, 1.0f, 0.01f)
            .setVisible(() -> cooldownMode.is("Кастомный"));
    private final SliderSetting dynamicMinCooldown = new SliderSetting(this, "Мин. КД", 0.85f, 0.5f, 1.0f, 0.01f)
            .setVisible(() -> cooldownMode.is("Динамический"));
    private final SliderSetting dynamicMaxCooldown = new SliderSetting(this, "Макс. КД", 0.95f, 0.5f, 1.0f, 0.01f)
            .setVisible(() -> cooldownMode.is("Динамический"));

    private final BooleanSetting onlyCrits = new BooleanSetting(this, "Только криты", true);
    private final BooleanSetting smartCrits = new BooleanSetting(this, "Умные криты", false)
            .setVisible(onlyCrits::getValue);

    private final BooleanSetting stopSprint = new BooleanSetting(this, "Стоп спринт", true);

    private final PerfectDelay perfectDelay = new PerfectDelay();

    private boolean canCrit;
    private float currentHitboxSize = 0.47f;
    private float currentDynamicCooldown = 0.93f;

    @Override
    public void toggle() {
        super.toggle();
        reset();
    }

    @EventHandler
    public void onHitBox(EntityHitBoxEvent event) {
        if (!hitboxExpand.getValue()) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (event.getEntity() instanceof ArmorStandEntity) return;

        if (event.getEntity() == mc.player) return;

        if (event.getEntity() instanceof PlayerEntity player) {
            String name = TextFormatting.removeFormatting(player.getGameProfile().getName());
            if (Excellent.inst().friendManager().isFriend(name)) return;
        }

        boolean isPlayer = event.getEntity() instanceof PlayerEntity;
        boolean isMob = event.getEntity() instanceof MobEntity;

        if ((isPlayer && targets.getValue("Игроки")) || (isMob && targets.getValue("Мобы"))) {
            event.setSize(currentHitboxSize);
        }
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        IPacket<?> packet = event.getPacket();
        if (packet instanceof CHeldItemChangePacket) {
            perfectDelay.reset(650L);
        } else if (packet instanceof CAnimateHandPacket) {
            perfectDelay.reset(500L);
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        reset();
    }

    @EventHandler
    public void onWorldChange(WorldChangeEvent event) {
        reset();
    }

    @EventHandler
    public void onMove(MoveEvent event) {
        if (mc.player == null || mc.world == null) {
            canCrit = false;
            return;
        }
        final boolean fallCheck = mc.player.nextFallDistance != 0F;
        canCrit = !event.isToGround() && event.getFrom().y > event.getTo().y && fallCheck;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            reset();
            return;
        }

        if (mc.pointedEntity instanceof LivingEntity target && !(mc.pointedEntity instanceof ArmorStandEntity)) {
            if (!isValidTarget(target)) return;
            if (isFriendOrSelf(target)) return;

            processAttack(target);
            return;
        }

        if (hitboxExpand.getValue()) {
            LivingEntity expandedTarget = findTargetWithExpandedHitbox();
            if (expandedTarget != null) {
                processAttack(expandedTarget);
            }
        }
    }

    private boolean isFriendOrSelf(Entity entity) {
        if (entity == mc.player) return true;

        if (entity instanceof PlayerEntity player) {
            String name = TextFormatting.removeFormatting(player.getGameProfile().getName());
            return Excellent.inst().friendManager().isFriend(name);
        }

        return false;
    }

    private LivingEntity findTargetWithExpandedHitbox() {
        if (mc.player == null || mc.world == null) return null;

        double range = attackRange.getValue();
        float expand = currentHitboxSize;

        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        Vector3d lookVec = mc.player.getLook(1.0f);
        Vector3d reachVec = eyePos.add(lookVec.scale(range));

        LivingEntity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : mc.world.getAllEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (entity instanceof ArmorStandEntity) continue;
            if (isFriendOrSelf(entity)) continue;
            if (!isValidTarget(living)) continue;

            double distance = mc.player.getDistance(entity);
            if (distance > range) continue;

            AxisAlignedBB expandedBox = entity.getBoundingBox().grow(expand);

            Optional<Vector3d> hitResult = expandedBox.rayTrace(eyePos, reachVec);

            if (hitResult.isPresent()) {
                double hitDistance = eyePos.distanceTo(hitResult.get());
                if (hitDistance < closestDistance) {
                    closestDistance = hitDistance;
                    closestEntity = living;
                }
            }
        }

        return closestEntity;
    }

    private boolean rayTraceExpandedHitbox(LivingEntity target, double range, float expand) {
        if (mc.player == null) return false;

        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        Vector3d lookVec = mc.player.getLook(1.0f);
        Vector3d reachVec = eyePos.add(lookVec.scale(range));

        AxisAlignedBB expandedBox = target.getBoundingBox().grow(expand);

        Optional<Vector3d> hitResult = expandedBox.rayTrace(eyePos, reachVec);

        return hitResult.isPresent();
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (entity instanceof PlayerEntity && targets.getValue("Игроки")) return true;
        return entity instanceof MobEntity && targets.getValue("Мобы");
    }

    private void processAttack(LivingEntity target) {
        if (!shouldAttack()) return;

        double range = attackRange.getValue();
        float expand = hitboxExpand.getValue() ? currentHitboxSize : 0f;

        boolean canHit = rayTraceExpandedHitbox(target, range, expand) || mc.player.isElytraFlying();

        if (!canHit) return;

        double distance = getExpandedDistance(target, expand);
        if (distance >= range) return;

        if (stopSprint.getValue()) {
            boolean isInLiquid = mc.player.isActualySwimming()
                    || (mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER))
                    || mc.player.areEyesInFluid(FluidTags.LAVA);
            boolean sprinting = mc.player.isSprinting();

            if (!isInLiquid && sprinting) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                mc.player.setServerSprintState(false);
                mc.player.setSprinting(false);
            }
        }

        attackEntity(target);
        updateRandomValues();
        canCrit = false;
    }

    private double getExpandedDistance(LivingEntity target, float expand) {
        if (mc.player == null) return Double.MAX_VALUE;

        Vector3d playerPos = mc.player.getEyePosition(1.0f);
        AxisAlignedBB targetBox = target.getBoundingBox().grow(expand);

        double closestX = MathHelper.clamp(playerPos.x, targetBox.minX, targetBox.maxX);
        double closestY = MathHelper.clamp(playerPos.y, targetBox.minY, targetBox.maxY);
        double closestZ = MathHelper.clamp(playerPos.z, targetBox.minZ, targetBox.maxZ);

        double dx = playerPos.x - closestX;
        double dy = playerPos.y - closestY;
        double dz = playerPos.z - closestZ;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private void attackEntity(Entity entity) {
        mc.playerController.attackEntity(mc.player, entity);
        mc.player.swingArm(Hand.MAIN_HAND);
    }

    private void updateRandomValues() {
        if (hitboxExpand.getValue()) {
            float min = hitboxMinSize.getValue();
            float max = hitboxMaxSize.getValue();
            if (min > max) {
                float temp = min;
                min = max;
                max = temp;
            }
            currentHitboxSize = min + ThreadLocalRandom.current().nextFloat() * (max - min);
        }

        if (cooldownMode.is("Динамический")) {
            float min = dynamicMinCooldown.getValue();
            float max = dynamicMaxCooldown.getValue();
            if (min > max) {
                float temp = min;
                min = max;
                max = temp;
            }
            currentDynamicCooldown = min + ThreadLocalRandom.current().nextFloat() * (max - min);
        }
    }

    private boolean shouldAttack() {
        if (!perfectDelay.cooldownComplete()) return false;
        if (!cooldownComplete()) return false;

        boolean isBlockAboveHead = PlayerUtil.isBlockAboveHead();

        boolean isInLiquid = mc.player.isActualySwimming()
                || (mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER))
                || mc.player.areEyesInFluid(FluidTags.LAVA);

        if (isInLiquid) return true;

        if (onlyCrits.getValue() && !smartCrits.getValue()) {
            return shouldCritical()
                    && mc.player.fallDistance > 0
                    && canCrit
                    && isBlockAboveHead;
        }

        if (smartCrits.getValue()) {
            boolean canSmartCrit = isBlockAboveHead
                    && !mc.player.movementInput.jump
                    && !canCrit
                    && mc.player.isOnGround()
                    && mc.player.collidedVertically;

            return (shouldCritical() && mc.player.fallDistance > 0 && canCrit && isBlockAboveHead) || canSmartCrit;
        }

        return true;
    }

    private boolean shouldCritical() {
        boolean isDeBuffed = mc.player.isPotionActive(Effects.LEVITATION)
                || mc.player.isPotionActive(Effects.BLINDNESS)
                || mc.player.isPotionActive(Effects.SLOW_FALLING);
        boolean isInLiquid = mc.player.isActualySwimming()
                || (mc.player.isSwimming() && mc.player.areEyesInFluid(FluidTags.WATER))
                || mc.player.areEyesInFluid(FluidTags.LAVA);
        boolean isFlying = mc.player.abilities.isFlying || mc.player.isElytraFlying();
        boolean isClimbing = mc.player.isOnLadder();
        boolean isCantJump = mc.player.isPassenger();
        boolean isOnWeb = PlayerUtil.isPlayerInWeb();

        return !(isDeBuffed || isInLiquid || isFlying || isClimbing || isCantJump || isOnWeb);
    }

    private boolean cooldownComplete() {
        float requiredCooldown;

        switch (cooldownMode.getValue()) {
            case "Кастомный":
                requiredCooldown = customCooldown.getValue();
                break;
            case "Динамический":
                requiredCooldown = currentDynamicCooldown;
                break;
            default:
                requiredCooldown = 0.93f;
                break;
        }

        return mc.player.getCooledAttackStrength(1.5F) >= requiredCooldown;
    }

    private void reset() {
        canCrit = false;
        currentHitboxSize = 0.47f;
        currentDynamicCooldown = 0.93f;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        updateRandomValues();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        reset();
    }
}