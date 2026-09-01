package godweer.modules.impl.movement;

import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ModeSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public final class Speed extends Module {

    private final ModeSetting mode = new ModeSetting("Режим", "Страф", "Страф", "Ваниль", "Земля");
    private final FloatSetting multiplier = new FloatSetting("Множитель", 1.08F, 1.0F, 1.35F, 0.01F);
    private final FloatSetting groundMultiplier = new FloatSetting("Множитель на земле", 1.04F, 1.0F, 1.25F, 0.01F);
    private final FloatSetting acceleration = new FloatSetting("Ускорение", 0.035F, 0.001F, 0.15F, 0.001F);
    private final FloatSetting maxSpeed = new FloatSetting("Максимальная скорость", 0.38F, 0.1F, 1.0F, 0.01F);
    private final FloatSetting friction = new FloatSetting("Трение", 0.91F, 0.5F, 1.0F, 0.01F);
    private final BooleanSetting sprint = new BooleanSetting("Авто-спринт", true);
    private final BooleanSetting onlyForward = new BooleanSetting("Только вперёд", false);
    private final BooleanSetting whileJumping = new BooleanSetting("В воздухе", true);
    private final BooleanSetting preserveY = new BooleanSetting("Сохранять вертикаль", true);
    private final BooleanSetting respectCollision = new BooleanSetting("Учитывать столкновения", true);

    public Speed() {
        super("Speed", "Настраиваемое ускорение движения", ModuleCategory.MOVEMENT);
        addSettings(mode, multiplier, groundMultiplier, acceleration, maxSpeed, friction, sprint,
                onlyForward, whileJumping, preserveY, respectCollision);
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.screen != null || !player.isAlive() || player.isPassenger()) {
            return;
        }

        if (respectCollision.get() && (player.horizontalCollision || player.verticalCollision && !player.onGround())) {
            return;
        }
        if (!whileJumping.get() && !player.onGround()) {
            return;
        }
        if (onlyForward.get() && player.zza <= 0.0F) {
            return;
        }
        if (player.input == null || !player.input.hasForwardImpulse()) {
            applyFriction(player);
            return;
        }

        if (sprint.get() && !player.isSprinting()) {
            player.setSprinting(true);
        }

        float factor = player.onGround() ? groundMultiplier.get() : multiplier.get();
        if (mode.is("Ваниль")) {
            player.setSprinting(sprint.get());
            return;
        }
        if (mode.is("Земля") && !player.onGround()) {
            return;
        }

        Vec3 movement = player.getDeltaMovement();
        double horizontal = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
        double target = Math.min(maxSpeed.get(), horizontal * factor + acceleration.get());
        if (horizontal > 1.0E-5D && target > horizontal) {
            double scale = target / horizontal;
            double x = movement.x * scale;
            double z = movement.z * scale;
            if (preserveY.get()) {
                player.setDeltaMovement(x, movement.y, z);
            } else {
                player.setDeltaMovement(x, movement.y, z);
            }
        }
    }

    private void applyFriction(LocalPlayer player) {
        Vec3 movement = player.getDeltaMovement();
        double frictionValue = friction.get();
        player.setDeltaMovement(movement.x * frictionValue, movement.y, movement.z * frictionValue);
    }
}