package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.command.impl.GPSCommand;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.Random;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Taxi", category = Category.MOVEMENT)
public class Taxi extends Module {
    public static Taxi getInstance() {
        return Instance.get(Taxi.class);
    }

    private final BooleanSetting antiCheatMode = new BooleanSetting(this, "Анти-античит режим", true);
    private final BooleanSetting requireManualTakeoff = new BooleanSetting(this, "Ручной взлет", true);
    private final SliderSetting optimalGlideRatio = new SliderSetting(this, "Коэф. планирования", 4.0F, 2.0F, 8.0F, 0.1F);
    private final SliderSetting heightBuffer = new SliderSetting(this, "Запас высоты", 20.0F, 0.0F, 50.0F, 5.0F);
    private final SliderSetting maxHeight = new SliderSetting(this, "Макс. высота", 200.0F, 100.0F, 300.0F, 10.0F);
    private final SliderSetting fireworkDelay = new SliderSetting(this, "Задержка фейерверков (мс)", 1500, 500, 5000, 100);
    private final SliderSetting arrivalDistance = new SliderSetting(this, "Дистанция прибытия", 5, 1, 20, 1);
    private final BooleanSetting autoDisable = new BooleanSetting(this, "Авто-выключение", true);
    private final BooleanSetting showInfo = new BooleanSetting(this, "Показывать информацию", true);
    private final BooleanSetting moveFireworks = new BooleanSetting(this, "Перемещать фейерверки", true);
    private final BooleanSetting naturalRotations = new BooleanSetting(this, "Естественные повороты", true);
    private final SliderSetting rotationSpeed = new SliderSetting(this, "Скорость поворота", 3.0F, 0.5F, 10.0F, 0.5F)
            .setVisible(() -> naturalRotations.getValue());

    private enum FlightState {
        WAITING_FOR_TAKEOFF,
        ASCENDING,
        GLIDING,
        ARRIVED
    }

    private FlightState currentState = FlightState.WAITING_FOR_TAKEOFF;
    private Vector2f targetCoords = null;
    private double targetHeight = 0;
    private double initialPlayerY = 0;
    private final StopWatch fireworkTimer = new StopWatch();
    private final StopWatch infoTimer = new StopWatch();
    private final StopWatch rotationTimer = new StopWatch();
    private int fireworksUsed = 0;
    private double distanceToTarget = 0;
    private boolean hasElytra = false;
    private boolean hasFireworks = false;
    private int fireworksCount = 0;
    private float initialYaw = 0;
    private float initialPitch = 0;
    private float targetYaw = 0;
    private float targetPitch = 0;
    private final Random random = new Random();
    private boolean waitingForElytraActivation = false;

    @Override
    public void onEnable() {
        super.onEnable();

        if (!GPSCommand.hasGPSCoords()) {
            ChatUtil.addText("§7[§cTaxi§7] §fGPS точка не установлена. Используйте §e.gps set <x> <z>");
            toggle();
            return;
        }

        targetCoords = GPSCommand.getGPSCoords();

        checkEquipment();
        if (!hasElytra) {
            ChatUtil.addText("§7[§cTaxi§7] §fЭлитры не экипированы");
            toggle();
            return;
        }

        if (!hasFireworks) {
            ChatUtil.addText("§7[§cTaxi§7] §fФейерверки не найдены в инвентаре");
            toggle();
            return;
        }

        initialPlayerY = mc.player.getPosY();
        distanceToTarget = calculateDistance();

        double requiredHeight = Math.min(
                distanceToTarget / optimalGlideRatio.getValue() + heightBuffer.getValue(),
                maxHeight.getValue() - initialPlayerY
        );
        targetHeight = initialPlayerY + requiredHeight;

        if (targetHeight > maxHeight.getValue()) {
            targetHeight = maxHeight.getValue();
        }

        initialYaw = mc.player.rotationYaw;
        initialPitch = mc.player.rotationPitch;

        updateTargetRotations();

        fireworkTimer.reset();
        infoTimer.reset();
        rotationTimer.reset();
        fireworksUsed = 0;

        if (requireManualTakeoff.getValue()) {
            currentState = FlightState.WAITING_FOR_TAKEOFF;
            ChatUtil.addText(String.format("§7[§aTaxi§7] §fПрыгните и активируйте элитры для начала полета к §e[%d, %d]§f (дистанция: §e%.1f§f блоков)",
                    (int) targetCoords.x, (int) targetCoords.y, distanceToTarget));
        } else {
            currentState = FlightState.ASCENDING;
            ChatUtil.addText(String.format("§7[§aTaxi§7] §fНачинаем полет к точке §e[%d, %d]§f, дистанция: §e%.1f§f блоков",
                    (int) targetCoords.x, (int) targetCoords.y, distanceToTarget));
        }

        ChatUtil.addText(String.format("§7[§aTaxi§7] §fНайдено §e%d§f фейерверков, целевая высота: §e%.1f§f блоков",
                fireworksCount, targetHeight));

        if (moveFireworks.getValue()) {
            moveFireworksToHotbar();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (currentState != FlightState.WAITING_FOR_TAKEOFF && showInfo.getValue()) {
            ChatUtil.addText(String.format("§7[§aTaxi§7] §fПолет завершен. Использовано фейерверков: §e%d", fireworksUsed));
        }
        currentState = FlightState.WAITING_FOR_TAKEOFF;
    }

    private void updateTargetRotations() {
        if (targetCoords == null || mc.player == null) return;

        double deltaX = targetCoords.x - mc.player.getPosX();
        double deltaZ = targetCoords.y - mc.player.getPosZ();

        targetYaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90F;
        targetYaw = MathHelper.wrapDegrees(targetYaw);

        switch (currentState) {
            case ASCENDING:
                targetPitch = -60.0f + (random.nextFloat() * 10.0f - 5.0f);
                break;
            case GLIDING:
                double heightAboveTarget = mc.player.getPosY() - initialPlayerY;
                double optimalGlideAngle = 0;

                if (distanceToTarget > 0 && heightAboveTarget > 0) {
                    optimalGlideAngle = Math.toDegrees(Math.atan2(heightAboveTarget, distanceToTarget));
                }

                if (distanceToTarget > 100) {
                    targetPitch = Math.max(0f, (float) optimalGlideAngle * 0.7f);
                } else if (distanceToTarget > 50) {
                    targetPitch = Math.max(0f, (float) optimalGlideAngle * 0.9f);
                } else {
                    targetPitch = Math.max(0f, (float) optimalGlideAngle * 1.1f);
                }

                targetPitch += (random.nextFloat() * 5.0f - 2.5f);
                break;
            default:
                targetPitch = mc.player.rotationPitch;
                break;
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (infoTimer.finished(1000)) {
            checkEquipment();
            distanceToTarget = calculateDistance();
            updateTargetRotations();
            infoTimer.reset();

            if (showInfo.getValue() && currentState != FlightState.WAITING_FOR_TAKEOFF && currentState != FlightState.ARRIVED) {
                ChatUtil.addText(String.format("§7[§aTaxi§7] §fДистанция: §e%.1f§f блоков, Высота: §e%.1f§f, Фейерверков: §e%d",
                        distanceToTarget, mc.player.getPosY(), fireworksCount));
            }
        }

        switch (currentState) {
            case WAITING_FOR_TAKEOFF:
                handleWaitingForTakeoff();
                break;

            case ASCENDING:
            case GLIDING:
                if (naturalRotations.getValue() && rotationTimer.finished(50)) {
                    applyNaturalRotations();
                    rotationTimer.reset();
                }

                if (distanceToTarget <= arrivalDistance.getValue()) {
                    if (currentState != FlightState.ARRIVED) {
                        currentState = FlightState.ARRIVED;
                        ChatUtil.addText("§7[§aTaxi§7] §fВы прибыли к точке назначения!");

                        if (autoDisable.getValue()) {
                            toggle();
                        }
                    }
                }
                break;
        }

        if ((currentState == FlightState.ASCENDING || currentState == FlightState.GLIDING) && !hasElytra) {
            ChatUtil.addText("§7[§cTaxi§7] §fЭлитры не экипированы, отключаем модуль");
            toggle();
        }
    }

    private void handleWaitingForTakeoff() {
        if (mc.player.isElytraFlying()) {
            currentState = FlightState.ASCENDING;
            ChatUtil.addText("§7[§aTaxi§7] §fЭлитры активированы, начинаем набор высоты");

            if (hasFireworks) {
                useFirework();
            }
        } else if (!waitingForElytraActivation && !mc.player.isOnGround() && mc.player.getMotion().y < 0) {
            waitingForElytraActivation = true;
            ChatUtil.addText("§7[§aTaxi§7] §fНажмите пробел для активации элитр!");
        } else if (waitingForElytraActivation && mc.player.isOnGround()) {
            waitingForElytraActivation = false;
        }
    }

    private void applyNaturalRotations() {
        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float pitchDiff = targetPitch - currentPitch;

        float speedFactor = rotationSpeed.getValue();
        float yawStep = Math.min(Math.abs(yawDiff), speedFactor + random.nextFloat() * (speedFactor * 0.5f));
        float pitchStep = Math.min(Math.abs(pitchDiff), speedFactor * 0.5f + random.nextFloat() * (speedFactor * 0.25f));

        if (yawDiff > 0) {
            currentYaw += yawStep;
        } else if (yawDiff < 0) {
            currentYaw -= yawStep;
        }

        if (pitchDiff > 0) {
            currentPitch += pitchStep;
        } else if (pitchDiff < 0) {
            currentPitch -= pitchStep;
        }

        if (random.nextInt(5) == 0) {
            currentYaw += (random.nextFloat() * 0.5f - 0.25f);
            currentPitch += (random.nextFloat() * 0.3f - 0.15f);
        }

        mc.player.rotationYaw = MathHelper.wrapDegrees(currentYaw);
        mc.player.rotationPitch = MathHelper.clamp(currentPitch, -90.0F, 90.0F);
    }

    @EventHandler
    public void onMotion(MotionEvent event) {
        if (mc.player == null || mc.world == null || targetCoords == null) return;

        if (currentState == FlightState.WAITING_FOR_TAKEOFF || currentState == FlightState.ARRIVED) {
            return;
        }

        if (!mc.player.isElytraFlying()) {
            if (!mc.player.isOnGround() && !mc.player.isInWater()) {
                mc.player.connection.sendPacket(new CEntityActionPacket(
                        mc.player, CEntityActionPacket.Action.START_FALL_FLYING
                ));
            }
            return;
        }

        switch (currentState) {
            case ASCENDING:
                handleAscending();
                break;
            case GLIDING:
                handleGliding();
                break;
        }

        if (antiCheatMode.getValue() && Math.random() < 0.15) {
            sendAntiCheatCompatibleMovement();
        }
    }

    private void handleAscending() {
        if (mc.player.getPosY() >= targetHeight) {
            currentState = FlightState.GLIDING;
            ChatUtil.addText("§7[§aTaxi§7] §fДостигнута целевая высота, начинаем планирование");
            updateTargetRotations();
            return;
        }

        if (fireworkTimer.finished(fireworkDelay.getValue().intValue())) {
            useFirework();
        }

        if (random.nextInt(20) == 0) {
            targetPitch = -55.0f + (random.nextFloat() * 15.0f);
        }

        if (mc.player.getMotion().y < 0.8) {
            Vector3d motion = mc.player.getMotion();
            mc.player.setMotion(
                    motion.x,
                    Math.min(motion.y + 0.03, 1.0),
                    motion.z
            );
        }
    }

    private void handleGliding() {
        if (random.nextInt(30) == 0) {
            updateTargetRotations();
        }

        Vector3d motion = mc.player.getMotion();
        double speed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        double deltaX = targetCoords.x - mc.player.getPosX();
        double deltaZ = targetCoords.y - mc.player.getPosZ();
        double distHorizontal = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        double heightAboveTarget = mc.player.getPosY() - initialPlayerY;
        double optimalGlideAngle = 0;

        if (distHorizontal > 0 && heightAboveTarget > 0) {
            optimalGlideAngle = Math.toDegrees(Math.atan2(heightAboveTarget, distHorizontal));

            if (distHorizontal > 100) {
                targetPitch = Math.max(0f, (float) optimalGlideAngle * 0.7f);
            } else if (distHorizontal > 50) {
                targetPitch = Math.max(0f, (float) optimalGlideAngle * 0.9f);
            } else {
                targetPitch = Math.max(0f, (float) optimalGlideAngle * 1.1f);
            }

            targetPitch += (random.nextFloat() * 3.0f - 1.5f);
        }

        if (distHorizontal > 0) {
            deltaX /= distHorizontal;
            deltaZ /= distHorizontal;
        }

        double yaw = Math.toRadians(mc.player.rotationYaw);
        double dirX = -Math.sin(yaw);
        double dirZ = Math.cos(yaw);

        double alignment = dirX * deltaX + dirZ * deltaZ;

        if (alignment < 0.95 && speed > 0.1) {
            double correction = 0.01 * (1.0 - alignment);
            mc.player.setMotion(
                    motion.x + deltaX * correction,
                    motion.y,
                    motion.z + deltaZ * correction
            );
        }

        if (motion.y < -0.8 && distanceToTarget > 100 &&
                mc.player.getPosY() < (initialPlayerY + distanceToTarget / (optimalGlideRatio.getValue() * 1.5)) &&
                fireworkTimer.finished(fireworkDelay.getValue().intValue() * 2)) {
            useFirework();
        }
    }

    private void calculateGlidingDirection() {
        double deltaX = targetCoords.x - mc.player.getPosX();
        double deltaZ = targetCoords.y - mc.player.getPosZ();

        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX));

        yaw = MathHelper.wrapDegrees(yaw - 90);

        float pitch = 10.0f;

        mc.player.rotationYaw = yaw;
        mc.player.rotationPitch = pitch;
        mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(
                yaw, pitch, mc.player.isOnGround()
        ));
    }

    private void sendAntiCheatCompatibleMovement() {
        if (!antiCheatMode.getValue() || mc.player == null) return;

        Vector3d pos = mc.player.getPositionVec();
        Vector3d motion = mc.player.getMotion();

        double randomX = pos.x + (Math.random() - 0.5) * 0.001;
        double randomY = pos.y + (Math.random() - 0.5) * 0.0005;
        double randomZ = pos.z + (Math.random() - 0.5) * 0.001;

        double randomMotionX = motion.x + (Math.random() - 0.5) * 0.001;
        double randomMotionY = motion.y;
        double randomMotionZ = motion.z + (Math.random() - 0.5) * 0.001;

        mc.player.setMotion(randomMotionX, randomMotionY, randomMotionZ);

        if (random.nextInt(3) == 0) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(
                    randomX,
                    randomY,
                    randomZ,
                    mc.player.isOnGround()
            ));
        }
    }

    private void useFirework() {
        if (!hasFireworks) return;

        int fireworkSlot = findFireworkSlot();
        if (fireworkSlot == -1) {
            hasFireworks = false;
            return;
        }

        int currentSlot = mc.player.inventory.currentItem;

        if (fireworkSlot < 9) {
            mc.player.inventory.currentItem = fireworkSlot;

            if (antiCheatMode.getValue()) {
                try {
                    Thread.sleep(random.nextInt(30) + 10);
                } catch (InterruptedException e) {
                }
            }

            mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);

            if (antiCheatMode.getValue()) {
                try {
                    Thread.sleep(random.nextInt(30) + 10);
                } catch (InterruptedException e) {
                }
            }

            mc.player.inventory.currentItem = currentSlot;
            mc.playerController.syncCurrentPlayItem();

            fireworksUsed++;
            fireworkTimer.reset();
        } else if (moveFireworks.getValue()) {
            moveFireworksToHotbar();
        }
    }

    private void moveFireworksToHotbar() {
        Slot fireworkSlot = InvUtil.getInventorySlot(Items.FIREWORK_ROCKET);
        if (fireworkSlot == null) return;

        int emptyHotbarSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty()) {
                emptyHotbarSlot = i;
                break;
            }
        }

        if (emptyHotbarSlot != -1) {
            InvUtil.moveItem(fireworkSlot.slotNumber, emptyHotbarSlot);
            ChatUtil.addText("§7[§aTaxi§7] §fФейерверки перемещены в хотбар");
        }
    }

    private void checkEquipment() {
        ItemStack chestplate = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        hasElytra = chestplate.getItem() == Items.ELYTRA;

        if (!hasElytra) {
            Slot elytraSlot = InvUtil.getSlot(Items.ELYTRA);
            if (elytraSlot != null) {
                ChatUtil.addText("§7[§cTaxi§7] §fНайдены элитры в инвентаре, но они не экипированы");
                hasElytra = false;
            }
        }

        fireworksCount = InvUtil.getInventoryCount(Items.FIREWORK_ROCKET);

        ItemStack offhandItem = mc.player.getHeldItemOffhand();
        if (offhandItem.getItem() == Items.FIREWORK_ROCKET) {
            fireworksCount += offhandItem.getCount();
        }

        hasFireworks = fireworksCount > 0;
    }

    private int findFireworkSlot() {
        int hotbarSlot = InvUtil.getItemInHotBar(Items.FIREWORK_ROCKET);
        if (hotbarSlot != -1) {
            return hotbarSlot;
        }

        Slot invSlot = InvUtil.getInventorySlot(Items.FIREWORK_ROCKET);
        if (invSlot != null) {
            return invSlot.slotNumber;
        }

        ItemStack offhandItem = mc.player.getHeldItemOffhand();
        if (offhandItem.getItem() == Items.FIREWORK_ROCKET) {
            return 40;
        }

        return -1;
    }

    private double calculateDistance() {
        if (targetCoords == null) return 0;

        return Math.sqrt(
                Math.pow(targetCoords.x - mc.player.getPosX(), 2) +
                        Math.pow(targetCoords.y - mc.player.getPosZ(), 2)
        );
    }
}