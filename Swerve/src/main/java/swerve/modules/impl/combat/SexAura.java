package swerve.modules.impl.combat;

import swerve.managers.FriendManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public final class SexAura extends Module {

    private static final double MIN_DIRECTION_LENGTH_SQUARED = 1.0E-8D;
    private static final double MOVEMENT_DEAD_ZONE = 0.25D;
    private static final float ROTATION_SMOOTHING = 0.3F;

    private final FloatSetting maxDistance = new FloatSetting("Макс. дистанция", 10.0F, 3.0F, 15.0F, 0.5F);
    private final FloatSetting approachDistance = new FloatSetting("Дистанция подхода", 0.5F, 0.3F, 2.0F, 0.1F);
    private final FloatSetting sneakDelay = new FloatSetting("Задержка приседания", 70.0F, 50.0F, 200.0F, 10.0F);
    private final BooleanSetting lookAtBody = new BooleanSetting("Смотреть на туловище", true);
    private final BooleanSetting autoMove = new BooleanSetting("Авто подход", true);
    private final BooleanSetting onlyPlayers = new BooleanSetting("Только игроки", true);

    private Player target;
    private long lastSneakTime;
    private boolean sneaking;

    public SexAura() {
        super("SexAura", "Автоматически приседает на ближайшем игроке", ModuleCategory.COMBAT);
        addSettings(maxDistance, approachDistance, sneakDelay, lookAtBody, autoMove, onlyPlayers);
    }

    @Override
    public void onEnable() {
        target = null;
        lastSneakTime = 0L;
        sneaking = false;
    }

    @Override
    public void onDisable() {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.options.keyShift.setDown(false);
        releaseMovementKeys(minecraft);
        target = null;
        sneaking = false;
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }

        double maxDistanceSquared = maxDistance.get() * maxDistance.get();
        if (!isValidTarget(player, target, maxDistanceSquared)) {
            findClosestTarget(minecraft, player, maxDistanceSquared);
        }

        if (target != null) {
            handleTarget(minecraft, player);
            return;
        }

        minecraft.options.keyShift.setDown(false);
        releaseMovementKeys(minecraft);
        sneaking = false;
    }

    private boolean isValidTarget(LocalPlayer self, Player player, double maxDistanceSquared) {
        return player != null
                && player != self
                && player.isAlive()
                && !player.isRemoved()
                && !FriendManager.isFriend(player)
                && self.distanceToSqr(player) <= maxDistanceSquared;
    }

    private void findClosestTarget(Minecraft minecraft, LocalPlayer self, double maxDistanceSquared) {
        List<AbstractClientPlayer> players = minecraft.level.players();
        Player closest = null;
        double closestDistanceSquared = maxDistanceSquared;

        for (int i = 0, size = players.size(); i < size; i++) {
            AbstractClientPlayer candidate = players.get(i);
            if (candidate == self || !candidate.isAlive() || candidate.isRemoved() || FriendManager.isFriend(candidate)) {
                continue;
            }

            double distanceSquared = self.distanceToSqr(candidate);
            if (distanceSquared <= closestDistanceSquared) {
                closest = candidate;
                closestDistanceSquared = distanceSquared;
            }
        }

        target = closest;
    }

    private void handleTarget(Minecraft minecraft, LocalPlayer player) {
        double distance = player.distanceTo(target);

        if (lookAtBody.get()) {
            lookAtTarget(player);
        }

        if (autoMove.get() && distance > approachDistance.get()) {
            moveTowardsTarget(minecraft, player);
        } else {
            releaseMovementKeys(minecraft);
        }

        if (distance <= approachDistance.get()) {
            long now = System.currentTimeMillis();
            if (now - lastSneakTime >= (long) sneakDelay.get()) {
                sneaking = !sneaking;
                minecraft.options.keyShift.setDown(sneaking);
                lastSneakTime = now;
            }
            return;
        }

        minecraft.options.keyShift.setDown(false);
        sneaking = false;
    }

    private void lookAtTarget(LocalPlayer player) {
        double deltaX = target.getX() - player.getX();
        double deltaY = target.getY() + target.getBbHeight() * 0.5D - player.getEyeY();
        double deltaZ = target.getZ() - player.getZ();
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float targetYaw = (float) Math.toDegrees(Mth.atan2(deltaZ, deltaX)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Mth.atan2(deltaY, horizontalDistance));
        float yawDifference = Mth.wrapDegrees(targetYaw - player.getYRot());
        float pitchDifference = targetPitch - player.getXRot();

        player.setYRot(player.getYRot() + yawDifference * ROTATION_SMOOTHING);
        player.setXRot(Mth.clamp(player.getXRot() + pitchDifference * ROTATION_SMOOTHING, -90.0F, 90.0F));
    }

    private void moveTowardsTarget(Minecraft minecraft, LocalPlayer player) {
        double deltaX = target.getX() - player.getX();
        double deltaZ = target.getZ() - player.getZ();
        double lengthSquared = deltaX * deltaX + deltaZ * deltaZ;
        if (lengthSquared < MIN_DIRECTION_LENGTH_SQUARED) {
            releaseMovementKeys(minecraft);
            return;
        }

        double inverseLength = 1.0D / Math.sqrt(lengthSquared);
        deltaX *= inverseLength;
        deltaZ *= inverseLength;

        double yaw = Math.toRadians(player.getYRot());
        double forwardX = -Math.sin(yaw);
        double forwardZ = Math.cos(yaw);
        double rightX = -Math.cos(yaw);
        double rightZ = -Math.sin(yaw);
        double forward = deltaX * forwardX + deltaZ * forwardZ;
        double strafe = deltaX * rightX + deltaZ * rightZ;

        minecraft.options.keyUp.setDown(forward > MOVEMENT_DEAD_ZONE);
        minecraft.options.keyDown.setDown(forward < -MOVEMENT_DEAD_ZONE);
        minecraft.options.keyRight.setDown(strafe > MOVEMENT_DEAD_ZONE);
        minecraft.options.keyLeft.setDown(strafe < -MOVEMENT_DEAD_ZONE);
    }

    private void releaseMovementKeys(Minecraft minecraft) {
        minecraft.options.keyUp.setDown(false);
        minecraft.options.keyDown.setDown(false);
        minecraft.options.keyLeft.setDown(false);
        minecraft.options.keyRight.setDown(false);
    }
}
