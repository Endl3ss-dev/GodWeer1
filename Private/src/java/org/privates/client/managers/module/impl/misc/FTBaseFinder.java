package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.lib.util.time.StopWatch;

import java.awt.*;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "FTBaseFinder", category = Category.MISC, description = "Автоматический поиск баз на FunTime")
public class FTBaseFinder extends Module {

    public static FTBaseFinder getInstance() {
        return Instance.get(FTBaseFinder.class);
    }

    private final ModeSetting direction = new ModeSetting(this, "Направление", "North", "East", "South", "West");
    private final SliderSetting targetY = new SliderSetting(this, "Целевая высота", 15, -60, 60, 1);
    private final ModeSetting oreType = new ModeSetting(this, "Тип руды", "Diamond Ore", "Emerald Ore");
    private final SliderSetting checkDistance = new SliderSetting(this, "Дистанция проверки", 25, 5, 100, 1);

    private boolean diggingDown = true;
    private BlockPos blockToBreak = null;
    private BlockPos pendingOrePos = null;
    private BlockPos lastCheckPos = null;
    private final long lastDamageTime = 0;
    private final StopWatch mineTimer = new StopWatch();
    private final StopWatch breakTimer = new StopWatch();

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player == null) {
            toggle();
            return;
        }
        if (!hasRequiredItems()) {
            ChatUtil.addText("§c[Ошибка] §fНет кирки на Шелковое касание или руды в хотбаре!");
            onDisable();
            return;
        }

        diggingDown = true;
        blockToBreak = null;
        pendingOrePos = null;
        lastCheckPos = null;
        mineTimer.reset();
        breakTimer.reset();
        ChatUtil.addText("§a[Старт] §fСпускаюсь на высоту " + targetY.getValue().intValue());
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.gameSettings != null) {
            mc.gameSettings.keyBindForward.setPressed(false);
            mc.gameSettings.keyBindSneak.setPressed(false);
            mc.gameSettings.keyBindJump.setPressed(false);
            mc.gameSettings.keyBindAttack.setPressed(false);
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.hurtTime > 0) {
            boolean isEntityDamage = mc.world.getEntitiesWithinAABB(
                    net.minecraft.entity.monster.MonsterEntity.class,
                    mc.player.getBoundingBox().grow(5),
                    e -> true
            ).size() > 0;

            if (isEntityDamage && System.currentTimeMillis() - lastDamageTime > 5000) {
                ChatUtil.addText("§c§l[ВНИМАНИЕ] §fВас атакует моб! Остановка.");
                toggle();
                return;
            }
        }

        if (pendingOrePos != null && mc.world.isAirBlock(pendingOrePos)) {
            if (!tryPlaceOre(pendingOrePos)) {
                ChatUtil.addText("§6§l[БАЗА!] §fПриват на: §a" + pendingOrePos.getX() + " " + pendingOrePos.getY() + " " + pendingOrePos.getZ());
                toggle();
                return;
            } else {
                mineBackOre(pendingOrePos);
                pendingOrePos = null;
            }
        }

        if (blockToBreak != null) {
            if (mc.world.isAirBlock(blockToBreak)) {
                blockToBreak = null;
                mc.gameSettings.keyBindAttack.setPressed(false);
            } else {
                processBlockMining(blockToBreak);
                return;
            }
        }

        if (diggingDown) {
            handleDescending();
        } else {
            handleTunneling();
        }
    }

    private void handleDescending() {
        BlockPos playerPos = new BlockPos(mc.player.getPositionVec());
        BlockPos underPlayer = playerPos.down();

        if (isLiquidBelow(underPlayer)) {
            ChatUtil.addText("§c[!] §fПод ногами обнаружена жидкость. Запуск сканирования 10x10...");

            BlockPos safePoint = findBestSafePoint(playerPos);

            if (safePoint != null) {
                double distance = Math.sqrt(playerPos.distanceSq(safePoint));
                ChatUtil.addText(String.format("§a[Навигатор] §fНайдена безопасная зона в %.1f м. Обхожу...", distance));
                moveTowards(safePoint);
            } else {
                ChatUtil.addText("§4[Крит] §fВ радиусе 10 блоков нет безопасных мест. Смена вектора...");
                direction.set(Direction.Plane.HORIZONTAL.random(mc.world.rand).getString());
            }
            return;
        }

        if (mc.player.getPosY() <= targetY.getValue()) {
            diggingDown = false;
            ChatUtil.addText("§6[Инфо] §fЦелевая высота " + targetY.getValue().intValue() + " достигнута. Начинаю копку туннеля.");
            return;
        }

        if (!mc.world.isAirBlock(underPlayer)) {
            blockToBreak = underPlayer;
        }
    }

    private BlockPos findBestSafePoint(BlockPos startPos) {
        BlockPos bestPos = null;
        double minDistance = Double.MAX_VALUE;

        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                BlockPos checkPos = startPos.add(x, 0, z);

                if (isValidSafeSpot(checkPos)) {
                    double dist = startPos.distanceSq(checkPos);
                    if (dist < minDistance) {
                        minDistance = dist;
                        bestPos = checkPos;
                    }
                }
            }
        }
        return bestPos;
    }

    private boolean isValidSafeSpot(BlockPos pos) {
        boolean isSpaceClear = mc.world.getBlockState(pos).isAir() &&
                mc.world.getBlockState(pos.up()).isAir();

        if (!isSpaceClear) return false;

        for (int i = 0; i <= 5; i++) {
            if (mc.world.getBlockState(pos.down(i)).getMaterial().isLiquid()) {
                return false;
            }
        }

        return !mc.world.getBlockState(pos.down()).isAir();
    }

    private boolean isLiquidBelow(BlockPos startPos) {
        for (int i = 0; i < 5; i++) {
            BlockState state = mc.world.getBlockState(startPos.down(i));
            if (state.getMaterial().isLiquid()) return true;
        }
        return false;
    }

    private void moveTowards(BlockPos pos) {
        if (mc.player == null) return;

        rotateHeadToBlock(pos);

        double dx = pos.getX() + 0.5 - mc.player.getPosX();
        double dz = pos.getZ() + 0.5 - mc.player.getPosZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0.3) {
            mc.gameSettings.keyBindForward.setPressed(true);
            mc.gameSettings.keyBindJump.setPressed(mc.player.collidedHorizontally);
        } else {
            mc.gameSettings.keyBindForward.setPressed(false);
            mc.gameSettings.keyBindJump.setPressed(false);
        }
    }

    private void handleTunneling() {
        Direction dir = Direction.byName(direction.getValue().toLowerCase());
        if (dir == null) dir = Direction.NORTH;

        BlockPos playerPos = new BlockPos(mc.player.getPositionVec());
        BlockPos forwardLower = playerPos.offset(dir);
        BlockPos forwardUpper = forwardLower.up();
        BlockPos gapPos = forwardLower.down();

        if (mc.world.getBlockState(forwardLower).getMaterial().isLiquid() ||
                mc.world.getBlockState(forwardUpper).getMaterial().isLiquid()) {
            direction.set(dir.rotateY().getString());
            return;
        }

        if (mc.world.getBlockState(gapPos).isAir() || mc.world.getBlockState(gapPos).getMaterial().isLiquid()) {
            mc.gameSettings.keyBindForward.setPressed(false);
            placeBridge(gapPos);
            return;
        }

        if (!isBlockPassable(forwardUpper) || !isBlockPassable(forwardLower)) {
            mc.gameSettings.keyBindForward.setPressed(false);
            blockToBreak = !isBlockPassable(forwardUpper) ? forwardUpper : forwardLower;
        } else {
            mc.gameSettings.keyBindSneak.setPressed(false);
            if (blockToBreak == null && pendingOrePos == null) {
                moveForward();

                if (lastCheckPos == null) lastCheckPos = playerPos;

                double dist = Math.sqrt(playerPos.distanceSq(lastCheckPos));
                if (dist >= checkDistance.getValue()) {
                    mc.gameSettings.keyBindForward.setPressed(false);
                    checkBaseWithOre(dir);
                    lastCheckPos = playerPos;
                }
            }
        }
    }

    private void processBlockMining(BlockPos pos) {
        if (mc.world == null || mc.player == null || mc.playerController == null) return;

        if (checkForLiquids(pos)) {
            ChatUtil.addText("§c[Внимание] §fОбнаружена лава/вода. Остановка.");
            toggle();
            return;
        }

        selectBestTool(mc.world.getBlockState(pos));
        rotateHeadToBlock(pos);

        if (mineTimer.finished(50)) {
            mc.gameSettings.keyBindAttack.setPressed(true);
            mineTimer.reset();
        }
    }

    private int findFillerBlock() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            String name = stack.getItem().toString();
            if (name.contains("stone") || name.contains("dirt") || name.contains("cobble") ||
                    name.contains("netherrack") || name.contains("deepslate") || name.contains("andesite")) {
                return i;
            }
        }
        return -1;
    }

    private void placeBridge(BlockPos targetPos) {
        int fillerSlot = findFillerBlock();
        if (fillerSlot == -1) {
            ChatUtil.addText("§c[Ошибка] §fНет блоков для застройки!");
            toggle();
            return;
        }

        mc.gameSettings.keyBindForward.setPressed(false);
        mc.gameSettings.keyBindSneak.setPressed(true);
        int oldSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = fillerSlot;

        rotateHeadToBlock(targetPos);

        if (breakTimer.finished(100)) {
            BlockRayTraceResult hit = new BlockRayTraceResult(
                    new Vector3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5),
                    Direction.UP,
                    targetPos,
                    false
            );
            mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, hit);
            mc.player.swingArm(Hand.MAIN_HAND);
            breakTimer.reset();
        }

        mc.player.inventory.currentItem = oldSlot;
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (mc.world == null || mc.player == null) return;

        MatrixStack matrix = event.getMatrix();

        if (blockToBreak != null) {
            RenderUtil3D.drawBoundingBox(matrix, mc.world.getBlockState(blockToBreak).getShape(mc.world, blockToBreak).getBoundingBox().offset(blockToBreak), Color.RED.getRGB(), 2.0f, false, false);
        }

        if (!diggingDown) {
            Direction dir = Direction.byName(direction.getValue().toLowerCase());
            if (dir != null) {
                BlockPos p = mc.player.getPosition();
                BlockPos f1 = p.offset(dir);
                BlockPos f2 = p.up().offset(dir);

                if (!mc.world.isAirBlock(f1) && !f1.equals(blockToBreak)) {
                    RenderUtil3D.drawBoundingBox(matrix, mc.world.getBlockState(f1).getShape(mc.world, f1).getBoundingBox().offset(f1), Color.GREEN.getRGB(), 1.0f, false, false);
                }
                if (!mc.world.isAirBlock(f2) && !f2.equals(blockToBreak)) {
                    RenderUtil3D.drawBoundingBox(matrix, mc.world.getBlockState(f2).getShape(mc.world, f2).getBoundingBox().offset(f2), Color.GREEN.getRGB(), 1.0f, false, false);
                }
            }
        }
    }

    private void checkBaseWithOre(Direction dir) {
        Direction sideDir = dir.rotateY();
        BlockPos wallPos = mc.player.getPosition().offset(sideDir).up();

        if (!mc.world.isAirBlock(wallPos)) {
            blockToBreak = wallPos;
            pendingOrePos = wallPos;
        }
    }

    private void selectBestTool(BlockState blockState) {
        if (mc.player == null) return;
        int bestSlot = -1;
        float bestSpeed = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            float speed = stack.getDestroySpeed(blockState);

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }
        if (bestSlot != -1) {
            mc.player.inventory.currentItem = bestSlot;
        }
    }

    private void rotateHeadToBlock(BlockPos pos) {
        if (mc.player == null) return;
        Vector3d target = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        double dx = target.x - eyePos.x;
        double dy = target.y - eyePos.y;
        double dz = target.z - eyePos.z;
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));

        pitch = net.minecraft.util.math.MathHelper.clamp(pitch, -90.0f, 90.0f);

        RotationComponent.update(new Rotation(yaw, pitch), 180, 90, 0, 5);
    }

    private void moveForward() {
        if (mc.gameSettings == null) return;
        mc.gameSettings.keyBindForward.setPressed(true);
    }

    private boolean isBlockPassable(BlockPos pos) {
        if (mc.world == null) return false;
        return !mc.world.getBlockState(pos).isSolid();
    }

    private boolean checkForLiquids(BlockPos pos) {
        if (mc.world == null) return false;
        return mc.world.getBlockState(pos).getMaterial().isLiquid();
    }

    private boolean tryPlaceOre(BlockPos pos) {
        if (mc.player == null || mc.playerController == null || mc.world == null) return false;
        int oreSlot = findOreSlot();
        if (oreSlot == -1) return false;

        int oldSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = oreSlot;

        rotateHeadToBlock(pos);

        if (breakTimer.finished(100)) {
            BlockRayTraceResult hitResult = new BlockRayTraceResult(
                    new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
                    Direction.UP,
                    pos,
                    false
            );
            mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, hitResult);
            mc.player.swingArm(Hand.MAIN_HAND);
            breakTimer.reset();
        }

        boolean placed = !mc.world.getBlockState(pos).isAir();
        mc.player.inventory.currentItem = oldSlot;

        return placed;
    }

    private void mineBackOre(BlockPos pos) {
        if (mc.player == null || mc.playerController == null) return;
        int pickaxeSlot = findSilkTouchPickaxe();
        if (pickaxeSlot == -1) return;

        int oldSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = pickaxeSlot;

        rotateHeadToBlock(pos);

        mc.gameSettings.keyBindAttack.setPressed(true);

        mc.player.inventory.currentItem = oldSlot;
    }

    private boolean hasRequiredItems() {
        if (mc.player == null) return false;
        return findSilkTouchPickaxe() != -1 && findOreSlot() != -1;
    }

    private int findSilkTouchPickaxe() {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof PickaxeItem) {
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int findOreSlot() {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (oreType.is("Diamond Ore") && s.getItem() == Items.DIAMOND_ORE) return i;
            if (oreType.is("Emerald Ore") && s.getItem() == Items.EMERALD_ORE) return i;
        }
        return -1;
    }
}