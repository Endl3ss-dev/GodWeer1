package tech.lightclient.modules.impl.movement;

import tech.lightclient.client.events.impl.client.PostEvent;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.player.JumpEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.math.time.StopWatch;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.client.utils.player.rotation.RotationTask;
import tech.lightclient.client.utils.player.swap.InvBypassMode;
import tech.lightclient.client.utils.player.swap.InventoryTasks;
import tech.lightclient.client.utils.player.swap.SwapConfig;
import tech.lightclient.client.utils.player.swap.SwapInventory;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;

@FunctionAdd(name = "Spider", alias = "Spider", category = Category.Movement, description = "Позволяет забираться по стенам")
public class Spider extends Function {
    private final ModeSetting mode = new ModeSetting("Режим", "FuntimeFast", "FuntimeFast", "Громоотводы", "SpookyTime", "Blocks");
    private final ModeSetting funtimeFastMode = new ModeSetting("FuntimeFast тип", "SphereJump", "SphereJump", "SphereBoost", "SphereGlide", "SphereAnchor", "SphereLaunch", "SphereBounce")
            .setVisible(() -> mode.is("FuntimeFast"));
    private final BooleanSetting instantSwap = new BooleanSetting("Моментальный свап", false)
            .setVisible(() -> mode.is("Blocks"));

    private static final String ROTATION_TASK = "spider";
    private static final int ROTATION_PRIORITY = 8;

    private final StopWatch climbTimer = new StopWatch();
    private final StopWatch lightningTimer = new StopWatch();
    private int lastSlot;
    private float targetYaw;
    private int funtimeHeadSlot = -1;
    private boolean funtimeHeadWasOffhand;
    private boolean funtimeHeadSwapped;
    private int blockSwapSlotId = -1;
    private boolean blockSwapped;

    public Spider() {
        addSettings(mode, funtimeFastMode, instantSwap);
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            lastSlot = mc.player.getInventory().getSelectedSlot();
            targetYaw = mc.player.getHorizontalFacing().getPositiveHorizontalDegrees();
        }
        blockSwapped = false;
        blockSwapSlotId = -1;
        climbTimer.reset();
        lightningTimer.reset();
        super.onEnable();
    }

    @EventHandler
    public void onPost(PostEvent event) {
        if (nullCheck()) {
            return;
        }

        if (mode.is("FuntimeFast")) {
            handleFuntimeFast();
        } else if (mode.is("Громоотводы")) {
            handleLightningRods();
        } else if (mode.is("SpookyTime")) {
            handleSpookyTime();
        } else if (mode.is("Blocks")) {
            handleBlocks();
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (nullCheck() || !mode.is("Blocks") || !hasAnySideCollision()) {
            return;
        }

        if (!hasBlockAvailable()) {
            toggle();
            return;
        }

        mc.options.jumpKey.setPressed(true);
    }

    @EventHandler
    public void onJump(JumpEvent event) {
        climbTimer.reset();
    }

    private void handleFuntimeFast() {
        if (!hasAnySideCollision()) {
            restoreFuntimeHead();
            return;
        }

        switch (funtimeFastMode.get()) {
            case "SphereJump":
                handleSphereJump();
                break;
            case "SphereBoost":
                handleSphereBoost();
                break;
            case "SphereGlide":
                handleSphereGlide();
                break;
            case "SphereAnchor":
                handleSphereAnchor();
                break;
            case "SphereLaunch":
                handleSphereLaunch();
                break;
            case "SphereBounce":
                handleSphereBounce();
                break;
        }
    }

    private void handleSphereJump() {
        // SphereJump - классическое поднятие по сфере через прыжки (максимально легитное)
        if (!hasAnySideCollision()) {
            restoreFuntimeHead();
            return;
        }

        // Свапаем голову в основную руку
        if (!prepareFuntimeHead()) {
            return;
        }

        if (climbTimer.hasReached(430)) {
            // Максимально легитный прыжок по сфере
            mc.player.setOnGround(true);
            mc.player.jump();
            
            // Естественная постановка сферы после прыжка
            BlockHitResult hit = PlayerUtils.raycast(3.5, mc.player.getYaw(), 78.0F, false);
            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                mc.player.swingHand(Hand.MAIN_HAND);
                
                // Минимальное влияние на движение для естественности
                Vec3d currentVel = mc.player.getVelocity();
                // Слегка корректируем вертикальную скорость для плавности
                mc.player.setVelocity(currentVel.x, Math.min(currentVel.y, 0.4), currentVel.z);
            }
            
            climbTimer.reset();
        }
    }

    private void handleSphereBoost() {
        // SphereBoost - ускоренное поднятие (более легитное)
        if (!hasAnySideCollision()) {
            restoreFuntimeHead();
            return;
        }

        // Свапаем голову в основную руку
        if (!prepareFuntimeHead()) {
            return;
        }

        if (climbTimer.hasReached(280)) {
            // Умеренно быстрый прыжок
            mc.player.setOnGround(true);
            mc.player.jump();
            
            // Очень небольшой дополнительный буст
            Vec3d currentVel = mc.player.getVelocity();
            mc.player.setVelocity(currentVel.x, Math.min(currentVel.y + 0.04, 0.42), currentVel.z);
            
            // Ставим сферу сразу после прыжка
            BlockHitResult hit = PlayerUtils.raycast(3.8, mc.player.getYaw(), 80.0F, false);
            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
            
            climbTimer.reset();
        }
    }

    private void handleSphereGlide() {
        // SphereGlide - плавное скольжение по сфере (легитное)
        if (!hasAnySideCollision()) {
            restoreFuntimeHead();
            return;
        }

        // Свапаем голову в основную руку
        if (!prepareFuntimeHead()) {
            return;
        }

        if (climbTimer.hasReached(480)) {
            // Плавный прыжок для скольжения
            mc.player.setOnGround(true);
            mc.player.jump();
            
            // Немного уменьшаем высоту прыжка для плавности
            Vec3d currentVel = mc.player.getVelocity();
            mc.player.setVelocity(currentVel.x, Math.min(currentVel.y * 0.85, 0.35), currentVel.z);
            
            // Легкое сглаживание горизонтального движения
            mc.player.setVelocity(mc.player.getVelocity().x * 0.92, mc.player.getVelocity().y, mc.player.getVelocity().z * 0.92);
            
            // Ставим сферу для скольжения
            BlockHitResult hit = PlayerUtils.raycast(3.3, mc.player.getYaw(), 76.0F, false);
            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
            
            climbTimer.reset();
        }
    }

    private void handleSphereAnchor() {
        // SphereAnchor - стабильное закрепление на сфере (легитное)
        if (!hasAnySideCollision()) {
            restoreFuntimeHead();
            return;
        }

        // Свапаем голову в основную руку
        if (!prepareFuntimeHead()) {
            return;
        }

        if (climbTimer.hasReached(460)) {
            // Стабильный прыжок с закреплением
            mc.player.setOnGround(true);
            mc.player.jump();
            
            // Легкое стабилизирующее позиционирование (без резких поворотов)
            if (mc.player.getVelocity().y > 0.3) {
                mc.player.setVelocity(mc.player.getVelocity().x, 0.35, mc.player.getVelocity().z);
            }
            
            // Закрепляемся на сфере
            BlockHitResult hit = PlayerUtils.raycast(3.2, mc.player.getYaw(), 78.0F, false);
            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                mc.player.swingHand(Hand.MAIN_HAND);
                
                // Легкая стабилизация скорости (не слишком агрессивная)
                Vec3d currentVel = mc.player.getVelocity();
                mc.player.setVelocity(currentVel.x * 0.88, currentVel.y * 0.9, currentVel.z * 0.88);
            }
            
            climbTimer.reset();
        }
    }

    private void handleSphereLaunch() {
        // SphereLaunch - легкий запуск с сферы (более легитный)
        if (!hasAnySideCollision()) {
            restoreFuntimeHead();
            return;
        }

        // Свапаем голову в основную руку
        if (!prepareFuntimeHead()) {
            return;
        }

        if (climbTimer.hasReached(380)) {
            // Легитный прыжок с небольшой помощью от сферы
            mc.player.setOnGround(true);
            mc.player.jump();
            
            // Ставим сферу для легкого "отталкивания"
            BlockHitResult hit = PlayerUtils.raycast(3.8, mc.player.getYaw(), 80.0F, false);
            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                mc.player.swingHand(Hand.MAIN_HAND);
                
                // Очень небольшой импульс вперед для эффекта "помощи" от сферы
                float yawRad = (float) Math.toRadians(mc.player.getYaw());
                float forwardBoost = 0.08f; // Очень маленький буст
                float upwardBoost = 0.04f;  // Минимальный вертикальный буст
                
                mc.player.addVelocity(-Math.sin(yawRad) * forwardBoost, upwardBoost, Math.cos(yawRad) * forwardBoost);
                
                // Слегка увеличиваем вертикальную скорость для плавн��го поднятия
                Vec3d currentVel = mc.player.getVelocity();
                mc.player.setVelocity(currentVel.x, Math.min(currentVel.y + 0.05, 0.42), currentVel.z);
            }
            
            climbTimer.reset();
        }
    }

    private void handleSphereBounce() {
        // SphereBounce - легкое отталкивание от сферы (более легитное)
        if (!hasAnySideCollision()) {
            restoreFuntimeHead();
            return;
        }

        // Свапаем голову в основную руку
        if (!prepareFuntimeHead()) {
            return;
        }

        if (climbTimer.hasReached(320)) {
            // Прыжок с легким отталкиванием
            mc.player.setOnGround(true);
            mc.player.jump();
            
            // Ставим сферу для легкого отталкивания
            BlockHitResult hit = PlayerUtils.raycast(3.5, mc.player.getYaw(), 78.0F, false);
            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                // Стандартное взаимодействие вместо пакетов
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                mc.player.swingHand(Hand.MAIN_HAND);
                
                // Очень легкий эффект "пружины" от сферы
                Vec3d currentVel = mc.player.getVelocity();
                float bounceEffect = 0.06f; // Минимальный эффект отскока
                mc.player.setVelocity(currentVel.x, Math.min(currentVel.y + bounceEffect, 0.4), currentVel.z);
            }
            
            climbTimer.reset();
        }
    }

    private boolean prepareFuntimeHead() {
        int selected = mc.player.getInventory().getSelectedSlot();
        if (mc.player.getMainHandStack().isOf(Items.PLAYER_HEAD)) {
            funtimeHeadSwapped = true;
            return true;
        }

        if (mc.player.getOffHandStack().isOf(Items.PLAYER_HEAD)) {
            PlayerInventoryUtil.clickSlot(45, selected, SlotActionType.SWAP);
            funtimeHeadWasOffhand = true;
            funtimeHeadSlot = 45;
            funtimeHeadSwapped = true;
            return true;
        }

        funtimeHeadSlot = findInventorySlotId(Items.PLAYER_HEAD);
        if (funtimeHeadSlot == -1) {
            funtimeHeadSlot = findHotbarSlotId(Items.PLAYER_HEAD);
        }
        if (funtimeHeadSlot == -1) {
            return false;
        }

        PlayerInventoryUtil.clickSlot(funtimeHeadSlot, selected, SlotActionType.SWAP);
        funtimeHeadSwapped = true;
        return true;
    }

    private void placeFuntimeHead() {
        if (!mc.player.getMainHandStack().isOf(Items.PLAYER_HEAD)) {
            return;
        }

        BlockHitResult hit = PlayerUtils.raycast(4.0, mc.player.getYaw(), 82.0F, false);
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private void restoreFuntimeHead() {
        if (mc.player == null) {
            return;
        }

        // Для всех новых режимов восстанавливаем через свап
        if (!funtimeHeadSwapped) {
            return;
        }

        int selected = mc.player.getInventory().getSelectedSlot();
        if (funtimeHeadWasOffhand) {
            PlayerInventoryUtil.clickSlot(45, selected, SlotActionType.SWAP);
        } else if (funtimeHeadSlot != -1) {
            PlayerInventoryUtil.clickSlot(funtimeHeadSlot, selected, SlotActionType.SWAP);
        }
        PlayerInventoryUtil.updateSlots();
        funtimeHeadSlot = -1;
        funtimeHeadWasOffhand = false;
        funtimeHeadSwapped = false;
    }

    private void handleSpookyTime() {
        if (!hasAnySideCollision()) {
            mc.options.useKey.setPressed(false);
            return;
        }

        int waterSlot = findHotbarItem(Items.WATER_BUCKET);
        if (waterSlot == -1) {
            mc.options.useKey.setPressed(false);
            return;
        }

        mc.player.getInventory().setSelectedSlot(waterSlot);
        RotationTask.setTargetRotation(mc.player.getYaw(), 77.3F, 8);
        mc.options.sneakKey.setPressed(true);
        mc.options.forwardKey.setPressed(true);
        mc.options.jumpKey.setPressed(true);
        mc.options.useKey.setPressed(true);
    }

    private void handleBlocks() {
        if (!hasAnySideCollision()) {
            return;
        }

        if (!prepareBlockSwap()) {
            toggle();
            return;
        }

        mc.options.jumpKey.setPressed(true);
        RotationTask.create(ROTATION_TASK, ROTATION_PRIORITY);
        RotationTask.setTargetRotation(targetYaw, 80.0F, ROTATION_PRIORITY);

        if (isPlaceableBlock(mc.player.getMainHandStack().getItem())
                && mc.player.fallDistance > 0.0F && mc.player.fallDistance < 2.0F) {
            placeBlockFromMainHand();
        }
    }

    /**
     * Тянет любой блок из инвентаря в основную руку.
     * "Моментальный свап" - свап через HolyWorld (как в AutoSwap), без остановки движения.
     * Без галочки - свап с остановкой движения, как в режиме FunTime.
     */
    private boolean prepareBlockSwap() {
        if (isPlaceableBlock(mc.player.getMainHandStack().getItem())) {
            return true;
        }

        Slot slot = findBlockSlotInInventory();
        if (slot == null) {
            return false;
        }

        if (instantSwap.get()) {
            // Свап уже в очереди - ждём его выполнения
            if (!InventoryTasks.isRealCompleted(Spider.class)) {
                return true;
            }

            rememberBlockSwap(slot.id);
            SwapConfig.invBypassMode = InvBypassMode.HOLY_WORLD;
            SwapConfig.inventoryStopMoving = false;
            InventoryTasks.addTask(Spider.class, () -> SwapInventory.swapHand(slot, Hand.MAIN_HAND, true));
        } else {
            rememberBlockSwap(slot.id);
            PlayerInventoryUtil.swapHandPhased(slot, Hand.MAIN_HAND, true);
        }
        return true;
    }

    /** Запоминает слот, куда ушёл предмет из руки при первом свапе блока. */
    private void rememberBlockSwap(int slotId) {
        if (blockSwapped) {
            return;
        }
        blockSwapSlotId = slotId;
        blockSwapped = true;
    }

    /** Возвращает в руку предмет, который лежал там до свапа блока. */
    private void restoreBlockSwap() {
        if (!blockSwapped || mc.player == null) {
            return;
        }

        int slotId = blockSwapSlotId;
        blockSwapped = false;
        blockSwapSlotId = -1;

        Slot slot = slotById(slotId);
        if (slot == null) {
            return;
        }

        if (instantSwap.get()) {
            SwapConfig.invBypassMode = InvBypassMode.HOLY_WORLD;
            SwapConfig.inventoryStopMoving = false;
            InventoryTasks.addTask(Spider.class, () -> SwapInventory.swapHand(slot, Hand.MAIN_HAND, true));
        } else {
            PlayerInventoryUtil.swapHandPhased(slot, Hand.MAIN_HAND, true);
        }
    }

    private void resetRotation() {
        if (!RotationTask.hasTask(ROTATION_TASK)) {
            return;
        }

        if (RotationTask.rotationPriority > ROTATION_PRIORITY) {
            RotationTask.remove(ROTATION_TASK);
            return;
        }

        RotationTask.rotationState = RotationTask.RotationState.RESET;
        RotationTask.returnStartTime = System.currentTimeMillis() - 100L;
        RotationTask.rotationPriority = 0;
        RotationTask.inactiveMs = 0L;
        RotationTask.needSmoothReset = true;
        RotationTask.resetDelayMs = 400L;
        RotationTask.remove(ROTATION_TASK);
    }

    private void handleLightningRods() {
        if (!mc.player.horizontalCollision || !lightningTimer.hasReached(1)) {
            return;
        }

        mc.player.setOnGround(true);
        mc.player.jump();

        int rodSlot = findHotbarItem(Items.LIGHTNING_ROD);
        if (rodSlot == -1) {
            int inventorySlotId = findInventorySlotId(Items.LIGHTNING_ROD);
            if (inventorySlotId != -1) {
                PlayerInventoryUtil.clickSlot(inventorySlotId, lastSlot, SlotActionType.SWAP);
                PlayerInventoryUtil.updateSlots();
                rodSlot = lastSlot;
            }
        }

        if (rodSlot != -1) {
            placeLightningRods(rodSlot);
            mc.player.fallDistance = 0.0F;
            lightningTimer.reset();
        }
    }

    private boolean hasAnySideCollision() {
        Box box = mc.player.getBoundingBox();
        Box sideBox = new Box(
                box.minX - 0.06, box.minY + 0.05, box.minZ - 0.06,
                box.maxX + 0.06, box.maxY - 0.05, box.maxZ + 0.06
        );

        return BlockPos.stream(sideBox).anyMatch(pos -> {
            BlockState state = mc.world.getBlockState(pos);
            return !state.getCollisionShape(mc.world, pos).isEmpty()
                    && state.getCollisionShape(mc.world, pos).getBoundingBoxes().stream()
                    .map(shapeBox -> shapeBox.offset(pos.getX(), pos.getY(), pos.getZ()))
                    .anyMatch(sideBox::intersects);
        });
    }

    private boolean isPlaceableBlock(Item item) {
        return item instanceof BlockItem && item != Items.TORCH;
    }

    /** Любой блок в инвентаре (включая хотбар), доступный для свапа в руку. */
    private Slot findBlockSlotInInventory() {
        return PlayerInventoryUtil.slots()
                .filter(slot -> slot.id >= 9 && slot.id <= 44)
                .filter(slot -> isPlaceableBlock(slot.getStack().getItem()))
                .findFirst()
                .orElse(null);
    }

    private Slot slotById(int slotId) {
        if (slotId == -1) {
            return null;
        }
        return PlayerInventoryUtil.slots()
                .filter(slot -> slot.id == slotId)
                .findFirst()
                .orElse(null);
    }

    private boolean hasBlockAvailable() {
        return isPlaceableBlock(mc.player.getMainHandStack().getItem()) || findBlockSlotInInventory() != null;
    }

    private int findHotbarItem(Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(item)) {
                return i;
            }
        }
        return -1;
    }

    private int findHotbarSlotId(Item item) {
        int slot = findHotbarItem(item);
        return slot == -1 ? -1 : 36 + slot;
    }

    private int findInventorySlotId(Item item) {
        return PlayerInventoryUtil.slots()
                .filter(slot -> slot.id >= 9 && slot.id <= 35)
                .filter(slot -> slot.getStack().isOf(item))
                .map(slot -> slot.id)
                .findFirst()
                .orElse(-1);
    }

    /** Постановка блока, который уже лежит в основной руке (после свапа). */
    private void placeBlockFromMainHand() {
        BlockHitResult hitResult = PlayerUtils.raycast(4.0, targetYaw, 80.0F, false);
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        }
    }

    private void placeLightningRods(int slot) {
        int previousSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.getInventory().setSelectedSlot(slot);

        BlockPos playerPos = mc.player.getBlockPos();
        for (int offset = 1; offset <= 2; offset++) {
            BlockPos target = playerPos.up(offset);
            if (mc.world.getBlockState(target).isAir()) {
                placeBlockAt(target);
            }
        }

        mc.player.getInventory().setSelectedSlot(previousSlot);
    }

    private void placeBlockAt(BlockPos pos) {
        Vec3d hitVec = Vec3d.ofCenter(pos);
        BlockHitResult hitResult = new BlockHitResult(hitVec, Direction.UP, pos.down(), false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    @Override
    public void onDisable() {
        // Сбрасываем silent-ротацию, иначе голова остаётся смотреть вниз
        resetRotation();

        if (mc.player != null && mode.is("FuntimeFast")) {
            // Для всех новых режимов восстанавливаем голову
            restoreFuntimeHead();
        } else if (mc.player != null && mode.is("Blocks") && blockSwapped) {
            // Возвращаем предмет, который был в руке до свапа блока
            restoreBlockSwap();
        } else if (mc.player != null && !mode.is("Громоотводы")) {
            mc.player.getInventory().setSelectedSlot(lastSlot);
        }

        if (mc.options != null) {
            mc.options.useKey.setPressed(false);
            mc.options.forwardKey.setPressed(false);
            mc.options.sneakKey.setPressed(false);
            mc.options.jumpKey.setPressed(false);
        }

        super.onDisable();
    }
}
