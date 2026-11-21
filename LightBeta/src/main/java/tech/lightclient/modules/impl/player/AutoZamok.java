package tech.lightclient.modules.impl.player;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.baritone.BaritoneHelper;
import tech.lightclient.client.utils.client.IMinecraft;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import tech.lightclient.modules.api.settings.impl.StringSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FunctionAdd(name = "AutoZamok", alias = "Auto Zamok", category = Category.Player, description = "Автоматический замок: берёт кирку и инвизку, ломает шалкера")
public class AutoZamok extends Function implements IMinecraft {

    public final StringSetting homeName = new StringSetting("Название home", "zamok");
    public final StringSetting pickaxeSign = new StringSetting("Табличка кирок", "кирка");
    public final StringSetting invisSign = new StringSetting("Табличка инвизки", "инвизка");
    public final StringSetting storageHome = new StringSetting("Home склада", "home");
    public final SliderSetting invisCount = new SliderSetting("Инвизки брать", 2, 1, 8, 1);
    public final BooleanSetting storeWhenFull = new BooleanSetting("Складывать при полном инв.", false);
    public final SliderSetting stashScanRadius = new SliderSetting("Радиус скана", 8, 4, 16, 1);
    public final BooleanSetting autoStart = new BooleanSetting("Авто-старт", false);

    private enum Phase {
        IDLE("Ожидание"),
        GO_STORAGE("На склад"),
        FIND_PICKAXE_CHEST("Поиск сундука кирок"),
        TAKE_PICKAXE("Берём кирку"),
        FIND_INVIS_CHEST("Поиск сундука инвизки"),
        TAKE_INVIS("Берём инвизку"),
        TELEPORT_CITY("Телепорт на замок"),
        GO_ZERO("На нулевые координаты"),
        SCAN_SHULKERS("Сканируем шалкера"),
        BREAK_SHULKER("Ломаем шалкер"),
        REMOVE_PICKAXE("Убираем кирку"),
        STORE_ITEMS("Складываем предметы"),
        DONE("Готово");

        final String label;
        Phase(String label) { this.label = label; }
    }

    private Phase phase = Phase.IDLE;
    private long phaseStartedAt;
    private BlockPos storageChest;
    private BlockPos currentShulker;
    private int pickaxeSlot = -1;
    private int lastSlot = -1;
    private int shulkersBroken = 0;
    private int waitTicks = 0;

    public AutoZamok() {
        addSettings(homeName, pickaxeSign, invisSign, storageHome, invisCount, storeWhenFull, stashScanRadius, autoStart);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        resetRuntime();
        if (autoStart.get()) {
            setPhase(Phase.GO_STORAGE, "Начинаем замок");
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        BaritoneHelper.stop();
        closeScreen();
        if (lastSlot != -1 && mc.player != null) {
            mc.player.getInventory().setSelectedSlot(lastSlot);
        }
        resetRuntime();
    }

    private void resetRuntime() {
        phase = Phase.IDLE;
        storageChest = null;
        currentShulker = null;
        pickaxeSlot = -1;
        lastSlot = -1;
        shulkersBroken = 0;
        waitTicks = 0;
    }

    private void setPhase(Phase newPhase, String reason) {
        phase = newPhase;
        phaseStartedAt = System.currentTimeMillis();
    }

    private boolean phaseElapsed(long ms) {
        return System.currentTimeMillis() - phaseStartedAt >= ms;
    }

    @EventHandler
    public void onTick(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (phase == Phase.IDLE) return;

        if (waitTicks > 0) {
            waitTicks--;
            return;
        }

        switch (phase) {
            case GO_STORAGE -> goToStorage();
            case FIND_PICKAXE_CHEST -> findPickaxeChest();
            case TAKE_PICKAXE -> takePickaxe();
            case FIND_INVIS_CHEST -> findInvisChest();
            case TAKE_INVIS -> takeInvis();
            case TELEPORT_CITY -> teleportCity();
            case GO_ZERO -> goToZero();
            case SCAN_SHULKERS -> scanShulkers();
            case BREAK_SHULKER -> breakShulker();
            case REMOVE_PICKAXE -> removePickaxe();
            case STORE_ITEMS -> storeItems();
            case DONE -> done();
        }
    }

    private void goToStorage() {
        String cmd = storageHome.get().trim();
        if (cmd.isEmpty()) cmd = "home";
        mc.player.networkHandler.sendChatCommand(cmd);
        waitTicks = 80;
        setPhase(Phase.FIND_PICKAXE_CHEST, "Ищем сундук с кирками");
    }

    private void findPickaxeChest() {
        BlockPos chest = findChestBySign(pickaxeSign.get());
        if (chest == null) {
            setPhase(Phase.FIND_INVIS_CHEST, "Сундук кирок не найден, ищем инвизку");
            return;
        }
        storageChest = chest;
        if (distanceTo(chest) > 4.5) {
            if (!BaritoneHelper.isPathing() && phaseElapsed(2000)) {
                BaritoneHelper.goTo(chest, 2);
            }
            return;
        }
        BaritoneHelper.stop();
        openChest(chest);
        waitTicks = 5;
        setPhase(Phase.TAKE_PICKAXE, "Берём кирку");
    }

    private void takePickaxe() {
        if (!(mc.currentScreen instanceof GenericContainerScreen screen)) {
            if (phaseElapsed(3000)) setPhase(Phase.FIND_INVIS_CHEST, "Сундук не открылся");
            return;
        }
        GenericContainerScreenHandler handler = screen.getScreenHandler();
        Inventory inv = handler.getInventory();

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isOf(Items.GOLDEN_PICKAXE) || (stack.getName().getString().toLowerCase().contains("кирка") && stack.getItem().toString().contains("pickaxe"))) {
                mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                waitTicks = 5;
                closeScreen();
                setPhase(Phase.FIND_INVIS_CHEST, "Кирка взята, ищем инвизку");
                return;
            }
        }
        closeScreen();
        setPhase(Phase.FIND_INVIS_CHEST, "Кирка не найдена, ищем инвизку");
    }

    private void findInvisChest() {
        BlockPos chest = findChestBySign(invisSign.get());
        if (chest == null) {
            setPhase(Phase.TELEPORT_CITY, "Сундук инвизки не найден, телепорт");
            return;
        }
        if (distanceTo(chest) > 4.5) {
            if (!BaritoneHelper.isPathing() && phaseElapsed(2000)) {
                BaritoneHelper.goTo(chest, 2);
            }
            return;
        }
        BaritoneHelper.stop();
        openChest(chest);
        waitTicks = 5;
        setPhase(Phase.TAKE_INVIS, "Берём инвизку");
    }

    private void takeInvis() {
        if (!(mc.currentScreen instanceof GenericContainerScreen screen)) {
            if (phaseElapsed(3000)) setPhase(Phase.TELEPORT_CITY, "Сундук не открылся");
            return;
        }
        GenericContainerScreenHandler handler = screen.getScreenHandler();
        Inventory inv = handler.getInventory();
        int want = invisCount.get().intValue();

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION)) {
                if (stack.getName().getString().toLowerCase().contains("инвиз") || stack.getName().getString().toLowerCase().contains("invis")) {
                    if (stack.getCount() > want) {
                        mc.interactionManager.clickSlot(handler.syncId, i, 1, SlotActionType.PICKUP, mc.player);
                        waitTicks = 3;
                        mc.interactionManager.clickSlot(handler.syncId, i + 27, 0, SlotActionType.PICKUP, mc.player);
                    } else {
                        mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                    }
                    waitTicks = 5;
                    closeScreen();
                    setPhase(Phase.TELEPORT_CITY, "Инвизка взята, телепорт");
                    return;
                }
            }
        }
        closeScreen();
        setPhase(Phase.TELEPORT_CITY, "Инвизка не найдена, телепорт");
    }

    private void teleportCity() {
        String cmd = homeName.get().trim();
        if (cmd.isEmpty()) cmd = "zamok";
        mc.player.networkHandler.sendChatCommand("home " + cmd);
        waitTicks = 80;
        setPhase(Phase.GO_ZERO, "Идём на нулевые координаты");
    }

    private void goToZero() {
        if (mc.player == null) return;
        double dist = Math.hypot(mc.player.getX(), mc.player.getZ());
        if (dist < 5) {
            BaritoneHelper.stop();
            setPhase(Phase.SCAN_SHULKERS, "Сканируем шалкера");
            return;
        }
        if (!BaritoneHelper.isPathing() && phaseElapsed(2000)) {
            BaritoneHelper.goTo(new BlockPos(0, mc.player.getBlockY(), 0), 3);
        }
    }

    private void scanShulkers() {
        currentShulker = findNearestShulker();
        if (currentShulker == null) {
            if (!BaritoneHelper.isPathing() && phaseElapsed(3000)) {
                int r = 30;
                int rx = mc.player.getBlockX() + (int)(Math.random() * r * 2) - r;
                int rz = mc.player.getBlockZ() + (int)(Math.random() * r * 2) - r;
                BaritoneHelper.goTo(new BlockPos(rx, mc.player.getBlockY(), rz), 3);
            }
            return;
        }
        setPhase(Phase.BREAK_SHULKER, "Ломаем шалкер");
    }

    private void breakShulker() {
        if (currentShulker == null) {
            setPhase(Phase.SCAN_SHULKERS, "Шалкер потерян, сканируем");
            return;
        }
        double dist = distanceTo(currentShulker);
        if (dist > 4.5) {
            if (!BaritoneHelper.isPathing() && phaseElapsed(2000)) {
                BaritoneHelper.goTo(currentShulker, 1);
            }
            return;
        }
        BaritoneHelper.stop();

        pickaxeSlot = findItemInHotbar(Items.GOLDEN_PICKAXE);
        if (pickaxeSlot == -1) {
            pickaxeSlot = findPickaxeInHotbar();
        }
        if (pickaxeSlot == -1) {
            setPhase(Phase.REMOVE_PICKAXE, "Кирка не найдена");
            return;
        }

        if (lastSlot == -1) lastSlot = mc.player.getInventory().getSelectedSlot();
        if (mc.player.getInventory().getSelectedSlot() != pickaxeSlot) {
            mc.player.getInventory().setSelectedSlot(pickaxeSlot);
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(pickaxeSlot));
            waitTicks = 2;
            return;
        }

        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, currentShulker, Direction.UP));
        mc.player.swingHand(Hand.MAIN_HAND);
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, currentShulker, Direction.UP));

        shulkersBroken++;
        waitTicks = 3;
        setPhase(Phase.REMOVE_PICKAXE, "Убираем кирку");
    }

    private void removePickaxe() {
        if (lastSlot != -1 && mc.player != null) {
            mc.player.getInventory().setSelectedSlot(lastSlot);
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(lastSlot));
        }

        if (isInventoryFull() && storeWhenFull.get()) {
            setPhase(Phase.STORE_ITEMS, "Инвентарь полон, складываем");
            return;
        }

        BlockPos next = findNearestShulker();
        if (next != null) {
            currentShulker = next;
            setPhase(Phase.BREAK_SHULKER, "Ломаем следующий шалкер");
        } else {
            setPhase(Phase.SCAN_SHULKERS, "Сканируем дальше");
        }
    }

    private void storeItems() {
        BlockPos chest = findStorageChest();
        if (chest == null) {
            setPhase(Phase.DONE, "Сундук склада не найден, завершаем");
            return;
        }
        if (distanceTo(chest) > 4.5) {
            if (!BaritoneHelper.isPathing() && phaseElapsed(2000)) {
                BaritoneHelper.goTo(chest, 2);
            }
            return;
        }
        BaritoneHelper.stop();
        openChest(chest);
        waitTicks = 5;
        setPhase(Phase.DONE, "Складываем и завершаем");
    }

    private void done() {
        if (mc.currentScreen instanceof GenericContainerScreen) {
            if (phaseElapsed(2000)) {
                GenericContainerScreen screen = (GenericContainerScreen) mc.currentScreen;
                GenericContainerScreenHandler handler = screen.getScreenHandler();
                Inventory inv = handler.getInventory();
                int containerSize = inv.size();
                for (int i = containerSize; i < handler.slots.size(); i++) {
                    Slot slot = handler.getSlot(i);
                    if (slot.hasStack() && !slot.getStack().isEmpty()) {
                        ItemStack stack = slot.getStack();
                        if (!stack.isOf(Items.GOLDEN_PICKAXE) && !stack.getName().getString().toLowerCase().contains("инвиз")) {
                            mc.interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, mc.player);
                            waitTicks = 2;
                            return;
                        }
                    }
                }
                closeScreen();
            }
            return;
        }
        setState(false);
    }

    private BlockPos findChestBySign(String signText) {
        if (mc.world == null || mc.player == null) return null;
        String target = signText.toLowerCase().trim();
        int radius = stashScanRadius.get().intValue();
        BlockPos playerPos = mc.player.getBlockPos();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.getBlock() == net.minecraft.block.Blocks.CHEST
                            || state.getBlock() == net.minecraft.block.Blocks.TRAPPED_CHEST
                            || state.getBlock() == net.minecraft.block.Blocks.BARREL) {
                        for (Direction dir : Direction.values()) {
                            BlockPos signPos = pos.offset(dir);
                            BlockEntity be = mc.world.getBlockEntity(signPos);
                            if (be instanceof SignBlockEntity sign) {
                                StringBuilder sb = new StringBuilder();
                                for (int l = 0; l < 4; l++) {
                                    sb.append(" ").append(sign.getFrontText().getMessage(l, false).getString().toLowerCase());
                                }
                                if (sb.toString().contains(target)) {
                                    return pos;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private BlockPos findStorageChest() {
        return findChestBySign("склад");
    }

    private BlockPos findNearestShulker() {
        if (mc.world == null || mc.player == null) return null;
        int radius = 48;
        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -10; y <= 10; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.getBlock() == net.minecraft.block.Blocks.SHULKER_BOX
                            || state.getBlock() == net.minecraft.block.Blocks.WHITE_SHULKER_BOX
                            || state.getBlock() == net.minecraft.block.Blocks.BLACK_SHULKER_BOX
                            || state.getBlock() == net.minecraft.block.Blocks.BLUE_SHULKER_BOX
                            || state.getBlock() == net.minecraft.block.Blocks.RED_SHULKER_BOX
                            || state.getBlock() == net.minecraft.block.Blocks.GREEN_SHULKER_BOX) {
                        double dist = mc.player.getEntityPos().distanceTo(net.minecraft.util.math.Vec3d.ofCenter(pos));
                        if (dist < nearestDist) {
                            nearestDist = dist;
                            nearest = pos;
                        }
                    }
                }
            }
        }
        return nearest;
    }

    private int findItemInHotbar(net.minecraft.item.Item item) {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(item)) return i;
        }
        return -1;
    }

    private int findPickaxeInHotbar() {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getName().getString().toLowerCase().contains("кирка") && stack.getItem().toString().contains("pickaxe")) {
                return i;
            }
        }
        return -1;
    }

    private boolean isInventoryFull() {
        if (mc.player == null) return false;
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).isEmpty()) return false;
        }
        return true;
    }

    private void openChest(BlockPos pos) {
        if (mc.player == null || mc.interactionManager == null) return;
        net.minecraft.util.math.Direction dir = net.minecraft.util.math.Direction.UP;
        net.minecraft.util.hit.BlockHitResult hit = new net.minecraft.util.hit.BlockHitResult(
                net.minecraft.util.math.Vec3d.ofCenter(pos), dir, pos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private void closeScreen() {
        if (mc.player != null && mc.currentScreen != null) {
            mc.player.closeHandledScreen();
        }
    }

    private double distanceTo(BlockPos pos) {
        if (mc.player == null) return Double.MAX_VALUE;
        return mc.player.getEntityPos().distanceTo(net.minecraft.util.math.Vec3d.ofCenter(pos));
    }

    public int getShulkersBroken() {
        return shulkersBroken;
    }

    public String getPhaseLabel() {
        return phase.label;
    }
}
