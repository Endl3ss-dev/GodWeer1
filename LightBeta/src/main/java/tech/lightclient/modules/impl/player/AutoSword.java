package tech.lightclient.modules.impl.player;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.math.time.StopWatch;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.SliderSetting;

/**
 * Create by бабка чичовскава
 */
@FunctionAdd(name = "AutoSword", alias = "Auto Sword", category = Category.Player, description = "Автоматически крафтит мечи и продает их")
public class AutoSword extends Function {
    public static final AutoSword INSTANCE = new AutoSword();

    private final SliderSetting actionDelay = new SliderSetting("Задержка", 300f, 100f, 1000f, 50);
    private final SliderSetting sellPrice = new SliderSetting("Цена продажи", 35000f, 100f, 999999f, 1000f );

    private enum State {
        IDLE,
        CHECK_RESOURCES,
        FIND_CHEST,
        OPEN_CHEST,
        WAIT_CHEST,
        TAKE_STICKS,
        CLOSE_CHEST,
        SHOP_OPEN,
        SHOP_WAIT,
        SHOP_CLICK_GOLD,
        SHOP_WAIT_EMERALD,
        SHOP_CLICK_EMERALD,
        SHOP_CLOSE,
        CRAFT_OPEN,
        CRAFT_WAIT,
        CRAFT_PLACE_ITEMS,
        CRAFT_WAIT_RESULT,
        CRAFT_TAKE_RESULT,
        CRAFT_CLOSE,
        SELL_EQUIP_HAND,
        SELL_SEND_CMD,
        SELL_WAIT_AFTER_CMD,
        AH_CLEAR_OPEN,
        AH_CLEAR_WAIT,
        AH_CLEAR_CLICK_ENDER_CHEST,
        AH_CLEAR_WAIT_ENDER_CHEST,
        AH_CLEAR_CLICK_CLOCK,
        AH_CLEAR_CLOSE,
        AH_WAIT_COOLDOWN
    }

    private State state = State.IDLE;
    private static final int CRAFTING_TABLE_RADIUS = 3;
    private static final double INTERACTION_RANGE_SQUARED = 20.25;

    private final StopWatch timer = new StopWatch();
    private boolean auctionFull = false;
    private BlockPos craftingTablePos;

    public AutoSword() {
        addSettings(actionDelay, sellPrice);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        reset();
        state = State.CHECK_RESOURCES;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        reset();
    }

    private void reset() {
        state = State.IDLE;
        auctionFull = false;
        craftingTablePos = null;
        timer.reset();
    }

    private int countEmeralds() {
        if (mc.player == null) return 0;
        var inv = mc.player.getInventory();
        int total = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack s = inv.getStack(i);
            if (s.getItem() == Items.EMERALD) total += s.getCount();
        }
        return total;
    }

    private int countSticks() {
        if (mc.player == null) return 0;
        var inv = mc.player.getInventory();
        int total = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack s = inv.getStack(i);
            if (s.getItem() == Items.STICK) total += s.getCount();
        }
        return total;
    }

    private boolean isSword(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() == Items.DIAMOND_SWORD) return true;
        String name = stack.getName().getString().toLowerCase();
        return name.contains("меч") || name.contains("sword");
    }

    private int findSwordSlot() {
        if (mc.player == null) return -1;
        var inv = mc.player.getInventory();
        for (int i = 0; i < 36; i++) {
            if (isSword(inv.getStack(i))) {
                return i;
            }
        }
        return -1;
    }

    private BlockPos findChestBlock() {
        if (mc.player == null || mc.world == null) return null;
        BlockPos playerPos = mc.player.getBlockPos();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos pos = playerPos.add(dx, dy, dz);
                    var block = mc.world.getBlockState(pos).getBlock();
                    if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.BARREL) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private BlockPos findCraftingTable() {
        if (mc.player == null || mc.world == null) return null;

        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (int dx = -CRAFTING_TABLE_RADIUS; dx <= CRAFTING_TABLE_RADIUS; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -CRAFTING_TABLE_RADIUS; dz <= CRAFTING_TABLE_RADIUS; dz++) {
                    BlockPos pos = playerPos.add(dx, dy, dz);
                    if (mc.world.getBlockState(pos).getBlock() != Blocks.CRAFTING_TABLE) continue;

                    double distance = mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(pos));
                    if (distance <= INTERACTION_RANGE_SQUARED && distance < nearestDistance) {
                        nearest = pos;
                        nearestDistance = distance;
                    }
                }
            }
        }

        return nearest;
    }

    private void openCraftingTable(BlockPos pos) {
        Vec3d hitPos = Vec3d.ofCenter(pos).add(0.0, 0.5, 0.0);
        Vec3d difference = hitPos.subtract(mc.player.getEyePos());
        double horizontalDistance = Math.sqrt(difference.x * difference.x + difference.z * difference.z);

        mc.player.setYaw((float) Math.toDegrees(Math.atan2(-difference.x, difference.z)));
        mc.player.setPitch((float) Math.toDegrees(-Math.atan2(difference.y, horizontalDistance)));
        mc.interactionManager.interactBlock(
                mc.player,
                Hand.MAIN_HAND,
                new BlockHitResult(hitPos, Direction.UP, pos, false)
        );
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private int invToCraftingSlot(int invSlot) {
        if (invSlot >= 9 && invSlot <= 35) {
            return invSlot - 9 + 10;
        } else if (invSlot >= 0 && invSlot <= 8) {
            return invSlot + 37;
        }
        return -1;
    }

    private int invToPlayerContainerSlot(int invSlot) {
        if (invSlot >= 9 && invSlot <= 35) {
            return invSlot;
        } else if (invSlot >= 0 && invSlot <= 8) {
            return invSlot + 36;
        }
        return -1;
    }

    @EventHandler
    public void onPacket(EventPacket packetEvent) {
        if (!packetEvent.isReceive() || !(packetEvent.getPacket() instanceof GameMessageS2CPacket packet)) {
            return;
        }

        String message = packet.content().getString();
        if (message.contains("\u043e\u0441\u0432\u043e\u0431\u043e\u0434\u0438\u0442\u0435 \u0445\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435")) {
            auctionFull = true;
        } else if (message.contains("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0432\u044b\u0441\u0442\u0430\u0432\u0438\u0442\u044c")
                || message.contains("\u0430\u0440\u0435\u043d\u0434\u0443\u0439\u0442\u0435 \u0431\u043e\u043b\u044c\u0448\u0435 \u0441\u043b\u043e\u0442\u043e\u0432")) {
            auctionFull = true;
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        long delay = actionDelay.get().longValue();

        switch (state) {
            case CHECK_RESOURCES: {
                if (!timer.hasReached(delay)) return;
                if (findSwordSlot() != -1) {
                    state = State.SELL_EQUIP_HAND;
                    timer.reset();
                    return;
                }
                if (countSticks() < 1) {
                    state = State.FIND_CHEST;
                    timer.reset();
                    return;
                }
                if (countEmeralds() < 2) {
                    state = State.SHOP_OPEN;
                    timer.reset();
                    return;
                }
                state = State.CRAFT_OPEN;
                timer.reset();
                break;
            }
            case FIND_CHEST: {
                BlockPos chestPos = findChestBlock();
                if (chestPos == null) {
                    state = State.CHECK_RESOURCES;
                    timer.reset();
                    return;
                }
                state = State.OPEN_CHEST;
                timer.reset();
                break;
            }

            case OPEN_CHEST: {
                if (!timer.hasReached(delay)) return;
                BlockPos chestPos = findChestBlock();
                if (chestPos == null) {
                    state = State.FIND_CHEST;
                    return;
                }
                Vec3d center = Vec3d.ofCenter(chestPos);
                Vec3d diff = center.subtract(mc.player.getEyePos());
                double hDist = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
                mc.player.setYaw((float) Math.toDegrees(Math.atan2(-diff.x, diff.z)));
                mc.player.setPitch((float) Math.toDegrees(-Math.atan2(diff.y, hDist)));

                BlockHitResult hitResult = new BlockHitResult(center, Direction.UP, chestPos, false);
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
                state = State.WAIT_CHEST;
                timer.reset();
                break;
            }

            case WAIT_CHEST: {
                if (mc.currentScreen instanceof GenericContainerScreen) {
                    state = State.TAKE_STICKS;
                    timer.reset();
                } else if (timer.hasReached(3000)) {
                    state = State.OPEN_CHEST;
                    timer.reset();
                }
                break;
            }

            case TAKE_STICKS: {
                if (!timer.hasReached(delay)) return;
                if (!(mc.currentScreen instanceof GenericContainerScreen screen)) {
                    state = State.CHECK_RESOURCES;
                    return;
                }
                int stickSlot = -1;
                for (Slot slot : screen.getScreenHandler().slots) {
                    if (slot.inventory != mc.player.getInventory() && !slot.getStack().isEmpty() && slot.getStack().getItem() == Items.STICK) {
                        stickSlot = slot.id;
                        break;
                    }
                }
                if (stickSlot != -1) {
                    mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, stickSlot, 0, SlotActionType.QUICK_MOVE, mc.player);
                }
                state = State.CLOSE_CHEST;
                timer.reset();
                break;
            }

            case CLOSE_CHEST: {
                if (!timer.hasReached(delay)) return;
                if (mc.currentScreen != null) mc.currentScreen.close();
                state = State.CHECK_RESOURCES;
                timer.reset();
                break;
            }
            case SHOP_OPEN: {
                if (!timer.hasReached(delay)) return;
                sendCommand("shop");
                state = State.SHOP_WAIT;
                timer.reset();
                break;
            }

            case SHOP_WAIT: {
                if (mc.currentScreen instanceof GenericContainerScreen) {
                    state = State.SHOP_CLICK_GOLD;
                    timer.reset();
                } else if (timer.hasReached(5000)) {
                    state = State.SHOP_OPEN;
                    timer.reset();
                }
                break;
            }

            case SHOP_CLICK_GOLD: {
                if (!timer.hasReached(delay)) return;
                if (!(mc.currentScreen instanceof GenericContainerScreen screen)) {
                    state = State.SHOP_OPEN;
                    return;
                }
                int goldSlot = -1;
                for (Slot slot : screen.getScreenHandler().slots) {
                    if (!slot.getStack().isEmpty() && slot.getStack().getItem() == Items.GOLD_INGOT) {
                        goldSlot = slot.id;
                        break;
                    }
                }
                if (goldSlot != -1) {
                    mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, goldSlot, 0, SlotActionType.PICKUP, mc.player);
                    state = State.SHOP_WAIT_EMERALD;
                } else if (timer.hasReached(3000)) {
                    mc.currentScreen.close();
                    state = State.CHECK_RESOURCES;
                }
                timer.reset();
                break;
            }

            case SHOP_WAIT_EMERALD: {
                if (!timer.hasReached(delay)) return;
                if (!(mc.currentScreen instanceof GenericContainerScreen screen)) {
                    state = State.SHOP_OPEN;
                    return;
                }
                boolean emeraldFound = false;
                for (Slot slot : screen.getScreenHandler().slots) {
                    if (!slot.getStack().isEmpty() && slot.getStack().getItem() == Items.EMERALD) {
                        emeraldFound = true;
                        break;
                    }
                }
                if (emeraldFound) {
                    state = State.SHOP_CLICK_EMERALD;
                } else if (timer.hasReached(4000)) {
                    mc.currentScreen.close();
                    state = State.SHOP_OPEN;
                }
                timer.reset();
                break;
            }

            case SHOP_CLICK_EMERALD: {
                if (!timer.hasReached(delay)) return;
                if (!(mc.currentScreen instanceof GenericContainerScreen screen)) {
                    state = State.SHOP_OPEN;
                    return;
                }
                int emeraldSlot = -1;
                for (Slot slot : screen.getScreenHandler().slots) {
                    if (!slot.getStack().isEmpty() && slot.getStack().getItem() == Items.EMERALD) {
                        emeraldSlot = slot.id;
                        break;
                    }
                }
                if (emeraldSlot != -1) {
                    mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, emeraldSlot, 1, SlotActionType.PICKUP, mc.player);
                }
                state = State.SHOP_CLOSE;
                timer.reset();
                break;
            }

            case SHOP_CLOSE: {
                if (!timer.hasReached(delay)) return;
                if (mc.currentScreen != null) mc.currentScreen.close();
                state = State.CHECK_RESOURCES;
                timer.reset();
                break;
            }

            case CRAFT_OPEN: {
                if (!timer.hasReached(delay)) return;

                craftingTablePos = findCraftingTable();
                if (craftingTablePos == null) {
                    state = State.CHECK_RESOURCES;
                    timer.reset();
                    return;
                }

                openCraftingTable(craftingTablePos);
                state = State.CRAFT_WAIT;
                timer.reset();
                break;
            }

            case CRAFT_WAIT: {
                if (mc.currentScreen instanceof CraftingScreen) {
                    state = State.CRAFT_PLACE_ITEMS;
                    timer.reset();
                } else if (timer.hasReached(5000)) {
                    craftingTablePos = null;
                    state = State.CRAFT_OPEN;
                    timer.reset();
                }
                break;
            }

            case CRAFT_PLACE_ITEMS: {
                if (!timer.hasReached(delay)) return;
                if (!(mc.currentScreen instanceof CraftingScreen screen)) {
                    state = State.CRAFT_OPEN;
                    return;
                }
                CraftingScreenHandler handler = (CraftingScreenHandler) screen.getScreenHandler();
                var inv = mc.player.getInventory();

                int stickInvSlot = -1;
                int emeraldInvSlot = -1;

                for (int i = 0; i < 36; i++) {
                    if (inv.getStack(i).getItem() == Items.STICK && stickInvSlot == -1) stickInvSlot = i;
                    if (inv.getStack(i).getItem() == Items.EMERALD && inv.getStack(i).getCount() >= 2 && emeraldInvSlot == -1) emeraldInvSlot = i;
                }

                if (stickInvSlot == -1 || emeraldInvSlot == -1) {
                    mc.currentScreen.close();
                    state = State.CHECK_RESOURCES;
                    timer.reset();
                    return;
                }

                int stickSlot = invToCraftingSlot(stickInvSlot);
                int emeraldSlot = invToCraftingSlot(emeraldInvSlot);
                mc.interactionManager.clickSlot(handler.syncId, stickSlot, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(handler.syncId, 8, 1, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(handler.syncId, stickSlot, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(handler.syncId, emeraldSlot, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(handler.syncId, 2, 1, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(handler.syncId, 5, 1, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(handler.syncId, emeraldSlot, 0, SlotActionType.PICKUP, mc.player);

                state = State.CRAFT_WAIT_RESULT;
                timer.reset();
                break;
            }

            case CRAFT_WAIT_RESULT: {
                if (!timer.hasReached(delay)) return;
                if (!(mc.currentScreen instanceof CraftingScreen screen)) {
                    state = State.CRAFT_OPEN;
                    return;
                }
                CraftingScreenHandler handler = (CraftingScreenHandler) screen.getScreenHandler();
                if (!handler.getSlot(0).getStack().isEmpty()) {
                    state = State.CRAFT_TAKE_RESULT;
                } else if (timer.hasReached(3000)) {
                    mc.currentScreen.close();
                    state = State.CHECK_RESOURCES;
                }
                timer.reset();
                break;
            }

            case CRAFT_TAKE_RESULT: {
                if (!timer.hasReached(delay)) return;
                if (!(mc.currentScreen instanceof CraftingScreen screen)) {
                    state = State.CRAFT_OPEN;
                    return;
                }
                CraftingScreenHandler handler = (CraftingScreenHandler) screen.getScreenHandler();
                mc.interactionManager.clickSlot(handler.syncId, 0, 0, SlotActionType.QUICK_MOVE, mc.player);

                state = State.CRAFT_CLOSE;
                timer.reset();
                break;
            }

            case CRAFT_CLOSE: {
                if (!timer.hasReached(delay)) return;
                if (mc.currentScreen != null) mc.currentScreen.close();
                state = State.SELL_EQUIP_HAND;
                timer.reset();
                break;
            }

            case SELL_EQUIP_HAND: {
                if (!timer.hasReached(delay)) return;

                int swordSlot = findSwordSlot();
                if (swordSlot == -1) {
                    state = State.CHECK_RESOURCES;
                    timer.reset();
                    return;
                }

                var inv = mc.player.getInventory();
                inv.setSelectedSlot(0);

                if (swordSlot != 0) {
                    if (swordSlot < 9) {
                        inv.setSelectedSlot(swordSlot);
                    } else {
                        int containerSlot = invToPlayerContainerSlot(swordSlot);
                        mc.interactionManager.clickSlot(
                                mc.player.playerScreenHandler.syncId,
                                containerSlot,
                                0,
                                SlotActionType.SWAP,
                                mc.player
                        );
                    }
                }

                state = State.SELL_SEND_CMD;
                timer.reset();
                break;
            }

            case SELL_SEND_CMD: {
                if (!timer.hasReached(delay)) return;

                if (!isSword(mc.player.getMainHandStack())) {
                    state = State.SELL_EQUIP_HAND;
                    timer.reset();
                    return;
                }

                int price = sellPrice.get().intValue();
                auctionFull = false;

                sendCommand("ah sell " + price);

                state = State.SELL_WAIT_AFTER_CMD;
                timer.reset();
                break;
            }

            case SELL_WAIT_AFTER_CMD: {
                if (!timer.hasReached(delay * 2)) return;
                if (auctionFull) {
                    state = State.AH_CLEAR_OPEN;
                    timer.reset();
                    return;
                }

                if (findSwordSlot() != -1) {
                    state = State.SELL_EQUIP_HAND;
                    timer.reset();
                } else {
                    state = State.CHECK_RESOURCES;
                    timer.reset();
                }
                break;
            }
            case AH_CLEAR_OPEN: {
                if (!timer.hasReached(delay)) return;
                sendCommand("ah");
                state = State.AH_CLEAR_WAIT;
                timer.reset();
                break;
            }

            case AH_CLEAR_WAIT: {
                if (mc.currentScreen instanceof GenericContainerScreen) {
                    state = State.AH_CLEAR_CLICK_ENDER_CHEST;
                    timer.reset();
                } else if (timer.hasReached(5000)) {
                    state = State.AH_CLEAR_OPEN;
                    timer.reset();
                }
                break;
            }

            case AH_CLEAR_CLICK_ENDER_CHEST: {
                if (!timer.hasReached(delay)) return;
                if (!(mc.currentScreen instanceof GenericContainerScreen screen)) {
                    state = State.AH_CLEAR_OPEN;
                    return;
                }
                int enderChestSlot = -1;
                for (Slot slot : screen.getScreenHandler().slots) {
                    if (!slot.getStack().isEmpty() && slot.getStack().getItem() == Items.ENDER_CHEST) {
                        enderChestSlot = slot.id;
                        break;
                    }
                }
                if (enderChestSlot != -1) {
                    mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, enderChestSlot, 0, SlotActionType.PICKUP, mc.player);
                    state = State.AH_CLEAR_WAIT_ENDER_CHEST;
                } else if (timer.hasReached(3000)) {
                    state = State.AH_CLEAR_CLOSE;
                }
                timer.reset();
                break;
            }

            case AH_CLEAR_WAIT_ENDER_CHEST: {
                if (!timer.hasReached(delay)) return;
                state = State.AH_CLEAR_CLICK_CLOCK;
                timer.reset();
                break;
            }

            case AH_CLEAR_CLICK_CLOCK: {
                if (!timer.hasReached(delay)) return;
                if (!(mc.currentScreen instanceof GenericContainerScreen screen)) {
                    state = State.AH_CLEAR_OPEN;
                    return;
                }
                int clockSlot = -1;
                for (Slot slot : screen.getScreenHandler().slots) {
                    if (!slot.getStack().isEmpty() && slot.getStack().getItem() == Items.CLOCK) {
                        clockSlot = slot.id;
                        break;
                    }
                }
                if (clockSlot != -1) {
                    mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, clockSlot, 0, SlotActionType.PICKUP, mc.player);
                }
                state = State.AH_CLEAR_CLOSE;
                timer.reset();
                break;
            }

            case AH_CLEAR_CLOSE: {
                if (!timer.hasReached(delay)) return;
                if (mc.currentScreen != null) mc.currentScreen.close();

                auctionFull = false;
                state = State.AH_WAIT_COOLDOWN;
                timer.reset();
                break;
            }

            case AH_WAIT_COOLDOWN: {
                if (!timer.hasReached(62000)) {
                    return;
                }
                state = State.CHECK_RESOURCES;
                timer.reset();
                break;
            }
        }
    }

    private void sendCommand(String command) {
        if (mc.player == null || command == null || command.isBlank()) return;
        mc.player.networkHandler.sendChatCommand(command.startsWith("/") ? command.substring(1) : command);
    }
}
