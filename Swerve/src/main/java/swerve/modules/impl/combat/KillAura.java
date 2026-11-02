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
import swerve.utils.rotation.FreeLook;
import swerve.utils.rotation.Rotation;
import swerve.utils.rotation.RotationController;
import swerve.ui.newgui.GuiRender;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
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
 *   <li>удар 1.9 ждёт полную перезарядку оружия ({@code getAttackStrengthScale}),
 *       удар 1.8 — только интервал CPS: режимы больше не совпадают по темпу;</li>
 *   <li>отложенных ударов в обход гейтов нет: «Рандом криты» лишь пропускают
 *       часть окон крита, наземный удар при «Только криты» невозможен;</li>
 *   <li>ручные удары вынесены в отдельную настройку: аура наводит, бьёт игрок;</li>
 *   <li>мёртвые в оригинале настройки подключены к логике: «Не бить через блоки»,
 *       «Синхронизация по TPS», «Выключить после смерти», «Ломать щит» (тело
 *       shieldBreaker в оригинале реализовано, но не вызывалось);</li>
 *   <li>streams из findTarget заменены одним проходом без аллокаций;</li>
 *   <li> Sprint Reset портирован из MoveInput-хука {@code AuraNew.onInput}: без него
 *       удар в спринте отменялся, а спринт не сбрасывался — криты не проходили;</li>
 *   <li> камера настраивается отдельно: «Догоняет» (по умолчанию) — ротация
 *       молчаливая, а камера плавно доворачивается на цель, только когда мышь
 *       не двигается; «Свободная» — камера целиком за игроком; «Всегда на цели» —
 *       камера жёстко следует за телом (ротация становится видимой).</li>
 *   <li> «Только криты» бьёт строго в окне ванильного крита, а «Авто прыжок»
 *       подпрыгивает под цель, чтобы это окно вообще наступало.</li>
 * </ul>
 *
 * <p>Не портировано (сознательно, вне рамок трёх ротаций): режимы Custom/Smooth/
 * Фантайм/СпукиТайм и остальные элементы {@code auraelement/impl}, элитра-аура
 * ({@code ElytraAuraLogic}), захват цели {@code lockOnTarget} и «Таргетированный» —
 * все они принадлежат Custom-ротации и её настройкам.
 */
public final class KillAura extends Module {

    private static final String ATTACK_19 = "1.9";
    private static final String ATTACK_18 = "1.8";

    // [+] Ротации: имена ровно те, что в ModeSetting componentMode из Private.
    private static final String ROTATION_INTAVE = "Intave";
    private static final String ROTATION_MATRIX = "Matrix";
    private static final String ROTATION_HW = "HW";
    private static final String CORRECTION_SILENT = "Незаметная";
    private static final String CORRECTION_FOCUSED = "Сфокусированная";
    private static final String CORRECTION_ROUGH = "Грубая";

    // [+] Sprint Reset: живые значения из ModeSetting sprintReset в Private
    // («Пакетный (RAGE)» и «Обычный (RAGE)» в списке отсутствуют — эти ветки мертвы).
    private static final String SPRINT_NORMAL = "Обычный";
    private static final String SPRINT_LEGIT = "Легитный";
    private static final String COOLDOWN_VANILLA = "Ванильный";
    private static final String COOLDOWN_CUSTOM_SWORD = "Кастомный меч";

    // [+] Поведение камеры при ротации. «Догоняет» — молчаливая ротация + плавное
    // наведение камеры, когда мышь стоит: раньше жёсткий «Видимая» складывал шаг
    // ауры с движением мыши, из-за чего чувствительность ощущалась выше.
    private static final String CAMERA_OFF = "Свободная";
    private static final String CAMERA_CATCH_UP = "Догоняет";
    private static final String CAMERA_ALWAYS = "Всегда на цели";

    // ---- настройки (живые из Private) ----

    private final FloatSetting fov = new FloatSetting("Угол обзора", 180.0F, 30.0F, 180.0F, 1.0F);
    private final FloatSetting attackRange = new FloatSetting("Дистанция", 3.0F, 3.0F, 6.0F, 0.1F);
    private final FloatSetting preRange = new FloatSetting("Доп дистанция", 1.0F, 0.0F, 3.0F, 0.1F);
    private final FloatSetting elRange = new FloatSetting("Дистанция на элитре", 3.0F, 1.0F, 5.0F, 0.1F);
    private final BooleanSetting noHitThroughBlocks = new BooleanSetting("Не бить через блоки", true);
    private final BooleanSetting smartHitbox = new BooleanSetting("Улучшить хитбоксы", false);
    private final BooleanSetting syncTPS = new BooleanSetting("Синхронизация по TPS", false);
    private final FloatSetting tpsMultiplier = new FloatSetting("Множитель TPS", 0.5F, 0.1F, 2.0F, 0.1F)
            .visible(syncTPS::get);
    private final ListSetting targetSettings = ListSetting.of("Кого атаковать",
            BooleanSetting.of("Игроки", true),
            BooleanSetting.of("Мобы", true),
            BooleanSetting.of("Невидимые", true),
            BooleanSetting.of("Только в броне", false));
    // Как в Private: бьём только тогда, когда удар засчитается критом.
    private final BooleanSetting onlyCrits = new BooleanSetting("Только криты", true);
    // [+] Ручные удары: аура только наводит тело на цель, а бьёт игрок кликом.
    // Раньше это было единственным рабочим поведением; теперь — осознанная опция.
    private final BooleanSetting manualAttack = new BooleanSetting("Ручные удары (без автобоя)", false);
    // [+] «Рандом криты» больше не отменяют условия крита: они лишь пропускают
    // часть окон (30 %) — удар остаётся человечнее, но наземным быть не может.
    private final BooleanSetting randomCrits = new BooleanSetting("Рандом криты", false)
            .visible(onlyCrits::get);
    // [+] Авто прыжок (AuraNew.onInput): подпрыгиваем под цель, чтобы попасть в окно
    // крита — без него «Только криты» молчит, пока игрок не прыгнет сам. В Private
    // настройка выключена по умолчанию; здесь включена, иначе криты «не работают».
    private final BooleanSetting autoJump = new BooleanSetting("Авто прыжок", true);
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
    // 1.8: единственный лимитер — CPS, перезарядки оружия в этой версии нет.
    private final FloatSetting dynamicCps = new FloatSetting("CPS (1.8)", 12.0F, 8.0F, 20.0F, 1.0F)
            .visible(() -> attackMode.is(ATTACK_18));
    // [+] 1.9: удар разрешён только после полной перезарядки оружия. Жёсткие
    // миллисекунды Private (250/450/550/850) не совпадали с реальной скоростью
    // оружия, поэтому здесь ванильный attackStrengthTicker, а «Кастомный меч» —
    // необязательная ДОПОЛНИТЕЛЬНАЯ пауза поверх перезарядки.
    private final ModeSetting cooldownMode = new ModeSetting("Режим кулдауна", COOLDOWN_VANILLA,
            COOLDOWN_VANILLA, COOLDOWN_CUSTOM_SWORD)
            .visible(() -> attackMode.is(ATTACK_19));
    private final FloatSetting customSwordCooldown = new FloatSetting("Кулдаун меча (мс)", 450.0F, 50.0F, 1000.0F, 10.0F)
            .visible(() -> attackMode.is(ATTACK_19) && cooldownMode.is(COOLDOWN_CUSTOM_SWORD));
    // [+] Sprint Reset: без него удар в спринте отменяется, но спринт не сбрасывается —
    // криты не наносятся никогда (в оригинале это делал MoveInput-хук).
    private final ModeSetting sprintReset = new ModeSetting("Sprint Reset", SPRINT_NORMAL,
            SPRINT_NORMAL, SPRINT_LEGIT);

    // [+] Ротации: только три портированные (componentMode из Private содержит ещё 13 режимов,
    // которых в Swerve нет — мёртвые значения в списке не оставляем).
    private final ModeSetting rotationMode = new ModeSetting("Ротации", ROTATION_INTAVE,
            ROTATION_INTAVE, ROTATION_MATRIX, ROTATION_HW);
    private final ModeSetting cameraMode = new ModeSetting("Камера", CAMERA_CATCH_UP,
            CAMERA_CATCH_UP, CAMERA_OFF, CAMERA_ALWAYS);
    private final ModeSetting movementCorrection = new ModeSetting("Коррекция движения", CORRECTION_SILENT,
            CORRECTION_SILENT, CORRECTION_FOCUSED, CORRECTION_ROUGH);

    // ---- состояние ----

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
    private long lastAttackTime;
    private long lastTargetSwitchTime;
    /** Сброс спринта: сколько тиков держать forward = 0 (поле {@code p} из Private). */
    private int sprintStopTicks;
    private int sprintTicks;
    private long pressTime;

    public KillAura() {
        super("KillAura", "Автоматически атакует ближайших врагов", ModuleCategory.COMBAT);
        addSettings(fov, attackRange, preRange, elRange, noHitThroughBlocks,
                smartHitbox, syncTPS, tpsMultiplier, targetSettings, onlyCrits, manualAttack,
                randomCrits, shieldBreaker, priorityTargeting, priorityMode, targetSwitchDelay,
                minSwitchDelay, maxSwitchDelay, checks, attackMode, dynamicCps,
                cooldownMode, customSwordCooldown, sprintReset, autoJump,
                rotationMode, cameraMode, movementCorrection);
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
        // AuraNew.onDisable: reset() → element.onDisable() → stopRotation().
        // Порядок важен: Matrix в onDisable просит ускоренный возврат, а stopRotation
        // выполняется последним и сливает углы тела с камерой — выключение не дёргает обзор.
        reset();
        if (rotationElement != null) {
            rotationElement.onDisable();
        }
        RotationController.stopRotation();
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
        // Меню/пауза: тело не крутим, иначе игрок «плывёт» в инвентаре.
        if (minecraft.screen != null || minecraft.isPaused()) {
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
        // Камера: «Всегда на цели» — тело и камера вместе (свободный обзор выключен),
        // остальные режимы — молчаливая ротация, камера остаётся за мышью.
        RotationController.setVisible(cameraMode.is(CAMERA_ALWAYS));
        FreeLook.setFollowIdle(cameraMode.is(CAMERA_CATCH_UP));
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
        // Приоритеты элементов разные (Intave 4, Matrix/HW 3) — захват снимается,
        // иначе после Intave более низкий приоритет никогда не получит тик.
        RotationController.resetPriority();
        rotationElement.onEnable();
    }

    /**
     * Коррекция движения при молчаливой ротации (RotationComponent/MoveInputEvent из Private).
     * Тело развёрнуто на цель, поэтому WASD пересчитывается от камеры.
     */
    public void fixMovement(MoveInputEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        // [+] Авто прыжок (AuraNew.onInput): подпрыгиваем под цель, чтобы удар попал
        // в окно крита. Прыгаем только когда оружие уже перезаряжено — иначе игрок
        // прыгал бы без остановки весь кулдаун. В ручном режиме ввод не трогаем.
        // Место над головой проверяется, чтобы не прыгать впустую в коридоре.
        if (autoJump.get() && !manualAttack.get() && target != null && attackReady(player)
                && player.onGround() && !player.isInWater() && !player.isInLava()
                && AuraUtils.hasHeadroom(player)) {
            event.jump = true;
        }

        // [+] Sprint Reset (AuraNew.onInput): пока счётчик держится, forward обнуляется —
        // ванильный спринт гаснет, сервер перестаёт видеть спринт, удар проходит критом.
        if (sprintStopTicks > 0) {
            event.forward = 0.0F;
            sprintStopTicks--;
            return;
        }
        if (sprintReset.is(SPRINT_LEGIT) && sprintTicks > 0) {
            event.forward = 0.0F;
            sprintTicks--;
            return;
        }
        if (sprintReset.is(SPRINT_LEGIT) && pressTime > 0 && System.currentTimeMillis() >= pressTime) {
            event.forward = 1.0F;
            pressTime = 0L;
        }

        if (target == null) {
            return;
        }
        // Когда камера жёстко на цели, она совпадает с телом: пересчёт WASD не нужен.
        if (cameraMode.is(CAMERA_ALWAYS)) {
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
        if (minecraft.screen != null || minecraft.isPaused()) {
            return;
        }
        // [+] Цель и ротация обновляются в начале тика (rotationTick), здесь — сама
        // атака уже по свежим углам тела. Удар проверяет всё заново в updateAttack:
        // отложенных ударов в обход гейтов больше нет.
        if (player.isSpectator() || player.isDeadOrDying()) {
            return;
        }
        updateAttack(minecraft, player);
    }

    /** Цель для соседних модулей (ElytraFlight); выключенная аура цели не отдаёт. */
    public LivingEntity getTarget() {
        return isEnable() ? target : null;
    }

    // ---- выбор цели ----

    private void updateTarget(Minecraft minecraft, LocalPlayer player) {
        LivingEntity potential = findTarget(minecraft, player);
        if (target != potential) {
            // Задержка смены цели не должна удерживать мёртвую/исчезнувшую цель:
            // иначе аура простаивает весь таймер, пока труп ещё в списке.
            boolean currentUsable = target != null && target.isAlive() && !target.isRemoved();
            if (targetSwitchDelay.get() && currentUsable) {
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
        if (target == null || !target.isAlive() || target.isRemoved()) {
            return;
        }
        // [+] Ручные удары: аура наводит тело и держит цель, но удар делает игрок
        // кликом (ванильный attack идёт по углам тела). Так сохраняется ручной
        // вариант, не дублируя удар двумя пакетами за один тик.
        if (manualAttack.get()) {
            return;
        }

        // Порядок оригинала: checkReturn → темп оружия → сброс спринта → гейт крита.
        // Сброс спринта стоит ПОСЛЕ проверки перезарядки намеренно: иначе forward
        // обнулялся бы каждый тик, пока игрок просто бежит с включённой аурой,
        // и спринт не включался бы вообще.
        if (checkReturn(player) || !attackReady(player) || !clickIntervalReady()) {
            return;
        }

        // Классика критов: удар в спринте критом не считается, поэтому спринт гасится
        // ДО гейта крита (в Private сброс живёт внутри гейта, но там сам гейт спринт
        // не проверяет). Обычный — forward 0 на тик, Легитный — отпускание на 1..2 тика.
        // Бьём только когда сервер уже увидел остановку спринта.
        boolean liquid = isInLiquid(player);
        if (!liquid && player.isSprinting()) {
            if (sprintReset.is(SPRINT_NORMAL)) {
                sprintStopTicks = 1;
            } else if (sprintReset.is(SPRINT_LEGIT)) {
                sprintTicks = ThreadLocalRandom.current().nextInt(1, 3);
                pressTime = System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(15, 51);
            }
            if (((LocalPlayerAccessor) player).swerve$wasSprinting()) {
                return;
            }
        }

        if (!shouldAttack(player)) {
            return;
        }
        if (!rayTraceOk(player) || AuraUtils.strictDistance(player, target) >= attackDistance(player)) {
            return;
        }
        // Щит ломаем только когда удар уже разрешён: иначе свап топора пройдёт
        // без удара, и предмет зря слетает из руки.
        if (shieldBreaker.get() && breakShield(minecraft, player)) {
            return;
        }
        attackEntity(minecraft, player, target);
    }

    /** checkReturn из Private: не бить во время еды и без оружия. */
    private boolean checkReturn(LocalPlayer player) {
        boolean eating = player.isUsingItem() && checks.is("Не бить когда ешь");
        boolean noWeapon = !isHoldingWeapon(player) && checks.is("Бить только с оружием");
        return eating || noWeapon;
    }

    /**
     * Готовность 1.9: удар только после полной перезарядки оружия.
     *
     * <p>Ванильный {@code Player.attack} считает урон по
     * {@code getAttackStrengthScale(0.5F)}; при шкале 1.0 урон максимален, а крит
     * засчитывается только при {@code scale > 0.9}. Жёсткие миллисекунды Private
     * (250/450/550/850) не совпадали с реальной ATTACK_SPEED оружия — отсюда были
     * и «удары до перезарядки», и лишние паузы. В 1.8 перезарядки нет — там темп
     * задаёт {@link #clickIntervalReady()}.
     *
     * <p>«Кастомный меч» добавляет к полной перезарядке собственную паузу в мс.
     */
    private boolean attackReady(LocalPlayer player) {
        if (attackMode.is(ATTACK_18)) {
            return true;
        }
        if (player.getAttackStrengthScale(0.5F) < 1.0F) {
            return false;
        }
        if (cooldownMode.is(COOLDOWN_CUSTOM_SWORD)
                && player.getMainHandItem().getItem() instanceof SwordItem) {
            return System.currentTimeMillis() - lastAttackTime >= (long) customSwordCooldown.get();
        }
        return true;
    }

    /**
     * Темп 1.8: CPS из настройки с разбросом 0.8..1.2 и поправкой на TPS.
     * Для 1.9 всегда true — там работает перезарядка оружия.
     */
    private boolean clickIntervalReady() {
        if (!attackMode.is(ATTACK_18)) {
            return true;
        }
        long interval = applyTpsScale((long) (1000.0F / (dynamicCps.get() * randomLerp(0.8F, 1.2F))));
        // Быстрее одного удара за тик 1.8 всё равно не может — 50 мс это нижняя граница.
        return System.currentTimeMillis() - lastAttackTime >= Math.max(50L, interval);
    }

    /**
     * Гейт крита (shouldAttack из AuraNew).
     *
     * <p>При «Только криты» удар разрешён исключительно в окне ванильного крита.
     * Раньше отсюда было три обхода: «Умные криты» разрешали удар с земли по
     * {@code onGround && verticalCollision}, жидкость и элитра возвращали
     * {@code true} до проверок, а «Рандом криты» с шансом 30 % игнорировали
     * условия целиком — поэтому аура и била стоя на земле.
     *
     * <p>«Рандом криты» сохранены как человекоподобность: часть окон намеренно
     * пропускается, но наземный удар по-прежнему невозможен.
     */
    private boolean shouldAttack(LocalPlayer player) {
        if (!onlyCrits.get()) {
            return true;
        }
        if (!critWindow(player)) {
            return false;
        }
        return !randomCrits.get() || ThreadLocalRandom.current().nextFloat() >= 0.3F;
    }

    /**
     * Окно критического удара — ровно те условия, по которым ванильный
     * {@code Player.attack} начисляет крит: падаем, не на земле, не на лестнице,
     * не в воде, без слепоты, не пассажир, не в спринте. Дополнительно сохранены
     * блокеры {@code shouldCritical} из Private: левитация и паутина — в них
     * «падение» обманчиво, а крит сервер всё равно не начислит.
     *
     * <p>Проверки «места над головой» здесь нет: ванильный крит её не требует,
     * а в пещерах она молча запрещала любые удары. Исключений по земле тоже нет
     * по построению: {@code !onGround} — обязательное условие, а не ветвь.
     */
    private boolean critWindow(LocalPlayer player) {
        return player.fallDistance > 0.0F
                && !player.onGround()
                && !player.onClimbable()
                && !player.isInWater()
                && !player.hasEffect(MobEffects.BLINDNESS)
                && !player.hasEffect(MobEffects.LEVITATION)
                && !player.isPassenger()
                && !player.isSprinting()
                && !player.isFallFlying()
                && !AuraUtils.isPlayerInWeb(player);
    }

    /**
     * Удар с темпом режима.
     *
     * <p>1.8: перезарядки нет — интервал CPS уже проверен в
     * {@link #clickIntervalReady()}. Порядок пакетов как в {@code AttackOrder}
     * Private для протокола ≤ 1.8: сначала swing, затем attack.
     *
     * <p>1.9: удар разрешён только при полной перезарядке (страховка поверх
     * {@link #attackReady}), порядок ванильный: attack, затем swing.
     */
    private void attackEntity(Minecraft minecraft, LocalPlayer player, Entity entity) {
        if (attackMode.is(ATTACK_18)) {
            player.swing(InteractionHand.MAIN_HAND);
            minecraft.gameMode.attack(player, entity);
        } else {
            if (player.getAttackStrengthScale(0.5F) < 1.0F) {
                return;
            }
            minecraft.gameMode.attack(player, entity);
            player.swing(InteractionHand.MAIN_HAND);
        }
        lastAttackTime = System.currentTimeMillis();
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
            if (player.getEyePosition(1.0F).distanceTo(visiblePoint) > attackDistance(player)) {
                return false;
            }
            // [+] «Улучшить хитбоксы» не отменяет «Не бить через блоки»: раньше эта
            // ветка возвращала результат сразу, и включение одной настройки молча
            // выключало другую — аура била сквозь стену. hasVisiblePoint останавливается
            // на первой открытой точке, поэтому сверх уже сделанных проверок это обычно
            // ровно один clip.
            return !noHitThroughBlocks.get() || AuraUtils.hasVisiblePoint(player, target);
        }
        if (!AuraUtils.lookRayHitsBox(player, target, attackDistance(player))) {
            return false;
        }
        // «Не бить через блоки» (в оригинале настройка была мёртвой): цель
        // атакуется, только если видима хотя бы в одну точку хитбокса.
        return !noHitThroughBlocks.get() || AuraUtils.hasVisiblePoint(player, target);
    }

    /**
     * Дистанция удара (attackDistance из Private): элитра или обычная цель.
     *
     * <p>В 1.21.4 сервер проверяет удар по
     * {@code eye -> AABB < ENTITY_INTERACTION_RANGE + 3.0} (handleInteract):
     * отдельного «креативного» радиуса больше нет, поэтому потолок берётся из
     * атрибута игрока, а не из захардкоженной шестёрки.
     */
    private double attackDistance(LocalPlayer player) {
        if (player.isFallFlying() || (target != null && target.isFallFlying())) {
            return elRange.get();
        }
        double serverLimit = player.entityInteractionRange() + 3.0D;
        return Math.min(serverLimit, Math.max(3.0D, attackRange.get()));
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
        // Наблюдатель неуязвим: удар по нему — только лишний пакет и звук.
        if (entity instanceof Player other && other.isSpectator()) {
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
        // При молчаливой ротации тело уже на цели, а камера — нет: индикатор обязан
        // считать угол от камеры, иначе он всегда зелёный (баг старой версии).
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        float calcFov = (float) AuraUtils.fovFromAngles(player, target,
                Rotation.cameraYaw(), camera.getXRot());
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
        lastAttackTime = 0L;
        lastTargetSwitchTime = 0L;
        sprintStopTicks = 0;
        sprintTicks = 0;
        pressTime = 0L;
    }
}
