package swerve.modules.impl.movement;

import swerve.mixins.accessor.LocalPlayerAccessor;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;

// Speed масштабирует ВАНИЛЬНОЕ УСКОРЕНИЕ, а не уже проинтегрированную скорость.
//
// Точка внедрения — LivingEntity.getFrictionInfluencedSpeed (проверено разбором байткода 1.21.4,
// подробности в docs/Speed.md §2): из неё vanilla берёт приращение скорости для travel(), и только
// она интересует игрока на земле и в воздухе. Вода (travelInFluid), элитры (travelFallFlying),
// транспорт (getRiddenSpeed) и отталкивание при ударе (Player.attack) читают другие члены, поэтому
// остаются нетронутыми.
//
// Следствие: скорость меняется через ванический интегратор v = (v + a) * f. Резкого прыжка нет —
// разгон и торможение делает трение блока, а не модуль. Отсюда же отсутствие состояния: модулю
// нечего восстанавливать при выключении, и он не конфликтует с respawn/сменой мира.
public final class Speed extends Module {

    /**
     * Границы множителя. Нижняя = 1.0: модуль не должен тормозить, при 1.0 результат
     * совпадает с ваниллой. Верхняя выбрана из ванильного лимита перемещения за тик
     * (d^2 > 100, т.е. 10 блоков/тик): при 1.6 смещение ≈0.44 блока/тик даже на льду
     * со спринтом — запас в разы, но дальше он начинает съедаться быстрее, чем
     * растёт полезная скорость (см. docs/Speed.md §2). К поведению античитов это
     * отношение не имеет никакого отношения.
     */
    private static final float MIN_MULTIPLIER = 1.0F;
    private static final float MAX_MULTIPLIER = 1.6F;

    private final FloatSetting multiplier = new FloatSetting("Множитель", 1.15F, MIN_MULTIPLIER, MAX_MULTIPLIER, 0.01F);
    private final BooleanSetting onlyGround = new BooleanSetting("Только на земле", false);
    private final BooleanSetting onlyForward = new BooleanSetting("Только вперёд", false);
    private final BooleanSetting autoSprint = new BooleanSetting("Авто-спринт", true);

    public Speed() {
        super("Speed", "Множитель ускорения движения. Трение, гравитация и коллизии остаются ванильными.",
                ModuleCategory.MOVEMENT);
        addSettings(multiplier, onlyGround, onlyForward, autoSprint);
    }

    /**
     * Вызывается из миксина на RETURN {@code LivingEntity.getFrictionInfluencedSpeed}.
     *
     * @param acceleration ванильное приращение скорости за тик (уже с учётом трения блока)
     * @param entity       сущность, для которой считается ускорение; реакция только на своего игрока
     * @return ускорение с применённым множителем либо исходное значение, если условия не выполнены
     */
    public float onAcceleration(float acceleration, LivingEntity entity) {
        if (!isEnable()) {
            return acceleration;
        }
        float factor = clampMultiplier(this.multiplier.get());
        if (factor <= MIN_MULTIPLIER) {
            return acceleration;
        }
        // Нулевое или повреждённое (NaN/Inf) ускорение домножать нельзя: NaN из travel()
        // расходится по всей позиции игрока и лечится только респавном.
        if (acceleration == 0.0F || !Float.isFinite(acceleration)) {
            return acceleration;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        // Сравнение по идентичности, а не по классу: во время смерти/смены мира старый
        // LocalPlayer ещё может ходить по client tick.
        if (player == null || entity != player) {
            return acceleration;
        }
        // Полёт в креативе/спектейторе и езда на животном считаются по своим членам;
        // там множитель дал бы скачок, которого сервер не ожидает даже в принципе.
        if (player.getAbilities().flying || player.isPassenger()) {
            return acceleration;
        }
        if (this.onlyGround.get() && !player.onGround()) {
            return acceleration;
        }
        if (this.onlyForward.get()) {
            ClientInput input = player.input;
            if (input == null || !input.hasForwardImpulse()) {
                return acceleration;
            }
        }
        return acceleration * factor;
    }

    /**
     * Авто-спринт. Состояние выставляется только там, где сама игра разрешила бы его поднять:
     * {@code LocalPlayer.canStartSprinting()} — приватный ванильный шлюз (еда, слепота,
     * использование предмета, транспорт, элитры, крауч, сила импульса). Прямой {@code setSprinting(true)}
     * мимо него давал бы качание атрибута MOVEMENT_SPEED (спринт = +30% ADD_MULTIPLIED_TOTAL)
     * между тиками, то есть ровно те рывки, которых модуль обязан избегать.
     */
    @Override
    public void onTick() {
        if (!this.autoSprint.get()) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSprinting()) {
            return;
        }
        // Ванильный переход в спринт возможен с земли или из-под воды; в воздухе игра спринт
        // не начинает, и выдумывать это право незачем.
        if (!player.onGround() && !player.isUnderWater()) {
            return;
        }
        if (((LocalPlayerAccessor) player).swerve$canStartSprinting()) {
            player.setSprinting(true);
        }
    }

    private static float clampMultiplier(float value) {
        if (!Float.isFinite(value) || value < MIN_MULTIPLIER) {
            return MIN_MULTIPLIER;
        }
        return value > MAX_MULTIPLIER ? MAX_MULTIPLIER : value;
    }
}
