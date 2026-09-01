package godweer.modules.impl.combat;

import godweer.managers.FriendManager;
import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ModeSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.phys.AABB;

import java.util.concurrent.ThreadLocalRandom;

public final class KillAura extends Module {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private static final float DEG_TO_RAD = (float) (Math.PI / 180.0D);
    private static final double EPSILON = 1.0E-6D;

    private final ModeSetting bypass = new ModeSetting("Обход", "Смус",
            "Ваниль", "Смус", "Грим", "Матрикс", "Нейро", "Легит");
    private final ModeSetting priority = new ModeSetting("Приоритет", "Прицел", "Прицел", "Дистанция", "Здоровье");
    private final ModeSetting aimPoint = new ModeSetting("Точка прицела", "Точка", "Точка", "Голова", "Центр");

    private final BooleanSetting targetPlayers = new BooleanSetting("Игроки", true);
    private final BooleanSetting targetNaked = new BooleanSetting("Голые", true);
    private final BooleanSetting targetMobs = new BooleanSetting("Мобы", false);
    private final BooleanSetting targetAnimals = new BooleanSetting("Животные", false);

    private final FloatSetting range = new FloatSetting("Дистанция", 4.0F, 3.0F, 6.0F, 0.1F);
    private final FloatSetting extraRange = new FloatSetting("Доп. дистанция", 0.5F, 0.0F, 2.0F, 0.1F);
    private final FloatSetting fov = new FloatSetting("FOV", 120.0F, 30.0F, 180.0F, 5.0F);
    private final FloatSetting rotationSpeed = new FloatSetting("Скорость ротации", 38.0F, 1.0F, 180.0F, 1.0F)
            .visible(() -> !bypass.is("Ваниль"));
    private final FloatSetting reaction = new FloatSetting("Задержка реакции (тики)", 2.0F, 0.0F, 5.0F, 1.0F)
            .visible(() -> bypass.is("Матрикс") || bypass.is("Нейро"));
    private final FloatSetting prediction = new FloatSetting("Предикт", 0.5F, 0.0F, 3.0F, 0.1F);
    private final FloatSetting jitter = new FloatSetting("Джиттер", 0.6F, 0.0F, 3.0F, 0.1F);
    private final FloatSetting stickyTicks = new FloatSetting("Удержание цели (тики)", 10.0F, 0.0F, 40.0F, 1.0F);

    private final BooleanSetting cpsMode = new BooleanSetting("Режим CPS", false);
    private final FloatSetting cpsMin = new FloatSetting("CPS мин.", 8.0F, 1.0F, 20.0F, 1.0F)
            .visible(cpsMode::get);
    private final FloatSetting cpsMax = new FloatSetting("CPS макс.", 12.0F, 1.0F, 20.0F, 1.0F)
            .visible(cpsMode::get);
    private final FloatSetting cooldownThreshold = new FloatSetting("Порог кулдауна", 0.9F, 0.5F, 1.0F, 0.05F)
            .visible(() -> !cpsMode.get());

    private final BooleanSetting critOnly = new BooleanSetting("Только криты", true);
    private final BooleanSetting sprintReset = new BooleanSetting("Сброс спринта", true)
            .visible(critOnly::get);
    private final BooleanSetting wallCheck = new BooleanSetting("Не бить через стены", true);
    private final BooleanSetting gcdFix = new BooleanSetting("GCD-фикс", true);
    private final BooleanSetting swing = new BooleanSetting("Взмах рукой", true);

    private LivingEntity target;

    private int tick;
    private int acquiredTick = Integer.MIN_VALUE / 2;
    private long nextAttackAt;

    private float sentYaw;
    private float sentPitch;
    private float velYaw;
    private float velPitch;
    private float fromYaw;
    private float fromPitch;
    private float ease;
    private int reactTicks;
    private boolean sprintCanceled;

    private double aimX;
    private double aimY;
    private double aimZ;
    private float jitterYaw;
    private float jitterPitch;

    public KillAura() {
        super("KillAura", "Гигантская настраиваемая аура с выбором обходов", ModuleCategory.COMBAT);
        addSettings(
                bypass, priority, aimPoint,
                targetPlayers, targetNaked, targetMobs, targetAnimals,
                range, extraRange, fov, rotationSpeed, reaction, prediction, jitter, stickyTicks,
                cpsMode, cpsMin, cpsMax, cooldownThreshold,
                critOnly, sprintReset, wallCheck, gcdFix, swing
        );
    }

    @Override
    public void onEnable() {
        resetState();
    }

    @Override
    public void onDisable() {
        resetState();
    }

    private void resetState() {
        target = null;
        nextAttackAt = 0L;
        velYaw = 0.0F;
        velPitch = 0.0F;
        ease = 0.0F;
        reactTicks = 0;
        sprintCanceled = false;
        jitterYaw = 0.0F;
        jitterPitch = 0.0F;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            sentYaw = minecraft.player.getYRot();
            sentPitch = minecraft.player.getXRot();
        }
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.gameMode == null) {
            target = null;
            return;
        }

        tick++;
        updateTarget(minecraft, player);

        if (target == null) {
            sentYaw = player.getYRot();
            sentPitch = player.getXRot();
            sprintCanceled = false;
            return;
        }

        computeAim(player, target);

        if (canAttack(minecraft, player)) {
            minecraft.gameMode.attack(player, target);
            if (swing.get()) {
                player.swing(InteractionHand.MAIN_HAND);
            }
            scheduleCps();
            if (sprintCanceled) {
                sprintCanceled = false;
                // Возвращаем спринт после крит-удара, если игрок продолжает идти вперёд.
                if (player.zza > 0.0F) {
                    player.setSprinting(true);
                }
            }
        }

        applyRotation(player);
    }

    private void scheduleCps() {
        if (!cpsMode.get()) {
            return;
        }
        float min = Math.min(cpsMin.get(), cpsMax.get());
        float max = Math.max(cpsMin.get(), cpsMax.get());
        float cps = min + ThreadLocalRandom.current().nextFloat() * (max - min);
        nextAttackAt = System.currentTimeMillis() + (long) (1000.0F / Math.max(cps, 1.0F));
    }

    private void updateTarget(Minecraft minecraft, LocalPlayer player) {
        double reach = range.get() + extraRange.get();
        double acquireDistanceSquared = reach * reach;

        if (isTargetValid(player, target, acquireDistanceSquared)
                && tick - acquiredTick < (int) stickyTicks.get()) {
            return;
        }

        double eyeX = player.getEyeX();
        double eyeY = player.getEyeY();
        double eyeZ = player.getEyeZ();
        float lookYaw = player.getYRot();
        float lookPitch = player.getXRot();

        LivingEntity best = null;
        double bestScore = Double.MAX_VALUE;

        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            if (living == player || !living.isAlive() || living.isRemoved() || living instanceof ArmorStand) {
                continue;
            }
            if (!isAllowedTarget(living)) {
                continue;
            }

            AABB box = living.getBoundingBox();
            double distanceSquared = nearestPointSquared(eyeX, eyeY, eyeZ, box);
            if (distanceSquared > acquireDistanceSquared) {
                continue;
            }

            double centerX = (box.minX + box.maxX) * 0.5D;
            double centerY = (box.minY + box.maxY) * 0.5D;
            double centerZ = (box.minZ + box.maxZ) * 0.5D;

            float angle = angleBetween(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, lookYaw, lookPitch);
            if (angle > fov.get()) {
                continue;
            }

            double score = scoreTarget(living, distanceSquared, angle);
            if (score < bestScore) {
                bestScore = score;
                best = living;
            }
        }

        if (best != target) {
            target = best;
            acquiredTick = tick;
            onTargetSwitched(player, best);
        }
    }

    private double scoreTarget(LivingEntity living, double distanceSquared, float angle) {
        if (priority.is("Дистанция")) {
            return distanceSquared;
        }
        if (priority.is("Здоровье")) {
            return living.getHealth() + distanceSquared * 0.001D;
        }
        return angle + distanceSquared * 0.01D;
    }

    private void onTargetSwitched(LocalPlayer player, LivingEntity newTarget) {
        velYaw = 0.0F;
        velPitch = 0.0F;
        ease = 0.0F;
        fromYaw = player.getYRot();
        fromPitch = player.getXRot();

        if (newTarget == null) {
            reactTicks = 0;
            return;
        }

        float angleDiff = angleBetween(
                player.getEyeX(), player.getEyeY(), player.getEyeZ(),
                newTarget.getX(), newTarget.getEyeY(), newTarget.getZ(),
                player.getYRot(), player.getXRot()
        );
        reactTicks = reactionTicks(angleDiff);
    }

    private int reactionTicks(float angleDiff) {
        int max = Math.round(reaction.get());
        if (max <= 0 || (!bypass.is("Матрикс") && !bypass.is("Нейро"))) {
            return 0;
        }
        return angleDiff > 60.0F ? max : Math.max(1, max / 2);
    }

    private boolean isTargetValid(LocalPlayer player, LivingEntity living, double maxDistanceSquared) {
        if (living == null || living == player || !living.isAlive() || living.isRemoved()) {
            return false;
        }
        if (!isAllowedTarget(living)) {
            return false;
        }
        AABB box = living.getBoundingBox();
        return nearestPointSquared(player.getEyeX(), player.getEyeY(), player.getEyeZ(), box) <= maxDistanceSquared;
    }

    private boolean isAllowedTarget(LivingEntity living) {
        if (living instanceof Player playerEntity) {
            if (FriendManager.isFriend(playerEntity)) {
                return false;
            }
            if (!targetPlayers.get()) {
                return false;
            }
            return !isNaked(playerEntity) || targetNaked.get();
        }
        if (living instanceof Enemy) {
            return targetMobs.get();
        }
        if (living instanceof Animal || living instanceof IronGolem || living instanceof AbstractVillager) {
            return targetAnimals.get();
        }
        return false;
    }

    private static boolean isNaked(LivingEntity living) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (living.getItemBySlot(slot).getItem() instanceof ArmorItem) {
                return false;
            }
        }
        return true;
    }

    private void computeAim(LocalPlayer player, LivingEntity living) {
        AABB box = living.getBoundingBox();
        double eyeX = player.getEyeX();
        double eyeY = player.getEyeY();
        double eyeZ = player.getEyeZ();

        double pointX;
        double pointY;
        double pointZ;

        if (aimPoint.is("Голова")) {
            pointX = living.getX();
            pointY = living.getEyeY();
            pointZ = living.getZ();
        } else if (aimPoint.is("Центр")) {
            pointX = (box.minX + box.maxX) * 0.5D;
            pointY = (box.minY + box.maxY) * 0.5D;
            pointZ = (box.minZ + box.maxZ) * 0.5D;
        } else {
            pointX = Mth.clamp(eyeX, box.minX, box.maxX);
            pointY = Mth.clamp(eyeY, box.minY, box.maxY);
            pointZ = Mth.clamp(eyeZ, box.minZ, box.maxZ);
        }

        double predictTicks = prediction.get();
        if (predictTicks > 0.0D) {
            pointX += living.getDeltaMovement().x * predictTicks;
            pointY += living.getDeltaMovement().y * predictTicks;
            pointZ += living.getDeltaMovement().z * predictTicks;
        }

        aimX = pointX;
        aimY = pointY;
        aimZ = pointZ;

        float jitterAmount = jitter.get();
        if (jitterAmount > 0.0F) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            jitterYaw = (random.nextFloat() * 2.0F - 1.0F) * jitterAmount;
            jitterPitch = (random.nextFloat() * 2.0F - 1.0F) * jitterAmount * 0.6F;
        } else {
            jitterYaw = 0.0F;
            jitterPitch = 0.0F;
        }
    }

    private boolean canAttack(Minecraft minecraft, LocalPlayer player) {
        if (minecraft.screen != null || player.isUsingItem()) {
            return false;
        }

        AABB box = target.getBoundingBox();
        double reach = range.get();
        if (nearestPointSquared(player.getEyeX(), player.getEyeY(), player.getEyeZ(), box) > reach * reach) {
            return false;
        }

        if (wallCheck.get() && !player.hasLineOfSight(target)) {
            return false;
        }

        if (!isAligned(player, toleranceForMode())) {
            return false;
        }

        if (critOnly.get()) {
            // Спринт в воздухе съедает крит: гасим спринт и бьём на следующем тике.
            if (player.isSprinting() && !player.onGround()) {
                if (sprintReset.get()) {
                    player.setSprinting(false);
                    sprintCanceled = true;
                }
                return false;
            }
            if (!canCrit(player)) {
                return false;
            }
        }

        if (bypass.is("Легит") && !minecraft.options.keyAttack.isDown()) {
            return false;
        }

        if (cpsMode.get()) {
            return System.currentTimeMillis() >= nextAttackAt;
        }
        return player.getAttackStrengthScale(0.5F) >= cooldownThreshold.get();
    }

    private float toleranceForMode() {
        if (bypass.is("Ваниль")) {
            return 3.0F;
        }
        if (bypass.is("Легит")) {
            return 35.0F;
        }
        return 9.0F;
    }

    private static boolean canCrit(LocalPlayer player) {
        return player.fallDistance > 0.0F
                && !player.onGround()
                && !player.onClimbable()
                && !player.isInWater()
                && !player.isPassenger()
                && !player.isSprinting();
    }

    private void applyRotation(LocalPlayer player) {
        double deltaX = aimX - player.getEyeX();
        double deltaY = aimY - player.getEyeY();
        double deltaZ = aimZ - player.getEyeZ();
        double horizontal = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float goalYaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F + jitterYaw;
        float goalPitch = (float) -Math.toDegrees(Math.atan2(deltaY, horizontal)) + jitterPitch;

        switch (bypass.getCurrent()) {
            case "Ваниль" -> {
                sentYaw = goalYaw;
                sentPitch = Mth.clamp(goalPitch, -90.0F, 90.0F);
            }
            case "Смус" -> smoothStep(player, goalYaw, goalPitch, rotationSpeed.get());
            case "Грим" -> grimStep(player, goalYaw, goalPitch);
            case "Матрикс" -> matrixStep(player, goalYaw, goalPitch);
            case "Нейро" -> neuroStep(player, goalYaw, goalPitch);
            case "Легит" -> {
                if (Minecraft.getInstance().options.keyAttack.isDown()) {
                    smoothStep(player, goalYaw, goalPitch, rotationSpeed.get() * 0.7F);
                }
            }
            default -> {
            }
        }

        player.setYRot(sentYaw);
        player.setXRot(Mth.clamp(sentPitch, -90.0F, 90.0F));
    }

    private void smoothStep(LocalPlayer player, float goalYaw, float goalPitch, float maxStep) {
        float deltaYaw = Mth.wrapDegrees(goalYaw - sentYaw);
        float deltaPitch = goalPitch - sentPitch;

        sentYaw += Mth.clamp(deltaYaw, -maxStep, maxStep);
        sentPitch += Mth.clamp(deltaPitch, -maxStep, maxStep);

        quantizeToGcd(player);
    }

    // Грим: пока кулдаун не готов — ведём медленно, к удару доводка ускоряется.
    private void grimStep(LocalPlayer player, float goalYaw, float goalPitch) {
        float cooldownProgress = Mth.clamp(player.getAttackStrengthScale(0.0F), 0.0F, 1.0F);
        float maxStep = rotationSpeed.get() * (0.2F + 0.8F * cooldownProgress);
        smoothStep(player, goalYaw, goalPitch, maxStep);
    }

    // Матрикс: реакция после смены цели, плавная доводка ease-кривой и редкие "человеческие" паузы.
    private void matrixStep(LocalPlayer player, float goalYaw, float goalPitch) {
        if (reactTicks > 0) {
            reactTicks--;
            quantizeToGcd(player);
            return;
        }

        if (ease < 1.0F) {
            ease = Math.min(1.0F, ease + 0.14F + rotationSpeed.get() / 180.0F * 0.1F);
            if (ThreadLocalRandom.current().nextFloat() < 0.04F) {
                quantizeToGcd(player);
                return;
            }
        }

        float eased = ease * ease * (3.0F - 2.0F * ease);
        sentYaw = fromYaw + Mth.wrapDegrees(goalYaw - fromYaw) * eased;
        sentPitch = fromPitch + (goalPitch - fromPitch) * eased;

        quantizeToGcd(player);
    }

    // Нейро: пружинная интерполяция со скоростью и инерцией мыши.
    private void neuroStep(LocalPlayer player, float goalYaw, float goalPitch) {
        if (reactTicks > 0) {
            reactTicks--;
            quantizeToGcd(player);
            return;
        }

        float stiffness = 0.32F;
        float damping = 0.55F;
        float maxVelocity = rotationSpeed.get();

        float deltaYaw = Mth.wrapDegrees(goalYaw - sentYaw);
        float deltaPitch = goalPitch - sentPitch;

        velYaw = Mth.clamp(velYaw + deltaYaw * stiffness - velYaw * damping, -maxVelocity, maxVelocity);
        velPitch = Mth.clamp(velPitch + deltaPitch * stiffness - velPitch * damping,
                -maxVelocity * 0.7F, maxVelocity * 0.7F);

        sentYaw += velYaw;
        sentPitch += velPitch;

        quantizeToGcd(player);
    }

    // Квантование дельты ротации под шаг мыши клиента (GCD) — сервер видит "легитную" мышь.
    private void quantizeToGcd(LocalPlayer player) {
        if (!gcdFix.get()) {
            return;
        }

        double sensitivityFactor = Minecraft.getInstance().options.sensitivity().get() * 0.6D + 0.2D;
        float gcd = (float) (sensitivityFactor * sensitivityFactor * sensitivityFactor * 1.2D * 0.15D);
        if (gcd <= 0.0F) {
            return;
        }

        float baseYaw = player.getYRot();
        float basePitch = player.getXRot();
        sentYaw = baseYaw + Math.round((sentYaw - baseYaw) / gcd) * gcd;
        sentPitch = basePitch + Math.round((sentPitch - basePitch) / gcd) * gcd;
    }

    private boolean isAligned(LocalPlayer player, float tolerance) {
        float yawRadians = player.getYRot() * DEG_TO_RAD;
        float pitchRadians = player.getXRot() * DEG_TO_RAD;

        double lookX = -Mth.sin(yawRadians) * Mth.cos(pitchRadians);
        double lookY = -Mth.sin(pitchRadians);
        double lookZ = Mth.cos(yawRadians) * Mth.cos(pitchRadians);

        double deltaX = aimX - player.getEyeX();
        double deltaY = aimY - player.getEyeY();
        double deltaZ = aimZ - player.getEyeZ();
        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        if (length < EPSILON) {
            return true;
        }

        double dot = (lookX * deltaX + lookY * deltaY + lookZ * deltaZ) / length;
        double angle = Math.toDegrees(Math.acos(Mth.clamp(dot, -1.0D, 1.0D)));
        return angle <= tolerance;
    }

    private static float angleBetween(double eyeX, double eyeY, double eyeZ,
                                     double pointX, double pointY, double pointZ,
                                     float yaw, float pitch) {
        float yawRadians = yaw * DEG_TO_RAD;
        float pitchRadians = pitch * DEG_TO_RAD;

        double lookX = -Mth.sin(yawRadians) * Mth.cos(pitchRadians);
        double lookY = -Mth.sin(pitchRadians);
        double lookZ = Mth.cos(yawRadians) * Mth.cos(pitchRadians);

        double deltaX = pointX - eyeX;
        double deltaY = pointY - eyeY;
        double deltaZ = pointZ - eyeZ;
        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        if (length < EPSILON) {
            return 0.0F;
        }

        double dot = (lookX * deltaX + lookY * deltaY + lookZ * deltaZ) / length;
        return (float) Math.toDegrees(Math.acos(Mth.clamp(dot, -1.0D, 1.0D)));
    }

    private static double nearestPointSquared(double eyeX, double eyeY, double eyeZ, AABB box) {
        double pointX = Mth.clamp(eyeX, box.minX, box.maxX);
        double pointY = Mth.clamp(eyeY, box.minY, box.maxY);
        double pointZ = Mth.clamp(eyeZ, box.minZ, box.maxZ);
        double deltaX = eyeX - pointX;
        double deltaY = eyeY - pointY;
        double deltaZ = eyeZ - pointZ;
        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }
}
