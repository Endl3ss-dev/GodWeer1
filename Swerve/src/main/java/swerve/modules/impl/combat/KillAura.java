package swerve.modules.impl.combat;

import swerve.events.movement.MoveInputEvent;
import swerve.managers.FriendManager;
import swerve.managers.ModuleManager;
import swerve.mixins.accessor.LocalPlayerAccessor;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ListSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.modules.impl.combat.rotation.HWRotation;
import swerve.modules.impl.combat.rotation.IntaveRotation;
import swerve.modules.impl.combat.rotation.MatrixRotation;
import swerve.modules.impl.combat.rotation.RotationElement;
import swerve.ui.hud.HudManager;
import swerve.utils.color.ColorUtils;
import swerve.utils.combat.AuraUtils;
import swerve.utils.network.ServerTPS;
import swerve.utils.player.MoveUtil;
import swerve.utils.rotation.Rotation;
import swerve.utils.rotation.RotationController;
import swerve.utils.time.PerfectDelay;
import swerve.ui.newgui.GuiRender;

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

/**
 * KillAura: автоматическая атака ближайших врагов.
 *
 * <p>Порт из Private/src/java/org/privates/client/managers/module/impl/combat/KillAura.java
 * (модуль AuraNew): выбор цели, приоритеты, криты, проверка стен, режимы ударов
 * 1.8/1.9, синхронизация по TPS, FOV-индикатор и молчаливые ротации
 * Intave/Matrix/HW ({@code auraelement/impl}).
 *
 * <p>Адаптация под Swerve:
 * <ul>
 *   <li>ротации портированы как {@link RotationElement} поверх
 *       {@link RotationController}: тело доворачивается на цель, камера остаётся
 *       свободной ({@code FreeLook}, перехват {@code LocalPlayer.turn});</li>
 *   <li>порядок тика как в Private: цель и ротация — до тика игрока
 *       ({@code START_CLIENT_TICK}), удар — в конце тика;</li>
 *   <li>MoveEvent из Private заменён расчётом canCrit в конце тика: движение
 *       уже применено, fallDistance совпадает с тем, что увидит ванильный крит;</li>
 *   <li>фоновый scheduler рандомных критов заменён счётчиком тиков
 *       (атака из чужого потока небезопасна);</li>
 *   <li>мёртвые в оригинале настройки подключены к логике: «Не бить через блоки»,
 *       «Синхронизация по TPS», «Выключить после смерти», «Ломать щит» (тело
 *       shieldBreaker в оригинале реализовано, но не вызывалось);</li>
 *   <li> streams из findTarget заменены одним проходом без аллокаций.</li>
 * </ul>
 *
 * <p>Не портировано (сознательно, вне рамок трёх ротаций): режимы Custom/Smooth/
 * Фантайм/СпукиТайм и остальные элементы {@code auraelement/impl}, элитра-аура
 * ({@code ElytraAuraLogic}), «Sprint Reset», захват цели {@code lockOnTarget} и
 * «Таргетированный» — все они принадлежат Custom-ротации и её настройкам.
 */
public final class KillAura extends Module {

    private static final String ATTACK_19 = "1.9";
    private static final String ATTACK_18 = "1.8";

    /** Лимит частоты ударов режима 1.9, мс (из Private). */
    private static final long RATE_LIMIT_19_MS = 500L;
    /** Порог восстановления кулдауна атаки для удара (cooldownComplete из Private). */
    private static final float COOLED_ATTACK_STRENGTH = 1.5F;

    // [+] Ротации: имена ровно те, что в ModeSetting componentMode из Private.
    private static final String ROTATION_INTAVE = "Intave";
    private static final String ROTATION_MATRIX = "Matrix";
    private static final String ROTATION_HW = "HW";
    private static final String CORRECTION_SILENT = "Незаметная";
    private static final String CORRECTION_FOCUSED = "Сфокусированная";
    private static final String CORRECTION_ROUGH = "Грубая";

    // ---- настройки (живые из Private) ----

    private final FloatSetting fov = new FloatSetting("Угол обзора", 180.0F, 30.0F, 180.0F, 1.0F);
    private final FloatSetting attackRange = new FloatSetting("Дистанция", 3.0F, 3.0F, 6.0F, 0.1F);
    private final FloatSetting preRange = new FloatSetting("Доп дистанция", 1.0F, 0.0F, 3.0F, 0.1F);
    private final FloatSetting elRange = new FloatSetting("Дистанция на элитре", 3.0F, 1.0F, 5.0F, 0.1F);
    private final BooleanSetting noHitThroughBlocks = new BooleanSetting("Не бить через блоки", true);
    private final BooleanSetting smartDistance = new BooleanSetting("Умная дистанция", false);
    private final BooleanSetting smartHitbox = new BooleanSetting("Улучшить хитбоксы", false);
    private final BooleanSetting syncTPS = new BooleanSetting("Синхронизация по TPS", false);
    private final FloatSetting tpsMultiplier = new FloatSetting("Множитель TPS", 0.5F, 0.1F, 2.0F, 0.1F)
            .visible(syncTPS::get);
    private final ListSetting targetSettings = ListSetting.of("Кого атаковать",
            BooleanSetting.of("Игроки", true),
            BooleanSetting.of("Мобы", true),
            BooleanSetting.of("Невидимые", true),
            BooleanSetting.of("Только в броне", false));
    private final BooleanSetting onlyCrits = new BooleanSetting("Только криты", true);
    private final BooleanSetting smartCrits = new BooleanSetting("Умные криты", false)
            .visible(onlyCrits::get);
    private final BooleanSetting randomCrits = new BooleanSetting("Рандом криты", false)
            .visible(onlyCrits::get);
    private final BooleanSetting shieldBreaker = new BooleanSetting("Ломать щит", true);
    private final BooleanSetting priorityTargeting = new BooleanSetting("Приоритетное наведение", false);
    private final ModeSetting priorityMode = new ModeSetting("Режим приоритета", "Ближайший",
            "Ближайший", "Слабейший", "Низкое HP", "Опасный")
            .visible(priorityTargeting::get);
    private final BooleanSetting targetSwitchDelay = new BooleanSetting("Задержка смены цели", false);
    private final FloatSetting minSwitchDelay = new FloatSetting("Мин. задержка смены (мс)", 50.0F, 0.0F, 500.0F, 10.0F)
            .visible(targetSwitchDelay::get);
    private final FloatSetting maxSwitchDelay = new FloatSetting("Макс. задержка смены (мс)", 150.0F, 50.0F, 1000.0F, 10.0F)
            .visible(targetSwitchDelay::get);
    private final ListSetting checks = ListSetting.of("Прочее",
            BooleanSetting.of("Отображать FOV", false),
            BooleanSetting.of("Не бить когда ешь", false),
            BooleanSetting.of("Бить только с оружием", false),
            BooleanSetting.of("Выключить после смерти", true));
    private final ModeSetting attackMode = new ModeSetting("Режим ударов", ATTACK_19, ATTACK_19, ATTACK_18);
    private final FloatSetting dynamicCps = new FloatSetting("CPS (1.8)", 12.0F, 8.0F, 20.0F, 1.0F)
            .visible(() -> attackMode.is(ATTACK_18));
    private final FloatSetting cooldown = new FloatSetting("Кулдаун", 0.93F, 0.5F, 1.0F, 0.01F)
            .visible(() -> attackMode.is(ATTACK_19));

    // [+] Ротации: только три портированные (componentMode из Private содержит ещё 13 режимов,
    // которых в Swerve нет — мёртвые значения в списке не оставляем).
    private final ModeSetting rotationMode = new ModeSetting("Ротации", ROTATION_INTAVE,
            ROTATION_INTAVE, ROTATION_MATRIX, ROTATION_HW);
    private final ModeSetting movementCorrection = new ModeSetting("Коррекция движения", CORRECTION_SILENT,
            CORRECTION_SILENT, CORRECTION_FOCUSED, CORRECTION_ROUGH);

    // ---- состояние ----

    private final PerfectDelay perfectDelay = new PerfectDelay();
    /** Аккумуляторы поиска цели (переиспользуются, аллокаций в тике нет). */
    private final TargetScan scan = new TargetScan();

    // [+] Элементы ротаций живут столько же, сколько модуль: состояние сглаживания
    // между включениями не пересоздаётся (как в Private, где элементы в мапе компонента).
    private final RotationElement intaveRotation = new IntaveRotation();
    private final RotationElement matrixRotation = new MatrixRotation();
    private final RotationElement hwRotation = new HWRotation();
    private RotationElement rotationElement;
    private LivingEntity previousTarget;

    private LivingEntity target;
    private boolean canCrit;
    /** Отложенный удар рандомного крита, в тиках (scheduler из Private). */
    private int pendingCritTicks;
    private long lastAttackTime;
    private long lastClickTime;
    private long lastTargetSwitchTime;

    public KillAura() {
        super("KillAura", "Автоматически атакует ближайших врагов", ModuleCategory.COMBAT);
        addSettings(fov, attackRange, preRange, elRange, noHitThroughBlocks, smartDistance,
                smartHitbox, syncTPS, tpsMultiplier, targetSettings, onlyCrits, smartCrits,
                randomCrits, shieldBreaker, priorityTargeting, priorityMode, targetSwitchDelay,
                minSwitchDelay, maxSwitchDelay, checks, attackMode, dynamicCps, cooldown,
                rotationMode, movementCorrection);
        // Статическая ссылка: один канонический объект на call-site —
        // повторная инициализация модулей не плодит дубликаты слоя.
        HudManager.registerOverlay(KillAura::renderFovOverlay);
    }

    @Override
    public void onEnable() {
        syncRotationElement();
        reset();
    }

    @Override
    public void onDisable() {
        // AuraNew.onDisable: reset() → resetRotation() → element.onDisable().
        reset();
        RotationController.resetRotation();
        if (rotationElement != null) {
            rotationElement.onDisable();
        }
    }

    /**
     * Тик ротации (START_CLIENT_TICK, до тика игрока): тело доворачивается на цель,
     * и в этом же тике уходит пакет позиции — как GameUpdateEvent в Private.
     */
    public static void rotationTick(Minecraft minecraft) {
        KillAura aura = ModuleManager.get(KillAura.class);
        if (aura != null && aura.isEnable()) {
            aura.updateRotationTick(minecraft);
        }
    }

    private void updateRotationTick(Minecraft minecraft) {
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

        syncRotationElement();
        updateTarget(minecraft, player);
        if (target == null || rotationElement == null) {
            return;
        }
        rotationElement.update(target);
    }

    /** Смена режима ротации на лету (initRotationElements из Private). */
    private void syncRotationElement() {
        RotationElement wanted;
        if (rotationMode.is(ROTATION_MATRIX)) {
            wanted = matrixRotation;
        } else if (rotationMode.is(ROTATION_HW)) {
            wanted = hwRotation;
        } else {
            wanted = intaveRotation;
        }
        if (wanted == rotationElement) {
            return;
        }
        if (rotationElement != null) {
            rotationElement.onDisable();
        }
        rotationElement = wanted;
        rotationElement.onEnable();
    }

    /**
     * Коррекция движения при молчаливой ротации (RotationComponent/MoveInputEvent из Private).
     * Тело развёрнуто на цель, поэтому WASD пересчитывается от камеры.
     */
    public void fixMovement(MoveInputEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || target == null) {
            return;
        }
        if (movementCorrection.is(CORRECTION_ROUGH)) {
            // applyGrubayaPull: направление ввода считается от угла на цель.
            double dx = target.getX() - player.getX();
            double dz = target.getZ() - player.getZ();
            if (Math.sqrt(dx * dx + dz * dz) < 0.2D) {
                return;
            }
            float yawToTarget = Mth.wrapDegrees((float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
            MoveUtil.fixMovementToTarget(event, player.getYRot(), yawToTarget);
            return;
        }
        if (!RotationController.isRotating()) {
            return;
        }
        float cameraYaw = Rotation.cameraYaw();
        if (movementCorrection.is(CORRECTION_FOCUSED)) {
            MoveUtil.fixMovement2(event);
        } else {
            MoveUtil.fixMovement(event, cameraYaw);
        }
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.gameMode == null) {
            // Смена мира/выход: цель и таймеры сбрасываются (WorldChangeEvent из Private).
            reset();
            return;
        }
        // [+] Цель и ротация обновляются в начале тика (rotationTick), здесь — добивание:
        // криты, отложенный удар и сама атака уже по свежим углам тела.
        // Страховки повторяются: между START- и END-тиком состояние могло измениться.
        if (player.isSpectator() || player.isDeadOrDying()) {
            return;
        }

        // MoveEvent из Private: canCrit предсказывался в момент движения.
        // В конце тика движение уже применено, fallDistance — тот же, что
        // увидит ванильная проверка крита в момент удара.
        canCrit = !player.onGround()
                && player.fallDistance > ThreadLocalRandom.current().nextFloat() * 0.15F;

        // Отложенный удар рандомного крита (scheduler из Private, 1-2 тика).
        if (pendingCritTicks > 0) {
            pendingCritTicks--;
            if (pendingCritTicks == 0 && target != null && target.isAlive() && !target.isRemoved()) {
                attackEntity(minecraft, player, target);
            }
        }

        updateAttack(minecraft, player);
    }

    /** Цель для соседних модулей (ElytraFlight). */
    public LivingEntity getTarget() {
        return target;
    }

    /**
     * Исходящие пакеты (PacketEvent из Private): смена предмета и свинг
     * руки сбрасывают PerfectDelay.
     */
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

    // ---- выбор цели ----

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
        if (target != null
                && (!isValidTarget(player, target) || AuraUtils.fovFromCamera(player, target) > fov.get())) {
            target = null;
        }
        if (previousTarget != null && target == null) {
            // AuraNew.updateTarget: сброс накопленного lerp и плавный возврат камеры.
            if (rotationElement != null) {
                rotationElement.resetLerp();
            }
            RotationController.smoothResetRotation();
        }
        previousTarget = target;
    }

    /**
     * Поиск цели за один проход без аллокаций (findTarget/findPriorityTarget
     * из Private: ближний/слабейший/низкое HP/опасный считаются одновременно,
     * streams и промежуточные списки убраны).
     */
    private LivingEntity findTarget(Minecraft minecraft, LocalPlayer player) {
        double searchRange = attackRange.get() + preRange.get();
        double searchRangeSquared = searchRange * searchRange;
        scan.reset();

        Iterable<Entity> entities = minecraft.level.entitiesForRendering();
        // Список отдаётся без обёртки: идём по индексу, без аллокаций в тике.
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

    /**
     * Аккумуляторы поиска цели: один экземпляр на модуль, переиспользуется
     * между тиками (замена списков из findPriorityTarget оригинала).
     */
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
                    // Цель ниже половины HP, иначе первая попавшаяся (оригинал).
                    return lowHp != null ? lowHp : first;
                case "Опасный":
                    return firstPlayer != null ? firstPlayer : first;
                default:
                    return nearest;
            }
        }
    }

    // ---- атака ----

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

        // Классика критов: удар в спринте критом не считается — ждём,
        // пока сервер узнает об остановке спринта (isServerSprintState из Private).
        if (!isInLiquid(player) && player.isSprinting()
                && ((LocalPlayerAccessor) player).swerve$wasSprinting()) {
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

    /**
     * Готовность к удару (shouldAttack из Private): пауза PerfectDelay,
     * жидкость и условия критов.
     */
    private boolean shouldAttack(LocalPlayer player) {
        if (!perfectDelay.cooldownComplete()) {
            return false;
        }
        if (isInLiquid(player)) {
            return false;
        }
        // checkReturn из Private: не бить во время еды и без оружия.
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

    /** Ванильные условия критического удара (shouldCritical из Private). */
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

    /** Удар (attackEntity из Private) с лимитами частоты и кулдауном. */
    private void attackEntity(Minecraft minecraft, LocalPlayer player, Entity entity) {
        if (!attackReady(player)) {
            return;
        }
        minecraft.gameMode.attack(player, entity);
        player.swing(InteractionHand.MAIN_HAND);
        player.resetAttackStrengthTicker();
        lastAttackTime = System.currentTimeMillis();
    }

    /**
     * Готовность к удару режима 1.9: лимит частоты из Private (500 мс, с
     * поправкой на TPS) и восстановление кулдауна атаки (cooldownComplete
     * из Private, 0.93).
     */
    private boolean attackReady(LocalPlayer player) {
        if (!attackMode.is(ATTACK_19)) {
            return true;
        }
        if (System.currentTimeMillis() - lastAttackTime < applyTpsScale(RATE_LIMIT_19_MS)) {
            return false;
        }
        return player.getAttackStrengthScale(COOLED_ATTACK_STRENGTH) >= cooldown.get();
    }

    /**
     * Ломание щита топором (shieldBreaker из Private — в оригинале тело
     * существовало, но не вызывалось): свап топора в руку, удар, свап обратно.
     *
     * @return true, если вместо обычного удара выполнен удар топором
     */
    private boolean breakShield(Minecraft minecraft, LocalPlayer player) {
        if (!target.isBlocking()) {
            return false;
        }
        int axeHotbarSlot = findAxeHotbarSlot(player);
        if (axeHotbarSlot < 0) {
            return false;
        }
        // inventoryMenu: контейнерные слоты 36..44 — хотбар; ClickType.SWAP
        // с кнопкой = выбранный слот меняет местами выбранный слот с топором.
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

    /** Слот хотбара с топором, -1 если нет или топор уже в руке. */
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

    // ---- проверки прицела и дистанции ----

    /**
     * Проверка прицела (rayTrace из Private): без ротаций луч берётся из
     * текущего взгляда игрока. Умные хитбоксы заменяют проверку дистанцией
     * до ближайшей видимой точки цели (fallback на грудь — как в оригинале).
     */
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
        // «Не бить через блоки» (в оригинале настройка была мёртвой): цель
        // атакуется, только если видима хотя бы в одну точку хитбокса.
        return !noHitThroughBlocks.get() || AuraUtils.hasVisiblePoint(player, target);
    }

    /** Дистанция удара (attackDistance из Private): элитра, умная дистанция, креатив. */
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

    /** Жидкость (isInLiquid из Private): плавание или глаза в воде/лаве. */
    private boolean isInLiquid(LocalPlayer player) {
        return player.isVisuallySwimming()
                || player.isSwimming() && player.isEyeInFluid(FluidTags.WATER)
                || player.isEyeInFluid(FluidTags.LAVA);
    }

    // ---- фильтры цели ----

    /** Пригодность цели (isValidTarget из Private). */
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

    /** Мобы-цели (isMob из Private). */
    private boolean isMob(Entity entity) {
        return entity instanceof Monster || entity instanceof Slime || entity instanceof Ghast;
    }

    /** Случайное значение в [min, max] (randomLerp из Private). */
    private static float randomLerp(float min, float max) {
        return min + ThreadLocalRandom.current().nextFloat() * (max - min);
    }

    /**
     * Масштаб интервала ударов по TPS сервера: чем ниже TPS, тем реже удары.
     * Множитель управляет силой коррекции (1.0 — линейно от просадки).
     */
    private long applyTpsScale(long intervalMs) {
        if (!syncTPS.get()) {
            return intervalMs;
        }
        float lag = Math.max(0.0F, (20.0F - ServerTPS.tps()) / 20.0F);
        return (long) (intervalMs * (1.0F + lag * tpsMultiplier.get()));
    }

    // ---- FOV-индикатор (Render2DEvent из Private) ----

    /** Точка входа HUD-слоя: находит активный экземпляр модуля и рисует индикатор. */
    private static void renderFovOverlay(GuiGraphics context) {
        KillAura killAura = ModuleManager.get(KillAura.class);
        if (killAura != null) {
            killAura.renderFovIndicator(context);
        }
    }

    /** Квадрат-индикатор вокруг прицела: зелёный — цель в угле обзора, красный — вне. */
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
            int calcColor = (calcAlpha << 24) | (ColorUtils.RED.getRGB() & 0xFFFFFF);
            float calcRadius = Math.max(1.0F, calcFov - delta);
            GuiRender.roundedOutline(context.pose(), centerX - calcFov, centerY - calcFov,
                    calcFov * 2.0F, calcFov * 2.0F, delta, calcColor, calcRadius);
        }
        int baseColor = allow ? ColorUtils.GREEN.getRGB() : ColorUtils.RED.getRGB();
        float outline = allow ? 5.0F : delta;
        float baseRadius = Math.max(1.0F, baseFov - outline);
        GuiRender.roundedOutline(context.pose(), centerX - baseFov, centerY - baseFov,
                baseFov * 2.0F, baseFov * 2.0F, outline, baseColor, baseRadius);
    }

    private void reset() {
        target = null;
        previousTarget = null;
        canCrit = false;
        pendingCritTicks = 0;
        lastAttackTime = 0L;
        lastClickTime = 0L;
        lastTargetSwitchTime = 0L;
        perfectDelay.reset(0L);
    }
}
