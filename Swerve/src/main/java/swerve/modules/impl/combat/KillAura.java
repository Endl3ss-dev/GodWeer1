package swerve.modules.impl.combat;

import swerve.managers.FriendManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Iterator;
import java.util.List;

/**
 * KillAura: автоатака цели в радиусе.
 *
 * Порядок в тике: выбор цели → наведение → проверка прицела → кулдаун → удар.
 * Проверка прицела читает crosshairPickEntity — это тот же луч, по которому клиент считает
 * точку попадания, поэтому удар уходит с корректной геометрией и без промахов сквозь блоки.
 * После наведения камера ещё не смотрит на цель (пик обновляется рендером), поэтому удар
 * проходит на следующем тике. За это время ванильный клиент успевает отправить пакет
 * поворота (LocalPlayer.tick), и сервер видит углы наведения раньше пакета атаки.
 */
public final class KillAura extends Module {

    private static final String ROTATION_SNAP = "Резкие";
    private static final String ROTATION_SMOOTH = "Плавные";
    private static final String ROTATION_OFF = "Выключены";

    private static final double MIN_LENGTH_SQUARED = 1.0E-8D;
    private static final double NO_DISTANCE = Double.MAX_VALUE;
    private static final float DEGREES_TO_RADIANS = (float) (Math.PI / 180.0D);
    private static final float FULL_COOLDOWN = 1.0F;

    private final ModeSetting rotationMode = new ModeSetting("Повороты", ROTATION_SNAP,
            ROTATION_SNAP, ROTATION_SMOOTH, ROTATION_OFF);
    private final FloatSetting rotationSpeed = new FloatSetting("Скорость поворота", 0.45F, 0.05F, 1.0F, 0.05F)
            .visible(() -> rotationMode.is(ROTATION_SMOOTH));
    private final BooleanSetting moveCamera = new BooleanSetting("Двигать камеру", true);
    private final FloatSetting radius = new FloatSetting("Радиус", 3.0F, 1.0F, 5.0F, 0.1F);
    private final FloatSetting viewAngle = new FloatSetting("Угол обзора", 60.0F, 5.0F, 180.0F, 5.0F);
    private final BooleanSetting onlyPlayers = new BooleanSetting("Только игроки", true);
    private final BooleanSetting ignoreFriends = new BooleanSetting("Игнорировать друзей", true);
    private final BooleanSetting crosshairCheck = new BooleanSetting("Проверка прицела", true);
    private final BooleanSetting swing = new BooleanSetting("Свинг", true);
    private final FloatSetting cooldown = new FloatSetting("Кулдаун", 1.0F, 0.0F, 1.0F, 0.05F);
    private final FloatSetting delay = new FloatSetting("Задержка (мс)", 0.0F, 0.0F, 1000.0F, 25.0F);

    private Entity target;
    private Entity scanned;
    private double scannedDistance;
    private long lastAttackTime;
    private float aimYaw;
    private float aimPitch;

    public KillAura() {
        super("KillAura", "Автоматически атакует цель в радиусе", ModuleCategory.COMBAT);
        addSettings(rotationMode, rotationSpeed, moveCamera, radius, viewAngle, onlyPlayers,
                ignoreFriends, crosshairCheck, swing, cooldown, delay);
    }

    @Override
    public void onEnable() {
        reset();
    }

    @Override
    public void onDisable() {
        reset();
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.gameMode == null) {
            reset();
            return;
        }
        if (minecraft.screen != null || !player.isAlive() || player.isCreative() || player.isSpectator()) {
            target = null;
            return;
        }

        if (!isValidTarget(player, target)) {
            target = findTarget(minecraft, player);
        }
        if (target == null) {
            return;
        }

        boolean rotating = !rotationMode.is(ROTATION_OFF);
        if (rotating) {
            updateAim(player, target);
            if (moveCamera.get()) {
                player.setYRot(aimYaw);
                player.setXRot(aimPitch);
            }
        }

        if (crosshairCheck.get() && minecraft.crosshairPickEntity != target) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastAttackTime < (long) delay.get()) {
            return;
        }
        if (player.getAttackStrengthScale(FULL_COOLDOWN) < cooldown.get()) {
            return;
        }

        if (rotating && !moveCamera.get()) {
            // Камера осталась на курсе мыши: серверу углы наведения уходят отдельным пакетом.
            player.connection.send(new ServerboundMovePlayerPacket.Rot(
                    aimYaw,
                    aimPitch,
                    player.onGround(),
                    player.horizontalCollision
            ));
        }

        minecraft.gameMode.attack(player, target);
        player.resetAttackStrengthTicker();
        if (swing.get()) {
            player.swing(InteractionHand.MAIN_HAND);
        }
        lastAttackTime = now;
    }

    public Entity getTarget() {
        return target;
    }

    private boolean isValidTarget(LocalPlayer player, Entity candidate) {
        if (!(candidate instanceof LivingEntity living) || candidate == player) {
            return false;
        }
        if (living.isRemoved() || !living.isAlive() || !living.isAttackable()) {
            return false;
        }
        if (onlyPlayers.get() && !(candidate instanceof Player)) {
            return false;
        }
        if (ignoreFriends.get() && candidate instanceof Player other && FriendManager.isFriend(other)) {
            return false;
        }
        double limit = radius.get();
        return player.distanceToSqr(candidate) <= limit * limit;
    }

    private Entity findTarget(Minecraft minecraft, LocalPlayer player) {
        scanned = null;
        scannedDistance = NO_DISTANCE;

        double limit = radius.get();
        double limitSquared = limit * limit;
        double cosLimit = Math.cos(Math.toRadians(viewAngle.get()));

        float yaw = player.getYRot() * DEGREES_TO_RADIANS;
        float pitch = player.getXRot() * DEGREES_TO_RADIANS;
        float cosPitch = Mth.cos(pitch);
        double viewX = -Mth.sin(yaw) * cosPitch;
        double viewY = -Mth.sin(pitch);
        double viewZ = Mth.cos(yaw) * cosPitch;

        List<AbstractClientPlayer> players = minecraft.level.players();
        for (int index = 0, size = players.size(); index < size; index++) {
            consider(players.get(index), player, limitSquared, cosLimit, viewX, viewY, viewZ);
        }
        if (!onlyPlayers.get()) {
            scanNonPlayers(minecraft, player, limitSquared, cosLimit, viewX, viewY, viewZ);
        }

        Entity result = scanned;
        scanned = null;
        scannedDistance = NO_DISTANCE;
        return result;
    }

    private void scanNonPlayers(Minecraft minecraft, LocalPlayer player, double limitSquared, double cosLimit,
                                double viewX, double viewY, double viewZ) {
        Iterable<Entity> entities = minecraft.level.entitiesForRendering();
        // Список сущностей отдаётся без обёртки: идём по индексу, без аллокаций в тике.
        if (entities instanceof List<?> list) {
            for (int index = 0, size = list.size(); index < size; index++) {
                Object raw = list.get(index);
                if (raw instanceof Entity entity) {
                    consider(entity, player, limitSquared, cosLimit, viewX, viewY, viewZ);
                }
            }
            return;
        }
        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            consider(iterator.next(), player, limitSquared, cosLimit, viewX, viewY, viewZ);
        }
    }

    private void consider(Entity candidate, LocalPlayer player, double limitSquared, double cosLimit,
                          double viewX, double viewY, double viewZ) {
        if (candidate == player || !isValidTarget(player, candidate)) {
            return;
        }
        double distanceSquared = player.distanceToSqr(candidate);
        if (distanceSquared > limitSquared || distanceSquared >= scannedDistance) {
            return;
        }
        if (!isWithinView(player, candidate, cosLimit, viewX, viewY, viewZ)) {
            return;
        }
        scannedDistance = distanceSquared;
        scanned = candidate;
    }

    private boolean isWithinView(LocalPlayer player, Entity candidate, double cosLimit,
                                 double viewX, double viewY, double viewZ) {
        double deltaX = candidate.getX() - player.getX();
        double deltaY = candidate.getEyeY() - player.getEyeY();
        double deltaZ = candidate.getZ() - player.getZ();
        double lengthSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
        if (lengthSquared < MIN_LENGTH_SQUARED) {
            return true;
        }
        double dot = (deltaX * viewX + deltaY * viewY + deltaZ * viewZ) / Math.sqrt(lengthSquared);
        return dot >= cosLimit;
    }

    private void updateAim(LocalPlayer player, Entity candidate) {
        double deltaX = candidate.getX() - player.getX();
        double deltaY = candidate.getEyeY() - player.getEyeY();
        double deltaZ = candidate.getZ() - player.getZ();
        double horizontal = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        double length = Math.sqrt(horizontal * horizontal + deltaY * deltaY);
        if (length < MIN_LENGTH_SQUARED) {
            aimYaw = player.getYRot();
            aimPitch = player.getXRot();
            return;
        }

        aimYaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0D);
        aimPitch = (float) Mth.clamp(-Math.toDegrees(Math.atan2(deltaY, horizontal)), -90.0D, 90.0D);
        if (rotationMode.is(ROTATION_SMOOTH)) {
            float yawDelta = Mth.wrapDegrees(aimYaw - player.getYRot());
            float pitchDelta = aimPitch - player.getXRot();
            float speed = rotationSpeed.get();
            aimYaw = player.getYRot() + yawDelta * speed;
            aimPitch = Mth.clamp(player.getXRot() + pitchDelta * speed, -90.0F, 90.0F);
        }
    }

    private void reset() {
        target = null;
        scanned = null;
        scannedDistance = NO_DISTANCE;
        lastAttackTime = 0L;
    }
}
