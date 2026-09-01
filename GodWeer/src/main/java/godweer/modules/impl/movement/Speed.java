package godweer.modules.impl.movement;

import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.FloatSetting;
import godweer.modules.api.setting.ModeSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class Speed extends Module {

    private static final double MATRIX_HOP = 0.2D;
    private static final double GRIM_UP_BOOST = 0.03D;
    private static final double GRIM_TICK_DELAY = 3.0D;

    private final ModeSetting mode = new ModeSetting("Режим", "Страф",
            "Страф", "Ваниль", "Земля", "BHop", "Матрикс", "Грим", "Колизия");

    private final BooleanSetting autoSprint = new BooleanSetting("Авто-спринт", true);
    private final BooleanSetting onlyForward = new BooleanSetting("Только вперёд", false);
    private final BooleanSetting inAir = new BooleanSetting("В воздухе", true);
    private final BooleanSetting respectCollision = new BooleanSetting("Учитывать столкновения", true);

    private final FloatSetting boostSpeed = new FloatSetting("Скорость", 0.34F, 0.20F, 0.60F, 0.01F)
            .visible(() -> mode.is("BHop") || mode.is("Матрикс") || mode.is("Грим"));
    private final FloatSetting jumpStrength = new FloatSetting("Сила прыжка", 0.42F, 0.30F, 0.50F, 0.01F)
            .visible(() -> mode.is("BHop") || mode.is("Грим"));

    private final FloatSetting multiplier = new FloatSetting("Множитель", 1.08F, 1.0F, 1.35F, 0.01F)
            .visible(() -> mode.is("Страф"));
    private final FloatSetting groundMultiplier = new FloatSetting("Множитель на земле", 1.04F, 1.0F, 1.25F, 0.01F)
            .visible(() -> mode.is("Страф") || mode.is("Земля"));
    private final FloatSetting acceleration = new FloatSetting("Ускорение", 0.035F, 0.001F, 0.15F, 0.001F)
            .visible(() -> mode.is("Страф"));
    private final FloatSetting maxSpeed = new FloatSetting("Максимальная скорость", 0.38F, 0.1F, 1.0F, 0.01F)
            .visible(() -> mode.is("Страф") || mode.is("Земля"));
    private final FloatSetting friction = new FloatSetting("Трение", 0.91F, 0.5F, 1.0F, 0.01F)
            .visible(() -> mode.is("Страф") || mode.is("Земля"));

    private final FloatSetting collisionRadius = new FloatSetting("Радиус колизии", 1.25F, 0.3F, 2.5F, 0.05F)
            .visible(() -> mode.is("Колизия"));
    private final FloatSetting collisionBoost = new FloatSetting("Буст колизии", 0.02F, 0.005F, 0.06F, 0.005F)
            .visible(() -> mode.is("Колизия"));

    private final double[] direction = new double[2];
    private int boostTicks;
    private int groundTicks;

    public Speed() {
        super("Speed", "Настраиваемое ускорение движения с выбором обхода", ModuleCategory.MOVEMENT);
        addSettings(
                mode, autoSprint, onlyForward, inAir, respectCollision,
                boostSpeed, jumpStrength,
                multiplier, groundMultiplier, acceleration, maxSpeed, friction,
                collisionRadius, collisionBoost
        );
    }

    @Override
    public void onEnable() {
        boostTicks = 0;
        groundTicks = 0;
    }

    @Override
    public void onDisable() {
        boostTicks = 0;
        groundTicks = 0;
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }
        if (minecraft.screen != null || !player.isAlive() || player.isPassenger()) {
            return;
        }
        if (respectCollision.get()
                && (player.horizontalCollision || player.verticalCollision && !player.onGround())) {
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
            if (mode.is("Страф") || mode.is("Земля")) {
                applyFriction(player);
            }
            boostTicks = 0;
            groundTicks = 0;
            return;
        }

        if (autoSprint.get() && !player.isSprinting()) {
            player.setSprinting(true);
        }

        switch (mode.getCurrent()) {
            case "Ваниль" -> {
                return;
            }
            case "Страф" -> tickStrafe(player);
            case "Земля" -> tickGround(player);
            case "BHop" -> tickBhop(player);
            case "Матрикс" -> tickMatrix(player);
            case "Грим" -> tickGrim(player);
            case "Колизия" -> tickCollision(minecraft, player);
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

    private void tickGround(LocalPlayer player) {
        if (!player.onGround()) {
            applyFriction(player);
            return;
        }

        Vec3 movement = player.getDeltaMovement();
        double horizontal = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
        double target = Math.min(maxSpeed.get(), horizontal * groundMultiplier.get() + acceleration.get());

        if (target > horizontal && target > 1.0E-5D) {
            moveVector(player, target);
            player.setDeltaMovement(direction[0], movement.y, direction[1]);
        }
    }

    private void tickBhop(LocalPlayer player) {
        Vec3 movement = player.getDeltaMovement();

        if (player.onGround()) {
            moveVector(player, boostSpeed.get());
            player.setDeltaMovement(direction[0], jumpStrength.get(), direction[1]);
            return;
        }

        double horizontal = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
        if (horizontal < boostSpeed.get()) {
            // Небольшое довоздушное ускорение в сторону движения.
            moveVector(player, 0.012D);
            player.push(direction[0], 0.0D, direction[1]);
        }
    }

    // Matrix: низкий хоп на земле + фиксированная горизонтальная скорость по направлению ввода.
    private void tickMatrix(LocalPlayer player) {
        if (!player.onGround()) {
            return;
        }

        moveVector(player, boostSpeed.get());
        player.setDeltaMovement(direction[0], MATRIX_HOP, direction[1]);
    }

    // Грим: чётные тики — микро-подброс, каждый тик — горизонтальный буст по направлению движения.
    private void tickGrim(LocalPlayer player) {
        boostTicks++;
        if (boostTicks <= GRIM_TICK_DELAY) {
            return;
        }

        double boost = player.onGround() ? boostSpeed.get() * 0.25D : boostSpeed.get() * 0.09D;
        if (boostTicks % 2 == 0) {
            player.push(0.0D, GRIM_UP_BOOST, 0.0D);
        }
        moveVector(player, boost);
        player.push(direction[0], 0.0D, direction[1]);

        groundTicks = player.verticalCollision ? groundTicks + 1 : 0;
        if (groundTicks >= 1 && player.onGround() && boostTicks % 2 == 0) {
            Vec3 movement = player.getDeltaMovement();
            player.setDeltaMovement(movement.x, jumpStrength.get(), movement.z);
        }
    }

    // Колизия: буст при контакте хитбокса с другим игроком (по мотивам AlphaDLC/SkyCore).
    private void tickCollision(Minecraft minecraft, LocalPlayer player) {
        AABB searchBox = player.getBoundingBox().inflate(collisionRadius.get());

        for (AbstractClientPlayer other : minecraft.level.players()) {
            if (other == player || !other.isAlive() || other.isRemoved()) {
                continue;
            }
            if (!searchBox.intersects(other.getBoundingBox())) {
                continue;
            }

            moveVector(player, collisionBoost.get());
            player.push(direction[0], 0.0D, direction[1]);
            break;
        }
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
