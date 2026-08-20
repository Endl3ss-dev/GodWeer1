package org.privates.client.managers.module.impl.movement;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

@Category2
@ModuleInfo(name = "Spider", category = Category.MOVEMENT, description = "Поднимает вас в верх рядом с блоками")
public class Spider extends Module {

    public static Spider getInstance() {
        return Instance.get(Spider.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Обычный",
            "Jump Boost", "Ladder Climb", "Vine Climb", "Scaffolding", "Slime Bounce", "Water Elevator", "Cobweb Push",
            "СпукиТайм", "Matrix", "Vulcan", "AAC", "NCP", "GrimLatest");
    private final SliderSetting motionY = new SliderSetting(this, "Скорость Y", 0.2f, 0.1f, 1.0f, 0.05f);

    private final SliderSetting jumpBoostMultiplier = new SliderSetting(this, "Множитель Jump Boost", 1.5f, 1.0f, 3.0f, 0.1f)
            .setVisible(() -> mode.is("Jump Boost"));

    private final SliderSetting ladderSpeed = new SliderSetting(this, "Скорость лестницы", 0.3f, 0.1f, 0.5f, 0.05f)
            .setVisible(() -> mode.is("Ladder Climb"));
    private final BooleanSetting autoPlaceLadder = new BooleanSetting(this, "Авто-установка лестниц", true)
            .setVisible(() -> mode.is("Ladder Climb"));

    private final SliderSetting vineSpeed = new SliderSetting(this, "Скорость лиан", 0.25f, 0.1f, 0.5f, 0.05f)
            .setVisible(() -> mode.is("Vine Climb"));
    private final BooleanSetting autoPlaceVine = new BooleanSetting(this, "Авто-установка лиан", false)
            .setVisible(() -> mode.is("Vine Climb"));

    private final SliderSetting scaffoldingSpeed = new SliderSetting(this, "Скорость scaffolding", 0.35f, 0.1f, 0.6f, 0.05f)
            .setVisible(() -> mode.is("Scaffolding"));
    private final BooleanSetting autoPlaceScaffolding = new BooleanSetting(this, "Авто-установка scaffolding", true)
            .setVisible(() -> mode.is("Scaffolding"));

    private final SliderSetting slimeBounceHeight = new SliderSetting(this, "Высота отскока", 0.5f, 0.2f, 1.0f, 0.05f)
            .setVisible(() -> mode.is("Slime Bounce"));
    private final BooleanSetting autoPlaceSlime = new BooleanSetting(this, "Авто-установка слайм блоков", true)
            .setVisible(() -> mode.is("Slime Bounce"));

    private final SliderSetting waterElevatorSpeed = new SliderSetting(this, "Скорость лифта", 0.4f, 0.2f, 0.8f, 0.05f)
            .setVisible(() -> mode.is("Water Elevator"));
    private final BooleanSetting autoPlaceSoulSand = new BooleanSetting(this, "Авто-установка soul sand", true)
            .setVisible(() -> mode.is("Water Elevator"));

    private final SliderSetting cobwebSpeed = new SliderSetting(this, "Скорость в паутине", 0.15f, 0.05f, 0.3f, 0.05f)
            .setVisible(() -> mode.is("Cobweb Push"));
    private final BooleanSetting autoPlaceCobweb = new BooleanSetting(this, "Авто-установка паутины", true)
            .setVisible(() -> mode.is("Cobweb Push"));

    private final StopWatch stopWatch = new StopWatch();
    private final StopWatch packetTimer = new StopWatch();
    private int climbTicks = 0;
    private boolean lastCollided = false;
    private final Vector2f currentRotation = new Vector2f(0, 0);
    private int placeAttempts = 0;
    private boolean wasInWater = false;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        boolean currentlyCollided = mc.player.collidedHorizontally;

        switch (mode.getValue()) {
            case "Обычный" -> handleVanillaSpider();
            case "Jump Boost" -> handleJumpBoostMode();
            case "Ladder Climb" -> handleLadderClimbMode();
            case "Vine Climb" -> handleVineClimbMode();
            case "Scaffolding" -> handleScaffoldingMode();
            case "Slime Bounce" -> handleSlimeBounceMode();
            case "Water Elevator" -> handleWaterElevatorMode();
            case "Cobweb Push" -> handleCobwebPushMode();
            case "СпукиТайм" -> handleSpookyMode();
            case "Matrix" -> handleMatrixBypass();
            case "Vulcan" -> handleVulcanBypass();
            case "AAC" -> handleAACBypass();
            case "NCP" -> handleNCPBypass();
            case "GrimLatest" -> handleGrimLatestBypass();
        }

        if (currentlyCollided) {
            climbTicks++;
        } else {
            climbTicks = 0;
            placeAttempts = 0;
        }
        lastCollided = currentlyCollided;
    }

    private void handleVanillaSpider() {
        if (mc.player.collidedHorizontally) {
            Vector3d motion = mc.player.getMotion();
            mc.player.setMotion(motion.x, motionY.getValue(), motion.z);
        }
    }

    private void handleJumpBoostMode() {
        if (!mc.player.collidedHorizontally) return;

        if (mc.player.isPotionActive(Effects.JUMP_BOOST)) {
            int amplifier = mc.player.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier();
            float boost = (0.1f * (amplifier + 1)) * jumpBoostMultiplier.getValue();

            Vector3d motion = mc.player.getMotion();
            mc.player.setMotion(motion.x, boost, motion.z);
        } else {
            if (mc.player.isOnGround()) {
                mc.player.jump();
            }
        }
    }

    private void handleLadderClimbMode() {
        if (!mc.player.collidedHorizontally) return;

        if (isNearLadder()) {
            Vector3d motion = mc.player.getMotion();
            mc.player.setMotion(motion.x, ladderSpeed.getValue(), motion.z);
        } else if (autoPlaceLadder.getValue() && stopWatch.finished(200)) {
            placeLadderStack();
            stopWatch.reset();
        }
    }

    private void handleVineClimbMode() {
        if (!mc.player.collidedHorizontally) return;

        if (isNearVines()) {
            Vector3d motion = mc.player.getMotion();
            mc.player.setMotion(motion.x, vineSpeed.getValue(), motion.z);
        } else if (autoPlaceVine.getValue() && stopWatch.finished(250)) {
            placeVine();
            stopWatch.reset();
        }
    }

    private void handleScaffoldingMode() {
        if (!mc.player.collidedHorizontally) return;

        if (isNearScaffolding()) {
            Vector3d motion = mc.player.getMotion();
            mc.player.setMotion(motion.x, scaffoldingSpeed.getValue(), motion.z);
            mc.player.fallDistance = 0;
        } else if (autoPlaceScaffolding.getValue() && stopWatch.finished(150)) {
            placeScaffolding();
            stopWatch.reset();
        }
    }

    private void handleSlimeBounceMode() {
        if (!mc.player.collidedHorizontally) return;

        if (isNearSlimeBlock()) {
            if (mc.player.fallDistance > 0) {
                Vector3d motion = mc.player.getMotion();
                mc.player.setMotion(motion.x, slimeBounceHeight.getValue(), motion.z);
                mc.player.fallDistance = 0;
            }
        } else if (autoPlaceSlime.getValue() && stopWatch.finished(200)) {
            placeSlimeBlock();
            stopWatch.reset();
        }
    }

    private void handleWaterElevatorMode() {
        if (!mc.player.collidedHorizontally) return;

        if (mc.player.isInWater()) {
            if (isNearSoulSand() || isNearMagmaBlock()) {
                Vector3d motion = mc.player.getMotion();
                float speed = isNearSoulSand() ? waterElevatorSpeed.getValue() : -waterElevatorSpeed.getValue();
                mc.player.setMotion(motion.x, speed, motion.z);
            } else if (autoPlaceSoulSand.getValue() && stopWatch.finished(300)) {
                placeSoulSandUnderWater();
                stopWatch.reset();
            }
        }
    }

    private void handleCobwebPushMode() {
        if (!mc.player.collidedHorizontally) return;

        if (isInCobweb()) {
            Vector3d motion = mc.player.getMotion();
            mc.player.setMotion(motion.x, cobwebSpeed.getValue(), motion.z);
        } else if (autoPlaceCobweb.getValue() && stopWatch.finished(250)) {
            placeCobweb();
            stopWatch.reset();
        }
    }

    private void handleSpookyMode() {
        if (!mc.player.collidedHorizontally) return;

        long speed = 200;
        if (stopWatch.finished(speed)) {
            mc.player.setOnGround(true);
            mc.player.collidedVertically = true;
            mc.player.collidedHorizontally = true;
            mc.player.isAirBorne = true;
            mc.player.jump();
            placeLadderStack();
            stopWatch.reset();
        }
    }

    private void handleMatrixBypass() {
        if (mc.player.collidedHorizontally) {
            if (stopWatch.finished(100)) {
                mc.player.connection.sendPacket(new net.minecraft.network.play.client.CEntityActionPacket(mc.player, net.minecraft.network.play.client.CEntityActionPacket.Action.START_SPRINTING));
                mc.player.connection.sendPacket(new net.minecraft.network.play.client.CEntityActionPacket(mc.player, net.minecraft.network.play.client.CEntityActionPacket.Action.STOP_SPRINTING));

                Vector3d motion = mc.player.getMotion();
                mc.player.setMotion(motion.x, Math.min(0.12, motion.y + 0.08), motion.z);
                stopWatch.reset();
            }
        }
    }

    private void handleVulcanBypass() {
        if (mc.player.collidedHorizontally) {
            if (climbTicks % 8 == 0 && climbTicks > 0) {
                mc.player.connection.sendPacket(new net.minecraft.network.play.client.CPlayerDiggingPacket(
                        net.minecraft.network.play.client.CPlayerDiggingPacket.Action.START_DESTROY_BLOCK,
                        mc.player.getPosition().up(),
                        Direction.UP));
            }

            if (stopWatch.finished(120)) {
                Vector3d motion = mc.player.getMotion();
                mc.player.setMotion(motion.x, 0.15, motion.z);
                stopWatch.reset();
            }
        }
    }

    private void handleAACBypass() {
        if (mc.player.collidedHorizontally) {
            Vector3d motion = mc.player.getMotion();
            mc.player.setMotion(motion.x, 0.14, motion.z);
        }
    }

    private void handleNCPBypass() {
        if (mc.player.collidedHorizontally) {
            if (stopWatch.finished(150)) {
                Vector3d motion = mc.player.getMotion();
                double newY = Math.sin(climbTicks * 0.1) * 0.08 + 0.1;
                mc.player.setMotion(motion.x, newY, motion.z);
                stopWatch.reset();
            }
        }
    }

    private void handleGrimLatestBypass() {
        if (!mc.player.collidedHorizontally) return;

        boolean nearSlime = isNearSlimeBlock();
        double climbSpeed = nearSlime ? 0.45 : motionY.getValue();

        Vector3d motion = mc.player.getMotion();
        mc.player.setMotion(motion.x, climbSpeed, motion.z);

        if (stopWatch.finished(50)) {
            if (!nearSlime) {
                placeSlimeBlock();
            }
            stopWatch.reset();
        }
    }

    private boolean isNearLadder() {
        BlockPos playerPos = mc.player.getPosition();
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.LADDER) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isNearVines() {
        BlockPos playerPos = mc.player.getPosition();
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.VINE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isNearScaffolding() {
        BlockPos playerPos = mc.player.getPosition();
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.SCAFFOLDING) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isNearSlimeBlock() {
        BlockPos playerPos = mc.player.getPosition();
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.SLIME_BLOCK) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isNearSoulSand() {
        BlockPos playerPos = mc.player.getPosition();
        for (int x = -1; x <= 1; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.SOUL_SAND) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isNearMagmaBlock() {
        BlockPos playerPos = mc.player.getPosition();
        for (int x = -1; x <= 1; x++) {
            for (int y = -2; y <= 0; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.MAGMA_BLOCK) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isInCobweb() {
        BlockPos playerPos = mc.player.getPosition();
        return mc.world.getBlockState(playerPos).getBlock() == Blocks.COBWEB ||
                mc.world.getBlockState(playerPos.up()).getBlock() == Blocks.COBWEB;
    }

    private void placeVine() {
        int slotVine = getSlotForItem(Items.VINE);
        if (slotVine == -1) return;

        int lastSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = slotVine;

        BlockPos playerPos = mc.player.getPosition();
        BlockPos targetPos = getWallPosition(playerPos);

        if (targetPos != null) {
            placeBlock(targetPos);
        }

        mc.player.inventory.currentItem = lastSlot;
    }

    private void placeScaffolding() {
        int slotScaffolding = getSlotForItem(Items.SCAFFOLDING);
        if (slotScaffolding == -1) return;

        int lastSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = slotScaffolding;

        BlockPos playerPos = mc.player.getPosition();
        BlockPos targetPos = playerPos.up();

        if (mc.world.getBlockState(targetPos).isAir()) {
            placeBlock(targetPos);
        }

        mc.player.inventory.currentItem = lastSlot;
    }

    private void placeCobweb() {
        int slotCobweb = getSlotForItem(Items.COBWEB);
        if (slotCobweb == -1) return;

        int lastSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = slotCobweb;

        BlockPos playerPos = mc.player.getPosition();
        BlockPos targetPos = playerPos.up();

        if (mc.world.getBlockState(targetPos).isAir()) {
            placeBlock(targetPos);
        }

        mc.player.inventory.currentItem = lastSlot;
    }

    private void placeSoulSandUnderWater() {
        int slotSoulSand = getSlotForItem(Items.SOUL_SAND);
        if (slotSoulSand == -1) return;

        int lastSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = slotSoulSand;

        BlockPos playerPos = mc.player.getPosition();
        BlockPos targetPos = playerPos.down();

        if (mc.world.getBlockState(targetPos).isAir() || mc.world.getBlockState(targetPos).getBlock() == Blocks.WATER) {
            placeBlock(targetPos);
        }

        mc.player.inventory.currentItem = lastSlot;
    }

    private void placeSlimeBlock() {
        int slotSlime = getSlotForItem(Items.SLIME_BLOCK);
        if (slotSlime == -1) return;

        int lastSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = slotSlime;

        BlockPos playerPos = mc.player.getPosition();
        BlockPos targetPos = getOptimalSlimePosition(playerPos);

        if (targetPos != null && mc.world.getBlockState(targetPos).isAir()) {
            placeBlock(targetPos);
            placeAttempts++;
        }

        mc.player.inventory.currentItem = lastSlot;
    }

    private BlockPos getWallPosition(BlockPos playerPos) {
        Vector3d lookVec = mc.player.getLookVec();
        int offsetX = lookVec.x > 0.5 ? 1 : (lookVec.x < -0.5 ? -1 : 0);
        int offsetZ = lookVec.z > 0.5 ? 1 : (lookVec.z < -0.5 ? -1 : 0);

        if (offsetX == 0 && offsetZ == 0) {
            offsetX = lookVec.x > 0 ? 1 : -1;
        }

        BlockPos wallPos = playerPos.add(offsetX, 0, offsetZ);
        if (!mc.world.getBlockState(wallPos).isAir()) {
            return wallPos;
        }

        return null;
    }

    private BlockPos getOptimalSlimePosition(BlockPos playerPos) {
        Vector3d lookVec = mc.player.getLookVec();
        int offsetX = lookVec.x > 0.5 ? 1 : (lookVec.x < -0.5 ? -1 : 0);
        int offsetZ = lookVec.z > 0.5 ? 1 : (lookVec.z < -0.5 ? -1 : 0);

        if (offsetX == 0 && offsetZ == 0) {
            offsetX = lookVec.x > 0 ? 1 : -1;
        }

        for (int yOffset = 0; yOffset <= 2; yOffset++) {
            BlockPos checkPos = playerPos.add(offsetX, yOffset, offsetZ);
            if (mc.world.getBlockState(checkPos).isAir()) {
                BlockPos below = checkPos.down();
                if (!mc.world.getBlockState(below).isAir() || yOffset == 0) {
                    return checkPos;
                }
            }
        }

        return playerPos.add(offsetX, 1, offsetZ);
    }

    private void placeBlock(BlockPos pos) {
        if (!mc.world.getBlockState(pos).isAir()) {
            return;
        }

        Direction[] priorities = {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP};

        for (Direction direction : priorities) {
            BlockPos neighbor = pos.offset(direction);
            if (!mc.world.getBlockState(neighbor).isAir()) {
                Vector3d hitVec = new Vector3d(
                        neighbor.getX() + 0.5 + direction.getOpposite().getXOffset() * 0.5,
                        neighbor.getY() + 0.5 + direction.getOpposite().getYOffset() * 0.5,
                        neighbor.getZ() + 0.5 + direction.getOpposite().getZOffset() * 0.5
                );

                BlockRayTraceResult result = new BlockRayTraceResult(hitVec, direction.getOpposite(), neighbor, false);

                mc.playerController.rightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, result);
                mc.player.swingArm(Hand.MAIN_HAND);
                return;
            }
        }
    }

    private void placeLadderStack() {
        int slotLadder = getSlotForItem(Items.LADDER);
        if (slotLadder == -1) {
            ChatUtil.sendText("Лестницы не найдены!");
            return;
        }

        int lastSlot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = slotLadder;

        BlockPos playerPos = new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());

        for (int i = 1; i <= 2; i++) {
            BlockPos ladderPos = playerPos.up(i);
            if (mc.world.getBlockState(ladderPos).isAir()) {
                placeLadderBlock(ladderPos);
            }
        }

        mc.player.inventory.currentItem = lastSlot;
    }

    private void placeLadderBlock(BlockPos pos) {
        if (!mc.world.getBlockState(pos).isAir()) {
            return;
        }

        BlockPos downPos = pos.down();
        Vector3d vec = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        BlockRayTraceResult result = new BlockRayTraceResult(vec, Direction.UP, downPos, false);

        mc.playerController.clickBlock(downPos, Direction.UP);
        mc.player.swingArm(Hand.MAIN_HAND);
        mc.playerController.rightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, result);
    }

    private int getSlotForItem(net.minecraft.item.Item item) {
        for (int i = 0; i < 36; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        climbTicks = 0;
        placeAttempts = 0;
        wasInWater = false;
    }
}