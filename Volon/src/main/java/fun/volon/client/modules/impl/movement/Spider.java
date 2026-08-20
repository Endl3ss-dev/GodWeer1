package fun.volon.client.modules.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.api.storages.implement.RotationStorage;
import fun.volon.api.utils.rotate.Rotation;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;
import fun.volon.client.modules.settings.implement.ModeSetting;

public class Spider extends Module {

    public static Spider INSTANCE = new Spider();

    private final ModeSetting mode = new ModeSetting("Мод", "Вода", "Вода", "SpookyTime", "GrimLatest");
    private final BooleanSetting legit = new BooleanSetting("Легит", false);
    private final FloatSetting motionY = new FloatSetting("Скорость", 0.42F, 0.1F, 1.0F, 0.01F).visible(() -> mode.is("GrimLatest"));

    private int lastSlot = -1;
    private boolean isClimbing = false;
    private int swapBackSlot = -1;
    private int spookyTicks;
    private int chargeSlot = -1;
    private boolean charging;
    private long lastPlaceTime = 0L;

    public Spider() {
        super("Spider", "Позволяет взбираться по стенам", ModuleCategory.MOVEMENT);
        addSettings(mode, legit, motionY);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player == null) return;

        if (lastSlot != -1 && legit.isState()) {
            mc.player.getInventory().selectedSlot = lastSlot;
        }

        lastSlot = -1;
        swapBackSlot = -1;
        isClimbing = false;
        spookyTicks = 0;
        chargeSlot = -1;
        charging = false;
        lastPlaceTime = 0L;
    }

    @EventLink
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;

        if (!mc.player.horizontalCollision) {
            stopClimbing();
            return;
        }

        isClimbing = true;
        RotationStorage.update(new Rotation(mc.player.getYaw(), 0), 360, 360, 360, 360, 1, 1, false);

        if (mode.is("SpookyTime")) {
            processSpookyTime();
            return;
        }
        
        if (mode.is("GrimLatest")) {
            processGrimLatest();
            return;
        }

        int bucketSlot = getBucketSlot(false);
        if (bucketSlot == -1) return;

        useBucket(bucketSlot, legit.isState());
        mc.player.setVelocity(mc.player.getVelocity().x, 0.36, mc.player.getVelocity().z);
    }

    private void stopClimbing() {
        if (lastSlot != -1 && legit.isState()) {
            mc.player.getInventory().selectedSlot = lastSlot;
            lastSlot = -1;
        }

        if (swapBackSlot != -1) {
            mc.interactionManager.clickSlot(0, swapBackSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
            swapBackSlot = -1;
        }

        isClimbing = false;
        spookyTicks = 0;
        chargeSlot = -1;
        charging = false;
    }

    private void processSpookyTime() {
        int bucketSlot = getBucketSlot(true);
        boolean bucketPulse = spookyTicks % 5 == 0;
        boolean boostPulse = spookyTicks % 4 != 3;

        keepChargeHeld();

        if (bucketSlot != -1 && bucketPulse) {
            useBucket(bucketSlot, false);
            keepChargeHeld();
        }

        double y = boostPulse ? 0.18 : 0.03;
        mc.player.setVelocity(mc.player.getVelocity().x, y, mc.player.getVelocity().z);
        spookyTicks++;
    }

    private void useBucket(int bucketSlot, boolean legitMode) {
        if (!legitMode) {
            int currentSlot = mc.player.getInventory().selectedSlot;
            boolean isInventorySwap = bucketSlot >= 9 && bucketSlot <= 35;

            if (isInventorySwap) {
                mc.interactionManager.clickSlot(0, bucketSlot, currentSlot, SlotActionType.SWAP, mc.player);
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                mc.interactionManager.clickSlot(0, bucketSlot, currentSlot, SlotActionType.SWAP, mc.player);
            } else {
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(bucketSlot));
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(currentSlot));
            }
            return;
        }

        boolean isInventorySwap = bucketSlot >= 9 && bucketSlot <= 35;

        if (isInventorySwap) {
            mc.interactionManager.clickSlot(0, bucketSlot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
            swapBackSlot = bucketSlot;
        } else if (mc.player.getInventory().selectedSlot != bucketSlot) {
            if (lastSlot == -1) {
                lastSlot = mc.player.getInventory().selectedSlot;
            }
            mc.player.getInventory().selectedSlot = bucketSlot;
        }

        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
    }

    private void keepChargeHeld() {
        if (isChargeItem(mc.player.getOffHandStack())) {
            if (!charging || spookyTicks % 12 == 0) {
                sendChargeUsePacket(Hand.OFF_HAND);
            }
            charging = true;
            return;
        }

        if (chargeSlot == -1 || !isChargeItem(mc.player.getInventory().getStack(chargeSlot))) {
            chargeSlot = getChargeHotbarSlot();
            charging = false;
        }
        if (chargeSlot == -1) return;

        if (mc.player.getInventory().selectedSlot != chargeSlot) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(chargeSlot));
            mc.player.getInventory().selectedSlot = chargeSlot;
            charging = false;
        }

        if (!charging || spookyTicks % 12 == 0) {
            sendChargeUsePacket(Hand.MAIN_HAND);
        }
        charging = true;
    }

    private void sendChargeUsePacket(Hand hand) {
        mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(hand, 0, mc.player.getYaw(), mc.player.getPitch()));
    }

    private int getBucketSlot(boolean allowLava) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isBucket(stack, allowLava)) {
                return i;
            }
        }

        if (!legit.isState() || mode.is("SpookyTime")) {
            for (int i = 9; i < 36; i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (isBucket(stack, allowLava)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private int getChargeHotbarSlot() {
        for (int i = 0; i < 9; i++) {
            if (isChargeItem(mc.player.getInventory().getStack(i))) {
                return i;
            }
        }
        return -1;
    }

    private boolean isBucket(ItemStack stack, boolean allowLava) {
        return stack.getItem() == Items.WATER_BUCKET || allowLava && stack.getItem() == Items.LAVA_BUCKET;
    }

    private boolean isChargeItem(ItemStack stack) {
        return stack.getItem() instanceof BowItem || stack.getItem() instanceof TridentItem;
    }
    
    // ========== GrimLatest Mode ==========
    
    private void processGrimLatest() {
        if (!mc.player.horizontalCollision) return;
        
        boolean nearSlime = isNearSlimeBlock();
        double climbSpeed = nearSlime ? 0.45 : motionY.get();
        
        mc.player.setVelocity(mc.player.getVelocity().x, climbSpeed, mc.player.getVelocity().z);
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlaceTime >= 50) {
            placeSlimeBlock();
            lastPlaceTime = currentTime;
        }
    }
    
    private boolean isNearSlimeBlock() {
        BlockPos playerPos = mc.player.getBlockPos();
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    Block block = mc.world.getBlockState(checkPos).getBlock();
                    if (block == Blocks.SLIME_BLOCK) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void placeSlimeBlock() {
        int slotSlime = getSlotForSlime();
        if (slotSlime == -1) return;
        
        int lastSlotSave = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = slotSlime;
        
        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos targetPos = getOptimalSlimePosition(playerPos);
        
        if (targetPos != null && mc.world.getBlockState(targetPos).isAir()) {
            // Находим соседний блок для размещения
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = targetPos.offset(direction);
                if (!mc.world.getBlockState(neighbor).isAir()) {
                    Vec3d hitVec = new Vec3d(
                        neighbor.getX() + 0.5 + direction.getOpposite().getOffsetX() * 0.5,
                        neighbor.getY() + 0.5 + direction.getOpposite().getOffsetY() * 0.5,
                        neighbor.getZ() + 0.5 + direction.getOpposite().getOffsetZ() * 0.5
                    );
                    
                    BlockHitResult result = new BlockHitResult(hitVec, direction.getOpposite(), neighbor, false);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, result);
                    mc.player.swingHand(Hand.MAIN_HAND);
                    break;
                }
            }
        }
        
        mc.player.getInventory().selectedSlot = lastSlotSave;
    }
    
    private BlockPos getOptimalSlimePosition(BlockPos playerPos) {
        Vec3d lookVec = mc.player.getRotationVec(1.0f);
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
    
    private int getSlotForSlime() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.SLIME_BLOCK) {
                return i;
            }
        }
        return -1;
    }
}
