package tech.lightclient.modules.impl.player;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.player.MoveInputEvent;
import tech.lightclient.client.utils.baritone.BaritoneHelper;
import tech.lightclient.client.utils.baritone.BaritoneRotationHook;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.AnarchyUtil;
import tech.lightclient.client.utils.player.ServerUtil;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.StringSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Перенос AutoWarden из AutoWarden.java из Temp в API LightBeta.
 * Логика сохранена: склад -> расходники -> город вардена -> сундуки -> отход при агре.
 * Старый obfuscated API заменён на Yarn/API проекта, а недостающий WardenESP заменён
 * локальным сканированием известных сундуков и голограмм ArmorStand.
 */
@FunctionAdd(name = "AutoWarden", alias = "Auto Warden", category = Category.Player,
        description = "Автоматизирует фарм варденов на анархии через Baritone")
public final class AutoWarden extends Function {
    private static final long COMMAND_DELAY_MS = 850L;
    private static final long OPEN_RETRY_MS = 700L;
    private static final int WARDEN_RADIUS = 30;
    private static final int MAX_INVENTORY_SLOTS = 30;
    private static final Pattern ANARCHY_PATTERN = Pattern.compile("(?iu)(?:anarchy|анархия|an)\\s*[-:#№.]?\\s*(\\d{1,5})");
    private static final Pattern TIMER_MMSS = Pattern.compile("(\\d{1,2})\\s*[:.]\\s*(\\d{1,2})");
    private static final Pattern TIMER_NUMBER = Pattern.compile("(\\d{1,3})");

    private static final List<BlockPos> WARDEN_CHESTS = List.of(
            new BlockPos(1982, -49, 1960), new BlockPos(1988, -56, 1946),
            new BlockPos(1940, -56, 1960), new BlockPos(1948, -56, 1957),
            new BlockPos(1941, -50, 1960), new BlockPos(1957, -56, 2014),
            new BlockPos(2016, -55, 1946), new BlockPos(1958, -55, 1987),
            new BlockPos(1988, -54, 1975), new BlockPos(1960, -55, 1993),
            new BlockPos(1985, -55, 2039), new BlockPos(2049, -55, 1957),
            new BlockPos(2033, -56, 1972), new BlockPos(1942, -55, 2041),
            new BlockPos(1948, -49, 2041), new BlockPos(2055, -49, 1968),
            new BlockPos(1968, -56, 2050));

    private static final Set<Item> VALUABLES = Set.of(
            Items.NETHERITE_SCRAP, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS, Items.TRIPWIRE_HOOK,
            Items.ENDER_EYE, Items.PAPER, Items.SPLASH_POTION, Items.SUGAR,
            Items.IRON_NUGGET, Items.ELYTRA, Items.NETHERITE_SWORD, Items.POPPED_CHORUS_FRUIT,
            Items.EXPERIENCE_BOTTLE, Items.TOTEM_OF_UNDYING, Items.GOLDEN_APPLE,
            Items.PLAYER_HEAD, Items.PHANTOM_MEMBRANE, Items.TNT, Items.EMERALD_ORE,
            Items.BEACON, Items.NETHERITE_PICKAXE, Items.DRIED_KELP, Items.AMETHYST_SHARD,
            Items.NETHER_STAR, Items.ENCHANTED_GOLDEN_APPLE, Items.FEATHER, Items.GUNPOWDER,
            Items.VILLAGER_SPAWN_EGG, Items.REDSTONE_TORCH);

    private final BooleanSetting useSpeed = new BooleanSetting("Использовать скорость", false);
    private final BooleanSetting reportAttackers = new BooleanSetting("Репортить обидчиков", false);
    private final ModeSetting lootPriority = new ModeSetting("Приоритеты лута", "Средний", "Низкий", "Средний", "Высокий");
    private final StringSetting storageAnarchy = new StringSetting("Анархия склада", "308");
    private final StringSetting wardenAnarchy = new StringSetting("Анархия вардена", "110");
    private final StringSetting homeName = new StringSetting("Название home", "home");

    private final Map<BlockPos, Integer> chestAttempts = new HashMap<>();
    private final Map<BlockPos, Long> wardenSeenUntil = new HashMap<>();
    private final Set<BlockPos> checkedChests = new HashSet<>();
    private final List<BlockPos> discoveredChests = new ArrayList<>();
    private Phase phase = Phase.SAVE;
    private BlockPos targetChest;
    private BlockPos storageChest;
    private BlockPos supplyChest;
    private boolean localSupplySearch;
    private final Set<BlockPos> checkedSupplyChests = new HashSet<>();
    private long lastCommandAt;
    private long nextOpenAt;
    private long phaseStartedAt;
    private long lastScanAt;
    private boolean died;
    private String attacker;

    private enum Phase { SAVE, TAKE, COLLECTING, ESCAPE }

    public AutoWarden() {
        addSettings(useSpeed, reportAttackers, lootPriority, storageAnarchy, wardenAnarchy, homeName);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        reset();
        BaritoneHelper.applySettings(true, 0.0, false, false);
        BaritoneRotationHook.enable();
        chat("Shift + Пробел — быстрое выключение функции");
    }

    @Override
    public void onDisable() {
        closeScreen();
        BaritoneHelper.stop();
        BaritoneRotationHook.disable();
        BaritoneHelper.applySettings(false, 2.0);
        super.onDisable();
    }

    private void reset() {
        phase = Phase.COLLECTING;
        targetChest = storageChest = supplyChest = null;
        localSupplySearch = false;
        checkedSupplyChests.clear();
        chestAttempts.clear();
        checkedChests.clear();
        discoveredChests.clear();
        wardenSeenUntil.clear();
        phaseStartedAt = System.currentTimeMillis();
        lastCommandAt = 0L;
        nextOpenAt = 0L;
        died = false;
        attacker = null;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.currentScreen instanceof DeathScreen || mc.player.isDead()) {
            if (!died) { died = true; phase = Phase.ESCAPE; chat("Смерть обнаружена, восстанавливаю цикл"); }
            return;
        }
        if (died && !(mc.currentScreen instanceof DeathScreen)) {
            died = false;
            phase = Phase.SAVE;
            phaseStartedAt = System.currentTimeMillis();
        }
        scanWardens();
        if (attacker != null) {
            reportAttackerWhenReady();
            return;
        }
        if (isAggroed() || isInventoryFull()) phase = Phase.ESCAPE;

        int anarchy = currentAnarchy();
        if (anarchy < 0) {
            if (elapsed(3000)) sendCommand("an" + firstNumber(storageAnarchy.get(), 308));
            return;
        }
        switch (phase) {
            case SAVE -> saveLoot(anarchy);
            case TAKE -> takeSupplies(anarchy);
            case COLLECTING -> collect(anarchy);
            case ESCAPE -> escape(anarchy);
        }
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        if (!event.isReceive() || !(event.getPacket() instanceof GameMessageS2CPacket packet)) return;
        String text = packet.content().getString();
        if (text.contains("Вас убил") || text.toLowerCase(Locale.ROOT).contains("killed")) {
            int split = text.indexOf("Вас убил");
            if (split >= 0) {
                String name = text.substring(split + "Вас убил".length()).replaceAll("[,.:].*", "").trim();
                if (!name.isEmpty() && reportAttackers.get()) attacker = name;
            }
        }
    }

    @EventHandler
    public void onInput(MoveInputEvent event) {
        if (mc.player == null || phase != Phase.ESCAPE) return;
        if (!mc.player.isOnGround() && !mc.player.isSwimming()) {
            event.forward(false);
            event.backward(false);
            event.left(false);
            event.right(false);
        }
    }

    private void saveLoot(int anarchy) {
        if (anarchy != firstNumber(storageAnarchy.get(), 308)) {
            sendCommand("an" + firstNumber(storageAnarchy.get(), 308));
            return;
        }
        if (storageChest == null) findStorage();
        if (storageChest == null) { action("Ищу сундук хранилища"); return; }
        if (!near(storageChest, 4.0)) { goTo(storageChest, 2); return; }
        if (!(mc.currentScreen instanceof GenericContainerScreen screen)) { open(storageChest); return; }
        deposit(screen.getScreenHandler());
        if (!hasValuableLoot()) { closeScreen(); phase = Phase.TAKE; phaseStartedAt = System.currentTimeMillis(); }
    }

    private void takeSupplies(int anarchy) {
        if (!localSupplySearch && anarchy != firstNumber(storageAnarchy.get(), 308)) {
            sendCommand("an" + firstNumber(storageAnarchy.get(), 308));
            return;
        }
        if (supplyChest == null) {
            supplyChest = localSupplySearch ? findNearbySupplyChest() : null;
            if (supplyChest == null && !localSupplySearch) findStorage();
        }
        if (supplyChest == null) {
            localSupplySearch = false;
            action("Ищу сундук расходников");
            return;
        }
        if (!near(supplyChest, 4.0)) { goTo(supplyChest, 2); return; }
        if (!(mc.currentScreen instanceof GenericContainerScreen screen)) { open(supplyChest); return; }
        takeSuppliesFrom(screen.getScreenHandler());
        boolean ready = hasRequiredSupplies();
        BlockPos checked = supplyChest;
        closeScreen();
        supplyChest = null;
        if (checked != null) checkedSupplyChests.add(checked);
        if (ready) {
            localSupplySearch = false;
            phase = Phase.COLLECTING;
            phaseStartedAt = System.currentTimeMillis();
        } else if (localSupplySearch) {
            supplyChest = findNearbySupplyChest();
        } else {
            phase = Phase.TAKE;
        }
    }

    private void collect(int anarchy) {
        int target = firstNumber(wardenAnarchy.get(), 110);
        if (anarchy != target) { sendCommand("an" + target); return; }
        if (!inWardenCity()) { sendCommand("home " + cleanHome()); return; }
        if (!hasRequiredSupplies()) {
            BlockPos nearby = findNearbySupplyChest();
            if (nearby != null) {
                supplyChest = nearby;
                localSupplySearch = true;
                phase = Phase.TAKE;
                return;
            }
            localSupplySearch = false;
            phase = Phase.TAKE;
            return;
        }
        if (isPvpActive() && !nearSafeChest()) { action("Жду окончание PVP"); return; }
        if (targetChest == null || checkedChests.contains(targetChest) || !isValidChest(targetChest)) targetChest = chooseChest();
        if (targetChest == null) { action("Ищу сундук вардена"); return; }
        if (!near(targetChest, 4.0)) { goTo(targetChest, 2); return; }
        if (!(mc.currentScreen instanceof GenericContainerScreen screen)) { open(targetChest); return; }
        loot(screen.getScreenHandler());
        checkedChests.add(targetChest);
        chestAttempts.put(targetChest, chestAttempts.getOrDefault(targetChest, 0) + 1);
        closeScreen();
        targetChest = null;
    }

    private void escape(int anarchy) {
        closeScreen();
        BaritoneHelper.stop();
        int storage = firstNumber(storageAnarchy.get(), 308);
        if (anarchy != storage) { sendCommand("an" + storage); return; }
        if (storageChest == null) findStorage();
        if (storageChest != null && !near(storageChest, 4.0)) { goTo(storageChest, 2); return; }
        if (storageChest != null && hasValuableLoot()) { open(storageChest); return; }
        phase = Phase.SAVE;
        phaseStartedAt = System.currentTimeMillis();
    }

    private void scanWardens() {
        long now = System.currentTimeMillis();
        if (now - lastScanAt < 500L) return;
        lastScanAt = now;
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof WardenEntity warden && warden.squaredDistanceTo(mc.player) < 900.0) {
                wardenSeenUntil.put(warden.getBlockPos(), now + 5000L);
            }
        }
        wardenSeenUntil.entrySet().removeIf(e -> e.getValue() < now);
        discoveredChests.clear();
        discoveredChests.addAll(WARDEN_CHESTS);
        for (ArmorStandEntity stand : mc.world.getEntitiesByClass(ArmorStandEntity.class, new Box(mc.player.getBlockPos()).expand(80), e -> e.hasCustomName())) {
            if (parseTimer(stand.getCustomName()) >= 0) {
                BlockPos chest = findChestNear(stand.getBlockPos());
                if (chest != null && !discoveredChests.contains(chest)) discoveredChests.add(chest);
            }
        }
    }

    private BlockPos chooseChest() {
        BlockPos best = null;
        double bestScore = Double.MAX_VALUE;
        for (BlockPos pos : discoveredChests) {
            BlockPos chest = resolveChest(pos);
            if (chest == null || checkedChests.contains(chest) || !isValidChest(chest)) continue;
            if (chestAttempts.getOrDefault(chest, 0) >= 3) continue;
            double score = mc.player.squaredDistanceTo(chest.toCenterPos());
            int timer = parseTimerNear(chest);
            if (timer >= 0) score += Math.max(0, timer) * (lootPriority.is("Низкий") ? 1.5 : lootPriority.is("Высокий") ? 0.4 : 1.0);
            if (isDangerous(chest)) score += 10000.0;
            if (score < bestScore) { bestScore = score; best = chest; }
        }
        return best;
    }

    private void findStorage() {
        if (mc.player == null || mc.world == null) return;
        int radius = 8;
        BlockPos origin = mc.player.getBlockPos();
        BlockPos normal = null;
        for (int x = -radius; x <= radius; x++) for (int y = -4; y <= 4; y++) for (int z = -radius; z <= radius; z++) {
            BlockPos pos = origin.add(x, y, z);
            if (!isChest(pos)) continue;
            if (hasSignNear(pos)) { storageChest = pos; }
            else if (normal == null) normal = pos;
        }
        if (storageChest == null) storageChest = normal;
        if (supplyChest == null && normal != null && !normal.equals(storageChest)) supplyChest = normal;
    }

    /** Ищет ближайший ещё не проверенный сундук с расходниками рядом с игроком. */
    private BlockPos findNearbySupplyChest() {
        if (mc.player == null || mc.world == null) return null;
        BlockPos origin = mc.player.getBlockPos();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.iterateOutwards(origin, 16, 6, 16)) {
            if (checkedSupplyChests.contains(pos) || !isChest(pos)) continue;
            double distance = mc.player.squaredDistanceTo(pos.toCenterPos());
            if (distance < bestDistance) {
                bestDistance = distance;
                best = pos.toImmutable();
            }
        }
        return best;
    }

    private void deposit(GenericContainerScreenHandler handler) {
        for (int i = handler.getInventory().size(); i < handler.slots.size(); i++) {
            Slot slot = handler.getSlot(i);
            if (!slot.getStack().isEmpty() && isValuable(slot.getStack())) {
                mc.interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, mc.player);
                return;
            }
        }
    }

    private void takeSuppliesFrom(GenericContainerScreenHandler handler) {
        for (int i = 0; i < handler.getInventory().size(); i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (stack.isOf(Items.GOLDEN_CARROT) || isUtilityPotion(stack)) {
                mc.interactionManager.clickSlot(handler.syncId, handler.getSlot(i).id, 0, SlotActionType.QUICK_MOVE, mc.player);
            }
        }
    }

    private void loot(GenericContainerScreenHandler handler) {
        for (int i = 0; i < handler.getInventory().size(); i++) {
            ItemStack stack = handler.getSlot(i).getStack();
            if (isValuable(stack)) mc.interactionManager.clickSlot(handler.syncId, handler.getSlot(i).id, 0, SlotActionType.QUICK_MOVE, mc.player);
        }
    }

    private boolean hasValuableLoot() {
        for (ItemStack stack : mc.player.getInventory().getMainStacks()) if (isValuable(stack)) return true;
        return false;
    }

    private boolean hasRequiredSupplies() {
        int carrots = 0;
        boolean invis = mc.player.hasStatusEffect(StatusEffects.INVISIBILITY);
        boolean speed = mc.player.hasStatusEffect(StatusEffects.SPEED);
        for (ItemStack stack : mc.player.getInventory().getMainStacks()) {
            if (stack.isOf(Items.GOLDEN_CARROT)) carrots += stack.getCount();
            if (hasPotionEffect(stack, StatusEffects.INVISIBILITY)) invis = true;
            if (hasPotionEffect(stack, StatusEffects.SPEED)) speed = true;
        }
        return carrots >= 3 && invis && (!useSpeed.get() || speed);
    }

    private boolean isValuable(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return VALUABLES.contains(stack.getItem()) || stack.getName().getString().toLowerCase(Locale.ROOT).contains("warden");
    }

    private boolean isUtilityPotion(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof PotionItem) || stack.getItem() instanceof SplashPotionItem) return false;
        return hasPotionEffect(stack, StatusEffects.INVISIBILITY)
                || (useSpeed.get() && hasPotionEffect(stack, StatusEffects.SPEED))
                || stack.getName().getString().toLowerCase(Locale.ROOT).contains("невид")
                || stack.getName().getString().toLowerCase(Locale.ROOT).contains("invis");
    }

    private boolean hasPotionEffect(ItemStack stack, net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect> effect) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof PotionItem)
                || stack.getItem() instanceof SplashPotionItem) return false;
        PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (contents == null) return false;
        for (StatusEffectInstance instance : contents.getEffects()) {
            if (instance.getEffectType().equals(effect)) return true;
        }
        return false;
    }

    private boolean isAggroed() {
        if (wardenSeenUntil.isEmpty()) return false;
        for (Entity entity : mc.world.getEntities()) if (entity instanceof WardenEntity warden && warden.squaredDistanceTo(mc.player) < 900.0) {
            Vec3d toPlayer = mc.player.getEntityPos().subtract(warden.getEntityPos());
            double angle = Math.abs(((warden.getYaw() - (float) Math.toDegrees(Math.atan2(-toPlayer.x, toPlayer.z)) + 540.0) % 360.0) - 180.0);
            if (warden.squaredDistanceTo(mc.player) < 16.0 || angle < 10.0) return true;
        }
        return false;
    }

    private boolean isPvpActive() { return false; }
    private boolean nearSafeChest() { return targetChest != null && near(targetChest, 8.0); }

    private boolean isValidChest(BlockPos pos) { return pos != null && isChest(pos) && !isDangerous(pos); }
    private boolean isDangerous(BlockPos pos) {
        for (Map.Entry<BlockPos, Long> entry : wardenSeenUntil.entrySet()) if (entry.getKey().getSquaredDistance(pos) < 25.0) return true;
        for (PlayerEntity player : mc.world.getPlayers()) if (player != mc.player && player.squaredDistanceTo(pos.toCenterPos()) < 400.0) return true;
        return false;
    }
    private boolean isInventoryFull() { return mc.player.getInventory().getMainStacks().stream().filter(s -> !s.isEmpty()).count() >= MAX_INVENTORY_SLOTS; }
    private boolean inWardenCity() { return Math.abs(mc.player.getX()) > 1900 && Math.abs(mc.player.getZ()) > 1900 && mc.player.getY() < -15; }
    private boolean isChest(BlockPos pos) { BlockState state = mc.world.getBlockState(pos); return state.isOf(net.minecraft.block.Blocks.CHEST) || state.isOf(net.minecraft.block.Blocks.TRAPPED_CHEST); }

    private boolean hasSignNear(BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos p = pos.offset(direction);
            BlockEntity entity = mc.world.getBlockEntity(p);
            if (entity instanceof net.minecraft.block.entity.SignBlockEntity sign) {
                String text = (sign.getFrontText().getMessage(0, false).getString() + sign.getFrontText().getMessage(1, false).getString()).toLowerCase(Locale.ROOT);
                if (text.contains("склад") || text.contains("stash") || text.contains("хран")) return true;
            }
        }
        return false;
    }

    private BlockPos resolveChest(BlockPos pos) { if (isChest(pos)) return pos; return findChestNear(pos); }
    private BlockPos findChestNear(BlockPos pos) {
        for (int dx = -2; dx <= 2; dx++) for (int dy = -2; dy <= 2; dy++) for (int dz = -2; dz <= 2; dz++) {
            BlockPos candidate = pos.add(dx, dy, dz); if (isChest(candidate)) return candidate;
        }
        return null;
    }

    private int parseTimerNear(BlockPos pos) {
        int best = -1;
        for (ArmorStandEntity stand : mc.world.getEntitiesByClass(ArmorStandEntity.class, new Box(pos).expand(3), e -> e.hasCustomName())) {
            int timer = parseTimer(stand.getCustomName()); if (timer >= 0 && (best < 0 || timer < best)) best = timer;
        }
        return best;
    }
    private int parseTimer(Text text) {
        if (text == null) return -1;
        String value = text.getString().replaceAll("§.", "");
        Matcher matcher = TIMER_MMSS.matcher(value);
        if (matcher.find()) return Integer.parseInt(matcher.group(1)) * 60 + Math.min(59, Integer.parseInt(matcher.group(2)));
        matcher = TIMER_NUMBER.matcher(value);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
    }

    private void goTo(BlockPos pos, int range) { if (pos != null && System.currentTimeMillis() - lastScanAt > 150L) BaritoneHelper.goTo(pos, range); }
    private void open(BlockPos pos) {
        if (pos == null || System.currentTimeMillis() < nextOpenAt) return;
        nextOpenAt = System.currentTimeMillis() + OPEN_RETRY_MS;
        Vec3d eye = mc.player.getEyePos(); Vec3d center = pos.toCenterPos();
        BlockHitResult hit = mc.world.raycast(new RaycastContext(eye, center, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
        if (hit.getType() != HitResult.Type.BLOCK || !hit.getBlockPos().equals(pos)) hit = new BlockHitResult(center, Direction.UP, pos, false);
        BaritoneHelper.stop(); mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit); mc.player.swingHand(Hand.MAIN_HAND, false);
    }
    private void closeScreen() { if (mc.player != null && mc.currentScreen != null) mc.player.closeHandledScreen(); }

    private void reportAttackerWhenReady() { if (mc.player.age % 10 == 0) { sendCommand("report " + attacker + " чит"); attacker = null; } }
    private void sendCommand(String command) { if (mc.player == null || command == null || System.currentTimeMillis() - lastCommandAt < COMMAND_DELAY_MS) return; lastCommandAt = System.currentTimeMillis(); mc.player.networkHandler.sendChatCommand(command.startsWith("/") ? command.substring(1) : command); }
    private int currentAnarchy() { if (mc.world == null) return -1; String value = mc.world.getScoreboard().getObjectiveForSlot(net.minecraft.scoreboard.ScoreboardDisplaySlot.SIDEBAR) == null ? "" : mc.world.getScoreboard().getObjectiveForSlot(net.minecraft.scoreboard.ScoreboardDisplaySlot.SIDEBAR).getDisplayName().getString(); Matcher matcher = ANARCHY_PATTERN.matcher(value); return matcher.find() ? Integer.parseInt(matcher.group(1)) : AnarchyUtil.getCurrentAnarchy(); }
    private int firstNumber(String value, int fallback) { try { return Integer.parseInt(value.replaceAll("[^0-9]", "")); } catch (Exception ignored) { return fallback; } }
    private String cleanHome() { return homeName.get().replaceAll("[^a-zA-Z0-9_А-Яа-я-]", ""); }
    private boolean near(BlockPos pos, double distance) { return pos != null && mc.player.squaredDistanceTo(pos.toCenterPos()) <= distance * distance; }
    private boolean elapsed(long milliseconds) { return System.currentTimeMillis() - phaseStartedAt >= milliseconds; }
    private void action(String message) { if (mc.player.age % 40 == 0) chat(message); }
    private void chat(String message) { Function.sendMessage("§7[AutoWarden] §f" + message); }
}
