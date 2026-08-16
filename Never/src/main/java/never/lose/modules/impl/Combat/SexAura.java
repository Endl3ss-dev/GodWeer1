package never.lose.modules.impl.Combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import never.lose.managers.FriendManager;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.modules.api.setting.SliderSetting;

import java.util.Comparator;

@ModuleInfo(name = "SexAura", desc = "Автоматически приседает на ближайшем игроке", category = ModuleCategory.COMBAT)
public class SexAura extends Module {

    public final SliderSetting maxDistance = new SliderSetting("Макс. дистанция", 10, 3, 15, 0.5);
    public final SliderSetting approachDistance = new SliderSetting("Дистанция подхода", 0.5, 0.3, 2, 0.1);
    public final SliderSetting sneakDelay = new SliderSetting("Задержка приседания", 70, 50, 200, 10);
    public final BooleanSetting lookAtBody = new BooleanSetting("Смотреть на туловище", true);
    public final BooleanSetting autoMove = new BooleanSetting("Авто подход", true);
    public final BooleanSetting onlyPlayers = new BooleanSetting("Только игроки", true);

    private PlayerEntity target;
    private long lastSneakTime;
    private boolean sneaking;

    @Override
    public void onEnable() {
        target = null;
        lastSneakTime = 0L;
        sneaking = false;
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options != null) {
            mc.options.sneakKey.setPressed(false);
            releaseMovementKeys(mc);
        }
        target = null;
        sneaking = false;
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null || mc.options == null) {
            return;
        }

        if (!isValidTarget(player, target)) {
            findClosestTarget(mc, player);
        }

        if (target != null) {
            handleTarget(mc, player);
            return;
        }

        mc.options.sneakKey.setPressed(false);
        releaseMovementKeys(mc);
        sneaking = false;
    }

    private boolean isValidTarget(ClientPlayerEntity self, PlayerEntity player) {
        if (player == null || player == self || !player.isAlive() || FriendManager.isFriend(player)) {
            return false;
        }
        return self.distanceTo(player) <= maxDistance.getValue();
    }

    private void findClosestTarget(MinecraftClient mc, ClientPlayerEntity self) {
        target = mc.world.getPlayers().stream()
                .filter(player -> player != self)
                .filter(PlayerEntity::isAlive)
                .filter(player -> !FriendManager.isFriend(player))
                .filter(player -> self.distanceTo(player) <= maxDistance.getValue())
                .min(Comparator.comparingDouble(self::distanceTo))
                .orElse(null);
    }

    private void handleTarget(MinecraftClient mc, ClientPlayerEntity player) {
        double distance = player.distanceTo(target);

        if (lookAtBody.getValue()) {
            lookAtTarget(player);
        }

        if (autoMove.getValue() && distance > approachDistance.getValue()) {
            moveTowardsTarget(mc, player);
        } else {
            releaseMovementKeys(mc);
        }

        if (distance <= approachDistance.getValue()) {
            long now = System.currentTimeMillis();
            if (now - lastSneakTime >= sneakDelay.getValue()) {
                sneaking = !sneaking;
                mc.options.sneakKey.setPressed(sneaking);
                lastSneakTime = now;
            }
        } else {
            mc.options.sneakKey.setPressed(false);
            sneaking = false;
        }
    }

    private void lookAtTarget(ClientPlayerEntity player) {
        double deltaX = target.getX() - player.getX();
        double deltaY = target.getY() + target.getHeight() / 2.0 - player.getEyeY();
        double deltaZ = target.getZ() - player.getZ();
        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) (MathHelper.atan2(deltaZ, deltaX) * (180.0 / Math.PI)) - 90.0F;
        float pitch = (float) -(MathHelper.atan2(deltaY, distanceXZ) * (180.0 / Math.PI));

        float yawDiff = MathHelper.wrapDegrees(yaw - player.getYaw());
        float pitchDiff = pitch - player.getPitch();

        player.setYaw(player.getYaw() + yawDiff * 0.3F);
        player.setPitch(MathHelper.clamp(player.getPitch() + pitchDiff * 0.3F, -90.0F, 90.0F));
    }

    private void moveTowardsTarget(MinecraftClient mc, ClientPlayerEntity player) {
        double dx = target.getX() - player.getX();
        double dz = target.getZ() - player.getZ();
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < 1.0E-4) {
            releaseMovementKeys(mc);
            return;
        }
        dx /= length;
        dz /= length;

        // Keys are applied in look-space. Convert world direction to WASD
        // so the player walks to the target even when look-at-body is off.
        double yaw = Math.toRadians(player.getYaw());
        double forwardX = -Math.sin(yaw);
        double forwardZ = Math.cos(yaw);
        double rightX = -Math.cos(yaw);
        double rightZ = -Math.sin(yaw);

        double forward = dx * forwardX + dz * forwardZ;
        double strafe = dx * rightX + dz * rightZ;
        double deadzone = 0.25;

        mc.options.forwardKey.setPressed(forward > deadzone);
        mc.options.backKey.setPressed(forward < -deadzone);
        mc.options.rightKey.setPressed(strafe > deadzone);
        mc.options.leftKey.setPressed(strafe < -deadzone);
    }

    private void releaseMovementKeys(MinecraftClient mc) {
        if (mc.options == null) {
            return;
        }
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
    }
}
