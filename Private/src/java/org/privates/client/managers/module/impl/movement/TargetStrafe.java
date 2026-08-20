package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.player.MoveInputEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

import java.util.List;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "TargetStrafe", category = Category.MOVEMENT, description = "Таргет с ускорением движение за целью")
public class TargetStrafe extends Module {

    public static TargetStrafe getInstance() {
        return Instance.get(TargetStrafe.class);
    }

    public final ModeSetting mode = new ModeSetting(this, "Режим", "Default", "Ice Boost", "Entity Collision", "Slime Boost", "Soul Speed", "Honey Block", "Boat Collision", "SpookyTime");

    public final SliderSetting distance = new SliderSetting(this, "Дистанция входа", 1.5f, 0.0f, 3.0f, 0.1f);

    private final BooleanSetting iceBoost = new BooleanSetting(this, "Буст от льда", true)
            .setVisible(() -> mode.is("Ice Boost"));
    private final SliderSetting iceBoostPower = new SliderSetting(this, "Сила буста от льда", 1.4f, 1.0f, 2.5f, 0.1f)
            .setVisible(() -> mode.is("Ice Boost") && iceBoost.getValue());
    private final SliderSetting iceSearchRadius = new SliderSetting(this, "Радиус поиска льда", 3, 1, 5, 1)
            .setVisible(() -> mode.is("Ice Boost") && iceBoost.getValue());

    private final BooleanSetting entityCollision = new BooleanSetting(this, "Буст от столкновений", true)
            .setVisible(() -> mode.is("Entity Collision"));
    private final SliderSetting collisionBoostPower = new SliderSetting(this, "Сила буста от столкновений", 1.3f, 1.0f, 2.0f, 0.1f)
            .setVisible(() -> mode.is("Entity Collision") && entityCollision.getValue());
    private final SliderSetting collisionRadius = new SliderSetting(this, "Радиус столкновений", 2, 1, 4, 1)
            .setVisible(() -> mode.is("Entity Collision") && entityCollision.getValue());

    private final BooleanSetting slimeBoost = new BooleanSetting(this, "Буст от слаймов", true)
            .setVisible(() -> mode.is("Slime Boost"));
    private final SliderSetting slimeBoostPower = new SliderSetting(this, "Сила буста от слаймов", 1.6f, 1.0f, 3.0f, 0.1f)
            .setVisible(() -> mode.is("Slime Boost") && slimeBoost.getValue());
    private final SliderSetting slimeSearchRadius = new SliderSetting(this, "Радиус поиска слаймов", 4, 1, 6, 1)
            .setVisible(() -> mode.is("Slime Boost") && slimeBoost.getValue());

    private final BooleanSetting dolphinGrace = new BooleanSetting(this, "Грация дельфина", true)
            .setVisible(() -> mode.is("Dolphin Grace"));
    private final SliderSetting dolphinBoostPower = new SliderSetting(this, "Сила буста дельфина", 1.5f, 1.0f, 2.5f, 0.1f)
            .setVisible(() -> mode.is("Dolphin Grace") && dolphinGrace.getValue());

    private final BooleanSetting soulSpeed = new BooleanSetting(this, "Песок душ буст", true)
            .setVisible(() -> mode.is("Soul Speed"));
    private final SliderSetting soulSpeedPower = new SliderSetting(this, "Сила буста от песка душ", 1.3f, 1.0f, 2.0f, 0.1f)
            .setVisible(() -> mode.is("Soul Speed") && soulSpeed.getValue());
    private final SliderSetting soulSpeedRadius = new SliderSetting(this, "Радиус песка душ", 3, 1, 5, 1)
            .setVisible(() -> mode.is("Soul Speed") && soulSpeed.getValue());

    private final BooleanSetting honeyBlock = new BooleanSetting(this, "Буст от меда", true)
            .setVisible(() -> mode.is("Honey Block"));
    private final SliderSetting honeyBoostPower = new SliderSetting(this, "Сила буста от меда", 1.2f, 1.0f, 2.0f, 0.1f)
            .setVisible(() -> mode.is("Honey Block") && honeyBlock.getValue());
    private final SliderSetting honeySearchRadius = new SliderSetting(this, "Радиус меда", 2, 1, 4, 1)
            .setVisible(() -> mode.is("Honey Block") && honeyBlock.getValue());

    private final BooleanSetting boatCollision = new BooleanSetting(this, "Буст от лодок", true)
            .setVisible(() -> mode.is("Boat Collision"));
    private final SliderSetting boatBoostPower = new SliderSetting(this, "Сила буста от лодок", 1.7f, 1.0f, 3.0f, 0.1f)
            .setVisible(() -> mode.is("Boat Collision") && boatCollision.getValue());
    private final SliderSetting boatSearchRadius = new SliderSetting(this, "Радиус лодок", 3, 1, 5, 1)
            .setVisible(() -> mode.is("Boat Collision") && boatCollision.getValue());

    private final BooleanSetting beeBoost = new BooleanSetting(this, "Буст от пчел", false);
    private final SliderSetting beeBoostPower = new SliderSetting(this, "Сила буста от пчел", 1.2f, 1.0f, 2.0f, 0.1f)
            .setVisible(beeBoost::getValue);
    private final SliderSetting beeSearchRadius = new SliderSetting(this, "Радиус пчел", 4, 1, 6, 1)
            .setVisible(beeBoost::getValue);

    private final BooleanSetting speedPotionBoost = new BooleanSetting(this, "Буст от зелья скорости", true);
    private final SliderSetting speedPotionMultiplier = new SliderSetting(this, "Множитель зелья", 1.3f, 1.0f, 2.0f, 0.1f)
            .setVisible(speedPotionBoost::getValue);

    boolean left;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            left = true;
        }
        if (mc.gameSettings.keyBindRight.isKeyDown()) {
            left = false;
        }
    }

    @EventHandler
    public void onMotion(MotionEvent event) {
        if (mode.is("SpookyTime")) {
            AxisAlignedBB aabb = mc.player.getBoundingBox().grow(0.1);
            int armorstands = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb).size();
            boolean canBoost = armorstands > 1 || mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb).size() > 1;

            if (canBoost) {
                if (!mc.player.isOnGround()) {
                    mc.player.jumpMovementFactor = armorstands > 1 ? 1F / armorstands : random(0.08f, 0.115f);
                }
            }
        }
    }

    @EventHandler
    public void onMove(MoveInputEvent event) {
        LivingEntity target = AuraNew.getInstance().target();
        if (target == null) return;

        double distanceToTarget = mc.player.getDistanceSq(target);
        double enterDistance = distance.getValue();

        float boostMultiplier = 1.0f;

        if (speedPotionBoost.getValue() && mc.player.isPotionActive(Effects.SPEED)) {
            int amplifier = mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier();
            boostMultiplier *= speedPotionMultiplier.getValue() * (1 + amplifier * 0.2f);
        }

        switch (mode.getValue()) {
            case "Ice Boost":
                if (iceBoost.getValue()) {
                    float iceMultiplier = checkIceBoost();
                    boostMultiplier *= iceMultiplier;
                }
                handleDefaultStrafe(event, target, distanceToTarget, enterDistance, boostMultiplier);
                break;

            case "Entity Collision":
                if (entityCollision.getValue()) {
                    float collisionMultiplier = checkEntityCollision();
                    boostMultiplier *= collisionMultiplier;
                }
                handleDefaultStrafe(event, target, distanceToTarget, enterDistance, boostMultiplier);
                break;

            case "Slime Boost":
                if (slimeBoost.getValue()) {
                    float slimeMultiplier = checkSlimeBoost();
                    boostMultiplier *= slimeMultiplier;
                }
                handleDefaultStrafe(event, target, distanceToTarget, enterDistance, boostMultiplier);
                break;

            case "Dolphin Grace":
                if (dolphinGrace.getValue() && mc.player.isPotionActive(Effects.DOLPHINS_GRACE)) {
                    boostMultiplier *= dolphinBoostPower.getValue();
                }
                handleDefaultStrafe(event, target, distanceToTarget, enterDistance, boostMultiplier);
                break;

            case "Soul Speed":
                if (soulSpeed.getValue()) {
                    float soulMultiplier = checkSoulSpeedBoost();
                    boostMultiplier *= soulMultiplier;
                }
                handleDefaultStrafe(event, target, distanceToTarget, enterDistance, boostMultiplier);
                break;

            case "Honey Block":
                if (honeyBlock.getValue()) {
                    float honeyMultiplier = checkHoneyBlockBoost();
                    boostMultiplier *= honeyMultiplier;
                }
                handleDefaultStrafe(event, target, distanceToTarget, enterDistance, boostMultiplier);
                break;

            case "Boat Collision":
                if (boatCollision.getValue()) {
                    float boatMultiplier = checkBoatCollision();
                    boostMultiplier *= boatMultiplier;
                }
                handleDefaultStrafe(event, target, distanceToTarget, enterDistance, boostMultiplier);
                break;

            case "SpookyTime":
                handleDefaultStrafe(event, target, distanceToTarget, enterDistance, 1.0f);
                break;

            case "Default":
            default:
                handleDefaultStrafe(event, target, distanceToTarget, enterDistance, boostMultiplier);
                break;
        }

        if (beeBoost.getValue()) {
            float beeMultiplier = checkBeeBoost();
            if (beeMultiplier > 1.0f) {
                event.setForward(event.getForward() * beeMultiplier);
                event.setStrafe(event.getStrafe() * beeMultiplier);
            }
        }
    }

    private void handleDefaultStrafe(MoveInputEvent event, LivingEntity target, double distanceToTarget, double enterDistance, float boostMultiplier) {
        if (distanceToTarget > enterDistance * enterDistance) {
            moveTowardsTarget(event, target, boostMultiplier);
        } else {
            strafeAroundTarget(event, target, 0.8f * boostMultiplier, 0.3f * boostMultiplier);
        }
    }

    private float checkIceBoost() {
        int radius = iceSearchRadius.getValue().intValue();
        BlockPos playerPos = mc.player.getPosition();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.ICE ||
                            mc.world.getBlockState(checkPos).getBlock() == Blocks.PACKED_ICE ||
                            mc.world.getBlockState(checkPos).getBlock() == Blocks.BLUE_ICE ||
                            mc.world.getBlockState(checkPos).getBlock() == Blocks.FROSTED_ICE) {
                        return iceBoostPower.getValue();
                    }
                }
            }
        }
        return 1.0f;
    }

    private float checkEntityCollision() {
        AxisAlignedBB searchBox = mc.player.getBoundingBox().grow(collisionRadius.getValue());
        List<Entity> entities = mc.world.getEntitiesWithinAABB(Entity.class, searchBox,
                entity -> entity != mc.player && entity instanceof LivingEntity);

        if (!entities.isEmpty()) {
            return collisionBoostPower.getValue();
        }
        return 1.0f;
    }

    private float checkSlimeBoost() {
        int radius = slimeSearchRadius.getValue().intValue();
        AxisAlignedBB searchBox = mc.player.getBoundingBox().grow(radius);
        List<SlimeEntity> slimes = mc.world.getEntitiesWithinAABB(SlimeEntity.class, searchBox);

        if (!slimes.isEmpty()) {
            return slimeBoostPower.getValue();
        }

        BlockPos playerPos = mc.player.getPosition();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.SLIME_BLOCK) {
                        return slimeBoostPower.getValue() * 0.8f;
                    }
                }
            }
        }
        return 1.0f;
    }

    private float checkSoulSpeedBoost() {
        int radius = soulSpeedRadius.getValue().intValue();
        BlockPos playerPos = mc.player.getPosition();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.SOUL_SAND ||
                            mc.world.getBlockState(checkPos).getBlock() == Blocks.SOUL_SOIL) {
                        return soulSpeedPower.getValue();
                    }
                }
            }
        }
        return 1.0f;
    }

    private float checkHoneyBlockBoost() {
        int radius = honeySearchRadius.getValue().intValue();
        BlockPos playerPos = mc.player.getPosition();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.HONEY_BLOCK) {
                        return honeyBoostPower.getValue();
                    }
                }
            }
        }
        return 1.0f;
    }

    private float checkBoatCollision() {
        int radius = boatSearchRadius.getValue().intValue();
        AxisAlignedBB searchBox = mc.player.getBoundingBox().grow(radius);
        List<BoatEntity> boats = mc.world.getEntitiesWithinAABB(BoatEntity.class, searchBox);

        if (!boats.isEmpty()) {
            return boatBoostPower.getValue();
        }
        return 1.0f;
    }

    private float checkBeeBoost() {
        int radius = beeSearchRadius.getValue().intValue();
        AxisAlignedBB searchBox = mc.player.getBoundingBox().grow(radius);
        List<BeeEntity> bees = mc.world.getEntitiesWithinAABB(BeeEntity.class, searchBox);

        if (!bees.isEmpty()) {
            return beeBoostPower.getValue();
        }
        return 1.0f;
    }

    private void moveTowardsTarget(MoveInputEvent event, LivingEntity target, float speedMultiplier) {
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d targetPos = target.getPositionVec();

        double deltaX = targetPos.x - playerPos.x;
        double deltaZ = targetPos.z - playerPos.z;

        float yawToTarget = (float) (MathHelper.atan2(deltaZ, deltaX) * 180.0 / Math.PI) - 90.0F;
        float yawDiff = MathHelper.wrapDegrees(yawToTarget - mc.player.rotationYaw);

        if (Math.abs(yawDiff) < 45) {
            event.setForward(speedMultiplier);
            event.setStrafe(0.0f);
        } else if (yawDiff > 0) {
            event.setForward(0.7f * speedMultiplier);
            event.setStrafe(-0.7f * speedMultiplier);
        } else {
            event.setForward(0.7f * speedMultiplier);
            event.setStrafe(0.7f * speedMultiplier);
        }
    }

    private void strafeAroundTarget(MoveInputEvent event, LivingEntity target, float strafeSpeed, float forwardSpeed) {
        float strafe = left ? 1 : -1;
        event.setStrafe(strafe * strafeSpeed);
        event.setForward(forwardSpeed);
    }

    public static float random(float min, float max) {
        return min + (float) (Math.random() * (max - min));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        left = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}