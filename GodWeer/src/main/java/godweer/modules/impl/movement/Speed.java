package godweer.modules.impl.movement;

import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ModeSetting;
import godweer.utils.movement.TimerManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class Speed extends Module {

    private static final int RW_WARMUP_TICKS = 3;
    private static final double RW_UP_BOOST = 0.03D;

    private final ModeSetting mode = new ModeSetting("Режим", "Ваниль", "Ваниль", "Страф", "RW");

    private final BooleanSetting autoSprint = new BooleanSetting("Авто-спринт", true);
    private final BooleanSetting onlyForward = new BooleanSetting("Только вперёд", false);
    private final BooleanSetting inAir = new BooleanSetting("В воздухе", true);

    private final FloatSetting multiplier = new FloatSetting("Множитель в воздухе", 1.06F, 1.0F, 1.2F, 0.01F)
            .visible(() -> mode.is("Страф"));
    private final FloatSetting groundMultiplier = new FloatSetting("Множитель на земле", 1.02F, 1.0F, 1.15F, 0.01F)
            .visible(() -> mode.is("Страф"));
    private final FloatSetting acceleration = new FloatSetting("Ускорение", 0.02F, 0.001F, 0.1F, 0.001F)
            .visible(() -> mode.is("Страф"));
    private final FloatSetting maxSpeed = new FloatSetting("Максимальная скорость", 0.285F, 0.2F, 0.4F, 0.005F)
            .visible(() -> mode.is("Страф"));
    private final FloatSetting friction = new FloatSetting("Трение", 0.91F, 0.5F, 1.0F, 0.01F)
            .visible(() -> mode.is("Страф"));

    private final FloatSetting rwBoost = new FloatSetting("RW буст на земле", 0.085F, 0.02F, 0.15F, 0.005F)
            .visible(() -> mode.is("RW"));
    private final FloatSetting rwAirBoost = new FloatSetting("RW буст в воздухе", 0.03F, 0.01F, 0.08F, 0.005F)
            .visible(() -> mode.is("RW"));
    private final BooleanSetting rwAutoJump = new BooleanSetting("RW авто-прыжок", true)
            .visible(() -> mode.is("RW"));
    private final BooleanSetting rwUseTimer = new BooleanSetting("RW таймер", false)
            .visible(() -> mode.is("RW"));
    private final FloatSetting rwTimer = new FloatSetting("Скорость таймера", 1.4F, 1.0F, 3.0F, 0.05F)
            .visible(() -> mode.is("RW") && rwUseTimer.get());

    private final double[] direction = new double[2];
    private int rwTicks;

    public Speed() {
        super("Speed", "Ускорение движения: леджит-страф и RW-обход (фейк-элитра)", ModuleCategory.MOVEMENT);
        addSettings(
                mode, autoSprint, onlyForward, inAir,
                multiplier, groundMultiplier, acceleration, maxSpeed, friction,
                rwBoost, rwAirBoost, rwAutoJump, rwUseTimer, rwTimer
        );
    }

    @Override
    public void onEnable() {
        rwTicks = 0;
    }

    @Override
    public void onDisable() {
        rwTicks = 0;
        TimerManager.reset();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options != null) {
            minecraft.options.keyJump.setDown(false);
        }
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }

        boolean rw = mode.is("RW");
        if (!rw) {
            TimerManager.reset();
            minecraft.options.keyJump.setDown(false);
            rwTicks = 0;
        }

        if (minecraft.screen != null || !player.isAlive() || player.isPassenger()) {
            return;
        }
        if (!inAir.get() && !player.onGround()) {
            return;
        }
        if (onlyForward.get() && player.zza <= 0.0F) {
            return;
        }

        boolean moving = player.zza != 0.0F || player.xxa != 0.0F;
        if (!moving) {
            if (mode.is("Страф")) {
                applyFriction(player);
            }
            return;
        }

        if (autoSprint.get() && !player.isSprinting()) {
            player.setSprinting(true);
        }

        switch (mode.getCurrent()) {
            case "Страф" -> tickStrafe(player);
            case "RW" -> tickRw(minecraft, player);
            default -> {
            }
        }
    }

    private void tickStrafe(LocalPlayer player) {
        Vec3 movement = player.getDeltaMovement();
        double horizontal = Math.sqrt(movement.x * movement.x + movement.z * movement.z);

        float factor = player.onGround() ? groundMultiplier.get() : multiplier.get();
        double target = Math.min(maxSpeed.get(), horizontal * factor + acceleration.get());

        if (horizontal > 1.0E-5D && target > horizontal) {
            double scale = target / horizontal;
            player.setDeltaMovement(movement.x * scale, movement.y, movement.z * scale);
        }
    }

    // RW: фейк START_FALL_FLYING + велесити-буст (рассчитано на старые анчиты, пропускающие элитру).
    private void tickRw(Minecraft minecraft, LocalPlayer player) {
        if (player.getAbilities().flying || minecraft.getConnection() == null) {
            TimerManager.reset();
            return;
        }

        minecraft.options.keyJump.setDown(player.onGround() && rwAutoJump.get());

        minecraft.getConnection().send(
                new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING)
        );

        if (rwUseTimer.get()) {
            TimerManager.set(rwTimer.get());
        } else {
            TimerManager.reset();
        }

        if (rwTicks > RW_WARMUP_TICKS) {
            double boost = player.onGround() ? rwBoost.get() : rwAirBoost.get();
            if (rwTicks % 2 == 0) {
                player.push(0.0D, RW_UP_BOOST, 0.0D);
            }
            if (player.zza >= 0.0F) {
                moveVector(player, boost);
                player.push(direction[0], 0.0D, direction[1]);
            }
        }

        rwTicks++;
    }

    private void applyFriction(LocalPlayer player) {
        Vec3 movement = player.getDeltaMovement();
        double frictionValue = friction.get();
        player.setDeltaMovement(movement.x * frictionValue, movement.y, movement.z * frictionValue);
    }

    // Вектор движения по вводу (forward/strafe) с коррекцией диагоналей на 45°.
    private void moveVector(LocalPlayer player, double speed) {
        float forward = Mth.clamp(player.zza, -1.0F, 1.0F);
        float strafe = Mth.clamp(player.xxa, -1.0F, 1.0F);
        float yaw = player.getYRot();

        if (forward != 0.0F) {
            if (strafe > 0.0F) {
                yaw += forward > 0.0F ? -45.0F : 45.0F;
            } else if (strafe < 0.0F) {
                yaw += forward > 0.0F ? 45.0F : -45.0F;
            }
            strafe = 0.0F;
            forward = forward > 0.0F ? 1.0F : -1.0F;
        }

        double radians = Math.toRadians(yaw + 90.0F);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        direction[0] = forward * speed * cos + strafe * speed * sin;
        direction[1] = forward * speed * sin - strafe * speed * cos;
    }
}
