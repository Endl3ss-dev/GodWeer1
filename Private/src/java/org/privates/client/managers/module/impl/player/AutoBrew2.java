package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.rotation.SensUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "FtFarm", category = Category.MISC, description = "Автоматически варит зелья невидимости")
public class AutoBrew2 extends Module {

    public static AutoBrew2 getInstance() {
        return Instance.get(AutoBrew2.class);
    }

    private final SliderSetting searchRadius = new SliderSetting(this, "Радиус поиска", 5, 3, 10, 1);
    private final BooleanSetting rotate = new BooleanSetting(this, "Ротация", true);

    private final StopWatch timer = new StopWatch();
    private final StopWatch collectTimer = new StopWatch();
    private final StopWatch actionTimer = new StopWatch();
    private final StopWatch rotationTimer = new StopWatch();
    private final StopWatch humanizeTimer = new StopWatch();

    private final List<BlockPos> brewingStands = new ArrayList<>();
    private int currentBrewingStand = 0;
    private BrewingPhase currentPhase = BrewingPhase.SEARCH;
    private PlaceState placeState = PlaceState.IDLE;

    private Vector2f targetRotation = null;
    private boolean isRotating = false;
    private boolean rotationComplete = false;

    private boolean needToOpenBrewingStand = false;
    private boolean needToOpenChest = false;
    private BlockPos targetBrewingStand = null;
    private BlockPos targetChest = null;
    private boolean ingredientPlaced = false;
    private Item lastPlacedItem = null;

    private int currentBottleSlot = 0;
    private int sourceSlot = -1;
    private int targetSlot = -1;
    private Item targetItem = null;

    private final Random random = new Random();

    private enum BrewingPhase {
        SEARCH,
        WATER_BOTTLES,
        GOLDEN_CARROT,
        FERMENTED_SPIDER_EYE,
        REDSTONE,
        COLLECTING,
        STORING,
        FINISHED
    }

    private enum PlaceState {
        IDLE,
        FIND_ITEM,
        WAIT_BEFORE_PICKUP,
        PICKUP_ITEM,
        WAIT_AFTER_PICKUP,
        PLACE_ITEM,
        WAIT_AFTER_PLACE,
        PUT_BACK,
        WAIT_AFTER_PUT_BACK,
        NEXT_SLOT,
        FINISHED
    }

    @Override
    public void onEnable() {
        super.onEnable();
        timer.reset();
        collectTimer.reset();
        actionTimer.reset();
        rotationTimer.reset();
        humanizeTimer.reset();
        brewingStands.clear();
        currentBrewingStand = 0;
        currentPhase = BrewingPhase.SEARCH;
        placeState = PlaceState.IDLE;
        targetRotation = null;
        isRotating = false;
        rotationComplete = false;
        needToOpenBrewingStand = false;
        needToOpenChest = false;
        targetBrewingStand = null;
        targetChest = null;
        ingredientPlaced = false;
        lastPlacedItem = null;
        currentBottleSlot = 0;
        sourceSlot = -1;
        targetSlot = -1;
        targetItem = null;
        ChatUtil.addText("§aАвто-варка активирована");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        brewingStands.clear();
        currentBrewingStand = 0;
        currentPhase = BrewingPhase.SEARCH;
        placeState = PlaceState.IDLE;
        targetRotation = null;
        isRotating = false;
        rotationComplete = false;
        needToOpenBrewingStand = false;
        needToOpenChest = false;
        targetBrewingStand = null;
        targetChest = null;
        ingredientPlaced = false;
        lastPlacedItem = null;
        currentBottleSlot = 0;
        sourceSlot = -1;
        targetSlot = -1;
        targetItem = null;
        ChatUtil.addText("§cАвто-варка деактивирована");
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        handleRotation();

        if (currentPhase == BrewingPhase.SEARCH) {
            findBrewingStands();
            if (brewingStands.isEmpty()) {
                ChatUtil.addText("§cЗельеварки не найдены!");
                toggle();
                return;
            }
            currentPhase = BrewingPhase.WATER_BOTTLES;
            currentBrewingStand = 0;
            needToOpenBrewingStand = true;
            targetBrewingStand = brewingStands.get(currentBrewingStand);
            ingredientPlaced = false;
            return;
        }

        if (needToOpenChest && targetChest != null) {
            if (!actionTimer.finished(getRandomDelay())) return;
            if (rotate.getValue() && !rotationComplete) {
                startRotation(targetChest);
                return;
            }
            openChest(targetChest);
            needToOpenChest = false;
            rotationComplete = false;
            actionTimer.reset();
            return;
        }

        if (needToOpenBrewingStand && targetBrewingStand != null) {
            if (!actionTimer.finished(getRandomDelay())) return;
            if (rotate.getValue() && !rotationComplete) {
                startRotation(targetBrewingStand);
                return;
            }
            openBrewingStand(targetBrewingStand);
            needToOpenBrewingStand = false;
            rotationComplete = false;
            actionTimer.reset();
            return;
        }

        if (currentPhase == BrewingPhase.STORING) {
            if (mc.player.openContainer instanceof ChestContainer) {
                handleStoringPhase();
            } else if (!needToOpenChest) {
                needToOpenChest = true;
            }
            return;
        }

        if (!(mc.player.openContainer instanceof BrewingStandContainer)) {
            if (targetBrewingStand != null && !needToOpenBrewingStand) {
                needToOpenBrewingStand = true;
            }
            return;
        }

        switch (currentPhase) {
            case WATER_BOTTLES:
                handleWaterBottlesPhase();
                break;
            case GOLDEN_CARROT:
                handleIngredientPhase(Items.GOLDEN_CARROT, BrewingPhase.FERMENTED_SPIDER_EYE, "золотой моркови");
                break;
            case FERMENTED_SPIDER_EYE:
                handleIngredientPhase(Items.FERMENTED_SPIDER_EYE, BrewingPhase.REDSTONE, "паучьего глаза");
                break;
            case REDSTONE:
                handleIngredientPhase(Items.REDSTONE, BrewingPhase.COLLECTING, "редстоуна");
                break;
            case COLLECTING:
                handleCollectingPhase();
                break;
            case FINISHED:
                currentPhase = BrewingPhase.WATER_BOTTLES;
                currentBrewingStand = 0;
                needToOpenBrewingStand = true;
                targetBrewingStand = brewingStands.get(currentBrewingStand);
                ingredientPlaced = false;
                placeState = PlaceState.IDLE;
                break;
        }
    }

    private void startRotation(BlockPos pos) {
        if (!isRotating) {
            Vector3d targetPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            targetRotation = calculateRotation(targetPos);
            isRotating = true;
            rotationTimer.reset();
        }
    }

    private void handleRotation() {
        if (!rotate.getValue() || !isRotating || targetRotation == null) return;

        if (!rotationTimer.finished(getRandomDelay())) return;

        float newYaw = smoothRotation(mc.player.rotationYaw, targetRotation.x, 10F + random.nextFloat() * 5F);
        float newPitch = MathHelper.clamp(smoothRotation(mc.player.rotationPitch, targetRotation.y, 10F + random.nextFloat() * 5F), -90F, 90F);

        mc.player.rotationYaw = newYaw;
        mc.player.rotationYawHead = newYaw;
        mc.player.renderYawOffset = newYaw;
        mc.player.prevRotationYaw = newYaw;
        mc.player.prevRotationYawHead = newYaw;

        mc.player.rotationPitch = newPitch;
        mc.player.prevRotationPitch = newPitch;

        float yawDiff = Math.abs(MathHelper.wrapDegrees(newYaw - targetRotation.x));
        float pitchDiff = Math.abs(newPitch - targetRotation.y);

        if (yawDiff < 1.0f && pitchDiff < 1.0f) {
            rotationComplete = true;
            isRotating = false;
            targetRotation = null;
        }

        rotationTimer.reset();
    }

    private void openBrewingStand(BlockPos pos) {
        Vector3d hitVec = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        BlockRayTraceResult result = new BlockRayTraceResult(hitVec, Direction.UP, pos, false);
        mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, result);
    }

    private void openChest(BlockPos pos) {
        Vector3d hitVec = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        BlockRayTraceResult result = new BlockRayTraceResult(hitVec, Direction.UP, pos, false);
        mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, result);
    }

    private void findBrewingStands() {
        brewingStands.clear();
        BlockPos playerPos = mc.player.getPosition();
        int radius = searchRadius.getValue().intValue();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    TileEntity tileEntity = mc.world.getTileEntity(pos);
                    if (tileEntity instanceof BrewingStandTileEntity) {
                        brewingStands.add(pos);
                    }
                }
            }
        }
    }

    private BlockPos findNearestChest() {
        BlockPos playerPos = mc.player.getPosition();
        int radius = searchRadius.getValue().intValue();
        BlockPos nearestChest = null;
        double minDistance = Double.MAX_VALUE;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
                        double distance = mc.player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ());
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestChest = pos;
                        }
                    }
                }
            }
        }
        return nearestChest;
    }

    private void handleWaterBottlesPhase() {
        if (placeState == PlaceState.IDLE) {
            boolean allFilled = true;
            for (int i = 0; i < 3; i++) {
                if (getItem(i) instanceof AirItem) {
                    allFilled = false;
                    break;
                }
            }

            if (!allFilled) {
                currentBottleSlot = 0;
                targetItem = Items.POTION;
                placeState = PlaceState.FIND_ITEM;
            } else if (!ingredientPlaced && getItem(3) instanceof AirItem) {
                targetItem = Items.NETHER_WART;
                targetSlot = 3;
                placeState = PlaceState.FIND_ITEM;
            } else if (ingredientPlaced) {
                ingredientPlaced = false;
                currentBrewingStand++;
                if (currentBrewingStand >= brewingStands.size()) {
                    currentBrewingStand = 0;
                    currentPhase = BrewingPhase.GOLDEN_CARROT;
                }
                mc.player.closeScreen();
                targetBrewingStand = brewingStands.get(currentBrewingStand);
                needToOpenBrewingStand = true;
                placeState = PlaceState.IDLE;
            }
        } else {
            handlePlaceItemLogic();
        }
    }

    private void handleIngredientPhase(Item ingredient, BrewingPhase nextPhase, String itemName) {
        if (placeState == PlaceState.IDLE) {
            if (!ingredientPlaced && getItem(3) instanceof AirItem) {
                targetItem = ingredient;
                targetSlot = 3;
                placeState = PlaceState.FIND_ITEM;
            } else if (ingredientPlaced) {
                ingredientPlaced = false;
                currentBrewingStand++;
                if (currentBrewingStand >= brewingStands.size()) {
                    currentBrewingStand = 0;
                    currentPhase = nextPhase;
                    if (nextPhase == BrewingPhase.COLLECTING) {
                        collectTimer.reset();
                    }
                }
                mc.player.closeScreen();
                targetBrewingStand = brewingStands.get(currentBrewingStand);
                needToOpenBrewingStand = true;
                placeState = PlaceState.IDLE;
            }
        } else {
            handlePlaceItemLogic();
        }
    }

    private void handlePlaceItemLogic() {
        switch (placeState) {
            case FIND_ITEM:
                if (targetItem == Items.POTION && currentBottleSlot < 3) {
                    if (!(getItem(currentBottleSlot) instanceof AirItem)) {
                        currentBottleSlot++;
                        return;
                    }
                }

                sourceSlot = findItemInBrewingStand(targetItem);
                if (sourceSlot == -1) {
                    ChatUtil.addText("§cНет " + getItemName(targetItem) + "!");
                    toggle();
                    return;
                }

                if (targetItem == Items.POTION) {
                    targetSlot = currentBottleSlot;
                }

                placeState = PlaceState.WAIT_BEFORE_PICKUP;
                humanizeTimer.reset();
                break;

            case WAIT_BEFORE_PICKUP:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    placeState = PlaceState.PICKUP_ITEM;
                    timer.reset();
                }
                break;

            case PICKUP_ITEM:
                if (mc.currentScreen != null) {
                    humanizedClick(mc.player.openContainer.windowId, sourceSlot, 0, ClickType.PICKUP);
                    placeState = PlaceState.WAIT_AFTER_PICKUP;
                    humanizeTimer.reset();
                }
                break;

            case WAIT_AFTER_PICKUP:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    placeState = PlaceState.PLACE_ITEM;
                    timer.reset();
                }
                break;

            case PLACE_ITEM:
                if (mc.currentScreen != null) {
                    humanizedClick(mc.player.openContainer.windowId, targetSlot, 1, ClickType.PICKUP);
                    placeState = PlaceState.WAIT_AFTER_PLACE;
                    humanizeTimer.reset();
                }
                break;

            case WAIT_AFTER_PLACE:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    placeState = PlaceState.PUT_BACK;
                    timer.reset();
                }
                break;

            case PUT_BACK:
                if (mc.currentScreen != null) {
                    humanizedClick(mc.player.openContainer.windowId, sourceSlot, 0, ClickType.PICKUP);
                    placeState = PlaceState.WAIT_AFTER_PUT_BACK;
                    humanizeTimer.reset();
                }
                break;

            case WAIT_AFTER_PUT_BACK:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    if (targetItem == Items.POTION) {
                        currentBottleSlot++;
                        if (currentBottleSlot >= 3) {
                            placeState = PlaceState.IDLE;
                        } else {
                            placeState = PlaceState.FIND_ITEM;
                        }
                    } else {
                        ingredientPlaced = true;
                        lastPlacedItem = targetItem;
                        placeState = PlaceState.IDLE;
                    }
                    timer.reset();
                }
                break;
        }
    }

    private void handleCollectingPhase() {
        if (!collectTimer.finished(5000)) {
            if (!needToOpenBrewingStand && !(mc.player.openContainer instanceof BrewingStandContainer)) {
                needToOpenBrewingStand = true;
            }
            return;
        }

        if (!timer.finished(getRandomDelay())) return;

        boolean hasItems = false;
        for (int i = 0; i < 3; i++) {
            if (!(getItem(i) instanceof AirItem)) {
                mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
                timer.reset();
                hasItems = true;
                return;
            }
        }

        if (!hasItems) {
            currentBrewingStand++;

            if (currentBrewingStand >= brewingStands.size()) {
                currentPhase = BrewingPhase.STORING;
                mc.player.closeScreen();

                targetChest = findNearestChest();
                if (targetChest == null) {
                    currentPhase = BrewingPhase.FINISHED;
                } else {
                    needToOpenChest = true;
                }
            } else {
                mc.player.closeScreen();
                targetBrewingStand = brewingStands.get(currentBrewingStand);
                needToOpenBrewingStand = true;
            }
        }
    }

    private void handleStoringPhase() {
        if (!timer.finished(getRandomDelay())) return;

        ChestContainer chestContainer = (ChestContainer) mc.player.openContainer;
        int inventoryRows = chestContainer.getLowerChestInventory().getSizeInventory() / 9;
        int chestSlots = inventoryRows * 9;
        int totalSlots = chestContainer.inventorySlots.size();

        boolean hasInvisibilityPotions = false;

        for (int i = chestSlots; i < totalSlots; i++) {
            Slot slot = chestContainer.getSlot(i);
            ItemStack stack = slot.getStack();

            if (slot.getHasStack() && stack.getItem() == Items.POTION) {
                Potion potion = PotionUtils.getPotionFromItem(stack);
                ResourceLocation potionKey = Registry.POTION.getKey(potion);

                if (potionKey != null && potionKey.getPath().contains("invisibility")) {
                    mc.playerController.windowClick(chestContainer.windowId, slot.slotNumber, 0, ClickType.QUICK_MOVE, mc.player);
                    timer.reset();
                    hasInvisibilityPotions = true;
                    return;
                }
            }
        }

        if (!hasInvisibilityPotions) {
            mc.player.closeScreen();
            currentPhase = BrewingPhase.FINISHED;
        }
    }

    private Item getItem(int slotId) {
        if (slotId < 0 || slotId >= mc.player.openContainer.inventorySlots.size()) {
            return Items.AIR;
        }
        return mc.player.openContainer.inventorySlots.get(slotId).getStack().getItem();
    }

    private int findItemInBrewingStand(Item item) {
        for (int i = 5; i < 41; i++) {
            if (getItem(i) == item) {
                return i;
            }
        }
        return -1;
    }

    private String getItemName(Item item) {
        if (item == Items.POTION) return "бутылок с водой";
        if (item == Items.NETHER_WART) return "нароста";
        if (item == Items.GOLDEN_CARROT) return "золотой моркови";
        if (item == Items.FERMENTED_SPIDER_EYE) return "паучьего глаза";
        if (item == Items.REDSTONE) return "редстоуна";
        return "предмета";
    }

    private Vector2f calculateRotation(Vector3d targetPos) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d toTarget = targetPos.subtract(playerEyes).normalize();

        double horizontalLength = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        float targetYaw = (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(toTarget.y, horizontalLength));

        targetPitch = MathHelper.clamp(targetPitch, -90.0F, 90.0F);

        return new Vector2f(targetYaw, targetPitch);
    }

    private float smoothRotation(float currentAngle, float targetAngle, float smoothFactor) {
        float angleDifference = MathHelper.wrapDegrees(targetAngle - currentAngle);
        float adjustmentSpeed = Math.abs(angleDifference / smoothFactor);
        float angleAdjustment = adjustmentSpeed * Math.signum(angleDifference);

        if (Math.abs(angleAdjustment) > Math.abs(angleDifference)) {
            angleAdjustment = angleDifference;
        }

        float sensAdjustedAngle = SensUtil.getSens(angleAdjustment);

        return currentAngle + sensAdjustedAngle;
    }

    private long getRandomDelay() {
        return 150 + random.nextInt(151);
    }

    private long getRandomHumanPause() {
        int baseMin = 80;
        int baseMax = 180;
        int extraVariation = random.nextInt(100);
        return baseMin + random.nextInt(baseMax - baseMin) + extraVariation;
    }

    private void humanizedClick(int windowId, int slotId, int mouseButton, ClickType clickType) {
        try {
            long microDelay = 10 + random.nextInt(30);
            Thread.sleep(microDelay);
        } catch (InterruptedException ignored) {
        }
        mc.playerController.windowClick(windowId, slotId, mouseButton, clickType, mc.player);
    }
}