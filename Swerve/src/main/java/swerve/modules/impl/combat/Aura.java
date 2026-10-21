package swerve.modules.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import swerve.managers.FriendManager;
import swerve.managers.ModuleManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ListSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.modules.impl.combat.rotation.AuraRotation;
import swerve.modules.impl.combat.rotation.JitterRotation;
import swerve.modules.impl.combat.rotation.SmoothRotation;
import swerve.modules.impl.combat.rotation.SnapRotation;
import swerve.ui.hud.HudManager;
import swerve.ui.newgui.GuiRender;
import swerve.utils.combat.AuraUtils;
import swerve.utils.network.ServerTPS;
import swerve.utils.time.PerfectDelay;

/**
 * Aura — автоматическая атака ближайших врагов с поддержкой ротаций.
 *
 * <p>Полная переработка модуля KillAura из клиента Private, адаптированная под
 * архитектуру Swerve (Fabric 1.21.4, Mojang маппинги, Java 21).
 *
 * <p>Функционал:
 * <ul>
 *   <li>Выбор цели: ближний, слабейший, низкое HP, опасный (игрок).</li>
 *   <li>Фильтры: игроки / мобы / невидимые / только в броне, друзья, хитбокс через блоки.</li>
 *   <li>Дистанция: обычная, доп. дистанция, элитра, умная дистанция.</li>
 *   <li>Угол обзора (FOV) с индикатором на экране.</li>
 *   <li>Криты: только криты, умные криты, рандом криты.</li>
 *   <li>Режимы ударов: 1.8 (CPS) и 1.9 (кулдаун + лимит частоты).</li>
 *   <li>Ломание щита топором.</li>
 *   <li>Синхронизация по TPS сервера.</li>
 *   <li>Задержка смены цели.</li>
 *   <li>3 ротации на выбор: Снап, Плавная, Джиттер.</li>
 * </ul>
 */
public final class Aura extends Module {

    // ==================== Ротации ====================

    private final ModeSetting rotationMode = new ModeSetting(
            "Ротация",
            "Снап",
            "Снап",
            "Плавная",
            "Джиттер"
    );

    private final FloatSetting smoothSpeed = new FloatSetting(
            "Скорость плавной ротации",
            0.15F,
            0.01F,
            1.0F,
            0.01F
    ).visible(() -> rotationMode.is("Плавная"));

    private final AuraRotation snapRotation = new SnapRotation();
    private final AuraRotation smoothRotation = new SmoothRotation();
    private final AuraRotation jitterRotation = new JitterRotation();

    /** Возвращает активную реализацию ротации в зависимости от текущей настройки. */
    public AuraRotation getActiveRotation() {
        return switch (rotationMode.getCurrent()) {
            case "Плавная" -> smoothRotation;
            case "Джиттер" -> jitterRotation;
            default -> snapRotation;
        };
    }

    // ==================== Настройки цели ====================

    private final FloatSetting fov = new FloatSetting("Угол обзора", 180.0F, 30.0F, 180.0F, 1.0F);
    private final FloatSetting attackRange = new FloatSetting("Дистанция", 3.0F, 3.0F, 6.0F, 0.1F);
    private final FloatSetting preRange = new FloatSetting("Доп дистанция", 1.0F, 0.0F, 3.0F, 0.1F);
    private final FloatSetting elRange = new FloatSetting("Дистанция на элитре", 3.0F, 1.0F, 5.0F, 0.1F);

    private final ListSetting targetSettings = ListSetting.of(
            "Кого атаковать",
            BooleanSetting.of("Игроки", true),
            BooleanSetting.of("Мобы", true),
            BooleanSetting.of("Невидимые", true),
            BooleanSetting.of("Только в броне", false)
    );

    private final BooleanSetting noHitThroughBlocks = new BooleanSetting("Не бить через блоки", true);
    private final BooleanSetting smartDistance = new BooleanSetting("Умная дистанция", false);
    private final BooleanSetting smartHitbox = new BooleanSetting("Улучшить хитбоксы", false);

    // ==================== Криты ====================

    private final BooleanSetting onlyCrits = new BooleanSetting("Только криты", true);
    private final BooleanSetting smartCrits = new BooleanSetting("Умные криты", false)
            .visible(onlyCrits::get);
    private final BooleanSetting randomCrits = new BooleanSetting("Рандом криты", false)
            .visible(onlyCrits::get);

    // ==================== Прочее ====================

    private final BooleanSetting shieldBreaker = new BooleanSetting("Ломать щит", true);
    private final BooleanSetting priorityTargeting = new BooleanSetting("Приоритетное наведение", false);
    private final ModeSetting priorityMode = new ModeSetting(
            "Режим приоритета",
            "Ближайший",
            "Ближайший",
            "Слабейший",
            "Низкое HP",
            "Опасный"
    ).visible(priorityTargeting::get);

    private final BooleanSetting targetSwitchDelay = new BooleanSetting("Задержка смены цели", false);
    private final FloatSetting minSwitchDelay = new FloatSetting("Мин. задержка смены (мс)", 50.0F, 0.0F, 500.0F, 10.0F)
            .visible(targetSwitchDelay::get);
    private final FloatSetting maxSwitchDelay = new FloatSetting("Макс. задержка смены (мс)", 150.0F, 50.0F, 1000.0F, 10.0F)
            .visible(targetSwitchDelay::get);

    private final ListSetting checks = ListSetting.of(
            "Прочее",
            BooleanSetting.of("Отображать FOV", false),
            BooleanSetting.of("Не бить когда ешь", false),
            BooleanSetting.of("Бить только с оружием", false),
            BooleanSetting.of("Выключить после смерти", true)
    );

    // ==================== Режимы ударов ====================

    private static final String ATTACK_19 = "1.9";
    private static final String ATTACK_18 = "1.8";

    private final ModeSetting attackMode = new ModeSetting("Режим ударов", ATTACK_19, ATTACK_19, ATTACK_18);
    private final FloatSetting dynamicCps = new FloatSetting("CPS (1.8)", 12.0F, 8.0F, 20.0F, 1.0F)
            .visible(() -> attackMode.is(ATTACK_18));
    private final FloatSetting cooldown = new FloatSetting("Кулдаун", 0.93F, 0.5F, 1.0F, 0.01F)
            .visible(() -> attackMode.is(ATTACK_19));

    // ==================== TPS ====================

    private final BooleanSetting syncTPS = new BooleanSetting("Синхронизация по TPS", false);
    private final FloatSetting tpsMultiplier = new FloatSetting("Множитель TPS", 0.5F, 0.1F, 2.0F, 0.1F)
            .visible(syncTPS::get);

    // ==================== Состояние ====================

    private static final long RATE_LIMIT_19_MS = 500L;
    private static final float COOLED_ATTACK_STRENGTH = 1.5F;

    private final PerfectDelay perfectDelay = new PerfectDelay();
    private final TargetScan scan = new TargetScan();

    private LivingEntity target;
    private boolean canCrit;
    private int pendingCritTicks;
    private long lastAttackTime;
    private long lastClickTime;
    private long lastTargetSwitchTime;

    public Aura() {
        super("Aura", "Автоматически атакует ближайших врагов", ModuleCategory.COMBAT);
        addSettings(
                rotationMode, smoothSpeed,
                fov, attackRange, preRange, elRange,
                targetSettings, noHitThroughBlocks, smartDistance, smartHitbox,
                onlyCrits, smartCrits, randomCrits,
                shieldBreaker, priorityTargeting, priorityMode,
                targetSwitchDelay, minSwitchDelay, maxSwitchDelay,
                checks, attackMode, dynamicCps, cooldown,
                syncTPS, tpsMultiplier
        );

        HudManager.registerOverlay(Aura::renderFovOverlay);
    }

    @Override
    public void onEnable() {
        getActiveRotation().onEnable();
        reset();
    }

    @Override
    public void onDisable() {
        getActiveRotation().onDisable();
        reset();
    }

    /** Выбранная в данный момент реализация ротации. */
    public AuraRotation getRotation() {
        return getActiveRotation();
    }

    /** Текущая цель ауры (доступна для соседних модулей, например ElytraFlight). */
    public LivingEntity getTarget() {
        return target;
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.gameMode == null) {
            reset();
            return;
        }
        if (player.isSpectator()) {
            return;
        }
        if (checks.is("Выключить после смерти") && player.isDeadOrDying()) {
            setEnabled(false);
            return;
        }

        // Применяем настройку скорости плавной ротации.
        if (rotationMode.is("Плавная")) {
            ((SmoothRotation) smoothRotation).setSpeed(smoothSpeed.get());
        }

        // canCrit предсказывается в конце тика — движение уже применено,
        // fallDistance совпадает с тем, что увидит ванильная проверка крита.
        canCrit = !player.onGround()
                && player.fallDistance > ThreadLocalRandom.current().nextFloat() * 0.15F;

        if (pendingCritTicks > 0) {
            pendingCritTicks--;
            if (pendingCritTicks == 0 && target != null && target.isAlive() && !target.isRemoved()) {
                attackEntity(minecraft, player, target);
            }
        }

        updateTarget(minecraft, player);
        updateAttack(minecraft, player);
    }

    /** Доставка исходящих пакетов (аналог PacketEvent из Private). */
    public void onPacketSend(Packet<?> packet) {
        if (!isEnable()) {
            return;
        }
        if (packet instanceof ServerboundSetCarriedItemPacket) {
            perfectDelay.reset(550L);
        } else if (packet instanceof ServerboundSwingPacket) {
            perfectDelay.reset(450L);
        }
    }

    // ==================== Выбор цели ====================

    private void updateTarget(Minecraft minecraft, LocalPlayer player) {
        LivingEntity potential = findTarget(minecraft, player);

        if (target != potential) {
            if (targetSwitchDelay.get() && target != null) {
                float delayTime = randomLerp(minSwitchDelay.get(), maxSwitchDelay.get());
                if (System.currentTimeMillis() - lastTargetSwitchTime < (long) delayTime) {
                    return;
                }
            }
            target = potential;
            if (target != null) {
                lastTargetSwitchTime = System.currentTimeMillis();
            }
        }
        if (target == null) {
            return;
        }
        if (!isValidTarget(player, target)) {
            target = null;
            return;
        }
        if (AuraUtils.fovFromCamera(player, target) > fov.get()) {
            target = null;
        }
    }

    private LivingEntity findTarget(Minecraft minecraft, LocalPlayer player) {
        double searchRange = attackRange.get() + preRange.get();
        double searchRangeSquared = searchRange * searchRange;
        scan.reset();

        Iterable<Entity> entities = minecraft.level.entitiesForRendering();
        if (entities instanceof List<?> list) {
            for (int index = 0, size = list.size(); index < size; index++) {
                if (list.get(index) instanceof LivingEntity living && isValidTarget(player, living)) {
                    scan.consider(player, living, searchRangeSquared);
                }
            }
        } else {
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity living && isValidTarget(player, living)) {
                    scan.consider(player, living, searchRangeSquared);
                }
            }
        }
        return scan.pick(priorityTargeting.get(), priorityMode.getCurrent());
    }

    private static final class TargetScan {
        private LivingEntity nearest;
        private double nearestDistanceSquared = Double.MAX_VALUE;
        private LivingEntity weakest;
        private float weakestHealth = Float.MAX_VALUE;
        private LivingEntity lowHp;
        private float lowHpHealth = Float.MAX_VALUE;
        private LivingEntity firstPlayer;
        private LivingEntity first;

        private void reset() {
            nearest = null;
            nearestDistanceSquared = Double.MAX_VALUE;
            weakest = null;
            weakestHealth = Float.MAX_VALUE;
            lowHp = null;
            lowHpHealth = Float.MAX_VALUE;
            firstPlayer = null;
            first = null;
        }

        private void consider(LocalPlayer player, LivingEntity living, double searchRangeSquared) {
            double distanceSquared = player.distanceToSqr(living);
            if (distanceSquared > searchRangeSquared) {
                return;
            }
            if (distanceSquared < nearestDistanceSquared) {
                nearestDistanceSquared = distanceSquared;
                nearest = living;
            }
            float health = living.getHealth();
            if (health < weakestHealth) {
                weakestHealth = health;
                weakest = living;
            }
            if (health < living.getMaxHealth() * 0.5F && health < lowHpHealth) {
                lowHpHealth = health;
                lowHp = living;
            }
            if (first == null) {
                first = living;
            }
            if (firstPlayer == null && living instanceof Player) {
                firstPlayer = living;
            }
        }

        private LivingEntity pick(boolean priority, String mode) {
            if (!priority) {
                return nearest;
            }
            switch (mode) {
                case "Слабейший":
                    return weakest;
                case "Низкое HP":
                    return lowHp != null ? lowHp : first;
                case "Опасный":
                    return firstPlayer != null ? firstPlayer : first;
                default:
                    return nearest;
            }
        }
    }

    // ==================== Атака ====================

    private void updateAttack(Minecraft minecraft, LocalPlayer player) {
        if (target == null) {
            return;
        }
        if (!shouldAttack(player)) {
            return;
        }
        if (!rayTraceOk(player)) {
            return;
        }
        if (AuraUtils.strictDistance(player, target) >= attackDistance(player)) {
            return;
        }

        // В спринте удар не считается критом — ждём остановки спринта.
        if (!isInLiquid(player) && player.isSprinting() && wasSprinting(player)) {
            return;
        }

        if (shieldBreaker.get() && attackReady(player) && breakShield(minecraft, player)) {
            return;
        }

        if (attackMode.is(ATTACK_18)) {
            long now = System.currentTimeMillis();
            float variation = randomLerp(0.8F, 1.2F);
            long clickInterval = applyTpsScale((long) (1000.0F / (dynamicCps.get() * variation)));
            if (now - lastClickTime >= clickInterval) {
                attackEntity(minecraft, player, target);
                lastClickTime = now;
            }
        } else if (randomCrits.get() && shouldCritical(player)
                && ThreadLocalRandom.current().nextFloat() < 0.3F) {
            pendingCritTicks = ThreadLocalRandom.current().nextInt(1, 3);
        } else {
            attackEntity(minecraft, player, target);
        }
    }

    private boolean shouldAttack(LocalPlayer player) {
        if (!perfectDelay.cooldownComplete()) {
            return false;
        }
        if (isInLiquid(player)) {
            return false;
        }
        boolean eating = player.isUsingItem() && checks.is("Не бить когда ешь");
        boolean noWeapon = !isHoldingWeapon(player) && checks.is("Бить только с оружием");
        if (eating || noWeapon) {
            return false;
        }

        boolean headroom = AuraUtils.hasHeadroom(player);
        boolean canDefaultCrit = player.fallDistance > 0.0F && canCrit && headroom;
        boolean canSmartCrit = headroom && !player.input.keyPresses.jump()
                && !canCrit && player.onGround() && player.verticalCollision;

        if (onlyCrits.get() && !smartCrits.get() && !randomCrits.get()) {
            return shouldCritical(player) && canDefaultCrit;
        }
        if (smartCrits.get()) {
            return shouldCritical(player) || canDefaultCrit || canSmartCrit;
        }
        if (randomCrits.get()) {
            return shouldCritical(player) || canDefaultCrit
                    || ThreadLocalRandom.current().nextFloat() < 0.3F;
        }
        return true;
    }

    private boolean shouldCritical(LocalPlayer player) {
        return player.fallDistance > ThreadLocalRandom.current().nextFloat() * 0.15F
                && !player.onGround()
                && !player.onClimbable()
                && !player.isPassenger()
                && !player.hasEffect(MobEffects.BLINDNESS)
                && !player.hasEffect(MobEffects.LEVITATION)
                && !player.hasEffect(MobEffects.SLOW_FALLING)
                && !isInLiquid(player)
                && !player.getAbilities().flying
                && !player.isFallFlying()
                && !AuraUtils.isPlayerInWeb(player);
    }

    private void attackEntity(Minecraft minecraft, LocalPlayer player, Entity entity) {
        if (!attackReady(player)) {
            return;
        }
        minecraft.gameMode.attack(player, entity);
        player.swing(InteractionHand.MAIN_HAND);
        player.resetAttackStrengthTicker();
        lastAttackTime = System.currentTimeMillis();
    }

    private boolean attackReady(LocalPlayer player) {
        if (!attackMode.is(ATTACK_19)) {
            return true;
        }
        if (System.currentTimeMillis() - lastAttackTime < applyTpsScale(RATE_LIMIT_19_MS)) {
            return false;
        }
        return player.getAttackStrengthScale(COOLED_ATTACK_STRENGTH) >= cooldown.get();
    }

    private boolean breakShield(Minecraft minecraft, LocalPlayer player) {
        if (!target.isBlocking()) {
            return false;
        }
        int axeHotbarSlot = findAxeHotbarSlot(player);
        if (axeHotbarSlot < 0) {
            return false;
        }
        int containerId = player.inventoryMenu.containerId;
        int containerSlot = 36 + axeHotbarSlot;
        int selected = player.getInventory().selected;
        minecraft.gameMode.handleInventoryMouseClick(
                containerId, containerSlot, selected, ClickType.SWAP, player);
        attackEntity(minecraft, player, target);
        minecraft.gameMode.handleInventoryMouseClick(
                containerId, containerSlot, selected, ClickType.SWAP, player);
        return true;
    }

    private int findAxeHotbarSlot(LocalPlayer player) {
        for (int slot = 0; slot < 9; slot++) {
            if (slot == player.getInventory().selected) {
                continue;
            }
            if (player.getInventory().items.get(slot).getItem() instanceof AxeItem) {
                return slot;
            }
        }
        return -1;
    }

    // ==================== Проверка прицела и дистанции ====================

    private boolean rayTraceOk(LocalPlayer player) {
        if (player.isFallFlying() || (target != null && target.isFallFlying())) {
            return true;
        }
        if (smartHitbox.get() && target != null) {
            Vec3 visiblePoint = AuraUtils.visibleTargetPoint(player, target);
            return player.getEyePosition(1.0F).distanceTo(visiblePoint) <= attackDistance(player);
        }
        if (!AuraUtils.lookRayHitsBox(player, target, attackDistance(player))) {
            return false;
        }
        return !noHitThroughBlocks.get() || AuraUtils.hasVisiblePoint(player, target);
    }

    private double attackDistance(LocalPlayer player) {
        if (player.isFallFlying() || (target != null && target.isFallFlying())) {
            return elRange.get();
        }
        if (smartDistance.get() && target != null) {
            return 3.0D;
        }
        return Math.max(player.isCreative() ? 6.0D : 3.0D, attackRange.get());
    }

    private boolean isHoldingWeapon(LocalPlayer player) {
        return player.getMainHandItem().getItem() instanceof SwordItem
                || player.getMainHandItem().getItem() instanceof AxeItem;
    }

    private boolean isInLiquid(LocalPlayer player) {
        return player.isVisuallySwimming()
                || player.isSwimming() && player.isEyeInFluid(FluidTags.WATER)
                || player.isEyeInFluid(FluidTags.LAVA);
    }

    // ==================== Фильтры цели ====================

    private boolean isValidTarget(LocalPlayer player, LivingEntity entity) {
        if (entity == player || !entity.isAlive() || entity.isRemoved()) {
            return false;
        }
        double limit = attackRange.get() + preRange.get();
        if (player.distanceToSqr(entity) > limit * limit) {
            return false;
        }
        boolean isPlayer = entity instanceof Player;
        if (isPlayer && FriendManager.isFriend((Player) entity)) {
            return false;
        }
        boolean canAttackType = isPlayer && targetSettings.is("Игроки")
                || !isPlayer && isMob(entity) && targetSettings.is("Мобы");
        if (!canAttackType) {
            return false;
        }
        if (!targetSettings.is("Невидимые") && entity.isInvisible()) {
            return false;
        }
        if (targetSettings.is("Только в броне")) {
            return !entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                    || !entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
                    || !entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty()
                    || !entity.getItemBySlot(EquipmentSlot.FEET).isEmpty();
        }
        return true;
    }

    private boolean isMob(Entity entity) {
        return entity instanceof Monster || entity instanceof Slime || entity instanceof Ghast;
    }

    private static float randomLerp(float min, float max) {
        return min + ThreadLocalRandom.current().nextFloat() * (max - min);
    }

    private long applyTpsScale(long intervalMs) {
        if (!syncTPS.get()) {
            return intervalMs;
        }
        float lag = Math.max(0.0F, (20.0F - ServerTPS.tps()) / 20.0F);
        return (long) (intervalMs * (1.0F + lag * tpsMultiplier.get()));
    }

    private void reset() {
        target = null;
        canCrit = false;
        pendingCritTicks = 0;
        lastAttackTime = 0L;
        lastClickTime = 0L;
        lastTargetSwitchTime = 0L;
        perfectDelay.reset(0L);
    }

    // ==================== FOV-индикатор ====================

    private static void renderFovOverlay(GuiGraphics context) {
        Aura aura = ModuleManager.get(Aura.class);
        if (aura != null) {
            aura.renderFovIndicator(context);
        }
    }

    private void renderFovIndicator(GuiGraphics context) {
        if (!isEnable() || target == null || !checks.is("Отображать FOV")) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        float baseFov = this.fov.get();
        float calcFov = (float) AuraUtils.fovFromCamera(player, target);
        float centerX = context.guiWidth() * 0.5F;
        float centerY = context.guiHeight() * 0.5F;
        boolean allow = calcFov < baseFov;
        float delta = Math.max(5.0F, calcFov - baseFov);

        if (!allow) {
            float percent = delta / Math.max(1.0F, fov.getMax() - baseFov);
            int calcAlpha = Math.round(255.0F * Mth.clamp(1.0F - percent, 0.0F, 1.0F));
            int calcColor = (calcAlpha << 24) | (0xFF0000);
            float calcRadius = Math.max(1.0F, calcFov - delta);
            GuiRender.roundedOutline(context.pose(), centerX - calcFov, centerY - calcFov,
                    calcFov * 2.0F, calcFov * 2.0F, delta, calcColor, calcRadius);
        }
        int baseColor = allow ? 0x00FF00 : 0xFF0000;
        float outline = allow ? 5.0F : delta;
        float baseRadius = Math.max(1.0F, baseFov - outline);
        GuiRender.roundedOutline(context.pose(), centerX - baseFov, centerY - baseFov,
                baseFov * 2.0F, baseFov * 2.0F, outline, baseColor, baseRadius);
    }

    // ==================== Доступ к флагу спринта игрока ====================

    /**
     * Читает флаг "был ли игрок в спринте в прошлом тике".
     * Обеспечивается миксином LocalPlayerAccessor (поле wasSprinting).
     */
    private static boolean wasSprinting(LocalPlayer player) {
        if (player instanceof swerve.mixins.accessor.LocalPlayerAccessor accessor) {
            return accessor.swerve$wasSprinting();
        }
        return false;
    }
}
