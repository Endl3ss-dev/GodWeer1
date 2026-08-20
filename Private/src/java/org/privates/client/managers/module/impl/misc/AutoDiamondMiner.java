package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InventoryUtility;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.client.utils.rotation.SensUtil;
import org.privates.lib.util.time.StopWatch;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoDiamondMiner", category = Category.MISC)
public class AutoDiamondMiner extends Module {

    public static AutoDiamondMiner getInstance() {
        return Instance.get(AutoDiamondMiner.class);
    }

    private final StopWatch timer = new StopWatch();
    private final StopWatch delayTimer = new StopWatch();
    private final StopWatch walkTimer = new StopWatch();
    private final Random random = new Random();

    private enum MiningState {
        SEARCH_DIAMONDS,
        WALK_TO_DIAMOND,
        MINING,
        REPAIR_PICKAXE,
        FINISHED
    }

    private enum RepairState {
        SEARCH_XP,
        BUY_XP,
        SWAP_ITEMS,
        ROTATE_DOWN,
        REPAIRING,
        FINISHED
    }

    private enum BuyState {
        SEND_SEARCH,
        WAIT_OPEN,
        BUY_ITEM,
        CLOSE_SCREEN
    }

    private MiningState miningState = MiningState.SEARCH_DIAMONDS;
    private RepairState repairState = RepairState.SEARCH_XP;
    private BuyState buyState = BuyState.SEND_SEARCH;

    private final BlockPos minCorner = new BlockPos(-95, 77, 17);
    private final BlockPos maxCorner = new BlockPos(-75, 87, 37);

    private final List<BlockPos> diamondBlocks = new ArrayList<>();
    private BlockPos currentTarget = null;
    private boolean isRotating = false;
    private boolean rotationComplete = false;
    private float targetYaw = 0;
    private float targetPitch = 0;
    private int pickaxeSlot = -1;
    private int xpSlot = -1;

    @Override
    public void onEnable() {
        super.onEnable();
        miningState = MiningState.SEARCH_DIAMONDS;
        repairState = RepairState.SEARCH_XP;
        buyState = BuyState.SEND_SEARCH;
        diamondBlocks.clear();
        currentTarget = null;
        isRotating = false;
        rotationComplete = false;
        pickaxeSlot = -1;
        xpSlot = -1;
        timer.reset();
        delayTimer.reset();
        walkTimer.reset();
        ChatUtil.addText("Модуль AutoDiamondMiner запущен");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        stopAllMovement();
        diamondBlocks.clear();
        currentTarget = null;
        isRotating = false;
        rotationComplete = false;
        ChatUtil.addText("Модуль AutoDiamondMiner остановлен");
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent e) {
        if (diamondBlocks.isEmpty()) return;

        for (BlockPos pos : diamondBlocks) {
            AxisAlignedBB box = new AxisAlignedBB(pos);
            RenderUtil3D.drawBoundingBox(e.getMatrix(), box, new Color(0, 255, 255).getRGB(), 1.5f, true, true);
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        handleRotation();

        switch (miningState) {
            case SEARCH_DIAMONDS:
                handleSearchDiamonds();
                break;
            case WALK_TO_DIAMOND:
                handleWalkToDiamond();
                break;
            case MINING:
                handleMining();
                break;
            case REPAIR_PICKAXE:
                handleRepairPickaxe();
                break;
            case FINISHED:
                ChatUtil.addText("Работа завершена все алмазы выкопаны и кирка починена");
                toggle();
                break;
        }
    }

    private void handleSearchDiamonds() {
        diamondBlocks.clear();
        ChatUtil.addText("Начинаем поиск алмазов в кубе");

        int minX = Math.min(minCorner.getX(), maxCorner.getX());
        int maxX = Math.max(minCorner.getX(), maxCorner.getX());
        int minY = Math.min(minCorner.getY(), maxCorner.getY());
        int maxY = Math.max(minCorner.getY(), maxCorner.getY());
        int minZ = Math.min(minCorner.getZ(), maxCorner.getZ());
        int maxZ = Math.max(minCorner.getZ(), maxCorner.getZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.getBlock() == Blocks.DIAMOND_ORE) {
                        diamondBlocks.add(pos);
                    }
                }
            }
        }

        if (diamondBlocks.isEmpty()) {
            ChatUtil.addText("Алмазы не найдены переходим к завершению");
            miningState = MiningState.FINISHED;
            return;
        }

        diamondBlocks.sort(Comparator.comparingInt(BlockPos::getY).reversed());
        ChatUtil.addText("Найдено алмазов: " + diamondBlocks.size() + " сортируем сверху вниз");

        pickaxeSlot = findNetheritePickaxe();
        if (pickaxeSlot == -1) {
            ChatUtil.addText("Незеритовая кирка не найдена отключаем модуль");
            toggle();
            return;
        }

        ChatUtil.addText("Кирка найдена в слоте " + pickaxeSlot);
        currentTarget = diamondBlocks.get(0);
        ChatUtil.addText("Цель установлена на блок " + currentTarget.toString());
        miningState = MiningState.WALK_TO_DIAMOND;
        timer.reset();
    }

    private void handleWalkToDiamond() {
        if (currentTarget == null) {
            ChatUtil.addText("Цель потеряна возвращаемся к поиску");
            miningState = MiningState.SEARCH_DIAMONDS;
            return;
        }

        double distance = mc.player.getDistanceSq(Vector3d.copyCentered(currentTarget));

        if (distance < 16.0) {
            stopAllMovement();
            ChatUtil.addText("Достигли алмаза подготавливаем кирку");
            equipPickaxe();
            ChatUtil.addText("Переходим к копанию");
            miningState = MiningState.MINING;
            isRotating = false;
            rotationComplete = false;
            timer.reset();
            return;
        }

        if (!walkTimer.finished(50)) return;

        Vector3d targetPos = Vector3d.copyCentered(currentTarget);
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d direction = targetPos.subtract(playerPos).normalize();

        targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        targetPitch = 0;

        mc.player.rotationYaw = smoothRotation(mc.player.rotationYaw, targetYaw, 15f);
        mc.player.rotationPitch = smoothRotation(mc.player.rotationPitch, targetPitch, 15f);

        mc.gameSettings.keyBindForward.setPressed(true);
        mc.gameSettings.keyBindSprint.setPressed(true);

        walkTimer.reset();
    }

    private void handleMining() {
        if (currentTarget == null) {
            ChatUtil.addText("Цель потеряна во время копания");
            miningState = MiningState.SEARCH_DIAMONDS;
            return;
        }

        BlockState state = mc.world.getBlockState(currentTarget);
        if (state.getBlock() != Blocks.DIAMOND_ORE) {
            ChatUtil.addText("Блок успешно выкопан");
            diamondBlocks.remove(currentTarget);

            if (diamondBlocks.isEmpty()) {
                ChatUtil.addText("Все алмазы выкопаны переходим к починке кирки");
                miningState = MiningState.REPAIR_PICKAXE;
                repairState = RepairState.SEARCH_XP;
                buyState = BuyState.SEND_SEARCH;
                currentTarget = null;
                timer.reset();
                return;
            }

            currentTarget = diamondBlocks.get(0);
            ChatUtil.addText("Следующая цель " + currentTarget.toString());
            miningState = MiningState.WALK_TO_DIAMOND;
            isRotating = false;
            rotationComplete = false;
            timer.reset();
            return;
        }

        if (!rotationComplete && !isRotating) {
            startBlockRotation(currentTarget);
            return;
        }

        if (isRotating) {
            return;
        }

        if (!timer.finished(50)) return;

        mc.playerController.onPlayerDamageBlock(currentTarget, Direction.UP);
        mc.player.swingArm(Hand.MAIN_HAND);

        timer.reset();
    }

    private void handleRepairPickaxe() {
        switch (repairState) {
            case SEARCH_XP:
                ChatUtil.addText("Ищем опыт в инвентаре");
                xpSlot = findExperienceBottle();
                if (xpSlot == -1) {
                    ChatUtil.addText("Опыт не найден начинаем покупку");
                    repairState = RepairState.BUY_XP;
                    buyState = BuyState.SEND_SEARCH;
                    timer.reset();
                } else {
                    ChatUtil.addText("Опыт найден в слоте " + xpSlot);
                    repairState = RepairState.SWAP_ITEMS;
                    timer.reset();
                }
                break;

            case BUY_XP:
                handleBuyXP();
                break;

            case SWAP_ITEMS:
                handleSwapItems();
                break;

            case ROTATE_DOWN:
                handleRotateDown();
                break;

            case REPAIRING:
                handleRepairing();
                break;

            case FINISHED:
                ChatUtil.addText("Кирка успешно починена");
                miningState = MiningState.FINISHED;
                break;
        }
    }

    private void handleBuyXP() {
        switch (buyState) {
            case SEND_SEARCH:
                if (!timer.finished(500)) return;
                ChatUtil.addText("Отправляем команду поиска опыта");
                mc.player.sendChatMessage("/ah search опыт");
                buyState = BuyState.WAIT_OPEN;
                delayTimer.reset();
                timer.reset();
                break;

            case WAIT_OPEN:
                if (delayTimer.finished(2000)) {
                    if (mc.currentScreen instanceof ChestScreen) {
                        ChatUtil.addText("Аукцион открыт");
                        buyState = BuyState.BUY_ITEM;
                        timer.reset();
                    } else {
                        ChatUtil.addText("Аукцион не открылся повторяем команду");
                        mc.player.sendChatMessage("/ah search опыт");
                        delayTimer.reset();
                    }
                }
                break;

            case BUY_ITEM:
                if (!timer.finished(300)) return;

                if (mc.currentScreen instanceof ChestScreen) {
                    ChestContainer container = (ChestContainer) mc.player.openContainer;
                    ItemStack cheapest = null;
                    int cheapestPrice = Integer.MAX_VALUE;
                    int cheapestSlot = -1;

                    ChatUtil.addText("Ищем самый дешевый опыт");

                    for (Slot slot : container.inventorySlots) {
                        if (slot.slotNumber > 44 || !slot.getHasStack()) continue;

                        ItemStack stack = slot.getStack();
                        if (stack.getItem() != Items.EXPERIENCE_BOTTLE || stack.getCount() != 64) continue;

                        int price = getPrice(stack);
                        if (price != -1 && price < cheapestPrice) {
                            cheapestPrice = price;
                            cheapest = stack;
                            cheapestSlot = slot.slotNumber;
                        }
                    }

                    if (cheapest != null) {
                        ChatUtil.addText("Покупаем опыт за " + cheapestPrice + " монет");
                        InventoryUtility.clickSlotId(cheapestSlot, 0, ClickType.QUICK_MOVE, false);
                        buyState = BuyState.CLOSE_SCREEN;
                        timer.reset();
                    } else {
                        ChatUtil.addText("Подходящий опыт не найден закрываем аукцион");
                        buyState = BuyState.CLOSE_SCREEN;
                        timer.reset();
                    }
                } else {
                    ChatUtil.addText("Аукцион закрылся повторяем поиск");
                    buyState = BuyState.SEND_SEARCH;
                    timer.reset();
                }
                break;

            case CLOSE_SCREEN:
                if (!timer.finished(500)) return;
                ChatUtil.addText("Закрываем аукцион");
                mc.player.closeScreen();
                repairState = RepairState.SEARCH_XP;
                timer.reset();
                break;
        }
    }

    private void handleSwapItems() {
        if (!timer.finished(200)) return;

        xpSlot = findExperienceBottle();
        if (xpSlot == -1) {
            ChatUtil.addText("Опыт не найден после покупки ошибка");
            toggle();
            return;
        }

        pickaxeSlot = findNetheritePickaxe();
        if (pickaxeSlot == -1) {
            ChatUtil.addText("Кирка не найдена ошибка");
            toggle();
            return;
        }

        ChatUtil.addText("Перемещаем кирку в левую руку");
        swapToOffhand(pickaxeSlot);

        ChatUtil.addText("Берем опыт в основную руку слот " + xpSlot);
        mc.player.inventory.currentItem = xpSlot;
        mc.player.connection.sendPacket(new CHeldItemChangePacket(xpSlot));

        repairState = RepairState.ROTATE_DOWN;
        isRotating = false;
        rotationComplete = false;
        timer.reset();
    }

    private void handleRotateDown() {
        if (!isRotating) {
            ChatUtil.addText("Поворачиваем взгляд вниз");
            targetYaw = mc.player.rotationYaw;
            targetPitch = 90f;
            isRotating = true;
        }

        if (!rotationComplete) {
            return;
        }

        ChatUtil.addText("Взгляд повернут вниз начинаем чинить");
        repairState = RepairState.REPAIRING;
        isRotating = false;
        rotationComplete = false;
        timer.reset();
    }

    private void handleRepairing() {
        if (!timer.finished(500)) return;

        ItemStack offhandItem = mc.player.getHeldItemOffhand();
        if (offhandItem.isEmpty() || offhandItem.getItem() != Items.NETHERITE_PICKAXE) {
            ChatUtil.addText("Кирка не в левой руке ошибка");
            repairState = RepairState.FINISHED;
            return;
        }

        if (!offhandItem.isDamaged()) {
            ChatUtil.addText("Кирка полностью починена");
            repairState = RepairState.FINISHED;
            return;
        }

        ChatUtil.addText("Используем опыт для починки прочность " + (offhandItem.getMaxDamage() - offhandItem.getDamage()));
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        mc.player.swingArm(Hand.MAIN_HAND);

        timer.reset();
    }

    private void handleRotation() {
        if (!isRotating || rotationComplete) return;

        if (!timer.finished(50)) return;

        float newYaw = smoothRotation(mc.player.rotationYaw, targetYaw, 10f);
        float newPitch = MathHelper.clamp(smoothRotation(mc.player.rotationPitch, targetPitch, 10f), -90f, 90f);

        mc.player.rotationYaw = newYaw;
        mc.player.rotationYawHead = newYaw;
        mc.player.renderYawOffset = newYaw;
        mc.player.prevRotationYaw = newYaw;
        mc.player.prevRotationYawHead = newYaw;

        mc.player.rotationPitch = newPitch;
        mc.player.prevRotationPitch = newPitch;

        float yawDiff = Math.abs(MathHelper.wrapDegrees(newYaw - targetYaw));
        float pitchDiff = Math.abs(newPitch - targetPitch);

        if (yawDiff < 1.0f && pitchDiff < 1.0f) {
            rotationComplete = true;
            isRotating = false;
        }

        timer.reset();
    }

    private void startBlockRotation(BlockPos pos) {
        ChatUtil.addText("Поворачиваемся к блоку");
        Vector3d targetPos = Vector3d.copyCentered(pos);
        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d toTarget = targetPos.subtract(playerEyes);

        double horizontalLength = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        targetYaw = (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90.0f;
        targetPitch = (float) -Math.toDegrees(Math.atan2(toTarget.y, horizontalLength));

        targetPitch = MathHelper.clamp(targetPitch, -90f, 90f);

        isRotating = true;
        rotationComplete = false;
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

    private int findNetheritePickaxe() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.NETHERITE_PICKAXE) {
                return i;
            }
        }
        return -1;
    }

    private int findExperienceBottle() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.EXPERIENCE_BOTTLE) {
                return i;
            }
        }
        return -1;
    }

    private void equipPickaxe() {
        if (pickaxeSlot >= 0 && pickaxeSlot < 9) {
            ChatUtil.addText("Экипируем кирку из хотбара");
            mc.player.inventory.currentItem = pickaxeSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(pickaxeSlot));
        } else if (pickaxeSlot >= 9 && pickaxeSlot < 36) {
            int emptySlot = findEmptyHotbarSlot();
            if (emptySlot != -1) {
                ChatUtil.addText("Перемещаем кирку из инвентаря в хотбар");
                swapInventoryItems(pickaxeSlot, emptySlot);
                mc.player.inventory.currentItem = emptySlot;
                mc.player.connection.sendPacket(new CHeldItemChangePacket(emptySlot));
                pickaxeSlot = emptySlot;
            }
        }
    }

    private int findEmptyHotbarSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private void swapInventoryItems(int fromSlot, int toSlot) {
        int windowId = mc.player.openContainer.windowId;
        mc.playerController.windowClick(windowId, fromSlot < 9 ? fromSlot + 36 : fromSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(windowId, toSlot < 9 ? toSlot + 36 : toSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(windowId, fromSlot < 9 ? fromSlot + 36 : fromSlot, 0, ClickType.PICKUP, mc.player);
    }

    private void swapToOffhand(int slot) {
        int windowId = mc.player.openContainer.windowId;
        mc.playerController.windowClick(windowId, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(windowId, 45, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(windowId, slot < 9 ? slot + 36 : slot, 0, ClickType.PICKUP, mc.player);
    }

    private void stopAllMovement() {
        mc.gameSettings.keyBindForward.setPressed(false);
        mc.gameSettings.keyBindBack.setPressed(false);
        mc.gameSettings.keyBindLeft.setPressed(false);
        mc.gameSettings.keyBindRight.setPressed(false);
        mc.gameSettings.keyBindSprint.setPressed(false);
    }

    private final Pattern funTimePricePattern = Pattern.compile("\\$(\\d{1,3}(?:,? ?\\d{3})*)");

    private int getPrice(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10)) {
            CompoundNBT display = tag.getCompound("display");
            if (display.contains("Lore", 9)) {
                ListNBT lore = display.getList("Lore", 8);
                for (int j = 0; j < lore.size(); ++j) {
                    String loreLine = TextFormatting.getTextWithoutFormattingCodes(lore.getString(j));
                    if (loreLine == null) continue;

                    Matcher matcher = funTimePricePattern.matcher(loreLine);
                    if (matcher.find()) {
                        try {
                            String priceString = matcher.group(1).replaceAll(",", "").replaceAll(" ", "");
                            if (!priceString.isEmpty()) {
                                return Integer.parseInt(priceString);
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }
        return -1;
    }
}