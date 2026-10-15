package swerve.modules.impl.render;

import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BindSetting;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.utils.render.anim.HandMotion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Процедурные анимации рук от первого лица. Ни одного ключевого кадра и ассета: все слои —
 * пружины и фазы, посчитанные из состояния игрока (скорость, земля, взгляд, предмет, урон).
 * Слои аддитивны и накладываются до смещений ViewModel, поэтому оба модуля работают вместе.
 * Вся математика — в swerve.utils.render.anim.HandMotion, модуль хранит настройки.
 */
public final class HandAnimations extends Module {

    public static final String SPRINT_SWING = "Размах";
    public static final String SPRINT_RUN = "Бег";
    public static final String SPRINT_SWAY = "Качание";

    public static final String EQUIP_BELOW = "Снизу";
    public static final String EQUIP_SIDE = "Сбоку";
    public static final String EQUIP_SPIN = "Поворот";

    private static HandAnimations instance;

    private final FloatSetting master = new FloatSetting("Общая сила", 1.0F, 0.1F, 2.0F, 0.05F);
    private final FloatSetting offhand = new FloatSetting("Сила оффхенда", 0.7F, 0.0F, 1.5F, 0.05F);
    private final FloatSetting handBob = new FloatSetting("Боббинг рук", 1.0F, 0.0F, 2.0F, 0.05F);
    private final BooleanSetting handBobForce = new BooleanSetting("Боббинг рук без камеры", false);

    private final BooleanSetting lag = new BooleanSetting("Инерция взгляда", true);
    private final FloatSetting lagStrength = new FloatSetting("Сила инерции", 1.0F, 0.1F, 3.0F, 0.05F)
            .visible(() -> lag.get());
    private final FloatSetting lagStiffness = new FloatSetting("Жёсткость инерции", 1.0F, 0.3F, 3.0F, 0.05F)
            .visible(() -> lag.get());

    private final BooleanSetting walk = new BooleanSetting("Ходьба", true);
    private final FloatSetting walkStrength = new FloatSetting("Сила ходьбы", 1.0F, 0.1F, 3.0F, 0.05F)
            .visible(() -> walk.get());

    private final BooleanSetting sprint = new BooleanSetting("Спринт", true);
    private final ModeSetting sprintStyle = new ModeSetting("Стиль спринта", SPRINT_SWING,
            SPRINT_SWING, SPRINT_RUN, SPRINT_SWAY).visible(() -> sprint.get());
    private final FloatSetting sprintStrength = new FloatSetting("Сила спринта", 1.0F, 0.1F, 3.0F, 0.05F)
            .visible(() -> sprint.get());

    private final BooleanSetting idle = new BooleanSetting("Дыхание", true);
    private final FloatSetting idleStrength = new FloatSetting("Сила дыхания", 1.0F, 0.1F, 3.0F, 0.05F)
            .visible(() -> idle.get());

    private final BooleanSetting landing = new BooleanSetting("Прыжок и приземление", true);
    private final FloatSetting landingStrength = new FloatSetting("Сила приземления", 1.0F, 0.1F, 3.0F, 0.05F)
            .visible(() -> landing.get());

    private final BooleanSetting sneak = new BooleanSetting("Приседание", true);
    private final BooleanSetting falling = new BooleanSetting("Падение", true);

    private final BooleanSetting equip = new BooleanSetting("Смена предмета", true);
    private final ModeSetting equipStyle = new ModeSetting("Стиль смены", EQUIP_BELOW,
            EQUIP_BELOW, EQUIP_SIDE, EQUIP_SPIN).visible(() -> equip.get());
    private final FloatSetting equipSpeed = new FloatSetting("Скорость смены", 1.0F, 0.5F, 3.0F, 0.05F)
            .visible(() -> equip.get());

    private final BooleanSetting use = new BooleanSetting("Отдача использования", true);

    private final BooleanSetting inspect = new BooleanSetting("Осмотр предмета", true);
    private final BindSetting inspectBind = new BindSetting("Бинд осмотра", -1)
            .visible(() -> inspect.get());
    private final FloatSetting inspectDuration = new FloatSetting("Длительность осмотра", 2.0F, 1.0F, 5.0F, 0.1F)
            .visible(() -> inspect.get());

    private final BooleanSetting shield = new BooleanSetting("Щит", true);

    private final BooleanSetting hit = new BooleanSetting("Отдача удара", true);
    private final FloatSetting hitStrength = new FloatSetting("Сила отдачи", 1.0F, 0.1F, 3.0F, 0.05F)
            .visible(() -> hit.get());

    private final BooleanSetting hurt = new BooleanSetting("Урон", true);
    private final BooleanSetting shiver = new BooleanSetting("Дрожь (огонь, мороз)", true);
    private final BooleanSetting bow = new BooleanSetting("Натяжение лука", true);
    private final BooleanSetting swim = new BooleanSetting("Плавание и ползание", true);
    private final BooleanSetting climb = new BooleanSetting("Лестница", true);
    private final BooleanSetting ride = new BooleanSetting("Езда", true);
    private final BooleanSetting screen = new BooleanSetting("Руки в меню", true);

    public HandAnimations() {
        super("HandAnimations", "Процедурные анимации рук: инерция, спринт, дыхание, смена и осмотр предмета",
                ModuleCategory.RENDER);
        instance = this;
        addSettings(master, offhand, handBob, handBobForce,
                lag, lagStrength, lagStiffness,
                walk, walkStrength,
                sprint, sprintStyle, sprintStrength,
                idle, idleStrength,
                landing, landingStrength,
                sneak, falling,
                equip, equipStyle, equipSpeed, use,
                inspect, inspectBind, inspectDuration,
                shield, hit, hitStrength, hurt, shiver, bow,
                swim, climb, ride, screen);
    }

    public static HandAnimations get() {
        return instance;
    }

    public static boolean active() {
        HandAnimations module = instance;
        return module != null && module.isEnable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        HandMotion.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HandMotion.reset();
    }

    @Override
    public void onTick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().level == null) {
            HandMotion.reset();
            return;
        }
        HandMotion.tick(player);
    }

    // Множитель ванильного боббинга рук: 1.0 — ванильный вызов без вмешательства.
    public static boolean overrideHandBob() {
        HandAnimations module = instance;
        return module != null && module.isEnable() && module.handBob.get() != 1.0F;
    }

    // Ванильный View Bobbing выключен (камера не качается), а руки качать всё равно нужно.
    public static boolean forceHandBob() {
        HandAnimations module = instance;
        return module != null && module.isEnable() && module.handBobForce.get() && module.handBob.get() > 0.0F;
    }

    public float handBobScale() {
        return handBob.get();
    }

    public float masterStrength() {
        return master.get();
    }

    public float offhandScale() {
        return offhand.get();
    }

    public boolean lagEnabled() {
        return lag.get();
    }

    public float lagStrength() {
        return lagStrength.get();
    }

    public float lagStiffness() {
        return lagStiffness.get();
    }

    public boolean walkEnabled() {
        return walk.get();
    }

    public float walkStrength() {
        return walkStrength.get();
    }

    public boolean sprintEnabled() {
        return sprint.get();
    }

    public int sprintStyle() {
        return sprintStyle.getIndex();
    }

    public float sprintStrength() {
        return sprintStrength.get();
    }

    public boolean idleEnabled() {
        return idle.get();
    }

    public float idleStrength() {
        return idleStrength.get();
    }

    public boolean landingEnabled() {
        return landing.get();
    }

    public float landingStrength() {
        return landingStrength.get();
    }

    public boolean sneakEnabled() {
        return sneak.get();
    }

    public boolean fallingEnabled() {
        return falling.get();
    }

    public boolean equipEnabled() {
        return equip.get();
    }

    public int equipStyle() {
        return equipStyle.getIndex();
    }

    public float equipSpeed() {
        return equipSpeed.get();
    }

    public boolean useEnabled() {
        return use.get();
    }

    public boolean inspectEnabled() {
        return inspect.get();
    }

    public int inspectKey() {
        return inspectBind.getKey();
    }

    public float inspectDuration() {
        return inspectDuration.get();
    }

    public boolean shieldEnabled() {
        return shield.get();
    }

    public boolean hitEnabled() {
        return hit.get();
    }

    public float hitStrength() {
        return hitStrength.get();
    }

    public boolean hurtEnabled() {
        return hurt.get();
    }

    public boolean shiverEnabled() {
        return shiver.get();
    }

    public boolean bowEnabled() {
        return bow.get();
    }

    public boolean swimEnabled() {
        return swim.get();
    }

    public boolean climbEnabled() {
        return climb.get();
    }

    public boolean rideEnabled() {
        return ride.get();
    }

    public boolean screenEnabled() {
        return screen.get();
    }
}
