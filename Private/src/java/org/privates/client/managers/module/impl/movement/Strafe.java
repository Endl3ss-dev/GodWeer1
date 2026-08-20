package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.MoveEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.Random;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Strafe", category = Category.MOVEMENT, description = "Улучшает и ускоряет ваши движение в право и лево")
public class Strafe extends Module {

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Default", "Matrix", "MetaHVH");
    private final BooleanSetting damageBoost = new BooleanSetting(this, "Буст от Урона", false);
    private final BooleanSetting targetStrafe = new BooleanSetting(this, "Tаргет Стрейф", false);
    private final BooleanSetting visualRotation = new BooleanSetting(this, "Визуальная ротация", true).setVisible(targetStrafe::getValue);
    private final BooleanSetting onlyGround = new BooleanSetting(this, "Только на Земле", false).setVisible(() -> mode.getValue().equals("MetaHVH"));
    private final SliderSetting boostSpeed = new SliderSetting(this, "Буст скорости", 0.7F, 0.1F, 5.0F, 0.1F).setVisible(damageBoost::getValue);
    private final SliderSetting targetStrafeRadius = new SliderSetting(this, "Радиус стрейфа", 2.5F, 1.0F, 6.0F, 0.1F).setVisible(targetStrafe::getValue);

    private final StopWatch damageTimer = new StopWatch();
    private double oldSpeed = 0;
    private boolean needSwap = false;
    private final boolean NDS = true;
    private double lastHorizontalMove = 0;
    private int waterTicks = 0;
    private float serverYaw = 0;
    private float serverPitch = 0;

    public static Strafe getInstance() {
        return Instance.get(Strafe.class);
    }

    public Strafe() {

    }

    @Override
    public void onEnable() {
        oldSpeed = 0;
        lastHorizontalMove = 0;
        waterTicks = 0;
        if (mc.player != null) {
            serverYaw = mc.player.rotationYaw;
            serverPitch = mc.player.rotationPitch;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.motion.x *= 0.7f;
            mc.player.motion.z *= 0.7f;
        }
        oldSpeed = 0;
        lastHorizontalMove = 0;
        waterTicks = 0;
        super.onDisable();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (!strafes()) return;

        if (CEntityActionPacket.lastUpdatedSprint != NDS) {
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player,
                    NDS ? CEntityActionPacket.Action.START_SPRINTING : CEntityActionPacket.Action.STOP_SPRINTING));
            CEntityActionPacket.lastUpdatedSprint = NDS;
        }

        if (needSwap) {
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player,
                    mc.player.isServerSprintState() ? CEntityActionPacket.Action.STOP_SPRINTING : CEntityActionPacket.Action.START_SPRINTING));
            needSwap = false;
        }

        if (lastHorizontalMove > 0) {
            oldSpeed = lastHorizontalMove;
            lastHorizontalMove = 0;
        }

        if (mc.player.isInWater() || mc.player.isInLava()) {
            waterTicks = 10;
        } else if (waterTicks > 0) {
            waterTicks--;
        }

        if (targetStrafe.getValue() && visualRotation.getValue()) {
            updateTargetRotation();
        }
    }

    @EventHandler
    public void onMove(MoveEvent event) {
        if (!strafes()) {
            oldSpeed = 0;
            lastHorizontalMove = 0;
            return;
        }

        float forward = getForwardInput();
        float strafe = getStrafeInput();

        if (forward == 0 && strafe == 0 && !mode.getValue().equals("Matrix")) {
            oldSpeed = 0;
            lastHorizontalMove = 0;
            return;
        }

        String currentMode = mode.getValue();
        if (currentMode.equals("MetaHVH") && onlyGround.getValue() && !mc.player.isOnGround()) {
            return;
        }

        boolean canBoost = damageBoost.getValue() && damageTimer.finished(700L);
        if (canBoost) {
            damageTimer.reset();
        }

        double speed;
        if (currentMode.equals("Matrix")) {
            handleMatrixMode(event);
            return;
        } else if (currentMode.equals("MetaHVH")) {
            speed = calculateSpeed(forward, strafe, canBoost, boostSpeed.getValue() / 10.0F);
        } else {
            speed = calculateSpeed(forward, strafe, canBoost, boostSpeed.getValue() / 10.0F);
        }

        setMoveMotion(event, speed);
        lastHorizontalMove = speed;
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (!event.isReceive()) return;

        if (event.getPacket() instanceof SPlayerPositionLookPacket && oldSpeed != 0) {
            oldSpeed = 0;
            lastHorizontalMove = 0;
            waterTicks = 0;
        }
    }

    private void handleMatrixMode(MoveEvent event) {
        if (waterTicks > 0) return;
        if (isMoving() && getMotion() <= 0.289385188) {
            if (!isToGround()) {
                double strafeSpeed = 0.245f - (new Random().nextFloat() * 0.000001f);
                setMoveMotion(event, strafeSpeed);
                lastHorizontalMove = strafeSpeed;
            }
        }
    }

    private boolean strafes() {
        if (mc.player == null || mc.world == null) {
            return false;
        }
        if (mc.player.isSneaking() || mc.player.isElytraFlying()) {
            return false;
        }
        if (mc.player.isInWater() || mc.player.isInLava() || waterTicks > 0) {
            return false;
        }
        BlockPos playerPos = new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
        if (mc.world.getBlockState(playerPos).getMaterial() == Material.WEB
                || mc.world.getBlockState(playerPos.down()).getBlock() instanceof SoulSandBlock) {
            return false;
        }
        return !mc.player.abilities.isFlying && !mc.player.isPotionActive(Effects.LEVITATION);
    }

    private double calculateSpeed(float forward, float strafe, boolean isBoosting, float damageSpeed) {
        double baseSpeed = 0.2873;

        if (mode.getValue().equals("MetaHVH")) {
            baseSpeed = 0.35;
        }

        if (mc.player.isPotionActive(Effects.SPEED)) {
            int amplifier = mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier() + 1;
            if (amplifier > 3) amplifier = 3;
            baseSpeed *= 1.0 + 0.2 * amplifier;
        }

        double speed = baseSpeed;

        if (isBoosting) {
            if (mode.getValue().equals("MetaHVH")) {
                speed += damageSpeed * 2;
            } else {
                speed += damageSpeed;
            }
        }

        if (oldSpeed > speed && (forward != 0 || strafe != 0)) {
            speed = oldSpeed;
        }

        return speed;
    }

    private void setMoveMotion(MoveEvent event, double speed) {
        float yaw = targetStrafe.getValue() ? getTargetStrafeYaw() : mc.player.rotationYaw;
        float forward = getForwardInput();
        float strafe = getStrafeInput();

        if (forward == 0 && strafe == 0 && !mode.getValue().equals("Matrix")) {
            setX(event, 0);
            setZ(event, 0);
            return;
        }

        double inputMagnitude = Math.sqrt(forward * forward + strafe * strafe);
        if (inputMagnitude > 1.0) {
            forward /= inputMagnitude;
            strafe /= inputMagnitude;
        }

        double radYaw = Math.toRadians(yaw);
        double newX = (strafe * Math.cos(radYaw) - forward * Math.sin(radYaw)) * speed;
        double newZ = (strafe * Math.sin(radYaw) + forward * Math.cos(radYaw)) * speed;

        setX(event, newX);
        setZ(event, newZ);
    }

    private float getForwardInput() {
        float forward = 0;

        if (targetStrafe.getValue()) {
            AuraNew aura = AuraNew.getInstance();
            Entity target = aura.target != null ? aura.target.getRevengeTarget() : null;
            if (target != null) {
                double distance = mc.player.getDistance(target);
                float radius = targetStrafeRadius.getValue();
                if (distance > radius + 0.5) {
                    forward += 1;
                } else if (distance < radius - 0.5) {
                    forward -= 0.5f;
                }
                return forward;
            }
        }

        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            forward += 1;
        }
        if (mc.gameSettings.keyBindBack.isKeyDown()) {
            forward -= 1;
        }

        return forward;
    }

    private float getStrafeInput() {
        float strafe = 0;

        if (targetStrafe.getValue()) {
            AuraNew aura = AuraNew.getInstance();
            Entity target = aura.target != null ? aura.target.getRevengeTarget() : null;
            if (target != null) {
                strafe = 1;
                return strafe;
            }
        }

        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            strafe += 1;
        }
        if (mc.gameSettings.keyBindRight.isKeyDown()) {
            strafe -= 1;
        }
        return strafe;
    }

    private float getTargetStrafeYaw() {
        if (!targetStrafe.getValue()) {
            return mc.player.rotationYaw;
        }

        AuraNew aura = AuraNew.getInstance();
        Entity target = aura.target != null ? aura.target.getRevengeTarget() : null;

        if (target == null) {
            return mc.player.rotationYaw;
        }

        double deltaX = target.getPosX() - mc.player.getPosX();
        double deltaZ = target.getPosZ() - mc.player.getPosZ();
        float targetYaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0f;

        boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
        boolean right = mc.gameSettings.keyBindRight.isKeyDown();

        if (left && !right) {
            targetYaw += 90.0f;
        } else if (right && !left) {
            targetYaw -= 90.0f;
        }

        return targetYaw;
    }

    private void updateTargetRotation() {
        AuraNew aura = AuraNew.getInstance();
        Entity target = aura.target != null ? aura.target.getRevengeTarget() : null;

        if (target == null) {
            return;
        }

        double deltaX = target.getPosX() - mc.player.getPosX();
        double deltaY = target.getPosY() + target.getEyeHeight() - mc.player.getPosY() - mc.player.getEyeHeight();
        double deltaZ = target.getPosZ() - mc.player.getPosZ();

        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, distance));

        serverYaw = yaw;
        serverPitch = pitch;

        if (!visualRotation.getValue()) {
            mc.player.rotationYawHead = yaw;
            mc.player.renderYawOffset = yaw;
            mc.player.rotationYaw = yaw;
            mc.player.rotationPitch = pitch;
        }
    }

    private double getX(MoveEvent event) {
        return event.getMotion().x;
    }

    private double getZ(MoveEvent event) {
        return event.getMotion().z;
    }

    private void setX(MoveEvent event, double x) {
        Vector3d motion = event.getMotion();
        event.setMotion(new Vector3d(x, motion.y, motion.z));
    }

    private void setY(MoveEvent event, double y) {
        Vector3d motion = event.getMotion();
        event.setMotion(new Vector3d(motion.x, y, motion.z));
    }

    private void setZ(MoveEvent event, double z) {
        Vector3d motion = event.getMotion();
        event.setMotion(new Vector3d(motion.x, motion.y, z));
    }

    private boolean isMoving() {
        return getForwardInput() != 0 || getStrafeInput() != 0;
    }

    private double getMotion() {
        return Math.sqrt(mc.player.motion.x * mc.player.motion.x + mc.player.motion.z * mc.player.motion.z);
    }

    private boolean isToGround() {
        return mc.player.isOnGround();
    }
}