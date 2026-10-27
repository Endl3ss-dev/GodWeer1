package net.skycore.client.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/**
 * Текущий снимок настроек фейк-игрока.
 *
 * <p>Почему отдельный класс, а не поля модуля: сам фейк-игрок живёт в
 * {@code command.FakeplayerCommand} (там же вся физика, урон и тотем), и тянуть
 * зависимость команды на GUI-модуль было бы встречной связью. Здесь только те
 * примитивы, которые читает {@code FakeplayerCommand.FakePlayer}.
 *
 * <p>Модуль раз в тик собирает {@link FakePlayerConfig} и кладёт его сюда через
 * {@link #apply(FakePlayerConfig)}; болванка берёт {@link #current()} и живёт по нему.
 * Геттеры ниже оставлены для старых мест вызова (тотем, щит) — они просто читают
 * текущий конфиг.
 *
 * <p>Режимы тотема и щита повторяют шкалу из запроса и означают «насколько рано бот
 * защищается», то есть насколько трудно его убить:
 * <ul>
 *   <li>{@code Legit} — как игрок-человек: тотем только на последних хитах,
 *       щит после первого попадания;</li>
 *   <li>{@code Normal} — базовое поведение;</li>
 *   <li>{@code Hard} — тратит тотем раньше, щит держит при приближении;</li>
 *   <li>{@code Extra} — тотем почти на полном здоровье, щит поднят постоянно.</li>
 * </ul>
 */
public final class FakePlayerSettings {

    /** Порог здоровья, при котором бот тратит тотем, по режимам. */
    private static final float[] TOTEM_THRESHOLD = {3.0F, 6.0F, 10.0F, 16.0F};

    /** Конфиг «всё выключено» — используется до первого {@link #apply}. */
    private static final FakePlayerConfig DISABLED = new FakePlayerConfig()
            .useTotem(false)
            .totemCount(0)
            .useShield(false)
            .armorType(FakePlayerConfig.ARMOR_NONE)
            .heldItem(FakePlayerConfig.HELD_NONE);

    private static volatile FakePlayerConfig current = DISABLED;

    private FakePlayerSettings() {
    }

    /** Кладёт новый снимок настроек (вызывается модулем каждый тик). */
    public static void apply(FakePlayerConfig config) {
        current = config == null ? DISABLED : config;
    }

    /** Текущий снимок настроек. */
    public static FakePlayerConfig current() {
        return current;
    }

    /** Сбрасывает всё в «выключено» — вызывается при выключении модуля. */
    public static void reset() {
        current = DISABLED;
    }

    public static boolean copyInventory() {
        return current.copyInventory();
    }

    public static boolean useTotem() {
        return current.useTotem();
    }

    public static boolean infiniteTotems() {
        return current.infiniteTotems();
    }

    public static int totemCount() {
        return current.totemCount();
    }

    public static int totemMode() {
        return current.totemMode();
    }

    public static boolean useShield() {
        return current.useShield();
    }

    public static int shieldMode() {
        return current.shieldMode();
    }

    /** Здоровье, на котором бот тратит тотем в текущем режиме. */
    public static float totemThreshold() {
        return TOTEM_THRESHOLD[current.totemMode()];
    }

    /** Нужно ли поднимать щит прямо сейчас. */
    public static boolean shieldWanted(Player fake) {
        if (!useShield() || fake == null) {
            return false;
        }
        Player self = Minecraft.getInstance().player;
        double distance = self == null ? Double.MAX_VALUE : fake.distanceTo(self);
        return switch (shieldMode()) {
            case 0 -> distance <= 4.0 && fake.hurtTime > 0;
            case 2 -> distance <= 12.0;
            case 3 -> true;
            default -> distance <= 6.0;
        };
    }
}
