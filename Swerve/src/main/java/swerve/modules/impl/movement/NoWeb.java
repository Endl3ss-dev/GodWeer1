package swerve.modules.impl.movement;

import swerve.Swerve;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

// Adapted from AlphaDLC/src/main/java/zov/alphadlc/module/list/movement/NoWeb.java
public final class NoWeb extends Module {

    // Жёсткий потолок на фактическую скорость за тик, чтобы не словить ванильный
    // "moved too quickly" (порог сервера d^2 > 100.0, т.е. ~10 блоков/тик).
    private static final double MAX_TICK_SPEED = 7.5D;

    private final ModeSetting mode = new ModeSetting("Режим", "Обход", "Обход", "Компенсация");

    private final FloatSetting speedXZ = (FloatSetting) new FloatSetting("Скорость XZ", 0.05F, 0.0F, 0.30F, 0.005F)
            .visible(() -> mode.is("Компенсация"));
    private final FloatSetting speedUp = (FloatSetting) new FloatSetting("Скорость вверх Y", 0.06F, 0.0F, 0.20F, 0.005F)
            .visible(() -> mode.is("Компенсация"));
    private final FloatSetting speedDown = (FloatSetting) new FloatSetting("Скорость вниз Y", 0.175F, 0.0F, 0.40F, 0.005F)
            .visible(() -> mode.is("Компенсация"));
    private final BooleanSetting onlyWithInput = (BooleanSetting) new BooleanSetting("Только с вводом", true)
            .visible(() -> mode.is("Компенсация"));

    private final BooleanSetting killFallDamage = new BooleanSetting("Гасить урон падения", true);
    private final BooleanSetting ignoreInVehicle = new BooleanSetting("Не трогать в транспорте", true);
    private final BooleanSetting ignoreOnElytra = new BooleanSetting("Не трогать на элитрах", true);

    public NoWeb() {
        super("NoWeb", "Отменяет замедление в паутине (режимы: полный обход либо компенсация скорости).",
                ModuleCategory.MOVEMENT);
        addSettings(mode, speedXZ, speedUp, speedDown, onlyWithInput,
                killFallDamage, ignoreInVehicle, ignoreOnElytra);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    /**
     * Вызывается миксином на HEAD WebBlock.entityInside.
     * @return true, если миксин должен отменить ванильную логику (режим "Обход").
     */
    public boolean onCobwebHead(Entity entity, BlockState state) {
        if (!isEnable()) return false;
        if (!isLocalTarget(entity)) return false;
        if (isFiltered(entity)) return false;
        if (!mode.is("Обход")) return false;

        if (killFallDamage.get()) {
            entity.resetFallDistance();
        }
        return true;
    }

    /**
     * Вызывается миксином на RETURN WebBlock.entityInside (режим "Компенсация").
     * Ванила уже установила stuckSpeedMultiplier; мы задаём delta так,
     * чтобы фактическая скорость следующего тика равнялась настройкам.
     */
    public void onCobwebReturn(Entity entity, BlockState state) {
        if (!isEnable()) return;
        if (!isLocalTarget(entity)) return;
        if (isFiltered(entity)) return;
        if (!mode.is("Компенсация")) return;

        // В креатив-полёте ванила не ставит множитель (Player.makeStuckInBlock) -
        // компенсация даст рывок. Отфильтровываем.
        if (entity instanceof Player player && player.getAbilities().flying) return;

        boolean weaving = entity instanceof LivingEntity living && living.hasEffect(MobEffects.WEAVING);
        double mx = weaving ? 0.5D : 0.25D;
        double my = weaving ? 0.25D : 0.05D;

        Minecraft mc = Minecraft.getInstance();
        Options options = mc.options;
        if (options == null) return;

        boolean fwd = options.keyUp.isDown();
        boolean back = options.keyDown.isDown();
        boolean left = options.keyLeft.isDown();
        boolean right = options.keyRight.isDown();
        boolean jump = options.keyJump.isDown();
        boolean sneak = options.keyShift.isDown();

        double inForward = (fwd ? 1.0D : 0.0D) - (back ? 1.0D : 0.0D);
        double inStrafe = (left ? 1.0D : 0.0D) - (right ? 1.0D : 0.0D);

        if (onlyWithInput.get() && inForward == 0.0D && inStrafe == 0.0D && !jump && !sneak) {
            return;
        }

        double targetXZ = Math.min(speedXZ.get(), MAX_TICK_SPEED * mx);
        double targetUp = Math.min(speedUp.get(), MAX_TICK_SPEED * my);
        double targetDown = Math.min(speedDown.get(), MAX_TICK_SPEED * my);

        double deltaX = 0.0D;
        double deltaZ = 0.0D;

        if (inForward != 0.0D || inStrafe != 0.0D) {
            // Нормализация, чтобы диагональ не была быстрее прямой.
            double len = Math.sqrt(inForward * inForward + inStrafe * inStrafe);
            double normF = inForward / len;
            double normS = inStrafe / len;

            float yawDeg = 0.0F;
            if (entity instanceof LivingEntity living) {
                yawDeg = living.getYRot();
            }
            double yawRad = Math.toRadians(yawDeg);
            double sin = Math.sin(yawRad);
            double cos = Math.cos(yawRad);

            // target - фактическая скорость; делим на множитель, т.к. move() умножит обратно.
            double magnitude = targetXZ / mx;

            // forward в MC: dx = -sin(yaw), dz = +cos(yaw); strafe (влево): dx = +cos(yaw), dz = +sin(yaw).
            deltaX = (normF * -sin + normS * cos) * magnitude;
            deltaZ = (normF * cos + normS * sin) * magnitude;
        }

        double deltaY;
        if (jump) {
            deltaY = targetUp / my;
        } else if (sneak) {
            deltaY = -(targetDown / my);
        } else {
            // Y не трогаем - пусть работают гравитация/прыжок, которые ванила обнулит через
            // setDeltaMovement(ZERO) в move(), но это уже на следующий тик будет пересчитано.
            Vec3 current = entity.getDeltaMovement();
            deltaY = current.y;
        }

        // Финальный хард-клэмп по модулю на всякий случай.
        deltaX = Mth.clamp(deltaX, -MAX_TICK_SPEED, MAX_TICK_SPEED);
        deltaY = Mth.clamp(deltaY, -MAX_TICK_SPEED, MAX_TICK_SPEED);
        deltaZ = Mth.clamp(deltaZ, -MAX_TICK_SPEED, MAX_TICK_SPEED);

        entity.setDeltaMovement(deltaX, deltaY, deltaZ);

        if (killFallDamage.get()) {
            entity.resetFallDistance();
        }
    }

    private boolean isLocalTarget(Entity entity) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return false;
        return entity == player;
    }

    private boolean isFiltered(Entity entity) {
        if (ignoreInVehicle.get() && (entity.isPassenger() || entity.getVehicle() != null)) {
            return true;
        }
        if (ignoreOnElytra.get() && entity instanceof LivingEntity living && living.isFallFlying()) {
            return true;
        }
        return false;
    }

    public void setEnabled(boolean state) {
        if (state && !isEnable()) {
            onEnable();
        } else if (!state && isEnable()) {
            onDisable();
        }
    }

    static {
        // Guard: защищаемся от того, что Swerve.LOGGER может быть недоступен в unit-тестах.
        try {
            Swerve.LOGGER.getClass();
        } catch (Throwable ignored) {
        }
    }
}
