package tech.lightclient.modules.impl.player;

import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.baritone.BaritoneHelper;
import tech.lightclient.client.utils.baritone.BaritoneRotationHook;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.rotation.RotationTask;
import tech.lightclient.client.utils.render.color.basic.ColorUtils;
import tech.lightclient.client.utils.render.main.world.WorldGeometryEmitter;
import tech.lightclient.client.utils.render.main.world.WorldRenderLayers;
import tech.lightclient.client.utils.render.main.world.WorldRenderer;
import tech.lightclient.mixins.accessors.BossBarHudAccessor;
import tech.lightclient.mixins.accessors.GameRendererAccessor;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import tech.lightclient.modules.api.settings.impl.StringSetting;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Деобфусцированный и адаптированный перенос модуля AutoEnd (exploit.lon).
 * <p>
 * Полный цикл:
 * FARMING (ломает кувшины / чистит brushable, подбирает лут)
 * -> при заполнении рюкзака или разбитии N кувшинов -> PVP_WAIT (ждёт снятия
 * pvp-таймера через boss-bar) -> HOME (телепорт /home) -> FIND/WALK/OPEN/DEPOSIT
 * (складирует лут в сундук) -> FIND/WALK/OPEN/TAKE_SUPPLY (пополняет припасы из
 * сундука с табличкой) -> FIND/WALK/ENTER_PORTAL (возвращается в End) -> FARMING.
 * <p>
 * Числовые/булевы константы и строки, спрятанные в оригинале за вызовами
 * обфускатора (mff37983..., maaa82bf...), восстановлены в читаемый вид.
 */
@FunctionAdd(name = "AutoEnd", alias = "Auto End", category = Category.Player,
        description = "Ломает кувшины, чистит археологические блоки, подбирает лут и обслуживает себя")
public class AutoEnd extends Function {

    // --- Настройки ---
    private final SliderSetting breakReach = new SliderSetting("Дальность работы", 4.25f, 3f, 5.5f, 0.25f);
    private final SliderSetting scanRadius = new SliderSetting("Радиус поиска (чанки)", 6f, 1f, 8f, 1f);
    private final BooleanSetting brushBlocks = new BooleanSetting("Чистить кистью", true);
    private final BooleanSetting collectLoot = new BooleanSetting("Подбирать лут", true);
    private final BooleanSetting renderTarget = new BooleanSetting("Отрисовка цели", true);

    private final SliderSetting potsBeforeHome = new SliderSetting("Кувшинов до возврата", 5f, 1f, 64f, 1f);
    private final BooleanSetting autoSupplies = new BooleanSetting("Авто-припасы", true);
    private final StringSetting supplySign = new StringSetting("Текст таблички припасов", "припасы", "", false);
    private final StringSetting homeCommand = new StringSetting("Команда домой", "home", "", false);
    private final BooleanSetting takeFood = new BooleanSetting("Брать еду", true);
    private final SliderSetting foodAmount = new SliderSetting("Кол-во еды", 16f, 1f, 64f, 1f);
    private final BooleanSetting takeInvisibility = new BooleanSetting("Брать невидимость", true);
    private final SliderSetting invisibilityAmount = new SliderSetting("Кол-во невидимости", 3f, 1f, 16f, 1f);
    private final BooleanSetting takeSpeed = new BooleanSetting("Брать скорость", true);
    private final SliderSetting speedAmount = new SliderSetting("Кол-во скорости", 3f, 1f, 16f, 1f);
    private final BooleanSetting autoConsume = new BooleanSetting("Авто-потребление", true);
    private final BooleanSetting returnToPortal = new BooleanSetting("Возврат через портал", true);

    // --- Константы (деобфусцированы) ---
    private static final int RESCAN_DELAY_TICKS = 4;
    private static final int REPATH_DELAY_TICKS = 8;
    private static final int AIM_WARMUP_TICKS = 2;
    private static final int LOOT_TIMEOUT_TICKS = 120;
    private static final int LOOT_EMPTY_CONFIRM_TICKS = 8;
    private static final double LOOT_SEARCH_RADIUS = 12.0;
    private static final int UNREACHABLE_TARGET_TICKS = 1200;
    private static final int BRUSH_USE_COOLDOWN_TICKS = 4;

    private static final int ACTION_DELAY_TICKS = 3;
    private static final int HOME_RETRY_TICKS = 120;
    private static final int PVP_CLEAR_CONFIRM_TICKS = 3;
    private static final int SUPPLY_CHEST_SEARCH_RADIUS = 40;
    private static final int HOME_CHEST_SEARCH_RADIUS = 8;
    private static final int PORTAL_SEARCH_RADIUS = 48;
    private static final int CONSUMABLE_USE_TIMEOUT_TICKS = 60;
    private static final double CONTAINER_OPEN_DISTANCE_SQ = 20.25; // 4.5^2
    private static final String ROTATION_TASK_NAME = "auto_end";

    // --- Состояние цели (фарм) ---
    private BlockPos targetBlock;
    private boolean targetBrushable;
    private boolean breakingStarted;
    private boolean brushingStarted;
    private boolean pathRequested;

    private int scanDelay;
    private int repathDelay;
    private int aimTicks;
    private int brushUseCooldown;

    // --- Состояние лута ---
    private BlockPos lootOrigin;
    private int lootTicks;
    private int lootEmptyTicks;

    // --- Инструмент (кисть) ---
    private int previousSelectedSlot = -1;
    private int brushHotbarSlot = -1;

    // --- Недостижимые цели ---
    private final Map<BlockPos, Integer> temporarilySkippedTargets = new HashMap<>();

    // --- Машина состояний цикла ---
    private CycleState cycleState = CycleState.FARMING;
    private int cycleTicks;
    private int actionDelay;
    private int brokenPots;
    private boolean returnHomePending;

    private BlockPos homeChest;
    private BlockPos supplyChest;
    private BlockPos portalTarget;
    private int depositSlot;
    private boolean containerOpened;

    private final Set<BlockPos> exhaustedHomeChests = new HashSet<>();
    private final Set<BlockPos> exhaustedSupplyChests = new HashSet<>();
    private final EnumSet<SupplyKind> unavailableSupplyKinds = EnumSet.noneOf(SupplyKind.class);

    private Vec3d homeCommandPos;
    private Object homeCommandWorld;
    private volatile boolean homeTeleportConfirmed;
    private int pvpClearTicks;

    // --- Потребление ---
    private SupplyKind activeConsumable;
    private int consumablePreviousSlot = -1;
    private int consumableHotbarSlot = -1;
    private int consumableTicks;

    public AutoEnd() {
        addSettings(breakReach, scanRadius, brushBlocks, collectLoot, renderTarget,
                potsBeforeHome, autoSupplies, supplySign, homeCommand,
                takeFood, foodAmount, takeInvisibility, invisibilityAmount,
                takeSpeed, speedAmount, autoConsume, returnToPortal);
    }

    @Override
    public void onEnable() {
        if (!BaritoneHelper.isAvailable()) {
            this.setState(false);
            Function.sendMessage("§c[AutoEnd] §fBaritone §cне установлен!");
            return;
        }
        resetAll();
        // Baritone must own both route calculation and rotation while walking.
        // freeLook=true makes it press forward relative to the current camera
        // yaw, which bypasses corners and sends the player into walls.
        BaritoneHelper.applySettings(true, 0.0, false, false);
        BaritoneRotationHook.enable();
        Function.sendMessage("§a[AutoEnd] §fЗапущен!");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        stopBrush();
        cancelConsumable();
        BaritoneHelper.setPaused(false);
        BaritoneHelper.stop();
        BaritoneRotationHook.disable();
        BaritoneHelper.applySettings(false, 2.0);
        resetAll();
        super.onDisable();
    }

    private void resetAll() {
        clearRotation();
        targetBlock = null;
        targetBrushable = false;
        breakingStarted = false;
        brushingStarted = false;
        pathRequested = false;
        scanDelay = 0;
        repathDelay = 0;
        aimTicks = 0;
        brushUseCooldown = 0;
        lootOrigin = null;
        lootTicks = 0;
        lootEmptyTicks = 0;
        previousSelectedSlot = -1;
        brushHotbarSlot = -1;
        temporarilySkippedTargets.clear();

        cycleState = CycleState.FARMING;
        cycleTicks = 0;
        actionDelay = 0;
        brokenPots = 0;
        returnHomePending = false;
        homeChest = null;
        supplyChest = null;
        portalTarget = null;
        depositSlot = 0;
        containerOpened = false;
        exhaustedHomeChests.clear();
        exhaustedSupplyChests.clear();
        unavailableSupplyKinds.clear();
        homeCommandPos = null;
        homeCommandWorld = null;
        homeTeleportConfirmed = false;
        pvpClearTicks = 0;
        cancelConsumableState();
    }

    // ===================== ГЛАВНЫЙ ДИСПЕТЧЕР =====================

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (nullCheck() || mc.interactionManager == null) return;
        if (!BaritoneHelper.isAvailable()) {
            this.setState(false);
            return;
        }

        // смерть -> ждём респавна
        if (mc.currentScreen instanceof DeathScreen || mc.player.isDead() || mc.player.getHealth() <= 0.0f) {
            BaritoneHelper.stop();
            return;
        }

        tickTimers();
        tickSkippedTargets();

        // Авто-потребление (еда/зелья) — идёт поверх любого состояния, кроме открытых экранов
        if (autoConsume.get() && mc.currentScreen == null && tickConsumable()) {
            return;
        }

        switch (cycleState) {
            case FARMING -> tickFarming();
            case PVP_WAIT -> tickPvpWait();
            case HOME_WAIT -> tickHomeWait();
            case FIND_CHEST -> tickFindChest();
            case WALK_CHEST -> tickWalkChest();
            case OPEN_CHEST -> tickOpenChest();
            case DEPOSIT -> tickDeposit();
            case FIND_SUPPLY -> tickFindSupply();
            case WALK_SUPPLY -> tickWalkSupply();
            case OPEN_SUPPLY -> tickOpenSupply();
            case TAKE_SUPPLY -> tickTakeSupply();
            case FIND_PORTAL -> tickFindPortal();
            case WALK_PORTAL -> tickWalkPortal();
            case ENTER_PORTAL -> tickEnterPortal();
        }
    }

    private void tickTimers() {
        if (scanDelay > 0) scanDelay--;
        if (repathDelay > 0) repathDelay--;
        if (brushUseCooldown > 0) brushUseCooldown--;
        if (actionDelay > 0) actionDelay--;
        if (cycleTicks < Integer.MAX_VALUE) cycleTicks++;
    }

    private void enterState(CycleState state) {
        cycleState = state;
        cycleTicks = 0;
        actionDelay = 0;
        scanDelay = 0;
        pathRequested = false;
        repathDelay = 0;
    }

    // ===================== FARMING =====================

    private void tickFarming() {

        // 1. Подбор лута — приоритет
        if (lootOrigin != null) {
            tickLoot();
            return;
        }

        // 2. Цель "испарилась"?
        if (targetBlock != null && targetBrushable && brushingStarted && !isBrushable(targetBlock)) {
            BlockPos pos = targetBlock.toImmutable();
            stopBrush();
            countPot(false);
            beginLoot(pos);
            return;
        }
        if (targetBlock != null && !targetBrushable && !isDecoratedPot(targetBlock)) {
            BlockPos pos = targetBlock.toImmutable();
            resetTarget(false);
            countPot(true);
            beginLoot(pos);
            return;
        }

        // 3. Пора домой? (набили кувшинов или рюкзак полон)
        if (targetBlock == null && lootOrigin == null && (returnHomePending || isInventoryFull())) {
            beginReturnHome();
            return;
        }

        // 4. Нет цели -> сканируем
        if (targetBlock == null) {
            if (scanDelay > 0) return;
            scanDelay = RESCAN_DELAY_TICKS;
            TargetSelection selection = scanForNearestTarget();
            if (selection == null) return;
            targetBlock = selection.pos();
            targetBrushable = selection.brushable();
            breakingStarted = false;
            brushingStarted = false;
            aimTicks = 0;
            requestPath();
            return;
        }

        // 5. Далеко -> идём
        if (!inReach(targetBlock)) {
            clearRotation();
            if (!pathRequested || (!BaritoneHelper.isPathing() && repathDelay <= 0)) {
                requestPath();
            }
            return;
        }

        // 6. Дошли -> ломаем/чистим
        BaritoneHelper.stop();
        pathRequested = false;
        updateRotationTo(Vec3d.ofCenter(targetBlock));
        if (targetBrushable) tickBrush();
        else tickBreak();
    }

    private void countPot(boolean wasPot) {
        if (wasPot) {
            brokenPots++;
            if (brokenPots >= Math.max(1, Math.round((float) potsBeforeHome.get()))) {
                returnHomePending = true;
            }
        }
    }

    private void beginReturnHome() {
        BaritoneHelper.stop();
        pathRequested = false;
        homeTeleportConfirmed = false;
        homeCommandPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        homeCommandWorld = mc.world;
        sendCommand(homeCommand.get());
        enterState(CycleState.HOME_WAIT);
    }

    // ===================== ЛОМАНИЕ / ЧИСТКА =====================

    private void tickBreak() {
        breakingStarted = false;
        aimTicks++;
        if (aimTicks < AIM_WARMUP_TICKS) return;
        HitResult crosshair = mc.crosshairTarget;
        if (!(crosshair instanceof BlockHitResult hit) || !hit.getBlockPos().equals(targetBlock)) {
            aimTicks = 0;
            return;
        }
        breakingStarted = true;
        mc.interactionManager.updateBlockBreakingProgress(targetBlock, hit.getSide());
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private void tickBrush() {
        if (!equipBrush()) return;
        HitResult crosshair = mc.crosshairTarget;
        if (!(crosshair instanceof BlockHitResult hit) || !hit.getBlockPos().equals(targetBlock)) {
            brushingStarted = false;
            return;
        }
        brushingStarted = true;
        if (!(mc.player.isUsingItem() && mc.player.getActiveItem().isOf(Items.BRUSH)) && brushUseCooldown <= 0) {
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
            brushUseCooldown = BRUSH_USE_COOLDOWN_TICKS;
        }
        if (!isBrushable(targetBlock)) {
            BlockPos pos = targetBlock.toImmutable();
            stopBrush();
            countPot(false);
            beginLoot(pos);
        }
    }

    private boolean equipBrush() {
        if (mc.player.getMainHandStack().isOf(Items.BRUSH)) return true;
        int slot = findHotbarItem(Items.BRUSH);
        if (slot < 0) return false;
        if (previousSelectedSlot == -1) previousSelectedSlot = mc.player.getInventory().getSelectedSlot();
        brushHotbarSlot = slot;
        mc.player.getInventory().setSelectedSlot(slot);
        brushUseCooldown = BRUSH_USE_COOLDOWN_TICKS;
        return true;
    }

    private void stopBrush() {
        if (mc.player != null && mc.interactionManager != null && mc.player.isUsingItem()) {
            mc.interactionManager.stopUsingItem(mc.player);
        }
        if (mc.player != null && previousSelectedSlot >= 0) {
            mc.player.getInventory().setSelectedSlot(previousSelectedSlot);
        }
        brushingStarted = false;
        brushUseCooldown = 0;
        previousSelectedSlot = -1;
        brushHotbarSlot = -1;
    }

    private int findHotbarItem(Item item) {
        for (int i = 0; i < 9; i++) if (mc.player.getInventory().getStack(i).isOf(item)) return i;
        return -1;
    }

    // ===================== ЛУТ =====================

    private void beginLoot(BlockPos origin) {
        BaritoneHelper.stop();
        pathRequested = false;
        targetBlock = null;
        targetBrushable = false;
        if (!collectLoot.get()) {
            lootOrigin = null;
            scanDelay = RESCAN_DELAY_TICKS;
            return;
        }
        lootOrigin = origin.toImmutable();
        lootTicks = 0;
        lootEmptyTicks = 0;
    }

    private void tickLoot() {
        lootTicks++;
        ItemEntity item = findNearestLootItem();
        if (item == null) {
            lootEmptyTicks++;
            if (lootEmptyTicks >= LOOT_EMPTY_CONFIRM_TICKS || lootTicks >= LOOT_TIMEOUT_TICKS) clearLoot();
            return;
        }
        lootEmptyTicks = 0;
        if (isWithinPickup(item)) return;
        BlockPos lootPos = BlockPos.ofFloored(item.getX(), item.getY(), item.getZ());
        if (!pathRequested || (!BaritoneHelper.isPathing() && repathDelay <= 0)) {
            BaritoneHelper.goTo(lootPos, 1);
            pathRequested = true;
            repathDelay = REPATH_DELAY_TICKS;
        }
    }

    private ItemEntity findNearestLootItem() {
        if (lootOrigin == null || mc.world == null) return null;
        Box box = new Box(lootOrigin).expand(LOOT_SEARCH_RADIUS);
        ItemEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof ItemEntity item)) continue;
            if (!item.isAlive() || item.getStack().isEmpty()) continue;
            if (!box.intersects(item.getBoundingBox())) continue;
            double d = mc.player.squaredDistanceTo(item);
            if (d < bestDist) {
                bestDist = d;
                best = item;
            }
        }
        return best;
    }

    private boolean isWithinPickup(ItemEntity item) {
        double dx = item.getX() - mc.player.getX();
        double dz = item.getZ() - mc.player.getZ();
        return dx * dx + dz * dz <= 1.5 && Math.abs(item.getY() - mc.player.getY()) <= 1.5;
    }

    private void clearLoot() {
        lootOrigin = null;
        lootTicks = 0;
        lootEmptyTicks = 0;
        pathRequested = false;
        BaritoneHelper.stop();
        scanDelay = RESCAN_DELAY_TICKS;
    }

    // ===================== PVP_WAIT =====================

    private void tickPvpWait() {
        BaritoneHelper.stop();
        int seconds = pvpCooldownSeconds();
        if (seconds > 0) {
            pvpClearTicks = 0;
            return;
        }
        pvpClearTicks++;
        if (pvpClearTicks < PVP_CLEAR_CONFIRM_TICKS) return;
        // pvp снят -> телепорт домой
        homeTeleportConfirmed = false;
        homeCommandPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        homeCommandWorld = mc.world;
        sendCommand(homeCommand.get());
        enterState(CycleState.HOME_WAIT);
    }

    // ===================== HOME_WAIT =====================

    private void tickHomeWait() {
        BaritoneHelper.stop();
        boolean moved = homeCommandWorld != null && mc.world != homeCommandWorld;
        boolean teleported = homeTeleportConfirmed || moved
                || (homeCommandPos != null && playerPos().squaredDistanceTo(homeCommandPos) > 256.0);
        if (teleported) {
            homeTeleportConfirmed = false;
            enterState(CycleState.FIND_CHEST);
            return;
        }
        if (cycleTicks > 0 && cycleTicks % HOME_RETRY_TICKS == 0) {
            sendCommand(homeCommand.get());
        }
    }

    // ===================== СУНДУК: FIND / WALK / OPEN / DEPOSIT =====================

    private void tickFindChest() {
        if (scanDelay > 0) return;
        scanDelay = RESCAN_DELAY_TICKS;
        homeChest = findNearestChest(HOME_CHEST_SEARCH_RADIUS, exhaustedHomeChests);
        if (homeChest != null) {
            enterState(CycleState.WALK_CHEST);
        } else if (cycleTicks > 60) {
            // рядом сундука нет — пропускаем депозит, сразу к припасам
            exhaustedHomeChests.clear();
            enterState(autoSupplies.get() ? CycleState.FIND_SUPPLY : CycleState.FIND_PORTAL);
        }
    }

    private void tickWalkChest() {
        if (homeChest == null || !isChest(homeChest)) {
            homeChest = null;
            enterState(CycleState.FIND_CHEST);
            return;
        }
        if (inReach(homeChest)) {
            BaritoneHelper.stop();
            updateRotationTo(homeChest.toCenterPos());
            enterState(CycleState.OPEN_CHEST);
            return;
        }
        clearRotation();
        if (!pathRequested || (!BaritoneHelper.isPathing() && repathDelay <= 0)) {
            BaritoneHelper.goTo(homeChest, 2);
            pathRequested = true;
            repathDelay = REPATH_DELAY_TICKS;
        }
    }

    private void tickOpenChest() {
        if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler container) {
            containerOpened = true;
            depositSlot = container.getInventory().size();
            enterState(CycleState.DEPOSIT);
            actionDelay = ACTION_DELAY_TICKS;
            return;
        }
        if (homeChest == null || !isChest(homeChest)) {
            homeChest = null;
            enterState(CycleState.FIND_CHEST);
            return;
        }
        updateRotationTo(homeChest.toCenterPos());
        if (actionDelay > 0) return;
        if (mc.player.squaredDistanceTo(homeChest.toCenterPos()) > CONTAINER_OPEN_DISTANCE_SQ) {
            enterState(CycleState.WALK_CHEST);
            return;
        }
        BlockHitResult hit = raycastBlock(homeChest);
        if (hit == null) {
            if (cycleTicks > 40) {
                enterState(CycleState.WALK_CHEST);
            }
            return;
        }
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
        actionDelay = ACTION_DELAY_TICKS;
    }

    private void tickDeposit() {
        ScreenHandler handler = mc.player.currentScreenHandler;
        if (!(handler instanceof GenericContainerScreenHandler container)) {
            containerOpened = false;
            homeChest = null;
            enterState(CycleState.FIND_CHEST);
            return;
        }
        if (actionDelay > 0) return;
        int containerSlots = container.getInventory().size();
        int slot = findDepositableSlot(container, Math.max(depositSlot, containerSlots));
        if (slot >= 0) {
            Slot s = container.slots.get(slot);
            clickSlot(container, s.id, 0, SlotActionType.QUICK_MOVE);
            depositSlot = slot - 1;
            actionDelay = ACTION_DELAY_TICKS;
            return;
        }
        // депозит закончен
        closeContainer();
        if (findDepositableSlot(container, containerSlots) >= 0) {
            // сундук полон — помечаем и ищем другой
            exhaustedHomeChests.add(homeChest.toImmutable());
            homeChest = null;
            enterState(CycleState.FIND_CHEST);
            return;
        }
        homeChest = null;
        enterState(autoSupplies.get() && needSupplies() ? CycleState.FIND_SUPPLY : CycleState.FIND_PORTAL);
    }

    private int findDepositableSlot(GenericContainerScreenHandler container, int start) {
        for (int i = Math.max(0, start); i < container.slots.size(); i++) {
            Slot slot = container.slots.get(i);
            ItemStack stack = slot.getStack();
            if (stack.isEmpty() || isProtectedItem(stack)) continue;
            return i;
        }
        return -1;
    }

    private boolean isProtectedItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.isOf(Items.BRUSH)) return true;
        if (isSupplyItem(stack, SupplyKind.FOOD)) return true;
        if (isSupplyItem(stack, SupplyKind.INVISIBILITY)) return true;
        if (isSupplyItem(stack, SupplyKind.SPEED)) return true;
        return false;
    }

    // ===================== ПРИПАСЫ: FIND / WALK / OPEN / TAKE =====================

    private void tickFindSupply() {
        if (!needSupplies()) {
            enterState(CycleState.FIND_PORTAL);
            return;
        }
        if (scanDelay > 0) return;
        scanDelay = RESCAN_DELAY_TICKS;
        supplyChest = findSupplyChest();
        if (supplyChest != null) {
            unavailableSupplyKinds.clear();
            enterState(CycleState.WALK_SUPPLY);
        } else if (cycleTicks > 80) {
            enterState(CycleState.FIND_PORTAL);
        }
    }

    private void tickWalkSupply() {
        if (supplyChest == null || !isChest(supplyChest)) {
            supplyChest = null;
            enterState(CycleState.FIND_SUPPLY);
            return;
        }
        if (inReach(supplyChest)) {
            BaritoneHelper.stop();
            updateRotationTo(supplyChest.toCenterPos());
            enterState(CycleState.OPEN_SUPPLY);
            return;
        }
        clearRotation();
        if (!pathRequested || (!BaritoneHelper.isPathing() && repathDelay <= 0)) {
            BaritoneHelper.goTo(supplyChest, 2);
            pathRequested = true;
            repathDelay = REPATH_DELAY_TICKS;
        }
    }

    private void tickOpenSupply() {
        if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
            containerOpened = true;
            enterState(CycleState.TAKE_SUPPLY);
            actionDelay = ACTION_DELAY_TICKS;
            return;
        }
        if (supplyChest == null || !isChest(supplyChest)) {
            supplyChest = null;
            enterState(CycleState.FIND_SUPPLY);
            return;
        }
        updateRotationTo(supplyChest.toCenterPos());
        if (actionDelay > 0) return;
        if (mc.player.squaredDistanceTo(supplyChest.toCenterPos()) > CONTAINER_OPEN_DISTANCE_SQ) {
            enterState(CycleState.WALK_SUPPLY);
            return;
        }
        BlockHitResult hit = raycastBlock(supplyChest);
        if (hit == null) {
            if (cycleTicks > 40) enterState(CycleState.WALK_SUPPLY);
            return;
        }
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
        actionDelay = ACTION_DELAY_TICKS;
    }

    private void tickTakeSupply() {
        ScreenHandler handler = mc.player.currentScreenHandler;
        if (!(handler instanceof GenericContainerScreenHandler container)) {
            containerOpened = false;
            supplyChest = null;
            unavailableSupplyKinds.clear();
            enterState(CycleState.FIND_SUPPLY);
            return;
        }
        if (actionDelay > 0) return;

        SupplyKind kind = nextNeededSupply();
        if (kind == null) {
            closeContainer();
            supplyChest = null;
            enterState(CycleState.FIND_PORTAL);
            return;
        }

        int containerSlots = container.getInventory().size();
        int slot = findSupplyInContainer(container, containerSlots, kind);
        if (slot < 0) {
            unavailableSupplyKinds.add(kind);
            actionDelay = ACTION_DELAY_TICKS;
            return;
        }
        // Use a normal pickup transfer first. Some server containers accept
        // QUICK_MOVE but do not actually update the client inventory.
        if (!moveSupplyToInventory(container, slot, containerSlots)) {
            clickSlot(container, container.getSlot(slot).id, 0, SlotActionType.QUICK_MOVE);
        }
        actionDelay = ACTION_DELAY_TICKS;
    }

    private boolean moveSupplyToInventory(GenericContainerScreenHandler container, int sourceIndex, int containerSlots) {
        if (sourceIndex < 0 || sourceIndex >= containerSlots || sourceIndex >= container.slots.size()) {
            return false;
        }
        ItemStack source = container.getSlot(sourceIndex).getStack();
        if (source.isEmpty()) return false;

        int destination = -1;
        for (int i = containerSlots; i < container.slots.size(); i++) {
            ItemStack existing = container.getSlot(i).getStack();
            if (existing.isEmpty()
                    || (ItemStack.areItemsAndComponentsEqual(existing, source)
                    && existing.getCount() < existing.getMaxCount())) {
                destination = i;
                break;
            }
        }
        if (destination < 0) return false;

        clickSlot(container, container.getSlot(sourceIndex).id, 0, SlotActionType.PICKUP);
        clickSlot(container, container.getSlot(destination).id, 0, SlotActionType.PICKUP);
        return true;
    }

    private int findSupplyInContainer(GenericContainerScreenHandler container, int containerSlots, SupplyKind kind) {
        for (int i = 0; i < containerSlots && i < container.slots.size(); i++) {
            if (isSupplyItem(container.getSlot(i).getStack(), kind)) return i;
        }
        return -1;
    }

    // ===================== ПОРТАЛ: FIND / WALK / ENTER =====================

    private void tickFindPortal() {
        if (!returnToPortal.get()) {
            // не возвращаемся — просто начинаем фарм заново на месте
            beginFarmingReset();
            return;
        }
        if (scanDelay > 0) return;
        scanDelay = RESCAN_DELAY_TICKS;
        portalTarget = findNearestPortal();
        if (portalTarget != null) {
            enterState(CycleState.WALK_PORTAL);
        } else if (cycleTicks > 100) {
            // портал не найден — сброс в фарм
            beginFarmingReset();
        }
    }

    private void tickWalkPortal() {
        if (portalTarget == null || !isEndPortal(portalTarget)) {
            portalTarget = null;
            enterState(CycleState.FIND_PORTAL);
            return;
        }
        double dx = mc.player.getX() - (portalTarget.getX() + 0.5);
        double dz = mc.player.getZ() - (portalTarget.getZ() + 0.5);
        if (dx * dx + dz * dz <= 1.5) {
            updateRotationTo(portalTarget.toCenterPos());
            enterState(CycleState.ENTER_PORTAL);
            return;
        }
        clearRotation();
        if (!pathRequested || (!BaritoneHelper.isPathing() && repathDelay <= 0)) {
            BaritoneHelper.goTo(portalTarget, 0);
            pathRequested = true;
            repathDelay = REPATH_DELAY_TICKS;
        }
    }

    private void tickEnterPortal() {
        BaritoneHelper.stop();
        // Смена измерения = телепорт сработал
        if (cycleTicks > 100) {
            beginFarmingReset();
        }
        // стоим в портале, ждём загрузки End
    }

    private void beginFarmingReset() {
        BaritoneHelper.stop();
        pathRequested = false;
        brokenPots = 0;
        returnHomePending = false;
        exhaustedHomeChests.clear();
        exhaustedSupplyChests.clear();
        unavailableSupplyKinds.clear();
        homeChest = null;
        supplyChest = null;
        portalTarget = null;
        enterState(CycleState.FARMING);
    }

    // ===================== ПОТРЕБЛЕНИЕ ЕДЫ/ЗЕЛИЙ =====================

    private boolean tickConsumable() {
        if (activeConsumable != null) {
            // держим цель, пока предмет используется
            int slot = consumableHotbarSlot;
            if (slot < 0 || !isSupplyItem(mc.player.getInventory().getStack(slot), activeConsumable)
                    || (consumableTicks++ > CONSUMABLE_USE_TIMEOUT_TICKS)) {
                cancelConsumable();
                return true;
            }
            mc.player.getInventory().setSelectedSlot(slot);
            if (!mc.player.isUsingItem()) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }
            return true;
        }

        SupplyKind need = null;
        if (takeFood.get() && mc.player.getHungerManager().getFoodLevel() < 18) {
            need = SupplyKind.FOOD;
        } else if (takeInvisibility.get() && needsEffect(StatusEffects.INVISIBILITY)) {
            need = SupplyKind.INVISIBILITY;
        } else if (takeSpeed.get() && needsEffect(StatusEffects.SPEED)) {
            need = SupplyKind.SPEED;
        }
        if (need == null) return false;

        int slot = findSupplyHotbarSlot(need);
        if (slot < 0) return false;
        consumablePreviousSlot = mc.player.getInventory().getSelectedSlot();
        consumableHotbarSlot = slot;
        activeConsumable = need;
        consumableTicks = 0;
        mc.player.getInventory().setSelectedSlot(slot);
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        return true;
    }

    private void cancelConsumable() {
        if (mc.player != null && mc.player.isUsingItem() && mc.interactionManager != null) {
            mc.interactionManager.stopUsingItem(mc.player);
        }
        if (mc.player != null && consumablePreviousSlot >= 0) {
            mc.player.getInventory().setSelectedSlot(consumablePreviousSlot);
        }
        cancelConsumableState();
    }

    private void cancelConsumableState() {
        activeConsumable = null;
        consumablePreviousSlot = -1;
        consumableHotbarSlot = -1;
        consumableTicks = 0;
    }

    private boolean needsEffect(RegistryEntry<StatusEffect> effect) {
        StatusEffectInstance instance = mc.player.getStatusEffect(effect);
        return instance == null || (!instance.isInfinite() && instance.getDuration() <= 100);
    }

    private int findSupplyHotbarSlot(SupplyKind kind) {
        for (int i = 0; i < 9; i++) {
            if (isSupplyItem(mc.player.getInventory().getStack(i), kind)) return i;
        }
        return -1;
    }

    // ===================== SUPPLY/EFFECT ПРОВЕРКИ =====================

    private boolean needSupplies() {
        if (!autoSupplies.get()) return false;
        for (SupplyKind kind : SupplyKind.values()) {
            if (!isSupplyEnabled(kind)) continue;
            if (countSupply(kind) < desiredSupplyAmount(kind)) return true;
        }
        return false;
    }

    private SupplyKind nextNeededSupply() {
        for (SupplyKind kind : SupplyKind.values()) {
            if (!isSupplyEnabled(kind) || unavailableSupplyKinds.contains(kind)) continue;
            if (countSupply(kind) < desiredSupplyAmount(kind)) return kind;
        }
        return null;
    }

    private boolean isSupplyEnabled(SupplyKind kind) {
        return switch (kind) {
            case FOOD -> takeFood.get();
            case INVISIBILITY -> takeInvisibility.get();
            case SPEED -> takeSpeed.get();
        };
    }

    private int desiredSupplyAmount(SupplyKind kind) {
        return Math.max(1, Math.round(switch (kind) {
            case FOOD -> (float) foodAmount.get();
            case INVISIBILITY -> (float) invisibilityAmount.get();
            case SPEED -> (float) speedAmount.get();
        }));
    }

    private int countSupply(SupplyKind kind) {
        int total = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isSupplyItem(stack, kind)) total += stack.getCount();
        }
        return total;
    }

    private boolean isSupplyItem(ItemStack stack, SupplyKind kind) {
        if (stack == null || stack.isEmpty()) return false;
        return switch (kind) {
            case FOOD -> {
                FoodComponent food = stack.get(DataComponentTypes.FOOD);
                yield food != null;
            }
            case INVISIBILITY -> potionHasEffect(stack, StatusEffects.INVISIBILITY);
            case SPEED -> potionHasEffect(stack, StatusEffects.SPEED);
        };
    }

    private boolean potionHasEffect(ItemStack stack, RegistryEntry<StatusEffect> effect) {
        if (!stack.isOf(Items.POTION) && !stack.isOf(Items.SPLASH_POTION) && !stack.isOf(Items.LINGERING_POTION))
            return false;
        PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (contents == null) return false;
        for (StatusEffectInstance instance : contents.getEffects()) {
            if (instance.getEffectType().equals(effect)) return true;
        }
        return false;
    }

    private boolean isInventoryFull() {
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).isEmpty()) return false;
        }
        return true;
    }

    // ===================== ПОИСК БЛОКОВ =====================

    private BlockPos findNearestChest(int radius, Set<BlockPos> exhausted) {
        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.iterateOutwards(playerPos, radius, radius, radius)) {
            if (exhausted.contains(pos) || !isChest(pos)) continue;
            double d = mc.player.getEyePos().squaredDistanceTo(pos.toCenterPos());
            if (d < bestDist) {
                bestDist = d;
                best = pos.toImmutable();
            }
        }
        return best;
    }

    private BlockPos findSupplyChest() {
        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.iterateOutwards(playerPos, SUPPLY_CHEST_SEARCH_RADIUS, 6, SUPPLY_CHEST_SEARCH_RADIUS)) {
            if (exhaustedSupplyChests.contains(pos) || !isChest(pos)) continue;
            if (!hasSupplySignNearby(pos)) continue;
            double d = playerPos.getSquaredDistance(pos);
            if (d < bestDist) {
                bestDist = d;
                best = pos.toImmutable();
            }
        }
        return best;
    }

    private boolean hasSupplySignNearby(BlockPos chest) {
        String needle = normalizeSign(supplySign.get());
        if (needle.isBlank()) return true;
        for (BlockPos pos : BlockPos.iterate(chest.add(-1, -1, -1), chest.add(1, 1, 1))) {
            BlockEntity be = mc.world.getBlockEntity(pos);
            if (!(be instanceof SignBlockEntity sign)) continue;
            if (signContains(sign, needle)) return true;
        }
        return false;
    }

    private boolean signContains(SignBlockEntity sign, String needle) {
        return signTextContains(sign.getFrontText(), needle) || signTextContains(sign.getBackText(), needle);
    }

    private boolean signTextContains(SignText text, String needle) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) sb.append(text.getMessage(i, false).getString()).append(' ');
        return normalizeSign(sb.toString()).contains(needle);
    }

    private String normalizeSign(String s) {
        return s == null ? "" : s.replaceAll("[^\\p{L}\\p{N} ]", "").toLowerCase(Locale.ROOT).trim();
    }

    private BlockPos findNearestPortal() {
        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.iterateOutwards(playerPos, PORTAL_SEARCH_RADIUS, 16, PORTAL_SEARCH_RADIUS)) {
            if (!isEndPortal(pos)) continue;
            double d = playerPos.getSquaredDistance(pos);
            if (d < bestDist) {
                bestDist = d;
                best = pos.toImmutable();
            }
        }
        return best;
    }

    private boolean isChest(BlockPos pos) {
        if (pos == null || mc.world == null) return false;
        BlockState state = mc.world.getBlockState(pos);
        return state.getBlock() instanceof ChestBlock
                || state.isOf(Blocks.CHEST) || state.isOf(Blocks.TRAPPED_CHEST) || state.isOf(Blocks.BARREL);
    }

    private boolean isEndPortal(BlockPos pos) {
        if (pos == null || mc.world == null) return false;
        return mc.world.getBlockState(pos).isOf(Blocks.END_PORTAL);
    }

    // ===================== PVP BOSS-BAR =====================

    private int pvpCooldownSeconds() {
        BossBarHud hud = mc.inGameHud != null ? mc.inGameHud.getBossBarHud() : null;
        if (hud == null) return 0;
        Map<UUID, ClientBossBar> bars = ((BossBarHudAccessor) hud).getBossBars();
        for (ClientBossBar bar : bars.values()) {
            String name = bar.getName().getString().toLowerCase(Locale.ROOT);
            if (name.contains("pvp") || name.contains("бой") || name.contains("таймер")) {
                return Math.max(1, Math.round(bar.getPercent() * 60f));
            }
        }
        return 0;
    }

    // ===================== ЧАТ (подтверждение home) =====================

    @EventHandler
    public void onPacket(EventPacket event) {
        if (!getState()) return;
        if (event.getPacket() instanceof GameMessageS2CPacket msg) {
            String text = msg.content().getString().toLowerCase(Locale.ROOT);
            if (text.contains("телепорт") || text.contains("teleport") || text.contains("дом")
                    || text.contains("home") || text.contains("спавн") || text.contains("spawn")) {
                homeTeleportConfirmed = true;
            }
        }
    }

    // ===================== НАВИГАЦИЯ / РОТАЦИЯ =====================

    private void requestPath() {
        if (targetBlock == null) return;
        BlockPos goal = targetBrushable ? targetBlock.up() : targetBlock;
        BaritoneHelper.goTo(goal, 2);
        pathRequested = true;
        repathDelay = REPATH_DELAY_TICKS;
    }

    private void updateRotation() {
        Vec3d look = null;
        if (lootOrigin != null) {
            ItemEntity item = findNearestLootItem();
            if (item != null) look = new Vec3d(item.getX(), item.getY(), item.getZ());
        } else if (targetBlock != null) {
            look = Vec3d.ofCenter(targetBlock);
        }
        if (look == null) {
            clearRotation();
            return;
        }
        updateRotationTo(look);
    }

    private void updateRotationTo(Vec3d look) {
        if (look == null || mc.player == null) return;
        float[] rot = calcRotation(look);
        RotationTask.create(ROTATION_TASK_NAME, 40);
        RotationTask.setTargetRotation(rot[0], rot[1],
                40.0f, 30.0f, 40.0f, 30.0f,
                2.0, 40, 120L);
    }

    private void clearRotation() {
        RotationTask.remove(ROTATION_TASK_NAME);
    }

    private float[] calcRotation(Vec3d target) {
        Vec3d eye = mc.player.getEyePos();
        double dx = target.x - eye.x, dy = target.y - eye.y, dz = target.z - eye.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) (-Math.toDegrees(Math.atan2(dy, horiz)));
        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.clamp(pitch, -90f, 90f)};
    }

    // ===================== СКАНИРОВАНИЕ ФАРМ-ЦЕЛЕЙ =====================

    private TargetSelection scanForNearestTarget() {
        if (mc.world == null || mc.player == null) return null;
        BlockPos playerPos = mc.player.getBlockPos();
        int centerChunkX = playerPos.getX() >> 4;
        int centerChunkZ = playerPos.getZ() >> 4;
        int radius = Math.min(8, Math.round((float) scanRadius.get()));
        boolean canBrush = brushBlocks.get() && findHotbarItem(Items.BRUSH) >= 0;

        BlockPos best = null;
        boolean bestBrushable = false;
        double bestDist = Double.MAX_VALUE;

        for (int cx = centerChunkX - radius; cx <= centerChunkX + radius; cx++) {
            for (int cz = centerChunkZ - radius; cz <= centerChunkZ + radius; cz++) {
                WorldChunk chunk = mc.world.getChunkManager().getWorldChunk(cx, cz);
                if (chunk == null) continue;
                for (BlockPos pos : chunk.getBlockEntities().keySet()) {
                    if (temporarilySkippedTargets.containsKey(pos)) continue;
                    boolean isPot = isDecoratedPot(pos);
                    boolean isBrush = canBrush && isBrushable(pos);
                    if (!isPot && !isBrush) continue;
                    double d = playerPos.getSquaredDistance(pos);
                    if (d < bestDist) {
                        bestDist = d;
                        best = pos.toImmutable();
                        bestBrushable = isBrush;
                    }
                }
            }
        }
        return best == null ? null : new TargetSelection(best, bestBrushable);
    }

    private boolean isDecoratedPot(BlockPos pos) {
        if (pos == null || mc.world == null) return false;
        return mc.world.getBlockState(pos).isOf(Blocks.DECORATED_POT);
    }

    private boolean isBrushable(BlockPos pos) {
        if (pos == null || mc.world == null) return false;
        BlockState state = mc.world.getBlockState(pos);
        return state.isOf(Blocks.SUSPICIOUS_SAND) || state.isOf(Blocks.SUSPICIOUS_GRAVEL);
    }

    private boolean inReach(BlockPos pos) {
        if (pos == null) return false;
        double reach = breakReach.get();
        return mc.player.getEyePos().squaredDistanceTo(pos.toCenterPos()) <= reach * reach;
    }

    private void resetTarget(boolean stopPath) {
        targetBlock = null;
        targetBrushable = false;
        breakingStarted = false;
        brushingStarted = false;
        aimTicks = 0;
        if (stopPath) {
            BaritoneHelper.stop();
            pathRequested = false;
            repathDelay = 0;
        }
    }

    private void skipCurrentTarget() {
        if (targetBlock != null) {
            temporarilySkippedTargets.put(targetBlock.toImmutable(), UNREACHABLE_TARGET_TICKS);
        }
        resetTarget(true);
        scanDelay = RESCAN_DELAY_TICKS;
    }

    private void tickSkippedTargets() {
        temporarilySkippedTargets.replaceAll((pos, ticks) -> ticks - 1);
        temporarilySkippedTargets.entrySet().removeIf(e -> e.getValue() <= 0);
    }

    // ===================== ХЕЛПЕРЫ ИНВЕНТАРЯ/КОМАНД =====================

    private void clickSlot(GenericContainerScreenHandler container, int slotId, int button, SlotActionType action) {
        mc.interactionManager.clickSlot(container.syncId, slotId, button, action, mc.player);
    }

    private void closeContainer() {
        if (containerOpened && mc.player != null) {
            mc.player.closeHandledScreen();
        }
        containerOpened = false;
    }

    private BlockHitResult raycastBlock(BlockPos pos) {
        if (pos == null || mc.world == null || mc.player == null) return null;
        HitResult hitResult = mc.crosshairTarget;
        if (hitResult instanceof BlockHitResult bhr
                && bhr.getType() == HitResult.Type.BLOCK
                && bhr.getBlockPos().equals(pos)) {
            return bhr;
        }
        Vec3d eye = mc.player.getEyePos();
        Vec3d center = pos.toCenterPos();
        BlockHitResult rayHit = mc.world.raycast(new RaycastContext(
                eye, center, RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE, mc.player));
        if (rayHit.getType() == HitResult.Type.BLOCK && rayHit.getBlockPos().equals(pos)) {
            return rayHit;
        }

        // crosshairTarget may still describe the previous camera frame while
        // RotationTask is turning. Interact with the target block directly.
        Direction side = nearestInteractSide(pos, eye);
        Vec3d hitVec = center.add(
                side.getOffsetX() * 0.5,
                side.getOffsetY() * 0.5,
                side.getOffsetZ() * 0.5);
        return new BlockHitResult(hitVec, side, pos, false);
    }

    private Direction nearestInteractSide(BlockPos pos, Vec3d eye) {
        Vec3d center = pos.toCenterPos();
        double dx = eye.x - center.x;
        double dy = eye.y - center.y;
        double dz = eye.z - center.z;
        if (Math.abs(dx) >= Math.abs(dz) && Math.abs(dx) > 0.15) {
            return dx > 0.0 ? Direction.EAST : Direction.WEST;
        }
        if (Math.abs(dz) > 0.15) {
            return dz > 0.0 ? Direction.SOUTH : Direction.NORTH;
        }
        return dy >= 0.0 ? Direction.UP : Direction.DOWN;
    }

    private void sendCommand(String command) {
        if (mc.player == null || command == null || command.isBlank()) return;
        mc.player.networkHandler.sendChatCommand(command.startsWith("/") ? command.substring(1) : command);
    }

    private Vec3d playerPos() {
        return new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
    }

    // ===================== РЕНДЕР =====================

    @EventHandler
    public void onRender3D(EventRender.World event) {
        if (!renderTarget.get()) return;
        BlockPos render = targetBlock != null ? targetBlock
                : lootOrigin != null ? lootOrigin
                : homeChest != null ? homeChest
                : supplyChest != null ? supplyChest
                : portalTarget;
        if (nullCheck() || render == null || mc.world == null) return;

        Camera camera = mc.gameRenderer.getCamera();
        float fov = ((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, event.getTicks(), true);
        Matrix4f posMatrix = new Matrix4f(event.getMatrixStack().peek().getPositionMatrix());
        Matrix4f projMatrix = new Matrix4f(mc.gameRenderer.getBasicProjectionMatrix(fov));

        int color = cycleColor();
        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera, posMatrix, projMatrix)) {
            renderBlock(renderer, camera, event.getMatrixStack().peek(), render, color);
            renderer.flush();
        }
    }

    private int cycleColor() {
        if (targetBlock != null) return targetBrushable
                ? ColorUtils.rgba(255, 200, 0, 255)
                : ColorUtils.rgba(0, 255, 128, 255);
        if (lootOrigin != null) return ColorUtils.rgba(0, 200, 255, 255);
        if (homeChest != null) return ColorUtils.rgba(255, 80, 80, 255);
        if (supplyChest != null) return ColorUtils.rgba(180, 80, 255, 255);
        return ColorUtils.rgba(255, 255, 255, 255);
    }

    private void renderBlock(WorldRenderer renderer, Camera camera, MatrixStack.Entry entry, BlockPos pos, int rgba) {
        Vec3d cam = camera.getCameraPos();
        Box box = new Box(pos).expand(0.002).offset(-cam.x, -cam.y, -cam.z);
        int fill = ColorUtils.injectAlpha(rgba, 90);
        WorldGeometryEmitter emitter = new WorldGeometryEmitter(
                camera, entry,
                renderer.getBuffer(WorldRenderLayers.POSITION_COLOR_QUADS_ADDITIVE_NO_DEPTH()));
        emitBoxFill(emitter, box, fill);
    }

    private void emitBoxFill(WorldGeometryEmitter e, Box b, int c) {
        Vec3d p000 = new Vec3d(b.minX, b.minY, b.minZ), p001 = new Vec3d(b.minX, b.minY, b.maxZ),
                p010 = new Vec3d(b.minX, b.maxY, b.minZ), p011 = new Vec3d(b.minX, b.maxY, b.maxZ),
                p100 = new Vec3d(b.maxX, b.minY, b.minZ), p101 = new Vec3d(b.maxX, b.minY, b.maxZ),
                p110 = new Vec3d(b.maxX, b.maxY, b.minZ), p111 = new Vec3d(b.maxX, b.maxY, b.maxZ);
        e.emitQuad(p000, p100, p110, p010, c, c, c, c);
        e.emitQuad(p001, p011, p111, p101, c, c, c, c);
        e.emitQuad(p000, p001, p101, p100, c, c, c, c);
        e.emitQuad(p010, p110, p111, p011, c, c, c, c);
        e.emitQuad(p000, p010, p011, p001, c, c, c, c);
        e.emitQuad(p100, p101, p111, p110, c, c, c, c);
    }

    private record TargetSelection(BlockPos pos, boolean brushable) {
    }

    private enum CycleState {
        FARMING, PVP_WAIT, HOME_WAIT,
        FIND_CHEST, WALK_CHEST, OPEN_CHEST, DEPOSIT,
        FIND_SUPPLY, WALK_SUPPLY, OPEN_SUPPLY, TAKE_SUPPLY,
        FIND_PORTAL, WALK_PORTAL, ENTER_PORTAL
    }

    private enum SupplyKind {
        FOOD, INVISIBILITY, SPEED
    }
}
