package antileak.base.client.modules.impl.combat;

import com.adl.nativeprotect.Native;
import lombok.Getter;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import antileak.base.Punch;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventAttackEntity;
import antileak.base.api.events.implement.EventGameUpdate;
import antileak.base.api.events.implement.EventKeyboardInput;
import antileak.base.api.events.implement.EventMoveInput;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.api.events.implement.EventUpdatePost;
import antileak.base.api.storages.implement.FreeLookStorage;
import antileak.base.api.storages.implement.NeuroAuraStorage;
import antileak.base.api.storages.implement.RotationStorage;
import antileak.base.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import antileak.base.api.utils.combat.IdealHitUtils;
import antileak.base.api.utils.combat.RayTraceUtil;
import antileak.base.api.utils.input.MovingUtil;
import antileak.base.api.utils.math.MathUtils;
import antileak.base.api.utils.math.TimerUtils;
import antileak.base.api.utils.player.InventoryUtils;
import antileak.base.api.utils.rotate.MultipointUtils;
import antileak.base.api.utils.rotate.Rotation;
import antileak.base.api.utils.rotate.RotationUtils;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.impl.combat.components.AuraBurst;
import antileak.base.client.modules.impl.combat.components.RotationsSystem;
import antileak.base.client.modules.impl.combat.components.interpolation.BestPoint;
import antileak.base.client.modules.impl.combat.components.gcd.GCDUtil;
import antileak.base.client.modules.impl.combat.components.rotations.SpookyTimeRots;
import antileak.base.client.modules.impl.movement.AirStuck;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.FloatSetting;
import antileak.base.client.modules.settings.implement.ListSetting;
import antileak.base.client.modules.settings.implement.ModeSetting;
import antileak.base.mixin.ILivingEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class Aura extends Module {

    public static Aura INSTANCE = new Aura();

    /** Прежняя пауза между ударами режима 1.9, мс. */
    private static final long ATTACK_DELAY_MS = 467L;

    public final ModeSetting rotationType = new ModeSetting("Ротация", "SpookyTime",
            "SpookyTime");

    private final ListSetting targets = new ListSetting("Таргеты",
            new BooleanSetting("Игроки", true),
            new BooleanSetting("Невидимки", true),
            new BooleanSetting("Мирные", false),
            new BooleanSetting("Мобы", true)
    );

    public final FloatSetting range = new FloatSetting("Дистанция атаки", 3f, 0f, 6f, 0.05f);
    private final FloatSetting aimRange = new FloatSetting("Дистанция наводки", 3f, 0f, 6f, 0.05f);
    private final FloatSetting elytraAimRange = new FloatSetting("Дистанция на элитрах", 50f, 10f, 100f, 0.05f);
    public final BooleanSetting smartCrit = new BooleanSetting("Умные криты", false);
    public final BooleanSetting sprintReset = new BooleanSetting("Сброс спринта", true);
    private final BooleanSetting throughWalls = new BooleanSetting("Бить через стены", true);
    private final BooleanSetting raycast = new BooleanSetting("Проверка на наведение", false);
    private final BooleanSetting unpressShield = new BooleanSetting("Отжимать щит", false);
    private final BooleanSetting breakShield = new BooleanSetting("Ломать щит", true);
    private final BooleanSetting attackOnEating = new BooleanSetting("Не бить когда ешь", true);
    public static BooleanSetting clientLook = new BooleanSetting("Наводка от первого лица", false);
    private final ModeSetting priority = new ModeSetting("Приоритет", "Дистанция", "Дистанция", "Здоровье", "Угол", "Никакой");
    private final ModeSetting moveFix = new ModeSetting("Коррекция", "Свободная", "Свободная");

    // Пачки 1.8 (в SkyCore — «1.8 NoDelay») переносятся из AttackAuraModule/AuraBurst.
    private final AuraBurst burst = new AuraBurst();

    @Getter
    private LivingEntity target;
    @Getter
    private Vec2f currentRotations = new Vec2f(0f, 0f);
    @Getter
    private Vec2f targetRotations = new Vec2f(0f, 0f);
    @Getter
    private final NeuroAuraStorage dataSystem = new NeuroAuraStorage();
    @Getter
    private final TimerUtils attackTimer = new TimerUtils();
    private final SpookyTimeRots spookyTimeRots = new SpookyTimeRots();
    private final TimerUtils backTimer = new TimerUtils();

    /** Прежний 467 мс троттлинг ударов режима 1.9; в 1.8 темп задают пачки {@link AuraBurst}. */
    private long cps = 0;
    /** Номер игрового тика: пачка 1.8 уходит не чаще одного раза за тик. */
    private int burstTick = 0;
    @Getter
    private boolean needSprintReset = false;
    private boolean sprintResetDone = false;
    private int sprintResetTicks = 0;
    private int ticksToAttack = 0;
    private int bypassAttackAge = -1;
    private boolean bypassAttackQueued = false;

    public static float adjYaw;
    public static float adjPitch;
    public static float otvodkaYaw;
    public static float otvodkaPitch;

    public boolean isRotated;

    public Aura() {
        super("AttackAura", "Автоматически наводиться и бьёт таргета", ModuleCategory.COMBAT);
        addSettings(rotationType, targets, range, aimRange, elytraAimRange, smartCrit, sprintReset,
                attackOnEating, throughWalls, raycast, unpressShield, breakShield, clientLook, moveFix, priority);
        addSettings(burst.settings);
    }
    @Native
    @EventLink
    public void onPlayerTick(EventUpdate e) {
        if (mc.player == null || mc.world == null) return;

        updateTarget();

        if (dataSystem.isRecording()) {
            LivingEntity recordTarget = findTargetForRecording();
            dataSystem.recordTick(recordTarget, mc.player.getYaw(), mc.player.getPitch());
        }
    }
    @Native
    @EventLink
    public void onAttackEntity(EventAttackEntity event) {
        if (mc.player == null || mc.world == null) return;
        if (event.getPlayer() != mc.player) return;
        if (!(event.getTarget() instanceof LivingEntity living)) return;
        if (!isValidTarget(living)) return;

        target = living;
    }

    @EventLink
    public void onMoveInput(EventMoveInput event) {
        if (needSprintReset) {
            event.setForward(0);
            event.setStrafe(0);
            needSprintReset = false;
            sprintResetDone = true;
            sprintResetTicks = 0;
            return;
        }

        applyMoveFix(event);
    }

    @EventLink
    public void onKeyboardInput(EventKeyboardInput event) {
        if (mc.player == null || mc.world == null || target == null || moveFix.is("Нет")) {
            return;
        }
        float correctionYaw = getCorrectionYaw();
        event.setYaw(correctionYaw, mc.player.getYaw());
    }

    @EventLink
    private void onGameUpdate(EventGameUpdate e) {
        if (mc.player == null || mc.world == null || target == null) return;
        rotate();
    }
    @Native
    @EventLink
    public void onTick(EventUpdate e) {
        if (mc.player == null || mc.world == null) return;

        if (ticksToAttack > 0) {
            ticksToAttack--;
        }

        if (sprintResetDone) {
            sprintResetTicks++;
        }

        processAttack();

        if (dataSystem.isShowStats() && mc.player.age % 40 == 0 && (dataSystem.isRecording() || dataSystem.isUsingNeuro())) {
            mc.player.sendMessage(net.minecraft.text.Text.literal(dataSystem.getStatusString()), true);
        }
    }

    @EventLink
    public void onPost(EventUpdatePost e) {
        if (mc.player == null || mc.world == null) return;

    }
    @Native
    private LivingEntity findTargetForRecording() {
        LivingEntity bestTarget = null;
        double bestDistance = 100.0;
        Vec3d eyePos = mc.player.getEyePos();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (living == mc.player) continue;
            if (!living.isAlive() || living.getHealth() <= 0) continue;
            if (living instanceof ArmorStandEntity) continue;

            double distance = eyePos.squaredDistanceTo(living.getBoundingBox().getCenter());
            if (distance > bestDistance) continue;

            bestDistance = distance;
            bestTarget = living;
        }

        return bestTarget;
    }
    @Native
    private void processAttack() {
        long now = System.nanoTime();
        burstTick++;
        updateTarget();

        if (target != null) {
            tryAttack(now);
        } else {
            cps = System.currentTimeMillis();
            backTimer.reset();
            adjPitch = 0;
            adjYaw = 0;
            spookyTimeRots.reset();
            dataSystem.resetState();
            sprintResetDone = false;
            sprintResetTicks = 0;
            ticksToAttack = 0;
            burst.reset();
            resetBypassAttack();
        }
    }

    /**
     * Полная цепочка проверок и сама атака. В режиме 1.8 удар не ждёт ванильной перезарядки,
     * поэтому пачка уходит одним пакетом, а дистанция, луч, профиль цели и сброс спринта
     * проверяются на каждой попытке.
     */
    private boolean tryAttack(long now) {
        if (target == null || !isEnable() || !shouldAttack() || !burstReady(now)) {
            return false;
        }

        if (attackOnEating.isState() && mc.player.isUsingItem()) return false;

        if (sprintReset.isState() && mc.player.isSprinting() && !sprintResetDone) {
            needSprintReset = true;
            return false;
        }

        if (sprintReset.isState() && sprintResetDone && sprintResetTicks < 1) {
            return false;
        }

        if (isBypassRotationActive() && !prepareBypassAttack()) {
            return false;
        }

        attack();
        resetBypassAttack();
        sprintResetDone = false;
        sprintResetTicks = 0;
        return true;
    }

    public void Rotate() {
        rotate();
    }
    @Native
    private void rotate() {
        if (target == null) return;

        if (isBypassRotationActive()) {
            updateBypassRotation(target);
            return;
        }

        // Используем только SpookyTime ротацию
        spookyTimeRots.updateRotations(target);
    }

    private void applyMoveFix(EventMoveInput event) {
        if (mc.player == null || target == null || moveFix.is("Нет")) {
            return;
        }
        if (moveFix.is("Свободная")) {
            MovingUtil.fixMovementFree(event);
        }
    }

    private float getCorrectionYaw() {
        if (RotationStorage.instance != null && RotationStorage.instance.targetRotation() != null) {
            return RotationStorage.instance.targetRotation().getYaw();
        }
        return mc.player.getYaw();
    }

    private float getTargetDirectionYaw() {
        return RotationUtils.getRotations(target.getBoundingBox().getCenter()).x;
    }

    @Native
    private void updateBypassRotation(LivingEntity target) {
        Vec3d point = MultipointUtils.getClosestPoint(target);
        if (point == null) {
            point = target.getBoundingBox().getCenter();
        }

        Vec3d predicted = getPredictedRotationPoint(target, point);
        Vec2f targetRot = RotationUtils.getRotations(predicted);

        targetRotations = targetRot;
        currentRotations = new Vec2f(mc.player.getYaw(), mc.player.getPitch());

        boolean isBypassAttackTick = mc.player.age <= bypassAttackAge;

        float finalYaw = targetRot.x;
        float finalPitch = targetRot.y;
        if (!isBypassAttackTick) {
            finalYaw = FreeLookStorage.getFreeYaw();
            finalPitch = FreeLookStorage.getFreePitch();
        }

        RotationStorage.update(new Rotation(finalYaw, finalPitch), 360f, 360f, 360f, 360f, 0, 6, clientLook.isState());
    }

    private boolean isBypassRotationActive() {
        return isUsingRwWallBypass();
    }

    private boolean prepareBypassAttack() {
        if (!bypassAttackQueued) {
            bypassAttackQueued = true;
            bypassAttackAge = mc.player.age + 1;
            return false;
        }

        if (mc.player.age > bypassAttackAge) {
            resetBypassAttack();
            return false;
        }

        return isBypassAimReadyForAttack();
    }
    @Native
    private boolean isBypassAimReadyForAttack() {
        if (target == null || mc.player == null) {
            return false;
        }

        float yawDiff = Math.abs(MathHelper.wrapDegrees(targetRotations.x - mc.player.getYaw()));
        float pitchDiff = Math.abs(targetRotations.y - mc.player.getPitch());
        boolean onTarget = isUsingRwWallBypass() || isCurrentAimOnTarget();

        return yawDiff <= 3.0f && pitchDiff <= 2.5f && onTarget;
    }

    private boolean isUsingRwWallBypass() {
        return false;
    }

    private EntityHitResult getAttackRaycastResult() {
        Vec3d eyePos = mc.player.getCameraPosVec(1.0F);
        Vec3d lookVec = mc.player.getRotationVec(1.0F);
        float reach = getEffectiveRange() * 2.0f;
        Vec3d reachVec = eyePos.add(lookVec.multiply(reach));

        return ProjectileUtil.raycast(
                mc.player,
                eyePos,
                reachVec,
                mc.player.getBoundingBox().expand(reach),
                ex -> ex != mc.player && ex.isAlive(),
                reach * reach
        );
    }

    private boolean isTargetBehindWall(LivingEntity entity) {
        return entity != null && !mc.player.canSee(entity);
    }
    @Native
    private Vec3d getPredictedRotationPoint(LivingEntity target, Vec3d point) {
        return point;
    }
    @Native
    private boolean isCurrentAimOnTarget() {
        if (target == null || mc.player == null) {
            return false;
        }

        if (mc.player.isGliding() && target.isGliding()) {
            return RayTraceUtil.rayTraceEntity(mc.player.getYaw(), mc.player.getPitch(), getMaxAimRange(), target, false);
        }

        EntityHitResult result = getAttackRaycastResult();
        return result != null && result.getEntity() == target;
    }
    @Native
    private LivingEntity findTarget() {
        List<LivingEntity> entities = new ArrayList<>();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (!isValidTarget(living)) continue;
            entities.add(living);
        }

        if (entities.isEmpty() || !isEnable()) return null;

        switch (priority.getCurrent()) {
            case "Дистанция" ->
                    entities.sort(Comparator.comparingDouble(entity -> entity.getBoundingBox().getCenter().squaredDistanceTo(mc.player.getEyePos())));
            case "Здоровье" -> entities.sort(Comparator.comparingDouble(LivingEntity::getHealth));
            case "Угол" -> entities.sort(Comparator.comparingDouble(entity -> {
                Vec2f vec = RotationUtils.getRotations(entity.getBoundingBox().getCenter());
                double dy = Math.abs(wrapDegrees(vec.x - mc.player.getYaw()));
                double dp = Math.abs(wrapDegrees(vec.y - mc.player.getPitch()));
                return dy + dp;
            }));
            case "Никакой" -> {
            }
        }

        return entities.isEmpty() ? null : entities.get(0);
    }
    @Native
    private void updateTarget() {
        if (!isEnable()) {
            target = null;
            return;
        }

        if (target != null && isValidTarget(target)) {
            return;
        }

        target = findTarget();
    }
    @Native
    private boolean shouldFocusDataRotation() {
        float cooldown = mc.player.getAttackCooldownProgress(1.5f);
        float focusThreshold = Math.max(0.82f, IdealHitUtils.getAICooldown() - 0.08f);
        boolean readyByCooldown = cooldown >= focusThreshold;
        boolean fallingForCrit = !mc.player.isOnGround()
                && mc.player.getVelocity().y < 0.0
                && mc.player.fallDistance > 0.0f;

        return readyByCooldown || fallingForCrit;
    }
    @Native
    private void attack() {
        if (unpressShield.isState() && mc.player.isBlocking()) mc.interactionManager.stopUsingItem(mc.player);
        boolean attacked = false;
        if (target instanceof PlayerEntity player && player.isBlocking() && breakShield.isState()) {
            attacked = shieldBreak(player);
        }

        boolean burst = usesBurstClicks();
        int swingsSent = 0;
        if (!attacked) {
            if (burst) {
                // Пачка 1.8: N ударов уходят одним пакетом, перезарядка оружия не участвует.
                int swings = this.burst.attacks();
                for (int i = 0; i < swings; i++) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                    swingsSent++;
                }
            } else {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
                swingsSent = 1;
            }
        } else {
            // Разбиение щита топором занимает пачку целиком: отдельный удар сверху не нужен.
            mc.player.swingHand(Hand.MAIN_HAND);
            if (burst) {
                swingsSent = Math.max(1, this.burst.attacks());
            }
        }

        // Прежний темп 1.9 держится на ванильной перезарядке и 467 мс троттлинге,
        // режим 1.8 — на пачках, окне анти-кика и границах CPS.
        long now = System.nanoTime();
        if (burst) {
            this.burst.recordBurst(now, burstTick, swingsSent);
        } else {
            cps = System.currentTimeMillis() + ATTACK_DELAY_MS;
        }
        ticksToAttack = 10;
        attackTimer.reset();
    }

    @Native

    private boolean shieldBreak(PlayerEntity entity) {
        if (mc.player == null || mc.interactionManager == null || entity == null) {
            return false;
        }

        int axeHotbarSlot = findAxeHotbarSlot();
        if (axeHotbarSlot != -1) {
            attackWithHotbarSlot(entity, axeHotbarSlot);
            return true;
        }

        int axeInventorySlot = findAxeInventorySlot();
        if (axeInventorySlot == -1) {
            return false;
        }

        int selectedSlot = mc.player.getInventory().selectedSlot;
        int containerSlot = InventoryUtils.toContainerSlot(axeInventorySlot);

        mc.interactionManager.clickSlot(0, containerSlot, selectedSlot, SlotActionType.SWAP, mc.player);
        mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));

        try {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(selectedSlot));
            mc.interactionManager.attackEntity(mc.player, entity);
            return true;
        } finally {
            mc.interactionManager.clickSlot(0, containerSlot, selectedSlot, SlotActionType.SWAP, mc.player);
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(selectedSlot));
        }
    }

    private void attackWithHotbarSlot(PlayerEntity entity, int slot) {
        int previousSlot = mc.player.getInventory().selectedSlot;
        if (slot != previousSlot) {
            mc.player.getInventory().selectedSlot = slot;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }

        try {
            mc.interactionManager.attackEntity(mc.player, entity);
        } finally {
            if (slot != previousSlot) {
                mc.player.getInventory().selectedSlot = previousSlot;
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
            }
        }
    }

    private int findAxeHotbarSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() instanceof AxeItem) {
                return i;
            }
        }
        return -1;
    }

    private int findAxeInventorySlot() {
        for (int i = 9; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem() instanceof AxeItem) {
                return i;
            }
        }
        return -1;
    }

    private boolean isWeapon() {
        Item item = mc.player.getMainHandStack().getItem();
        return item != Items.AIR && (item instanceof SwordItem
                || item instanceof PickaxeItem
                || item instanceof AxeItem
                || item instanceof HoeItem
                || item instanceof ShovelItem
                || item instanceof MaceItem
                || item == Items.MACE);
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (entity == null || entity == mc.player) return false;
        if (!entity.isAlive() || entity.getHealth() <= 0) return false;
        if (entity instanceof ArmorStandEntity) return false;

        if (AntiBot.checkBot(entity)) return false;

        if (entity instanceof PlayerEntity player) {
            if (!targets.is("Игроки")) return false;
            if (player.hasStatusEffect(StatusEffects.INVISIBILITY) && !targets.is("Невидимки")) return false;
            if (Punch.INSTANCE.friendStorage.isFriend(entity.getName().getString())) return false;
        } else if (entity instanceof HostileEntity) {
            if (!targets.is("Мобы")) return false;
        } else {
            if (!targets.is("Мирные")) return false;
        }

        Vec3d nearestPoint = BestPoint.getNearestPoint(entity);
        if (nearestPoint == null) nearestPoint = MultipointUtils.getClosestPoint(entity);
        if (mc.player.getEyePos().distanceTo(nearestPoint) > getMaxAimRange()) return false;
        if (!throughWalls.isState() && !mc.player.canSee(entity)) return false;

        return true;
    }

    private boolean shouldAttack() {
        // Режим 1.8 задаёт темп пачками: перезарядка оружия и криты не проверяются.
        if (!usesClickClock() && mc.player.getAttackCooldownProgress(1.5f) < IdealHitUtils.getAICooldown()) {
            return false;
        }
        EntityHitResult result = getAttackRaycastResult();
        boolean aimOnTarget = isCurrentAimOnTarget();
        if (raycast.isState() && !isUsingRwWallBypass() && !aimOnTarget) {
            return false;
        }
        if (mc.player.isGliding() && target.isGliding()) {
            double currentDistance = mc.player.getEyePos().distanceTo(target.getBoundingBox().getCenter());
            if (currentDistance > getEffectiveRange()) return false;
            return true;
        } else {
            double distanceCheck = mc.player.getEyePos().distanceTo(target.getBoundingBox().getCenter());
            Vec3d checkPoint = distanceCheck > 3 ? BestPoint.getNearestPoint(target) : target.getBoundingBox().getCenter();
            if (checkPoint == null) checkPoint = MultipointUtils.getClosestPoint(target);
            if (mc.player.getEyePos().distanceTo(checkPoint) > getEffectiveRange()) return false;
            return usesClickClock() || IdealHitUtils.canCritical(target);
        }
    }

    /** Включены пачки 1.8 (в SkyCore — «1.8 NoDelay»). */
    public boolean usesBurstClicks() {
        return this.burst.usesBurst();
    }

    /** Удары 1.8 не ждут перезарядки оружия и окна крита. */
    public boolean usesClickClock() {
        return usesBurstClicks();
    }

    /** Темп удара: 1.8 — пачки по тикам, 1.9 — прежний троттлинг поверх ванильной перезарядки. */
    private boolean burstReady(long now) {
        return usesBurstClicks() ? this.burst.ready(now, burstTick) : cps <= System.currentTimeMillis();
    }

    public int getWhiteRiseTicksToAttack() {
        return ticksToAttack;
    }

    public boolean isAboveWater() {
        BlockPos pos = BlockPos.ofFloored(mc.player.getPos().add(0, -0.4, 0));
        return !mc.player.isSubmergedInWater() && mc.world.getBlockState(pos).isOf(Blocks.WATER);
    }

    public float getAttackCooldown() {
        return MathHelper.clamp(((float) ((ILivingEntity) mc.player).getLastAttackedTicks()) / getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
    }

    public float getAttackCooldownProgressPerTick() {
        return (float) (1.0 / mc.player.getAttributeValue(EntityAttributes.ATTACK_SPEED) * 20);
    }

    private float getMaxAimRange() {
        return mc.player.isGliding()
                ? elytraAimRange.getValue().floatValue()
                : getEffectiveRange() + aimRange.getValue().floatValue();
    }

    private float getEffectiveRange() {
        float base = range.getValue().floatValue();
        if (AirStuck.INSTANCE.isEnable() && AirStuck.INSTANCE.extraRangeEnabled.isState()) {
            base += AirStuck.INSTANCE.extraRange.getValue().floatValue();
        }
        return base;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (target != null) backTimer.reset();
        target = null;
        spookyTimeRots.reset();
        dataSystem.resetState();
        needSprintReset = false;
        sprintResetDone = false;
        sprintResetTicks = 0;
        ticksToAttack = 0;
        burst.reset();
        resetBypassAttack();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        spookyTimeRots.reset();
        dataSystem.resetState();
        needSprintReset = false;
        sprintResetDone = false;
        sprintResetTicks = 0;
        ticksToAttack = 0;
        burst.reset();
        resetBypassAttack();
        if (mc.player != null) {
            currentRotations = new Vec2f(mc.player.getYaw(), mc.player.getPitch());
        }
    }

    private void resetBypassAttack() {
        bypassAttackAge = -1;
        bypassAttackQueued = false;
    }
}
