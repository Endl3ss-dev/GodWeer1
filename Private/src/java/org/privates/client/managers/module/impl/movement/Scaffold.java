package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.*;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.player.MoveEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.MoveUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Scaffold", category = Category.MOVEMENT)
public class Scaffold extends Module {

    public static Scaffold getInstance() {
        return Instance.get(Scaffold.class);
    }

    // === ОСНОВНЫЕ РЕЖИМЫ ===
    private final ModeSetting mode = new ModeSetting(this, "Режим", "Hypixel",
            "Matrix", "Vulcan",
            "Verus", "Intave", "AAC", "NCP", "Spartan", "Grim", "Vanilla");

    // === НАСТРОЙКИ ВРАЩЕНИЯ ===
    private final ModeSetting rotationMode = new ModeSetting(this, "Режим поворотов", "Packet",
            "Packet", "Silent", "None", "Smooth", "Snap", "Custom");

    private final SliderSetting rotationSpeed = new SliderSetting(this, "Скорость поворота", 180f, 10f, 180f, 5f)
            .setVisible(() -> rotationMode.is("Smooth"));

    private final SliderSetting randomization = new SliderSetting(this, "Рандомизация", 3f, 0f, 15f, 0.5f)
            .setVisible(() -> !rotationMode.is("None"));

    // === НАСТРОЙКИ РАЗМЕЩЕНИЯ ===
    private final ModeSetting placementMode = new ModeSetting(this, "Режим размещения", "Normal",
            "Normal", "Legit", "Aggressive", "Safe", "Godbridge", "Moonwalk", "Telly");

    private final SliderSetting delay = new SliderSetting(this, "Задержка (мс)", 0f, 0f, 200f, 5f);

    private final SliderSetting blocksPerTick = new SliderSetting(this, "Блоков за тик", 1f, 1f, 5f, 1f);

    // === НАСТРОЙКИ ПОЗИЦИИ ===
    private final ModeSetting positionMode = new ModeSetting(this, "Позиция", "Down",
            "Down", "Extend", "Smart", "Edge", "Center", "Custom");

    private final SliderSetting extend = new SliderSetting(this, "Расширение", 1f, 1f, 5f, 1f)
            .setVisible(() -> positionMode.is("Extend") || positionMode.is("Custom"));

    // === БЕЗОПАСНОСТЬ ===
    private final BooleanSetting safeWalk = new BooleanSetting(this, "Безопасная ходьба", true);
    private final BooleanSetting airSafe = new BooleanSetting(this, "Проверка воздуха", true);
    private final BooleanSetting keepY = new BooleanSetting(this, "Сохранять Y", false);
    private final BooleanSetting sameY = new BooleanSetting(this, "Одинаковый Y", false);

    // === ОБХОДЫ АНТИЧИТОВ ===
    private final BooleanSetting antiCheat = new BooleanSetting(this, "Обход античита", true);
    private final BooleanSetting jumpFix = new BooleanSetting(this, "Фикс прыжков", true);
    private final BooleanSetting sprintFix = new BooleanSetting(this, "Фикс спринта", true);
    private final BooleanSetting slowdown = new BooleanSetting(this, "Замедление", false);

    private final SliderSetting slowdownFactor = new SliderSetting(this, "Фактор замедления", 0.7f, 0.1f, 1.0f, 0.05f)
            .setVisible(slowdown::getValue);

    // === ВИЗУАЛЬНЫЕ НАСТРОЙКИ ===
    private final BooleanSetting swing = new BooleanSetting(this, "Анимация руки", true);
    private final BooleanSetting render = new BooleanSetting(this, "Рендер блока", true);
    private final BooleanSetting counter = new BooleanSetting(this, "Счетчик блоков", true);

    // === СПЕЦИАЛЬНЫЕ РЕЖИМЫ ===
    private final BooleanSetting tower = new BooleanSetting(this, "Башня", false);
    private final ModeSetting towerMode = new ModeSetting(this, "Режим башни", "Jump",
            "Jump", "Motion", "Packet", "Vanilla").setVisible(tower::getValue);

    private final BooleanSetting eagle = new BooleanSetting(this, "Eagle", false);
    private final BooleanSetting breezily = new BooleanSetting(this, "Breezily", false);
    private final BooleanSetting downwards = new BooleanSetting(this, "Вниз", false);

    // === ВНУТРЕННИЕ ПЕРЕМЕННЫЕ ===
    private final StopWatch placeTimer = new StopWatch();
    private final StopWatch rotationTimer = new StopWatch();
    private final StopWatch serverTimer = new StopWatch();

    private BlockPos targetPos;
    private Direction placeSide;
    private Vector3d hitVec;
    private float[] rotations;
    private float[] lastRotations;

    private int blocksPlaced = 0;
    private int slot = -1;
    private int lastSlot = -1;
    private boolean isPlacing = false;
    private boolean wasPlacing = false;
    private double startY;

    // Специальные переменные для разных режимов
    private boolean hypixelFlag = false;
    private int hypixelTicks = 0;
    private boolean matrixBoost = false;
    private final StopWatch matrixTimer = new StopWatch();

    @Override
    public void onEnable() {
        super.onEnable();
        startY = mc.player.getPosY();
        blocksPlaced = 0;
        isPlacing = false;
        wasPlacing = false;
        hypixelFlag = false;
        hypixelTicks = 0;
        matrixBoost = false;
        placeTimer.reset();
        rotationTimer.reset();
        serverTimer.reset();
        matrixTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (lastSlot != -1 && mc.player != null) {
            mc.player.inventory.currentItem = lastSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(lastSlot));
            lastSlot = -1;
        }
        isPlacing = false;
        wasPlacing = false;
    }

    @EventHandler

    public void onUpdate(UpdateEvent event) {
        // Проверяем наличие блоков
        slot = getBlockSlot();
        if (slot == -1) {
            if (counter.getValue()) {
                ChatUtil.addText("§cНет блоков в хотбаре!");
            }
            return;
        }

        // Проверяем условия размещения
        if (!shouldScaffold()) return;

        // Получаем позицию для размещения
        targetPos = getPlacePosition();
        if (targetPos == null) return;

        // Получаем сторону размещения
        PlaceData placeData = getPlaceData(targetPos);
        if (placeData == null) return;

        placeSide = placeData.direction;
        hitVec = placeData.hitVec;

        // Вычисляем поворот
        if (!rotationMode.is("None")) {
            rotations = getRotations();
        }

        // Применяем специфичную логику для режимов
        handleModeSpecificLogic();

        // Размещаем блок
        if (canPlace()) {
            placeBlock();
        }
    }

    @EventHandler

    public void onMotion(MotionEvent event) {
        if (targetPos == null || rotations == null) return;

        // Применяем поворот
        if (rotationMode.is("Packet") || rotationMode.is("Silent")) {
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
            lastRotations = rotations.clone();
        } else if (rotationMode.is("Smooth")) {
            applySmoothRotation();
        } else if (rotationMode.is("Snap")) {
            mc.player.rotationYaw = rotations[0];
            mc.player.rotationPitch = rotations[1];
        }
    }

    @EventHandler

    public void onMove(MoveEvent event) {
        if (!safeWalk.getValue()) return;

        // SafeWalk логика
        if (isPlacing && shouldSafeWalk()) {
            if (mc.player.isOnGround() && !mc.gameSettings.keyBindJump.isKeyDown()) {
                double edge = 0.3;

                if (event.getMotion().x != 0 || event.getMotion().z != 0) {
                    double x = mc.player.getPosX() + event.getMotion().x;
                    double z = mc.player.getPosZ() + event.getMotion().z;

                    BlockPos pos = new BlockPos(x, mc.player.getPosY() - 1, z);
                    if (mc.world.isAirBlock(pos)) {
                        // Замедляем движение на краю
                        Vector3d motion = event.getMotion();
                        event.setMotion(new Vector3d(
                                motion.x * 0.3,
                                motion.y,
                                motion.z * 0.3
                        ));
                    }
                }
            }
        }

        // Замедление если включено
        if (slowdown.getValue() && isPlacing) {
            Vector3d motion = event.getMotion();
            double factor = slowdownFactor.getValue();
            event.setMotion(new Vector3d(
                    motion.x * factor,
                    motion.y,
                    motion.z * factor
            ));
        }
    }

    // === МЕТОДЫ РЕЖИМОВ ===


    private void handleModeSpecificLogic() {
        switch (mode.getValue()) {
            case "Hypixel":
                handleHypixelMode();
                break;
            case "HolyWorld":
                handleHolyWorldMode();
                break;
            case "FunTime":
                handleFunTimeMode();
                break;
            case "ReallyWorld":
                handleReallyWorldMode();
                break;
            case "Matrix":
                handleMatrixMode();
                break;
            case "Vulcan":
                handleVulcanMode();
                break;
            case "Verus":
                handleVerusMode();
                break;
            case "Intave":
                handleIntaveMode();
                break;
            case "AAC":
                handleAACMode();
                break;
            case "NCP":
                handleNCPMode();
                break;
            case "Spartan":
                handleSpartanMode();
                break;
            case "Grim":
                handleGrimMode();
                break;
        }
    }


    private void handleHypixelMode() {
        // Hypixel специфичная логика
        if (mc.player.isOnGround() && MoveUtil.isMoving()) {
            hypixelTicks++;

            // Специальная логика для Hypixel
            if (hypixelTicks % 4 == 0) {
                hypixelFlag = true;
            }

            // Замедление для обхода
            if (jumpFix.getValue() && mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.setMotion(
                        mc.player.getMotion().x * 0.85,
                        mc.player.getMotion().y,
                        mc.player.getMotion().z * 0.85
                );
            }
        } else {
            hypixelTicks = 0;
            hypixelFlag = false;
        }
    }


    private void handleHolyWorldMode() {
        // HolyWorld обход
        if (mc.player.isOnGround() && serverTimer.finished(150)) {
            // Отправляем пакеты для обхода
            mc.player.connection.sendPacket(new CEntityActionPacket(
                    mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
            mc.player.connection.sendPacket(new CEntityActionPacket(
                    mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
            serverTimer.reset();
        }
    }


    private void handleFunTimeMode() {
        // FunTime специфичная логика
        if (isPlacing && mc.player.fallDistance > 0) {
            // Уменьшаем fallDistance для обхода
            mc.player.fallDistance *= 0.9f;
        }
    }


    private void handleReallyWorldMode() {
        // ReallyWorld обход
        if (antiCheat.getValue() && serverTimer.finished(100)) {
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                    CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK,
                    mc.player.getPosition().up(),
                    Direction.NORTH
            ));
            serverTimer.reset();
        }
    }


    private void handleMatrixMode() {
        // Matrix обход
        if (matrixTimer.finished(200) && mc.player.isOnGround()) {
            matrixBoost = !matrixBoost;
            matrixTimer.reset();
        }

        if (matrixBoost && isPlacing) {
            // Микро-движения для обхода Matrix
            double offset = ThreadLocalRandom.current().nextDouble(-0.001, 0.001);
            mc.player.setMotion(
                    mc.player.getMotion().x + offset,
                    mc.player.getMotion().y,
                    mc.player.getMotion().z + offset
            );
        }
    }


    private void handleVulcanMode() {
        // Vulcan обход
        if (sprintFix.getValue() && mc.player.isSprinting() && isPlacing) {
            mc.player.connection.sendPacket(new CEntityActionPacket(
                    mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
            mc.player.connection.sendPacket(new CEntityActionPacket(
                    mc.player, CEntityActionPacket.Action.START_SPRINTING));
        }
    }


    private void handleVerusMode() {
        // Verus обход - медленные размещения
        if (isPlacing && placeTimer.finished(75)) {
            // Дополнительная задержка для Verus
            placeTimer.reset();
        }
    }


    private void handleIntaveMode() {
        // Intave обход
        if (isPlacing && serverTimer.finished(120)) {
            // Случайные пакеты для обхода детекции
            mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(
                    mc.player.getPosX() + ThreadLocalRandom.current().nextDouble(-0.001, 0.001),
                    mc.player.getPosY(),
                    mc.player.getPosZ() + ThreadLocalRandom.current().nextDouble(-0.001, 0.001),
                    mc.player.rotationYaw + ThreadLocalRandom.current().nextFloat() * 0.1f - 0.05f,
                    mc.player.rotationPitch,
                    mc.player.isOnGround()
            ));
            serverTimer.reset();
        }
    }


    private void handleAACMode() {
        // AAC обход
        if (jumpFix.getValue() && mc.player.isOnGround() && !mc.gameSettings.keyBindJump.isKeyDown()) {
            // Предотвращаем случайные прыжки
            mc.player.setMotion(mc.player.getMotion().x, 0, mc.player.getMotion().z);
        }
    }


    private void handleNCPMode() {
        // NoCheatPlus обход
        if (eagle.getValue() && mc.player.isOnGround()) {
            BlockPos edge = new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 1, mc.player.getPosZ());
            mc.gameSettings.keyBindSneak.setPressed(mc.world.isAirBlock(edge));
        }
    }


    private void handleSpartanMode() {
        // Spartan обход
        if (isPlacing && rotationTimer.finished(100)) {
            // Плавные повороты для обхода Spartan
            rotationTimer.reset();
        }
    }


    private void handleGrimMode() {
        // GrimAC обход
        if (antiCheat.getValue() && isPlacing) {
            // Специальные пакеты для обхода GrimAC
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        }
    }

    // === ОСНОВНЫЕ МЕТОДЫ ===


    private boolean shouldScaffold() {
        if (mc.player == null || mc.world == null) return false;
        if (!MoveUtil.isMoving() && !downwards.getValue()) return false;
        if (mc.player.isInWater() || mc.player.isInLava()) return false;

        // Проверка Y координаты
        if (keepY.getValue() && mc.player.getPosY() > startY + 1) return false;
        return !sameY.getValue() || !(Math.abs(mc.player.getPosY() - startY) > 0.1);
    }


    private BlockPos getPlacePosition() {
        BlockPos playerPos = mc.player.getPosition();

        switch (positionMode.getValue()) {
            case "Down":
                return playerPos.down();
            case "Extend":
                return getExtendedPosition();
            case "Smart":
                return getSmartPosition();
            case "Edge":
                return getEdgePosition();
            case "Center":
                return getCenterPosition();
            case "Custom":
                return getCustomPosition();
            default:
                return playerPos.down();
        }
    }


    private BlockPos getExtendedPosition() {
        double yaw = Math.toRadians(mc.player.rotationYaw);
        double extend = this.extend.getValue();

        double x = -Math.sin(yaw) * extend;
        double z = Math.cos(yaw) * extend;

        return new BlockPos(
                mc.player.getPosX() + x,
                mc.player.getPosY() - 1,
                mc.player.getPosZ() + z
        );
    }


    private BlockPos getSmartPosition() {
        // Умная логика выбора позиции
        BlockPos[] positions = {
                mc.player.getPosition().down(),
                mc.player.getPosition().down().north(),
                mc.player.getPosition().down().south(),
                mc.player.getPosition().down().east(),
                mc.player.getPosition().down().west()
        };

        for (BlockPos pos : positions) {
            if (mc.world.isAirBlock(pos) && getPlaceData(pos) != null) {
                return pos;
            }
        }

        return mc.player.getPosition().down();
    }


    private BlockPos getEdgePosition() {
        // Позиция на краю
        AxisAlignedBB bb = mc.player.getBoundingBox();
        double minX = bb.minX;
        double maxX = bb.maxX;
        double minZ = bb.minZ;
        double maxZ = bb.maxZ;
        double y = mc.player.getPosY() - 1;

        BlockPos[] edges = {
                new BlockPos(minX - 0.1, y, minZ - 0.1),
                new BlockPos(maxX + 0.1, y, minZ - 0.1),
                new BlockPos(minX - 0.1, y, maxZ + 0.1),
                new BlockPos(maxX + 0.1, y, maxZ + 0.1)
        };

        for (BlockPos pos : edges) {
            if (mc.world.isAirBlock(pos)) {
                return pos;
            }
        }

        return mc.player.getPosition().down();
    }


    private BlockPos getCenterPosition() {
        return new BlockPos(
                Math.floor(mc.player.getPosX()) + 0.5,
                mc.player.getPosY() - 1,
                Math.floor(mc.player.getPosZ()) + 0.5
        );
    }


    private BlockPos getCustomPosition() {
        // Кастомная логика позиционирования
        return getExtendedPosition();
    }


    private PlaceData getPlaceData(BlockPos pos) {
        Direction[] directions = {
                Direction.UP, Direction.NORTH, Direction.SOUTH,
                Direction.EAST, Direction.WEST, Direction.DOWN
        };

        for (Direction direction : directions) {
            BlockPos neighbor = pos.offset(direction);

            if (!mc.world.isAirBlock(neighbor)) {
                Vector3d hitVec = new Vector3d(
                        neighbor.getX() + 0.5 + direction.getOpposite().getXOffset() * 0.5,
                        neighbor.getY() + 0.5 + direction.getOpposite().getYOffset() * 0.5,
                        neighbor.getZ() + 0.5 + direction.getOpposite().getZOffset() * 0.5
                );

                return new PlaceData(neighbor, direction.getOpposite(), hitVec);
            }
        }

        return null;
    }


    private float[] getRotations() {
        if (hitVec == null) return new float[]{mc.player.rotationYaw, mc.player.rotationPitch};

        double diffX = hitVec.x - mc.player.getPosX();
        double diffY = hitVec.y - (mc.player.getPosY() + mc.player.getEyeHeight());
        double diffZ = hitVec.z - mc.player.getPosZ();

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));

        // Добавляем рандомизацию
        if (randomization.getValue() > 0) {
            float random = randomization.getValue();
            yaw += ThreadLocalRandom.current().nextFloat() * random - random / 2;
            pitch += ThreadLocalRandom.current().nextFloat() * random - random / 2;
        }

        return new float[]{yaw, pitch};
    }


    private void applySmoothRotation() {
        if (rotations == null || lastRotations == null) return;

        float speed = rotationSpeed.getValue();
        float yawDiff = MathHelper.wrapDegrees(rotations[0] - lastRotations[0]);
        float pitchDiff = rotations[1] - lastRotations[1];

        float yawStep = Math.min(Math.abs(yawDiff), speed) * Math.signum(yawDiff);
        float pitchStep = Math.min(Math.abs(pitchDiff), speed) * Math.signum(pitchDiff);

        mc.player.rotationYaw = lastRotations[0] + yawStep;
        mc.player.rotationPitch = lastRotations[1] + pitchStep;

        lastRotations[0] = mc.player.rotationYaw;
        lastRotations[1] = mc.player.rotationPitch;
    }


    private boolean canPlace() {
        if (!placeTimer.finished(delay.getValue())) return false;
        if (targetPos == null || placeSide == null) return false;
        if (blocksPlaced >= blocksPerTick.getValue()) return false;

        // Проверки безопасности
        return !airSafe.getValue() || hasNeighbor(targetPos);
    }


    private void placeBlock() {
        // Переключаемся на блок
        if (mc.player.inventory.currentItem != slot) {
            if (lastSlot == -1) {
                lastSlot = mc.player.inventory.currentItem;
            }
            mc.player.inventory.currentItem = slot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
        }

        // Обработка башни
        if (tower.getValue() && !MoveUtil.isMoving() && mc.gameSettings.keyBindJump.isKeyDown()) {
            handleTower();
        }

        // Размещение блока
        switch (placementMode.getValue()) {
            case "Normal":
                placeNormal();
                break;
            case "Legit":
                placeLegit();
                break;
            case "Aggressive":
                placeAggressive();
                break;
            case "Safe":
                placeSafe();
                break;
            case "Godbridge":
                placeGodbridge();
                break;
            case "Moonwalk":
                placeMoonwalk();
                break;
            case "Telly":
                placeTelly();
                break;
        }

        isPlacing = true;
        blocksPlaced++;
        placeTimer.reset();

        // Анимация руки
        if (swing.getValue()) {
            mc.player.swingArm(Hand.MAIN_HAND);
        }

        // Счетчик
        if (counter.getValue() && blocksPlaced % 10 == 0) {
            ChatUtil.addText("§aРазмещено блоков: §f" + blocksPlaced);
        }
    }


    private void placeNormal() {
        BlockRayTraceResult result = new BlockRayTraceResult(
                hitVec, placeSide, targetPos.offset(placeSide), false);

        mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(
                Hand.MAIN_HAND, result));
    }


    private void placeLegit() {
        // Легит размещение с проверками
        if (mc.player.getHeldItemMainhand().getItem() instanceof BlockItem) {
            placeNormal();
        }
    }


    private void placeAggressive() {
        // Агрессивное размещение
        for (int i = 0; i < blocksPerTick.getValue(); i++) {
            placeNormal();
        }
    }


    private void placeSafe() {
        // Безопасное размещение с дополнительными проверками
        if (hasNeighbor(targetPos) && !mc.world.isAirBlock(targetPos)) {
            return;
        }
        placeNormal();
    }


    private void placeGodbridge() {
        // Godbridge техника
        if (mc.player.isOnGround() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.gameSettings.keyBindSneak.setPressed(true);
            placeNormal();
            mc.gameSettings.keyBindSneak.setPressed(false);
        } else {
            placeNormal();
        }
    }


    private void placeMoonwalk() {
        // Moonwalk техника - размещение назад
        if (mc.player.isOnGround()) {
            // Поворачиваемся назад для размещения
            float originalYaw = mc.player.rotationYaw;
            mc.player.rotationYaw += 180;
            placeNormal();
            mc.player.rotationYaw = originalYaw;
        }
    }


    private void placeTelly() {
        // Telly техника - быстрое размещение с рывками
        if (mc.player.isOnGround() && serverTimer.finished(50)) {
            // Быстрое размещение блоков
            placeNormal();
            // Небольшой рывок вперед
            mc.player.setMotion(
                    mc.player.getMotion().x * 1.1,
                    mc.player.getMotion().y,
                    mc.player.getMotion().z * 1.1
            );
            serverTimer.reset();
        }
    }


    private void handleTower() {
        switch (towerMode.getValue()) {
            case "Jump":
                if (mc.player.isOnGround()) {
                    mc.player.jump();
                }
                break;
            case "Motion":
                mc.player.setMotion(mc.player.getMotion().x, 0.42, mc.player.getMotion().z);
                break;
            case "Packet":
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(
                        mc.player.getPosX(),
                        mc.player.getPosY() + 1,
                        mc.player.getPosZ(),
                        false
                ));
                break;
            case "Vanilla":
                if (mc.player.isOnGround()) {
                    mc.player.setMotion(mc.player.getMotion().x, 0.42, mc.player.getMotion().z);
                }
                break;
        }
    }


    private boolean shouldSafeWalk() {
        BlockPos pos = new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 1, mc.player.getPosZ());
        return mc.world.isAirBlock(pos);
    }


    private boolean hasNeighbor(BlockPos pos) {
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            if (!mc.world.isAirBlock(pos.offset(direction))) {
                return true;
            }
        }
        return false;
    }


    private int getBlockSlot() {
        // Приоритетные блоки для scaffold
        List<Item> blocks = Arrays.asList(
                Items.COBBLESTONE, Items.DIRT, Items.STONE,
                Items.NETHERRACK, Items.SANDSTONE,
                Items.GLASS, Items.CLAY
        );

        for (Item block : blocks) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == block) {
                    return i;
                }
            }
        }

        // Поиск любого блока
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                Block block = ((BlockItem) stack.getItem()).getBlock();
                if (block != Blocks.AIR && block.getDefaultState().isOpaqueCube(mc.world, BlockPos.ZERO)) {
                    return i;
                }
            }
        }

        return -1;
    }


    private int getBlockCount() {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                count += stack.getCount();
            }
        }
        return count;
    }

    // === СПЕЦИАЛЬНЫЕ МЕТОДЫ ===


    public boolean isScaffolding() {
        return isEnabled() && isPlacing;
    }


    public BlockPos getTargetPosition() {
        return targetPos;
    }


    public int getBlocksPlaced() {
        return blocksPlaced;
    }


    public float[] getCurrentRotations() {
        return rotations != null ? rotations.clone() : new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
    }

    // === RESET МЕТОДЫ ===


    private void resetPlacementData() {
        targetPos = null;
        placeSide = null;
        hitVec = null;
        rotations = null;
        isPlacing = false;
        blocksPlaced = 0;
    }


    private void resetTimers() {
        placeTimer.reset();
        rotationTimer.reset();
        serverTimer.reset();
        matrixTimer.reset();
    }


    private void resetModeData() {
        hypixelFlag = false;
        hypixelTicks = 0;
        matrixBoost = false;
    }

    // === ДОПОЛНИТЕЛЬНЫЕ НАСТРОЙКИ ===

    // Настройки для специальных техник
    private final BooleanSetting fastPlace = new BooleanSetting(this, "Быстрое размещение", false);
    private final BooleanSetting autoJump = new BooleanSetting(this, "Авто прыжок", false);
    private final BooleanSetting smartRotation = new BooleanSetting(this, "Умные повороты", true);
    private final BooleanSetting rayCast = new BooleanSetting(this, "Ray Cast", true);
    private final BooleanSetting predictMove = new BooleanSetting(this, "Предсказание движения", false);

    // Настройки для разных серверов
    private final BooleanSetting hypixelBoost = new BooleanSetting(this, "Hypixel Boost", false)
            .setVisible(() -> mode.is("Hypixel"));
    private final BooleanSetting matrixBypass = new BooleanSetting(this, "Matrix Bypass", true)
            .setVisible(() -> mode.is("Matrix"));
    private final BooleanSetting vulcanSafe = new BooleanSetting(this, "Vulcan Safe", true)
            .setVisible(() -> mode.is("Vulcan"));

    // === КЛАСС ДЛЯ ДАННЫХ РАЗМЕЩЕНИЯ ===

    private record PlaceData(BlockPos pos, Direction direction, Vector3d hitVec) {
    }

    // === ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ОБХОДА ===


    private void sendBypassPackets() {
        switch (mode.getValue()) {
            case "Hypixel":
                sendHypixelBypass();
                break;
            case "Matrix":
                sendMatrixBypass();
                break;
            case "Vulcan":
                sendVulcanBypass();
                break;
            case "Grim":
                sendGrimBypass();
                break;
        }
    }


    private void sendHypixelBypass() {
        if (hypixelBoost.getValue() && hypixelFlag) {
            mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(
                    mc.player.getPosX(),
                    mc.player.getPosY() + 0.001,
                    mc.player.getPosZ(),
                    mc.player.isOnGround()
            ));
        }
    }


    private void sendMatrixBypass() {
        if (matrixBypass.getValue() && matrixBoost) {
            mc.player.connection.sendPacket(new CEntityActionPacket(
                    mc.player, CEntityActionPacket.Action.START_SPRINTING));
            mc.player.connection.sendPacket(new CEntityActionPacket(
                    mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
        }
    }


    private void sendVulcanBypass() {
        if (vulcanSafe.getValue() && isPlacing) {
            mc.player.connection.sendPacket(new CPlayerDiggingPacket(
                    CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK,
                    targetPos,
                    Direction.UP
            ));
        }
    }


    private void sendGrimBypass() {
        if (antiCheat.getValue()) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
        }
    }

    // === МЕТОДЫ ОБНОВЛЕНИЯ ===

    @EventHandler

    public void onTick() {
        wasPlacing = isPlacing;

        // Сброс счетчика блоков каждый тик
        if (blocksPlaced > 0) {
            blocksPlaced = 0;
        }

        // Отправка bypass пакетов
        if (antiCheat.getValue()) {
            sendBypassPackets();
        }

        // Обновление позиции башни
        if (tower.getValue() && !MoveUtil.isMoving()) {
            startY = mc.player.getPosY();
        }
    }

    // === МЕТОДЫ РЕНДЕРА (если нужны) ===


    public void renderBlock() {
        if (!render.getValue() || targetPos == null) {
        }

        // Здесь можно добавить рендер кода для отображения блока
        // который будет размещен (требует OpenGL код)
    }


    public String getDisplayInfo() {
        if (!counter.getValue()) return "";

        int blockCount = getBlockCount();
        return String.format("§7[§fScaffold§7] §fБлоков: §a%d §7| §fРежим: §b%s",
                blockCount, mode.getValue());
    }
}